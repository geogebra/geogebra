package org.geogebra.common.euclidian.draw.dropdown;

import static org.geogebra.common.euclidian.draw.dropdown.DrawOptions.MARGIN;
import static org.geogebra.common.euclidian.draw.dropdown.DrawOptions.ROUND;
import static org.geogebra.common.gui.util.DropDownList.MAX_WIDTH;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;

class ScrollArrows {

	private GRectangle up;
	private GRectangle down;
	private boolean clip;

	ScrollArrows() {
		up = AwtFactory.getPrototype().newRectangle();
		down = AwtFactory.getPrototype().newRectangle();
	}

	void update(int left, int top, int height, GDimension dimension) {
		up = AwtFactory.getPrototype().newRectangle(dimension.getWidth(),
				dimension.getHeight() / 2);
		down = AwtFactory.getPrototype()
				.newRectangle(dimension.getWidth(), dimension.getHeight() / 2);
		up.setBounds(left, top, (int) (up.getWidth()), (int) (up.getHeight()));
		down.setBounds(left, top + height
						- (int) (down.getHeight()),
				(int) (down.getWidth()),
				(int) (down.getHeight()));
	}

	void draw(GGraphics2D g2, GPaint backgroundColor) {
		int x = (int) up.getX();
		int y = (int) up.getY();
		int h = (int) up.getHeight();
		int w = (int) up.getWidth();
		if (y < MARGIN) {
			y = MARGIN;
		}

		g2.setPaint(backgroundColor);
		g2.fillRoundRect(x, y, w, h, ROUND, ROUND);
		drawUp(g2, x, y, w, h);

		int x2 = (int) down.getX();
		int y2 = (int) down.getY();
		int h2 = (int) down.getHeight();
		int w2 = (int) down.getWidth();

		g2.setPaint(backgroundColor);

		g2.fillRoundRect(x2, y2, w2, h2, ROUND, ROUND);
		drawDown(g2, x2, y2, w2, h2);
	}

	void drawUp(GGraphics2D g2, int left, int top, int width,
			int height) {
		g2.setColor(GColor.DARK_GRAY);

		int middleX = left + width / 2;

		int w = Math.min(width, MAX_WIDTH);
		int tW = w / 6;
		int tH = w / 6;

		int middleY = top + (height / 2 - (int) Math.round(tH * 1.5));

		int x1 = middleX - tW;
		int y1 = middleY + 2 * tW;
		int x2 = middleX + tW;
		int y3 = middleY + tH;
		AwtFactory.fillTriangle(g2, x1, y1, x2, y1, middleX, y3);
	}

	void drawDown(GGraphics2D g2, int left, int top, int width,
			int height) {
		g2.setColor(GColor.DARK_GRAY);
		int middleX = left + width / 2;

		int w = Math.min(width, MAX_WIDTH);
		int tW = w / 6;
		int tH = w / 6;

		int middleY = top + (height / 2 - (int) Math.round(tH * 1.5));

		int x1 = middleX - tW;
		int y1 = middleY + tH;
		int x2 = middleX + tW;
		int y3 = middleY + 2 * tW;
		AwtFactory.fillTriangle(g2, x1, y1, x2, y1, middleX, y3);
	}

	int getUpHeight() {
		return (int) up.getHeight();
	}

	int getUpBottom() {
		return (int) (up.getBounds().getY()
				+ up.getBounds().getHeight());
	}

	int getDownBottom() {
		return (int) (down.getBounds().getY()
				+ down.getBounds().getHeight());
	}

	boolean intersect(OptionItem item) {
		return item.intersects(up) || item.intersects(down);
	}

	int distanceBetween() {
		return (int) (getDownTop() - getUpBottom());
	}

	private double getDownTop() {
		return down.getBounds().getY();
	}

	boolean isUpHit(int x, int y) {
		return up.contains(x, y);
	}

	boolean isDownHit(int x, int y) {
		return down.contains(x, y);
	}

	boolean isHit(int x, int y) {
		return isUpHit(x, y) || isDownHit(x, y);
	}

	int getDownHeight() {
		return (int) down.getHeight();
	}

	boolean clip(GGraphics2D g2, OptionItem item, int dragOffset) {
		clip = dragOffset != 0 && intersect(item);
		if (clip) {
			g2.setClip(item.getLeft(), getUpBottom(), (int) item.getBoundsWidth(),
					distanceBetween(), true);
		}
		return clip;
	}

	boolean isClip() {
		return clip;
	}
}
