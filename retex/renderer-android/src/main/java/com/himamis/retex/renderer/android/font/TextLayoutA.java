package com.himamis.retex.renderer.android.font;

import com.himamis.retex.renderer.android.geom.Rectangle2DA;
import com.himamis.retex.renderer.android.graphics.Graphics2DA;
import com.himamis.retex.renderer.share.platform.font.TextLayout;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class TextLayoutA implements TextLayout {

	private Paint mPaint;
	private String mString;

	private FontA mFont;

	public TextLayoutA(String string, FontA font, FontRenderContextA fontRenderContext) {
		mString = string;
		mFont = font;
		mPaint = fontRenderContext.getPaint();
	}

	public Rectangle2D getBounds() {
		updatePaint();
		Rect bounds = new Rect();

		mPaint.getTextBounds(mString, 0, mString.length(), bounds);

		return new Rectangle2DA(bounds);
	}

	public void draw(Graphics2DInterface graphics, int x, int y) {
		if (graphics instanceof Graphics2DA) {
			updatePaint();

			Graphics2DA g2d = (Graphics2DA) graphics;
			g2d.drawString(mString, x, y, mPaint);
		}
	}

	private void updatePaint() {
		mPaint.setTypeface(mFont.getTypeface());
		mPaint.setTextSize(mFont.getSize());
		mPaint.setStyle(Style.FILL);
	}
}
