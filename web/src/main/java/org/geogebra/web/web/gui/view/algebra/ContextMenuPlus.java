package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.web.MathFieldW;

public class ContextMenuPlus implements SetLabels {
	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	private AppW app;
	private RadioTreeItem item;
	private MathFieldW mf;
	private TabbedKeyboard kbd;
	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuPlus(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
		mf = ((LatexTreeItemController)item.getController()).getRetexListener().getMathField();
		kbd = (TabbedKeyboard)((GuiManagerW)app.getGuiManager()).getOnScreenKeyboard(item, null);
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addExpressionItem();
		addTextItem();
		addImageItem();
		addHelpItem();
	}
	
	private void addExpressionItem() {
		String img = StyleBarResources.INSTANCE.description().getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getPlain("Expression"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(false);
						item.setText(" ");
						mf.getKeyListener().onKeyPressed(new KeyEvent(KeyEvent.VK_SPACE));
						kbd.selectNumbers();
					}
				});

		wrappedPopup.addItem(mi);
	}

	private void addTextItem() {
		String img = MaterialDesignResources.INSTANCE.icon_quote_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(
				MainMenu.getMenuBarHtml(img, loc.getPlain("Text"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(true);
						item.setText(" ");
						mf.getKeyListener().onKeyPressed(new KeyEvent(KeyEvent.VK_SPACE));
						kbd.selectAbc();
					}
				});

		wrappedPopup.addItem(mi);
	}
	
	private void addImageItem() {
		String img = MaterialDesignResources.INSTANCE.insert_photo_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(
				MainMenu.getMenuBarHtml(img, loc.getPlain("Image"), true), true,
				new Command() {
					
					@Override
					public void execute() {

						item.getController().setInputAsText(false);
						app.getImageManager().setPreventAuxImage(true);
						
						((GuiManagerW)app.getGuiManager()).loadImage(null, null, false, app.getActiveEuclidianView());
					}
				});

		wrappedPopup.addItem(mi);
	}

	private void addHelpItem() {
		String img = MaterialDesignResources.INSTANCE.icon_help_black()
				.getSafeUri()
		.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img, loc.getMenu("Help"), true),
				true, new Command() {
					
					@Override
					public void execute() {
						showHelp();
					}
				});

		wrappedPopup.addItem(mi);
	}
	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
	private void showHelp() {
		item.preventBlur();
		item.requestFocus();
		if (item.showCurrentError()) {

			return;
		}

		item.app.hideKeyboard();
		Scheduler.get().scheduleDeferred(
				new Scheduler.ScheduledCommand() {
					@Override
					public void execute() {
						item.setShowInputHelpPanel(
								true);
						
						((InputBarHelpPanelW) item.app
								.getGuiManager()
								.getInputHelpPanel())
						.focusCommand(
								item.getCommand());
					}

				});
	}

}

