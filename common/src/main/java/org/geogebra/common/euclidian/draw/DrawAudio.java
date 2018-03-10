package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
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
	private static final int PLAY_MARGIN = 5;
	private static final int DEFAULT_PLAYER_WIDTH = 300;
	private static final int DEFAULT_PLAYER_HEIGHT = 25;
	private static final int PLAY_SIZE = DEFAULT_PLAYER_HEIGHT - 2 * PLAY_MARGIN;

	private final GeoAudio geoAudio;
	private int top;
	private int left;
	private int width;
	private int height;
	private boolean isVisible;
	private GRectangle bounds;
	private GRectangle playRect;

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

	@Override
	public void update() {
		isVisible = geoAudio.isDefined();
		if (!isVisible) {
			return;
		}
		left = geoAudio.labelOffsetX;
		top = geoAudio.labelOffsetY;

		width = DEFAULT_PLAYER_WIDTH;
		height = DEFAULT_PLAYER_HEIGHT;
		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
		playRect = AwtFactory.getPrototype().newRectangle(left + PLAY_MARGIN, top + PLAY_MARGIN, PLAY_SIZE, PLAY_SIZE);
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawBox(g2);
		drawPlay(g2, PLAY_SIZE, GColor.BLACK, false);
		drawTime(g2);
	}

	private void drawPlay(GGraphics2D g2, int size, GColor bgColor, boolean pressed) {
		g2.setColor(bgColor);
		int x = left + PLAY_MARGIN;
		int y = top + PLAY_MARGIN;
		AwtFactory.fillTriangle(g2, x, y, x, y + size, x + size, y + (size / 2));

	}


	private void drawBox(GGraphics2D g2) {
		g2.setPaint(GColor.LIGHT_GRAY);
		g2.fillRect(left - 1, top - 1, width, height);
	}

	private void drawTime(GGraphics2D g2) {
		g2.setPaint(GColor.DARK_GRAY);
		int x = left + PLAY_MARGIN + PLAY_SIZE + 2 * PLAY_MARGIN;
		int y = top + DEFAULT_PLAYER_HEIGHT - PLAY_MARGIN;
		int duration = geoAudio.getDuration();
		int currTime = geoAudio.getCurrentTime();
		
		StringBuilder sb = new StringBuilder();
		if (currTime != -1) {
			sb.append(currTime);
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

}
