/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.gui.VirtualKeyboardListener;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.OptionType;
import geogebra.common.util.StringUtil;
import geogebra.gui.GuiManagerD;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.util.HelpAction;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

public class InputDialogD extends geogebra.common.gui.dialog.InputDialog implements ActionListener,
		WindowFocusListener, VirtualKeyboardListener, UpdateFonts {

	protected AppD app;

	protected GeoElement geo;

	protected GeoElementSelectionListener sl;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;

	protected InputPanelD inputPanel;
	protected JPanel optionPane, buttonsPanel, btPanel, btPanel2;

	protected JLabel msgLabel;
	protected JButton btApply, btCancel, btProperties, btOK, btHelp;

	protected JCheckBox checkBox;
	
	protected JDialog wrappedDialog;

	/**
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param autoComplete
	 * @param handler
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler) {
		this(app, message, title, initString, autoComplete, handler, false,
				false, null);
	}

	
	/**
	 * Creates a non-modal standard input dialog.
	 * 
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param autoComplete
	 * @param handler
	 * @param geo
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, false,
				false, geo);
	}

	
	/**
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param autoComplete
	 * @param handler
	 * @param modal
	 * @param selectInitText
	 * @param geo
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText, GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, modal,
				selectInitText, geo, null, DialogType.GeoGebraEditor);
	}

	/**
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param autoComplete
	 * @param handler
	 * @param modal
	 * @param selectInitText
	 * @param geo
	 * @param checkBox
	 * @param type
	 */
	public InputDialogD(AppD app, String message, String title,
			String initString, boolean autoComplete, InputHandler handler,
			boolean modal, boolean selectInitText, GeoElement geo,
			JCheckBox checkBox, DialogType type) {

		this(app.getFrame(), modal);
		this.app = app;
		this.geo = geo;
		this.inputHandler = handler;
		this.initString = initString;
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
		wrappedDialog.setResizable(true);
		wrappedDialog.pack();
	}

	/**
	 * Creates a bare-bones input dialog for highly customized dialogs.
	 * 
	 * @param frame
	 * @param modal
	 */
	protected InputDialogD(JFrame frame, boolean modal) {
		this.wrappedDialog = new JDialog(frame, modal);
	}

	// ===================================================
	// GUI
	// ===================================================

	/**
	 * @param title
	 * @param message
	 * @param autoComplete
	 * @param columns
	 * @param rows
	 * @param showSymbolPopupIcon
	 * @param selectInitText
	 * @param showProperties
	 * @param showApply
	 * @param type
	 */
	protected void createGUI(String title, String message,
			boolean autoComplete, int columns, int rows,
			boolean showSymbolPopupIcon, boolean selectInitText,
			boolean showProperties, boolean showApply, DialogType type) {
		wrappedDialog.setResizable(true);

		// Create components to be displayed
		inputPanel = new InputPanelD(initString, app, rows, columns,
				showSymbolPopupIcon, type);

		sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo,
					boolean addToSelection) {
				insertGeoElement(geo);
				inputPanel.getTextComponent().requestFocusInWindow();
			}
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
		buttonsPanel = new JPanel(new BorderLayout(5, 5));
		buttonsPanel.add(btPanel2, BorderLayout.WEST); // used for Help or
		buttonsPanel.add(btPanel, BorderLayout.EAST);

		// add buttons to panels
		loadBtPanel(showApply);
		if (showProperties)
			btPanel2.add(btProperties);

		// =====================================================================
		// Create the optionPane: a panel with message label on top, button
		// panel on bottom. The center panel holds the inputPanel, which is
		// added later.
		// =====================================================================
		optionPane = new JPanel(new BorderLayout(5, 5));
		msgLabel = new JLabel(message);
		optionPane.add(msgLabel, BorderLayout.NORTH);
		optionPane.add(buttonsPanel, BorderLayout.SOUTH);
		optionPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// add optionPane to the dialog
		wrappedDialog.setContentPane(optionPane);
		setLabels(title);
	}

	protected void loadBtPanel(boolean showApply) {
		btPanel.add(btOK);
		btPanel.add(btCancel);
		if (showApply)
			btPanel.add(btApply);
	}

	protected void centerOnScreen() {
		wrappedDialog.pack();
		// center on screen
		wrappedDialog.setLocationRelativeTo(app.getMainComponent());
	}

	public void showSymbolTablePopup(boolean flag) {
		inputPanel.showSpecialChars(flag);
	}

	public void addHelpButton(String articleName) {
		btHelp = new JButton();
		HelpAction helpAction = new HelpAction(app,
				app.getImageIcon("help.png"), app.getMenu("Help"), articleName);
		btHelp.setAction(helpAction);
		btPanel2.add(btHelp);
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

		btOK.setText(app.getPlain("OK"));
		btCancel.setText(app.getPlain("Cancel"));
		btApply.setText(app.getPlain("Apply"));
		btProperties.setText(app.getPlain("Properties") + "...");
	}

	// ===================================================
	// Text Handlers
	// ===================================================

	public void insertGeoElement(GeoElement geo) {
		if (geo != null)
			insertString(" " + geo.getLabel(StringTemplate.defaultTemplate)
					+ " ");
	}

	public void insertString(String str) {
		insertString(str, false);
	}

	public void insertString(String str, boolean isLatex) {

		boolean convertGreekLetters = !app.getLocale().getLanguage()
				.equals("gr");
		if (str != null) {
			if (isLatex) {
				str = StringUtil.toLaTeXString(str, convertGreekLetters);
			}
			inputPanel.insertString(str);
		}
	}

	public String getInputString() {
		return inputText;
	}

	public String getText() {
		return inputPanel.getText();
	}

	public void setText(String text) {
		inputPanel.setText(text);
	}

	public void selectText() {
		inputPanel.selectText();
	}

	public void setCaretPosition(int pos) {
		JTextComponent tc = inputPanel.getTextComponent();
		tc.setCaretPosition(pos);
		tc.requestFocusInWindow();
	}

	public void setRelativeCaretPosition(int pos) {
		JTextComponent tc = inputPanel.getTextComponent();
		try {
			tc.setCaretPosition(tc.getCaretPosition() + pos);
		} catch (Exception e) {
		}
		tc.requestFocusInWindow();
	}

	// ===================================================
	// Event Handlers
	// ===================================================

	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// boolean finished = false;
		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();
				setVisible(!processInputHandler());
			} else if (source == btApply) {
				inputText = inputPanel.getText();
				processInputHandler();
			} else if (source == btCancel) {
				setVisible(false);
			} else if (source == btProperties && geo != null) {
				setVisible(false);
				tempArrayList.clear();
				tempArrayList.add(geo);
				app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, tempArrayList);

			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisible(false);
		}
		// setVisible(!finished);
	}

	

	// ===================================================
	// Visibility Handlers
	// ===================================================

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

	public void setVisibleForTools(boolean flag) {
		if (!wrappedDialog.isModal()) {
			if (flag) { // set old mode again
				wrappedDialog.addWindowFocusListener(this);
			} else {
				wrappedDialog.removeWindowFocusListener(this);
				app.setCurrentSelectionListener(null);
			}
		}
		wrappedDialog.setVisible(flag);
	}

	// ===================================================
	// Window Focus Listeners
	// ===================================================

	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setSelectionListenerMode(sl);
		}
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(this, true);
	}

	public void windowLostFocus(WindowEvent arg0) {
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(null,
				!(arg0.getOppositeWindow() instanceof VirtualKeyboard));
	}
	
	public JDialog getWrappedDialog() {
		return wrappedDialog;
	}


	public void updateFonts() {
		
		Font font = app.getPlainFont();
		

		wrappedDialog.setFont(font);
		
		inputPanel.updateFonts();

		btOK.setFont(font);
		btCancel.setFont(font);
		btApply.setFont(font);
		btProperties.setFont(font);
		
	}





}