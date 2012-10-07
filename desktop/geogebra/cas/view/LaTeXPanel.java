package geogebra.cas.view;

import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * LaTeX panel for CAS output
 *
 */
public class LaTeXPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private AppD app;	
	private String latex;	
	private BufferedImage image;
	private Graphics2D g2image;
	private Dimension equSize;
	
	/**
	 * @param app application
	 */
	public LaTeXPanel(AppD app) {
		this.app = app;
		ensureImageSize(100, 100);
	}
	
	/**
	 * @param latex LaTeX text
	 */
	public void setLaTeX(String latex) {
		if (latex.equals(this.latex)) return;
		
		this.latex = latex;
		
		updateLaTeX();
	}		
	
	private void updateLaTeX() {
		// draw equation to get its size
				equSize = drawEquationToImage();		
				
				// check if image was big enough for equation
				if (ensureImageSize(equSize.width, equSize.height)) {
					equSize = drawEquationToImage();
				}
				
				setPreferredSize(equSize);
				setSize(equSize);	
				validate();
		
	}

	@Override
	public void setForeground(Color c){
		super.setForeground(c);
		//repaint image, but keep size
		drawEquationToImage();
	}
	private Dimension drawEquationToImage() {
		if(g2image==null || latex ==null)
			return new Dimension(0,0);
		g2image.setBackground(getBackground());
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
							RenderingHints.VALUE_ANTIALIAS_ON);
		

		geogebra.common.awt.GDimension fd = app.getDrawEquation().
				drawEquation(app, null, new geogebra.awt.GGraphics2DD(g2image), 0, 0, latex, 
				app.getPlainFontCommon(), false, new geogebra.awt.GColorD(getForeground()), 
				new geogebra.awt.GColorD(getBackground()), true);	

		return new Dimension(fd.getWidth(), fd.getHeight());
	}
	
	private boolean ensureImageSize(int width, int height) {
		if (image == null || image.getWidth() < width || image.getHeight() < height) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			g2image = image.createGraphics();
			return true;
		}		
		return false;
	}
	
	@Override
	public void setFont(Font f){
		super.setFont(f);
		if(latex!=null){
			updateLaTeX();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if (app.exporting){
			app.getDrawEquation();
			//draw full resolution image directly on g
			app.getDrawEquation().drawEquation(app, null, new geogebra.awt.GGraphics2DD((Graphics2D) g), 0, 0, latex, app.getPlainFontCommon(), 
					false, new geogebra.awt.GColorD(getForeground()), new geogebra.awt.GColorD(getBackground()), true);
		} else {
			// draw part of image that contains equation
			if (image != null && equSize != null) {
				g.drawImage(image, 0, 0, equSize.width, equSize.height, 0, 0,
						equSize.width, equSize.height, null);
			}
		}
	}

}
