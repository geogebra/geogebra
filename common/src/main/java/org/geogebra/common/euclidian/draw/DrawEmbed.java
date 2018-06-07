package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;

/**
 * Drawable for embedded apps
 */
public class DrawEmbed extends Drawable {

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            embedded applet
	 */
	public DrawEmbed(EuclidianView ev, GeoEmbed geo) {
		this.view = ev;
		this.geo = geo;
		if (getEmbedManager() != null) {
			getEmbedManager().add(this);
		}
		update();
	}

	private EmbedManager getEmbedManager() {
		return view.getApplication().getEmbedManager();
	}

	@Override
	public void update() {
		if (getEmbedManager() != null) {
			getEmbedManager().update(this);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getTop() {
		return getView().toScreenCoordYd(((GeoEmbed) geo).getCorner(2).getInhomY());
	}

	public double getLeft() {
		return getView().toScreenCoordYd(((GeoEmbed) geo).getCorner(0).getInhomX());
	}

	public double getRight() {
		return getView().toScreenCoordYd(((GeoEmbed) geo).getCorner(1).getInhomX());
	}

	public double getBottom() {
		return getView().toScreenCoordYd(((GeoEmbed) geo).getCorner(0).getInhomY());
	}

}
