package tomby.scala.bricks

import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.Scene
import scalafx.scene.paint.{Color => FxColor}
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.Pane
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.ButtonType
import tomby.scala.bricks.Matrix._

object ClickEmAll extends JFXApp {

  private var matrix: Matrix = Matrix(10, 15).shuffle(ColorGenerator.randomColor)
  private val _size = 20
  private val _height = matrix.height * _size
  private val _width = matrix.width * _size
  private val _padding = _size * 2
  
  private val pane = new Pane {
    children = paint
  }
  
  pane.handleEvent(MouseEvent.MouseClicked) {
    event: MouseEvent => {
      val _x = (event.sceneX / _size).toInt - 1
      val _y = matrix.height - (event.sceneY / _size).toInt
      matrix = run(matrix, Position(_x, _y))
      pane.children = paint
      if (matrix.gameover) if (matrix.isEmpty) win else gameover
    }
  }
  
  stage = new JFXApp.PrimaryStage {
    title.value = "Click'em all!!"
    scene = new Scene(_width + _padding, _height + _padding) {
      fill = FxColor.White
      root = pane
    }
  }

  private def gameover: Unit = {
    val result = new Alert(AlertType.Confirmation, "GAME OVER!") {
      initOwner(stage)
      headerText = "GAME OVER!"
      contentText = "Do you want to play again?"
    }.showAndWait()
    result match {
      case Some(ButtonType.OK) => playAgain
      case _ => Platform.exit()
    }
  }

  private def playAgain: Unit = {
    matrix = matrix.shuffle(ColorGenerator.randomColor)
    pane.children = paint
  }

  private def win: Unit =
    new Alert(AlertType.Information, "YOU WIN!").showAndWait()
  
  private def toColor(tile: Tile): FxColor = tile.color match {
    case Red => FxColor.Red
    case Green => FxColor.Green
    case Blue => FxColor.Blue
    case Yellow => FxColor.Yellow
  }

  private def paint: Seq[Rectangle] = matrix.tiles.map(toRectangle)
  
  private def toRectangle(tile: Tile): Rectangle = 
    new Rectangle {
      x = _size + (tile.position.x * _size)
      y = _height - (tile.position.y * _size)
      width = _size
      height = _size
      fill = toColor(tile)
    }
}
