package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GeoInlineTable;

public class DrawInlineTable extends Drawable implements DrawInline {

	private final GeoInlineTable table;
	private InlineTableController tableController;

	private final TransformableRectangle rectangle;

	/**
	 * @param view view
	 * @param table editable table
	 */
	public DrawInlineTable(EuclidianView view, GeoInlineTable table) {
		super(view, table);
		rectangle = new TransformableRectangle(view, table, false);
		this.table = table;
		update();
	}

	@Override
	public void update() {
		table.zoomIfNeeded();
		rectangle.updateSelfAndBoundingBox();
		if (tableController == null && table.getLocation() != null) {
			// make sure we don't initialize the controller during paste XML parsing
			// to avoid inconsistent state
			tableController = view.getApplication().createTableController(view, table);
		}

		if (tableController != null) {
			double contentWidth = table.getContentWidth();
			double contentHeight = table.getContentHeight();
			tableController.update();
			tableController.setTransform(table.getAngle(),
					table.getWidth() / contentWidth, table.getHeight() / contentHeight);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (geo.isEuclidianVisible() && tableController != null
			&& rectangle.getDirectTransform() != null) {
			GAffineTransform tr =
					rectangle.scaleForZoom(table.getContentWidth(), table.getContentHeight());
			tableController.draw(g2, tr);
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return rectangle.hit(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
	}

	@Override
	public MediaBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		if (tableController != null) {
			GPoint2D p = rectangle.getInversePoint(x, y);
			return tableController.urlByCoordinate((int) p.getX(), (int) p.getY());
		}

		return "";
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D point, EuclidianBoundingBoxHandler handler) {
		rectangle.updateByBoundingBoxResize(point, handler);
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		rectangle.fromPoints(points);
	}

	@Override
	protected List<GPoint2D> toPoints() {
		return rectangle.toPoints();
	}

	@Override
	public void remove() {
		tableController.removeFromDom();
	}

	public boolean isInEditMode() {
		return tableController != null && tableController.isInEditMode();
	}

	@Override
	public void updateContent() {
		if (tableController != null) {
			tableController.updateContent();
		}
	}

	@Override
	public void saveContent() {
		if (tableController != null) {
			tableController.saveContent();
		}
	}

	@Override
	public GAffineTransform getTransform() {
		return rectangle.getDirectTransform();
	}

	@Override
	public void toBackground() {
		if (tableController != null) {
			tableController.toBackground();
		}
	}

	@Override
	public void toForeground(int x, int y) {
		if (tableController != null) {
			GPoint2D p = rectangle.getInversePoint(x, y);
			tableController.toForeground((int) p.getX(), (int) p.getY());
		}
	}

	@Override
	public InlineTableController getController() {
		return tableController;
	}

	/**
	 * For tests
	 * @param tableController mock controller
	 */
	public void setTextController(InlineTableController tableController) {
		this.tableController = tableController;
	}

	/**
	 * Set hit cell in the Carota editor
	 * @param mouseLoc mouse location
	 */
	public void setHitCellFromMouse(GPoint mouseLoc) {
		if (tableController != null) {
			GPoint2D p = rectangle.getInversePoint(mouseLoc.x, mouseLoc.y);
			tableController.setHitCell(p.x, p.y);
		}
	}
}
