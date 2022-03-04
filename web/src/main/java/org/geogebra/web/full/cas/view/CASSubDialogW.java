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
