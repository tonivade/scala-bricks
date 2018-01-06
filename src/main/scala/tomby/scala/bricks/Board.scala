package tomby.scala.bricks

import scala.collection.mutable.HashMap

trait BoardDSL extends Iterable[Tile] {
  val height: Int
  val width: Int
  def shuffle(nextColor: Position => Color)
  def click(x: Int, y: Int)
  def gameover(): Boolean
  def win(): Boolean
}

class Board(val height: Int, val width: Int) extends BoardDSL {

  private val bricks = new HashMap[Position, Tile]

  def shuffle(nextColor: Position => Color) =
    matrix().foreach(p => bricks += p -> Tile(p, nextColor(p)))
  
  def iterator: Iterator[Tile] = bricks.values.iterator

  def click(x: Int, y: Int) = {
    lookup(Position(x, y)).foreach(clean)
    fall()
    shift()
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

  private def clean(position: Position) = bricks -= position

  private def fall() = 
    for {
      column <- columns()
    } yield fallCol(column)
    
  private def fallCol(column: Seq[Position]) = 
    for {
      position <- column
      tile <- atPosition(position)
      _y <- nextY(column, position)
    } yield move(tile, Position(position.x, _y))
  
  private def nextY(column: Seq[Position], position : Position) : Option[Int] = 
    column.takeWhile(_.y < position.y).find(!isPresent(_)).map(_.y)
    
  private def shift() = 
    for {
      column <- columns()
    } yield shiftColumn(column)
    
  private def shiftColumn(column: Seq[Position]) =
    for {
      _x <- nextX(column.head)
    } yield moveColumn(column, _x)
    
  private def nextX(pos: Position): Option[Int] = 
    row(0).takeWhile(_.x < pos.x).find(!isPresent(_)).map(_.x)
    
  private def moveColumn(column: Seq[Position], x: Int) =
    for {
      position <- column
      tile <- atPosition(position)
    } yield move(tile, Position(x, position.y))

  private def move(tile: Tile, position: Position) = {
    bricks -= tile.position
    bricks += position -> Tile(position, tile.color)
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
    
  def columns(): Seq[Seq[Position]] =
    for {
      x <- 0 until width
    } yield column(x)
    
  def column(x: Int) : Seq[Position] =
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
        result.append(atPosition(Position(x, y)).fold(" ") {
          tile => {
            tile.color match {
              case Red() => "R"
              case Blue() => "B"
              case Yellow() => "Y"
              case Green() => "G"
            }
          }
        })
      }
      result.append("\n")
    }

    result.toString
  }
}
