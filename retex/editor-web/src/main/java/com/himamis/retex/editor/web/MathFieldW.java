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
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.Resource;
import com.himamis.retex.renderer.web.JlmLib;

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
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {


		ctx.getCanvas().setHeight(icon.getIconHeight() + 15);
		ctx.getCanvas().getStyle().setHeight(icon.getIconHeight() + 15,
				Unit.PX);

		ctx.getCanvas().setWidth(icon.getIconWidth() + 30);
		ctx.getCanvas().getStyle().setWidth(icon.getIconWidth() + 30, Unit.PX);
		ctx.setFillStyle("rgb(255,255,255)");
		ctx.fillRect(0, 0, ctx.getCanvas().getWidth(),
				icon.getIconHeight() + 15);
		JlmLib.draw(icon, ctx, 0, 0, "#000000", "#FFFFFF", null);
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		// addFocusListener(new FocusListenerAdapterW(focusListener));
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		ClickAdapterW adapter = new ClickAdapterW(clickListener);
		adapter.listenTo(html);
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
				debug(code + "");
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
				keyListener.onKeyPressed(
						new KeyEvent(code, getModifiers(event),
								getChar(event.getNativeEvent())));
				MathFieldW.this.setFocus(true);
				if (code == 8 || code == 27) {
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
				+ (event.isAltKeyDown() ? KeyEvent.ALT_DOWN_MASK : 0);
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
		// TODO Auto-generated method stub

	}

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

	public void setFocus(boolean focus) {
		if (focus) {
			Timer t = new Timer() {

				@Override
				public void run() {
					html.getElement().focus();

				}
			};
			t.schedule(200);
			startEditing();
			html.getElement().focus();

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
}
