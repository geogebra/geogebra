package com.himamis.retex.renderer.android.graphics;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class ScaleStack {

	private final List<Float> mScaleStackX;
	private final List<Float> mScaleStackY;

	public ScaleStack() {
		mScaleStackX = new ArrayList<>();
		mScaleStackX.add(1.0f);
		mScaleStackY = new ArrayList<>();
		mScaleStackY.add(1.0f);
	}

	public void appendScale(float sx, float sy) {
		float x = scaleX(sx);
		float y = scaleY(sy);
		mScaleStackX.set(getScaleStackXTopIndex(), x);
		mScaleStackY.set(getScaleStackYTopIndex(), y);
	}

	public float getScaleX() {
		return mScaleStackX.get(getScaleStackXTopIndex());
	}

	public float getScaleY() {
		return mScaleStackY.get(getScaleStackYTopIndex());
	}

	public void pushScaleValues() {
		mScaleStackX.add(getScaleX());
		mScaleStackY.add(getScaleY());
	}

	public void popScaleValues() {
		mScaleStackX.remove(getScaleStackXTopIndex());
		mScaleStackY.remove(getScaleStackYTopIndex());
	}

	private int getScaleStackXTopIndex() {
		return mScaleStackX.size() - 1;
	}

	private int getScaleStackYTopIndex() {
		return mScaleStackY.size() - 1;
	}

	public float scaleX(float x) {
		return x * getScaleX();
	}

	public float scaleY(float y) {
		return y * getScaleY();
	}

	public float scaleFontSize(float size) {
		return scaleByLargerScale(size);
	}

	public float scaleThickness(float thickness) {
		return scaleByLargerScale(thickness);
	}

	private float scaleByLargerScale(float value) {
		return Math.max(getScaleX(), getScaleY()) * value;
	}

	public RectF scaleRectF(RectF rect) {
		rect.bottom *= getScaleY();
		rect.top *= getScaleY();
		rect.left *= getScaleX();
		rect.right *= getScaleX();
		return rect;
	}

	public Bitmap scaleBitmap(Bitmap bitmap) {
		int scaledWidth = Math.round(scaleX(bitmap.getWidth()));
		int scaledHeight = Math.round(scaleY(bitmap.getHeight()));
		return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight,
				false);
	}
}
