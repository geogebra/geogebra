package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.MyMath;

import com.google.j2objc.annotations.Weak;

/**
 * Helper class for drawing the grid
 * 
 * @author zbynek
 *
 */
public class DrawGrid {
	@Weak
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
	 * @param subGrid
	 *            whether to draw subgrid
	 */
	protected void drawCartesianGrid(GGraphics2D g2, double xCrossPix,
			double yCrossPix, boolean subGrid) {
		if (view.getXaxisLog()) {
			drawVerticalGridLog(g2, xCrossPix, yCrossPix);
		} else {
			drawVerticalGridLinear(g2, xCrossPix, yCrossPix, subGrid);
		}

		// horizontal grid lines
		if (view.getYaxisLog()) {
			drawHorizontalGridLog(g2, xCrossPix, yCrossPix);
		} else {
			drawHorizontalGridLinear(g2, xCrossPix, yCrossPix, subGrid);
		}
	}

	private void drawHorizontalGridLinear(GGraphics2D g2, double xCrossPix,
			double yCrossPix, boolean subGrid) {
		double tickStepY = view.getYscale() * view.gridDistances[1];
		double start = view.getYZero() % tickStepY;
		int topSubGrids = 0;

		// number of parts splitted by subgrids
		int n = 1;
		if (subGrid) {
			n = 5;
			double smallStep = tickStepY / n;
			//start of subgrids
			start = view.getYZero() % smallStep;
			//start of grids
			double start2 = view.getYZero() % tickStepY;		
			// number of subgrids at the top, above the highest main grid
			topSubGrids = Math.round((float) ((start2 - start) / smallStep));
		}

		final double left = view.positiveAxes[0] ? xCrossPix : 0;

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();

		double pix;

		// draw main grid
		g2.setColor(view.getGridColor());
		g2.startGeneralPath();
		double startGrid = start + topSubGrids * tickStepY / n;
		pix = startGrid;
		for (int j = 0; pix <= yAxisEnd; j++) {
			drawHorizontalGridLine(g2, pix, left, yCrossPix);
			pix = startGrid + (j * tickStepY);
		}
		g2.endAndDrawGeneralPath();

		// draw sub grid
		g2.setColor(GColor.getSubGridColor(view.getGridColor()));
		g2.startGeneralPath();
		pix = start;
		for (int j = 0; pix <= yAxisEnd; j++) {
			if ((j - topSubGrids - 1) % n != 0) {
				// don't draw over main grid
				drawHorizontalGridLine(g2, pix, left, yCrossPix);
			}
			pix = start + (j * tickStepY / n);
		}
		g2.endAndDrawGeneralPath();
	}

	private void drawHorizontalGridLine(GGraphics2D g2, double pix, double left,
			double yCrossPix) {
		// don't draw the grid line x=0 if the y-axis is showing
		// or if it's too close (eg sticky axes)
		if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {
			addStraightLineToGeneralPath(g2, left, pix, view.getWidth(), pix);
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
		g2.startGeneralPath();
		for (int j = 0; pix <= yAxisEnd; j++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			pix = view.toScreenCoordYd(pow);
			if (!view.showAxes[0] || Math.abs(pix - yCrossPix) > 2d) {

				// not hitting axis label, just draw it
				addStraightLineToGeneralPath(g2, left, pix, view.getWidth(),
						pix);

			}

			pix = start + (j * tickStepY);
			pow = pow * 10;
		}
		g2.endAndDrawGeneralPath();

	}

	private void drawVerticalGridLinear(GGraphics2D g2, double xCrossPix,
			double yCrossPix, boolean subGrid) {
		// vertical grid lines
		double tickStepX = view.getXscale() * view.gridDistances[0];
		double xAxisStartMajor = getFirstVisibleVerticalLineX(xCrossPix, tickStepX);
		double xAxisStart = xAxisStartMajor;
		int leftSubGrids = 0;

		// number of parts splitted by subgrids
		int n = 1;
		if (subGrid) {
			n = 5;
			double smallStep = tickStepX / n;
			// start of subgrids
			xAxisStart = getFirstVisibleVerticalLineX(xCrossPix, smallStep);
			// number of subgrids on the left
			leftSubGrids = Math
					.round((float) ((xAxisStartMajor - xAxisStart) / smallStep));
		}

		final double yAxisEnd = (view.positiveAxes[1]
				&& yCrossPix < view.getHeight()) ? yCrossPix : view.getHeight();
		final double bottom = view.positiveAxes[1] ? yAxisEnd
				: view.getHeight();
		double pix;

		// draw main grid
		g2.setColor(view.getGridColor());
		g2.startGeneralPath();
		double startGrid = xAxisStart + leftSubGrids * tickStepX / n;
		pix = startGrid;
		for (int i = 0; pix <= view.getWidth(); i++) {
			drawVerticalGridLine(g2, pix, bottom, xCrossPix, yCrossPix);
			pix = startGrid + (i * tickStepX);
		}
		g2.endAndDrawGeneralPath();

		// draw sub grid
		g2.setColor(GColor.getSubGridColor(view.getGridColor()));
		g2.startGeneralPath();
		pix = xAxisStart;
		for (int i = 1; pix <= view.getWidth(); i++) {
			if ((i - leftSubGrids - 1) % n != 0) {
				// don't draw over main grid
				drawVerticalGridLine(g2, pix, bottom, xCrossPix, yCrossPix);
			}
			pix = xAxisStart + (i * tickStepX / n);
		}
		g2.endAndDrawGeneralPath();
	}

	private double getFirstVisibleVerticalLineX(double xCrossPix, double stepX) {
		return (view.positiveAxes[0] && xCrossPix > 0)
				? xCrossPix + (((view.getXZero() - xCrossPix) % stepX)
				+ stepX) % stepX
				: (view.getXZero() % stepX);
	}

	private void drawVerticalGridLine(GGraphics2D g2, double pix, double bottom,
			double xCrossPix, double yCrossPix) {
		// don't draw the grid line x=0 if the y-axis is showing
		// or if it's too close (eg sticky axes)
		if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
			if (view.axesLabelsPositionsX.contains(
					Integer.valueOf((int) (pix + Kernel.MIN_PRECISION)))) {
				// hits axis label, draw in 2 sections
				drawLineAvoidingLabelsV(g2, pix, 0, pix, bottom, yCrossPix);
			} else {
				// not hitting axis label, just draw it
				addStraightLineToGeneralPath(g2, pix, 0, pix, bottom);
			}
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
		g2.startGeneralPath();
		for (int i = 0; pix <= view.getWidth(); i++) {
			// don't draw the grid line x=0 if the y-axis is showing
			// or if it's too close (eg sticky axes)

			pix = view.toScreenCoordXd(pow);

			if (!view.showAxes[1] || Math.abs(pix - xCrossPix) > 2d) {
				if (view.axesLabelsPositionsX.contains(
						Integer.valueOf((int) (pix + Kernel.MIN_PRECISION)))) {

					// hits axis label, draw in 2 sections
					drawLineAvoidingLabelsV(g2, pix, 0, pix, bottom, yCrossPix);
				} else {
					// not hitting axis label, just draw it
					addStraightLineToGeneralPath(g2, pix, 0, pix, bottom);

				}

			}
			pow = pow * 10;
			pix = xAxisStart + (i * tickStepX);
		}
		g2.endAndDrawGeneralPath();

	}

	private void drawLineAvoidingLabelsV(GGraphics2D g2, double x1, double y1,
			double x2, double y2, double yCrossPix) {

		if (yCrossPix > y1 && yCrossPix < y2) {
			// split in 2
			addStraightLineToGeneralPath(g2, x1, y1, x2, yCrossPix);

			addStraightLineToGeneralPath(g2, x1, yCrossPix + view.xLabelHeights + 5, x2, y2);

		} else {
			addStraightLineToGeneralPath(g2, x1, y1, x2, y2);
		}
	}

	private static void addStraightLineToGeneralPath(GGraphics2D g2, double x1,
			double y1, double x2, double y2) {
		g2.addStraightLineToGeneralPath(x1, y1, x2, y2);
	}

}
