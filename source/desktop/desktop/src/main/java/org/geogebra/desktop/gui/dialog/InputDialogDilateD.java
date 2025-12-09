/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.view.algebra.DialogType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;

public class InputDialogDilateD extends InputDialogD {

	GeoPointND[] points;
	GeoElement[] selGeos;

	private Kernel kernel;

	private EuclidianController ec;

	/**
	 * @param app app
	 * @param title title
	 * @param handler input handler
	 * @param points points
	 * @param selGeos selected geos
	 * @param kernel kernel
	 * @param ec controller
	 */
	public InputDialogDilateD(AppD app, String title, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel,
			EuclidianController ec) {
		super(app.getFrame(), false, app.getLocalization());

		this.app = app;
		setInputHandler(handler);

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

		this.ec = ec;

		createGUI(title, loc.getMenu("Dilate.Factor"), false, DEFAULT_COLUMNS,
				1, true, false, false, false, DialogType.GeoGebraEditor);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(inputPanel, BorderLayout.CENTER);
		wrappedDialog.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				processInput();
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisibleForTools(false);
		}
	}

	private void processInput() {
		getInputHandler().processInput(inputPanel.getText(), this,
				ok -> {
					if (ok) {
						DialogManager
								.doDilate(kernel,
										((NumberInputHandler) getInputHandler())
												.getNum(),
										points, selGeos, ec);
					}
					setVisibleForTools(!ok);

				});
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.resetCurrentSelectionListener();
		}
		((GuiManagerD) app.getGuiManager()).setCurrentTextfield(this, true);
	}

	@Override
	public void handleDialogVisibilityChange(boolean isVisible) {
		// nothing to do
	}

}
