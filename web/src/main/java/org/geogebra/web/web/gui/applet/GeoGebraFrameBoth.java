package org.geogebra.web.web.gui.applet;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.GeoGebraFrame;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.debug.GeoGebraLogger;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboard;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.app.ShowKeyboardButton;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoGebraFrameBoth extends GeoGebraFrame implements
		HeaderPanelDeck, UpdateKeyBoardListener {

	private AppletFactory factory;
	private DockGlassPaneW glass;
	private GGWToolBar ggwToolBar = null;

	public GeoGebraFrameBoth(AppletFactory factory, GLookAndFeel laf) {
		super(laf);
		this.factory = factory;
	}

	@Override
	protected AppW createApplication(ArticleElement ae, GLookAndFeelI laf) {
		AppW app = factory.getApplet(ae, this, laf);
		this.glass = new DockGlassPaneW();
		this.add(glass);
		return app;
	}

	protected AppW createApplicationSimple(ArticleElement ae, GeoGebraFrame gf) {
		AppW app = new AppWsimple(ae, gf);
		return app;
	}

	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * 
	 * @param geoGebraMobileTags
	 *            list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags,
			AppletFactory factory, GLookAndFeel laf) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrameBoth(factory, laf);
			inst.ae = articleElement;
			GeoGebraLogger.startLogger(inst.ae);
			inst.createSplash(articleElement);
			RootPanel.get(articleElement.getId()).add(inst);
		}
		if (geoGebraMobileTags.isEmpty()) {
			return;
		}

		if (geoGebraMobileTags.size() > 0) {
			// now we can create dummy elements before & after each applet
			// with tabindex 10000, for ticket #5158
			tackleFirstDummy(geoGebraMobileTags.get(0));
			tackleLastDummy(geoGebraMobileTags
					.get(geoGebraMobileTags.size() - 1));
			programFocusEvent(firstDummy, lastDummy);
		}
	}

	/**
	 * @param el
	 *            html element to render into
	 */
	public static void renderArticleElement(Element el, AppletFactory factory,
			GLookAndFeel laf, JavaScriptObject clb) {

		GeoGebraFrame.renderArticleElementWithFrame(el, new GeoGebraFrameBoth(
				factory, laf), clb);

		GeoGebraFrame.reCheckForDummies(el);
	}

	@Override
	public Object getGlassPane() {
		return this.glass;
	}

	@Override
	public void attachGlass() {
		if (this.glass != null) {
			this.add(glass);
		}
	}

	private boolean[] childVisible = new boolean[0];
	private boolean keyboardShowing = false;
	private ShowKeyboardButton showKeyboardButton;
	private int keyboardHeight;

	@Override
	public void showBrowser(HeaderPanel bg) {
		GeoGebraFrame frameLayout = this;
		final int count = frameLayout.getWidgetCount();
		final int oldHeight = this.getOffsetHeight();
		final int oldWidth = this.getOffsetWidth();
		childVisible = new boolean[count];
		for (int i = 0; i < count; i++) {
			childVisible[i] = frameLayout.getWidget(i).isVisible();
			frameLayout.getWidget(i).setVisible(false);
		}
		frameLayout.add(bg);
		bg.setHeight(oldHeight + "px");
		bg.setWidth(oldWidth + "px");
		bg.onResize();
		bg.setVisible(true);

		((MyHeaderPanel) bg).setFrame(this);
		// frameLayout.forceLayout();

	}

	@Override
	public void hideBrowser(MyHeaderPanel bg) {
		GeoGebraFrame frameLayout = this;
		frameLayout.remove(bg);
		final int count = frameLayout.getWidgetCount();
		for (int i = 0; i < count; i++) {
			if (childVisible.length > i) {
				frameLayout.getWidget(i).setVisible(childVisible[i]);
			}
		}
		// frameLayout.setLayout(app);
		// frameLayout.forceLayout();
		app.updateViewSizes();

	}

	public AppW getAppW() {
		return app;
	}

	public void doShowKeyBoard(final boolean show,
			MathKeyboardListener textField) {
		if (this.keyboardShowing == show) {
			app.getGuiManager().setOnScreenKeyboardTextField(textField);
			return;
		}

		// this.mainPanel.clear();

		if (show) {
			app.hideMenu();
			app.persistWidthAndHeight();
			addKeyboard(textField);
		} else {
			app.persistWidthAndHeight();
			showKeyboardButton(textField);
			removeKeyboard(textField);
		}

		// this.mainPanel.add(this.dockPanel);

		Timer timer = new Timer() {
			@Override
			public void run() {
				// onResize();
				// dockPanel.onResize();
				scrollToInputField();
			}
		};
		timer.schedule(0);
	}

	private void removeKeyboard(MathKeyboardListener textField) {
		final VirtualKeyboard keyBoard = app.getGuiManager()
				.getOnScreenKeyboard(textField, this);
		App.printStacktrace("HIDE KEYBOARD");
		this.keyboardShowing = false;
		app.addToHeight(keyboardHeight);
		this.remove(keyBoard);
		app.updateCenterPanel(true);
		// TODO too expensive
		app.updateViewSizes();
		keyBoard.resetKeyboardState();
	}

	private void addKeyboard(MathKeyboardListener textField) {
		final VirtualKeyboard keyBoard = app.getGuiManager()
				.getOnScreenKeyboard(textField, this);
		this.keyboardShowing = true;
		keyBoard.show();
		keyBoard.setVisible(false);
		CancelEventTimer.keyboardSetVisible();
		// this.mainPanel.addSouth(keyBoard, keyBoard.getOffsetHeight());
		this.add(keyBoard);

		app.getGuiManager().invokeLater(new Runnable() {

			@Override
			public void run() {

				keyboardHeight = keyBoard.getOffsetHeight();
				app.addToHeight(-keyboardHeight);
				app.updateCenterPanel(true);
				// TODO maybe too expensive?
				app.updateViewSizes();
				GeoGebraFrameBoth.this.add(keyBoard);
				keyBoard.setVisible(true);
				if (showKeyboardButton != null) {
					showKeyboardButton.hide();
				}
			}
		});
	}

	@Override
	public void showInputField() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				scrollToInputField();
			}
		};
		timer.schedule(0);
	}

	/**
	 * Scroll to the input-field, if the input-field is in the algebraView.
	 */
	void scrollToInputField() {
		if (app.showAlgebraInput()
				&& app.getInputPosition() == InputPositon.algebraView) {
			((AlgebraDockPanelW) (app.getGuiManager().getLayout()
					.getDockManager()
					.getPanel(org.geogebra.common.main.App.VIEW_ALGEBRA)))
					.scrollToBottom();
		}
	}

	public void showKeyBoard(boolean show, MathKeyboardListener textField,
			boolean forceShow) {
		if (forceShow) {
			doShowKeyBoard(show, textField);
		} else {
			keyBoardNeeded(show, textField);
		}
	}

	@Override
	public void keyBoardNeeded(boolean show, MathKeyboardListener textField) {
		if (app.getLAF().isTablet()
				|| keyboardShowing // if keyboard is already
									// showing, we don't have
									// to handle the showKeyboardButton
				|| app.getGuiManager().getOnScreenKeyboard(textField, this)
						.shouldBeShown()) {
			doShowKeyBoard(show, textField);
		} else {
			showKeyboardButton(textField);
		}

	}

	private void showKeyboardButton(MathKeyboardListener textField) {
		if (!appNeedsKeyboard()) {
			return;
		}
		if (showKeyboardButton == null) {
			if (app.has(Feature.CAS_EDITOR)) {
				DockManagerW dm = (DockManagerW) app.getGuiManager()
						.getLayout().getDockManager();
				DockPanelW dockPanelKB = dm.getPanelForKeyboard();

				if (dockPanelKB != null) {
					showKeyboardButton = new ShowKeyboardButton(this, dm,
							dockPanelKB);
					dockPanelKB.setKeyBoardButton(showKeyboardButton);
				}
			} else {
				showInAlgebra();
			}

		}
		showKeyboardButton.show(app.isKeyboardNeeded(), textField);
	}

	private boolean appNeedsKeyboard() {
		return (app.showAlgebraInput() && app.getInputPosition() == InputPositon.algebraView)
				|| (app.has(Feature.CAS_EDITOR) && app.showView(App.VIEW_CAS));
	}

	private void showInAlgebra() {
		DockPanelW algebraDockPanel = (DockPanelW) app.getGuiManager()
				.getLayout().getDockManager().getPanel(App.VIEW_ALGEBRA);
		showKeyboardButton = new ShowKeyboardButton(this, (DockManagerW) app
				.getGuiManager().getLayout().getDockManager(), algebraDockPanel);

	}

	public void showKeyboardButton(boolean show) {
		if (showKeyboardButton == null) {
			return;
		}
		if (show) {
			showKeyboardButton.setVisible(true);
		} else {
			showKeyboardButton.hide();
		}
	}

	public void refreshKeyboard() {
		if (keyboardShowing) {
			final VirtualKeyboard keyBoard = app.getGuiManager()
					.getOnScreenKeyboard(null, this);
			if (app.isKeyboardNeeded()) {
				add(keyBoard);
			} else {
				removeKeyboard(null);
				if (this.showKeyboardButton != null) {
					this.showKeyboardButton.hide();
				}
			}
		} else {
			if (app != null && app.isKeyboardNeeded() && appNeedsKeyboard()) {
				if (app.getArticleElement().getDataParamBase64String().length() == 0) {
					keyboardShowing = true;
					app.getGuiManager().invokeLater(new Runnable() {

						@Override
						public void run() {
							app.persistWidthAndHeight();
							addKeyboard(null);
							app.getGuiManager().focusScheduled(false, false,
									false);
							new Timer() {

								@Override
								public void run() {
									DockManagerW dm = (DockManagerW) app
											.getGuiManager().getLayout()
											.getDockManager();
									MathKeyboardListener ml = dm
											.getPanelForKeyboard()
											.getKeyboardListener();
									((GuiManagerW) app.getGuiManager())
											.setOnScreenKeyboardTextField(ml);
									ml.setFocus(true, true);
									ml.ensureEditing();

								}
							}.schedule(500);

						}
					});
				} else {
					this.showKeyboardButton(null);
					app.getGuiManager().getOnScreenKeyboard(null, this)
							.showOnFocus();
				}

			} else if (app != null && app.isKeyboardNeeded()) {
				this.showKeyboardButton(true);
			}

			else if (app != null && !app.isKeyboardNeeded()
					&& this.showKeyboardButton != null) {
				this.showKeyboardButton.hide();
			}
		}
	}

	@Override
	public boolean isKeyboardShowing() {
		return this.keyboardShowing;
	}

	public void showKeyboardOnFocus() {
		this.app.getGuiManager().getOnScreenKeyboard(null, this).showOnFocus();
	}

	public void updateKeyboardHeight() {
		// TODO update of height
	}

	public double getKeyboardHeight() {
		return keyboardShowing ? keyboardHeight : 0;
	}

	public void attachMenubar(AppW app) {
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(app);
			insert(ggwToolBar, 0);
		}
		ggwToolBar.attachMenubar();
	}

	public void attachToolbar(AppW app) {
		// reusing old toolbar is probably a good decision
		if (ggwToolBar == null) {
			ggwToolBar = new GGWToolBar();
			ggwToolBar.init(app);
		}
		insert(ggwToolBar, 0);
	}

	public GGWToolBar getToolbar() {
		return ggwToolBar;
	}
}