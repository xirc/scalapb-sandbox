name := "scalapb-sandbox"
version := "0.1"
scalaVersion := "2.13.3"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value / "scalapb"
)