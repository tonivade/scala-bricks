package tomby.scala.bricks

case class Position(x: Int, y: Int) {

  def neighbors: Set[Position] = Set(up, down, right, left)
  
  def up: Position = {
    Position(x, y + 1)
  }
  
  def down: Position = {
    Position(x, y - 1)
  }
  
  def right: Position = {
    Position(x + 1, y)
  }
  
  def left: Position = {
    Position(x - 1, y)
  }

  def distance(other: Position): Double = {
    val diffx = x - other.x
    val diffy = y - other.y
    math.sqrt(math.pow(diffx, 2) + math.pow(diffy, 2))
  }
  
  def adjacent(other: Position): Boolean = {
    distance(other) == 1.0
  }
}