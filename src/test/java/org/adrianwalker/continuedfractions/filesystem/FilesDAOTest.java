package org.adrianwalker.continuedfractions.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.adrianwalker.continuedfractions.filesystem.Printer.print;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public final class FilesDAOTest {

  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "postgres";

  private static Connection connection;

  public FilesDAOTest() {
  }

  @BeforeClass
  public static void openConnection() throws ClassNotFoundException, SQLException {

    Class.forName(DRIVER);

    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    connection.setAutoCommit(false);
  }

  @AfterClass
  public static void closeConnection() throws SQLException {

    if (null != connection) {
      connection.close();
    }
  }

  @After
  public void commit() throws SQLException {

    connection.commit();
  }

  private void clear() throws SQLException {

    FilesDAO dao = new FilesDAO(connection);
    dao.clear();
  }

  @Test
  public void write() throws SQLException {

    clear();

    FilesDAO dao = new FilesDAO(connection);
    assertNotNull(dao.write("o1", 1));
    assertNotNull(dao.write("o2", 2));
    assertNotNull(dao.write("o2o1", 2, 1));
    assertNotNull(dao.write("o2o2", 2, 2));
    assertNotNull(dao.write("o2o3", 2, 3));
    assertNotNull(dao.write("o2o4", 2, 4));
    assertNotNull(dao.write("o2o4o1", 2, 4, 1));
    assertNotNull(dao.write("o2o4o2", 2, 4, 2));
    assertNotNull(dao.write("o2o4o3", 2, 4, 3));
    assertNotNull(dao.write("o2o5", 2, 5));
    assertNotNull(dao.write("o2o5o1", 2, 5, 1));
    assertNotNull(dao.write("o2o5o2", 2, 5, 2));
    assertNotNull(dao.write("o2o5o3", 2, 5, 3));
    assertNotNull(dao.write("o3", 3));
  }

  @Test
  public void read() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);
    assertEquals("o2", dao.read(2).getName());
    assertEquals("o2o1", dao.read(2, 1).getName());
    assertEquals("o2o2", dao.read(2, 2).getName());
    assertEquals("o2o3", dao.read(2, 3).getName());
    assertEquals("o2o4", dao.read(2, 4).getName());
    assertEquals("o2o5", dao.read(2, 5).getName());
    assertEquals("o2o4o1", dao.read(2, 4, 1).getName());
    assertEquals("o2o4o2", dao.read(2, 4, 2).getName());
    assertEquals("o2o4o3", dao.read(2, 4, 3).getName());
  }

  @Test
  public void children() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);
    File[] files = dao.children(2, 4);
    print(files);

    assertEquals(3, files.length);
    assertEquals("o2o4o1", files[0].getName());
    assertEquals("o2o4o2", files[1].getName());
    assertEquals("o2o4o3", files[2].getName());

    files = dao.children(2);
    print(files);

    assertEquals(5, files.length);
    assertEquals("o2o1", files[0].getName());
    assertEquals("o2o2", files[1].getName());
    assertEquals("o2o3", files[2].getName());
    assertEquals("o2o4", files[3].getName());
    assertEquals("o2o5", files[4].getName());
  }

  @Test
  public void child() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);

    assertEquals("o2o4o1", dao.child("o2o4o1", 2, 4).getName());
    assertEquals("o2o4o2", dao.child("o2o4o2", 2, 4).getName());
    assertEquals("o2o4o3", dao.child("o2o4o3", 2, 4).getName());
    assertNull(dao.child("???", 2, 4));
  }

  @Test
  public void lastChild() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);

    assertEquals("o2o5", dao.lastChild(2).getName());
    assertEquals("o2o4o3", dao.lastChild(2, 4).getName());
    assertEquals("o2o5o3", dao.lastChild(2, 5).getName());
    assertNull(dao.lastChild(2, 4, 3));
  }

  @Test
  public void tree() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);
    File[] files = dao.tree(2, 4);

    assertEquals(4, files.length);
    assertEquals("o2o4", files[0].getName());
    assertEquals("o2o4o1", files[1].getName());
    assertEquals("o2o4o2", files[2].getName());
    assertEquals("o2o4o3", files[3].getName());

    files = dao.tree(2);

    assertEquals(12, files.length);
    assertEquals("o2", files[0].getName());
    assertEquals("o2o1", files[1].getName());
    assertEquals("o2o2", files[2].getName());
    assertEquals("o2o3", files[3].getName());
    assertEquals("o2o4", files[4].getName());
    assertEquals("o2o4o1", files[5].getName());
    assertEquals("o2o4o2", files[6].getName());
    assertEquals("o2o4o3", files[7].getName());
    assertEquals("o2o5", files[8].getName());
    assertEquals("o2o5o1", files[9].getName());
    assertEquals("o2o5o2", files[10].getName());
    assertEquals("o2o5o3", files[11].getName());
  }

  @Test
  public void rename() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);
    assertEquals(1, dao.rename("dir1", 2));
    assertEquals(1, dao.rename("dir2", 2, 1));
    assertEquals(1, dao.rename("dir3", 2, 2));
    assertEquals(1, dao.rename("dir4", 2, 3));
    assertEquals(1, dao.rename("dir5", 2, 4));
    assertEquals(1, dao.rename("file1", 2, 4, 1));
    assertEquals(1, dao.rename("file2", 2, 4, 2));
    assertEquals(1, dao.rename("file3", 2, 4, 3));
    assertEquals(1, dao.rename("dir6", 2, 5));
    assertEquals(1, dao.rename("file1", 2, 5, 1));
    assertEquals(1, dao.rename("file2", 2, 5, 2));
    assertEquals(1, dao.rename("file3", 2, 5, 3));

    File[] files = dao.tree(2);
    print(files);

    assertEquals(12, files.length);
    assertEquals("dir1", files[0].getName());
    assertEquals("dir2", files[1].getName());
    assertEquals("dir3", files[2].getName());
    assertEquals("dir4", files[3].getName());
    assertEquals("dir5", files[4].getName());
    assertEquals("file1", files[5].getName());
    assertEquals("file2", files[6].getName());
    assertEquals("file3", files[7].getName());
    assertEquals("dir6", files[8].getName());
    assertEquals("file1", files[9].getName());
    assertEquals("file2", files[10].getName());
    assertEquals("file3", files[11].getName());
  }

  @Test
  public void move() throws SQLException, IOException {

    write();

    FilesDAO dao = new FilesDAO(connection);

    print(dao.tree(2));

    dao.move(new int[]{2, 4}, new int[]{2, 5});

    print(dao.tree(2));

    File[] files = dao.tree(2, 5);

    assertEquals(8, files.length);
    assertEquals("o2o5", files[0].getName());
    assertEquals("o2o5o1", files[1].getName());
    assertEquals("o2o5o2", files[2].getName());
    assertEquals("o2o5o3", files[3].getName());
    assertEquals("o2o4", files[4].getName());
    assertEquals("o2o4o1", files[5].getName());
    assertEquals("o2o4o2", files[6].getName());
    assertEquals("o2o4o3", files[7].getName());

    files = dao.tree(2);

    assertEquals(12, files.length);
    assertEquals("o2", files[0].getName());
    assertEquals("o2o1", files[1].getName());
    assertEquals("o2o2", files[2].getName());
    assertEquals("o2o3", files[3].getName());
    assertEquals("o2o5", files[4].getName());
    assertEquals("o2o5o1", files[5].getName());
    assertEquals("o2o5o2", files[6].getName());
    assertEquals("o2o5o3", files[7].getName());
    assertEquals("o2o4", files[8].getName());
    assertEquals("o2o4o1", files[9].getName());
    assertEquals("o2o4o2", files[10].getName());
    assertEquals("o2o4o3", files[11].getName());
  }

  @Test
  public void copy() throws SQLException, IOException {

    write();

    FilesDAO dao = new FilesDAO(connection);

    print(dao.tree(2));

    dao.copy(new int[]{2, 4}, new int[]{2, 5});

    print(dao.tree(2));

    File[] files = dao.tree(2, 5);

    assertEquals(8, files.length);
    assertEquals("o2o5", files[0].getName());
    assertEquals("o2o5o1", files[1].getName());
    assertEquals("o2o5o2", files[2].getName());
    assertEquals("o2o5o3", files[3].getName());
    assertEquals("o2o4", files[4].getName());
    assertEquals("o2o4o1", files[5].getName());
    assertEquals("o2o4o2", files[6].getName());
    assertEquals("o2o4o3", files[7].getName());

    files = dao.tree(2);

    assertEquals(16, files.length);
    assertEquals("o2", files[0].getName());
    assertEquals("o2o1", files[1].getName());
    assertEquals("o2o2", files[2].getName());
    assertEquals("o2o3", files[3].getName());
    assertEquals("o2o4", files[4].getName());
    assertEquals("o2o4o1", files[5].getName());
    assertEquals("o2o4o2", files[6].getName());
    assertEquals("o2o4o3", files[7].getName());
    assertEquals("o2o5", files[8].getName());
    assertEquals("o2o5o1", files[9].getName());
    assertEquals("o2o5o2", files[10].getName());
    assertEquals("o2o5o3", files[11].getName());
    assertEquals("o2o4", files[12].getName());
    assertEquals("o2o4o1", files[13].getName());
    assertEquals("o2o4o2", files[14].getName());
    assertEquals("o2o4o3", files[15].getName());
  }

  @Test
  public void remove() throws SQLException {

    write();

    FilesDAO dao = new FilesDAO(connection);
    File[] files = dao.tree(2);
    assertEquals(12, files.length);

    print(dao.tree(2));

    assertEquals(4, dao.remove(2, 4));

    print(dao.tree(2));

    files = dao.tree(2, 4);
    assertEquals(0, files.length);

    files = dao.tree(2);
    assertEquals(8, files.length);

    assertEquals(8, dao.remove(2));

    files = dao.tree(2);
    assertEquals(0, files.length);
  }

  @Test
  public void readWriteContent() throws SQLException, IOException {

    String text = "Hello World!\n";

    write();

    FilesDAO dao = new FilesDAO(connection);

    long oid = dao.read(3).getContent();

    OutputStream os = dao.getOutputStream(oid);
    os.write(text.getBytes());
    os.close();

    byte[] b = new byte[text.length()];

    InputStream is = dao.getInputStream(oid);
    is.read(b);
    is.close();

    assertEquals(text, new String(b));
  }

  @Test
  public void copyContent() throws SQLException, IOException {

    String text = "Hello World!\n";

    write();

    FilesDAO dao = new FilesDAO(connection);

    OutputStream os = dao.getOutputStream(dao.read(2, 4).getContent());
    os.write(text.getBytes());
    os.close();

    dao.copy(new int[]{2, 4}, new int[]{2, 5});

    byte[] b = new byte[text.length()];

    InputStream is = dao.getInputStream(dao.read(2, 4).getContent());
    is.read(b);
    is.close();

    assertEquals(text, new String(b));

    is = dao.getInputStream(dao.read(2, 5, 4).getContent());
    is.read(b);
    is.close();

    assertEquals(text, new String(b));
  }

  @Test
  public void moveContent() throws SQLException, IOException {

    String text = "Hello World!\n";

    write();

    FilesDAO dao = new FilesDAO(connection);

    OutputStream os = dao.getOutputStream(dao.read(2, 4).getContent());
    os.write(text.getBytes());
    os.close();

    dao.move(new int[]{2, 4}, new int[]{2, 5});

    byte[] b = new byte[text.length()];

    assertNull(dao.read(2, 4));

    InputStream is = dao.getInputStream(dao.read(2, 5, 4).getContent());
    is.read(b);
    is.close();

    assertEquals(text, new String(b));
  }
}
