package regressionmodel.gui

import org.scalafx.extras.BusyWorker.SimpleTask
import org.scalafx.extras.{BusyWorker, onFX, onFXAndWait}
import regressionmodel.GlobalVars
import scalafx.scene.chart.XYChart

import java.util.concurrent.Future

class DataPointSeries(name: String) extends PointSeries(name) {

  protected val index = 0

  override def update(): Unit = {
    this.clear()
    if (BottomPanel.busyWorker == null) {
      // Now GlobalVars.myView isn't null
      BottomPanel.busyWorker = new BusyWorker("Busyworker", GlobalVars.myView) {
        BottomPanel.progressBar.progress <== progressValue
        BottomPanel.progressLabel.text <== progressMessage
      }
    }
    BottomPanel.busyWorker.doTask("Datapoint-drawing")(new SimpleTask[Unit] {
      override def call(): Unit = {
        BottomPanel.progressBar.visible = true
        var i = 0
        for (point <- Plot.dataPoints) {
          if (i % 125 == 0) {
            //onFXAndWait is better, but it's way too slow for big datafiles, so I don't want to use it for all dots
            onFXAndWait {
              series.getData.add(XYChart.Data(point.x, point.y))
            }
          } else {
            // onFX Usually freezes, but with busyWorker and small wait time here and there this should work!
            onFX {
              series.getData.add(XYChart.Data(point.x, point.y))
            }
          }
          i += 1
          progress() = i * 1.0 / Plot.dataPoints.length
          message() = s"Progress: $i/${Plot.dataPoints.length}\t(${math.round(progress.toDouble * 100)}%)"

          // Sleep on the busyWorker so the window doesn't freeze!
          if (i % 25 == 0) {
            Thread.sleep(22)
          }
        }
      }

      override def onFinish(result: Future[Unit], successful: Boolean): Unit = {
        BottomPanel.progressBar.visible = false
        applyStyles()
        println("Drawing finished!")
      }
    })

  }
}
