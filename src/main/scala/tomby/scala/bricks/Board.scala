package tomby.scala.bricks

import scala.collection.mutable.HashMap

trait BoardDSL extends Iterable[Tile] {
  val height: Int
  val width: Int
  def atPosition(x: Int, y: Int) : Option[Tile]
  def click(x: Int, y: Int): Boolean
}

class Board(val height: Int, val width: Int)(implicit nextColor: Position => String) extends BoardDSL {

  private val bricks = new HashMap[Position, Tile]

  for (x <- 0 until width) {
    for (y <- 0 until height) {
      val position = Position(x, y)
      val color = nextColor(position)
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
    val adjacentTiles = atPosition(x, y).map(visit(_, Set())).getOrElse(Set())
    clean(adjacentTiles)
    fall() 
    shift()
    gameover()
  }
  
  private def visit(tile: Tile, visited: Set[Tile]): Set[Tile] = {
    val tiles = search(tile)
    val _visited = visited + tile
    tiles ++ tiles.filterNot(visited.contains(_)).flatMap(visit(_, _visited))
  }

  private def search(current: Tile): Set[Tile] = 
    for {
      adjacent <- neighbors(current).filter(current.adjacent(_))
    } yield adjacent
  
  private def neighbors(current: Tile): Set[Tile] = 
    for {
      position <- current.position.neighbors
      tile <- atPosition(position.x, position.y)
    } yield tile

  private def clean(adjacentTiles: Set[Tile]) = 
    adjacentTiles.foreach(bricks -= _.position)

  private def fall() {
    for (y <- 1 until height) {
      for (x <- 0 until width) {
        for {
          tile <- atPosition(x, y)
          _y <- nextY(x, y)
        } yield move(tile, Position(x, _y))
      }
    }
  }

  private def nextY(x: Int, y: Int): Option[Int] = {
    for (_y <- 0 until y) {
      if (atPosition(x, _y).isEmpty) {
        return Some(_y)
      }
    }
    None
  }
    
  private def shift() {
    for (x <- 1 until width) {
      for (y <- 0 until height) {
        for {
          tile <- atPosition(x, y)
          _x <- nextX(x, y)
        } yield move(tile, Position(_x, y))
      }
    }
  }

  private def nextX(x: Int, y: Int): Option[Int] = {
    for (_x <- 0 until x) {
      if (atPosition(_x, y).isEmpty) {
        return Some(_x)
      }
    }
    None
  }

  private def move(tile: Tile, position: Position) {
    bricks -= tile.position
    bricks += position -> Tile(position, tile.color)
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

    for (y <- height - 1 until -1 by -1) {
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
    implicit val nextColor: Position => String = new ColorGenerator(Array("R", "G", "B")).randomColor
    val board = new Board(height = 5, width = 5)
    println(board)
  }
}
