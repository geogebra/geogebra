/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.inputbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.view.algebra.AlgebraInputDropTargetListener;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInputD extends JPanel implements ActionListener,
		KeyListener, FocusListener, SetLabels, MouseListener {
	private static final long serialVersionUID = 1L;

	protected AppD app;

	// autocompletion text field
	protected AutoCompleteTextFieldD inputField;

	private JLabel inputLabel;
	private JToggleButton btnHelpToggle;
	private InputPanelD inputPanel;
	private LocalizationD loc;

	private String autoInput;

	/***********************************************************
	 * creates new AlgebraInput
	 * 
	 * @param app
	 */
	public AlgebraInputD(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();

		app.removeTraversableKeys(this);

		initGUI();

		addMouseListener(this);
	}

	private void addPreviewListener() {
		inputPanel.getTextComponent().getDocument()
				.addDocumentListener(new DocumentListener() {

					@Override
					public void changedUpdate(DocumentEvent e) {
						preview();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						preview();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						preview();
					}

					public void preview() {
						if (!inputField.isPreviewActive()) {
							return;
						}
						setAutoInput(null);
						inputField.setBackground(Color.WHITE);
						app.getKernel().getInputPreviewHelper()
								.updatePreviewFromInputBar(inputField.getText(),
										new ErrorLogger() {
											@Override
											public void resetError() {
												showError(null);
											}

											@Override
											public void showError(String msg) {
												updateIcons(msg != null);
												btnHelpToggle.setToolTipText(
														msg == null
																? loc.getMenu(
																		"InputHelp")
																: msg);

											}

											@Override
											public void showCommandError(
													String command,
													String message) {
												updateIcons(true);
												if (((GuiManagerD) app
														.getGuiManager())
																.hasInputHelpPanel()) {
													InputBarHelpPanelD helpPanel = (InputBarHelpPanelD) ((GuiManagerD) app
															.getGuiManager())
																	.getInputHelpPanel();
													helpPanel.focusCommand(app
															.getLocalization()
															.getCommand(command));
													btnHelpToggle.setToolTipText(
															loc.getInvalidInputError());
												}
											}

											@Override
											public String getCurrentCommand() {
												return inputField.getCommand();
											}

											@Override
											public boolean onUndefinedVariables(
													String string,
													AsyncOperation<String[]> callback) {
												return false;
											}

											@Override
											public void log(Throwable e) {
												Log.debug("Preview:" + e
														.getLocalizedMessage());
											}

										});
					}
				});
	}

	@SuppressWarnings("serial")
	public void initGUI() {
		removeAll();
		inputLabel = new JLabel();
		inputPanel = new InputPanelD(null, app, 30, true);

		addPreviewListener();

		// create and set up the input field
		inputField = (AutoCompleteTextFieldD) inputPanel.getTextComponent();
		inputField.setEditable(true);
		inputField.addKeyListener(this);
		inputField.addFocusListener(this);

		// enable a history popup and embedded button
		inputField.addHistoryPopup(app.getInputPosition() == InputPosition.top);

		// enable drops
		inputField.setDragEnabled(true);
		inputField.setDropTarget(new DropTarget(this,
				new AlgebraInputDropTargetListener(app, inputField)));
		inputField.setColoringLabels(true);

		updateFonts();

		// create toggle button to hide/show the input help panel
		btnHelpToggle = new JToggleButton() {
			@Override
			public Point getToolTipLocation(MouseEvent e) {
				// make sure tooltip doesn't cover button (when window
				// maximized)
				return new Point(0, (int) -this.getSize().getHeight() / 2);
			}
		};

		updateIcons(false);

		btnHelpToggle.addActionListener(this);
		btnHelpToggle.setFocusable(false);
		btnHelpToggle.setContentAreaFilled(false);
		btnHelpToggle.setBorderPainted(false);

		// create sub-panels
		JPanel labelPanel = new JPanel(new BorderLayout());

		labelPanel.add(inputLabel, loc.borderEast());

		JPanel eastPanel = new JPanel(new BorderLayout());
		if (app.showInputHelpToggle()) {
			eastPanel.add(btnHelpToggle, loc.borderWest());
		}

		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 2));
		eastPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

		setLayout(new BorderLayout(0, 0));
		add(labelPanel, loc.borderWest());
		add(inputPanel, BorderLayout.CENTER);
		add(eastPanel, loc.borderEast());

		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				SystemColor.controlShadow));
		setLabels();
	}

	private void updateIcons(boolean warning) {
		if (btnHelpToggle == null) {
			return;
		}

		btnHelpToggle.setIcon(app.getScaledIconCommon(warning
				? GuiResourcesD.DIALOG_ERROR : GuiResourcesD.MENU_HELP));
	}

	@Override
	public boolean requestFocusInWindow() {
		return inputField.requestFocusInWindow();
	}

	@Override
	public void requestFocus() {
		requestFocusInWindow();
	}

	@Override
	public boolean hasFocus() {
		return inputField.hasFocus();
	}

	public void clear() {
		inputField.setText(null);
	}

	public AutoCompleteTextFieldD getTextField() {
		return inputField;
	}

	public void updateOrientation(boolean showInputTop) {
		inputField.setOpenSymbolTableUpwards(!showInputTop);
	}

	/**
	 * updates labels according to current locale
	 */
	@Override
	public void setLabels() {
		if (inputLabel != null) {
			inputLabel.setText(loc.getMenu("InputLabel") + ":");
		}

		if (btnHelpToggle != null) {
			btnHelpToggle.setToolTipText(loc.getMenu("InputHelp"));
		}

		inputField.setDictionary(false);
		inputField.setLabels();
	}

	public void updateFonts() {
		inputField.setFont(app.getBoldFont());
		inputLabel.setFont(app.getPlainFont());
		inputField.setPopupsFont(app.getPlainFont());

		// update the help panel
		if (((GuiManagerD) app.getGuiManager()).hasInputHelpPanel()) {
			InputBarHelpPanelD helpPanel = (InputBarHelpPanelD) ((GuiManagerD) app
					.getGuiManager()).getInputHelpPanel();
			helpPanel.updateFonts();
		}

		updateIcons(false);

	}

	// /**
	// * Inserts string at current position of the input textfield and gives
	// focus
	// * to the input textfield.
	// * @param str: inserted string
	// */
	// public void insertString(String str) {
	// inputField.replaceSelection(str);
	// }

	/**
	 * Sets the content of the input textfield and gives focus to the input
	 * textfield.
	 * 
	 * @param str
	 */
	public void replaceString(String str) {
		inputField.setText(str);
	}

	// see actionPerformed
	public void insertCommand(String cmd) {
		if (cmd == null) {
			return;
		}

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = oldText.substring(0, pos) + cmd + "[]"
				+ oldText.substring(pos);

		inputField.setText(newText);
		inputField.setCaretPosition(pos + cmd.length() + 1);
		inputField.requestFocus();
	}

	public void insertString(String str) {
		if (str == null) {
			return;
		}

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = oldText.substring(0, pos) + str
				+ oldText.substring(pos);

		inputField.setText(newText);
		inputField.setCaretPosition(pos + str.length());
		inputField.requestFocus();
	}

	/**
	 * action listener implementation for input help panel toggle button
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnHelpToggle) {

			// ==========================================
			// hidden switch to toggle native/crossPlatform LAF
			if (app.getControlDown() && app.getShiftDown()) {
				AppD.toggleCrossPlatformLAF();
				SwingUtilities.updateComponentTreeUI(app.getFrame());
				app.getFrame().pack();
				return;
			}
			// =========================================

			if (btnHelpToggle.isSelected()) {
				InputBarHelpPanelD helpPanel = (InputBarHelpPanelD) ((GuiManagerD) app
						.getGuiManager()).getInputHelpPanel();
				helpPanel.setLabels();
				helpPanel.setCommands();
				app.setShowInputHelpPanel(true);
			} else {
				app.setShowInputHelpPanel(false);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// the input field may have consumed this event
		// for auto completion
		if (e.isConsumed()) {
			return;
		}

		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_A:
		case KeyEvent.VK_C:
		case KeyEvent.VK_X:
		case KeyEvent.VK_V:
			// make sure eg Ctrl-A not passed on
			return;
		case KeyEvent.VK_ENTER:
			onEnterPressed(true);

			break;
		default:
			app.getGlobalKeyDispatcher().handleGeneralKeys(e); // handle eg
																// ctrl-tab
		}
	}

	private void onEnterPressed(boolean explicit) {
		if (!explicit && autoInput != null
				&& autoInput.equals(getTextField().getText())) {
			return;
		}
		autoInput = null;
		app.getKernel().clearJustCreatedGeosInViews();
		boolean valid = app.getKernel().getInputPreviewHelper().isValid();
		String input = app.getKernel().getInputPreviewHelper()
				.getInput(getTextField().getText());

		if (input == null || input.length() == 0) {
			app.getActiveEuclidianView().requestFocus();
			return;
		}

		app.setScrollToShow(true);
		try {
			EvalInfo info = new EvalInfo(true, true).withSliders(true)
					.addDegree(app.getKernel().getAngleUnitUsesDegrees()).withFractions(true);
			AsyncOperation<GeoElementND[]> callback =
					new InputBarCallback(app, inputField, input,
							app.getKernel().getConstructionStep());
			app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(input, true,
							getErrorHandler(valid, explicit), info, callback);

		} catch (Exception ee) {
			inputField.addToHistory(getTextField().getText());
			app.showGenericError(ee);
			return;
		} catch (MyError ee) {
			inputField.addToHistory(getTextField().getText());
			inputField.showError(ee);
			return;
		}

	}

	/**
	 * Store content of input bar in history (even when it's wrong); update
	 * autoInput to prevent resending
	 */
	protected void storeFaultyInput() {
		inputField.addToHistory(getTextField().getText());
		autoInput = getTextField().getText();
	}

	private ErrorHandler getErrorHandler(final boolean valid,
			final boolean explicit) {
		return new ErrorHandler() {

			@Override
			public void showError(String msg) {
				if (explicit) {
					app.getDefaultErrorHandler().showError(msg);
					storeFaultyInput();
				}

			}

			@Override
			public void resetError() {
				showError(null);
			}

			@Override
			public void showCommandError(String command, String message) {
				if (explicit) {
					app.getDefaultErrorHandler().showCommandError(command,
							message);
					storeFaultyInput();
				}

			}

			@Override
			public String getCurrentCommand() {
				return inputField.getCommand();
			}

			@Override
			public boolean onUndefinedVariables(String string,
					AsyncOperation<String[]> callback) {
				if (explicit) {
					if (valid) {
						return app.getGuiManager()
								.checkAutoCreateSliders(string, callback);
					} else if (loc
							.getReverseCommand(getCurrentCommand()) != null) {
						ErrorHelper.handleCommandError(loc, getCurrentCommand(),
								app.getDefaultErrorHandler());

						return false;
					}
					callback.callback(new String[] { "7" });
				}

				return false;
			}
		};
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// app.clearSelectedGeos();
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		onEnterPressed(false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//

	}

	@Override
	public void mousePressed(MouseEvent e) {
		//

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// make sure tooltips from Tool Bar don't get in the way
		setToolTipText("");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//

	}

	public void setAutoInput(String string) {
		this.autoInput = string;

	}
}