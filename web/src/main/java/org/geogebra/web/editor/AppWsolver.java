package org.geogebra.web.editor;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.FontManagerW;
import org.geogebra.web.html5.main.GeoGebraTubeAPIWSimple;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.ShareDialog;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.geogebra.web.solver.Solver;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.web.MathFieldW;

public class AppWsolver extends AppW implements HasKeyboard {
    private GeoGebraFrameW frame;
	private MathFieldW mathField;

    /******************************************************
     * Constructs AppW for applets
     *
     * @param ae
     *            article element
     * @param gf
     *            frame

     */
    public AppWsolver(ArticleElementInterface ae, GeoGebraFrameW gf) {
        super(ae, 2, null);
        this.frame = gf;
        setAppletHeight(frame.getComputedHeight());
        setAppletWidth(frame.getComputedWidth());

        this.useFullGui = false;

        Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
                + GeoGebraConstants.BUILD_DATE + " "
                + Window.Navigator.getUserAgent());
        initCommonObjects();
        initing = true;

        initCoreObjects();

        resetFonts();
        Browser.removeDefaultContextMenu(this.getArticleElement().getElement());
        if (Browser.runningLocal() && ArticleElement.isEnableUsageStats()) {
            new GeoGebraTubeAPIWSimple(has(Feature.TUBE_BETA), ae)
                    .checkAvailable(null);
        }
		initSignInEventFlow(new LoginOperationW(this), true);
		GlobalHeader.INSTANCE.addSignIn(this);
		addShareButton();
    }

	private void addShareButton() {
		final RootPanel share = GlobalHeader.getShareButton();
		if (share == null) {
			return;
		}
		ClickStartHandler.init(share, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				String url = Location.getHref().replaceAll("\\?.*", "")
						+ Solver.getRelativeURLforEqn(getMathField().getText());
				ShareDialog sd = new ShareDialog(AppWsolver.this, url);
				sd.setVisible(true);
				sd.center();
			}
		});
	}

	protected MathFieldW getMathField() {
		return mathField;
	}

	@Override
    protected void initCoreObjects() {
        kernel = newKernel(this);
		initSettings();
        fontManager = new FontManagerW();
    }

    @Override
    public void buildApplicationPanel() {
		// no frame
    }

    @Override
    public void afterLoadFileAppOrNot(boolean asSlide) {
		// no file loading
    }

    @Override
    public void focusLost(View v, Element el) {
        super.focusLost(v, el);
        this.getGlobalKeyDispatcher().setFocused(false);
    }

    @Override
    public void focusGained(View v, Element el) {
        super.focusGained(v, el);
		this.getGlobalKeyDispatcher().setFocusedIfNotTab();
    }

    @Override
    public Element getFrameElement() {
        return frame.getElement();
    }

    @Override
    public HasAppletProperties getAppletFrame() {
        return frame;
    }

    @Override
    public boolean isSelectionRectangleAllowed() {
        return getToolbar() != null;
    }

    @Override
    public void setLanguage(final String browserLang) {
        // no localization support needed in webSimple
    }

    @Override
    public Panel getPanel() {
        return frame;
    }

    @Override
    public void copyGraphicsViewToClipboard() {
        Log.debug("unimplemented");
    }

    @Override
    public void updateKeyboardHeight() {
        // do nothing yet
    }

    @Override
    public double getInnerWidth() {
        return 798;
    }

	public void setMathField(MathFieldW mathField) {
		this.mathField = mathField;
	}
}
