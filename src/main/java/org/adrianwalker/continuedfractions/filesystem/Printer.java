package org.adrianwalker.continuedfractions.filesystem;

import java.sql.SQLException;
import java.util.Arrays;

public final class Printer {

  private Printer() {
  }

  public static void print(final File[] files) throws SQLException {

    for (File file : files) {
      char[] indent = new char[2 * (file.getLevel() - files[0].getLevel()) + 1];
      Arrays.fill(indent, ' ');
      indent[indent.length - 1] = '/';

      System.out.printf("%s%s\n", String.valueOf(indent), file.getName());
    }
  }
}
