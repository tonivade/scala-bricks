package tomby.scala.bricks

import scala.collection.mutable.HashMap

trait Board extends Iterable[Tile] {
  val height: Int
  val width: Int
  def shuffle(nextColor: Position => Color): Board
  def click(x: Int, y: Int): Board
  def gameover(): Boolean
  def win(): Boolean
}

object Board {
  def apply(width: Int, height: Int): Board =
    new InmutableBoard(width, height)
}

private class InmutableBoard(val width: Int, val height: Int, val tiles: Seq[Tile] = Seq()) extends Board {

  private val bricks = Map(tiles.map(tile => tile.position -> tile): _*)

  def shuffle(nextColor: Position => Color): Board =
    new InmutableBoard(height, width, matrix().map(p => Tile(p, nextColor(p))))
  
  def iterator: Iterator[Tile] = bricks.values.iterator

  def click(x: Int, y: Int): Board = {
    val _bricks = bricks -- lookup(Position(x, y))
    val _board = new MutableBoard(height, width, _bricks.values.toSeq)
    _board.fall()
    _board.shift()
    _board.toBoard()
  }
  
  def win(): Boolean = bricks.isEmpty

  def gameover(): Boolean = 
    bricks.values.flatMap(search(_)).isEmpty
  
  private def lookup(position: Position): Set[Position] =
    atPosition(position).map(visit(_, Set())).getOrElse(Set())
  
  private def atPosition(pos: Position): Option[Tile] = 
    bricks.get(pos)
  
  private def isPresent(pos: Position): Boolean =
    atPosition(pos).isDefined
    
  private def visit(tile: Tile, visited: Set[Tile]): Set[Position] = {
    val tiles = search(tile)
    tiles.map(_.position) ++ tiles.filterNot(visited.contains(_)).flatMap(visit(_, visited + tile))
  }

  private def search(current: Tile): Set[Tile] = 
    for {
      adjacent <- neighbors(current).filter(current.adjacent(_))
    } yield adjacent
  
  private def neighbors(current: Tile): Set[Tile] = 
    for {
      pos <- current.position.neighbors
      tile <- atPosition(pos)
    } yield tile
    
  def matrix() : Seq[Position] = 
    for {
      x <- 0 until width
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
        result.append(atPosition(Position(x, y)).fold(" ") {
          tile => {
            tile.color match {
              case Red => "R"
              case Green => "G"
              case Blue => "B"
              case Yellow => "Y"
            }
          }
        })
      }
      result.append("\n")
    }

    result.toString
  }
}

private class MutableBoard(val height: Int, val width: Int, tiles: Seq[Tile] = Seq()) {
  private val bricks = HashMap(tiles.map(tile => tile.position -> tile): _*)

  def fall() = 
    for {
      column <- columns()
    } yield fallCol(column)
    
  def shift() = 
    for {
      column <- columns()
    } yield shiftColumn(column)
    
  def toBoard(): Board = new InmutableBoard(height, width, bricks.values.toSeq)
    
  private def fallCol(column: Seq[Position]) =
    for {
      position <- column
      tile <- bricks.get(position)
      _y <- nextY(column, position)
    } yield move(tile, Position(position.x, _y))
  
  private def nextY(column: Seq[Position], position : Position) : Option[Int] = 
    column.takeWhile(_.y < position.y).find(!isPresent(_)).map(_.y)
    
  private def shiftColumn(column: Seq[Position]) =
    for {
      _x <- nextX(column.head)
    } yield moveColumn(column, _x)
    
  private def nextX(pos: Position): Option[Int] = 
    row(0).takeWhile(_.x < pos.x).find(!isPresent(_)).map(_.x)
    
  private def moveColumn(column: Seq[Position], x: Int) =
    for {
      position <- column
      tile <- bricks.get(position)
    } yield move(tile, Position(x, position.y))

  private def move(tile: Tile, position: Position) = {
    bricks -= tile.position
    bricks += position -> Tile(position, tile.color)
  }
  
  private def rows(): Seq[Seq[Position]] =
    for {
      y <- 0 until height
    } yield row(y)
    
  private def row(y: Int): Seq[Position] =
    for {
      x <- 0 until width
    } yield Position(x, y)
    
  private def columns(): Seq[Seq[Position]] =
    for {
      x <- 0 until width
    } yield column(x)
    
  private def column(x: Int): Seq[Position] =
    for {
      y <- 0 until height
    } yield Position(x, y)
  
  private def isPresent(pos: Position): Boolean =
    bricks.get(pos).isDefined
}
