import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Main {
  private static final String JAVA_FILE_EXTENSION = ".java";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm");

  public static void main(String[] args) {
    if (args.length == 0)
      printAndExitProgram("No directory passed");// the path was not passed

    File mainFolder = new File(args[0]);

    if (mainFolder.isFile())
      printAndExitProgram("You must pass a directory");

    if (!mainFolder.exists())
      printAndExitProgram("Directory don't exists");

    commentJavaFiles(mainFolder);
  }

  public static void printAndExitProgram(String message) {
    System.out.println(message);
    System.exit(0);
  }

  public static void commentJavaFiles(File f) {
    if (f.isFile() && f.getName().endsWith(JAVA_FILE_EXTENSION))
      addHeaderComment(f);

    // so it's a directory
    File[] files = f.listFiles();
    if (files == null)
      return;

    for (File file : files)
      commentJavaFiles(file);
  }

  public static void addHeaderComment(File f) {
    try {
      BasicFileAttributes fileAtt;

      fileAtt = Files.readAttributes(
          f.toPath(), BasicFileAttributes.class);

      String header = headerComment(
          f.getName(),
          formatDate(fileAtt.creationTime()),
          formatDate(f.lastModified()));

      writeNewFile(header, f);

    } catch (IOException e) {
    }
  }

  public static void writeNewFile(String header, File oldFile) {
    try {
      Scanner scanner = new Scanner(oldFile);

      String firstLine = scanner.nextLine();
      if(hasHeaderComment(firstLine)) {
        scanner.close();
        return;
      }

      System.out.println("Adding header to " + oldFile.getName());

      File newFile = new File(oldFile.getAbsolutePath());
      FileWriter fileWriter = new FileWriter(newFile);
      fileWriter.write(header);
      fileWriter.write(firstLine + "\n");
      

      while (scanner.hasNextLine()) {
        fileWriter.write(scanner.nextLine() + "\n");
      }
      
      fileWriter.close();
      scanner.close();
    } catch (Exception e) {
    }
  }

  public static String formatDate(FileTime fileTime) {
    return DATE_FORMAT.format(fileTime.toMillis());
  }

  public static String formatDate(long time) {
    return DATE_FORMAT.format(time);
  }

  public static boolean hasHeaderComment(String firstLine) {
    return firstLine.startsWith("/***************************");
  }

  public static String headerComment(String fileName, String creationDate, String lastModified) {
    return "/********************************************************************" + "\n" +
        "* Author: Alan Bonfim Santos" + "\n" +
        "* Registration: 201911912" + "\n" +
        "* Initial date: " + creationDate + "\n" +
        "* Last update: " + lastModified + "\n" +
        "* Name: " + fileName + "\n" +
        "* Function:" + "\n" +
        "*******************************************************************/" + "\n";
  }
}