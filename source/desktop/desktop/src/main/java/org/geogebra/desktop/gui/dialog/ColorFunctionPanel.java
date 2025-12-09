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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.ColorFunctionModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.main.AppD;

/**
 * panel for condition to show object
 * @author Michael Borcherds 2008-04-01
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class ColorFunctionPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, ColorFunctionModel.IColorFunctionListener {

	private static final long serialVersionUID = 1L;
	/** color fun model */
	ColorFunctionModel model;
	private JTextField tfRed;
	private JTextField tfGreen;
	private JTextField tfBlue;
	private JTextField tfAlpha;
	private JButton btRemove;
	private JLabel nameLabelR;
	private JLabel nameLabelG;
	private JLabel nameLabelB;
	private JLabel nameLabelA;

	private JComboBox cbColorSpace;
	private int colorSpace = GeoElement.COLORSPACE_RGB;
	// flag to prevent unneeded relabeling of the colorSpace comboBox
	private boolean allowSetComboBoxLabels = true;

	private String defaultR = "0";
	private String defaultG = "0";
	private String defaultB = "0";
	private String defaultA = "1";

	private Kernel kernel;
	private PropertiesPanelD propPanel;

	/**
	 * @param app app
	 * @param propPanel properties panel
	 */
	public ColorFunctionPanel(AppD app, PropertiesPanelD propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;
		model = new ColorFunctionModel(app, this);
		// non auto complete input panel
		InputPanelD inputPanelR = new InputPanelD(null, app, -1, true, false);
		InputPanelD inputPanelG = new InputPanelD(null, app, -1, true, false);
		InputPanelD inputPanelB = new InputPanelD(null, app, -1, true, false);
		InputPanelD inputPanelA = new InputPanelD(null, app, -1, true, false);
		tfRed = (AutoCompleteTextFieldD) inputPanelR.getTextComponent();
		tfGreen = (AutoCompleteTextFieldD) inputPanelG.getTextComponent();
		tfBlue = (AutoCompleteTextFieldD) inputPanelB.getTextComponent();
		tfAlpha = (AutoCompleteTextFieldD) inputPanelA.getTextComponent();

		tfRed.addActionListener(this);
		tfRed.addFocusListener(this);
		tfGreen.addActionListener(this);
		tfGreen.addFocusListener(this);
		tfBlue.addActionListener(this);
		tfBlue.addFocusListener(this);
		tfAlpha.addActionListener(this);
		tfAlpha.addFocusListener(this);

		nameLabelR = new JLabel("", SwingConstants.TRAILING);
		nameLabelR.setLabelFor(inputPanelR);
		nameLabelG = new JLabel("", SwingConstants.TRAILING);
		nameLabelG.setLabelFor(inputPanelG);
		nameLabelB = new JLabel("", SwingConstants.TRAILING);
		nameLabelB.setLabelFor(inputPanelB);
		nameLabelA = new JLabel("", SwingConstants.TRAILING);
		nameLabelA.setLabelFor(inputPanelA);

		btRemove = new JButton("\u2718");
		btRemove.addActionListener(e -> model.removeAll());

		cbColorSpace = new JComboBox();
		cbColorSpace.addActionListener(this);

		setLayout(new BorderLayout());

		SpringLayout layout = new SpringLayout();
		JPanel colorsPanel = new JPanel(layout);
		colorsPanel.add(nameLabelR);
		colorsPanel.add(inputPanelR);
		colorsPanel.add(nameLabelG);
		colorsPanel.add(inputPanelG);
		colorsPanel.add(nameLabelB);
		colorsPanel.add(inputPanelB);
		colorsPanel.add(nameLabelA);
		colorsPanel.add(inputPanelA);

		SpringUtilities.makeCompactGrid(colorsPanel, layout, 4, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		add(colorsPanel, BorderLayout.CENTER);

		SpringLayout buttonsLayout = new SpringLayout();
		JPanel buttonsPanel = new JPanel(buttonsLayout);

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(cbColorSpace);
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(btRemove);
		buttonsPanel.add(leftPanel);
		buttonsPanel.add(rightPanel);

		SpringUtilities.makeCompactGrid(buttonsPanel, buttonsLayout, 1, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		add(buttonsPanel, BorderLayout.SOUTH);

		setLabels();
	}

	@Override
	public void setLabels() {
		Localization loc = kernel.getLocalization();

		setBorder(
				BorderFactory.createTitledBorder(loc.getMenu("DynamicColors")));

		if (allowSetComboBoxLabels) {
			cbColorSpace.removeActionListener(this);
			cbColorSpace.removeAllItems();
			cbColorSpace.addItem(loc.getMenu("RGB"));
			cbColorSpace.addItem(loc.getMenu("HSV"));
			cbColorSpace.addItem(loc.getMenu("HSL"));
			cbColorSpace.addActionListener(this);
		}
		allowSetComboBoxLabels = true;

		switch (colorSpace) {
		default:
		case GeoElement.COLORSPACE_RGB:
			nameLabelR
					.setText(StringUtil.capitalize(loc.getColor("red")) + ":");
			nameLabelG
					.setText(
							StringUtil.capitalize(loc.getColor("green")) + ":");
			nameLabelB
					.setText(StringUtil.capitalize(loc.getColor("blue")) + ":");
			break;
		case GeoElement.COLORSPACE_HSB:
			nameLabelR.setText(loc.getMenu("Hue") + ":");
			nameLabelG.setText(loc.getMenu("Saturation") + ":");
			nameLabelB.setText(loc.getMenu("Value") + ":");
			break;
		case GeoElement.COLORSPACE_HSL:
			nameLabelR.setText(loc.getMenu("Hue") + ":");
			nameLabelG.setText(loc.getMenu("Saturation") + ":");
			nameLabelB.setText(loc.getMenu("Lightness") + ":");
			break;
		}

		nameLabelA.setText(loc.getMenu("Opacity") + ":");

		btRemove.setToolTipText(loc.getPlainTooltip("Remove"));
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		// remove action listeners
		tfRed.removeActionListener(this);
		tfGreen.removeActionListener(this);
		tfBlue.removeActionListener(this);
		tfAlpha.removeActionListener(this);
		btRemove.removeActionListener(this);
		cbColorSpace.removeActionListener(this);

		model.updateProperties();

		// restore action listeners
		tfRed.addActionListener(this);
		tfGreen.addActionListener(this);
		tfBlue.addActionListener(this);
		tfAlpha.addActionListener(this);
		cbColorSpace.addActionListener(this);

		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfRed || e.getSource() == tfGreen
				|| e.getSource() == tfBlue || e.getSource() == tfAlpha) {
			doActionPerformed();
		}
		if (e.getSource() == cbColorSpace) {
			colorSpace = cbColorSpace.getSelectedIndex();
			allowSetComboBoxLabels = false;
			setLabels();
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;

		String strRed = tfRed.getText();
		String strGreen = tfGreen.getText();
		String strBlue = tfBlue.getText();
		String strAlpha = tfAlpha.getText();

		strRed = PropertiesPanelD.replaceEqualsSigns(strRed);
		strGreen = PropertiesPanelD.replaceEqualsSigns(strGreen);
		strBlue = PropertiesPanelD.replaceEqualsSigns(strBlue);
		strAlpha = PropertiesPanelD.replaceEqualsSigns(strAlpha);

		model.applyChanges(strRed, strGreen, strBlue, strAlpha, colorSpace,
				defaultR, defaultG, defaultB, defaultA);

	}

	@Override
	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	private boolean processed = false;

	@Override
	public void focusLost(FocusEvent e) {
		if (!processed) {
			doActionPerformed();
		}
	}

	@Override
	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		setFont(font);

		cbColorSpace.setFont(font);

		nameLabelR.setFont(font);
		nameLabelG.setFont(font);
		nameLabelB.setFont(font);
		nameLabelA.setFont(font);

		btRemove.setFont(font);

		tfRed.setFont(font);
		tfGreen.setFont(font);
		tfBlue.setFont(font);
		tfAlpha.setFont(font);
	}

	@Override
	public void setRedText(final String text) {
		tfRed.setText(text);

	}

	@Override
	public void setGreenText(final String text) {
		tfGreen.setText(text);
		// TODO Auto-generated method stub

	}

	@Override
	public void setBlueText(final String text) {
		tfBlue.setText(text);

	}

	@Override
	public void setAlphaText(final String text) {
		tfAlpha.setText(text);

	}

	@Override
	public void setDefaultValues(GeoElement geo) {
		Color col = GColorD.getAwtColor(geo.getObjectColor());
		defaultR = "" + col.getRed() / 255.0;
		defaultG = "" + col.getGreen() / 255.0;
		defaultB = "" + col.getBlue() / 255.0;
		defaultA = "" + geo.getFillColor().getAlpha() / 255.0;

		// set the selected color space and labels to match the first geo's
		// color space
		colorSpace = geo.getColorSpace();
		cbColorSpace.setSelectedIndex(colorSpace);
		allowSetComboBoxLabels = false;
		setLabels();

	}

	@Override
	public void showAlpha(boolean value) {
		tfAlpha.setVisible(value);
		nameLabelA.setVisible(value);
	}

	@Override
	public void updateSelection(Object[] geos) {
		propPanel.updateSelection(geos);

	}

}
