package tsingjyujing.geo.util.file.selector;

import javax.swing.*;
import java.io.File;

/**
 * @author tsingjyujing
 * @Mail tsingjyujing@163.com
 * @Telephone 182-2085-2215
 */
public class CommonDialog {

    public static String getFolder(String initPath) throws Exception {
        return getPath(initPath, JFileChooser.DIRECTORIES_ONLY, "选择文件夹");
    }

    public static String getFolder() throws Exception {
        return getFolder(System.getProperty("user.dir"));
    }


    public static String putFile(String initPath) throws Exception {
        return getPath(initPath, JFileChooser.SAVE_DIALOG, "保存文件");
    }

    public static String putFile() throws Exception {
        return putFile(System.getProperty("user.dir"));
    }

    public static String getFile(String initPath) throws Exception {
        return getPath(initPath, JFileChooser.FILES_ONLY, "打开文件");
    }

    public static String getFile() throws Exception {
        return getFile(System.getProperty("user.dir"));
    }


    static String getPath(String initPath, int dialogMode, String title) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(dialogMode);
        chooser.setCurrentDirectory(new File(initPath));
        int returnValue = chooser.showDialog(null, title);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        } else {
            throw new Exception("Didn't select file normally");
        }
    }

}
