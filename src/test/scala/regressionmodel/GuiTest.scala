package regressionmodel

import org.scalatest.flatspec.AnyFlatSpec
import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}

class GuiTest extends AnyFlatSpec {

  behavior of "Linear regression"
  val linear = new LinearRegression()
  it should "be (None, None) before any calculations" in {
    assert(linear.getCoefficients == (None, None))
  }
  it should "throw exception when dataset is empty" in {
    assertThrows[Exception] { linear.calculateCoefficients() }
  }
  it should "throw exception when dataset is defined but invalid" in {
    // Vertical line, impossible to fit y = kx + b line
    linear.setData(Array[PVector](new PVector(0, 1), new PVector(0, 2), new PVector(0, 3)))
    assertThrows[Exception] {
      linear.calculateCoefficients()
    }
  }
  it should "be (None, None) with dataset but with no calculations" in {
    linear.setData(Array[PVector](new PVector(0, 2), new PVector(1, 3), new PVector(2, 4)))
    assert(linear.getCoefficients == (None, None))
  }
  it should "be (Some(1), Some(2)) when given simple linear example" in {
    // linear.calculateCoefficients()
    linear.setData(Array[PVector](new PVector(0, 2), new PVector(1, 3), new PVector(2, 4)))
    linear.calculateCoefficients()
    assert(linear.getCoefficients == (Some(1), Some(2)))
  }
  it should "be (Some(0.81729090909090), Some(1.14118181818182)) and R^2=0.96432526726731" in {
    linear.setData(Array[PVector](new PVector(0, 1.634), new PVector(1, 1.259), new PVector(2, 3.521),
      new PVector(3, 3.283), new PVector(4, 4.138), new PVector(5, 4.903), new PVector(6, 6.375),
      new PVector(7, 7.237), new PVector(8, 7.025), new PVector(9, 8.194), new PVector(10, 9.935)))
    linear.calculateCoefficients()
    val coefs = linear.getCoefficients
    // I got these values from Excel, when I plotted these exact points and drew a trendLine.
    assert(coefs._1.getOrElse(0).toString.startsWith("0.817290909090"))
    assert(coefs._2.getOrElse(0).toString.startsWith("1.141181818181"))
    assert(linear.rSquared.getOrElse(0).toString.startsWith("0.96432526726731"))
  }

  behavior of "Exponential Regression"
  val exponential = new ExponentialRegression()
  it should "be (None, None) before any calculations" in {
    assert(exponential.getCoefficients == (None, None))
  }
  it should "throw exception when dataset is empty" in {
    assertThrows[Exception] { exponential.calculateCoefficients() }
  }
  it should "throw exception when dataset is defined but invalid" in {
    // Vertical line, impossible to fit y = b * e^(mx) line
    exponential.setData(Array[PVector](new PVector(0, 1), new PVector(0, 4), new PVector(0, 8)))
    assertThrows[Exception] {
      exponential.calculateCoefficients()
    }
  }
  it should "throw exception when some Y-values are negative" in {
    exponential.setData(Array[PVector](new PVector(1, 1), new PVector(2, 2), new PVector(3, -0.5), new PVector(4, 4)))
    assertThrows[Exception] {
      exponential.calculateCoefficients()
    }
  }
  it should "be (None, None) with dataset but with no calculations" in {
    exponential.setData(Array[PVector](new PVector(0, 2), new PVector(1, 3.5), new PVector(2, 6)))
    assert(exponential.getCoefficients == (None, None))
  }
  it should "be (Some(0.9), Some(0.75)) when given simple exponential example" in {
    exponential.setData(Array[PVector](new PVector(0, 0.75),
      new PVector(1, 0.75*math.exp(0.9*1)), new PVector(2, 0.75*math.exp(0.9*2))))
    exponential.calculateCoefficients()
    val coefs = exponential.getCoefficients
    assert(math.abs(coefs._1.get - 0.9) < 1E-12) // Margin of error I accept here is 1E-12
    assert(math.abs(coefs._2.get - 0.75) < 1E-12)
  }
  it should "be (Some(0.36703017219179), Some(1.59668459178779)) and R^2=0.99991522867196" in {
    exponential.setData(Array[PVector](new PVector(-1, 1.0945031930644),
      new PVector(0, 1.6188311153941), new PVector(2, 3.3246184655921), new PVector(3, 4.7934955074420),
      new PVector(4, 6.9322954937878), new PVector(5, 9.9957260700099)))
    exponential.calculateCoefficients()
    val coefs = exponential.getCoefficients
    // I got these values from Excel, when I plotted these exact points and drew a trendLine.
    assert(coefs._1.getOrElse(0).toString.startsWith("0.3670301721917"))
    assert(coefs._2.getOrElse(0).toString.startsWith("1.5966845917877"))
    assert(exponential.rSquared.getOrElse(0).toString.startsWith("0.9999152286719"))
  }

}