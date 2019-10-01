package com.himamis.retex.editor.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.InputType;
import android.util.AttributeSet;
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
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathComponent;
import com.himamis.retex.editor.share.model.MathContainer;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.parser.Parser;
import com.himamis.retex.renderer.android.FactoryProviderAndroid;
import com.himamis.retex.renderer.android.graphics.ColorA;
import com.himamis.retex.renderer.android.graphics.Graphics2DA;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings({"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass", "OverlyCoupledClass"})
public class FormulaEditor extends View implements MathField {

    private final static int CURSOR_MARGIN = 5;
    // tolerance for cursor color
    private final static int CURSOR_TOLERANCE = 10;
    private static final int DEFAULT_SIZE = 20;
    private static final int PRE_INIT_VALUE = -1;
    private static final int NO_BACKGROUND = -2;

    public static MetaModel sMetaModel = new MetaModel();
    protected MathFieldInternal mMathFieldInternal;
    protected float mScale;
    private TeXIcon mTeXIcon;
    private Graphics2DA mGraphics;
    private float mSize = DEFAULT_SIZE;
    private float mMinWidth;
    private int mBackgroundColor = Color.TRANSPARENT;
    private ColorA mForegroundColor = new ColorA(Color.BLACK);
    private int mType = TeXFormula.SERIF;
    private String mText;
    private float mMinHeight;
    private Parser mParser;
    private int mIconWidth;
    private Canvas mHiddenCanvas;
    private Bitmap mHiddenBitmap;
    private int mHiddenBitmapW = -1;
    private int mHiddenBitmapH = -1;
    private int mShiftX = 0;

    private TeXIcon mFormulaPreviewTeXIcon;

    public FormulaEditor(Context context) {
        super(context);
        initFormulaEditor();
    }

    public FormulaEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        beforeStyling();
        readAttributes(context, attrs, 0);
        initFormulaEditor();
    }

    public FormulaEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        beforeStyling();
        readAttributes(context, attrs, defStyleAttr);
        initFormulaEditor();
    }

    public void debug(@SuppressWarnings("unused") String message) {
        //System.out.println(message);
    }

    @Override
    public boolean useCustomPaste() {
        return false;
    }

    private void beforeStyling() {
        mSize = PRE_INIT_VALUE;
        mMinWidth = PRE_INIT_VALUE;
        mBackgroundColor = PRE_INIT_VALUE;
        mForegroundColor = null;
        mText = null;
        mType = PRE_INIT_VALUE;
    }

    protected void readAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FormulaEditor,
                defStyleAttr, 0);

        try {
            initSize(a);
            initMinWidth(a);
            initBackgroundColor(a);
            initForegroundColor(a);
            initText(a);
            initType(a);
        } finally {
            a.recycle();
        }
    }

    private void initSize(TypedArray typedArray) {
        float size = typedArray.getFloat(R.styleable.FormulaEditor_fe_size, PRE_INIT_VALUE);
        if (size != PRE_INIT_VALUE) {
            mSize = size;
        } else if (mSize == PRE_INIT_VALUE) {
            mSize = DEFAULT_SIZE;
        }
    }

    private void initMinWidth(TypedArray typedArray) {
        float minWidth = typedArray.getDimension(R.styleable.FormulaEditor_fe_minWidth, PRE_INIT_VALUE);
        if (minWidth != PRE_INIT_VALUE) {
            mMinWidth = minWidth;
        } else if (mMinWidth == PRE_INIT_VALUE) {
            mMinWidth = 0;
        }
    }

    private void initBackgroundColor(TypedArray typedArray) {
        int backgroundColor = typedArray.getColor(R.styleable.FormulaEditor_fe_backgroundColor, PRE_INIT_VALUE);
        if (backgroundColor != PRE_INIT_VALUE) {
            mBackgroundColor = backgroundColor;
        } else if (mBackgroundColor == PRE_INIT_VALUE) {
            mBackgroundColor = NO_BACKGROUND;
        }
    }

    private void initForegroundColor(TypedArray typedArray) {
        int foregroundColor = typedArray.getColor(R.styleable.FormulaEditor_fe_foregroundColor, PRE_INIT_VALUE);
        if (foregroundColor != PRE_INIT_VALUE) {
            mForegroundColor = new ColorA(foregroundColor);
        } else if (mForegroundColor == null) {
            mForegroundColor = new ColorA(Color.BLACK);
        }
    }

    private void initText(TypedArray typedArray) {
        String text = typedArray.getString(R.styleable.FormulaEditor_fe_text);
        if (text != null) {
            mText = text;
        }
    }

    private void initType(TypedArray typedArray) {
        int type = typedArray.getInteger(R.styleable.FormulaEditor_fe_type, PRE_INIT_VALUE);
        if (type != PRE_INIT_VALUE) {
            mType = type;
        } else if (mType == PRE_INIT_VALUE) {
            mType = TeXFormula.SANSSERIF;
        }
    }

    protected void initFormulaEditor() {
        initFactoryProvider();
        setFocusable(true);
        setFocusableInTouchMode(true);

        mScale = getResources().getDisplayMetrics().scaledDensity;

        mMathFieldInternal = new MathFieldInternal(this);
        mMathFieldInternal.setSize(mSize * mScale);
        mMathFieldInternal.setType(mType);
        mMathFieldInternal.setFormula(MathFormula.newFormula(sMetaModel));
    }

    protected float getMinHeight() {
        if (mMinHeight == 0) {
            TeXIcon tempIcon = new TeXFormula("|").new TeXIconBuilder().setSize(mSize * mScale)
                    .setStyle(TeXConstants.STYLE_DISPLAY).build();
            tempIcon.setInsets(createInsetsFromPadding());
            mMinHeight = tempIcon.getIconHeight();
        }
        return mMinHeight;
    }

    private void initFactoryProvider() {
        if (FactoryProvider.getInstance() == null) {
            FactoryProvider.setInstance(new FactoryProviderAndroid(getContext().getAssets()));
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

    public void setSize(float size) {
        mSize = size;
        mMathFieldInternal.setSize(mSize * mScale);
        mMathFieldInternal.update();
    }

    public void setPreviewText(String text) {
        if (text != null) {
            TeXFormula texFormula = new TeXFormula(text);
            mFormulaPreviewTeXIcon = texFormula.new TeXIconBuilder().setSize(mSize * mScale).setType(mType)
                    .setStyle(TeXConstants.STYLE_DISPLAY).build();
            mFormulaPreviewTeXIcon.setInsets(createInsetsFromPadding());
        } else {
            mFormulaPreviewTeXIcon = null;
        }
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
        updateShiftX();
    }

    @Override
    public void setFocusListener(FocusListener focusListener) {
        setOnFocusChangeListener(new FocusListenerAdapter(focusListener));
    }

    @Override
    public void setClickListener(ClickListener clickListener) {
        setOnTouchListener(new ClickListenerAdapter(clickListener, getContext()));
    }

    @Override
    public void setKeyListener(KeyListener keyListener) {
        setOnKeyListener(new KeyListenerAdapter(keyListener, this));
    }

    public void onEnter() {
        // used in AlgebraInput
    }

    public void afterKeyTyped(KeyEvent keyEvent) {
        // used in FormulaInput
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

    protected int getWidthForIconWithPadding() {
        if (hasFocus() || mFormulaPreviewTeXIcon == null) {
            return mTeXIcon.getIconWidth();
        }
        return mFormulaPreviewTeXIcon.getIconWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int desiredWidth = Math.max(getWidthForIconWithPadding(), Math.round(mMinWidth));
        final int desiredHeight = (int) (Math.max(getMinHeight(), mTeXIcon.getIconHeight()) + 0.5);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        int height;
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
        if (hasFocus() || mFormulaPreviewTeXIcon == null) {
            drawShifted(canvas, mShiftX);
        } else {
            drawShiftedWithTexIcon(canvas, mShiftX, mFormulaPreviewTeXIcon);
        }
    }

    protected void drawShifted(Canvas canvas, int shiftX) {
        drawShiftedWithTexIcon(canvas, shiftX, mTeXIcon);
    }

    protected void drawShiftedWithTexIcon(Canvas canvas, int shiftX, TeXIcon teXIcon) {
        if (teXIcon == null) {
            return;
        }

        if (mGraphics == null) {
            mGraphics = new Graphics2DA();
        }

        if (mBackgroundColor != NO_BACKGROUND) {
            // draw background
            canvas.drawColor(mBackgroundColor);
        }

        //noinspection MagicNumber
        int y = Math.round((getMeasuredHeight() - teXIcon.getIconHeight()) / 2.0f);
        // draw latex
        mGraphics.setCanvas(canvas);
        teXIcon.setForeground(mForegroundColor);
        teXIcon.paintIcon(null, mGraphics, shiftX, y);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            updateShiftX();
        }
    }

    public void fireInputChangedEvent() {
        // implemented in AlgebraInput
    }

    @Override
    public void paste() {

    }

    @Override
    public void copy() {

    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    protected int calcCursorX() {

        int inputBarWidth = getWidth();

        if (inputBarWidth == 0) {
            debug("updateShiftX: inputBarWidth == 0");
            return -1;
        }

        debug("mShiftX: " + mShiftX + ", inputBarWidth:" + inputBarWidth);

        mIconWidth = mTeXIcon.getIconWidth();
        int iconHeight = mTeXIcon.getIconHeight();

        // check if last shift is not too long
        // (e.g. if new formula is shorter)
        if (mIconWidth + mShiftX < inputBarWidth) {
            mShiftX = inputBarWidth - mIconWidth;
            if (mShiftX > 0) {
                mShiftX = 0;
            }
            debug("shorter formula: mShiftX = " + mShiftX);
        }

        // find cursor (red pixels) and ensure is visible in view
        if (mIconWidth > mHiddenBitmapW || iconHeight > mHiddenBitmapH) {
            mHiddenBitmap = Bitmap.createBitmap(mIconWidth, iconHeight,
                    Bitmap.Config.ARGB_8888);
            mHiddenCanvas = new Canvas(mHiddenBitmap);
            mHiddenBitmapW = mIconWidth;
            mHiddenBitmapH = iconHeight;
            debug("==== new Bitmap");
        } else {
            mHiddenCanvas.drawColor(Color.BLACK);
        }
        drawShifted(mHiddenCanvas, 0);
        int pixRed = 0;
        int cursorRed = 0;
        try {
	        int[] pix = new int[mIconWidth * iconHeight];
	        mHiddenBitmap.getPixels(pix, 0, mIconWidth, 0, 0, mIconWidth, iconHeight);
	        int index = 0;
	        for (int y = 0; y < iconHeight; y++) {
	            for (int x = 0; x < mIconWidth; x++) {
	                int color = pix[index];
	                int red = Color.red(color);
	                if (red > GraphicsFactory.CURSOR_RED - CURSOR_TOLERANCE
	                        && red < GraphicsFactory.CURSOR_RED + CURSOR_TOLERANCE) {
	                    int green = Color.green(color);
	                    if (green > GraphicsFactory.CURSOR_GREEN - CURSOR_TOLERANCE
	                            && green < GraphicsFactory.CURSOR_GREEN + CURSOR_TOLERANCE) {
	                        int blue = Color.blue(color);
	                        if (blue > GraphicsFactory.CURSOR_BLUE - CURSOR_TOLERANCE
	                                && blue < GraphicsFactory.CURSOR_BLUE + CURSOR_TOLERANCE) {
	                            pixRed++;
	                            cursorRed += x;
	                        }
	                    }
	                }
	                index++;
	            }
	        }
        } catch (java.lang.OutOfMemoryError e) {
        	debug("problem allocating array");
        	return -1;
        }

        // if no red pixel, no cursor: do nothing
        if (pixRed == 0) {
            return -1;
        }

        return cursorRed / pixRed;

    }

    protected void updateShiftX() {

        int cursorX = calcCursorX();
        if (cursorX < 0) {
            return;
        }

        int inputBarWidth = getWidth();

        debug("cursorX: " + cursorX);
        int margin = (int) (CURSOR_MARGIN * mScale);
        if (cursorX - margin + mShiftX < 0) {
            mShiftX = -cursorX + margin;
        } else if (cursorX + margin + mShiftX > inputBarWidth) {
            mShiftX = inputBarWidth - cursorX - margin;
        }
        debug("mShiftX: " + mShiftX);
    }

    public void scroll(int dx, int dy) {
        if (isEmpty()) {
            return;
        }

        mShiftX -= dx;
        int inputBarWidth = getWidth();
        if (mIconWidth + mShiftX < inputBarWidth) {
            mShiftX = inputBarWidth - mIconWidth;
        }
        if (mShiftX > 0) {
            mShiftX = 0;
        }

        repaint();
    }

    /**
     * @return current shift in x for drawing the formula
     */
    public int getShiftX() {
        return mShiftX;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        BaseInputConnection fic = new BaseInputConnection(this, false);
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_NULL;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return fic;
    }

    public boolean showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }
        return true;
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    public void showCopyPasteButtons() {
        // not implemented here
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

    public MathFieldInternal getMathFieldInternal() {
        return mMathFieldInternal;
    }

    public ArrayList<Integer> getCurrentPath(MathComponent component) {
        ArrayList<Integer> currentPath = new ArrayList<>();
        getPath(component, currentPath);
        Collections.reverse(currentPath);
        return currentPath;
    }

    private void getPath(MathComponent component, ArrayList<Integer> path) {

        MathContainer container = component.getParent();
        if (container != null) {
            int index = container.indexOf(component);
            path.add(index);
            getPath(container, path);
        }
    }

    private MathSequence getCurrentField(MathSequence root, ArrayList<Integer> currentPath) {
        MathContainer child = root;
        for (int i : currentPath) {
            child = (MathContainer) child.getArgument(i);
        }
        return (MathSequence) child;
    }

    public void hideCopyPasteButtons() {
        // implemented in ReTeXInput
    }

    public boolean isEmpty() {
        return mMathFieldInternal.isEmpty();
    }

    public void setFormula(int currentOffset, ArrayList<Integer> currentPath, MathSequence rootComponent) {
        // Set the formula
        MathFormula mathFormula = MathFormula.newFormula(sMetaModel);
        mathFormula.setRootComponent(rootComponent);
        mMathFieldInternal.setFormula(mathFormula);

        // Change the editor state
        EditorState editorState = getEditorState();
        editorState.setRootComponent(rootComponent);
        editorState.setCurrentField(getCurrentField(rootComponent, currentPath));
        editorState.setCurrentOffset(currentOffset);
    }

    protected boolean hasPreview() {
        return mFormulaPreviewTeXIcon != null;
    }

    public float getTextSize() {
        return mSize;
    }
    
    @Override
    public void tab(boolean shiftDown){

    }
}
