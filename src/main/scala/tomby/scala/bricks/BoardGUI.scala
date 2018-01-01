package tomby.scala.bricks

import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBoxBuilder
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage

class BoardGUI extends Application {

  private val board: BoardDSL = new Board(new DefaultColorGenerator(Array("R", "G", "B", "Y")))(15, 10)

  override def start(primaryStage: Stage) = {
    primaryStage.setTitle("Click'em all!!")

    val size = 20
    val root = new Group
    val scene = new Scene(root, (board.width + 2) * size, (board.height + 2) * size, Color.WHITE)

    paint(size, root)

    scene.setOnMouseClicked(
      new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent) {
          val x = (event.getSceneX() / size).intValue - 1
          val y = (event.getSceneY() / size).intValue - 1
          println(x + ":" + y)
          if (board.click(x.intValue, y.intValue)) {
            gameover()
          }
          paint(size, root)
        }
      })

    primaryStage.setScene(scene)
    primaryStage.show()
  }

  def gameover() {
    val dialogStage = new Stage()
    dialogStage.initModality(Modality.WINDOW_MODAL)

    val builder = VBoxBuilder.create()
    val yes = new Button("Yeah!")
    builder.children(new Text("GAME OVER!"), yes)
    builder.alignment(Pos.CENTER)
    builder.padding(new Insets(5))

    yes.setOnAction(
      new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent) {
          dialogStage.close()
        }
      })

    dialogStage.setScene(new Scene(builder.build()))
    dialogStage.show();
  }

  def getColor(x: Int, y: Int): Color = {
    board.position(x, y).color match {
      case "R" => Color.RED
      case "G" => Color.GREEN
      case "B" => Color.BLUE
      case "Y" => Color.YELLOW
      case _ => Color.WHITE
    }
  }

  private def paint(size: Int, root: Group) {
    root.getChildren().clear()

    for (i <- 0 until board.height) {
      for (j <- 0 until board.width) {
        val r = new Rectangle
        r.setX(size + (j * size))
        r.setY(size + (i * size))
        r.setHeight(size)
        r.setWidth(size)
        r.setFill(getColor(j, i))
        root.getChildren().add(r)
      }
    }
  }
}

object BoardGUI {
  def main(args: Array[String]) {
    Application.launch(classOf[BoardGUI], args: _*)
  }
}
