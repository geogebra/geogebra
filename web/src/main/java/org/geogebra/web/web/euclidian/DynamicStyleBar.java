package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoElementSelectionListener;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Dynamically positioned stylebar
 * 
 * @author Judit
 *
 */
public class DynamicStyleBar extends EuclidianStyleBarW {

	/**
	 * @param ev
	 *            parent view
	 */
	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		if (!app.has(Feature.NEW_TOOLBAR)) {
			addStyleName("DynamicStyleBar");
		} else {
			addStyleName("matDynStyleBar");
		}

		app.getSelectionManager()
				.addSelectionListener(new GeoElementSelectionListener() {
					@Override
					public void geoElementSelected(GeoElement geo,
							boolean addToSelection) {
						if (addToSelection) {
							return;
						}

						if (app.has(Feature.LOCKED_GEO_HAVE_DYNAMIC_STYLEBAR)) {
							// If the activeGeoList will be null or empty, this will
							// hide the dynamic stylebar.
							// If we clicked on a locked geo, the activeGeoList will
							// contain it, so in this case the dynamic stylebar will
							// be visible yet.
							DynamicStyleBar.this.updateStyleBar();
						} else {
							DynamicStyleBar.this.setVisible(addToSelection);
						}
					}
				});
		stopPointer(getElement());
	}

	private native void stopPointer(Element element) /*-{
		if ($wnd.PointerEvent) {
			var evts = [ "PointerDown", "PointerUp" ];
			for ( var k in evts) {
				element.addEventListener(evts[k].toLowerCase(), function(e) {
					e.stopPropagation()
				});
			}
		}

	}-*/;

	/**
	 * Sets the position of dynamic style bar. newPos position of right top
	 * corner of bounding box of drawable
	 */
	@Override
	public void setPosition(GRectangle2D gRectangle2D, boolean hasBoundingBox, boolean isPoint) {

		if (gRectangle2D == null) {
			return;
		}

		int move = this.getContextMenuButton().getAbsoluteLeft()
				- this.getAbsoluteLeft();
		int height = this.getOffsetHeight();

		double left, top = -1;

		if (!isPoint) {
			if (hasBoundingBox) {
				top = gRectangle2D.getMinY() - height - 10;
			} else { // line has no bounding box
				top = gRectangle2D.getMinY();
			}
		}

		// if there is no enough place on the top of bounding box, dynamic
		// stylebar will be visible at the bottom of bounding box,
		// stylebar of points will be bottom of point if possible.
		if (top < 0) {
			top = gRectangle2D.getMaxY() + 10;
		}

		int maxtop = app.getActiveEuclidianView().getHeight() - height - 5;
		if (top > maxtop) {
			if (isPoint) {
				// if there is no enough place under the point
				// put the dyn. stylebar above the point
				top = gRectangle2D.getMinY() - height - 10;
			} else {
				top = maxtop;
			}
		}


		// get left position
		if (hasBoundingBox) {
			left = gRectangle2D.getMaxX() - move;
		} else { // line has no bounding box
			left = gRectangle2D.getMaxX() - height / 2.0;
		}
		if (left < 0) {
			left = 0;
		}
		if (left + this.getOffsetWidth() > app.getActiveEuclidianView().getWidth()) {
			left = app.getActiveEuclidianView().getWidth() - this.getOffsetWidth();
		}


		this.getElement().getStyle().setLeft(left, Unit.PX);
		this.getElement().getStyle().setTop(top, Unit.PX);
	}
	
	@Override
	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		// make sure it reflects selected geos
		setOpen(true);

		setMode(EuclidianConstants.MODE_MOVE);
		super.updateStyleBar();

		if (activeGeoList == null || activeGeoList.size() == 0) {
			this.setVisible(false);
			return;
		}
		
		this.getElement().getStyle().setTop(-10000, Unit.PX);

		DrawableND dr = ev.getDrawableND(activeGeoList.get(0));

		if (!(dr instanceof Drawable)) {
			return;
		}


		if (app.has(Feature.FUNCTIONS_DYNAMIC_STYLEBAR_POSITION)
				&& activeGeoList.get(0) instanceof GeoFunction) {
			setPositionForFunction();
		} else if (app.has(Feature.DYNAMIC_STYLEBAR_SELECTION_TOOL)
				&& app.getMode() == EuclidianConstants.MODE_SELECT) {
			setPosition(app.getActiveEuclidianView().getSelectionRectangle(),
					true, false);
		} else {
			setPosition(((Drawable) dr).getBoundsForStylebarPosition(),
					!(dr instanceof DrawLine), dr instanceof DrawPoint);
		}
	}

	private void setPositionForFunction() {
		GPoint lastMouseLoc = this.getView().getEuclidianController()
				.getMouseLoc();
		int xPos = lastMouseLoc.x + 10;
		int yPos = lastMouseLoc.y + 10;

		// Keep dynamic stylebar on the screen
		if (yPos < 5) {
			yPos = 5;
		}
		int maxtop = app.getActiveEuclidianView().getHeight()
				- getOffsetHeight() - 5;
		if (yPos > maxtop) {
			yPos = maxtop;
		}
		if (xPos < 0) {
			xPos = 0;
		}
		if (xPos + this.getOffsetWidth() > app.getActiveEuclidianView()
				.getWidth()) {
			xPos = app.getActiveEuclidianView().getWidth()
					- this.getOffsetWidth();
		}

		this.getElement().getStyle().setLeft(xPos, Unit.PX);
		this.getElement().getStyle().setTop(yPos, Unit.PX);
	}

	@Override
	protected boolean isDynamicStylebar(){
		return true;
	}
	
}
