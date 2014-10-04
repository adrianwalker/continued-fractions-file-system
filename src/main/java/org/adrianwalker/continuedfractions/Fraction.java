package org.adrianwalker.continuedfractions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class Fraction {

  private static final int[][] F = {
    {0, 1},
    {1, 1}
  };
  private static final int PRECISION = 16;

  private Fraction() {
  }

  public static int[] fraction(final int n, final int d) {

    return new int[]{n, d};
  }

  public static int[] fraction(final int[] pf, final int c, final int[] spf) {

    return fraction(pf[0] + c * spf[0], pf[1] + c * spf[1]);
  }

  public static int[] fraction(final int[] c) {

    int[] f = F[0];

    for (int i = c.length - 1; i >= 0; i--) {
      f = add(F[1], invert(f));
      f = add(fraction(c[i], 1), invert(f));
    }

    return f;
  }

  public static int[] continued(final int[] f) {

    int[] a = f;

    List<Integer> c = new ArrayList<>();
    while (a[0] > 0) {

      int i = a[0] / a[1];
      c.add(i);

      a = invert(subtract(a, fraction(i, 1)));
      a = invert(subtract(a, F[1]));
    }

    return toArray(c);
  }

  public static BigDecimal decimal(final int[] f) {

    return BigDecimal.valueOf(f[0]).divide(BigDecimal.valueOf(f[1]), PRECISION, RoundingMode.HALF_DOWN);
  }

  public static int[] add(final int[] f1, final int[] f2) {

    return fraction((f1[0] * f2[1]) + (f2[0] * f1[1]), f2[1] * f1[1]);
  }

  public static int[] subtract(final int[] f1, final int[] f2) {

    return fraction((f1[0] * f2[1]) - (f2[0] * f1[1]), f1[1] * f2[1]);
  }

  public static int[] invert(final int[] f) {

    return fraction(f[1], f[0]);
  }

  private static int[] toArray(final List<Integer> l) {

    int[] a = new int[l.size()];

    for (int i = 0; i < a.length; i++) {
      a[i] = l.get(i);
    }

    return a;
  }
}
