package org.geogebra.web.full.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.dom.style.shared.Unit;

/**
 * Dynamically positioned stylebar
 * 
 * @author Judit
 *
 */
public class DynamicStyleBar extends EuclidianStyleBarW {

	private GPoint oldPos = null;

	/**
	 * @param ev
	 *            parent view
	 */
	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		addStyleName("matDynStyleBar");
		if (app.isWhiteboardActive()) {
			addStyleName("mowDynStyleBar");
		}
		TestHarness.setAttr(this, "dynamicStyleBar");

		app.getSelectionManager()
				.addSelectionListener((geo, addToSelection) -> {
					if (addToSelection) {
						return;
					}
					// If the activeGeoList will be null or empty, this
					// will
					// hide the dynamic stylebar.
					// If we clicked on a locked geo, the activeGeoList
					// will
					// contain it, so in this case the dynamic stylebar
					// will
					// be visible yet.
					updateStyleBar();
				});
		EventUtil.stopPointer(getElement());
		ClickStartHandler.initDefaults(this, false, true);
	}

	private GPoint calculatePosition(GRectangle2D gRectangle2D, boolean isPoint,
			boolean isFunction) {
		int height = this.getOffsetHeight();
		double left, top = -1;
		boolean functionOrLine = isFunction || gRectangle2D == null;
		if (functionOrLine) {
			GPoint mouseLoc = this.getView().getEuclidianController()
					.getMouseLoc();
			if (mouseLoc == null) {
				return null;
			}
			top = mouseLoc.y + 10;
		} else if (!isPoint) {
			top = gRectangle2D.getMinY() - height - 10;
		}

		// if there is no enough place on the top of bounding box, dynamic
		// stylebar will be visible at the bottom of bounding box,
		// stylebar of points will be bottom of point if possible.
		if (top < 0 && gRectangle2D != null) {
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
		if (functionOrLine) {
			left = this.getView().getEuclidianController().getMouseLoc().x + 10;
		} else {
			left = gRectangle2D.getMaxX() - getOffsetWidth();
			left += getContextMenuButtonWidth();

			// do not hide rotation handler
			left = Math.max(left,
					gRectangle2D.getMinX() + gRectangle2D.getWidth() / 2 + 16);
		}

		if (left < 0) {
			left = 0;
		}
		int maxLeft = app.getActiveEuclidianView().getWidth()
				- this.getOffsetWidth();
		if (left > maxLeft) {
			left = maxLeft;
		}

		return new GPoint((int) left, (int) top);
	}

	@Override
	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		// make sure it reflects selected geos
		setMode(EuclidianConstants.MODE_MOVE);
		super.updateStyleBar();

		if (activeGeoList == null || activeGeoList.size() == 0) {
			setVisible(false);
			return;
		}

		this.getElement().getStyle().setTop(-10000, Unit.PX);

		if (app.getMode() == EuclidianConstants.MODE_SELECT) {
			GRectangle selectionRectangle = app.getActiveEuclidianView().getSelectionRectangle();
			if (selectionRectangle != null) {
				setPosition(
						calculatePosition(selectionRectangle, false, false));
				return;
			}
		}

		GPoint newPos = null, nextPos;
		boolean hasVisibleGeo = false;

		for (int i = 0; i < activeGeoList.size(); i++) {
			GeoElement geo = activeGeoList.get(i);
			// it's possible if a non visible geo is in activeGeoList, if we
			// duplicate a geo, which has descendant.
			if (geo.isEuclidianVisible()) {
				hasVisibleGeo = true;
				if (geo instanceof GeoFunction || (geo.isGeoLine()
						&& !geo.isGeoSegment())) {
					if (getView().getHits().contains(geo)) {
						nextPos = calculatePosition(null, false, true);
						oldPos = nextPos;
					} else {
						nextPos = null;
					}
				} else {
					nextPos = fromDrawable(geo);
				}

				if (newPos == null) {
					newPos = nextPos;
				} else if (nextPos != null) {
					newPos.x = Math.max(newPos.x, nextPos.x);
					newPos.y = Math.min(newPos.y, nextPos.y);
				}
			}
		}

		// function selected, but dyn stylebar hit
		// do not calculate the new position of stylebar
		// set the current position instead
		if (hasVisibleGeo && newPos == null && oldPos != null) {
			newPos = oldPos;
		}

		setPosition(newPos);
	}

	private GPoint fromDrawable(GeoElement geo) {
		DrawableND dr = ev.getDrawableND(geo);
		if (dr != null && (!(geo instanceof AbsoluteScreenLocateable
				&& ((AbsoluteScreenLocateable) geo).isFurniture())
				|| geo instanceof GeoEmbed)) {
			return calculatePosition(dr.getBoundsForStylebarPosition(),
					dr instanceof DrawPoint && activeGeoList.size() < 2, false);
		}
		return null;
	}

	/**
	 * Sets the position of dynamic style bar. for newPos
	 */
	private void setPosition(GPoint newPos) {
		if (newPos == null) {
			return;
		}
		this.getElement().getStyle().setLeft(newPos.x, Unit.PX);
		this.getElement().getStyle().setTop(newPos.y, Unit.PX);
	}

	@Override
	protected boolean isDynamicStylebar() {
		return true;
	}

	@Override
	public void setVisible(boolean v) {
		// Close label popup if opened when dynamic stylebar visiblity changed
		if (isVisible()) {
			closeLabelPopup();
		}
		super.setVisible(v);
	}

	@Override
	protected boolean hasVisibleGeos(ArrayList<GeoElement> geoList) {
		if (ev.checkHitForStylebar()) {
			for (GeoElement geo : geoList) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible()
						&& !geo.isAxis()
						&& ev.getHits().contains(geo)) {
					return true;
				}
			}
			return false;
		}
		return super.hasVisibleGeos(geoList);
	}
}
