/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.awt.BufferedImage;
import geogebra.common.awt.Color;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;

/**
 * GeoElement for drawing turtle graphics.
 * 
 * @author G. Sturr
 * 
 */
public class GeoTurtle extends GeoElement {

	// private GeoPointND[] points;
	private boolean defined = true;

	private ArrayList<BufferedImage> turtleImageList;

	// List to store sequential turtle drawing commands.
	// TODO: use a better data structure?
	private ArrayList<Object> cmdList;

	// turtle status fields
	private GeoPointND startPoint = new GeoPoint2(cons, 0d, 0d, 1d);
	private double[] position = { 0d, 0d, 1d };
	private GeoPointND currentPoint = new GeoPoint2(cons, 0d, 0d, 1d);
	private Color penColor = Color.BLACK;
	private int penThickness = 1;
	private boolean penDown = true;
	private double turnAngle = 0d;
	private double sinAngle = 0d;
	private double cosAngle = 1d;
	private int turtleImageIndex = 0;

	private boolean autoUpdate = true;
	/**
	 * Constructor with label
	 * 
	 * @param c
	 * @param label
	 */
	public GeoTurtle(Construction c, String label) {
		this(c);
		setLabel(label);
	}

	/**
	 * Constructor without label.
	 * 
	 * @param c
	 */
	public GeoTurtle(Construction c) {
		super(c);
		cmdList = new ArrayList<Object>();
		// TODO: put this in default construction?
		this.setObjColor(Color.GRAY);

		this.turn(turnAngle);

		turtleImageList = new ArrayList<BufferedImage>();
		
		//String imagePath = "geogebra/gui/images/";
		//turtleImageList.add(app.getInternalImageAdapter(imagePath + "go-next.png"));
		

	}

	// ==================================================
	// Copy constructors
	// TODO code is copied from GeoPolyLine, needs correcting
	// ==================================================

	/**
	 * The copy of a polygon is a number (!) with its value set to the polygons
	 * current area
	 */
	@Override
	public GeoElement copy() {
		return new GeoNumeric(cons, 2);
	}

	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoTurtle ret = new GeoTurtle(cons1, null);
		// ret.points = GeoElement.copyPoints(cons1, points);
		ret.set(this);

		return ret;
	}

	// ===============================================
	// GETTERS/SETTERS
	// ===============================================

	@Override
	public String getClassName() {
		return "GeoTurtle";
	}

	@Override
	public String getTypeString() {
		return "Turtle";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.TURTLE;
	}

	/**
	 * @return list of turtle commands that define the current turtle drawing
	 */
	public ArrayList<Object> getTurtleCommandList() {
		return cmdList;
	}

	/**
	 * @return current turn angle in degrees [0,360)
	 */
	public double getTurnAngle() {
		return (turnAngle * 180 / Math.PI) % 360;
	}

	/**
	 * @return current sin and cos of turn angle
	 */
	public double[] getAngleRotators() {
		double[] ar = {this.cosAngle, this.sinAngle};
		return ar;
	}
	
	/**
	 * @return current turtle coordinates
	 */
	public GeoPointND getPosition() {
		return currentPoint;
	}

	/**
	 * @return current pen thickness
	 */
	public int getPenThickness() {
		return penThickness;
	}

	/**
	 * @return current pen color
	 */
	public Color getPenColor() {
		return penColor;
	}

	/**
	 * @return true if the pen is down
	 */
	public boolean getPenDown() {
		return penDown;
	}

	public GeoPointND getStartPoint() {
		return startPoint;
	}

	public ArrayList<BufferedImage> getTurtleImageList() {
		return turtleImageList;
	}

	public void addTurtleImage(BufferedImage image) {
		turtleImageList.add(image);
	}

	
	public void setTurtleImageList(BufferedImage image) {
		
		turtleImageList.add(image);
	}
	
	
	public int getTurtle() {
		return turtleImageIndex;
	}

	public void setTurtle(int index) {
		this.turtleImageIndex = index;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	
	// ===============================================
	// LOGO COMMANDS
	// ===============================================

	

	public void forward(double distance) {

		position[0] += distance * cosAngle;
		position[1] += distance * sinAngle;

		// cmdList.add(position);

		GeoPointND pt = new GeoPoint2(cons, position[0], position[1], 1d);
		cmdList.add(pt);
		currentPoint.setCoords(position[0], position[1], 1.0);
		doUpdate();

	}

	public void setPosition(double x, double y) {

		position[0] = x;
		position[1] = y;

		// cmdList.add(position);

		GeoPoint2 pt = new GeoPoint2(cons, position[0], position[1], 1d);
		cmdList.add(pt);
		doUpdate();
	}

	public void turn(double turnAngle) {

		this.turnAngle += turnAngle * Math.PI / 180;

		this.sinAngle = Math.sin(this.turnAngle);
		this.cosAngle = Math.cos(this.turnAngle);

		cmdList.add(turnAngle);
		doUpdate();
	}

	public void setPenDown(boolean penDown) {
		this.penDown = penDown;
		cmdList.add(penDown);
		doUpdate();
	}

	public void setPenColor(int r, int g, int b) {
		setPenColor(AwtFactory.prototype.newColor(r, g, b));
		doUpdate();
	}

	public void setPenColor(Color penColor) {
		if (penColor.equals(this.penColor))
			return;
		this.penColor = penColor;
		cmdList.add(penColor);
		doUpdate();
	}

	public void clear() {
		cmdList.clear();
		turn(-turnAngle);
		setPenDown(false);
		setPosition(0,0);
		setPenDown(true);
		doUpdate();
	}
	
	private void doUpdate(){
		if(autoUpdate)
			updateRepaint();
	}

	// ===========================================================
	// Overridden GeoElement methods
	// TODO: review these
	// ===========================================================

	@Override
	public boolean isGeoTurtle() {
		return true;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub
		defined = false;

	}

	@Override
	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		// TODO Auto-generated method stub
		return false;
	}

}
