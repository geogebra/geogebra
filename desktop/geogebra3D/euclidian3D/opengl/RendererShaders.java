

package geogebra3D.euclidian3D.opengl;

import geogebra.common.awt.GColor;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders;
import geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.main.App;
import geogebra3D.euclidian3D.opengl.RendererJogl.GL2ES2;
import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;


/**
 * Renderer using shaders
 * @author mathieu
 *
 */
public class RendererShaders extends RendererD implements RendererShadersInterface {
	
	final static private int GLSL_ATTRIB_POSITION = 0; 
	final static private int GLSL_ATTRIB_COLOR = 1; 
	final static private int GLSL_ATTRIB_NORMAL = 2; 
	final static private int GLSL_ATTRIB_TEXTURE = 3; 



	/**
	 * constructor
	 * @param view 3D view
	 * @param useCanvas says if we want a canvas
	 */
	public RendererShaders(EuclidianView3D view, boolean useCanvas){
		super(view,useCanvas);
	}

	
	@Override
	public void setGL(GLAutoDrawable gLDrawable){		
		jogl.setGL2ES2(gLDrawable);
	}
	
	
	
	
	
	
	@Override
	public GL getGL(){
		
		return jogl.getGL2ES2(); 
	}
	
	
	
	
	
	
	
	
	
	


/* Introducing the GL2ES2 demo
 *
 * How to render a triangle using ~500 lines of code using the RAW
 * OpenGL ES 2 API.
 * The Programmable pipeline in OpenGL ES 2 are both fast and flexible
 * yet it do take some extra lines of code to setup.
 *
 */
    private double t0 = System.currentTimeMillis();
    private double theta;
    private double s;
    

    private int shaderProgram;
    private int vertShader;
    private int fragShader;
    
    // location values for shader fields
    private int modelviewLocation, projectionLocation; // matrices
    private int lightPositionLocation, ambiantDiffuseLocation; // light
    private int textureTypeLocation; // textures
    private int colorLocation; // color
    private int normalLocation; // one normal for all vertices
    //private int normalMatrixLocation;
    
    final static private int TEXTURE_TYPE_NONE = 0;
    final static private int TEXTURE_TYPE_FADING = 1;
    final static private int TEXTURE_TYPE_TEXT = 2;
    final static private int TEXTURE_TYPE_DASH = 4;
       

    int[] vboHandles;
    private int vboVertices, vboColors, vboNormals, vboTextureCoords;
    
    static final private float[] PER_VERTEX_COLOR = {-1,0,1,1};
    
    private String readTxt(String file) throws IOException{
    	BufferedReader br = new BufferedReader(new FileReader("geogebra3D/euclidian3D/opengl/shaders/"+file+".txt"));
    	StringBuilder sb = new StringBuilder();     
        try {
            String line = br.readLine();                
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        
        return sb.toString();
    }

    
    @Override
	protected void initShaders(){

        /* The initialization below will use the OpenGL ES 2 API directly
         * to setup the two shader programs that will be run on the GPU.
         *
         * Its recommended to use the jogamp/opengl/util/glsl/ classes
         * import com.jogamp.opengl.util.glsl.ShaderCode;
         * import com.jogamp.opengl.util.glsl.ShaderProgram;
         * import com.jogamp.opengl.util.glsl.ShaderState; 
         * to simplify shader customization, compile and loading.
         *
         * You may also want to look at the JOGL RedSquareES2 demo
         * http://jogamp.org/git/?p=jogl.git;a=blob;f=src/test/com/jogamp/opengl/test/junit/jogl/demos/es2/RedSquareES2.java;hb=HEAD#l78
         * to see how the shader customization, compile and loading is done
         * using the recommended JogAmp GLSL utility classes.
         */ 
        
        String vertexShaderString, fragmentShaderString;
        
        try {
        	vertexShaderString = readTxt("vertexShader");
        	fragmentShaderString  = readTxt("fragmentShader");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			vertexShaderString = "";
        	fragmentShaderString  = "";
		}

        // Make the shader strings compatible with OpenGL 3 core if needed
        // GL2ES2 also includes the intersection of GL3 core 
        // The default implicit GLSL version 1.1 is now depricated in GL3 core
        // GLSL 1.3 is the minimum version that now has to be explicitly set.
        // This allows the shaders to compile using the latest
        // desktop OpenGL 3 and 4 drivers.
        if(jogl.getGL2ES2().isGL3core()){
            System.out.println("GL3 core detected: explicit add #version 130 to shaders");
            vertexShaderString = "#version 130\n"+vertexShaderString;
            fragmentShaderString = "#version 130\n"+fragmentShaderString;
	}

        // Create GPU shader handles
        // OpenGL ES retuns a index id to be stored for future reference.
        vertShader = jogl.getGL2ES2().glCreateShader(GL2ES2.GL_VERTEX_SHADER);
        fragShader = jogl.getGL2ES2().glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);

        //Compile the vertexShader String into a program.
        String[] vlines = new String[] { vertexShaderString };
        
        //for (int i = 0; i < vlines.length; i++)
        //	System.out.println(vlines[i]);
        
        
		
        
        int[] vlengths = new int[] { vlines[0].length() };
        jogl.getGL2ES2().glShaderSource(vertShader, vlines.length, vlines, vlengths, 0);
        jogl.getGL2ES2().glCompileShader(vertShader);

        //Check compile status.
        int[] compiled = new int[1];
        jogl.getGL2ES2().glGetShaderiv(vertShader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
        if(compiled[0]!=0){System.out.println("Horray! vertex shader compiled");}
        else {
            int[] logLength = new int[1];
            jogl.getGL2ES2().glGetShaderiv(vertShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            jogl.getGL2ES2().glGetShaderInfoLog(vertShader, logLength[0], (int[])null, 0, log, 0);

            System.err.println("Error compiling the vertex shader: " + new String(log));
            System.exit(1);
        }

        //Compile the fragmentShader String into a program.
        String[] flines = new String[] { fragmentShaderString };
        int[] flengths = new int[] { flines[0].length() };
        jogl.getGL2ES2().glShaderSource(fragShader, flines.length, flines, flengths, 0);
        jogl.getGL2ES2().glCompileShader(fragShader);

        //Check compile status.
        jogl.getGL2ES2().glGetShaderiv(fragShader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
        if(compiled[0]!=0){System.out.println("Horray! fragment shader compiled");}
        else {
            int[] logLength = new int[1];
            jogl.getGL2ES2().glGetShaderiv(fragShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            jogl.getGL2ES2().glGetShaderInfoLog(fragShader, logLength[0], (int[])null, 0, log, 0);

            System.err.println("Error compiling the fragment shader: " + new String(log));
            System.exit(1);
        }

        //Each shaderProgram must have
        //one vertex shader and one fragment shader.
        shaderProgram = jogl.getGL2ES2().glCreateProgram();
        jogl.getGL2ES2().glAttachShader(shaderProgram, vertShader);
        jogl.getGL2ES2().glAttachShader(shaderProgram, fragShader);

        //Associate attribute ids with the attribute names inside
        //the vertex shader.
        jogl.getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_POSITION, "attribute_Position");
        jogl.getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_COLOR, "attribute_Color");
        jogl.getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_NORMAL, "attribute_Normal");
        jogl.getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_TEXTURE, "attribute_Texture");

        jogl.getGL2ES2().glLinkProgram(shaderProgram);

        //Get a id number to the uniform_Projection matrix
        //so that we can update it.
        modelviewLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "modelview");
        projectionLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "projection");
        
        //normalMatrixLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "normalMatrix");        
        lightPositionLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "lightPosition");
        ambiantDiffuseLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "ambiantDiffuse");
        
        //texture
        textureTypeLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "textureType");
               
        //color
        colorLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "color");

        //color
        normalLocation = jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "normal");

        /* GL2ES2 also includes the intersection of GL3 core
         * GL3 core and later mandates that a "Vector Buffer Object" must
         * be created and bound before calls such as gl.glDrawArrays is used.
         * The VBO lines in this demo makes the code forward compatible with
         * OpenGL 3 and ES 3 core and later where a default
         * vector buffer object is deprecated.
         *
         * Generate two VBO pointers / handles
         * VBO is data buffers stored inside the graphics card memory.
         */
        vboHandles = new int[4];
        jogl.getGL2ES2().glGenBuffers(4, vboHandles, 0);
        vboColors = vboHandles[GLSL_ATTRIB_COLOR];
        vboVertices = vboHandles[GLSL_ATTRIB_POSITION];
        vboNormals = vboHandles[GLSL_ATTRIB_NORMAL];
        vboTextureCoords = vboHandles[GLSL_ATTRIB_TEXTURE];
        //super.init(drawable);
        
        
 
    }

    /*
    private void drawTriangle(float[] vertices, float[] normals, float[] colors, float[] textureCoords){
    	
    	//jogl.getGL2ES2().glUniform1i(jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "Texture0"), 0);
    	//getGL().glActiveTexture(GLlocal.GL_TEXTURE0);


       	
       	byte[] bytes = new byte[]{
       			(byte) 255, (byte) 255, (byte) 255, 
       			(byte) 0, (byte) 0, (byte) 0
       	};
           	
    	int texture = getTextures().createAlphaTexture(2, 2, bytes);
    	

    	getGL().glEnable(GLlocal.GL_TEXTURE_2D);  
    	getTextures().setTextureLinear(texture);
    	//getGL().glActiveTexture(GLlocal.GL_TEXTURE0);
    	//getGL().glBindTexture(GLlocal.GL_TEXTURE_2D, texture);
    	
    	
    	//enableTextures();
    	//enableFading();
    	
    	ArrayList<Float> array = new ArrayList<Float>();
    	
    	for (int i = 0; i < 3 * 3; i++){ array.add(vertices[i]); }
    	loadVertexBuffer(ManagerShaders.floatBuffer(array), 3);
    	
    	array.clear(); for (int i = 0; i < 3 * 3; i++){ array.add(normals[i]); }
		loadNormalBuffer(ManagerShaders.floatBuffer(array), 3);
		
		array.clear(); for (int i = 0; i < 3 * 2; i++){ array.add(textureCoords[i]); }
		loadTextureBuffer(ManagerShaders.floatBuffer(array), 3);	
		
		draw(Manager.Type.TRIANGLES, 3);
		
		getGL().glBindTexture(GLlocal.GL_TEXTURE_2D,  0);
		getTextures().removeTexture(texture);
        
    }
    */
    
    
    
   public void loadVertexBuffer(GLBuffer fbVertices, int length){
     	
    	
    	/////////////////////////////////////
        // VBO - vertices
 
        // Select the VBO, GPU memory data, to use for vertices
        jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboVertices);

        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
        int numBytes = length * 12; // 4 bytes per float * 3 coords per vertex
        glBufferData(numBytes, fbVertices);

        // Associate Vertex attribute 0 with the last bound VBO
        jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_POSITION /* the vertex attribute */, 3,
                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
                                 0 /* The bound VBO data offset */);

        // VBO
        jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
   }
   

   
   private boolean oneNormalForAllVertices;
   
   private void resetOneNormalForAllVertices(){
	   oneNormalForAllVertices = false;
	   jogl.getGL2ES2().glUniform3f(normalLocation, 2,2,2);
   }
   
   /**
    * push buffer data
    * @param numBytes data size
    * @param array buffer array
    */
   protected void glBufferData(int numBytes, GLBuffer fb){
	   jogl.getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, ((GLBufferD) fb).getBuffer(), GL.GL_STATIC_DRAW);

   }


   public void loadNormalBuffer(GLBuffer fbNormals, int length){

	   if (fbNormals == null){ // no normals
		   return;
	   }
	   
	   if (fbNormals.capacity() == 3){ // one normal for all vertices
		   jogl.getGL2ES2().glUniform3fv(normalLocation, 1, fbNormals.array(), 0);
		   oneNormalForAllVertices = true;
		   return;
	   }

	   /////////////////////////////////////
	   // VBO - normals
	   
	   if(oneNormalForAllVertices){
		   resetOneNormalForAllVertices();
	   }

	   // Select the VBO, GPU memory data, to use for normals 
	   jogl.getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboNormals);
	   int numBytes = length * 12; // 4 bytes per float * * 3 coords per normal
	   glBufferData(numBytes, fbNormals);

	   // Associate Vertex attribute 1 with the last bound VBO
	   jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_NORMAL /* the vertex attribute */, 3 /* 3 normal values used for each vertex */,
			   GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
			   0 /* The bound VBO data offset */);

	   jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
   }
   
   
   public void loadTextureBuffer(GLBuffer fbTextures, int length){

	   if (fbTextures == null){		
		   setCurrentGeometryHasNoTexture();
		   return;
	   }
	   
	   setCurrentGeometryHasTexture();
	   
       // Select the VBO, GPU memory data, to use for normals 
       jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboTextureCoords);
       int numBytes = length * 8; // 4 bytes per float * 2 coords per texture
       glBufferData(numBytes, fbTextures);

       // Associate Vertex attribute 1 with the last bound VBO
       jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_TEXTURE /* the texture attribute */, 2 /* 2 texture values used for each vertex */,
                                GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
                                0 /* The bound VBO data offset */);

       jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
   }
   
   public void loadColorBuffer(GLBuffer fbColors, int length){

	   if (fbColors == null){
		   return;
	   }
	   
	   setColor(-1, -1, -1, -1);
	   
       // Select the VBO, GPU memory data, to use for normals 
       jogl.getGL2ES2().glBindBuffer(GL.GL_ARRAY_BUFFER, vboColors);
       int numBytes = length * 16; // 4 bytes per float * 4 color values (rgba)
       glBufferData(numBytes, fbColors);

       // Associate Vertex attribute 1 with the last bound VBO
       jogl.getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_COLOR/* the color attribute */, 4 /* 4 color values used for each vertex */,
                                GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
                                0 /* The bound VBO data offset */);

       jogl.getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
   }
   
 
   

  
   
   public void draw(Manager.Type type, int length){  

	   /////////////////////////
	   // draw

	   jogl.getGL2ES2().glDrawArrays(ManagerD.getGLType(type), 0, length); 
   }

    @Override
	protected void draw(){
    	
    	// NOT NEEDED (default value)
    	//jogl.getGL2ES2().glUniform1i(jogl.getGL2ES2().glGetUniformLocation(shaderProgram, "Texture0"), 0);
    	
    	resetOneNormalForAllVertices();
     	disableTextures();
    	
        //labels
     	float[] m = new float[]{
     			1,0,0,0,
     			0,1,0,0,
     			0,0,1,0,
     			0,0,0,1
     	};
     	jogl.getGL2ES2().glUniformMatrix4fv(modelviewLocation, 1, false, m, 0);
     	enableTexturesForText();
        drawFaceToScreen();
        
        //init drawing matrix to view3D toScreen matrix
        setMatrixView(); 
 
        setLightPosition();     
        setLight(0);

        //drawing the cursor
        getGL().glEnable(GLlocal.GL_LIGHTING);
        getGL().glDisable(GLlocal.GL_ALPHA_TEST);       
        getGL().glEnable(GLlocal.GL_CULL_FACE);
        
        
        
        disableTextures();
        view3D.drawCursor(this);
                 
        
        
        //drawing hidden part     
        getGL().glEnable(GLlocal.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts 
        drawable3DLists.drawHiddenNotTextured(this);
        enableDash();
        drawable3DLists.drawHiddenTextured(this);
        
        enableFading();
        drawNotTransp();
        //disableTextures();
        getGL().glDisable(GLlocal.GL_ALPHA_TEST);       
        
                
        //drawing transparents parts
        getGL().glDepthMask(false);
        enableFading();
        drawTransp();      
        getGL().glDepthMask(true);
       
        
        getGL().glEnable(GLlocal.GL_CULL_FACE);
        getGL().glDisable(GLlocal.GL_BLEND);        
        
        //drawing hiding parts
        getGL().glColorMask(false,false,false,false); //no writing in color buffer		
        getGL().glCullFace(GLlocal.GL_FRONT); //draws inside parts    
        disableTextures();
        drawable3DLists.drawClosedSurfacesForHiding(this); //closed surfaces back-faces
        if (drawable3DLists.containsClippedSurfaces()){
        	enableClipPlanesIfNeeded();
        	drawable3DLists.drawClippedSurfacesForHiding(this); //clipped surfaces back-faces
        	disableClipPlanesIfNeeded();
        }
        
        getGL().glDisable(GLlocal.GL_CULL_FACE);      
        drawable3DLists.drawSurfacesForHiding(this); //non closed surfaces
        setColorMask();

        //re-drawing transparents parts for better transparent effect
        //TODO improve it !
        getGL().glDepthMask(false);
        getGL().glEnable(GLlocal.GL_BLEND);
        enableFading();
        drawTransp();
        getGL().glDepthMask(true);
        disableTextures();
        
        //drawing hiding parts
        getGL().glColorMask(false,false,false,false); //no writing in color buffer		
        getGL().glDisable(GLlocal.GL_BLEND);
        getGL().glEnable(GLlocal.GL_CULL_FACE);
        getGL().glCullFace(GLlocal.GL_BACK); //draws inside parts
        drawable3DLists.drawClosedSurfacesForHiding(this); //closed surfaces front-faces
        if (drawable3DLists.containsClippedSurfaces()){
        	enableClipPlanesIfNeeded();
        	drawable3DLists.drawClippedSurfacesForHiding(this); //clipped surfaces back-faces
        	disableClipPlanesIfNeeded();
        }
        setColorMask();        
        
        //re-drawing transparents parts for better transparent effect
        //TODO improve it !
        getGL().glDepthMask(false);
        getGL().glEnable(GLlocal.GL_BLEND);
        enableFading();
        drawTransp();
        getGL().glDepthMask(true);
        
        //drawing not hidden parts
        disableTextures();
        getGL().glEnable(GLlocal.GL_CULL_FACE);
        drawable3DLists.draw(this);        
        
            
        //FPS
        getGL().glDisable(GLlocal.GL_LIGHTING);
        getGL().glDisable(GLlocal.GL_DEPTH_TEST);
 
        
        unsetMatrixView();  
   	
        
    	getGL().glEnable(GLlocal.GL_DEPTH_TEST);
    	getGL().glEnable(GLlocal.GL_LIGHTING);    
    	
        
    	//if (!objDone){ doObj(); objDone = true;}
    	
    }    
    
    
    private boolean objDone = false;

    private void doObj(){
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("geogebra3D/test.obj"));
			writer.write("######## CREATED WITH GEOGEBRA ########");
			
			((ManagerShadersObj) getGeometryManager()).startObjFile(writer);
			
	    	App.debug("=== Creating .OBJ === ");
	    	drawable3DLists.drawInObjFormat(this);
	    	
	    	writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /*
    @Override
    public void display(GLAutoDrawable drawable) {
    //protected void draw(){
    	
        //update 3D controller
        view3D.getEuclidianController().update();
        
        view3D.updateAnimation();

    	// say that 3D view changed has been performed
        view3D.resetViewChanged();

    	
        // Update variables used in animation
        double t1 = System.currentTimeMillis();
        theta += (t1-t0)*0.005f;
        t0 = t1;
        s = Math.sin(theta);

        // Get gl
        setGL(drawable);
        
        

        // Clear screen
        jogl.getGL2ES2().glClearColor(0, 0, 0, 1f);  // Purple
        jogl.getGL2ES2().glClear(GL2ES2.GL_STENCIL_BUFFER_BIT |
                   GL2ES2.GL_COLOR_BUFFER_BIT   |
                   GL2ES2.GL_DEPTH_BUFFER_BIT   );
                   

       useShaderProgram();
        



        
        
        setView();
        setMatrixView();
        
       
        
        
        
      

        
        //float[] normalMatrix = view3D.getRotationMatrix().get3x3ForGL();
        //jogl.getGL2ES2().glUniformMatrix3fv(normalMatrixLocation, 1, false, normalMatrix, 0);

        
        
        // light
        
        setLightPosition();
        setLight(GLlocal.GL_LIGHT0);
        
        
        

        drawSample();
       
       
       releaseVBOs();
       
        
    }
    
    */
    	
    @Override
	protected void useShaderProgram(){
        jogl.getGL2ES2().glUseProgram(shaderProgram);
    }
    
    /*
    private void drawSample(){

        // texture
        
        //jogl.getGL2ES2().glUniform1i(fadingLocation, 0);
        
        
        float[] textureCoords = { 
        		0, 0,
                0, 1f,
                1f, 1f
        };
        
        
        // draw
        
        
        float l = 1f;
        
        float[] vertices = {  0.0f,  0f, 0.0f,
        		0, l, 0, 
        		0, 0, l  
        };
        
        float[] normals = {  
        		1, 0, 0,
                1, 0, 0, 
                1, 0, 0  
        };

        float alpha = 1f;
        

        float[] color = {1,0,1,1};
        jogl.getGL2ES2().glUniform4fv(colorLocation, 1, color, 0);
        //loadVertexBuffer(vertices);//, normals, textureCoords);
        //draw(Manager.TRIANGLES, 3);
        
        jogl.getGL2ES2().glUniform4fv(colorLocation, 1, PER_VERTEX_COLOR, 0);
        
        float[] vertices2 = {  0.0f,  0f, 0f,
                l, 0, 0f,
                0, 0, l  
        };
        
        float[] normals2 = {  
        		0, 1, 0,
                0, 1, 0, 
                0, 1, 0  
        };
        
       float[] colors2 = {    0.0f, 1.0f, 0.0f, alpha, //Top color (red)
               0.0f, 1.0f, 0.0f, alpha, //Bottom Left color (black)
               0.0f, 1.0f, 0.0f, alpha  //Bottom Right color (yellow) with 10% transparence
                                    };
       
       
       drawTriangle(vertices2, normals2, colors2, textureCoords);
       
       

       float[] vertices4 ={  0,  -l, l,
               l, 0, 0f,
               0, 0, l  
       };

       float[] normals4 = {  
    		   0, 0, 1,
    		   0, 0, 1, 
    		   0, 0, 1  
       };

       drawTriangle(vertices4, normals4, colors2, textureCoords);

  
       float z = 0f;

       float[] vertices3 = {  0.0f,  0f, z,
    		   0, l, z,
    		   l, 0, z  
       };
       float[] colors3 = {    0.0f, 0.0f, 1.0f, alpha, //Top color (red)
       0.0f, 0.0f, 1.0f, alpha, //Bottom Left color (black)
       0.0f, 0.0f, 1.0f, alpha  //Bottom Right color (yellow) with 10% transparence
       };

       drawTriangle(vertices3, normals4, colors3, textureCoords);

    }
*/
    
    private void releaseVBOs(){
    	jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_POSITION); // Allow release of vertex position memory
        jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_COLOR); // Allow release of vertex color memory		
        jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL); // Allow release of vertex normal memory		
        jogl.getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE); // Allow release of vertex texture memory		

        jogl.getGL2ES2().glDeleteBuffers(4, vboHandles, 0); // Release VBO, color and vertices, buffer GPU memory.
    }
    
	public void dispose(GLAutoDrawable drawable){
        System.out.println("cleanup, remember to release shaders");

        setGL(drawable);
        
        jogl.getGL2ES2().glUseProgram(0);
        jogl.getGL2ES2().glDetachShader(shaderProgram, vertShader);
        jogl.getGL2ES2().glDeleteShader(vertShader);
        jogl.getGL2ES2().glDetachShader(shaderProgram, fragShader);
        jogl.getGL2ES2().glDeleteShader(fragShader);
        jogl.getGL2ES2().glDeleteProgram(shaderProgram);
        //System.exit(0);
    }




















	@Override
	public void setClipPlane(int n, double[] equation) {
		// TODO Auto-generated method stub
		
	}



















	@Override
	protected void setMatrixView() {
        jogl.getGL2ES2().glUniformMatrix4fv(modelviewLocation, 1, false, view3D.getToScreenMatrix().getForGL(), 0);
	}



















	@Override
	protected void unsetMatrixView() {
		// TODO Auto-generated method stub
		
	}



















	@Override
	protected void setExportImage() {
		// TODO Auto-generated method stub
		
	}



















	@Override
	public void setColor(Coords color) {
		setColor(
				(float) color.getX(),
				(float) color.getY(),
				(float) color.getZ(),
				(float) color.getW()
		);
		
	}
	
	private void setColor(float r, float g, float b, float a){
		jogl.getGL2ES2().glUniform4f(colorLocation, r,g,b,a);
	}


	@Override
	public void setColor(GColor color) {
		setColor(
				color.getRed() / 255f,
				color.getGreen() / 255f,
				color.getBlue() / 255f,
				color.getAlpha() / 255f
		);
	}



















	@Override
	public void initMatrix() {
		 jogl.getGL2ES2().glUniformMatrix4fv(modelviewLocation, 1, false, view3D.getToScreenMatrix().mul(getMatrix()).getForGL(), 0);		
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
	protected void setLightPosition(int light, float[] values){
		jogl.getGL2ES2().glUniform4fv(lightPositionLocation, 1, values, 0);
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

		jogl.getGL2ES2().glUniform2fv(ambiantDiffuseLocation, 1, ambiantDiffuse[light], 0);
	}













	@Override
	protected void setColorMaterial() {
		getGL().glEnable(GLlocal.GL_COLOR_MATERIAL);
		
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
	protected void setView() {
		
		float[] projection = {
                2.0f/getWidth(), 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f/getHeight(), 0.0f, 0.0f,
                0.0f, 0.0f, -2.0f/getVisibleDepth(), 0f,
                0.0f, 0.0f, -1f/getVisibleDepth(), 1.0f,
        };

        jogl.getGL2ES2().glUniformMatrix4fv(projectionLocation, 1, false, projection, 0);
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
	protected Manager createManager(){
    	return new ManagerShaders(this, view3D);
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

	/**
	 * enable fading (e.g. for planes)
	 */
	final public void enableFading(){  
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_FADING);
	}
	
	/**
	 * enable fading (e.g. for planes)
	 */
	final public void enableDash(){  
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
		jogl.getGL2ES2().glUniform1i(textureTypeLocation, type);
	}
	
	
	
	/**
	 * @return true if textures are enabled
	 */
	public boolean areTexturesEnabled(){
		return texturesEnabled;
	}

    public void setLineWidth(int width){
    	getGL().glLineWidth(width);
    }   


}
