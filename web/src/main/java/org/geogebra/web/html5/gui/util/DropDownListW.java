package org.geogebra.web.html5.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.ggbjdk.java.awt.geom.Polygon;

import com.google.gwt.user.client.Timer;

public class DropDownListW implements DropDownList {
	private static final int BOX_ROUND = 8;
	private static final GColor FOCUS_COLOR = GColor.BLUE;
	private static final GColor NORMAL_COLOR = GColor.LIGHT_GRAY;
	private static final int MAX_WIDTH = 40;
	private Timer timer;
	private int timerDelay = 100;
	private DropDownListener listener;

	public DropDownListW(DropDownListener listener) {
		this.listener = listener;
		timer = new Timer() {

			@Override
			public void run() {
				DropDownListW.this.listener.execTimer();
			}
		};
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

	public void startTimer() {
		timer.scheduleRepeating(timerDelay);
	}

	public void stopTimer() {
		timer.cancel();
	}

	public boolean isTimerRunning() {
		return timer.isRunning();
	}

	public void setTimerDelay(int timerDelay) {
		this.timerDelay = timerDelay;
	}


}
