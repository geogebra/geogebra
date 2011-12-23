package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianStatic;

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
			return geogebra.awt.Rectangle.getAWTRectangle(EuclidianStatic.drawMultilineLaTeX(view.getApplication(), new geogebra.awt.Graphics2D(tempGraphics), geo, new geogebra.awt.Graphics2D(tempGraphics), new geogebra.awt.Font(font), 
					geogebra.awt.Color.BLACK, geogebra.awt.Color.WHITE, text, 0, 0, false));
		else
			return EuclidianStatic.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, new geogebra.awt.Graphics2D(tempGraphics), false);
		
	}

	final protected void draw(Graphics2D g2d){
		if (geo.isLaTeX())
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(), new geogebra.awt.Graphics2D(tempGraphics), geo, new geogebra.awt.Graphics2D(g2d), new geogebra.awt.Font(font), 
					geogebra.awt.Color.BLACK, geogebra.awt.Color.WHITE, text, 0, 0, false);
		else
			EuclidianStatic.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, new geogebra.awt.Graphics2D(g2d), false);
	}
	

    protected GeoText geo;
    
	public void setGeo(GeoText geo){
		this.geo = geo;
	}
	
}
