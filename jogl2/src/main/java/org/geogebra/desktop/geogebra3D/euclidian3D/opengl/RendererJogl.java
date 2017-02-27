package org.geogebra.desktop.geogebra3D.euclidian3D.opengl;



import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;


public class RendererJogl {
	
	private GLAutoDrawable gLDrawable;
	
	static public final int GL_STREAM_DRAW = GL2ES2.GL_STREAM_DRAW;
	
	public GL getGL(){
		
		return getGL2(); 
	}
	
	public GL2 getGL2(){
		if (gLDrawable.getGL() == null) {
			return null;
		}
		return gLDrawable.getGL().getGL2(); 
	}
	
	
	
	public void setGL(GLAutoDrawable gLDrawable){		
		this.gLDrawable = gLDrawable;
	}
	
	
	
	
	/**
	 * 
	 * @return current GL (as GL2ES2)
	 */
	public javax.media.opengl.GL2ES2 getGL2ES2() {
		
		return gLDrawable.getGL().getGL2ES2();
	}
	
	
	
	
	

	public final static IntBuffer newIntBuffer(int size){
		return GLBuffers.newDirectIntBuffer(size); 
	}
	
	public final static ByteBuffer newByteBuffer(int size){
		return GLBuffers.newDirectByteBuffer(size); 
	}

	public interface GLlocal extends GL2{}

	public interface GL2ES2 extends javax.media.opengl.GL2ES2{}
	
	public static GLCapabilities caps = null;
	
	
	final static public void initSingleton(){
		try{
			GLProfile.initSingleton(); 
		}catch(Exception e){
			// No GLProfile.initSingleton() working -- maybe not needed
		}
	}

	static private GLProfile profile;

	static private boolean isGL2ES2;

	/**
	 * set the default profile to current profile
	 * 
	 * @return true if default profile supports shader language (GL2ES2)
	 */
	final static public boolean setDefaultProfile() {
		if (profile == null) {
			profile = GLProfile.getDefault();
			isGL2ES2 = profile.isGL2ES2();
		}
		
		return isGL2ES2;
	}


	final static public void initCaps(boolean stereo){
		
		if (caps != null){
			return;
		}
						
		//System.out.println("profile -- is GL2 = " + profile.isGL2()+" -- isHardwareRasterizer = "+ profile.isHardwareRasterizer());
		caps = new GLCapabilities(profile);
		
		
		//caps.setAlphaBits(8);

		
		//anti-aliasing
		caps.setSampleBuffers(true);
		caps.setNumSamples(4);    	
		
		//avoid flickering
		caps.setDoubleBuffered(true);
		//caps.setDoubleBuffered(false);
		
		if (stereo){
			//add stereo
			caps.setStereo(true);
		}
		
		//stencil buffer is needed for hacked passive 3D
		caps.setStencilBits(1);
		

	}
	
	final public static String[] getGLInfos(GLAutoDrawable drawable) {
		
		GL gl = drawable.getGL(); 

		GLCapabilitiesImmutable c = drawable.getChosenGLCapabilities();
		
		String[] ret = { c + "", c.getDoubleBuffered() + "", c.getStereo() + "",
				c.getStencilBits() + "", gl.getClass().getName(),
				gl.glGetString(GL.GL_VENDOR), gl.glGetString(GL.GL_RENDERER),
				gl.glGetString(GL.GL_VERSION) };

		return ret;
		
	}
	
	
	
	static private boolean useCanvas;
	
	/**
	 * 
	 * @param useCanvas0 says if we use Canvas or JPanel
	 * @return 3D component
	 */
	static public Component3D createComponent3D(boolean useCanvas0){
		
		useCanvas = useCanvas0;
		
		if(useCanvas){
			return new ComponentGLCanvas();
		}
		return new ComponentGLJPanel();
		
	}
	

	static public Animator createAnimator(Component3D canvas, int i){

		if(useCanvas){
			return new AnimatorCanvas((GLCanvas) canvas, i);
		}
		return new AnimatorJPanel((GLJPanel) canvas, i);

		
	}
	
	
	/////////////////////////
	// 3D Component
	
	
	@SuppressWarnings("serial")
	private static class ComponentGLJPanel extends GLJPanel implements Component3D{ 
		
		public ComponentGLJPanel(){
			super(caps);
		}
		
	}
	
	@SuppressWarnings("serial")
	private static class ComponentGLCanvas extends GLCanvas implements Component3D{ 
		
		public ComponentGLCanvas(){
			super(caps);
		}
		
	}
	
	/////////////////////////
	// 3D Animator
	
	
	private static class AnimatorJPanel extends FPSAnimator implements Animator{ 
		
		public AnimatorJPanel(GLJPanel canvas, int i){
			super(canvas,i);
		}
		
	}
	
	private static class AnimatorCanvas extends FPSAnimator implements Animator{ 
		
		public AnimatorCanvas(GLCanvas canvas, int i){
			super(canvas,i);
		}
		
	}
	
	
	
	
	
	/////////////////////////
	// JOGL Version
	
	
	
	final public static String JOGL_VERSION="JOGL2";
	
}
