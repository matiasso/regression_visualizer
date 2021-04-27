package regressionmodel

// For this project I mainly care about the x and y coordinates which this class holds.
// To improve the accuracy, they should maybe be of type BigDecimal, instead of Double.
class PVector(var x: Double, var y: Double) {

  //Most of these methods are not needed in this project, but could be handy later in some other project
  def magnitude: Double = {
    math.sqrt(x * x + y * y)
  }

  def add(v: PVector): Unit = {
    this.x += v.x
    this.y += v.y
  }

  def add(dx: Double, dy: Double): Unit = {
    this.x += dx
    this.y += dy
  }

  def subtract(v: PVector): Unit = {
    this.x -= v.x
    this.y -= v.y
  }

  def subtract(dx: Double, dy: Double): Unit = {
    this.x -= dx
    this.y -= dy
  }

  def multiply(d: Double): Unit = {
    this.x *= d
    this.y *= d
  }

  def divide(d: Double): Unit = {
    this.x /= d
    this.y /= d
  }

  def distance(v: PVector): Double = {
    val dx = this.x - v.x
    val dy = this.y - v.y
    math.sqrt(dx * dx + dy * dy)
  }

  def normalize(): Unit = {
    val m = this.magnitude
    if (m != 0 && m != 1) {
      this.divide(m)
    }
  }

  def setMagnitude(len: Double): Unit = {
    this.normalize()
    this.multiply(len)
  }

  def reverseXY(): Unit = {
    val a = this.x
    this.x = this.y
    this.y = a
  }

  override def toString: String = s"(X, Y)=($x, $y)"

}

object PVector {

  def reverse(arr: Array[PVector]): Unit = {
    arr.foreach(_.reverseXY())
  }
}