package geogebra.gui.layout;

import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.gui.toolbar.ToolbarConfigDialog;
import geogebra.main.AppD;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class CustomToolBarPanel extends JPanel {

	private AppD app;

	private OptionsDialog.Factory optionsDialogFactory;
	private OptionsDialog optionsDialog;

	private ToolbarConfigDialog toolDialog;


	public CustomToolBarPanel(AppD app) {

		this.app = app;
		setLayout(new BorderLayout());

		toolDialog = new ToolbarConfigDialog(app);

		add(toolDialog.getContentPane(), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 40, 10));
		toolDialog.pack();
	}


}
