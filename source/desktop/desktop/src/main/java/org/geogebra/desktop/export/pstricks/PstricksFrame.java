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

package org.geogebra.desktop.export.pstricks;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.geogebra.common.export.pstricks.GeoGebraExport;
import org.geogebra.common.util.FileExtensions;

/**
 * @author Le Coq Loic
 */
public class PstricksFrame extends ExportFrame {
	private static final long serialVersionUID = 1L;
	final String[] format = { "LaTeX (article class)", "LaTeX (beamer class)" };

	/**
	 * @param ggb2pst PSTricks converter
	 */
	public PstricksFrame(final GeoGebraExport ggb2pst) {
		super(ggb2pst, "GeneratePstricks");
		initGui();
		fileExtension = FileExtensions.TEX;
		fileExtensionMsg = "TeX ";
	}

	protected void initGui() {
		comboFormat = new JComboBox(format);
		labelFormat = new JLabel(loc.getMenu("Format"));
		setTitle(loc.getMenu("TitleExportPstricks"));
		js.getViewport().add(textarea);
		panel.setLayout(new GridBagLayout());
		panel.add(labelXUnit,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textXUnit, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelwidth,
				new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textwidth, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelYUnit,
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textYUnit, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelheight,
				new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textheight, new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelXmin,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textXmin, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelXmax,
				new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textXmax, new GridBagConstraints(3, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelYmin,
				new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textYmin, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelYmax,
				new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textYmax, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelFontSize,
				new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(comboFontSize,
				new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelFormat,
				new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(comboFormat,
				new GridBagConstraints(3, 4, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(jcbPointSymbol, new GridBagConstraints(2, 5, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(jcbGrayscale, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(button, new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(buttonSave, new GridBagConstraints(2, 7, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(button_copy, new GridBagConstraints(3, 7, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(js,
				new GridBagConstraints(0, 8, 4, 5, 1.0, 20.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 0, 0));
		textXUnit.setPreferredSize(
				new Dimension(110, textXUnit.getFont().getSize() + 6));
		textYUnit.setPreferredSize(
				new Dimension(110, textYUnit.getFont().getSize() + 6));
		js.setPreferredSize(new Dimension(400, 400));
		getContentPane().add(panel);
		centerOnScreen();
		setVisible(true);
	}

	// end changes.
	@Override
	protected boolean isBeamer() {
		if (comboFormat.getSelectedIndex() == 1) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean isLaTeX() {
		return true;
	}

	@Override
	protected boolean isPlainTeX() {
		return false;
	}

	@Override
	protected boolean isConTeXt() {
		return false;
	}

}
