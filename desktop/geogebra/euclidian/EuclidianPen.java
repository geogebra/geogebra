package geogebra.euclidian;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCircleThreePoints;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.Unicode;
import geogebra.main.Application;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import geogebra.euclidianND.EuclidianViewND;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class EuclidianPen extends geogebra.common.euclidian.EuclidianPen{

	private Application app;
	private EuclidianViewND view;

	private int penOffsetX = 0;
	private int penOffsetY = 0;
	private boolean penUsingOffsets = false;
	private BufferedImage penImage = null;
	private GeoImage penGeo = null; // used if drawing to existing GeoImage
	private GeoImage lastPenImage = null;
	private boolean penWritingToExistingImage = false;
	private ArrayList<Point> penPoints = new ArrayList<Point>();
	private ArrayList<Point> temp = null;
	int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
	double CIRCLE_MIN_DET=0.95;
	double CIRCLE_MAX_SCORE=0.10;
	double score=0;
	int brk[];
	int recognizer_queue_length = 0;
	int MAX_POLYGON_SIDES=4;
	double LINE_MAX_DET=0.015;
	double SLANT_TOLERANCE=5*Math.PI/180;
	double RECTANGLE_ANGLE_TOLERANCE = 15*Math.PI/180;
	double RECTANGLE_LINEAR_TOLERANCE = 0.20;
	double POLYGON_LINEAR_TOLERANCE = 0.20;
	Inertia a = null;
	Inertia b = null;
	Inertia c = null;
	Inertia d = null;
	RecoSegment reco_queue_a = new RecoSegment();
	RecoSegment reco_queue_b = new RecoSegment();
	RecoSegment reco_queue_c = new RecoSegment();
	RecoSegment reco_queue_d = new RecoSegment();
	RecoSegment reco_queue_e = new RecoSegment();
	/**
     * String representation of slant movement.
     */
	private static final String LEFT_UP = "Q";
	private static final String RIGHT_UP = "W";
	private static final String RIGHT_DOWN = "E";
	private static final String LEFT_DOWN = "R";
	/**
     * String representation of left movement.
     */
	private static final String LEFT_MOVE = "L";
    /**
     * String representation of right movement.
     */
    private static final String RIGHT_MOVE = "R";
    /**
     * String representation of up movement.
     */
    private static final String UP_MOVE = "U";
    /**
     * String representation of down movement.
     */
    private static final String DOWN_MOVE = "D";
    /**
     * Grid size. Default is 30.
     */
    private int gridSize = 30;
    private java.awt.Point startPoint = null;
    /**
     * String representation of gesture.
     */
    private StringBuffer gesture = new StringBuffer();
    int deltaX = 0;
    int deltaY = 0;
    int absDeltaX = 0;
    int absDeltaY = 0;
    float absTangent = 0;


	private boolean erasing = false;

	private int penSize;

	public int getPenSize() {
		return penSize;
	}

	public void setPenSize(int penSize) {
		this.penSize = penSize;
	}

	public int getPenLineStyle() {
		return penLineStyle;
	}

	public void setPenLineStyle(int penLineStyle) {
		this.penLineStyle = penLineStyle;
	}

	public Color getPenColor() {
		return penColor;
	}

	public void setPenColor(Color penColor) {
		this.penColor = penColor;
	}

	private int eraserSize;
	private int penLineStyle;
	private Color penColor;
	private boolean freehand = false;

	/************************************************
	 * Construct EuclidianPen
	 */
	public EuclidianPen(Application app, EuclidianViewND view) {
		this.view = view;
		this.app = app;

		setDefaults();
	}

	// ===========================================
	// Getters/Setters
	// ===========================================

	public void setDefaults() {
		penSize = 3;
		eraserSize = 16;
		penLineStyle = EuclidianStyleConstants.LINE_TYPE_FULL;
		penColor = Color.black;
	}

	public boolean isErasing() {
		return erasing;
	}

	public void setErasing(boolean erasing) {
		this.erasing = erasing;
	}

	public boolean isPenWritingToExistingImage() {
		return penWritingToExistingImage;
	}
	
	public GeoImage getPenGeo() {
		return penGeo;
	}

	public void setPenGeo(GeoImage penGeo) {
		this.penGeo = penGeo;
		penWritingToExistingImage = penGeo!= null;
	}

	public void resetPenOffsets() {
		penOffsetX = 0;
		penOffsetY = 0;
		penUsingOffsets = false;
		penImage = null;
		penGeo = null;
		lastPenImage = null;
	}

	// ===========================================
	// Mouse Event Handlers
	// ===========================================

	public void handleMousePressedForPenMode(MouseEvent e, Hits hits) {

		Rectangle rect = geogebra.awt.Rectangle.getAWTRectangle(view.getSelectionRectangle());

		if (Application.isRightClick(e) && !freehand) {
			view.setCursor(app.getEraserCursor());
			erasing = true;
		} else {
			view.setCursor(app.getTransparentCursor());
			erasing = false;
		}

		// Graphics2D g2D = null;

		if (penGeo != null) {
			// image was selected before Pen Tool selected

			penUsingOffsets = true;
			penImage = geogebra.awt.BufferedImage.getAwtBufferedImage(penGeo
					.getFillImage());
			// lastPenImage = penGeo;

			penWritingToExistingImage = true;

			if (penGeo.isAbsoluteScreenLocActive()) {
				penOffsetX = penGeo.getAbsoluteScreenLocX();
				penOffsetY = penGeo.getAbsoluteScreenLocY();
			} else {
				GeoPoint2 startPoint = penGeo.getStartPoint();
				penOffsetX = view.toScreenCoordX(startPoint.inhomX);
				penOffsetY = view.toScreenCoordY(startPoint.inhomY)
						- penImage.getHeight();

			}

			app.addSelectedGeo(penGeo);

			penGeo = null;
		} else if (rect != null
				&& rect.getWidth() > 1
				&& rect.getHeight() > 1
				&& (!penUsingOffsets || penOffsetX != rect.x || penOffsetY != rect.y)) {
			// just draw on a subset of the Graphics View

			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gs.getDefaultConfiguration();

			penImage = gc.createCompatibleImage((int) rect.getWidth(),
					(int) rect.getHeight(), Transparency.BITMASK);

			lastPenImage = null;

			penOffsetX = rect.x;
			penOffsetY = rect.y;
			penUsingOffsets = true;

			penWritingToExistingImage = false;

			// view.setSelectionRectangle(null);
		} else if (lastPenImage != null && !penWritingToExistingImage) {

			penImage = geogebra.awt.BufferedImage
					.getAwtBufferedImage(lastPenImage.getFillImage());

			GeoPoint2 corner = lastPenImage.getCorner(0);
			int x = view.toScreenCoordX(corner.getInhomX());
			int y = view.toScreenCoordY(corner.getInhomY());
			int width = penImage.getWidth();
			int height = penImage.getHeight();

			// check if image is still the same size as the current euclidian
			// view window
			if ((penOffsetX > 0 && penOffsetY > 0)
					|| (x == 0 && y == height && height == view.getHeight() && width == view
					.getWidth()))
				penImage = geogebra.awt.BufferedImage
				.getAwtBufferedImage(lastPenImage.getFillImage());
			else {
				penImage = null;
				lastPenImage = null;
			}

			penWritingToExistingImage = false;

		}

		// check if mouse pressed over existing image
		if (penImage == null && hits != null && hits.size() > 0) {
			GeoImage hit = (GeoImage) hits.get(0);

			GeoPoint2 c1 = hit.getCorner(0);
			GeoPoint2 c2 = hit.getCorner(1);

			int width = hit.getFillImage().getWidth();

			int x1 = view.toScreenCoordX(c1.getInhomX());
			int y1 = view.toScreenCoordY(c1.getInhomY());
			int x2 = c2 == null ? x1 + width : view.toScreenCoordX(c2
					.getInhomX());
			int y2 = c2 == null ? y1 : view.toScreenCoordY(c2.getInhomY());

			if (y1 == y2 && x1 + width == x2) { // check image isn't rotated /
				// scaled
				penGeo = hit;
				penUsingOffsets = true;
				penImage = geogebra.awt.BufferedImage
						.getAwtBufferedImage(penGeo.getFillImage());
				// lastPenImage = penGeo;

				penWritingToExistingImage = true;

				if (penGeo.isAbsoluteScreenLocActive()) {
					penOffsetX = penGeo.getAbsoluteScreenLocX();
					penOffsetY = penGeo.getAbsoluteScreenLocY();
				} else {
					GeoPoint2 startPoint = penGeo.getStartPoint();
					penOffsetX = view.toScreenCoordX(startPoint.inhomX);
					penOffsetY = view.toScreenCoordY(startPoint.inhomY)
							- penImage.getHeight();

				}

				app.addSelectedGeo(penGeo);
			}
		}

		if (penImage == null) {
			view.setSelectionRectangle(null);
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			penImage = gc.createCompatibleImage(view.getWidth(),
					view.getHeight(), Transparency.BITMASK);

		}

		// if (g2D == null) g2D = penImage.createGraphics();

		Point newPoint = new Point(e.getX() - penOffsetX, e.getY() - penOffsetY);
		Graphics2D g2D = view.getGraphicsForPen();
		Shape circle;
		if (Application.isRightClick(e) && !freehand) {
			g2D.setColor(Color.white);
			circle = new Ellipse2D.Float(e.getX() - eraserSize, e.getY()
					- eraserSize, eraserSize * 2, eraserSize * 2);
		} else {
			g2D.setColor(penColor);
			circle = new Ellipse2D.Float(e.getX() - penSize,
					e.getY() - penSize, penSize * 2, penSize * 2);
		}
		// g2D.drawOval(e.getX(), e.getY(), penSize, penSize);
		g2D.fill(circle);

		if (minX > e.getX())
			minX = e.getX();
		if (maxX < e.getX())
			maxX = e.getX();

		if (penPoints.size() == 0)
			penPoints.add(newPoint);
		else {
			Point lastPoint = penPoints.get(penPoints.size() - 1);
			if (lastPoint.distance(newPoint) > 3)
				penPoints.add(newPoint);
		}
		java.awt.Point point  = e.getPoint();
		if (startPoint == null)
			startPoint = e.getPoint();
		deltaX = this.getDeltaX(startPoint, point);
	    deltaY = this.getDeltaY(startPoint, point);
	    absDeltaX = Math.abs(deltaX);
	    absDeltaY = Math.abs(deltaY);
	    absTangent = ((float) absDeltaX) / absDeltaY;
	    if (!((absDeltaX < gridSize) && (absDeltaY < gridSize)))
	    {
	    	if (absTangent < 0.5) 
	        {
	    		if (deltaY < 0)
	    			this.saveMove(UP_MOVE);
	    		else
	    			this.saveMove(DOWN_MOVE);
	            startPoint = point;
	        } 
	    	if (absTangent >= 0.5 && absTangent <= 2)
	    	{
	    		if (deltaX > 0 && deltaY < 0)
	    			this.saveMove(LEFT_UP);
	    		if (deltaX < 0 && deltaY < 0)
	    			this.saveMove(RIGHT_UP);
	    		if (deltaX < 0 && deltaY > 0)
	    			this.saveMove(RIGHT_DOWN);
	    		if (deltaX > 0 && deltaY > 0)
	    			this.saveMove(LEFT_DOWN);
	    	}
	        if (absTangent > 2)
	        {
	        	if (deltaX < 0)
	        		this.saveMove(LEFT_MOVE);
	            else
	            	this.saveMove(RIGHT_MOVE);
	            startPoint = point;
	        }
	    }
	}

	public void handleMouseReleasedForPenMode(MouseEvent e) {

		if (freehand) {
			mouseReleasedFreehand();

			return;
		}

		if (penImage == null)
			return; // right click

		app.setDefaultCursor();

		String gesture = this.getGesture();
		System.out.println(gesture);
		this.clearTemporaryInfo();
		Point newPoint = new Point(e.getX() - penOffsetX, e.getY() - penOffsetY);
		penPoints.add(newPoint);
		//System.out.println(penPoints);
		//if recognize_shape option is checked
		brk=new int[5];
		a = new Inertia();
		b = new Inertia();
		c = new Inertia();
		d = new Inertia();
		int j = 0;
		RecoSegment rs = null;
		Inertia ss = null;
		RecoSegment temp = null;
		//System.out.println(penPoints);
		Inertia s=new Inertia();
		this.calc_inertia(0,penPoints.size()-1,s);
		int n=this.findPolygonal(0,penPoints.size()-1,MAX_POLYGON_SIDES,0,0);
		//System.out.println(n);
		if(n > 0)
		{
			this.optimize_polygonal(n);
			while(n+recognizer_queue_length > MAX_POLYGON_SIDES)
			{
				j = 1;
				temp = reco_queue_b;
				while(j<recognizer_queue_length && temp.startpt!=0)
				{
					j++;
					if(j == 2)
						temp = reco_queue_c;
					if(j == 3)
						temp = reco_queue_d;
					if(j == 4)
						temp = reco_queue_e;
				}
				int te1 = 0;
				int te2 = j;
				RecoSegment t1 = null;
				RecoSegment t2 = null;
				for(int k=0; k<recognizer_queue_length; ++k)
				{
					if(te1 == 0)
						t1 = reco_queue_a;
					if(te1 == 1)
						t1 = reco_queue_b;
					if(te1 == 2)
						t1 = reco_queue_c;
					if(te1 == 3)
						t1 = reco_queue_d;
					if(te1 == 4)
						t1 = reco_queue_e;
					if(te2 == 0)
						t2 = reco_queue_a;
					if(te2 == 1)
						t2 = reco_queue_b;
					if(te2 == 2)
						t2 = reco_queue_c;
					if(te2 == 3)
						t2 = reco_queue_d;
					if(te2 == 4)
					t2 = reco_queue_e;
					t1.startpt = t2.startpt;
					t1.endpt = t2.endpt;
					t1.xcenter = t2.xcenter;
					t1.ycenter = t2.ycenter;
					t1.angle = t2.angle;
					t1.radius = t2.radius;
					t1.x1 = t2.x1;
					t1.x2 = t2.x2;
					t1.y1 = t2.y2;
					t1.y2 = t2.y2;
					t1.reversed = t2.reversed;
					te1++;
					te2++;
				}
			}
			int temp_reco = recognizer_queue_length; 
			recognizer_queue_length = recognizer_queue_length + n;
			for(j=0; j<n; ++j)
			{
				if(temp_reco+j == 0)
						rs = reco_queue_a;
				if(temp_reco+j == 1)
					rs = reco_queue_b;
				if(temp_reco+j == 2)
					rs = reco_queue_c;
				if(temp_reco+j == 3)
					rs = reco_queue_d;
				if(temp_reco+j == 4)
					rs = reco_queue_e;
				if(j == 0)
					ss = a;
				if(j == 1)
					ss = b;
				if(j == 2)
					ss = c;
				if(j == 3)
					ss = d;
				rs.startpt = brk[j];
				rs.endpt = brk[j+1];
				this.get_segment_geometry(brk[j], brk[j+1], ss, rs);
			}
			if(this.try_rectangle())
			{
				recognizer_queue_length = 0;
				System.out.println("Rectangle Recognized");
			}
			if(n==1)//then stroke is a line
			{
				System.out.println("Current stroke is a line");
				double xcenter,ycenter,x1,y1,x2,y2,x,y,z,angle;
				xcenter=a.sx/a.mass;
				ycenter=a.sy/a.mass;
				x=this.I_xx(a);
				y=this.I_xy(a);
				z=this.I_yy(a);
				angle=Math.atan2(2*y, x-z)/2;
				if(Math.abs(angle)<SLANT_TOLERANCE)
				{
					angle=0;
					y1=ycenter;
					y2=ycenter;
				}
				if(Math.abs(angle)>Math.PI/2-SLANT_TOLERANCE)
				{
					x1=xcenter;
					x2=xcenter;
				}
				//	line1=new Line2D();
				double x_first=view.toRealWorldCoordX(penPoints.get(0).x);
				double y_first=view.toRealWorldCoordY(penPoints.get(0).y);
				double x_last=view.toRealWorldCoordX(penPoints.get(penPoints.size()-1).x);
				double y_last=view.toRealWorldCoordY(penPoints.get(penPoints.size()-1).y);
				String equation=null;
				if(x_first==x_last)
				{
					equation="x" + "=" + (x_first);
					System.out.println(equation);
					GeoPoint2 p = new GeoPoint2(app.getKernel().getConstruction(), x_first, y_first, 1.0);
					GeoPoint2 q = new GeoPoint2(app.getKernel().getConstruction(), x_last, y_last, 1.0);
					AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(app.getKernel().getConstruction(), equation, p, q);
				}
				else if(y_last==y_first)
				{
					equation="y" + "=" + " " + (y_first);
					System.out.println(equation);
					GeoPoint2 p = new GeoPoint2(app.getKernel().getConstruction(), x_first, y_first, 1.0);
					GeoPoint2 q = new GeoPoint2(app.getKernel().getConstruction(), x_last, y_last, 1.0);
					AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(app.getKernel().getConstruction(), equation, p, q);
				}
				else
				{
					double y_diff=(y_first-y_last);
					double x_diff=(x_last-x_first);
					if(x_diff<0)
					{
						equation=y_diff + "x" + "-" + -x_diff + "y" + "=" + ((x_diff*y_first)+(y_diff*x_first));
						AbstractApplication.debug(equation);
					}
					else
					{
						equation=y_diff + "x" + "+" + x_diff + "y" + "=" + ((x_diff*y_first)+(y_diff*x_first));
						AbstractApplication.debug(equation);
					}
					GeoPoint2 p = new GeoPoint2(app.getKernel().getConstruction(), x_first, y_first, 1.0);
					GeoPoint2 q = new GeoPoint2(app.getKernel().getConstruction(), x_last, y_last, 1.0);
					AlgoJoinPointsSegment algo = new AlgoJoinPointsSegment(app.getKernel().getConstruction(), equation, p, q);
				}		
			}
		}
		if(this.I_det(s) > CIRCLE_MIN_DET)
		{
			score=this.score_circle(0,penPoints.size()-1,s);
			if(score<CIRCLE_MAX_SCORE)
			{
				this.makeACircle(this.center_x(s), this.center_y(s), this.I_rad(s));
			}
		}
		// if (lastPenImage != null) penImage = lastPenImage.getImage();
		// //app.getExternalImage(lastPenImage);

		// Application.debug(penPoints.size()+"");

		doDrawPoints(null, penPoints);
		if (app.getScriptManager() != null) {
			double x[] = new double[penPoints.size()], y[] = new double[penPoints
			                                                            .size()];
			for (int i = 0; i < penPoints.size(); i++) {
				x[i] = view.toRealWorldCoordX(penPoints.get(i).getX()
						+ penOffsetX);
				y[i] = view.toRealWorldCoordY(penPoints.get(i).getY()
						+ penOffsetY);
			}
			// we want to clear the points before notifyDraw throws potential
			// exception
			penPoints.clear();
			app.getScriptManager().notifyDraw(lastPenImage.getLabelSimple(), x, y);
		} else
			penPoints.clear();

	}

	public void doDrawPoints(GeoImage gi, List<Point> penPoints2) {
		PolyBezier pb = new PolyBezier(penPoints2);
		BufferedImage penImage2 = gi == null ? penImage
				: geogebra.awt.BufferedImage.getAwtBufferedImage(gi
						.getFillImage());
		boolean giNeedsInit = false;
		if (penImage2 == null) {
			giNeedsInit = true;
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			penImage2 = gc.createCompatibleImage(view.getWidth(),
					view.getHeight(), Transparency.BITMASK);
		}
		Graphics2D g2d = (Graphics2D) penImage2.getGraphics();

		EuclidianView.setAntialiasing(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		if (erasing) {
			g2d.setStroke(geogebra.awt.BasicStroke.getAwtStroke(geogebra.common.euclidian.EuclidianStatic.getStroke(2 * eraserSize,
					EuclidianStyleConstants.LINE_TYPE_FULL)));
			g2d.setColor(new Color(0, 0, 0, 0)); // transparent
			g2d.setComposite(AlphaComposite.Src);
		} else {
			g2d.setStroke(geogebra.awt.BasicStroke.getAwtStroke(geogebra.common.euclidian.EuclidianStatic.getStroke(2 * penSize, (penPoints2
					.size() <= 2) ? EuclidianStyleConstants.LINE_TYPE_FULL
							: penLineStyle)));
			g2d.setColor(penColor);
		}
		g2d.draw(pb.gp);

		EuclidianView ev = (EuclidianView) app.getActiveEuclidianView();

		app.refreshViews(); // clear trace
		ev.getGraphics().drawImage(penImage2, penOffsetX, penOffsetY, null);

		if (giNeedsInit
				|| (gi == null && lastPenImage == null && !penWritingToExistingImage)) {
			String fileName = app.createImage(penImage2, "penimage.png");
			// Application.debug(fileName);
			GeoImage geoImage = null;
			if (gi == null)
				geoImage = new GeoImage(app.getKernel().getConstruction());
			else
				geoImage = gi;
			geoImage.setImageFileName(fileName);
			geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);
			GeoPoint2 corner = (new GeoPoint2(
					app.getKernel().getConstruction(), null,
					ev.toRealWorldCoordX(penOffsetX),
					ev.toRealWorldCoordY(penOffsetY + penImage2.getHeight()),
					1.0));
			GeoPoint2 corner2 = (new GeoPoint2(app.getKernel()
					.getConstruction(), null, ev.toRealWorldCoordX(penOffsetX
							+ penImage2.getWidth()), ev.toRealWorldCoordY(penOffsetY
									+ penImage2.getHeight()), 1.0));
			corner.setLabelVisible(false);
			corner2.setLabelVisible(false);
			corner.setAuxiliaryObject(!penUsingOffsets);
			corner2.setAuxiliaryObject(!penUsingOffsets);
			corner.update();
			corner2.update();
			if (gi == null)
				geoImage.setLabel(null);
			geoImage.setCorner(corner, 0);
			geoImage.setCorner(corner2, 1);

			// need 3 corner points if axes ratio isn't 1:1
			if (!Kernel.isEqual(ev.getXscale(), ev.getYscale())) {
				GeoPoint2 corner4 = (new GeoPoint2(app.getKernel()
						.getConstruction(), null,
						ev.toRealWorldCoordX(penOffsetX),
						ev.toRealWorldCoordY(penOffsetY), 1.0));
				corner4.setLabelVisible(false);
				corner4.setAuxiliaryObject(!penUsingOffsets);
				corner4.update();
				geoImage.setCorner(corner4, 2);
			}

			geoImage.setFixed(!penUsingOffsets);
			geoImage.setSelectionAllowed(penUsingOffsets);
			geoImage.setAuxiliaryObject(!penUsingOffsets);
			geoImage.update();

			GeoImage.updateInstances();

			lastPenImage = geoImage;
		}

		// doesn't work as all changes are in the image not the XML
		// app.storeUndoInfo();
		app.setUnsaved();

		if (!penWritingToExistingImage)
			penImage = null;
		// penWritingToExistingImage = false;

		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;

	}

	private void mouseReleasedFreehand() {
		int n = maxX - minX + 1;
		double[] freehand = new double[n];

		for (int i = 0; i < n; i++)
			freehand[i] = Double.NaN;

		for (int i = 0; i < penPoints.size(); i++) {
			Point p = penPoints.get(i);
			if (Double.isNaN(freehand[p.x - minX])) {
				freehand[p.x - minX] = view.toRealWorldCoordY(p.y);
			}
		}

		// fill in any gaps (eg from fast mouse movement)
		double val = freehand[0];
		int valIndex = 0;
		double nextVal = Double.NaN;
		int nextValIndex = -1;
		for (int i = 0; i < n; i++) {
			if (Double.isNaN(freehand[i])) {
				if (i > nextValIndex) {
					nextValIndex = i;
					while (nextValIndex < n
							&& Double.isNaN(freehand[nextValIndex]))
						nextValIndex++;
				}
				if (nextValIndex >= n)
					freehand[i] = val;
				else {
					nextVal = freehand[nextValIndex];
					freehand[i] = (val * (nextValIndex - i) + nextVal
							* (i - valIndex))
							/ (nextValIndex - valIndex);
				}
			} else {
				val = freehand[i];
				valIndex = i;
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Function[{");
		sb.append(view.toRealWorldCoordX(minX));
		sb.append(",");
		sb.append(view.toRealWorldCoordX(maxX));
		sb.append(",");
		for (int i = 0; i < n; i++) {
			sb.append(freehand[i]);
			if (i < n - 1)
				sb.append(",");
		}
		sb.append("}]");

		app.getKernel().getAlgebraProcessor()
		.processAlgebraCommand(sb.toString(), true);

		penPoints.clear();

		app.refreshViews(); // clear trace

		minX = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
	}

	public void setFreehand(boolean b) {
		freehand = b;
	}

	public void handleMouseReleasedForPenMode(AbstractEvent event) {
		handleMouseReleasedForPenMode(geogebra.euclidian.event.MouseEvent.getEvent(event));
	}

	@Override
	public void handleMousePressedForPenMode(AbstractEvent e, Hits hits) {
		handleMousePressedForPenMode(geogebra.euclidian.event.MouseEvent.getEvent(e), hits);
	}
	
	/*
	 * ported from xournal by Neel Shah
	 */
	public int findPolygonal(int start, int end, int nsides,int offset1,int offset2)
	{
		Inertia s=new Inertia();
		Inertia s1=new Inertia();
		Inertia s2=new Inertia();
		int k, i1=0, i2=0, n1=0, n2;
		double det1, det2;  
		//System.out.println(start);
		//System.out.println(end);
		if (end == start) 
			return 0; // no way
		if (nsides <= 0) 
			return 0;
		if (end-start<5)
			nsides = 1; // too small for a polygon
		// look for a linear piece that's big enough
		for(k=0;k<nsides;++k)
		{
			i1 = start + (k*(end-start))/nsides; 
			//System.out.println(i1);
			i2 = start + ((k+1)*(end-start))/nsides;
			//System.out.println(i2);
			calc_inertia(i1,i2,s);
			if(this.I_det(s) < LINE_MAX_DET)
				break;
		}
		if(k==nsides)
			return 0;
		while(true)
		{
			if(i1 > start)
			{
				s1.mass = s.mass;
				s1.sx = s.sx;
				s1.sxx = s.sxx;
				s1.sxy = s.sxy;
				s1.syy = s.syy;
				s1.sy = s.sy;
				this.incr_inertia(i1-1, s1, 1);
				det1=this.I_det(s1);
			}
			else
				det1=1;
			if(i2 < end)
			{
				s2.mass = s.mass;
				s2.sx = s.sx;
				s2.sxx = s.sxx;
				s2.sxy = s.sxy;
				s2.syy = s.syy;
				s2.sy = s.sy;
				this.incr_inertia(i2, s2, 1);
				det2=this.I_det(s2);
			}
			else
				det2=1;
			if (det1<det2 && det1<LINE_MAX_DET) 
			{
				i1--; 
				s.mass = s1.mass;
				s.sx = s1.sx;
				s.sxx = s1.sxx;
				s.sxy = s1.sxy;
				s.syy = s1.syy;
				s.sy = s1.sy;
			}
			else if (det2<det1 && det2<LINE_MAX_DET) 
			{ 
				i2++; 
				s.mass = s2.mass;
				s.sx = s2.sx;
				s.sxx = s2.sxx;
				s.sxy = s2.sxy;
				s.syy = s2.syy;
				s.sy = s2.sy;
			}
			else
				break;
		}
		if(i1 > start)
		{
			n1=this.findPolygonal(start, i1, (i2 == end)?(nsides-1):(nsides-2), offset1,offset2);
			if(n1==0)
				return 0;
		}
		else
			n1=0;
		brk[n1+offset1] = i1;
		brk[n1+1+offset1] = i2;
		if(offset2+n1==0)
		{
			a.mass = s.mass;
			a.sx = s.sx;
			a.sxx = s.sxx;
			a.sxy = s.sxy;
			a.syy = s.syy;
			a.sy = s.sy;
		}
		if(offset2+n1==1)
		{
			b.mass = s.mass;
			b.sx = s.sx;
			b.sxx = s.sxx;
			b.sxy = s.sxy;
			b.syy = s.syy;
			b.sy = s.sy;
		}
		if(offset2+n1==2)
		{
			c.mass = s.mass;
			c.sx = s.sx;
			c.sxx = s.sxx;
			c.sxy = s.sxy;
			c.syy = s.syy;
			c.sy = s.sy;
		}
		if(offset2+n1==3)
		{
			d.mass = s.mass;
			d.sx = s.sx;
			d.sxx = s.sxx;
			d.sxy = s.sxy;
			d.syy = s.syy;
			d.sy = s.sy;
		}
		if(i2 < end)
		{
			n2=this.findPolygonal(i2,end,nsides-n1-1, offset1+n1+1,offset2+n1+1);
			if(n2==0.)
				return 0;
		}
		else
			n2=0;
		return n1+n2+1;
	}
	public void calc_inertia(int start,int end,Inertia s)
	{
		int i;
		int coeff=1;
		int temp[]=new int[4];
		double dm=0;
		s.mass=0.;
		s.sx=0.;
		s.sxx=0.;
		s.sxy=0.;
		s.sy=0.;
		s.syy=0.;
		temp[0]=penPoints.get(start).x;
		temp[1]=penPoints.get(start).y;
		temp[2]=penPoints.get(start+1).x;
		temp[3]=penPoints.get(start+1).y;
		dm=coeff*Math.hypot(temp[2]-temp[0],temp[3]-temp[1]);
		s.mass=s.mass+dm;
		s.sx=s.sx+(dm*temp[0]);
		s.sxx=s.sxx+(dm*temp[0]*temp[0]);
		s.sxy=s.sxy+(dm*temp[0]*temp[1]);
		s.sy=s.sy+(dm*temp[1]);
		s.syy=s.syy+(dm*temp[1]*temp[1]);
		for(i=start+1;i<end;++i)
		{
			temp[0]=penPoints.get(i).x;
			temp[1]=penPoints.get(i).y;
			temp[2]=penPoints.get(i+1).x;
			temp[3]=penPoints.get(i+1).y;
			dm=coeff*Math.hypot(temp[2]-temp[0],temp[3]-temp[1]);
			s.mass=s.mass+dm;
			s.sx=s.sx+(dm*temp[0]);
			s.sxx=s.sxx+(dm*temp[0]*temp[0]);
			s.sxy=s.sxy+(dm*temp[0]*temp[1]);
			s.sy=s.sy+(dm*temp[1]);
			s.syy=s.syy+(dm*temp[1]*temp[1]);
		}
	}
	final double I_det(Inertia s)
	{
		double ixx=I_xx(s);
		double iyy=I_yy(s);
		double ixy=I_xy(s);
		if(s.mass <= 0.)
			return 0.;
		if(ixx+iyy <= 0.)
			return 0.;
		return 4*(ixx*iyy-ixy*ixy)/(ixx+iyy)/(ixx+iyy);
	}
	double I_xx(Inertia s)
	{
		if(s.mass <= 0.)
			return 0.;
		return (s.sxx - s.sx*s.sx/s.mass)/s.mass;
	}
	double I_xy(Inertia s)
	{
		if (s.mass <= 0.) 
			return 0.;
		return (s.sxy - s.sx*s.sy/s.mass)/s.mass;
	}
	double I_yy(Inertia s)
	{
		if (s.mass <= 0.) 
			return 0.;
		return (s.syy - s.sy*s.sy/s.mass)/s.mass;
	}
	double score_circle(int start, int end, Inertia s)
	{
		double sum, x0, y0, r0, dm, deltar;
		int i;
		if(s.mass==0.)
			return 0;
		sum=0.;
		x0=this.center_x(s);
		y0=this.center_y(s);
		r0=this.I_rad(s);
		for(i=start;i<end;++i)
		{
			dm=Math.hypot(penPoints.get(i+1).x-penPoints.get(i).x,penPoints.get(i+1).y-penPoints.get(i).y );
			deltar=Math.hypot(penPoints.get(i).x-x0,penPoints.get(i).y-y0)-r0;
			sum=sum+(dm*Math.abs(deltar));
		}
		return sum/(s.mass*r0);
	}
	
	double center_x(Inertia s)
	{
		return s.sx/s.mass;
	}
	
	double center_y(Inertia s)
	{
		return s.sy/s.mass;
	}
	
	double I_rad(Inertia s)
	{
		double ixx=this.I_xx(s);
		double iyy=this.I_yy(s);
		if(ixx+iyy<=0.)
			return 0.;
		return Math.sqrt(ixx+iyy);
	}
	
	void makeACircle(double x, double y, double r)
	{
		temp = new ArrayList<Point>();
		int npts, i=0;
		npts = (int)(2*r);
		if (npts<12) 
			npts = 12;
		Point p;
		for(i=0; i<=npts; i++)
		{			
			p = new Point();
			p.x = (int) (x + r*Math.cos((2*i*Math.PI)/npts));
			p.y = (int) (y + r*Math.sin((2*i*Math.PI)/npts));
			temp.add(p);
		}
		int size=temp.size();
		String equation=null;
		double x1=view.toRealWorldCoordX(temp.get(0).x);
		double y1=view.toRealWorldCoordY(temp.get(0).y);
		double x2=view.toRealWorldCoordX(temp.get(size/3).x);
		double y2=view.toRealWorldCoordY(temp.get(size/3).y);
		double x3=view.toRealWorldCoordX(temp.get(2*size/3).x);
		double y3=view.toRealWorldCoordY(temp.get(2*size/3).y);
		double m1=(y2-y1)/(x2-x1);
		double m2=(y3-y2)/(x3-x2); 
		if(x2 == x1)
		{
			x1=view.toRealWorldCoordX(temp.get(size/4).x);
			y1=view.toRealWorldCoordY(temp.get(size/4).y);
			m1=(y2-y1)/(x2-x1);
		}
		if(x2 == x3)
		{
			x3=view.toRealWorldCoordX(temp.get(11*size/12).x);
			y3=view.toRealWorldCoordY(temp.get(11*size/12).y);
			m2=(y3-y2)/(x3-x2);
		}
		double x_center=(((m1*m2)*(y1-y3)) + (m2*(x1+x2)) - (m1*(x2+x3)))/(2*(m2-m1));
		double y_center=((-1/m2)*(x_center-((x2+x3)/2))) + ((y2 + y3)/2);
		double rad = ((x_center - x2)*(x_center - x2)) + ((y_center - y2)*(y_center - y2));
		if(x_center>0 && y_center>0)
			equation = "(x - " + x_center + ")" + Unicode.Superscript_2 + "+ " + "(y - " + y_center + ")" + Unicode.Superscript_2  + "= " + rad;
		if(x_center<0 && y_center>0)
			equation = "(x + " + -x_center + ")" + Unicode.Superscript_2 + "+ " + "(y - " + y_center + ")" + Unicode.Superscript_2  + "= " + rad;
		if(x_center<0 && y_center<0)
			equation = "(x + " + -x_center + ")" + Unicode.Superscript_2 +  "+ " + "(y + " + -y_center + ")" + Unicode.Superscript_2  + "= " + rad;
		if(x_center>0 && y_center<0)
			equation = "(x - " + x_center + ")" + Unicode.Superscript_2 + "+ " + "(y + " + -y_center + ")" + Unicode.Superscript_2  + "= " + rad;
		GeoPoint2 p1 = new GeoPoint2(app.getKernel().getConstruction(), x1, y1, 1.0);
	    GeoPoint2 q = new GeoPoint2(app.getKernel().getConstruction(), x2, y2, 1.0);
	    GeoPoint2 z = new GeoPoint2(app.getKernel().getConstruction(), x3, y3, 1.0);
		AlgoCircleThreePoints algo=new AlgoCircleThreePoints(app.getKernel().getConstruction() , equation, p1, q, z);
		
		GeoElement circle = algo.getGeoElements()[0];
		circle.setLineThickness(penSize * 2);
		circle.setLineType(penLineStyle);
		circle.setObjColor(new geogebra.awt.Color(penColor));
		circle.setLayer(1);
		circle.updateRepaint();
		
	}
	/**
     * Returns delta x.
     *
     * @param startPoint2 First point
     * @param point Second point
     * @return Delta x
     */
    private int getDeltaX(java.awt.Point startPoint2, java.awt.Point point)
    {
        return point.x - startPoint2.x;
    }

    /**
     * Returns delta y.
     *
     * @param startPoint2 First point
     * @param point Second point
     * @return Delta y
     */
    private int getDeltaY(java.awt.Point startPoint2, java.awt.Point point) 
    {
        return point.y - startPoint2.y;
    }
    /**
     * Adds movement to buffer.
     *
     * @param move String representation of recognized movement
     */
    private void saveMove(String move)
    {
        // should not store two equal moves in succession
        if ((gesture.length() > 0) && (gesture.charAt(gesture.length() - 1) == move.charAt(0)))
            return;
        gesture.append(move);
    }
    /**
     * Returns string representation of mouse gesture.
     *
     * @return String representation of mouse gesture. "L" for left, "R" for right,
     *         "U" for up, "D" for down movements. For example: "ULD".
     */
    String getGesture()
    {
        return gesture.toString();
    }
    void clearTemporaryInfo()
    {
        startPoint = null;
        gesture.delete(0, gesture.length());
    }
    void optimize_polygonal(int nsides)
    {
    	int i;
    	double cost, newcost;
    	boolean improved;
    	Inertia temp1 = new Inertia();
    	Inertia temp2 = new Inertia();
    	for(i=1; i<nsides; ++i)
    	{
    		if((i-1) == 0)
    		{
    			temp1.mass = a.mass;
    			temp1.sx = a.sx;
    			temp1.sxx = a.sxx;
    			temp1.sxy = a.sxy;
    			temp1.sy = a.sy;
    			temp1.syy = a.syy;
    			temp2.mass = b.mass;
    			temp2.sx = b.sx;
    			temp2.sxx = b.sxx;
    			temp2.sxy = b.sxy;
    			temp2.sy = b.sy;
    			temp2.syy = b.syy;
    		}
    		if((i-1) == 1)
    		{
    			temp1.mass = b.mass;
    			temp1.sx = b.sx;
    			temp1.sxx = b.sxx;
    			temp1.sxy = b.sxy;
    			temp1.sy = b.sy;
    			temp1.syy = b.syy;
    			temp2.mass = c.mass;
    			temp2.sx = c.sx;
    			temp2.sxx = c.sxx;
    			temp2.sxy = c.sxy;
    			temp2.sy = c.sy;
    			temp2.syy = c.syy;
    		}
    		if((i-1) == 2)
    		{
    			temp1.mass = c.mass;
    			temp1.sx = c.sx;
    			temp1.sxx = c.sxx;
    			temp1.sxy = c.sxy;
    			temp1.sy = c.sy;
    			temp1.syy = c.syy;
    			temp2.mass = d.mass;
    			temp2.sx = d.sx;
    			temp2.sxx = d.sxx;
    			temp2.sxy = d.sxy;
    			temp2.sy = d.sy;
    			temp2.syy = d.syy;
    		}
    		cost = this.I_det(temp1)*this.I_det(temp1) + this.I_det(temp2)*this.I_det(temp2);
    		improved = false;
    		while(brk[i] > brk[i-1]+1)
    		{
    			this.incr_inertia(brk[i]-1, temp1, -1);
    			this.incr_inertia(brk[i]-1, temp2, 1);
    			newcost = this.I_det(temp1)*this.I_det(temp1) + this.I_det(temp2)*this.I_det(temp2);
    			if(newcost >= cost)
    				break;
    			improved = true;
    			cost = newcost;
    			brk[i]--;
    			if(i-1 == 0)
    			{
    				a.mass = temp1.mass;
    				a.sx = temp1.sx;
    				a.sy = temp1.sy;
    				a.sxx = temp1.sxx;
    				a.sxy = temp1.sxy;
    				a.syy = temp1.syy;
    				b.mass = temp2.mass;
    				b.sx = temp2.sx;
    				b.sy = temp2.sy;
    				b.sxx = temp2.sxx;
    				b.sxy = temp2.sxy;
    				b.syy = temp2.syy;
    			}
    			if(i-1 == 1)
    			{
    				b.mass = temp1.mass;
    				b.sx = temp1.sx;
    				b.sy = temp1.sy;
    				b.sxx = temp1.sxx;
    				b.sxy = temp1.sxy;
    				b.syy = temp1.syy;
    				c.mass = temp2.mass;
    				c.sx = temp2.sx;
    				c.sy = temp2.sy;
    				c.sxx = temp2.sxx;
    				c.sxy = temp2.sxy;
    				c.syy = temp2.syy;
    			}
    			if(i-1 == 2)
    			{
    				c.mass = temp1.mass;
    				c.sx = temp1.sx;
    				c.sy = temp1.sy;
    				c.sxx = temp1.sxx;
    				c.sxy = temp1.sxy;
    				c.syy = temp1.syy;
    				d.mass = temp2.mass;
    				d.sx = temp2.sx;
    				d.sy = temp2.sy;
    				d.sxx = temp2.sxx;
    				d.sxy = temp2.sxy;
    				d.syy = temp2.syy;
    	  		}
    		}
    		if(improved)
    			continue;
    		if((i-1) == 0)
    		{
    			temp1.mass = a.mass;
    			temp1.sx = a.sx;
    			temp1.sxx = a.sxx;
    			temp1.sxy = a.sxy;
    			temp1.sy = a.sy;
    			temp1.syy = a.syy;
    			temp2.mass = b.mass;
    			temp2.sx = b.sx;
    			temp2.sxx = b.sxx;
    			temp2.sxy = b.sxy;
    			temp2.sy = b.sy;
    			temp2.syy = b.syy;
    		}
    		if((i-1) == 1)
    		{
    			temp1.mass = b.mass;
    			temp1.sx = b.sx;
    			temp1.sxx = b.sxx;
    			temp1.sxy = b.sxy;
    			temp1.sy = b.sy;
    			temp1.syy = b.syy;
    			temp2.mass = c.mass;
    			temp2.sx = c.sx;
    			temp2.sxx = c.sxx;
    			temp2.sxy = c.sxy;
    			temp2.sy = c.sy;
    			temp2.syy = c.syy;
    		}
    		if((i-1) == 2)
    		{
    			temp1.mass = c.mass;
    			temp1.sx = c.sx;
    			temp1.sxx = c.sxx;
    			temp1.sxy = c.sxy;
    			temp1.sy = c.sy;
    			temp1.syy = c.syy;
    			temp2.mass = d.mass;
    			temp2.sx = d.sx;
    			temp2.sxx = d.sxx;
    			temp2.sxy = d.sxy;
    			temp2.sy = d.sy;
    			temp2.syy = d.syy;
    		}
    		while (brk[i] < brk[i+1]-1) 
    		{
    			this.incr_inertia(brk[i], temp1, 1);
    			this.incr_inertia(brk[i], temp2, -1);
    			newcost = (this.I_det(temp1)*this.I_det(temp1)) + (this.I_det(temp2)*this.I_det(temp2));
    			if(newcost >= cost)
    				break;
    			cost = newcost;
    			brk[i]++;
    			if(i-1 == 0)
    			{
    				a.mass = temp1.mass;
    				a.sx = temp1.sx;
    				a.sy = temp1.sy;
    				a.sxx = temp1.sxx;
    				a.sxy = temp1.sxy;
    				a.syy = temp1.syy;
    				b.mass = temp2.mass;
    				b.sx = temp2.sx;
    				b.sy = temp2.sy;
    				b.sxx = temp2.sxx;
    				b.sxy = temp2.sxy;
    				b.syy = temp2.syy;
    			}
    			if(i-1 == 1)
    			{
    				b.mass = temp1.mass;
    				b.sx = temp1.sx;
    				b.sy = temp1.sy;
    				b.sxx = temp1.sxx;
    				b.sxy = temp1.sxy;
    				b.syy = temp1.syy;
    				c.mass = temp2.mass;
    				c.sx = temp2.sx;
    				c.sy = temp2.sy;
    				c.sxx = temp2.sxx;
    				c.sxy = temp2.sxy;
    				c.syy = temp2.syy;
    			}
    			if(i-1 == 2)
    			{
    				c.mass = temp1.mass;
    				c.sx = temp1.sx;
    				c.sy = temp1.sy;
    				c.sxx = temp1.sxx;
    				c.sxy = temp1.sxy;
    				c.syy = temp1.syy;
    				d.mass = temp2.mass;
    				d.sx = temp2.sx;
    				d.sy = temp2.sy;
    				d.sxx = temp2.sxx;
    				d.sxy = temp2.sxy;
    				d.syy = temp2.syy;
    	  		}
    		}
    	}
    }
    void incr_inertia(int start, Inertia s, int coeff)
    {
    	double pt1_x = penPoints.get(start).x;
    	double pt1_y = penPoints.get(start).y;
    	double pt2_x = penPoints.get(start+1).x;
    	double pt2_y = penPoints.get(start+1).y;
    	double dm = 0;
    	dm = coeff*Math.hypot(pt2_x - pt1_x, pt2_y - pt1_y);
    	s.mass = s.mass + dm;
    	s.sx = s.sx + (dm*pt1_x);
    	s.sy = s.sy + (dm*pt1_y);
    	s.sxx = s.sxx + (dm*pt1_x*pt1_x);
    	s.syy = s.syy + (dm*pt1_y*pt1_y);
    	s.sxy = s.sxy + (dm*pt1_x*pt1_y);
    }
    void get_segment_geometry(int start, int end, Inertia s,RecoSegment r)
    {
    	double a, b, c, lmin, lmax, l;
    	int i;
    	r.xcenter = this.center_x(s);
    	r.ycenter = this.center_y(s);
    	a = this.I_xx(s);
    	b = this.I_xy(s);
    	c = this.I_yy(s);
    	r.angle = Math.atan2(2*b, a-c)/2;
    	r.radius = Math.sqrt(3*(a+c));
    	lmin=lmax=0;
    	for(i=start; i<=end; ++i)
    	{
    		l = (penPoints.get(start).x - r.xcenter)*Math.cos(r.angle) + (penPoints.get(start).y - r.ycenter)*Math.sin(r.angle);
    		if(l < lmin)
    			lmin = l;
    		if(l > lmax)
    			lmax = l;
    		start++;
    	}
    	r.x1 = r.xcenter + lmin*Math.cos(r.angle);
    	r.y1 = r.ycenter + lmin*Math.sin(r.angle);
    	r.x2 = r.xcenter + lmax*Math.cos(r.angle);
    	r.y2 = r.ycenter + lmax*Math.sin(r.angle);
    }
    boolean try_rectangle()
    {
    	RecoSegment rs = null;
    	RecoSegment r1 = null;
    	RecoSegment r2 = null;
    	int i;
    	double dist, avg_angle=0;
    	if(recognizer_queue_length < 4)
    		return false;
    	if(recognizer_queue_length-4 == 0)
    		rs = reco_queue_a;
    	if(recognizer_queue_length-4 == 1)
    		rs = reco_queue_b;
    	if(recognizer_queue_length-4 == 2)
    		rs = reco_queue_c;
    	if(recognizer_queue_length-4 == 3)
    		rs = reco_queue_d;
    	if(recognizer_queue_length-4 == 4)
    		rs = reco_queue_e;
    	//System.out.println(rs.startpt);
    	if(rs.startpt != 0)
    		return false;
    	for(i=0; i<=3; ++i)
    	{
    		if(recognizer_queue_length-4+i == 0)
    			r1 = reco_queue_a;
    		if(recognizer_queue_length-4+i == 1)
    			r1 = reco_queue_b;
    		if(recognizer_queue_length-4+i == 2)
    			r1 = reco_queue_c;
    		if(recognizer_queue_length-4+i == 3)
    			r1 = reco_queue_d;
    		if(recognizer_queue_length-4+i == 4)
    			r1 = reco_queue_e;
    		if(recognizer_queue_length-4+((i+1)%4) == 0)
    			r2 = reco_queue_a;
    		if(recognizer_queue_length-4+((i+1)%4) == 1)
    			r2 = reco_queue_b;
    		if(recognizer_queue_length-4+((i+1)%4) == 2)
    			r2 = reco_queue_c;
    		if(recognizer_queue_length-4+((i+1)%4) == 3)
    			r2 = reco_queue_d;
    		if(recognizer_queue_length-4+((i+1)%4) == 4)
    			r2 = reco_queue_e;
    		//System.out.println(Math.abs(Math.abs(r1.angle-r2.angle)-Math.PI/2) > RECTANGLE_ANGLE_TOLERANCE);
    		if(Math.abs(Math.abs(r1.angle-r2.angle)-Math.PI/2) > RECTANGLE_ANGLE_TOLERANCE)
    			return false;
    		avg_angle = avg_angle + r1.angle;
    		if(r2.angle > r1.angle)
    			avg_angle = avg_angle + ((i+1)*Math.PI/2);
    		else
    			avg_angle = avg_angle - ((i+1)*Math.PI/2);
    		r1.reversed = ((r1.x2 - r1.x1)*(r2.xcenter - r1.xcenter) + (r1.y2 - r1.y1)*(r2.ycenter - r1.ycenter)) < 0;
    	}
    	for(i=0; i<=3; ++i)
    	{
    		if(recognizer_queue_length-4+i == 0)
    			r1 = reco_queue_a;
    		if(recognizer_queue_length-4+i == 1)
    			r1 = reco_queue_b;
    		if(recognizer_queue_length-4+i == 2)
    			r1 = reco_queue_c;
    		if(recognizer_queue_length-4+i == 3)
    			r1 = reco_queue_d;
    		if(recognizer_queue_length-4+i == 4)
    			r1 = reco_queue_e;
    		if(recognizer_queue_length-4+((i+1)%4) == 0)
    			r2 = reco_queue_a;
    		if(recognizer_queue_length-4+((i+1)%4) == 1)
    			r2 = reco_queue_b;
    		if(recognizer_queue_length-4+((i+1)%4) == 2)
    			r2 = reco_queue_c;
    		if(recognizer_queue_length-4+((i+1)%4) == 3)
    			r2 = reco_queue_d;
    		if(recognizer_queue_length-4+((i+1)%4) == 4)
    			r2 = reco_queue_e;
    		dist = Math.hypot((r1.reversed?r1.x1:r1.x2) - (r2.reversed?r2.x2:r2.x1), (r1.reversed?r1.y1:r1.y2) - (r2.reversed?r2.y2:r2.y1));
    		if(dist > RECTANGLE_LINEAR_TOLERANCE*(r1.radius+r2.radius))
    			return false;
    	}
    	avg_angle = avg_angle/4;
    	if(Math.abs(avg_angle) < SLANT_TOLERANCE)
    		avg_angle = 0;
    	if(Math.abs(avg_angle) > Math.PI/2-SLANT_TOLERANCE)
    		avg_angle = Math.PI/2;
    	return true;
    }
}
