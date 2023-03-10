package regressionmodel.gui

import org.scalafx.extras.BusyWorker
import regressionmodel.GlobalVars
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, ProgressBar, Tooltip}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{Clipboard, ClipboardContent}
import scalafx.scene.layout.{HBox, Priority, StackPane, VBox}

object BottomPanel extends VBox {

  private val paddingInt = 10
  private var decimalCount: Int = 6
  private val labelRSquared = new Label(GlobalVars.textRSquared)
  private val labelFunc = new Label(GlobalVars.textForGraphLabel)
  val progressBar: ProgressBar = new ProgressBar() {
    progress = 0
    visible = false
  }
  val progressLabel: Label = new Label() {
    alignment = Pos.Center
  }
  var busyWorker: BusyWorker = _ // Define this later in DataPointSeries.update()

  private def updateAllLabels(): Unit = {
    this.decimalLabel.text = s"Decimals: $decimalCount"
    this.updateRSquared()
    this.updateFunctionLabel()
  }

  private val decimalTooltip = new Tooltip("Range is limited to 1—16")
  private val buttonLess: Button = new Button() {
    graphic = new ImageView(new Image("left-arrow.png")) {
      preserveRatio = true
      fitHeight = 15
    }
    visible = false
    tooltip = decimalTooltip
    onAction = _ => {
      decimalCount = math.max(decimalCount - 1, 1)
      updateAllLabels()
    }
  }
  private val buttonMore: Button = new Button() {
    graphic = new ImageView(new Image("right-arrow.png")) {
      preserveRatio = true
      fitHeight = 15
    }
    visible = false
    tooltip = decimalTooltip
    onAction = _ => {
      decimalCount = math.min(decimalCount + 1, 16)
      updateAllLabels()
    }
  }
  private val decimalLabel: Label = new Label(s"Decimals: $decimalCount") {
    visible = false
  }
  private val copyButton: Button = new Button("Copy values") {
    visible = false
    onAction = _ => {
      try {
        val clipboard = Clipboard.systemClipboard
        val content = new ClipboardContent()
        content.putString(s"${GlobalVars.textRSquared}$getRSquaredStr\n" +
          s"${GlobalVars.textForGraphLabel}${getFunctionStr(false)}")
        clipboard.content = content
      } catch {
        case e: Throwable =>
      }
    }
  }
  private val secondHBox: HBox = new HBox() { // This takes ~50% of the width, leaving 50% for R^2 value label
    spacing = paddingInt
    children = Seq(copyButton, buttonLess, decimalLabel, buttonMore)
  }
  private val progressStackPane: StackPane = new StackPane() {
    children = Seq(progressBar, progressLabel)
  }
  for (node <- Seq(labelRSquared, labelFunc, progressStackPane, progressBar)) {
    node.hgrow = Priority.Always
    node.maxWidth = Double.MaxValue
  }

  this.padding = Insets(paddingInt)
  this.spacing = paddingInt
  this.children = Seq(new HBox() {
    children = Seq(labelRSquared, secondHBox)
    spacing = paddingInt
  }, new HBox() {
    children = Seq(labelFunc, progressStackPane)
    spacing = paddingInt
  })


  private def getFunctionStr(superscripted: Boolean): String = {
    Plot.regressionSeries.regressionInstance.getCoefficients match {
      case (Some(m), Some(b)) =>

        val bStr = if (math.abs(b) > 1E4) s"%6.${decimalCount}e".format(b) else s"%.${decimalCount}f".format(b)
        val bWithSign = (if (b >= 0) "+" else "") + bStr
        val mStr = if (math.abs(m) > 1E4) s"%6.${decimalCount}e".format(m) else s"%.${decimalCount}f".format(m)
        if (Plot.regressionSeries.isLinear) {
          s"y = $mStr * x $bWithSign"
        } else {
          if (superscripted)
            s"y = $bStr * e${this.expPowerToSuperscript(mStr + "x")}"
          else
            s"y = $bStr * e^($mStr*x)"
        }
      case _ => GlobalVars.textUnknownCoefficients
    }
  }

  private def checkButtonVisibility(): Unit = {
    val visibility = (Plot.regressionSeries.regressionInstance.getCoefficients, Plot.regressionSeries.regressionInstance.rSquared) match {
      case ((Some(_), Some(_)), Some(_)) => true
      case _ => false
    }
    this.copyButton.visible = visibility
    this.decimalLabel.visible = visibility
    this.buttonMore.visible = visibility
    this.buttonLess.visible = visibility
  }

  def updateFunctionLabel(): Unit = {
    // Check whether we're using linear or exponential graph
    this.labelFunc.text = GlobalVars.textForGraphLabel + "\t" + this.getFunctionStr(true)
    this.checkButtonVisibility()
  }

  private def getRSquaredStr: String = {
    Plot.regressionSeries.regressionInstance.rSquared match {
      case Some(r) => s"%.${decimalCount}f".format(r)
      case None => GlobalVars.textUnknownCoefficients
    }
  }

  def updateRSquared(): Unit = {
    this.labelRSquared.text = GlobalVars.textRSquared + "\t" + this.getRSquaredStr
    this.checkButtonVisibility()
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
      case 'E' => 'ᴱ'
      case _ => ' '
    }
  }

  private def expPowerToSuperscript(exp: String): String = {
    exp.foldLeft("")((prev, cur) => prev + this.getSuperscript(cur))
  }
}
