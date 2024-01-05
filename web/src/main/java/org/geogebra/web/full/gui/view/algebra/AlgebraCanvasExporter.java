package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
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
	private final ArrayList<GeoElement> elements;
	private final int offsetWidth;
	private static final int RECTANGLE_WIDTH = 58;
	private static final int RECTANGLE_HEIGHT = 48;
	private static final int DESCRIPTION_PADDING_X = 10;
	private static final int MARBLE_PADDING_X = 19;
	private static final int MARBLE_PADDING_Y = 15;
	private static final int MARBLE_SIZE = 18;

	/**
	 * @param app AppW
	 * @param algebraView AlgebraViewW
	 * @param context2d Context
	 * @param offsetWidth Parent panel width
	 */
	public AlgebraCanvasExporter(AppW app, AlgebraViewW algebraView, CanvasRenderingContext2D context2d, int offsetWidth) {
		this.app = app;
		this.algebraView = algebraView;
		this.graphics = new GGraphics2DW(context2d);
		this.offsetWidth = offsetWidth;
		this.elements =
				app.getKernel().getConstruction().getGeoSetConstructionOrder().stream()
						.filter(GeoElement::isAlgebraShowable)
						.collect(Collectors.toCollection(ArrayList::new));
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

	private void drawLines(int left, int top) {
		graphics.setColor(GColor.LIGHT_GRAY);
		graphics.setStrokeLineWidth(0.5);
		graphics.drawLine(RECTANGLE_WIDTH + left, top, RECTANGLE_WIDTH + left,
				elements.size() * RECTANGLE_HEIGHT);
		for (int i = 1; i <= elements.size(); i++) {
			graphics.drawLine(left, top + i * RECTANGLE_HEIGHT,
					offsetWidth, top + i * RECTANGLE_HEIGHT);
		}
	}

	private void drawMarbles(int left, int top) {
		drawMarbleOutlines(left, top);
		fillMarbles(left, top);
	}

	private void drawMarbleOutlines(int left, int top) {
		graphics.setStrokeLineWidth(1);
		for (int i = 0; i < elements.size(); i++) {
			graphics.setColor(elements.get(i).getAlgebraColor());
			graphics.drawRoundRect(left + MARBLE_PADDING_X,
					top + RECTANGLE_HEIGHT * i + MARBLE_PADDING_Y, MARBLE_SIZE, MARBLE_SIZE,
					MARBLE_SIZE, MARBLE_SIZE);
		}
	}

	private void fillMarbles(int left, int top) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).isEuclidianVisible()) {
				graphics.setColor(elements.get(i).getAlgebraColor().deriveWithAlpha(102));
				graphics.fillRoundRect(left + MARBLE_PADDING_X,
						top + RECTANGLE_HEIGHT * i + MARBLE_PADDING_Y, MARBLE_SIZE, MARBLE_SIZE,
						MARBLE_SIZE, MARBLE_SIZE);
			}
		}
	}

	private void drawAlgebraDescriptions(int left, int top) {
		int fontSize = app.getFontSize();
		GFont font = app.getFontCommon(false, GFont.PLAIN, fontSize);
		graphics.setFont(font);
		graphics.setColor(GColor.BLACK);
		for (int i = 0; i < elements.size(); i++) {
			drawAlgebraDescription(i, elements.get(i),
					left + RECTANGLE_WIDTH + DESCRIPTION_PADDING_X,
					top + RECTANGLE_HEIGHT * i + RECTANGLE_HEIGHT / 2.0 + fontSize / 3.0);
		}
	}

	private void drawAlgebraDescription(int index, GeoElement geo, double x, double y) {
		TreeItem item = algebraView.getItem(index);
		if (item instanceof RadioTreeItem) {
			Widget widget = ((RadioTreeItem) item).getContent().getWidget(0);
			if (widget instanceof Canvas) {
				HTMLCanvasElement formula = Js.uncheckedCast(widget.getElement());
				graphics.getContext().drawImage(formula, x - DESCRIPTION_PADDING_X,
						y - RECTANGLE_HEIGHT / 2.0);
				return;
			}

//			if (geo.getDescriptionMode() == DescriptionMode.DEFINITION_VALUE) {
//				graphics.drawString(geo.getLabel(StringTemplate.algebraTemplate) + " = "
//						+ geo.getDefinition(StringTemplate.algebraTemplate)
//						+ geo.toOutputValueString(StringTemplate.algebraTemplate), x, y);
//				return;
//			}
			graphics.drawString(geo.getAlgebraDescriptionDefault(), x, y);
		}
	}
}
