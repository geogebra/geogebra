/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.editor.GeoGebraEditorPane;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.gui.view.algebra.MyComboBoxListener;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

public class ButtonDialogD extends Dialog
		implements ActionListener, KeyListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextComponent tfCaption, tfScript;
	private JPanel btPanel;
	// private DefaultListModel listModel;
	private ButtonDialogModel model;
	private DefaultComboBoxModel comboModel;

	private JButton btOK, btCancel;
	private JPanel optionPane;

	private AppD app;

	InputPanelD inputPanel, inputPanel2;
	private LocalizationD loc;

	/**
	 * Creates a dialog to create a new GeoNumeric for a slider.
	 * 
	 * @param app
	 *            application
	 * 
	 * @param x
	 *            location of button in screen coords
	 * @param y
	 *            location of button in screen coords
	 * @param textField
	 *            whether we need an input box
	 */
	public ButtonDialogD(AppD app, int x, int y, boolean textField) {
		super(app.getFrame(), false);
		this.app = app;
		this.loc = app.getLocalization();
		// this.textField = textField;
		addWindowListener(this);
		model = new ButtonDialogModel(app, x, y, textField);
		// create temp geos that may be returned as result
		// Construction cons = app.getKernel().getConstruction();
		// button = textField ? new GeoTextField(cons) : new GeoButton(cons);
		// button.setEuclidianVisible(true);
		// button.setAbsoluteScreenLoc(x, y);

		createGUI();
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	private void createGUI() {
		setTitle(model.getTitle());
		setResizable(true);

		// create caption panel
		JLabel captionLabel = new JLabel(loc.getMenu("Button.Caption") + ":");
		InputPanelD ip = new InputPanelD(model.getInitString(), app, 1, 25,
				true);
		tfCaption = ip.getTextComponent();
		if (tfCaption instanceof AutoCompleteTextFieldD) {
			AutoCompleteTextFieldD atf = (AutoCompleteTextFieldD) tfCaption;
			atf.setAutoComplete(false);
		}

		captionLabel.setLabelFor(tfCaption);
		JPanel captionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		captionPanel.add(captionLabel);
		captionPanel.add(ip);

		// combo box to link GeoElement to TextField
		comboModel = new DefaultComboBoxModel();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction()
				.getGeoSetNameDescriptionOrder();

		final JComboBox cbAdd = new JComboBox(comboModel);

		if (model.isTextField()) {
			// lists for combo boxes to select input and output objects
			// fill combobox models
			Iterator<GeoElement> it = sortedSet.iterator();
			comboModel.addElement(null);
			FontMetrics fm = getFontMetrics(getFont());

			// minimum width
			// make sure if there are just objects with short descriptions
			// eg sliders
			// then they display OK
			int width = app.getGUIFontSize() * 10;

			while (it.hasNext()) {
				GeoElement geo = it.next();
				if (!geo.isGeoImage() && !(geo.isGeoButton())
						&& !(geo.isGeoBoolean())) {
					comboModel.addElement(geo);
					String str = geo.toString(StringTemplate.defaultTemplate);
					if (width < fm.stringWidth(str)) {
						width = fm.stringWidth(str);
					}
				}
			}

			// make sure it's not too wide (eg long GeoList)
			Dimension size = new Dimension(
					Math.min(AppD.getScreenSize().width / 2, width),
					cbAdd.getPreferredSize().height);
			cbAdd.setMaximumSize(size);
			cbAdd.setPreferredSize(size);

			if (comboModel.getSize() > 1) {

				// listener for the combobox
				MyComboBoxListener ac = new MyComboBoxListener() {
					@Override
					public void doActionPerformed(Object source) {
						model.setLinkedGeo(
								(GeoElement) cbAdd.getSelectedItem());
						// cbAdd.removeActionListener(this);
						//
						// cbAdd.setSelectedItem(null);
						// cbAdd.addActionListener(this);
					}
				};
				cbAdd.addActionListener(ac);
				cbAdd.addMouseListener(ac);

				captionPanel.add(cbAdd);
			}
		}

		// create script panel
		JLabel scriptLabel = new JLabel(loc.getMenu("Script") + ":");
		// XXX Remark 1: This has been incorrect as it assumes the click script
		// is GgbScript. However I'm only adapting it to the new scripting
		// structure so it will need to be dealt with later

		InputPanelD ip2 = new InputPanelD(model.getClickScript(), app, 10, 40,
				false);
		Dimension dim = ((GeoGebraEditorPane) ip2.getTextComponent())
				.getPreferredSizeFromRowColumn(10, 40);
		ip2.setPreferredSize(dim);

		ip2.setShowLineNumbering(true);
		tfScript = ip2.getTextComponent();
		// add a small margin
		tfScript.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		if (tfScript instanceof AutoCompleteTextFieldD) {
			AutoCompleteTextFieldD atf = (AutoCompleteTextFieldD) tfScript;
			atf.setAutoComplete(false);
		}

		scriptLabel.setLabelFor(tfScript);
		JPanel scriptPanel = new JPanel(new BorderLayout(5, 5));
		scriptPanel.add(scriptLabel, BorderLayout.NORTH);
		scriptPanel.add(ip2, BorderLayout.CENTER);
		scriptPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel linkedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel linkedLabel = new JLabel(loc.getMenu("LinkedObject") + ":");
		linkedPanel.add(linkedLabel);
		linkedPanel.add(cbAdd);

		// buttons
		btOK = new JButton(loc.getMenu("OK"));
		btOK.setActionCommand("OK");
		btOK.addActionListener(this);
		btCancel = new JButton(loc.getMenu("Cancel"));
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btPanel.add(btOK);
		btPanel.add(btCancel);

		// Create the JOptionPane.
		optionPane = new JPanel(new BorderLayout(5, 5));

		// create object list
		optionPane.add(captionPanel, BorderLayout.NORTH);
		if (model.isTextField()) {
			optionPane.add(linkedPanel, BorderLayout.CENTER);
		} else {
			optionPane.add(scriptPanel, BorderLayout.CENTER);
		}
		optionPane.add(btPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Make this dialog display it.
		setContentPane(optionPane);

		// TODO: for buttons too when script panel works in RTL
		if (model.isTextField()) {
			app.setComponentOrientation(this);
		}
		/*
		 * 
		 * inputPanel = new InputPanel("ggbApplet.evalCommand('A=(3,4)');", app,
		 * 10, 50, false, true, false ); inputPanel2 = new InputPanel(
		 * "function func() {\n}", app, 10, 50, false, true, false );
		 * 
		 * JPanel centerPanel = new JPanel(new BorderLayout());
		 * 
		 * centerPanel.add(inputPanel, BorderLayout.CENTER);
		 * centerPanel.add(inputPanel2, BorderLayout.SOUTH);
		 * getContentPane().add(centerPanel, BorderLayout.CENTER);
		 * //centerOnScreen();
		 * 
		 * setContentPane(centerPanel); pack();
		 * setLocationRelativeTo(app.getFrame());
		 */
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		Log.debug(tfScript.getText());
		if (source == btOK) {
			model.apply(tfCaption.getText(), tfScript.getText());
			setVisible(false);
		} else if (source == btCancel) {
			setVisible(false);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		default:
			// do nothing
			break;
		case KeyEvent.VK_ENTER:
			btOK.doClick();
			break;

		case KeyEvent.VK_ESCAPE:
			btCancel.doClick();
			e.consume();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// nothing to do
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// setLabelFieldFocus();
	}

}