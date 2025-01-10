package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EventType;

/**
 * Geo representing spotlight
 */
public class GeoSpotlight extends GeoConic {
	private static final double SPOTLIGHT_RADIUS = 100;

	/**
	 * Constructor
	 * @param c construction
	 */
	public GeoSpotlight(Construction c) {
		super(c);
		setSphere();
		setVisualStyle();
		setLabel("spotlight");
	}

	private void setSphere() {
		EuclidianView ev = getApp().getActiveEuclidianView();
		int screenHorizontalMiddle = ev.getWidth() / 2;
		int screenVerticalMiddle = ev.getHeight() / 2;
		Coords coords = new Coords(ev.toRealWorldCoordX(screenHorizontalMiddle),
				ev.toRealWorldCoordY(screenVerticalMiddle), 1);
		double rSqr = SPOTLIGHT_RADIUS / ev.getXscale();
		setSphereND(coords, rSqr);
	}

	private void setVisualStyle() {
		setFixed(false);
		setInverseFill(true);
		setObjColor(GColor.BLACK);
		setAlphaValue(0.32);
	}

	@Override
	public boolean isSpotlight() {
		return true;
	}

	@Override
	public void doRemove() {
		super.doRemove();
		getApp().getEventDispatcher().dispatchEvent(EventType.HIDE_SPOTLIGHT, null);
	}
}
