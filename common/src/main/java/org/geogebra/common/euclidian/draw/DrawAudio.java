package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Drawable class for Audio elemens.
 *
 * @author laszlo
 *
 */
public class DrawAudio extends Drawable {
	private static final int TIME_FONT = 14;
	private static final int PLAY_MARGIN = 5;
	private static final GColor BACKGROUND_COLOR = GColor.newColorRGB(0xf5f5f5);
	private static final GColor PLAY_COLOR = GColor.newColor(0, 0, 0, 54);

	// text-primary
	private static final GColor TIME_COLOR = GColor.newColor(0, 0, 0, 87);

	// mebis-teal
	private static final GColor PLAY_HOVER_COLOR = GColor.newColorRGB(0x00a8d5);

	private final GeoAudio geoAudio;
	private int top;
	private int left;
	private int width;
	private int height;
	private boolean isVisible;
	private GRectangle bounds;
	private GRectangle playRect;
	private boolean hovered = false;

	/**
	 * @param view
	 *            The euclidian view.
	 * @param geo
	 *            The GeoElement that represents the audio content.
	 */
	public DrawAudio(EuclidianView view, GeoAudio geo) {
		this.view = view;
		this.geoAudio = geo;
		this.geo = geo;
		update();
	}

	private static int getPlaySize() {
		return 24;
	}

	@Override
	public void update() {
		isVisible = geoAudio.isDefined();
		if (!isVisible) {
			return;
		}
		left = geoAudio.labelOffsetX;
		top = geoAudio.labelOffsetY;

		width = geoAudio.getWidth();
		height = geoAudio.getHeight();
		int size = 2 * getPlaySize();
		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
		playRect = AwtFactory.getPrototype().newRectangle(left, top, size, size);
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawBox(g2);
		drawPlay(g2);
		drawTime(g2);
	}

	private void drawPlay(GGraphics2D g2) {
		g2.setColor(hovered ? PLAY_HOVER_COLOR : PLAY_COLOR);
		int size = getPlaySize();
		int margin = (height - size) / 2;
		int x = left + margin;
		int y = top + margin;
		int x1 = x + PLAY_MARGIN;
		int y1 = y + PLAY_MARGIN;
		int x2 = x1;
		int y2 = y + size - PLAY_MARGIN;
		int x3 = x + size - PLAY_MARGIN;
		int y3 = y + size / 2;

		AwtFactory.fillTriangle(g2, x1, y1, x2, y2, x3, y3);
	}

	private void drawBox(GGraphics2D g2) {
		g2.setPaint(BACKGROUND_COLOR);
		g2.fillRect(left - 1, top - 1, width, height);
	}

	private void drawTime(GGraphics2D g2) {
		GFont font = view.getFont().deriveFont(GFont.PLAIN, TIME_FONT);
		g2.setFont(font);
		g2.setPaint(TIME_COLOR);
		int x = left + 2 * getPlaySize();
		int y = top + geoAudio.getHeight() / 2 - TIME_FONT / 2;
		int duration = geoAudio.getDuration();
		int currTime = geoAudio.getCurrentTime();


		StringBuilder sb = new StringBuilder();
		if (currTime != -1) {
			sb.append(Integer.toString(currTime));
		} else {
			sb.append("-:-");
		}
		sb.append(" / ");

		if (duration != -1) {
			sb.append(duration);
		} else {
			sb.append("-:-");
		}

		EuclidianStatic.drawIndexedString(view.getApplication(), g2, sb.toString(),
				x, y, false, null, null);
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return bounds.contains(x, y) && isVisible;
	}

	/**
	 *
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 * @return true if play was hit.
	 */
	public boolean isPlayHit(int x, int y) {
		return playRect.contains(x, y) && isVisible;
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(bounds);
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GRectangle getBounds() {
		return bounds;
	}

	/**
	 * Mouse down handler.
	 *
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 * @return if was handled here or not.
	 */
	public boolean onMouseDown(int x, int y) {
		if (isPlayHit(x, y)) {
			geoAudio.play();
			return true;
		}
		return false;
	}

	/**
	 * Mouse over handler.
	 *
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	public void onMouseOver(int x, int y) {
		hovered = isPlayHit(x, y);
		view.repaintView();
	}

}
