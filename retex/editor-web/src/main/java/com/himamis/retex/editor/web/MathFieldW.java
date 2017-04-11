/**
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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.web.JlmLib;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class MathFieldW implements MathField, IsWidget {

	protected static MetaModel sMetaModel = new MetaModel();

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
	private Timer focuser;
	private boolean pasteInstalled = false;

	private int bottomOffset;
	static ArrayList<MathFieldW> instances = new ArrayList<MathFieldW>();
	// can't be merged with instances.size because we sometimes remove an
	// instance
	private static int counter = 0;

	/**
	 * 
	 * @param parent
	 *            parent element
	 * @param canvas
	 *            drawing context
	 * @param listener
	 *            listener for special events
	 */
	public MathFieldW(Panel parent, Canvas canvas,
			MathFieldListener listener) {
		html = canvas;
		bottomOffset = 10;
		this.parent = parent;
		mathFieldInternal = new MathFieldInternal(this);
		getHiddenTextArea();

		// el.getElement().setTabIndex(1);
		this.ctx = canvas.getContext2d();
		SelectionBox.touchSelection = false;

		mathFieldInternal.setSelectionMode(true);
		mathFieldInternal.setFieldListener(listener);
		mathFieldInternal.setType(TeXFormula.SANSSERIF);
		mathFieldInternal.setFormula(MathFormula.newFormula(sMetaModel));
		initTimer();
		instances.add(this);
		canvas.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (!isEnabled()) {
					return;
				}
				event.stopPropagation();
				// prevent default to keep focus; also avoid dragging the whole
				// editor
				event.preventDefault();
				setFocus(true);
				rightAltDown = false;
				leftAltDown = false;

			}
		}, MouseDownEvent.getType());


		setKeyListener(wrap, keyListener);
	}

	private static void initTimer() {
		if (tick == null) {
			tick = new Timer() {

				@Override
				public void run() {
					CursorBox.blink = !CursorBox.blink;
					for (MathFieldW field : instances) {
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

		double height = roundUp(icon.getIconHeight() + bottomOffset);
		ctx.getCanvas().getStyle().setHeight(height,
				Unit.PX);

		ctx.getCanvas().getStyle()
				.setWidth(roundUp(icon.getIconWidth() + 30), Unit.PX);
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
		html2.addDomHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				// don't kill Ctrl+V or write V
				if (event.isControlKeyDown() && (event.getCharCode() == 'v'
						|| event.getCharCode() == 'V') || leftAltDown) {

					event.stopPropagation();
				} else {
					keyListener.onKeyTyped(
							new KeyEvent(event.getNativeEvent().getKeyCode(), 0,
									getChar(event.getNativeEvent())));
					event.stopPropagation();
					event.preventDefault();
				}

			}
		}, KeyPressEvent.getType());
		html2.addDomHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (checkPowerKeyInput(html2.getElement())) {
					keyListener.onKeyTyped(new KeyEvent(0, 0, '^'));
					onFocusTimer(); // refocus to remove the half-written letter
					updateAltForKeyUp(event);
					event.preventDefault();
					return;
				}
				int code = fixCode(event.getNativeEvent());
				keyListener.onKeyReleased(
						new KeyEvent(code, getModifiers(event),
								getChar(event.getNativeEvent())));
				updateAltForKeyUp(event);
				if (code == 8 || code == 27) {
					event.preventDefault();
				}
			}
		}, KeyUpEvent.getType());
		html2.addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (isRightAlt(event.getNativeEvent())) {
					rightAltDown = true;
				}
				if (isLeftAlt(event.getNativeEvent())) {
					leftAltDown = true;
				}

				int code = fixCode(event.getNativeEvent());
				boolean handled = keyListener.onKeyPressed(
						new KeyEvent(code, getModifiers(event),
								getChar(event.getNativeEvent())));
				// need to prevent sdefault for arrows to kill keypress
				// (otherwise strange chars appear in Firefox). Backspace/delete
				// also need killing.
				// also kill events while left alt down: alt+e, alt+d working in
				// browser
				if (code == 8 || code == 27 || handled || leftAltDown) {
					event.preventDefault();
				}
				event.stopPropagation();

			}
		}, KeyDownEvent.getType());
		


	}

	/**
	 * Update alt flags after key released
	 * 
	 * @param event
	 *            keyUp event
	 */
	protected void updateAltForKeyUp(KeyUpEvent event) {
		if (isRightAlt(event.getNativeEvent())) {
			rightAltDown = false;
		}
		if (isLeftAlt(event.getNativeEvent())) {
			leftAltDown = false;
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

	public static native boolean checkCode(NativeEvent evt,
			String check) /*-{
		return evt.code == check;
	}-*/;

	private static native boolean checkNativeKey(NativeEvent evt,
			String check) /*-{
		return evt.key == check;
	}-*/;
	/**
	 * @param nativeEvent
	 *            native event
	 * @return whether this is left alt up/down event
	 */
	public static boolean isLeftAlt(NativeEvent nativeEvent) {
		return checkCode(nativeEvent, "AltLeft");
	}

	protected int fixCode(NativeEvent evt) {
		if (evt.getKeyCode() == 46) {
			return KeyEvent.VK_DELETE;
		}
		if (evt.getKeyCode() == 44) {
			return KeyEvent.VK_DELETE;
		}
		if (checkNativeKey(evt, "[")) {
			return KeyEvent.VK_OPEN_BRACKET;
		}
		if (checkNativeKey(evt, "{")) {
			return KeyEvent.VK_OPEN_BRACKET;
		}
		if (checkNativeKey(evt, "(")) {
			return KeyEvent.VK_OPEN_PAREN;
		}



		return evt.getKeyCode();

	}

	protected int getModifiers(
			com.google.gwt.event.dom.client.KeyEvent<?> event) {
		return (event.isShiftKeyDown() ? KeyEvent.SHIFT_MASK : 0)
				+ (event.isControlKeyDown() || rightAltDown ? KeyEvent.CTRL_MASK
						: 0)
				+ (event.isAltKeyDown() ? KeyEvent.ALT_MASK : 0);
	}

	protected char getChar(NativeEvent nativeEvent) {
		if (MathFieldW.checkCode(nativeEvent, "NumpadDecimal")) {
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
		return sMetaModel;
	}
	@Override
	public void repaint() {
		// called to often, use repaintWeb for actual repaint
	}

	public void repaintWeb() {
		if (lastIcon == null) {
			return;
		}
		if (!active(wrap.getElement()) && this.enabled) {
			wrap.getElement().focus();
		}
		final double height = roundUp(lastIcon.getIconHeight() + bottomOffset);
		final double width = roundUp(lastIcon.getIconWidth() + 30);
		ctx.getCanvas().setHeight(((int) Math.ceil(height * ratio)));
		ctx.getCanvas().setWidth((int) Math.ceil(width * ratio));

		ctx.setFillStyle("rgb(255,255,255)");
		((JLMContext2d) ctx).scale2(ratio, ratio);
		ctx.fillRect(0, 0, ctx.getCanvas().getWidth(), height);
		JlmLib.draw(lastIcon, ctx, 0, 0, "#000000", "#FFFFFF", null);
	}

	private native boolean active(Element element) /*-{
		return $doc.activeElement == element;
	}-*/;

	/**
	 * 
	 * for ratio 1.5 and w=5 CSS width we would get 7.5 coord space width; round
	 * up to 8
	 */
	private double roundUp(int w) {

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
		// TODO Auto-generated method stub

	}

	@Override
	public Widget asWidget() {
		return html;
	}

	public void setFormula(MathFormula formula) {
		this.mathFieldInternal.setFormula(formula);
	}

	public MathFormula getFormula() {
		return this.mathFieldInternal.getFormula();
	}


	public void setFocus(boolean focus) {
		if (focus) {
			startBlink();

			focuser = new Timer() {

				@Override
				public void run() {
					onFocusTimer();

				}
			};
			focuser.schedule(200);
			startEditing();
			wrap.getElement().focus();
			if (!pasteInstalled) {
				pasteInstalled = true;
				installPaste(this.getHiddenTextArea());
			}

		} else {
			if (focuser != null) {
				focuser.cancel();
			}
			instances.remove(this);
			// last repaint with no cursor
			CursorBox.blink = false;
			repaintWeb();
			this.lastIcon = null;

		}
		this.focused = focus;
	}

	/**
	 * Make sure the HTML element has focus and update to render cursor
	 */
	protected void onFocusTimer() {
		BlurHandler oldBlur = this.onTextfieldBlur;
		onTextfieldBlur = null;
		mathFieldInternal.update();
		// first focus canvas to get the scrolling right
		html.getElement().focus();
		// after set focus to the keyboard listening element
		wrap.getElement().focus();
		onTextfieldBlur = oldBlur;


	}

	private native void installPaste(Element target) /*-{
		var that = this;
		target.addEventListener('paste',
			function(a){
				if(a.clipboardData){
					that.@com.himamis.retex.editor.web.MathFieldW::insertString(Ljava/lang/String;)(a.clipboardData.getData("text/plain"));
				}else if($wnd.clipboardData){
					that.@com.himamis.retex.editor.web.MathFieldW::insertString(Ljava/lang/String;)($wnd.clipboardData.getData("Text"));
				}
			}
			);
		
	}-*/;

	public void startEditing() {
		if (mathFieldInternal.getEditorState().getCurrentField() == null) {
			mathFieldInternal.getCursorController();
			CursorController
					.lastField(mathFieldInternal.getEditorState());
		}
		// update even when cursor didn't change here
		mathFieldInternal.update();

	}

	public String deleteCurrentWord() {
		return this.mathFieldInternal.deleteCurrentWord();
	}

	public String getCurrentWord() {
		return this.mathFieldInternal.getCurrentWord();
	}

	public void selectNextArgument() {
		this.mathFieldInternal.selectNextArgument();

	}

	public void startBlink() {
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
		KeyboardInputAdapter.insertString(mathFieldInternal, text);

		mathFieldInternal.selectNextArgument();

		mathFieldInternal.update();

	}


	private TextArea wrap;
	private SimplePanel clip;

	private Element getHiddenTextArea() {
		if (clip == null) {
			clip = new SimplePanel();
			Element el = getHiddenTextAreaNative(counter++,
					clip.getElement());
			wrap = TextArea.wrap(el);

			wrap.addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					event.stopPropagation();

				}
			});
			wrap.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					event.stopPropagation();
					if (onTextfieldBlur != null) {
						onTextfieldBlur.onBlur(event);
					}

				}
			});
			clip.setWidget(wrap);
		}
		if (parent != null) {
			parent.add(clip);

			}

		return wrap.getElement();
	}

	public void setOnBlur(BlurHandler run) {
		this.onTextfieldBlur = run;
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
			clipDiv.style.display = "inline";
			clipDiv.style.width = "1px";
			clipDiv.style.height = "1px";
			clipDiv.style.position = "relative";
			clipDiv.style.top = "-15px";
			clipDiv.className = "textAreaClip";
			hiddenTextArea.style.width = "1px";
			hiddenTextArea.style.padding = 0;
			hiddenTextArea.style.border = 0;
			hiddenTextArea.style.minHeight = 0;
			hiddenTextArea.style.height = "1px";//prevent messed up scrolling in FF/IE
			$doc.body.appendChild(hiddenTextArea);
			if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
					.test(window.navigator.userAgent)) {
				hiddenTextArea.setAttribute("disabled", "true");
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
	public native boolean useCustomPaste() /*-{
		return false;
	}-*/;



	public void setFontSize(double size) {
		this.mathFieldInternal.setSize(size);
		this.mathFieldInternal.update();
	}

	public void adjustCaret(int absX, int absY) {
		if (SelectionBox.touchSelection) {
			return;
		}
		int x = absX - asWidget().getAbsoluteLeft();
		int y = absY - asWidget().getAbsoluteTop();
		if (x > asWidget().getOffsetWidth()) {

			CursorController.lastField(mathFieldInternal.getEditorState());
			mathFieldInternal.update();
		} else if (x < 0) {

			CursorController.firstField(mathFieldInternal.getEditorState());
			mathFieldInternal.update();
		}else {
			mathFieldInternal.onPointerUp(x, y);
		}

	}

	public void insertFunction(String text) {
		mathFieldInternal.insertFunction(text);

	}

	public void checkEnterReleased(Runnable r) {
		mathFieldInternal.checkEnterReleased(r);

	}

	public void blur() {
		this.wrap.setFocus(false);
		if (this.onTextfieldBlur != null) {
			this.onTextfieldBlur.onBlur(null);
		}

	}

}
