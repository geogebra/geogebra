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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.VirtualKeyboardListener;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.gui.virtualkeyboard.VirtualKeyboardD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

import com.himamis.retex.editor.share.util.Unicode;

public class InputDialogD extends InputDialog
		implements ActionListener, WindowFocusListener, VirtualKeyboardListener,
		UpdateFonts, WindowListener {

	protected AppD app;
	protected final LocalizationD loc;

	protected GeoElement geo;

	protected GeoElementSelectionListener sl;

	public static final int DEFAULT_COLUMNS = 30;

	protected InputPanelD inputPanel;
	protected JPanel optionPane;
	protected JPanel buttonsPanel;
	protected JPanel btPanel;
	protected JPanel btPanel2;

	protected JLabel msgLabel;
	protected JButton btApply;
	protected JButton btCancel;
	protected JButton btProperties;
	protected JButton btOK;
	protected JButton btHelp;

	protected JCheckBox checkBox;

	protected JDialog wrappedDialog;
	private JPanel errorPanel;

	/**
	 * @param app application
	 * @param message message
	 * @param title title
	 * @param initString initial string
	 * @param autoComplete whether to allow autocomplete
	 * @param handler input handler
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler) {
		this(app, message, title, initString, autoComplete, handler, false,
				false, null);
	}

	/**
	 * @param app application
	 * @param message message
	 * @param title title
	 * @param initString initial string
	 * @param autoComplete whether to allow autocomplete
	 * @param handler input handler
	 * @param selectInitText select text?
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean selectInitText) {
		this(app, message, title, initString, autoComplete, handler, false,
				selectInitText, null);
	}

	/**
	 * Creates a non-modal standard input dialog.
	 *
	 * @param app application
	 * @param message message
	 * @param title title
	 * @param initString initial string
	 * @param autoComplete whether to allow autocomplete
	 * @param handler input handler
	 * @param geo for properties button
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, false,
				false, geo);
	}

	/**
	 * @param app application
	 * @param message message
	 * @param title title
	 * @param initString initial string
	 * @param autoComplete whether to allow autocomplete
	 * @param handler input handler
	 * @param modal model?
	 * @param selectInitText select text?
	 * @param geo for properties button
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText, GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, modal,
				selectInitText, geo, null, DialogType.GeoGebraEditor);
	}

	/**
	 * @param app application
	 * @param message message
	 * @param title title
	 * @param initString initial string
	 * @param autoComplete whether to allow autocomplete
	 * @param handler input handler
	 * @param modal model?
	 * @param selectInitText select text?
	 * @param geo for properties button
	 * @param checkBox checkbox
	 * @param type type
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText, GeoElement geo,
			JCheckBox checkBox, DialogType type) {

		this(app.getFrame(), modal, app.getLocalization());
		this.app = app;
		this.geo = geo;
		this.setInputHandler(handler);
		this.setInitString(initString);
		this.checkBox = checkBox;

		// Build the essential GUI: a borderLayout panel with
		// a message label on top (North) and a button panel below (South).
		// An InputPanel is also created for the center panel, but this is added
		// later to allow customizing dialogs.

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true,
				selectInitText, geo != null, geo != null, type);

		// wrap inputPanel in a BorderLayout.NORTH component so it keeps a
		// single row height when resizing the dialog
		JPanel p = new JPanel(new BorderLayout());
		p.add(inputPanel, BorderLayout.NORTH);
		optionPane.add(p, BorderLayout.CENTER);

		// prepare the input panel text selection
		if (initString != null && selectInitText) {
			inputPanel.selectText();
		}

		// finalize the GUI
		centerOnScreen();

		app.setComponentOrientation(wrappedDialog);

		wrappedDialog.setResizable(true);
		wrappedDialog.pack();
	}

	/**
	 * Creates a bare-bones input dialog for highly customized dialogs.
	 * 
	 * @param frame parent frame
	 * @param modal modal?
	 */
	protected InputDialogD(JFrame frame, boolean modal, LocalizationD loc) {
		this.loc = loc;
		this.wrappedDialog = new Dialog(frame, modal) {
			@Override
			public void setVisible(boolean b) {
				super.setVisible(b);
				if (!b) {
					showError(null);
				}
				handleDialogVisibilityChange(b);
			}
		};

		wrappedDialog.addWindowListener(this);
	}

	// ===================================================
	// GUI
	// ===================================================

	protected void createGUI(String title, String message, boolean autoComplete,
			int columns, int rows, boolean showSymbolPopupIcon,
			boolean selectInitText, boolean showProperties, boolean showApply,
			DialogType type) {
		wrappedDialog.setResizable(true);

		// Create components to be displayed
		inputPanel = new InputPanelD(getInitString(), app, rows, columns,
				showSymbolPopupIcon, type);

		sl = (geo1, addToSelection) -> {
			insertGeoElement(geo1);
			inputPanel.getTextComponent().requestFocusInWindow();
		};

		// add listeners to textfield
		JTextComponent textComp = inputPanel.getTextComponent();
		if (textComp instanceof AutoCompleteTextFieldD) {
			AutoCompleteTextFieldD tf = (AutoCompleteTextFieldD) textComp;
			tf.setAutoComplete(autoComplete);
			tf.addActionListener(this);
		}

		// create buttons
		btProperties = new JButton();
		btProperties.setActionCommand("OpenProperties");
		btProperties.addActionListener(this);
		btOK = new JButton();
		btOK.setActionCommand("OK");
		btOK.addActionListener(this);
		btCancel = new JButton();
		btCancel.setActionCommand("Cancel");
		btCancel.addActionListener(this);
		btApply = new JButton();
		btApply.setActionCommand("Apply");
		btApply.addActionListener(this);

		// create button panels
		btPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		errorPanel = new JPanel(new BorderLayout(5, 5));
		buttonsPanel = new JPanel(new BorderLayout(5, 5));
		buttonsPanel.add(btPanel2, loc.borderWest()); // used for Help or
		buttonsPanel.add(btPanel, loc.borderEast());

		// add buttons to panels
		loadBtPanel(showApply);
		if (showProperties) {
			btPanel2.add(btProperties);
		}

		// =====================================================================
		// Create the optionPane: a panel with message label on top, button
		// panel on bottom. The center panel holds the inputPanel, which is
		// added later.
		// =====================================================================
		optionPane = new JPanel(new BorderLayout(5, 5));
		msgLabel = new JLabel(message);
		errorPanel.add(msgLabel, BorderLayout.NORTH);
		optionPane.add(errorPanel, BorderLayout.NORTH);
		optionPane.add(buttonsPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// add optionPane to the dialog
		wrappedDialog.setContentPane(optionPane);
		setLabels(title);
	}

	protected void loadBtPanel(boolean showApply) {
		btPanel.add(btOK);
		btPanel.add(btCancel);
		if (showApply) {
			btPanel.add(btApply);
		}
	}

	protected void centerOnScreen() {
		wrappedDialog.pack();
		// center on screen
		wrappedDialog.setLocationRelativeTo(app.getMainComponent());
	}

	public JPanel getButtonPanel() {
		return btPanel;
	}

	/**
	 * Update the labels of this component (applied if the language was
	 * changed).
	 * 
	 * @param title
	 *            The title of the dialog which is customized for every dialog
	 */
	public void setLabels(String title) {

		wrappedDialog.setTitle(title);

		btOK.setText(loc.getMenu("OK"));
		btCancel.setText(loc.getMenu("Cancel"));
		btApply.setText(loc.getMenu("Apply"));
		btProperties.setText(loc.getMenu("Properties") + Unicode.ELLIPSIS);
	}

	// ===================================================
	// Text Handlers
	// ===================================================

	/**
	 * @param geo1 construction element
	 */
	public void insertGeoElement(GeoElement geo1) {
		if (geo1 != null) {
			insertString(
					" " + geo1.getLabel(StringTemplate.defaultTemplate) + " ");
		}
	}

	@Override
	public void insertString(String str) {
		insertString(str, false);
	}

	/**
	 * @param str0 string to insert
	 * @param isLatex whether to convert to LaTeX
	 */
	public void insertString(String str0, boolean isLatex) {

		if (str0 != null) {
			String str = str0;
			if (isLatex) {
				// don't convert Greek letters
				// * JLaTeXMath can display Unicode
				// * it might be inserted in the "box" where Unicode is
				// necessary
				str = StringUtil.toLaTeXString(str, false);
			}
			inputPanel.insertString(str);
		}
	}

	public String getText() {
		return inputPanel.getText();
	}

	/**
	 * Set text for editing.
	 * @param text initial text
	 */
	public void setText(String text) {
		inputPanel.setText(text);
	}

	// ===================================================
	// Event Handlers
	// ===================================================

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// boolean finished = false;
		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				String inputText = inputPanel.getText();
				processInputHandler(inputText, ok -> setVisible(!ok));
			} else if (source == btApply) {
				String inputText = inputPanel.getText();
				processInputHandler(inputText, null);
			} else if (source == btCancel) {
				cancel();
			} else if (source == btProperties && geo != null) {
				setVisible(false);
				openProperties(app, geo);

			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
		// setVisible(!finished);
	}

	protected void cancel() {
		setVisible(false);
	}

	/**
	 * Show or hide the dialog
	 * @param flag show the dialog?
	 */
	public void setVisible(boolean flag) {
		if (!wrappedDialog.isModal()) {
			if (flag) { // set old mode again
				wrappedDialog.addWindowFocusListener(this);
			} else {
				wrappedDialog.removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}
		wrappedDialog.setVisible(flag);
	}

	/**
	 * Like setVisible, but handles selection listener
	 * @param flag whether to show dialog
	 */
	public void setVisibleForTools(boolean flag) {
		if (!wrappedDialog.isModal()) {
			if (flag) { // set old mode again
				wrappedDialog.addWindowFocusListener(this);
			} else {
				wrappedDialog.removeWindowFocusListener(this);
				app.resetCurrentSelectionListener();
			}
		}
		wrappedDialog.setVisible(flag);
	}

	// ===================================================
	// Window Focus Listeners
	// ===================================================

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setSelectionListenerMode(sl);
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(null,
				!(arg0.getOppositeWindow() instanceof VirtualKeyboardD));
	}

	public JDialog getWrappedDialog() {
		return wrappedDialog;
	}

	@Override
	public void updateFonts() {

		Font font = app.getPlainFont();

		wrappedDialog.setFont(font);

		inputPanel.updateFonts();

		btOK.setFont(font);
		btCancel.setFont(font);
		btApply.setFont(font);
		btProperties.setFont(font);

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (!wrappedDialog.isModal()) {
			app.setSelectionListenerMode(null);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// this one is actually useful, overridden in subclass(es)
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// not needed
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// not needed
	}

	/**
	 * @param isVisible whether dialog is visible
	 */
	public void handleDialogVisibilityChange(boolean isVisible) {
		if (!wrappedDialog.isModal() && !isVisible) {
			app.setSelectionListenerMode(null);
		}
	}

	private boolean showingError = false;

	@Override
	public void showError(String msg) {
		if (msg == null) {
			errorPanel.removeAll();
			showingError = false;
			errorPanel.add(msgLabel);
		} else if (!showingError) {
			showingError = true;
			errorPanel.removeAll();
			JLabel errorLabel = new JLabel(msg);
			errorLabel.setForeground(Color.RED);
			errorPanel.add(errorLabel);
		}
		SwingUtilities.updateComponentTreeUI(wrappedDialog);

	}

	@Override
	public void showCommandError(String command, String message) {
		app.getDefaultErrorHandler().showCommandError(command, message);

	}

	@Override
	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void resetError() {
		showError(null);
	}

}