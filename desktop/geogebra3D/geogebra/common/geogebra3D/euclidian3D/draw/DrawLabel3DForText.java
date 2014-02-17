package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.draw.DrawText;
import geogebra.common.kernel.geos.GeoText;
import geogebra.euclidian.EuclidianStaticD;
import geogebra.main.AppD;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

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
	final protected GRectangle getBounds(){
	
		if (geo.isLaTeX())
			return EuclidianStatic.drawMultilineLaTeX(view.getApplication(), tempGraphics, geo, tempGraphics, new geogebra.awt.GFontD(font), 
					GColor.BLACK, GColor.WHITE, text, 0, 0, false);
		
			return EuclidianStaticD.drawMultiLineIndexedText((AppD) view.getApplication(), text, 0, 0, tempGraphics, false);
		
	}

	@Override
	final protected void draw(GGraphics2D g2d){
		if (geo.isLaTeX())
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(), tempGraphics, geo, g2d, new geogebra.awt.GFontD(font), 
					GColor.BLACK, GColor.WHITE, text, 0, 0, false);
		else
			EuclidianStaticD.drawMultiLineIndexedText((AppD) view.getApplication(), text, 0, 0, g2d, false);
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
			renderer.getGeometryManager().rectangleBounds(x, y, z, width, height);
			renderer.enableMultisample();
			renderer.enableTextures();
		}
	}
	
}
