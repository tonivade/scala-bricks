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
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage

import scala.collection.mutable.MutableList
import javafx.scene.paint.Color

class BoardGUI extends Application {

  private val colors = Array("R", "G", "B", "Y")
  private implicit val generator: Position => String = new ColorGenerator(colors).randomColor
  private val board: BoardDSL = new Board(15, 10)(generator)
  private val size = 20

  override def start(primaryStage: Stage) = {
    primaryStage.setTitle("Click'em all!!")

    val root = new Group
    val scene = new Scene(root, (board.width + 2) * size, (board.height + 2) * size, Color.WHITE)

    root.getChildren.clear()
    paint().foreach(root.getChildren().add(_))

    scene.setOnMouseClicked(
      new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent) {
          val x = (event.getSceneX() / size).intValue - 1
          val y = (event.getSceneY() / size).intValue - 1
          println(x + ":" + y)
          if (board.click(x.intValue, y.intValue)) {
            gameover()
          }
          root.getChildren.clear()
          paint().foreach(root.getChildren().add(_))
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

  def getColor(position: Position): Color = {
    board.atPosition(position.x, position.y).fold(Color.WHITE)(toColor)
  }
  
  def toColor(tile: Tile): Color = {
    tile.color match {
        case "R" => Color.RED
        case "G" => Color.GREEN
        case "B" => Color.BLUE
        case "Y" => Color.YELLOW
      }
  }

  private def paint(): Seq[Rectangle] = {
    board.map(toRectangle).toSeq
  }
  
  private def toRectangle(tile: Tile): Rectangle = {
    val rectangle = new Rectangle(size, size, getColor(tile.position))
    rectangle.setX(size + (tile.position.x * size))
    rectangle.setY(size + (tile.position.y * size))
    rectangle
  }
}

object BoardGUI {
  def main(args: Array[String]) {
    Application.launch(classOf[BoardGUI], args: _*)
  }
}
