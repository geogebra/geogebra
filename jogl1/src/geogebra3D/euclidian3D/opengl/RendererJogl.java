package geogebra3D.euclidian3D.opengl;



import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;



import javax.media.opengl.GL; 

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;


public class RendererJogl {
	
	protected GL gl;
	
	public void setGL(GLAutoDrawable gLDrawable){		
		gl = gLDrawable.getGL();
	}
	
	
	public GL getGL(){
		
		return gl; 
	}
	
	public GL getGL2(){
		
		return gl; 
	}
	
	
	/**
	 * 
	 * @return current GL (as GL2ES2)
	 */
	public GL2ES2 getGL2ES2(){
		// only used in jogl2
		return null; 
	}
	
	
	
	public void setGL2ES2(GLAutoDrawable gLDrawable){	
		// only used in jogl2
	}
	
	
	public final static IntBuffer newIntBuffer(int size){
		return BufferUtil.newIntBuffer(size); 
	}
	
	public final static ByteBuffer newByteBuffer(int size){
		return BufferUtil.newByteBuffer(size); 
	}

	public interface GLlocal extends GL{}
	
	public interface GL2ES2 extends GL{

		boolean isGL3core();

	}


	private static GLCapabilities caps;

	final static public void initCaps(){

		caps = new GLCapabilities();


		//anti-aliasing
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);    	
		//avoid flickering
		caps.setDoubleBuffered(true);

	}


	final public static String getGLInfos(GLAutoDrawable drawable){
		
		GL gl = drawable.getGL(); 

		GLCapabilities c = drawable.getChosenGLCapabilities();
		
		return "Init on "+Thread.currentThread()
				+"\nChosen GLCapabilities: " + c
				+"\ndouble buffered: " + c.getDoubleBuffered()
				+"\nstereo: " + c.getStereo()
				+"\nINIT GL IS: " + gl.getClass().getName()
				+"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)
				+"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER)
				+"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION);
		
	}
	
	

	/**
	 * WARNING: No implementation for GLJPanel
	 * @param useCanvas0
	 * @return GLCanvas
	 */
	static protected Component3D createComponent3D(boolean useCanvas0){

		return new ComponentGLCanvas();

	}
	

	static public Animator createAnimator(Component3D canvas, int i){

			return new AnimatorCanvas((GLCanvas) canvas, i);
		
	}
	

	
	
	
	

	private static class ComponentGLCanvas extends GLCanvas implements Component3D{ 

		public ComponentGLCanvas(){
			super(caps);
		}

	}


	private static class AnimatorCanvas extends FPSAnimator implements Animator{ 
		
		public AnimatorCanvas(GLCanvas canvas, int i){
			super(canvas,i);
		}
		

		public void resume(){
			// used for JOGL2
		}
		
		
		
	}
	

	
	
	
	
	final public static String JOGL_VERSION="JOGL1";
	
}
