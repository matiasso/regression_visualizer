package regressionModel

import scalafx.scene.Scene
import scalafx.scene.layout.{Background, BackgroundFill, ColumnConstraints, CornerRadii, GridPane, HBox, Pane, RowConstraints, StackPane, VBox}
import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Label}
import scalafx.scene.effect.BlendMode.Blue
import scalafx.scene.text.Font
import scalafx.scene.text.FontSmoothingType.Gray

object Main extends JFXApp {
  stage = new JFXApp.PrimaryStage {
      title.value = "Hello Stage"
      width = 600
      height = 450
  }

  val view1 = new StackPane
  val view2 = new StackPane

  val mainScene = new Scene(view1)
  stage.scene = mainScene


  val button1 = new Button{
      text = "Swap to 2"
      onAction = (event) => mainScene.root = view2
  }

  val button2 = new Button{
      text = "Swap to 1"
      onAction = (event) => mainScene.root = view1
  }

  view1.children = button1
  view2.children = button2
}
