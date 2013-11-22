//
//
//package geogebra3D.euclidian3D.opengl;
//
//import geogebra.common.awt.GColor;
//import geogebra.common.kernel.Matrix.Coords;
//import geogebra3D.euclidian3D.EuclidianView3D;
//import geogebra3D.euclidian3D.Hits3D;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.FloatBuffer;
//import java.nio.IntBuffer;
//
//import javax.media.opengl.GL;
//import javax.media.opengl.GL2ES2;
//import javax.media.opengl.GLAutoDrawable;
//
//import com.jogamp.common.nio.Buffers;
//
///**
// * Renderer using shaders
// * @author mathieu
// *
// */
//public class RendererShaders extends Renderer {
//	
//	final static private int GLSL_ATTRIB_POSITION = 0; 
//	final static private int GLSL_ATTRIB_COLOR = 1; 
//	final static private int GLSL_ATTRIB_NORMAL = 2; 
//	final static private int GLSL_ATTRIB_TEXTURE = 3; 
//
//
//
//	/**
//	 * constructor
//	 * @param view 3D view
//	 */
//	public RendererShaders(EuclidianView3D view){
//		super(view,false);
//	}
//
//	
//	
//	
//	
//	
//	
//	
//	private GL2ES2 gl2es2; 
//	
//	@Override
//	public GL getGL(){
//		
//		return gl2es2; 
//	}
//	
//	/**
//	 * 
//	 * @return current GL (as GL2ES2)
//	 */
//	public GL2ES2 getGL2ES2(){
//		
//		return gl2es2; 
//	}
//	
//	
//	
//	@Override
//	public void setGL(GLAutoDrawable gLDrawable){		
//		gl2es2 = gLDrawable.getGL().getGL2ES2();
//	}
//	
//	
//	
//	
//	
//	
//	
//
//
///* Introducing the GL2ES2 demo
// *
// * How to render a triangle using ~500 lines of code using the RAW
// * OpenGL ES 2 API.
// * The Programmable pipeline in OpenGL ES 2 are both fast and flexible
// * yet it do take some extra lines of code to setup.
// *
// */
//    private double t0 = System.currentTimeMillis();
//    private double theta;
//    private double s;
//    
//
//    private int shaderProgram;
//    private int vertShader;
//    private int fragShader;
//    
//    // location values for shader fields
//    private int modelviewLocation, projectionLocation; // matrices
//    private int lightPositionLocation, ambiantDiffuseLocation; // light
//    private int fadingLocation; // textures
//    private int colorLocation; // color
//    //private int normalMatrixLocation;
//
//    int[] vboHandles;
//    private int vboVertices, vboColors, vboNormals, vboTextureCoords;
//    
//    static final private float[] PER_VERTEX_COLOR = {-1,0,1,1};
//    
//    private String readTxt(String file) throws IOException{
//    	BufferedReader br = new BufferedReader(new FileReader("geogebra3D/euclidian3D/opengl/shaders/"+file+".txt"));
//    	StringBuilder sb = new StringBuilder();     
//        try {
//            String line = br.readLine();                
//            while (line != null) {
//                sb.append(line);
//                sb.append("\n");
//                line = br.readLine();
//            }
//        } finally {
//            br.close();
//        }
//        
//        return sb.toString();
//    }
//
//    
//    protected void initShaders(){
//
//        /* The initialization below will use the OpenGL ES 2 API directly
//         * to setup the two shader programs that will be run on the GPU.
//         *
//         * Its recommended to use the jogamp/opengl/util/glsl/ classes
//         * import com.jogamp.opengl.util.glsl.ShaderCode;
//         * import com.jogamp.opengl.util.glsl.ShaderProgram;
//         * import com.jogamp.opengl.util.glsl.ShaderState; 
//         * to simplify shader customization, compile and loading.
//         *
//         * You may also want to look at the JOGL RedSquareES2 demo
//         * http://jogamp.org/git/?p=jogl.git;a=blob;f=src/test/com/jogamp/opengl/test/junit/jogl/demos/es2/RedSquareES2.java;hb=HEAD#l78
//         * to see how the shader customization, compile and loading is done
//         * using the recommended JogAmp GLSL utility classes.
//         */ 
//        
//        String vertexShaderString, fragmentShaderString;
//        
//        try {
//        	vertexShaderString = readTxt("vertexShader");
//        	fragmentShaderString  = readTxt("fragmentShader");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			vertexShaderString = "";
//        	fragmentShaderString  = "";
//		}
//
//        // Make the shader strings compatible with OpenGL 3 core if needed
//        // GL2ES2 also includes the intersection of GL3 core 
//        // The default implicit GLSL version 1.1 is now depricated in GL3 core
//        // GLSL 1.3 is the minimum version that now has to be explicitly set.
//        // This allows the shaders to compile using the latest
//        // desktop OpenGL 3 and 4 drivers.
//        if(getGL2ES2().isGL3core()){
//            System.out.println("GL3 core detected: explicit add #version 130 to shaders");
//            vertexShaderString = "#version 130\n"+vertexShaderString;
//            fragmentShaderString = "#version 130\n"+fragmentShaderString;
//	}
//
//        // Create GPU shader handles
//        // OpenGL ES retuns a index id to be stored for future reference.
//        vertShader = getGL2ES2().glCreateShader(GL2ES2.GL_VERTEX_SHADER);
//        fragShader = getGL2ES2().glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
//
//        //Compile the vertexShader String into a program.
//        String[] vlines = new String[] { vertexShaderString };
//        
//        //for (int i = 0; i < vlines.length; i++)
//        //	System.out.println(vlines[i]);
//        
//        
//		
//        
//        int[] vlengths = new int[] { vlines[0].length() };
//        getGL2ES2().glShaderSource(vertShader, vlines.length, vlines, vlengths, 0);
//        getGL2ES2().glCompileShader(vertShader);
//
//        //Check compile status.
//        int[] compiled = new int[1];
//        getGL2ES2().glGetShaderiv(vertShader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
//        if(compiled[0]!=0){System.out.println("Horray! vertex shader compiled");}
//        else {
//            int[] logLength = new int[1];
//            getGL2ES2().glGetShaderiv(vertShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);
//
//            byte[] log = new byte[logLength[0]];
//            getGL2ES2().glGetShaderInfoLog(vertShader, logLength[0], (int[])null, 0, log, 0);
//
//            System.err.println("Error compiling the vertex shader: " + new String(log));
//            System.exit(1);
//        }
//
//        //Compile the fragmentShader String into a program.
//        String[] flines = new String[] { fragmentShaderString };
//        int[] flengths = new int[] { flines[0].length() };
//        getGL2ES2().glShaderSource(fragShader, flines.length, flines, flengths, 0);
//        getGL2ES2().glCompileShader(fragShader);
//
//        //Check compile status.
//        getGL2ES2().glGetShaderiv(fragShader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
//        if(compiled[0]!=0){System.out.println("Horray! fragment shader compiled");}
//        else {
//            int[] logLength = new int[1];
//            getGL2ES2().glGetShaderiv(fragShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);
//
//            byte[] log = new byte[logLength[0]];
//            getGL2ES2().glGetShaderInfoLog(fragShader, logLength[0], (int[])null, 0, log, 0);
//
//            System.err.println("Error compiling the fragment shader: " + new String(log));
//            System.exit(1);
//        }
//
//        //Each shaderProgram must have
//        //one vertex shader and one fragment shader.
//        shaderProgram = getGL2ES2().glCreateProgram();
//        getGL2ES2().glAttachShader(shaderProgram, vertShader);
//        getGL2ES2().glAttachShader(shaderProgram, fragShader);
//
//        //Associate attribute ids with the attribute names inside
//        //the vertex shader.
//        getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_POSITION, "attribute_Position");
//        getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_COLOR, "attribute_Color");
//        getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_NORMAL, "attribute_Normal");
//        getGL2ES2().glBindAttribLocation(shaderProgram, GLSL_ATTRIB_TEXTURE, "attribute_Texture");
//
//        getGL2ES2().glLinkProgram(shaderProgram);
//
//        //Get a id number to the uniform_Projection matrix
//        //so that we can update it.
//        modelviewLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "modelview");
//        projectionLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "projection");
//        
//        //normalMatrixLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "normalMatrix");        
//        lightPositionLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "lightPosition");
//        ambiantDiffuseLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "ambiantDiffuse");
//        
//        //texture
//        fadingLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "fading");
//               
//        //color
//        colorLocation = getGL2ES2().glGetUniformLocation(shaderProgram, "color");
//
//        /* GL2ES2 also includes the intersection of GL3 core
//         * GL3 core and later mandates that a "Vector Buffer Object" must
//         * be created and bound before calls such as gl.glDrawArrays is used.
//         * The VBO lines in this demo makes the code forward compatible with
//         * OpenGL 3 and ES 3 core and later where a default
//         * vector buffer object is deprecated.
//         *
//         * Generate two VBO pointers / handles
//         * VBO is data buffers stored inside the graphics card memory.
//         */
//        vboHandles = new int[4];
//        getGL2ES2().glGenBuffers(4, vboHandles, 0);
//        vboColors = vboHandles[GLSL_ATTRIB_COLOR];
//        vboVertices = vboHandles[GLSL_ATTRIB_POSITION];
//        vboNormals = vboHandles[GLSL_ATTRIB_NORMAL];
//        vboTextureCoords = vboHandles[GLSL_ATTRIB_TEXTURE];
//        //super.init(drawable);
//        
//        
// 
//    }
//
//    
//    private void drawTriangle(float[] vertices, float[] normals, float[] colors, float[] textureCoords){
//    	
//    	
//    	getGL().glDisable(GLlocal.GL_CULL_FACE);
//    	
//    	
//    	 /////////////////////////////////////
//        // VBO - vertices
//        
//  
//
//        // Observe that the vertex data passed to glVertexAttribPointer must stay valid
//        // through the OpenGL rendering lifecycle.
//        // Therefore it is mandatory to allocate a NIO Direct buffer that stays pinned in memory
//        // and thus can not get moved by the java garbage collector.
//        // Also we need to keep a reference to the NIO Direct buffer around up untill
//        // we call glDisableVertexAttribArray first then will it be safe to garbage collect the memory. 
//        // I will here use the com.jogamp.common.nio.Buffers to quicly wrap the array in a Direct NIO buffer.
//        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
//
//        // Select the VBO, GPU memory data, to use for vertices
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboVertices);
//
//        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
//        int numBytes = vertices.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbVertices, GL.GL_STATIC_DRAW);
//        fbVertices = null; // It is OK to release CPU vertices memory after transfer to GPU
//
//        // Associate Vertex attribute 0 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_POSITION /* the vertex attribute */, 3,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        // VBO
//        // getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, 0); // You can unbind the VBO after it have been associated using glVertexAttribPointer
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
//
//        
//        
//        /////////////////////////////////////
//        // VBO - colors
//
//
//                                             
//        FloatBuffer fbColors = Buffers.newDirectFloatBuffer(colors);
//
//        // Select the VBO, GPU memory data, to use for colors 
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboColors);
//        numBytes = colors.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbColors, GL.GL_STATIC_DRAW);
//        fbColors = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_COLOR /* the vertex attribute */, 4 /* four positions used for each vertex */,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_COLOR);
//        
//        
//        
//        
//        /////////////////////////////////////
//        // VBO - normals
//
//
//                                             
//        FloatBuffer fbNormals = Buffers.newDirectFloatBuffer(normals);
//
//        // Select the VBO, GPU memory data, to use for normals 
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboNormals);
//        numBytes = normals.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbNormals, GL.GL_STATIC_DRAW);
//        fbNormals = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_NORMAL /* the vertex attribute */, 3 /* 3 normal values used for each vertex */,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
//
//        
//        /////////////////////////////////////
//        // VBO - texture
//
//
//                                             
//        FloatBuffer fbTextures = Buffers.newDirectFloatBuffer(textureCoords);
//
//        // Select the VBO, GPU memory data, to use for normals 
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboTextureCoords);
//        numBytes = textureCoords.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbTextures, GL.GL_STATIC_DRAW);
//        fbTextures = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_TEXTURE /* the vertex attribute */, 2 /* 2 texture values used for each vertex */,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
//
//        
//        
//        
//        
//        
//        
//        /////////////////////////
//        // draw
//
//        getGL2ES2().glDrawArrays(GL2ES2.GL_TRIANGLES, 0, 3); //Draw the vertices as triangle // 3 <=> 1 triangle
//    }
//    
//    
//    
//    private void drawTriangle(float[] vertices, float[] normals, float[] textureCoords){
//    	
//    	
//    	getGL().glDisable(GLlocal.GL_CULL_FACE);
//    	
//    	
//    	 /////////////////////////////////////
//        // VBO - vertices
//        
//  
//
//        // Observe that the vertex data passed to glVertexAttribPointer must stay valid
//        // through the OpenGL rendering lifecycle.
//        // Therefore it is mandatory to allocate a NIO Direct buffer that stays pinned in memory
//        // and thus can not get moved by the java garbage collector.
//        // Also we need to keep a reference to the NIO Direct buffer around up untill
//        // we call glDisableVertexAttribArray first then will it be safe to garbage collect the memory. 
//        // I will here use the com.jogamp.common.nio.Buffers to quicly wrap the array in a Direct NIO buffer.
//        FloatBuffer fbVertices = Buffers.newDirectFloatBuffer(vertices);
//
//        // Select the VBO, GPU memory data, to use for vertices
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboVertices);
//
//        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
//        int numBytes = vertices.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbVertices, GL.GL_STATIC_DRAW);
//        fbVertices = null; // It is OK to release CPU vertices memory after transfer to GPU
//
//        // Associate Vertex attribute 0 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_POSITION /* the vertex attribute */, 3,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        // VBO
//        // getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, 0); // You can unbind the VBO after it have been associated using glVertexAttribPointer
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_POSITION);
//
//        
//        
//        
//        
//        
//        /////////////////////////////////////
//        // VBO - normals
//
//
//                                             
//        FloatBuffer fbNormals = Buffers.newDirectFloatBuffer(normals);
//
//        // Select the VBO, GPU memory data, to use for normals 
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboNormals);
//        numBytes = normals.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbNormals, GL.GL_STATIC_DRAW);
//        fbNormals = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_NORMAL /* the vertex attribute */, 3 /* 3 normal values used for each vertex */,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_NORMAL);
//
//        
//        /////////////////////////////////////
//        // VBO - texture
//
//
//                                             
//        FloatBuffer fbTextures = Buffers.newDirectFloatBuffer(textureCoords);
//
//        // Select the VBO, GPU memory data, to use for normals 
//        getGL2ES2().glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboTextureCoords);
//        numBytes = textureCoords.length * 4; // 4 bytes per float
//        getGL2ES2().glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbTextures, GL.GL_STATIC_DRAW);
//        fbTextures = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        getGL2ES2().glVertexAttribPointer(GLSL_ATTRIB_TEXTURE /* the vertex attribute */, 2 /* 2 texture values used for each vertex */,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        getGL2ES2().glEnableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
//
//        
//        
//        
//        
//        
//        
//        /////////////////////////
//        // draw
//
//        getGL2ES2().glDrawArrays(GL2ES2.GL_TRIANGLES, 0, 3); //Draw the vertices as triangle // 3 <=> 1 triangle
//    }
//
//
//    @Override
//    public void display(GLAutoDrawable drawable) {
//    //protected void draw(){
//    	
//        //update 3D controller
//        view3D.getEuclidianController().update();
//        
//        view3D.updateAnimation();
//
//    	// say that 3D view changed has been performed
//        view3D.resetViewChanged();
//
//    	
//        // Update variables used in animation
//        double t1 = System.currentTimeMillis();
//        theta += (t1-t0)*0.005f;
//        t0 = t1;
//        s = Math.sin(theta);
//
//        // Get gl
//        setGL(drawable);
//        
//        
//
//        // Clear screen
//        getGL2ES2().glClearColor(0, 0, 0, 1f);  // Purple
//        getGL2ES2().glClear(GL2ES2.GL_STENCIL_BUFFER_BIT |
//                   GL2ES2.GL_COLOR_BUFFER_BIT   |
//                   GL2ES2.GL_DEPTH_BUFFER_BIT   );
//                   
//
//        // Use the shaderProgram that got linked during the init part.
//        getGL2ES2().glUseProgram(shaderProgram);
//        
//
//        /* Change a projection matrix
//         * The matrix multiplications and OpenGL ES2 code below
//         * basically match this OpenGL ES1 code.
//         * note that the model_view_projection matrix gets sent to the vertexShader.
//         *
//         * getGL2ES2().glLoadIdentity();
//         * getGL2ES2().glTranslatef(0.0f,0.0f,-0.1f);
//         * getGL2ES2().glRotatef((float)30f*(float)s,1.0f,0.0f,1.0f);
//         *
//         */
//
//        /*
//        float[] model_view_projection;
//        float[] identity_matrix = {
//            1.0f, 0.0f, 0.0f, 0.0f,
//            0.0f, 1.0f, 0.0f, 0.0f,
//            0.0f, 0.0f, 1.0f, 0.0f,
//            0.0f, 0.0f, 0.0f, 1.0f,
//        };
//        model_view_projection =  translate(identity_matrix,0.0f,0.0f, -0.1f);
//        model_view_projection =  rotate(model_view_projection,(float)30f*(float)s,1.0f,0.0f,1.0f);
//        
//        
//        float[] model_view_projection = {
//                2.0f/width, 0.0f, 0.0f, 0.0f,
//                0.0f, 2.0f/height, 0.0f, 0.0f,
//                0.0f, 0.0f, 2.0f/depth, 0f,
//                100.0f/width, 0.0f, 0.0f, 1.0f,
//            };
//        
//        double[] m = view3D.getToScreenMatrix().get();
//        for (int i = 0; i < m.length ; i++){
//        	model_view_projection[i] = (float) m[i];
//        }
//        */
//        
//        //App.debug("\n"+view3D.getToScreenMatrix());
//        
//        float[] modelview = view3D.getToScreenMatrix().getForGL();
//               
//        float[] projection = {
//                2.0f/getWidth(), 0.0f, 0.0f, 0.0f,
//                0.0f, 2.0f/getHeight(), 0.0f, 0.0f,
//                0.0f, 0.0f, -2.0f/getVisibleDepth(), 0f,
//                0.0f, 0.0f, -1f/getVisibleDepth(), 1.0f,
//        };
//        
//        
//        /*
//        float[] modelview = {
//        		1.0f, 0.0f, 0.0f, 0.0f,
//        		0.0f, 1.0f, 0.0f, 0.0f,
//        		0.0f, 0.0f, 1.0f, 0.0f,
//        		0.0f, 0.0f, 0.0f, 1.0f,
//        };
//
//        float[] projection = {
//        		1.0f, 0.0f, 0.0f, 0.0f,
//        		0.0f, 1.0f, 0.0f, 0.0f,
//        		0.0f, 0.0f, 1.0f, 0.0f,
//        		0.0f, 0.0f, 0.0f, 1.0f,
//        };
//         */
//        
//        
//        
//        
//        /*
//        float[] normalMatrix = {
//        		1.0f, 0.0f, 0.0f,
//        		0.0f, 1.0f, 0.0f,
//        		0.0f, 0.0f, 1.0f
//        };
//        */
//        
//        //App.debug("\n"+view3D.getRotationMatrix()+"\n"+normalMatrix[0]+","+normalMatrix[1]+","+normalMatrix[2]);
//        
//
//        // Send the final projection matrix to the vertex shader by
//        // using the uniform location id obtained during the init part.
//        //getGL2ES2().glUniformMatrix4fv(ModelViewProjectionMatrix_location, 1, false, model_view_projection, 0);
//
//        getGL2ES2().glUniformMatrix4fv(modelviewLocation, 1, false, modelview, 0);
//        getGL2ES2().glUniformMatrix4fv(projectionLocation, 1, false, projection, 0);
//        
//        //float[] normalMatrix = view3D.getRotationMatrix().get3x3ForGL();
//        //getGL2ES2().glUniformMatrix3fv(normalMatrixLocation, 1, false, normalMatrix, 0);
//
//        
//        
//        // light
//        
//        setLightPosition();
//        setLight(GLlocal.GL_LIGHT0);
//        
//        
//        
//        // texture
//        
//        getGL2ES2().glUniform1i(fadingLocation, 1);
//        
//        
//        float[] textureCoords = { 
//        		0, 0,
//                0, 3f,
//                3f, 3f
//        };
//        
//        
//        // draw
//        
//        
//        float l = 1f;
//        
//        float[] vertices = {  0.0f,  0f, 0.0f,
//                 0, 0, l, 
//                 0, l, 0  
//                                 };
//        
//        float[] normals = {  
//        		1, 0, 0,
//                1, 0, 0, 
//                1, 0, 0  
//        };
//
//        float alpha = 1f;
//        
//
//        float[] color = {1,0,1,1};
//        getGL2ES2().glUniform4fv(colorLocation, 1, color, 0);
//        drawTriangle(vertices, normals, textureCoords);
//        
//        getGL2ES2().glUniform4fv(colorLocation, 1, PER_VERTEX_COLOR, 0);
//        
//        float[] vertices2 = {  0.0f,  0f, 0f,
//                l, 0, 0f,
//                0, 0, l  
//        };
//        
//        float[] normals2 = {  
//        		0, 1, 0,
//                0, 1, 0, 
//                0, 1, 0  
//        };
//        
//       float[] colors2 = {    0.0f, 1.0f, 0.0f, alpha, //Top color (red)
//               0.0f, 1.0f, 0.0f, alpha, //Bottom Left color (black)
//               0.0f, 1.0f, 0.0f, alpha  //Bottom Right color (yellow) with 10% transparence
//                                    };
//       
//       
//       drawTriangle(vertices2, normals2, colors2, textureCoords);
//       
//       
//
//       float[] vertices4 ={  0,  -l, l,
//               l, 0, 0f,
//               0, 0, l  
//       };
//
//       float[] normals4 = {  
//    		   0, 0, 1,
//    		   0, 0, 1, 
//    		   0, 0, 1  
//       };
//
//       drawTriangle(vertices4, normals4, colors2, textureCoords);
//
//  
//       float z = 0f;
//
//       float[] vertices3 = {  0.0f,  0f, z,
//    		   0, l, z,
//    		   l, 0, z  
//       };
//       float[] colors3 = {    0.0f, 0.0f, 1.0f, alpha, //Top color (red)
//       0.0f, 0.0f, 1.0f, alpha, //Bottom Left color (black)
//       0.0f, 0.0f, 1.0f, alpha  //Bottom Right color (yellow) with 10% transparence
//       };
//
//       drawTriangle(vertices3, normals4, colors3, textureCoords);
//
//
//     
//       
//       
//        getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_POSITION); // Allow release of vertex position memory
//        getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_COLOR); // Allow release of vertex color memory		
//        getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_NORMAL); // Allow release of vertex normal memory		
//        getGL2ES2().glDisableVertexAttribArray(GLSL_ATTRIB_TEXTURE); // Allow release of vertex texture memory		
//
//        getGL2ES2().glDeleteBuffers(2, vboHandles, 0); // Release VBO, color and vertices, buffer GPU memory.
//    }
//
//	public void dispose(GLAutoDrawable drawable){
//        System.out.println("cleanup, remember to release shaders");
//
//        setGL(drawable);
//        
//        getGL2ES2().glUseProgram(0);
//        getGL2ES2().glDetachShader(shaderProgram, vertShader);
//        getGL2ES2().glDeleteShader(vertShader);
//        getGL2ES2().glDetachShader(shaderProgram, fragShader);
//        getGL2ES2().glDeleteShader(fragShader);
//        getGL2ES2().glDeleteProgram(shaderProgram);
//        //System.exit(0);
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void setClipPlane(int n, double[] equation) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setMatrixView() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void unsetMatrixView() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setExportImage() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void setColor(Coords color) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void setColor(GColor color) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void initMatrix() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void resetMatrix() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void drawMouseCursor() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected IntBuffer createSelectBufferForPicking(int bufSize) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setGLForPicking() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void pushSceneMatrix() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void storePickingInfos(Hits3D hits3d, int pointAndCurvesLoop,
//			int labelLoop) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void doPick() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void pickIntersectionCurves() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void glLoadName(int loop) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setLightPosition(int light, float[] values){
//		getGL2ES2().glUniform4fv(lightPositionLocation, 1, values, 0);
//	}
//
//
//	private float[][] ambiantDiffuse;
//	
//	@Override
//	protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0, float ambiant1, float diffuse1){
//       
//		ambiantDiffuse = new float[][] {
//				{ambiant0, diffuse0},
//				{ambiant1, diffuse1}
//		};
//        
//	}
//
//
//
//	@Override
//	protected void setLight(int light){
//		int l = 1;
//		if (light == GLlocal.GL_LIGHT0){
//			l = 0;
//		}
//		getGL2ES2().glUniform2fv(ambiantDiffuseLocation, 1, ambiantDiffuse[l], 0);
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setColorMaterial() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setLightModel() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setAlphaFunc() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setView() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void setStencilLines() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void viewOrtho() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void viewPersp() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void viewGlasses() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	protected void viewOblique() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	@Override
//	protected Manager createManager(){
//    	return null;
//    }
//    
//    
//    
//    
//    
//    
//    
//
//
//}
