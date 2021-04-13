package regressionmodel.gui

import org.scalafx.extras.BusyWorker.SimpleTask
import org.scalafx.extras.{BusyWorker, onFX, onFXAndWait}
import regressionmodel.GlobalVars
import scalafx.scene.chart.XYChart

import java.util.concurrent.Future

class DataPointSeries(name: String) extends PointSeries(name) {

  val index = 0

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

          if (i % 200 == 0) {
            //onFXAndWait is better, but it's way too slow for big datafiles, so I use it only every 200th time
            onFXAndWait {
              if (GlobalVars.leftCoordinateIsX) {
                series.getData.add(XYChart.Data(point.first, point.second))
              } else {
                series.getData.add(XYChart.Data(point.second, point.first))
              }
            }
          } else {
            // onFX Usually freezes, but with busyWorker and 20ms wait time here and there this should work!
            onFX {
              if (GlobalVars.leftCoordinateIsX) {
                series.getData.add(XYChart.Data(point.first, point.second))
              } else {
                series.getData.add(XYChart.Data(point.second, point.first))
              }
            }
          }
          // The window freezes with big datafiles so I'd like to slow this down and show progress instead
          i += 1
          progress() = i * 1.0 / Plot.dataPoints.length
          message() = s"Progress: $i/${Plot.dataPoints.length}(${math.round(progress.toDouble * 100)}%)"
          if (i % 25 == 0) {
            // Sleep on the busyWorker so the window doesn't freeze!
            Thread.sleep(20)
          }
        }
      }

      override def onFinish(result: Future[Unit], successful: Boolean): Unit = {
        BottomPanel.progressBar.visible = false
        println("Drawing finished!")
      }
    })

  }
}
