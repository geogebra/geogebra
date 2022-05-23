package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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
