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

	private final static int EMBED_SIZE_THRESHOLD = 100;
	private MyImage preview;

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            embedded applet
	 */
	public DrawEmbed(EuclidianView ev, GeoEmbed geo) {
		this.view = ev;
		this.geo = geo;
		this.geoEmbed = geo;
		embedManager = view.getApplication().getEmbedManager();
		update();
		if (embedManager != null) {
			preview = embedManager.getPreview(this);
		}
	}

	@Override
	public void update() {
		updateBounds();

		if (geoEmbed.getEmbedID() >= 0 && embedManager != null) {
			embedManager.add(this);
		}
		if (embedManager != null) {
			embedManager.update(this);
		}
	}

	@Override
	public double getWidthThreshold() {
		return EMBED_SIZE_THRESHOLD;
	}

	@Override
	public double getHeightThreshold() {
		return EMBED_SIZE_THRESHOLD;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (view.getApplication().getExportType() == ExportType.NONE) {
			view.embed(g2, this);
			return;
		}

		int sx = getWidth();
		int sy = getHeight();
		g2.setColor(GColor.WHITE);
		g2.fillRect(getLeft(), getTop(), sx, sy);
		g2.setColor(GColor.BLACK);
		g2.drawRect(getLeft(), getTop(), sx, sy);

		int s = Math.min(sx, sy);
		int iconLeft = getLeft() + Math.max((sx - s) / 2, 0);
		int iconTop = getTop() + Math.max((sy - s) / 2, 0);
		g2.drawImage(preview, iconLeft, iconTop, s, s);
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

	@Override
	public boolean isFixedRatio() {
		return false;
	}
}
