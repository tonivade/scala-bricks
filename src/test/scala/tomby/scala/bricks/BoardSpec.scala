package tomby.scala.bricks

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import tomby.scala.bricks.MatrixOps._

class BoardSpec extends FlatSpec with Matchers {
  
  "A Matrix" should "be empty at the beginning" in {
    val matrix = Matrix(3, 3)
    
    println(matrix.mkString)
    
    matrix.isEmpty should be (true)
  }
  
  "A Matrix" should "be not empty when shuffle" in {
    val matrix = Matrix(3, 3, 
        Seq(Tile(Position(0, 0), Blue),
            Tile(Position(1, 0), Blue),
            Tile(Position(2, 0), Blue),
            Tile(Position(0, 1), Yellow),
            Tile(Position(1, 1), Yellow),
            Tile(Position(2, 1), Yellow),
            Tile(Position(0, 2), Red),
            Tile(Position(1, 2), Red),
            Tile(Position(2, 2), Red)))
            
    println(matrix.mkString)

    matrix.isEmpty should be (false)
    matrix.atPosition(Position(1, 2)) should be (Some(Tile(Position(1, 2), Red)))
    matrix.isPresent(Position(1, 2)) should be (true)
  }
  
  "A Matrix" should "move tiles to position" in {
    val matrix0 = Matrix(3, 3, Seq(Tile(Position(0, 0), Red)))
    
    println(matrix0.mkString)
    val matrix1 = matrix0.atPosition(Position(0, 0)).map(matrix0.move(_, Position(1,1))).get
    println(matrix1.mkString)
    
    matrix0.atPosition(Position(0, 0)) should be (Some(Tile(Position(0, 0), Red)))
    matrix1.atPosition(Position(1, 1)) should be (Some(Tile(Position(1, 1), Red)))
  }
  
  "A Matrix" should "move column of tiles" in {
    val matrix0 = Matrix(3, 3, 
        Seq(Tile(Position(0, 0), Red), 
            Tile(Position(0, 1), Blue),
            Tile(Position(0, 2), Yellow)))
    
    println(matrix0.mkString)
    val matrix1 = matrix0.moveColumn(0, 2)
    println(matrix1.mkString)
    
    matrix1.column(0).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq())
    matrix1.column(2).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq(Red, Blue, Yellow))
  }
  
  "A Matrix" should "move row of tiles" in {
    val matrix0 = Matrix(3, 3, 
        Seq(Tile(Position(0, 0), Red), 
            Tile(Position(1, 0), Blue),
            Tile(Position(2, 0), Yellow)))
    
    println(matrix0.mkString)
    val matrix1 = matrix0.moveRow(0, 2)
    println(matrix1.mkString)
    
    matrix1.row(0).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq())
    matrix1.row(2).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq(Red, Blue, Yellow))
  }
  
  "A Matrix" should "fall tiles" in {
    val matrix0 = Matrix(3, 3, 
        Seq(Tile(Position(0, 2), Red), 
            Tile(Position(1, 2), Blue),
            Tile(Position(2, 2), Yellow)))
    
    println(matrix0.mkString)
    val matrix1 = fall(matrix0)
    println(matrix1.mkString)
    
    matrix1.row(2).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq())
    matrix1.row(0).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq(Red, Blue, Yellow))
  }
  
  "A Matrix" should "shift tiles" in {
    val matrix0 = Matrix(3, 3, 
        Seq(Tile(Position(2, 0), Red), 
            Tile(Position(2, 1), Blue),
            Tile(Position(2, 2), Yellow)))
    
    println(matrix0.mkString)
    val matrix1 = shift(matrix0)
    println(matrix1.mkString)
    
    matrix1.column(2).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq())
    matrix1.column(0).flatMap(matrix1.atPosition(_).toSeq).map(_.color) should be (Seq(Red, Blue, Yellow))
  }
}
