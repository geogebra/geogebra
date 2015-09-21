package org.geogebra.common.euclidian.draw;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoTransferFunction;

/**
 * @author Giuliano
 * 
 *         Draw a Nyquist diagram
 * 
 */
public class DrawNyquist extends Drawable {

	private GeoTransferFunction gcf;

	private boolean isVisible;

	private GeneralPathClipped gpP;

	private List<Coords> coordsList;

	private Coords p;

	private double yP;

	private double xPN;

	private GeneralPathClipped gpN;

	private double yN;

	private static final double LEN = 6;
	private static final double ANGLE_L = Math.toRadians(135);
	private static final double ANGLE_R = -ANGLE_L;

	/**
	 * @param view
	 *            Euclidian view
	 * @param geo
	 *            Function
	 */
	public DrawNyquist(EuclidianView view, GeoTransferFunction geo) {
		gcf = geo;
		this.geo = geo;
		this.view = view;
		labelDesc = geo.getLabelSimple();
		coordsList = gcf.getCoordsList();
		update();
	}

	@Override
	public void update() {
		isVisible = gcf.isEuclidianVisible();
		if (!isVisible || !gcf.isDefined()) {
			return;
		}

	}

	@Override
	public void draw(GGraphics2D g2) {

		if (!isVisible || !gcf.isDefined()) {
			return;
		}
		boolean highlighting = geo.doHighlighting();

		if (highlighting) {
			g2.setPaint(geo.getSelColor());
			g2.setStroke(selStroke);
		} else {
			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
		}
		gpP = new GeneralPathClipped(view);
		gpN = new GeneralPathClipped(view);
		for (int i = 0; i < coordsList.size(); i++) {
			p = coordsList.get(i);
			xPN = view.toScreenCoordXd(p.getX());
			yP = view.toScreenCoordYd(p.getY());
			yN = view.toScreenCoordYd(-p.getY());
			gpP.lineTo(xPN, yP);
			gpN.lineTo(xPN, yN);
		}
		GColor geoColor = getObjectColor();
		GColor color = AwtFactory.prototype.newColor(geoColor.getRed(),
				geoColor.getGreen(), geoColor.getBlue(), 127);
		g2.setColor(color);
		g2.drawWithValueStrokePure(gpN);
		g2.setColor(geoColor);
		g2.drawWithValueStrokePure(gpP);
		drawArrow(g2);
	}

	private void drawArrow(GGraphics2D g2) {
		int i = (int) (coordsList.size() / 2.3);
		double x1 = view.toScreenCoordXd(coordsList.get(i).getX());
		double y1 = view.toScreenCoordYd(coordsList.get(i).getY());
		double y2 = view.toScreenCoordYd(coordsList.get(i - 1).getY());
		double x2 = view.toScreenCoordXd(coordsList.get(i - 1).getX());
		double angle = getAngle(x1, y1, x2, y2);
		GColor color = g2.getColor();
		g2.setColor(GColor.BLUE);
		fill(g2, y2, x2, angle);
		y1 = view.toScreenCoordYd(-coordsList.get(i).getY());
		y2 = view.toScreenCoordYd(-coordsList.get(i + 1).getY());
		x2 = view.toScreenCoordXd(coordsList.get(i + 1).getX());
		angle = getAngle(x1, y1, x2, y2);
		fill(g2, y2, x2, angle);
		g2.setColor(color);
	}

	private void fill(GGraphics2D g2, double y2, double x2, double angle) {
		GeneralPathClipped arrow = new GeneralPathClipped(view);
		arrow.moveTo(x2, y2);
		arrow.lineTo(x2 + LEN * Math.cos(angle + ANGLE_L),
				y2 + LEN * Math.sin(angle + ANGLE_L));
		arrow.lineTo(x2 + LEN * Math.cos(angle + ANGLE_R),
				y2 + LEN * Math.sin(angle + ANGLE_R));
		arrow.closePath();
		g2.fill(arrow);
	}

	private static double getAngle(double x1, double y1, double x2, double y2) {
		double m = (y2 - y1) / (x2 - x1);
		if (x2 - x1 < 0 && y2 - y1 < 0) {
			return Math.atan(m) + Math.PI;
		} else if (x2 - x1 < 0 && y2 - y1 >= 0) {
			return Math.atan(m) + Math.PI;
		} else if (x2 - x1 >= 0 && y2 - y1 < 0) {
			return 2 * Math.PI + Math.atan(m);
		} else if (x2 - x1 >= 0 && y2 - y1 >= 0) {
			return Math.atan(m);
		}
		return 0;
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (!isVisible || !gcf.isDefined()) {
			return false;
		}
		return gpP.contains(x, y) || gpN.contains(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		if (!isVisible || !gcf.isDefined()) {
			return false;
		}
		return gpP.contains(rect) || gpN.contains(rect);
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

}
