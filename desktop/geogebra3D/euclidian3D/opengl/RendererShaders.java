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
//    private void glMultMatrixf(FloatBuffer a, FloatBuffer b, FloatBuffer d) {
//        final int aP = a.position();
//        final int bP = b.position();
//        final int dP = d.position();
//        for (int i = 0; i < 4; i++) {
//            final float ai0=a.get(aP+i+0*4),  ai1=a.get(aP+i+1*4),  ai2=a.get(aP+i+2*4),  ai3=a.get(aP+i+3*4);
//            d.put(dP+i+0*4 , ai0 * b.get(bP+0+0*4) + ai1 * b.get(bP+1+0*4) + ai2 * b.get(bP+2+0*4) + ai3 * b.get(bP+3+0*4) );
//            d.put(dP+i+1*4 , ai0 * b.get(bP+0+1*4) + ai1 * b.get(bP+1+1*4) + ai2 * b.get(bP+2+1*4) + ai3 * b.get(bP+3+1*4) );
//            d.put(dP+i+2*4 , ai0 * b.get(bP+0+2*4) + ai1 * b.get(bP+1+2*4) + ai2 * b.get(bP+2+2*4) + ai3 * b.get(bP+3+2*4) );
//            d.put(dP+i+3*4 , ai0 * b.get(bP+0+3*4) + ai1 * b.get(bP+1+3*4) + ai2 * b.get(bP+2+3*4) + ai3 * b.get(bP+3+3*4) );
//        }
//    }
//
//    private float[] multiply(float[] a,float[] b){
//        float[] tmp = new float[16];
//        glMultMatrixf(FloatBuffer.wrap(a),FloatBuffer.wrap(b),FloatBuffer.wrap(tmp));
//        return tmp;
//    }
//
//    private float[] translate(float[] m,float x,float y,float z){
//        float[] t = { 1.0f, 0.0f, 0.0f, 0.0f,
//                      0.0f, 1.0f, 0.0f, 0.0f,
//                      0.0f, 0.0f, 1.0f, 0.0f,
//                      x, y, z, 1.0f };
//        return multiply(m, t);
//    }
//
//    private float[] rotate(float[] m,float a,float x,float y,float z){
//        float s, c;
//        s = (float)Math.sin(Math.toRadians(a));
//        c = (float)Math.cos(Math.toRadians(a));
//        float[] r = {
//            x * x * (1.0f - c) + c,     y * x * (1.0f - c) + z * s, x * z * (1.0f - c) - y * s, 0.0f,
//            x * y * (1.0f - c) - z * s, y * y * (1.0f - c) + c,     y * z * (1.0f - c) + x * s, 0.0f,
//            x * z * (1.0f - c) + y * s, y * z * (1.0f - c) - x * s, z * z * (1.0f - c) + c,     0.0f,
//            0.0f, 0.0f, 0.0f, 1.0f };
//            return multiply(m, r);
//        }
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
//    private int width=1920;
//    private int height=1080;
//    private int depth=width;
//
//    private int shaderProgram;
//    private int vertShader;
//    private int fragShader;
//    //private int ModelViewProjectionMatrix_location;
//    private int modelviewLocation, projectionLocation;
//
//    int[] vboHandles;
//    private int vboVertices, vboColors;
//    
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
//    @Override
//	public void init(GLAutoDrawable drawable) {
//        GL2ES2 gl = drawable.getGL().getGL2ES2();
//
//        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
//        System.err.println("INIT GL IS: " + gl.getClass().getName());
//        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
//        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
//        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
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
//        if(gl.isGL3core()){
//            System.out.println("GL3 core detected: explicit add #version 130 to shaders");
//            vertexShaderString = "#version 130\n"+vertexShaderString;
//            fragmentShaderString = "#version 130\n"+fragmentShaderString;
//	}
//
//        // Create GPU shader handles
//        // OpenGL ES retuns a index id to be stored for future reference.
//        vertShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);
//        fragShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER);
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
//        gl.glShaderSource(vertShader, vlines.length, vlines, vlengths, 0);
//        gl.glCompileShader(vertShader);
//
//        //Check compile status.
//        int[] compiled = new int[1];
//        gl.glGetShaderiv(vertShader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
//        if(compiled[0]!=0){System.out.println("Horray! vertex shader compiled");}
//        else {
//            int[] logLength = new int[1];
//            gl.glGetShaderiv(vertShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);
//
//            byte[] log = new byte[logLength[0]];
//            gl.glGetShaderInfoLog(vertShader, logLength[0], (int[])null, 0, log, 0);
//
//            System.err.println("Error compiling the vertex shader: " + new String(log));
//            System.exit(1);
//        }
//
//        //Compile the fragmentShader String into a program.
//        String[] flines = new String[] { fragmentShaderString };
//        int[] flengths = new int[] { flines[0].length() };
//        gl.glShaderSource(fragShader, flines.length, flines, flengths, 0);
//        gl.glCompileShader(fragShader);
//
//        //Check compile status.
//        gl.glGetShaderiv(fragShader, GL2ES2.GL_COMPILE_STATUS, compiled,0);
//        if(compiled[0]!=0){System.out.println("Horray! fragment shader compiled");}
//        else {
//            int[] logLength = new int[1];
//            gl.glGetShaderiv(fragShader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);
//
//            byte[] log = new byte[logLength[0]];
//            gl.glGetShaderInfoLog(fragShader, logLength[0], (int[])null, 0, log, 0);
//
//            System.err.println("Error compiling the fragment shader: " + new String(log));
//            System.exit(1);
//        }
//
//        //Each shaderProgram must have
//        //one vertex shader and one fragment shader.
//        shaderProgram = gl.glCreateProgram();
//        gl.glAttachShader(shaderProgram, vertShader);
//        gl.glAttachShader(shaderProgram, fragShader);
//
//        //Associate attribute ids with the attribute names inside
//        //the vertex shader.
//        gl.glBindAttribLocation(shaderProgram, 0, "attribute_Position");
//        gl.glBindAttribLocation(shaderProgram, 1, "attribute_Color");
//
//        gl.glLinkProgram(shaderProgram);
//
//        //Get a id number to the uniform_Projection matrix
//        //so that we can update it.
//        //ModelViewProjectionMatrix_location = gl.glGetUniformLocation(shaderProgram, "uniform_Projection");
//        modelviewLocation = gl.glGetUniformLocation(shaderProgram, "modelview");
//        projectionLocation = gl.glGetUniformLocation(shaderProgram, "projection");
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
//        vboHandles = new int[2];
//        gl.glGenBuffers(2, vboHandles, 0);
//        vboColors = vboHandles[0];
//        vboVertices = vboHandles[1];
//        
//        //super.init(drawable);
//        
//        
//        gl.glEnable(GLlocal.GL_DEPTH_TEST);
//        gl.glDepthFunc(GLlocal.GL_LEQUAL); //less or equal for transparency
//		gl.glEnable(GLlocal.GL_POLYGON_OFFSET_FILL);
//
//        //gl.glPolygonOffset(1.0f, 2f);
//
//        //gl.glEnable(GLlocal.GL_CULL_FACE);
//        
//        //blending
//        gl.glBlendFunc(GLlocal.GL_SRC_ALPHA, GLlocal.GL_ONE_MINUS_SRC_ALPHA);
//        //gl.glBlendFunc(GLlocal.GL_SRC_ALPHA, GLlocal.GL_DST_ALPHA);
//        gl.glEnable(GLlocal.GL_BLEND);	
//        
//        //projection type
//        //viewOrtho(gl); 
//               
//        //normal anti-scaling
//        gl.glEnable(GLlocal.GL_NORMALIZE);
// 
//    }
//
//    /*
//    public void reshape(GLAutoDrawable drawable, int x, int y, int z, int h) {
//        System.out.println("Window resized to width=" + z + " height=" + h);
//        width = z;
//        height = h;
//
//        // Get gl
//        GL2ES2 gl = drawable.getGL().getGL2ES2();
//
//        // Optional: Set viewport
//        // Render to a square at the center of the window.
//        gl.glViewport((width-height)/2,0,height,height);
//    }
//    */
//    
//    private void drawTriangle(GL2ES2 gl, float[] vertices, float[] colors){
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
//        gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboVertices);
//
//        // transfer data to VBO, this perform the copy of data from CPU -> GPU memory
//        int numBytes = vertices.length * 4;
//        gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbVertices, GL.GL_STATIC_DRAW);
//        fbVertices = null; // It is OK to release CPU vertices memory after transfer to GPU
//
//        // Associate Vertex attribute 0 with the last bound VBO
//        gl.glVertexAttribPointer(0 /* the vertex attribute */, 3,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        // VBO
//        // gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, 0); // You can unbind the VBO after it have been associated using glVertexAttribPointer
//
//        gl.glEnableVertexAttribArray(0);
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
//        gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vboColors);
//        numBytes = colors.length * 4;
//        gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, fbColors, GL.GL_STATIC_DRAW);
//        fbColors = null; // It is OK to release CPU color memory after transfer to GPU
//
//        // Associate Vertex attribute 1 with the last bound VBO
//        gl.glVertexAttribPointer(1 /* the vertex attribute */, 4 /* four possitions used for each vertex */,
//                                 GL2ES2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
//                                 0 /* The bound VBO data offset */);
//
//        gl.glEnableVertexAttribArray(1);
//        
//        /////////////////////////
//        // draw
//
//        gl.glDrawArrays(GL2ES2.GL_TRIANGLES, 0, 3); //Draw the vertices as triangle // 3 <=> 1 triangle
//    }
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
//        GL2ES2 gl = drawable.getGL().getGL2ES2();
//
//        // Clear screen
//        gl.glClearColor(0, 0, 0, 1f);  // Purple
//        gl.glClear(GL2ES2.GL_STENCIL_BUFFER_BIT |
//                   GL2ES2.GL_COLOR_BUFFER_BIT   |
//                   GL2ES2.GL_DEPTH_BUFFER_BIT   );
//                   
//
//        // Use the shaderProgram that got linked during the init part.
//        gl.glUseProgram(shaderProgram);
//        
//
//        /* Change a projection matrix
//         * The matrix multiplications and OpenGL ES2 code below
//         * basically match this OpenGL ES1 code.
//         * note that the model_view_projection matrix gets sent to the vertexShader.
//         *
//         * gl.glLoadIdentity();
//         * gl.glTranslatef(0.0f,0.0f,-0.1f);
//         * gl.glRotatef((float)30f*(float)s,1.0f,0.0f,1.0f);
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
//                2.0f/width, 0.0f, 0.0f, 0.0f,
//                0.0f, 2.0f/height, 0.0f, 0.0f,
//                0.0f, 0.0f, -2.0f/depth, 0f,
//                0.0f, 0.0f, -1f/depth, 1.0f,
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
//        // Send the final projection matrix to the vertex shader by
//        // using the uniform location id obtained during the init part.
//        //gl.glUniformMatrix4fv(ModelViewProjectionMatrix_location, 1, false, model_view_projection, 0);
//
//        gl.glUniformMatrix4fv(modelviewLocation, 1, false, modelview, 0);
//        gl.glUniformMatrix4fv(projectionLocation, 1, false, projection, 0);
//
//        
//        
//        
//        /*
//        float[] vertices = {  0.0f,  1.0f, 1.0f, //Top
//                -1.0f, -1.0f, 0.0f, //Bottom Left
//                 1.0f, -1.0f, 0.0f  //Bottom Right
//                                 };
//           
//                                 */
//        
//        
//        float l = 1f;
//        
//        float[] vertices = {  0.0f,  0f, 0.0f,
//                 0, 0, l, 
//                 0, l, 0  
//                                 };
//
//        float alpha = 1f;
//        
//        float[] colors = {    1.0f, 0.0f, 0.0f, alpha, //Top color (red)
//                1.0f, 0.0f, 0.0f, alpha, //Bottom Left color (black)
//                1.0f, 0.0f, 0.0f, alpha  //Bottom Right color (yellow) with 10% transparence
//                                     };
//
//        drawTriangle(gl, vertices, colors);
//        
//        
//        float[] vertices2 = {  0.0f,  0f, 0f,
//                l, 0, 0f,
//                0, 0, l  
//                                };
//       float[] colors2 = {    0.0f, 1.0f, 0.0f, alpha, //Top color (red)
//               0.0f, 1.0f, 0.0f, alpha, //Bottom Left color (black)
//               0.0f, 1.0f, 0.0f, alpha  //Bottom Right color (yellow) with 10% transparence
//                                    };
//       
//       drawTriangle(gl, vertices2, colors2);
//  
//       float z = 0f;
//       
//       float[] vertices3 = {  0.0f,  0f, z,
//               0, l, z,
//               l, 0, z  
//                               };
//      float[] colors3 = {    0.0f, 0.0f, 1.0f, alpha, //Top color (red)
//              0.0f, 0.0f, 1.0f, alpha, //Bottom Left color (black)
//              0.0f, 0.0f, 1.0f, alpha  //Bottom Right color (yellow) with 10% transparence
//                                   };
//      
//      drawTriangle(gl, vertices3, colors3);
// 
//      
//        gl.glDisableVertexAttribArray(0); // Allow release of vertex position memory
//        gl.glDisableVertexAttribArray(1); // Allow release of vertex color memory		
//
//        gl.glDeleteBuffers(2, vboHandles, 0); // Release VBO, color and vertices, buffer GPU memory.
//    }
//
//    @Override
//	public void dispose(GLAutoDrawable drawable){
//        System.out.println("cleanup, remember to release shaders");
//        GL2ES2 gl = drawable.getGL().getGL2ES2();
//        gl.glUseProgram(0);
//        gl.glDetachShader(shaderProgram, vertShader);
//        gl.glDeleteShader(vertShader);
//        gl.glDetachShader(shaderProgram, fragShader);
//        gl.glDeleteShader(fragShader);
//        gl.glDeleteProgram(shaderProgram);
//        //System.exit(0);
//    }
//
//
//    @Override
//	public void reshape(GLAutoDrawable drawable, int x, int y, int z, int h) {
//    	System.out.println("Window resized to width=" + z + " height=" + h);
//    	width = z;
//    	height = h;
//    	depth = width;
//
//    	// Get gl
//    	GL2ES2 gl = drawable.getGL().getGL2ES2();
//
//    	// Optional: Set viewport
//    	// Render to a square at the center of the window.
//    	//gl.glViewport((width-height)/2,0,height,height);
//    	
//
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
//	protected void setLight(int light, int attr, float[] values) {
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
//}
