package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoText;
import geogebra.euclidian.EuclidianStaticD;

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
			return geogebra.awt.GRectangleD.getAWTRectangle(EuclidianStaticD.drawMultilineLaTeX(view.getApplication(), new geogebra.awt.GGraphics2DD(tempGraphics), geo, new geogebra.awt.GGraphics2DD(tempGraphics), new geogebra.awt.GFontD(font), 
					geogebra.awt.GColorD.BLACK, geogebra.awt.GColorD.WHITE, text, 0, 0, false));
		
			return geogebra.awt.GRectangleD.getAWTRectangle(EuclidianStaticD.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, new geogebra.awt.GGraphics2DD(tempGraphics), false));
		
	}

	final protected void draw(Graphics2D g2d){
		if (geo.isLaTeX())
			EuclidianStaticD.drawMultilineLaTeX(view.getApplication(), new geogebra.awt.GGraphics2DD(tempGraphics), geo, new geogebra.awt.GGraphics2DD(g2d), new geogebra.awt.GFontD(font), 
					geogebra.awt.GColorD.BLACK, geogebra.awt.GColorD.WHITE, text, 0, 0, false);
		else
			EuclidianStaticD.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, new geogebra.awt.GGraphics2DD(g2d), false);
	}
	

    protected GeoText geo;
    
	public void setGeo(GeoText geo){
		this.geo = geo;
	}
	
}
