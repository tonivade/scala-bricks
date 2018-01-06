package tomby.scala.bricks

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.Pane
import scalafx.scene.input.MouseEvent

object BoardGUI extends JFXApp {

  private val colors = Array("R", "G", "B", "Y")
  private implicit val generator: Position => String = new ColorGenerator(colors).randomColor
  private val board: BoardDSL = new Board(15, 10)
  private val _size = 20
  private val _height = board.height * _size;
  private val _width = board.width * _size;
  private val _padding = _size * 2
  
  private val pane = new Pane {
    children = paint()
  }
  
  pane.handleEvent(MouseEvent.MouseClicked) {
    event: MouseEvent => {
      val _x = (event.getSceneX / _size).toInt - 1
      val _y = board.height - (event.getSceneY / _size).toInt
      if (board.click(_x, _y)) {
        gameover()
      }
      pane.children = paint()
    }
  }
  
  stage = new JFXApp.PrimaryStage {
    title.value = "Click'em all!!"
    scene = new Scene(_width + _padding, _height + _padding) {
      fill = Color.White
      root = pane
    }
  }

  def gameover() {
    new Alert(AlertType.Information, "GAME OVER!").showAndWait()
  }
  
  def toColor(tile: Tile): Color = {
    tile.color match {
      case "R" => Color.Red
      case "G" => Color.Green
      case "B" => Color.Blue
      case "Y" => Color.Yellow
    }
  }

  private def paint(): Seq[Rectangle] = {
    board.map(toRectangle).toSeq
  }
  
  private def toRectangle(tile: Tile): Rectangle = {
    new Rectangle {
      x = _size + (tile.position.x * _size)
      y = _height - (tile.position.y * _size)
      width = _size
      height = _size
      fill = toColor(tile)
    }
  }
}
