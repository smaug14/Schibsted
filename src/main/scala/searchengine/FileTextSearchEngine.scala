package searchengine

import utils.FilesUtils.{extractFileContent, readFilesInDirectory}
import utils.TextUtils.normalizeText
import utils.{FileContent, WordInFileStats}

class FileTextSearchEngine(indexedFiles: Map[String, Vector[WordInFileStats]]) {

  private val topFilesCountLimit = 10

  def searchText(textInput: String): Unit = {
    val normalizedTextInput = normalizeText(textInput)
    val searchResult = searchTextInIndexedFiles(normalizedTextInput)
    printSearchResult(searchResult)
  }

  private def searchTextInIndexedFiles(textInput: Vector[String]): Vector[FileTextSearchResult] = {
    calculateFilesScore(textInput)
      .sortBy(_.percentageScore)(Ordering[Int].reverse)
      .take(topFilesCountLimit)
  }

  private def calculateFilesScore(textInput: Vector[String]): Vector[FileTextSearchResult] = {
    textInput.flatMap(indexedFiles.get)
      .flatten
      .groupBy(_.fileName)
      .toVector
      .map { case (fileName, wordInFileStats) =>
        fileName -> wordInFileStats.size
      }
      .map { case (fileName, userInputWordCount) =>
        val fileFitPercentage = (userInputWordCount * 100) / textInput.size
        fileName -> fileFitPercentage
      }
      .map { case (fileName, fileFitPercentage) => FileTextSearchResult(fileName, fileFitPercentage) }
  }

  private def printSearchResult(searchResult: Vector[FileTextSearchResult]): Unit = {
    if (searchResult.isEmpty) {
      println("No matches found")
    } else {
      searchResult.foreach { result =>
        println(s"${result.fileName} - ${result.percentageScore}%")
      }
    }
  }

}

object FileTextSearchEngine {

  def apply(directoryToIndex: String): FileTextSearchEngine = {
    new FileTextSearchEngine(indexFilesInDirectory(directoryToIndex))
  }

  private def indexFilesInDirectory(directoryToIndex: String): Map[String, Vector[WordInFileStats]] = {
    val filesToIndex = readFilesInDirectory(directoryToIndex)
    println("Indexing files in directory...")
    val filesContent = filesToIndex.map(extractFileContent)
    val indexedFiles = filesContent.map(indexWordsInFile)
    mergeIndexedFiles(indexedFiles)
  }

  private def indexWordsInFile(fileContent: FileContent): Map[String, WordInFileStats] = {
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

  private def mergeIndexedFiles(indexedFiles: Vector[Map[String, WordInFileStats]]): Map[String, Vector[WordInFileStats]] = {
    indexedFiles.flatMap(_.toSeq)
      .groupBy { case (key, _) => key }
      .view
      .mapValues { values =>
        values.map { case (_, wordInFileStats) => wordInFileStats }
      }.toMap
  }

}

case class FileTextSearchResult(fileName: String, percentageScore: Int)
