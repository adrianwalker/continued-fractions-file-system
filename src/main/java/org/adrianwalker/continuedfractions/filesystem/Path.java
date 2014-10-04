package org.adrianwalker.continuedfractions.filesystem;

import java.util.Arrays;

public class Path {

  private Path() {
  }

  public static int[] sibling(final int[] path) {

    int[] spath = Arrays.copyOf(path, path.length);
    spath[spath.length - 1]++;

    return spath;
  }

  public static int[] parent(final int[] path) {

    return Arrays.copyOf(path, path.length - 1);
  }

  public static int[] range(final int[] path, final int from, final int to) {

    return Arrays.copyOfRange(path, from, to);
  }
}
