package cz.cvut.fel.mvod.prologueServer;

/*
Copyright 2011 Radovan Murin

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/


import cz.cvut.fel.mvod.common.Voter;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that loads files containng plaintexts and objects
 * @author Radovan Murin
 */
public class FileOperator {

    public FileOperator() {
    }
/**
 * Returns a string with the file's string data.
 * @param path the path to the file.
 * @return the string with the contents.
 */
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
            e.printStackTrace();
        } catch (IOException e) {
            ret = "Other file reading exception";
            e.printStackTrace();
        }
        return ret;
    }
/**
 * Appends an object to the and of a file
 * @param obj the object to append
 * @param path the path to the file
 * @return if true the operation has been successful.
 */
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
