package tomby.scala.bricks

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.{Color => FxColor}
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.Pane
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.ButtonType

object BoardGUI extends JFXApp {

  private val board: BoardDSL = new Board(15, 10)
  private val _size = 20
  private val _height = board.height * _size
  private val _width = board.width * _size
  private val _padding = _size * 2
  
  board.shuffle(ColorGenerator.randomColor)

  private val pane = new Pane {
    children = paint()
  }
  
  pane.handleEvent(MouseEvent.MouseClicked) {
    event: MouseEvent => {
      val _x = (event.getSceneX / _size).toInt - 1
      val _y = board.height - (event.getSceneY / _size).toInt
      board.click(_x, _y)
      pane.children = paint()
      if (board.gameover()) if (board.win()) win() else gameover()
    }
  }
  
  stage = new JFXApp.PrimaryStage {
    title.value = "Click'em all!!"
    scene = new Scene(_width + _padding, _height + _padding) {
      fill = FxColor.White
      root = pane
    }
  }

  private def gameover() {
    val result = new Alert(AlertType.Confirmation, "GAME OVER!") {
      initOwner(stage)
      headerText = "GAME OVER!"
      contentText = "Do you want to play again?"
    }.showAndWait()
    result match {
      case Some(ButtonType.OK) => {
        board.shuffle(ColorGenerator.randomColor)
        pane.children = paint()
      }
      case _ => ()
    }
  }

  private def win() {
    new Alert(AlertType.Information, "YOU WIN!").showAndWait()
  }
  
  private def toColor(tile: Tile): FxColor = {
    tile.color match {
      case Red() => FxColor.Red
      case Green() => FxColor.Green
      case Blue() => FxColor.Blue
      case Yellow() => FxColor.Yellow
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
