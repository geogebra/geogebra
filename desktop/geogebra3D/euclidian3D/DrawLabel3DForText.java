package geogebra3D.euclidian3D;

import geogebra.euclidian.Drawable;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoText;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * class for drawing texts
 * @author matthieu
 *
 */
public class DrawLabel3DForText extends DrawLabel3D {

	public DrawLabel3DForText(EuclidianView3D view) {
		super(view);
	}
	
	final protected Rectangle2D getBounds(){
	
		if (geo.isLaTeX())
			return Drawable.drawMultilineLaTeX(view.getApplication(), tempGraphics, geo, tempGraphics, font, Color.BLACK, Color.WHITE, text, 0, 0, false);
		else
			return Drawable.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, tempGraphics, false);
		
	}

	final protected void draw(Graphics2D g2d){
		if (geo.isLaTeX())
			Drawable.drawMultilineLaTeX(view.getApplication(), tempGraphics, geo, g2d, font, Color.BLACK, Color.WHITE, text, 0, 0, false);
		else
			Drawable.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, g2d, false);
	}
	

    protected GeoText geo;
    
	public void setGeo(GeoText geo){
		this.geo = geo;
	}
	
}
