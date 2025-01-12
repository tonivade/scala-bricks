package tomby.scala.bricks

import scalafx.Includes._
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.scene.paint.{Color => FxColor}
import scalafx.scene.shape.Rectangle
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.Pane
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.ButtonType
import tomby.scala.bricks.Matrix._

object ClickEmAll extends JFXApp3 {

  private var matrix: Matrix = Matrix(10, 15).shuffle(ColorGenerator.randomColor)
  private val tileSize = 20
  private val boardHeight = matrix.height * tileSize
  private val boardWidth = matrix.width * tileSize

  private val pane = new Pane {
    children = paint
  }

  pane.handleEvent(MouseEvent.MouseClicked) {
    event: MouseEvent => {
      val x = (event.sceneX / tileSize).toInt - 1
      val y = matrix.height - (event.sceneY / tileSize).toInt
      matrix = run(matrix, Position(x, y))
      pane.children = paint
      if (matrix.gameOver) if (matrix.isEmpty) win else gameOver
    }
  }

  override def start(): Unit = {
    val padding = tileSize * 2
    stage = new JFXApp3.PrimaryStage {
      title.value = "Click'em all!!"
      scene = new Scene(boardWidth + padding, boardHeight + padding) {
        fill = FxColor.White
        root = pane
      }
    }
  }

  private def gameOver: Unit = {
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

  private def paint: Seq[Rectangle] = matrix.tiles.map(toRectangle)

  private def toRectangle(tile: Tile): Rectangle =
    new Rectangle {
      x = tileSize + (tile.position.x * tileSize)
      y = boardHeight - (tile.position.y * tileSize)
      width = tileSize
      height = tileSize
      fill = toColor(tile)
    }

  private def toColor(tile: Tile): FxColor = tile.color match {
    case Red => FxColor.Red
    case Green => FxColor.Green
    case Blue => FxColor.Blue
    case Yellow => FxColor.Yellow
  }
}
