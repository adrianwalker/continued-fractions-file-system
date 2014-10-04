package org.adrianwalker.continuedfractions;

public final class Matrix {

  private Matrix() {
  }

  public static int[][] matrix(
          final int M00, final int M01,
          final int M10, final int M11) {

    return new int[][]{{M00, M01}, {M10, M11}};
  }

  public static int[][] multiply(final int[][] M1, final int[][] M2) {

    return matrix(
            M1[0][0] * M2[0][0] + M1[0][1] * M2[1][0],
            M1[0][0] * M2[0][1] + M1[0][1] * M2[1][1],
            M1[1][0] * M2[0][0] + M1[1][1] * M2[1][0],
            M1[1][0] * M2[0][1] + M1[1][1] * M2[1][1]
    );
  }

  public static int[][] invert(final int[][] M) {

    return matrix(-M[1][1], M[0][1], M[1][0], -M[0][0]);
  }

  public static int[][] moveSubtree(final int[][] p0, final int m, final int[][] p1, int n, final int[][] M) {

    return multiply(multiply(multiply(p1, matrix(1, 0, m - n, 1)), invert(p0)), M);
  }
}
