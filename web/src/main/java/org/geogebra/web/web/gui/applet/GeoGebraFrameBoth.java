package org.geogebra.web.web.gui.applet;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
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
import org.geogebra.web.web.gui.HeaderPanelDeck;
import org.geogebra.web.web.gui.MyHeaderPanel;
import org.geogebra.web.web.gui.app.ShowKeyboardButton;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.gui.layout.DockGlassPaneW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoGebraFrameBoth extends GeoGebraFrame implements
        HeaderPanelDeck, UpdateKeyBoardListener {

	private AppletFactory factory;
	private DockGlassPaneW glass;

	public GeoGebraFrameBoth(AppletFactory factory, GLookAndFeel laf) {
		super(laf);
		this.factory = factory;
	}

	@Override
	protected AppW createApplication(ArticleElement ae,
	        GLookAndFeelI laf) {
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
	 * @param geoGebraMobileTags
	 *          list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags, AppletFactory factory, GLookAndFeel laf) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrameBoth(factory, laf);
			inst.ae = articleElement;
			GeoGebraLogger.startLogger(inst.ae);
			inst.createSplash(articleElement);
			RootPanel.get(articleElement.getId()).add(inst);
		}

		// now we can create dummy elements before & after each applet
		// with tabindex 10000, for ticket #5158
		firstDummy = DOM.createSpan().cast();
		firstDummy.addClassName("geogebraweb-dummy-invisible");
		firstDummy.setTabIndex(GRAPHICS_VIEW_TABINDEX);
		geoGebraMobileTags.get(0).insertFirst(firstDummy);

		lastDummy = DOM.createSpan().cast();
		lastDummy.addClassName("geogebraweb-dummy-invisible");
		lastDummy.setTabIndex(GRAPHICS_VIEW_TABINDEX);
		geoGebraMobileTags.get(geoGebraMobileTags.size() - 1).appendChild(
				lastDummy);

		programFocusEvent(firstDummy, lastDummy);
	}

	/**
	 * @param el html element to render into
	 */
	public static void renderArticleElement(Element el, AppletFactory factory, GLookAndFeel laf, JavaScriptObject clb) {
		GeoGebraFrame.renderArticleElementWithFrame(el, new GeoGebraFrameBoth(factory, laf), clb);
	}
	
	@Override
	public Object getGlassPane(){
		return this.glass;
	}
	
	@Override
	public void attachGlass(){
		if(this.glass!=null){
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
	    for(int i = 0; i<count;i++){
	    	childVisible[i] = frameLayout.getWidget(i).isVisible(); 
	    	frameLayout.getWidget(i).setVisible(false);
	    }
	    frameLayout.add(bg);
		bg.setHeight(oldHeight + "px");
		bg.setWidth(oldWidth + "px");
		bg.onResize();
	    bg.setVisible(true);

	    ((MyHeaderPanel)bg).setFrame(this);
	    //frameLayout.forceLayout();
	    
    }

	@Override
    public void hideBrowser(MyHeaderPanel bg) {
		GeoGebraFrame frameLayout = this;
		frameLayout.remove(bg);
		final int count = frameLayout.getWidgetCount();
		for(int i = 0; i<count;i++){
			if(childVisible.length > i){
				frameLayout.getWidget(i).setVisible(childVisible[i]);
			}
	    }
	    //frameLayout.setLayout(app);
	    //frameLayout.forceLayout();
	    app.updateViewSizes(); 
	    
    }

	public void doShowKeyBoard(final boolean show,
	        MathKeyboardListener textField) {
		if (this.keyboardShowing == show) {
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
				.getOnScreenKeyboard(textField,
				this);
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
				.getOnScreenKeyboard(textField,
				this);
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
		keyBoardNeeded(show, textField);
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
		if(app.getLAF().isSmart() || !(app.showAlgebraInput() && app.getInputPosition() == InputPositon.algebraView)){
			return;
		}
		if (showKeyboardButton == null) {
			DockPanelW algebraDockPanel = (DockPanelW) app.getGuiManager()
			        .getLayout()
			        .getDockManager().getPanel(App.VIEW_ALGEBRA);
			showKeyboardButton = new ShowKeyboardButton(this,
					(DockManagerW) app.getGuiManager().getLayout()
							.getDockManager(), algebraDockPanel);
		}
		showKeyboardButton.show(app.isKeyboardNeeded(), textField);
	}

	public void showKeyboardButton(boolean show) {
		if (showKeyboardButton == null) {
			return;
		}
		if(show){
			showKeyboardButton.setVisible(true);
		}else{
			showKeyboardButton.hide();
		}
	}

	public void refreshKeyboard() {
		if (keyboardShowing) {
			final VirtualKeyboard keyBoard = app.getGuiManager()
					.getOnScreenKeyboard(null,
					this);
			if (app.isKeyboardNeeded()) {
				add(keyBoard);
			} else {
				removeKeyboard(null);
				if (this.showKeyboardButton != null) {
					this.showKeyboardButton.hide();
				}
			}
		} else {
			if (app != null
			        && app.isKeyboardNeeded()
 && app.showAlgebraInput()) {
				if (app.getArticleElement().getDataParamBase64String().length() == 0) {
				keyboardShowing = true;
				app.getGuiManager().invokeLater(new Runnable() {

					@Override
					public void run() {
						app.persistWidthAndHeight();
						addKeyboard(null);
						new Timer() {

							@Override
							public void run() {
								((AlgebraViewW) app.getAlgebraView())
								        .setFocus(true);
								((AlgebraViewW) app.getAlgebraView())
						        .getInputTreeItem().ensureEditing();


							}
						}.schedule(500);

					}
					});
				} else {
					app.getGuiManager().getOnScreenKeyboard(null, this)
							.showOnFocus();
				}

			}
 else if (app != null && app.isKeyboardNeeded()) {
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

}
