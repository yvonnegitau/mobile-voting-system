/*
 * © 2010, Jakub Valenta
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Jakub Valenta
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors “as is” and any
 * express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the foundation or contributors be liable for any direct, indirect,
 * incidental, special, exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or profits; or business
 * interruption) however caused and on any theory of liability, whether in contract, strict
 * liability, or tort (including negligence or otherwise) arising in any way out of the use
 * of this software, even if advised of the possibility of such damage.
 */

package cz.cvut.fel.mvod.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility to load files from zip file.
 * @author jakub
 */
public class ZipReader {
	
	private String filename;
	private String destinationPath;
	private ZipInputStream zip;

	/**
	 *
	 * @param filename
	 * @param destinationPath
	 */
	public ZipReader(String filename, String destinationPath) {
		this.filename = filename;
		this.destinationPath = destinationPath;
		zip = null;
	}

	/**
	 * Reads content of the zip file and saves it to file system.
	 * @throws IOException if IO error occurs
	 */
	public void readFromFile() throws IOException {
		zip = new ZipInputStream(new FileInputStream(new File(filename)));
		ZipEntry entry;
		while((entry = zip.getNextEntry()) != null) {
			FileContainer file = new FileContainer(entry, destinationPath);
			file.read(zip);
		}
	}

	/**
	 * Closes zip file.
	 * @throws IOException if closing of file fails
	 */
	public void closeFile() throws IOException {
		if(zip != null) {
			zip.close();
		}
	}

}
