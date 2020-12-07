package fileHandler;

import java.io.File;

public class fileValidation {
    public fileValidation() {
    }

    public static int getNumServices(String s) {
        File directory = new File(s);
        int fileCount = directory.list().length;
        System.out.println("Counted: " + fileCount + " of files");
        return fileCount;
    }
}
