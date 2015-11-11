package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.desktop.gui.util.FullWidthLayout;
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
		panel.setLayout(new FullWidthLayout());
		app.setComponentOrientation(panel);
	}

	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {

	}


	public void setLabels() {
		// TODO Auto-generated method stub

	}

	public void revalidate() {
		// TODO Auto-generated method stub

	}

	public void setBorder(Border border) {
		// TODO Auto-generated method stub

	}

	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

	public void applyModifications() {
		// TODO Auto-generated method stub

	}

	public void updateFont() {
		// TODO Auto-generated method stub

	}

	public void setSelected(boolean flag) {
		// TODO Auto-generated method stub

	}
}
