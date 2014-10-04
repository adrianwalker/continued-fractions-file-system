package org.adrianwalker.continuedfractions.filesystem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public final class FileSystemTest {

  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "postgres";

  private static Connection connection;

  public FileSystemTest() {
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
  public void create() throws SQLException, FileSystemException {

    clear();

    FilesDAO dao = new FilesDAO(connection);
    FileSystem fs = new FileSystem(dao, 1);

    assertNotNull(fs.create("/dir1"));
    assertNotNull(fs.create("/dir2/file1"));
    assertNotNull(fs.create("/dir2/file2"));
    assertNotNull(fs.create("/dir2/file3"));
    assertNotNull(fs.create("/dir2/dir3/file1"));
    assertNotNull(fs.create("/dir2/dir3/file2"));
    assertNotNull(fs.create("/dir2/dir3/file3"));
    assertNotNull(fs.create("/dir2/dir4/file1"));
    assertNotNull(fs.create("/dir2/dir4/file2"));
    assertNotNull(fs.create("/dir2/dir4/file3"));
    assertNotNull(fs.create("/dir5"));
  }

  @Test
  public void delete() throws SQLException, IOException, FileSystemException {

    create();

    FilesDAO dao = new FilesDAO(connection);
    FileSystem fs = new FileSystem(dao, 1);

    fs.delete("/dir1");
    fs.delete("/dir2");
    fs.delete("/dir5");

    commit();

    File[] files = fs.list("/");
    assertEquals(0, files.length);

    files = fs.list("/dir2");
    assertEquals(0, files.length);

    files = fs.list("/dir2/dir3");
    assertEquals(0, files.length);

    files = fs.list("/dir2/dir4");
    assertEquals(0, files.length);

    files = fs.list("/dir5");
    assertEquals(0, files.length);
  }

  @Test
  public void list() throws SQLException, FileSystemException {

    create();

    FilesDAO dao = new FilesDAO(connection);
    FileSystem fs = new FileSystem(dao, 1);

    File[] files = fs.list("/");
    assertEquals(3, files.length);

    files = fs.list("/dir1");
    assertEquals(0, files.length);

    files = fs.list("/dir2");
    assertEquals(5, files.length);

    files = fs.list("/dir2/dir3");
    assertEquals(3, files.length);

    files = fs.list("/dir2/dir4");
    assertEquals(3, files.length);

    files = fs.list("/dir5");
    assertEquals(0, files.length);
  }

  @Test
  public void readWrite() throws SQLException, IOException, FileSystemException {

    create();

    FilesDAO dao = new FilesDAO(connection);
    FileSystem fs = new FileSystem(dao, 1);

    String path = "/dir2/dir3/file1";
    String text = "";

    assertEquals(text, fs.read(path));

    path = "/dir6/dir7/file1";
    text = "Hello World!";

    fs.write(path, text);

    assertEquals(text, fs.read(path));
  }

  @Test
  public void move() throws SQLException, FileSystemException {

    create();

    FilesDAO dao = new FilesDAO(connection);
    FileSystem fs = new FileSystem(dao, 1);

    File[] files = fs.list("/");
    assertEquals(3, files.length);

    files = fs.list("/dir5");
    assertEquals(0, files.length);

    fs.move("/dir2", "/dir5");

    files = fs.list("/");
    assertEquals(2, files.length);

    files = fs.list("/dir5");
    assertEquals(1, files.length);
  }

  @Test
  public void copy() throws SQLException, FileSystemException {

    create();

    FilesDAO dao = new FilesDAO(connection);
    FileSystem fs = new FileSystem(dao, 1);

    File[] files = fs.list("/");
    assertEquals(3, files.length);

    files = fs.list("/dir5");
    assertEquals(0, files.length);

    fs.copy("/dir2", "/dir5");

    files = fs.list("/");
    assertEquals(3, files.length);

    files = fs.list("/dir5");
    assertEquals(1, files.length);
  }
}
