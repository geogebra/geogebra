package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.draw.CanvasDrawable;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Draws axes in 2D
 */
public class DrawAxis {
	private static final int CURRENCY_DOLLAR_BATCH = 1000;
	private static final int CURRENCY_DOLLAR_MOD = 3;
	/** view */
	EuclidianView view;
	private GGeneralPath gp;

	// used for deciding if there is a number to close for "0" on the x axis
	private Integer beforeZeroX;
	// used for deciding if there is a number to close for "0" on the y axis
	private Integer beforeZeroY;
	private boolean firstCallX = true;
	private boolean firstCallY = true;

	/**
	 * @param euclidianView
	 *            view
	 */
	public DrawAxis(EuclidianView euclidianView) {
		this.view = euclidianView;
	}

	/**
	 * @param g2
	 *            graphics
	 */
	protected void drawAxes(GGraphics2D g2) {
		// TRAC-5292
		// problem exporting to PDF
		// GGB-766
		// also needed for Braille
		char minusSign = view.getApplication().getExportType()
				.getAxisMinusSign();

		// xCrossPix: yAxis crosses the xAxis at this x pixel
		double xCrossPix = view.getXAxisCrossingPixel();

		// yCrossPix: xAxis crosses the yAxis at this y pixel
		double yCrossPix = view.getYAxisCrossingPixel();

		// yAxis end value (for drawing half-axis)
		int yAxisEnd = view.positiveAxes[1] ? (int) yCrossPix
				: view.getHeight();

		// xAxis start value (for drawing half-axis)
		int xAxisStart = view.positiveAxes[0] ? (int) xCrossPix : 0;

		// for axes ticks

		boolean bold = view.areAxesBold();
		boolean filled = (view.axesLineType
				& EuclidianStyleConstants.AXES_FILL_ARROWS) != 0;

		if (filled && gp == null) {
			gp = AwtFactory.getPrototype().newGeneralPath();
		}

		boolean drawRightArrow = ((view.axesLineType
				& EuclidianStyleConstants.AXES_RIGHT_ARROW) != 0)
				&& !(view.positiveAxes[0]
						&& (view.getXmax() < view.axisCross[1]));
		boolean drawTopArrow = ((view.axesLineType
				& EuclidianStyleConstants.AXES_RIGHT_ARROW) != 0)
				&& !(view.positiveAxes[1]
						&& (view.getYmax() < view.axisCross[0]));

		boolean drawLeftArrow = ((view.axesLineType
				& EuclidianStyleConstants.AXES_LEFT_ARROW) != 0)
				&& !(view.positiveAxes[0]);
		boolean drawBottomArrow = ((view.axesLineType
				& EuclidianStyleConstants.AXES_LEFT_ARROW) != 0)
				&& !(view.positiveAxes[1]);

		// AXES_TICK_STYLE_MAJOR_MINOR = 0;
		// AXES_TICK_STYLE_MAJOR = 1;
		// AXES_TICK_STYLE_NONE = 2;

		g2.setFont(view.getFontAxes());
		int fontsize = view.getFontAxes().getSize();
		int arrowSize = fontsize / 3;
		g2.setPaint(view.axesColor);
		GFontRenderContext frc = g2.getFontRenderContext();

		if (bold) {
			view.axesStroke = EuclidianView.boldAxesStroke;
			view.tickStroke = EuclidianView.boldAxesStroke;

			arrowSize += 1;
		} else {
			view.axesStroke = EuclidianView.defAxesStroke;
			view.tickStroke = EuclidianView.defAxesStroke;
		}

		// make sure arrows don't go off screen (eg EMF export)
		double arrowAdjustx = drawRightArrow ? view.axesStroke.getLineWidth()
				: 0;

		// Draw just y-axis first (in case any labels need to be drawn over it)
		if (view.yAxisOnscreen()) {

			predrawYAxis(g2, xCrossPix, arrowSize, filled, drawTopArrow,
					drawBottomArrow, yAxisEnd);
			// erase grid to make space for labels

		}

		beforeZeroY = null;
		beforeZeroX = null;

		// ========================================
		// X-AXIS
		if (view.showAxes[0]) {
			// erase the grid to make space for labels, use two rectangles

			// label of x axis
			if (view.axesLabels[0] != null) {
				drawAxisLabelX(g2, (int) (yCrossPix - 4), frc);
			}

			// numbers

			// x-Axis itself
			g2.setStroke(view.axesStroke);
			g2.drawStraightLine(xAxisStart + (drawLeftArrow ? 2 : 0), yCrossPix,
					view.getWidth() - arrowAdjustx - 1, yCrossPix);

			if (drawRightArrow) {

				if (filled) {

					gp.reset();
					gp.moveTo((view.getWidth() - arrowAdjustx), yCrossPix);
					gp.lineTo(
							(view.getWidth() - arrowAdjustx
									- arrowSize * 4),
							(yCrossPix - arrowSize));
					gp.lineTo(
							(view.getWidth() - arrowAdjustx
									- arrowSize * 4),
							(yCrossPix + arrowSize));

					g2.fill(gp);

				} else {

					// draw right arrow for x-axis
					g2.drawStraightLine(view.getWidth() - arrowAdjustx,
							yCrossPix,
							view.getWidth() - arrowAdjustx - arrowSize,
							yCrossPix - arrowSize);

					g2.drawStraightLine(view.getWidth() - arrowAdjustx,
							yCrossPix,
							view.getWidth() - arrowAdjustx - arrowSize,
							yCrossPix + arrowSize);

				}
			}

			if (drawLeftArrow) {

				if (filled) {

					gp.reset();
					gp.moveTo((arrowAdjustx), yCrossPix);
					gp.lineTo((arrowAdjustx + arrowSize * 4),
							(yCrossPix - arrowSize));
					gp.lineTo((arrowAdjustx + arrowSize * 4),
							(yCrossPix + arrowSize));

					g2.fill(gp);

				} else {

					// draw left arrow for x-axis
					g2.drawStraightLine(arrowAdjustx, yCrossPix,
							arrowAdjustx + arrowSize, yCrossPix - arrowSize);
					g2.drawStraightLine(arrowAdjustx, yCrossPix,
							arrowAdjustx + arrowSize, yCrossPix + arrowSize);
				}
			}

			view.axesNumberingDistances[0] = DoubleUtil
					.checkDecimalFraction(view.axesNumberingDistances[0]);

			if (view.logAxes[0]) {
				drawXTicksLog(g2, yCrossPix, minusSign, drawRightArrow,
						fontsize, xAxisStart);
			} else {
				drawXTicksLinear(g2, yCrossPix, minusSign, drawRightArrow,
						fontsize, xAxisStart);
			}

		}

		// ========================================
		// Y-AXIS

		if (view.showAxes[1]) {

			// label of y axis
			if (view.axesLabels[1] != null) {
				drawAxisLabelY(g2, (int) (xCrossPix + 5), frc);
			}

			if (view.logAxes[1]) {
				drawYticksLog(g2, xCrossPix, fontsize, minusSign, drawTopArrow,
						yCrossPix, yAxisEnd);
			} else {
				drawYticksLinear(g2, xCrossPix, fontsize, minusSign,
						drawTopArrow, yCrossPix, yAxisEnd);
			}
		}

		if (!view.logAxes[0] || !view.logAxes[1]) {
			if (view.axisCross[0] == 0 && view.axisCross[1] == 0) {
				drawZero(g2, xCrossPix, yCrossPix, fontsize);
			}
		}

	}

	private void drawAxisLabelY(GGraphics2D g2, int x, GFontRenderContext frc) {
		GFont old = g2.getFont();
		GFont font = view.getFontLine().deriveFont(view.axesLabelsStyle[1]);
		GTextLayout layout = AwtFactory.getPrototype()
				.newTextLayout(view.axesLabels[1], font, frc);
		if (CanvasDrawable.isLatexString(view.axesLabels[1])) {
			GeoElement geo = view.getApplication().getKernel().getXAxis();
			// GDimension dim = view.getApplication().getDrawEquation()
			// .measureEquation(view.getApplication(), geo,
			// view.axesLabels[0], font, false);
			view.getApplication().getDrawEquation().drawEquation(
					view.getApplication(), geo, g2,
					x - 2, 10,
					view.axesLabels[1], font,
					StringUtil.startsWithFormattingCommand(view.axesLabels[1]),
					GColor.BLACK, null, true, false,
					view.getCallBack(geo, firstCallY));

			firstCallY = false;
		} else if (!view.axesLabels[1].contains("_")) {
			layout.draw(g2, x,
					(int) (5 + layout.getAscent()));
		} else {
			g2.setFont(font);
			EuclidianStatic.drawIndexedString(view.getApplication(), g2,
					view.axesLabels[1], x,
					(int) (5 + layout.getAscent()), false, view,
					view.axesColor);
		}
		g2.setFont(old);
	}

	private void drawAxisLabelX(GGraphics2D g2, int y, GFontRenderContext frc) {
		GFont old = g2.getFont();
		GFont font = view.getFontLine().deriveFont(view.axesLabelsStyle[0]);
		GTextLayout layout = AwtFactory.getPrototype()
				.newTextLayout(view.axesLabels[0], font, frc);
		if (CanvasDrawable.isLatexString(view.axesLabels[0])) {
			GeoElement geo = view.getApplication().getKernel().getXAxis();
			GDimension dim = view.getApplication().getDrawEquation()
					.measureEquation(
					view.getApplication(), geo,
					view.axesLabels[0], font, false);

			view.getApplication().getDrawEquation().drawEquation(
					view.getApplication(), geo, g2,
					view.getWidth() - 5 - dim.getWidth(),
					y + 4 - dim.getHeight(),
					view.axesLabels[0], font,
					StringUtil.startsWithFormattingCommand(view.axesLabels[0]),
					GColor.BLACK, null, true,
					false, view.getCallBack(geo, firstCallX));

			firstCallX = false;
		} else if (!view.axesLabels[0].contains("_")) {
			layout.draw(g2, (int) (view.getWidth() - 10 - layout.getAdvance()),
					y);
		} else {
			g2.setFont(font);
			EuclidianStatic.drawIndexedString(view.getApplication(), g2,
					view.axesLabels[0],
					view.getWidth() - 10 - layout.getAdvance(),
					y, false, view, view.axesColor);
		}
		g2.setFont(old);
	}

	private void predrawYAxis(GGraphics2D g2, double xCrossPix,
			double arrowSize, boolean filled, boolean drawTopArrow,
			boolean drawBottomArrow, double yAxisEnd) {
		double arrowAdjusty = drawTopArrow ? view.axesStroke.getLineWidth() : 0;
		// y-Axis itself
		g2.setStroke(view.axesStroke);
		g2.drawStraightLine(xCrossPix, arrowAdjusty + (drawTopArrow ? 1 : -1),
				xCrossPix, yAxisEnd + (drawBottomArrow ? -2 : 0));

		if (drawTopArrow) {

			if (filled) {

				gp.reset();
				gp.moveTo(xCrossPix, arrowAdjusty);
				gp.lineTo((xCrossPix - arrowSize),
						(arrowAdjusty + 4 * arrowSize));
				gp.lineTo((xCrossPix + arrowSize),
						(arrowAdjusty + 4 * arrowSize));

				g2.fill(gp);

			} else {
				// draw top arrow for y-axis
				g2.drawStraightLine(xCrossPix, arrowAdjusty, xCrossPix
						- arrowSize, arrowAdjusty + arrowSize);
				g2.drawStraightLine(xCrossPix, arrowAdjusty, xCrossPix
						+ arrowSize, arrowAdjusty + arrowSize);

			}
		}

		if (drawBottomArrow) {

			if (filled) {

				gp.reset();
				gp.moveTo(xCrossPix, (view.getHeight() - arrowAdjusty));
				gp.lineTo((xCrossPix - arrowSize), (view.getHeight()
						- arrowAdjusty - 4 * arrowSize));
				gp.lineTo((xCrossPix + arrowSize), (view.getHeight()
						- arrowAdjusty - 4 * arrowSize));

				g2.fill(gp);

			} else {
				// draw bottom arrow for y-axis
				g2.drawStraightLine(xCrossPix, view.getHeight() - arrowAdjusty,
						xCrossPix - arrowSize, view.getHeight() - arrowAdjusty
								- arrowSize);
				g2.drawStraightLine(xCrossPix, view.getHeight() - arrowAdjusty,
						xCrossPix + arrowSize, view.getHeight() - arrowAdjusty
								- arrowSize);
			}
		}

	}

	private void drawYticksLinear(GGraphics2D g2, double xCrossPix,
			int fontsize, char minusSign, boolean drawTopArrow,
			double yCrossPix, double yAxisEnd) {
		double xoffset = -4 - (fontsize / 4d);
		double yoffset = (fontsize / 2d) - 1;
		
		boolean enableTicks = !view.getShowGrid()
				|| (view.axesNumberingDistances[1] != view.getGridDistances()[1]);
		boolean[] drawMajorTicks = {
				view.getAxisTickStyle(0) <= 1 && enableTicks,
				view.getAxisTickStyle(1) <= 1 && enableTicks };
		boolean[] drawMinorTicks = {
				view.getAxisTickStyle(0) == 0 && enableTicks,
				view.getAxisTickStyle(1) == 0 && enableTicks };

		double xSmall1 = xCrossPix - 0;
		double xSmall2 = xCrossPix - 2;
		double xBig = xCrossPix - 3;
		double smallTickOffset = 0;
		double xZeroTick = xCrossPix;
		if (view.areAxesBold()) {
			xSmall2--;
		}
		// numbers
		double rw = view.getYmin()
				- (view.getYmin() % view.axesNumberingDistances[1]);
		long labelno = Math.round(rw / view.axesNumberingDistances[1]);
		// by default we start with minor tick to the left of first major
		// tick, exception is for positive only
		double axesStep = view.getYscale() * (view.axesNumberingDistances[1]); // pixelstep
		if (view.getPositiveAxes()[1]
				&& (DoubleUtil.isGreaterEqual(view.axisCross[0], view.getYmin()))) {
			// start labels at the y-axis instead of screen border
			// be careful: view.axisCross[1] = x value for which the y-axis
			// crosses,
			// so xmin is replaced view.axisCross[1] and not
			// view.axisCross[0]
			rw = MyMath.nextMultiple(view.axisCross[0],
					view.axesNumberingDistances[1]);
			smallTickOffset = axesStep;
			labelno = Math.round(rw / view.axesNumberingDistances[1]);
		}

		double pix = view.getYZero() - (rw * view.getYscale());

		double tickStep = axesStep / 2;

		double maxHeight = EuclidianView
				.estimateNumberHeight(view.getFontAxes());
		int unitsPerLabelY = (int) MyMath.nextPrettyNumber(maxHeight / axesStep,
				1);

		if (pix > (view.getHeight() - EuclidianView.SCREEN_BORDER)) {
			// big tick
			if (drawMajorTicks[1]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(xBig, pix, xZeroTick, pix);
			}
			pix -= axesStep;
			rw += view.axesNumberingDistances[1];
			labelno++;
		}

		// draw all of the remaining ticks and labels

		int maxY = EuclidianView.SCREEN_BORDER;
		
		// yAxisEnd

		String crossAtStr = "" + view.kernel.formatPiE(view.axisCross[0],
				view.axesNumberFormat[1], StringTemplate.defaultTemplate);

		// value of yLabelMaxWidthNeg and view.yLabelMaxWidthPos will be
		// changed at the next iteration, last value will be needed deciding if
		// number should be fitted at the edge. So numbers will be draw after
		// iteration only, and because of this, they will be saved in "numbers"
		// arraylist
		ArrayList<TickNumber> numbers = new ArrayList<>();

		for (; pix >= maxY; rw += view.axesNumberingDistances[1], pix -= axesStep, labelno++) {
			if (pix >= maxY && pix < yAxisEnd + 1) {
				if (view.showAxesNumbers[1]
						// Don't show the biggest number on y-axis if x-axis is
						// too close to the top of EV.
						&& (pix > maxY + fontsize || yCrossPix > 0)
						// Don't show the lowest number on y-axis if x-axis is
						// too close to the bottom of EV.
						&& (pix < view.getHeight() - (view.xLabelHeights + 5)
								|| yCrossPix < view.getHeight()
										- (view.xLabelHeights + 5))) {

					boolean currency = isCurrencyUnit(1);
					String strNum = currency ? tickUnit(view, labelno, 1)
							: tickDescription(view, labelno, 1);

					if ((labelno % unitsPerLabelY) == 0) {

						StringBuilder sb = formatUnitLabel(strNum, 1, minusSign, currency);

						GTextLayout layout = AwtFactory.getPrototype()
								.newTextLayout(sb.toString(),
										view.getFontAxes(),
										g2.getFontRenderContext());

						double width = layout.getAdvance();
						
						int x = (int) ((xCrossPix + xoffset) - width);
						
						// flag for handling label at axis cross point
						boolean zero = strNum.equals(crossAtStr);
						
						int y;

						boolean bothNull = zero && view.axisCross[0] == 0
								&& view.axisCross[1] == 0;

						// if the label is at the axis cross point then draw
						// it 2 pixels above
						if (zero && view.showAxes[0] && !view.positiveAxes[0]) {
							y = (int) (yCrossPix - 2);
						} else {
							y = (int) (pix + yoffset);
						}

						if (!bothNull) {
							numbers.add(new TickNumber(g2, sb.toString(), x, y,
									xCrossPix, xoffset, width));
							
							if (labelno == -unitsPerLabelY) {
								beforeZeroY = y - fontsize;
							}	
						}

						// measure width, so grid line can avoid it
						// use same (max) for all labels
						if (sb.charAt(0) == minusSign
								&& width > view.yLabelMaxWidthNeg) {
							view.yLabelMaxWidthNeg = width;
						} else if (sb.charAt(0) != minusSign
								&& width > view.yLabelMaxWidthPos) {
							view.yLabelMaxWidthPos = width;
						}

					}
				}
				if (drawMajorTicks[1] && (!view.showAxes[0]
						|| !DoubleUtil.isEqual(rw, view.axisCross[0]))) {
					g2.setStroke(view.tickStroke);
					g2.drawStraightLine(xBig, pix, xZeroTick, pix);
				}
			} else if (drawMajorTicks[1] && !drawTopArrow) {
				// draw last tick if there is no arrow
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(xBig, pix, xZeroTick, pix);
			}

			// small tick
			double smallTickPix = (pix + tickStep) - smallTickOffset;
			if (drawMinorTicks[1]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(xSmall1, smallTickPix, xSmall2,
						smallTickPix);
			}
		}

		for (int i = 0; i < numbers.size(); i++) {
			numbers.get(i).draw();
		}
	}

	private StringBuilder formatUnitLabel(String strNum, int idx, char minusSign,
			boolean currency) {
		String unit = view.axesUnitLabels[idx];
		StringBuilder sb = new StringBuilder();
		// "," is treated like a special currency to force thousands separator
		// but isn't printed at the start
		if (currency && !",".equals(unit)) {
			boolean negative = strNum.charAt(0) == '-';
			if (negative) {
				sb.append(minusSign);
			}
			sb.append(unit);
			sb.append(negative ? strNum.substring(1) : strNum);
		} else {
			sb.append(strNum);
		}

		// don't check rw < 0 as it fails for eg
		// -0.0000000001
		if (sb.charAt(0) == '-' && !currency) {
			// change minus sign (too short) to n-dash
			sb.setCharAt(0, minusSign);
		}

		if ((view.axesUnitLabels[idx] != null) && !view.piAxisUnit[idx] && !currency) {
			sb.append(view.axesUnitLabels[idx]);
		}
		return sb;
	}

	/**
	 * 
	 * @param xCrossPix
	 *            x-coord of axis cross (in pixels)
	 * @param xoffset
	 *            offset from screen edge
	 * @param width
	 *            label width
	 * @return if number should be fixed at the left or right edge, it returns
	 *         the x position of number, otherwise returns null
	 */
	Integer getXPositionAtEdge(double xCrossPix, double xoffset, double width) {
		double leftLimit = (view.yLabelMaxWidthNeg > 0 ? view.yLabelMaxWidthNeg
				: view.yLabelMaxWidthPos) + 10;
		if (xCrossPix < leftLimit) {
			return (int) ((leftLimit + xoffset) - width);
		} else if (xCrossPix > view.getWidth()) {
			return (int) (view.getWidth() - width + xoffset);
		}
		return null;
	}

	private void drawZero(GGraphics2D g2, double xCrossPix, double yCrossPix,
			int fontsize) {

		if ((!view.showAxes[0] || !view.showAxesNumbers[0])
				&& (!view.showAxes[1] || !view.showAxesNumbers[1])) {
			return;
		}

		if (view.positiveAxes[0] && !view.positiveAxes[1]
				&& !view.showAxesNumbers[1]
				|| view.positiveAxes[1] && !view.positiveAxes[0]
						&& !view.showAxesNumbers[0]) {
			return;
		}

		GTextLayout layout = AwtFactory.getPrototype().newTextLayout("0",
				view.getFontAxes(), g2.getFontRenderContext());
		double width = layout.getAdvance();
		double xoffset = -4 - (fontsize / 4d);
		double yoffset = view.getYOffsetForXAxis(fontsize);
		double yoffset2 = (fontsize / 2d) - 1;

		Integer x = getXPositionAtEdge(xCrossPix, xoffset, width);
		if (x == null || view.showAxes[0] && view.showAxesNumbers[0]) {
			if (view.positiveAxes[1] && !view.positiveAxes[0]
					|| view.positiveAxes[0] && view.positiveAxes[1]
							&& !view.showAxesNumbers[1]
					|| !view.showAxes[1]) {
				x = (int) (xCrossPix - (EuclidianView.estimateTextWidth("0",
						view.getFontAxes()) / 2));
			} else {
				x = (int) ((xCrossPix + xoffset) - width); // left
			}
		}

		// Don't draw zero, if it goes out of the screen
		if (x < 0 || xCrossPix > view.getWidth()) {
			return;
		}

		// Don't draw "0" if the number before zero on x axis is too close to
		// "0".
		if (beforeZeroX != null && x < this.beforeZeroX + fontsize / 2) {
			return;
		}

		int y;
		if (view.positiveAxes[0] && !view.positiveAxes[1]
				|| view.positiveAxes[0] && view.positiveAxes[1]
						&& !view.showAxesNumbers[0]
				|| !view.showAxes[0]) {
			y = (int) (yCrossPix + yoffset2);
		} else {
			y = (int) (yCrossPix + yoffset); // bottom
		}

		// Don't draw "0" if the number before on y axis zero is too close to
		// "0".
		if (beforeZeroY != null && y > beforeZeroY) {
			return;
		}

		// improve y position at top and bottom edge
		if (view.showAxes[1] && view.showAxesNumbers[1]) {
			if (y < fontsize || y > view.getHeight()) {
				return;
			}
		} else if (yCrossPix >= view.getHeight() - (view.xLabelHeights + 5)) {
			y = (int) (view.getHeight() - view.xLabelHeights - 5 + yoffset);
		} else if (yCrossPix <= 0) {
			y = (int) yoffset;
		}

		drawString(g2, "0", x, y);
	}

	private class TickNumber {
		String text;
		int x;
		int y;
		double xCrossPix;
		double xoffset;
		double width;
		GGraphics2D g2;

		TickNumber(GGraphics2D graphics, String text1, int x1, int y1,
				double xCrossPix1,
				double xoffset1, double width1) {
			text = text1;
			x = x1;
			y = y1;
			xCrossPix = xCrossPix1;
			xoffset = xoffset1;
			width = width1;
			g2 = graphics;
		}

		public void draw() {
			// At the left and right edge numbers will stay at the border

			Integer x2 = getXPositionAtEdge(xCrossPix, xoffset, width);
			if (x2 != null) {
				x = x2;
			}

			drawString(g2, text, x, y);
		}

	}

	private void drawYticksLog(GGraphics2D g2, double xCrossPix, int fontsize,
			char minusSign, boolean drawTopArrow, double yCrossPix,
			double yAxisEnd) {
		double xoffset = -4 - (fontsize / 4d);
		double yoffset = (fontsize / 2d) - 1;
		boolean[] drawMajorTicks = { view.getAxisTickStyle(0) <= 1, view.getAxisTickStyle(1) <= 1 };
		boolean[] drawMinorTicks = { view.getAxisTickStyle(0) == 0, view.getAxisTickStyle(1) == 0 };
		double xSmall1 = xCrossPix - 0;
		double xSmall2 = xCrossPix - 2;
		double xBig = xCrossPix - 3;
		double smallTickOffset = 0;
		double xZeroTick = xCrossPix;
		if (view.areAxesBold()) {
			xSmall2--;
		}
		// numbers
		double rw = view.getYmin()
				- (view.getYmin() % view.axesNumberingDistances[1]);
		long labelno = Math.round(rw / view.axesNumberingDistances[1]);
		// by default we start with minor tick to the left of first major
		// tick, exception is for positive only

		double pow = MyMath.nextPrettyNumber(view.getYmin(), 1);
		double axisStep = view.getHeight()
				/ (Math.log10(view.getYmax()) - Math.log10(view.getYmin()));
		double pix = (Math.log10(view.getYmax()) - Math.log10(pow)) * axisStep;
		if (view.getPositiveAxes()[1]
				&& (DoubleUtil.isGreaterEqual(rw, view.getYmin()))) {
			// start labels at the y-axis instead of screen border
			// be careful: view.axisCross[1] = x value for which the y-axis
			// crosses,
			// so xmin is replaced view.axisCross[1] and not
			// view.axisCross[0]
			rw = MyMath.nextMultiple(view.axisCross[0],
					view.axesNumberingDistances[1]);
			smallTickOffset = axisStep;
			labelno = Math.round(rw / view.axesNumberingDistances[1]);
		}

		double tickStep = axisStep / 2;

		double maxHeight = EuclidianView
				.estimateNumberHeight(view.getFontAxes());
		int unitsPerLabelY = (int) MyMath.nextPrettyNumber(maxHeight / axisStep,
				1);

		if (pix > (view.getHeight() - EuclidianView.SCREEN_BORDER)) {
			// big tick
			if (drawMajorTicks[1]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(xBig, pix, xZeroTick, pix);
			}
			pix -= axisStep;
			rw += view.axesNumberingDistances[1];
			labelno++;
		}

		double smallTickPix = pix + tickStep;

		// draw all of the remaining ticks and labels

		// int maxY = height - view.SCREEN_BORDER;
		int maxY = EuclidianView.SCREEN_BORDER;

		// yAxisEnd

		String crossAtStr = "" + view.kernel.formatPiE(view.axisCross[0],
				view.axesNumberFormat[1], StringTemplate.defaultTemplate);
		for (; pix >= maxY; rw += view.axesNumberingDistances[1], pix -= axisStep, labelno++) {
			if (pix >= maxY && pix < yAxisEnd + 1) {
				if (view.showAxesNumbers[1]) {
					String strNum = tickDescriptionLog(view, pow, 1);
					pow = pow * 10;
					if ((labelno % unitsPerLabelY) == 0) {

						StringBuilder sb = new StringBuilder(strNum);

						// don't check rw < 0 as it fails for eg
						// -0.0000000001
						if (sb.charAt(0) == '-') {
							// change minus sign (too short) to n-dash
							sb.setCharAt(0, minusSign);
						}

						if ((view.axesUnitLabels[1] != null)
								&& !view.piAxisUnit[1]) {
							sb.append(view.axesUnitLabels[1]);
						}

						GTextLayout layout = AwtFactory.getPrototype()
								.newTextLayout(sb.toString(),
										view.getFontAxes(),
										g2.getFontRenderContext());

						double width = layout.getAdvance();

						int x = (int) ((xCrossPix + xoffset) - width);
						int y;

						// flag for handling label at axis cross point
						boolean zero = strNum.equals(crossAtStr);

						// if the label is at the axis cross point then draw
						// it 2 pixels above
						if (zero && view.showAxes[0] && !view.positiveAxes[0]) {
							y = (int) (yCrossPix - 2);
						} else {
							y = (int) (pix + yoffset);
						}
						// draw number
						drawString(g2, sb.toString(), x, y);
						// measure width, so grid line can avoid it
						// use same (max) for all labels
						if (sb.charAt(0) == minusSign
								&& width > view.yLabelMaxWidthNeg) {
							view.yLabelMaxWidthNeg = width;
						} else if (sb.charAt(0) != minusSign
								&& width > view.yLabelMaxWidthPos) {
							view.yLabelMaxWidthPos = width;
						}

					}
				}
				if (drawMajorTicks[1] && (!view.showAxes[0]
						|| !DoubleUtil.isEqual(rw, view.axisCross[0]))) {
					g2.setStroke(view.tickStroke);
					g2.drawStraightLine(xBig, pix, xZeroTick, pix);
				}
			} else if (drawMajorTicks[1] && !drawTopArrow) {
				// draw last tick if there is no arrow
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(xBig, pix, xZeroTick, pix);
			}

			// small tick
			smallTickPix = (pix + tickStep) - smallTickOffset;
			if (drawMinorTicks[1]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(xSmall1, smallTickPix, xSmall2,
						smallTickPix);
			}
		}
	}

	/**
	 * spaceToLeft so that minus signs are more visible next to grid
	 * 
	 * @param g2
	 *            graphics
	 * @param text
	 *            text
	 * @param x
	 *            x-coord of text (left side)
	 * @param y
	 *            y-coord of text (top)
	 */
	void drawString(GGraphics2D g2, String text, double x, double y) {
		view.drawStringWithOutline(g2, text, x, y, view.axesColor);
	}

	private void drawXTicksLinear(GGraphics2D g2, double yCrossPix,
			char minusSign, boolean drawRightArrow, int fontsize,
			double xAxisStart) {
		double yoffset = view.getYOffsetForXAxis(fontsize);

		boolean enableTicks = !view.getShowGrid()
				|| (view.axesNumberingDistances[0] != view.getGridDistances()[0]);

		boolean[] drawMajorTicks = { view.getAxisTickStyle(0) <= 1 && enableTicks,
				view.getAxisTickStyle(1) <= 1 && enableTicks };
		boolean[] drawMinorTicks = { view.getAxisTickStyle(0) == 0 && enableTicks,
				view.getAxisTickStyle(1) == 0 && enableTicks };

		double rw = view.getXmin()
				- (view.getXmin() % view.axesNumberingDistances[0]);
		long labelno = Math.round(rw / view.axesNumberingDistances[0]);
		// by default we start with minor tick to the left of first major
		// tick, exception is for positive only
		double smallTickOffset = 0;
		double axesStep = view.getXscale() * view.axesNumberingDistances[0]; // pixelstep
		if (view.getPositiveAxes()[0]
				&& (DoubleUtil.isGreaterEqual(view.axisCross[1], view.getXmin()))) {
			// start labels at the y-axis instead of screen border
			// be careful: view.axisCross[1] = x value for which the y-axis
			// crosses,
			// so xmin is replaced view.axisCross[1] and not
			// view.axisCross[0]
			rw = MyMath.nextMultiple(view.axisCross[1],
					view.axesNumberingDistances[0]);
			smallTickOffset = axesStep;
			labelno = Math.round(rw / view.axesNumberingDistances[0]);
		}
		int maxX = view.getWidth() - EuclidianView.SCREEN_BORDER;
		double pix = view.getXZero() + (rw * view.getXscale());

		double smallTickPix;
		double tickStep = axesStep / 2;
		double labelLengthMax = Math.max(
				view.estimateNumberWidth(rw, view.getFontAxes()),
				view.estimateNumberWidth(
						MyMath.nextMultiple(view.getXmax(),
								view.axesNumberingDistances[0]),
						view.getFontAxes()));
		int unitsPerLabelX = (int) MyMath
				.nextPrettyNumber(labelLengthMax / axesStep, 1);
		String crossAtStr = "" + view.kernel.formatPiE(view.axisCross[1],
				view.axesNumberFormat[0], StringTemplate.defaultTemplate);
		double yZeroTick = yCrossPix;
		double yBig = yCrossPix + 3;
		double ySmall1 = yCrossPix + 0;
		double ySmall2 = yCrossPix + 2;
		if (view.areAxesBold()) {
			ySmall2++;
		}
		if (pix < EuclidianView.SCREEN_BORDER) {
			// big tick
			if (drawMajorTicks[0]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(pix, yZeroTick, pix, yBig);
			}
			pix += axesStep;
			labelno += 1;
		}

		for (; pix < view.getWidth(); pix += axesStep) {

			// 285, 285.1, 285.2 -> rounding problems
			if (pix >= xAxisStart && pix <= maxX) {
				if (view.showAxesNumbers[0]) {
					boolean currency = isCurrencyUnit(0);
					String strNum = currency ? tickUnit(view, labelno, 0)
							: tickDescription(view, labelno, 0);

					if ((labelno % unitsPerLabelX) == 0) {
						StringBuilder sb = formatUnitLabel(strNum, 0, minusSign, currency);

						int x, y = (int) (yCrossPix + yoffset);

						// flag to handle drawing a label at axis crossing
						// point
						boolean zero = strNum.equals(crossAtStr);
						// if label intersects the y-axis then draw it 6
						// pixels to the left
						if (zero && view.showAxes[1] && !view.positiveAxes[1]) {
							x = (int) (pix + 6);
						} else {
							x = (int) ((pix + 1)
									- (EuclidianView.estimateTextWidth(
											sb.toString(), view.getFontAxes())
											/ 2));
						}

						if (labelno == -unitsPerLabelX) {
							beforeZeroX = (int) ((pix + 1)
									+ (EuclidianView.estimateTextWidth(
											sb.toString(), view.getFontAxes())
											/ 2));
						}

						if (yCrossPix >= view.getHeight()
								- (view.xLabelHeights + 5)) {
							y = (int) (view.getHeight() - view.xLabelHeights - 5
									+ yoffset);
						} else if (yCrossPix <= 0) {
							y = (int) yoffset;
						}

						boolean bothNull = zero && view.axisCross[0] == 0
								&& view.axisCross[1] == 0;

						if (!bothNull) {
							drawString(g2, sb.toString(), x, y);
						}

						// store position of number, so grid line can avoid
						// it
						view.axesLabelsPositionsX.add(Integer
								.valueOf((int) (pix + Kernel.MIN_PRECISION)));
					}
				}
				// big tick
				if (drawMajorTicks[0]
						&& (!view.showAxes[1] || !DoubleUtil.isEqual(pix,
								view.toScreenCoordX(view.axisCross[1])))) {
					g2.setStroke(view.tickStroke);
					g2.drawStraightLine(pix, yZeroTick, pix, yBig);
				}
			} else if (drawMajorTicks[0] && !drawRightArrow) {
				// draw last tick if there is no arrow
				g2.drawStraightLine(pix, yZeroTick, pix, yBig);
			}

			// small tick
			smallTickPix = (pix - tickStep) + smallTickOffset;
			if (drawMinorTicks[0]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(smallTickPix, ySmall1, smallTickPix,
						ySmall2);
			}
			labelno++;
		}
		// last small tick
		smallTickPix = (pix - tickStep) + smallTickOffset;
		if (drawMinorTicks[0]) {
			g2.drawStraightLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
		}

	}

	private void drawXTicksLog(GGraphics2D g2, double yCrossPix, char minusSign,
			boolean drawRightArrow, int fontsize, double xAxisStart) {
		double yoffset = view.getYOffsetForXAxis(fontsize);
		boolean[] drawMajorTicks = { view.getAxisTickStyle(0) <= 1, view.getAxisTickStyle(1) <= 1 };
		boolean[] drawMinorTicks = { view.getAxisTickStyle(0) == 0, view.getAxisTickStyle(1) == 0 };
		// by default we start with minor tick to the left of first major
		// tick, exception is for positive only
		double smallTickOffset = 0;

		int maxX = view.getWidth() - EuclidianView.SCREEN_BORDER;

		double smallTickPix;
		// TODO use only pretty numbers when zoomed
		String crossAtStr = "" + view.kernel.formatPiE(view.axisCross[1],
				view.axesNumberFormat[0], StringTemplate.defaultTemplate);
		double yZeroTick = yCrossPix;
		double yBig = yCrossPix + 3;
		double ySmall1 = yCrossPix + 0;
		double ySmall2 = yCrossPix + 2;
		if (view.areAxesBold()) {
			ySmall2++;
		}

		double pow = MyMath.nextPrettyNumber(view.getXmin(), 1);
		double axisStep = view.getWidth()
				/ (Math.log10(view.getXmax()) - Math.log10(view.getXmin()));
		double pix = (Math.log10(pow) - Math.log10(view.getXmin())) * axisStep;
		while (pow < view.getXmax()) {

			// 285, 285.1, 285.2 -> rounding problems
			if (pix >= xAxisStart && pix <= maxX) {
				if (view.showAxesNumbers[0]) {
					String strNum = tickDescriptionLog(view, pow, 0);

					StringBuilder sb = new StringBuilder(strNum);

					// don't check rw < 0 as it fails for eg
					// -0.0000000001
					if (sb.charAt(0) == '-') {
						// change minus sign (too short) to n-dash
						sb.setCharAt(0, minusSign);
					}
					if ((view.axesUnitLabels[0] != null)
							&& !view.piAxisUnit[0]) {
						sb.append(view.axesUnitLabels[0]);
					}

					int x, y = (int) (yCrossPix + yoffset);

					// flag to handle drawing a label at axis crossing
					// point
					boolean zero = strNum.equals(crossAtStr);
					// if label intersects the y-axis then draw it 6
					// pixels to the left
					if (zero && view.showAxes[1] && !view.positiveAxes[1]) {
						x = (int) (pix + 6);
					} else {
						x = (int) ((pix + 1) - (EuclidianView.estimateTextWidth(
								sb.toString(), view.getFontAxes()) / 2));
					}

					drawString(g2, sb.toString(), x, y);

					// store position of number, so grid line can avoid
					// it
					view.axesLabelsPositionsX.add(Integer
							.valueOf((int) (pix + Kernel.MIN_PRECISION)));
				}

				// big tick
				if (drawMajorTicks[0]
						&& (!view.showAxes[1] || !DoubleUtil.isEqual(pix,
								view.toScreenCoordX(view.axisCross[1])))) {
					g2.setStroke(view.tickStroke);
					g2.drawStraightLine(pix, yZeroTick, pix, yBig);
				}
			} else if (drawMajorTicks[0] && !drawRightArrow) {
				// draw last tick if there is no arrow
				g2.drawStraightLine(pix, yZeroTick, pix, yBig);
			}

			// small tick
			smallTickPix = (pix - 0) + smallTickOffset;
			if (drawMinorTicks[0]) {
				g2.setStroke(view.tickStroke);
				g2.drawStraightLine(smallTickPix, ySmall1, smallTickPix,
						ySmall2);
			}
			pow = pow * 10;
			pix += axisStep;
		}
		// last small tick
		smallTickPix = (pix - 0) + smallTickOffset;
		if (drawMinorTicks[0]) {
			g2.drawStraightLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
		}

	}

	private static String tickUnit(EuclidianView view, long labelno, int axis) {
		double num = Math.round(100 * labelno * view.axesNumberingDistances[axis]) / 100.0;

		String strNum0 = "";
		if (DoubleUtil.isInteger(num)) {
			strNum0 += (int) num;
		} else {
			strNum0 += num;
		}

		StringBuilder sb = new StringBuilder();
		String strNum = "";

		if (num < 0) {
			strNum = strNum0.substring(1);
			sb.append(strNum0.charAt(0));
		} else {
			strNum = strNum0;
		}

		if (useThousandsSeparator(view.axesUnitLabels[axis])
				&& Math.abs(num) >= CURRENCY_DOLLAR_BATCH) {
			int length = strNum.length();

			int mod = length % CURRENCY_DOLLAR_MOD;
			if (mod == 0) {
				mod = 3;
			}

			sb.append(strNum.substring(0, mod));

			for (int i = mod; i < length; i += CURRENCY_DOLLAR_MOD) {
				sb.append(",");
				sb.append(strNum.substring(i, i + CURRENCY_DOLLAR_MOD));
			}
		} else {
			sb.append(strNum);
		}
		if (!DoubleUtil.isInteger(num)) {
			sb.append((Math.round(num * 100) % 10 == 0) ? "0" : "");
		}
		return sb.toString();
	}

	private static boolean useThousandsSeparator(String s) {
		return s != null && s.length() == 1
				&& (s.charAt(0) == Unicode.CURRENCY_DOLLAR
						|| s.charAt(0) == ',');
	}

	/**
	 * @param view
	 *            view
	 * @param labelno
	 *            coefficient
	 * @param axis
	 *            axis index
	 * @return description
	 */
	public static String tickDescription(EuclidianView view, long labelno,
			int axis) {
		if (view.getAxesDistanceObjects()[axis] != null
				&& !view.isAutomaticAxesNumberingDistance()[axis]
				&& view.getAxesDistanceObjects()[axis].getDefinition() != null
				&& view.getAxesDistanceObjects()[axis].getDouble() > 0) {
			return multiple(view.getAxesDistanceObjects()[axis].getDefinition(),
					labelno);
		}
		return view.kernel.formatPiE(
				DoubleUtil.checkDecimalFraction(
						labelno * view.axesNumberingDistances[axis]),
				view.axesNumberFormat[axis], StringTemplate.defaultTemplate);
	}

	/**
	 * @param view
	 *            view
	 * @param num
	 *            numbr to be printed
	 * @param axis
	 *            0 for x,1 for y
	 * @return description
	 */
	public static String tickDescriptionLog(EuclidianView view, double num,
			int axis) {
		return view.kernel.formatPiE(DoubleUtil.checkDecimalFraction(num),
				view.axesNumberFormat[axis], StringTemplate.defaultTemplate);
	}

	/**
	 * @param definition
	 *            step definition
	 * @param labelno
	 *            step coefficient
	 * @return description of labelno*definition
	 */
	public static String multiple(ExpressionNode definition, long labelno) {
		return definition.multiply(labelno)
				.toFractionString(StringTemplate.defaultTemplate);
	}

	private boolean isCurrencyUnit(int axis) {
		return StringUtil.isCurrency(view.axesUnitLabels[axis])
				|| ",".equals(view.axesUnitLabels[axis]);

	}
}
