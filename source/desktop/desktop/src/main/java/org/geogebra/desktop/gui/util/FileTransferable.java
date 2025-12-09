/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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