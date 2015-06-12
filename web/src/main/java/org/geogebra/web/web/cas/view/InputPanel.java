package org.geogebra.web.web.cas.view;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.DrawEquationWeb;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public interface InputPanel extends IsWidget {

	public void setText(String input);

	public String getText();

	public class InputPanelLabel extends Label implements InputPanel {

		public void setLaTeX(String laTeX) {
			// TODO Auto-generated method stub

		}

	}

	public class InputPanelCanvas implements InputPanel {
		private String text;
		private Canvas c;
		private App app;

		public InputPanelCanvas(App app) {
			this.app = app;
			c = Canvas.createIfSupported();
			// if shown on init, make sure it's not huge
			c.setCoordinateSpaceHeight(1);
			c.setCoordinateSpaceWidth(1);
		}
		public void setText(String input) {

			this.text = input;

		}

		public String getText() {
			return text;
		}

		public void addStyleName(String style) {
			c.addStyleName(style);

		}

		public Widget asWidget() {
			return c;
		}

		public void removeStyleName(String string) {
			c.removeStyleName(string);

		}

		public Element getElement() {
			return c.getElement();
		}

		public void setLaTeX(String laTeX) {
			if (laTeX == null) {
				c.setCoordinateSpaceHeight(1);
				c.setCoordinateSpaceWidth(1);
				return;
			}
			DrawEquationWeb.paintOnCanvas(new GeoNumeric(app.getKernel()
					.getConstruction()), laTeX, c, app.getFontSize());

		}

	}

	public void addStyleName(String string);

	public void removeStyleName(String string);

	public Element getElement();

	public void setLaTeX(String laTeX);

}
