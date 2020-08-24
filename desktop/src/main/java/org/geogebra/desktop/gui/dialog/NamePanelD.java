package org.geogebra.desktop.gui.dialog;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.ObjectNameModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.dialog.options.OptionPanelD;
import org.geogebra.desktop.gui.dialog.options.OptionsObjectD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;
import org.geogebra.desktop.gui.util.SpringUtilities;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.main.AppD;

/**
 * panel for name of object
 *
 * @author Markus Hohenwarter
 */
class NamePanelD extends JPanel implements ActionListener, FocusListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts, ObjectNameModel.IObjectNameListener {

	private static final long serialVersionUID = 1L;
	/** name model */
	ObjectNameModel model;
	private AutoCompleteTextFieldD tfName, tfDefinition, tfCaption;

	private Runnable doActionStopped = new Runnable() {
		@Override
		public void run() {
			model.setBusy(false);
		}
	};
	private JLabel nameLabel, defLabel, captionLabel;
	private InputPanelD inputPanelName;
	private UpdateTabs tabs;
	private InputPanelD inputPanelDef;
	private InputPanelD inputPanelCap;
	DynamicCaptionPanelD dynamicCaptionPanel;
	private AppD app;
	private Localization loc;

	/**
	 * @param app
	 *            application
	 */
	public NamePanelD(AppD app, UpdateTabs tabs) {
		this.app = app;
		this.loc = app.getLocalization();
		model = new ObjectNameModel(app, this);
		// NAME PANEL

		// non auto complete input panel
		inputPanelName = new InputPanelD(null, app, 1, -1, true);
		this.tabs = tabs;
		tfName = (AutoCompleteTextFieldD) inputPanelName.getTextComponent();
		tfName.setAutoComplete(false);
		tfName.addActionListener(this);
		tfName.addFocusListener(this);

		// definition field: non auto complete input panel
		inputPanelDef = new InputPanelD(null, app, 1, -1, true);
		tfDefinition = (AutoCompleteTextFieldD) inputPanelDef
				.getTextComponent();
		tfDefinition.setAutoComplete(false);
		tfDefinition.addActionListener(this);
		tfDefinition.addFocusListener(this);

		// caption field: non auto complete input panel
		inputPanelCap = new InputPanelD(null, app, 1, -1, true);
		tfCaption = (AutoCompleteTextFieldD) inputPanelCap.getTextComponent();
		tfCaption.setAutoComplete(false);
		tfCaption.addActionListener(this);
		tfCaption.addFocusListener(this);

		// name panel
		nameLabel = new JLabel();
		nameLabel.setLabelFor(inputPanelName);

		// definition panel
		defLabel = new JLabel();
		defLabel.setLabelFor(inputPanelDef);

		dynamicCaptionPanel = new DynamicCaptionPanelD(app, tfCaption, tabs);
		// caption panel
		captionLabel = new JLabel();
		captionLabel.setLabelFor(inputPanelCap);

		setLabels();
		updateGUI(true, true);
	}

	@Override
	public void setLabels() {
		nameLabel.setText(loc.getMenu("Name") + ":");
		defLabel.setText(loc.getMenu("Definition") + ":");
		captionLabel.setText(loc.getMenu("Button.Caption") + ":");
		dynamicCaptionPanel.setLabels();
	}

	@Override
	public void updateGUI(boolean showDefinition, boolean showCaption) {
		int newRows = 1;
		removeAll();

		if (loc.isRightToLeftReadingOrder()) {
			add(inputPanelName);
			add(nameLabel);
		} else {
			add(nameLabel);
			add(inputPanelName);
		}

		if (showDefinition) {
			newRows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelDef);
				add(defLabel);
			} else {
				add(defLabel);
				add(inputPanelDef);
			}
		}

		if (showCaption) {
			newRows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelCap);
				add(captionLabel);
			} else {
				add(captionLabel);
				add(inputPanelCap);
			}
		}

		this.rows = newRows;
		setLayout();
		app.setComponentOrientation(this);
	}

	private int rows;

	private void setLayout() {
		// Lay out the panel
		setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this, rows, 2, // rows, cols
				5, 5, // initX, initY
				5, 5); // xPad, yPad
	}

	/**
	 * current geo on which focus lost shouls apply (may be different to current
	 * geo, due to threads)
	 */
	private GeoElementND currentGeoForFocusLost = null;

	@Override
	public JPanel updatePanel(Object[] geos) {

		/*
		 * DON'T WORK : MAKE IT A TRY FOR 5.0 ? //apply textfields modification
		 * on previous geo before switching to new geo //skip this if label is
		 * not set (we re in the middle of redefinition) //skip this if action
		 * is performing if (currentGeo!=null && currentGeo.isLabelSet() &&
		 * !actionPerforming && (geos.length!=1 || geos[0]!=currentGeo)){
		 *
		 * //App.printStacktrace("\n"+tfName.getText()+"\n"+currentGeo.getLabel(
		 * StringTemplate.defaultTemplate));
		 *
		 * String strName = tfName.getText(); if (strName !=
		 * currentGeo.getLabel(StringTemplate.defaultTemplate))
		 * nameInputHandler.processInput(tfName.getText());
		 *
		 *
		 * String strDefinition = tfDefinition.getText(); if
		 * (strDefinition.length()>0 &&
		 * !strDefinition.equals(getDefText(currentGeo)))
		 * defInputHandler.processInput(strDefinition);
		 *
		 * String strCaption = tfCaption.getText(); if
		 * (!strCaption.equals(currentGeo.getCaptionSimple())){
		 * currentGeo.setCaption(tfCaption.getText());
		 * currentGeo.updateVisualStyleRepaint(); } }
		 */

		model.setGeos(geos);
		if (!model.checkGeos()) {
			// currentGeo=null;
			return null;
		}

		model.updateProperties();

		return this;
	}

	private String redefinitionForFocusLost = "";

	/**
	 * @param geo
	 *            element
	 */
	public void updateDef(GeoElementND geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy()) {
			return;
		}

		tfDefinition.removeActionListener(this);
		model.getDefInputHandler().setGeoElement(geo);
		String text = ObjectNameModel.getDefText(geo);
		if (app.isMacOS()) {
			if (app.isMacOS() && text.length() > 300) {
				text = text.substring(0, 300);
				tfDefinition.setEditable(false);
			} else {
				tfDefinition.setEditable(true);
			}
		}

		tfDefinition.setText(text);
		tfDefinition.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	/**
	 * @param geo
	 *            element
	 */
	public void updateName(GeoElement geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy()) {
			return;
		}

		tfName.removeActionListener(this);
		model.getNameInputHandler().setGeoElement(geo);
		tfName.setText(geo.getLabel(StringTemplate.editTemplate));
		tfName.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (model.isBusy()) {
			return;
		}

		doActionPerformed(e.getSource());
	}

	private synchronized void doActionPerformed(Object source) {

		model.setBusy(true);

		if (source == tfName) {
			// rename
			model.applyNameChange(tfName.getText(),
					app.getDefaultErrorHandler());

		} else if (source == tfDefinition) {

			model.applyDefinitionChange(tfDefinition.getText(),
					app.getDefaultErrorHandler());
			tfDefinition.requestFocusInWindow();

		} else if (source == tfCaption) {
			model.applyCaptionChange(tfCaption.getText());
			if (!"".equals(tfCaption.getText())) {
				GeoElement geo0 = model.getGeoAt(0);
				geo0.setLabelVisible(true);
				geo0.setLabelMode(GeoElement.LABEL_CAPTION);

				OptionPanelD op = ((PropertiesViewD) app.getGuiManager()
						.getPropertiesView())
								.getOptionPanel(OptionType.OBJECTS);

				if (op != null && op instanceof OptionsObjectD) {
					PropertiesPanelD propPanel = ((OptionsObjectD) op)
							.getPropPanel();
					if (propPanel != null) {
						propPanel.getLabelPanel().update(true, true, 0);
					}
				}

			}
		}

		SwingUtilities.invokeLater(doActionStopped);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// started to type something : store current geo if focus lost
		currentGeoForFocusLost = model.getCurrentGeo();
	}

	@Override
	public void focusLost(FocusEvent e) {

		if (model.isBusy()) {
			return;
		}

		Object source = e.getSource();

		if (source == tfDefinition) {
			// currentGeo may has changed if focus is lost by clicking another
			// geo

			if (!tfDefinition.isEditable()) {
				return;
			}

			if (model.getCurrentGeo() == currentGeoForFocusLost) {
				model.applyDefinitionChange(tfDefinition.getText(),
						app.getDefaultErrorHandler());
			} else {
				model.redefineCurrentGeo(currentGeoForFocusLost,
						tfDefinition.getText(), redefinitionForFocusLost,
						app.getDefaultErrorHandler());
			}

			SwingUtilities.invokeLater(doActionStopped);

		} else {
			doActionPerformed(source);
		}
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		nameLabel.setFont(font);
		defLabel.setFont(font);
		captionLabel.setFont(font);

		inputPanelName.updateFonts();
		inputPanelDef.updateFonts();
		inputPanelCap.updateFonts();
		dynamicCaptionPanel.updateFonts();
		setLayout();

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// NOTHING SHOULD BE DONE HERE (ENDLESS CALL WITH UPDATE)

	}

	@Override
	public void setNameText(final String text) {
		tfName.setText(text);
		tfName.requestFocus();
	}

	@Override
	public void setDefinitionText(final String text) {
		tfDefinition.setText(text);
	}

	@Override
	public void setCaptionText(final String text) {
		tfCaption.setText(text);
		tfCaption.requestFocus();
	}

	@Override
	public void updateCaption(String text) {
		tfCaption.removeActionListener(this);
		tfCaption.setText(text);
		tfCaption.addActionListener(this);
	}

	@Override
	public void updateDefLabel() {
		updateDef(model.getCurrentGeo());

		if (model.getCurrentGeo().isIndependent()) {
			defLabel.setText(loc.getMenu("Value") + ":");
		} else {
			defLabel.setText(loc.getMenu("Definition") + ":");
		}
	}

	@Override
	public void updateName(String text) {
		tfName.removeActionListener(this);
		tfName.setText(text);

		// if a focus lost is called in between, we keep the current definition
		// text
		redefinitionForFocusLost = tfDefinition.getText();
		tfName.addActionListener(this);

	}

	public DynamicCaptionPanelD getDynamicCaptionPanel() {
		return dynamicCaptionPanel;
	}
}
