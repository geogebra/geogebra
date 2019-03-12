package com.himamis.retex.renderer.android;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.himamis.retex.renderer.android.graphics.ColorA;
import com.himamis.retex.renderer.android.graphics.Graphics2DA;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LaTeXView extends View {

    private ExecutorService mServicePool = Executors.newSingleThreadExecutor();

    private Future<?> mTexIconBuilderFuture;

    volatile private TeXFormula mFormula;
    volatile private TeXFormula.TeXIconBuilder mTexIconBuilder;
    volatile private TeXIcon mTexIcon;

    private Graphics2DA mGraphics;

    volatile private String mLatexText = "";
    volatile private float mSize = 20;
    volatile private int mStyle = TeXConstants.STYLE_DISPLAY;
    volatile private int mType = TeXFormula.SERIF;

    private int mBackgroundColor = android.graphics.Color.TRANSPARENT;
    private Color mForegroundColor = new ColorA(android.graphics.Color.BLACK);

    private float mScreenDensity;
    private float mSizeScale;

    private TexIconCreatorRunnable mTexIconBuilderRunnable = new TexIconCreatorRunnable();

    private Runnable mCleanFormula = new Runnable() {
        @Override
        public void run() {
            mFormula = null;
            mTexIconBuilder = null;
            mTexIcon = null;
        }
    };

    private Runnable mCleanTexIcon = new Runnable() {
        @Override
        public void run() {
            mTexIcon = null;
        }
    };

    private Runnable mRequestLayout = new RequestLayoutAction(this);

    public LaTeXView(Context context) {
        super(context);
        mScreenDensity = context.getResources().getDisplayMetrics().density;
        mSizeScale = context.getResources().getDisplayMetrics().scaledDensity;
        initFactoryProvider();
        ensureTeXIconExists();
    }

    public LaTeXView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenDensity = context.getResources().getDisplayMetrics().density;
        mSizeScale = context.getResources().getDisplayMetrics().scaledDensity;
        initFactoryProvider();
        readAttributes(context, attrs, 0);
    }

    public LaTeXView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenDensity = context.getResources().getDisplayMetrics().density;
        mSizeScale = context.getResources().getDisplayMetrics().scaledDensity;
        initFactoryProvider();
        readAttributes(context, attrs, defStyleAttr);
    }

    private void initFactoryProvider() {
        if (FactoryProvider.getInstance() == null) {
            FactoryProvider.setInstance(new FactoryProviderAndroid(getContext().getAssets()));
        }
    }

    private void readAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.LaTeXView,
                defStyleAttr, 0);

        try {
            mLatexText = a.getString(R.styleable.LaTeXView_lv_latexText);
            mSize = a.getFloat(R.styleable.LaTeXView_lv_size, 20);
            mStyle = a.getInteger(R.styleable.LaTeXView_lv_style, 0);
            mType = a.getInteger(R.styleable.LaTeXView_lv_type, TeXFormula.SERIF);
            mBackgroundColor = a.getColor(R.styleable.LaTeXView_lv_backgroundColor, android.graphics.Color.TRANSPARENT);
            mForegroundColor = new ColorA(a.getColor(R.styleable.LaTeXView_lv_foregroundColor, android.graphics.Color.BLACK));
        } finally {
            a.recycle();
        }
        ensureTeXIconExists();
    }

    /**
     * Sets the LaTeX text of this view. Must be called from the UI thread.
     *
     * @param latexText formula in LaTeX format
     */
    public void setLatexText(String latexText) {
        mLatexText = latexText;
        cleanFormula();
        ensureTeXIconExists();
        invalidate();
        requestLayout();
    }

    /**
     * Sets the size of the text. Must be called from the UI thread.
     *
     * @param size size
     */
    public void setSize(float size) {
        if (Math.abs(mSize - size) > 0.01) {
            mSize = size;
            cleanTexIcon();
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    /**
     * Sets the style of the LaTeX. Must be called from the UI thread.
     *
     * @param style one of {@link TeXConstants#STYLE_TEXT} , {@link TeXConstants#STYLE_DISPLAY},
     *              {@link TeXConstants#STYLE_SCRIPT} or {@link TeXConstants#STYLE_SCRIPT_SCRIPT}
     */
    public void setStyle(int style) {
        if (mStyle != style) {
            mStyle = style;
            cleanTexIcon();
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    /**
     * Sets the type of the LaTeX view. Must be called from the UI thread.
     *
     * @param type one of {@link TeXFormula#SERIF}, {@link TeXFormula#SANSSERIF},
     *             {@link TeXFormula#BOLD}, {@link TeXFormula#ITALIC}, {@link TeXFormula#ROMAN}
     */
    public void setType(int type) {
        if (mType != type) {
            mType = type;
            cleanTexIcon();
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    /**
     * Sets the color of the text. Must be called from the UI thread.
     *
     * @param foregroundColor color represented as packed ints
     */
    public void setForegroundColor(int foregroundColor) {
        mForegroundColor = new ColorA(foregroundColor);
        invalidate();
    }

    /**
     * Sets the color of the background. Must be called from the UI thread.
     *
     * @param backgroundColor color represented as packed ints
     */
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        invalidate();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        float newSizeScale = mScreenDensity * newConfig.fontScale;
        if (Math.abs(mSizeScale - newSizeScale) > 0.001) {
            mSizeScale = newSizeScale;
            cleanTexIcon();
            ensureTeXIconExists();
            invalidate();
            requestLayout();
        }
    }

    private void ensureTeXIconExists() {
        createTexIcon();
    }

    private int getIconWidth() {
        TeXIcon teXIcon = mTexIcon;
        if (teXIcon == null) {
            return 0;
        }
        return teXIcon.getIconWidth();
    }

    private int getIconHeight() {
        TeXIcon teXIcon = mTexIcon;
        if (teXIcon == null) {
            return 0;
        }
        return teXIcon.getIconHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int desiredWidth = getIconWidth();
        final int desiredHeight = getIconHeight();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TeXIcon teXIcon = mTexIcon;
        if (teXIcon == null) {
            return;
        }

        if (mGraphics == null) {
            mGraphics = new Graphics2DA();
        }
        // draw background
        canvas.drawColor(mBackgroundColor);

        // draw latex
        mGraphics.setCanvas(canvas);
        teXIcon.setForeground(mForegroundColor);
        teXIcon.paintIcon(null, mGraphics, 0, 0);
    }

    class TexIconCreatorRunnable implements Runnable {

        @Override
        public void run() {
            if (mFormula == null) {
                try {
                    mFormula = new TeXFormula(mLatexText);
                } catch (ParseException exception) {
                    mFormula = TeXFormula.getPartialTeXFormula(mLatexText);
                }
            }
            if (mTexIconBuilder == null) {
                mTexIconBuilder = mFormula.new TeXIconBuilder();
            }
            if (mTexIcon == null) {
                mTexIconBuilder.setSize(mSize * mSizeScale).setStyle(mStyle).setType(mType);
                mTexIcon = mTexIconBuilder.build();
            }
            mTexIcon.setInsets(new Insets(
                    getPaddingTop(),
                    getPaddingLeft(),
                    getPaddingBottom(),
                    getPaddingRight()
            ));
            invalidateView();
            requestViewLayout();
        }
    }

    private void invalidateView() {
        postInvalidate();
    }

    private void requestViewLayout() {
        post(mRequestLayout);
    }

    /**
     * Static Runnable for wrapping the View.requestLayout() action by using a WeakReference to the
     * LaTeXView.
     * Solution to memory leak issues occurring on devices running on API < 24
     */
    private static class RequestLayoutAction implements Runnable {
        private WeakReference<View> latexView;

        RequestLayoutAction(View view) {
            latexView = new WeakReference<>(view);
        }

        @Override
        public void run() {
            View view = latexView.get();
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    private void cleanFormula() {
        mServicePool.submit(mCleanFormula);
    }

    private void cleanTexIcon() {
        mServicePool.submit(mCleanTexIcon);
    }

    private void createTexIcon() {
        cancelFuture();

        mTexIconBuilderFuture = mServicePool.submit(mTexIconBuilderRunnable);
    }

    private void cancelFuture() {
        if (mTexIconBuilderFuture != null) {
            mTexIconBuilderFuture.cancel(true);
            mTexIconBuilderFuture = null;
        }
    }
}
