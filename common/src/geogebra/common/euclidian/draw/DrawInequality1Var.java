package geogebra.common.euclidian.draw;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.arithmetic.Inequality;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.plugin.EuclidianStyleConstants;


/**
 * @author kondr
 * 
 */
public class DrawInequality1Var extends Drawable {

	/** ratio of dot radius and line thickness */
	public static final double DOT_RADIUS = 1;
	private Inequality ineq;
	private GeneralPathClipped[] gp;
	private geogebra.common.awt.GEllipse2DDouble[] circle;
	private boolean varIsY;

	/**
	 * Creates new drawable inequality
	 * 
	 * @param view view
	 * @param geo the top-level function (e.g x>1 && x<3)
	 * @param ineq inequality
	 * @param varIsY true if this is inequality in Y
	 */
	public DrawInequality1Var(Inequality ineq, EuclidianView view,
			GeoElement geo, boolean varIsY) {
		super();
		this.ineq = ineq;
		this.geo = geo;
		this.view = view;
		this.varIsY = varIsY;

	}

	@Override
	public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (gp == null)
			return;
		int i = 0;
		while (i < gp.length && gp[i] != null) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(gp[i], g2);
			}			

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(EuclidianStatic.getStroke(geo.lineThickness / 2.0f, ((GeoElement)ineq
						.getFunBorder()).lineType));
				EuclidianStatic.drawWithValueStrokePure(gp[i], g2);
			}

			// TODO: draw label
			i++;
		}
		if (circle == null)
			return;
		while (i < circle.length && circle[i] != null) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(circle[i], g2);
			}	

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(EuclidianStatic.getStroke(geo.lineThickness / 2.0f,
						EuclidianStyleConstants.LINE_TYPE_FULL));
				EuclidianStatic.drawWithValueStrokePure(circle[i], g2);
				if (!ineq.isStrict()) {
					g2.fill(circle[i]);
				}
			}

			// TODO: draw label
			i++;
		}

	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		for (int i = 0; i < gp.length; i++)
			if (gp[i] != null && gp[i].contains(x, y))
				return true;
		return false;
	}

	@Override
	public boolean isInside(geogebra.common.awt.GRectangle rect) {
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
			GeoPoint[] roots = ineq.getZeros();
			double[] x = new double[roots.length + 2];
			x[0] = view.getHeight() + 10;
			int numOfX = 1;
			for (int i = 0; i < roots.length; i++)
				if (roots[i].x < view.toRealWorldCoordY(-10)
						&& roots[i].x > view
								.toRealWorldCoordY(view.getHeight() + 10))
					x[numOfX++] = view.toScreenCoordY(roots[i].x);
			x[numOfX++] = -10;
			if(numOfX > 2 && x[numOfX-2]>0 && x[numOfX-2]<view.getHeight())
				yLabel = (int) x[numOfX-2] - 5;
			else
				yLabel = 10;
			xLabel = (int) view.getxZero() + 6;
			if (gp == null)
				gp = new GeneralPathClipped[numOfX / 2];
			int j = ineq.getFunBorder().evaluate(
					view.toRealWorldCoordY(view.getHeight() + 10)) <= 0 ? 1 : 0;
			geogebra.common.awt.GArea a = 
					geogebra.common.factories.AwtFactory.prototype.newArea();
			for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
				gp[i] = new GeneralPathClipped(view);
				gp[i].moveTo(-10, x[2 * i + j]);
				gp[i].lineTo(view.getWidth() + 10, x[2 * i + j]);
				gp[i].lineTo(view.getWidth() + 10, x[2 * i + j + 1]);
				gp[i].lineTo(-10, x[2 * i + j + 1]);
				gp[i].lineTo(-10, x[2 * i + j]);
				gp[i].closePath();
				a.add(geogebra.common.factories.AwtFactory.prototype.newArea(gp[i]));
			}
			setShape(a);
		} else {
			GeoPoint[] roots = ineq.getZeros();
			double[] x = new double[roots.length + 2];
			x[0] = -10;
			int numOfX = 1;
			for (int i = 0; i < roots.length; i++)
				if (roots[i].x > view.toRealWorldCoordX(-10)
						&& roots[i].x < view.toRealWorldCoordX(view.getWidth() + 10))
					x[numOfX++] = view.toScreenCoordX(roots[i].x);
			x[numOfX++] = view.getWidth() + 10;

			if(numOfX > 2 && x[numOfX-2]>0 && x[numOfX-2]<view.getHeight())
				xLabel = (int) x[numOfX-2] - 10;
			else
				xLabel = 10;
			yLabel = (int) view.getyZero() + 15;
			
			if (gp == null)
				gp = new GeneralPathClipped[numOfX / 2];

			geogebra.common.awt.GArea a = geogebra.common.factories.AwtFactory.prototype.newArea();
			int circleCount = 0;
			if ((geo instanceof GeoFunction)&&((GeoFunction) geo).showOnAxis()) {
				circle = new geogebra.common.awt.GEllipse2DDouble[numOfX];
				for (int i = 0; i < numOfX; i++) {					
					if (x[i] < 0)
						continue;
					if (x[i] > view.getWidth())
						break;
					circle[circleCount] = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble();
					double radius = geo.getLineThickness() * DOT_RADIUS;
					circle[circleCount].setFrame(x[i] - radius, view.toScreenCoordY(0)
							- radius, 2 * radius, 2 * radius);
					circleCount++;
				}
			} else {
				int j = ineq.getFunBorder().evaluate(
						view.toRealWorldCoordX(-10)) <= 0 ? 1 : 0;

				for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
					gp[i] = new GeneralPathClipped(view);
					gp[i].moveTo(x[2 * i + j], -10);
					gp[i].lineTo(x[2 * i + j], view.getHeight() + 10);
					gp[i].lineTo(x[2 * i + j + 1], view.getHeight() + 10);
					gp[i].lineTo(x[2 * i + j + 1], -10);
					gp[i].lineTo(x[2 * i + j], -10);
					gp[i].closePath();
					a.add(geogebra.common.factories.AwtFactory.prototype.newArea(gp[i]));
				}
			}
			setShape(a);
		}
		updateStrokes(geo);
	}

}
