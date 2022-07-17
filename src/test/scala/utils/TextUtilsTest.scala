package utils

import org.scalatest.funsuite.AnyFunSuite
import utils.TextUtils.normalizeText

class TextUtilsTest extends AnyFunSuite {

  test("normalizeText - Text should be converted to basic words") {
    val fileText = "a b\na.b\na,b\na/b\na-b\na b c"
    val normalizedText = normalizeText(fileText)
    assert(normalizedText == Vector("a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "a", "b", "c"))
  }

}
