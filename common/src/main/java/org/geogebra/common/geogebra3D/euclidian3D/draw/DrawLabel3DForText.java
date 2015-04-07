package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * class for drawing texts
 * 
 * @author matthieu
 *
 */
public class DrawLabel3DForText extends DrawLabel3D {

	public DrawLabel3DForText(EuclidianView3D view, Drawable3D drawable) {
		super(view, drawable);
	}

	@Override
	final protected GRectangle getBounds() {

		if (geo.isLaTeX()) {
			return EuclidianStatic.drawMultilineLaTeX(view.getApplication(),
					tempGraphics, geo, tempGraphics, font, GColor.BLACK,
					GColor.WHITE, text, 0, 0, false);
		}

		return EuclidianStatic.drawMultiLineText(view.getApplication(), text,
				0, 0, tempGraphics, false, tempGraphics.getFont());

	}

	@Override
	final protected GBufferedImage draw() {
		GBufferedImage bimg = createBufferedImage();
		GGraphics2D g2d = createGraphics2D(bimg);

		if (geo.isLaTeX()) {
			EuclidianStatic.drawMultilineLaTeX(view.getApplication(),
					tempGraphics, geo, g2d, font, GColor.BLACK, GColor.WHITE,
					text, 0, 0, false);
		} else {
			EuclidianStatic.drawMultiLineText(view.getApplication(), text, 0,
					0, g2d, false, g2d.getFont());
		}

		return bimg;
	}

	protected GeoText geo;

	public void setGeo(GeoText geo) {
		this.geo = geo;
	}

	@Override
	protected void drawText(Renderer renderer) {

		// draw text
		super.drawText(renderer);

		if (geo.doHighlighting()) {
			// draw bounds if highlighted
			renderer.disableTextures();
			renderer.disableMultisample();
			renderer.setLineWidth(geo.getLineThickness() / 2);
			renderer.setColor(DrawText.HIGHLIGHT_COLOR);
			renderer.getGeometryManager().draw(highLightIndex);
			renderer.enableMultisample();
			renderer.enableTextures();
		}
	}

	private int highLightIndex = -1;

	@Override
	public void updatePosition(Renderer renderer) {

		super.updatePosition(renderer);

		if (origin == null) {
			renderer.getGeometryManager().remove(highLightIndex);
			highLightIndex = -1;
			return;
		}

		int old = highLightIndex;
		highLightIndex = renderer.getGeometryManager().rectangleBounds(drawX,
				drawY, drawZ, width / getFontScale(), height / getFontScale(),
				highLightIndex);
		renderer.getGeometryManager().remove(old);
		// App.debug("highLightIndex: "+highLightIndex);
	}

	@Override
	public void setWaitForReset() {
		super.setWaitForReset();

		highLightIndex = -1;
	}
}
