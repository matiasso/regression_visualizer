package regressionmodel

class PVector(var first: Double, var second: Double) {

  def magnitude: Double = {
    math.sqrt(first * first + second * second)
  }

  def add(v: PVector): Unit = {
    this.first += v.first
    this.second += v.second
  }

  def add(dx: Double, dy: Double): Unit = {
    this.first += dx
    this.second += dy
  }

  def subtract(v: PVector): Unit = {
    this.first -= v.first
    this.second -= v.second
  }

  def subtract(dx: Double, dy: Double): Unit = {
    this.first -= dx
    this.second -= dy
  }

  def multiply(d: Double): Unit = {
    this.first *= d
    this.second *= d
  }

  def divide(d: Double): Unit = {
    this.first /= d
    this.second /= d
  }

  def distance(v: PVector): Double = {
    val dx = this.first - v.first
    val dy = this.second - v.second
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
    val a = this.first
    this.first = this.second
    this.second = a
  }

  override def toString: String = "FIRST:" + this.first.toString + ", SECOND:" + this.second.toString

}

object PVector {

  def reverse(arr: Array[PVector]): Unit = {
    arr.foreach(_.reverseXY())
  }
}