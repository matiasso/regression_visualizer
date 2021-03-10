package regressionModel

class PVector(var x:Double, var y:Double) {

  def magnitude() = {
    math.sqrt(x*x + y*y)
  }

  def add(v: PVector) = {
    this.x += v.x
    this.y += v.y
  }

  def add(dx: Double, dy: Double) = {
    this.x += dx
    this.y += dy
  }

  def subtract(v: PVector) = {
    this.x -= v.x
    this.y -= v.y
  }

  def subtract(dx: Double, dy: Double) = {
    this.x -= dx
    this.y -= dy
  }

  def multiply(d: Double) = {
    this.x *= d
    this.y *= d
  }

  def divide(d: Double) = {
    this.x /= d
    this.y /= d
  }

  def distance(v: PVector) = {
    val dx = this.x - v.x
    val dy = this.y - v.y
    math.sqrt(dx*dx + dy*dy)
  }

  def normalize() = {
    val m = this.magnitude()
    if (m != 0 && m != 1){
      this.divide(m)
    }
  }

  def setMagnitude(len: Double) = {
    this.normalize()
    this.multiply(len)
  }

  override def toString: String = "X:" + this.x.toString + ", Y:" + this.y.toString

}
