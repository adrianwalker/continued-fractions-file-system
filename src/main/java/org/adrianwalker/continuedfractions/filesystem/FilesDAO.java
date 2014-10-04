package org.adrianwalker.continuedfractions.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.adrianwalker.continuedfractions.Fraction.decimal;
import static org.adrianwalker.continuedfractions.Fraction.fraction;
import static org.adrianwalker.continuedfractions.Matrix.matrix;
import static org.adrianwalker.continuedfractions.Matrix.moveSubtree;
import static org.adrianwalker.continuedfractions.filesystem.Path.parent;
import static org.adrianwalker.continuedfractions.filesystem.Path.sibling;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

public final class FilesDAO {

  private static final String WRITE
          = "insert into files (id, nv, dv, sid, snv, sdv, level, name, content) "
          + "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String READ
          = "select id, nv, dv, sid, snv, sdv, level, name, content "
          + "from files "
          + "where id = ?";
  private static final String TREE
          = "select id, nv, dv, sid, snv, sdv, level, name, content "
          + "from files "
          + "where id >= ? "
          + "and id < (select sid from files where id = ?)";
  private static final String CHILDREN
          = TREE
          + " and level = ?";
  private static final String CHILD
          = CHILDREN
          + " and name = ?";
  private static final String LAST_CHILD
          = CHILDREN
          + " order by id desc "
          + "limit 1";
  private static final String RENAME
          = "update files "
          + "set name = ? "
          + "where id = ?";
  private static final String MOVE
          = "update files "
          + "set id = ?, nv = ?, dv = ?, sid = ?, snv = ?, sdv = ?, level = ? "
          + "where id = ?";
  private static final String REMOVE
          = "delete from files "
          + "where id >= ? "
          + "and id < (select sid from files where id = ?)";
  private static final String CLEAR = "delete from files";
  private static final String ORDER_BY_ID = " order by id";
  private static final String ORDER_BY_NAME = " order by name";

  private final Connection connection;
  private final LargeObjectManager lom;

  public FilesDAO(final Connection connection) throws SQLException {

    this.connection = connection;
    this.lom = ((org.postgresql.PGConnection) connection).getLargeObjectAPI();
  }

  private PreparedStatement prepareStatement(final String sql) throws SQLException {

    return connection.prepareStatement(sql);
  }

  public int clear() throws SQLException {

    PreparedStatement remove = prepareStatement(CLEAR);

    return remove.executeUpdate();
  }

  public File write(final String filename, final int... path) throws SQLException {

    int[] f = fraction(path);
    int[] sf = fraction(sibling(path));
    int level = path.length;

    BigDecimal id = decimal(f);
    BigDecimal sid = decimal(sf);
    long content = lom.createLO();

    PreparedStatement write = prepareStatement(WRITE);
    write.setBigDecimal(1, id);
    write.setInt(2, f[0]);
    write.setInt(3, f[1]);
    write.setBigDecimal(4, sid);
    write.setInt(5, sf[0]);
    write.setInt(6, sf[1]);
    write.setInt(7, level);
    write.setString(8, filename);
    write.setLong(9, content);

    write.executeUpdate();

    return new File(id, f[0], f[1], sid, sf[0], sf[1], level, filename, content);
  }

  public File read(final int... path) throws SQLException {

    BigDecimal id = decimal(fraction(path));

    PreparedStatement read = prepareStatement(READ);
    read.setBigDecimal(1, id);

    ResultSet rs = read.executeQuery();

    return toFile(rs);
  }

  public File[] children(final int... path) throws SQLException {

    BigDecimal id = decimal(fraction(path));

    PreparedStatement children = prepareStatement(CHILDREN + ORDER_BY_NAME);
    children.setBigDecimal(1, id);
    children.setBigDecimal(2, id);
    children.setInt(3, path.length + 1);

    return toFiles(children.executeQuery());
  }

  public File child(final String name, final int... path) throws SQLException {

    BigDecimal id = decimal(fraction(path));

    PreparedStatement child = prepareStatement(CHILD);
    child.setBigDecimal(1, id);
    child.setBigDecimal(2, id);
    child.setInt(3, path.length + 1);
    child.setString(4, name);

    return toFile(child.executeQuery());
  }

  public File lastChild(final int... path) throws SQLException {

    BigDecimal id = decimal(fraction(path));

    PreparedStatement lastChild = prepareStatement(LAST_CHILD);
    lastChild.setBigDecimal(1, id);
    lastChild.setBigDecimal(2, id);
    lastChild.setInt(3, path.length + 1);

    return toFile(lastChild.executeQuery());
  }

  public File[] tree(final int... path) throws SQLException {

    BigDecimal id = decimal(fraction(path));

    PreparedStatement tree = prepareStatement(TREE + ORDER_BY_ID);
    tree.setBigDecimal(1, id);
    tree.setBigDecimal(2, id);

    return toFiles(tree.executeQuery());
  }

  public int rename(final String filename, final int... path) throws SQLException {

    BigDecimal id = decimal(fraction(path));

    PreparedStatement rename = prepareStatement(RENAME);
    rename.setString(1, filename);
    rename.setBigDecimal(2, id);

    return rename.executeUpdate();
  }

  public int remove(final int... path) throws SQLException {

    for (File file : tree(path)) {
      lom.delete(file.getContent());
    }

    BigDecimal id = decimal(fraction(path));

    PreparedStatement remove = prepareStatement(REMOVE);
    remove.setBigDecimal(1, id);
    remove.setBigDecimal(2, id);

    return remove.executeUpdate();
  }

  public int[] move(final int[] from, final int[] to) throws SQLException, IOException {

    return moveCopy(MOVE, from, to);
  }

  public int[] copy(final int[] from, final int[] to) throws SQLException, IOException {

    return moveCopy(WRITE, from, to);
  }

  private int[] moveCopy(final String sql, final int[] from, final int[] to) throws SQLException, IOException {

    int[] p = parent(from);
    int[] pf0 = fraction(p);
    int[] psf0 = fraction(sibling(p));
    int[] pf1 = fraction(to);
    int[] psf1 = fraction(sibling(to));

    int m = 1;
    File lc = lastChild(to);
    if (null != lc) {
      m = (lc.getSnv() - pf1[0]) / psf1[0];
    }

    int n = from[from.length - 1];

    int[][] p0 = matrix(pf0[0], psf0[0], pf0[1], psf0[1]);
    int[][] p1 = matrix(pf1[0], psf1[0], pf1[1], psf1[1]);

    PreparedStatement move = prepareStatement(sql);

    for (File file : tree(from)) {

      int[][] M0 = matrix(file.getNv(), file.getSnv(), file.getDv(), file.getSdv());
      int[][] M1 = moveSubtree(p0, m, p1, n, M0);

      int[] f = fraction(M1[0][0], M1[1][0]);
      int[] sf = fraction(M1[0][1], M1[1][1]);
      BigDecimal id = decimal(f);
      BigDecimal sid = decimal(sf);
      int level = to.length + (file.getLevel() - p.length);

      move.setBigDecimal(1, id);
      move.setInt(2, f[0]);
      move.setInt(3, f[1]);
      move.setBigDecimal(4, sid);
      move.setInt(5, sf[0]);
      move.setInt(6, sf[1]);
      move.setInt(7, level);

      switch (sql) {

        case MOVE:
          move.setBigDecimal(8, file.getId());
          break;

        case WRITE:
          long oid = lom.createLO();

          move.setString(8, file.getName());
          move.setLong(9, oid);

          Stream.copy(getInputStream(file.getContent()), getOutputStream(oid));

          break;
      }

      move.addBatch();
    }

    return move.executeBatch();
  }

  public InputStream getInputStream(final long oid) throws SQLException {

    LargeObject obj = lom.open(oid, LargeObjectManager.READ);

    return obj.getInputStream();
  }

  public OutputStream getOutputStream(final long oid) throws SQLException {

    LargeObject obj = lom.open(oid, LargeObjectManager.WRITE);

    return obj.getOutputStream();
  }

  private File toFile(final ResultSet rs) throws SQLException {

    File file = null;

    if (rs.next()) {
      file = new File(rs.getBigDecimal(1), rs.getInt(2), rs.getInt(3),
              rs.getBigDecimal(4), rs.getInt(5), rs.getInt(6),
              rs.getInt(7), rs.getString(8), rs.getLong(9));
    }

    return file;
  }

  private File[] toFiles(final ResultSet rs) throws SQLException {

    List<File> l = new ArrayList<>();

    while (rs.next()) {
      l.add(new File(rs.getBigDecimal(1), rs.getInt(2), rs.getInt(3),
              rs.getBigDecimal(4), rs.getInt(5), rs.getInt(6),
              rs.getInt(7), rs.getString(8), rs.getLong(9)));
    }

    return l.toArray(new File[l.size()]);
  }
}
