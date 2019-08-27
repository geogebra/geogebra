package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.html5.util.Persistable;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

/**
 * Class for editing in-place text on whiteboard.
 * 
 * @author laszlo
 *
 */
public class MowTextEditor extends AdvancedFlowPanel implements Persistable,
		MouseUpHandler, MouseDownHandler, MouseMoveHandler {
	private GRectangle bounds;

	/**
	 * constructor
	 */
	public MowTextEditor() {
		super();
		setAttribute("contenteditable", "true");
		getWidget().addStyleName("mowTextEditor");
		setWidth(80);
		addMouseDownHandler(this);
		addMouseUpHandler(this);
		addMouseMoveHandler(this);
	}

	/**
	 * Show editor
	 */
	public void show() {
		getWidget().removeStyleName("hidden");
	}

	/**
	 * Hide editor
	 */
	public void hide() {
		getWidget().addStyleName("hidden");
	}

	@Override
	public boolean isVisible() {
		return !getWidget().getStyleName().contains("hidden");
	}

	/**
	 * Sets position of the editor.
	 * 
	 * @param x
	 *            coordinate.
	 * @param y
	 *            coordinate.
	 */
	public void setPosition(int x, int y) {
		getElement().getStyle().setLeft(x, Unit.PX);
		getElement().getStyle().setTop(y, Unit.PX);
	}

	/**
	 * Sets the width of editor.
	 * 
	 * @param width
	 *            to set.
	 */
	public void setWidth(int width) {
		getElement().getStyle().setWidth(width, Unit.PX);
	}

	/**
	 * Sets the width of editor.
	 * 
	 * @param height
	 *            to set.
	 */
	public void setHeight(int height) {
		getElement().getStyle().setProperty("minHeight", height, Unit.PX);
	}

	/**
	 * Sets the text of the editor.
	 * 
	 * @param text
	 *            to set.
	 */
	public void setText(String text) {
		getElement().setInnerHTML(StringUtil.newlinesToHTML(text));
	}

	/**
	 * 
	 * @return text of the editor.
	 */
	public String getText() {
		String html = getElement().getInnerHTML();
		return StringUtil.htmlToNewlines(html);
	}

	/**
	 * Sets editor font.
	 * 
	 * @param font
	 *            to set.
	 */
	public void setFont(GFont font) {
		if (font == null) {
			return;
		}

		getWidget().getElement().getStyle().setProperty("font",
				((GFontW) font).getFullFontString());
	}

	/**
	 * Sets the line height of the editor
	 * 
	 * @param h
	 *            the new line height.
	 */
	public void setLineHeight(double h) {
		this.getWidget().getElement().getStyle().setLineHeight(h, Unit.PX);
	}

	/**
	 * Sets editor color.
	 * 
	 * @param color
	 *            to set.
	 */
	public void setColor(GColor color) {
		if (color == null) {
			return;
		}

		getWidget().getElement().getStyle().setProperty("color",
				GColor.getColorString(color));

	}

	/**
	 * 
	 * @return the bounding rectangle.
	 */
	public GRectangle getBounds(EuclidianViewW viewW) {
		if (bounds == null) {
			bounds = AwtFactory.getPrototype().newRectangle();
		}
		bounds.setBounds(getAbsoluteLeft(), getAbsoluteTop() - viewW.getAbsoluteTop() , getOffsetWidth(),
				getOffsetHeight());
		return bounds;
	}

	/**
	 * Sets bounds of the editor.
	 * 
	 * @param rect
	 *            to set.
	 */
	public void setBounds(GRectangle2D rect) {
		setWidth((int) rect.getWidth());
		setHeight((int) rect.getHeight());
		bounds.setBounds(rect.getBounds());
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.stopPropagation();
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		event.stopPropagation();
	}

	/**
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public void moveCursor(int x, int y) {
		try {
			moveCursorNative(getElement(), x, y);
		} catch (Exception e) {
			Log.warn("Invalid cursor position");
		}
	}

	private native void moveCursorNative(Element elTop, int x, int y)/*-{
		var targetContainer = $doc.elementFromPoint(x, y);
		var chars = targetContainer.innerText.split("");
		var styled = [];
		for (var k = 0; k < chars.length; k++) {
			styled.push("<span data-char=" + k + ">" + chars[k] + "</span>");
		}
		var oldHTML = targetContainer.innerHTML;
		targetContainer.innerHTML = styled.join("");
		var targetNode = $doc.elementFromPoint(x, y);
		var charIndex = parseInt(targetNode.getAttribute("data-char"));
		targetContainer.innerHTML = oldHTML;

		if (charIndex) {
			this.@org.geogebra.web.html5.euclidian.MowTextEditor::select(*)(
					targetContainer.childNodes[0], charIndex);
		} else {
			var targetChildren;
			// find the last node recursively
			do {
				targetChildren = targetElement.childNodes;
				targetElement = targetChildren[targetChildren.length - 1];
			} while (targetNode.nodeType == Element.ELEMENT_NODE);
			this.@org.geogebra.web.html5.euclidian.MowTextEditor::select(*)(
				targetNode, targetNode.length);
		}
	}-*/;

	@ExternalAccess
	private native void select(JavaScriptObject node, int index) /*-{
		var range = $doc.createRange();
		var sel = $wnd.getSelection();
		range.setStart(node, index);
		range.collapse(true);
		sel.removeAllRanges();
		sel.addRange(range);
		targetContainer.focus();
	}-*/;

}
