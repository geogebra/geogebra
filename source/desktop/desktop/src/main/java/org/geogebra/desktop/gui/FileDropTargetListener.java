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

package org.geogebra.desktop.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

public class FileDropTargetListener implements DropTargetListener {

	static DataFlavor urlFlavor;

	static {
		try {
			urlFlavor = new DataFlavor(
					"application/x-java-url; class=java.net.URL");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	private AppD app;

	/**
	 * @param app
	 *            application
	 */
	public FileDropTargetListener(AppD app) {
		this.app = app;
	}

	@Override
	public void dragEnter(DropTargetDragEvent event) {
		// only dragOver /drop
	}

	@Override
	public void dragExit(DropTargetEvent event) {
		// only dragOver /drop
	}

	@Override
	public void dragOver(DropTargetDragEvent event) {
		// provide visual feedback
		event.acceptDrag(DnDConstants.ACTION_COPY);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent event) {
		// only dragOver /drop
	}

	@Override
	public void drop(DropTargetDropEvent event) {
		if ((event.getSourceActions() & DnDConstants.ACTION_COPY) != 0) {
			event.acceptDrop(DnDConstants.ACTION_COPY);
		} else {
			event.rejectDrop();
			return;
		}

		event.dropComplete(handleFileDrop(event.getTransferable()));

	}

	/**
	 * Determines if a transferable contains ggb/ggt files and attempts to open
	 * them.
	 * 
	 * @param t trnaferable
	 * @return whether ggb/ggt files were found
	 */
	public boolean handleFileDrop(Transferable t) {
		ArrayList<File> al = getGGBfiles(t);

		if (al.size() == 0) {
			return false;
		}

		boolean allGGT = true;
		for (int i = al.size() - 1; i >= 0; i--) {
			if (!isGGBFile(al.get(i).getName())
					&& !isGGTFile(al.get(i).getName())) {
				al.remove(i);
			} else {
				if (!isGGTFile(al.get(i).getName())) {
					allGGT = false;
				}
			}
		}

		if (al.size() == 0) {
			return false;
		}

		else if (allGGT || app.isSaved() || app.saveCurrentFile()) {
			File[] files = new File[al.size()];
			for (int i = 0; i < al.size(); i++) {
				files[i] = al.get(i);
			}
			((GuiManagerD) app.getGuiManager()).doOpenFiles(files, true);
			return true;
		}
		return false;
	}

	/**
	 * Tests if a file has the GeoGebra ggb extension
	 * 
	 * @param fileName filename
	 * @return whether it has .ggb extension
	 */
	private static boolean isGGBFile(String fileName) {
		return StringUtil.getFileExtension(fileName)
				.equals(FileExtensions.GEOGEBRA);
	}

	/**
	 * Tests if a file has the GeoGebra ggt extension
	 * 
	 * @param fileName filename
	 * @return whether it has .ggt extension
	 */
	private static boolean isGGTFile(String fileName) {
		FileExtensions ext = StringUtil.getFileExtension(fileName);
		return ext.equals(FileExtensions.GEOGEBRA_TOOL);
	}

	private ArrayList<File> getGGBfiles(Transferable transferable) {

		ArrayList<File> al = new ArrayList<>();

		try {
			// try to get an image
			if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				Log.debug("image flavor not supported");
				// Image img = (Image) trans.getTransferData
				// (DataFlavor.imageFlavor);
			} else if (transferable
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<File> list = (List<File>) transferable
						.getTransferData(DataFlavor.javaFileListFlavor);
				ListIterator<File> it = list.listIterator();
				while (it.hasNext()) {
					File f = it.next();
					al.add(f);
				}
			} else if (transferable
					.isDataFlavorSupported(GuiManagerD.uriListFlavor)) {
				String uris = (String) transferable
						.getTransferData(GuiManagerD.uriListFlavor);

				// url-lists are defined by rfc 2483 as crlf-delimited
				StringTokenizer st = new StringTokenizer(uris, "\r\n");
				while (st.hasMoreTokens()) {
					String uriString = st.nextToken();
					if (uriString.startsWith("http://")
							&& isGGBFile(uriString)) {
						((GuiManagerD) app.getGuiManager()).loadURL(uriString,
								true);
					} else {
						URI uri = new URI(uriString);
						al.add(new File(uri));
					}
				}
			} else if (transferable.isDataFlavorSupported(urlFlavor)) {
				Log.debug("url flavor not supported");
				// URL url = (URL) trans.getTransferData (urlFlavor);
			} else {
				Log.debug("flavor not supported: " + transferable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return al;
	}
}