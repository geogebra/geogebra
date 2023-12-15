package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;

import elemental2.dom.CanvasRenderingContext2D;

public class AlgebraCanvasExporter {

	private final AppW app;
	private final GGraphics2DW graphics;
	private final ArrayList<GeoElement> elements;
	private static final int RECTANGLE_WIDTH = 58;
	private static final int RECTANGLE_HEIGHT = 48;
	private static final int DESCRIPTION_PADDING_X = 10;
	private static final int MARBLE_PADDING_X = 19;

	private static final int MARBLE_SIZE = 18;
	private static final GColor marbleColor = GColor.newColor(77, 77, 255, 255);

	public AlgebraCanvasExporter(AppW app, CanvasRenderingContext2D context2d) {
		this.app = app;
		this.graphics = new GGraphics2DW(context2d);
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

	private void drawAlgebraDescriptions(int left, int top) {
		int fontSize = app.getFontSize();
		GFont font = app.getFontCommon(false, GFont.PLAIN, fontSize);
		graphics.setFont(font);
		graphics.setColor(GColor.BLACK);
		for (int i = 0; i < elements.size(); i++) {
			graphics.drawString(elements.get(i).getAlgebraDescriptionDefault(),
					left + RECTANGLE_WIDTH + DESCRIPTION_PADDING_X,
					top + RECTANGLE_HEIGHT * i + RECTANGLE_HEIGHT / 2.0 + fontSize / 2.0);
		}
	}

	private void drawLines(int left, int top) {
		graphics.setColor(GColor.LIGHT_GRAY);
		graphics.setStrokeLineWidth(0.5);
		graphics.drawLine(RECTANGLE_WIDTH + left, top, RECTANGLE_WIDTH + left,
				elements.size() * RECTANGLE_HEIGHT);
		for (int i = 1; i <= elements.size(); i++) {
			graphics.drawLine(left, top + i * RECTANGLE_HEIGHT, 500, top + i * RECTANGLE_HEIGHT);
		}
	}

	private void drawMarbles(int left, int top) {
		drawMarbleOutlines(left, top);
		fillMarbles(left, top);
	}

	//TODO Alignments (draw + fill)
	private void drawMarbleOutlines(int left, int top) {
		graphics.setColor(marbleColor);
		for (int i = 0; i < elements.size(); i++) {
			graphics.drawRoundRect(left + MARBLE_PADDING_X,
					top + RECTANGLE_HEIGHT * i + RECTANGLE_HEIGHT / 2, MARBLE_SIZE, MARBLE_SIZE, 20, 20);
		}
	}

	private void fillMarbles(int left, int top) {
		graphics.setColor(marbleColor.deriveWithAlpha(102));
		for (int i = 0; i < elements.size(); i++) {
			graphics.fillRoundRect(left + MARBLE_PADDING_X,
					top + RECTANGLE_HEIGHT * i + RECTANGLE_HEIGHT / 2, MARBLE_SIZE, MARBLE_SIZE, 20, 20);
		}
	}
}
