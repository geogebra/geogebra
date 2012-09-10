/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GImage;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;

/**
 * GeoElement for drawing turtle graphics.
 * 
 * @author G. Sturr, arnaud
 * 
 */
public class GeoTurtle extends GeoElement implements Animatable{

	// private GeoPointND[] points;
	private boolean defined = true;

	private ArrayList<GImage> turtleImageList;

	// List to store sequential turtle drawing commands.
	// TODO: use a better data structure?
	private ArrayList<Command> cmdList;

	// turtle status fields
	private GeoPointND startPoint = new GeoPoint(cons, 0d, 0d, 1d);
	/** current position */
	protected double[] position = { 0d, 0d, 1d };
	/** current position as point */
	protected GeoPointND currentPoint = new GeoPoint(cons, 0d, 0d, 1d);
	/** pen color */
	protected GColor penColor = GColor.BLACK;
	/** pen thickness */
	protected int penThickness = 1;
	/** whether pen is down (active) */ 
	protected boolean penDown = true;
	/** direction angle (wrt positive x-axis) */
	protected double turnAngle = 0d;
	/** sine of current direction angle */
	protected double sinAngle = 0d;
	/** cosine of current direction angle */
	protected double cosAngle = 1d;
	private int turtleImageIndex = 1;

	private int nCompletedCommands = 0;
	private double currentCommandProgress = 0d;
	private double speed = 1d;
	
	private boolean autoUpdate = true;
	/**
	 * Constructor with label
	 * 
	 * @param c construction
	 * @param label label
	 */
	public GeoTurtle(Construction c, String label) {
		this(c);
		setLabel(label);
	}

	/**
	 * Constructor without label.
	 * 
	 * @param c construction
	 */
	public GeoTurtle(Construction c) {
		super(c);
		cmdList = new ArrayList<Command>();
		
		// TODO: put this in default construction?
		this.setObjColor(GColor.GRAY);

		// this.turn(turnAngle);
		turtleImageList = new ArrayList<GImage>();
		turtleImageList.add(c.getApplication().getInternalImageAdapter("/gui/images/go-next.png"));

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
	public GeoClass getGeoClassType() {
		return GeoClass.TURTLE;
	}

	/**
	 * @return list of turtle commands that define the current turtle drawing
	 */
	public ArrayList<Command> getTurtleCommandList() {
		return cmdList;
	}

	/**
	 * @return current turn angle in degrees [0,360)
	 */
	public double getTurnAngle() {
		return (turnAngle * 180 / Math.PI) % 360;
	}
	
	/**
	 * @param a the new turning angle
	 */
	public void setTurnAngle(double a) {
		turn(a - turnAngle * 180 / Math.PI);
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
	public GColor getPenColor() {
		return penColor;
	}

	/**
	 * @return true if the pen is down
	 */
	public boolean getPenDown() {
		return penDown;
	}

	/**
	 * @return start point
	 */
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	/**
	 * @return list of turtle images
	 */
	public ArrayList<GImage> getTurtleImageList() {
		return turtleImageList;
	}

	/**
	 * Adds image to image list
	 * @param image image
	 */
	public void addTurtleImage(GImage image) {
		turtleImageList.add(image);
	}

	
	/**
	 * @return image index
	 */
	public int getTurtle() {
		return turtleImageIndex;
	}

	/**
	 * @param index image index (may be arbitrary, %4 is done here)
	 */
	public void setTurtle(int index) {
		int index1 = index % 4;
		this.turtleImageIndex = index1;
	}

	/**
	 * @return whether the turtle is repainted automatically after every command
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate whether the turtle is repainted automatically after every command
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	/**
	 * @return speed of the turtle
	 */
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * @param s speed of the turtle
	 */
	public void setSpeed(double s) {
		if (s < 0d) {
			speed = 0d;
		} else {
			speed = s;
		}
	}
	
	/**
	 * @return number of completed commands
	 */
	public int getNumberOfCompletedCommands() {
		return nCompletedCommands;
	}
	
	/**
	 * @return current progress (remaining commands)
	 */
	public double getCurrentCommandProgress() {
		if (currentCommandProgress == 0d) {
			return 0d;
		}
		return currentCommandProgress/cmdList.get(nCompletedCommands).getTime();
	}
	
	/**
	 * Reset current progress to 0
	 */
	public void resetProgress() {
		nCompletedCommands = 0;
		currentCommandProgress = 0d;
		doUpdate();
	}
	
	/**
	 * Do one step
	 */
	public void stepTurtle() {
		stepTurtle(1d);
	}
	
	private boolean doStepTurtle(double nSteps) {
		int totalNCommands = cmdList.size();
		if (speed == 0d || nCompletedCommands >= totalNCommands) {
			return false;
		}
		currentCommandProgress += speed*nSteps;
		double t;
		while (currentCommandProgress >= (t = cmdList.get(nCompletedCommands).getTime())) {
			nCompletedCommands += 1;
			currentCommandProgress -= t;
			if (nCompletedCommands == totalNCommands) {
				currentCommandProgress = 0d;
				break;
			}
		}
		return true;
	}
	
	/**
	 * @param nSteps do n steps
	 */
	public void stepTurtle(double nSteps) {
		if (doStepTurtle(nSteps)) {
			doUpdate();
		}
	}
	
	// ===============================================
	// LOGO COMMANDS
	// ===============================================

	

	/**
	 * Moves the turtle forward (in direction given by current turn angle)
	 * @param distance distance
	 */
	public void forward(double distance) {
		addCommand(new CmdForward(distance));
	}

	/**
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void setPosition(double x, double y) {
		addCommand(new CmdSetPosition(x, y));
	}
	
	/**
	 * @param turnAngleChange change of turn angle in degrees
	 */
	public void turn(double turnAngleChange) {
		addCommand(new CmdTurn(turnAngleChange));
	}

	/**
	 * Puts the pen down or up, i.e. starts / stops drawing
	 * @param penDown true to put pen down
	 */
	public void setPenDown(boolean penDown) {
		addCommand(new CmdSetPen(penDown));
	}

	/**
	 * Changes pen color used by the turtle
	 * @param r red component
	 * @param g green component
	 * @param b blue component
	 */
	public void setPenColor(int r, int g, int b) {
		setPenColor(AwtFactory.prototype.newColor(r, g, b));
	}

	/**
	 * Changes pen color used by the turtle
	 * @param penColor new pen color
	 */
	public void setPenColor(GColor penColor) {
		addCommand(new CmdSetColor(penColor));
	}
	
	/**
	 * Set the thickness of the turtle pen
	 * @param thickness new thickness
	 */
	public void setPenThickness(int thickness) {
		addCommand(new CmdSetThickness(thickness));
	}
	
	/**
	 * 
	 */
	public void clear() {
		// Temporarily set speed to 0 in order to avoid stepping
		double s = speed;
		speed = 0;
		resetProgress();
		cmdList.clear();
		turnAngle = 0d;
		sinAngle = 0d;
		cosAngle = 1d;
		position[0] = 0d;
		position[1] = 0d;
		currentPoint.setCoords(0d, 0d, 1d);
		speed = s;
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
	
	/*
	 * Animatable implementation
	 */

	@Override
	public boolean isAnimatable() {
		return true;
	}
	
	public boolean doAnimationStep(double frameRate) {
		return doStepTurtle(1.0/frameRate);
	}
	
	
	/*
	 * Turtle commands
	 */
	
	/**
	 * Add command to the turtle's command list and perform the command
	 * @param cmd the command to add to the command list
	 */
	public void addCommand(Command cmd) {
		cmdList.add(cmd);
		cmd.perform();
		doUpdate();
	}
	/** command types */
	public enum CmdType {
		/** forward */
		FORWARD,
		/** set_position*/
		SET_POSITION,
		/** turn (left / right)*/
		TURN,
		/** set_color*/
		SET_COLOR,
		/** set_pen*/
		SET_PEN,
		/** set_thickness*/
		SET_THICKNESS
	}
	
	/**
	 * @author arno
	 * Interface for turtle commands
	 */
	public interface Command {
		/**
		 * @return the type of the command
		 */
		public CmdType getType();
		/**
		 * @return the time taken to execute the command
		 */
		public double getTime();
		/**
		 * perform the command on the enclosed GeoTurtle
		 */
		public void perform();
		/**
		 * Draw the command
		 * @param ds the DrawState object to use for drawing
		 */
		public void draw(DrawState ds);
		/**
		 * Draw the command partially
		 * @param ds the drawState object to use for drawing
		 * @param progress the fraction of the command which is completed (between 0 and 1)
		 */
		public void partialDraw(DrawState ds, double progress);
	}
	
	/**
	 * @author arno
	 * Interface for drawing turtle paths.  DrawTurtle classes must implement this interface 
	 */
	public interface DrawState {
		/**
		 * Set pen status
		 * @param down true to put pen down, false to lift it
		 */
		public void setPen(boolean down);
		/**
		 * Move turtle to new position
		 * @param newPosition the new turtle position
		 */
		public void move(GeoPointND newPosition);
		/**
		 * Turn turtle
		 * @param angle anticlockwise angle in radians
		 */
		public void turn(double angle);
		/**
		 * Partially move turtle
		 * @param newPosition the new turtle position
		 * @param progress between 0 (not started) and 1 (all done)
		 */
		public void partialMove(GeoPointND newPosition, double progress);
		/**
		 * Partially turn turtle
		 * @param angle anticlockwise angle in radians
		 * @param progress between 0 (not started) and 1 (all done)
		 */
		public void partialTurn(double angle, double progress);
		/**
		 * Set the pen color
		 * @param color new color
		 */
		public void setColor(GColor color);
		/**
		 * Set the pen thickness
		 * @param th new thickness
		 */
		public void setThickness(int th);
	}
	
	/**
	 * @author arno
	 * Command: Move turtle forward
	 */
	public class CmdForward implements Command {
		private double length;
		private double time;
		private GeoPoint destination;
		
		/**
		 * @param l how far to move
		 */
		public CmdForward(double l) {
			length = l;
			time = Math.abs(l);
		}
		
		public CmdType getType() {
			return CmdType.FORWARD;
		}
		
		public double getTime() {
			return time;
		}
		
		public void perform() {
			position[0] += length*cosAngle;
			position[1] += length*sinAngle;
			destination = new GeoPoint(cons, position[0], position[1], 1d);
			currentPoint.setCoords(position[0], position[1], 1d);
		}
		
		public void draw(DrawState ds) {
			ds.move(destination);
		}
		
		public void partialDraw(DrawState ds, double progress) {
			ds.partialMove(destination, progress);
		}
		
		@Override
		public String toString() {
			return "fd " + length;
		}
	}
	
	/**
	 * @author arno
	 * Set turtle position
	 */
	public class CmdSetPosition implements Command {
		private double destX;
		private double destY;
		private double time;
		private GeoPoint destination;
		
		/**
		 * @param x new x-coord
		 * @param y new y-coord
		 */
		public CmdSetPosition(double x, double y) {
			destX = x;
			destY = y;
			time = Math.hypot(x - position[0], y - position[1]);
		}
		
		public CmdType getType() {
			return CmdType.SET_POSITION;
		}

		public double getTime() {
			return time;
		}

		public void perform() {
			position[0] = destX;
			position[1] = destY;
			destination = new GeoPoint(cons, position[0], position[1], 1d);
			currentPoint.setCoords(position[0], position[1], 1d);
		}

		public void draw(DrawState ds) {
			ds.move(destination);
		}

		public void partialDraw(DrawState ds, double progress) {
			ds.partialMove(destination, progress);
		}
	}
	
	/**
	 * @author arno
	 * Command: turn turtle
	 */
	public class CmdTurn implements Command {
		private double degAngle;
		private double angle;
		private double time;
		
		/**
		 * @param a anticlokwise angle in degrees
		 */
		public CmdTurn(double a) {
			degAngle = a;
			angle = a * Math.PI / 180;
			time = a / 90;
		}
		public CmdType getType() {
			return CmdType.TURN;
		}
		
		public double getTime() {
			return time;
		}
		
		public void perform() {
			turnAngle += angle;
			sinAngle = Math.sin(turnAngle);
			cosAngle = Math.cos(turnAngle);
		}
		
		public void draw(DrawState ds) {
			ds.turn(angle);
		}
		
		public void partialDraw(DrawState ds, double progress) {
			ds.partialTurn(angle, progress);
		}
		
		@Override
		public String toString() {
			if (degAngle > 0) {
				return "tl " + degAngle;
			}
			return "tr " + (-degAngle);
		}
	}
	
	/**
	 * @author arno
	 * Command: set pen color
	 */
	public class CmdSetColor implements Command {
		private GColor color;
		
		/**
		 * @param c the new pen color
		 */
		public CmdSetColor(GColor c) {
			color = c;
		}
		
		public CmdType getType() {
			return CmdType.SET_COLOR;
		}
		
		public double getTime() {
			return 0d;
		}
		
		public void perform() {
			penColor = color;
		}
		
		public void draw(DrawState ds) {
			ds.setColor(color);
		}
		
		public void partialDraw(DrawState ds, double progress) {
			// nothing to do
		}
		
		@Override
		public String toString() {
			return "color " + color;
		}
	}
	
	/**
	 * @author arno
	 * Command: set pen state (up or down)
	 */
	public class CmdSetPen implements Command {
		private boolean down;
		
		/**
		 * @param d true for pen down, false for up
		 */
		public CmdSetPen(boolean d) {
			down = d;
		}
		
		public CmdType getType() {
			return CmdType.SET_PEN;
		}
		
		public double getTime() {
			return 0d;
		}
		
		public void perform() {
			penDown = down;
		}
		
		public void draw(DrawState ds) {
			ds.setPen(down);
		}
		
		public void partialDraw(DrawState ds, double progress) {
			// nothing to do
		}
		
		@Override
		public String toString() {
			return down ? "pd" : "pu";
		}
	}
	
	/**
	 * @author arno
	 * Command: set pen thickness
	 */
	public class CmdSetThickness implements Command {
		private int thickness;
		
		/**
		 * @param th the new pen thickness
		 */
		public CmdSetThickness(int th) {
			thickness = th;
		}
		
		public CmdType getType() {
			return CmdType.SET_THICKNESS;
		}
		
		public double getTime() {
			return 0d;
		}
		
		public void perform() {
			penThickness = thickness;
		}
		
		public void draw(DrawState ds) {
			ds.setThickness(thickness);
		}
		
		public void partialDraw(DrawState ds, double progress) {
			// nothing to do
		}
		
		@Override
		public String toString() {
			return "thickness " + thickness;
		}
	}

}
