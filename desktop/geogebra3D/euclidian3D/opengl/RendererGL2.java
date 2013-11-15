package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.awt.GPointWithZ;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.Hits3D;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
 * Renderer using GL2
 * @author mathieu
 *
 */
public class RendererGL2 extends Renderer{

	/**
	 * Constructor
	 * @param view
	 * @param useCanvas
	 */
	public RendererGL2(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);
	}



	private GL2 gl;

	@Override
	protected GL2 getGL(){
		return gl;
	}

	@Override
	protected void setGL(GLAutoDrawable drawable){
		gl = RendererJogl.getGL(drawable);
	}



	@Override
	public void setClipPlane(int n, double[] equation){
		getGL().glClipPlane( GL_CLIP_PLANE[n] , equation, 0 );
	}

	@Override
	protected void setMatrixView(){
		getGL().glPushMatrix();
		getGL().glLoadMatrixd(view3D.getToScreenMatrix().get(),0);           
	}

	@Override
	protected void unsetMatrixView(){
		getGL().glPopMatrix();  	
	}


	@Override
	protected void setExportImage(){

		getGL().glReadBuffer(GLlocal.GL_FRONT);
		int width = right-left;
		int height = top-bottom;
		FloatBuffer buffer = FloatBuffer.allocate(3*width*height);
		getGL().glReadPixels(0, 0, width, height, GLlocal.GL_RGB, GLlocal.GL_FLOAT, buffer);
		float[] pixels = buffer.array();

		bi = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);

		int i =0;
		for (int y=height-1; y>=0 ; y--)
			for (int x =0 ; x<width ; x++){
				int r = (int) (pixels[i]*255);
				int g = (int) (pixels[i+1]*255);
				int b = (int) (pixels[i+2]*255);
				bi.setRGB(x, y, ( (r << 16) | (g << 8) | b));
				i+=3;
			}
		bi.flush();
	}


	@Override
	public void setColor(Coords color){

		getGL().glColor4f((float) color.getX(),
				(float) color.getY(),
				(float) color.getZ(),
				(float) color.getW());  

	}

	@Override
	public void setColor(geogebra.common.awt.GColor color){
		getGL().glColor4f((float) color.getRed()/255,
				(float) color.getBlue()/255,
				(float) color.getGreen()/255,
				(float) color.getAlpha()/255);  
	}



	@Override
	public void initMatrix(){
		getGL().glPushMatrix();
		getGL().glMultMatrixd(m_drawingMatrix.get(),0);
	}



	@Override
	public void resetMatrix(){
		getGL().glPopMatrix();
	}


	@Override
	public void drawMouseCursor(){
		//Application.debug("ici");

		initMatrix();
		setBlending(false);
		getGL().glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_POINT);
		getGL().glColor4f(0,0,0,1);
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		getGL().glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_LINE);
		getGL().glColor4f(0,0,0,1);
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		getGL().glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_FILL);
		getGL().glColor4f(1,1,1,1);
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		setBlending(true);
		resetMatrix();   	
	}


	@Override
	protected IntBuffer createSelectBufferForPicking(int bufSize){
		// Set Up the Selection Buffer
		//Application.debug(bufSize);
		IntBuffer ret = RendererJogl.newIntBuffer(bufSize);
		getGL().glSelectBuffer(bufSize, ret); // Tell OpenGL To Use Our Array For Selection
		return ret; 
	}

	@Override
	protected void setGLForPicking(){		

		// The Size Of The Viewport. [0] Is <x>, [1] Is <y>, [2] Is <length>, [3] Is <width>
		int[] viewport = new int[4];
		getGL().glGetIntegerv(GLlocal.GL_VIEWPORT, viewport, 0);      
		Dimension dim = canvas.getSize();
		// Puts OpenGL In Selection Mode. Nothing Will Be Drawn.  Object ID's and Extents Are Stored In The Buffer.
		getGL().glRenderMode(GLlocal.GL_SELECT);
		getGL().glInitNames(); // Initializes The Name Stack
		getGL().glPushName(0); // Push 0 (At Least One Entry) Onto The Stack

		getGL().glMatrixMode(GLlocal.GL_PROJECTION);
		getGL().glLoadIdentity();


		/* create MOUSE_PICK_WIDTH x MOUSE_PICK_WIDTH pixel picking region near cursor location */
		double x = mouse.getX();
		double y = mouse.getY();

		// if we use an input3D, scale x & y values
		if (mouse instanceof GPointWithZ){ 

			if(view3D.getProjection() == EuclidianView3D.PROJECTION_PERSPECTIVE){ 
				double f = eyeToScreenDistance/(eyeToScreenDistance-((GPointWithZ) mouse).getZ());
				x = dim.width/2 + f*(x - dim.width/2);
				y = dim.height/2 + f*(y - dim.height/2);

			}else if(view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES){
				double f = eyeToScreenDistance/(eyeToScreenDistance-((GPointWithZ) mouse).getZ() - view3D.getScreenZOffset());
				x = dim.width/2 + f*(x + glassesEyeSep - dim.width/2) - glassesEyeSep;
				y = dim.height/2 + f*(y-dim.height/2);

			}

		}

		glu.gluPickMatrix(x, dim.height - y, MOUSE_PICK_WIDTH, MOUSE_PICK_WIDTH, viewport, 0);
		setProjectionMatrixForPicking();
		getGL().glMatrixMode(GLlocal.GL_MODELVIEW);

		getGL().glDisable(GLlocal.GL_ALPHA_TEST);
		getGL().glDisable(GLlocal.GL_BLEND);
		getGL().glDisable(GLlocal.GL_LIGHTING);
		disableTextures();


		// picking 
		pickingLoop = 0;
	}


	@Override
	protected void pushSceneMatrix(){
		// set the scene matrix
		getGL().glPushMatrix();
		getGL().glLoadMatrixd(view3D.getToScreenMatrix().get(),0);
	}


	@Override
	protected void storePickingInfos(Hits3D hits3D, int pointAndCurvesLoop, int labelLoop){

		int hits = getGL().glRenderMode(GLlocal.GL_RENDER); // Switch To Render Mode, Find Out How Many

		int names, ptr = 0;
		double zFar, zNear;
		int num;


		//App.error("");

		for (int i = 0; i < hits ; i++) { 

			names = selectBuffer.get(ptr);  
			ptr++; // min z    
			zNear = getScreenZFromPickingDepth(getDepth(ptr, selectBuffer));
			ptr++; // max z
			zFar = getScreenZFromPickingDepth(getDepth(ptr, selectBuffer));           

			ptr++;


			for (int j = 0; j < names; j++){ 
				num = selectBuffer.get(ptr);

				if (hits3D==null){ // just update z min/max values for the drawable
					drawHits[num].setZPick(zNear,zFar);        		  
				}else{ // if for hits array, some checks are done
					//App.debug("\n"+drawHits[num].getGeoElement());
					if (!(mouse instanceof GPointWithZ) 
							|| intersectsMouse3D(zNear, zFar, ((GPointWithZ) mouse).getZ()) ){ // check if mouse is nearer than objet (for 3D input)
						PickingType type;
						if (num >= labelLoop){
							type = PickingType.LABEL;
						}else if (num >= pointAndCurvesLoop){
							type = PickingType.POINT_OR_CURVE;
						}else{
							type = PickingType.SURFACE;
						}
						hits3D.addDrawable3D(drawHits[num], type, zNear, zFar);
						//App.debug("\n"+drawHits[num].getGeoElement()+"\nzFar = "+zFar+"\nmouse z ="+((GPointWithZ) mouse).getZ());
					}
				}


				//Application.debug(drawHits[num]+"\nzMin="+zMin+", zMax="+zMax);
				ptr++;
			}         
		}
	}


	@Override
	protected void doPick(){


		if (geoToPickSize!=oldGeoToPickSize || needsNewPickingBuffer){
			int bufSize=geoToPickSize*2+1 +20; //TODO remove "+20" due to intersection curve
			selectBuffer=createSelectBufferForPicking(bufSize);
			drawHits=createDrawableListForPicking(bufSize);
			oldGeoToPickSize=geoToPickSize;
			needsNewPickingBuffer=false;
		}

		setGLForPicking();
		pushSceneMatrix();

		// picking surfaces
		drawable3DLists.drawForPickingSurfaces(this);        

		// picking points and curves
		int pointAndCurvesLoop = pickingLoop;
		drawable3DLists.drawForPickingPointsAndCurves(this);       


		// set off the scene matrix
		getGL().glPopMatrix();

		// picking labels
		int labelLoop = pickingLoop;

		if (pickingMode == PICKING_MODE_LABELS){
			// picking labels            	
			drawable3DLists.drawLabelForPicking(this);          
		}        

		//end picking             

		//hits are stored
		//Hits3D hits3D = new Hits3D();
		Hits3D hits3D = view3D.getHits3D();
		hits3D.init();
		storePickingInfos(hits3D, pointAndCurvesLoop, labelLoop);

		// sets the GeoElements in view3D
		hits3D.sort();
		/* DEBUG /
        StringBuilder sbd = new StringBuilder();
        sbd.append("hits~~~"+hits3D.toString());
        for (int i = 0; i<drawHits.length; i++) {
        	if (drawHits[i]!=null && drawHits[i].getGeoElement()!=null) {
        		if (hits3D.contains(drawHits[i].getGeoElement())) {
        	sbd.append("\n" + drawHits[i].getGeoElement().getLabel()+
        			"~~~ zPickMin=" + drawHits[i].zPickMin +
        			"  zPickMax=" + drawHits[i].zPickMax);}}
        }
		Application.debug(sbd.toString());
		/ END DEBUG*/
		//view3D.setHits(hits3D);

		//App.debug(hits3D);

		waitForPick = false;

		getGL().glEnable(GLlocal.GL_LIGHTING);
	}



	@Override
	public void pickIntersectionCurves(){

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D.getEuclidianController()).getIntersectionCurves();

		int bufSize=curves.size();
		//IntBuffer selectBuffer=createSelectBufferForPicking(bufSize);
		//Drawable3D[] drawHits=createDrawableListForPicking(bufSize);
		if (bufSize>geoToPickSize){
			selectBuffer=createSelectBufferForPicking(bufSize);
			drawHits=createDrawableListForPicking(bufSize);
			oldGeoToPickSize=-1;
		}        

		setGLForPicking();
		pushSceneMatrix();


		// picking objects
		for (IntersectionCurve intersectionCurve : curves){
			Drawable3D d = intersectionCurve.drawable;
			d.setZPick(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
			pick(d, true, PickingType.POINT_OR_CURVE);
		}


		// set off the scene matrix
		getGL().glPopMatrix();

		storePickingInfos(null, 0, 0); // 0, 0 will be ignored since hits are passed as null        

		getGL().glEnable(GLlocal.GL_LIGHTING);
	}   

	@Override
	public void glLoadName(int loop){
		getGL().glLoadName(loop);
	}



	@Override
	protected void setLight(int light, int attr, float[] values){
		getGL().glLightfv(light, attr, values, 0);
	}

	@Override
	protected void setColorMaterial(){
		getGL().glColorMaterial(GLlocal.GL_FRONT_AND_BACK, GLlocal.GL_AMBIENT_AND_DIFFUSE);
	}

	@Override
	protected void setLightModel(){
		getGL().glShadeModel(GLlocal.GL_SMOOTH);
		getGL().glLightModeli(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,GLlocal.GL_TRUE);
		getGL().glLightModelf(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,GLlocal.GL_TRUE);
	}


	@Override
	protected void setAlphaFunc(){
        getGL().glAlphaFunc(GLlocal.GL_NOTEQUAL, 0);//pixels with alpha=0 are not drawn
        //getGL().glAlphaFunc(GLlocal.GL_GREATER, 0.8f);//pixels with alpha=0 are not drawn
	}

	@Override
	protected void setView(){
		getGL().glViewport(0,0,right-left,top-bottom);

		getGL().glMatrixMode(GLlocal.GL_PROJECTION);
		getGL().glLoadIdentity();

		setProjectionMatrix();

    	getGL().glMatrixMode(GLlocal.GL_MODELVIEW);		
	}	
	
	
	
	@Override
	protected void setStencilLines(){
		
		// disable clip planes if used
        if (enableClipPlanes)
        	disableClipPlanes();
        
		final int w = right-left;
		final int h = top-bottom;
		//App.debug(w+" * "+h+" = "+(w*h));

		// projection for real 2D
		getGL().glViewport(0,0,w,h);
		
		getGL().glMatrixMode(GLlocal.GL_PROJECTION);
		getGL().glLoadIdentity();
		glu.gluOrtho2D(0, w, h, 0);
		
		getGL().glMatrixMode(GLlocal.GL_MODELVIEW);
		getGL().glLoadIdentity();

		
		getGL().glEnable(GLlocal.GL_STENCIL_TEST);

		// draw stencil pattern
		getGL().glStencilMask(0xFF);
		getGL().glClear(GLlocal.GL_STENCIL_BUFFER_BIT);  // needs mask=0xFF
		  
 

		// no multisample here to prevent ghosts
		getGL().glDisable(GLlocal.GL_MULTISAMPLE);
        
		// data for stencil : one line = 0, one line = 1, etc.
		
		/*
		final int h2 = h+10;// (int) (h*1.1) ; //TODO : understand why buffer doens't match glDrawPixels dimension
        ByteBuffer data = newByteBuffer(w * h2);
        byte b = 0;
        for (int y=0; y<h2; y++){
        	b=(byte) (1-b);
        	for (int x=0; x<w; x++){
        		data.put(b);
        	}
        }
        data.rewind();
        
        // check if we start with 0 or with 1
		int y = (canvas.getLocationOnScreen().y) % 2;

        gl.glRasterPos2i(0, h-y); 
        //App.debug("== "+w+" * "+h+" = "+(w*h));
    	gl.glDrawPixels(w, h, GLlocal.GL_STENCIL_INDEX, GLlocal.GL_UNSIGNED_BYTE, data); 
		 
		 */

		
		ByteBuffer data = newByteBuffer(w);
		byte b = 1;
		for (int x=0; x<w; x++){
			data.put((byte) b);
		}

		data.rewind();
        
        // check if we start with 0 or with 1
		// seems to be sensible to canvas location on screen and to parent relative location
		// (try docked with neighboors / undocked or docked alone)
		int y0 = (canvas.getParent().getLocation().y + canvas.getLocationOnScreen().y + 1) % 2;
		
		//App.debug("\nparent.y="+canvas.getParent().getLocation().y+"\ncanvas.y="+canvas.getLocation().y+"\nscreen.y="+canvas.getLocationOnScreen().y+"\nh="+h+"\ny0="+y0);
		//App.debug("== "+w+" * "+h+" = "+(w*h)+"\ny0="+y0);
		
		for (int y = 0; y<h/2 ; y++){
			getGL().glRasterPos2i(0, 2*y+y0); 
			getGL().glDrawPixels(w, 1, GLlocal.GL_STENCIL_INDEX, GLlocal.GL_UNSIGNED_BYTE, data); 
		}
		
		
		
		
    	// current mask for stencil test
    	getGL().glStencilMask(0x00);

    	// back to multisample
		getGL().glEnable(GLlocal.GL_MULTISAMPLE);
		
		
		waitForSetStencilLines = false;


		// restore clip planes
        if (enableClipPlanes)
    		enableClipPlanes();

	}
	

	@Override
	protected void viewOrtho(){

		getGL().glOrtho(getLeft(),getRight(),getBottom(),getTop(), -getVisibleDepth()/2, getVisibleDepth()/2);   	
	}


	@Override
	protected void viewPersp(){

		getGL().glFrustum(perspLeft,perspRight,perspBottom,perspTop,perspNear,perspFar);
		getGL().glTranslated(0, 0, perspFocus);          	
	}     
	
	
    @Override
	protected void viewGlasses(){
    	
    	
    	//eye separation
    	double eyesep, eyesep1;
    	if(eye==EYE_LEFT){
    		eyesep=-glassesEyeSep;
    		eyesep1=-glassesEyeSep1;
    	}else{
    		eyesep=glassesEyeSep;
    		eyesep1=glassesEyeSep1;
    	}  	
   	
       	getGL().glFrustum(perspLeft+eyesep1,perspRight+eyesep1,perspBottom,perspTop,perspNear,perspFar);
    	getGL().glTranslated(eyesep, 0, perspFocus);          	
    }
    
    
    @Override
	protected void viewOblique(){
    	viewOrtho();
    	
    	getGL().glMultMatrixd(new double[] {
    			1,0,0,0,
    			0,1,0,0,
    			obliqueX,obliqueY,1,0, 
    			0,0,0,1
    	}, 0);  	
    }

}
