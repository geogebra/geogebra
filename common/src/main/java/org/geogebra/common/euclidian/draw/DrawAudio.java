package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Drawable class for Audio elemens.
 *
 * @author laszlo
 *
 */
public class DrawAudio extends DrawWidget {

	private static final int SLIDER_AREA_WIDTH = 4;
	private static final int BLOB_RADIUS = 8;
	private static final int INNER_BLOB_RADIUS = 6;
	private static final int TAP_AREA_SIZE = 48;
	private static final int MARGIN_BEFORE_TEXT_X = 8;
	private static final int MARGIN_AFTER_TEXT_X = 16;
	private static final int TIME_FONT = 14;

	private static final int PLAY_SIZE = 16;
	private static final int PLAY_LEFT_PADDING = 14;

	private static final int SLIDER_MARGIN = 8;
	private static final int SLIDER_THICKNESS = 4;
	private static final GColor BACKGROUND_COLOR = GColor.MOW_WIDGET_BACKGROUND;
	private static final GColor PLAY_COLOR = GColor.TEXT_PRIMARY;
	private static final GColor TIME_COLOR = GColor.TEXT_PRIMARY;
	private static final GColor SLIDER_STROKE_COLOR = GColor.TEXT_PRIMARY;
	private static final GBasicStroke SLIDER_STROKE = EuclidianStatic.getStroke(SLIDER_THICKNESS,
			EuclidianStyleConstants.LINE_TYPE_FULL);

	private final GeoAudio geoAudio;

	private boolean isVisible;
	private GRectangle playRect;
	private boolean playHovered = false;
	private boolean playing = false;
	private int sliderWidth = -1;
	private int diameter = 2 * GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE + 1;
	private GLine2D line = AwtFactory.getPrototype().newLine2D();

	// for dot
	private GEllipse2DDouble circle = AwtFactory.getPrototype().newEllipse2DDouble();
	private GEllipse2DDouble circleOuter = AwtFactory.getPrototype().newEllipse2DDouble();

	private double[] coords = new double[2];
	private int sliderLeft;
	private int duration;
	private boolean sliderHighlighted = false;

	private GColor playHoverColor = GColor.MOW_MEBIS_TEAL;
	private GColor blobColor = GColor.MOW_MEBIS_TEAL;

	/**
	 * @param view
	 *            The euclidian view.
	 * @param geo
	 *            The GeoElement that represents the audio content.
	 */
	public DrawAudio(EuclidianView view, GeoAudio geo) {
		super(view, geo, false);
		this.geoAudio = geo;
		getColors();
		update();
	}

	private void getColors() {
		App app = geo.getKernel().getApplication();
		playHoverColor = app.getPrimaryColor();
		blobColor = app.getPrimaryColor();
	}

	@Override
	public void update() {
		updateBounds();
		isVisible = geo.isEuclidianVisible() && geoAudio.isDefined();
		if (!isVisible) {
			return;
		}

		int size = 2 * PLAY_SIZE;
		int margin = (int) (getHeight() / 2 - PLAY_SIZE);
		playRect = AwtFactory.getPrototype().newRectangle(0, margin,
				size + PLAY_LEFT_PADDING, size);

		// NPE in Classic 5 on file loading
		try {
			updateSlider(view.getGraphicsForPen());
		} catch (Exception e) {
			e.printStackTrace();
		}
		playing = geoAudio.isPlaying();
	}

	private void updateSlider(GGraphics2D g2) {
		GFont font = view.getApplication().getFontCommon(false, GFont.PLAIN, TIME_FONT);
		g2.setFont(font);
		duration = geoAudio.getDuration();
		String textAll = getElapsedTime(duration, duration);
		GTextLayout txtLayout = AwtFactory.getPrototype().newTextLayout(textAll,
				font, g2.getFontRenderContext());
		int x = TAP_AREA_SIZE + MARGIN_BEFORE_TEXT_X;

		double d = geoAudio.getDuration();
		double param = geoAudio.getCurrentTime() / d;
		if (!MyDouble.isFinite(param) || param < 0) {
			param = 0;
		}
		sliderLeft = (int) (x + txtLayout.getBounds().getWidth()
				+ 2 * BLOB_RADIUS) + MARGIN_AFTER_TEXT_X;
		sliderWidth = (int) (getWidth() - (sliderLeft + SLIDER_MARGIN + 2 * BLOB_RADIUS));
		int middle = (int) (getHeight() / 2);

		updateDot(sliderLeft + sliderWidth * param, middle);
		AwtFactory.getPrototype().newRectangle(sliderLeft, middle - SLIDER_AREA_WIDTH,
				sliderLeft, 2 * SLIDER_AREA_WIDTH);
		line.setLine(sliderLeft, middle, sliderLeft + sliderWidth, middle);
	}

	private void updateDot(double rwX, double rwY) {
		coords[0] = rwX;
		coords[1] = rwY;

		double xUL = (coords[0] - BLOB_RADIUS);
		double yUL = (coords[1] - BLOB_RADIUS);

		double ixUL = (coords[0] - INNER_BLOB_RADIUS);
		double iyUL = (coords[1] - INNER_BLOB_RADIUS);

		diameter = 2 * BLOB_RADIUS + 1;
		int innerDiameter = 2 * INNER_BLOB_RADIUS + 1;

		int hightlightDiameter = 2 * BLOB_RADIUS + 1;
		circle.setFrame(ixUL, iyUL, innerDiameter, innerDiameter);
		// selection area
		circleOuter.setFrame(xUL, yUL, hightlightDiameter, hightlightDiameter);
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (isVisible) {
			g2.saveTransform();
			g2.transform(getTransform());
			drawBox(g2);
			if (playing) {
				drawPause(g2);
			} else {
				drawPlay(g2);
			}
			drawTime(g2);
			updateStrokes(geoAudio);
			drawSlider(g2);
			g2.restoreTransform();
		}
	}

	private void drawSlider(GGraphics2D g2) {
		int x = sliderLeft;
		int y = (int) (getHeight() / 2);

		g2.setStroke(SLIDER_STROKE);
		g2.setPaint(SLIDER_STROKE_COLOR);
		g2.drawStraightLine(x, y, x + sliderWidth, y);

		g2.setPaint(blobColor);
		g2.drawStraightLine(x, y, coords[0], y);

		if (sliderHighlighted) {
			g2.fill(circleOuter);
		} else {
			g2.fill(circle);
		}
	}

	private void drawPlay(GGraphics2D g2) {
		g2.setColor(playHovered ? playHoverColor : PLAY_COLOR);
		int size = PLAY_SIZE;
		int margin = (int) ((getHeight() - size) / 2);
		int x1 = PLAY_LEFT_PADDING;
		int y1 = margin;
		int x2 = x1;
		int y2 = margin + size;
		int x3 = PLAY_LEFT_PADDING + size;
		int y3 = margin + size / 2;

		AwtFactory.fillTriangle(g2, x1, y1, x2, y2, x3, y3);
	}

	private void drawPause(GGraphics2D g2) {
		g2.setColor(playHovered ? playHoverColor : PLAY_COLOR);
		int size = PLAY_SIZE;
		int margin = (int) ((getHeight() - size) / 2);
		int barWidth = size / 4;

		int x = PLAY_LEFT_PADDING;
		int x1 = x + 2 * barWidth;

		g2.fillRect(x, margin, barWidth, size);
		g2.fillRect(x1, margin, barWidth, size);
	}

	private void drawBox(GGraphics2D g2) {
		g2.setPaint(BACKGROUND_COLOR);
		g2.fillRect(0, 0, (int) getWidth(), (int) getHeight());
	}

	private void drawTime(GGraphics2D g2) {
		GFont font = view.getApplication().getFontCommon(false, GFont.PLAIN, TIME_FONT);
		g2.setFont(font);
		g2.setPaint(TIME_COLOR);
		duration = geoAudio.getDuration() / 1000;
		int currTime = geoAudio.getCurrentTime() / 1000;

		String text = getElapsedTime(currTime, duration);
		String textAll = getElapsedTime(duration, duration);

		GTextLayout txtLayout = AwtFactory.getPrototype().newTextLayout(textAll, font,
				g2.getFontRenderContext());
		int x = TAP_AREA_SIZE + MARGIN_BEFORE_TEXT_X;
		int y = (int) (getHeight() + txtLayout.getBounds().getHeight()) / 2;

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
		GPoint2D inversePoint = getInversePoint(x, y);

		boolean sh = isSliderHit(inversePoint.getX(), inversePoint.getY(), 2);
		boolean ph = isPlayHit(inversePoint.getX(), inversePoint.getY());
		boolean repaint = sh != sliderHighlighted || ph != playHovered;
		sliderHighlighted = sh;
		playHovered = ph;
		if (repaint) {
			view.repaintView();
		}
		return super.hit(x, y, hitThreshold);
	}

	/**
	 *
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 * @return true if play was hit.
	 */
	public boolean isPlayHit(double x, double y) {
		return playRect.contains(x, y);
	}

	/**
	 * Returns true iff the movable point was hit
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param hitThreshold
	 *            threshold
	 * @return true iff the movable point was hit
	 */
	final public boolean isBlobHit(int x, int y, int hitThreshold) {
		int r = hitThreshold + Math.max(diameter, GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE);
		double dx = coords[0] - x;
		double dy = coords[1] - y;
		return dx < r && dx > -r && dx * dx + dy * dy <= r * r;
	}

	/**
	 * Returns true if the slider line was hit, false for fixed sliders
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            threshold
	 * @return true if the slider line was hit, false for fixed sliders
	 */
	public boolean isSliderHit(double x, double y, int hitThreshold) {
		int r = hitThreshold + SLIDER_THICKNESS;
		return line.intersects(x - r, y - r, 2 * r, 2 * r);
	}

	/**
	 * Returns true if the slider line was hit, false for fixed sliders
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @param hitThreshold
	 *            threshold
	 * @return true if the slider line was hit, but not the blob
	 */
	public boolean hitSliderNotBlob(int x, int y, int hitThreshold) {
		return isSliderHit(x, y, hitThreshold) && !isBlobHit(x, y, hitThreshold);
	}

	@Override
	public GeoWidget getGeoElement() {
		return geoAudio;
	}

	@Override
	public int getEmbedID() {
		return 0;
	}

	@Override
	public boolean isBackground() {
		return false;
	}

	@Override
	public void setBackground(boolean b) {
		// background state not possible for audios
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
		GPoint2D inversePoint = getInversePoint(x, y);
		if (isPlayHit(inversePoint.getX(), inversePoint.getY())) {
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
	 * @return left of the slider.
	 */
	public int getSliderLeft() {
		return sliderLeft;
	}

	/**
	 * 
	 * @return the width of the time slider.
	 */
	public double getSliderWidth() {
		return sliderWidth;
	}

}
