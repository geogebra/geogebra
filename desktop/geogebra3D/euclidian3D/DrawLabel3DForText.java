package geogebra3D.euclidian3D;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.draw.DrawText;
import geogebra.common.kernel.geos.GeoText;
import geogebra.euclidian.EuclidianStaticD;
import geogebra3D.euclidian3D.opengl.Renderer;

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
	
	@Override
	final protected Rectangle2D getBounds(){
	
		if (geo.isLaTeX())
			return geogebra.awt.GRectangleD.getAWTRectangle(EuclidianStatic.drawMultilineLaTeX(view.getApplication(), new geogebra.awt.GGraphics2DD(tempGraphics), geo, new geogebra.awt.GGraphics2DD(tempGraphics), new geogebra.awt.GFontD(font), 
					GColor.BLACK, GColor.WHITE, text, 0, 0, false));
		
			return geogebra.awt.GRectangleD.getAWTRectangle(EuclidianStaticD.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, new geogebra.awt.GGraphics2DD(tempGraphics), false));
		
	}

	@Override
	final protected void draw(Graphics2D g2d){
		if (geo.isLaTeX())
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(), new geogebra.awt.GGraphics2DD(tempGraphics), geo, new geogebra.awt.GGraphics2DD(g2d), new geogebra.awt.GFontD(font), 
					GColor.BLACK, GColor.WHITE, text, 0, 0, false);
		else
			EuclidianStaticD.drawMultiLineIndexedText(view.getApplication(), text, 0, 0, new geogebra.awt.GGraphics2DD(g2d), false);
	}
	

    protected GeoText geo;
    
	public void setGeo(GeoText geo){
		this.geo = geo;
	}
	
	
	@Override
	protected void draw(Renderer renderer, int x, int y, int z){
		
		//draw text
		super.draw(renderer, x, y, z);

		if (geo.doHighlighting()){
			//draw bounds if highlighted
			renderer.disableTextures();
			renderer.disableMultisample();
			renderer.setLineWidth(geo.getLineThickness()/2);
			renderer.setColor(DrawText.HIGHLIGHT_COLOR);
			renderer.getGeometryManager().getText().rectangleBounds(x, y, z, width, height);
			renderer.enableMultisample();
			renderer.enableTextures();
		}
	}
	
}
