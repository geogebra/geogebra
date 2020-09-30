/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.geogebra.web.html5.gui;

import org.geogebra.common.main.App;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A form of popup that has a caption area at the top and can be dragged by the
 * user. Unlike a PopupPanel, calls to {@link #setWidth(String)} and
 * {@link #setHeight(String)} will set the width and height of the dialog box
 * itself, even if a widget has not been added as yet.
 * <p>
 * <img class='gallery' src='doc-files/DialogBox.png'/>
 * </p>
 * <h3>CSS Style Rules</h3>
 * 
 * <ul>
 * <li>.gwt-DialogBox { the outside of the dialog }</li>
 * <li>.gwt-DialogBox .Caption { the caption }</li>
 * <li>.gwt-DialogBox .dialogContent { the wrapper around the content }</li>
 * <li>.gwt-DialogBox .dialogTopLeft { the top left cell }</li>
 * <li>.gwt-DialogBox .dialogTopLeftInner { the inner element of the cell }</li>
 * <li>.gwt-DialogBox .dialogTopCenter { the top center cell, where the caption
 * is located }</li>
 * <li>.gwt-DialogBox .dialogTopCenterInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogTopRight { the top right cell }</li>
 * <li>.gwt-DialogBox .dialogTopRightInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogMiddleLeft { the middle left cell }</li>
 * <li>.gwt-DialogBox .dialogMiddleLeftInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogMiddleCenter { the middle center cell, where the
 * content is located }</li>
 * <li>.gwt-DialogBox .dialogMiddleCenterInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogMiddleRight { the middle right cell }</li>
 * <li>.gwt-DialogBox .dialogMiddleRightInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogBottomLeft { the bottom left cell }</li>
 * <li>.gwt-DialogBox .dialogBottomLeftInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogBottomCenter { the bottom center cell }</li>
 * <li>.gwt-DialogBox .dialogBottomCenterInner { the inner element of the cell }
 * </li>
 * <li>.gwt-DialogBox .dialogBottomRight { the bottom right cell }</li>
 * <li>.gwt-DialogBox .dialogBottomRightInner { the inner element of the cell }
 * </li>
 * </ul>
 * <p>
 * <h3>Example</h3> {@example com.google.gwt.examples.DialogBoxExample}
 * </p>
 * 
 * You may also create your own header caption. The caption must implement
 * {@link Caption}.
 *
 * <p>
 *
 * For example:
 * 
 * <p>
 * 
 * <pre>
 * &lt;g:DialogBox autoHide="true" modal="true">
 *   &lt;-- foo is your prefix and Bar is a class that implements {@link Caption}-->
 *   &lt;g:customCaption>&lt;foo:Bar/>&lt;/g:customCaption>
 *   &lt;g:HTMLPanel>
 *     Body text
 *     &lt;g:Button ui:field='cancelButton'>Cancel&lt;/g:Button>
 *     &lt;g:Button ui:field='okButton'>Okay&lt;/g:Button>
 *   &lt;/g:HTMLPanel>
 * &lt;/g:DialogBox>
 * </pre>
 * 
 */
public class GDialogBox extends GDecoratedPopupPanel
		implements HasHTML, HasSafeHtml {

	/**
	 * The default style name.
	 */
	private static final String DEFAULT_STYLENAME = "gwt-DialogBox";

	private Caption caption;
	private boolean dragging;
	private int dragStartX;
	private int dragStartY;
	private int windowWidth;
	private int clientLeft;
	private int clientTop;

	// Visible for Testing
	HandlerRegistration resizeHandlerRegistration;

	/**
	 * Set of characteristic interfaces supported by the {@link GDialogBox}
	 * caption.
	 *
	 */
	public interface Caption
			extends HasAllMouseHandlers, HasHTML, HasSafeHtml, IsWidget {

		void onAttach();

		void onDetach();
	}

	/**
	 * Default implementation of Caption. This will be created as the header if
	 * there isn't a header specified.
	 */
	public static class CaptionImpl extends HTML implements Caption {

		/**
		 * Caption implementation.
		 */
		public CaptionImpl() {
			super();
			setStyleName("Caption");
		}

		@Override
		public void onAttach() {
			super.onAttach();
		}

		@Override
		public void onDetach() {
			super.onDetach();
		}

	}

	private class MouseHandler implements MouseDownHandler, MouseUpHandler,
			MouseMoveHandler {

		@Override
		public void onMouseDown(MouseDownEvent event) {
			beginDragging(event);
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			continueDragging(event);
		}

		@Override
		public void onMouseUp(MouseUpEvent event) {
			endDragging(event);
		}
	}

	/**
	 * Creates an empty dialog box. It should not be shown until its child
	 * widget has been added using {@link #add(Widget)}.
	 */
	public GDialogBox(Panel root, App app) {
		this(false, root, app);
	}

	/**
	 * Creates an empty dialog box specifying its "auto-hide" property. It
	 * should not be shown until its child widget has been added using
	 * {@link #add(Widget)}.
	 *
	 * @param autoHide
	 *            <code>true</code> if the dialog should be automatically hidden
	 *            when the user clicks outside of it
	 */
	public GDialogBox(boolean autoHide, Panel root, App app) {
		this(autoHide, true, root, app);
	}

	/**
	 * Creates an empty dialog box specifying its {@link Caption}. It should not
	 * be shown until its child widget has been added using {@link #add(Widget)}
	 * .
	 *
	 * @param captionWidget
	 *            the widget that is the DialogBox's header.
	 */
	public GDialogBox(Caption captionWidget, Panel root, App app) {
		this(false, true, captionWidget, root, app);
	}

	/**
	 * Creates an empty dialog box specifying its "auto-hide" and "modal"
	 * properties. It should not be shown until its child widget has been added
	 * using {@link #add(Widget)}.
	 *
	 * @param autoHide
	 *            <code>true</code> if the dialog should be automatically hidden
	 *            when the user clicks outside of it
	 * @param modal
	 *            <code>true</code> if keyboard and mouse events for widgets not
	 *            contained by the dialog should be ignored
	 */
	public GDialogBox(boolean autoHide, boolean modal, Panel root, App app) {
		this(autoHide, modal, new CaptionImpl(), root, app);
	}

	/**
	 *
	 * Creates an empty dialog box specifying its "auto-hide", "modal"
	 * properties and an implementation a custom {@link Caption}. It should not
	 * be shown until its child widget has been added using {@link #add(Widget)}
	 * .
	 *
	 * @param autoHide
	 *            <code>true</code> if the dialog should be automatically hidden
	 *            when the user clicks outside of it
	 * @param modal
	 *            <code>true</code> if keyboard and mouse events for widgets not
	 *            contained by the dialog should be ignored
	 * @param captionWidget
	 *            the widget that is the DialogBox's header.
	 */
	public GDialogBox(boolean autoHide, boolean modal, Caption captionWidget,
			Panel root, App app) {
		super(autoHide, modal, "dialog", root, app);

		assert captionWidget != null : "The caption must not be null";
		captionWidget.asWidget().removeFromParent();
		caption = captionWidget;

		// Add the caption to the top row of the decorator panel. We need to
		// logically adopt the caption so we can catch mouse events.
		Element td = getCellElement(0, 1);
		DOM.appendChild(td, caption.asWidget().getElement());
		adopt(caption.asWidget());

		// Set the style name
		setStyleName(DEFAULT_STYLENAME);

		windowWidth = Window.getClientWidth();
		clientLeft = Document.get().getBodyOffsetLeft();
		clientTop = Document.get().getBodyOffsetTop();

		MouseHandler mouseHandler = new MouseHandler();
		addDomHandler(mouseHandler, MouseDownEvent.getType());
		addDomHandler(mouseHandler, MouseUpEvent.getType());
		addDomHandler(mouseHandler, MouseMoveEvent.getType());
	}

	/**
	 * Provides access to the dialog's caption.
	 *
	 * @return the logical caption for this dialog box
	 */
	public Caption getCaption() {
		return caption;
	}

	@Override
	public String getHTML() {
		return caption.getHTML();
	}

	@Override
	public String getText() {
		return caption.getText();
	}

	@Override
	public void hide(boolean autoClosed) {
		if (resizeHandlerRegistration != null) {
			resizeHandlerRegistration.removeHandler();
			resizeHandlerRegistration = null;
		}
		super.hide(autoClosed);
	}

	@Override
	public void onBrowserEvent(Event event) {
		// If we're not yet dragging, only trigger mouse events if the event
		// occurs
		// in the caption wrapper
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEUP:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEOVER:
		case Event.ONMOUSEOUT:
			if (!dragging && !isCaptionEvent(event)) {
				return;
			}
		}

		super.onBrowserEvent(event);
	}

	/**
	 * Sets the html string inside the caption by calling its
	 * {@link #setHTML(SafeHtml)} method.
	 *
	 * Use {@link #setWidget(Widget)} to set the contents inside the
	 * {@link GDialogBox}.
	 *
	 * @param html
	 *            the object's new HTML
	 */
	@Override
	public void setHTML(SafeHtml html) {
		caption.setHTML(html);
	}

	/**
	 * Sets the html string inside the caption by calling its
	 * {@link #setHTML(SafeHtml)} method. Only known safe HTML should be
	 * inserted in here.
	 *
	 * Use {@link #setWidget(Widget)} to set the contents inside the
	 * {@link GDialogBox}.
	 *
	 * @param html
	 *            the object's new HTML
	 */
	@Override
	public void setHTML(String html) {
		caption.setHTML(SafeHtmlUtils.fromTrustedString(html));
	}

	/**
	 * Sets the text inside the caption by calling its {@link #setText(String)}
	 * method.
	 *
	 * Use {@link #setWidget(Widget)} to set the contents inside the
	 * {@link GDialogBox}.
	 *
	 * @param text
	 *            the object's new text
	 */
	@Override
	public void setText(String text) {
		caption.setText(text);
	}

	@Override
	public void show() {
		if (resizeHandlerRegistration == null) {
			resizeHandlerRegistration = Window
					.addResizeHandler(new ResizeHandler() {
						@Override
						public void onResize(ResizeEvent event) {
							windowWidth = event.getWidth();
						}
					});
		}
		super.show();
	}

	/**
	 * Called on mouse down in the caption area, begins the dragging loop by
	 * turning on event capture.
	 *
	 * @see DOM#setCapture
	 * @see #continueDragging
	 * @param event
	 *            the mouse down event that triggered dragging
	 */
	protected void beginDragging(MouseDownEvent event) {
		if (DOM.getCaptureElement() == null) {
			/*
			 * Need to check to make sure that we aren't already capturing an
			 * element otherwise events will not fire as expected. If this check
			 * isn't here, any class which extends custom button will not fire
			 * its click event for example.
			 */
			dragging = true;
			DOM.setCapture(getElement());
			dragStartX = event.getX();
			dragStartY = event.getY();
		}
	}

	/**
	 * Called on mouse move in the caption area, continues dragging if it was
	 * started by {@link #beginDragging}.
	 *
	 * @see #beginDragging
	 * @see #endDragging
	 * @param event
	 *            the mouse move event that continues dragging
	 */
	protected void continueDragging(MouseMoveEvent event) {
		if (dragging) {
			int absX = event.getX() + getAbsoluteLeft();
			int absY = event.getY() + getAbsoluteTop();

			// if the mouse is off the screen to the left, right, or top, don't
			// move the dialog box. This would let users lose dialog boxes,
			// which
			// would be bad for modal popups.
			if (absX < clientLeft || absX >= windowWidth || absY < clientTop) {
				return;
			}

			setPopupPosition(
					absX - dragStartX - getRootPanel().getAbsoluteLeft(),
					absY - dragStartY - getRootPanel().getAbsoluteTop());
		}
	}

	@Override
	protected void doAttachChildren() {
		try {
			super.doAttachChildren();
		} finally {
			// See comment in doDetachChildren for an explanation of this call
			caption.onAttach();
		}
	}

	@Override
	protected void doDetachChildren() {
		try {
			super.doDetachChildren();
		} finally {
			/*
			 * We need to detach the caption specifically because it is not part
			 * of the iterator of Widgets that the {@link SimplePanel} super
			 * class returns. This is similar to a {@link ComplexPanel}, but we
			 * do not want to expose the caption widget, as its just an internal
			 * implementation.
			 */
			caption.onDetach();
		}
	}

	/**
	 * Called on mouse up in the caption area, ends dragging by ending event
	 * capture.
	 *
	 * @param event
	 *            the mouse up event that ended dragging
	 *
	 * @see DOM#releaseCapture
	 * @see #beginDragging
	 * @see #endDragging
	 */
	protected void endDragging(MouseUpEvent event) {
		caption.asWidget();
		event.getX();
		event.getY();
		dragging = false;
		DOM.releaseCapture(getElement());
	}

	/**
	 * <b>Affected Elements:</b>
	 * <ul>
	 * <li>-caption = text at the top of the {@link GDialogBox}.</li>
	 * <li>-content = the container around the content.</li>
	 * </ul>
	 *
	 * @see UIObject#onEnsureDebugId(String)
	 */
	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		caption.asWidget().ensureDebugId(baseID + "-caption");
		ensureDebugId(getCellElement(1, 1), baseID, "content");
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		// We need to preventDefault() on mouseDown events (outside of the
		// DialogBox content) to keep text from being selected when it
		// is dragged.
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONMOUSEDOWN)
				&& isCaptionEvent(nativeEvent)) {
			nativeEvent.preventDefault();
		}

		super.onPreviewNativeEvent(event);
	}

	private boolean isCaptionEvent(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return getCellElement(0, 1).getParentElement()
					.isOrHasChild(Element.as(target));
		}
		return false;
	}
}
