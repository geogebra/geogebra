package geogebra.web3D.euclidian3D.opengl;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.RootPanel;
//import com.googlecode.gwtgl.binding.WebGLRenderingContext;

public class Test {
	

	//private WebGLRenderingContext glContext;
	

	public Test() {
		  final Canvas webGLCanvas = Canvas.createIfSupported();
          webGLCanvas.setCoordinateSpaceHeight(500);
          webGLCanvas.setCoordinateSpaceWidth(500);
         // glContext = (WebGLRenderingContext) webGLCanvas.getContext("experimental-webgl");
         // if(glContext == null) {
         //         Window.alert("Sorry, Your Browser doesn't support WebGL!");
         // }
         // glContext.viewport(0, 0, 500, 500);
          
          RootPanel.get("webGL").add(webGLCanvas);
          start();
	}
	
	
	private void start() {
		
        //initShaders();
        //glContext.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //glContext.clearDepth(1.0f);
        //glContext.enable(WebGLRenderingContext.DEPTH_TEST);
        //glContext.depthFunc(WebGLRenderingContext.LEQUAL);
        //initBuffers();

        drawScene();
        
	}
	
	private void drawScene() {
        //glContext.clear(WebGLRenderingContext.COLOR_BUFFER_BIT | WebGLRenderingContext.DEPTH_BUFFER_BIT);
        //float[] perspectiveMatrix = createPerspectiveMatrix(45, 1, 0.1f, 1000);
        //WebGLUniformLocation uniformLocation = glContext.getUniformLocation(shaderProgram, "perspectiveMatrix");
        //glContext.uniformMatrix4fv(uniformLocation, false, perspectiveMatrix);
        //glContext.vertexAttribPointer(vertexPositionAttribute, 3, WebGLRenderingContext.FLOAT, false, 0, 0);
        //glContext.drawArrays(WebGLRenderingContext.TRIANGLES, 0, 3);
	}

}
