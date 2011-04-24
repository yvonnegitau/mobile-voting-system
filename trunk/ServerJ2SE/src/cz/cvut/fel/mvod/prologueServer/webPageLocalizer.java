/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Murko
 */
public class webPageLocalizer {

    HashMap<String, String> webPages;

    /**
     * Localizes a web page, the web page name has to follow some simple guidelines
     * @param URI the name of the page, the general name should be ended by a "_" follower by the language code. ex.: index_en-EN.html
     */
    public webPageLocalizer(final String URI) {
        webPages = new HashMap<String, String>();
        FileOperator fo = new FileOperator();
        File incomingDir = new File("..");
        File[] contents = incomingDir.listFiles();
        for (int i = 0; i < contents.length; i++) {
            //    System.out.println(contents[i]);
        }
        List files = new ArrayList();
        FilenameFilter ff = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                //System.out.println("Accept " + name);
                if (name.contains(URI + "_")) {
                    //  System.out.println("FOUND ONE");
                    return true;


                }

                return false;
            }
        };


        for (int i = 0; i < contents.length; i++) {

            File[] pages = contents[i].listFiles(ff);


            if (pages != null) {
                if (pages.length != 0) {
                    //   System.out.println("FOUND SOMETHING");
                    //System.out.println(Arrays.asList(pages).);
                    files.addAll(Arrays.asList(pages));
                }
            }
        }



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
            System.out.println(path + " " + lang);
            webPages.put(lang, rawPage);

        }





    }

    public String getWP(String[] langs) {

        String page = null;

        if (webPages.isEmpty()) {
            return "Error";
        }
        try {
            for (int i = 0; i < langs.length; i++) {

                String simple = langs[i].split("-")[0];

                page = webPages.get(simple.replace(" ", ""));
                if (page != null) {
                    if (!page.equals("")) {

                        return page;
                    }
                }
            }
            if (page == null) {


                page = webPages.get("default");
                if (page != null) {

                    return page;
                }
            }
            if (page == null) {
                // System.out.println("LAST RESORT");
                page = webPages.values().iterator().next();
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
