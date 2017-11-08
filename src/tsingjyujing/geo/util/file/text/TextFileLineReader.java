package tsingjyujing.geo.util.file.text;

/**
 * @author Yuan Yifan
 * Function: Read large text file line by line.
 * Usage:
 * Firstly, new a object  TxtFileLineRead and initial it by your filename:
 * TxtFileLineRead FRL = new TxtFileLineRead(FileName);
 * Secondly, the function in this object fReadln to return a line.
 * For example:
 * String Exper = FRL.fReadln();
 * If the text file has N lines you have to execute this function for N times to read it all
 * Last but not least:
 * You have to close file before terminating your program like this:
 * FRL.FreeFile();
 * Have fun!
 */

import java.io.*;
import java.util.Iterator;


public class TextFileLineReader implements Closeable, Iterable<String> {
    //public static void main(){
    //  You can write Usage in main or a new function.
    //  Such as：
    //  System.out.println("Firstly, new a object  TextFileLineReader and initial it by your filename:");
    //  And so on
    //}

    private FileReader fileReader = null;
    private BufferedReader bufferReader = null;
    private int flagLoaded = 0;
    public boolean isSilence = false;

    public TextFileLineReader(String filename) {
        loadFile(filename);
    }

    public TextFileLineReader() {
        //Do nothing
    }

    public int loadFile(String filename) {
        //Using for debug
        if (!isSilence) {
            System.out.println("Try to load " + filename);
        }
        try {
            if (flagLoaded == 1) {
                close();
            }
            fileReader = new FileReader(filename);
            bufferReader = new BufferedReader(fileReader);
            flagLoaded = 1;
            return (0);
        } catch (FileNotFoundException exFileNotFound) {
            if (!isSilence) {
                System.out.println("Error while reading: file not found.");
                exFileNotFound.printStackTrace();
            }
            return (-1);
        }
    }

    public String lineRead() {
        try {

            String Str = bufferReader.readLine();
            return (Str);
            //return null while end of the line
        } catch (Exception e) {
            if (!isSilence) {
                System.out.println("Error while reading the line");
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void close() {
        try {
            if (flagLoaded != 0) {
                bufferReader.close();
                fileReader.close();
                flagLoaded = 0;
            }
        } catch (Exception e) {
            if (!isSilence) {
                System.out.println("Error while closing the file");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public Iterator<String> iterator() {
        try {
            bufferReader.reset();
        } catch (IOException e) {
            System.err.println("Error while reset bufferReader.");
            e.printStackTrace();
        }
        return new LineReaderIter();
    }


    class LineReaderIter implements Iterator<String> {

        String currentLine;

        LineReaderIter() {
            try {
                currentLine = bufferReader.readLine();
            } catch (IOException e) {
                System.err.println("Error while initializing LineReaderIter.");
                e.printStackTrace();
            }
        }

        @Override
        public boolean hasNext() {
            return currentLine != null;
        }

        @Override
        public String next() {
            String returnLine = currentLine;
            try {
                currentLine = bufferReader.readLine();
            } catch (Exception ex) {
                currentLine = null;
            }
            return returnLine;
        }
    }
}
