package org.adrianwalker.continuedfractions.filesystem.example;

import java.sql.Connection;
import java.sql.DriverManager;
import org.adrianwalker.continuedfractions.filesystem.File;
import org.adrianwalker.continuedfractions.filesystem.FileSystem;
import org.adrianwalker.continuedfractions.filesystem.FilesDAO;
import static org.adrianwalker.continuedfractions.filesystem.Printer.print;

public class Example {

  private static final String DRIVER = "org.postgresql.Driver";
  private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
  private static final String USERNAME = "postgres";
  private static final String PASSWORD = "postgres";

  public static void main(final String[] args) throws Exception {

    Class.forName(DRIVER);
    Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    connection.setAutoCommit(false);

    FilesDAO dao = new FilesDAO(connection);
    dao.clear();

    FileSystem fs = new FileSystem(dao, 1);

    System.out.println("Create directories\n");

    fs.create("/bin");
    fs.create("/dev");
    fs.create("/etc");
    fs.create("/home");
    fs.create("/sbin");
    fs.create("/usr");

    fs.create("/home/adrian");
    fs.create("/home/other");

    fs.create("/home/adrian/documents/text");
    fs.create("/home/adrian/documents/presentations");
    fs.create("/home/adrian/documents/spreadsheets");

    connection.commit();

    print(fs.tree("/"));

    System.out.println("\nWrite files\n");

    fs.write("/home/adrian/documents/text/test1.txt", "Hello");
    fs.write("/home/adrian/documents/text/test2.txt", "Database");
    fs.write("/home/adrian/documents/text/test3.txt", "File System");
    fs.write("/home/adrian/documents/text/test4.txt", "World!");

    connection.commit();

    print(fs.tree("/"));

    System.out.println("\nMove files\n");

    fs.move("/home/adrian/documents", "/home/other");

    connection.commit();

    print(fs.tree("/"));

    System.out.println("\nPrint files\n");

    for (File file : fs.list("/home/other/documents/text")) {
      System.out.println(fs.read("/home/other/documents/text/" + file.getName()));
    }
  }
}
