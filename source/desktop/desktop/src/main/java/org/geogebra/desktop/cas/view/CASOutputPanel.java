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

package org.geogebra.desktop.cas.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.App;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.main.AppD;

/**
 * Panel for CAS output, can contain LaTeX or normal output
 */
public class CASOutputPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// public static final int INDENT = 20; // pixel

	/**
	 * The text color of the output
	 */
	private static Color ERROR_COLOR = Color.red;
	private static Color OUTPUT_PREFIX_COLOR = Color.gray;

	private JLabel outputSign;
	private JLabel outputArea;
	private LaTeXPanel latexPanel;
	private AppD app;
	private JPanel centerPanel;

	/**
	 * @param app
	 *            application
	 */
	public CASOutputPanel(AppD app) {
		this.app = app;
		setBackground(Color.white);
		setLayout(new BorderLayout(5, 0));

		outputSign = new JLabel();
		outputSign.setForeground(OUTPUT_PREFIX_COLOR);

		outputArea = new JLabel();
		latexPanel = new LaTeXPanel(app);
		// will be overwritten later
		latexPanel.setForeground(Color.black);
		latexPanel.setBackground(Color.white);

		add(outputSign, app.getLocalization().borderWest());
		centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerPanel.setBackground(Color.white);
		centerPanel.add(outputArea);
		centerPanel.add(latexPanel);
		add(centerPanel, BorderLayout.CENTER);
	}

	/**
	 * @param c
	 *            foreground color
	 */
	public void setForeground(GColor c) {
		outputArea.setForeground(GColorD.getAwtColor(c));
		latexPanel.setForeground(GColorD.getAwtColor(c));
	}

	/**
	 * @param output
	 *            plain output (used when latexOutput is null or isError)
	 * @param latexOutput
	 *            LaTeX output (used when not null and !isError)
	 * @param cmd
	 *            top level command
	 * @param isError
	 *            whether outpput is error
	 * @param c
	 *            color
	 */
	public void setOutput(String output, String latexOutput, String cmd,
			boolean isError, GColor c, App app) {
		boolean useLaTeXpanel = latexOutput != null && !isError;
		outputArea.setVisible(!useLaTeXpanel);
		latexPanel.setVisible(useLaTeXpanel);
		setForeground(c);
		if (useLaTeXpanel) {
			latexPanel.setLaTeX(latexOutput);
		} else {
			outputArea.setText(output);
			if (isError) {
				outputArea.setForeground(ERROR_COLOR);
			}
		}

		outputSign.setText(cmd);
	}

	@Override
	final public void setFont(Font ft) {
		super.setFont(ft);

		if (ft == null) {
			return;
		}

		if (latexPanel != null) {
			latexPanel.setFont(ft.deriveFont(ft.getSize() + 2f));
		}

		if (outputArea != null) {
			outputArea.setFont(ft);
		}
		if (outputSign != null) {
			outputSign.setFont(ft);
		}
	}

	/**
	 * Set orientation
	 */
	public void setOrientation() {
		remove(outputSign);
		add(outputSign, app.getLocalization().borderWest());
		app.setFlowLayoutOrientation(centerPanel);
	}
}