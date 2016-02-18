package org.geogebra.common.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.geos.GeoElement;

public abstract class DropDownList {
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
	private DropDownListener listener;

	public DropDownList(DropDownListener listener) {
		this.listener = listener;
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

	public abstract void drawControl(GGraphics2D g2, int left, int top,
			int width, int height,
			GColor bgColor, boolean pressed);

	public abstract void drawScrollUp(GGraphics2D g2, int left, int top,
			int width,
 int height,
			GColor bgColor, boolean pressed);

	public abstract void drawScrollDown(GGraphics2D g2, int left, int top,
			int width,
			int height, GColor bgColor, boolean pressed);

	private void setMouse(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	protected abstract void runClickTimer();

	protected abstract void runScrollTimer();

	public void startClickTimer(int x, int y) {
		setMouse(x, y);
		runClickTimer();
	}

	public void startScrollTimer(int x, int y) {
		setMouse(x, y);
		runScrollTimer();
	}

	public abstract void stopClickTimer();

	public abstract void stopScrollTimer();

	public abstract boolean isClickTimerRunning();

	public abstract boolean isScrollTimerRunning();

	public abstract void setTimerDelay(int timerDelay);


}
