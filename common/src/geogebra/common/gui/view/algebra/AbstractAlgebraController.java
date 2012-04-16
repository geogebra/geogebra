/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.gui.view.algebra;

import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

import java.util.Iterator;

public class AbstractAlgebraController {

	//FIXME: make e.isControlDown like Application.isControlDown etc.
	//FIXME: make something instead of the outcommented things, etc.

	private Kernel kernel;
	private AbstractApplication app;

	private AlgebraView view;

	//private GeoVector tempVec;
	//private boolean kernelChanged;

	//private DragSource ds;

	/** Creates new CommandProcessor */
	public AbstractAlgebraController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();		
	}

	public void setView(AlgebraView view) {
		this.view = view;
	}

	public AbstractApplication getApplication() {
		return app;
	}

	public Kernel getKernel() {
		return kernel;
	}

	/*protected void enableDnD(){
		ds = new DragSource();
		DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(view, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}*/

	/*
	 * MouseListener implementation for popup menus
	 */

	private GeoElement lastSelectedGeo = null;
	private boolean skipSelection;

	public void mouseClicked(AbstractEvent e) {	
		// right click is consumed in mousePressed, but in GeoGebra 3D,
		// where heavyweight popup menus are enabled this doesn't work
		// so make sure that this is no right click as well (ticket #302)
		if (/*e.isConsumed() FIXME||*/ e.isRightClick()) {
			return;
		}

		// get GeoElement at mouse location		
		Object tp = view.getPathForLocation(e.getX(), e.getY());
		GeoElement geo = view.getGeoElementForPath(tp);

		// check if we clicked on the 16x16 show/hide icon
		if (geo != null) {
			Rectangle rect = (Rectangle)view.getPathBounds(tp);
			boolean iconClicked = rect != null && e.getX() - rect.getX() < 16; // distance from left border				
			if (iconClicked) {
				// icon clicked: toggle show/hide
				geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
				geo.update();
				app.storeUndoInfo();
				kernel.notifyRepaint();
				return;
			}		
		}

		// check double click
		int clicks = e.getClickCount();
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (clicks == 2) {										
			app.clearSelectedGeos();
			ev.resetMode();
			if (geo != null && !e.isControlDown()) {
				view.startEditing(geo, e.isShiftDown());
			}
			return;
		} 	

		int mode = ev.getMode();
		if (!skipSelection && (mode == EuclidianConstants.MODE_MOVE || mode == EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) ) {
			// update selection	
			if (geo == null){
				app.clearSelectedGeos();
			}
			else {					
				// handle selecting geo
				if (e.isControlDown()) {
					app.toggleSelectedGeo(geo); 													
					if (app.getSelectedGeos().contains(geo)) lastSelectedGeo = geo;
				} else if (e.isShiftDown() && lastSelectedGeo != null) {
					boolean nowSelecting = true;
					boolean selecting = false;
					boolean aux = geo.isAuxiliaryObject();
					boolean ind = geo.isIndependent();
					boolean aux2 = lastSelectedGeo.isAuxiliaryObject();
					boolean ind2 = lastSelectedGeo.isIndependent();

					if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {

						Iterator<GeoElement> it = kernel.getConstruction().getGeoSetLabelOrder().iterator();

						boolean direction = geo.getLabel(StringTemplate.defaultTemplate).
								compareTo(lastSelectedGeo.getLabel(StringTemplate.defaultTemplate)) < 0;

						while (it.hasNext()) {
							GeoElement geo2 = it.next();
							if ((geo2.isAuxiliaryObject() == aux && aux)
									|| (geo2.isAuxiliaryObject() == aux && geo2.isIndependent() == ind)) {

								if (direction && geo2.equals(lastSelectedGeo)) selecting = !selecting;
								if (!direction && geo2.equals(geo)) selecting = !selecting;

								if (selecting) {
									app.toggleSelectedGeo(geo2);
									nowSelecting = app.getSelectedGeos().contains(geo2);
								}

								if (!direction && geo2.equals(lastSelectedGeo)) selecting = !selecting;
								if (direction && geo2.equals(geo)) selecting = !selecting;
							}
						}
					}

					if (nowSelecting) {
						app.addSelectedGeo(geo); 
						lastSelectedGeo = geo;
					} else {
						app.removeSelectedGeo(lastSelectedGeo);
						lastSelectedGeo = null;
					}

				} else {							
					app.clearSelectedGeos();
					app.addSelectedGeo(geo);
					lastSelectedGeo = geo;
				}
			}
		} 
		else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			AbstractEvent event = e;
			ev.clickedGeo(geo, event);
			event.release();
		} else 
			// tell selection listener about click
			app.geoElementSelected(geo, false);


		// Alt click: copy definition to input field
		if (geo != null && e.isAltDown() && app.showAlgebraInput()) {			
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);			
		}

		ev.mouseMovedOver(null);		
	}

	public void mousePressed(AbstractEvent e) {
		view.cancelEditing();
		
		new geogebra.common.awt.Point(e.getPoint().x,e.getPoint().y);

		boolean rightClick = app.isRightClickEnabled() && e.isRightClick();

		// RIGHT CLICK
		if (rightClick) {
			/*e.consume(); FIXME*/

			// get GeoElement at mouse location		
			Object tp = view.getPathForLocation(e.getX(), e.getY());
			GeoElement geo = view.getGeoElementForPath(tp);

			if (geo != null && !app.containsSelectedGeo(geo)) {
				app.clearSelectedGeos();					
			}

			// single selection: popup menu
			if (app.selectedGeosSize() < 2) {
				/*
				if(geo == null) {
					AlgebraContextMenu contextMenu = new AlgebraContextMenu(app);
					contextMenu.show(view, e.getPoint().x, e.getPoint().y);
				} else {
					ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
					temp.add(geo);
					app.getGuiManager().showPopupMenu(temp, view, mouseCoords);
				}
				*/			
			} 
			// multiple selection: popup menu (several geos)
			else {
				if(geo != null) {
					//app.getGuiManager().showPopupMenu(app.getSelectedGeos(), view, mouseCoords);
				}
			}	

			// LEFT CLICK	
		} else {

			// When a single, new selection is made with no key modifiers
			// we need to handle selection in mousePressed, not mouseClicked.
			// By doing this selection early, a DnD drag will come afterwards
			// and grab the new selection. 
			// All other selection types must be handled later in mouseClicked. 
			// In this case a DnD drag starts first and grabs the previously selected 
			// geos (e.g. cntrl-selected or EV selected) as the user expects.

			skipSelection = false; // flag to prevent duplicate selection in MouseClicked

			Object tp = view.getPathForLocation(e.getX(), e.getY());
			GeoElement geo = view.getGeoElementForPath(tp);	
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			int mode = ev.getMode();

			if ( (mode == EuclidianConstants.MODE_MOVE || mode == EuclidianConstants.MODE_SELECTION_LISTENER)  && 
					!e.isControlDown() && !e.isShiftDown() 
					&& geo != null  && !app.containsSelectedGeo(geo)) 
			{					
				app.clearSelectedGeos();
				app.addSelectedGeo(geo);
				lastSelectedGeo = geo;
				skipSelection = true;
			} 

		}
	}

	public void mouseReleased(AbstractEvent e) {
	}

	public void mouseEntered(AbstractEvent p1) {
	}

	public void mouseExited(AbstractEvent p1) {		
	}

	// MOUSE MOTION LISTENER
	public void mouseDragged(AbstractEvent arg0) {}

	// tell EuclidianView
	public void mouseMoved(AbstractEvent e) {		
		if (view.isEditing())
			return;

		int x = e.getX();
		int y = e.getY();

		GeoElement geo = view.getGeoElementForLocation(view, x, y);

		// tell EuclidianView to handle mouse over
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		ev.mouseMovedOver(geo);								

		if (geo != null) {
			app.setTooltipFlag();
			//FIXMEview.setToolTipText(geo.getLongDescriptionHTML(true, true));
			app.clearTooltipFlag();
		} //FIXMEelse
			//FIXMEview.setToolTipText(null);						
	}
	

	//=====================================================
	// Drag and Drop 
	//=====================================================
	
	public void dragDropEnd(AbstractEvent e) {}
	public void dragEnter(AbstractEvent e) {}
	public void dragExit(AbstractEvent e) {}
	public void dragOver(AbstractEvent e) {}
	public void dropActionChanged(AbstractEvent e) {}

	/*
	public void dragGestureRecognized(AbstractEvent dge) {

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
			ImageIcon ic  = GeoGebraIcon.createLatexIcon(app, sb.toString(), app.getPlainFont(), false, Color.DARK_GRAY, null);
			
			// start drag
			ds.startDrag(dge, DragSource.DefaultCopyDrop, ic.getImage(), 
					new Point(-5,-ic.getIconHeight()+5), new TransferableAlgebraView(geoLabelList),  this);
		}

	}*/

	/**
	 * 	Extension of Transferable for exporting AlgegraView selections as a list of Geo labels
	 */
	/*class TransferableAlgebraView implements Transferable {

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
	}*/
}
