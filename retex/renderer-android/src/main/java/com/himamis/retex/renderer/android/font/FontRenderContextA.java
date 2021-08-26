package com.himamis.retex.renderer.android.font;

import com.himamis.retex.renderer.share.platform.font.Font;
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

	@Override
	public Font getFont() {
		// not used, web only
		return null;
	}
}
