package boot

import searchengine.FileTextSearchEngine
import utils.Constants.Commands.commandExitApplication

import java.util.Scanner
import scala.util.{Failure, Success, Try}

object Main {

  def main(args: Array[String]): Unit = {
    Try {
      val directoryToIndex = args.headOption.getOrElse(throw new IllegalArgumentException("Missing first argument: directory to index"))
      val searchEngine = FileTextSearchEngine(directoryToIndex)
      println(s"Indexing complete. Please enter phrase you would like to search in indexed files. To exit application use command: $commandExitApplication")

      val inputScanner = new Scanner(System.in)
      var isRunning = true

      do {
        print("search> ")
        val userInput = inputScanner.nextLine

        if (userInput == commandExitApplication) {
          isRunning = false
        } else {
          searchEngine.searchText(userInput)
        }
      } while (isRunning)
    } match {
      case Success(_) => println("Application has been stopped successfully")
      case Failure(exception) => println(s"Application terminated abnormally due to exception - ${exception.getMessage}")
    }
  }

}
