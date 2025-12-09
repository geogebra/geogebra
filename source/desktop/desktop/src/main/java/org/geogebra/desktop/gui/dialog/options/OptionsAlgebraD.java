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

package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Advanced options for the options dialog.
 */
public class OptionsAlgebraD
		implements SetLabels, OptionPanelD {

	private static final List<SortMode> SUPPORTED_MODES = Arrays.asList(
			SortMode.DEPENDENCY, SortMode.TYPE, SortMode.ORDER, SortMode.LAYER);

	/**
	 * Application object.
	 */
	private final AppD app;
	private final LocalizationD loc;
	private final List<AlgebraStyle> algebraStyles;

	private JPanel wrappedPanel;
	private JCheckBox auxiliary;
	private JComboBox<String> sortMode;
	private JComboBox<String> description;
	private JLabel descriptionLabel;
	private JLabel sortLabel;
	private boolean ignoreActions;

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 *            application
	 */
	public OptionsAlgebraD(AppD app) {
		this.wrappedPanel = new JPanel(new BorderLayout());

		this.app = app;
		this.loc = app.getLocalization();
		this.algebraStyles = AlgebraStyle.getAvailableValues(app);

		initGUI();
		updateGUI();
		addListeners();
	}

	private void addListeners() {
		description.addActionListener(e -> onDescriptionChange());

		sortMode.addActionListener(e -> onSortChange());

		auxiliary.addActionListener(e -> onAuxChange());

	}

	/**
	 * Auxiliary change handler.
	 */
	protected void onAuxChange() {
		app.setShowAuxiliaryObjects(auxiliary.isSelected());
	}

	/**
	 * Description change handler.
	 */
	protected void onDescriptionChange() {
		if (ignoreActions) {
			return;
		}
		if (description.getSelectedIndex() >= 0) {
			app.getSettings().getAlgebra().setStyle(
					algebraStyles.get(description.getSelectedIndex()));
			app.getKernel().updateConstruction(false);
		}
	}

	/**
	 * Sort mode handler.
	 */
	protected void onSortChange() {
		if (ignoreActions) {
			return;
		}
		if (sortMode.getSelectedIndex() >= 0) {
			int index = sortMode.getSelectedIndex();
			app.getSettings().getAlgebra()
					.setTreeMode(SUPPORTED_MODES.get(index));
		}
	}

	/**
	 * Initialize the user interface.
	 * 
	 * <p>Remark: updateGUI() will be called directly after this method
	 * <p>Remark: Do not use translations here, the option dialog will take care of
	 *         calling setLabels()
	 */
	@SuppressWarnings("serial")
	private void initGUI() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1));
		app.setComponentOrientation(panel);
		this.auxiliary = new JCheckBox();
		this.sortMode = new JComboBox<String>() {
			@Override
			public void setSelectedIndex(int i) {
				super.setSelectedIndex(i);
			}
		};
		this.description = new JComboBox<>();

		panel.add(auxiliary);

		JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		sortLabel = new JLabel();
		sortLabel.setLabelFor(sortMode);
		sortPanel.add(sortLabel);
		sortPanel.add(sortMode);
		panel.add(sortPanel);

		JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		descriptionLabel = new JLabel();
		descriptionLabel.setLabelFor(description);
		descriptionPanel.add(descriptionLabel);
		descriptionPanel.add(description);
		panel.add(descriptionPanel);
		setLabels();
		wrappedPanel.add(panel);

	}

	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * <p>Remark: Do not call setLabels() here
	 */
	@Override
	public void updateGUI() {
		auxiliary.setSelected(app.showAuxiliaryObjects);
		updateSortMode();
		updateDescription();
	}

	private void updateSortMode() {
		ignoreActions = true;
		sortMode.removeAllItems();

		for (SortMode mode : SUPPORTED_MODES) {
			sortMode.addItem(app.getLocalization().getMenu(mode.toString()));
		}

		SortMode selectedMode = app.getAlgebraView().getTreeMode();
		sortMode.setSelectedIndex(SUPPORTED_MODES.indexOf(selectedMode));
		ignoreActions = false;
	}

	private void updateDescription() {
		ignoreActions = true;
		description.removeAllItems();
		algebraStyles.forEach(style -> description.addItem(loc.getMenu(style.getTranslationKey())));
		int index = algebraStyles.indexOf(app.getAlgebraStyle());
		if (index != -1) {
			description.setSelectedIndex(index);
		}
		ignoreActions = false;
	}

	@Override
	public void setLabels() {
		auxiliary.setText(loc.getMenu("AuxiliaryObjects"));
		descriptionLabel.setText(loc.getMenu("AlgebraDescriptions"));
		sortLabel.setText(loc.getMenu("SortBy") + ":");
		updateDescription();
		updateSortMode();

	}

	@Override
	public void revalidate() {
		wrappedPanel.revalidate();

	}

	@Override
	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);

	}

	@Override
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

	@Override
	public void applyModifications() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFont() {
		Font font = app.getPlainFont();
		wrappedPanel.setFont(font);
	}

	@Override
	public void setSelected(boolean flag) {
		// TODO Auto-generated method stub

	}
}
