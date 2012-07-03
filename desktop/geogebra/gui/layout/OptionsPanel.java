package geogebra.gui.layout;

import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.main.AppD;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class OptionsPanel extends JPanel {

	private AppD app;

	private OptionsDialog.Factory optionsDialogFactory;
	private OptionsDialog optionsDialog;


	public OptionsPanel(AppD app) {

		this.app = app;
		setLayout(new BorderLayout());

		optionsDialogFactory = new OptionsDialog.Factory();
		optionsDialog = optionsDialogFactory.create(app);

		add(optionsDialog.getContentPane(), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 40, 10));

		int n = optionsDialog.getTabbedPane().getTabCount();
		for (int i = 0; i < n; i++) {
			// optionsDialog.getTabbedPane().setIconAt(i, null);
		}
	}

	public void setOptionsPanel(int tabIndex) {

		optionsDialog.updateGUI();
		if (tabIndex > -1)
			optionsDialog.showTab(tabIndex);
		optionsDialog.getTabbedPane().repaint();
		optionsDialog.pack();
	}

}
