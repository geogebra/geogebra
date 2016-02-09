package org.geogebra.desktop.gui.util;

import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.awt.GGraphics2DD;

public class DropDownListD implements DropDownList, ActionListener {
	private static final GColor FOCUS_COLOR = GColor.BLUE;
	private static final GColor NORMAL_COLOR = GColor.LIGHT_GRAY;
	private static final int MAX_WIDTH = 40;
	private Timer timClick;
	private Timer timDrag;
	private DropDownListener listener;
	private int clickDelay = 100;
	private int dragDelay = 100;
	private int mouseX = 0;
	private int mouseY = 0;

	public DropDownListD(DropDownListener listener) {
		this.listener = listener;
		timClick = new Timer(clickDelay, this);
		timDrag = new Timer(dragDelay, this);
	}
	public void drawSelected(GeoElement geo, GGraphics2D g2, GColor bgColor,
			int left, int top, int width, int height) {
		g2.setPaint(bgColor);
		g2.fillRect(left, top, width, height);

		// TF Rectangle
		g2.setPaint(geo.doHighlighting() ? FOCUS_COLOR : NORMAL_COLOR);
		g2.drawRect(left, top, width, height);

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
		GGraphics2DD.getAwtGraphics(g2).fillPolygon(p);

	}

	public void drawScrollUp(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {

	}

	public void drawScrollDown(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed) {

	}

	public void startScrollTimer(int x, int y) {
		timDrag.start();
	}

	public void stopScrollTimer() {
		timDrag.stop();
	}


	public void setTimerDelay(int timerDelay) {
		timDrag.setDelay(timerDelay);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timClick) {
			listener.onClick(mouseX, mouseY);
		} else {
			listener.onScroll(mouseX, mouseY);
		}
	}

	public void startClickTimer(int x, int y) {
		mouseX = x;
		mouseY = y;
		timClick.start();
	}

	public void stopClickTimer() {
		timClick.stop();
	}

	public boolean isClickTimerRunning() {
		return timClick.isRunning();
	}

	public boolean isScrollTimerRunning() {
		return timDrag.isRunning();
	}

}
