package geogebra3D.euclidian3D.opengl;



import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GLAutoDrawable;
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

	

	final static public void initSingleton(){
		GLProfile.initSingleton(); 
	}
	
	
}
