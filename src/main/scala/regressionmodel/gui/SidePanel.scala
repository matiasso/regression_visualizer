package regressionmodel.gui

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane

class SidePanel extends GridPane {
  val paddingInt = 10
  this.padding = Insets(paddingInt, paddingInt, paddingInt, paddingInt)
  this.hgap = paddingInt
  this.add(new Label("R value:"), 0, 0)
  this.add(new Label("???"), 1, 0)
  this.add(new Label("Graph f(x)="), 0, 1)
  this.add(new Label("???"), 1, 1)
}
