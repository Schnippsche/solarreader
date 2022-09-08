package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.SolarMain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.stream.Stream;

public class FileOperation
{

  /**
   * read the last n lines from a file and return them as a string
   *
   * @param path      the path to the file
   * @param lineCount linecount n
   * @return String with all lines
   * @throws IOException excpetion
   */
  public String lastLinesFromFile(Path path, int lineCount) throws IOException
  {

    LinkedList<String> list = new LinkedList<>();
    try (Stream<String> lines = Files.lines(path))
    {
      lines.forEach(s ->
      {
        if (list.size() > lineCount)
        {
          list.removeFirst();
        }
        list.add(s);
      });
    }
    return String.join(SolarMain.NEWLINE, list);
  }

}
