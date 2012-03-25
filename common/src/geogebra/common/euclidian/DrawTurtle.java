/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Color;
import geogebra.common.awt.Rectangle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTurtle;
import geogebra.common.kernel.kernelND.GeoPointND;

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

	/**
	 * @param view
	 * @param turtle
	 */
	public DrawTurtle(AbstractEuclidianView view, GeoTurtle turtle) {
		this.view = view;
		this.turtle = turtle;
		geo = turtle;

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

			for (int i = 0; i < turtle.getTurtleCommandList().size(); i++) {

				Object cmd = turtle.getTurtleCommandList().get(i);

				if (cmd instanceof Boolean) {
					penDown = (Boolean) cmd;
				}

				if (cmd instanceof Double) {
					turnAngle = (Double) cmd;
				}

				else if (cmd instanceof GeoPointND) {
					if (needsNewPath) {
						gp = new GeneralPathClipped(view);
						// mode to start point of path
						addPointToPath(gp, startPoint, false);
						cmdList.add(gp);
						gpList.add(gp);
						needsNewPath = false;
					}
					addPointToPath(gp, (GeoPointND) cmd, penDown);
					startPoint = (GeoPointND) cmd;

				} else {
					cmdList.add(cmd);
					needsNewPath = true;
				}
			}

		}

		// get bounds
		for (GeneralPathClipped path : gpList) {
			if (boundRect == null)
				boundRect = path.getBounds();
			else
				boundRect = boundRect.union(path.getBounds());
		}

		// turtle path on screen?
		isVisible = false;
		isVisible = boundRect != null
				&& boundRect
						.intersects(0, 0, view.getWidth(), view.getHeight());

	}

	private void addPointToPath(GeneralPathClipped gp, GeoPointND pt,
			boolean penDown) {

		pt.getInhomCoords(coords);
		view.toScreenCoords(coords);

		if (penDown) {
			gp.lineTo(coords[0], coords[1]);
		} else {
			gp.moveTo(coords[0], coords[1]);
		}
	}

	private void drawTurtleShape(geogebra.common.awt.Graphics2D g2) {

		BufferedImage img = turtle.getTurtleImageList().get(turtle.getTurtle());
		turtle.getPosition().getInhomCoords(coords);
		view.toScreenCoords(coords);

		int x = (int) (coords[0] - img.getWidth() / 2);
		int y = (int) (coords[1] - img.getHeight() / 2);

		// rotate the shape according to the turn angle

		// draw the image
		g2.drawImage(img, null, x, y);

	}

	@Override
	final public void draw(geogebra.common.awt.Graphics2D g2) {

		// System.out.println("TURTLE isVisible: " + isVisible);

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

		//	drawTurtleShape(g2);

		
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		if (isVisible) {
			for (GeneralPathClipped path : gpList) {
				if (path.intersects(x - hitThreshold, y - hitThreshold,
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

		boundRect = null;
		// get bounds
		for (GeneralPathClipped path : gpList) {
			if (boundRect == null)
				boundRect = path.getBounds();
			else
				boundRect = boundRect.union(path.getBounds());
		}

		return boundRect;
	}

}
