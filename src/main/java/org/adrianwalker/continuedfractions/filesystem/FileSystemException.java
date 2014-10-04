package org.adrianwalker.continuedfractions.filesystem;

public class FileSystemException extends Exception {

  public FileSystemException() {
  }

  public FileSystemException(final String string) {
    super(string);
  }

  public FileSystemException(final String string, final Throwable thrwbl) {
    super(string, thrwbl);
  }

  public FileSystemException(final Throwable thrwbl) {
    super(thrwbl);
  }

  public FileSystemException(final String string, final Throwable thrwbl, final boolean bln, final boolean bln1) {
    super(string, thrwbl, bln, bln1);
  }
}
