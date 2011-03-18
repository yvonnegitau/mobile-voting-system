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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * This class supports mechanism to save directory in file system into zip file.
 * @author jakub
 */
public class ZipCreator {

	private String filename;
	private String sourcePath;
	private ZipOutputStream out;

	/**
	 * Creates new instance of <code>ZipCreator</code> class.
	 * @param filename name of zip file
	 * @param sourcePath directory which will be stored in zip file
	 */
	public ZipCreator(String filename, String sourcePath) {
		this.filename = filename;
		this.sourcePath = sourcePath;
		out = null;
	}

	/**
	 * Copy content of source directory into the zip file.
	 * @throws IOException if IO error occurs
	 */
	public void saveToFile() throws IOException{
		try {
			out = new ZipOutputStream(new FileOutputStream(new File(filename)));
		} catch(FileNotFoundException ex) {
			throw new IOException(ex);
		}
		File file = new File(sourcePath);
		if(file.isDirectory()) {
			Directory dir = new Directory(file, null);
			dir.write(out);
		} else {
			FileContainer f = new FileContainer(file.getAbsolutePath(), null);
			f.write(out);
		}
	}


	/**
	 * Closes zip file.
	 * @throws IOException if closing of file fails
	 */
	public void closeFile() throws IOException {
		if(out != null) {
			out.close();
		}
	}

}
