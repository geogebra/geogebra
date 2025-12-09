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

package org.geogebra.web.full.cas.view;

import org.geogebra.common.cas.view.CASSubDialog;
import org.geogebra.common.cas.view.CASView;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog to substitute expressions in CAS Input.
 * 
 * @author balazs.bencze
 *
 */
public class CASSubDialogW extends CASSubDialog {
	private CASSubstituteDialogW dialog;
	private AppW app;
	private CASViewW casView;

	/**
	 * Substitute dialog for CAS.
	 * 
	 * @param casView
	 *            view
	 * @param prefix
	 *            before selection, not effected by the substitution
	 * @param evalText
	 *            the String which will be substituted
	 * @param postfix
	 *            after selection, not effected by the substitution
	 * @param editRow
	 *            row to edit
	 */
	public CASSubDialogW(CASViewW casView, String prefix, String evalText,
	        String postfix, int editRow) {
		super(prefix, evalText, postfix, editRow);

		this.casView = casView;
		this.app = casView.getApp();

		createGUI();
	}

	private void createGUI() {
		GeoCasCell cell = casView.getConsoleTable().getGeoCasCell(editRow);
		initData(cell);

		DialogData dialogData = new DialogData("Substitute", app.getLocalization()
				.getPlainDefault("RowA", "Row %0", Integer.toString(editRow + 1)),
				"Cancel", "OK");
		dialog = new CASSubstituteDialogW(app, dialogData, data);
		dialog.setOnPositiveAction(() -> apply(ACTION_SUBSTITUTE));
	}

	@Override
	protected CASView getCASView() {
		return casView;
	}

	/**
	 * @return dialog
	 */
	public CASSubstituteDialogW getDialog() {
		return dialog;
	}
}
