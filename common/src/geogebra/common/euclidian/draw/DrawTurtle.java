/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GEllipse2DDouble;
import geogebra.common.awt.GGeneralPath;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GImage;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.kernel.kernelND.GeoPointND;

import java.util.ArrayList;

/**
 * 
 * @author G.Sturr adapted from DrawPolyLine
 */
public class DrawTurtle extends Drawable {
	/** turtle */
	protected GeoTurtle turtle;
	private boolean isVisible, labelVisible;
	/** list of paths */
	protected ArrayList<PartialPath> pathList;
	
	private GRectangle boundRect;

	private double turnAngle = 0.0;

	private geogebra.common.awt.GRectangle turtleImageBounds = AwtFactory.prototype.newRectangle();
	private double imageSize = 10;
	private double[] currentCoords = new double[2];

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
			stroke =  AwtFactory.prototype.newBasicStroke(thickness);
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
		double coords[] = new double[2];
		
		public DrawState() {
			currentPath = new GeneralPathClipped(getView());
			penDown = false;
			move(turtle.getStartPoint());
			penDown = true;
			nlines = 0;
		}
		
		public void setPen(boolean down) {
			penDown = down;
		}
		
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
		
		public void partialMove(GeoPointND newPosition, double progress) {
			double[] newCoords = new double[2];
			newPosition.getInhomCoords(newCoords);
			getView().toScreenCoords(newCoords);
			coords[0] = coords[0]*(1d - progress) + newCoords[0]*progress;
			coords[1] = coords[1]*(1d - progress) + newCoords[1]*progress;
			if (penDown) {
				currentPath.lineTo(coords[0], coords[1]);
				nlines += 1;
			} else {
				currentPath.moveTo(coords[0], coords[1]);
			}
		}
		
		public void turn(double angle) {
			turnAngle1 += angle;
		}
		
		public void partialTurn(double angle, double progress) {
			turnAngle1 += angle*progress;
		}
		
		public void setColor(GColor color) {
			if (penColor != color) {
				finishPartialPath();
				penColor = color;
			}
		}
		
		public void setThickness(int thickness) {
			if (penThickness != thickness) {
				finishPartialPath();
				penThickness = thickness;
			}
		}
		
		public void finishPartialPath() {
			if (nlines > 0) {
				pathList.add(new PartialPath(penColor, penThickness, currentPath));
			}
			currentPath = new GeneralPathClipped(getView());
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
				pathList = new ArrayList<PartialPath>();
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
			
			for (GeoTurtle.Command cmd : turtle.getTurtleCommandList()) {
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
		isVisible = getBounds() != null
				&& getBounds().intersects(0, 0, view.getWidth(),
						view.getHeight());

	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {

		if (isVisible) {

			// TODO: handle variable line thickness
			g2.setStroke(objStroke);
			
			for (PartialPath path : pathList) {
				path.draw(g2);
			}

			if (geo.doHighlighting()) {
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

			// draw rotated turtle
			GAffineTransform tr = g2.getTransform();
			g2.translate(currentCoords[0], currentCoords[1]);
			g2.rotate(-turnAngle);
			drawTurtleShape(g2, turtle.getTurtle(), turtle.getPenColor());
			g2.setTransform(tr);

		}
	}

	@Override
	final public boolean hit(int x, int y) {
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
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
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

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {

		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}

		boundRect = turtleImageBounds;
		for (PartialPath path : pathList) {
			boundRect = boundRect.union(path.path1.getBounds());
		}

		return boundRect;
	}

	// ===================================================
	// Turtle Shapes
	//
	// TODO: handle images when Common supports loading internal images
	// ===================================================

	private static GEllipse2DDouble ellipse = AwtFactory.prototype.newEllipse2DDouble();
	private static GBasicStroke stroke1 = AwtFactory.prototype.newBasicStroke(1f);
	private static GBasicStroke stroke2 = AwtFactory.prototype.newBasicStroke(2f);
	private static GGeneralPath gPath = AwtFactory.prototype.newGeneralPath();

	/**
	 * Draw turtle shapes.
	 * 
	 * @param g2
	 */
	private void drawTurtleShape(geogebra.common.awt.GGraphics2D g2,
			int shapeNumber, GColor penColor) {

		int r = 8; // turtle radius
		float x, y;
		gPath.reset();

		switch (shapeNumber) {

		case 0: // no turtle is drawn
			break;

		case 1: // ellipse body with legs and head

			// back legs
			g2.setStroke(stroke2);
			x = (float) (1.3 * r * Math.cos(Math.PI / 6));
			y = (float) (1.3 * r * Math.sin(Math.PI / 6));
			gPath.moveTo(0, 0);
			gPath.lineTo(-x, y);
			gPath.moveTo(0, 0);
			gPath.lineTo(-x, -y);
			g2.setColor(GColor.black);
			g2.draw(gPath);

			// front legs
			g2.setStroke(stroke2);
			x = (float) (1.2 * r * Math.cos(Math.PI / 4));
			y = (float) (1.2 * r * Math.sin(Math.PI / 4));
			gPath.moveTo(0, 0);
			gPath.lineTo(x, y);
			gPath.moveTo(0, 0);
			gPath.lineTo(x, -y);
			g2.setColor(GColor.black);
			g2.draw(gPath);

			g2.setStroke(stroke1);

			// head
			ellipse.setFrame(r - 3, -3, 6, 6);
			g2.setColor(GColor.gray);
			g2.fill(ellipse);
			g2.setColor(GColor.black);
			g2.draw(ellipse);

			// body
			ellipse.setFrame(-r, -r, 2 * r, 1.8 * r);
			g2.setColor(GColor.green);
			g2.fill(ellipse);
			g2.setColor(GColor.black);
			g2.draw(ellipse);
			
			// pen color dot
			ellipse.setFrame(-3, -3, 6, 6);
			g2.setColor(turtle.getPenColor());
			g2.fill(ellipse);
			//g2.setColor(Color.black);
			//g2.draw(ellipse);

			break;

		case 2: // triangle shape

			g2.setStroke(stroke1);

			// body
			ellipse.setFrame(-r, -r, 2 * r, 2 * r);
			g2.setColor(GColor.green);
			g2.fill(ellipse);
			g2.setColor(GColor.black);
			g2.draw(ellipse);

			// triangle
			x = (float) (r * Math.cos(2*Math.PI / 3));
			y = (float) (r * Math.sin(2*Math.PI / 3));
			gPath.moveTo(r, 0);
			gPath.lineTo(x, y);
			gPath.lineTo(x, -y);
			gPath.lineTo(r, 0);
			g2.setColor(penColor);
			g2.fill(gPath);

			break;

		case 3:

			GImage img = turtle.getTurtleImageList().get(0);
			g2.drawImage(img, -8, -8);

		}
	}
}
