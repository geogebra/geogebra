/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Color;
import geogebra.common.awt.Rectangle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.util.Cloner;

import java.util.ArrayList;

/**
 * 
 * @author G.Sturr adapted from DrawPolyLine
 */
public class DrawTurtle extends Drawable {

	private GeoTurtle turtle;
	private boolean isVisible, labelVisible;

	private ArrayList<Object> cmdList;
	private ArrayList<GeneralPathClipped> gpList;

	private double[] coords = new double[2];

	private Rectangle boundRect;

	private double turnAngle = 0.0;

	private geogebra.common.awt.Rectangle turtleImageBounds = geogebra.common.factories.AwtFactory.prototype
			.newRectangle();
	private double imageSize = 10;
	private double[] currentCoords;

	/**
	 * @param view view
	 * @param turtle turtle
	 */
	public DrawTurtle(AbstractEuclidianView view, GeoTurtle turtle) {
		this.view = view;
		this.turtle = turtle;
		geo = turtle;
		turtleImageBounds.setFrame(0, 0, 0, 0);
		update();
	}

	@Override
	final public void update() {

		isVisible = geo.isEuclidianVisible();

		if (isVisible) {
			labelVisible = geo.isLabelVisible();
			updateStrokes(turtle);

			if (cmdList == null) {
				cmdList = new ArrayList<Object>();
			} else {
				cmdList.clear();
			}

			if (gpList == null) {
				gpList = new ArrayList<GeneralPathClipped>();
			} else {
				gpList.clear();
			}

			GeneralPathClipped gp = null;
			boolean penDown = true;
			boolean needsNewPath = true;
			GeoPointND startPoint = turtle.getStartPoint();
			startPoint.getInhomCoords(coords);
			view.toScreenCoords(coords);
			currentCoords = Cloner.clone(coords);
			turnAngle = 0d;

			int ncommands = turtle.getTurtleCommandList().size();
			if (turtle.getSpeed() != 0d) {
				ncommands = turtle.getNumberOfCompletedCommands();
			}

			// Partially process the turtle command list.
			// The turtle command list is converted to a list of drawing style
			// commands and a list of general paths that can be used by the draw
			// method. Iteration stops when ncommands is reached, the current
			// in-progress limit.

			for (int i = 0; i < ncommands; i++) {

				Object cmd = turtle.getTurtleCommandList().get(i);

				if (cmd instanceof Boolean) {
					penDown = (Boolean) cmd;
				}

				if (cmd instanceof Double) {
					turnAngle += (Double) cmd;
				}

				else if (cmd instanceof GeoPointND) {
					if (needsNewPath) {
						gp = new GeneralPathClipped(view);
						// move to start point of path
						addPointToPath(gp, startPoint, false);
						cmdList.add(gp);
						gpList.add(gp);
						needsNewPath = false;
					}
					addPointToPath(gp, (GeoPointND) cmd, penDown);
					startPoint = (GeoPointND) cmd;
				}

				// penColor commands are added here
				// a new general path is needed when color is reset
				else {
					cmdList.add(cmd);
					needsNewPath = true;
				}
			}

			// Handle the next turtle command. Line segments and angles are
			// partially drawn according to the turtle progress field value
			
			if (ncommands < turtle.getTurtleCommandList().size()) {
				Object cmd = turtle.getTurtleCommandList().get(ncommands);
				double progress = turtle.getCurrentCommandProgress();

				if (cmd instanceof Boolean) {
					// TODO
				}

				if (cmd instanceof Double) {
					turnAngle += ((Double) cmd) * progress;
				}

				else if (cmd instanceof GeoPointND) {
					if (needsNewPath) {
						gp = new GeneralPathClipped(view);
						addPointToPath(gp, startPoint, false);
						cmdList.add(gp);
						gpList.add(gp);
					}

					double[] startCoords = new double[2];
					startPoint.getInhomCoords(startCoords);
					double[] endCoords = new double[2];
					((GeoPointND) cmd).getInhomCoords(endCoords);
					coords[0] = startCoords[0] * (1d - progress) + endCoords[0]
							* progress;
					coords[1] = startCoords[1] * (1d - progress) + endCoords[1]
							* progress;
					addPointToPath(gp, penDown);
				}
			}

		}

		turtleImageBounds.setFrame(currentCoords[0] - imageSize / 2,
				currentCoords[1] - imageSize / 2, imageSize, imageSize);

		// turtle path on screen?
		isVisible = false;
		isVisible = getBounds() != null
				&& getBounds().intersects(0, 0, view.getWidth(),
						view.getHeight());

	}

	private void updateCurrentCoords() {
		currentCoords[0] = coords[0];
		currentCoords[1] = coords[1];
	}

	private void addPointToPath(GeneralPathClipped gp, boolean penDown) {

		view.toScreenCoords(coords);

		if (penDown) {
			gp.lineTo(coords[0], coords[1]);
		} else {
			gp.moveTo(coords[0], coords[1]);
		}
		updateCurrentCoords();
	}

	private void addPointToPath(GeneralPathClipped gp, GeoPointND pt,
			boolean penDown) {
		pt.getInhomCoords(coords);
		addPointToPath(gp, penDown);
	}

	@Override
	final public void draw(geogebra.common.awt.Graphics2D g2) {

		//System.out.println("TURTLE isVisible: " + isVisible);

		if (isVisible) {

			// TODO: handle variable line thickness
			g2.setStroke(objStroke);
			g2.setColor(Color.black);

			for (int i = 0; i < cmdList.size(); i++) {
				Object cmd = cmdList.get(i);
				if (cmd instanceof Color) {
					g2.setColor((Color) cmdList.get(i));
				} else if (cmd instanceof GeneralPathClipped) {
					g2.draw((GeneralPathClipped) cmd);
				}
			}

			if (geo.doHighlighting()) {
				g2.setPaint(turtle.getSelColor());
				g2.setStroke(selStroke);
				for (int i = 0; i < cmdList.size(); i++) {
					Object cmd = cmdList.get(i);
					if (cmd instanceof GeneralPathClipped) {
						g2.draw((GeneralPathClipped) cmd);
					}
				}
			}

			if (labelVisible) {
				g2.setPaint(turtle.getLabelColor());
				g2.setFont(view.getFontPoint());
				drawLabel(g2);
			}

			// draw rotated turtle
			AffineTransform tr = g2.getTransform();
			g2.translate(currentCoords[0], currentCoords[1]);
			g2.rotate(-turnAngle * Math.PI / 180);
			drawTurtleShape(g2, turtle.getTurtle(), turtle.getPenColor());
			g2.setTransform(tr);

		}
	}

	@Override
	final public boolean hit(int x, int y) {
		if (isVisible) {
			for (GeneralPathClipped gp : gpList) {
				if (gp.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.Rectangle rect) {
		return cmdList != null && rect.contains(getBounds());
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
	final public geogebra.common.awt.Rectangle getBounds() {

		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}

		boundRect = turtleImageBounds;
		for (GeneralPathClipped gp : gpList) {
			boundRect = boundRect.union(gp.getBounds());
		}

		return boundRect;
	}

	// ===================================================
	// Turtle Shapes
	//
	// TODO: handle images when Common supports loading internal images
	// ===================================================

	private static geogebra.common.awt.Line2D line = geogebra.common.factories.AwtFactory.prototype
			.newLine2D();
	private static geogebra.common.awt.Ellipse2DDouble ellipse = geogebra.common.factories.AwtFactory.prototype
			.newEllipse2DDouble();
	private geogebra.common.awt.Ellipse2DDouble turtleCircle2 = geogebra.common.factories.AwtFactory.prototype
			.newEllipse2DDouble();
	private static geogebra.common.awt.BasicStroke stroke1 = geogebra.common.factories.AwtFactory.prototype
			.newBasicStroke(1f);
	private static geogebra.common.awt.BasicStroke stroke2 = geogebra.common.factories.AwtFactory.prototype
			.newBasicStroke(2f);
	private static geogebra.common.awt.GeneralPath path = geogebra.common.factories.AwtFactory.prototype
			.newGeneralPath();

	/**
	 * Draw turtle shapes.
	 * 
	 * @param g2
	 */
	private static void drawTurtleShape(geogebra.common.awt.Graphics2D g2,
			int shapeNumber, Color penColor) {

		int r = 8; // turtle radius
		float x, y;
		path.reset();

		switch (shapeNumber) {

		case 0: // no turtle is drawn
			break;
			
		case 1: // ellipse body with legs and head

			// back legs
			g2.setStroke(stroke2);
			x = (float) (1.3 * r * Math.cos(Math.PI / 6));
			y = (float) (1.3 * r * Math.sin(Math.PI / 6));
			path.moveTo(0, 0);
			path.lineTo(-x, y);
			path.moveTo(0, 0);
			path.lineTo(-x, -y);
			g2.setColor(Color.black);
			g2.draw(path);

			// front legs
			g2.setStroke(stroke2);
			x = (float) (1.2 * r * Math.cos(Math.PI / 4));
			y = (float) (1.2 * r * Math.sin(Math.PI / 4));
			path.moveTo(0, 0);
			path.lineTo(x, y);
			path.moveTo(0, 0);
			path.lineTo(x, -y);
			g2.setColor(Color.black);
			g2.draw(path);

			g2.setStroke(stroke1);

			// head
			ellipse.setFrame(r - 3, -3, 6, 6);
			g2.setColor(Color.gray);
			g2.fill(ellipse);
			g2.setColor(Color.black);
			g2.draw(ellipse);

			// body
			ellipse.setFrame(-r, -r, 2 * r, 1.8 * r);
			g2.setColor(Color.green);
			g2.fill(ellipse);
			g2.setColor(Color.black);
			g2.draw(ellipse);

			break;

		case 2: // triangle shape

			g2.setStroke(stroke1);

			// body
			ellipse.setFrame(-r, -r, 2 * r, 2 * r);
			g2.setColor(Color.green);
			g2.fill(ellipse);
			g2.setColor(Color.black);
			g2.draw(ellipse);

			// triangle
			x = (float) (r * Math.cos(3 * Math.PI / 4));
			y = (float) (r * Math.sin(3 * Math.PI / 4));
			path.moveTo(r, 0);
			path.lineTo(x, y);
			path.lineTo(-x, -y);
			path.lineTo(r, 0);
			g2.setColor(penColor);
			g2.fill(path);

			break;

		case 3:

			// draw turtle body
			ellipse.setFrame(-r, -r, 2 * r, 2 * r);
			g2.setColor(Color.green);
			g2.fill(ellipse);

			g2.setColor(Color.black);
			g2.draw(ellipse);

			// draw orientation line
			g2.setStroke(stroke1);
			g2.setColor(Color.black);
			line.setLine(0, 0, r + 3, 0);
			g2.draw(line);
			g2.setStroke(stroke2);

			// small dot in center colored with current pencolor
			ellipse.setFrame(-2, -2, 4, 4);
			g2.setColor(penColor);
			g2.fill(ellipse);

		}
	}
}
