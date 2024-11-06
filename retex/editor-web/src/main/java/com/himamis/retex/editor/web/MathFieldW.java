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
import java.util.List;
import java.util.function.Predicate;

import org.geogebra.gwtutil.NavigatorUtil;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.dom.style.shared.VerticalAlign;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyPressEvent;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

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
import com.himamis.retex.renderer.web.geom.Point2DW;
import com.himamis.retex.renderer.web.graphics.ColorW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContextHelper;

import elemental2.dom.CSSProperties;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.ClipboardEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.KeyboardEvent;
import jsinterop.base.Js;

public class MathFieldW implements MathField, IsWidget, MathFieldAsync, BlurHandler {

	public static final int SCROLL_THRESHOLD = 14;
	protected static MetaModel sMetaModel = new MetaModel();
	private static Predicate<NativeEvent> isGlobalEvent = evt -> false;
	private MetaModel metaModel;

	private final MathFieldInternal mathFieldInternal;
	private final Canvas html;
	private CanvasRenderingContext2D ctx;
	private final Panel parent;
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

	private MyTextArea inputTextArea;
	private SimplePanel clip;

	private double scale = 1.0;

	private ExpressionReader expressionReader;

	private static final ArrayList<MathFieldW> instances = new ArrayList<>();
	// can't be merged with instances.size because we sometimes remove an
	// instance
	private static int counter = 0;
	private ColorW foregroundColor = new ColorW("#000000");
	private ColorW backgroundColor = new ColorW("#ffffff");
	private ChangeHandler changeHandler;
	private int fixMargin = 0;
	private int minHeight = 0;
	private boolean wasPaintedWithCursor;
	private int rightMargin = 30;
	private int bottomOffset = 10;
	private double maxHeight = -1;
	private ClickAdapterW adapter;

	/**
	 * @param converter
	 *            latex/mathml-&lt; ascii math converter (optional)
	 * @param parent
	 *            parent element
	 * @param canvas
	 *            drawing context
	 * @param listener
	 *            listener for special events
	 */
	public MathFieldW(SyntaxAdapter converter, Panel parent, Canvas canvas,
					  MathFieldListener listener) {
		this(converter, parent, canvas, listener, sMetaModel);
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
	 * @param metaModel
	 *            model
	 */
	public MathFieldW(SyntaxAdapter converter, Panel parent, Canvas canvas,
			MathFieldListener listener, MetaModel metaModel) {

		this.metaModel = metaModel;
		FactoryProviderGWT.ensureLoaded();
		html = canvas;
		this.parent = parent;
		mathFieldInternal = new MathFieldInternal(this);
		mathFieldInternal.setSyntaxAdapter(converter);
		getHiddenTextArea();

		// el.getElement().setTabIndex(1);
		if (canvas != null) {
			this.ctx = JLMContextHelper.as(canvas.getContext2d());
		}
		SelectionBox.touchSelection = false;
		mathFieldInternal.setSelectionMode(true);
		mathFieldInternal.addMathFieldListener(listener);
		mathFieldInternal.setType(TeXFont.SANSSERIF);
		mathFieldInternal.setFormula(MathFormula.newFormula(sMetaModel));
		initTimer();
		instances.add(this);
		if (canvas != null) {

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
	}

	public static void setGlobalEventCheck(Predicate<NativeEvent> globalEvent) {
		MathFieldW.isGlobalEvent = globalEvent;
	}

	public static void removeAll() {
		instances.clear();
	}

	/**
	 * Enables or disables line break in the editor.
	 */
	public void setLineBreakEnabled(boolean b) {
		mathFieldInternal.getMathFieldController().setLineBreakEnabled(b);
	}

	public void setUseSimpleScripts(boolean useSimpleScripts) {
		mathFieldInternal.getInputController().setUseSimpleScripts(useSimpleScripts);
	}

	/**
	 * @param label
	 *            label for assistive technology
	 */
	public void setAriaLabel(String label) {
		Element target = getElementForAriaLabel();
		if (target != null) {
			if (expressionReader != null) {
				expressionReader.debug(label);
			}
			target.setAttribute("aria-label", label);
		}
	}

	private Element getElementForAriaLabel() {
		if (NavigatorUtil.isiOS() || NavigatorUtil.isMacOS()) {
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

		Point2DW size = computeSize();
		if (ctx == null || size == null) {
			return;
		}

		ctx.canvas.style.height = CSSProperties.HeightUnionType.of(size.getY() + "px");
		ctx.canvas.style.width = CSSProperties.WidthUnionType.of(size.getX() + "px");
		parent.setHeight(size.getY() + "px");
		parent.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		repaintWeb();
	}

	private Point2DW computeSize() {
		double height = computeHeight();
		if (ctx == null || height < 0) {
			return null;
		}
		double width = computeWidth();
		if (maxHeight > 0) {
			scale = 1;
			if (height > maxHeight) {
				scale = maxHeight / height;
				width = width * scale;
				height = maxHeight;
			}
			adapter.setScale(scale);
		}
		return new Point2DW(width, height);
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		// addFocusListener(new FocusListenerAdapterW(focusListener));
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		adapter = new ClickAdapterW(clickListener, this);
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
				if (event.getUnicodeCharCode() > 31) {
					keyListener.onKeyTyped(
							new KeyEvent(event.getNativeEvent().getKeyCode(), 0,
									getChar(event.getNativeEvent())));
				}
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

			if (isShortcutDefaultPrevented(event.getNativeEvent())) {
				event.preventDefault();
			}

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
			if (!isGlobalEvent.test(event.getNativeEvent())) {
				event.stopPropagation();
			}

		}, KeyDownEvent.getType());
	}

	/**
	 * Checks if the default action of a key combination snould be prevented.
	 * @param event to check.
	 * @return if the default action should be prevented.
	 */
	public static boolean isShortcutDefaultPrevented(NativeEvent event) {
		int code = convertToJavaKeyCode(event);
		return event.getCtrlKey() && event.getShiftKey()
				&& (code == JavaKeyCodes.VK_B || code == JavaKeyCodes.VK_M);
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

	private boolean checkPowerKeyInput(Element element) {
		HTMLTextAreaElement textArea = Js.uncheckedCast(element);
		if (textArea.value.matches(".*\\^")) {
			textArea.value = "";
			return true;
		}
		return false;
	}

	/**
	 * @param nativeEvent
	 *            native event
	 * @return whether this is right alt up/down event
	 */
	public static boolean isRightAlt(NativeEvent nativeEvent) {
		return checkCode(nativeEvent, "AltRight");
	}

	/**
	 * @param evt native event
	 * @param check key name (e.g. "AltRight")
	 * @return whether the event code matches
	 */
	public static boolean checkCode(Object evt, String check) {
		return check.equals(Js.<KeyboardEvent>uncheckedCast(evt).code);
	}

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
	static int convertToJavaKeyCode(NativeEvent evt) {

		int keyCodeGWT = evt.getKeyCode();

		// most keycodes are the same between Java and GWT
		// so don't check the common ones that are the same
		if ((keyCodeGWT >= GWTKeycodes.KEY_A && keyCodeGWT <= GWTKeycodes.KEY_Z)
				|| (keyCodeGWT >= GWTKeycodes.KEY_ZERO
						&& keyCodeGWT <= GWTKeycodes.KEY_NINE)) {
			return keyCodeGWT;
		}

		// eg Delete has a different code
		KeyCodes keyCode = NavigatorUtil.translateGWTcode(keyCodeGWT);

		return keyCode.getJavaKeyCode();
	}

	protected int getModifiers(
			org.gwtproject.event.dom.client.KeyEvent<?> event) {

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
	boolean controlDown(org.gwtproject.event.dom.client.KeyEvent<?> event) {
		return NavigatorUtil.isMacOS() ? event.isMetaKeyDown() : event.isControlKeyDown();
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
		Point2DW size = computeSize();
		if (size == null) {
			return;
		}
		ctx.canvas.height = (int) Math.ceil(size.getY() * ratio);
		ctx.canvas.width = (int) Math.ceil(size.getX() * ratio);
		wasPaintedWithCursor = CursorBox.visible();

		double margin = getMargin(lastIcon);

		paint(ctx, margin, backgroundColor, scale);
		Graphics2DW g = new Graphics2DW(ctx);
		lastIcon.paintCursor(g, margin);
	}

	private double computeWidth() {
		return roundUp(lastIcon.getIconWidth() + rightMargin);
	}

	/**
	 * Paints the formula on a canvas
	 * @param ctx canvas context
	 */
	public void paint(CanvasRenderingContext2D ctx, double top, ColorW bgColor, double scale) {
		JlmLib.draw(lastIcon, ctx, 0, top, foregroundColor,
				bgColor, null, ratio * scale);
	}

	/**
	 * @param ctx canvas
	 * @param top top
	 * @param bgColor background color
	 */
	public void paintFormulaNoPlaceholder(CanvasRenderingContext2D ctx,
			double top, ColorW bgColor) {
		TeXIcon iconNoPlaceholder = mathFieldInternal.buildIconNoPlaceholder();
		if (iconNoPlaceholder != null) {
			// use ratio 1 here to fit SVG export
			JlmLib.draw(iconNoPlaceholder, ctx, 0, top, foregroundColor,
					bgColor, null, 1);
		}
	}

	private boolean isEdited() {
		return instances.contains(this);
	}

	private double computeHeight() {
		return Math.max(getHeightWithMargin(), minHeight);
	}

	public double getHeightWithMargin() {
		return lastIcon.getIconHeight() + getMargin(lastIcon) + bottomOffset;
	}

	/**
	 *
	 * @param bottomOffset to set.
	 */
	public void setBottomOffset(int bottomOffset) {
		this.bottomOffset = bottomOffset;
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

	private double getMargin(TeXIcon lastIcon2) {
		return fixMargin + Math.max(0, -lastIcon2.getTrueIconHeight()
				+ lastIcon2.getTrueIconDepth() + getFontSize());
	}

	private boolean active(Object element) {
		return DomGlobal.document.activeElement == element;
	}

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
				installPaste(Js.uncheckedCast(getHiddenTextArea()));
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
		Element parentElement = html.getElement().getParentElement();
		if (parentElement != null) {
			int scroll = parentElement.getScrollLeft();
			inputTextArea.getElement().focus();
			parentElement.setScrollLeft(scroll);
			parentElement.setScrollTop(0);
		} else {
			inputTextArea.getElement().focus();
		}
		startBlink();
	}

	private void installPaste(elemental2.dom.Element target) {
		target.onpaste = (e) -> {
			ClipboardEvent event = (ClipboardEvent) e;
			if (event.clipboardData != null) {
				String exp = event.clipboardData.getData("text/plain");
				mathFieldInternal.convertAndInsert(exp);
			}
			return null;
		};
	}

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
	 * @param text
	 *            input text; similar to simple keyPress events but do not
	 *            create fractions/exponents
	 */
	public void insertString(String text) {
		mathFieldInternal.insertString(text);
	}

	private Element getHiddenTextArea() {
		if (clip == null) {
			clip = new SimplePanel();
			Element el = getHiddenTextAreaNative(counter++, clip.getElement());

			inputTextArea = MyTextArea.wrap(el);

			new EditorCompositionHandler(this).attachTo(inputTextArea);
			addFocusListener(inputTextArea, event -> {
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

	private void addFocusListener(MyTextArea inputTextArea, EventListener o) {
		// circumvent GWT event system to make sure this is fired
		elemental2.dom.Element elh = Js.uncheckedCast(inputTextArea.getElement());
		elh.addEventListener("focus", o);
	}

	public void clearState() {
		Js.<HTMLTextAreaElement>uncheckedCast(inputTextArea.getElement()).value = "";
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

	private static Element getHiddenTextAreaNative(int counter,
			Element clipDiv) {
		Element hiddenTextArea = DOM.getElementById("hiddenCopyPasteLatexArea"
				+ counter);
		if (hiddenTextArea == null) {
			hiddenTextArea = DOM.createTextArea();
			hiddenTextArea.setId("hiddenCopyPasteLatexArea" + counter);
			hiddenTextArea.getStyle().setOpacity(0);
			clipDiv.getStyle().setZIndex(-32000);
			// although clip is for absolute position, necessary!
			// as it is deprecated, may cause CSS challenges later
			clipDiv.getStyle().setProperty("clip", "rect(1em 1em 1em 1em)");
			// top/left will be specified dynamically, depending on scrollbar
			clipDiv.getStyle().setHeight(1, Unit.PX);
			clipDiv.getStyle().setWidth(1, Unit.PX);
			clipDiv.getStyle().setPosition(Position.RELATIVE);
			clipDiv.getStyle().setTop(-100, Unit.PCT);
			clipDiv.setClassName("textAreaClip");
			hiddenTextArea.getStyle().setWidth(1, Unit.PX);
			hiddenTextArea.getStyle().setPadding(0, Unit.PX);
			hiddenTextArea.getStyle().setProperty("border", "0");
			hiddenTextArea.getStyle().setProperty("minHeight", "0");
			//prevent messed up scrolling in FF/IE
			hiddenTextArea.getStyle().setHeight(1, Unit.PX);
			RootPanel.getBodyElement().appendChild(hiddenTextArea);
			if (NavigatorUtil.isMobile()) {
				hiddenTextArea.setAttribute("readonly", "true");
			}
		}
		//hiddenTextArea.value = '';
		return hiddenTextArea;
	}

	@Override
	public void copy() {
		nativeCopy(mathFieldInternal.copy());
	}

	private void nativeCopy(String value) {
		HTMLTextAreaElement copyFrom = Js.uncheckedCast(getHiddenTextArea());
		copyFrom.value = value;
		copyFrom.select();
		DocumentUtil.copySelection();
	}

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
	 * @param extScale external scaling factor
	 */
	public void adjustCaret(int absX, int absY, double extScale) {
		if (SelectionBox.touchSelection) {
			return;
		}
		int x = toCanvasPixels(absX - asWidget().getAbsoluteLeft(), extScale);
		int y = toCanvasPixels(absY - asWidget().getAbsoluteTop(), extScale);
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

	public void setPlainTextMode(boolean plainText) {
		this.mathFieldInternal.setPlainTextMode(plainText);
	}

	@Override
	public void blur() {
		this.inputTextArea.setFocus(false);
		if (this.onTextfieldBlur != null) {
			this.onTextfieldBlur.onBlur(null);
		}
	}

	public int toCanvasPixels(int x, double extScale) {
		return (int) (x / scale / extScale);
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
				mathFieldInternal.getEditorState().getRootComponent(),
					expressionReader.getAdapter());
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

	@Override
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
	 * @param cssColor color to set
	 */
	public void setForegroundColor(String cssColor) {
		this.foregroundColor = new ColorW(cssColor);
	}

	/**
	 * Sets background color in #rrggbb format.
	 *
	 * @param cssColor color to set
	 */
	public void setBackgroundColor(String cssColor) {
		this.backgroundColor = new ColorW(cssColor);
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

	/**
	 * Scrolls content horizontally,  based on the cursor position
	 *
	 * @param parentPanel
	 *            panel to be scrolled
	 */
	public void scrollParentHorizontally(Widget parentPanel) {
		MathFieldScroller.scrollHorizontallyToCursor(parentPanel,
				rightMargin, lastIcon.getCursorX());
	}

	/**
	 * Scrolls content verically, based on the cursor position
	 *
	 * @param parentPanel
	 *            panel to be scrolled
	 * @param margin
	 *            minimal distance from cursor to left/right border
	 */
	public void scrollParentVertically(FlowPanel parentPanel, int margin) {
		MathFieldScroller.scrollVerticallyToCursor(parentPanel, margin, lastIcon.getCursorY());
	}

	public ColorW getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param funcVars function variables of the input box
	 */
	public void setInputBoxFunctionVariables(List<String> funcVars) {
		metaModel.setInputBoxFunctionVars(funcVars);
		metaModel.enableSubstitutions();
	}

	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMaxHeight(double maxHeight) {
		this.maxHeight = maxHeight;
	}
}
