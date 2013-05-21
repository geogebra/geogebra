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

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.AppD;

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

public class AlgebraControllerD extends AlgebraTreeController
implements DragGestureListener, DragSourceListener {

	private DragSource ds;
	
	/** Creates new CommandProcessor */
	public AlgebraControllerD(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected boolean checkDoubleClick(GeoElement geo, MouseEvent e){
		// check double click
		int clicks = e.getClickCount();
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (clicks == 2) {										
			selection.clearSelectedGeos(true,false);
			app.updateSelection(false);
			ev.resetMode();
			if (geo != null && !AppD.isControlDown(e)) {
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
	public void mousePressed(java.awt.event.MouseEvent e) {
		view.cancelEditing();
		boolean rightClick = app.isRightClickEnabled() && AppD.isRightClick(e);
		if (rightClick) {// RIGHT CLICK
			geogebra.common.awt.GPoint mouseCoords = new geogebra.common.awt.GPoint(e.getPoint().x,e.getPoint().y);
			rightPress(e, mouseCoords);
		} else {// LEFT CLICK	
			leftPress(e);
		}
	}

	@Override	
	public void setTree(AlgebraTree tree){
		super.setTree(tree);
		this.view = (AlgebraViewD) tree;
	}
	

	protected void enableDnD(){
		ds = new DragSource();
		DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer((AlgebraViewD)view, DnDConstants.ACTION_COPY_OR_MOVE, this);
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

		for(GeoElement geo : selection.getSelectedGeos()){
			geoLabelList.add(geo.getLabel(StringTemplate.defaultTemplate));
		}

		// if we have something ... do the drag! 
		if(geoLabelList.size() > 0){

			String latex;
			
			boolean showJustFirstGeoInDrag = false;

			if (selection.getSelectedGeos().size() == 1) {
				showJustFirstGeoInDrag = true;
			} else {

				// workaround for http://forge.scilab.org/index.php/p/jlatexmath/issues/749/#preview
				for(GeoElement geo:selection.getSelectedGeos()){
					if (geo.isGeoCurveCartesian()) {
						showJustFirstGeoInDrag = true;
						break;
					}
				}
			}


			if (showJustFirstGeoInDrag) {
				latex = selection.getSelectedGeos().get(0).getLaTeXAlgebraDescription(true,StringTemplate.latexTemplate);
			} else {

				// create drag image 
				StringBuilder sb = new StringBuilder();
				sb.append("\\fbox{\\begin{array}{l}"); 
				for(GeoElement geo:selection.getSelectedGeos()){
					sb.append(geo.getLaTeXAlgebraDescription(true,StringTemplate.latexTemplate));
					sb.append("\\\\");
				}
				sb.append("\\end{array}}");
				latex = sb.toString();
			}
			ImageIcon ic  = GeoGebraIcon.createLatexIcon((AppD)app, latex, ((AppD)app).getPlainFont(), false, Color.DARK_GRAY, null);

			// start drag
			ds.startDrag(dge, DragSource.DefaultCopyDrop, ic.getImage(), 
					new Point(-5, -30), new TransferableAlgebraView(geoLabelList),  this);
		}

	}

	/**
	 * 	Extension of Transferable for exporting AlgegraView selections as a list of Geo labels
	 */
	class TransferableAlgebraView implements Transferable {

		public final DataFlavor algebraViewFlavor = new DataFlavor(AlgebraViewD.class, "geoLabel list");
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
	
	
	
	@Override
	protected void euclidianViewClick(EuclidianViewInterfaceCommon ev, GeoElement geo, MouseEvent e){
		// let euclidianView know about the click
		AbstractEvent event = geogebra.euclidian.event.MouseEventD.wrapEvent(e);
		ev.clickedGeo(geo, app.isControlDown(event));
		event.release();
		
		//if click created a geo, notify app to select it
		ev.getEuclidianController().setJustCreatedGeosSelected();
		
		
	}
	
	@Override
	protected void highlight(EuclidianViewInterfaceCommon ev, GeoElement geo){
		if (ev.getMode()==EuclidianConstants.MODE_MOVE)
			super.highlight(ev, geo);
		else
			ev.mouseMovedOver(geo);
	}
	
	@Override
	protected void highlight(EuclidianViewInterfaceCommon ev, ArrayList<GeoElement> geos){
		if (ev.getMode()==EuclidianConstants.MODE_MOVE)
			super.highlight(ev, geos);
		else
			ev.mouseMovedOverList(geos);
	}

	@Override
	protected boolean leftPressCanSelectGeo(java.awt.event.MouseEvent e, GeoElement geo){

		int mode = app.getActiveEuclidianView().getMode();	
		if ( (mode == EuclidianConstants.MODE_MOVE || mode == EuclidianConstants.MODE_SELECTION_LISTENER)  && 
				!AppD.isControlDown(e) && !e.isShiftDown())
		{
			if( !setSelectedGeo(geo)) 
				return true;

		}
		return false;

	}
	
	@Override
	protected boolean isSelectionModeForClick(int mode){
		return mode == EuclidianConstants.MODE_MOVE || mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET;
	}
}
