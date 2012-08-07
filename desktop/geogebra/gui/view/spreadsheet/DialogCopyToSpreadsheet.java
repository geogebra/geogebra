package geogebra.gui.view.spreadsheet;

import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/**
 * Dialog for selecting copy to spreadsheet options.
 * 
 * @author G. Sturr
 * 
 */
public class DialogCopyToSpreadsheet extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private SpreadsheetViewDnD dndHandler;
	private AppD app;

	private JButton btnCancel, btnCopy;
	private JRadioButton rbFree, rbDependent;
	
	private JPanel optionsPanel;

	private String title;
	private JCheckBox ckTranspose;
	


	public DialogCopyToSpreadsheet(AppD app, SpreadsheetViewDnD dndHandler) {

		super(app.getFrame(), app.getMenu("CopyToSpreadsheet"), true);  // modal dialog
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
		//orderTypePanel.add(rbOrderRow);
		//orderTypePanel.add(rbOrderCol);
		orderTypePanel.add(ckTranspose);

		JPanel cancelOKPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		cancelOKPanel.add(btnCopy);
		cancelOKPanel.add(btnCancel);

		Box vBox = Box.createVerticalBox();
		vBox.add(copyTypePanel);
		vBox.add(orderTypePanel);
		vBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2), 
				BorderFactory.createTitledBorder(app.getMenu("Options"))));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(vBox, BorderLayout.CENTER);
		getContentPane().add(cancelOKPanel, BorderLayout.SOUTH);

		setLabels();
	}



	private void createGUIElements(){

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

		btnCopy.setText(app.getMenu("Copy"));
		btnCancel.setText(app.getMenu("Cancel"));

		rbDependent.setText(app.getPlain("DependentObjects"));
		rbFree.setText(app.getPlain("FreeObjects"));

		ckTranspose.setText(app.getMenu("Transpose"));
	}




	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnCancel) 
			setVisible(false);

		else if (source == btnCopy) {
			dndHandler.setCopyByValue(rbFree.isSelected());
			dndHandler.setTranspose(ckTranspose.isSelected());
			dndHandler.setAllowDrop(true);
			setVisible(false);
		} 
	}

	@Override
	public void setVisible(boolean isVisible) {	
		if(!isVisible){

		}
		super.setVisible(isVisible);
	}















}
