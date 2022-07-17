import java.io.File
import java.util.Scanner
import scala.io.Source
import scala.util.{Failure, Success, Try}

object Main {

  private val allWhitespaceRegex = "\\s+"
  private val allSpecialCharactersRegex = "[^A-Za-z0-9\\s_]"
  private val commandExitApplication = ":quit"
  private val topFilesCountLimit = 10

  def main(args: Array[String]): Unit = {
    Try {
      //Read arguments
      val directoryToIndex = args.headOption.getOrElse(throw new IllegalArgumentException("Missing first argument: directory to index"))

      //Read files in directory
      val indexedDirectory = new File(directoryToIndex)
      val indexedFiles = if (indexedDirectory.exists && indexedDirectory.isDirectory) {
        val files = indexedDirectory.listFiles.filter(_.isFile).toVector
        println(s"The following files were found in given directory '$directoryToIndex'")
        files.foreach(file => println(file.getName))
        files
      } else {
        throw new IllegalArgumentException(s"Given directory to index '$directoryToIndex' doesn't exist or is not a directory")
      }

      println("Indexing files in directory...")

      //Convert list of files to list of contents
      val filesContent = indexedFiles.map { file =>
        val fileSource = Source.fromFile(file)
        val fileContent = fileSource.getLines
          .mkString(" ")
          .split(allWhitespaceRegex)
          .toVector

        val enrichedFileContent = fileContent.flatMap { fileElement =>
          fileElement.replaceAll(allSpecialCharactersRegex, " ")
            .split(allWhitespaceRegex)
            .filter(_.nonEmpty)
            .map(_.toLowerCase)
        }

        fileSource.close

        FileContent(file.getName, enrichedFileContent)
      }

      //Convert list of contents to list of maps
      val indexedFilesAsMaps = filesContent.map { fileContent =>
        val wordsWithIndex = fileContent.words.zipWithIndex

        wordsWithIndex.foldLeft(Map.empty[String, WordInFileStats]) { case (indexedWords, (word, index)) =>
          indexedWords.get(word) match {
            case Some(indexedWord) =>
              val wordWithUpdatedIndexes = indexedWord.copy(
                indexes = indexedWord.indexes :+ index
              )
              indexedWords + (word -> wordWithUpdatedIndexes)
            case None =>
              indexedWords + (word -> WordInFileStats(fileContent.fileName, List(index)))
          }
        }
      }

      //Merge maps of different files into one
      val completeIndex = indexedFilesAsMaps.flatMap(_.toSeq)
        .groupBy { case (key, _) => key }
        .view
        .mapValues { values =>
          values.map { case (_, wordInFileStats) => wordInFileStats }
        }.toMap

      println(s"Indexing complete. Please enter phrase you would like to search in indexed files. To exit application use command: $commandExitApplication")

      //Allow user input for phrase searching
      val inputScanner = new Scanner(System.in)
      var isRunning = true

      do {
        print("search> ")
        val userInput = inputScanner.nextLine

        if (userInput == commandExitApplication) {
          isRunning = false
        } else {
          //Enrich user input
          val enrichedUserInputs = userInput.split(allWhitespaceRegex) //TODO processing similar to input files, unify
            .flatMap { inputElement =>
              inputElement.replaceAll(allSpecialCharactersRegex, " ")
                .split(allWhitespaceRegex)
                .filter(_.nonEmpty)
                .map(_.toLowerCase)
            }.toVector

          //Search index for user input and display result
          val searchResult = enrichedUserInputs.flatMap(completeIndex.get)
            .flatten
            .groupBy(_.fileName)
            .toSeq
            .map { case (fileName, wordInFileStats) =>
              fileName -> wordInFileStats.size
            }
            .map { case (fileName, userInputWordCount) =>
              val fileFitPercentage = (userInputWordCount * 100) / enrichedUserInputs.size
              fileName -> fileFitPercentage
            }
            .sortBy { case (_, fileFitPercentage) => fileFitPercentage }(Ordering[Int].reverse)
            .take(topFilesCountLimit)

          //Print search result
          if (searchResult.isEmpty) {
            println("No matches found")
          } else {
            searchResult.foreach { case (fileName, fileFitPercentage) =>
              println(s"$fileName - $fileFitPercentage%")
            }
          }
        }
      } while (isRunning)
    } match {
      case Success(_) => println("Application has been stopped successfully")
      case Failure(exception) => println(s"Application terminated abnormally due to exception - ${exception.getMessage}")
    }
  }

}

case class FileContent(fileName: String, words: Vector[String])

case class WordInFileStats(fileName: String, indexes: List[Int])