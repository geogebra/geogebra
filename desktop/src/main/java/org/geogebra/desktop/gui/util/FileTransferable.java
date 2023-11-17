/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileTransferable implements Transferable {
	DataFlavor[] dataFlavors = { DataFlavor.javaFileListFlavor };

	List<File> files = new LinkedList<>();

	public FileTransferable(File file) {
		files.add(file);
	}

	// Returns supported flavors
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavors;
	}

	// Returns true if flavor is supported
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return dataFlavors[0].equals(flavor);
	}

	// Returns file
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return files;
	}

}