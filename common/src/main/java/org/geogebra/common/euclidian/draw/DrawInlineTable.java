package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.TableController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;

public class DrawInlineTable extends Drawable implements DrawInline {

	private final TableController tableController;
	private GeoInlineTable table;

	/**
	 * @param view view
	 * @param table editable table
	 */
	public DrawInlineTable(EuclidianView view, GeoInlineTable table) {
		super(view, table);
		this.table = table;
		tableController = view.getApplication().createTableController(view, table);
		update();
	}

	@Override
	public void update() {
		GPoint2D point = table.getLocation();
		if (tableController != null && point != null) {
			double angle = table.getAngle();
			double width = table.getWidth();
			double height = table.getHeight();

			tableController.setLocation(view.toScreenCoordX(point.getX()),
					view.toScreenCoordY(point.getY()));
			tableController.setHeight((int) (height));
			tableController.setWidth((int) (width));
			tableController.setAngle(angle);
		}
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

	/**
	 * @param key
	 *            formatting option
	 * @param val
	 *            value (String, int or bool, depending on key)
	 */
	public void format(String key, Object val) {
		tableController.format(key, val);
	}

	@Override
	public void updateContent() {

	}

	@Override
	public void toForeground(int x, int y) {

	}

	@Override
	public void toBackground() {

	}
}
