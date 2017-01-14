import java.io.File;
import java.util.ArrayList;

public class Showcase {

    public static void main(String[] args) {
    	System.out.println("=========================================================================================");
    	System.out.println("         Welcome to [projekt.]'s automatic Substratum Showcase cloud updater!");
    	System.out.println("=========================================================================================");

    	ArrayList<String> files = new ArrayList<>();

        File folder = new File(System.getProperty("user.dir"));
        System.out.println("\nLocating XML files in directory : " + folder.getAbsolutePath() + "\n");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile() && listOfFiles[i].toString().endsWith(".xml")) {
        	  files.add(listOfFiles[i].getName());
        	  System.out.println("Found XML file : " + listOfFiles[i].getName());
          }
        }
        
        for (int i = 0; i < files.size(); i++) {
        	System.out.println("\n============================================"
        			+ "=============================================");
        	System.out.println("Processing file : " + files.get(i));
        	System.out.println("============================================"
        			+ "=============================================\n");
        	String[] checkerCommands = {files.get(i), "true"};
            RefactorFile.main(checkerCommands);
        }
        
    	System.out.println("\nThe program has finished.");
    }
}