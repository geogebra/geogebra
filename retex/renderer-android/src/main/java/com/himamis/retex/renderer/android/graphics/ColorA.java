package com.himamis.retex.renderer.android.graphics;

import com.himamis.retex.renderer.share.platform.graphics.Color;

public class ColorA implements Color {
	
	private int mColor;

	public ColorA(int color) {
		mColor = color;
	}
	
	public ColorA(int red, int green, int blue) {
		mColor = android.graphics.Color.rgb(red, green, blue);
	}

	public Object getNativeObject() {
		return new Integer(mColor);
	}

	public int getColor() {
		return mColor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mColor;
		return result;
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
