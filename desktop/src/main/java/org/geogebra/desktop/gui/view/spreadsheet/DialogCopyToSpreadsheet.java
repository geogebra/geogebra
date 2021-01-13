package org.geogebra.desktop.gui.view.spreadsheet;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.main.AppD;

/**
 * Dialog for selecting copy to spreadsheet options.
 * 
 * @author G. Sturr
 * 
 */
public class DialogCopyToSpreadsheet extends Dialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private SpreadsheetViewDnD dndHandler;
	private AppD app;

	private JButton btnCancel, btnCopy;
	private JRadioButton rbFree, rbDependent;

	private JCheckBox ckTranspose;

	public DialogCopyToSpreadsheet(AppD app, SpreadsheetViewDnD dndHandler) {

		super(app.getFrame(),
				app.getLocalization().getMenu("CopyToSpreadsheet"), true); // modal
																			// dialog
		this.app = app;
		this.dndHandler = dndHandler;

		createGUI();

		this.setResizable(false);
		pack();
		setLocationRelativeTo(app.getMainComponent());
		btnCopy.requestFocus();

		dndHandler.setAllowDrop(false);
	}

	private void createGUI() {

		createGUIElements();

		JPanel copyTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		copyTypePanel.add(rbFree);
		copyTypePanel.add(rbDependent);

		JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// orderTypePanel.add(rbOrderRow);
		// orderTypePanel.add(rbOrderCol);
		orderTypePanel.add(ckTranspose);

		JPanel cancelOKPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		cancelOKPanel.add(btnCopy);
		cancelOKPanel.add(btnCancel);

		Box vBox = Box.createVerticalBox();
		vBox.add(copyTypePanel);
		vBox.add(orderTypePanel);
		Localization loc = app.getLocalization();
		vBox.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 2, 5, 2),
				BorderFactory.createTitledBorder(loc.getMenu("Options"))));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(vBox, BorderLayout.CENTER);
		getContentPane().add(cancelOKPanel, BorderLayout.SOUTH);

		setLabels();
	}

	private void createGUIElements() {

		btnCopy = new JButton();
		btnCopy.addActionListener(this);
		btnCancel = new JButton();
		btnCancel.addActionListener(this);

		rbDependent = new JRadioButton();
		rbFree = new JRadioButton();
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbDependent);
		bg.add(rbFree);
		rbFree.setSelected(dndHandler.isCopyByValue());
		rbDependent.setSelected(!dndHandler.isCopyByValue());

		ckTranspose = new JCheckBox();
		ckTranspose.setSelected(dndHandler.isTranspose());

	}

	public void setLabels() {
		Localization loc = app.getLocalization();
		btnCopy.setText(loc.getMenu("Copy"));
		btnCancel.setText(loc.getMenu("Cancel"));

		rbDependent.setText(loc.getMenu("DependentObjects"));
		rbFree.setText(loc.getMenu("FreeObjects"));

		ckTranspose.setText(loc.getMenu("Transpose"));
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnCancel) {
			setVisible(false);
		} else if (source == btnCopy) {
			dndHandler.setCopyByValue(rbFree.isSelected());
			dndHandler.setTranspose(ckTranspose.isSelected());
			dndHandler.setAllowDrop(true);
			setVisible(false);
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (!isVisible) {

		}
		super.setVisible(isVisible);
	}

}
