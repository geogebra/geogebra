package org.geogebra.web.full.cas.view;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * CAS input panel
 */
public interface InputPanel extends IsWidget, HasText {

	/**
	 * Text input
	 */
	public class InputPanelLabel extends Label implements InputPanel {

		@Override
		public void setLaTeX(String laTeX) {
			// not needed
		}

		@Override
		public void setPixelRatio(double ratio) {
			// not needed
		}
	}

	/**
	 * JLM based input
	 */
	public class InputPanelCanvas implements InputPanel {
		private String text;
		private Canvas c;
		private App app;
		private String laTex;

		/**
		 * @param app
		 *            application
		 */
		public InputPanelCanvas(App app) {
			this.app = app;
			c = Canvas.createIfSupported();
			// if shown on init, make sure it's not huge
			c.setCoordinateSpaceHeight(1);
			c.setCoordinateSpaceWidth(1);
		}

		@Override
		public void setText(String input) {
			this.text = input;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public void addStyleName(String style) {
			c.addStyleName(style);
		}

		@Override
		public Widget asWidget() {
			return c;
		}

		@Override
		public void removeStyleName(String string) {
			c.removeStyleName(string);
		}

		@Override
		public Element getElement() {
			return c.getElement();
		}

		@Override
		public void setLaTeX(String laTeX) {
			this.laTex = laTeX;
			if (laTeX == null) {
				c.setCoordinateSpaceHeight(1);
				c.setCoordinateSpaceWidth(1);
				return;
			}
			DrawEquationW.paintOnCanvas(new GeoNumeric(app.getKernel()
					.getConstruction()), laTeX, c, app.getFontSizeWeb());
		}

		@Override
		public void setPixelRatio(double ratio) {
			if (this.laTex != null) {
				setLaTeX(laTex);
			}
		}
	}

	/**
	 * @param string
	 *            CSS class name
	 */
	public void addStyleName(String string);

	/**
	 * @param string
	 *            CSS class name
	 */
	public void removeStyleName(String string);

	/**
	 * @return element
	 */
	public Element getElement();

	/**
	 * @param laTeX
	 *            LaTeX content
	 */
	public void setLaTeX(String laTeX);

	/**
	 * @param ratio
	 *            pixel ratio
	 */
	public void setPixelRatio(double ratio);
}
