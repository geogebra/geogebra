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

package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.EventType;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;

/**
 * panel for text editingA
 */
public class TextEditPanel extends JPanel implements
		UpdateablePropertiesPanel, SetLabels, UpdateFonts {
	/**
	 * 
	 */
	private final PropertiesPanelD propertiesPanelD;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** text dialog */
	TextInputDialogD td = null;
	private JPanel editPanel;

	/**
	 * New text edit panel
	 * @param propertiesPanelD parent panel
	 */
	public TextEditPanel(PropertiesPanelD propertiesPanelD) {
		this.propertiesPanelD = propertiesPanelD;
		initGUI();
	}

	private void initGUI() {
		if (td != null) {
			return;
		}
		td = new TextInputDialogD(this.propertiesPanelD.app,
				this.propertiesPanelD.loc.getMenu("Text"), null, null,
				true, 30, 5, false);
		setLayout(new BorderLayout());

		editPanel = new JPanel(new BorderLayout(0, 0));
		editPanel.add(td.getInputPanel(), BorderLayout.CENTER);
		editPanel.add(td.getToolBar(), BorderLayout.SOUTH);
		editPanel.setBorder(BorderFactory.createEtchedBorder());

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPanel,
				td.getPreviewPanel());
		sp.setResizeWeight(0.5);
		sp.setBorder(BorderFactory.createEmptyBorder());

		add(sp, BorderLayout.CENTER);
		add(td.getButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		td.applyModifications();
	}

	@Override
	public void setLabels() {
		td.setLabels(this.propertiesPanelD.loc.getMenu("Text"));
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		if (geos.length != 1 || !checkGeos(geos)) {
			td.reset();
			return null;
		}

		GeoText text = (GeoText) geos[0];
		td.setGeoText(text);
		td.updateRecentSymbolTable();

		return this;
	}

	private static boolean checkGeos(Object[] geos) {
		return geos.length == 1 && geos[0] instanceof GeoText
				&& !((GeoText) geos[0]).isTextCommand()
				&& !((GeoText) geos[0]).isProtected(EventType.UPDATE);
	}

	@Override
	public void updateFonts() {
		Font font = this.propertiesPanelD.app.getPlainFont();

		editPanel.setFont(font);
		td.updateFonts();
	}
}