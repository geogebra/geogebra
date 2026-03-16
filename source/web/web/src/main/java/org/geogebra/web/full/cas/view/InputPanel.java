/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.cas.view;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.html5.main.DrawEquationW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.HasText;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * CAS input panel
 */
public interface InputPanel extends IsWidget, HasText {

	/**
	 * Text input
	 */
	class InputPanelLabel extends Label implements InputPanel {

		@Override
		public void repaint() {
			// not needed
		}

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
	class InputPanelCanvas implements InputPanel {
		private String text;
		private final Canvas canvas;
		private final App app;
		private String laTex;

		/**
		 * @param app
		 *            application
		 */
		public InputPanelCanvas(App app) {
			this.app = app;
			canvas = Canvas.createIfSupported();
			// if shown on init, make sure it's not huge
			canvas.setCoordinateSpaceHeight(1);
			canvas.setCoordinateSpaceWidth(1);
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
			canvas.addStyleName(style);
		}

		@Override
		public Widget asWidget() {
			return canvas;
		}

		@Override
		public void removeStyleName(String string) {
			canvas.removeStyleName(string);
		}

		@Override
		public Element getElement() {
			return canvas.getElement();
		}

		@Override
		public void setLaTeX(String laTeX) {
			this.laTex = laTeX;
			repaint();
		}

		@Override
		public void repaint() {
			if (laTex == null) {
				canvas.setCoordinateSpaceHeight(1);
				canvas.setCoordinateSpaceWidth(1);
				return;
			}
			String toRender = laTex;
			if ("\\nbsp{}".equals(laTex) && StringUtil.empty(text)) {
				toRender = "\\text{" + app.getLocalization().getMenu("InputLabel")
						+ Unicode.ELLIPSIS + "}";
			}
			DrawEquationW.paintOnCanvas(new GeoNumeric(app.getKernel()
					.getConstruction()), toRender, canvas, app.getFontSize());
		}

		@Override
		public void setPixelRatio(double ratio) {
			repaint();
		}
	}

	/**
	 * @param string
	 *            CSS class name
	 */
	void addStyleName(String string);

	/**
	 * @param string
	 *            CSS class name
	 */
	void removeStyleName(String string);

	/**
	 * @return element
	 */
	Element getElement();

	/**
	 * @param laTeX
	 *            LaTeX content
	 */
	void setLaTeX(String laTeX);

	/**
	 * @param ratio
	 *            pixel ratio
	 */
	void setPixelRatio(double ratio);

	/**
	 * Refresh content.
	 */
	void repaint();

}
