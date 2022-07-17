package utils

import utils.TextUtils.normalizeText

import java.io.File
import scala.io.Source

object FilesUtils {

  def readFilesInDirectory(directoryPath: String): Vector[File] = {
    val directory = new File(directoryPath)
    if (directory.exists && directory.isDirectory) {
      val files = directory.listFiles.filter(_.isFile).toVector
      println(s"The following files were found in given directory '$directoryPath'")
      files.foreach(file => println(file.getName))
      files
    } else {
      throw new IllegalArgumentException(s"Given directory '$directoryPath' doesn't exist or is not a directory")
    }
  }

  def extractFileContent(file: File): FileContent = {
    val fileSource = Source.fromFile(file)
    val fileContent = fileSource.getLines.mkString(" ")
    val normalizedFileContent = normalizeText(fileContent)
    fileSource.close

    FileContent(file.getName, normalizedFileContent)
  }

}

case class FileContent(fileName: String, words: Vector[String])

case class WordInFileStats(fileName: String, indexes: List[Int])
