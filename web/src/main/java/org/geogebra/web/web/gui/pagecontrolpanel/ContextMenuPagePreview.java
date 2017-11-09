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
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;
import org.geogebra.web.web.javax.swing.GPopupMenuW;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Context Menu of Page Preview Cards
 * 
 * @author Alicia Hofstaetter
 *
 */
public class ContextMenuPagePreview implements SetLabels {

	/** visible component */
	protected GPopupMenuW wrappedPopup;
	private Localization loc;
	private AppW app;
	private GeoGebraFrameBoth frame;
	private PagePreviewCard card;
	private FlowPanel contentPanel;
	private AutoCompleteTextFieldW textField;

	/**
	 * @param app
	 *            application
	 * @param card
	 *            associated preview card
	 */
	public ContextMenuPagePreview(AppW app, PagePreviewCard card) {
		this.app = app;
		this.card = card;
		loc = app.getLocalization();
		frame = ((AppWapplet) app).getAppletFrame();
		initGUI();
	}

	private void initGUI() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matPopupPanel");

		contentPanel = new FlowPanel();
		contentPanel.addStyleName("mowCardContextMenu");
		wrappedPopup.getPopupPanel().setWidget(contentPanel);

		addRenameItem();
		addDeleteItem();
	}

	private void addRenameItem() {
		String img = MaterialDesignResources.INSTANCE.mow_label().getSafeUri()
				.asString();
		Label name = new Label(loc.getMenu("Title") + ":");
		textField = InputPanelW.newTextComponent(app);
		textField.setAutoComplete(false);
		textField.setText(card.getTitleText().getText());
		
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
		contentPanel.add(LayoutUtilW.panelRow(new Image(img), name, textField));
	}

	/**
	 * execute textfield input
	 */
	protected void onEnter() {
		card.rename(textField.getText());
		wrappedPopup.hide();
	}

	private void addDeleteItem() {
		String img = MaterialDesignResources.INSTANCE.delete_black()
				.getSafeUri().asString();
		Label delete = new Label(loc.getMenu("Delete"));
		FlowPanel deletePanel = LayoutUtilW.panelRow(new Image(img), delete);
		deletePanel.addStyleName("mowMenuRow");

		deletePanel.addDomHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onDelete();
			}
		}, ClickEvent.getType());

		contentPanel.add(deletePanel);
	}

	/**
	 * execute delete action
	 */
	protected void onDelete() {
		frame.getPageControlPanel().removePreviewCard(card);
		wrappedPopup.hide();
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
		int y1 = y;
		if (y + wrappedPopup.getPopupPanel().getOffsetHeight() > app
				.getHeight()) {
			y1 = (int) (app.getHeight()
					- wrappedPopup.getPopupPanel().getOffsetHeight());
		}
		wrappedPopup.show(new GPoint(x, y1));
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
