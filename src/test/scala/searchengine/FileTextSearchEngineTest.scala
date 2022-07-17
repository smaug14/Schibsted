package searchengine

import org.scalatest.funsuite.AnyFunSuite

class FileTextSearchEngineTest extends AnyFunSuite {

  test("searchText - Search should return files containing word from input text in correct order") {

    val searchEngine = FileTextSearchEngine.apply("src/test/resources/filetextengine")
    val results = searchEngine.searchText("c funny b d")

    assert(results.size == 3)

    assert(results(0).fileName == "test_file_11.txt")
    assert(results(1).fileName == "test_file_2.txt")
    assert(results(2).fileName == "test_file.txt")

    assert(results(0).percentageScore == 100)
    assert(results(1).percentageScore == 50)
    assert(results(2).percentageScore == 25)

  }

  test("searchText - Search should return only top 10 results in correct order") {

    val searchEngine = FileTextSearchEngine.apply("src/test/resources/filetextengine")
    val results = searchEngine.searchText("aaaa")

    assert(results.size == 10)
    assert(results.forall(_.percentageScore == 100))
  }

}
