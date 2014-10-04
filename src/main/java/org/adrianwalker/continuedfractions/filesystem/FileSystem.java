package org.adrianwalker.continuedfractions.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import static org.adrianwalker.continuedfractions.filesystem.Path.range;

public final class FileSystem {

  private static final String SEPERATOR = "/";
  private static final String EMPTY_STRING = "";
  private final FilesDAO dao;
  private final int[] rootPath;

  public FileSystem(final FilesDAO dao, final int... rootPath) throws FileSystemException {

    this.dao = dao;
    this.rootPath = rootPath;

    try {
      if (null == dao.read(rootPath)) {
        dao.write("", rootPath);
      }
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }
  }

  public int[] create(final String path) throws FileSystemException {

    try {
      return path(path, true);
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }
  }

  public File[] list(final String path) throws FileSystemException {

    try {
      return dao.children(path(path, false));
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }
  }

  public File[] tree(final String path) throws FileSystemException {

    try {
      return dao.tree(path(path, false));
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }
  }

  public void write(final String path, final String text) throws FileSystemException {

    OutputStream out = getOutputStream(path);

    try {
      Stream.fromString(text, out);
    } catch (final IOException ioe) {
      throw new FileSystemException(ioe);
    }
  }

  public OutputStream getOutputStream(final String path) throws FileSystemException {

    OutputStream out;

    try {
      out = dao.getOutputStream(dao.read(path(path, true)).getContent());
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }

    return out;
  }

  public String read(final String path) throws FileSystemException {

    InputStream in = getInputStream(path);

    if (null == in) {
      return EMPTY_STRING;
    }

    try {
      return Stream.toString(in);
    } catch (final IOException ioe) {
      throw new FileSystemException(ioe);
    }
  }

  public InputStream getInputStream(final String path) throws FileSystemException {

    InputStream in;

    try {
      in = dao.getInputStream(dao.read(path(path, false)).getContent());
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }

    return in;
  }

  public void delete(final String path) throws FileSystemException {

    try {
      dao.remove(path(path, false));
    } catch (final SQLException sqle) {
      throw new FileSystemException(sqle);
    }
  }

  public void move(final String from, final String to) throws FileSystemException {

    try {
      dao.move(path(from, false), path(to, true));
    } catch (final SQLException | IOException ex) {
      throw new FileSystemException(ex);
    }
  }

  public void copy(final String from, final String to) throws FileSystemException {

    try {
      dao.copy(path(from, false), path(to, true));
    } catch (final SQLException | IOException ex) {
      throw new FileSystemException(ex);
    }
  }

  private int[] path(final String s, final boolean create) throws SQLException {

    String[] names = s.split(SEPERATOR);
    int[] path = range(rootPath, 0, rootPath.length + (names.length > 0 ? names.length - 1 : 0));

    File f = dao.read(rootPath);

    for (int level = 1; level < names.length; level++) {

      File p = f;

      f = dao.child(names[level], range(path, 0, level));

      if (null != f) {

        path[level] = (f.getNv() - p.getNv()) / p.getSnv();

      } else if (null == f && create) {

        path[level] = 1;
        File lc = dao.lastChild(range(path, 0, level));
        if (null != lc) {
          path[level] = (lc.getSnv() - p.getNv()) / p.getSnv();
        }

        f = dao.write(names[level], range(path, 0, level + 1));
      }
    }

    return path;
  }
}
