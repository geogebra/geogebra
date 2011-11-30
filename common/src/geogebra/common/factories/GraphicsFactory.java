package geogebra.common.factories;

import geogebra.common.awt.Font;
import geogebra.common.awt.Graphics2D;

public abstract class GraphicsFactory {
	
	public abstract Graphics2D createGraphics2D();
	public abstract Font createFont();
	

}
