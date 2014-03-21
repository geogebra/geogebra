package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.euclidian3D.openGL.shaders.Shaders;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
public class RendererW extends Renderer implements RendererShadersInterface{
	
	private WebGLRenderingContext glContext;
	
	private Canvas webGLCanvas;
    private WebGLProgram shaderProgram;
    private int vertexPositionAttribute, colorAttribute, normalAttribute, textureAttribute;
    
    
    
    private Timer loopTimer;
    
    // location values for shader fields
    private WebGLUniformLocation modelviewLocation, projectionLocation; // matrices
    private WebGLUniformLocation lightPositionLocation, ambiantDiffuseLocation; // light
    private WebGLUniformLocation viewDirectionLocation; //view direction
    private WebGLUniformLocation textureTypeLocation; // textures
    private WebGLUniformLocation colorLocation; // color
    private WebGLUniformLocation normalLocation; // one normal for all vertices
    
    private WebGLBuffer vboVertices, vboColors, vboNormals, vboTextureCoords;
    
    
    final static private int TEXTURE_TYPE_NONE = 0;
    final static private int TEXTURE_TYPE_FADING = 1;
    final static private int TEXTURE_TYPE_TEXT = 2;
    final static private int TEXTURE_TYPE_DASH = 4;

    

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

		loopTimer = new Timer() {
			@Override
			public void run() {
				drawScene();
			}
		}; 
		loopTimer.scheduleRepeating(10);

	}
	

	/**
	 * init shaders
	 */
	@Override
    public void initShaders() {
        WebGLShader fragmentShader = getShader(WebGLRenderingContext.FRAGMENT_SHADER, Shaders.INSTANCE.fragmentShader().getText());
        WebGLShader vertexShader = getShader(WebGLRenderingContext.VERTEX_SHADER, Shaders.INSTANCE.vertexShader().getText());

        // create shader program
        shaderProgram = glContext.createProgram();
        
        // attach shaders
        glContext.attachShader(shaderProgram, vertexShader);
        glContext.attachShader(shaderProgram, fragmentShader);
        
        // bind attributes location
        glContext.bindAttribLocation(shaderProgram, 0, "attribute_Position");
        glContext.bindAttribLocation(shaderProgram, 1, "attribute_Normal");
        glContext.bindAttribLocation(shaderProgram, 2, "attribute_Color");
        glContext.bindAttribLocation(shaderProgram, 3, "attribute_Texture");

        // link the program
        glContext.linkProgram(shaderProgram);

        if (!glContext.getProgramParameterb(shaderProgram, WebGLRenderingContext.LINK_STATUS)) {
                throw new RuntimeException("Could not initialise shaders");
        }       
         
        // use the program
        glContext.useProgram(shaderProgram);
        
        
        
        // attributes : note that vertex shader must use it, otherwise getAttribLocation will return -1 (undefined)
        vertexPositionAttribute = glContext.getAttribLocation(shaderProgram, "attribute_Position");
        normalAttribute = glContext.getAttribLocation(shaderProgram, "attribute_Normal");
        colorAttribute = glContext.getAttribLocation(shaderProgram, "attribute_Color");
        textureAttribute = glContext.getAttribLocation(shaderProgram, "attribute_Texture");

        App.debug("vertexPositionAttribute="+vertexPositionAttribute+","
        		+"normalAttribute="+normalAttribute+","
        		+"colorAttribute="+colorAttribute+","
        		+"textureAttribute="+textureAttribute);
        
        
        // uniform location
        modelviewLocation = glContext.getUniformLocation(shaderProgram, "modelview");
        projectionLocation = glContext.getUniformLocation(shaderProgram, "projection");
        
        lightPositionLocation = glContext.getUniformLocation(shaderProgram, "lightPosition");
        ambiantDiffuseLocation = glContext.getUniformLocation(shaderProgram, "ambiantDiffuse");
        viewDirectionLocation = glContext.getUniformLocation(shaderProgram, "viewDirection");
     
        
        //texture
        textureTypeLocation = glContext.getUniformLocation(shaderProgram, "textureType");
               
        //color
        colorLocation = glContext.getUniformLocation(shaderProgram, "color");

        //color
        normalLocation = glContext.getUniformLocation(shaderProgram, "normal");

        
        // VBOs
        vboColors = glContext.createBuffer();
        vboVertices = glContext.createBuffer();
        vboNormals = glContext.createBuffer();
        vboTextureCoords = glContext.createBuffer();
 
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
	

	
	
	private void drawTriangle(float[] vertices, float[] normals, float[] textureCoords){
  
       	/*
       	byte[] bytes = new byte[]{
       			(byte) 255, (byte) 255, (byte) 255, 
       			(byte) 0, (byte) 0, (byte) 0
       	};
           	
    	int texture = getTextures().createAlphaTexture(2, 2, bytes);
    	

    	enableTextures2D();
    	getTextures().setTextureLinear(texture);
    	*/
    	
    	
    	ArrayList<Float> array = new ArrayList<Float>();
    	
    	for (int i = 0; i < 3 * 3; i++){ array.add(vertices[i]); }
    	loadVertexBuffer(GLFactory.prototype.newBuffer(array), 3);

    	if (normals != null){
    		array.clear(); for (int i = 0; i < 3 * 3; i++){ array.add(normals[i]); }
    		loadNormalBuffer(GLFactory.prototype.newBuffer(array), 3);
    	}

    	if (textureCoords != null){
    		array.clear(); for (int i = 0; i < 3 * 2; i++){ array.add(textureCoords[i]); }
    		loadTextureBuffer(GLFactory.prototype.newBuffer(array), 3);	
    	}

		draw(Manager.Type.TRIANGLES, 3);
		
		/*
		bindTexture(0);
		getTextures().removeTexture(texture);
		*/
        
    }


	private static final float[] MODEL_VIEW_IDENTITY = {
		1,0,0,0,
		0,1,0,0,
		0,0,1,0,
		0,0,0,1
	};

	private final void setModelViewIdentity(){
		glContext.uniformMatrix4fv(modelviewLocation, false, MODEL_VIEW_IDENTITY);     
	}

	@Override
	protected void draw(){
		
		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();
		enableTexturesForText();

		super.draw();
	}  

	
	/*
	@Override
    protected void draw() {

    	
		setMatrixView();
        setLightPosition();     
        setLight(0);
        
        
        
        
		
		float[] vertices = {
				0f, 0f, 0f,
				1f, 0f, 0f,
				0f, 1f, 0f
		};
		
		float[] normals = {
				0f, 0f, 1f,
				0f, 0f, 1f,
				0f, 0f, 1f
		};
		
		setColor(1f, 0f, 0f, 1f);
		drawTriangle(vertices, normals, null);
		
		vertices = new float[] {
				0f, 0f, 0f,
				0f, 1f, 0f,
				0f, 0f, 1f
		};
		
		normals = new float[] {
				1f, 0f, 0f,
				1f, 0f, 0f,
				1f, 0f, 0f
		};
		
		setColor(0f, 1f, 0f, 1f);
		drawTriangle(vertices, normals, null);
		
		vertices = new float[] {
				0f, 0f, 0f,
				0f, 0f, 1f,
				1f, 0f, 0f
		};
		
		normals = new float[] {
				0f, 1f, 0f,
				0f, 1f, 0f,
				0f, 1f, 0f
		};
		
		setColor(0f, 0f, 1f, 1f);
		drawTriangle(vertices, normals, null);
		
	}
	*/
	
	



	@Override
    protected void clearColorBuffer() {
		glContext.clear(WebGLRenderingContext.COLOR_BUFFER_BIT);	    
    }






	@Override
    protected void clearDepthBuffer() {
		glContext.clear(WebGLRenderingContext.DEPTH_BUFFER_BIT);	    
    }

	
	
	
	
	

	@Override
    protected Manager createManager() {
	    return new ManagerShaders(this, view3D);
    }
	
	
	

	@Override
    public void display() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void enableCulling() {
		glContext.enable(WebGLRenderingContext.CULL_FACE);
	    
    }

	@Override
    public void disableCulling() {
		glContext.disable(WebGLRenderingContext.CULL_FACE);
	    
    }

	@Override
	public void setCullFaceFront() {
		glContext.cullFace(WebGLRenderingContext.FRONT);

	}

	@Override
	public void setCullFaceBack() {
		glContext.cullFace(WebGLRenderingContext.BACK);

	}

	@Override
    public void disableBlending() {
		glContext.disable(WebGLRenderingContext.BLEND);
	    
    }

	@Override
    public void enableBlending() {
		glContext.enable(WebGLRenderingContext.BLEND);
	    
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
	    // done by shader
    }

	@Override
    public void disableAlphaTest() {
		// done by shader
    }

	@Override
    public void disableLighting() {
		// done by shader
    }

	@Override
    public void enableLighting() {
		// done by shader
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
		
		 glContext.uniformMatrix4fv(modelviewLocation, false, view3D.getToScreenMatrix().getForGL());     
    }

	@Override
    protected void unsetMatrixView() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	public void enableDepthMask() {
		glContext.depthMask(true);

	}

	@Override
	public void disableDepthMask() {
		glContext.depthMask(false);

	}

	@Override
	public void enableDepthTest() {
		glContext.enable(WebGLRenderingContext.DEPTH_TEST);

	}

	@Override
	public void disableDepthTest() {
		glContext.disable(WebGLRenderingContext.DEPTH_TEST);

	}

	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {

		glContext.colorMask(r, g, b, a);
	    
    }

	@Override
    public void setLineWidth(float width) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setColor(float r, float g, float b, float a) {
		glContext.uniform4f(colorLocation, r,g,b,a);
    }


	@Override
    public void setLayer(float l) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	public void initMatrix() {
		glContext.uniformMatrix4fv(modelviewLocation, false, view3D.getToScreenMatrix().mul(getMatrix()).getForGL());		
	}



	@Override
	public void resetMatrix() {
		setMatrixView();
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
    protected void setLightPosition(float[] values) {
		glContext.uniform3fv(lightPositionLocation, values);	    
		glContext.uniform3fv(viewDirectionLocation, view3D.getViewDirection().get3ForGL());	    
    }
	
	private float[][] ambiantDiffuse;
	
	@Override
	protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0, float ambiant1, float diffuse1){
       
		ambiantDiffuse = new float[][] {
				{ambiant0, diffuse0},
				{ambiant1, diffuse1}
		};
        
	}



	@Override
	protected void setLight(int light){

		glContext.uniform2fv(ambiantDiffuseLocation, ambiantDiffuse[light]);
	}

	@Override
    public void setClearColor(float r, float g, float b, float a) {
		glContext.clearColor(r, g, b ,a);	    
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
	    // done by shader
	    
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

        glContext.uniformMatrix4fv(projectionLocation, false, projection);        

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
    public void removeTexture(int index) {
	    // TODO Auto-generated method stub
	    
    }
	


	@Override
    public void bindTexture(int index) {
	    // TODO Auto-generated method stub
		//glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);
	    
    }

    @Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf){
    	// TODO
    	//glContext.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0, WebGLRenderingContext.ALPHA, WebGLRenderingContext.ALPHA, WebGLRenderingContext.UNSIGNED_BYTE, getImage(Resources.INSTANCE.texture()).getElement());
        
    }
    
    @Override
	public void setTextureLinear(){
    	/*
    	glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR);
    	glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR);
    	glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_S, WebGLRenderingContext.CLAMP_TO_EDGE); //prevent repeating the texture
    	glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_WRAP_T, WebGLRenderingContext.CLAMP_TO_EDGE); //prevent repeating the texture
    	 */
	}
    
    
	@Override
	public void setTextureNearest(){
		/*
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.NEAREST);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.NEAREST);
		*/
	}









	@Override
    protected void setStencilFunc(int value) {
	    // TODO Auto-generated method stub
	    
    }






	@Override
    protected void exportImage() {
	    // TODO Auto-generated method stub
	    
    }


	
	
	
	
	
	
	
	
	
	





	
	public void loadColorBuffer(GLBuffer fbColors, int length) {


		if (fbColors == null){
			glContext.disableVertexAttribArray(colorAttribute);
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

	   	/////////////////////////////////////
        // VBO - colors
 
        // Select the VBO, GPU memory data
        glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboColors);
        
        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
        glBufferData(fbColors);
        
        // Associate attribute
        glContext.vertexAttribPointer(colorAttribute, 4, WebGLRenderingContext.FLOAT, false, 0, 0);
  
        // VBO
        glContext.enableVertexAttribArray(colorAttribute);	  
        
        
    }






	private boolean oneNormalForAllVertices;

	private void resetOneNormalForAllVertices(){
		oneNormalForAllVertices = false;
		glContext.uniform3f(normalLocation, 2,2,2);
	}

	public void loadNormalBuffer(GLBuffer fbNormals, int length) {

		if (fbNormals == null){ // no normals
			glContext.disableVertexAttribArray(normalAttribute);
			return;
		}

		if (fbNormals.capacity() == 3){ // one normal for all vertices
			glContext.uniform3fv(normalLocation, fbNormals.array());
			oneNormalForAllVertices = true;
			return;
		}

		/////////////////////////////////////
		// VBO - normals

		if(oneNormalForAllVertices){
			resetOneNormalForAllVertices();
		}

    	/////////////////////////////////////
        // VBO - normals
 
        // Select the VBO, GPU memory data
        glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboNormals);
        
        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
        glBufferData(fbNormals);
        
        // Associate attribute
        glContext.vertexAttribPointer(normalAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
  
        // VBO
        glContext.enableVertexAttribArray(normalAttribute);
        
    }






	public void loadTextureBuffer(GLBuffer fbTextures, int length) {
		

		if (fbTextures == null){		
			setCurrentGeometryHasNoTexture();
			glContext.disableVertexAttribArray(textureAttribute);
			return;
		}

		setCurrentGeometryHasTexture();

    	/////////////////////////////////////
        // VBO - texture
 
        // Select the VBO, GPU memory data
        glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboTextureCoords);
        
        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
        glBufferData(fbTextures);
        
        // Associate attribute
        glContext.vertexAttribPointer(textureAttribute, 2, WebGLRenderingContext.FLOAT, false, 0, 0);
  
        // VBO
        glContext.enableVertexAttribArray(textureAttribute);
        

	}




	private void glBufferData(GLBuffer fb){
        glContext.bufferData(WebGLRenderingContext.ARRAY_BUFFER, ((GLBufferW) fb).getBuffer(), WebGLRenderingContext.STATIC_DRAW);
	}



	public void loadVertexBuffer(GLBuffer fbVertices, int length) {
        

		
    	/////////////////////////////////////
        // VBO - vertices
 
        // Select the VBO, GPU memory data, to use for vertices
        glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboVertices);
        
        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
        glBufferData(fbVertices);
        
        // Associate Vertex attribute 0 with the last bound VBO
        glContext.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
  
        // VBO
        glContext.enableVertexAttribArray(vertexPositionAttribute);
        
	    
    }













	public void draw(Type type, int length) {
		glContext.drawArrays(getGLType(type), 0, length);
	    
    }


	/**
	 * 
	 * @param type Manager type
	 * @return GL type
	 */
	protected static int getGLType(Type type){
		switch(type){
		case TRIANGLE_STRIP : 
			return WebGLRenderingContext.TRIANGLE_STRIP;
		case TRIANGLE_FAN : 
			return WebGLRenderingContext.TRIANGLE_FAN;
		case TRIANGLES : 
			return WebGLRenderingContext.TRIANGLES;
		case LINE_LOOP : 
			return WebGLRenderingContext.LINE_LOOP;
		}
		
		return WebGLRenderingContext.TRIANGLES;
	}





	@Override
    protected void setDepthFunc() {
		glContext.depthFunc(WebGLRenderingContext.LEQUAL);
    }








	@Override
    protected void enablePolygonOffsetFill() {
	    // TODO Auto-generated method stub
	    
    }








	@Override
    protected void setBlendFunc() {
		glContext.blendFunc(WebGLRenderingContext.SRC_ALPHA, WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);    
    }








	@Override
    protected void enableNormalNormalized() {
	    // TODO Auto-generated method stub
	    
    }
	
	
	
	
	
	
	
	

	private boolean texturesEnabled;

	@Override
	final public void enableTextures(){  
		texturesEnabled = true;
		setCurrentGeometryHasNoTexture(); // let first geometry init textures
	}


	@Override
	final public void disableTextures(){
		texturesEnabled = false;
		setCurrentTextureType(TEXTURE_TYPE_NONE);
		glContext.disableVertexAttribArray(textureAttribute);
	}


	
	/**
	 * tells that current geometry has a texture
	 */
	final public void setCurrentGeometryHasTexture(){
		if (areTexturesEnabled() && currentTextureType == TEXTURE_TYPE_NONE){
			setCurrentTextureType(oldTextureType);
		}
	}

	/**
	 * tells that current geometry has no texture
	 */
	final public void setCurrentGeometryHasNoTexture(){
		if (areTexturesEnabled() && currentTextureType != TEXTURE_TYPE_NONE){
			oldTextureType = currentTextureType;
			setCurrentTextureType(TEXTURE_TYPE_NONE);
			
		}
	}


	@Override
	public void enableFading(){  
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_FADING);
	}
	

	@Override
	public void enableDash(){  
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_DASH);
	}

	
	/**
	 * enable text textures 
	 */
	final public void enableTexturesForText(){  
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_TEXT);
	}

	
	private int currentTextureType = TEXTURE_TYPE_NONE;
	private int oldTextureType = TEXTURE_TYPE_NONE;
	
	private void setCurrentTextureType(int type){
		currentTextureType = type;
		glContext.uniform1i(textureTypeLocation, type);
	}
	
	
	
	/**
	 * @return true if textures are enabled
	 */
	@Override
	public boolean areTexturesEnabled(){
		return texturesEnabled;
	}
	
	@Override
	protected float[] getLightPosition(){
		return LIGHT_POSITION_W;
	}

}
