package org.geogebra.common.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPolygon;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimer.GTimerListener;

public class DropDownList {
	public interface DropDownListener {
		void onClick(int x, int y);

		void onScroll(int x, int y);
	}

	protected static final int BOX_ROUND = 8;
	protected static final GColor FOCUS_COLOR = GColor.BLUE;
	protected static final GColor NORMAL_COLOR = GColor.LIGHT_GRAY;
	protected static final int MAX_WIDTH = 40;

	protected int scrollDelay = 100;
	protected int clickDelay = 500;

	private int mouseX = 0;
	private int mouseY = 0;
	private GTimer clickTimer;
	private GTimer scrollTimer;

	private DropDownListener listener;
	private App app;

	public DropDownList(App app, DropDownListener listener) {
		this.listener = listener;
		this.app = app;
		clickTimer = app.newTimer(new GTimerListener() {

			public void onRun() {
				doRunClick();
			}
		}, clickDelay);

		scrollTimer = app.newTimer(new GTimerListener() {

			public void onRun() {
				doScroll();
			}
		}, scrollDelay);
	}

	public void doRunClick() {
		listener.onClick(mouseX, mouseY);
	}

	public void doScroll() {
		listener.onScroll(mouseX, mouseY);
	}

	public void drawSelected(GeoElement geo, GGraphics2D g2, GColor bgColor,
			int left, int top, int width, int height) {
		g2.setPaint(bgColor);
		g2.fillRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

		// TF Rectangle
		g2.setPaint(geo.doHighlighting() ? FOCUS_COLOR : NORMAL_COLOR);
		g2.drawRoundRect(left, top, width, height, BOX_ROUND, BOX_ROUND);

	}

	public void drawControl(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 4;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		GPolygon p = AwtFactory.prototype.newPolygon();
		p.addPoint(midx - tW, midy + tH);
		p.addPoint(midx + tW, midy + tH);
		p.addPoint(midx, midy + 2 * tW);
		g2.fill(p);

	}

	public void drawScrollUp(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 6;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		GPolygon p = AwtFactory.prototype.newPolygon();
		p.addPoint(midx - tW, midy + 2 * tW);
		p.addPoint(midx + tW, midy + 2 * tW);
		p.addPoint(midx, midy + tH);
		g2.fill(p);

	}

	public void drawScrollDown(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 6;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		GPolygon p = AwtFactory.prototype.newPolygon();
		p.addPoint(midx - tW, midy + tH);
		p.addPoint(midx + tW, midy + tH);
		p.addPoint(midx, midy + 2 * tW);
		g2.fill(p);

	}

	private void setMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	public void startClickTimer(int x, int y) {
		setMouse(x, y);
		clickTimer.start();
	}

	public void startScrollTimer(int x, int y) {
		setMouse(x, y);
		scrollTimer.startRepeat();
	}

	public void stopClickTimer() {
		clickTimer.stop();
	}

	public void stopScrollTimer() {
		scrollTimer.stop();
	}

	public boolean isClickTimerRunning() {
		return clickTimer.isRunning();
	}

	public boolean isScrollTimerRunning() {
		return scrollTimer.isRunning();
	}

}
