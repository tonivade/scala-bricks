package tomby.scala.bricks

import scala.collection.mutable.HashMap

class Board(val height: Int, val width: Int, generator: ColorGenerator) {

  private val tiles = Array.ofDim[Tile](height, width)
  private val bricks = new HashMap[Position, String]

  for (i <- 0 until height) {
    for (j <- 0 until width) {
      val position = new Position(j, i)
      val color = generator.nextColor
      tiles(i)(j) = new Tile(position, color)
      bricks += position -> color
    }
  }

  def position(x: Int, y: Int): Tile = {
    if (y >= 0 && y < height && x >= 0 && x < width) {
      tiles(y)(x)
    } else {
      null
    }
  }

  def click(x: Int, y: Int): Boolean = {
    val tile = position(x, y)
    if (tile != null) {
      search(tile)

      clean(); fall(); shift()
    }
    gameover()
  }

  private def search(current: Tile) {
    if (current.navegable) {
      for (neighbor <- current.position.neighbors) {
        navegate(current, neighbor)
      }
    }
  }

  private def navegate(current: Tile, other: Position) {
    val tile = position(other.x, other.y)
    if (current.adjacent(tile, (t: Tile) => { t.mark = true; bricks -= t.position })) {
      search(tile)
    }
  }

  private def clean() {
    for (x <- 0 until width) {
      for (y <- 0 until height) {
        position(x, y).clean()
      }
    }
  }

  private def fall() {
    for (x <- 0 until width) {
      for (y <- height - 1 to 1 by -1) {
        if (position(x, y).empty) {
          set(position(x, y), position(x, nextFall(x, y - 1)))
        }
      }
    }
  }

  private def nextFall(x: Int, start: Int): Int = {
    for (y <- start to 1 by -1) {
      if (!position(x, y).empty) {
        return y
      }
    }
    return 0
  }

  private def shift() {
    for (x <- 0 until width - 1) {
      if (position(x, height - 1).empty) {
        val next = nextShift(x + 1)
        for (y <- 0 until height) {
          set(position(x, y), position(next, y))
        }
      }
    }
  }

  def nextShift(x: Int): Int = {
    for (i <- x until width - 1) {
      if (!position(i, height - 1).empty) {
        return i
      }
    }
    width - 1
  }

  private def set(a: Tile, b: Tile) {
    a.color = b.color
    bricks -= b.position
    bricks += a.position -> a.color
    b.color = null
  }

  private def gameover(): Boolean = {
    for (i <- bricks.keySet) {
      val current = position(i.x, i.y)
      for (neighbor <- current.position.neighbors) {
        if (adjacent(current, neighbor)) {
          return false
        }
      }
    }
    return true
  }

  private def adjacent(current: Tile, other: Position): Boolean = {
    current.adjacent(position(other.x, other.y), null)
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
        result.append(position(x, y))
      }
      result.append("\n")
    }
    result.toString
  }
}

object Board {
  def main(args: Array[String]) {
    println("Board")
    val generator = new DefaultColorGenerator(Array("R", "G", "B"))
    val board = new Board(12, 10, generator)
    println(board)
    for (i <- 11 to 0 by -1) {
      println("click " + i)
      board.click(0, i)
      println(board)
    }
  }
}