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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Decorator to java.io.File.
 * @author jakub
 */
class FileContainer extends File implements FileSystemUnit {

	private static final long serialVersionUID = 123648964987458498l;
	private String relativePath;
	private ZipEntry entry;

	/**
	 * Creates new instance of <code>FileContainer</code>.
	 * @param absolutePath system dependent path to file in file system
	 * @param relativePath path of file in zipe file
	 */
	public FileContainer(String absolutePath, String relativePath) {
		super(absolutePath);
		this.relativePath = (relativePath == null ? "" : relativePath);
	}

	/**
	 * Creates new instance of <code>FileContainer</code>.
	 * @param entry of this file in zip file
	 * @param pathToSaveEntry absolute path to directory, where will be this file saved
	 */
	public FileContainer(ZipEntry entry, String pathToSaveEntry) {
		super(pathToSaveEntry + "/" + entry.getName());
		this.entry = entry;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write(ZipOutputStream out) throws IOException {
		FileInputStream in = null;
		try {
			out.putNextEntry(new ZipEntry(relativePath + getName()));
			in = new FileInputStream(this);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			out.write(buffer, 0, buffer.length);
		} finally {
			try {
				out.closeEntry();
			} catch(IOException ex) {
				//ok
			}
			if(in != null) {
				try {
					in.close();
				} catch(IOException ex) {
					//ok
				}
			}
		}
	}

	/**
	 * Reads content of file from zip file and stores it into the file in specified location.
	 * @param in zip file
	 * @throws IOException if IO error occurs
	 */
	public void read(ZipInputStream in) throws IOException {
		FileOutputStream out = null;
		try {
			createNewFile();
			out = new FileOutputStream(this);
			byte[] buffer = new byte[(int) entry.getSize()];
			in.read(buffer);
			out.write(buffer);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch(IOException ex) {
					//ok
				}
			}
		}
	}
	
}
