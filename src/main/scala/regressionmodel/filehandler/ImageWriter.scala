package regressionmodel.filehandler

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageWriter(filePath: File) extends Writer {

  override def saveImage(img: BufferedImage): Unit = {
    ImageIO.write(img, "png", filePath)
  }
}
