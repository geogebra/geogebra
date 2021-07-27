package org.geogebra.web.touch.gui.view;

import org.geogebra.web.full.gui.view.consprotocol.ConstructionProtocolViewW;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * ConstructionProtocolView for touch-devices
 * @see ConstructionProtocolViewW
 */
public class ConstructionProtocolViewT extends ConstructionProtocolViewW {
	
	/** long press timeout in ms **/
	private static final int LONG_PRESS_TIMEOUT = 500;
	/** **/
	private static final int LONG_PRESS_THRESHOLD = 10;
	/** **/
	private static final int DUMMY_DRAG_ELEM_OFFSET_Y = 15;
	/** y coordinates of first tap, used to detect longTaps **/
	int yStart;
	/** x coordinates of first tap, used to detect longTaps **/
	int xStart;
	/** start of first tap, used to detect longTaps **/
	long startOfTap;
	/** current y coordinates of touchMove **/
	int yMove;
	/** current x coordinates of touchMove **/
	int xMove;
	/** if tap was detected as "dragging a row" **/
	boolean isDragging;
	/** a dummy drag-element **/
	FlowPanel dummyDragElem;

	/**
	 * @param app {@link AppW}
	 */
	public ConstructionProtocolViewT(AppW app) {
	    super(app);
    }

	@Override
    protected void addDragDropHandlers() {
    	table.addBitlessDomHandler(new TouchStartHandler() {
			
			@Override
			public void onTouchStart(TouchStartEvent event) {
				isDragging = false;
				startOfTap = System.currentTimeMillis();
				yStart = event.getTouches().get(0).getClientY();
				xStart = event.getTouches().get(0).getClientX();
			}
		}, TouchStartEvent.getType());
    	
    	table.addBitlessDomHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				// used to get coordinates for TouchEnd
				yMove = event.getTouches().get(0).getClientY();
				xMove = event.getTouches().get(0).getClientX();
				if (!isDragging && isLongTap()) {
					//start dragging, select row to drag, show dummyDragElement
					isDragging = true;
					handleDrag(yStart);
					dummyDragElem = new FlowPanel();
					dummyDragElem.addStyleName("dummyDragElem");
					dummyDragElem.getElement().setInnerHTML(draggedRow.getInnerHTML());
					RootPanel.get().add(dummyDragElem);
					event.preventDefault(); //to avoid scrolling
				} else if (isDragging) {
					dummyDragElem.getElement().setAttribute("style", getStyleString());
					event.preventDefault();
				}
				//TODO if end of table is reached with pressed finger, scroll!!!
			}

			/**
			 * 
			 * @return String - the style for the {@link ConstructionProtocolViewW#dummyDragElem dummy drag-element}
			 */
			private String getStyleString() {
				int width = draggedRow.getOffsetWidth();
				String color = draggedRow.getAttribute("style");
				return color + "; width: " + width
						+ "px; background-color: #CCCCFF; opacity: 0.5;"
						+ " z-index: 900; position: absolute; left: "
						+ table.getElement().getAbsoluteLeft() + "px; top: "
						+ (yMove - DUMMY_DRAG_ELEM_OFFSET_Y)
						+ "px; overflow: hidden; display: -webkit-inline-box;font-family: "
						+ GFontW.GEOGEBRA_FONT_SANSERIF + ";";
			}

			/**
			 * @return true if it is a long tap
			 */
			private boolean isLongTap() {
				return xMove <= xStart + LONG_PRESS_THRESHOLD
						&& xMove >= xStart - LONG_PRESS_THRESHOLD
						&& yMove <= yStart + LONG_PRESS_THRESHOLD
						&& yMove >= yStart - LONG_PRESS_THRESHOLD
						&& System.currentTimeMillis() > startOfTap
								+ LONG_PRESS_TIMEOUT;
			}
		}, TouchMoveEvent.getType());
    	
    	table.addBitlessDomHandler(new TouchEndHandler() {
			
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				if (draggedRow != null) {
					draggedRow.removeClassName("isDragging");
				}
				if (!isDragging) {
					return;
				}
				RootPanel.get().remove(dummyDragElem);
				handleDrop(yMove);
				isDragging = false;
			}
		}, TouchEndEvent.getType());
    	
    }
}
