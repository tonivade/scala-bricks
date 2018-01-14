package tomby.scala.bricks

import cats.effect.IO
import cats.data.StateT
import cats.data.StateT.liftF
import scala.io.StdIn.readLine

object Main extends App {
  
  val gets = IO(readLine())
  def puts(str: String) = IO(println(str))
  def toInt(str: String) = IO(str.toInt)

  def click(position: Position) = StateT[IO, Matrix, Unit] {
    matrix => IO(MatrixOps.click(matrix, position), ())
  }
  
  def matrixToString = StateT[IO, Matrix, String] {
    matrix => IO(matrix, matrix.mkString)
  }
  
  val gameover = StateT[IO, Matrix, Boolean] {
    matrix => IO(matrix, matrix.gameover())
  }
  
  val exit = for {
    str <- matrixToString
    _   <- liftF(puts(str))
    _   <- liftF(puts("Gameover!!!"))
    _   <- liftF(IO(Unit))
  } yield ()
  
  val loop: StateT[IO, Matrix, Unit] = 
    for {
      str <- matrixToString
      _   <- liftF(puts(str))
      _   <- liftF(puts("Please enter X"))
      _x  <- liftF(gets)
      _   <- liftF(puts("Please enter Y"))
      _y  <- liftF(gets)
      x   <- liftF(toInt(_x))
      y   <- liftF(toInt(_y))
      _   <- click(Position(x, y))
      go  <- gameover
      _   <- if (go) exit else loop
    } yield ()
  
  val start = Matrix(3, 3).shuffle(ColorGenerator.randomColor)
  val result = loop.runS(start).unsafeRunSync()
}
