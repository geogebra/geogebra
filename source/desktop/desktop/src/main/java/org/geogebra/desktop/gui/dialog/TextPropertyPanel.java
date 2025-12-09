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

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.gui.dialog.options.model.TextPropertyModel;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * panel for textfield size
 * @author Michael
 */
public class TextPropertyPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, ITextFieldListener {

	private static final long serialVersionUID = 1L;

	private TextPropertyModel model;
	private JLabel label;
	private MyTextFieldD tfTextfieldSize;

	private LocalizationD loc;

	/**
	 * @param app app
	 */
	public TextPropertyPanel(AppD app, TextPropertyModel model) {
		this(app, model, new MyTextFieldD(app, 5));
	}

	protected TextPropertyPanel(AppD app, TextPropertyModel model, MyTextFieldD textField) {
		this.loc = app.getLocalization();
		this.model = model;
		model.setListener(this);
		// text field for textfield size
		label = new JLabel();
		tfTextfieldSize = textField;
		label.setLabelFor(tfTextfieldSize);
		tfTextfieldSize.addActionListener(this);
		tfTextfieldSize.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(label);
		animPanel.add(tfTextfieldSize);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);

		setLabels();
	}

	@Override
	public void setLabels() {
		label.setText(loc.getMenu(model.getTitle()) + ": ");
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		tfTextfieldSize.removeActionListener(this);

		model.updateProperties();

		tfTextfieldSize.addActionListener(this);
		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfTextfieldSize) {
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		model.applyChanges(tfTextfieldSize.getText());
		updatePanel(model.getGeos());
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// only focus lost is important
	}

	@Override
	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setText(String text) {
		tfTextfieldSize.setText(text);

	}
}
