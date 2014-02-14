package geogebra3D.euclidian3D.opengl;

import geogebra.common.main.App;
import geogebra.main.AppD;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import java.awt.Toolkit;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * openGL renderers for desktop
 * @author mathieu
 *
 */
public abstract class RendererD extends Renderer  implements GLEventListener {
	

	private Animator animator;
	
	/** canvas usable for a JPanel */
	public Component3D canvas;

	
	/**
	 * constructor
	 * @param view 3D view
	 * @param useCanvas true if we want to use Canvas (instead of JPanel)
	 */
	public RendererD(EuclidianView3D view, boolean useCanvas) {
		super(view);
		
		jogl = new RendererJogl();
		
		//canvas = view;
		App.debug("create 3D component -- use Canvas : " + useCanvas);
        canvas = RendererJogl.createComponent3D(useCanvas);
        
        App.debug("add gl event listener");
	    canvas.addGLEventListener(this);
	    
	    
	    App.debug("create animator");
	    animator = RendererJogl.createAnimator( canvas, 60 );
        //animator.setRunAsFastAsPossible(true);	  
        //animator.setRunAsFastAsPossible(false);	
	    

	    App.debug("start animator");
        animator.start();

	}
	
    @Override
	public void resumeAnimator(){
    	animator.resume();
    }
    
    
	@Override
	public void display(){		
		canvas.display();
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
    	
    	//update 3D controller
    	((EuclidianController3D) view3D.getEuclidianController()).updateInput3D();
    	
    	//Application.debug("display");

    	//double displayTime = System.currentTimeMillis();
        
        setGL(gLDrawable);         
        
        useShaderProgram();
        
        //picking        
        if(waitForPick){
        	doPick();        	
        }
        	
        
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
        ((EuclidianController3D) view3D.getEuclidianController()).update();
        

        
        // update 3D view
        view3D.update();
        view3D.updateOwnDrawablesNow();
        
        // update 3D drawables
        drawable3DLists.updateAll();

    	// say that 3D view changed has been performed
        view3D.resetViewChanged();
       

        
        if (waitForSetStencilLines){
        	setStencilLines();
        }
        
        if (waitForDisableStencilLines){
        	disableStencilLines();
        }

        
        if (waitForUpdateClearColor) {
        	updateClearColor();
        	waitForUpdateClearColor=false;
        }
        
        //clear color buffer
        getGL().glClear(GLlocal.GL_COLOR_BUFFER_BIT);
        
        
        if (view3D.getProjection()==EuclidianView3D.PROJECTION_GLASSES) {
 
        	//setStencilLines();


        	getGL().glClear(GLlocal.GL_DEPTH_BUFFER_BIT);


        	//left eye
        	if (view3D.isPolarized()){
        		// draw where stencil's value is 0
        		getGL().glStencilFunc(GLlocal.GL_EQUAL, 0, 0xFF);
        	}

        	eye=EYE_LEFT;
        	setColorMask();
        	setView();
        	draw(); 
        	

        	//right eye
           	if (view3D.isPolarized()){
        		// draw where stencil's value is 1
        		getGL().glStencilFunc(GLlocal.GL_EQUAL, 1, 0xFF);
        	}
           	
        	eye=EYE_RIGHT;
        	setColorMask();
        	getGL().glClear(GLlocal.GL_DEPTH_BUFFER_BIT); //clear depth buffer
        	setView();
        	draw(); 
        	
        } else {  
        	getGL().glClear(GLlocal.GL_DEPTH_BUFFER_BIT);
        	setView();
        	draw(); 
        }
        
        // prepare correct color mask for next clear
    	getGL().glColorMask(true,true,true,true);
        
            
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
			exportType = ExportType.NONE;
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
			
      	
        	break;
        case UPLOAD_TO_GEOGEBRATUBE:
			exportType = ExportType.NONE;
        	App.debug("Uploading to GeoGebraTube");
        	
        	setExportImage();
        	
			if (bi == null) {
				App.error("image null");
			} else {
				
				view3D.getApplication().uploadToGeoGebraTube();
				
			}
			
      	
        	break;

        }
        
        
    }
    
    

	public void dispose(GLAutoDrawable arg0) {
		// NOTHING TO DO HERE -- NEEDED TO AVOID ERRORS IN INSTALLED/PORTABLE VERSIONS	
	}
	
	
	   /** Called by the drawable immediately after the OpenGL context is
     * initialized for the first time. Can be used to perform one-time OpenGL
     * initialization such as setup of lights and display lists.
     * @param drawable The GLAutoDrawable object.
     */
    public void init(GLAutoDrawable drawable) {
    	
    	// reset picking
    	oldGeoToPickSize = -1;
    	
    	// start init
    	App.debug("\n"+RendererJogl.getGLInfos(drawable)); 
        
        // JOGL2 only, don't commit
        //App.debug("GL GLSL: "+gl2.hasGLSL()+", has-compiler: "+gl2.isFunctionAvailable("glCompileShader")+", version "+(gl2.hasGLSL() ? gl2.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION) : "none")); 
        //App.debug("GL Profile: "+gl2.getGLProfile()); 
        //App.debug("GL:" + gl2 + ", " + gl2.getContext().getGLVersion()); 
        
        // doesn't seem to work on JOGL1 or 2
        //App.debug("GL FBO: basic "+ gl2.hasBasicFBOSupport()+", full "+gl2.hasFullFBOSupport()); 
    		
    	//Application.printStacktrace("");

    	setGL(drawable);
        
        
        // check openGL version
        final String version = getGL().glGetString(GLlocal.GL_VERSION);
       
        
        // Check For VBO support
        final boolean VBOsupported = getGL().isFunctionAvailable("glGenBuffersARB") &&
                getGL().isFunctionAvailable("glBindBufferARB") &&
                getGL().isFunctionAvailable("glBufferDataARB") &&
                getGL().isFunctionAvailable("glDeleteBuffersARB");
        
        AppD.debug("openGL version : "+version
        		+", vbo supported : "+VBOsupported);
        

        initShaders();
               
        
        geometryManager = createManager();
                    
        
        
        //GL_LIGHT0 & GL_LIGHT1
        float ambiant0 = 0.5f;
        float diffuse0 = 1f-ambiant0; 
        
        float ambiant1 = 0.4f;
        float diffuse1=0.7f;//1f-ambiant;
        
        setLightAmbiantDiffuse(ambiant0, diffuse0, ambiant1, diffuse1);
                
        
        
        
        //material and light
        setColorMaterial();
        getGL().glEnable(GLlocal.GL_COLOR_MATERIAL);
        
        
        
        //setLight(GLlocal.GL_LIGHT0);        
        setLightModel();        
        getGL().glEnable(GLlocal.GL_LIGHTING);
        
   
        //common enabling
        getGL().glEnable(GLlocal.GL_DEPTH_TEST);
        getGL().glDepthFunc(GLlocal.GL_LEQUAL); //less or equal for transparency
		getGL().glEnable(GLlocal.GL_POLYGON_OFFSET_FILL);
        getGL().glEnable(GLlocal.GL_CULL_FACE);
        
        //blending
        getGL().glBlendFunc(GLlocal.GL_SRC_ALPHA, GLlocal.GL_ONE_MINUS_SRC_ALPHA);
        getGL().glEnable(GLlocal.GL_BLEND);	
        updateClearColor();
               
        setAlphaFunc();
        
               
        //normal anti-scaling
        getGL().glEnable(GLlocal.GL_NORMALIZE);
        
        //textures
        textures.init();
       
        //reset euclidian view
        view3D.reset();       
        
        //reset picking buffer
        needsNewPickingBuffer = true;
        
       	// ensure that animation is on (needed when undocking/docking 3D view)
        resumeAnimator();        

    }  
    
    
    /**
     * openGL method called when the canvas is reshaped.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
    	
    	setGL(drawable);   
    	
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
	
	
    @Override
	public void enableTextures2D(){
    	getGL().glEnable(GL.GL_TEXTURE_2D);
    }
    
    
    @Override
	public void disableTextures2D(){
    	getGL().glDisable(GL.GL_TEXTURE_2D);
    }
    
    @Override
	public void genTextures2D(int number, int[] index){
    	getGL().glGenTextures(number, index, 0);
    }
    
    @Override
	public void bindTexture(int index){
    	getGL().glBindTexture(GL.GL_TEXTURE_2D, index);
    }
    
    @Override
	public void removeTexture(int index){
    	getGL().glDeleteTextures(1, new int[] {index}, 0);
    }
    
    @Override
	public void textureImage2D(int sizeX, int sizeY, ByteBuffer buf){
    	getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0,  GL.GL_ALPHA, sizeX, sizeY, 0, GL.GL_ALPHA, GL.GL_UNSIGNED_BYTE, buf);
		 
    }
    
    public void setTextureLinear(){
    	getGL().glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_LINEAR);
    	getGL().glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
    	getGL().glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture
    	getGL().glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE); //prevent repeating the texture

	}
    
    
	public void setTextureNearest(){
		getGL().glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
		getGL().glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);
	}

    
    

}
