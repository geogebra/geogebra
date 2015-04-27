package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;

public class GLabelW extends org.geogebra.common.javax.swing.GLabel {

	private com.google.gwt.user.client.ui.HTML impl;
	private GColor color = GColor.WHITE;
	private boolean opaque = false;

	GLabelW() {
		impl = new com.google.gwt.user.client.ui.HTML();
		impl.addStyleName("ggw_glabel");
	}

	public GLabelW(String string) {
		impl = new com.google.gwt.user.client.ui.HTML(string);
		impl.addStyleName("ggw_glabel");
	}

	public GLabelW(String string, boolean bool) {
		this(string);
		if (!bool) {
			impl.addStyleName("ggw_glabel_nomargin");
		}
	}

	public static com.google.gwt.user.client.ui.Label getImpl(GLabelW label) {
		if (label == null)
			return null;
		return label.impl;
	}

	@Override
	public void setVisible(boolean b) {
		impl.setVisible(b);
	}

	@Override
	public void setText(String text) {
		impl.setHTML(text);

	}

	@Override
	public void setOpaque(boolean b) {
		this.opaque = b;
		if (b == true) {
			DOM.setStyleAttribute(impl.getElement(), "background",
			        GColor.getColorString(color));
		} else {
			DOM.setStyleAttribute(impl.getElement(), "background",
			        "rgba(0,0,0,0)");
		}
	}

	@Override
	public void setFont(GFont font) {
		// App.debug("GLabelW.setFont() implementation needed"); // TODO
		// Auto-generated
		impl.getElement().getStyle().setFontSize(font.getSize(), Unit.PX);
	}

	@Override
	public void setForeground(GColor color) {
		impl.getElement().getStyle().setColor(color.toString());
	}

	@Override
	public void setBackground(GColor color) {
		this.color = color;
		if (this.opaque) {
			DOM.setStyleAttribute(impl.getElement(), "background",
			        GColor.getColorString(color));
		}
		// DOM.setStyleAttribute(impl.getElement(), "background", "rgba("+
		// color.getRed()+", "+color.getGreen()+", "+color.getBlue()+", 1)");

	}

}
