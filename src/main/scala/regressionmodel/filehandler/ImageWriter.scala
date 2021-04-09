package regressionmodel.filehandler

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File

class ImageWriter(filePath: File) extends Writer {

  override def saveImage(img: BufferedImage): Unit = {
    ImageIO.write(img, "png", filePath)
  }
}
