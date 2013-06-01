/* 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.export;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.TitlePanel;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.HelpAction;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;
import geogebra.util.Util;

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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog which provides for exporting into an HTML page enriched with an
 * Applet.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class WorksheetExportDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	AppD app;
	Kernel kernel;
	private InputPanelD textAboveUpload, textBelowUpload;

	JButton uploadButton;

	private JButton helpButton;
	private GraphicSizePanel sizePanel;
	boolean kernelChanged = false;
	private GeoGebraPreferencesD ggbPref;
	private GuiManagerD guiManager;
	MyTextField titleField;
	private TitlePanel titlePanel;

	//private JTabbedPane modeSwitch;
	private JPanel modeUploadPanel;

	/**
	 * @param app app
	 */
	public WorksheetExportDialog(AppD app) {
		super(app.getFrame(), true);
		this.app = app;
		kernel = app.getKernel();

		ggbPref = GeoGebraPreferencesD.getPref();
		guiManager = app.getGuiManager();

		initGUI();
	}

	private void initGUI() {

		// title, author, date
		titlePanel = new TitlePanel(app);
		ActionListener kernelChangedListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
			}
		};
		titlePanel.addActionListener(kernelChangedListener);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Cancel and Export Button
		JButton cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		helpButton = new JButton(app.getMenu("Help"));
		HelpAction helpAction = new HelpAction(app, app.getImageIcon("help.png"),
				app.getMenu("Help"), App.WIKI_EXPORT_WORKSHEET);
		helpButton.setAction(helpAction);

		uploadButton = new JButton(app.getPlain("Upload"));
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setVisible(false);
						if (kernelChanged)
							app.storeUndoInfo();


						GeoGebraTubeExportDesktop ggtExport = new GeoGebraTubeExportDesktop(app);
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

		modeUploadPanel = createUploadPanel();



		// init text of text areas
		Construction cons = kernel.getConstruction();
		String text = cons.getWorksheetText(0);
		if (text.length() > 0)
			textAboveUpload.setText(text);
		text = cons.getWorksheetText(1);
		if (text.length() > 0)
			textBelowUpload.setText(text);

		titleField.setText(cons.getTitle());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(modeUploadPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		Util.registerForDisposeOnEscape(this);

		setTitle(app.getMenu("UploadGeoGebraTube"));
		setResizable(true);
		centerOnScreen();
	}


	private JPanel createUploadPanel() {
		JPanel panel = new JPanel(new BorderLayout(5,5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// title textfield
		titleField = new MyTextField(app);

		titleField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Construction cons = kernel.getConstruction();

				if (titleField.getText().equals(cons.getTitle()))
					return;
				cons.setTitle(titleField.getText());

				kernelChanged = true;
			}
		});

		titleField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				Construction cons = kernel.getConstruction();

				if (titleField.getText().equals(cons.getTitle()))
					return;
				cons.setTitle(titleField.getText());

				kernelChanged = true;
			}

			public void focusGained(FocusEvent e) {
				//
			}
		});

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(new JLabel(app.getPlain("Title") + ": "), app.borderWest());
		p.add(titleField, BorderLayout.CENTER);
		panel.add(p, BorderLayout.NORTH);

		// text areas
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		JLabel label = new JLabel(app.getPlain("TextBeforeConstruction") + ":");
		textAboveUpload = new InputPanelD(null, app, 5, 40, true, DialogType.TextArea);
		// JScrollPane scrollPane = new JScrollPane(textAbove);

		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textAboveUpload, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.CENTER);

		label = new JLabel(app.getPlain("TextAfterConstruction") + ":");
		textBelowUpload = new InputPanelD(null, app, 8, 40, true, DialogType.TextArea);

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