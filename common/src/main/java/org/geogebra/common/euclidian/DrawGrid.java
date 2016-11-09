package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.MyMath;

/**
 * Helper class for drawing the grid
 * 
 * @author zbynek
 *
 */
public class DrawGrid {
	private EuclidianView view;

	/**
	 * 
	 * @param euclidianView
	 *            view
	 */
	public DrawGrid(EuclidianView euclidianView) {
		view = euclidianView;
	}

	/**
	 * 
	 * @param g2
	 *            graphics
	 * @param xCrossPix
	 *            x crossing pixel
	 * @param yCrossPix
	 *            y crossing pixel
	 */
	protected void drawCartesianGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		if (view.getXaxisLog()) {
			drawVerticalGridLog(g2, xCrossPix, yCrossPix);
		} else {
			drawVerticalGridLinear(g2, xCrossPix, yCrossPix);
		}

		// horizontal grid lines
		if (view.getYaxisLog()) {
			drawHorizontalGridLog(g2, xCrossPix, yCrossPix);
		} else {
			drawHorizontalGridLinear(g2, xCrossPix, yCrossPix);
		}

	}

	private void drawHorizontalGridLinear(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		double tickStepY = view.getYscale() * view.gridDistances[1];
		double start = view.getYZero() % tickStepY;
		double pix = start;
		final double left = view.positiveAxes[0] ? xCrossPix : 0;
		if (pix > (view.getHeight() - EuclidianView.SCREEN_BORDER)) {
			pix -= tickStepY;
		}
		final double yAxisEnd = (view.positiveAxes[1] && yCrossPix < view
				.getHeight()) ? yCrossPix : view.getHeight();
		for (int j = 0; pix <= yAxisEnd; j++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)
			if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {

				if (view.axesLabelsPositionsY.contains(new Integer(
						(int) (pix + Kernel.MIN_PRECISION)))) {

					// hits axis label, draw in 2 sections
					drawLineAvoidingLabelsH(g2, left, pix, view.getWidth(),
							pix, xCrossPix);
				} else {

					// not hitting axis label, just draw it
					g2.drawStraightLine(left, pix, view.getWidth(), pix);
				}
			}

			pix = start + (j * tickStepY);
		}

	}

	private void drawHorizontalGridLog(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		double tickStepY = view.getYscale() * view.gridDistances[1];
		double start = view.getYZero() % tickStepY;
		double pix = 0;
		final double left = view.positiveAxes[0] ? xCrossPix : 0;		
		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		double pow = MyMath.nextPrettyNumber(view.getYmin(), 1);
		for (int j = 0; pix <= yAxisEnd; j++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			pix = view.toScreenCoordYd(pow);
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
			pow = pow * 10;
		}

	}

	private void drawVerticalGridLinear(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		// vertical grid lines
		double tickStepX = view.getXscale() * view.gridDistances[0];
		final double xAxisStart = (view.positiveAxes[0] && xCrossPix > 0) ? xCrossPix
				+ (((view.getXZero() - xCrossPix) % tickStepX) + tickStepX)
				% tickStepX
				: (view.getXZero() % tickStepX);

		final double yAxisEnd = (view.positiveAxes[1] && yCrossPix < view
				.getHeight()) ? yCrossPix : view.getHeight();
		final double bottom = view.positiveAxes[1] ? yAxisEnd : view
				.getHeight();
		double pix = xAxisStart;

		if (pix < EuclidianView.SCREEN_BORDER) {
			pix += tickStepX;
		}
		for (int i = 0; pix <= view.getWidth(); i++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
				if (view.axesLabelsPositionsX.contains(new Integer(
						(int) (pix + Kernel.MIN_PRECISION)))) {

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

	private void drawVerticalGridLog(GGraphics2D g2, double xCrossPix,
			double yCrossPix) {
		// vertical grid lines
		double tickStepX = view.getXscale() * view.gridDistances[0];
		final double xAxisStart = (view.positiveAxes[0] && xCrossPix > 0)
				? xCrossPix + (((view.getXZero() - xCrossPix) % tickStepX)
						+ tickStepX) % tickStepX
				: (view.getXZero() % tickStepX);

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		final double bottom = view.positiveAxes[1] ? yAxisEnd
				: view.getHeight();
		double pix = 0;
		double pow = MyMath.nextPrettyNumber(view.getYmin(), 1);
		for (int i = 0; pix <= view.getWidth(); i++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			pix = view.toScreenCoordXd(pow);

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
			pow = pow * 10;
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
