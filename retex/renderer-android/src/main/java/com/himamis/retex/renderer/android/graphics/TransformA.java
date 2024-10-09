package com.himamis.retex.renderer.android.graphics;

import android.graphics.Matrix;

import com.himamis.retex.renderer.share.platform.graphics.Transform;

public class TransformA extends Matrix implements Transform {

	private float[] mValues;
	
	public TransformA() {
		mValues = new float[9];
	}

	public TransformA(Matrix matrix) {
		this();
		set(matrix);
	}
	
	public double getTranslateX() {
		getValues(mValues);
		return mValues[MTRANS_X];
	}

	public double getTranslateY() {
		getValues(mValues);
		return mValues[MTRANS_Y];
	}

	public double getScaleX() {
		getValues(mValues);
		return mValues[MSCALE_X];
	}

	public double getScaleY() {
		getValues(mValues);
		return mValues[MSCALE_Y];
	}

	public double getShearX() {
		getValues(mValues);
		return mValues[MSKEW_X];
	}

	public double getShearY() {
		getValues(mValues);
		return mValues[MSKEW_Y];
	}

	public Transform createClone() {
		return new TransformA(this);
	}

	public void scale(double sx, double sy) {
		setScale((float) sx, (float) sy);
	}

	public void translate(double tx, double ty) {
		setTranslate((float) tx, (float) ty);
	}

	public void shear(double sx, double sy) {
		setSkew((float) sx, (float) sy);
	}

}
