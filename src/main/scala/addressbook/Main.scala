package addressbook

import java.io.{FileInputStream, FileNotFoundException, FileOutputStream}

import tutorial.addressbook.{AddressBook, Person}

import scala.io.StdIn
import scala.util.Using

object Main extends App {
  def readFromFile(): AddressBook = {
    Using(new FileInputStream("addressbook.pb")) { is =>
      AddressBook.parseFrom(is)
    }.recover {
      case _: FileNotFoundException =>
        println("No address book found. Will create a new file.")
        AddressBook()
    }.get
  }
  def writeToFile(addressBook: AddressBook): Unit = {
    Using(new FileOutputStream("addressbook.pb")) { os =>
      addressBook.writeTo(os)
    }
  }
  def personFromStdin(): Person = {
    print("Enter person ID (int): ")
    val id = StdIn.readInt()
    print("Enter name: ")
    val name = StdIn.readLine()
    print("Enter email address (blank for none): ")
    val email = StdIn.readLine()

    def getPhone(): Option[Person.PhoneNumber] = {
      print("Enter a phone number (or leave blank to finish): ")
      val number = StdIn.readLine()
      if (number.nonEmpty) {
        print("Is this a mobile, home, or work phone [mobile, home, work] ? ")
        val tpe = StdIn.readLine() match {
          case "mobile" => Some(Person.PhoneType.MOBILE)
          case "home" => Some(Person.PhoneType.HOME)
          case "work" => Some(Person.PhoneType.WORK)
          case _ =>
            println("Unknown phone type. Leaving as None.")
            None
        }
        Some(Person.PhoneNumber(number, tpe))
      } else {
        None
      }
    }

    val phones =
      Iterator
        .continually(getPhone())
        .takeWhile(_.nonEmpty)
        .flatten
        .toSeq

    Person(
      id = id,
      name = name,
      email = if (email.nonEmpty) Some(email) else None,
      phones = phones
    )
  }

  val addressBook = readFromFile()
  println(addressBook)
  val newPerson = personFromStdin()
  val updated = addressBook.update(
    _.people :+= newPerson
  )
  writeToFile(updated)
}
