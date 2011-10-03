package geogebra.gui;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
 * Component to preview image files in a file chooser.
 * 
 * This file is based on Hack #31 in 
 * "Swing Hacks - Tips & Tools for Building Killer GUIs"
 * by Joshua Marinacci and Chris Adamson.
 * 
 * @author Joshua Marinacci
 * @author Chris Adamson
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */

public class ImagePreview extends JPanel implements PropertyChangeListener {

    /*
     * Ideas:
     *  - Currently throws an IllegalArgumentException on .ico images
     *    -> add plug-in to support this file type (important under Windows)
     *  - Speed up image loading (use Thumbnail images whenever possible;
     *    tried this out, rarely used!)
     *  - Implement this as a split pane (if possible) hence allow user
     *    to increase size to better preview large images
     *    NOT POSSIBLE without baking the whole thing!
     */

    private JFileChooser jfc;

    private BufferedImage img = null;

    private final static int SIZE = 200;

    private final static int HALF_SIZE = SIZE / 2;

    public ImagePreview(JFileChooser jfc) {
	this.jfc = jfc;
	setPreferredSize(new Dimension(SIZE, SIZE));
	setBorder(BorderFactory.createLoweredBevelBorder());
    }

    public void propertyChange(PropertyChangeEvent evt) {
	// only update on selected file change
	if ("SelectedFileChangedProperty".equals(evt.getPropertyName())) {
	    try {
		File file = jfc.getSelectedFile();
		if (file != null) // don't update on directory change
		    updateImage(file);
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
    }

    private void updateImage(File file) throws IOException {
	try {
		// fails for a few JPEGs (Java bug? -> OutOfMemory)
		// so turn off preview for large files
		if (file.length() < 512*1024)
			img = ImageIO.read(file); //returns null if file isn't an image
		else
			img = null;
		repaint();
	} catch (IllegalArgumentException iae) {
	    // This is thrown if you select .ico files
	    //TODO Print error message, or do nothing?
	}
	catch (Throwable t) {
		Application.debug(t.getClass()+"");
		img = null;
	}
    }

    public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	// fill background
	g2.setColor(Color.white);
	g2.fillRect(0, 0, getWidth(), getHeight());

	g2.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);

	// if the selected file is an image go on
	if (img != null) {
	    // calculate the scaling factor
	    int width = img.getWidth();
	    int height = img.getHeight();

	    // set drawing location to upper left corner
	    int x = 0, y = 0;

	    int largerSide = Math.max(width, height);
	    if (largerSide > SIZE) { // only resize large images
		double scale = (double) SIZE / (double) largerSide;

		width = (int) (scale * (double) width);
		height = (int) (scale * (double) height);
	    }
	    else { // centre small images
		x = (int) (HALF_SIZE - (width / 2));
		y = (int) (HALF_SIZE - (height / 2));
	    }

	    // draw the image
	    g2.drawImage(img, x, y, width, height, null);
	}
    }

}
