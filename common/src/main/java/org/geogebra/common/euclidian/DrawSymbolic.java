package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Drawable for symbolic geos
 */
public class DrawSymbolic extends Drawable {

	private GeoSymbolic symbolic;
	private DrawableND twinDrawable;

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            symbolic geo
	 */
	public DrawSymbolic(EuclidianView ev, GeoSymbolic geo) {
		this.view = ev;
		this.geo = geo;
		this.symbolic = geo;
		update();
	}

	@Override
	public void update() {
		GeoElementND twinGeo = symbolic.getTwinGeo();
		if (twinGeo == null) {
			twinDrawable = null;
		} else if (twinDrawable != null
				&& twinDrawable.getGeoElement() == twinGeo) {
			twinDrawable.update();
		} else {
			if (twinDrawable instanceof RemoveNeeded) {
				((RemoveNeeded) twinDrawable).remove();
			}
			twinDrawable = view.newDrawable(symbolic.getTwinGeo());
			twinGeo.setVisualStyle(symbolic);
			twinGeo.setEuclidianVisible(symbolic.isEuclidianVisible());
			if (twinDrawable instanceof Drawable) {
				((Drawable) twinDrawable).setTopLevelGeo(symbolic);
				twinDrawable.update();
			}
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (twinDrawable != null && geo.isEuclidianVisible()) {
			((Drawable) twinDrawable).draw(g2);
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (twinDrawable != null) {
			return ((Drawable) twinDrawable).hit(x, y, hitThreshold);
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		if (twinDrawable != null) {
			return ((Drawable) twinDrawable).isInside(rect);
		}
		return false;
	}

	@Override
	public GRectangle getBounds() {
		if (twinDrawable instanceof Drawable) {
			return ((Drawable) twinDrawable).getBounds();
		}
		return null;
	}

	@Override
	public void updateIfNeeded() {
		if (twinDrawable instanceof Drawable) {
			((Drawable) twinDrawable).updateIfNeeded();
		}
		super.updateIfNeeded();
	}

	@Override
	public void setNeedsUpdate(boolean b) {
		super.setNeedsUpdate(b);
		if (twinDrawable != null) {
			twinDrawable.setNeedsUpdate(b);
		}
	}
}
