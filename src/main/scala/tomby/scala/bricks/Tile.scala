package tomby.scala.bricks

class Tile(val position: Position, var color: String) {

  var mark = false

  def adjacent(other: Tile, callback: (Tile) => Unit): Boolean = {
    val result = !empty && other != null && position.adjacent(other.position) && color == other.color
    if (result) {
      if (callback != null) {
        callback(this)
      }
    }
    result
  }

  def empty: Boolean = {
    color == null
  }

  def navegable: Boolean = {
    !empty && !mark
  }

  def clean() {
    if (mark) {
      color = null
      mark = false
    }
  }

  override def toString(): String = {
    if (!empty) {
      return color
    }
    return " "
  }

}