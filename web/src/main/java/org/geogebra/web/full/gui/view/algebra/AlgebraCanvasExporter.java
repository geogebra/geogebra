package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.TreeItem;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.base.Js;

public class AlgebraCanvasExporter {

	private final AppW app;
	private final AlgebraViewW algebraView;
	private final GGraphics2DW graphics;
	private final int offsetWidth;
	private static final int RECTANGLE_WIDTH = 58;
	private static final int DESCRIPTION_PADDING_X = 10;
	private static final int MARBLE_PADDING_X = 19;
	private static final int MARBLE_SIZE = 18;

	/**
	 * @param algebraView AlgebraViewW
	 * @param context2d Context
	 * @param offsetWidth Parent panel width
	 */
	public AlgebraCanvasExporter(AlgebraViewW algebraView, CanvasRenderingContext2D context2d,
			int offsetWidth) {
		this.app = algebraView.getApp();
		this.algebraView = algebraView;
		this.graphics = new GGraphics2DW(context2d);
		this.offsetWidth = offsetWidth;
	}

	/**
	 * @param left Horizontal offset
	 * @param top Vertical offset
	 */
	public void paintToCanvas(int left, int top) {
		drawLines(left, top);
		drawMarbles(left, top);
		drawAlgebraDescriptions(left, top);
	}

	/**
	 * Draws the vertical and horizontal lines
	 * @param left Horizontal offset
	 * @param top Vertical offset
	 */
	private void drawLines(int left, int top) {
		graphics.setColor(GColor.LIGHT_GRAY);
		graphics.setStrokeLineWidth(0.5);

		graphics.drawLine(RECTANGLE_WIDTH + left, top,
				RECTANGLE_WIDTH + left, getVerticalLineLength());

		for (int i = 0; i < algebraView.getItemCount(); i++) {
			graphics.drawLine(left, top + getYCoordinateForItem(i),
					offsetWidth, top + getYCoordinateForItem(i));
		}
	}

	/**
	 * Draws the outlines of all visible marbles and consequently fills them if needed
	 * @param left Horizontal offset
	 * @param top Vertical offset
	 */
	private void drawMarbles(int left, int top) {
		GeoElement geo;
		TreeItem item;

		for (int i = 0; i < algebraView.getItemCount() - 1; i++) {
			item = algebraView.getItem(i);
			geo = getGeoElement(item);

			if (geo != null && !item.getHTML().contains("marbleHidden")) {
				drawMarbleOutline(left, top, geo, item, i);
				if (geo.isEuclidianVisible()) {
					fillMarble(left, top, geo, item, i);
				}
			}
		}
	}

	/**
	 * Draws the outline of a marble
	 * @param left Horizontal offset
	 * @param top Vertical offset
	 * @param geo GeoElement
	 * @param item TreeItem
	 * @param index Index of the TreeItem
	 */
	private void drawMarbleOutline(int left, int top, GeoElement geo, TreeItem item, int index) {
		graphics.setStrokeLineWidth(1);
		graphics.setColor(geo.getAlgebraColor());
		graphics.drawRoundRect(left + MARBLE_PADDING_X,
				top + getYCoordinateForItem(index) + getMarblePaddingY(item),
				MARBLE_SIZE, MARBLE_SIZE, MARBLE_SIZE, MARBLE_SIZE);
	}

	/**
	 * Fills a marble
	 * @param left Horizontal offset
	 * @param top Vertical offset
	 * @param geo GeoElement
	 * @param item TreeItem
	 * @param index Index of the TreeItem
	 */
	private void fillMarble(int left, int top, GeoElement geo, TreeItem item, int index) {
		graphics.setColor(geo.getAlgebraColor().deriveWithAlpha(102));
		graphics.fillRoundRect(left + MARBLE_PADDING_X,
				top + getYCoordinateForItem(index) + getMarblePaddingY(item),
				MARBLE_SIZE, MARBLE_SIZE, MARBLE_SIZE, MARBLE_SIZE);
	}

	/**
	 * Draws the algebra descriptions
	 * @param left Horizontal offset
	 * @param top Vertical offset
	 */
	private void drawAlgebraDescriptions(int left, int top) {
		int fontSize = app.getFontSize();
		GFont font = app.getFontCommon(false, GFont.PLAIN, fontSize);
		graphics.setFont(font);
		graphics.setColor(GColor.BLACK);

		GeoElement geo;
		TreeItem item;
		for (int i = 0; i < algebraView.getItemCount() - 1; i++) {
			item = algebraView.getItem(i);
			geo = getGeoElement(item);
			if (geo != null) {
				drawAlgebraDescription(i, geo, left + RECTANGLE_WIDTH + DESCRIPTION_PADDING_X,
						top + getYCoordinateForItem(i)
								+ item.getOffsetHeight() / 2.0 + fontSize / 3.0);
			}
		}
	}

	private void drawAlgebraDescription(int index, GeoElement geo, double x, double y) {
		TreeItem item = algebraView.getItem(index);
		if (item instanceof RadioTreeItem) {
			Widget widget = ((RadioTreeItem) item).getContent().getWidget(0);
			if (widget instanceof Canvas) {
				drawSingleWidget(widget, item, x, y);
				return;
			} else if (isDrawableFraction(geo, item)) {
				drawFraction(item, geo, x, y);
				return;
			}
		}

		if (AlgebraItem.getDescriptionModeForGeo(geo, app.getSettings().getAlgebra().getStyle())
				== DescriptionMode.DEFINITION_VALUE) {
			drawDefinitionAndValue(geo, item, x, y);
			return;
		}
		graphics.drawString(geo.getAlgebraDescriptionDefault(), x, y);
	}

	/**
	 * @return The length for the vertical line drawn
	 */
	private int getVerticalLineLength() {
		return getYCoordinateForItem(algebraView.getItemCount() - 1);
	}

	/**
	 * @param index Index of the element in the list of (Radio)TreeItems
	 * @return The y-coordinate of the (row) element with given index
	 */
	private int getYCoordinateForItem(int index) {
		int ycoord = 0;
		for (int i = 0; i < index; i++) {
			ycoord += algebraView.getItem(i).getOffsetHeight();
		}
		return ycoord;
	}

	/**
	 * @param item TreeItem
	 * @return Vertical padding used for drawing the marbles
	 */
	private int getMarblePaddingY(TreeItem item) {
		return (item.getOffsetHeight() - MARBLE_SIZE) / 2;
	}

	/**
	 * @param item TreeItem
	 * @return The GeoElement held by the RadioTreeItem, null if the TreeItem is not of type
	 * RadioTreeItem
	 */
	private GeoElement getGeoElement(TreeItem item) {
		if (item instanceof RadioTreeItem) {
			return ((RadioTreeItem) item).getGeo();
		}
		return null;
	}

	private void drawSingleWidget(Widget widget, TreeItem item, double x, double y) {
		HTMLCanvasElement formula = Js.uncheckedCast(widget.getElement());
		double formulaHeight = StringUtil.parseDouble(formula.getAttribute("height"));

		graphics.getContext().drawImage(formula, x - DESCRIPTION_PADDING_X / 2.0,
				y - item.getOffsetHeight() / 2.0 + formulaHeight * 0.1,
				StringUtil.parseDouble(formula.getAttribute("width")),
				formulaHeight * 0.9);
	}

	/**
	 * @param geo GeoElement
	 * @param item TreeItem
	 * @return True if geo is a fraction that and can be drawn to canvas
	 */
	private boolean isDrawableFraction(GeoElement geo, TreeItem item) {
		return geo.getDefinition() != null
				&& geo.getDefinition().getOperation() == Operation.DIVIDE
				&& ((RadioTreeItem) item).getCanvas() != null;
	}

	private void drawFraction(TreeItem item, GeoElement geo, double x, double y) {
		HTMLCanvasElement formula =
				Js.uncheckedCast(((RadioTreeItem) item).getCanvas().getElement());
		double formulaHeight = StringUtil.parseDouble(formula.getAttribute("height"));

		graphics.getContext().drawImage(formula, x - DESCRIPTION_PADDING_X / 2.0,
				y - item.getOffsetHeight() / 2.0 + formulaHeight * 0.1,
				StringUtil.parseDouble(formula.getAttribute("width")),
				formulaHeight * 0.9);
		graphics.drawString("= " + geo.evaluateDouble(), x,
				y + item.getOffsetHeight() / 4.0);
	}

	private void drawDefinitionAndValue(GeoElement geo, TreeItem item, double x, double y) {
		graphics.drawString(geo.getNameAndDefinition(StringTemplate.algebraTemplate),
				x, y - item.getOffsetHeight() / 4.0);
		graphics.drawString("= " + geo.toOutputValueString(StringTemplate.algebraTemplate),
				x, y + item.getOffsetHeight() / 4.0);
	}
}