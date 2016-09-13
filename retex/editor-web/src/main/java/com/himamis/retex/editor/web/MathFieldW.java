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

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.MetaModelParser;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.Resource;
import com.himamis.retex.renderer.web.JlmLib;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class MathFieldW implements MathField, IsWidget {

	private static final MetaModel metaModel;

	static {
		metaModel = new MetaModelParser().parse(new Resource().loadResource(
				"/com/himamis/retex/editor/desktop/meta/Octave.xml"));
	}


	private MathFieldInternal mathFieldInternal;
	private Widget html;
	private Context2d ctx;
	private boolean focused = false;
	private TeXIcon lastIcon;
	private float ratio = 1;
	private static Timer tick;
	static ArrayList<MathFieldW> instances = new ArrayList<MathFieldW>();

	/**
	 * 
	 * @param el
	 *            parent element
	 * @param context
	 *            drawing context
	 * @param listener
	 *            listener for special events
	 */
	public MathFieldW(Widget el, Context2d context,
			MathFieldListener listener) {
		html = el;
		el.getElement().setTabIndex(1);
		this.ctx = context;
		SelectionBox.touchSelection = false;
		mathFieldInternal = new MathFieldInternal(this);
		mathFieldInternal.setSelectionMode(true);
		mathFieldInternal.setFieldListener(listener);
		mathFieldInternal.setType(TeXFormula.SANSSERIF);
		mathFieldInternal.setFormula(MathFormula.newFormula(metaModel));
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
		instances.add(this);
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {
		this.lastIcon = icon;


		ctx.getCanvas().getStyle().setHeight(icon.getIconHeight() + 15,
				Unit.PX);

		ctx.getCanvas().getStyle().setWidth(icon.getIconWidth() + 30, Unit.PX);
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

	public void setPixelRatio(float ratio) {
		this.ratio = ratio;
	}
	@Override
	public void setKeyListener(final KeyListener keyListener) {
		html.addDomHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				keyListener.onKeyTyped(
						new KeyEvent(event.getNativeEvent().getKeyCode(), 0,
								event.getCharCode()));
				event.stopPropagation();
				event.preventDefault();

			}
		}, KeyPressEvent.getType());
		html.addDomHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				int code = event.getNativeEvent().getKeyCode();
				code = fixCode(code);
				keyListener.onKeyReleased(
						new KeyEvent(code, getModifiers(event),
								getChar(event.getNativeEvent())));
				if (code == 8 || code == 27) {
					event.preventDefault();
				}
				event.stopPropagation();

			}
		}, KeyUpEvent.getType());
		html.addDomHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				int code = event.getNativeEvent().getKeyCode();
				code = fixCode(code);
				boolean handled = keyListener.onKeyPressed(
						new KeyEvent(code, getModifiers(event),
								getChar(event.getNativeEvent())));
				// need to prevent sdefault for arrows to kill keypress
				// (otherwise strange chars appear in Firefox). Backspace/delete
				// also need killing.
				if (code == 8 || code == 27 || handled) {
					event.preventDefault();
				}
				event.stopPropagation();

			}
		}, KeyDownEvent.getType());

	}

	protected int fixCode(int code) {
		switch (code) {
		case 46:
			return KeyEvent.VK_DELETE;
		}
		return code;
	}

	protected native void debug(String string) /*-{
		$wnd.console.log(string);

	}-*/;

	protected int getModifiers(com.google.gwt.event.dom.client.KeyEvent event) {
		return (event.isShiftKeyDown() ? KeyEvent.SHIFT_MASK : 0)
				+ (event.isControlKeyDown() ? KeyEvent.CTRL_MASK : 0)
				+ (event.isAltKeyDown() ? KeyEvent.ALT_MASK : 0);
	}

	protected char getChar(NativeEvent nativeEvent) {
		return 0;
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public void requestViewFocus() {
		setFocus(true);
	}

	@Override
	public void requestLayout() {

	}

	public KeyListener getKeyListener() {
		return mathFieldInternal;

	}

	public MetaModel getMetaModel() {
		return metaModel;
	}
	public void repaint() {

	}

	public void repaintWeb() {
		if (lastIcon == null) {
			return;
		}
		ctx.getCanvas()
				.setHeight((int) ((lastIcon.getIconHeight() + 15) * ratio));
		ctx.getCanvas()
				.setWidth((int) ((lastIcon.getIconWidth() + 30) * ratio));
		ctx.setFillStyle("rgb(255,255,255)");
		((JLMContext2d) ctx).scale2(ratio, ratio);
		ctx.fillRect(0, 0, ctx.getCanvas().getWidth(),
				lastIcon.getIconHeight() + 15);
		JlmLib.draw(lastIcon, ctx, 0, 0, "#000000", "#FFFFFF", null);

	}

	private native void debug(boolean blink) /*-{
		$wnd.console.log(blink);
	}-*/;

	// private native void trace(String txt) /*-{
	// $wnd.console.trace(txt);
	// }-*/;

	public boolean hasFocus() {
		return focused;
	}

	public void hideCopyPasteButtons() {
		// TODO Auto-generated method stub

	}

	public boolean showKeyboard() {
		// TODO Auto-generated method stub
		return false;
	}

	public void showCopyPasteButtons() {
		// TODO Auto-generated method stub

	}

	public void scroll(int dx, int dy) {
		// TODO Auto-generated method stub

	}

	public void fireInputChangedEvent() {
		// TODO Auto-generated method stub

	}

	public Widget asWidget() {
		return html;
	}

	public void setFormula(MathFormula formula) {
		this.mathFieldInternal.setFormula(formula);
	}

	public MathFormula getFormula() {
		return this.mathFieldInternal.getFormula();
	}

	private Timer focuser;
	public void setFocus(boolean focus) {
		if (focus) {
			startBlink();

			focuser = new Timer() {

				@Override
				public void run() {
					html.getElement().focus();

				}
			};
			focuser.schedule(200);
			startEditing();
			html.getElement().focus();

		} else {
			if (focuser != null) {
				focuser.cancel();
			}
			this.lastIcon = null;
			instances.remove(this);
		}
		this.focused = focus;
	}

	public void startEditing() {
		if (mathFieldInternal.getEditorState().getCurrentField() == null) {
			mathFieldInternal.getCursorController()
					.lastField(mathFieldInternal.getEditorState());
		}

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
}
