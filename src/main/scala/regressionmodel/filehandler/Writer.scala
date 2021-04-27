package regressionmodel.filehandler

import java.awt.image.BufferedImage

// Abstract trait that could be used for more writer types later on
trait Writer {

  def saveImage(img: BufferedImage): Unit
}
