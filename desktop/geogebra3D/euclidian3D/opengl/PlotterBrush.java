package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Coords3D;
import geogebra.common.kernel.geos.GeoCurveCartesian3DInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.euclidian3D.plots.CurveMesh;
import geogebra3D.euclidian3D.plots.CurveTree;
import geogebra3D.euclidian3D.plots.MarchingCubes;

import java.awt.Color;
import java.nio.FloatBuffer;

/**
 * 3D brush, drawing circular-section curves.
 * 
 * @author mathieu
 *
 */
public class PlotterBrush {

	/** thickness for drawing 3D lines*/
	public static final float LINE3D_THICKNESS = 0.5f;

	
	/** manager */
	private Manager manager;
	
	/** index */
	private int index;
	
	/** start and end sections*/
	private PlotterBrushSection start, end;
	
	/** current thickness */
	private float thickness;
	private int lineThickness;
	
	/** view scale */
	private float scale;
	
	/** global length of the curve */
	private float length;
	
	
	//color
	/** color r, g, b, a */
	private float red, green, blue, alpha;
	/** says if it's colored */
	private boolean hasColor;
	
	
	//texture
	/** start and end textures values */
	private float texturePosZero, textureValZero;
	/** textures coords */
	private float[] textureX = new float[2];
	private float[] textureY = new float[2];
	/** type of texture */
	static final public int TEXTURE_CONSTANT_0 = 0; 
	static final private int TEXTURE_ID = 1;
	static final private int TEXTURE_AFFINE = 2;
	static final private int TEXTURE_LINEAR = 3;
	private int textureTypeX = TEXTURE_ID;
	private int textureTypeY = TEXTURE_CONSTANT_0;
	
	static final private float TEXTURE_AFFINE_FACTOR = 0.05f; 
	
	/** curve position (for texture) */
	private float curvePos;
	
	// arrows	
	/** no arrows */
	static final public int ARROW_TYPE_NONE=0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE=1;
	/** closed segment */
	static final public int ARROW_TYPE_CLOSED=2;
	
	
	
	private int arrowType=ARROW_TYPE_NONE;
	/** length of the arrow */
	static private float ARROW_LENGTH = 3f;
	/** width of the arrow */	
	static private float ARROW_WIDTH = ARROW_LENGTH/4f;
	///** length of the arrow */
	//static private float ARROW_HANDLE_LENGTH = ARROW_LENGTH;//ARROW_WIDTH;
	
	
	// ticks	
	/** no ticks */
	static final public boolean TICKS_OFF=false;
	/** with ticks */
	static final public boolean TICKS_ON=true;
	/** has ticks ? */
	private boolean ticks = TICKS_OFF;
	/** distance between two ticks */
	private float ticksDistance;
	/** offset for origin of the ticks (0: start of the curve, 1: end of the curve) */
	private float ticksOffset;
	
	
	//for GeoCartesianCurve
	/** curve */
	GeoCurveCartesian3DInterface curve;
	
	//level of detail
	/** number of rules */
	private int latitude;
	
	
	/** default constructor
	 * @param manager
	 */
	public PlotterBrush(Manager manager){
		this.manager = manager;
	}


	////////////////////////////////////
	// START AND END
	////////////////////////////////////
	
	/**
	 * start new curve
	 * @param latitude number of rules
	 */
	public void start(int latitude){
		index = manager.startNewList();
		hasColor = false;
		this.latitude = latitude;
		
	}
	
	
	/** end curve
	 * @return gl index of the curve
	 */
	public int end(){
		manager.endList();
		return index;
	}
	
	////////////////////////////////////
	// SIMPLE DRAWING METHODS
	////////////////////////////////////

	/** start new curve part
	 * @param point
	 */
	public void down(Coords point){
		
		down(point,null,null);
	}
	
	/** start new curve part
	 * @param point
	 */
	private void down(Coords point, Coords clockU, Coords clockV){
		
		start = new PlotterBrushSection(point,thickness, clockU, clockV);
		end = null;
	}
	
	/** move to point and draw curve part
	 * @param point
	 */
	public void moveTo(Coords point){
		// update start and end sections
		if (end==null){
			end = new PlotterBrushSection(start, point, thickness,true);
		}else{
			start = end;
			end = new PlotterBrushSection(start, point, thickness,false);
		}
		
		join();
	}
	
	
	/** move to point and draw curve part
	 * @param point
	 */
	private void moveTo(Coords point, Coords clockU, Coords clockV){

		if (end!=null)
			start = end;
		
		end = new PlotterBrushSection(point, thickness, clockU, clockV);
		
		join();
	}	
	
	/**
	 * join start to end
	 */
	public void join(){
		
		// draw curve part
		manager.startGeometry(Manager.QUAD_STRIP);
		if(hasColor)
			manager.color(red, green, blue, alpha);
		//manager.startGeometry(Manager.TRIANGLE_STRIP);
    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	float u, v;    	
    	for( int i = 0; i <= latitude  ; i++ ) { 
    		u = (float) Math.sin ( i * da ); 
    		v = (float) Math.cos ( i * da ); 
    		setTextureY(i*dt);
    		//Application.debug("i="+i);
    		draw(start,u, v, 0); //bottom of the tube rule
    		draw(end,u, v, 1); //top of the tube rule
    	}
    	
		manager.endGeometry();
	}
	
	
	
	/** draws a section point
	 * 
	 */
	private void draw(PlotterBrushSection s, double u, double v, int texture){
		
		Coords[] vectors = s.getNormalAndPosition(u, v);
		
		//set normal
		manager.normal(vectors[0]);
		
		//set texture
		float pos = textureX[texture];
		switch(textureTypeX){
		case TEXTURE_ID:
		default:
			manager.texture(pos, 0);
			break;
		case TEXTURE_CONSTANT_0:
			manager.texture(0, 0);
			break;
		case TEXTURE_AFFINE:
			//float factor = (int) (TEXTURE_AFFINE_FACTOR*length*scale); //TODO integer for cycles
			float factor =  (TEXTURE_AFFINE_FACTOR*length*scale);
			manager.texture(factor*(pos-texturePosZero)+textureValZero, 0);
			break;
		case TEXTURE_LINEAR:
			manager.texture(TEXTURE_AFFINE_FACTOR*scale*pos,0);
			break;

		}
		
		//set vertex
		manager.vertex(vectors[1]);
		
	}
	


	////////////////////////////////////
	// GEOMETRY DRAWING METHODS
	////////////////////////////////////
	
	/** segment curve
	 * @param p1 
	 * @param p2
	 */
	public void segment(Coords p1, Coords p2){
		
		length = (float) p1.distance(p2);
		if (Kernel.isEqual(length, 0, Kernel.STANDARD_PRECISION))
			return;

		down(p1);
		
		float factor, arrowPos;
		Coords arrowBase;
		
		switch(arrowType){
		case ARROW_TYPE_NONE:
		default:
			setTextureX(0,1);
			moveTo(p2);
			break;
		case ARROW_TYPE_SIMPLE:
			factor = (12+lineThickness)*LINE3D_THICKNESS/scale;
			arrowPos = ARROW_LENGTH/length * factor;
			arrowBase = start.getCenter().mul(arrowPos).add(p2.mul(1-arrowPos));

			setTextureX(0);
			if (hasTicks()){
				Coords d = p2.sub(p1).normalized();
				float thickness = this.thickness;
				
				float i = ticksOffset*length-((int) (ticksOffset*length/ticksDistance))*ticksDistance;
				float ticksDelta = thickness;
				float ticksThickness = 4*thickness;
				if (i<=ticksDelta)
					i+=ticksDistance;

				for(;i<=length*(1-arrowPos);i+=ticksDistance){
					
					Coords p1b=p1.add(d.mul(i-ticksDelta));
					Coords p2b=p1.add(d.mul(i+ticksDelta));
					
					setTextureType(TEXTURE_AFFINE);
					setTextureX(i/length);
					moveTo(p1b);
					setThickness(ticksThickness);
					setTextureType(TEXTURE_CONSTANT_0);
					moveTo(p1b);
					moveTo(p2b);
					setThickness(thickness);
					moveTo(p2b);
					
				}
			}
			
			setTextureType(TEXTURE_AFFINE);
			setTextureX(1-arrowPos);
			moveTo(arrowBase);
			
			
			textureTypeX = TEXTURE_ID;
			setTextureX(0,0);
			setThickness(factor*ARROW_WIDTH);
			moveTo(arrowBase);
			setThickness(0);
			moveTo(p2);
			break;
		}
		
		
		if (arrowType==ARROW_TYPE_CLOSED){
			setThickness(0);
			moveTo(p2);
		}
	}
	
	
	/** draws a circle
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 */
	public void circle(Coords center, Coords v1, Coords v2, double radius){
		
		
		length=(float) (2*Math.PI*radius); //TODO use integer to avoid bad dash cycle connection
		
		int longitude = 60;
		
		Coords vn1;
		Coords vn2 = v1.crossProduct(v2);
		
    	float dt = (float) 1/longitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	float u=0, v=1;
    	
    	setTextureX(0,0);
		vn1 = v1.mul(u).add(v2.mul(v));
		down(center.add(vn1.mul(radius)),vn1,vn2);  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.sin ( i * da ); 
    		v = (float) Math.cos ( i * da ); 
    		
    		setTextureX(i*dt);
    		vn1 = v1.mul(u).add(v2.mul(v));
    		moveTo(center.add(vn1.mul(radius)),vn1,vn2);
    	} 
    	
	}

	
	public void arc(Coords center, Coords v1, Coords v2, double radius, double start, double extent){
		
		
		length=(float) (extent*radius); //TODO use integer to avoid bad dash cycle connection
		
		int longitude = 60;
		
		Coords vn1;
		Coords vn2 = v2.crossProduct(v1);
		
    	float dt = (float) 1/longitude;
    	float da = (float) (extent *dt) ; 
    	float u, v;
		u = (float) Math.cos (start); 
		v = (float) Math.sin (start); 
   	
    	setTextureX(0,0);
		vn1 = v1.mul(u).add(v2.mul(v));
		down(center.add(vn1.mul(radius)),vn1,vn2);  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cos (start + i * da ); 
    		v = (float) Math.sin (start + i * da ); 
    		
    		setTextureX(i*dt);
    		vn1 = v1.mul(u).add(v2.mul(v));
    		moveTo(center.add(vn1.mul(radius)),vn1,vn2);
    	} 
    	
	}

	
	/** draws an ellipse
	 * @param center
	 * @param v1 1st eigenvector
	 * @param v2 2nd eigenvector
	 * @param a  1st eigenvalue
	 * @param b  2nd eigenvalue
	 *  	 */
	public void arcEllipse(Coords center, Coords v1, Coords v2, double a, double b, double start, double extent){
		
		//Ramanujan approximation
		//length=(float) (Math.PI*(3*(a+b)-Math.sqrt((3*a+b)*(a+3*b)))); //TODO use integer to avoid bad dash cycle connection
		length=1;
		

		setCurvePos(0);
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		
		//foci
		double f = Math.sqrt(a*a-b*b);
		Coords f1 = v1.mul(f);
		Coords f2 = v1.mul(-f);

		
		int longitude = 60;
		
		Coords m,mold,vn1;
		Coords vn2 = v2.crossProduct(v1);
		
    	float dt = (float) 1/longitude;
    	float da = (float) (extent *dt) ; 
    	float u, v;
		u = (float) Math.cos (start); 
		v = (float) Math.sin (start); 
     	
		m = v1.mul(a*u).add(v2.mul(b*v));
		vn1 = (m.sub(f1).normalized()).add((m.sub(f2).normalized())).normalized(); //bissector
		down(center.add(m),vn1,vn2);  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cos (start + i * da ); 
    		v = (float) Math.sin (start + i * da ); 
    		
    		mold=m;
    		m = v1.mul(a*u).add(v2.mul(b*v));
    		addCurvePos((float) m.sub(mold).norm());
    		
    		vn1 = (m.sub(f1).normalized()).add((m.sub(f2).normalized())).normalized(); //bissector
    		moveTo(center.add(m),vn1,vn2);
    	} 
    	
	}
	
	
	
		
	/** draws quarter of an hyperbola
	 * @param center center
	 * @param v1 1st eigenvector
	 * @param v2 2nd eigenvector
	 * @param a  1st eigenvalue
	 * @param b  2nd eigenvalue
	 * @param tMin t min
	 * @param tMax t max
	 */
	public void hyperbolaBranch(Coords center, Coords v1, Coords v2, double a, double b,
				double tMin, double tMax){
				
		
		//foci
		double f = Math.sqrt(a*a+b*b);
		Coords f1 = v1.mul(f);
		Coords f2 = v1.mul(-f);
		
		//dash
		length=1;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		setCurvePos(0.75f/(TEXTURE_AFFINE_FACTOR*scale)); //midpoint is middle of an empty dash
		
		int longitude = 120;
		
		Coords m,mold,vn1;
		Coords vn2 = v1.crossProduct(v2);
		
		
    	float dt = (float) (tMax-tMin)/longitude;
    	
    	float u, v;
    	u = (float) Math.cosh ( tMin ); 
		v = (float) Math.sinh ( tMin );  
		

		
		m = v1.mul(a*u).add(v2.mul(b*v));
		vn1 = (m.sub(f1).normalized()).sub((m.sub(f2).normalized())).normalized(); //bissector
		down(center.add(m),vn1,vn2);  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cosh ( tMin + i * dt ); 
    		v = (float) Math.sinh ( tMin + i * dt ); 
    		
       		mold=m;
       		m = v1.mul(a*u).add(v2.mul(b*v));
    		addCurvePos((float) m.sub(mold).norm());

    		vn1 = (m.sub(f1).normalized()).sub((m.sub(f2).normalized())).normalized(); //bissector
    		moveTo(center.add(m),vn1,vn2);
    	} 
    	
	}

	
	/** draws a parabola, and save ends coords in p1, p2 (if not null)
	 * @param center center
	 * @param v1 1st eigenvector
	 * @param v2 2nd eigenvector
	 * @param p eigenvalue
	 * @param tMin t min
	 * @param tMax t max
	 * @param p1 to store start point
	 * @param p2 to store end point
	 */
	public void parabola(Coords center, Coords v1, Coords v2, double p,
			double tMin, double tMax, Coords p1, Coords p2){
		
		//focus
		Coords f1 = v1.mul(p/2);

		Coords vn2 = v1.crossProduct(v2);

		int longitude = 120;

		//dash
		length=1;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		setCurvePos(0.75f/(TEXTURE_AFFINE_FACTOR*scale)); 

		Coords m,vn1,mold;


		float dt = (float) (tMax-tMin)/longitude;

		float u, v; 
		double t;
		t=tMin;
		u = (float) ( p*t*t/2 ); 
		v = (float) ( p*t); 



		m = v1.mul(u).add(v2.mul(v));
		vn1 = (m.sub(f1).normalized()).sub(v1).normalized(); //bissector
		if (p1!=null){
			p1.set(center.add(m));
			down(p1,vn1,vn2);  	
		}else{
			down(center.add(m),vn1,vn2); 
		}
		

		for( int i = 1; i <= longitude  ; i++ ) { 

			t= tMin + i * dt;
			u = (float) ( p*t*t/2 ); 
			v = (float) ( p*t);     		

			mold=m;
			m = v1.mul(u).add(v2.mul(v));    		
			addCurvePos((float) m.sub(mold).norm());

			vn1 = (m.sub(f1).normalized()).sub(v1).normalized(); //bissector
			moveTo(center.add(m),vn1,vn2);
		} 
		
		if (p2!=null){
			p2.set(center.add(m));
		}

	}

	////////////////////////////////////
	// 3D CURVE DRAWING METHODS
	////////////////////////////////////
	
	private boolean firstCurvePoint = false;
	private Coords previousPosition;
	private Coords previousTangent;
	
	/**
	 * Starts drawing a curve
	 */
	private void startDrawingCurve(){
		firstCurvePoint = true;
	}
	
	/** adds the point with the specified position and tangent to the curve currently being drawn.
	 * @param position
	 * @param tangent
	 */
	public void addPointToCurve(Coords position, Coords tangent){
		if(firstCurvePoint){
			end = new PlotterBrushSection(position, tangent, thickness);
			firstCurvePoint=false;
		}
		else {
			if(discontinuityPassed(position)) {
				startDrawingCurve();				//start drawing a new segment
				addPointToCurve(position,tangent);
				return;
			} else {
				start = end;
				end = new PlotterBrushSection(start,position,tangent,thickness);
	
				addCurvePos(0);
				join();
			}
		}
		previousPosition = position;
		previousTangent  = tangent;
	}
	
	/** adds the point with the specified position and tangent to the curve currently being drawn.
	 * @param p the point's position vector
	 * @param t the tangent at the point
	 */
	public void addPointToCurve3D(Coords3D p, Coords3D t){
		Coords position = new Coords(p.getX(),p.getY(),p.getZ(),0);
		Coords tangent = new Coords(t.getX(),t.getY(),t.getZ(),0);
		if(firstCurvePoint){
			end = new PlotterBrushSection(position, tangent, thickness);
			firstCurvePoint=false;
		}
		else {
			if(discontinuityPassed(position)) {
				startDrawingCurve();				//start drawing a new segment
				addPointToCurve(position,tangent);
				return;
			} else {
				start = end;
				end = new PlotterBrushSection(start,position,tangent,thickness);
	
				addCurvePos((float) position.sub(start.getCenter()).norm());
				join();
			}
		}
		previousPosition = position;
		previousTangent  = tangent;
	}
	
	/** A test used to judge if the curve has passed over a discontinuity since
	 *  the last point was added.
	 * @param position the position of the new point (pos2)
	 * @return true iff (pos2-pos1)/||pos2-pos1|| . tangent1 < CurveTree.discontinuityThreshold
	 */
	private boolean discontinuityPassed(Coords position) {
		Coords dir = position.sub(previousPosition).normalized();
		
		if(dir.dotproduct(previousTangent)<CurveTree.discontinuityThreshold)
			return true;
		return false;
	}
	
	/** draws the curve defined by tree, in the viewing volume of a sphere
	 *  with radius r centered at the origin
	 * @param tree
	 * @param radius the radius of a sphere bounding the viewing volume
	 */
	public void draw(CurveTree tree, double radius){
		
		

		setTextureType(PlotterBrush.TEXTURE_LINEAR);
		
		tree.setRadius(radius);
		
		startDrawingCurve();
		
		//draw the start point if visible and defined
		tree.drawStartPointIfVisible(this);
		
		tree.beginRefinement(this);
		
		//draw the end point if visible and defined
		tree.drawEndPointIfVisible(this);
	}
	
	public void draw(CurveMesh mesh){
 		FloatBuffer b1 = mesh.getVertices();
		FloatBuffer b2 = mesh.getNormals();
		int cnt = mesh.getVisibleChunks();
		int vps = mesh.getVerticesPerChunk();
		
		manager.texture(0, 0);
		
		float[] f = new float[3]; float[] n = new float[3];
		b1.rewind(); b2.rewind();
		for(int i = 0; i < cnt; i++) {
			manager.startGeometry(Manager.TRIANGLE_STRIP);
			
			for(int j = 0; j < vps; j++){
				b1.get(f);b2.get(n);
				manager.normal(n[0],n[1],n[2]);
				manager.vertex(f[0],f[1],f[2]);
			}
			manager.endGeometry();
		}
	}
	
	public void draw(MarchingCubes mc) {
 		FloatBuffer b1 = mc.getVertices();
		int cnt = mc.getVisibleChunks();
		int vps = mc.getVerticesPerChunk();
		
		manager.texture(0, 0);
		
		float[] f = new float[3];
		b1.rewind();
		for(int i = 0; i < cnt; i++) {
			manager.startGeometry(Manager.TRIANGLE_STRIP);
			
			for(int j = 0; j < vps; j++){
				b1.get(f);
				manager.normal(1,0,0);
				manager.vertex(f[0],f[1],f[2]);
			}
			manager.endGeometry();
		}
	}
	
	////////////////////////////////////
	// THICKNESS
	////////////////////////////////////

	/** set the current thickness of the brush, using integer for thickness (see {@link GeoElement#getLineThickness()}}
	 * @param thickness
	 * @param scale 
	 * @return real world thickness
	 */
	public float setThickness(int thickness, float scale){
		
		this.lineThickness = thickness;
		this.scale = scale;
		
		float t = lineThickness*LINE3D_THICKNESS/scale;
		setThickness(t);
		return t;
		
	}
	
	/** set the current thickness of the brush
	 * @param thickness
	 */
	public void setThickness(float thickness){
		this.thickness = thickness;
	}
	
	
	
	////////////////////////////////////
	// COLOR
	////////////////////////////////////

	/** sets the current color
	 * @param color
	 * @param alpha
	 */
	public void setColor(Color color, float alpha){
		this.red = color.getRed()/255f;
		this.green = color.getGreen()/255f;
		this.blue = color.getBlue()/255f;
		this.alpha = alpha;
		hasColor = true;
	}
	
	/** sets the current color (alpha set to 1)
	 * @param color
	 */
	public void setColor(Color color){
		setColor(color,1);
	}
	
	

	////////////////////////////////////
	// TEXTURE
	////////////////////////////////////
	
	/**
	 * sets the position of the point on the curve
	 * and sets the texture x
	 * @param pos 
	 * 
	 */
	public void setCurvePos(float pos){
		curvePos = pos;
		setTextureX(pos);
	}
	
	/**
	 * add the distance to the position on the curve
	 * @param distance 
	 * 
	 */
	public void addCurvePos(float distance){
		setCurvePos(curvePos+distance);
	}
	
	/** set affine texture zero position
	 * @param posZero position of the "center" of the cylinder
	 * @param valZero texture coord for the "center"
	 */	
	public void setAffineTexture(float posZero, float valZero){

		texturePosZero = posZero;
		textureValZero = valZero;
		setTextureType(TEXTURE_AFFINE);
	}
	

	
	/**
	 * 
	 */
	public void setPlainTexture(){
		setTextureType(TEXTURE_CONSTANT_0);
	}
	
	
	/** sets the type of texture
	 * @param type
	 */
	private void setTextureType(int type){
		textureTypeX = type;
	}


	
	private void setTextureX(float x0, float x1){
		this.textureX[0] = x0;
		this.textureX[1] = x1;
	}
	
	private void setTextureX(float x){
		setTextureX(textureX[1],x);
	}

	
	private void setTextureY(float y1){
		this.textureY[0] = y1;
		this.textureY[1] = y1;
	}

	
	
	
	
	////////////////////////////////////
	// ARROWS
	////////////////////////////////////
	
    /**
     * sets the type of arrow used by the pencil.
     * @param arrowType type of arrow, see {@link #ARROW_TYPE_NONE}, {@link #ARROW_TYPE_SIMPLE}, ... 
     */
    public void setArrowType(int arrowType){
    	this.arrowType = arrowType;
    } 
	
	////////////////////////////////////
	// TICKS
	////////////////////////////////////
	
    /**
     * sets the type of arrow used by the pencil.
     * @param ticks 
     */
    public void setTicks(boolean ticks){
    	this.ticks = ticks;
    } 
    
    /**
     * sets the distance between two ticks
     * @param distance
     */
    public void setTicksDistance(float distance){
    	this.ticksDistance = distance;
    }
    
    /**
     * sets the offset for origin of the ticks (0: start of the curve, 1: end of the curve)
     * @param offset
     */
    public void setTicksOffset(float offset){
    	this.ticksOffset = offset;
    }
    
    /**
     * @return true if it draws ticks
     */
    public boolean hasTicks(){
    	return ticks && (ticksDistance>0);
    }
}
