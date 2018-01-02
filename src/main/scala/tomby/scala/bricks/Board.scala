package tomby.scala.bricks

import scala.collection.mutable.HashMap
import scala.collection.mutable.MutableList

trait BoardDSL extends Iterable[Tile] {
  val height: Int
  val width: Int
  def atPosition(x: Int, y: Int) : Option[Tile]
  def click(x: Int, y: Int): Boolean
}

class Board(val height: Int, val width: Int)(implicit generator: ColorGenerator) extends BoardDSL {

  private val bricks = new HashMap[Position, Tile]

  for (x <- 0 until width) {
    for (y <- 0 until height) {
      val position = Position(x, y)
      val color = generator.nextColor
      bricks += position -> Tile(position, color)
    }
  }
  
  def iterator: Iterator[Tile] = {
    bricks.values.iterator
  }

  def atPosition(x: Int, y: Int): Option[Tile] = {
    bricks.get(Position(x, y))
  }

  def click(x: Int, y: Int): Boolean = {
    atPosition(x, y).map { tile =>
      search(tile)
      fall() 
      shift()
    }
    gameover()
  }

  private def search(current: Tile) {
    for (neighbor <- current.position.neighbors) {
      navegate(current, neighbor)
    }
  }

  private def navegate(current: Tile, other: Position) {
    atPosition(other.x, other.y).map { tile => {
        if (current.adjacent(tile)) {
          bricks -= current.position
          bricks -= tile.position
          search(tile)
        }
      }
    }
  }

  private def fall() {
    for (x <- 0 until width) {
      for (y <- height - 1 to 1 by -1) {
        for {
          a <- atPosition(x, y)
          b <- atPosition(x, nextFall(x, y - 1))
        } yield set(a, b)
      }
    }
  }

  private def nextFall(x: Int, start: Int): Int = {
    for (y <- start to 1 by -1) {
      if (!atPosition(x, y).isDefined) {
        return y
      }
    }
    return 0
  }

  private def shift() {
    for (x <- 0 until width - 1) {
      if (atPosition(x, height - 1).isDefined) {
        val next = nextShift(x + 1)
        for (y <- 0 until height) {
          for {
            a <- atPosition(x, y)
            b <- atPosition(next, y)
          } yield set(a, b)
        }
      }
    }
  }

  def nextShift(x: Int): Int = {
    for (i <- x until width - 1) {
      if (!atPosition(i, height - 1).isDefined) {
        return i
      }
    }
    width - 1
  }

  private def set(a: Tile, b: Tile) {
    bricks -= b.position
    bricks += a.position -> Tile(a.position, b.color)
  }

  private def gameover(): Boolean = {
    for (current <- bricks.values) {
      for (neighbor <- current.position.neighbors) {
        val other = atPosition(neighbor.x, neighbor.y)
        if (other.isDefined && current.adjacent(other.get)) {
          return false
        }
      }
    }
    return true
  }

  override def toString(): String = {
    val result = new StringBuilder

    result.append("  ")
    for (x <- 0 until width) {
      result.append(x)
    }
    result.append("\n")

    for (y <- 0 until height) {
      if (y < 10) {
        result.append(" ")
      }
      result.append(y)
      for (x <- 0 until width) {
        result.append(atPosition(x, y).fold(" ")(_.color))
      }
      result.append("\n")
    }
    result.toString
  }
}

object Board {
  def main(args: Array[String]) {
    println("Board")
    implicit val generator = new DefaultColorGenerator(Array("R", "G", "B"))
    val board = new Board(height = 8, width = 10)
    println(board)
    for (i <- 11 to 0 by -1) {
      println("click " + i)
      board.click(0, i)
      println(board)
    }
  }
}
