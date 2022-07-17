package utils

import utils.Constants.Regex.{allSpecialCharactersRegex, allWhitespaceRegex}

object TextUtils {

  def normalizeText(text: String): Vector[String] = {
    text.split(allWhitespaceRegex)
      .flatMap { textElement =>
        textElement.replaceAll(allSpecialCharactersRegex, " ")
          .split(allWhitespaceRegex)
          .filter(_.nonEmpty)
          .map(_.toLowerCase)
      }.toVector
  }

}
