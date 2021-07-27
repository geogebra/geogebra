package com.himamis.retex.renderer.android.graphics;

import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;

import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

public class BasicStrokeA implements BasicStroke {

	private double mWidth;
	private double mMiterLimit;
	private int mCap;
	private int mJoin;
	
	public BasicStrokeA(double width, double miterLimit, Cap cap, Join join) {
		this(width, miterLimit, getCap(cap), getJoin(join));
	}

	public BasicStrokeA(double width, double miterLimit, int cap, int join) {
		mWidth = width;
		mMiterLimit = miterLimit;
		mCap = cap;
		mJoin = join;
	}

	public double getWidth() {
		return mWidth;
	}

	public double getMiterLimit() {
		return mMiterLimit;
	}

	public int getCap() {
		return mCap;
	}

	public int getJoin() {
		return mJoin;
	}

	public Object getNativeObject() {
		return null;
	}

	public Cap getNativeCap() {
		switch (mCap) {
		case CAP_BUTT:
			return Cap.BUTT;
		case CAP_ROUND:
			return Cap.ROUND;
		case CAP_SQUARE:
			return Cap.SQUARE;
		default:
			return Cap.BUTT;
		}
	}

	public Join getNativeJoin() {
		switch (mJoin) {
		case JOIN_BEVEL:
			return Join.BEVEL;
		case JOIN_MITER:
			return Join.MITER;
		case JOIN_ROUND:
			return Join.ROUND;
		default:
			return Join.BEVEL;
		}
	}

	private static int getCap(Cap cap) {
		switch (cap) {
		case BUTT:
			return CAP_BUTT;
		case ROUND:
			return CAP_ROUND;
		case SQUARE:
			return CAP_SQUARE;
		default:
			return CAP_BUTT;
		}
	}
	
	private static int getJoin(Join join) {
		switch (join) {
		case BEVEL:
			return JOIN_BEVEL;
		case MITER:
			return JOIN_MITER;
		case ROUND:
			return JOIN_ROUND;
		default:
			return JOIN_BEVEL;
		}
	}
}
