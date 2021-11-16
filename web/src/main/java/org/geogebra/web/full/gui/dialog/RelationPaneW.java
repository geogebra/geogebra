package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.kernel.Relation;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.debug.LoggerW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web implementation of the Relation Tool dialog
 */

public class RelationPaneW extends ComponentDialog
		implements RelationPane, ClickHandler {
	private Button[] btnCallbacks;
	private Relation[] callbacks;
	private int rels;
	private FlowPanel[] texts;
	private FlowPanel[] buttons;

	/**
	 * @param app see {@link AppW}
	 * @param data dialog data
	 */
	public RelationPaneW(AppW app, DialogData data) {
		super(app, data, false, false);
	}

	@Override
	public void showDialog(String title, RelationRow[] relations, App app1) {
		GDialogBox db = new GDialogBox(((AppW) app).getPanel(), app);
		FlowPanel fp = new FlowPanel();

		rels = relations.length;

		btnCallbacks = new Button[rels];
		callbacks = new Relation[rels];
		texts = new FlowPanel[rels];
		buttons = new FlowPanel[rels];

		for (int i = 0; i < rels; ++i) {
			texts[i] = new FlowPanel();
			buttons[i] = new FlowPanel();
			HTML text = new HTML(relations[i].getInfo());
			texts[i].add(text);

			if (relations[i].getCallback() != null) {
				callbacks[i] = relations[i].getCallback();
				btnCallbacks[i] = new Button();
				btnCallbacks[i].setStyleName("moreBtn");
				btnCallbacks[i].setText(app1.isUnbundledOrWhiteboard()
						? app1.getLocalization().getMenu("More")
						: app1.getLocalization().getMenu("More")
								+ Unicode.ELLIPSIS);
				btnCallbacks[i].addClickHandler(this);
				buttons[i].add(btnCallbacks[i]);
			}
			fp.add(LayoutUtilW.panelRow(texts[i], buttons[i]));
		}

		db.add(fp);

		HorizontalPanel messagePanel = new HorizontalPanel();
		messagePanel.addStyleName("Dialog-messagePanel");
		messagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		VerticalPanel messageTextPanel = new VerticalPanel();
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("Dialog-content");
		messagePanel.clear();
		messageTextPanel.clear();
		messageTextPanel.add(fp);
		messagePanel.add(messageTextPanel);
		mainPanel.add(messagePanel);
		setDialogContent(mainPanel);
		show();
	}

	@Override
	public void onClick(final ClickEvent event) {
		final Object source = event.getSource();

		((AppW) app).getAsyncManager().asyncEvalCommand("Delete(Prove(true))",
				(_unused) -> {
					LoggerW.loaded("prover");
					for (int i = 0; i < rels; ++i) {
						if (source == btnCallbacks[i]) {
							expandRow(i);
						}
					}
				}, null);
	}

	/**
	 * Update UI after More button clicked
	 * 
	 * @param row
	 *            row number
	 */
	protected void expandRow(int row) {
		RelationRow relation = callbacks[row].getExpandedRow(row);
		texts[row].clear();
		HTML text = new HTML(relation.getInfo());
		texts[row].add(text);
		callbacks[row] = relation.getCallback();
		if (callbacks[row] == null) {
			buttons[row].setVisible(false);
		}
	}
}
