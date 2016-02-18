package org.geogebra.desktop.gui.util;

import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.desktop.awt.GGraphics2DD;

public class DropDownListD extends DropDownList implements ActionListener

{
	private Timer timClick;
	private Timer timScroll;

	public DropDownListD(DropDownListener listener) {
		super(listener);
		timClick = new Timer(clickDelay, this);
		timScroll = new Timer(scrollDelay, this);
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
		GGraphics2DD.getAwtGraphics(g2).fillPolygon(p);

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
		GGraphics2DD.getAwtGraphics(g2).fillPolygon(p);

	}

	@Override
	protected void runScrollTimer() {
		timScroll.start();
	}

	public void stopScrollTimer() {
		timScroll.stop();
	}


	public void setTimerDelay(int timerDelay) {
		timScroll.setDelay(timerDelay);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timClick) {
			doRunClick();
		} else {
			doScroll();
		}
	}

	@Override
	protected void runClickTimer() {
		timClick.start();
	}

	public void stopClickTimer() {
		timClick.stop();
	}

	public boolean isClickTimerRunning() {
		return timClick.isRunning();
	}

	public boolean isScrollTimerRunning() {
		return timScroll.isRunning();
	}
}
