package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.web.html5.awt.GFontW;
import org.geogebra.web.html5.gui.util.AdvancedFlowPanel;
import org.geogebra.web.html5.util.Persistable;

import com.google.gwt.dom.client.Style.Unit;

/**
 * Class for editing in-place text on whiteboard.
 * 
 * @author laszlo
 *
 */
public class MowTextEditor extends AdvancedFlowPanel implements Persistable {
	private GRectangle bounds;
	/**
	 * constructor
	 */
	public MowTextEditor() {
		super();
		setAttribute("contenteditable", "true");
		getWidget().addStyleName("mowTextEditor");
		setWidth(80);
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
		getElement().getStyle().setHeight(height, Unit.PX);
	}

	/**
	 * Sets the text of the editor.
	 * 
	 * @param text
	 *            to set.
	 */
	public void setText(String text) {
		String[] lines = text.split("\n");
		StringBuilder sb = new StringBuilder();
		sb.append(lines[0]);
		for (int i = 1; i < lines.length; i++) {
			sb.append("<div>");
			sb.append(lines[i]);
			sb.append("</div>");
		}
		getElement().setInnerHTML(sb.toString());
	}

	/**
	 * 
	 * @return text of the editor.
	 */
	public String getText() {
		String html = getElement().getInnerHTML();

		return html.replace("<div>", "\n").replace("</div>", "").replace("<br>", "\n");
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
	 * 
	 * @return the bounding rectangle.
	 */
	public GRectangle getBounds() {
		if (bounds == null) {
			bounds = AwtFactory.getPrototype().newRectangle();
		}
		bounds.setBounds(getAbsoluteLeft(), getAbsoluteTop(), getOffsetWidth(), getOffsetHeight());
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

}
