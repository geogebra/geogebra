package geogebra3D.euclidian3D.opengl;



import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLProfile;



import javax.media.opengl.GL2; 
import com.jogamp.opengl.util.GLBuffers;


public class RendererJogl {
	
	protected GL2 gl; 
	
	public static final GL2 getGL(GLAutoDrawable gLDrawable){
		
		return gLDrawable.getGL().getGL2(); 
	}
	

	public final static IntBuffer newIntBuffer(int size){
		return GLBuffers.newDirectIntBuffer(size); 
	}
	
	public final static ByteBuffer newByteBuffer(int size){
		return GLBuffers.newDirectByteBuffer(size); 
	}

	public interface GLlocal extends GL2{}

	
	public static GLCapabilities caps;
	

	final static public void initCaps(){
		
		
		//GLProfile.initSingleton(); 
		
		caps = new GLCapabilities(GLProfile.getDefault());
		
		//caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		
		//caps.setAlphaBits(8);

		
		//anti-aliasing
		//caps.setSampleBuffers(true);
		//caps.setNumSamples(4);    	
		
		//avoid flickering
		//caps.setDoubleBuffered(true);
		caps.setDoubleBuffered(false);

	}
	
	final public static String getGLInfos(GLAutoDrawable drawable){
		
		GL gl = drawable.getGL(); 

		GLCapabilitiesImmutable c = drawable.getChosenGLCapabilities();
		
		return "Init on "+Thread.currentThread()
				+"\nChosen GLCapabilities: " + c
				+"\ndouble buffered: " + c.getDoubleBuffered()
				+"\nstereo: " + c.getStereo()
				+"\nINIT GL IS: " + gl.getClass().getName()
				+"\nGL_VENDOR: " + gl.glGetString(GL.GL_VENDOR)
				+"\nGL_RENDERER: " + gl.glGetString(GL.GL_RENDERER)
				+"\nGL_VERSION: " + gl.glGetString(GL.GL_VERSION);
		
	}
	
	final public static String JOGL_VERSION="JOGL2";
	
}
