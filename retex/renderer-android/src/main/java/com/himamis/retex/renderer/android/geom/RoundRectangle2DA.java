package com.himamis.retex.renderer.android.geom;

import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;

import android.graphics.RectF;

public class RoundRectangle2DA implements RoundRectangle2D {

	private RectF mRect;
	private double mArcw;
	private double mArch;

	public RoundRectangle2DA(double x, double y, double w, double h, double arcw, double arch) {
		mRect = new RectF();
		setRectangle(x, y, w, h);
		mArcw = arcw;
		mArch = arch;
	}

	public void setRectangle(double x, double y, double w, double h) {
		float left = (float) x;
		float top = (float) y;
		float right = left + (float) w;
		float bottom = top + (float) h;
		mRect.set(left, top, right, bottom);
	}

	public double getArcW() {
		return mArcw;
	}

	public double getArcH() {
		return mArch;
	}

	public double getX() {
		return mRect.left;
	}

	public double getY() {
		return mRect.top;
	}

	public double getWidth() {
		return mRect.right - mRect.left;
	}

	public double getHeight() {
		return mRect.bottom - mRect.top;
	}

	public RectF getRectF() {
		return mRect;
	}

	public void setRoundRectangle(double x, double y, double w, double h, double arcw, double arch) {
		setRectangle(x, y, w, h);
		mArcw = arcw;
		mArch = arch;
	}

}
