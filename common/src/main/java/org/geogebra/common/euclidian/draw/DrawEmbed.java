package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.main.App.ExportType;

/**
 * Drawable for embedded apps
 */
public class DrawEmbed extends DrawWidget implements RemoveNeeded {

	private final GeoEmbed geoEmbed;
	private final EmbedManager embedManager;

	private MyImage preview;

	/**
	 * @param view
	 *            view
	 * @param geo
	 *            embedded applet
	 */
	public DrawEmbed(EuclidianView view, GeoEmbed geo) {
		super(view, geo, false);
		this.geoEmbed = geo;
		embedManager = view.getApplication().getEmbedManager();
		update();
		if (embedManager != null) {
			preview = embedManager.getPreview(this);
		}
	}

	@Override
	public void update() {
		getGeoElement().zoomIfNeeded();
		updateBounds();

		if (geoEmbed.getEmbedID() >= 0 && embedManager != null) {
			embedManager.add(this);
		}
		if (embedManager != null) {
			embedManager.update(this);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (view.getApplication().getExportType() == ExportType.NONE) {
			view.embed(g2, this);
			return;
		}

		g2.saveTransform();
		g2.transform(getTransform());
		int sx = (int) getWidth();
		int sy = (int) getHeight();

		g2.setColor(GColor.WHITE);
		g2.fillRect(0, 0, sx, sy);
		g2.setColor(GColor.BLACK);
		g2.drawRect(0, 0, sx, sy);

		int s = Math.min(sx, sy);
		int iconLeft = Math.max((sx - s) / 2, 0);
		int iconTop = Math.max((sy - s) / 2, 0);
		g2.drawImage(preview, iconLeft, iconTop, s, s);
		g2.restoreTransform();
	}

	@Override
	public GeoWidget getGeoElement() {
		return geoEmbed;
	}

	@Override
	public int getEmbedID() {
		return geoEmbed.getEmbedID();
	}

	@Override
	public boolean isBackground() {
		return geoEmbed.isBackground();
	}

	@Override
	public void setBackground(boolean b) {
		geoEmbed.setBackground(b);
	}

	/**
	 * @return embedded applet as geo
	 */
	public GeoEmbed getGeoEmbed() {
		return geoEmbed;
	}

	@Override
	public void remove() {
		if (embedManager != null) {
			embedManager.remove(this);
		}
	}
}
