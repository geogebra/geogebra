package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;

public class DialogDataViewSettings extends JDialog implements ActionListener,
		WindowFocusListener {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private StatDialog statDialog;
	private JButton btnCancel, btnOK;

	private String title;
	private int mode;

	private DataViewSettingsPanel settingsPanel;

	public DialogDataViewSettings(AppD app, StatDialog statDialog, int mode) {

		// non-modal dialog (to prevent conflict with toolbar popup)
		super(app.getFrame(), app.getMenu(""), false);
		this.app = app;
		this.statDialog = statDialog;
		this.mode = mode;
		createGUI();

		this.setResizable(true);
		pack();
		setLocationRelativeTo(app.getMainComponent());

		addWindowFocusListener(this);
	}

	private void createGUI() {

		settingsPanel = new DataViewSettingsPanel(app, statDialog, mode);

		btnOK = new JButton();
		btnOK.addActionListener(this);

		btnCancel = new JButton();
		btnCancel.addActionListener(this);

		btnCancel.setText(app.getMenu("Cancel"));
		btnOK.setText(app.getMenu("OK"));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(settingsPanel, BorderLayout.CENTER);
		getContentPane().add(
				OptionsUtil.flowPanelRight(5, 5, 0, btnCancel, btnOK),
				BorderLayout.SOUTH);

	}

	@Override
	public void setVisible(boolean isVisible) {
		//app.printStacktrace("");
		if (isVisible) {
			//settingsPanel.updatePanel();
		}
		
		super.setVisible(isVisible);
		//this.requestFocusInWindow();
	}

	public void windowGainedFocus(WindowEvent e) {
		// do nothing
	}

	public void windowLostFocus(WindowEvent e) {
		this.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnOK) {
			settingsPanel.applySettings();
			setVisible(false);

		} else if (source == btnCancel) {
			setVisible(false);
		}

	}
	

}
