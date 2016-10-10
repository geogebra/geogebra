package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.Kernel;

public class DrawGrid {
	private EuclidianView view;

	public DrawGrid(EuclidianView euclidianView) {
		view = euclidianView;
	}
	protected void drawCartesianGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		drawVerticalGrid(g2, xCrossPix, yCrossPix);

		// horizontal grid lines
		drawHorizontalGrid(g2, xCrossPix, yCrossPix);

	}

	private void drawHorizontalGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		double tickStepY = view.getYscale() * view.gridDistances[1];
		double start = view.getyZero() % tickStepY;
		double pix = start;
		double rw = view.getYmin()
				- (view.getYmin() % view.axesNumberingDistances[1]);
		double rwBase = Kernel.checkDecimalFraction(rw);
		final double left = view.positiveAxes[0] ? xCrossPix : 0;
		if (pix > (view.getHeight() - EuclidianView.SCREEN_BORDER)) {
			pix -= tickStepY;
			if (!view.getYaxisLog() || view.getYmin() < 0)
				rw += view.axesNumberingDistances[1];
		}
		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		for (int j = 0; pix <= yAxisEnd; j++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)
			if (view.getYaxisLog()) {
				double r = rwBase + Kernel
						.checkDecimalFraction(
								view.axesNumberingDistances[1] * j);
				if (Math.round(r) == r)
					rw = Math.pow(10, r); // condition of integer power
				else {
					rw = Math.pow(10, (int) r);
					double decimal = r - (int) r;
					rw = decimal * 10 * rw;
				}
				pix = 2 * view.getYZero() - view.toScreenCoordYd(rw);
			}
			if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {

				if (view.axesLabelsPositionsY.contains(
						new Integer((int) (pix + Kernel.MIN_PRECISION)))) {

					// hits axis label, draw in 2 sections
					drawLineAvoidingLabelsH(g2, left, pix, view.getWidth(),
							pix,
							xCrossPix);
				} else {

					// not hitting axis label, just draw it
					g2.drawStraightLine(left, pix, view.getWidth(), pix);
				}
			}

			pix = start + (j * tickStepY);
		}

	}
	private void drawVerticalGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		// vertical grid lines
		double tickStepX = view.getXscale() * view.gridDistances[0];
		final double xAxisStart = (view.positiveAxes[0] && xCrossPix > 0)
				? xCrossPix + (((view.getxZero() - xCrossPix) % tickStepX)
						+ tickStepX) % tickStepX
				: (view.getXZero() % tickStepX);

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		final double bottom = view.positiveAxes[1] ? yAxisEnd
				: view.getHeight();
		double pix = xAxisStart;
		double rw = view.getXmin()
				- (view.getXmin() % view.axesNumberingDistances[0]);
		double rwBase = Kernel.checkDecimalFraction(rw);


		if (pix < EuclidianView.SCREEN_BORDER) {
			pix += tickStepX;
			if (!view.getXaxisLog() || view.getXmin() < 0)
				rw += view.axesNumberingDistances[0];
		}
		for (int i = 0; pix <= view.getWidth(); i++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)
			if (view.getXaxisLog()) {
				double r = rwBase + Kernel.checkDecimalFraction(
						view.axesNumberingDistances[0] * i);
				if (Math.round(r) == r)
					rw = Math.pow(10, r); // condition of integer power
				else {
					rw = Math.pow(10, (int) r);
					double decimal = r - (int) r;
					rw = decimal * 10 * rw;
				}
				pix = view.toScreenCoordXd(rw);
			}
			if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
				if (view.axesLabelsPositionsX.contains(
						new Integer((int) (pix + Kernel.MIN_PRECISION)))) {

					// hits axis label, draw in 2 sections
					drawLineAvoidingLabelsV(g2, pix, 0, pix, bottom, yCrossPix);
				} else {
					// not hitting axis label, just draw it
					g2.drawStraightLine(pix, 0, pix, bottom);

				}

			}

			pix = xAxisStart + (i * tickStepX);
		}

	}
	private void drawLineAvoidingLabelsH(GGraphics2D g2, double x1, double y1,
			double x2, double y2, double xCrossPix) {

		if (xCrossPix > x1 && xCrossPix < x2) {
			// split in 2
			g2.drawStraightLine(x1, y1,
					xCrossPix - (view.toRealWorldCoordY(y1) > 0
							? view.yLabelMaxWidthPos : view.yLabelMaxWidthNeg)
							- 10,
					y2);
			g2.drawStraightLine(xCrossPix, y1, x2, y2);

		} else {
			g2.drawStraightLine(x1, y1, x2, y2);
		}
	}

	private void drawLineAvoidingLabelsV(GGraphics2D g2, double x1, double y1,
			double x2, double y2, double yCrossPix) {

		if (yCrossPix > y1 && yCrossPix < y2) {
			// split in 2
			g2.drawStraightLine(x1, y1, x2, yCrossPix);

			g2.drawStraightLine(x1, yCrossPix + view.xLabelHeights + 5, x2, y2);

		} else {
			g2.drawStraightLine(x1, y1, x2, y2);
		}

	}
}
