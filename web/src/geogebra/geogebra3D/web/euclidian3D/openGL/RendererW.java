package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.awt.GColor;
import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.euclidian3D.openGL.shaders.Shaders;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.googlecode.gwtgl.array.Float32Array;
import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLProgram;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLShader;
import com.googlecode.gwtgl.binding.WebGLUniformLocation;

/**
 * class for web openGL renderer
 * @author mathieu
 *
 */
public class RendererW extends Renderer{
	
	private WebGLRenderingContext glContext;
	
	private Canvas webGLCanvas;
    private WebGLProgram shaderProgram;
    private int vertexPositionAttribute;
    private WebGLBuffer vertexBuffer;
    
    
    
    private Timer loopTimer;
    private long lastTime, timeNow;


	/**
	 * constructor
	 * @param view 3D view
	 */
    public RendererW(EuclidianView3DW view) {
    	super(view);

    	webGLCanvas = Canvas.createIfSupported();

    	glContext = (WebGLRenderingContext) webGLCanvas.getContext("experimental-webgl");
    	if(glContext == null) {
    		Window.alert("Sorry, Your Browser doesn't support WebGL!");
    	}

    	setView(0, 0, 500, 500);


    	lastTime = 0;

    	loopTimer = new Timer() {
    		@Override
    		public void run() {
    			loop();
    		}
    	}; 
    	loopTimer.scheduleRepeating(10);


    }
    
	protected void loop() {
		drawScene();
		update();
	}

	private void update() {
		timeNow = System.currentTimeMillis();
		if (lastTime != 0) {
			long elapsed = timeNow - lastTime;
			//mesh.rotate(elapsed);
		}
		lastTime = timeNow; 
	}

	
	

	@Override
    public void setView(int x, int y, int w, int h) {        
		webGLCanvas.setCoordinateSpaceWidth(w);
        webGLCanvas.setCoordinateSpaceHeight(h);
        glContext.viewport(0, 0, w, h);
        
        super.setView(x, y, w, h);
        
        start();
        
	}
	
	
	
	
	
	
	/**
	 * 
	 * @return openGL canvas
	 */
	public Canvas getGLCanvas(){
		return webGLCanvas;
	}
	
	
	private void start() {
        initShaders();
        glContext.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glContext.clearDepth(1.0f);
        glContext.enable(WebGLRenderingContext.DEPTH_TEST);
        glContext.depthFunc(WebGLRenderingContext.LEQUAL);
        initBuffers();

        drawScene();
	}
	
	/**
	 * init shaders
	 */
	@Override
    public void initShaders() {
        WebGLShader fragmentShader = getShader(WebGLRenderingContext.FRAGMENT_SHADER, Shaders.INSTANCE.fragmentShader().getText());
        WebGLShader vertexShader = getShader(WebGLRenderingContext.VERTEX_SHADER, Shaders.INSTANCE.vertexShader().getText());

        shaderProgram = glContext.createProgram();
        glContext.attachShader(shaderProgram, vertexShader);
        glContext.attachShader(shaderProgram, fragmentShader);
        glContext.linkProgram(shaderProgram);

        if (!glContext.getProgramParameterb(shaderProgram, WebGLRenderingContext.LINK_STATUS)) {
                throw new RuntimeException("Could not initialise shaders");
        }

        glContext.useProgram(shaderProgram);

        vertexPositionAttribute = glContext.getAttribLocation(shaderProgram, "vertexPosition");
        glContext.enableVertexAttribArray(vertexPositionAttribute);
	}
	
	private WebGLShader getShader(int type, String source) {
        WebGLShader shader = glContext.createShader(type);

        glContext.shaderSource(shader, source);
        glContext.compileShader(shader);

        if (!glContext.getShaderParameterb(shader, WebGLRenderingContext.COMPILE_STATUS)) {
                throw new RuntimeException(glContext.getShaderInfoLog(shader));
        }

        return shader;
	}
	
	
	private void initBuffers() {
        vertexBuffer = glContext.createBuffer();
        glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vertexBuffer);
        float[] vertices = new float[]{
                         0.0f,  1.0f,  -5.0f, // first vertex
                        -1.0f, -1.0f,  -5.0f, // second vertex
                         1.0f, -1.0f,  -5.0f  // third vertex
        };
        glContext.bufferData(WebGLRenderingContext.ARRAY_BUFFER, Float32Array.create(vertices), WebGLRenderingContext.STATIC_DRAW);
	
	
	}
	
	
	
	private void drawScene() {

        glContext.clear(WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT);
         
       	setView();
    	
        WebGLUniformLocation uniformLocation = glContext.getUniformLocation(shaderProgram, "modelview");
        glContext.uniformMatrix4fv(uniformLocation, false, view3D.getToScreenMatrix().getForGL());        
        glContext.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
        glContext.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 3);
	}
	
	
	private float[] createPerspectiveMatrix(int fieldOfViewVertical, float aspectRatio, float minimumClearance, float maximumClearance) {
        float top    = minimumClearance * (float)Math.tan(fieldOfViewVertical * Math.PI / 360.0);
        float bottom = -top;
        float left   = bottom * aspectRatio;
        float right  = top * aspectRatio;

        float X = 2*minimumClearance/(right-left);
        float Y = 2*minimumClearance/(top-bottom);
        float A = (right+left)/(right-left);
        float B = (top+bottom)/(top-bottom);
        float C = -(maximumClearance+minimumClearance)/(maximumClearance-minimumClearance);
        float D = -2*maximumClearance*minimumClearance/(maximumClearance-minimumClearance);

        return new float[]{     X, 0.0f, A, 0.0f,
                                                0.0f, Y, B, 0.0f,
                                                0.0f, 0.0f, C, -1.0f,
                                                0.0f, 0.0f, D, 0.0f};
	};
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
    protected Manager createManager() {
	    return new ManagerW(this, view3D);
    }
	
	
	

	@Override
    public void display() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableCulling() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableCulling() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setCullFaceFront() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setCullFaceBack() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableBlending() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableBlending() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLight(int light) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableTextures() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableTextures() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableMultisample() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableMultisample() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableAlphaTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableAlphaTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableLighting() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableLighting() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void enableClipPlane(int n) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void disableClipPlane(int n) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setClipPlane(int n, double[] equation) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setMatrixView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void unsetMatrixView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableDepthMask() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableDepthMask() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableDepthTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableDepthTest() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColorMask(boolean r, boolean g, boolean b, boolean a) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setLineWidth(float width) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColor(Coords color) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColor(GColor color) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setLayer(float l) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void initMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void resetMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void drawMouseCursor() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setGLForPicking() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void pushSceneMatrix() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void storePickingInfos(Hits3D hits3d, int pointAndCurvesLoop,
            int labelLoop) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void doPick() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void pickIntersectionCurves() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void glLoadName(int loop) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLightPosition(int light, float[] values) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
            float ambiant1, float diffuse1) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setClearColor(float r, float g, float b, float a) {
	    // TODO Auto-generated method stub
	    
    }


	@Override
    protected void setColorMaterial() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setLightModel() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setAlphaFunc() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void resumeAnimator() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	protected void setView() {
		
		float[] projection = {
                2.0f/getWidth(), 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f/getHeight(), 0.0f, 0.0f,
                0.0f, 0.0f, -2.0f/getVisibleDepth(), 0f,
                0.0f, 0.0f, -1f/getVisibleDepth(), 1.0f,
        };

        WebGLUniformLocation uniformLocation = glContext.getUniformLocation(shaderProgram, "projection");
        glContext.uniformMatrix4fv(uniformLocation, false, projection);        

	}

	@Override
    protected void disableStencilLines() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setStencilLines() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewOrtho() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewPersp() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewGlasses() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void viewOblique() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableTextures2D() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void disableTextures2D() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void genTextures2D(int number, int[] index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void bindTexture(int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void removeTexture(int index) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setTextureLinear() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setTextureNearest() {
	    // TODO Auto-generated method stub
	    
    }

}
