/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/**
 * AlgebraController.java
 *
 * Created on 05. September 2001, 09:11
 */

package geogebra.gui.view.algebra;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class AlgebraController extends AlgebraTreeController
implements DragGestureListener, DragSourceListener {

	private DragSource ds;
	
	/** Creates new CommandProcessor */
	public AlgebraController(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected boolean checkDoubleClick(GeoElement geo, MouseEvent e){
		// check double click
		int clicks = e.getClickCount();
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (clicks == 2) {										
			app.clearSelectedGeos();
			ev.resetMode();
			if (geo != null && !Application.isControlDown(e)) {
				view.startEditing(geo, e.isShiftDown());						
			}
			return true;
		} 	
		return false;
	}
	
	@Override
	protected boolean viewIsEditing(){
		return view.isEditing();
	}
	
	@Override
	protected void viewCancelEditing(){
		view.cancelEditing();
	}

	@Override	
	public void setTree(AlgebraTree tree){
		super.setTree(tree);
		this.view = (AlgebraView) tree;
	}
	

	protected void enableDnD(){
		ds = new DragSource();
		DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer((AlgebraView)view, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	

	//=====================================================
	// Drag and Drop 
	//=====================================================
	
	public void dragDropEnd(DragSourceDropEvent e) {}
	public void dragEnter(DragSourceDragEvent e) {}
	public void dragExit(DragSourceEvent e) {}
	public void dragOver(DragSourceDragEvent e) {}
	public void dropActionChanged(DragSourceDragEvent e) {}

	
	private ArrayList<String> geoLabelList;

	public void dragGestureRecognized(DragGestureEvent dge) {

		if(geoLabelList == null)
			geoLabelList = new ArrayList<String>();
		else
			geoLabelList.clear();

		for(GeoElement geo : app.getSelectedGeos()){
			geoLabelList.add(geo.getLabel(StringTemplate.defaultTemplate));
		}

		// if we have something ... do the drag! 
		if(geoLabelList.size() > 0){
			
			// create drag image 
			StringBuilder sb = new StringBuilder();
			sb.append("\\fbox{\\begin{array}{l}"); 
			for(GeoElement geo:app.getSelectedGeos()){
				sb.append(geo.getLaTeXAlgebraDescription(true,StringTemplate.latexTemplate));
				sb.append("\\\\");
			}
			sb.append("\\end{array}}");
			ImageIcon ic  = GeoGebraIcon.createLatexIcon((Application)app, sb.toString(), ((Application)app).getPlainFont(), false, Color.DARK_GRAY, null);
			
			// start drag
			ds.startDrag(dge, DragSource.DefaultCopyDrop, ic.getImage(), 
					new Point(-5,-ic.getIconHeight()+5), new TransferableAlgebraView(geoLabelList),  this);
		}

	}

	/**
	 * 	Extension of Transferable for exporting AlgegraView selections as a list of Geo labels
	 */
	class TransferableAlgebraView implements Transferable {

		public final DataFlavor algebraViewFlavor = new DataFlavor(AlgebraView.class, "geoLabel list");
		private final DataFlavor supportedFlavors[] = { algebraViewFlavor };

		private ArrayList<String> geoLabelList;

		public TransferableAlgebraView(ArrayList<String> geoLabelList) {
			this.geoLabelList = geoLabelList;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(algebraViewFlavor))
				return true;
			return false;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (flavor.equals(algebraViewFlavor))
				return geoLabelList;
			throw new UnsupportedFlavorException(flavor);
		}
	}
}
