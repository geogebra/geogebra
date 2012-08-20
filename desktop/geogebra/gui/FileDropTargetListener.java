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

package geogebra.gui;

import geogebra.common.main.App;
import geogebra.common.util.StringUtil;
import geogebra.main.AppD;

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

public class FileDropTargetListener implements DropTargetListener {
	
	static DataFlavor urlFlavor, macPictStreamFlavor;
	static {

		try { 
			urlFlavor = 
			new DataFlavor ("application/x-java-url; class=java.net.URL"); 			
		} catch (ClassNotFoundException cnfe) { 
			cnfe.printStackTrace( );
		}
	}

	private AppD app;

	public FileDropTargetListener(AppD app) {
		this.app = app;
	}

	public void dragEnter(DropTargetDragEvent event) {
	}

	public void dragExit(DropTargetEvent event) {
	}

	public void dragOver(DropTargetDragEvent event) {
		// provide visual feedback
		event.acceptDrag(DnDConstants.ACTION_COPY);
	}

	public void dropActionChanged(DropTargetDragEvent event) {
	}

	public void drop(DropTargetDropEvent event) {
		if ((event.getSourceActions() & DnDConstants.ACTION_COPY) != 0)
			event.acceptDrop(DnDConstants.ACTION_COPY);
		else {
			event.rejectDrop();
			return;
		}

		event.dropComplete(handleFileDrop(event.getTransferable()));

	}

	/**
	 * Determines if a transferable contains ggb/ggt files and attempts
	 * to open them.
	 * 
	 * @param t
	 * @return
	 */
	public boolean handleFileDrop(Transferable t){

		ArrayList<File> al = getGGBfiles(t);	

		if (al.size() == 0) {
			return false;
		}
		
		boolean allGGT = true;
		for (int i = al.size() - 1 ; i >= 0 ; i--) {
			if (!isGGBFile(al.get(i).getName()) && !isGGTFile(al.get(i).getName())) {
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
			File [] files = new File[al.size()];
			for (int i = 0 ; i < al.size() ; i++)
				files[i] = al.get(i);
			app.getGuiManagerD().doOpenFiles(files, true);			
			return true;			
		}			
		return false;
	}
	
	
	/**
	 * Tests if a file has the GeoGebra ggb extension
	 * @param fileName
	 * @return
	 */
	private static boolean isGGBFile(String fileName){
		int mid = fileName.lastIndexOf(".");
	    String ext = fileName.substring(mid+1,fileName.length());
	    return StringUtil.toLowerCase(ext).equals(AppD.FILE_EXT_GEOGEBRA);
	}

	/**
	 * Tests if a file has the GeoGebra ggt extension
	 * @param fileName
	 * @return
	 */
	private static boolean isGGTFile(String fileName){
		int mid = fileName.lastIndexOf(".");
	    String ext = fileName.substring(mid+1,fileName.length());
	    return StringUtil.toLowerCase(ext).equals(AppD.FILE_EXT_GEOGEBRA_TOOL);
	}

	private ArrayList<File> getGGBfiles(Transferable transferable) {

		ArrayList<File> al = new ArrayList<File>();

		try {
			// try to get an image
			if (transferable.isDataFlavorSupported (DataFlavor.imageFlavor)) { 
				App.debug("image flavor not supported"); 
				//Image img = (Image) trans.getTransferData (DataFlavor.imageFlavor); 
			} else if (transferable.isDataFlavorSupported (DataFlavor.javaFileListFlavor)) {
				//Application.debug("javaFileList is supported");
				List<File> list = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				ListIterator<File> it = list.listIterator( );   
				while (it.hasNext( )) {
					File f = it.next( );
					al.add(f);
				}
			} else if (transferable.isDataFlavorSupported (GuiManagerD.uriListFlavor)) {
				//Application.debug("uri-list flavor is supported"); 
				String uris = (String)
				transferable.getTransferData (GuiManagerD.uriListFlavor);

				// url-lists are defined by rfc 2483 as crlf-delimited 
				StringTokenizer st = new StringTokenizer (uris, "\r\n");   
				while (st.hasMoreTokens ( )) {
					String uriString = st.nextToken( );
					if(uriString.startsWith("http://") && isGGBFile(uriString)){
						app.getGuiManagerD().loadURL(uriString, true);
					}else{
						URI uri = new URI(uriString);
						al.add(new File(uri));
					}
				}
			} else if (transferable.isDataFlavorSupported (urlFlavor)) {
				App.debug("url flavor not supported");
				//URL url = (URL) trans.getTransferData (urlFlavor);
			} else App.debug("flavor not supported: "+transferable);
		} catch (Exception e) {
			e.printStackTrace( );
		} 

		return al;
	}
}