package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable class for Audio elemens.
 *
 * @author laszlo
 *
 */
public class DrawAudio extends Drawable {
	private static final int BLOB_SIZE = 6;
	private static final int TAP_AREA_SIZE = 48;
	private static final int TEXT_MARGIN_X = 4;
	private static final int TIME_FONT = 14;
	private static final int PLAY_MARGIN = 4;
	private static final int SLIDER_MARGIN = 8;
	private static final GColor BACKGROUND_COLOR = GColor.newColorRGB(0xf5f5f5);
	private static final GColor PLAY_COLOR = GColor.newColor(0, 0, 0, 138);

	// text-primary
	private static final GColor TIME_COLOR = GColor.newColor(0, 0, 0, 222);

	// mebis-teal
	private static final GColor PLAY_HOVER_COLOR = GColor.newColorRGB(0x00a8d5);

	private static final GColor BLOB_COLOR = GColor.newColorRGB(0x00a8d5);

	private final GeoAudio geoAudio;
	private int top;
	private int left;
	private int width;
	private int height;
	private boolean isVisible;
	private GRectangle bounds;
	private GRectangle playRect;
	private boolean hovered = false;
	private boolean playing = false;
	private int sliderLength = -1;
	private int diameter = 2 * GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE + 1;

	// for dot
	private GEllipse2DDouble circle = AwtFactory.getPrototype().newEllipse2DDouble();

	private double[] coords = new double[2];
	private int sliderLeft;

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
		updateSlider(view.getGraphicsForPen());
		playing = geoAudio.isPlaying();
	}

	private void updateSlider(GGraphics2D g2) {
		GFont font = view.getFont().deriveFont(GFont.PLAIN, TIME_FONT);
		g2.setFont(font);
		int duration = geoAudio.getDuration();
		String textAll = getElapsedTime(duration, duration);

		GTextLayout txtLayout = AwtFactory.getPrototype().newTextLayout(textAll, font, g2.getFontRenderContext());
		int x = left + TAP_AREA_SIZE + TEXT_MARGIN_X;

		double min = 0;
		double max = geoAudio.getDuration();
		double param = (geoAudio.getCurrentTime() - min) / (max - min);
		sliderLeft = (int) (x + txtLayout.getBounds().getWidth() + 2 * BLOB_SIZE);
		sliderLength = left + width - (sliderLeft + SLIDER_MARGIN + 2 * BLOB_SIZE);
		updateDot(sliderLeft + (sliderLength) * param, top + height / 2);

	}


	private void updateDot(double rwX, double rwY) {
		coords[0] = rwX;
		coords[1] = rwY;

		double xUL = (coords[0] - BLOB_SIZE);
		double yUL = (coords[1] - BLOB_SIZE);

		diameter = 2 * BLOB_SIZE + 1;

		circle.setFrame(xUL, yUL, diameter, diameter);
		Log.debug("[DOT] xUL: " + xUL + "  yUL: " + yUL + " diameter: " + diameter);
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawBox(g2);
		if (playing) {
			drawPause(g2);
		} else {
			drawPlay(g2);
		}
		drawTime(g2);
		updateStrokes(geoAudio);
		drawSlider(g2);
	}

	private void drawSlider(GGraphics2D g2) {
		if (isVisible) {
			int x = sliderLeft;
			int y = top + height / 2;

			g2.setPaint(geo.getSelColor());
			g2.drawStraightLine(x, y, x + sliderLength, y);

			g2.setPaint(BLOB_COLOR);
			g2.drawStraightLine(x, y, coords[0], y);

			// draw a dot
			g2.setPaint(BLOB_COLOR);
			g2.fill(circle);
		}
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

	private void drawPause(GGraphics2D g2) {
		g2.setColor(hovered ? PLAY_HOVER_COLOR : PLAY_COLOR);
		int size = getPlaySize();
		int margin = (height - size) / 2;
		int barWidth = size / 6;
		int x = left + margin + barWidth;
		int y = top + margin + PLAY_MARGIN;
		int x1 = x + 2 * barWidth;
		int barHeight = size - 2 * PLAY_MARGIN;
		g2.fillRect(x, y, barWidth, barHeight);
		g2.fillRect(x1, y, barWidth, barHeight);
	}

	private void drawBox(GGraphics2D g2) {
		g2.setPaint(BACKGROUND_COLOR);
		g2.fillRect(left - 1, top - 1, width, height);
	}

	private void drawTime(GGraphics2D g2) {
		GFont font = view.getFont().deriveFont(GFont.PLAIN, TIME_FONT);
		g2.setFont(font);
		g2.setPaint(TIME_COLOR);
		int duration = geoAudio.getDuration() / 1000;
		int currTime = geoAudio.getCurrentTime() / 1000;

		String text = getElapsedTime(currTime, duration);
		String textAll = getElapsedTime(duration, duration);

		GTextLayout txtLayout = AwtFactory.getPrototype().newTextLayout(textAll, font,
				g2.getFontRenderContext());
		int x = left + TAP_AREA_SIZE + TEXT_MARGIN_X;
		int y = top + (int) (height + txtLayout.getBounds().getHeight()) / 2;

		EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
				x, y, false, null, null);
	}

	private static String getElapsedTime(int current, int all) {
		StringBuilder sb = new StringBuilder();
		formatTime(sb, current);
		sb.append(" / ");
		formatTime(sb, all);
		return sb.toString();
	}
	private static void formatTime(StringBuilder sb, double secs) {
		if (secs < 0) {
			sb.append("-:-");
			return;
		}

		int hr = (int) Math.floor(secs / 3600);
		int min = (int) Math.floor((secs - (hr * 3600)) / 60);
		int sec = (int) Math.floor(secs - (hr * 3600) - (min * 60));

		String minStr = min + "";
		String secStr = sec + "";

		if (min < 10) {
			minStr = "0" + min;
		}
		if (sec < 10) {
			secStr = "0" + sec;
		}

		sb.append(minStr);
		sb.append(":");
		sb.append(secStr);
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
			playing = !playing;
			if (playing) {
				geoAudio.play();
			} else {
				geoAudio.pause();
			}
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
