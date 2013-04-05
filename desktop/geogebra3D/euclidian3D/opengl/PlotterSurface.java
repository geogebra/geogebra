package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.Functional2Var;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra3D.euclidian3D.plots.MarchingCubes;
import geogebra3D.euclidian3D.plots.SurfaceMesh2;

import java.nio.FloatBuffer;

/** Class for drawing surfaces.
 * @author matthieu
 *
 */
public class PlotterSurface {
	
	/** manager */
	private Manager manager;

	/** index */
	private int index;
	
	/** 2-var function */
	private Functional2Var functional2Var;
	
	private GeoFunctionNVar function;
	
	/** domain for plotting */
	private float uMin, uMax, vMin, vMax;
	
	/** number of plotting */
	private int uNb, vNb ;
	
	/** delta for plotting */
	private float du, dv;
	
	/** fading values */
	private float uMinFade, uMaxFade, vMinFade, vMaxFade;
	private float uMinFadeNb, uMaxFadeNb, vMinFadeNb, vMaxFadeNb;
	
	
	/** texture coord for out (alpha = 0) */
	static final private float TEXTURE_FADE_OUT = 0.75f;
	/** texture coord for in (alpha = 1) */
	static final private float TEXTURE_FADE_IN = 0f;
	

	/** default constructor
	 * @param manager
	 */
	public PlotterSurface(Manager manager){
		this.manager = manager;
		
	}
	
	
	////////////////////////////////////
	// START AND END
	////////////////////////////////////
	
	/**
	 * start new surface
	 */
	public void start(){
		index = manager.startNewList();
	}
	
	/**
	 * start new surface
	 * @param function 
	 */
	public void start(Functional2Var function){
		index = manager.startNewList();
		this.functional2Var = function;
		uMinFade = 0; vMinFade = 0;
		uMaxFade = 0; vMaxFade = 0;
		
	}
	
	/**
	 * start new surface
	 * @param function 
	 */
	public void start(GeoFunctionNVar function){
		index = manager.startNewList();
		this.function = function;
		uMinFade = 0; vMinFade = 0;
		uMaxFade = 0; vMaxFade = 0;
		
	}

	
	
	/** end surface
	 * @return gl index of the surface
	 */
	public int end(){
		manager.endList();
		return index;
	}


	////////////////////////////////////
	// DRAWING METHODS
	////////////////////////////////////

	
	/** set domain for u parameter
	 * @param min
	 * @param max
	 */
	public void setU(float min, float max){
		this.uMin = min;
		this.uMax = max;
	}
	
	/** set domain for v parameter
	 * @param min
	 * @param max
	 */
	public void setV(float min, float max){
		this.vMin = min;
		this.vMax = max;
	}	
	
	
	/** set number of plot for u
	 * @param n
	 */
	public void setNbU(int n){
		this.uNb = n;
	}
	
	/** set number of plot for v
	 * @param n
	 */
	public void setNbV(int n){
		this.vNb = n;
	}	
	
	
	
	/** set fading frontiers for u parameter
	 * @param min
	 * @param max
	 */
	public void setUFading(float min, float max){
		this.uMinFade = min;
		this.uMaxFade = max;
	}
	
	/** set fading frontiers for v parameter
	 * @param min
	 * @param max
	 */
	public void setVFading(float min, float max){
		this.vMinFade = min;
		this.vMaxFade = max;
	}
	
	public void drawTriangle(Coords p1, Coords p2, Coords p3){
		manager.startGeometry(Manager.TRIANGLE_STRIP);
		
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		
		manager.vertex(p1);
		manager.vertex(p3);
		manager.vertex(p2);
		manager.endGeometry();
	}
	
	/**
	 * draw a quadrilateral
	 * @param p1 point 1
	 * @param p2 point 2
	 * @param p3 point 3
	 * @param p4 point 4
	 */
	public void drawQuad(Coords p1, Coords p2, Coords p3, Coords p4){
		
		manager.startGeometry(Manager.QUAD_STRIP);
		
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		
		manager.vertex(p1);
		manager.vertex(p2);
		manager.vertex(p4);
		manager.vertex(p3);
		manager.endGeometry();
	}
	
	
	/**
	 * 
	 * @param radius radius of the sphere
	 * @param viewScale view scale
	 * @return longitude length needed to render the sphere
	 */
	public int calcSphereLongitudesNeeded(double radius, double viewScale){
		
		int longitude=8;
		double size=radius*viewScale;
		//App.error(""+size);
		while(longitude*longitude<=16*size){//find the correct longitude size 
			longitude*=2;
		}
		
		//App.debug("longitude="+longitude);
		return longitude;
	}
	
	
	/**
	 * draw a sphere with center and radius.
	 * view scaling is used to know how many triangles are needed
	 * @param center center of the sphere
	 * @param radius radius of the sphere
	 * @param viewScale view scaling
	 * @return calculated longitude
	 */
	public int drawSphere(Coords center, double radius, double viewScale){
		
		int longitude = calcSphereLongitudesNeeded(radius, viewScale);
		
		drawSphere(center, radius, longitude);
		
		return longitude;
	}
	
	
	/**
	 * draw a sphere with center and radius.
	 * view scaling is used to know how many triangles are needed
	 * @param center center of the sphere
	 * @param radius radius of the sphere
	 * @param longitude longitude length for rendering
	 */
	public void drawSphere(Coords center, double radius, int longitude){
		
		manager.startGeometry(Manager.TRIANGLES);
		
		//set texture to (0,0)
		manager.texture(0,0);
		
		int latitude=longitude/4;
		
		Coords[] n = new Coords[longitude];
		Coords n1, n2, n1b, n2b;
		
		double[] cosSinV = new double[2]; 
		
		//equator
		cosSinV[0] = 1; // cos(0)
		cosSinV[1] = 0; // sin(0)
		double lastCos = 1;
		for (int ui=0; ui<longitude; ui++){			
			n[ui]=sphericalCoords(ui,longitude,cosSinV);
		}
		
		//shift for longitude
		int shift = 1;
		
		boolean jumpNeeded = false;

		for (int vi=1; vi<latitude; vi++){		
			
			cosSin(vi, latitude, cosSinV);

			// check if parallel is small enough to make jumps
			if (2*cosSinV[0] < lastCos){
				lastCos = lastCos / 2;
				jumpNeeded = true;
			}else{
				jumpNeeded = false;
			}
						
			
			//first values 
			n2 = n[longitude - shift];
			if (jumpNeeded){
				n2b = sphericalCoords(longitude , longitude, cosSinV);
			}else{
				n2b = sphericalCoords(longitude - shift, longitude, cosSinV);
			}

			//first : no jump
			boolean jump = false;

			
			for (int ui=0; ui<longitude; ui += shift){
			
				//last latitude values
				n1 = n2;
				n2 = n[ui];
				
				
				//new latitude values and draw triangles
				n1b = n2b;
				if (jumpNeeded){
					if (jump){ //draw edge triangle and center triangle
						n2b = sphericalCoords(ui+shift, longitude, cosSinV);

						//top triangles
						drawNCr(n1,center,radius);
						drawNCr(n2,center,radius);
						drawNCr(n1b,center,radius);

						drawNCr(n1b,center,radius);
						drawNCr(n2,center,radius);
						drawNCr(n2b,center,radius);

						//bottom triangles
						drawNCrm(n1,center,radius);
						drawNCrm(n1b,center,radius);
						drawNCrm(n2,center,radius);

						drawNCrm(n1b,center,radius);
						drawNCrm(n2b,center,radius);
						drawNCrm(n2,center,radius);


					}else{ // draw edge triangle
						n2b = sphericalCoords(ui, longitude, cosSinV);

						//top triangles
						drawNCr(n1,center,radius);
						drawNCr(n2,center,radius);
						drawNCr(n1b,center,radius);

						//bottom triangles
						drawNCrm(n1,center,radius);
						drawNCrm(n1b,center,radius);
						drawNCrm(n2,center,radius);

					}
				}else{ // no jump :  draw two triangles
					n2b = sphericalCoords(ui, longitude, cosSinV);

					//top triangles
					drawNCr(n1,center,radius);
					drawNCr(n2,center,radius);
					drawNCr(n1b,center,radius);
					
					drawNCr(n2,center,radius);
					drawNCr(n2b,center,radius);
					drawNCr(n1b,center,radius);
					
					//bottom triangles
					drawNCrm(n1,center,radius);
					drawNCrm(n1b,center,radius);
					drawNCrm(n2,center,radius);
					
					drawNCrm(n2,center,radius);
					drawNCrm(n1b,center,radius);
					drawNCrm(n2b,center,radius);
					
				}
				
				
				n[ui] = n2b;
				
				if (jumpNeeded){
					jump = !jump;
				}
	
			}
			
			// if just jumps done, next shift is twice
			if(jumpNeeded){
				shift = shift * 2 ;
			}
			
			
		}
		
		//pole
		n2 = n[longitude - shift];
		for (int ui=0; ui<longitude; ui += shift){
			n1 = n2;
			n2 = n[ui];
			
			//top triangles
			drawNCr(n1,center,radius);
			drawNCr(n2,center,radius);
			drawNCr(Coords.VZ,center,radius);
			
			//bottom triangles
			drawNCrm(n1,center,radius);
			drawNCrm(Coords.VZ,center,radius);
			drawNCrm(n2,center,radius);
			
		}
		
		manager.endGeometry();
		
	}
	
	
	
	/** 
	 * draw part of the surface
	 */
	public void draw(){
		manager.startGeometry(Manager.QUADS);
		
		
		du = (uMax-uMin)/uNb;
		dv = (vMax-vMin)/vNb;
		
		/*
		uMinFadeNb = uNb*uMinFade/(uMax-uMin);
		uMaxFadeNb = uNb*uMaxFade/(uMax-uMin);
		vMinFadeNb = vNb*vMinFade/(vMax-vMin);
		vMaxFadeNb = vNb*vMaxFade/(vMax-vMin);
		*/
		uMinFadeNb = uMinFade/du;
		uMaxFadeNb = uMaxFade/du;
		vMinFadeNb = vMinFade/dv;
		vMaxFadeNb = vMaxFade/dv;
	
		//Application.debug("vMin, vMax, dv="+vMin+", "+vMax+", "+dv);

		for (int ui=0; ui<uNb; ui++){
			
			for (int vi=0; vi<vNb; vi++){			
				
				drawQuad(ui, vi);
	
			}
			
		}
		
		manager.endGeometry();
	}
	

	
	public void drawSphere(int size, Coords center, double radius){
		manager.startGeometry(Manager.QUADS);
		
		Coords n;
		
		int longitude=6;
		size+=3;
		while(longitude*6<=size*size){//find the correct longitude size (size=3 <-> longitude=12 and size=9 <-> longitude=48)
			longitude*=2;
		}
		
		//longitude=2;
		
		int latitude=longitude/2;

		for (int ui=0; ui<longitude; ui++){
			
			for (int vi=-latitude; vi<latitude; vi++){			
				
				n=sphericalCoords(ui,vi,longitude,latitude);
				drawNV(n,center.add(n.mul(radius)));
				n=sphericalCoords(ui+1,vi,longitude,latitude);
				drawNV(n,center.add(n.mul(radius)));
				n=sphericalCoords(ui+1,vi+1,longitude,latitude);
				drawNV(n,center.add(n.mul(radius)));
				n=sphericalCoords(ui,vi+1,longitude,latitude);
				drawNV(n,center.add(n.mul(radius)));
	
			}
			
		}
		
		manager.endGeometry();
	}
	
	private static void cosSin(int vi, int latitude, double[] ret){
		double v = ((double) vi/latitude)*Math.PI/2;
		ret[0] = Math.cos(v);
		ret[1] = Math.sin(v);	
	}
	
	private static Coords sphericalCoords(int ui, int longitude, double[] cosSinV){
		
		double u = ((double) ui/longitude)*2*Math.PI;
		
		return new Coords(				
				Math.cos(u)*cosSinV[0],
				Math.sin(u)*cosSinV[0],
				cosSinV[1],0);
			
	}
	
	
	private static Coords sphericalCoords(int ui, int vi, int longitude, int latitude){
		
		double u = ((double) ui/longitude)*2*Math.PI;
		double v = ((double) vi/latitude)*Math.PI/2;
		
		return new Coords(				
				Math.cos(u)*Math.cos(v),
				Math.sin(u)*Math.cos(v),
				Math.sin(v),0);
			
	}
	
	
	/** 
	 * draw part of the surface
	 */
	public void draw(SurfaceMesh2 tree){
		
		FloatBuffer b1 = tree.getVertices();
		FloatBuffer b2 = tree.getNormals();
		int cnt = tree.getTriangleCount();
		manager.startGeometry(Manager.TRIANGLES);
		
		/*TODO use fading texture
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		*/
		manager.texture(0, 0);
		
		float[] f = new float[9]; float[] n = new float[9];
		b1.rewind(); b2.rewind();
		for(int i = 0; i < cnt; i++) {
			b1.get(f);b2.get(n);
			manager.normal(n[0],n[1],n[2]);
			manager.vertex(f[0],f[1],f[2]);
			manager.normal(n[3],n[4],n[5]);
			manager.vertex(f[3],f[4],f[5]);
			manager.normal(n[6],n[7],n[8]);
			manager.vertex(f[6],f[7],f[8]);
		}
		manager.endGeometry();
	}
		
	public void draw(MarchingCubes mc, Renderer renderer) {
 		FloatBuffer b1 = mc.getVertices();
		FloatBuffer b2 = mc.getNormals();
		int cnt = mc.getVisibleChunks();

		manager.startGeometry(Manager.TRIANGLES);
		manager.texture(0, 0);

		float[] f = new float[9]; float[] n = new float[9];
		b1.rewind(); b2.rewind();
		for(int i = 0; i < cnt; i++) {
			b1.get(f);b2.get(n);
			manager.normal(n[0],n[1],n[2]);
			manager.vertex(f[0],f[1],f[2]);
			manager.normal(n[3],n[4],n[5]);
			manager.vertex(f[3],f[4],f[5]);
			manager.normal(n[6],n[7],n[8]);
			manager.vertex(f[6],f[7],f[8]);
		}
		manager.endGeometry();
		
//		Coords[][] segments = mc.getSegments();
//		
//		PlotterBrush brush = renderer.getGeometryManager().getBrush();
//		
//		System.out.println(segments.length/12);
//		
//		brush.start(10);
//		brush.setThickness(.03f);
//		brush.setColor(new Color(0.1f,0.1f,1.0f),0.2f);
//		brush.setAffineTexture(1.0f,  0.25f);
//		for(int i = 0; i < segments.length; i++)
//			brush.segment(segments[i][0], segments[i][1]);
//		brush.end();
	}
	
	
	/** draws a disc
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 */
	public void disc(Coords center, Coords v1, Coords v2, double radius){
		manager.startGeometry(Manager.TRIANGLE_FAN);

		int longitude = 60;
		
		Coords vn;
		
    	float dt = (float) 1/longitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	
    	

    	manager.texture(0, 0);
    	manager.normal(v1.crossProduct(v2));
    	manager.vertex(center);  
    	
    	float u=1, v=0;
		vn = (Coords) v1.mul(u).add(v2.mul(v));
		manager.vertex(center.add(vn.mul(radius)));  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cos ( i * da ); 
    		v = (float) Math.sin ( i * da ); 

    		vn = (Coords) v1.mul(u).add(v2.mul(v));
    		manager.vertex(center.add(vn.mul(radius)));
    	} 		
		

		manager.endGeometry();
	}
	
	/**
	 * draws a parallelogram
	 * @param center
	 * @param v1
	 * @param v2
	 * @param l1 
	 * @param l2 
	 */
	public void parallelogram(Coords center, Coords v1, Coords v2, double l1, double l2){
		manager.startGeometry(Manager.QUADS);

		manager.texture(0, 0);
		manager.normal(v1.crossProduct(v2));
		manager.vertex(center);  
		//manager.texture(1, 0);//TODO ?
		manager.vertex(center.add(v1.mul(l1)));  
		//manager.texture(1, 1);
		manager.vertex(center.add(v1.mul(l1)).add(v2.mul(l2)));  
		//manager.texture(0, 1);
		manager.vertex(center.add(v2.mul(l2)));      	           	

		manager.endGeometry();
	}
	
	
	
	/** draws an ellipse
	 * @param center
	 * @param v1
	 * @param v2
	 * @param a 
	 * @param b 
	 * @param start 
	 * @param extent 
	 */
	public void ellipsePart(Coords center, Coords v1, Coords v2, double a, double b, double start, double extent){

		ellipsePart(center, v1, v2, a, b, start, extent, true);

	}
	
	/**
	 * @param center
	 * @param v1
	 * @param v2
	 * @param a
	 * @param b
	 * @param start
	 * @param extent
	 * @param fromEllipseCenter says if the surface is drawn from center of the ellipse
	 */
	public void ellipsePart(Coords center, Coords v1, Coords v2, double a, double b, double start, double extent, boolean fromEllipseCenter){
		manager.startGeometry(Manager.TRIANGLE_FAN);
		
		int longitude = 60;
		
		Coords m;
    	float u, v;
		
    	float dt = (float) 1/longitude;
    	float da = (float) (extent *dt) ; 
    	
    	manager.texture(0, 0);
    	manager.normal(v1.crossProduct(v2));
    	
    	u = (float) Math.cos (start); 
		v = (float) Math.sin (start);
		m = v1.mul(a*u).add(v2.mul(b*v));
    	

		//center of the triangle fan
		if (fromEllipseCenter){ // center of the ellipse
			manager.vertex(center); 
		}else{ // mid point of the ellipse start and end
			u = (float) Math.cos (start+extent); 
			v = (float) Math.sin (start+extent);
			manager.vertex(center.add((m.add(v1.mul(a*u).add(v2.mul(b*v)))).mul(0.5)));  
		} 
    	
    	
    	//first point
		manager.vertex(center.add(m));  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cos (start + i * da ); 
    		v = (float) Math.sin (start + i * da ); 
    		
     		m = v1.mul(a*u).add(v2.mul(b*v));
    		manager.vertex(center.add(m));
    	} 
    	
    			
    	
    	manager.endGeometry();
    	
	}
	
	

	/**
	 * draws the inside of the hyperobola part
	 * @param center center
	 * @param v1 1st eigenvector
	 * @param v2 2nd eigenvector
	 * @param a  1st eigenvalue
	 * @param b  2nd eigenvalue
	 * @param tMin t min
	 * @param tMax t max
	 */
	public void hyperbolaPart(Coords center, Coords v1, Coords v2, double a, double b,
			double tMin, double tMax){


		manager.startGeometry(Manager.TRIANGLE_FAN);
		
		manager.texture(0, 0);
    	manager.normal(v1.crossProduct(v2));
		
    	

		int longitude = 120;

		Coords m;

		float dt = (float) (tMax-tMin)/longitude;

		float u, v;
		
		// first point on the branch
		u = (float) Math.cosh ( tMin ); 
		v = (float) Math.sinh ( tMin );  
		m = v1.mul(a*u).add(v2.mul(b*v));
		
		// center of the fan is midpoint of branch ends
		u = (float) Math.cosh (tMax); 
		v = (float) Math.sinh (tMax);
		manager.vertex(center.add((m.add(v1.mul(a*u).add(v2.mul(b*v)))).mul(0.5)));  
		
		
		//first point
		manager.vertex(center.add(m));  	

		for( int i = 1; i <= longitude  ; i++ ) { 
			u = (float) Math.cosh ( tMin + i * dt ); 
			v = (float) Math.sinh ( tMin + i * dt ); 
			
			m = v1.mul(a*u).add(v2.mul(b*v));
			manager.vertex(center.add(m));
		} 



		manager.endGeometry();
		

	}
	
	
	/** fill a parabola
	 * @param center center
	 * @param v1 1st eigenvector
	 * @param v2 2nd eigenvector
	 * @param p eigenvalue
	 * @param tMin t min
	 * @param tMax t max
	 */
	public void parabola(Coords center, Coords v1, Coords v2, double p,
			double tMin, double tMax){


		manager.startGeometry(Manager.TRIANGLE_FAN);
		
		manager.texture(0, 0);
    	manager.normal(v1.crossProduct(v2));
		
    	

		int longitude = 120;

		Coords m;

		float dt = (float) (tMax-tMin)/longitude;

		float u, v;
		double t;
		
		// first point
		t=tMin;
		u = (float) ( p*t*t/2 ); 
		v = (float) ( p*t); 
		m = v1.mul(u).add(v2.mul(v));
		
		// center of the fan is midpoint of branch ends
		t=tMax;
		u = (float) ( p*t*t/2 ); 
		v = (float) ( p*t); 
		manager.vertex(center.add((m.add(v1.mul(u).add(v2.mul(v)))).mul(0.5)));  
		
		
		//first point
		manager.vertex(center.add(m));  	

		for( int i = 1; i <= longitude  ; i++ ) { 
			t = tMin + i * dt ;
			u = (float) ( p*t*t/2 ); 
			v = (float) ( p*t); 

			m = v1.mul(u).add(v2.mul(v));
			manager.vertex(center.add(m));
		} 



		manager.endGeometry();		

	}
	
	
	
	private Coords calcNormal(float x, float y, float z){
		double[] n = new double[3];
		Coords v0 = new Coords(x,y,z,0);
		Coords v1 = function.evaluatePoint(x+1e-9,y);
		Coords v2 = function.evaluatePoint(x,y+1e-9);
		
		return v1.sub(v0).crossProduct(v2.sub(v0)).normalized();
	}
	
	public void drawTriangle(FloatBuffer d){
		manager.startGeometry(Manager.TRIANGLE_STRIP);
		
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		float[] f = new float[9];
		
		d.get(f);
		manager.vertex(f[0],f[1],f[2]);
		manager.vertex(f[3],f[4],f[5]);
		manager.vertex(f[6],f[7],f[8]);
		manager.endGeometry();
		d.flip();
		manager.endGeometry();
	}
	
	private void drawQuad(int ui, int vi){
		

		drawTNV(ui, vi);
		drawTNV(ui+1, vi);
		drawTNV(ui+1, vi+1);
		drawTNV(ui, vi+1);
		
	}
	
	private void drawTNV(int ui, int vi){
		
		float uT = getTextureCoord(ui, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(vi, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		
		float u = uMin+ui*du;
		float v = vMin+vi*dv;			
		drawNV(functional2Var.evaluateNormal(u, v),functional2Var.evaluatePoint(u, v));
	}

	/**
	 * draws normal and point at center + normal * radius
	 * @param normal
	 * @param center
	 * @param radius
	 */
	private void drawNCr(Coords normal, Coords center, double radius){
		drawNV(normal, center.add(normal.mul(radius)));
	}

	/**
	 * draws normal and point at center - normal * radius
	 * @param normal
	 * @param center
	 * @param radius
	 */
	private void drawNCrm(Coords normal, Coords center, double radius){
		drawNCr(normal.mul(-1), center, radius);
	}
	
	private void drawNV(Coords normal, Coords point){
		manager.normal(normal);
		manager.vertex(point);
	}
	
	private static float getTextureCoord(int i, int n, float fadeMin, float fadeMax){
		
		float t;
	
		if (fadeMin!=0){
			if (i<=n/2){
				t=i/fadeMin;
				return TEXTURE_FADE_OUT*(1-t)+TEXTURE_FADE_IN*t;
			}
		}
			

		if (fadeMax!=0){
			if (i>=n/2){
				t=(n-i)/fadeMax;
				return TEXTURE_FADE_OUT*(1-t)+TEXTURE_FADE_IN*t;
			}
		}

		return TEXTURE_FADE_IN;
	}
	

}


