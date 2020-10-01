/*
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.himamis.retex.editor.web;

import java.util.ArrayList;

import org.gwtproject.timer.client.Timer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldAsync;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.ScreenReaderSerializer;
import com.himamis.retex.editor.share.util.GWTKeycodes;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.KeyCodes;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.JlmLib;
import com.himamis.retex.renderer.web.graphics.ColorW;

public class MathFieldW implements MathField, IsWidget, MathFieldAsync, BlurHandler {

	public static final int SCROLL_THRESHOLD = 14;
	protected static MetaModel sMetaModel = new MetaModel();
	private MetaModel metaModel;

	private MathFieldInternal mathFieldInternal;
	private Canvas html;
	private Context2d ctx;
	private Panel parent;
	private boolean focused = false;
	private TeXIcon lastIcon;
	private double ratio = 1;
	private KeyListener keyListener;
	private boolean rightAltDown = false;
	private boolean leftAltDown = false;
	private boolean enabled = true;
	private static Timer tick;
	private BlurHandler onTextfieldBlur;
	private FocusHandler onTextfieldFocus;
	private Timer focuser;
	private boolean pasteInstalled = false;

	private int bottomOffset;
	private MyTextArea inputTextArea;
	private SimplePanel clip;

	private double scale = 1.0;

	private SyntaxAdapter converter;

	private ExpressionReader expressionReader;

	private static ArrayList<MathFieldW> instances = new ArrayList<>();
	// can't be merged with instances.size because we sometimes remove an
	// instance
	private static int counter = 0;
	private String foregroundCssColor = "#000000";
	private String backgroundCssColor = "#ffffff";
	private ChangeHandler changeHandler;
	private int fixMargin = 0;
	private int minHeight = 0;
	private boolean wasPaintedWithCursor;

	/**
	 * @param converter
	 *            latex/mathml-&lt; ascii math converter (optional)
	 * @param parent
	 *            parent element
	 * @param canvas
	 *            drawing context
	 * @param listener
	 *            listener for special events
	 * @param directFormulaBuilder
	 *            whether to convert content into JLM atoms directly without
	 *            reparsing
	 */
	public MathFieldW(SyntaxAdapter converter, Panel parent, Canvas canvas,
					  MathFieldListener listener, boolean directFormulaBuilder) {
		this(converter, parent, canvas, listener, directFormulaBuilder, sMetaModel);
	}

	/**
	 *
	 * @param converter
	 *            latex/mathml-&lt; ascii math converter (optional)
	 * @param parent
	 *            parent element
	 * @param canvas
	 *            drawing context
	 * @param listener
	 *            listener for special events
	 * @param directFormulaBuilder
	 *            whether to convert content into JLM atoms directly without
	 *            reparsing
	 * @param metaModel
	 *            model
	 */
	public MathFieldW(SyntaxAdapter converter, Panel parent, Canvas canvas,
			MathFieldListener listener, boolean directFormulaBuilder, MetaModel metaModel) {

		this.converter = converter;
		this.metaModel = metaModel;
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}
		html = canvas;
		bottomOffset = 10;
		this.parent = parent;
		mathFieldInternal = new MathFieldInternal(this, directFormulaBuilder);
		mathFieldInternal.getInputController().setFormatConverter(converter);
		getHiddenTextArea();

		// el.getElement().setTabIndex(1);
		if (canvas != null) {
			this.ctx = canvas.getContext2d();
		}
		SelectionBox.touchSelection = false;
		mathFieldInternal.setSelectionMode(true);
		mathFieldInternal.setFieldListener(listener);
		mathFieldInternal.setType(TeXFont.SANSSERIF);
		mathFieldInternal.setFormula(MathFormula.newFormula(sMetaModel));
		initTimer();
		instances.add(this);
		if (canvas == null) {
			return;
		}
		canvas.addDomHandler(event -> {
			if (!isEnabled()) {
				return;
			}
			event.stopPropagation();
			// prevent default to keep focus; also avoid dragging the whole
			// editor
			event.preventDefault();
			setFocus(true);
			setRightAltDown(false);
			setLeftAltDown(false);

		}, MouseDownEvent.getType());

		setKeyListener(inputTextArea, keyListener);
	}

	/**
	 * Enables or disables line break in the editor.
	 */
	public void setLineBreakEnabled(boolean b) {
		mathFieldInternal.getMathFieldController().setLineBreakEnabled(b);
	}

	/**
	 * @param label
	 *            label for assistive technology
	 */
	public void setAriaLabel(String label) {
		Element target = getElementForAriaLabel();
		if (target != null) {
			target.setAttribute("aria-label", label);
		}
	}

	private Element getElementForAriaLabel() {
		if ((isIOS() || isMacOS() || isIE())) {
			// mobile Safari: alttext is connected to parent so that screen
			// reader doesn't read "dimmed" for the textarea
			Element parentElement = parent.getElement();
			if (!"textbox".equals(parentElement.getAttribute("role"))) {
				parentElement.setAttribute("aria-live", "assertive");
				parentElement.setAttribute("aria-atomic", "true");
				parentElement.setAttribute("role", "textbox");
			}

			return parentElement;
		}
		if (inputTextArea != null) {
			return inputTextArea.getElement();
		}
		return null;
	}

	/**
	 * @return aria label
	 */
	public String getAriaLabel() {
		Element target = getElementForAriaLabel();
		if (target != null) {
			return target.getAttribute("aria-label");
		}
		return "";
	}

	/**
	 * @return whether we're running in a Mac browser
	 */
	public static boolean isMacOS() {
		return Navigator.getUserAgent().contains("Macintosh")
				|| Navigator.getUserAgent().contains("Mac OS");
	}

	/**
	 * @return whether we are running in IE
	 */
	public static boolean isIE() {
		return Navigator.getUserAgent().toLowerCase().contains("trident")
				|| Navigator.getUserAgent().toLowerCase().contains("msie");
	}

	private static void initTimer() {
		if (tick == null) {
			tick = new Timer() {

				@Override
				public void run() {
					CursorBox.toggleBlink();
					for (MathFieldW field : instances) {
						field.keepFocus();
						field.repaintWeb();
					}
				}
			};
			tick.scheduleRepeating(500);
		}

	}

	/**
	 * @return whether the field can repaint and accept events
	 */
	protected boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param flag
	 *            whether the field can repaint and accept events
	 */
	public void setEnabled(boolean flag) {
		this.enabled = flag;
		if (parent != null && clip != null) {
			parent.add(clip);
		}
		if (!flag) {
			setFocus(false);
		}
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {
		this.lastIcon = icon;

		double height = computeHeight();
		if (ctx == null || height < 0) {
			return;
		}
		ctx.getCanvas().getStyle().setHeight(height, Unit.PX);

		ctx.getCanvas().getStyle().setWidth(computeWidth(),
				Unit.PX);
		parent.setHeight(height + "px");
		parent.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		repaintWeb();
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		// addFocusListener(new FocusListenerAdapterW(focusListener));
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		ClickAdapterW adapter = new ClickAdapterW(clickListener, this);
		adapter.listenTo(html);
	}

	public void setPixelRatio(double ratio) {
		this.ratio = ratio;
	}

	@Override
	public void setKeyListener(final KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	private void setKeyListener(final Widget html2,
			final KeyListener keyListener) {
		html2.getElement().setAttribute("role", "application");
		html2.addDomHandler(event -> {
			// don't kill Ctrl+V or write V
			if (controlDown(event)
					&& (event.getCharCode() == 'v'
							|| event.getCharCode() == 'V')
					|| isLeftAltDown()) {

				event.stopPropagation();
			} else {
				keyListener.onKeyTyped(
						new KeyEvent(event.getNativeEvent().getKeyCode(), 0,
								getChar(event.getNativeEvent())));
				event.stopPropagation();
				event.preventDefault();
			}

		}, KeyPressEvent.getType());
		html2.addDomHandler(event -> {
			if (checkPowerKeyInput(html2.getElement())) {
				keyListener.onKeyTyped(new KeyEvent(0, 0, '^'));
				onFocusTimer(); // refocus to remove the half-written letter
				updateAltForKeyUp(event);
				event.preventDefault();
				return;
			}
			int code = convertToJavaKeyCode(event.getNativeEvent());
			keyListener.onKeyReleased(new KeyEvent(code,
					getModifiers(event), getChar(event.getNativeEvent())));
			updateAltForKeyUp(event);

			// YES WE REALLY DO want JavaKeyCodes not GWTKeycodes here
			if (code == JavaKeyCodes.VK_DELETE
					|| code == JavaKeyCodes.VK_ESCAPE) {
				event.preventDefault();
			}
		}, KeyUpEvent.getType());
		html2.addDomHandler(event -> {
			if (isRightAlt(event.getNativeEvent())) {
				setRightAltDown(true);
			}
			if (isLeftAlt(event.getNativeEvent())) {
				setLeftAltDown(true);
			}

			int code = convertToJavaKeyCode(event.getNativeEvent());
			boolean handled = keyListener.onKeyPressed(new KeyEvent(code,
					getModifiers(event), getChar(event.getNativeEvent())));
			// YES WE REALLY DO want JavaKeyCodes not GWTKeycodes here
			if (code == JavaKeyCodes.VK_LEFT
					|| code == JavaKeyCodes.VK_RIGHT) {
				readPosition();
			}
			// need to prevent default for arrows to kill keypress
			// (otherwise strange chars appear in Firefox). Backspace/delete
			// also need killing.
			// also kill events while left alt down: alt+e, alt+d working in
			// browser
			// YES WE REALLY DO want JavaKeyCodes not GWTKeycodes here
			if (code == JavaKeyCodes.VK_DELETE
					|| code == JavaKeyCodes.VK_ESCAPE || handled
					|| isLeftAltDown()) {
				event.preventDefault();
			}
			event.stopPropagation();

		}, KeyDownEvent.getType());
	}

	/** Read position in current */
	public void readPosition() {
		if (expressionReader != null) {
			setAriaLabel(this.mathFieldInternal.getEditorState()
					.getDescription(expressionReader));
		} else {
			FactoryProvider.debugS("no reader");
		}
	}

	/**
	 * @param expressionReader
	 *            expression reader
	 */
	public void setExpressionReader(ExpressionReader expressionReader) {
		this.expressionReader = expressionReader;
	}

	/**
	 * Update alt flags after key released
	 * 
	 * @param event
	 *            keyUp event
	 */
	protected void updateAltForKeyUp(KeyUpEvent event) {
		if (isRightAlt(event.getNativeEvent())) {
			setRightAltDown(false);
		}
		if (isLeftAlt(event.getNativeEvent())) {
			setLeftAltDown(false);
		}
		event.stopPropagation();
	}

	native boolean checkPowerKeyInput(Element element) /*-{
		if (element.value.match(/\^$/)) {
			element.value = '';
			return true;
		}
		return false;
	}-*/;

	/**
	 * @param nativeEvent
	 *            native event
	 * @return whether this is right alt up/down event
	 */
	public static boolean isRightAlt(NativeEvent nativeEvent) {
		return checkCode(nativeEvent, "AltRight");
	}

	public static native boolean checkCode(NativeEvent evt, String check) /*-{
		return evt.code == check;
	}-*/;

	/**
	 * @param nativeEvent
	 *            native event
	 * @return whether this is left alt up/down event
	 */
	public static boolean isLeftAlt(NativeEvent nativeEvent) {
		return checkCode(nativeEvent, "AltLeft");
	}

	/**
	 * @param evt
	 *            native event
	 * @return java key code
	 */
	int convertToJavaKeyCode(NativeEvent evt) {

		int keyCodeGWT = evt.getKeyCode();

		// most keycodes are the same between Java and GWT
		// so don't check the common ones that are the same
		if ((keyCodeGWT >= GWTKeycodes.KEY_A && keyCodeGWT <= GWTKeycodes.KEY_Z)
				|| (keyCodeGWT >= GWTKeycodes.KEY_ZERO
						&& keyCodeGWT <= GWTKeycodes.KEY_NINE)) {
			return keyCodeGWT;
		}

		// eg Delete has a different code
		KeyCodes keyCode = KeyCodes.translateGWTcode(keyCodeGWT);

		return keyCode.getJavaKeyCode();
	}

	protected int getModifiers(
			com.google.gwt.event.dom.client.KeyEvent<?> event) {

		// AltGr -> Ctrl+Alt
		return (event.isShiftKeyDown() ? KeyEvent.SHIFT_MASK : 0)
				+ (controlDown(event) || isRightAltDown() ? KeyEvent.CTRL_MASK
						: 0)
				+ (event.isAltKeyDown() || isRightAltDown() ? KeyEvent.ALT_MASK
						: 0);
	}

	/**
	 * @param event
	 *            browser keyboard event
	 * @return MacOS: whether meta is down; other os: whether Ctrl is down
	 */
	boolean controlDown(com.google.gwt.event.dom.client.KeyEvent<?> event) {
		return Navigator.getUserAgent().contains("Macintosh")
				|| Navigator.getUserAgent().contains("Mac OS")
						? event.isMetaKeyDown() : event.isControlKeyDown();
	}

	protected char getChar(NativeEvent nativeEvent) {
		if (MathFieldW.checkCode(nativeEvent, "NumpadDecimal")) {
			return '.';
		}
		// eg European keyboards, want . not ,
		if (MathFieldW.checkCode(nativeEvent, "NumpadComma")) {
			return '.';
		}
		return (char) nativeEvent.getCharCode();
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public void requestViewFocus() {
		setEnabled(true);
		setFocus(true);
	}

	@Override
	public void requestLayout() {
		// for desktop only
	}

	public KeyListener getKeyListener() {
		return mathFieldInternal;
	}

	@Override
	public MetaModel getMetaModel() {
		return metaModel;
	}

	public void setMetaModel(MetaModel model) {
		this.metaModel = model;
	}

	@Override
	public void repaint() {
		// called to often, use repaintWeb for actual repaint
	}

	/**
	 * Make sure focus is in the editable element
	 */
	public void keepFocus() {
		if (!active(inputTextArea.getElement()) && isEdited()) {
			inputTextArea.getElement().focus();
		}
	}

	/**
	 * Actually repaint the content (repaint() is ignored in Web
	 * implementation).
	 */
	public void repaintWeb() {
		if (lastIcon == null) {
			return;
		}
		final double height = computeHeight();
		final double width = computeWidth();
		ctx.getCanvas().setHeight((int) Math.ceil(height * ratio));
		ctx.getCanvas().setWidth((int) Math.ceil(width * ratio));
		wasPaintedWithCursor = CursorBox.visible();
		paint(ctx, getMargin(lastIcon));
	}

	private double computeWidth() {
		return roundUp(lastIcon.getIconWidth() + 30);
	}

	/**
	 * Paints the formula on a canvas
	 * @param ctx canvas context
	 */
	public void paint(Context2d ctx, int top) {
		JlmLib.draw(lastIcon, ctx, 0, top, new ColorW(foregroundCssColor),
				new ColorW(backgroundCssColor), null, ratio);
	}

	private boolean isEdited() {
		return instances.contains(this);
	}

	private static boolean isIOS() {
		return Navigator.getUserAgent().toLowerCase().matches(".*(ipad|iphone|ipod).*");
	}

	private double computeHeight() {
		int margin = getMargin(lastIcon);
		return Math.max(roundUp(lastIcon.getIconHeight() + margin + bottomOffset), minHeight);
	}

	public int getIconHeight() {
		return lastIcon.getIconHeight();
	}

	public int getIconWidth() {
		return lastIcon.getIconWidth();
	}

	public int getIconDepth() {
		return lastIcon.getIconDepth();
	}

	private int getMargin(TeXIcon lastIcon2) {
		return fixMargin > 0 ? fixMargin : (int) Math.max(0,
				roundUp(-lastIcon2.getTrueIconHeight()
						+ lastIcon2.getTrueIconDepth()
						+ getFontSize()));
	}

	private native boolean active(Element element) /*-{
		return $doc.activeElement == element;
	}-*/;

	/**
	 * 
	 * for ratio 1.5 and w=5 CSS width we would get 7.5 coord space width; round
	 * up to 8
	 */
	private double roundUp(double w) {

		return Math.ceil(w * ratio) / ratio;
	}

	@Override
	public boolean hasFocus() {
		return focused;
	}

	@Override
	public void hideCopyPasteButtons() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean showKeyboard() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showCopyPasteButtons() {
		// TODO Auto-generated method stub
	}

	@Override
	public void scroll(int dx, int dy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void fireInputChangedEvent() {
		if (changeHandler != null) {
			changeHandler.onChange(null);
		}
	}

	@Override
	public Widget asWidget() {
		return html;
	}

	/**
	 * @param formula
	 *            editor content
	 */
	public void setFormula(MathFormula formula) {
		this.mathFieldInternal.setFormula(formula);
	}

	/**
	 * @return editor content
	 */
	public MathFormula getFormula() {
		return this.mathFieldInternal.getFormula();
	}

	/**
	 * Focus or blur this field
	 * 
	 * @param focus
	 *            whether to focus this
	 */
	public void setFocus(boolean focus) {
		setFocus(focus, null);
	}

	private void setFocus(boolean focus, final Runnable callback) {
		if (focus) {
			if (onTextfieldFocus != null) {
				onTextfieldFocus.onFocus(null);
			}
			focuser = new Timer() {

				@Override
				public void run() {
					onFocusTimer();
					if (callback != null) {
						callback.run();
					}
				}
			};
			focuser.schedule(200);
			startEditing();
			focusTextArea();
			if (!pasteInstalled) {
				pasteInstalled = true;
				installPaste(this.getHiddenTextArea());
			}
		} else {
			if (focuser != null) {
				focuser.cancel();
			}
			removeCursor();
		}
		this.focused = focus;
	}

	/**
	 * Make sure the HTML element has focus and update to render cursor
	 */
	protected void onFocusTimer() {
		// set focused flag before update to make sure cursor is rendered
		focused = true;
		mathFieldInternal.update();
		// focus + scroll the editor
		focusTextArea();
	}

	private void focusTextArea() {
		inputTextArea.getElement().focus();
		if (html.getElement().getParentElement() != null) {
			html.getElement().getParentElement().setScrollTop(0);
		}
		startBlink();
	}

	private native void installPaste(Element target) /*-{
		var that = this;
		target
				.addEventListener(
						'paste',
						function(a) {
							var exp;
							if (a.clipboardData) {
								exp = a.clipboardData.getData("text/plain");
							} else if ($wnd.clipboardData) {
								exp = $wnd.clipboardData.getData("Text");
							}

							exp = that.@com.himamis.retex.editor.web.MathFieldW::convert(Ljava/lang/String;)(exp);

							that.@com.himamis.retex.editor.web.MathFieldW::insertString(Ljava/lang/String;)(exp);
						});

	}-*/;

	private void startEditing() {
		if (mathFieldInternal.getEditorState().getCurrentField() == null) {
			CursorController.lastField(mathFieldInternal.getEditorState());
		}
		// update even when cursor didn't change here
		mathFieldInternal.update();
	}

	public void deleteCurrentWord() {
		this.mathFieldInternal.deleteCurrentWord();
	}

	public String getCurrentWord() {
		return this.mathFieldInternal.getCurrentWord();
	}

	public void selectNextArgument() {
		this.mathFieldInternal.selectNextArgument();
	}

	/**
	 * Make the cursor blink in this editor.
	 */
	protected void startBlink() {
		if (!instances.contains(this)) {
			instances.add(this);
		}
	}

	@Override
	public void paste() {
		// insertString(getSystemClipboardChromeWebapp(html.getElement()));
	}

	/**
	 * @param exp
	 *            inserted string (latex/mathml/...)
	 * @return ASCII math syntax
	 */
	protected String convert(String exp) {
		if (converter != null) {
			return converter.convert(exp);
		}

		return exp;
	}

	/**
	 * @param text
	 *            input text; similar to simple keyPress events but do not
	 *            create fractions/exponents
	 */
	public void insertString(String text) {
		KeyboardInputAdapter.insertString(mathFieldInternal, text);
	}

	/**
	 * add derivative and move cursor back before /
	 * @param text - d/dx
	 */
	public void handleDerivative(String text) {
		String[] parts = text.split("/");
		insertString(parts[0]);
		insertFunction("frac");
		insertString(parts[1]);
		pressKeyLeft();
		pressKeyLeft();
		pressKeyLeft();
	}

	private void pressKeyLeft() {
		getKeyListener().onKeyPressed(new KeyEvent(JavaKeyCodes.VK_LEFT));
	}

	private Element getHiddenTextArea() {
		if (clip == null) {
			clip = new SimplePanel();
			Element el = getHiddenTextAreaNative(counter++, clip.getElement());
			inputTextArea = MyTextArea.wrap(el);

			inputTextArea.addCompositionUpdateHandler(
					new EditorCompositionHandler(this));

			inputTextArea.addFocusHandler(event -> {
				startBlink();
				event.stopPropagation();
			});

			if (html != null) {
				html.addBlurHandler(this);
			}
			inputTextArea.addBlurHandler(this);
			clip.setWidget(inputTextArea);
		}
		if (parent != null) {
			parent.add(clip);
		}

		return inputTextArea.getElement();
	}

	@Override
	public void onBlur(BlurEvent event) {
		removeCursor();
		resetFlags();
		runBlurCallback(event);
		event.stopPropagation();
	}

	private void removeCursor() {
		boolean hadSelection = mathFieldInternal.getEditorState().hasSelection();
		if (hadSelection) {
			mathFieldInternal.getEditorState().resetSelection();
			mathFieldInternal.update();
		}
		if (wasPaintedWithCursor || hadSelection) {
			CursorBox.setBlink(false);
			repaintWeb();
		}
		instances.remove(this);
	}

	/**
	 * Run blur callback.
	 */
	protected void runBlurCallback(BlurEvent event) {
		if (onTextfieldBlur != null) {
			onTextfieldBlur.onBlur(event);
		}
	}

	/**
	 * Reset alt key flags.
	 */
	protected void resetFlags() {
		this.setRightAltDown(false);
		this.setLeftAltDown(false);
	}

	public void setOnBlur(BlurHandler run) {
		this.onTextfieldBlur = run;
	}

	public void setOnFocus(FocusHandler run) {
		this.onTextfieldFocus = run;
	}

	private static native Element getHiddenTextAreaNative(int counter,
			Element clipDiv) /*-{
		var hiddenTextArea = $doc.getElementById('hiddenCopyPasteLatexArea'
				+ counter);
		if (!hiddenTextArea) {
			hiddenTextArea = $doc.createElement("textarea");
			hiddenTextArea.id = 'hiddenCopyPasteLatexArea' + counter;
			hiddenTextArea.style.opacity = 0;
			clipDiv.style.zIndex = -32000;
			//* although clip is for absolute position, necessary! 
			//* as it is deprecated, may cause CSS challenges later 
			clipDiv.style.clip = "rect(1em 1em 1em 1em)";
			//* top/left will be specified dynamically, depending on scrollbar
			clipDiv.style.width = "1px";
			clipDiv.style.height = "1px";
			clipDiv.style.position = "relative";
			clipDiv.style.top = "-100%";
			clipDiv.className = "textAreaClip";
			hiddenTextArea.style.width = "1px";
			hiddenTextArea.style.padding = 0;
			hiddenTextArea.style.border = 0;
			hiddenTextArea.style.minHeight = 0;
			hiddenTextArea.style.height = "1px";//prevent messed up scrolling in FF/IE
			$doc.body.appendChild(hiddenTextArea);
			if (@org.geogebra.web.html5.Browser::isMobile()()) {
				hiddenTextArea.setAttribute("readonly", "true");
			}
		}
		//hiddenTextArea.value = '';
		return hiddenTextArea;
	}-*/;

	@Override
	public void copy() {
		nativeCopy(mathFieldInternal.copy());
	}

	private native void nativeCopy(String value) /*-{
		var copyFrom = this.@com.himamis.retex.editor.web.MathFieldW::getHiddenTextArea()();
		copyFrom.value = value;
		copyFrom.select();
		$doc.execCommand('copy');
	}-*/;

	@Override
	public boolean useCustomPaste() {
		return false;
	}

	/**
	 * @param size
	 *            font size
	 */
	public void setFontSize(double size) {
		mathFieldInternal.setSizeAndUpdate(size);
	}

	/**
	 * @param type
	 *            font type
	 */
	public void setFontType(int type) {
		mathFieldInternal.setFontAndUpdate(type);
	}

	/**
	 * Returns the font size
	 * @return font size
	 */
	public double getFontSize() {
		return mathFieldInternal.getMathFieldController().getFontSize();
	}

	/**
	 * Move caret after pointer event; event may be outside the editor.
	 * 
	 * @param absX
	 *            abs x-coord of the event
	 * @param absY
	 *            abs y-coord of the event
	 */
	public void adjustCaret(int absX, int absY) {
		if (SelectionBox.touchSelection) {
			return;
		}
		int x = mouseX(absX - asWidget().getAbsoluteLeft());
		int y = mouseY(absY - asWidget().getAbsoluteTop());
		if (x > asWidget().getOffsetWidth()) {
			CursorController.lastField(mathFieldInternal.getEditorState());
			mathFieldInternal.update();
		} else if (x < 0) {
			CursorController.firstField(mathFieldInternal.getEditorState());
			mathFieldInternal.update();
		} else {
			mathFieldInternal.onPointerUp(x, y);
		}
	}

	/**
	 * @param text
	 *            function name
	 */
	public void insertFunction(String text) {
		mathFieldInternal.insertFunction(text);
	}

	public void checkEnterReleased(Runnable r) {
		mathFieldInternal.checkEnterReleased(r);
	}

	public void setPlainTextMode(boolean plainText) {
		this.mathFieldInternal.setPlainTextMode(plainText);
	}

	/**
	 * Remove focus and call blur handler.
	 */
	public void blur() {
		this.inputTextArea.setFocus(false);
		if (this.onTextfieldBlur != null) {
			this.onTextfieldBlur.onBlur(null);
		}
	}

	public int mouseX(int x) {
		return (int) (x / scale);
	}

	public int mouseY(int y) {
		return (int) (y / scale);
	}

	public void setScale(double scaleX) {
		this.scale = scaleX;
	}

	@Override
	public void tab(boolean shiftDown) {
		mathFieldInternal.onTab(shiftDown);
	}

	@Override
	public void requestViewFocus(Runnable runnable) {
		setEnabled(true);
		setFocus(true, runnable);
	}

	/**
	 * @return whether right alt is down
	 */
	protected boolean isRightAltDown() {
		return rightAltDown;
	}

	/**
	 * @param rightAltDown
	 *            whether right alt is down
	 */
	protected void setRightAltDown(boolean rightAltDown) {
		this.rightAltDown = rightAltDown;
	}

	/**
	 * @return whether left alt is down
	 */
	protected boolean isLeftAltDown() {
		return leftAltDown;
	}

	/**
	 * @param leftAltDown
	 *            whether left alt is down
	 */
	protected void setLeftAltDown(boolean leftAltDown) {
		this.leftAltDown = leftAltDown;
	}

	/**
	 * In plain mode just fill with text (linear), otherwise parse math (ASCII
	 * math syntax) into the editor.
	 * 
	 * @param text0
	 *            text
	 * @param asPlainText
	 *            whether to use it as plain text
	 */
	public void setText(String text0, boolean asPlainText) {
		if (asPlainText) {
			mathFieldInternal.parse("");
			setPlainTextMode(true);
			insertString(text0);
		} else {
			mathFieldInternal.parse(text0);
		}
	}

	/**
	 * @return text in GGB syntax
	 */
	public String getText() {
		return mathFieldInternal.getText();
	}

	/**
	 * @return description for screen reader
	 */
	public String getDescription() {
		if (expressionReader != null) {
			return ScreenReaderSerializer.fullDescription(
					expressionReader,
				mathFieldInternal.getEditorState().getRootComponent());
		}
		return "";
	}

	/**
	 * Gets caret position as sequence of child indices in the tree
	 * 
	 * @return caret path
	 */
	public ArrayList<Integer> getCaretPath() {
		return CursorController.getPath(mathFieldInternal.getEditorState());
	}

	/**
	 * Setter for {@link #getCaretPath()}
	 * 
	 * @param path
	 *            caret path
	 */
	public void setCaretPath(ArrayList<Integer> path) {
		mathFieldInternal.setCaretPath(path);
	}

	/**
	 * @return the cross-platform representation of this field
	 */
	public MathFieldInternal getInternal() {
		return mathFieldInternal;
	}

	/**
	 * @return textarea
	 */
	public MyTextArea getInputTextArea() {
		return inputTextArea;
	}

	/**
	 * Sets foreground color in rgba(r, g, b, a) format.
	 *
	 * @param cssColor
	 * 			to set.
	 */
	public void setForegroundCssColor(String cssColor) {
		this.foregroundCssColor = cssColor;
	}

	/**
	 * Sets background color in #rrggbb format.
	 *
	 * @param cssColor
	 * 			to set.
	 */
	public void setBackgroundCssColor(String cssColor) {
		this.backgroundCssColor = cssColor;
	}

	/**
	 * @param changeHandler
	 *            change event handler
	 */
	public void setChangeListener(ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

	/**
	 * sets a fix margin of the mathfield, only used when bigger than 0
	 * @param fixMargin value of the fix margin
	 */
	public void setFixMargin(int fixMargin) {
		this.fixMargin = fixMargin;
	}

	/**
	 * sets a minimum height of the mathfield, only used when bigger than 0
	 * @param minHeight value of the minimum height
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}
}
