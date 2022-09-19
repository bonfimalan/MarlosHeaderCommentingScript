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
  private static final String PATH_TO_BACKUP = "backup/";
  private static final String HELP_MESSAGE_ADIVICE = "(use -h to see help instructions)";
  private static boolean clearAtEnd = false;

  public static void main(String[] args) throws IOException {
    if (args.length == 0)
      printAndExitProgram("No directory passed " + HELP_MESSAGE_ADIVICE);// the path was not passed

    String pathToRootFolder = processFlags(args);
    File rootFolder = new File(pathToRootFolder);

    if (rootFolder.isFile())
      printAndExitProgram("You must pass a directory " + HELP_MESSAGE_ADIVICE);

    if (!rootFolder.exists())
      printAndExitProgram("Directory don't exists " + HELP_MESSAGE_ADIVICE);

    commentJavaFiles(rootFolder);

    // clear the backup file if the -c flag was used
    if (clearAtEnd)
      System.out.println(processFlagC());
  }

  public static String processFlags(String[] args) {
    switch (args[0]) {
      case "-c":
        // comment files than clear the backup directory
        if (args.length > 1) {
          clearAtEnd = true;
          return args[1];
        }
        // clear the backup directory and exit
        String message = processFlagC();
        printAndExitProgram(message);
        break;
      case "-h":
        printAndExitProgram(processFlagH());
        break;
    }
    return args[0];
  }

  public static String processFlagH() {
    return "\t-h \t\t\t\t HELP!!\n" +
        "\t-c <target directory path> \t Comment java files and clear the backup directory\n" +
        "\t-c \t\t\t\t Clear the backup directory\n" +
        "\t<target directory path> \t Comment java files\n";
  }

  public static String processFlagC() {
    if (clearBackupFolder())
      return "Backup folder cleared";
    else
      return "Backup folder not cleared";
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

  public static void writeNewFile(String header, File targetFile) {
    try {
      // save the file in a backup and uses the backup to add the header
      File backupFile = saveInBackup(targetFile);
      Scanner scanner = new Scanner(targetFile);

      // verifies if the file has already a header comment
      String firstLine = scanner.nextLine();
      if (hasHeaderComment(firstLine)) {
        scanner.close();
        return;
      }
      scanner.close(); // closes the scanner, it was only used to check the first line

      // creates a new Scanner with the backup file instead of the original file
      scanner = new Scanner(backupFile);
      System.out.println("Adding header to " + targetFile.getName());

      // creates a FileWriter to save the header and copy the lines of the backup in
      // it
      FileWriter fileWriter = new FileWriter(targetFile);
      fileWriter.write(header); // write the header

      // copies the lines from the backup file in the file
      while (scanner.hasNextLine()) {
        fileWriter.write(scanner.nextLine() + "\n");
      }

      fileWriter.close();
      scanner.close();
    } catch (Exception e) {
    }
  }

  public static File saveInBackup(File file) {
    try {
      File backupFile = new File(PATH_TO_BACKUP + file.getName());
      backupFile.createNewFile();
      FileWriter fileWriter = new FileWriter(backupFile);

      Scanner scanner = new Scanner(file);

      while (scanner.hasNextLine()) {
        fileWriter.write(scanner.nextLine() + "\n");
      }

      fileWriter.close();
      scanner.close();

      return backupFile;
    } catch (Exception e) {
      return null;
    }
  }

  public static boolean clearBackupFolder() {
    boolean isBackupCleared = true;
    File backupDirectory = new File(PATH_TO_BACKUP);
    for (File backupFile : backupDirectory.listFiles()) {
      // if one file could not be deleted it will return false
      isBackupCleared = isBackupCleared && backupFile.delete();
    }

    return isBackupCleared;
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