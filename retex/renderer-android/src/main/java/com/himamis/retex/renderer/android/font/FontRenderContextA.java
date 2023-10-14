package com.himamis.retex.renderer.android.font;

import android.graphics.Paint;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;

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
