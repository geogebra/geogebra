package com.himamis.retex.renderer.android.font;

import com.himamis.retex.renderer.share.platform.font.FontRenderContext;

import android.graphics.Paint;

public class FontRenderContextA implements FontRenderContext {
	
	private Paint mPaint;
	
	public FontRenderContextA(Paint paint) {
		mPaint = paint;
	}

	public Paint getPaint() {
		return mPaint;
	}

}
