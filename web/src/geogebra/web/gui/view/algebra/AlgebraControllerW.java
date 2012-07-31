package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.euclidian.event.MouseEvent;
import geogebra.web.euclidian.event.ZeroOffset;

import java.util.Iterator;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;


public class AlgebraControllerW extends geogebra.common.gui.view.algebra.AlgebraController
implements MouseOverHandler, MouseMoveHandler, MouseDownHandler, MouseUpHandler, MouseOutHandler {

	//FIXME: make e.isControlDown like Application.isControlDown etc.
	//FIXME: make something instead of the outcommented things, etc.
	//FIXME: make event handling

	public AlgebraControllerW(Kernel kernel) {
		super(kernel);
	}

	/*
	 * MouseListener implementation for popup menus
	 */

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
			GRectangle rect = (GRectangle)view.getPathBounds(tp);
			boolean iconClicked = rect != null && e.getX() - rect.getX() < 16; // distance from left border				
			if (iconClicked) {
				// icon clicked: toggle show/hide
				geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
				geo.updateVisualStyle();
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
					app.clearSelectedGeos(false); //repaint will be done next step
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
				app.clearSelectedGeos(false); //repaint will be done next step
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

	public void onMouseDown(MouseDownEvent event) {
		mousePressed(MouseEvent.wrapEvent(event.getNativeEvent(),ZeroOffset.instance));
	}

	public void onMouseUp(MouseUpEvent event) {
		// TODO: make it care for mouse down too 
		mouseClicked(MouseEvent.wrapEvent(event.getNativeEvent(),ZeroOffset.instance));
	}

	public void onMouseMove(MouseMoveEvent event) {
		mouseMoved(MouseEvent.wrapEvent(event.getNativeEvent(),ZeroOffset.instance));
	}

	public void onMouseOver(MouseOverEvent event) {
		
	}

	public void onMouseOut(MouseOutEvent event) {
		
	}
}
