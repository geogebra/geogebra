package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Style.Unit;

import elemental2.dom.HTMLImageElement;

/**
 * (dummy) 3D view for browsers that don't support webGL
 * 
 * @author mathieu
 *
 */
public class EuclidianView3DWnoWebGL extends EuclidianView3DW {

	private GBufferedImage thumb;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView3DWnoWebGL(EuclidianController3D ec,
	        EuclidianSettings settings) {
		super(ec, settings);
		setCurrentFile(((AppW) ec.getApplication()).getCurrentFile());
	}

	@Override
	protected Renderer createRenderer() {
		return new RendererWnoWebGL(this);
	}

	@Override
	public void repaintView() {
		// repaint will be done only when resized
	}

	@Override
	public void repaint() {
		if (thumb != null) {
			getG2P().scale(
					(double) getG2P().getOffsetWidth() / thumb.getWidth(),
					(double) getG2P().getOffsetHeight() / thumb.getHeight());
			getG2P().drawImage(thumb, 0, 0);
		}

		getG2P().setColor(GColor.BLACK);
		if (!getApplication().isScreenshotGenerator()) {
			getG2P().drawString(
					getApplication().getLocalization().getMenu("NoWebGL"), 10,
					20);
		}
	}

	@Override
	public void setCurrentFile(GgbFile file) {
		if (file != null && file.get("geogebra_thumbnail.png") != null) {
			HTMLImageElement img = Dom.createImage();
			img.src = file.get("geogebra_thumbnail.png");
			thumb = new GBufferedImageW(img);
			img.addEventListener("load", (event) -> repaint());
			Log.debug("Set thumbnail done");
		}

		repaint();
	}

	/**
	 * Update the image size
	 */
	public void onResize() {
		getG2P().setCoordinateSpaceSize(this.getWidth(), this.getHeight());
		getG2P().getElement().getParentElement().getStyle()
				.setWidth(getG2P().getCoordinateSpaceWidth(), Unit.PX);
		getG2P().getElement().getParentElement().getStyle()
				.setHeight(getG2P().getCoordinateSpaceHeight(), Unit.PX);
	}

}
