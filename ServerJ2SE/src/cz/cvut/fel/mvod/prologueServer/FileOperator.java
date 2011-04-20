package cz.cvut.fel.mvod.prologueServer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import cz.cvut.fel.mvod.common.Voter;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Murko
 */
public class FileOperator {

    public FileOperator() {
    }

    public String getWholeTextFile(String path) {
        String ret = "";
        File file = new File(path);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DataInputStream dis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            dis = new DataInputStream(bis);
            byte[] wholeThing = new byte[(int) file.length()];
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

    public boolean appendObjectToFile(Serializable obj, String path) {
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println("CANT CREATE FILE");
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            
            if (obj.getClass().equals(String.class)) {
                fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeBytes((String) obj);
                dos.writeBytes("\r\n");
                dos.close();

            } else {
                ObjectInputStream objectInput = null;

                ArrayList<Voter> voterArrayList = null;
                try {
                objectInput = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
                        path)));
                
                    voterArrayList = (ArrayList<Voter>) objectInput.readObject();
                    objectInput.close();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(FileOperator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex){
                    
                }
                file.delete();
                file.createNewFile();
                if(voterArrayList==null) voterArrayList = new ArrayList<Voter>();
                voterArrayList.add((Voter) obj);

                fos = new FileOutputStream(file);
                 bos = new BufferedOutputStream(fos);
                ObjectOutputStream dos = new ObjectOutputStream(bos);
                
                dos.writeObject(voterArrayList);
                dos.close();
            }

            bos.close();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return true;
    }
}
