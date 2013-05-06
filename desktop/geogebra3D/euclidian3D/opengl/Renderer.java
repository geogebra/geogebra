package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.gui.util.AnimatedGifEncoder;
import geogebra.main.AppD;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra3D.euclidian3D.Drawable3DLists;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.Hits3D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;



/**
 * 
 * Used for openGL display.
 * <p>
 * It provides:
 * <ul>
 * <li> methods for displaying {@link Drawable3D}, with painting parameters </li>
 * <li> methods for picking object </li>
 * </ul>
 * 
 * @author ggb3D
 * 
 */
public class Renderer extends RendererJogl implements GLEventListener {

	
	// openGL variables
	protected GLU glu = new GLU();
	//private GLUT glut = new GLUT();
	//private TextRenderer textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 16));
	/** default text scale factor */
	private static final float DEFAULT_TEXT_SCALE_FACTOR = 0.8f;

	/** matrix changing Y-direction to Z-direction */
	//private double[] matrixYtoZ = {1,0,0,0, 0,0,1,0, 0,1,0,0, 0,0,0,1}; 
	
	/** canvas usable for a JPanel */
	//public GLCanvas canvas;
	public Component3D canvas;

	
	private GLUquadric quadric;
	private Animator animator;
	
	/** for polygon tesselation */
	private GLUtessellator tobj;
	
	private static final int MOUSE_PICK_WIDTH = 3;
	
	Drawable3D[] drawHits;
	int pickingLoop;
	
	// other
	private Drawable3DLists drawable3DLists;
	
	private EuclidianView3D view3D;
	
	// for drawing
	private CoordMatrix4x4 m_drawingMatrix; //matrix for drawing
	
	
	///////////////////
	//primitives
	//private RendererPrimitives primitives;
	
	///////////////////
	//geometries
	private Manager geometryManager;
	

	///////////////////
	//textures
	private Textures textures;

	
	///////////////////
	// arrows
	
	/** no arrows */
	static final public int ARROW_TYPE_NONE=0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE=1;
	private int m_arrowType=ARROW_TYPE_NONE;
	
	private double m_arrowLength, m_arrowWidth;
	
	
	///////////////////
	// dilation
	
	private static final int DILATION_NONE = 0;
	private static final int DILATION_HIGHLITED = 1;
	private int dilation = DILATION_NONE;
	private double[] dilationValues = {
			1,  // DILATION_NONE
			1.3 // DILATION_HIGHLITED
	};
	
	
	///////////////////
	// for picking
	
	private int mouseX, mouseY;
	private boolean waitForPick = false;
	private boolean doPick = false;
	public static final int PICKING_MODE_OBJECTS = 0;
	public static final int PICKING_MODE_LABELS = 1;
	private int pickingMode = PICKING_MODE_OBJECTS;
	
	/**
	 * creates a renderer linked to an {@link EuclidianView3D} 
	 * @param view the {@link EuclidianView3D} linked to 
	 */
	public Renderer(EuclidianView3D view){
		
		
		//canvas = view;
		App.debug("create 3D component");
        canvas = new Component3D();
        
        App.debug("add gl event listener");
	    canvas.addGLEventListener(this);
	    
	    App.debug("create animator");
	    animator = new Animator( canvas, 60 );
        //animator.setRunAsFastAsPossible(true);	  
        //animator.setRunAsFastAsPossible(false);	
	    

	    App.debug("start animator");
        animator.start();
        

        //link to 3D view
		this.view3D=view;
		
		//textures
		textures = new Textures(view3D.getApplication().getImageManager());	
	}
	
	
	
	/**
	 * set the list of {@link Drawable3D} to be drawn
	 * @param dl list of {@link Drawable3D}
	 */
	public void setDrawable3DLists(Drawable3DLists dl){
		drawable3DLists = dl;
	}	
	
	/**
	 * re-calc the display immediately
	 */
	public void display(){
	
		App.debug("RRR");
		canvas.display();
	}		
	
	/** sets if openGL culling is done or not
	 * @param flag
	 */
	public void setCulling(boolean flag){
		if (flag)
			gl.glEnable(GLlocal.GL_CULL_FACE);
		else
			gl.glDisable(GLlocal.GL_CULL_FACE);
	}
	
	public void setCullFaceFront(){
		gl.glCullFace(GLlocal.GL_FRONT); 
	}
	
	public void setCullFaceBack(){
		gl.glCullFace(GLlocal.GL_BACK); 
	}
	
	/** sets if openGL blending is done or not
	 * @param flag
	 */
	public void setBlending(boolean flag){
		if (flag)
			gl.glEnable(GLlocal.GL_BLEND);
		else
			gl.glDisable(GLlocal.GL_BLEND);
	}
	
	private void drawTransp(){
			
		getTextures().loadTextureLinear(Textures.FADING);
		
		gl.glDisable(GLlocal.GL_CULL_FACE);
		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);
		
		//TODO fix it
		//gl.glDisable(GLlocal.GL_TEXTURE_2D);
		//TODO improve this !
		
		gl.glDisable(GLlocal.GL_LIGHT0);
		gl.glEnable(GLlocal.GL_LIGHT1);
		//gl.glEnable(GLlocal.GL_LIGHT2);
		
		gl.glEnable(GLlocal.GL_CULL_FACE);
		gl.glCullFace(GLlocal.GL_FRONT); 
		drawable3DLists.drawTranspClosedCurved(this);//draws inside parts  
		if (drawable3DLists.containsClippedSurfaces()){
			enableClipPlanesIfNeeded();
			drawable3DLists.drawTranspClipped(this); //clipped surfaces back-faces
			disableClipPlanesIfNeeded();
		}
		gl.glCullFace(GLlocal.GL_BACK); 
		drawable3DLists.drawTranspClosedCurved(this);//draws outside parts 	
		if (drawable3DLists.containsClippedSurfaces()){
			enableClipPlanesIfNeeded();
			drawable3DLists.drawTranspClipped(this); //clipped surfaces back-faces
			disableClipPlanesIfNeeded();
		}
		

		//gl.glDisable(GLlocal.GL_LIGHT2);
		gl.glDisable(GLlocal.GL_LIGHT1);
		gl.glEnable(GLlocal.GL_LIGHT0);

	}
		
	private void drawNotTransp(){
		
		getTextures().loadTextureLinear(Textures.FADING);

        gl.glEnable(GLlocal.GL_BLEND);
        
        //gl.glCullFace(GLlocal.GL_BACK);gl.glEnable(GLlocal.GL_CULL_FACE);
		gl.glDisable(GLlocal.GL_CULL_FACE);
        drawable3DLists.drawNotTransparentSurfaces(this);

        
		gl.glDisable(GLlocal.GL_LIGHT0);
		gl.glEnable(GLlocal.GL_LIGHT1);
		//gl.glEnable(GLlocal.GL_LIGHT2);
		
		//TODO improve this !
		gl.glEnable(GLlocal.GL_CULL_FACE);
		gl.glCullFace(GLlocal.GL_FRONT); 
		drawable3DLists.drawNotTransparentSurfacesClosed(this);//draws inside parts  
		if (drawable3DLists.containsClippedSurfaces()){
			enableClipPlanesIfNeeded();
			drawable3DLists.drawNotTransparentSurfacesClipped(this); //clipped surfaces back-faces
			disableClipPlanesIfNeeded();
		}
		gl.glCullFace(GLlocal.GL_BACK); 
		drawable3DLists.drawNotTransparentSurfacesClosed(this);//draws outside parts 	
		if (drawable3DLists.containsClippedSurfaces()){
			enableClipPlanesIfNeeded();
			drawable3DLists.drawNotTransparentSurfacesClipped(this); //clipped surfaces back-faces
			disableClipPlanesIfNeeded();
		}
		

		//gl.glDisable(GLlocal.GL_LIGHT2);
		gl.glDisable(GLlocal.GL_LIGHT1);
		gl.glEnable(GLlocal.GL_LIGHT0);
	}
	
	/**
	 * 
	 * openGL method called when the display is to be computed.
	 * <p>
	 * First, it calls {@link #doPick()} if a picking is to be done.
	 * Then, for each {@link Drawable3D}, it calls:
	 * <ul>
	 * <li> {@link Drawable3D#drawHidden(EuclidianRenderer3D)} to draw hidden parts (dashed segments, lines, ...) </li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to draw transparent objects (planes, spheres, ...) </li>
	 * <li> {@link Drawable3D#drawSurfacesForHiding(EuclidianRenderer3D)} to draw in the z-buffer objects that hides others (planes, spheres, ...) </li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to re-draw transparent objects for a better alpha-blending </li>
	 * <li> {@link Drawable3D#drawOutline(EuclidianRenderer3D)} to draw not hidden parts (dash-less segments, lines, ...) </li>
	 * </ul>
	 */
    public void display(GLAutoDrawable gLDrawable) {
    	
    	
    	//Application.debug("display");

    	//double displayTime = System.currentTimeMillis();
        
        gl = RendererJogl.getGL(gLDrawable);                
        
        //picking        
        if(waitForPick)
        	doPick();
        	//Application.debug("doPick");
        	//return;
        //else 
        
        //clip planes
        if (waitForUpdateClipPlanes){
        	//Application.debug(enableClipPlanes);
        	if (enableClipPlanes)
        		enableClipPlanes();
        	else
        		disableClipPlanes();
        	waitForUpdateClipPlanes=false;
        }
                
        //update 3D controller
        view3D.getEuclidianController().processMouseMoved();
        
        /*
        if (intersectionCurvesWaitForPick){//picking intersection curves
    		doPickIntersectionCurves();
    		//intersectionCurvesWaitForPick=false;
    		((EuclidianController3D) view3D.getEuclidianController()).endsIntersectionCurve();
    		
    	}
    	*/
        
        // update 3D view
        geometryManager.update();
        view3D.update();
        view3D.updateOwnDrawablesNow();
        
        // update 3D drawables
        drawable3DLists.updateAll();

    	// say that 3D view changed has been performed
        view3D.resetViewChanged();
       

        //start drawing
        /*
        eye=EYE_ONE;
        setColorMask();
        gl.glClear(GLlocal.GL_COLOR_BUFFER_BIT | GLlocal.GL_DEPTH_BUFFER_BIT);
        for (int i=0;i<2;i++){
        	//clear buffers
        	eye=EYE_ONE;
        	//setColorMask();
        	//gl.glClear(GLlocal.GL_DEPTH_BUFFER_BIT);
        	gl.glColorMask(false,false,false,true);
            gl.glClear(GLlocal.GL_COLOR_BUFFER_BIT | GLlocal.GL_DEPTH_BUFFER_BIT);
            
            //draw
           	eye=i;
           	setColorMask();
        	setView();

        	draw();       		
        }
        */
        
        if (waitForUpdateClearColor) {
        	updateClearColor();
        	waitForUpdateClearColor=false;
        }
        
        if (view3D.getProjection()==EuclidianView3D.PROJECTION_ANAGLYPH) {
        	//clear all
        	gl.glColorMask(true,true,true,true);
        	gl.glClear(GLlocal.GL_COLOR_BUFFER_BIT | GLlocal.GL_DEPTH_BUFFER_BIT);
        	//left eye
        	eye=EYE_LEFT;
        	setColorMask();
        	setView();
        	draw(); 
        	//right eye
        	eye=EYE_RIGHT;
        	setColorMask();
        	gl.glClear(GLlocal.GL_DEPTH_BUFFER_BIT); //clear depth buffer
        	setView();
        	draw();      	
        } else {  
        	setColorMask();
        	gl.glClear(GLlocal.GL_COLOR_BUFFER_BIT | GLlocal.GL_DEPTH_BUFFER_BIT);
        	setView();
        	draw(); 
        }
        
        gLDrawable.swapBuffers(); //TODO ?
            
        if (needExportImage) {
        	setExportImage();
        	needExportImage=false;
        	//notify();
        }
        
        switch (exportType) {
        case ANIMATEDGIF:
        	App.debug("Exporting frame: "+export_i);
        	
        	
        	setExportImage();
			if (bi == null) {
				App.error("image null");
			} else {
				gifEncoder.addFrame(bi);
			}
			
			export_val += export_step;
			
			if (export_val > export_max + 0.00000001 || export_val < export_min - 0.00000001) {
				export_val -= 2 * export_step;
				export_step *= -1;
			}
			
			
			export_i++;
			
			if (export_i>=export_n) {
				exportType = ExportType.NONE;
				gifEncoder.finish();

				App.debug("GIF export finished");
				
			} else {
				export_num.setValue(export_val);
				export_num.updateRepaint();
			}
			break;
			
        case CLIPBOARD:
        	App.debug("Exporting to clipboard");
        	
        	setExportImage();
        	
			if (bi == null) {
				App.error("image null");
			} else {
				geogebra.gui.util.ImageSelection imgSel = new geogebra.gui.util.ImageSelection(
						bi);
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(imgSel, null);
			}
			
			exportType = ExportType.NONE;
      	
        	break;

        }
    }


    /**
     * enable textures
     */
    final public void enableTextures(){  	
    	gl.glEnable(GLlocal.GL_TEXTURE_2D);
    }

    /**
     * disable textures
     */
    final public void disableTextures(){
    	gl.glDisable(GLlocal.GL_TEXTURE_2D);
    }
   
   
    private void drawFaceToScreen() {
    	//draw face-to screen parts (labels, ...)
        //drawing labels
        //gl.glEnable(GLlocal.GL_CULL_FACE);
        //gl.glCullFace(GLlocal.GL_BACK);

        gl.glEnable(GLlocal.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        gl.glDisable(GLlocal.GL_LIGHTING);
        gl.glEnable(GLlocal.GL_BLEND);
        enableTextures();
        //gl.glDisable(GLlocal.GL_BLEND);
        //gl.glDepthMask(false);
        drawable3DLists.drawLabel(this);
        
        //gl.glDepthMask(true);
    	//gl.glAlphaFunc(GLlocal.GL_EQUAL, 1);
        //drawable3DLists.drawLabel(this);
 
        //gl.glAlphaFunc(GLlocal.GL_GREATER, 0);
        
        
        disableTextures();
        
        if (enableClipPlanes)
        	disableClipPlanes();
        view3D.drawMouseCursor(this);
        if (enableClipPlanes)
    		enableClipPlanes();
        
    }
    
    private static final int[] GL_CLIP_PLANE = {GLlocal.GL_CLIP_PLANE0, GLlocal.GL_CLIP_PLANE1, GLlocal.GL_CLIP_PLANE2, GLlocal.GL_CLIP_PLANE3, GLlocal.GL_CLIP_PLANE4, GLlocal.GL_CLIP_PLANE5};
    
    private boolean enableClipPlanes;
    private boolean waitForUpdateClipPlanes=false;
    
    /**
     * sets if clip planes have to be enabled
     * @param flag flag
     */
    public void setEnableClipPlanes(boolean flag){
    	waitForUpdateClipPlanes = true;
    	enableClipPlanes = flag;
    }
       
    private void enableClipPlane(int n){
    	gl.glEnable( GL_CLIP_PLANE[n] );   	
    }
    
    private void disableClipPlane(int n){
    	gl.glDisable( GL_CLIP_PLANE[n] );   	
    }
    
    private void enableClipPlanes(){
    	for (int n=0; n<6; n++)
    		enableClipPlane(n);
    }
    
    /**
     * enable clipping if needed
     */
    public void enableClipPlanesIfNeeded(){
    	if (!enableClipPlanes)
    		enableClipPlanes();
    }
    
    private void disableClipPlanes(){
    	for (int n=0; n<6; n++)
    		disableClipPlane(n);
    }
    
    /**
     * disable clipping if needed
     */
    public void disableClipPlanesIfNeeded(){
    	if (!enableClipPlanes)
    		disableClipPlanes();
    }
    
    /**
     * sets the clip plane
     * @param n index of the clip plane
     * @param equation equation of the clip plane
     */
    public void setClipPlane(int n, double[] equation){
    	gl.glClipPlane( GL_CLIP_PLANE[n] , equation, 0 );
    }
    
    private void draw(){
	
        //labels
        drawFaceToScreen();
        
        //init drawing matrix to view3D toScreen matrix
        gl.glPushMatrix();
        //gl.glMultMatrixd(view3D.getToScreenMatrix().get(),0);
        gl.glLoadMatrixd(view3D.getToScreenMatrix().get(),0);            
 
        setLightPosition();      

        //drawing the cursor
        //gl.glEnable(GLlocal.GL_BLEND);
        gl.glEnable(GLlocal.GL_LIGHTING);
        gl.glDisable(GLlocal.GL_ALPHA_TEST);       
        gl.glEnable(GLlocal.GL_CULL_FACE);
        //gl.glCullFace(GLlocal.GL_FRONT);
        view3D.drawCursor(this);
                 
        //drawWireFrame();
               
        //primitives.enableVBO(gl);
        
        //drawing hidden part
        //gl.glEnable(GLlocal.GL_CULL_FACE);
        gl.glEnable(GLlocal.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        //gl.glDisable(GLlocal.GL_BLEND);
        drawable3DLists.drawHiddenNotTextured(this);
        enableTextures();
        //gl.glColorMask(false,false,false,false); //no writing in color buffer		
        drawable3DLists.drawHiddenTextured(this);
        drawNotTransp();
        //gl.glColorMask(true,true,true,true);
        disableTextures();
        gl.glDisable(GLlocal.GL_ALPHA_TEST);       
        
        //gl.glEnable(GLlocal.GL_BLEND);
        //gl.glDisable(GLlocal.GL_CULL_FACE);
                
        //drawing transparents parts
        gl.glDepthMask(false);
        enableTextures();
        drawTransp();      
        gl.glDepthMask(true);
       
        //drawing labels
        disableTextures();
        gl.glEnable(GLlocal.GL_CULL_FACE);
        //gl.glCullFace(GLlocal.GL_BACK);
        //gl.glEnable(GLlocal.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        //gl.glDisable(GLlocal.GL_LIGHTING);
        gl.glDisable(GLlocal.GL_BLEND);
        //drawList3D.drawLabel(this);
        //gl.glEnable(GLlocal.GL_LIGHTING);
        //gl.glDisable(GLlocal.GL_ALPHA_TEST);              
        
        //drawing hiding parts
        gl.glColorMask(false,false,false,false); //no writing in color buffer		
        gl.glCullFace(GLlocal.GL_FRONT); //draws inside parts    
        drawable3DLists.drawClosedSurfacesForHiding(this); //closed surfaces back-faces
        if (drawable3DLists.containsClippedSurfaces()){
        	enableClipPlanesIfNeeded();
        	drawable3DLists.drawClippedSurfacesForHiding(this); //clipped surfaces back-faces
        	disableClipPlanesIfNeeded();
        }
        gl.glDisable(GLlocal.GL_CULL_FACE);
        drawable3DLists.drawSurfacesForHiding(this); //non closed surfaces
        //gl.glColorMask(true,true,true,true);
        setColorMask();

        //re-drawing transparents parts for better transparent effect
        //TODO improve it !
        enableTextures();
        gl.glDepthMask(false);
        gl.glEnable(GLlocal.GL_BLEND);
        drawTransp();
        gl.glDepthMask(true);
        disableTextures();
        
        //drawing hiding parts
        gl.glColorMask(false,false,false,false); //no writing in color buffer		
        gl.glDisable(GLlocal.GL_BLEND);
        gl.glEnable(GLlocal.GL_CULL_FACE);
        gl.glCullFace(GLlocal.GL_BACK); //draws inside parts
        drawable3DLists.drawClosedSurfacesForHiding(this); //closed surfaces front-faces
        if (drawable3DLists.containsClippedSurfaces()){
        	enableClipPlanesIfNeeded();
        	drawable3DLists.drawClippedSurfacesForHiding(this); //clipped surfaces back-faces
        	disableClipPlanesIfNeeded();
        }
        setColorMask();        
        
        //re-drawing transparents parts for better transparent effect
        //TODO improve it !
        enableTextures();
        gl.glDepthMask(false);
        gl.glEnable(GLlocal.GL_BLEND);
        drawTransp();
        gl.glDepthMask(true);
        //gl.glDisable(GLlocal.GL_TEXTURE_2D);
        
        //drawing not hidden parts
        gl.glEnable(GLlocal.GL_CULL_FACE);
        //gl.glDisable(GLlocal.GL_BLEND);
        //gl.glEnable(GLlocal.GL_TEXTURE_2D);
        drawable3DLists.draw(this);        
        
        //primitives.disableVBO(gl);
            
        //FPS
        gl.glDisable(GLlocal.GL_LIGHTING);
        gl.glDisable(GLlocal.GL_DEPTH_TEST);

        //drawWireFrame();       
        
        gl.glPopMatrix();        
   	
    	//drawFPS();
        
    	gl.glEnable(GLlocal.GL_DEPTH_TEST);
    	gl.glEnable(GLlocal.GL_LIGHTING);        	
    }    
     
    private void drawWireFrame() {
  
    	gl.glPushAttrib(GLlocal.GL_ALL_ATTRIB_BITS);
    	
    	gl.glDepthMask(false);
    	gl.glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_LINE);gl.glPolygonMode(GLlocal.GL_BACK, GLlocal.GL_LINE);
        
    	gl.glLineWidth(5f);
    	
    	gl.glEnable(GLlocal.GL_LIGHTING);
        gl.glDisable(GLlocal.GL_LIGHT0);
        gl.glDisable(GLlocal.GL_CULL_FACE);
        gl.glDisable(GLlocal.GL_BLEND);
        gl.glEnable(GLlocal.GL_ALPHA_TEST);
        
    	drawable3DLists.drawTransp(this);
    	drawable3DLists.drawTranspClosedNotCurved(this);
    	drawable3DLists.drawTranspClosedCurved(this);
    	if (drawable3DLists.containsClippedSurfaces()){
    		enableClipPlanesIfNeeded();
    		drawable3DLists.drawTranspClipped(this); 
    		disableClipPlanesIfNeeded();
    	}
    	
    	gl.glPopAttrib();  	   	
    }
      
    
    //////////////////////////////////////
    // EXPORT IMAGE
    //////////////////////////////////////     
    
    private boolean needExportImage=false;
    
    /**
     * says that an export image is needed, and call immediate display
     */
    public void needExportImage(){
    	needExportImage = true;
    	display();  	
    }
    
    private BufferedImage bi;
    
    /**
     * creates an export image (and store it in BufferedImage bi)
     */
    private void setExportImage(){
    	
        gl.glReadBuffer(GLlocal.GL_FRONT);
        int width = right-left;
        int height = top-bottom;
        FloatBuffer buffer = FloatBuffer.allocate(3*width*height);
        gl.glReadPixels(0, 0, width, height, GLlocal.GL_RGB, GLlocal.GL_FLOAT, buffer);
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

    /**
     * @return a BufferedImage containing last export image created
     */
    public BufferedImage getExportImage(){
    	return bi;
    }   
    
    /**
     * openGL method called when the canvas is reshaped.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
      GL gl = drawable.getGL();
      
      //Application.debug("reshape\n x = "+x+"\n y = "+y+"\n w = "+w+"\n h = "+h);
      

      setView(x,y,w,h);

      
      view3D.reset();
    }

    /**
     * openGL method called when the display change.
     * empty method
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
        boolean deviceChanged) {
    }    
      
    
    ///////////////////////////////////////////////////
    //
    // pencil methods
    //
    /////////////////////////////////////////////////////
    
      
    /**
     * sets the material used by the pencil
     * @param color (r,g,b,a) vector
     * 
     */
    public void setColor(Coords color){
    	
    	gl.glColor4f((float) color.getX(),
    			(float) color.getY(),
    			(float) color.getZ(),
    			(float) color.getW());  
    				
    }
      
    //arrows
    
    /**
     * sets the type of arrow used by the pencil.
     * 
     * @param a_arrowType type of arrow, see {@link #ARROW_TYPE_NONE}, {@link #ARROW_TYPE_SIMPLE}, ... 
     */
    public void setArrowType(int a_arrowType){
    	m_arrowType = a_arrowType;
    } 
    
    /**
     * sets the width of the arrows painted by the pencil.
     * 
     * @param a_arrowWidth the width of the arrows
     */
    public void setArrowWidth(double a_arrowWidth){
    	m_arrowWidth = a_arrowWidth;
    } 
    
    
    /**
     * sets the length of the arrows painted by the pencil.
     * 
     * @param a_arrowLength the length of the arrows
     */
    public void setArrowLength(double a_arrowLength){
    	m_arrowLength = a_arrowLength;
    } 
       
    
    //layer
    /**
     * sets the layer to l. Use gl.glPolygonOffset( ).
     * @param l the layer
     */
    public void setLayer(float l){
    	
    	// 0<=l<10
    	// l2-l1>=1 to see something
    	//l=l/3f;
       	gl.glPolygonOffset(-l*0.05f, -l*10);
    	
       	//gl.glPolygonOffset(-l*0.75f, -l*0.5f);
       	
       	//gl.glPolygonOffset(-l, 0);
    }   
    
    //drawing matrix
    
    /**
     * sets the matrix in which coord sys the pencil draws.
     * 
     * @param a_matrix the matrix
     */
    public void setMatrix(CoordMatrix4x4 a_matrix){
    	m_drawingMatrix=a_matrix;
    }
    
    
    /**
     * gets the matrix describing the coord sys used by the pencil.
     * 
     * @return the matrix
     */
    public CoordMatrix4x4 getMatrix(){
    	return m_drawingMatrix;
    }
    
    
    /**
     * sets the drawing matrix to openGLlocal.
     * same as initMatrix(m_drawingMatrix)
     */
    public void initMatrix(){
    	initMatrix(m_drawingMatrix);
    }
    
    /**
     * sets a_drawingMatrix to openGLlocal.
     * @param a_drawingMatrix the matrix
     */
    private void initMatrix(CoordMatrix a_drawingMatrix){
    	initMatrix(a_drawingMatrix.get());
    }   
    
    
    /**
     * sets a_drawingMatrix to openGLlocal.
     * @param a_drawingMatrix the matrix
     */
    private void initMatrix(double[] a_drawingMatrix){
    	gl.glPushMatrix();
		gl.glMultMatrixd(a_drawingMatrix,0);
    }     

    public void translate(Coords v){
    	gl.glPushMatrix();
    	gl.glTranslated(v.getX(), v.getY(), v.getZ());
    }
    
    /**
     * turn off the last drawing matrix set in openGLlocal.
     */
    public void resetMatrix(){
    	gl.glPopMatrix();
    }
       
    
    ///////////////////////////////////////////////////////////
    //drawing geometries
    
    
    public Manager getGeometryManager(){
    	return geometryManager;
    }
    
    
    ///////////////////////////////////////////////////////////
    //textures
    
    
    public Textures getTextures(){
    	return textures;
    }
   
    
    /** draws a 3D cross cursor
     * @param type 
     */    
    public void drawCursor(int type){
    	
    	if (!PlotterCursor.isTypeAlready(type))
    		gl.glDisable(GLlocal.GL_LIGHTING);
    	
    	initMatrix();
    	geometryManager.draw(geometryManager.cursor.getIndex(type));
		resetMatrix();
    	
		if (!PlotterCursor.isTypeAlready(type))
			gl.glEnable(GLlocal.GL_LIGHTING);
   	
    } 
    
    /**
     * draws a view button
     */
    public void drawViewInFrontOf(){
    	//Application.debug("ici");
    	initMatrix();
    	setBlending(false);
    	geometryManager.draw(geometryManager.getViewInFrontOf().getIndex());
    	setBlending(true);
		resetMatrix();   	
    }
    
    /**
     * draws mouse cursor
     */
    public void drawMouseCursor(){
    	//Application.debug("ici");
    	
    	initMatrix();
    	setBlending(false);
    	gl.glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_POINT);
    	gl.glColor4f(0,0,0,1);
    	geometryManager.draw(geometryManager.getMouseCursor().getIndex());
    	gl.glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_LINE);
    	gl.glColor4f(0,0,0,1);
    	geometryManager.draw(geometryManager.getMouseCursor().getIndex());
    	gl.glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_FILL);
    	gl.glColor4f(1,1,1,1);
    	geometryManager.draw(geometryManager.getMouseCursor().getIndex());
    	setBlending(true);
		resetMatrix();   	
    }
    
    
    /**
     * set the tesselator to start drawing a new polygon
     * @param nx 
     * @param ny 
     * @param nz 
     * @return geometry manager index
     */
    public int startPolygon(float nx, float ny, float nz){
    	
    	return geometryManager.startPolygon(nx,ny,nz);
    }
    
    
    /** add the (x,y) point as a new vertex for the current polygon
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void addToPolygon(double x, double y){
    	addToPolygon(x, y, 0);
    }
    
    //TODO remove this
    /** add the (x,y,z) point as a new vertex for the current polygon
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    public void addToPolygon(double x, double y, double z){
    	geometryManager.addVertexToPolygon(x, y, z);
    }    
    
    /**
     * end of the current polygon
     */
    public void endPolygon(){
    	
    	geometryManager.endPolygon();       
    }
  
    public void drawPolygon(int index){
    	geometryManager.draw(index);
    }
    
     
    /* draws the text s
     * @param x x-coord
     * @param y y-coord
     * @param s text
     * @param colored says if the text has to be colored
     *
    public void drawText(float x, float y, String s, boolean colored){
    	
    	
    	//if (true)    		return;
    	
        gl.glMatrixMode(GLlocal.GL_TEXTURE);
        gl.glLoadIdentity();
        
    	gl.glMatrixMode(GLlocal.GL_MODELVIEW);
    	
    	
    	initMatrix();
    	initMatrix(view3D.getUndoRotationMatrix());
    	

    	textRenderer.begin3DRendering();

    	if (colored)
    		textRenderer.setColor(textColor);
    	
     
        float textScaleFactor = DEFAULT_TEXT_SCALE_FACTOR/((float) view3D.getScale());
    	
    	
        if (x<0)
        	x=x-(s.length()-0.5f)*8; //TODO adapt to police size
        
    	textRenderer.draw3D(s,
                x*textScaleFactor,//w / -2.0f * textScaleFactor,
                y*textScaleFactor,//h / -2.0f * textScaleFactor,
                0,
                textScaleFactor);
    	
        textRenderer.end3DRendering();
     
 
    	
        resetMatrix(); //initMatrix(m_view3D.getUndoRotationMatrix());
    	resetMatrix(); //initMatrix();
    	
    }
    
       
    */
    
        
    
    /////////////////////////
    // FPS
    
    /*
	double displayTime = 0;
	int nbFrame = 0;
	double fps = 0;
    
    private void drawFPS(){
    	
    	if (displayTime==0)
    		displayTime = System.currentTimeMillis();
    	
    	nbFrame++;
    	
    	double newDisplayTime = System.currentTimeMillis();
    	
    	
    	//displayTime = System.currentTimeMillis();
    	if (newDisplayTime > displayTime+1000){
    		
    		

    		fps = 1000*nbFrame/(newDisplayTime - displayTime);
    		displayTime = newDisplayTime;
    		nbFrame = 0;
    	}
    	
    	
    	
    	
        gl.glMatrixMode(GLlocal.GL_TEXTURE);
        gl.glLoadIdentity();
        
    	gl.glMatrixMode(GLlocal.GL_MODELVIEW);
    	
    	gl.glPushMatrix();
    	gl.glLoadIdentity();
    	
    	
    	textRenderer.begin3DRendering();

    	
    	textRenderer.setColor(Color.BLACK);
    	  	
        
    	textRenderer.draw3D("FPS="+ ((int) fps),left,bottom,0,1);
    	
        textRenderer.end3DRendering();
        
        gl.glPopMatrix();
    }
    
       
    */
    
   
    
    //////////////////////////////////////
    // picking
    
    /**
     * sets the mouse locations to (x,y) and asks for picking.
     * 
     * @param x x-coordinate of the mouse
     * @param y y-coordinate of the mouse
     */
    public void setMouseLoc(int x, int y, int pickingMode){
    	mouseX = x;
    	mouseY = y;
    	
    	this.pickingMode = pickingMode;
    	
    	// on next rending, a picking will be done : see doPick()
    	waitForPick = true;   	
    }
    
    /*
    private boolean intersectionCurvesWaitForPick = false;
    
    public void setIntersectionCurvesWaitForPick(){
    	intersectionCurvesWaitForPick = true;
    }
    */
    
    private int oldGeoToPickSize = -1;
    private int geoToPickSize = EuclidianView3D.DRAWABLES_NB;

	private IntBuffer selectBuffer;
    
	
    public void addOneGeoToPick(){
    	geoToPickSize++;
    }
    

	public void removeOneGeoToPick(){
		geoToPickSize--;
		/*
		if (geoToPickSize<0)
			Application.printStacktrace("");
			*/
	}
	
	
	
	private IntBuffer createSelectBufferForPicking(int bufSize){
		// Set Up the Selection Buffer
		//Application.debug(bufSize);
		IntBuffer ret = RendererJogl.newIntBuffer(bufSize);
        gl.glSelectBuffer(bufSize, ret); // Tell OpenGL To Use Our Array For Selection
        return ret; 
	}
	
	private static Drawable3D[] createDrawableListForPicking(int bufSize){
        return new Drawable3D[bufSize];
	}
	
	private void setGLForPicking(){		

        // The Size Of The Viewport. [0] Is <x>, [1] Is <y>, [2] Is <length>, [3] Is <width>
        int[] viewport = new int[4];
        gl.glGetIntegerv(GLlocal.GL_VIEWPORT, viewport, 0);      
        Dimension dim = canvas.getSize();
        // Puts OpenGL In Selection Mode. Nothing Will Be Drawn.  Object ID's and Extents Are Stored In The Buffer.
        gl.glRenderMode(GLlocal.GL_SELECT);
        gl.glInitNames(); // Initializes The Name Stack
        gl.glPushName(0); // Push 0 (At Least One Entry) Onto The Stack
         
        gl.glMatrixMode(GLlocal.GL_PROJECTION);
        gl.glLoadIdentity();
      
        
        /* create MOUSE_PICK_WIDTH x MOUSE_PICK_WIDTH pixel picking region near cursor location */
        glu.gluPickMatrix((double) mouseX, (double) (dim.height - mouseY), MOUSE_PICK_WIDTH, MOUSE_PICK_WIDTH, viewport, 0);
        setProjectionMatrixForPicking();
    	gl.glMatrixMode(GLlocal.GL_MODELVIEW);
    	
    	gl.glDisable(GLlocal.GL_ALPHA_TEST);
    	gl.glDisable(GLlocal.GL_BLEND);
    	gl.glDisable(GLlocal.GL_LIGHTING);
       	gl.glDisable(GLlocal.GL_TEXTURE);
    	
     	    	
    	// picking 
    	pickingLoop = 0;
	}
	
	private void pushSceneMatrix(){
        // set the scene matrix
    	gl.glPushMatrix();
        gl.glLoadMatrixd(view3D.getToScreenMatrix().get(),0);
	}
	
	private void storePickingInfos(Hits3D hits3D, int pointAndCurvesLoop, int labelLoop){

        int hits = gl.glRenderMode(GLlocal.GL_RENDER); // Switch To Render Mode, Find Out How Many
        
        int names, ptr = 0;
        float zMax, zMin;
        int num;
        

        //App.error("");
        
        for (int i = 0; i < hits ; i++) { 
        	     
          names = selectBuffer.get(ptr);  
          ptr++; // min z    
          zMin = getDepth(ptr, selectBuffer);
          ptr++; // max z
          zMax = getDepth(ptr, selectBuffer);           
          
          ptr++;


          for (int j = 0; j < names; j++){ 
        	  num = selectBuffer.get(ptr);

        	  if (hits3D==null){ // just update z min/max values for the drawable
        		  drawHits[num].zPickMin = zMin;
        		  drawHits[num].zPickMax = zMax;        		  
        	  }else{ // if for hits array, some checks are done
        		  PickingType type;
        		  if (num >= labelLoop){
        			  type = PickingType.LABEL;
        		  }else if (num >= pointAndCurvesLoop){
        			  type = PickingType.POINT_OR_CURVE;
        		  }else{
        			  type = PickingType.SURFACE;
        		  }
        		  hits3D.addDrawable3D(drawHits[num], type, zMin, zMax);
        		  //App.debug(i+": " + drawHits[num].getGeoElement());
        	  }
      		  
       	  
        	  //Application.debug(drawHits[num]+"\nzMin="+zMin+", zMax="+zMax);
        	  ptr++;
          }         
        }
	}
	
	private boolean needsNewPickingBuffer = true;
	
    /**
     * does the picking to sets which objects are under the mouse coordinates.
     */
    public void doPick(){
    	
   	
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
        gl.glPopMatrix();
 
        // picking labels
        int labelLoop = pickingLoop;
       
        if (pickingMode == PICKING_MODE_LABELS){
        	// picking labels
        	enableTextures();
        	gl.glDisable(GLlocal.GL_BLEND);
        	gl.glEnable(GLlocal.GL_ALPHA_TEST);
        	//gl.glAlphaFunc(GLlocal.GL_GREATER, 0);
            	
        	drawable3DLists.drawLabelForPicking(this);
        	
           	disableTextures();
           	gl.glDisable(GLlocal.GL_BLEND);
           	gl.glDisable(GLlocal.GL_ALPHA_TEST);
            
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
        
        gl.glEnable(GLlocal.GL_LIGHTING);
    }
    

	public enum PickingType {
		POINT_OR_CURVE,
		SURFACE,
		LABEL
	}
	
        
    /**
     * process picking for intersection curves
     * SHOULD NOT BE CALLED OUTSIDE THE DISPLAY LOOP
     */
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
        	d.zPickMax=Float.POSITIVE_INFINITY;
        	d.zPickMin=Float.POSITIVE_INFINITY; 
        	pick(d, true, PickingType.POINT_OR_CURVE);
        }
        
        
        // set off the scene matrix
        gl.glPopMatrix();
 
        storePickingInfos(null, 0, 0); // 0, 0 will be ignored since hits are passed as null        
        
        gl.glEnable(GLlocal.GL_LIGHTING);
    }   
    
    public void glLoadName(int loop){
    	gl.glLoadName(loop);
    }
    
    public void pick(Drawable3D d, PickingType type){  
    	 pick(d,false,type);
    }
    
    public void pick(Drawable3D d, boolean intersection, PickingType type){  
    	//Application.debug(d.getGeoElement()+"\npickingloop="+pickingLoop+"\ndrawHits length="+drawHits.length);  	
    	//Application.debug("1");
    	gl.glLoadName(pickingLoop);//Application.debug("2");
    	Drawable3D ret = d.drawForPicking(this,intersection, type);	//Application.debug("3");
    	if (ret!=null){
    		//App.debug(pickingLoop+","+ret);
    		drawHits[pickingLoop] = ret;//Application.debug("4");
    		pickingLoop++;//Application.debug("5");
    	}
    }
    
    public void pickLabel(Drawable3D d){   	
    	gl.glLoadName(pickingLoop);
    	if (d.drawLabelForPicking(this)){
    		//Application.debug(d.getGeoElement());
    		drawHits[pickingLoop] = d;
    		pickingLoop++;
    	}
    }
    
    /** returns the depth between 0 and 2, in double format, from an integer offset 
     *  lowest is depth, nearest is the object
     *  
     *  @param ptr the integer offset
     * */
    private static float getDepth(int ptr, IntBuffer selectBuffer){
     	
    	float depth = (float) selectBuffer.get(ptr)/0x7fffffff;
    	if (depth<0)
    		depth+=2;
    	return depth;   	
    }
    

    //////////////////////////////////
    // LIGHTS
    //////////////////////////////////

    static final public int LIGHT_NONE = 0;
    static final public int LIGHT_STANDARD = 1;
    static final public int LIGHT_HIGHLIGHTED = 2;
        
    private int light = LIGHT_NONE;
    
    /**
     * turn off current light and
     * turn on the light
     * @param light
     */
    public void setLight(int light){
    	if (this.light==light)
    		return;
    	
    	disableLight(this.light);
    	enableLight(light);
    	this.light=light;
    	
    }   
    
    private void disableLight(int light){
    	switch(light){
    	case LIGHT_STANDARD:
    		gl.glDisable(GLlocal.GL_LIGHT0);
    		break;
    	case LIGHT_HIGHLIGHTED:
    		gl.glDisable(GLlocal.GL_LIGHT1);
    		break;
    	}
    }
    
    private void enableLight(int light){
    	switch(light){
    	case LIGHT_STANDARD:
    		gl.glEnable(GLlocal.GL_LIGHT0);
    		break;
    	case LIGHT_HIGHLIGHTED:
    		gl.glEnable(GLlocal.GL_LIGHT1);
    		break;
    	}
    }  
    
    private float[] light0Position = {1f, 0f, 1f, 0f};
    //private float[] light2Position = {-1f, 0f, -1f, 0f};
    
    private void setLightPosition(){
        gl.glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_POSITION, light0Position, 0);
        gl.glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_POSITION, light0Position, 0);
        //gl.glLightfv(GLlocal.GL_LIGHT2, GLlocal.GL_POSITION, light2Position, 0);
    }
    
    //////////////////////////////////
    // clear color
    
    private boolean waitForUpdateClearColor = false;
    
    public void setWaitForUpdateClearColor(){
    	waitForUpdateClearColor = true;
    }
    
    private void updateClearColor(){
    	/*
    	if(view3D.getProjection()==EuclidianView3D.PROJECTION_ANAGLYPH)
    		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  
    	else
    		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);  
    		*/
    	Color c = view3D.getBackground();
    	float r = (float) c.getRed()/255;
    	float g = view3D.isShutDownGreen() ? 0 : (float) c.getGreen()/255;
    	float b = (float) c.getBlue()/255;
    	
        gl.glClearColor(r,g,b, 0.0f);   
    }
     
    
    //////////////////////////////////
    // initializations
    
    /** Called by the drawable immediately after the OpenGL context is
     * initialized for the first time. Can be used to perform one-time OpenGL
     * initialization such as setup of lights and display lists.
     * @param drawable The GLAutoDrawable object.
     */
    public void init(GLAutoDrawable drawable) {
    	
    	GL gl2 = drawable.getGL(); 
        App.debug("Init on "+Thread.currentThread()); 
        App.debug("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities()); 
        App.debug("INIT GL IS: " + gl2.getClass().getName()); 
        App.debug("GL_VENDOR: " + gl2.glGetString(GL.GL_VENDOR)); 
        App.debug("GL_RENDERER: " + gl2.glGetString(GL.GL_RENDERER)); 
        App.debug("GL_VERSION: " + gl2.glGetString(GL.GL_VERSION)); 
        
        // JOGL2 only, don't commit
        //App.debug("GL GLSL: "+gl2.hasGLSL()+", has-compiler: "+gl2.isFunctionAvailable("glCompileShader")+", version "+(gl2.hasGLSL() ? gl2.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION) : "none")); 
        //App.debug("GL Profile: "+gl2.getGLProfile()); 
        //App.debug("GL:" + gl2 + ", " + gl2.getContext().getGLVersion()); 
        
        // doesn't seem to work on JOGL1 or 2
        //App.debug("GL FBO: basic "+ gl2.hasBasicFBOSupport()+", full "+gl2.hasFullFBOSupport()); 
    		
    	//Application.printStacktrace("");

        gl = RendererJogl.getGL(drawable);
        
        // check openGL version
        final String version = gl.glGetString(GLlocal.GL_VERSION);
       
        
        // Check For VBO support
        final boolean VBOsupported = gl.isFunctionAvailable("glGenBuffersARB") &&
                gl.isFunctionAvailable("glBindBufferARB") &&
                gl.isFunctionAvailable("glBufferDataARB") &&
                gl.isFunctionAvailable("glDeleteBuffersARB");
        
        AppD.debug("openGL version : "+version
        		+", vbo supported : "+VBOsupported);
               
        //TODO use gl lists / VBOs
        //geometryManager = new GeometryManager(gl,GeometryManager.TYPE_DIRECT);
        geometryManager = new ManagerGLList(this,view3D);               
        
        float[] lightAmbient, lightDiffuse;
        
        //GL_LIGHT0
        float ambiant = 0.5f;
        lightAmbient = new float[] {ambiant, ambiant, ambiant, 1.0f};
        float diffuse=1f-ambiant;
        lightDiffuse = new float[] {diffuse, diffuse, diffuse, 1.0f};
        //float[] lightPosition = {-1.0f, 1f, 1.0f, 0.0f};
        
        gl.glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_DIFFUSE, lightDiffuse, 0);
        //gl.glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_POSITION, lightPosition, 0);
        
        /*
        float specular = 1f;
        float[] lightSpecular = {specular, specular, specular, 1f};
        gl.glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_SPECULAR, lightSpecular, 0);
        */
        //gl.glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_SHININESS, lightDiffuse, 0);
 
         
        
        //GL_LIGHT1
        ambiant = 0.4f;
        lightAmbient = new float[] {ambiant, ambiant, ambiant, 1.0f};
        diffuse=0.7f;//1f-ambiant;
        lightDiffuse = new float[] {diffuse, diffuse, diffuse, 1.0f};
        gl.glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_DIFFUSE, lightDiffuse, 0);
        //gl.glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_POSITION, lightPosition, 0);
                
        
        
        //material
        /*
        float[] mat_specular = { 1.0f, 1.0f, 1.0f, 1.0f };
        float[] mat_shininess = { 10.0f };
        gl.glMaterialfv(GLlocal.GL_FRONT, GLlocal.GL_SPECULAR, mat_specular,0);       
        gl.glMaterialfv(GLlocal.GL_FRONT, GLlocal.GL_SHININESS, mat_shininess,0);
        */
        
        gl.glColorMaterial(GLlocal.GL_FRONT_AND_BACK, GLlocal.GL_AMBIENT_AND_DIFFUSE);
        //gl.glColorMaterial(GLlocal.GL_FRONT, GLlocal.GL_AMBIENT_AND_DIFFUSE);
        //gl.glColorMaterial(GLlocal.GL_BACK, GLlocal.GL_AMBIENT);
        gl.glEnable(GLlocal.GL_COLOR_MATERIAL);
        
        /*
        //specular 
        FloatBuffer matSpecular = FloatBuffer.allocate(4);
        matSpecular.put(1f);
        matSpecular.put(0f);
        matSpecular.put(0f);
        matSpecular.put(1f);
        gl.glMaterialfv(GLlocal.GL_FRONT_AND_BACK, GLlocal.GL_SPECULAR, matSpecular);
        gl.glMateriali(GLlocal.GL_FRONT_AND_BACK, GLlocal.GL_SHININESS, 16);
         */
        
        
        gl.glEnable(GLlocal.GL_LIGHT0);
        
        gl.glShadeModel(GLlocal.GL_SMOOTH);
        gl.glLightModeli(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,GLlocal.GL_TRUE);
        gl.glLightModelf(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,GLlocal.GL_TRUE);
         
        gl.glEnable(GLlocal.GL_LIGHTING);
   
        //common enabling
        gl.glEnable(GLlocal.GL_DEPTH_TEST);
        gl.glDepthFunc(GLlocal.GL_LEQUAL); //less or equal for transparency
		gl.glEnable(GLlocal.GL_POLYGON_OFFSET_FILL);

        //gl.glPolygonOffset(1.0f, 2f);

        gl.glEnable(GLlocal.GL_CULL_FACE);
        
        //blending
        gl.glBlendFunc(GLlocal.GL_SRC_ALPHA, GLlocal.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GLlocal.GL_SRC_ALPHA, GLlocal.GL_DST_ALPHA);
        gl.glEnable(GLlocal.GL_BLEND);	
        updateClearColor();
               
        gl.glAlphaFunc(GLlocal.GL_NOTEQUAL, 0);//pixels with alpha=0 are not drawn
        //gl.glAlphaFunc(GLlocal.GL_GREATER, 0.8f);//pixels with alpha=0 are not drawn
        
        //using glu quadrics
        quadric = glu.gluNewQuadric();// Create A Pointer To The Quadric Object (Return 0 If No Memory) (NEW)
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);          // Create Smooth Normals (NEW)
        glu.gluQuadricTexture(quadric, true);                    // Create Texture Coords (NEW)
        
        //projection type
        //viewOrtho(gl); 
               
        //normal anti-scaling
        gl.glEnable(GLlocal.GL_NORMALIZE);
        //gl.glEnable(GLlocal.GL_RESCALE_NORMAL);
        
        //clipping planes
        //enableClipPlanes();
        //enableClipPlanes = true;
        
        //textures
        textures.init(gl);
       
        //reset euclidian view
        view3D.reset();       
        
        //reset picking buffer
        needsNewPickingBuffer = true;

    }  
    
    //projection mode
    
	int left = 0; int right = 640;
	int bottom = 0; int top = 480;
	int front = -1000; int back = 1000;
	int frontExtended;
	
	/** factor for drawing more than between front and back */
	private final static int DEPTH_FACTOR = 4;
		
	public int getLeft(){ return left;	}
	public int getRight(){ return right;	}
	public int getWidth(){return right-left;}
	public int getBottom(){ return bottom;	}
	public int getTop(){ return top;	}
	public int getHeight(){return top - bottom;}
	public float getFront(boolean extended){ 
		if (extended) {
			return frontExtended;
		} else {
			return front;
		}
	}
	public float getBack(boolean extended){ 
		if (extended) {
			return back*DEPTH_FACTOR;
		} else {
			return back;	
		}
	}	
	public float getDepth(){
		return back-front;
	}
	
	/** for a line described by (o,v), return the min and max parameters to draw the line
	 * @param minmax initial interval
	 * @param o origin of the line
	 * @param v direction of the line
	 * @param extendedDepth says if it looks to real depth bounds, or working depth bounds
	 * @return interval to draw the line
	 */
	public double[] getIntervalInFrustum(double[] minmax, 
			Coords o, Coords v,
			boolean extendedDepth){
			
		double left = (getLeft() - o.get(1))/v.get(1);
		double right = (getRight() - o.get(1))/v.get(1);		
		updateIntervalInFrustum(minmax, left, right);
		
		double top = (getTop() - o.get(2))/v.get(2);
		double bottom = (getBottom() - o.get(2))/v.get(2);
		updateIntervalInFrustum(minmax, top, bottom);
		
		double front = (getFront(extendedDepth) - o.get(3))/v.get(3);
		double back = (getBack(extendedDepth) - o.get(3))/v.get(3);
		updateIntervalInFrustum(minmax, front, back);
		
		return minmax;
	}
		
	/** return the intersection of intervals [minmax] and [v1,v2]
	 * @param minmax initial interval
	 * @param v1 first value
	 * @param v2 second value
	 * @return intersection interval
	 */
	private static double[] updateIntervalInFrustum(double[] minmax, double v1, double v2){
		
		if (v1>v2){
			double v = v1;
			v1 = v2; v2 = v;
		}
		
		if (v1>minmax[0])
			minmax[0] = v1;
				
		if (v2<minmax[1])
			minmax[1] = v2;
	
		return minmax;
	}
	
	/**
	 * set up the view
	 */
	private void setView(){
		gl.glViewport(0,0,right-left,top-bottom);

		gl.glMatrixMode(GLlocal.GL_PROJECTION);
		gl.glLoadIdentity();

		setProjectionMatrix();

    	gl.glMatrixMode(GLlocal.GL_MODELVIEW);		
	}	
	
	private void setProjectionMatrixForPicking(){
		
		switch(view3D.getProjection()){
		case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
			viewOrtho();
			break;
		case EuclidianView3D.PROJECTION_ANAGLYPH:
			viewAnaglyph();
			break;
		case EuclidianView3D.PROJECTION_PERSPECTIVE:
			viewPersp();
			break;
		case EuclidianView3D.PROJECTION_CAV:
			viewCav();
			break;
		}
				
	}
	
	private void setProjectionMatrix(){
		
		switch(view3D.getProjection()){
		case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
			viewOrtho();
			break;
		case EuclidianView3D.PROJECTION_PERSPECTIVE:
			viewPersp();
			break;
		case EuclidianView3D.PROJECTION_ANAGLYPH:
			viewAnaglyph();
			break;
		case EuclidianView3D.PROJECTION_CAV:
			viewCav();
			break;
		}
				
		//viewEye();
	}
   
	public void updateOrthoValues(){
		frontExtended = front*DEPTH_FACTOR;
	}
	
	
    /**
     * Set Up An Ortho View regarding left, right, bottom, front values
     * 
     */
    private void viewOrtho(){

    	gl.glOrtho(getLeft(),getRight(),getBottom(),getTop(),getFront(true),getBack(true));   	
    }

    
    private double near = 0;
    //private double distratio;
    
    public void setNear(double val){
    	near = val;
    	updatePerspValues();   	
    }
    
    private double perspLeft, perspRight, perspBottom, perspTop, perspFar, perspNear, perspDistratio, perspFocus;
    private Coords perspEye;
    
    private void updatePerspValues(){
    	
    	//distance camera-near plane
    	perspNear = 10; //TODO set this to avoid z-buffer issues
    	//distance near plane-origin
    	double d1 = near;
    	frontExtended = (int) -d1; //front clipping plane
    	
    	perspFocus = -perspNear-near;
    	
    	//Application.debug(near+"\nleft="+getLeft()+"\nd1="+d1);
    	//if (near<0.01)
    	//	near=0.01;
    	//ratio so that distance on origin plane are not changed
    	perspDistratio = perspNear/(perspNear+d1);
    	//frustum    	
    	perspLeft = getLeft()*perspDistratio;
    	perspRight = getRight()*perspDistratio;
    	perspBottom = getBottom()*perspDistratio;
    	perspTop = getTop()*perspDistratio;
    	//distance camera-far plane
    	perspFar = perspNear+getBack(true)-getFront(true);
    	
    	perspEye = new Coords(0,0,-perspFocus,1);   	
    }
    
    /**
     * 
     * @return coords of the eye (in real coords) when perspective projection
     */
    public Coords getPerspEye(){
    	return perspEye;
    }
    
    /**
     * 
     * @return eyes separation (half of, in real coords)
     */
    public double getEyeSep(){
    	return anaglyphEyeSep;
    }
    
    private void viewPersp(){
    	
    	gl.glFrustum(perspLeft,perspRight,perspBottom,perspTop,perspNear,perspFar);
    	gl.glTranslated(0, 0, perspFocus);           	
    }       
    
    private double anaglyphEyeSep, anaglyphEyeSep1;
    
    public void updateAnaglyphValues(){
    	//eye separation
    	anaglyphEyeSep= perspFocus*view3D.getEyeSepFactor();
    	//eye separation for frustum
    	anaglyphEyeSep1 = anaglyphEyeSep*perspDistratio;//(1-distratio);    
    	//Application.debug("eyesep="+eyesep+"\ne1="+e1);
    }
    
    private void viewAnaglyph(){
    	    	
    	//eye separation
    	double eyesep, eyesep1;
    	if(eye==EYE_LEFT){
    		eyesep=-anaglyphEyeSep;
    		eyesep1=-anaglyphEyeSep1;
    	}else{
    		eyesep=anaglyphEyeSep;
    		eyesep1=anaglyphEyeSep1;
    	}
   	
       	gl.glFrustum(perspLeft+eyesep1,perspRight+eyesep1,perspBottom,perspTop,perspNear,perspFar);
    	gl.glTranslated(eyesep, 0, perspFocus);          	
    }
        
    private static final int EYE_ONE = -1;
    private static final int EYE_LEFT = 0;
    private static final int EYE_RIGHT = 1;
    private int eye = EYE_ONE;
    
    private void setColorMask(){
    	if (view3D.getProjection()==EuclidianView3D.PROJECTION_ANAGLYPH){
    		if (eye==EYE_LEFT) {
    			gl.glColorMask(true,false,false,true); //cyan
    			//gl.glColorMask(false,true,false,true); //magenta
       			//gl.glColorMask(false,false,false,true);
    		} else {
    			gl.glColorMask(false,!view3D.isAnaglyphShutDownGreen(),true,true); //red
       			//gl.glColorMask(true,false,false,true); //cyan -> green
       			//gl.glColorMask(false,false,false,true);
       		}
    	} else {
    		gl.glColorMask(true,true,true,true);
    	}	
    }
    
    enum ExportType { NONE, ANIMATEDGIF, THUMBNAIL_IN_GGBFILE, PNG, CLIPBOARD };
    
    private double cavX, cavY;
    private Coords cavOrthoDirection; //direction "orthogonal" to the screen (i.e. not visible)
	private ExportType exportType = ExportType.NONE;
	private int export_n;
	private double export_val;
	private double export_min;
	private double export_max;
	private double export_step;
	private AnimatedGifEncoder gifEncoder;
	private int export_i;
	private GeoNumeric export_num;
    
    public void updateCavValues(){
    	updateOrthoValues();
    	double angle = Math.toRadians(view3D.getCavAngle());
    	cavX = -view3D.getCavFactor()*Math.cos(angle);
    	cavY = -view3D.getCavFactor()*Math.sin(angle);
    	cavOrthoDirection = new Coords(cavX, cavY, -1, 0);
    }
    
    private void viewCav(){
    	viewOrtho();
    	
    	gl.glMultMatrixd(new double[] {
    			1,0,0,0,
    			0,1,0,0,
    			cavX,cavY,1,0, 
    			0,0,0,1
    	}, 0);  	
    }
    
    public Coords getCavOrthoDirection(){
    	return cavOrthoDirection;
    }
    
    /**
     * Set Up An Ortho View after setting left, right, bottom, front values
     * @param x left
     * @param y bottom
     * @param w width
     * @param h height
     * 
     */
    private void setView(int x, int y, int w, int h){
    	left=x-w/2;
    	bottom=y-h/2;
    	right=left+w;
    	top = bottom+h;
    	
    	int depth = w/2;//sets depth equals width
       	//int depth = (w+h)/4;//sets depth equals mean(width,height)
      	front = -depth;   	
    	back = depth;


    	switch (view3D.getProjection()){
    	case EuclidianView3D.PROJECTION_ORTHOGRAPHIC:
    		updateOrthoValues();
    		break;
    	case EuclidianView3D.PROJECTION_PERSPECTIVE:
    		updatePerspValues();
    		break;
    	case EuclidianView3D.PROJECTION_ANAGLYPH:
    		updatePerspValues();
    		updateAnaglyphValues();
    		break;
    	}
    	    	
    	setView();
    	
    	view3D.setViewChanged();
    	view3D.setWaitForUpdate();
    }

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub		
	}



	public void startAnimatedGIFExport(AnimatedGifEncoder gifEncoder,
			GeoNumeric num, int n, double val, double min, double max,
			double step) {
		exportType  = ExportType.ANIMATEDGIF;
		
		num.setValue(val);
		num.updateRepaint();
		export_i = 0;

		
		this.export_n = n;
		this.export_num = num;
		this.export_val = val;
		this.export_min = min;
		this.export_max = max;
		this.export_step = step;
		this.gifEncoder = gifEncoder;
		
	}



	public void exportToClipboard() {
		exportType = ExportType.CLIPBOARD;
		
	}
	
}
