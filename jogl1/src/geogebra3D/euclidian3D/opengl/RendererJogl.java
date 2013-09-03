package geogebra3D.euclidian3D.opengl;



import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;



import javax.media.opengl.GL; 

import com.sun.opengl.util.BufferUtil;


public class RendererJogl {
	
	protected GL gl;
	
	public static final GL getGL(GLAutoDrawable gLDrawable){
		
		return gLDrawable.getGL();
	}
	

	public final static IntBuffer newIntBuffer(int size){
		return BufferUtil.newIntBuffer(size); 
	}
	
	public final static ByteBuffer newByteBuffer(int size){
		return BufferUtil.newByteBuffer(size); 
	}

	public interface GLlocal extends GL{}
	
	

	final static public void initCaps(){}
	

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
	
	final public static String JOGL_VERSION="JOGL1";
	
}
