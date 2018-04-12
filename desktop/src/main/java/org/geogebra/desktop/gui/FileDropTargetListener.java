/*
 * GeoGebra - Dynamic Mathematics for Everyone 
 * http://www.geogebra.org
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
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
	 * @param t
	 * @return
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
	 * @param fileName
	 * @return
	 */
	private static boolean isGGBFile(String fileName) {
		return StringUtil.getFileExtension(fileName)
				.equals(FileExtensions.GEOGEBRA);
	}

	/**
	 * Tests if a file has the GeoGebra ggt extension
	 * 
	 * @param fileName
	 * @return
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
				// Application.debug("javaFileList is supported");
				List<File> list = (List<File>) transferable
						.getTransferData(DataFlavor.javaFileListFlavor);
				ListIterator<File> it = list.listIterator();
				while (it.hasNext()) {
					File f = it.next();
					al.add(f);
				}
			} else if (transferable
					.isDataFlavorSupported(GuiManagerD.uriListFlavor)) {
				// Application.debug("uri-list flavor is supported");
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