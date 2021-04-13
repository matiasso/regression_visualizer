package regressionmodel.gui

import org.scalafx.extras.BusyWorker
import regressionmodel.GlobalVars
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, ProgressBar, Tooltip}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, Priority, VBox}

object BottomPanel extends VBox {

  val paddingInt = 10
  var decimalCount: Int = 6
  val labelRSquared = new Label(GlobalVars.textRSquared)
  val labelFunc = new Label(GlobalVars.textForGraphLabel)
  val progressBar: ProgressBar = new ProgressBar() {
    progress = 0
    visible = false
  }
  val progressLabel: Label = new Label()
  var busyWorker: BusyWorker = _ // Define this later in DataPointSeries.update()
  // Add "Copy graph string" button

  private def updateAllLabels(): Unit = {
    this.decimalLabel.text = s"Decimals: $decimalCount"
    this.updateRSquared()
    this.updateFunctionLabel()
  }

  val decimalTooltip = new Tooltip("Range is limited to 1—16")
  val buttonLess: Button = new Button() {
    graphic = new ImageView(new Image("left-arrow.png")) {
      preserveRatio = true
      fitHeight = 15
    }
    tooltip = decimalTooltip
    onAction = _ => {
      decimalCount = math.max(decimalCount - 1, 1)
      updateAllLabels()
    }
  }
  val buttonMore: Button = new Button() {
    graphic = new ImageView(new Image("right-arrow.png")) {
      preserveRatio = true
      fitHeight = 15
    }
    tooltip = decimalTooltip
    onAction = _ => {
      decimalCount = math.min(decimalCount + 1, 16)
      updateAllLabels()
    }
  }
  val decimalLabel = new Label(s"Decimals: $decimalCount")
  val secondHBox: HBox = new HBox() {
    spacing = paddingInt
    children = Seq(buttonLess, decimalLabel, buttonMore, progressLabel)
  }
  for (node <- Seq(labelRSquared, labelFunc, progressBar)) {
    node.hgrow = Priority.Always
    node.maxWidth = Double.MaxValue
  }

  this.padding = Insets(paddingInt)
  this.spacing = paddingInt
  this.children = Seq(new HBox() {
    children = Seq(labelRSquared, secondHBox)
    spacing = paddingInt
  }, new HBox() {
    children = Seq(labelFunc, progressBar)
    spacing = paddingInt
  })


  def updateFunctionLabel(): Unit = {
    //Check whether we're using linear or exponential graph
    this.labelFunc.text = GlobalVars.textForGraphLabel + "\t" +
      (Plot.regressionSeries.regressionObject.getCoefficients match {
        case (Some(m), Some(b)) =>
          val bStr = s"%.${decimalCount}f".format(b)
          val bWithSign = (if (b >= 0) "+" else "") + bStr
          val mStr = s"%.${decimalCount}f".format(m)
          if (Plot.regressionSeries.isLinear) {
            s"y = $mStr * x $bWithSign"
          } else {
            s"y = $bStr * e${this.expPowerToSuperscript(mStr + "x")}"
          }
        case _ => GlobalVars.textUnknownCoefficients
      })
  }

  def updateRSquared(): Unit = {
    this.labelRSquared.text = GlobalVars.textRSquared + "\t" + (Plot.regressionSeries.regressionObject.rSquared match {
      case Some(r) => s"%.${decimalCount}f".format(r)
      case None => GlobalVars.textUnknownCoefficients
    })
  }

  private def getSuperscript(char: Char): Char = {
    char match {
      case '0' => '\u2070' // ⁰
      case '1' => '\u00B9' // ¹
      case '2' => '\u00B2' // ²
      case '3' => '\u00B3' // ³
      case '4' => '\u2074' // ⁴
      case '5' => '\u2075' // ⁵
      case '6' => '\u2076' // ⁶
      case '7' => '\u2077' // ⁷
      case '8' => '\u2078' // ⁸
      case '9' => '\u2079' // ⁹
      case '.' | ',' => '\u02D9' // ˙
      case 'x' => '\u02E3' // ˣ
      case _ => ' '
    }
  }

  private def expPowerToSuperscript(exp: String): String = {
    exp.foldLeft("")((prev, cur) => prev + this.getSuperscript(cur))
  }
}
