/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author G.Sturr adapted from DrawPolyLine
 */
public class DrawTurtle extends Drawable {
	/** turtle */
	protected GeoTurtle turtle;
	private boolean isVisible;
	private boolean labelVisible;
	/** list of paths */
	protected ArrayList<PartialPath> pathList;

	private GRectangle boundRect;

	private double turnAngle = 0.0;

	private GRectangle turtleImageBounds = AwtFactory.getPrototype()
			.newRectangle();
	private double imageSize = 10;
	private double[] currentCoords = new double[2];
	private GAffineTransform at = AwtFactory.getPrototype()
			.newAffineTransform();
	// ===================================================
	// Turtle Shapes
	//
	// TODO: handle images when Common supports loading internal images
	// ===================================================

	private GEllipse2DDouble ellipse = AwtFactory.getPrototype()
			.newEllipse2DDouble();
	private GBasicStroke stroke1 = AwtFactory.getPrototype().newBasicStroke(1f);
	private GBasicStroke stroke2 = AwtFactory.getPrototype().newBasicStroke(2f);
	private GGeneralPath gPath = AwtFactory.getPrototype().newGeneralPath();
	private GShape legs;
	private GShape head;
	private GShape body;
	private GShape dot;

	/**
	 * @param view
	 *            view
	 * @param turtle
	 *            turtle
	 */
	public DrawTurtle(EuclidianView view, GeoTurtle turtle) {
		this.view = view;
		this.turtle = turtle;
		geo = turtle;
		turtleImageBounds.setFrame(0, 0, 0, 0);
		update();
		turtle.setCoords(turtle.inhomX, turtle.inhomY);
	}

	private static class PartialPath {
		public GColor color;
		public int thickness;
		public GeneralPathClipped path1;
		private GBasicStroke stroke;

		public PartialPath(GColor c, int th, GeneralPathClipped p) {
			color = c;
			thickness = th;
			path1 = p;
			stroke = AwtFactory.getPrototype().newBasicStroke(thickness);
		}

		public void draw(GGraphics2D g2) {
			g2.setColor(color);
			g2.setStroke(stroke);
			g2.draw(path1);
		}
	}

	private class DrawState implements GeoTurtle.DrawState {
		private boolean penDown = true;
		private GColor penColor = GColor.BLACK;
		private int penThickness = 1;
		private int nlines = 0;
		double turnAngle1 = 0d;
		private GeneralPathClipped currentPath;
		// private GeoPointND currentPosition = turtle.getStartPoint();
		double[] coords = new double[2];

		public DrawState() {
			currentPath = new GeneralPathClipped(getView());
			currentPath.resetWithThickness(geo.getLineThickness());
			penDown = false;
			move(turtle.getStartPoint());
			penDown = true;
			nlines = 0;
		}

		@Override
		public void setPen(boolean down) {
			penDown = down;
		}

		@Override
		public void move(GeoPointND newPosition) {
			newPosition.getInhomCoords(coords);
			getView().toScreenCoords(coords);
			if (penDown) {
				currentPath.lineTo(coords[0], coords[1]);
				nlines += 1;
			} else {
				currentPath.moveTo(coords[0], coords[1]);
			}
		}

		@Override
		public void partialMove(GeoPointND newPosition, double progress) {
			double[] newCoords = new double[2];
			newPosition.getInhomCoords(newCoords);
			getView().toScreenCoords(newCoords);
			coords[0] = coords[0] * (1d - progress) + newCoords[0] * progress;
			coords[1] = coords[1] * (1d - progress) + newCoords[1] * progress;
			if (penDown) {
				currentPath.lineTo(coords[0], coords[1]);
				nlines += 1;
			} else {
				currentPath.moveTo(coords[0], coords[1]);
			}
		}

		@Override
		public void turn(double angle) {
			turnAngle1 += angle;
		}

		@Override
		public void partialTurn(double angle, double progress) {
			turnAngle1 += angle * progress;
		}

		@Override
		public void setColor(GColor color) {
			if (penColor != color) {
				finishPartialPath();
				penColor = color;
			}
		}

		@Override
		public void setThickness(int thickness) {
			if (penThickness != thickness) {
				finishPartialPath();
				penThickness = thickness;
			}
		}

		public void finishPartialPath() {
			if (nlines > 0) {
				pathList.add(
						new PartialPath(penColor, penThickness, currentPath));
			}
			currentPath = new GeneralPathClipped(getView());
			currentPath.resetWithThickness(geo.getLineThickness());
			currentPath.moveTo(coords[0], coords[1]);
		}
	}

	@Override
	final public void update() {

		isVisible = geo.isEuclidianVisible();

		if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes(turtle);

			if (pathList == null) {
				pathList = new ArrayList<>();
			} else {
				pathList.clear();
			}
			DrawState ds = new DrawState();
			int ncommands = turtle.getTurtleCommandList().size();
			if (turtle.getSpeed() != 0d) {
				ncommands = turtle.getNumberOfCompletedCommands();
			}

			// Partially process the turtle command list.
			// The turtle command list is converted to a list of partial paths
			// which know how to draw themselves on a Graphic2D.
			// Iteration stops when ncommands is reached, the current
			// in-progress limit.

			for (GeoTurtle.TurtleCommand cmd : turtle.getTurtleCommandList()) {
				if (ncommands-- > 0) {
					cmd.draw(ds);
				} else {
					cmd.partialDraw(ds, turtle.getCurrentCommandProgress());
					break;
				}
			}
			ds.finishPartialPath();
			currentCoords[0] = ds.coords[0];
			currentCoords[1] = ds.coords[1];
			turnAngle = ds.turnAngle1;
		}

		turtleImageBounds.setFrame(currentCoords[0] - imageSize / 2,
				currentCoords[1] - imageSize / 2, imageSize, imageSize);

		// turtle path on screen?
		isVisible = false;
		GRectangle bounds = getBounds();
		isVisible = bounds != null && bounds.intersects(0, 0,
				view.getWidth(), view.getHeight());
		if (isVisible) {
			at.setTransform(1, 0, 0, 1, 1, 0);
			at.translate(currentCoords[0], currentCoords[1]);
			at.rotate(-turnAngle);
			if (geo.getFillImage() == null) {
				updateTurtleShape();
			}
		}
	}

	@Override
	final public void draw(GGraphics2D g2) {

		if (isVisible) {

			// TODO: handle variable line thickness
			g2.setStroke(objStroke);

			for (PartialPath path : pathList) {
				path.draw(g2);
			}

			if (isHighlighted()) {
				g2.setPaint(turtle.getSelColor());
				g2.setStroke(selStroke);
				for (PartialPath path : pathList) {
					g2.draw(path.path1);
				}
			}

			if (labelVisible) {
				g2.setPaint(turtle.getLabelColor());
				g2.setFont(view.getFontPoint());
				drawLabel(g2);
			}

			// draw turtle
			if (turtle.getFillImage() != null) {
				int imgWidth = turtle.getFillImage().getWidth();
				int imgHeight = turtle.getFillImage().getHeight();
				g2.saveTransform();
				g2.transform(at);
				// temp - until x,y parameters won't be used in drawImage on
				// desktop for SVG images
				if (turtle.getFillImage().isSVG()
						&& !turtle.kernel.getApplication().isHTML5Applet()) {
					g2.translate(-imgWidth / 2.0, -imgHeight / 2.0);
				}
				g2.drawImage(turtle.getFillImage(), -imgWidth / 2,
						-imgHeight / 2);
				g2.restoreTransform();
			} else {
				// draw rotated turtle
				drawTurtleShape(g2);
			}

		}
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		if (isVisible) {
			for (PartialPath path : pathList) {
				if (path.path1.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return pathList != null && rect.contains(getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (isVisible) {
			for (PartialPath p : pathList) {
				if (p.path1.intersects(rect)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {

		if (!geo.isDefined() || !geo.isEuclidianVisible()
				|| turtleImageBounds == null) {
			return null;
		}

		boundRect = turtleImageBounds;
		for (PartialPath path : pathList) {
			boundRect = boundRect.union(path.path1.getBounds());
		}

		return boundRect;
	}

	private void drawTurtleShape(GGraphics2D g2) {
		gPath.reset();

		// back legs
		g2.setStroke(stroke2);

		g2.setColor(GColor.BLACK);

		g2.draw(legs);

		// front legs

		g2.setStroke(stroke1);

		// head
		g2.setColor(GColor.GRAY);
		g2.fill(head);
		g2.setColor(GColor.BLACK);
		g2.draw(head);

		// body
		g2.setColor(GColor.GREEN);
		g2.fill(body);
		g2.setColor(GColor.BLACK);
		g2.draw(body);

		// pen color dot
		g2.setColor(turtle.getPenColor());
		g2.fill(dot);
		// g2.setColor(Color.black);
		// g2.draw(ellipse);
	}

	private void updateTurtleShape() {
		int r = 8; // turtle radius
		double x, y;
		gPath.reset();

		// back legs
		x = (1.3 * r * Math.cos(Math.PI / 6));
		y = (1.3 * r * Math.sin(Math.PI / 6));
		gPath.moveTo(0, 0);
		gPath.lineTo(-x, y);
		gPath.moveTo(0, 0);
		gPath.lineTo(-x, -y);

		// front legs
		x = (1.2 * r * Math.cos(Math.PI / 4));
		y = (1.2 * r * Math.sin(Math.PI / 4));
		gPath.moveTo(0, 0);
		gPath.lineTo(x, y);
		gPath.moveTo(0, 0);
		gPath.lineTo(x, -y);
		legs = gPath.createTransformedShape(at);

		// head
		ellipse.setFrame(r - 3, -3, 6, 6);
		head = at.createTransformedShape(ellipse);
		// body
		ellipse.setFrame(-r, -r, 2 * r, 1.8 * r);
		body = at.createTransformedShape(ellipse);

		// pen color dot
		ellipse.setFrame(-3, -3, 6, 6);
		dot = at.createTransformedShape(ellipse);
	}

}
