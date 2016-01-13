package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Advanced options for the options dialog.
 */
public class OptionsAlgebraD extends
		org.geogebra.common.gui.dialog.options.OptionsAlgebra implements
		SetLabels, OptionPanelD {

	/**
	 * Application object.
	 */
	private AppD app;
	private LocalizationD loc;

	/**
	 * Settings for all kind of application components.
	 */
	private Settings settings;


	private JPanel wrappedPanel;
	private JCheckBox auxiliary;
	private JComboBox sortMode, description;
	private JLabel descriptionLabel, sortLabel;
	boolean ignoreActions;
	private List<SortMode> supportedModes = Arrays.asList(SortMode.DEPENDENCY,
			SortMode.TYPE, SortMode.ORDER, SortMode.LAYER);

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 */
	public OptionsAlgebraD(AppD app) {
		this.wrappedPanel = new JPanel(new BorderLayout());

		this.app = app;
		this.loc = app.getLocalization();
		this.settings = app.getSettings();

		initGUI();
		updateGUI();
		addListeners();
	}

	private void addListeners() {
		description.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ignoreActions) {
					return;
				}
				if (description.getSelectedIndex() >= 0) {
					app.getKernel().setAlgebraStyle(
							description.getSelectedIndex());
					app.getKernel().updateConstruction();
				}
			}
		});

		sortMode.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ignoreActions) {
					return;
				}
				if (sortMode.getSelectedIndex() >= 0) {
					SortMode sort = supportedModes.get(sortMode
							.getSelectedIndex());
					app.getAlgebraView().setTreeMode(sort);
				}
			}
		});

		auxiliary.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				app.setShowAuxiliaryObjects(auxiliary.isSelected());

			}

		});

	}

	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of
	 *         calling setLabels()
	 */
	private void initGUI() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1));
		app.setComponentOrientation(panel);
		this.auxiliary = new JCheckBox();
		this.sortMode = new JComboBox() {
			@Override
			public void setSelectedIndex(int i) {
				super.setSelectedIndex(i);
			}
		};
		this.description = new JComboBox();

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
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {
		auxiliary.setSelected(app.showAuxiliaryObjects);
		updateSortMode();
		updateDescription();
	}

	private void updateSortMode() {
		ignoreActions = true;
		sortMode.removeAllItems();

		for (SortMode mode : supportedModes) {
			sortMode.addItem(app.getPlain(mode.toString()));
		}

		SortMode selectedMode = app.getAlgebraView().getTreeMode();
		sortMode.setSelectedIndex(supportedModes.indexOf(selectedMode));
		ignoreActions = false;
	}

	private void updateDescription() {
		ignoreActions = true;
		String[] modes = new String[] { app.getPlain("Value"),
				app.getPlain("Definition"), app.getPlain("Command") };
		description.removeAllItems();

		for (int i = 0; i < modes.length; i++) {
			description.addItem(app.getPlain(modes[i]));
		}

		int descMode = app.getKernel().getAlgebraStyle();
		description.setSelectedIndex(descMode);
		ignoreActions = false;
	}

	public void setLabels() {
		auxiliary.setText(loc.getMenu("AuxiliaryObjects"));
		descriptionLabel.setText(loc.getMenu("AlgebraDescriptions"));
		sortLabel.setText(loc.getMenu("SortBy") + ":");
		updateDescription();
		updateSortMode();

	}

	public void revalidate() {
		wrappedPanel.revalidate();

	}

	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);

	}

	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

	public void applyModifications() {
		// TODO Auto-generated method stub

	}

	public void updateFont() {
		Font font = app.getPlainFont();
		wrappedPanel.setFont(font);
	}

	public void setSelected(boolean flag) {
		// TODO Auto-generated method stub

	}
}
