package geogebra3D.euclidian3D.opengl;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


/**
 * 
 * class that gives a freezed representation of the 3D rendering
 * 
 * @author mathieu
 *
 */
public class RendererFreezingPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Renderer renderer;
	
	/**
	 * common constructor
	 * @param renderer
	 */
	public RendererFreezingPanel(Renderer renderer){
		this.renderer = renderer;
	}

	final public void paint(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(renderer.getExportImage(), 0, 0, null);
		
	}


	
}
