package de.yadrone.apps.controlcenter.plugins.recorder;

import java.io.File;

public class DirectoryRemover {
	/**
	 * Removes a directory with a given name including all files and sub-directories.
	 * @param name
	 * @return
	 */
	public static boolean remove(String name){
		File f = new File(name);
		return remove(f);
	}
	/**
	 * Removes a directory at the given path including all files and sub-directories.
	 * @param name
	 * @return
	 */
	public static boolean remove(File dir) {
		if (!dir.isDirectory()) throw new IllegalArgumentException("Expected a directory");
		File[] content = dir.listFiles();
		for (File f : content) {
			if (f.isDirectory()) {
				remove(f);
			} else {
				boolean deleted = f.delete();
				if (!deleted) return false;
			}
		}
		return dir.delete();
	}
}
