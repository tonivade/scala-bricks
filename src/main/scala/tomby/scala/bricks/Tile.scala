package tomby.scala.bricks

case class Tile(val position: Position, val color: String) {

  def adjacent(other: Tile): Boolean = {
    position.adjacent(other.position) && color == other.color
  }

}