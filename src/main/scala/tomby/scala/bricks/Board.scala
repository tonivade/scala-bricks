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

  matrix().foreach { position => {
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
    println(toString)
    clean(adjacentTiles)
    println(toString)
    fall()
    println(toString)
    shift()
    println(toString)
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

  private def fall() = 
    for {
      col <- cols()
    } yield fallCol(col)
    
  private def fallCol(col: Seq[Position]) = 
    for {
      pos <- col
      tile <- atPosition(pos.x, pos.y)
      _y <- nextY(col, pos)
    } yield move(tile, Position(pos.x, _y))
  
  private def nextY(col: Seq[Position], pos : Position) : Option[Int] = 
    col.filter(p => p.y < pos.y).find(p => atPosition(p.x, p.y).isEmpty).map(_.y)
    
  private def shift() = {
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
    
  def matrix() : Seq[Position] = 
    for {
      y <- 0 until height
      x <- 0 until width
    } yield Position(x, y)
  
  def rows() : Seq[Seq[Position]] =
    for {
      y <- 0 until height
    } yield row(y)
    
  def row(y: Int) : Seq[Position] =
    for {
      x <- 0 until width
    } yield Position(x, y)
    
  def cols(): Seq[Seq[Position]] =
    for {
      x <- 0 until width
    } yield col(x)
    
  def col(x: Int) : Seq[Position] =
    for {
      y <- 0 until height
    } yield Position(x, y)

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
    val board = new Board(height = 3, width = 3)
    println(board.matrix())
  }
}
