package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.OptionsCAS;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.desktop.main.AppD;

/**
 * Options for the CAS view.
 */
public class OptionsCASD implements OptionPanelD, ActionListener, SetLabels {
	/**
	 * Application object.
	 */
	private AppD app;

	private CASSettings casSettings;

	/** */
	private JLabel timeoutLabel;

	/** */
	private JComboBox cbTimeout;

	/** show rational exponents as roots */
	private JCheckBox cbShowRoots;
	private JCheckBox cbShowNavigation;

	private JPanel wrappedPanel;

	/**
	 * Construct CAS option panel.
	 * 
	 * @param app
	 */
	public OptionsCASD(AppD app) {
		this.wrappedPanel = new JPanel(new BorderLayout());

		this.app = app;
		casSettings = app.getSettings().getCasSettings();

		initGUI();
		updateGUI();
		setLabels();
	}

	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of
	 *         calling setLabels()
	 */
	private void initGUI() {
		JPanel timeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 1));

		cbTimeout = new JComboBox(OptionsCAS.getTimeoutOptions());
		cbTimeout.addActionListener(this);

		timeoutLabel = new JLabel();
		timeoutLabel.setLabelFor(cbTimeout);

		cbShowRoots = new JCheckBox();
		cbShowRoots.addActionListener(this);
		cbShowRoots.setSelected(casSettings.getShowExpAsRoots());

		cbShowNavigation = new JCheckBox();
		cbShowNavigation.addActionListener(this);
		cbShowNavigation.setSelected(casSettings.getShowExpAsRoots());

		timeoutPanel.add(timeoutLabel);
		timeoutPanel.add(cbTimeout);

		panel.add(timeoutPanel);
		panel.add(cbShowRoots);
		panel.add(cbShowNavigation);

		wrappedPanel.add(panel, BorderLayout.CENTER);

		app.setComponentOrientation(panel);
	}

	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	@Override
	public void updateGUI() {
		casSettings = app.getSettings().getCasSettings();
		cbTimeout.setSelectedItem(OptionsCAS
				.getTimeoutOption(casSettings.getTimeoutMilliseconds() / 1000));
		cbShowRoots.setSelected(casSettings.getShowExpAsRoots());
		cbShowNavigation.setSelected(app.showConsProtNavigation(App.VIEW_CAS));
	}

	/**
	 * React to actions.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// change timeout
		if (e.getSource() == cbTimeout) {
			casSettings.setTimeoutMilliseconds(
					((Integer) cbTimeout.getSelectedItem()) * 1000);
		}
		if (e.getSource() == cbShowNavigation) {
			app.toggleShowConstructionProtocolNavigation(App.VIEW_CAS);
		}
		/** show rational exponents as roots */
		if (e.getSource() == cbShowRoots) {
			casSettings.setShowExpAsRoots(cbShowRoots.isSelected());
		}
	}

	/**
	 * Update the language of the user interface.
	 */
	@Override
	public void setLabels() {
		Localization loc = app.getLocalization();
		timeoutLabel.setText(loc.getMenu("CasTimeout"));
		cbShowRoots.setText(loc.getMenu("CASShowRationalExponentsAsRoots"));
		cbShowNavigation.setText(loc.getMenu("NavigationBar"));
	}

	/**
	 * Apply changes
	 */
	@Override
	public void applyModifications() {
		// all controls fire immediately
	}

	@Override
	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	@Override
	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	@Override
	public void setBorder(Border border) {
		wrappedPanel.setBorder(border);
	}

	@Override
	public void updateFont() {
		Font font = app.getPlainFont();

		timeoutLabel.setFont(font);
		cbShowRoots.setFont(font);
		cbTimeout.setFont(font);
		cbShowNavigation.setFont(font);
	}

	@Override
	public void setSelected(boolean flag) {
		// see OptionsEuclidianD for possible implementation
	}
}
