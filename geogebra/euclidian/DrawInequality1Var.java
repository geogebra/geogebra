package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.arithmetic.Inequality;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * @author kondr
 * 
 */
public class DrawInequality1Var extends Drawable {

	/** ratio of dot radius and line thickness */
	public static final double DOT_RADIUS = 1;
	private Inequality ineq;
	private GeneralPathClipped[] gp;
	private Ellipse2D.Double[] circle;
	private boolean varIsY;

	/**
	 * Creates new drawable inequality
	 * 
	 * @param view
	 * @param geo
	 * @param ineq
	 * @param varIsY
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
	public void draw(Graphics2D g2) {
		if (gp == null)
			return;
		int i = 0;
		while (i < gp.length && gp[i] != null) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				Drawable.drawWithValueStrokePure(gp[i], g2);
			}			

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(EuclidianView.getStroke(geo.lineThickness / 2.0f, ineq
						.getFunBorder().lineType));
				Drawable.drawWithValueStrokePure(gp[i], g2);
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
				Drawable.drawWithValueStrokePure(circle[i], g2);
			}	

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(EuclidianView.getStroke(geo.lineThickness / 2.0f,
						EuclidianView.LINE_TYPE_FULL));
				Drawable.drawWithValueStrokePure(circle[i], g2);
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
	public boolean isInside(Rectangle rect) {
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
			x[0] = view.height + 10;
			int numOfX = 1;
			for (int i = 0; i < roots.length; i++)
				if (roots[i].x < view.toRealWorldCoordY(-10)
						&& roots[i].x > view
								.toRealWorldCoordY(view.height + 10))
					x[numOfX++] = view.toScreenCoordY(roots[i].x);
			x[numOfX++] = -10;
			if(numOfX > 2 && x[numOfX-2]>0 && x[numOfX-2]<view.height)
				yLabel = (int) x[numOfX-2] - 5;
			else
				yLabel = 10;
			xLabel = (int) view.xZero + 6;
			if (gp == null)
				gp = new GeneralPathClipped[numOfX / 2];
			int j = ineq.getFunBorder().evaluate(
					view.toRealWorldCoordY(view.height + 10)) <= 0 ? 1 : 0;
			Area a = new Area();
			for (int i = 0; 2 * i + j + 1 < numOfX; i++) {
				gp[i] = new GeneralPathClipped(view);
				gp[i].moveTo(-10, x[2 * i + j]);
				gp[i].lineTo(view.width + 10, x[2 * i + j]);
				gp[i].lineTo(view.width + 10, x[2 * i + j + 1]);
				gp[i].lineTo(-10, x[2 * i + j + 1]);
				gp[i].lineTo(-10, x[2 * i + j]);
				gp[i].closePath();
				a.add(new Area(gp[i]));
			}
			setShape(a);
		} else {
			GeoPoint[] roots = ineq.getZeros();
			double[] x = new double[roots.length + 2];
			x[0] = -10;
			int numOfX = 1;
			for (int i = 0; i < roots.length; i++)
				if (roots[i].x > view.toRealWorldCoordX(-10)
						&& roots[i].x < view.toRealWorldCoordX(view.width + 10))
					x[numOfX++] = view.toScreenCoordX(roots[i].x);
			x[numOfX++] = view.width + 10;

			if(numOfX > 2 && x[numOfX-2]>0 && x[numOfX-2]<view.height)
				xLabel = (int) x[numOfX-2] - 10;
			else
				xLabel = 10;
			yLabel = (int) view.yZero + 15;
			
			if (gp == null)
				gp = new GeneralPathClipped[numOfX / 2];

			Area a = new Area();
			int circleCount = 0;
			if ((geo instanceof GeoFunction)&&((GeoFunction) geo).showOnAxis()) {
				circle = new Ellipse2D.Double[numOfX];
				for (int i = 0; i < numOfX; i++) {					
					if (x[i] < 0)
						continue;
					if (x[i] > view.width)
						break;
					circle[circleCount] = new Ellipse2D.Double();
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
					gp[i].lineTo(x[2 * i + j], view.height + 10);
					gp[i].lineTo(x[2 * i + j + 1], view.height + 10);
					gp[i].lineTo(x[2 * i + j + 1], -10);
					gp[i].lineTo(x[2 * i + j], -10);
					gp[i].closePath();
					a.add(new Area(gp[i]));
				}
			}
			setShape(a);
		}
		updateStrokes(geo);
	}

}
