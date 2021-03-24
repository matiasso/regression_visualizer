package regressionmodel.mathematics


object ExponentialRegression extends RegressionModel {

  override def calculateCoefficients(leftX: Boolean): Unit = {
    this.m = None
    this.b = None
    //leftX boolean indicates whether "X;Y" coordinatepairs are given this way or the other way "Y;X"
    val xs = if (leftX) this.getXValues else this.getYValues
    val yValues = if (leftX) this.getYValues else this.getXValues
    //We cannot take logarithms if some values of Y are negative.
    if (yValues.forall(_ >= 0)){
      //Take logarithms and fit a linear model for them
      val ys = if (leftX) this.getYlogs else this.getXlogs
      val xAvg: Double = xs.sum / xs.length
      val yAvg: Double = ys.sum / ys.length

      val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
      val denominator = xs.indices.map(i => (xs(i) - xAvg) * (xs(i) - xAvg)).sum
      if (denominator != 0){
        this.m = Some(nominator / denominator)
        val helperB = yAvg - this.m.get * xAvg
        //Now ln(y)=mx+b
        //We want y=e^(mx+b)=e^b*e^(mx)=B*e^(mx)
        this.b = Some(math.exp(helperB))
      } else {
        //Show zero-division warning here!
      }
    }
  }
}
