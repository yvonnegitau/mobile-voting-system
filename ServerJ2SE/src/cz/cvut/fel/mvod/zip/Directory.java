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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * Representation of directory in filesystem.
 * @author jakub
 */
class Directory implements FileSystemUnit{

	private File directory;
	private String relativePath;
	private List<FileSystemUnit> list = null;

	/**
	 * Creates new instance of <code>Directory</code>.
	 * @param absolutePath system dependent path in file system
	 * @param relativePath path of this directory in zip file
	 */
	public Directory(String absolutePath, String relativePath) {
		this(new File(absolutePath), relativePath);
	}

	/**
	 * Creates new instance of <code>Directory</code>.
	 * @param directory 
	 * @param relativePath path of this directory in zip file
	 */
	public Directory(File directory, String relativePath) {
		if(!directory.isDirectory()) {
			throw new IllegalArgumentException("File is not a directory.");
		}
		this.directory = directory;
		this.relativePath = (relativePath == null ? "" : relativePath) + directory.getName() + "/";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRelativePath() {
		return relativePath;
	}

	public List<FileSystemUnit> getDirList() {
		if(list == null) {
			list = new ArrayList<FileSystemUnit>();
			String[] children = directory.list();
			for(String filename : children) {
				FileContainer file = new FileContainer(
						directory.getAbsolutePath() + "/" + filename, relativePath);
				if(file.isDirectory()) {
					list.add(new Directory(file, relativePath));
				} else {
					list.add(file);
				}
			}
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public void write(ZipOutputStream out) throws IOException {
		getDirList();
		for(FileSystemUnit file : list) {
			file.write(out);
		}
	}

}
