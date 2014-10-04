package org.adrianwalker.continuedfractions.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Stream {

  private static final int BUFFER_SIZE = 1024;
  private static final String ENCODING = "UTF-8";

  private Stream() {
  }

  public static void copy(final InputStream in, final OutputStream out) throws IOException {

    byte[] b = new byte[BUFFER_SIZE];
    int n;

    try {

      while ((n = in.read(b)) > 0) {

        out.write(b, 0, n);
        out.flush();
      }

    } finally {

      in.close();
      out.close();
    }
  }

  public static String toString(final InputStream in) throws IOException {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    copy(in, out);

    return out.toString(ENCODING);
  }

  public static void fromString(final String text, final OutputStream out) throws IOException {

    ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes(ENCODING));
    copy(in, out);
  }
}
