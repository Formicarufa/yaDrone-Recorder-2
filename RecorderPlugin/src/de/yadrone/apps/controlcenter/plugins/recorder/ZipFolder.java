/**
 * 
 */
package de.yadrone.apps.controlcenter.plugins.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Formicarufa (Tomas Prochazka)
 *15. 3. 2016
 */
public class ZipFolder {

	final static int buff= 2048;
	/**
	 * Zips a given directory to an archive with a given name.
	 * Empty directories are not copied to the zip file.
	 * @param directory Directory to zip
	 * @param outputfile Name of the zip, including suffix.
	 * @throws IOException 
	 */
	public static void pack(String directory, File outputfile) throws IOException {
		
		File f = new File(directory);
		pack(f, outputfile);

	}
	/**
	 * Zips a given directory to an archive with a given name.
	 * Empty directories are not copied to the zip file.
	 * @param directory Directory to zip
	 * @param outputfile Name of the zip, including suffix.
	 * @throws IOException 
	 */
	public static void pack(File directory, File outputfile) throws FileNotFoundException, IOException {
		if (!directory.isDirectory()) throw new IllegalArgumentException("Expected directory, not file.");
		FileOutputStream fos = new FileOutputStream(outputfile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		byte data[] = new byte[buff];
		addFolder(directory,"", zos, data);
		zos.close();
	}
	private static void addFile(File file,String name, ZipOutputStream zos, byte[] data) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ZipEntry entry = new ZipEntry(name);
		zos.putNextEntry(entry);
		int count;
		while ((count=in.read(data, 0, buff))!=-1) {
			zos.write(data,0,count);
		}
		in.close();
	}
	public static void addFolder(File dir, String name, ZipOutputStream zos, byte[] data ) throws IOException {
		String[] files = dir.list();
		for (String fileName : files) {
			String outName = name==""? fileName :  name+ "/"+fileName;
			File newFile = new File(dir.getPath()+"/"+fileName);
			if (newFile.isDirectory()) {
				addFolder(newFile,outName, zos, data);
			} else {
				addFile(newFile,outName,zos,data);
			}
		}
	}
}
