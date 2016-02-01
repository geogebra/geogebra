package org.geogebra.common.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.geos.GeoElement;

public interface DropDownList {
	public interface DropDownListener {
		void execTimer();
	}

	void drawSelected(GeoElement geo, GGraphics2D g2, GColor bgColor, int left,
			int top, int width, int height);

	void drawControl(GGraphics2D g2, int left, int top, int width, int height,
			GColor bgColor, boolean pressed);

	void drawScrollUp(GGraphics2D g2, int left, int top, int width,
 int height,
			GColor bgColor, boolean pressed);

	void drawScrollDown(GGraphics2D g2, int left, int top, int width,
			int height, GColor bgColor, boolean pressed);
		
	void startTimer();

	void stopTimer();

	boolean isTimerRunning();

	void setTimerDelay(int timerDelay);


}
