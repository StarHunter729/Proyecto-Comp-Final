package fileHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Clonation {
    public Clonation() {
    }

    public static File validateClone(String baseString, String iterationString) {
        String iterationStr = iterationString;
        File temp = new File(baseString);

        for(int i = 1; temp.exists(); ++i) {
            String tempStr = iterationStr + i + ".jar";
            temp = new File(tempStr);
        }

        return temp;
    }

    public static void copy(File src, File dest) throws IOException {
        InputStream is = null;
        FileOutputStream os = null;

        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dest);
            byte[] buf = new byte[1024];

            int bytesRead;
            while((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        } finally {
            System.out.println("Created " + dest + " through a marvelous clonation process.");
            is.close();
            os.close();
        }

    }
}

