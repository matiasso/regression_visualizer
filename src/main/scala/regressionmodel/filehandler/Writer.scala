package regressionmodel.filehandler

import java.awt.image.BufferedImage

trait Writer {

  def saveImage(img: BufferedImage): Unit
}
