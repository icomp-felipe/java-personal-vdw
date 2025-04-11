package com.vdw.model;

import javax.swing.filechooser.FileNameExtensionFilter;

/** @author Felipe André - felipeandre.eng@gmail.com
  * @version 1.0 - 11/APR/2025 */
public class Constants {
	
	/** Stores JFileChooser's file types.
	 *  @see FileNameExtensionFilter */
	public static class FileFormat {
		
		public static final FileNameExtensionFilter MP4 = new FileNameExtensionFilter("MPEG-4 File (.mp4)", "mp4");
		public static final FileNameExtensionFilter MKV = new FileNameExtensionFilter("Matroska File (.mkv)", "mkv");
		
	}

}
