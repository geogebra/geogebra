package com.himamis.retex.renderer.android.graphics;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import com.himamis.retex.renderer.android.font.FontA;
import com.himamis.retex.renderer.android.font.FontRenderContextA;
import com.himamis.retex.renderer.android.geom.Line2DA;
import com.himamis.retex.renderer.android.geom.Rectangle2DA;
import com.himamis.retex.renderer.android.geom.RoundRectangle2DA;
import com.himamis.retex.renderer.share.ColorUtil;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.RenderingHints;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

@SuppressLint("NewApi")
public class Graphics2DA implements Graphics2DInterface {

	private Canvas mCanvas;
	private View mView;

	private Paint mDrawPaint;
	private Paint mFontPaint;

	// The canvas is never scaled directly because with hardwareAcceleration:on
	// the drawing be pixelated. Instead the scale values are saved in a stack
	private ScaleStack mScaleStack;

	private FontA mFont;
	private ColorA mColor;
	private Style mOldDrawPaintStyle;

	public Graphics2DA() {
		mDrawPaint = new Paint();
		mDrawPaint.setStyle(Style.STROKE);
		mDrawPaint.setSubpixelText(true);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setLinearText(true);

		mFontPaint = new Paint();
		mFontPaint.set(mDrawPaint);

		mScaleStack = new ScaleStack();

		mFont = new FontA("Serif", Font.PLAIN, 10);

		mColor = (ColorA) ColorUtil.BLACK;
	}

	public Graphics2DA(Canvas canvas) {
		this();
		setCanvas(canvas);
	}

	public Graphics2DA(Canvas canvas, View view) {
		this(canvas);
		setView(view);
	}

	public void setCanvas(Canvas canvas) {
		mCanvas = canvas;
	}

	public void setView(View view) {
		mView = view;
	}

	private void setBasicStroke(BasicStrokeA basicStroke) {
		mDrawPaint.setStrokeWidth((float)basicStroke.getWidth());
		mDrawPaint.setStrokeMiter((float)basicStroke.getMiterLimit());
		mDrawPaint.setStrokeCap(basicStroke.getNativeCap());
		mDrawPaint.setStrokeJoin(basicStroke.getNativeJoin());
	}

	public Stroke getStroke() {
		return new BasicStrokeA(mDrawPaint.getStrokeWidth(), mDrawPaint.getStrokeMiter(),
				mDrawPaint.getStrokeCap(), mDrawPaint.getStrokeJoin());
	}

	public void setStroke(Stroke stroke) {
		BasicStrokeA basicStroke = (BasicStrokeA) stroke;
		setBasicStroke(basicStroke);
	}

	public Color getColor() {
		return mColor;
	}

	public void setColor(Color color) {
		mColor = (ColorA) color;
		mDrawPaint.setColor(mColor.getColor());
	}

	@SuppressWarnings("deprecation")
	public Transform getTransform() {
		Matrix matrix = null;
		if (mView != null && android.os.Build.VERSION.SDK_INT >= 11) {
			matrix = mView.getMatrix();
		}
		if (matrix == null) {
			matrix = mCanvas.getMatrix();
		}
		TransformA transform = new TransformA(matrix);
		transform.scale(mScaleStack.getScaleX(), mScaleStack.getScaleY());
		return transform;
	}

	public Font getFont() {
		return mFont;
	}

	public void setFont(Font font) {
		mFont = (FontA) font;
		mDrawPaint.setTypeface(mFont.getTypeface());
		mDrawPaint.setTextSize(mScaleStack.scaleFontSize(mFont.getSize()));
	}

	public void fillRect(int x, int y, int width, int height) {
		beforeFill();

		RectF rectF = new RectF(x, y, x + width, y + height);
		RectF scaled = mScaleStack.scaleRectF(rectF);
		RectF amended = AmendRect.amendRectF(scaled);

		mCanvas.drawRect(amended, mDrawPaint);

		afterFill();
	}

	@Override
	public void fill(Shape rectangle) {
		if (rectangle instanceof Rectangle2D) {
			beforeFill();
			draw((Rectangle2D) rectangle);
			afterFill();
		}
	}

	public void draw(Rectangle2D rectangle) {
		RectF rect = ((Rectangle2DA) rectangle).getRectF();
		RectF copy = new RectF(rect);
		RectF scaled = mScaleStack.scaleRectF(copy);
		RectF amended = AmendRect.amendRectF(scaled);

		mCanvas.drawRect(amended, mDrawPaint);
	}

	public void draw(RoundRectangle2D rectangle) {
		RectF rect = ((RoundRectangle2DA) rectangle).getRectF();
		RectF copy = new RectF(rect);

		mCanvas.drawRoundRect(mScaleStack.scaleRectF(copy), mScaleStack.scaleX((float) rectangle.getArcW()),
				mScaleStack.scaleY((float) rectangle.getArcH()), mDrawPaint);
	}

	public void draw(Line2D line) {
		Line2DA impl = (Line2DA) line;
		PointF start = impl.getStartPoint();
		PointF end = impl.getEndPoint();

		mCanvas.drawLine(mScaleStack.scaleX(start.x), mScaleStack.scaleY(start.y), mScaleStack.scaleX(end.x),
				mScaleStack.scaleY(end.y), mDrawPaint);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		beforeFill();

		mDrawPaint.setTextSize(mScaleStack.scaleFontSize(mFont.getSize()));
		mCanvas.drawText(data, offset, length, mScaleStack.scaleX(x), mScaleStack.scaleY(y), mDrawPaint);

		afterFill();
	}

	public void drawString(String text, int x, int y, Paint paint) {
		paint.setTextSize(mScaleStack.scaleFontSize(paint.getTextSize()));
		paint.setColor(mDrawPaint.getColor());
		mCanvas.drawText(text, mScaleStack.scaleX(x), mScaleStack.scaleY(y), paint);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		RectF oval = new RectF(x, y, (x + width), (y + height));

		mCanvas.drawArc(mScaleStack.scaleRectF(oval), startAngle, arcAngle, false, mDrawPaint);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		beforeFill();

		drawArc(x, y, width, height, startAngle, arcAngle);

		afterFill();
	}

	public void translate(double x, double y) {
		mCanvas.translate(mScaleStack.scaleX((float) x), mScaleStack.scaleY((float) y));
	}

	public void scale(double x, double y) {
		mScaleStack.appendScale((float) x, (float) y);
	}

	public void rotate(double theta, double x, double y) {
		translate(x, y);
		rotate(theta);
		translate(-x, -y);
	}

	public void rotate(double theta) {
		// theta is in radians
		// change to degrees
		float degrees = (float) Math.toDegrees(theta);
		mCanvas.rotate(degrees);
	}

	public void drawImage(Image image, int x, int y) {
		ImageA imageA = (ImageA) image;
		Bitmap bitmap = imageA.getBitmap();

		mCanvas.drawBitmap(mScaleStack.scaleBitmap(bitmap), mScaleStack.scaleX(x), mScaleStack.scaleY(y),
				mDrawPaint);

	}

	public void drawImage(Image image, Transform transform) {
		ImageA imageA = (ImageA) image;
		Bitmap bitmap = imageA.getBitmap();

		mCanvas.drawBitmap(mScaleStack.scaleBitmap(bitmap), (Matrix) transform, mDrawPaint);
	}

	public FontRenderContext getFontRenderContext() {
		mFontPaint.set(mDrawPaint);
		return new FontRenderContextA(mFontPaint);
	}

	public void setRenderingHint(int key, int value) {
		if (key == RenderingHints.KEY_ANTIALIASING && value == RenderingHints.VALUE_ANTIALIAS_ON) {
			mDrawPaint.setAntiAlias(true);
		} else {
			// No other rendering hint is supported
		}
	}

	public int getRenderingHint(int key) {
		// Not supported
		return -1;
	}

	public void dispose() {
		// NO-OP
	}

	private void saveDrawPaintStyle() {
		mOldDrawPaintStyle = mDrawPaint.getStyle();
	}

	private void restoreDrawPaintStyle() {
		mDrawPaint.setStyle(mOldDrawPaintStyle);
	}

	private void setDrawPaintFillStyle() {
		mDrawPaint.setStyle(Style.FILL);
	}

	private void beforeFill() {
		saveDrawPaintStyle();
		setDrawPaintFillStyle();
	}

	private void afterFill() {
		restoreDrawPaintStyle();
	}

	public void saveTransformation() {
		mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
		mScaleStack.pushScaleValues();
	}

	public void restoreTransformation() {
		mCanvas.restore();
		mScaleStack.popScaleValues();
	}

    @Override
    public void startDrawing() {

    }

    @Override
    public void moveTo(double x, double y) {

    }

    @Override
    public void lineTo(double x, double y) {

    }

    @Override
    public void quadraticCurveTo(double x, double y, double x1, double y1) {

    }

    @Override
    public void bezierCurveTo(double x, double y, double x1, double y1, double x2, double y2) {

    }

    @Override
    public void finishDrawing() {

    }
}
