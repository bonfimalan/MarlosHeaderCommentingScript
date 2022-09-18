import java.io.File;

public class Main {
  private static final String JAVA_FILE_EXTENSION = ".java";
  public static void main(String[] args) {
    if (args.length == 0) 
      printAndExitProgram("No directory passed");// the path was not passed

    File mainFolder = new File(args[0]);
    
    if(mainFolder.isFile()) 
      printAndExitProgram("You must pass a directory");

    if(!mainFolder.exists()) 
      printAndExitProgram("Directory don't exists");

    commentJavaFiles(mainFolder);
  }

  public static void printAndExitProgram(String message) {
    System.out.println(message);
    System.exit(0);
  }

  public static void commentJavaFiles(File f) {
    if(f.isFile() && f.getName().endsWith(JAVA_FILE_EXTENSION))
      addHeaderComment(f);

    // so it's a directory
    File[] files = f.listFiles();
    if(files == null) return;

    for(File file : files)
      commentJavaFiles(file);
  }

  public static void addHeaderComment(File f) {
    
  }
}