package tomby.scala.bricks

import scala.annotation.tailrec

object Matrix {
  import cats.data.State
  
  def click(matrix: Matrix, pos: Position) = {
    val program = for {
      _pos <- lookup(pos)
      _    <- clean(_pos)
      _    <- fallS
      _    <- shiftS
    } yield ()
    
    program.runS(matrix).value
  }
  
  def lookup(pos: Position) = State[Matrix, Seq[Position]] {
    matrix => (matrix, matrix.adjacent(pos))
  }
  
  def clean(xs: Seq[Position]) = State[Matrix, Unit] {
    matrix => (matrix.clean(xs), ())
  }
  
  val fallS = State[Matrix, Unit] {
    matrix => (fall(matrix), ())
  }
  
  val shiftS = State[Matrix, Unit] {
    matrix => (shift(matrix), ())
  }
  
  def fall(matrix: Matrix): Matrix = fallCol(matrix, 0)
  
  def shift(matrix: Matrix): Matrix = shiftCol(matrix, 0)
  
  @tailrec
  def fallCol(matrix: Matrix, col: Int): Matrix = 
    if (col < matrix.width) 
      fallCol(fallTile(matrix, col, 0), col + 1)
    else 
      matrix
  
  @tailrec
  def shiftCol(matrix: Matrix, col: Int): Matrix = 
    if (col < matrix.width) 
      shiftCol(tryMoveCol(matrix, col).fold(matrix)(identity), col + 1)
    else 
      matrix
    
  @tailrec
  def fallTile(matrix: Matrix, col: Int, top: Int): Matrix = 
    if (top < matrix.height) 
      fallTile(tryMove(matrix, col, top).fold(matrix)(identity), col, top + 1)
    else 
      matrix
  
  def tryMoveCol(matrix: Matrix, col: Int): Option[Matrix] = 
    for {
      nextX <- nextX(matrix, col)
    } yield matrix.moveColumn(col, nextX)

  def tryMove(matrix: Matrix, col: Int, top: Int): Option[Matrix] = 
    for {
      tile <- matrix.atPosition(Position(col, top))
      nextY <- nextY(matrix, col, top)
    } yield matrix.move(tile, Position(col, nextY))
  
  def nextX(matrix: Matrix, left: Int): Option[Int] = 
    matrix.row(0).takeWhile(_.x < left).find(!matrix.isPresent(_)).map(_.x)
  
  def nextY(matrix: Matrix, col: Int, top: Int) = 
     matrix.column(col).takeWhile(_.y < top).find(!matrix.isPresent(_)).map(_.y)
}

case class Matrix(width: Int, height: Int, tiles: Seq[Tile] = Seq()) {
  private val bricks = Map(tiles.map(tile => tile.position -> tile): _*)

  def shuffle(nextColor: Position => Color): Matrix =
    Matrix(width, height, matrix.map(p => Tile(p, nextColor(p))))
  
  def move(tile: Tile, position: Position): Matrix = 
    clean(Seq(tile.position)).addTiles(Seq(Tile(position, tile.color)))
  
  def moveColumn(fromX: Int, toX: Int): Matrix = {
    val newColumn = for {
      position <- column(fromX)
      tile <- atPosition(position)
    } yield Tile(Position(toX, position.y), tile.color)
    
    cleanColumn(fromX).addTiles(newColumn)
  }
  
  def moveRow(fromY: Int, toY: Int): Matrix = {
    val newRow = for {
      position <- row(fromY)
      tile <- atPosition(position)
    } yield Tile(Position(position.x, toY), tile.color)
    
    clean(row(fromY)).addTiles(newRow)
  }
  
  def cleanRow(y: Int): Matrix = clean(row(y))

  def cleanColumn(x: Int): Matrix = clean(column(x))
  
  def clean(positions: Seq[Position]): Matrix = 
    Matrix(width, height, (bricks -- positions).values.toSeq)
  
  def addTiles(toadd: Seq[Tile]): Matrix = {
    val newTiles = toadd.map(tile => tile.position -> tile)
    val _bricks = bricks ++ newTiles
    Matrix(width, height, _bricks.values.toSeq)
  }
  
  def adjacent(position: Position): Seq[Position] =
    atPosition(position).map(visit(_, Set())).getOrElse(Set()).toSeq
  
  def atPosition(position: Position): Option[Tile] = bricks.get(position)
  
  def isPresent(pos: Position): Boolean = bricks.get(pos).isDefined
    
  def isEmpty: Boolean = bricks.isEmpty
  
  def gameover(): Boolean = bricks.values.flatMap(search(_)).isEmpty
    
  def matrix: Seq[Position] = 
    for {
      x <- 0 until width
      y <- 0 until height
    } yield Position(x, y)
  
  def rows: Seq[Seq[Position]] =
    for {
      y <- 0 until height
    } yield row(y)
    
  def row(y: Int): Seq[Position] =
    for {
      x <- 0 until width
    } yield Position(x, y)
    
  def columns: Seq[Seq[Position]] =
    for {
      x <- 0 until width
    } yield column(x)
    
  def column(x: Int): Seq[Position] =
    for {
      y <- 0 until height
    } yield Position(x, y)
    
  private def visit(tile: Tile, visited: Set[Tile]): Set[Position] = {
    val tiles = search(tile)
    tiles.map(_.position) ++ tiles.filterNot(visited.contains(_)).flatMap(visit(_, visited + tile))
  }

  private def search(current: Tile): Set[Tile] = 
    neighbors(current).filter(current.adjacent(_))
  
  private def neighbors(current: Tile): Set[Tile] = 
    for {
      pos <- current.position.neighbors
      tile <- atPosition(pos)
    } yield tile
  
  import Console.{RESET, RED_B, GREEN_B, BLUE_B, YELLOW_B }

  def mkString(): String = {
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
              case Red => s"${RESET}${RED_B} ${RESET}"
              case Green => s"${RESET}${GREEN_B} ${RESET}"
              case Blue => s"${RESET}${BLUE_B} ${RESET}"
              case Yellow => s"${RESET}${YELLOW_B} ${RESET}"
            }
          }
        })
      }
      result.append("\n")
    }

    result.toString
  }
}
