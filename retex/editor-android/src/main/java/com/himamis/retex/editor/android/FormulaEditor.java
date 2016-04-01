package com.himamis.retex.editor.android;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.himamis.retex.editor.android.event.ClickListenerAdapter;
import com.himamis.retex.editor.android.event.FocusListenerAdapter;
import com.himamis.retex.editor.android.event.KeyListenerAdapter;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.MetaModelParser;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.parser.Parser;
import com.himamis.retex.renderer.android.FactoryProviderAndroid;
import com.himamis.retex.renderer.android.graphics.ColorA;
import com.himamis.retex.renderer.android.graphics.Graphics2DA;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.Resource;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

public class FormulaEditor extends View implements MathField {

    protected static MetaModel sMetaModel;

    protected MathFieldInternal mMathFieldInternal;

    private TeXIcon mTeXIcon;
    private Graphics2DA mGraphics;

    private float mSize = 20;
    private int mBackgroundColor = Color.TRANSPARENT;
    private ColorA mForegroundColor = new ColorA(android.graphics.Color.BLACK);
    private int mType = TeXFormula.SERIF;
    private String mText;

    private float mScale;

    private float mMinHeight;

    private Parser mParser;

    public FormulaEditor(Context context) {
        super(context);
        init();
    }

    public FormulaEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs, 0);
        init();
    }

    public FormulaEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(context, attrs, defStyleAttr);
        init();
    }

    private void readAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FormulaEditor,
                defStyleAttr, 0);

        try {
            mSize = a.getFloat(R.styleable.FormulaEditor_fe_size, 20);
            mBackgroundColor = a.getColor(R.styleable.FormulaEditor_fe_backgroundColor, android.graphics.Color.TRANSPARENT);
            mForegroundColor = new ColorA(a.getColor(R.styleable.FormulaEditor_fe_foregroundColor, android.graphics.Color.BLACK));
            mText = a.getString(R.styleable.FormulaEditor_fe_text);
            mType = a.getInteger(R.styleable.FormulaEditor_fe_type, TeXFormula.SERIF);
        } finally {
            a.recycle();
        }
    }

    protected void init() {
        initFactoryProvider();
        initMetaModel();
        setFocusable(true);
        setFocusableInTouchMode(true);

        mScale = getResources().getDisplayMetrics().scaledDensity;

        mMathFieldInternal = new MathFieldInternal(this);
        mMathFieldInternal.setSize(mSize * mScale);
        mMathFieldInternal.setType(mType);
        if (!isInEditMode()) {
            mMathFieldInternal.setFormula(MathFormula.newFormula(sMetaModel));
        }
    }

    private float getMinHeigth() {
        if (mMinHeight == 0) {
            TeXIcon tempIcon = new TeXFormula("|").new TeXIconBuilder().setSize(mSize * mScale)
                    .setStyle(TeXConstants.STYLE_DISPLAY).build();
            tempIcon.setInsets(createInsetsFromPadding());
            mMinHeight = tempIcon.getIconHeight();
        }
        return mMinHeight;
    }

    private void initFactoryProvider() {
        if (FactoryProvider.INSTANCE == null) {
            FactoryProvider.INSTANCE = new FactoryProviderAndroid(getContext().getAssets());
        }
    }

    private void initMetaModel() {
        if (!isInEditMode()) {
            if (sMetaModel == null) {
                sMetaModel = new MetaModelParser().parse(new Resource().loadResource("Octave.xml"));
            }
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

    /**
     * Sets the text of the view. Must be called from the UI thread.
     *
     * @param text e.g. x^2
     */
    public void setText(Parser parser, String text) {
        mParser = parser;
        mText = text;
        createTeXFormula();
        requestLayout();
    }

    private void createTeXFormula() {
        mMathFieldInternal.setFormula(MathFormula.newFormula(sMetaModel, mParser, mText));
    }

    private Insets createInsetsFromPadding() {
        return new Insets(
                getPaddingTop(),
                getPaddingLeft(),
                getPaddingBottom(),
                getPaddingRight()
        );
    }

    @Override
    public void setTeXIcon(TeXIcon icon) {
        mTeXIcon = icon;
        mTeXIcon.setInsets(createInsetsFromPadding());
    }

    @Override
    public void setFocusListener(FocusListener focusListener) {
        setOnFocusChangeListener(new FocusListenerAdapter(focusListener));
    }

    @Override
    public void setClickListener(ClickListener clickListener) {
        setOnClickListener(new ClickListenerAdapter(clickListener));
    }

    @Override
    public void setKeyListener(KeyListener keyListener) {
        setOnKeyListener(new KeyListenerAdapter(keyListener));
    }

    @Override
    public void repaint() {
        invalidate();
    }

    @Override
    public boolean hasParent() {
        return getParent() != null;
    }

    public void requestViewFocus() {
        requestFocus();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        float newFontScale = newConfig.fontScale;
        if (Math.abs(mScale - newFontScale) > 0.001) {
            mScale = newConfig.fontScale;
            mMinHeight = 0;
            mMathFieldInternal.update();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int desiredWidth = mTeXIcon.getIconWidth();
        final int desiredHeight = (int) (Math.max(getMinHeigth(), mTeXIcon.getIconHeight()) + 0.5);

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
        if (mTeXIcon == null) {
            return;
        }

        if (mGraphics == null) {
            mGraphics = new Graphics2DA();
        }

        // draw background
        canvas.drawColor(mBackgroundColor);

        int x = Math.round((getMeasuredHeight() - mTeXIcon.getIconHeight()) / 2.0f);
        // draw latex
        mGraphics.setCanvas(canvas);
        mTeXIcon.setForeground(mForegroundColor);
        mTeXIcon.paintIcon(null, mGraphics, 0, x);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        BaseInputConnection fic = new BaseInputConnection(this, false);
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_NULL;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT;
        return fic;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onCheckIsTextEditor()) {
            super.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // show the keyboard so we can enter text
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            }
            return false;
        } else {
            // default behaviour
            return super.onTouchEvent(event);
        }
    }

    @Override
    public MetaModel getMetaModel() {
        return sMetaModel;
    }

    public InputController getInputController() {
        return mMathFieldInternal.getInputController();
    }

    public EditorState getEditorState() {
        return mMathFieldInternal.getEditorState();
    }
}
