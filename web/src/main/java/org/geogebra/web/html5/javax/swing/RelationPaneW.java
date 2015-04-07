package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.gui.util.RelationMore;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.common.main.App;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Web implementation of the Relation Tool information window.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org> Thanks to Laszlo Gal and Judit
 *         Elias for many hints.
 */

public class RelationPaneW extends DialogBox implements RelationPane,
        ClickHandler {

	@Override
	public void setGlassEnabled(boolean enabled) {
		super.setGlassEnabled(enabled);
	}

	private Button btnOK;
	private Button[] btnCallbacks;
	private RelationMore[] callbacks;
	private int rels;
	private FlowPanel[] texts;
	private FlowPanel[] buttons;

	public void showDialog(String title, RelationRow[] relations, App app) {

		// setGlassEnabled(true);
		addStyleName("DialogBox");

		DialogBox db = new DialogBox();
		FlowPanel fp = new FlowPanel();

		rels = relations.length;

		btnCallbacks = new Button[rels];
		callbacks = new RelationMore[rels];
		texts = new FlowPanel[rels];
		buttons = new FlowPanel[rels];

		for (int i = 0; i < rels; ++i) {
			texts[i] = new FlowPanel();
			buttons[i] = new FlowPanel();
			HTML text = new HTML(relations[i].info);
			texts[i].add(text);

			if (relations[i].callback != null) {
				callbacks[i] = relations[i].callback;
				btnCallbacks[i] = new Button();
				btnCallbacks[i].setText(app.getPlain("More") + "...");
				btnCallbacks[i].addClickHandler(this);
				buttons[i].add(btnCallbacks[i]);
			}
			fp.add(org.geogebra.web.html5.gui.util.LayoutUtil.panelRow(texts[i],
			        buttons[i]));
		}

		db.add(fp);

		btnOK = new Button();
		btnOK.addClickHandler(this);

		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		HorizontalPanel messagePanel = new HorizontalPanel();
		messagePanel.addStyleName("Dialog-messagePanel");
		messagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		VerticalPanel messageTextPanel = new VerticalPanel();
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("Dialog-content");
		btnOK.setText("OK");
		buttonPanel.add(btnOK);
		messagePanel.clear();
		messageTextPanel.clear();
		messageTextPanel.add(fp);
		messagePanel.add(messageTextPanel);
		mainPanel.add(messagePanel);
		mainPanel.add(buttonPanel);
		clear();
		add(mainPanel);
		setText(title);
		center();
		show();
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();

		if (source == btnOK) {
			hide();
		}

		for (int i = 0; i < rels; ++i) {
			if (source == btnCallbacks[i]) {
				callbacks[i].action(this, i);
			}
		}
	}

	public void updateRow(int row, RelationRow relation) {
		texts[row].clear();
		HTML text = new HTML(relation.info);
		texts[row].add(text);
		callbacks[row] = relation.callback;
		if (callbacks[row] == null) {
			buttons[row].setVisible(false);
		}
	}
}
