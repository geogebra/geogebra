package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.Unicode;

/**
 * Draws axes in 2D
 */
public class DrawAxis {
	/** view */
	EuclidianView view;


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
		char minusSign = view.getApplication().getExportType()
				.equals(ExportType.PDF_EMBEDFONTS) ? '-' : Unicode.nDash;

		// xCrossPix: yAxis crosses the xAxis at this x pixel
		double xCrossPix = view.getXAxisCrossingPixel();

		// yCrossPix: xAxis crosses the yAxis at this y pixel
		double yCrossPix = view.getYAxisCrossingPixel();

		// yAxis end value (for drawing half-axis)
		int yAxisEnd = view.positiveAxes[1] ? (int) yCrossPix : view
				.getHeight();

		// xAxis start value (for drawing half-axis)
		int xAxisStart = view.positiveAxes[0] ? (int) xCrossPix : 0;

		// for axes ticks

		boolean bold = view.areAxesBold();
		boolean filled = (view.axesLineType & EuclidianStyleConstants.AXES_FILL_ARROWS) != 0;

		if (filled && view.gp == null) {
			view.gp = AwtFactory.prototype.newGeneralPath();
		}

		boolean drawRightArrow = ((view.axesLineType & EuclidianStyleConstants.AXES_RIGHT_ARROW) != 0)
				&& !(view.positiveAxes[0] && (view.getXmax() < view.axisCross[1]));
		boolean drawTopArrow = ((view.axesLineType & EuclidianStyleConstants.AXES_RIGHT_ARROW) != 0)
				&& !(view.positiveAxes[1] && (view.getYmax() < view.axisCross[0]));

		boolean drawLeftArrow = ((view.axesLineType & EuclidianStyleConstants.AXES_LEFT_ARROW) != 0)
				&& !(view.positiveAxes[0]);
		boolean drawBottomArrow = ((view.axesLineType & EuclidianStyleConstants.AXES_LEFT_ARROW) != 0)
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

		// turn antialiasing off
		// Object antiAliasValue = g2
		// .getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);

		// make sure arrows don't go off screen (eg EMF export)
		double arrowAdjustx = drawRightArrow ? view.axesStroke.getLineWidth()
				: 0;
		double arrowAdjusty = drawTopArrow ? view.axesStroke.getLineWidth() : 0;

		// Draw just y-axis first (in case any labels need to be drawn over it)
		if (view.yAxisOnscreen()) {

			// y-Axis itself
			g2.setStroke(view.axesStroke);
			g2.drawStraightLine(xCrossPix, arrowAdjusty
					+ (drawTopArrow ? 1 : -1), xCrossPix, yAxisEnd
					+ (drawBottomArrow ? -2 : 0));

			if (drawTopArrow) {

				if (filled) {

					view.gp.reset();
					view.gp.moveTo((float) xCrossPix, (float) arrowAdjusty);
					view.gp.lineTo((float) (xCrossPix - arrowSize),
							(float) (arrowAdjusty + 4 * arrowSize));
					view.gp.lineTo((float) (xCrossPix + arrowSize),
							(float) (arrowAdjusty + 4 * arrowSize));

					g2.fill(view.gp);

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

					view.gp.reset();
					view.gp.moveTo((float) xCrossPix,
							(float) (view.getHeight() - arrowAdjusty));
					view.gp.lineTo(
							(float) (xCrossPix - arrowSize),
							(float) (view.getHeight() - arrowAdjusty - 4 * arrowSize));
					view.gp.lineTo(
							(float) (xCrossPix + arrowSize),
							(float) (view.getHeight() - arrowAdjusty - 4 * arrowSize));

					g2.fill(view.gp);

				} else {
					// draw bottom arrow for y-axis
					g2.drawStraightLine(xCrossPix, view.getHeight()
							- arrowAdjusty, xCrossPix - arrowSize,
							view.getHeight() - arrowAdjusty
									- arrowSize);
					g2.drawStraightLine(xCrossPix, view.getHeight()
							- arrowAdjusty, xCrossPix + arrowSize,
							view.getHeight() - arrowAdjusty
									- arrowSize);
				}
			}
			// erase grid to make space for labels

		}

		// ========================================
		// X-AXIS
		if (view.xAxisOnscreen()) {
			// erase the grid to make space for labels, use two rectangles


			// label of x axis
			if (view.axesLabels[0] != null) {
				GFont font = view.getFontLine()
						.deriveFont(view.axesLabelsStyle[0]);
				GTextLayout layout = AwtFactory.prototype.newTextLayout(
						view.axesLabels[0], font, frc);
				if (!view.axesLabels[0].contains("_")) {
					layout.draw(g2,
							(int) (view.getWidth() - 10 - layout.getAdvance()),
							(int) (yCrossPix - 4));
				} else {
					GFont old = g2.getFont();
					g2.setFont(font);
					EuclidianStatic.drawIndexedString(view.getApplication(), g2,
							view.axesLabels[0],
							view.getWidth() - 10 - layout.getAdvance(),
							(int) (yCrossPix - 4), false, false);
					g2.setFont(old);
				}
			}

			// numbers






			// x-Axis itself
			g2.setStroke(view.axesStroke);
			g2.drawStraightLine(xAxisStart + (drawLeftArrow ? 2 : 0),
					yCrossPix, view.getWidth() - arrowAdjustx - 1, yCrossPix);

			if (drawRightArrow) {

				if (filled) {

					view.gp.reset();
					view.gp.moveTo((float) (view.getWidth() - arrowAdjustx),
							(float) yCrossPix);
					view.gp.lineTo(
							(float) (view.getWidth() - arrowAdjustx - arrowSize * 4),
							(float) (yCrossPix - arrowSize));
					view.gp.lineTo(
							(float) (view.getWidth() - arrowAdjustx - arrowSize * 4),
							(float) (yCrossPix + arrowSize));

					g2.fill(view.gp);

				} else {

					// draw right arrow for x-axis
					g2.drawStraightLine(view.getWidth() - arrowAdjustx,
							yCrossPix, view.getWidth() - arrowAdjustx
									- arrowSize, yCrossPix
									- arrowSize);

					g2.drawStraightLine(view.getWidth() - arrowAdjustx,
							yCrossPix, view.getWidth() - arrowAdjustx
									- arrowSize, yCrossPix
									+ arrowSize);

				}
			}

			if (drawLeftArrow) {

				if (filled) {

					view.gp.reset();
					view.gp.moveTo((float) (arrowAdjustx), (float) yCrossPix);
					view.gp.lineTo((float) (arrowAdjustx + arrowSize * 4),
							(float) (yCrossPix - arrowSize));
					view.gp.lineTo((float) (arrowAdjustx + arrowSize * 4),
							(float) (yCrossPix + arrowSize));

					g2.fill(view.gp);

				} else {

					// draw left arrow for x-axis
					g2.drawStraightLine(arrowAdjustx, yCrossPix, arrowAdjustx
							+ arrowSize, yCrossPix - arrowSize);
					g2.drawStraightLine(arrowAdjustx, yCrossPix, arrowAdjustx
							+ arrowSize, yCrossPix + arrowSize);
				}
			}



			view.axesNumberingDistances[0] = Kernel
					.checkDecimalFraction(view.axesNumberingDistances[0]);


			// for (; pix < view.getWidth(); rw +=
			// view.axesNumberingDistances[0], pix +=
			// axesStep) {
			drawXTicksLinear(g2, yCrossPix, minusSign, drawRightArrow,
					fontsize,
					xAxisStart);

		}

		// ========================================
		// Y-AXIS

		if (view.yAxisOnscreen()) {



			// label of y axis
			if (view.axesLabels[1] != null) {
				GFont font = view.getFontLine()
						.deriveFont(view.axesLabelsStyle[1]);
				GTextLayout layout = AwtFactory.prototype.newTextLayout(
						view.axesLabels[1],
						font,
						frc);
				if (!view.axesLabels[1].contains("_")) {
				layout.draw(g2, (int) (xCrossPix + 5),
						(int) (5 + layout.getAscent()));
				}else{
					GFont old = g2.getFont();
					g2.setFont(font);
					EuclidianStatic.drawIndexedString(view.getApplication(), g2,
							view.axesLabels[1],
							(int) (xCrossPix + 5),
							(int) (5 + layout.getAscent()), false, false);
					g2.setFont(old);
				}
			}


			drawYticksLinear(g2, xCrossPix, fontsize, minusSign, drawTopArrow,
					yCrossPix, yAxisEnd);
		}

	}

	private void drawYticksLinear(GGraphics2D g2, double xCrossPix,
			int fontsize, char minusSign, boolean drawTopArrow,
			double yCrossPix, double yAxisEnd) {
		double xoffset = -4 - (fontsize / 4);
		double yoffset = (fontsize / 2) - 1;
		boolean[] drawMajorTicks = { view.axesTickStyles[0] <= 1,
				view.axesTickStyles[1] <= 1 };
		boolean[] drawMinorTicks = { view.axesTickStyles[0] == 0,
				view.axesTickStyles[1] == 0 };
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

		double axesStep = view.getYscale() * view.axesNumberingDistances[1]; // pixelstep
		if (view.getPositiveAxes()[1]
				&& (Kernel.isGreaterEqual(rw, view.getYmin()))) {
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

		double pix = view.getyZero() - (rw * view.getYscale());

		double tickStep = axesStep / 2;

		double maxHeight = EuclidianView.estimateNumberHeight(view
				.getFontAxes());
		int unitsPerLabelY = (int) MyMath.nextPrettyNumber(
				maxHeight / axesStep, 1);

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

		double smallTickPix = pix + tickStep;

		// draw all of the remaining ticks and labels

		// int maxY = height - view.SCREEN_BORDER;
		int maxY = EuclidianView.SCREEN_BORDER;

		// yAxisEnd

		String crossAtStr = ""
				+ view.kernel.formatPiE(view.axisCross[0],
						view.axesNumberFormat[1],
						StringTemplate.defaultTemplate);
		for (; pix >= maxY; rw += view.axesNumberingDistances[1], pix -= axesStep, labelno++) {
			if (pix >= maxY && pix < yAxisEnd + 1) {
				if (view.showAxesNumbers[1]) {
					String strNum = tickDescription(labelno, 1);

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

						GTextLayout layout = AwtFactory.prototype
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
						view.drawString(g2, sb.toString(), x, y);
						// measure width, so grid line can avoid it
						// use same (max) for all labels
						if (sb.charAt(0) == minusSign
								&& width > view.yLabelMaxWidthNeg) {
							view.yLabelMaxWidthNeg = width;
						} else if (sb.charAt(0) != minusSign
								&& width > view.yLabelMaxWidthPos) {
							view.yLabelMaxWidthPos = width;
						}

						// store position of number, so grid line can avoid
						// it
						view.axesLabelsPositionsY.add(Integer
								.valueOf(
								(int) (pix + Kernel.MIN_PRECISION)));
					}
				}
				if (drawMajorTicks[1]
						&& (!view.showAxes[0]
								|| !Kernel.isEqual(rw, view.axisCross[0]))) {
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

	private void drawXTicksLinear(GGraphics2D g2, double yCrossPix,
			char minusSign, boolean drawRightArrow, int fontsize,
			double xAxisStart) {
		double yoffset = view.getYOffsetForXAxis(fontsize);
		boolean[] drawMajorTicks = { view.axesTickStyles[0] <= 1,
				view.axesTickStyles[1] <= 1 };
		boolean[] drawMinorTicks = { view.axesTickStyles[0] == 0,
				view.axesTickStyles[1] == 0 };
		double rw = view.getXmin()
				- (view.getXmin() % view.axesNumberingDistances[0]);
		long labelno = Math.round(rw / view.axesNumberingDistances[0]);
		// by default we start with minor tick to the left of first major
		// tick, exception is for positive only
		double smallTickOffset = 0;
		double axesStep = view.getXscale() * view.axesNumberingDistances[0]; // pixelstep
		if (view.getPositiveAxes()[0]
				&& (Kernel.isGreaterEqual(rw, view.getXmin()))) {
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
		double pix = view.getxZero() + (rw * view.getXscale());

		double smallTickPix;
		double tickStep = axesStep / 2;
		double labelLengthMax = Math.max(view.estimateNumberWidth(rw,
				view.getFontAxes()), view.estimateNumberWidth(MyMath
				.nextMultiple(view.getXmax(), view.axesNumberingDistances[0]),
				view.getFontAxes()));
		int unitsPerLabelX = (int) MyMath.nextPrettyNumber(labelLengthMax
				/ axesStep, 1);
		String crossAtStr = ""
				+ view.kernel.formatPiE(view.axisCross[1],
						view.axesNumberFormat[0],
						StringTemplate.defaultTemplate);
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
					String strNum = tickDescription(labelno, 0);

					if ((labelno % unitsPerLabelX) == 0) {

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
							x = (int) ((pix + 1) - (EuclidianView
									.estimateTextWidth(sb.toString(),
											view.getFontAxes()) / 2));
						}

						view.drawString(g2, sb.toString(), x, y);

						// store position of number, so grid line can avoid
						// it
						view.axesLabelsPositionsX.add(Integer
								.valueOf(
								(int) (pix + Kernel.MIN_PRECISION)));
					}
				}
				// big tick
				if (drawMajorTicks[0]
						&& (!view.showAxes[1] || !Kernel.isEqual(pix,
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

	private String tickDescription(long labelno, int axis) {
		if (view.getAxesDistanceObjects()[axis] != null
				&& !view.isAutomaticAxesNumberingDistance()[axis]
				&& view.getAxesDistanceObjects()[axis].getDefinition() != null
				&& view.getAxesDistanceObjects()[axis].getDouble() > 0) {
			return multiple(
					view.getAxesDistanceObjects()[axis].getDefinition(),
					labelno);
		}
		return view.kernel.formatPiE(
				Kernel.checkDecimalFraction(labelno
						* view.axesNumberingDistances[axis]),
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
		return definition.multiply(labelno).toFractionString(
				StringTemplate.defaultTemplate);
	}
}
