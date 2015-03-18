package geogebra.web.gui.applet;

import geogebra.common.main.App;
import geogebra.common.main.App.InputPositon;
import geogebra.html5.WebStatic;
import geogebra.html5.gui.GeoGebraFrame;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.main.AppW;
import geogebra.html5.main.AppWsimple;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.gui.HeaderPanelDeck;
import geogebra.web.gui.MyHeaderPanel;
import geogebra.web.gui.app.ShowKeyboardButton;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.gui.layout.panels.AlgebraDockPanelW;
import geogebra.web.util.keyboard.OnScreenKeyBoard;
import geogebra.web.util.keyboard.UpdateKeyBoardListener;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class GeoGebraFrameBoth extends GeoGebraFrame implements
        HeaderPanelDeck, UpdateKeyBoardListener {

	private AppletFactory factory;
	private DockGlassPaneW glass;

	public GeoGebraFrameBoth(AppletFactory factory, GLookAndFeel laf) {
		super(laf);
		this.factory = factory;
	}

	protected AppW createApplication(ArticleElement ae, GeoGebraFrame gf, GLookAndFeelI laf) {
		AppW app = factory.getApplet(ae, gf, laf);
		WebStatic.lastApp = app;
		this.glass = new DockGlassPaneW();
		this.add(glass);
		return app;
	}

	protected AppW createApplicationSimple(ArticleElement ae, GeoGebraFrame gf) {
		AppW app = new AppWsimple(ae, gf);
		WebStatic.lastApp = app;
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
			if(WebStatic.panelForApplets == null){
				RootPanel.get(articleElement.getId()).add(inst);
			}else{
				WebStatic.panelForApplets.add(inst);
			}
		}
	}
	
	/**
	 * @param el html element to render into
	 */
	public static void renderArticleElement(Element el, AppletFactory factory, GLookAndFeel laf, JavaScriptObject clb) {
		GeoGebraFrame.renderArticleElementWithFrame(el, new GeoGebraFrameBoth(factory, laf), clb);
	}
	
	public Object getGlassPane(){
		return this.glass;
	}
	
	public void attachGlass(){
		if(this.glass!=null){
			this.add(glass);
		}
	}
	private boolean[] childVisible = new boolean[0];
	private boolean isBrowserShowing = false;
	private boolean keyboardShowing = false;
	private ShowKeyboardButton showKeyboardButton;
	
	@Override
    public void showBrowser(HeaderPanel bg) {
		this.isBrowserShowing = true;
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
	    bg.setVisible(true);

	    ((MyHeaderPanel)bg).setFrame(this);
	    //frameLayout.forceLayout();
	    
    }

	@Override
    public void hideBrowser(MyHeaderPanel bg) {
		this.isBrowserShowing = false;
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

	public void doShowKeyBoard(boolean show, Widget textField) {
		App.debug("KEYBOARD" + this.keyboardShowing + "," + show);
		if (this.keyboardShowing == show) {
			return;
		}
		this.keyboardShowing = show;
		// this.mainPanel.clear();
		OnScreenKeyBoard keyBoard = OnScreenKeyBoard.getInstance(textField,
		        this, app);
		if (show && textField != null) {
			keyBoard.show();
			CancelEventTimer.keyboardSetVisible();
			// this.mainPanel.addSouth(keyBoard, keyBoard.getOffsetHeight());
			this.add(keyBoard);
		} else {
			this.remove(keyBoard);
			keyBoard.resetKeyboardState();
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
			        .getPanel(geogebra.common.main.App.VIEW_ALGEBRA)))
			        .scrollToBottom();
		}
	}

	public void showKeyBoard(boolean show, Widget textField) {
		keyBoardNeeded(show, textField);
	}

	@Override
	public void keyBoardNeeded(boolean show, Widget textField) {
		if (app.getLAF().isTablet()
		        || keyboardShowing // if keyboard is already
		                           // showing, we don't have
		                           // to handle the showKeyboardButton
		        || OnScreenKeyBoard.getInstance(textField, this, app)
		                .shouldBeShown()) {
			doShowKeyBoard(show, textField);
		} else {
			showKeyboardButton(show, textField);
		}

	}

	private void showKeyboardButton(boolean show, Widget textField) {
		if (showKeyboardButton == null) {
			showKeyboardButton = new ShowKeyboardButton(this, textField,
			        this.getElement());
		}
		showKeyboardButton.show(show, textField);
	}

}
