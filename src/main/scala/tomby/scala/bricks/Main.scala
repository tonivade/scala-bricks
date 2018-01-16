package tomby.scala.bricks

import cats.effect.IO
import cats.data.StateT
import cats.data.StateT.liftF
import scala.io.StdIn.readLine

object Main extends App {
  
  val read: StateT[IO, Matrix, String] = liftF(IO(readLine()))
  val readInt: StateT[IO, Matrix, Int] = liftF(IO(readLine().toInt))
  def print(str: String): StateT[IO, Matrix, Unit] = liftF(IO(println(str)))
  val quit: StateT[IO, Matrix, Unit] = liftF(IO(Unit))

  def click(position: Position) = StateT[IO, Matrix, Unit] {
    matrix => IO(MatrixOps.click(matrix, position), ())
  }
  
  def matrixToString = StateT[IO, Matrix, String] {
    matrix => IO(matrix, matrix.mkString)
  }
  
  val gameover = StateT[IO, Matrix, Boolean] {
    matrix => IO(matrix, matrix.gameover())
  }
  
  val printMatrix: StateT[IO, Matrix, Unit] = 
    for {
      str <- matrixToString
      _   <- print(str)
    } yield ()
  
  val exit = 
    for {
      _   <- printMatrix
      _   <- print("Gameover!!!")
    } yield ()
    
  val shuffle = StateT[IO, Matrix, Unit] {
    	matrix => IO(matrix.shuffle(ColorGenerator.randomColor), ())
  }
  
  val loop: StateT[IO, Matrix, Unit] = 
    for {
      _   <- printMatrix
      _   <- print("Please enter X")
      x   <- readInt
      _   <- print("Please enter Y")
      y   <- readInt
      _   <- click(Position(x, y))
      go  <- gameover
      _   <- if (go) exit else loop
    } yield ()
  
  val mainLoop: StateT[IO, Matrix, Unit] = 
    for {
      _ <- print("Let's play a game")
      _ <- shuffle
      _ <- loop
      _ <- print("Do you want to play again?")
      s <- read
      _ <- if (s == "y") mainLoop else quit
    } yield()
  
  val result = mainLoop.runS(Matrix(5, 5)).unsafeRunSync()
}
