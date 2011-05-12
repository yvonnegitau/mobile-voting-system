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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A class thats purpose is to load localized web pages.
 * @author Radovan Murin
 */
public class webPageLocalizer {

    HashMap<String, String> webPage;

    /**
     * Localizes a web page, the web page name has to follow some simple guidelines
     * @param URI the name of the page, the general name should be ended by a "_" follower by the language code. ex.: index_en-EN.html
     */
    public webPageLocalizer(final String URI,final String directory) {
        webPage = new HashMap<String, String>();
        FileOperator fo = new FileOperator();
        File incomingDir = new File(directory);
      //  System.out.println("Looking for files in directory: "+ incomingDir.getAbsolutePath());
        File contents = incomingDir;
        /*for (int i = 0; i < contents.length; i++) {
          //  System.out.println("fileList " +contents[i]);

        }*/
        List files = new ArrayList();
        FilenameFilter ff = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
       
                if (name.contains(URI + "_")) {
                  //  System.out.println("FOUND ONE");
                    return true;


                }

                return false;
            }
        };


     //   for (int i = 0; i < contents.length; i++) {

            File[] pages = contents.listFiles(ff);


            if (pages != null) {
                if (pages.length != 0) {
                    //   System.out.println("FOUND SOMETHING");
                    //System.out.println(Arrays.asList(pages).);
                    files.addAll(Arrays.asList(pages));
                }
            }
      //  }



        Iterator<File> itF = files.iterator();
        while (itF.hasNext()) {
            File f = itF.next();
            String path = f.getAbsolutePath();
            String lang = f.getName().replace(URI + "_", "");
            lang = lang.replace(".html", "").split("-")[0];
            if (lang.equals("")) {
                lang = "default";
            }
            String rawPage = fo.getWholeTextFile(path);
            rawPage = rawPage.replaceAll("<--PUBLIC_IP-->", GlobalSettingsAndNotifier.singleton.getSetting("PUBLIC_IP"));
            rawPage = rawPage.replaceAll("<--PORT-->", GlobalSettingsAndNotifier.singleton.getSetting("HTTP_PORT"));
            rawPage = rawPage.replace("<--PRIVATE_IP-->", GlobalSettingsAndNotifier.singleton.getSetting("PRIVATE_IP"));
          //  System.out.println(path + " " + lang);
            webPage.put(lang, rawPage);

        }





    }
/**
 * Returns the best matching web page for the given language
 * @param langs the requested langueage
 * @return the String representation of the web page.
 */
    public String getWP(String[] langs) {

        String page = null;

        if (webPage.isEmpty()) {
            return "Error";
        }
        try {
            for (int i = 0; i < langs.length; i++) {

                String simple = langs[i].split("-")[0];

                page = webPage.get(simple.replace(" ", ""));
                if (page != null) {
                    if (!page.equals("")) {

                        return page;
                    }
                }
            }
            if (page == null) {


                page = webPage.get("default");
                if (page != null) {

                    return page;
                }
            }
            if (page == null) {
                // System.out.println("LAST RESORT");
                page = webPage.values().iterator().next();
                if (!page.equals("")) {

                    return page;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Error";

    }
}
