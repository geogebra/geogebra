package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.draw.DrawText;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.geos.GeoText;

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
	
		if (geo.isLaTeX()){
			return EuclidianStatic.drawMultilineLaTeX(view.getApplication(), tempGraphics, geo, tempGraphics, font, 
					GColor.BLACK, GColor.WHITE, text, 0, 0, false);
		}
		
		return EuclidianStatic.drawMultiLineText(view.getApplication(), text, 0, 0, tempGraphics, false, tempGraphics.getFont());
		
	}

	@Override
	final protected void draw(GGraphics2D g2d){
		if (geo.isLaTeX()){
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(), tempGraphics, geo, g2d, font, 
					GColor.BLACK, GColor.WHITE, text, 0, 0, false);
		}else{
			EuclidianStatic.drawMultiLineText(view.getApplication(), text, 0, 0, g2d, false, g2d.getFont());
		}
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
