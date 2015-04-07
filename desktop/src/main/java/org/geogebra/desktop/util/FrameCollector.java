package org.geogebra.desktop.util;

import java.awt.image.BufferedImage;

/**
 * Interface for animated GIF export, needed to separate GUI from App
 * 
 * @author Zbynek
 */

public interface FrameCollector {

	public void addFrame(BufferedImage img);

	public void finish();

}
