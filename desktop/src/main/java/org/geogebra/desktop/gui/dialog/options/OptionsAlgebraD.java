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
	private AppD app;
	private LocalizationD loc;


	private JPanel wrappedPanel;
	private JCheckBox auxiliary;
	private JComboBox<String> sortMode;
	private JComboBox<String> description;
	private JLabel descriptionLabel, sortLabel;
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

		initGUI();
		updateGUI();
		addListeners();
	}

	private void addListeners() {
		description.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onDescriptionChange();
			}
		});

		sortMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onSortChange();
			}
		});

		auxiliary.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onAuxChange();
			}

		});

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
			app.getKernel().setAlgebraStyle(description.getSelectedIndex());
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
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of
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
	 * @remark Do not call setLabels() here
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
		String[] modes = new String[] { loc.getMenu("Value"),
				loc.getMenu("Description"), loc.getMenu("Definition") };
		description.removeAllItems();

		for (int i = 0; i < modes.length; i++) {
			description.addItem(loc.getMenu(modes[i]));
		}

		int descMode = app.getKernel().getAlgebraStyle();
		if (descMode < modes.length) {
			description.setSelectedIndex(descMode);
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
