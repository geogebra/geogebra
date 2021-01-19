/* 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.desktop.export;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.desktop.gui.TitlePanel;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.HelpAction;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.UtilD;

/**
 * Dialog which provides for exporting into an HTML page enriched with an
 * Applet.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class WorksheetExportDialog extends Dialog {

	/**
	 * Url for wiki article about exporting to HTML changed to GeoGebra
	 * Materials upload from ggb44
	 */
	public static final String WIKI_EXPORT_WORKSHEET = "Upload_to_GeoGebra_Materials";

	private static final long serialVersionUID = 1L;

	AppD app;
	Kernel kernel;
	private InputPanelD textAboveUpload, textBelowUpload;

	JButton uploadButton;

	boolean kernelChanged = false;
	MyTextFieldD titleField;


	private LocalizationD loc;

	/**
	 * @param app
	 *            app
	 */
	public WorksheetExportDialog(AppD app) {
		super(app.getFrame(), true);
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();


		initGUI();
	}

	private void initGUI() {

		// title, author, date
		TitlePanel titlePanel = new TitlePanel(app);
		ActionListener kernelChangedListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
			}
		};
		titlePanel.addActionListener(kernelChangedListener);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Cancel and Export Button
		JButton cancelButton = new JButton(loc.getMenu("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JButton helpButton = new JButton(loc.getMenu("Help"));
		HelpAction helpAction = new HelpAction(app,
				app.getScaledIcon(GuiResourcesD.HELP), loc.getMenu("Help"),
				WIKI_EXPORT_WORKSHEET);
		helpButton.setAction(helpAction);

		uploadButton = new JButton(loc.getMenu("Upload"));
		uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setVisible(false);
						if (kernelChanged) {
							app.storeUndoInfo();
						}

						GeoGebraTubeExportD ggtExport = new GeoGebraTubeExportD(
								app);
						ggtExport.uploadWorksheet(null);

					}
				};
				runner.start();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		buttonPanel.add(helpButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(uploadButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);

		JPanel modeUploadPanel = createUploadPanel();

		// init text of text areas
		Construction cons = kernel.getConstruction();
		String text = cons.getWorksheetText(0);
		if (text.length() > 0) {
			textAboveUpload.setText(text);
		}
		text = cons.getWorksheetText(1);
		if (text.length() > 0) {
			textBelowUpload.setText(text);
		}

		titleField.setText(cons.getTitle());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(modeUploadPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		UtilD.registerForDisposeOnEscape(this);

		setTitle(loc.getMenu("UploadGeoGebraTube"));
		setResizable(true);
		centerOnScreen();
	}

	private JPanel createUploadPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// title textfield
		titleField = new MyTextFieldD(app);

		titleField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Construction cons = kernel.getConstruction();

				if (titleField.getText().equals(cons.getTitle())) {
					return;
				}
				cons.setTitle(titleField.getText());

				kernelChanged = true;
			}
		});

		titleField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				Construction cons = kernel.getConstruction();

				if (titleField.getText().equals(cons.getTitle())) {
					return;
				}
				cons.setTitle(titleField.getText());

				kernelChanged = true;
			}

			@Override
			public void focusGained(FocusEvent e) {
				//
			}
		});

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(new JLabel(loc.getMenu("Title") + ": "),
				app.getLocalization().borderWest());
		p.add(titleField, BorderLayout.CENTER);
		panel.add(p, BorderLayout.NORTH);

		// text areas
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		JLabel label = new JLabel(loc.getMenu("TextBeforeConstruction") + ":");
		textAboveUpload = new InputPanelD(null, app, 5, 40, true,
				DialogType.TextArea);
		// JScrollPane scrollPane = new JScrollPane(textAbove);

		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textAboveUpload, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.CENTER);

		label = new JLabel(loc.getMenu("TextAfterConstruction") + ":");
		textBelowUpload = new InputPanelD(null, app, 8, 40, true,
				DialogType.TextArea);

		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textBelowUpload, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.SOUTH);

		panel.add(centerPanel, BorderLayout.CENTER);

		return panel;
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag) {
			pack();
			super.setVisible(true);
		} else {
			// store the texts of the text ares in
			// the current construction
			Construction cons = kernel.getConstruction();

			cons.setWorksheetText(textAboveUpload.getText(), 0);
			cons.setWorksheetText(textBelowUpload.getText(), 1);

			super.setVisible(false);
		}
	}

	private void centerOnScreen() {
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

}