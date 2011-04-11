/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fel.mvod.prologueServer;

import java.io.*;

/**
 *
 * @author Murko
 */
public class FileOperator {

    public FileOperator() {
    }


    public  String getWholeTextFile(String path){
        String ret = "";
        File file = new File(path);
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    DataInputStream dis = null;
                    try {
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        dis = new DataInputStream(bis);
                        byte[] wholeThing = new byte[(int)file.length()];
                        dis.readFully(wholeThing);
                        ret = new String(wholeThing);
                        fis.close();
                        bis.close();
                        dis.close();
                    } catch (FileNotFoundException e) {
                        ret = "File not found";
                    } catch (IOException e) {
                        ret = "Other file reading exception";
                        e.printStackTrace();
                    }
        return ret;
    }

    public boolean appendToFile(String str,String path) {
        File file = new File(path);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bos);
            dos.writeBytes(str);
            dos.close();
            bos.close();
            fos.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }

        return true;
    }

}
