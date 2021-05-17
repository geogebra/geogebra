package com.himamis.retex.renderer.android.graphics;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class ColorA implements Color {
	
	private int mColor;

	public ColorA(int color) {
		mColor = color;
	}
	
	public ColorA(int red, int green, int blue, int alpha) {
		mColor = android.graphics.Color.argb(alpha, red, green, blue);
	}

	public Object getNativeObject() {
		return Integer.valueOf(mColor);
	}

	public int getColor() {
		return mColor;
	}

	@Override
	public int hashCode() {
		return mColor;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColorA other = (ColorA) obj;
		if (mColor != other.mColor)
			return false;
		return true;
	}

	
}
