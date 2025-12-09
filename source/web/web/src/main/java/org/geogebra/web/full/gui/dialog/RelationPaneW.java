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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.HTML;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Web implementation of the Relation Tool dialog
 */

public class RelationPaneW extends ComponentDialog
		implements RelationPane, FastClickHandler {
	private Relation[] callbacks;
	private FlowPanel numerical;
	private int rels;

	/**
	 * @param app see {@link AppW}
	 * @param data dialog data
	 */
	public RelationPaneW(AppW app, DialogData data) {
		super(app, data, false, false);
		addStyleName("relationDialog");
	}

	@Override
	public void showDialog(String title, RelationRow[] relations, App app1) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("relationContent");

		numerical = new FlowPanel();
		Label numHeader = getHeader("RelationDialog.NumCheck");
		numerical.add(numHeader);

		for (RelationRow rel : relations) {
			Label relation = new Label();
			String info = rel.getInfo();
			relation.getElement().setInnerHTML(info);
			numerical.add(relation);
		}

		mainPanel.add(numerical);

		if (hasSymbolicSolution(relations)) {
			StandardButton checkSym = new StandardButton(app.getLocalization()
					.getMenu("RelationDialog.CheckSymbolically"));
			checkSym.addStyleName("materialTextButton");
			checkSym.addStyleName("checkSymBtn");
			checkSym.addFastClickHandler(this);
			mainPanel.add(checkSym);
		} else {
			addStyleName("numericOnly");
		}

		rels = relations.length;

		callbacks = new Relation[rels];
		for (int i = 0; i < rels; ++i) {
			callbacks[i] = relations[i].getCallback();
		}
		setDialogContent(mainPanel);
		show();
	}

	private Label getHeader(String label) {
		Label header = new Label();
		header.addStyleName("headerLbl");
		header.getElement().setInnerHTML(app.getLocalization()
				.getMenu(label));
		return header;
	}

	private boolean hasSymbolicSolution(RelationRow[] relations) {
		for (RelationRow row : relations) {
			if (row.getCallback() != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onClick(final Widget source) {
		((AppW) app).getAsyncManager().asyncEvalCommand("Delete(Prove(true))",
				(_unused) -> {
					LoggerW.loaded("prover");
					emptyAndRebuildContent();
					for (int i = 0; i < rels; ++i) {
						expandRow(i);
					}
				}, null);
	}

	private void emptyAndRebuildContent() {
		setDialogContent(numerical);

		SimplePanel divider = new SimplePanel();
		divider.addStyleName("divider");
		divider.addStyleName(Shades.NEUTRAL_300.getName());
		addDialogContent(divider);

		Label symHeader = getHeader("RelationDialog.SymCheck");
		addDialogContent(symHeader);
	}

	/**
	 * Add symbolical solutions
	 * @param row - row number
	 */
	protected void expandRow(int row) {
		RelationRow relation = callbacks[row].getExpandedRow(row);
		HTML text = new HTML(relation.getInfo());
		addDialogContent(text);
	}
}
