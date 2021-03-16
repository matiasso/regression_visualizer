package regressionmodel.gui

import regressionmodel.mathematics.RegressionModel
import regressionmodel.{GlobalVars, PVector}
import scalafx.scene.chart.{ScatterChart, XYChart}
import scalafx.scene.layout.StackPane

class Plot extends StackPane {

  val sc: ScatterChart[Number, Number] =  ScatterChart[Number, Number](GlobalVars.xAxis, GlobalVars.yAxis)
  sc.setTitle("Regression model by Matias")
  val series: XYChart.Series[Number, Number] = GlobalVars.pointSeries
  val testArr:Array[PVector] = new Array[PVector](16)
  series.getData.clear()
  for (x <- testArr.indices){
    val i = -8 + x
    testArr(x) = new PVector(i, i+6*(0.5-math.random()))
    series.getData.add(XYChart.Data(testArr(x).x, testArr(x).y))
  }
  series.setName("Points")
  val regrSeries: XYChart.Series[Number, Number] = new XYChart.Series()
  val regrInstance = new RegressionModel(testArr)
  regrInstance.calculateCoefficients()
  val coef:(Double, Double) = regrInstance.getCoefficients
  println("Coefficients: " + coef._1 + ", " + coef._2)
  for (x <- -100 to 100){
    regrSeries.getData.add(XYChart.Data(x/10.0, coef._1*(x/10.0)+coef._2))
  }
  regrSeries.setName("Regression")
  sc.getData.addAll(series, regrSeries)
  this.children = sc

}
