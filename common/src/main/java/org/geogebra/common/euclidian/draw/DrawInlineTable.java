package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.TableController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;

public class DrawInlineTable extends Drawable implements RemoveNeeded, HasFormat {

	private final TableController tableController;

	/**
	 * @param view view
	 * @param table editable table
	 */
	public DrawInlineTable(EuclidianView view, GeoInlineTable table) {
		super(view, table);
		update();
		tableController = view.getApplication().createTableController(view, table);
	}

	@Override
	public void update() {
		// TODO
	}

	@Override
	public void draw(GGraphics2D g2) {
		// TODO
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return false;
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void remove() {
		tableController.removeFromDom();
	}

	@Override
	public void format(String key, Object val) {
		tableController.format(key, val);
	}
}
