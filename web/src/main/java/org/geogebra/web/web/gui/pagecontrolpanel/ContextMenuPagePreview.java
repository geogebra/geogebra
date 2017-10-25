package org.geogebra.web.web.gui.pagecontrolpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.event.FocusListenerW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuPagePreview implements SetLabels {

	/** visible component */
	protected GPopupMenuW wrappedPopup;
	/** localization */
	protected Localization loc;
	private AppW app;
	/** parent item */
	protected PagePreviewCard card;
	private FlowPanel renamePanel;
	private Label name;
	private AutoCompleteTextFieldW textField;

	public ContextMenuPagePreview(AppW app, PagePreviewCard card) {
		this.app = app;
		this.card = card;
		loc = app.getLocalization();
		initGUI();
	}

	private void initGUI() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");

		addRenameItem();
		addDeleteItem();
	}

	private void addRenameItem() {
		//renamePanel = new FlowPanel();
		name = new Label(loc.getMenu("Title"));
		textField = InputPanelW.newTextComponent(app);
		textField.setAutoComplete(false);
		
		textField.addFocusListener(new FocusListenerW(this) {
			@Override
			protected void wrapFocusLost() {
				onEnter();
			}
		});

		textField.addKeyHandler(new KeyHandler() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					onEnter();

				}
			}
		});
		renamePanel = LayoutUtilW.panelRow(name, textField);
		// TODO add panelrow to wrappedPopup

	}

	protected void onEnter() {
		card.rename(textField.getText());
	}

	private void addDeleteItem() {
		String img = MaterialDesignResources.INSTANCE.delete_black()
				.getSafeUri().asString();
		MenuItem mi = new MenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Delete"), true), true,
				new Command() {

					@Override
					public void execute() {
						// TODO delete page and preview card
						card.removeFromParent();
						// card.getAssociatedView()...
					}
				});
		wrappedPopup.addItem(mi);

	}
	public void setLabels() {
		initGUI();
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
	}

	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
			}
		});
	}
}
