package com.himamis.retex.renderer.android.geom;

import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;

import android.graphics.Rect;
import android.graphics.RectF;

public class Rectangle2DA implements Rectangle2D {

	private RectF mRect;

	public Rectangle2DA(Rect rect) {
		mRect = new RectF();
		mRect.set(rect);
	}

	public Rectangle2DA(double x, double y, double w, double h) {
		mRect = new RectF();
		setRectangle(x, y, w, h);
	}

	public void setRectangle(double x, double y, double w, double h) {
		float left = (float) x;
		float top = (float) y;
		float right = left + (float) w;
		float bottom = top + (float) h;
		mRect.set(left, top, right, bottom);
	}

	public RectF getRectF() {
		return mRect;
	}

	@Override
	public Rectangle2D getBounds2DX() {
		return this;
	}

	public double getX() {
		return mRect.left;
	}

	public double getY() {
		return mRect.top;
	}

	public double getWidth() {
		return mRect.width();
	}

	public double getHeight() {
		return mRect.height();
	}
}
