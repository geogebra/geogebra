package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.ExtendedBoolean;

/**
 * @author Zbynek
 * 
 */
public class DrawInequality1Var extends SetDrawable {

	/** ratio of dot radius and line thickness */
	public static final double DOT_RADIUS = 1;
	private final IneqTree parentTree;
	private Inequality ineq;
	private GeneralPathClipped[] gp;
	private GLine2D[] lines;
	private GEllipse2DDouble[] circle;
	private boolean varIsY;

	/**
	 * Creates new drawable inequality
	 * 
	 * @param view
	 *            view
	 * @param geo
	 *            the top-level function (e.g. x &gt; 1 &amp;&amp; x &lt; 3)
	 * @param ineq
	 *            inequality
	 * @param varIsY
	 *            true if this is inequality in Y
	 */
	public DrawInequality1Var(Inequality ineq, EuclidianView view,
			GeoElement geo, boolean varIsY) {
		super();
		this.ineq = ineq;
		this.geo = geo;
		this.view = view;
		this.varIsY = varIsY;
		this.parentTree = geo instanceof GeoFunction ? ((GeoFunction) geo).getIneqs() : null;
	}

	@Override
	public void draw(GGraphics2D g2) {
		for (GLine2D line: lines) {
			if (line == null) {
				continue;
			}
			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(line);
			}

			if (geo.getLineThickness() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(
						EuclidianStatic.getStroke(geo.getLineThickness() / 2.0f,
								ineq.getFunBorder().lineType));
				g2.draw(line);
			}
		}
		if (circle == null) {
			return;
		}
		for (GEllipse2DDouble ellipse: circle) {
			if (ellipse == null) {
				continue;
			}
			if (isHighlighted()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				g2.draw(ellipse);
			}

			if (geo.getLineThickness() > 0) {
				g2.setPaint(getObjectColor());
				g2.setStroke(
						EuclidianStatic.getStroke(geo.getLineThickness() / 2.0f,
								EuclidianStyleConstants.LINE_TYPE_FULL));
				g2.draw(ellipse);
				if (!ineq.isStrict()) {
					g2.fill(ellipse);
				}
			}
		}

	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		for (int i = 0; i < gp.length; i++) {
			if (gp[i] != null && gp[i].contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public void update() {
		// get x-coords of the lines
		if (varIsY) {
			ArrayList<Double> roots = ineq.getZeros();
			double[] y = new double[roots.size() + 2];
			double[] yRW = new double[roots.size() + 2];
			y[0] = view.getHeight() + 10;
			int numOfX = 1;
			for (Double root : roots) {
				if (root < view.toRealWorldCoordY(-10)
						&& root > view.toRealWorldCoordY(view.getHeight() + 10)) {
					yRW[numOfX] = root;
					y[numOfX++] = view.toScreenCoordY(root);
				}
			}
			y[numOfX++] = -10;
			if (numOfX > 2 && y[numOfX - 2] > 0
					&& y[numOfX - 2] < view.getHeight()) {
				yLabel = (int) y[numOfX - 2] - 5;
			} else {
				yLabel = 10;
			}
			xLabel = (int) view.getXZero() + 6;
			initGP(numOfX);
			int j = ineq.getFunBorder().value(
					view.toRealWorldCoordY(view.getHeight() + 10)) <= 0 ? 1 : 0;
			GArea a = AwtFactory.getPrototype().newArea();
			for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
				gp[i] = new GeneralPathClipped(view);
				gp[i].resetWithThickness(geo.getLineThickness());
				gp[i].moveTo(-10, y[2 * i + j]);
				gp[i].lineTo(view.getWidth() + 10, y[2 * i + j]);
				gp[i].lineTo(view.getWidth() + 10, y[2 * i + j + 1]);
				gp[i].lineTo(-10, y[2 * i + j + 1]);
				gp[i].lineTo(-10, y[2 * i + j]);
				gp[i].closePath();
				lines[2 * i] = horizontalLine(yRW[2 * i + j], y[2 * i + j]);
				lines[2 * i + 1] = horizontalLine(yRW[2 * i + j + 1], y[2 * i + j + 1]);
				a.add(AwtFactory.getPrototype().newArea(gp[i]));
			}
			setShape(a);
		} else {
			ArrayList<Double> roots = ineq.getZeros();
			double[] x = new double[roots.size() + 2];
			double[] xRW = new double[roots.size() + 2];
			x[0] = -10;
			xRW[0] = view.getXmin() - 1;
			int numOfX = 1;
			for (Double root : roots) {
				if (root > view.toRealWorldCoordX(-10)
						&& root < view.toRealWorldCoordX(view.getWidth() + 10)) {
					xRW[numOfX] = root;
					x[numOfX++] = view.toScreenCoordX(root);
				}
			}
			xRW[numOfX] = view.getXmax() + 1;
			x[numOfX++] = view.getWidth() + 10;

			if (numOfX > 2 && x[numOfX - 2] > 0
					&& x[numOfX - 2] < view.getHeight()) {
				xLabel = (int) x[numOfX - 2] - 10;
			} else {
				xLabel = 10;
			}
			yLabel = (int) view.getYZero() + 15;

			initGP(numOfX);

			GArea a = AwtFactory.getPrototype().newArea();
			int circleCount = 0;
			if ((geo instanceof GeoFunction)
					&& ((GeoFunction) geo).showOnAxis()) {
				circle = new GEllipse2DDouble[numOfX];
				for (int i = 0; i < numOfX; i++) {
					if (x[i] < 0) {
						continue;
					}
					if (x[i] > view.getWidth()) {
						break;
					}
					if (!isVisibleRoot(xRW[i])) {
						break;
					}
					circle[circleCount] = AwtFactory.getPrototype()
							.newEllipse2DDouble();
					double radius = geo.getLineThickness() * DOT_RADIUS;
					circle[circleCount].setFrame(x[i] - radius,
							view.toScreenCoordY(0) - radius, 2 * radius,
							2 * radius);
					circleCount++;
				}
			} else {
				int j = ineq.getFunBorder()
						.value(view.toRealWorldCoordX(-10)) <= 0 ? 1 : 0;

				for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
					gp[i] = new GeneralPathClipped(view);
					gp[i].resetWithThickness(geo.getLineThickness());
					gp[i].moveTo(x[2 * i + j], -10);
					gp[i].lineTo(x[2 * i + j], view.getHeight() + 10);
					gp[i].lineTo(x[2 * i + j + 1], view.getHeight() + 10);
					gp[i].lineTo(x[2 * i + j + 1], -10);
					gp[i].lineTo(x[2 * i + j], -10);
					gp[i].closePath();
					lines[2 * i] = verticalLine(xRW[2 * i + j], x[2 * i + j]);
					lines[2 * i + 1] = verticalLine(xRW[2 * i + 1 + j], x[2 * i + 1 + j]);
					a.add(AwtFactory.getPrototype().newArea(gp[i]));
				}
			}
			setShape(a);
		}
		updateStrokes(geo);
	}

	private GLine2D verticalLine(double xRW, double x) {
		if (isVisibleRoot(xRW)) {
			GLine2D ret = AwtFactory.getPrototype().newLine2D();
			ret.setLine(x, -10, x, view.getHeight() + 10);
			return ret;
		} else {
			return null;
		}
	}

	private GLine2D horizontalLine(double yRW, double y) {
		if (isVisibleRoot(yRW)) {
			GLine2D ret = AwtFactory.getPrototype().newLine2D();
			ret.setLine(-10, y, view.getWidth() + 10, y);
			return ret;
		} else {
			return null;
		}
	}

	private boolean isVisibleRoot(double val) {
		return parentTree == null
				|| parentTree.valueAround(val, 0) == ExtendedBoolean.UNKNOWN;
	}

	private void initGP(int numOfX) {
		if (gp == null) {
			gp = new GeneralPathClipped[numOfX / 2];
			lines = new GLine2D[numOfX];
		}
	}

	/**
	 * @return inequality
	 */
	public Inequality getIneq() {
		return this.ineq;
	}
}
