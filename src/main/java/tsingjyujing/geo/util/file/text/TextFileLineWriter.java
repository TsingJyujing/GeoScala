package tsingjyujing.geo.util.file.text;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;

/**
 * @author Yuan Yifan
 * Function: Read large text file line by line.
 * Usage:
 * Firstly, new a object  TextFileLineWriter and initial it by your filename:
 * TextFileLineWriter FWL = new TextFileLineWriter(FileName);
 * Secondly, the function in this object write to write a String.
 * For example:
 * FWL.write("This is a test!\r\n");
 * You have to use "\r\n" in Win to shift a new line.
 * Last but not least:
 * You have to close file before terminating your program like this:
 * FWL.close();
 * Have fun!
 */
public class TextFileLineWriter implements Closeable {

    private FileWriter fileWriter = null;
    private BufferedWriter bufferWriter = null;
    private int flagLoaded = 0;
    public boolean isSilence = false;

    /**
     * initialization
     */
    public TextFileLineWriter() {
    }

    /**
     * initialization
     *
     * @param filename
     */
    public TextFileLineWriter(String filename) {
        loadFile(filename);
    }

    /**
     * Load file manuelly
     *
     * @param filename
     */
    public void loadFile(String filename) {
        try {
            if (flagLoaded == 1) {
                close();
            } else {
                fileWriter = new FileWriter(filename);
                bufferWriter = new BufferedWriter(fileWriter);
                flagLoaded = 1;
            }
        } catch (Exception e) {
            if (!isSilence) {
                System.out.println("Can't load file.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Write given str into file
     *
     * @param stringToWrite
     */
    public void write(String stringToWrite) {
        try {
            bufferWriter.write(stringToWrite, 0, stringToWrite.length());
        } catch (Exception e) {
            if (!isSilence) {
                System.out.println("Can't write file.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Write given str into file
     *
     * @param stringToWrite
     */
    public void writeln(String stringToWrite) {
        try {
            bufferWriter.write(stringToWrite, 0, stringToWrite.length());
            bufferWriter.write("\n");
        } catch (Exception e) {
            if (!isSilence) {
                System.out.println("Can't write file.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Free file while finalize
     */
    @Override
    public void close() {
        try {
            if (flagLoaded == 1) {
                bufferWriter.close();
                fileWriter.close();
                flagLoaded = 0;
            }
        } catch (Exception e) {
            if (!isSilence) {
                System.out.println("Can't close file.");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
