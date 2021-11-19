package org.geogebra.web.full.gui.menubar.action;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Clears construction.
 */
public class ClearAllAction extends DefaultMenuAction<Void> implements AsyncOperation<Boolean> {

	private boolean askForSave;
	private AppW app;

	/**
	 * @param askForSave whether asks for save
	 */
	public ClearAllAction(boolean askForSave) {
		this.askForSave = askForSave;
	}

	@Override
	public void execute(Void item, AppWFull app) {
		DialogData data = new DialogData("TAB Title", "Cancel", "OK");
		ComponentDialog dialog = new ComponentDialog(app, data, false, true);
		FlowPanel panel1 = new FlowPanel();
		panel1.getElement().getStyle().setWidth(452, Style.Unit.PX);
		panel1.getElement().getStyle().setHeight(150, Style.Unit.PX);
		panel1.getElement().getStyle().setBackgroundColor("blue");
		FlowPanel panel2 = new FlowPanel();
		panel2.getElement().getStyle().setWidth(452, Style.Unit.PX);
		panel2.getElement().getStyle().setHeight(150, Style.Unit.PX);
		panel2.getElement().getStyle().setBackgroundColor("teal");
		FlowPanel panel3 = new FlowPanel();
		panel3.getElement().getStyle().setWidth(452, Style.Unit.PX);
		panel3.getElement().getStyle().setHeight(150, Style.Unit.PX);
		panel3.getElement().getStyle().setBackgroundColor("green");

		TabData tab1 = new TabData("Tab 1 Title", panel1);
		TabData tab2 = new TabData("Tab 2", panel2);
		TabData tab3 = new TabData("Tab 3 loooooong",panel3);
		ComponentTab componentTab = new ComponentTab(
				new ArrayList<>(Arrays.asList(tab1, tab2, tab3)),
				app.getLocalization());
		dialog.addDialogContent(componentTab);
		dialog.show();
		/*this.app = app;
		if (askForSave) {
			BrowserStorage.SESSION.setItem("saveAction", "clearAll");
			app.getSaveController().showDialogIfNeeded(this, false);
		} else {
			callback(true);
		}*/
	}

	@Override
	public void callback(Boolean obj) {
		app.tryLoadTemplatesOnFileNew();
	}
}
