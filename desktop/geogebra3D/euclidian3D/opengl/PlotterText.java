package geogebra3D.euclidian3D.opengl;

import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;


/*
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
*/

/**
 * Class that manages text rendering
 * 
 * @author matthieu
 *
 */
public class PlotterText {
	
	/** geometry manager */
	private Manager manager;
	

	
	
	/**
	 * common constructor
	 * @param manager
	 */
	public PlotterText(Manager manager){
		
		this.manager = manager;
		
	}
	
	
	/**
	 * draws a rectangle
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 */
	public void rectangle(int x, int y, int z, int width, int height){
		
		//GL2 gl = manager.renderer.getGL();
		
		manager.renderer.gl.glBegin(GLlocal.GL_QUADS);
		manager.texture(0, 0);
		manager.renderer.gl.glVertex3i(x,y,z); 
		manager.texture(1, 0);
		manager.renderer.gl.glVertex3i(x+width,y,z); 
		manager.texture(1, 1);
		manager.renderer.gl.glVertex3i(x+width,y+height,z); 
		manager.texture(0, 1);
		manager.renderer.gl.glVertex3i(x,y+height,z); 	
		manager.renderer.gl.glEnd();
		
	}
	
	
	
	
}
