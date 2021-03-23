package regressionmodel.mathematics


object ExponentialRegression extends RegressionModel {

  override def calculateCoefficients(leftX: Boolean): Unit = {
    //Implement this method later!
    this.m = 1
    this.b = 2
  }
}
