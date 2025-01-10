/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.app;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;

/**
 * A convenience implementation of FileFilter that filters out all files except
 * for those type extensions that it knows about.
 *
 * Extensions are of the type ".foo", which is typically found on Windows and
 * Unix boxes, but not on Macintosh. Case is ignored.
 *
 * Example - create a new filter that filters out all files but gif and jpg
 * image files:
 * <pre>
 * JFileChooser chooser = new JFileChooser(); MyFileFilter filter = new
 * MyFileFilter( new String{"gif", "jpg"}, "JPEG and GIF Images")
 * chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 * </pre>
 */
public class FileExtensionFilter extends FileFilter implements java.io.FileFilter {

	// private static String TYPE_UNKNOWN = "Type Unknown";
	// private static String HIDDEN_FILE = "Hidden File";

	// changed to ArrayList as we need an ordered list (want .ggb first)
	private ArrayList<FileExtensions> filters = null;

	private String description = null;
	private String fullDescription = null;

	/**
	 * Creates a file filter. If no filters are added, then all files are
	 * accepted.
	 *
	 * @see #addExtension
	 */
	public FileExtensionFilter() {
		filters = new ArrayList<>();
	}

	/**
	 * Creates a file filter that accepts files with the given extension.
	 * Example: new MyFileFilter("jpg");
	 * 
	 * @param extension
	 *            either "ext" or ".ext"
	 * @see #addExtension
	 */
	public FileExtensionFilter(FileExtensions extension) {
		this();
		if (extension != null) {
			addExtension(extension);
		}
	}

	/**
	 * Return true if this file should be shown in the directory pane, false if
	 * it shouldn't.
	 *
	 * Files that begin with "." are ignored.
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	@Override
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}

			FileExtensions extension = FileExtensions.get(getExtension(f));

			return filters.contains(extension);
		}
		return false;
	}

	/**
	 * Return the extension portion of the file's name .
	 * 
	 * @param f file
	 * @return "ext" for file "filename.ext"
	 *
	 * @see FileFilter#accept
	 */
	private String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return StringUtil.toLowerCaseUS(filename.substring(i + 1));
			}
		}
		return null;
	}

	/**
	 * Adds a filetype "dot" extension to filter against.
	 *
	 * For example: the following code will create a filter that filters out all
	 * files except those that end in ".jpg" and ".tif":
	 *
	 * MyFileFilter filter = new MyFileFilter(); filter.addExtension("jpg");
	 * filter.addExtension("tif");
	 *
	 * Note that the "." before the extension is not needed and will be ignored.
	 * 
	 * @param extension
	 *            either ".ext" or "ext"
	 */
	public void addExtension(FileExtensions extension) {
		if (filters == null) {
			filters = new ArrayList<>(5);
		}

		filters.add(extension);
		fullDescription = null;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	/**
	 * Returns the human readable description of this filter. For example:
	 * "JPEG and GIF Image Files (*.jpg, *.gif)"
	 *
	 * @see #setDescription
	 * @see FileFilter#getDescription
	 */
	@Override
	public String getDescription() {
		if (fullDescription == null) {
			fullDescription = description == null ? "("
					: description + " (";
			// build the description from the extension list

			if (filters.size() > 0) {
				fullDescription += "." + filters.get(0);
			}

			for (int i = 1; i < filters.size(); i++) {
				fullDescription += ", ." + filters.get(i);
			}

			fullDescription += ")";
		}
		return fullDescription;
	}

	/**
	 * Sets the human readable description of this filter. For example:
	 * filter.setDescription("Gif and JPG Images");
	 * 
	 * @param description description
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	/**
	 * Returns the first extension contained in the extension list.
	 * 
	 * @return first extension (without ".")
	 */
	public FileExtensions getExtension() {
		return filters.get(0);
	}
}
