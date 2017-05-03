package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawLocus;
import org.geogebra.common.euclidian.draw.DrawSlider;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

public class DynamicStyleBar extends EuclidianStyleBarW {

	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		addStyleName("DynamicStyleBar");

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
	public void setPosition(GRectangle2D gRectangle2D, boolean hasBoundingBox) {

		if (gRectangle2D == null) {
			return;
		}

		int move = this.getContextMenuButton().getAbsoluteLeft()
				- this.getAbsoluteLeft();
		int height = this.getOffsetHeight();

		double left, top;
		if (hasBoundingBox) {
			left = gRectangle2D.getMaxX() - move;
			top = gRectangle2D.getMinY() - height - 10;

		} else { // line has no bounding box
			left = gRectangle2D.getMaxX() - height / 2.0;
			top = gRectangle2D.getMinY();
		}

		// if there is no enough place on the top of bounding box, dynamic
		// stylebar will be visible at the bottom of bounding box.
		if (top < 0) {
			top = gRectangle2D.getMaxY() + 10;
		}

		int maxtop = app.getActiveEuclidianView().getHeight() - height - 5;
		if (top > maxtop) {
			top = maxtop;
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
		
		if (dr instanceof DrawLine) {
			GRectangle rect = ((DrawLine) dr).getPreferredStylebarPosition();
			setPosition(rect, false);
		} else if (dr instanceof DrawLocus) {
			setPosition(((DrawLocus) dr).getGpBounds(), true);
		} else if (dr instanceof DrawSlider) {
			setPosition(((DrawSlider) dr).getBoundsForStylebarPosition(), true);
		} else {
			setPosition(((Drawable) dr).getBounds(), true);
		}

	}

	protected boolean isDynamicStylebar(){
		return true;
	}
	
}
