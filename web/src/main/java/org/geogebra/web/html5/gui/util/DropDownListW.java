package org.geogebra.web.html5.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Polygon;

import com.google.gwt.user.client.Timer;

public class DropDownListW extends DropDownList {
	private Timer timScroll;
	private Timer timClick;


	public DropDownListW(DropDownListener listener) {
		super(listener);
		timClick = new Timer() {

			@Override
			public void run() {
				doRunClick();
			}
		};

		timScroll = new Timer() {

			@Override
			public void run() {
				doScroll();
			}
		};
	}

	public void drawControl(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {
		g2.setColor(GColor.DARK_GRAY);

		int midx = left + width / 2;

		int w = width < MAX_WIDTH ? width : MAX_WIDTH;
		int tW = w / 4;
		int tH = w / 6;

		int midy = top + (height / 2 - (int) Math.round(tH * 1.5));

		Polygon p = new Polygon();
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

		Polygon p = new Polygon();
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

		Polygon p = new Polygon();
		p.addPoint(midx - tW, midy + tH);
		p.addPoint(midx + tW, midy + tH);
		p.addPoint(midx, midy + 2 * tW);
		g2.fill(p);

	}

	protected void runClickTimer() {
		timClick.schedule(clickDelay);
	}

	protected void runScrollTimer() {
		timClick.cancel();
		timScroll.scheduleRepeating(scrollDelay);
	}

	public void stopClickTimer() {
		timClick.cancel();
		Log.debug("[COMBOSCROLLING] CLICK CANCELED");
	}

	public boolean isClickTimerRunning() {
		return timClick.isRunning();
	}


	public void stopScrollTimer() {
		timScroll.cancel();
	}

	public boolean isScrollTimerRunning() {
		return timScroll.isRunning();
	}

	public void setTimerDelay(int timerDelay) {
		this.scrollDelay = timerDelay;
	}


}
