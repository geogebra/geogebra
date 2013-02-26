/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.gui.dialog;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/**
 * Dialog to create a GeoBoolean object (checkbox) that determines the
 * visibility of a list of objects.
 */
public class CheckboxCreationDialog extends JDialog implements
		WindowFocusListener, ActionListener, GeoElementSelectionListener {

	private static final long serialVersionUID = 1L;

	private JTextComponent tfCaption;
	private JButton btApply, btCancel;
	private JPanel optionPane, btPanel;
	private DefaultListModel listModel;
	private DefaultComboBoxModel comboModel;

	private Point location;
	private AppD app;
	private GeoBoolean geoBoolean;

	/**
	 * Input Dialog for a GeoText object
	 */
	public CheckboxCreationDialog(AppD app, Point location,
			GeoBoolean geoBoolean) {
		super(app.getFrame(), false);
		this.app = app;
		this.location = location;
		this.geoBoolean = geoBoolean;

		initLists();
		createGUI(app.getMenu("ShowCheckBox"));
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	private void initLists() {
		// fill combo box with all geos
		comboModel = new DefaultComboBoxModel();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();

		// lists for combo boxes to select input and output objects
		// fill combobox models
		Iterator<GeoElement> it = sortedSet.iterator();
		comboModel.addElement(null);
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isEuclidianShowable()) {
				comboModel.addElement(geo);
			}
		}

		// fill list with all selected geos
		listModel = new DefaultListModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public void addElement(Object ob) {
				if (contains(ob))
					return;

				if (ob instanceof GeoElement) {
					GeoElement geo = (GeoElement) ob;
					if (geo.isEuclidianShowable()) {
						super.addElement(geo);
						comboModel.removeElement(geo);
					}
				}
			}
		};

		// add all selected geos to list
		for (int i = 0; i < app.getSelectionManager().getSelectedGeos().size(); i++) {
			GeoElement geo = app.getSelectionManager().getSelectedGeos().get(i);
			listModel.addElement(geo);
		}
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		listModel.addElement(geo);
	}

	protected void createGUI(String title) {
		setTitle(title);
		setResizable(true);

		// create caption panel
		JLabel captionLabel = new JLabel(app.getMenu("Button.Caption") + ":");
		String initString = geoBoolean == null ? "" : geoBoolean.getCaption(StringTemplate.defaultTemplate);
		InputPanelD ip = new InputPanelD(initString, app, 1, 15, true);
		tfCaption = ip.getTextComponent();
		if (tfCaption instanceof AutoCompleteTextFieldD) {
			AutoCompleteTextFieldD atf = (AutoCompleteTextFieldD) tfCaption;
			atf.setAutoComplete(false);
		}

		captionLabel.setLabelFor(tfCaption);
		JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		captionPanel.add(captionLabel);
		captionPanel.add(ip);

		// list panel
		JPanel listPanel = ToolCreationDialog.createInputOutputPanel(app,
				listModel, comboModel, false, false, null);

		// buttons
		btApply = new JButton(app.getPlain("Apply"));
		btApply.setActionCommand("Apply");
		btApply.addActionListener(this);
		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btApply);
		btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));

		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		optionPane.add(listPanel, BorderLayout.CENTER);
		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Make this dialog display it.
		setContentPane(optionPane);
		
		app.setComponentOrientation(this);
	}

	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if (src == btCancel) {
			setVisible(false);
		} else if (src == btApply) {
			apply();
			setVisible(false);
		}
	}

	private void apply() {
		// create new GeoBoolean
		if (geoBoolean == null) {
			geoBoolean = new GeoBoolean(app.getKernel().getConstruction());
			geoBoolean.setAbsoluteScreenLoc(location.x, location.y);
			geoBoolean.setLabel(null);
		}

		// set visibility condition for all GeoElements in list
		try {
			for (int i = 0; i < listModel.size(); i++) {
				GeoElement geo = (GeoElement) listModel.get(i);
				geo.setShowObjectCondition(geoBoolean);
			}
		} catch (CircularDefinitionException e) {
			app.showError("CircularDefinition");
		}

		// set caption text
		String strCaption = tfCaption.getText().trim();
		if (strCaption.length() > 0) {
			geoBoolean.setCaption(strCaption);
		}

		// update boolean (updates visibility of geos from list too)
		geoBoolean.setValue(true);
		geoBoolean.setEuclidianVisible(true);
		geoBoolean.setLabelVisible(true);
		geoBoolean.updateRepaint();

		app.storeUndoInfo();
	}

	public void windowGainedFocus(WindowEvent arg0) {
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER
				|| app.getCurrentSelectionListener() != this) {
			app.setSelectionListenerMode(this);
		}
	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	@Override
	public void setVisible(boolean flag) {
		if (!isModal()) {
			if (flag) { // set old mode again
				addWindowFocusListener(this);
			} else {
				removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
				app.setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX);
			}
		}
		super.setVisible(flag);
	}

}
