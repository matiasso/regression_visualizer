package regressionModel

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane

class SidePanel extends GridPane {
  val paddingInt = 10
  this.padding = Insets(paddingInt, paddingInt, paddingInt, paddingInt)
  this.hgap = paddingInt
  this.add(new Label("test1"), 0, 0)
  this.add(new Label("test2"), 1, 0)
  this.add(new Label("test3"), 0, 1)
  this.add(new Label("test4"), 1, 1)
}
