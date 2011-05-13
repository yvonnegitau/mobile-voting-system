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
package cz.cvut.fel.mvod.prologueServer;

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import java.util.HashMap;

/**
 * Class for storing a web site, with web pages and fully localized
 * @author Radovan Murin
 */
class WebPageFetcher {

    /**
     * The sites mapped
     */
    HashMap<String, webPageLocalizer> sitesLoc = new HashMap<String, webPageLocalizer>();
/**
 * The class constructor
 * @param summaryFile the summary file - a list of root names of web pages, without localization suffixes
 * "index.html" will load all files like "index_..-..\.[html|htm]$"
 */
    public WebPageFetcher(String summaryFile,String dir) {
       
        FileOperator fo = new FileOperator();
      
        String files = fo.getWholeTextFile(dir +""+summaryFile);
     
        String[] fList = files.split("\n");
     
        for (int i = 0; i < fList.length; i++) {
          
            sitesLoc.put(fList[i], new webPageLocalizer(fList[i].replace(".html", ""), dir));
           
            
          
        }



    }
/**
 * Returns the given page from this collection
 * @param name the name of the webpage, with file extention
 * @param loc an array of preffered languages in descending order
 * @return the returned web page, or 404 Error
 */
    public String fetch(String name,String[] loc) {
        String output = GlobalSettingsAndNotifier.singleton.messages.getString("404Error");
        try{
        output=sitesLoc.get(name).getWP(loc);
        } catch(Exception ex) {
            System.out.println("Tried to fetch "+name);
            ex.printStackTrace();
        }
        return output;
    }
}
