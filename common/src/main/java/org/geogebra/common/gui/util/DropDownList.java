package org.geogebra.common.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;

import com.google.j2objc.annotations.Weak;

public class DropDownList {

	protected static final int BOX_ROUND = 8;
	protected static final int MAX_WIDTH = 40;

	protected int scrollDelay = 100;
	protected int clickDelay = 500;

	private int mouseX = 0;
	private int mouseY = 0;
	private GTimer clickTimer;
	private GTimer scrollTimer;

	@Weak
	private DropDownListener listener;

	public interface DropDownListener {
		void onClick(int x, int y);

		void onScroll(int x, int y);
	}

	/**
	 * @param app
	 *            application
	 * @param listener
	 *            selection listener
	 */
	public DropDownList(App app, DropDownListener listener) {
		this.listener = listener;
		clickTimer = app.newTimer(new GTimerListener() {

			@Override
			public void onRun() {
				doRunClick();
			}
		}, clickDelay);

		scrollTimer = app.newTimer(new GTimerListener() {

			@Override
			public void onRun() {
				doScroll();
			}
		}, scrollDelay);
	}

	/**
	 * Run click listener.
	 */
	public void doRunClick() {
		listener.onClick(mouseX, mouseY);
	}

	/**
	 * Run scroll listener.
	 */
	public void doScroll() {
		listener.onScroll(mouseX, mouseY);
	}

	/**
	 * @param geo
	 *            list
	 * @param g2
	 *            graphics
	 * @param bgColor
	 *            background color
	 * @param left
	 *            left
	 * @param top
	 *            top
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void drawSelected(GeoElement geo, GGraphics2D g2, GColor bgColor,
			int left, int top, int width, int height) {
		g2.setPaint(bgColor);
		g2.fillRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

		// TF Rectangle
		g2.setPaint(geo.doHighlighting() ? GColor.BLUE : GColor.BLACK);
		g2.drawRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param left
	 *            left
	 * @param top
	 *            top
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void drawControl(GGraphics2D g2, int left, int top, int width,
			int height) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 4;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		AwtFactory.fillTriangle(g2, midx - tW, midy + tH, midx + tW, midy + tH,
				midx, midy + 2 * tW);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param left
	 *            left
	 * @param top
	 *            top
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void drawScrollUp(GGraphics2D g2, int left, int top, int width,
			int height) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 6;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		AwtFactory.fillTriangle(g2, midx - tW, midy + 2 * tW, midx + tW,
				midy + 2 * tW, midx, midy + tH);
	}

	/**
	 * @param g2
	 *            graphics
	 * @param left
	 *            left
	 * @param top
	 *            top
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void drawScrollDown(GGraphics2D g2, int left, int top, int width,
			int height) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 6;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		AwtFactory.fillTriangle(g2, midx - tW, midy + tH, midx + tW, midy + tH,
				midx, midy + 2 * tW);
	}

	private void setMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	/**
	 * Start click timer.
	 * 
	 * @param x
	 *            pointer x
	 * @param y
	 *            pointer y
	 */
	public void startClickTimer(int x, int y) {
		setMouse(x, y);
		// might be null eg Android, iOS
		if (clickTimer != null) {
			clickTimer.start();
		}
	}

	/**
	 * Start scroll timer.
	 * 
	 * @param x
	 *            pointer x
	 * @param y
	 *            pointer y
	 */
	public void startScrollTimer(int x, int y) {
		setMouse(x, y);
		if (scrollTimer != null) {
			scrollTimer.startRepeat();
		}
	}

	/**
	 * Stop click timer.
	 */
	public void stopClickTimer() {
		// might be null eg Android, iOS
		if (clickTimer != null) {
			clickTimer.stop();
		}
	}

	/**
	 * Stop scroll timer.
	 */
	public void stopScrollTimer() {
		// might be null eg Android, iOS
		if (scrollTimer != null) {
			scrollTimer.stop();
		}
	}

	/**
	 * @return whether click timer is running
	 */
	public boolean isClickTimerRunning() {
		// might be null eg Android, iOS
		if (clickTimer != null) {
			return clickTimer.isRunning();
		}

		return false;
	}

	/**
	 * @return whether scroll timer is running
	 */
	public boolean isScrollTimerRunning() {
		// might be null eg Android, iOS
		if (scrollTimer != null) {
			return scrollTimer.isRunning();
		}

		return false;
	}
}
