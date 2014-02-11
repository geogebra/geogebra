package geogebra.geogebra3D.web.euclidian3D.openGL;

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
 * renderer for webGL
 * @author mathieu
 *
 */
public class RendererWebGL {
	

	private WebGLRenderingContext glContext;
	
	private Canvas webGLCanvas;
    private WebGLProgram shaderProgram;
    private int vertexPositionAttribute;
    private WebGLBuffer vertexBuffer;
    
    private int width, height;
    
    
    private Timer loopTimer;
    private long lastTime, timeNow;

    /**
     * renderer for webGL
     */
	public RendererWebGL() {
		  webGLCanvas = Canvas.createIfSupported();
		  
          glContext = (WebGLRenderingContext) webGLCanvas.getContext("experimental-webgl");
          if(glContext == null) {
                  Window.alert("Sorry, Your Browser doesn't support WebGL!");
          }

          setDimension(500, 500);
          
          
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
	
	
	/**
	 * set dimensions of the canvas
	 * @param w width
	 * @param h height
	 */
	public void setDimension(int w, int h){
        webGLCanvas.setCoordinateSpaceWidth(w);
        webGLCanvas.setCoordinateSpaceHeight(h);
        glContext.viewport(0, 0, w, h);
        
        width = w;
        height = h;
        
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
        float[] perspectiveMatrix = createPerspectiveMatrix(45, (float) width/height, 0.1f, 1000);
        WebGLUniformLocation uniformLocation = glContext.getUniformLocation(shaderProgram, "perspectiveMatrix");
        glContext.uniformMatrix4fv(uniformLocation, false, perspectiveMatrix);
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
	
	
}
