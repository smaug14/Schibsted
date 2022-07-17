package utils

import org.scalatest.funsuite.AnyFunSuite
import utils.FilesUtils.{extractFileContent, readFilesInDirectory}

import java.io.File
import scala.util.{Failure, Success, Try}

class FilesUtilsTest extends AnyFunSuite {

  test("readFilesInDirectory - All files are read from correct directory") {
    val filesInDirectory = readFilesInDirectory("src/test/resources/filetextengine")
    assert(filesInDirectory.size == 12)
  }

  test("readFilesInDirectory - Exception is thrown when wrong directory given") {
    Try {
      readFilesInDirectory("src/test/resources/notexistingdirectory")
    } match {
      case Success(_) => assert(false)
      case Failure(exception) if exception.isInstanceOf[IllegalArgumentException] => assert(true)
      case _ => assert(false)
    }
  }

  test("extractFileContent - File content is extracted from file") {
    val testFile = new File("src/test/resources/filetextengine/test_file_8.txt")
    val fileContent = extractFileContent(testFile)
    assert(fileContent.fileName == "test_file_8.txt")
    assert(fileContent.words == Vector("asdaad", "ddddd", "ddsdsda", "aaaa"))
  }

}
