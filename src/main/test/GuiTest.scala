import org.scalatest.flatspec.AnyFlatSpec
import regressionmodel.PVector
import regressionmodel.mathematics.LinearRegression

class GuiTest extends AnyFlatSpec {

  behavior of "Linear regression"
  val linear = new LinearRegression()
  it should "be None before calculations" in {
    assert(linear.getCoefficients == (None, None))
  }
  it should "throw exception when set is empty" in {
    linear.calculateCoefficients()
    assert(linear.getCoefficients == (None, None))
  }
  it should "be (Some(1), Some(2)) when given simple example" in {
    linear.setData(Array[PVector](new PVector(0, 2), new PVector(1, 3), new PVector(2, 4)))
    assert(linear.getCoefficients == (Some(1), Some(2)))
  }


}