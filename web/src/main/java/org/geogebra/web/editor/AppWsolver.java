package org.geogebra.web.editor;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.FontManagerW;
import org.geogebra.web.html5.util.ArticleElementInterface;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.SignInController;
import org.geogebra.web.shared.components.DialogData;
import org.geogebra.web.shared.ggtapi.LoginOperationW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * App for solver
 */
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

		getSettingsUpdater().getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(this.getArticleElement().getElement());

		initSignInEventFlow(new LoginOperationW(this));

		GlobalHeader.INSTANCE.addSignIn(this);
		addShareButton();
		frame.setApplication(this);
    }

	private void addShareButton() {
		GlobalHeader.INSTANCE.initShareButton(new AsyncOperation<Widget>() {

			@Override
			public void callback(Widget share) {
				String url = Location.getHref().replaceAll("\\?.*", "")
						+ getRelativeURLforEqn(getMathField().getText());
				DialogData data = new DialogData("Share", null, null);
				ShareLinkDialog sd = new ShareLinkDialog(AppWsolver.this, data, url,
						share);
				sd.setVisible(true);
				sd.center();
			}
		});
	}

	public static String getRelativeURLforEqn(String text) {
		return "?i=" + URL.encodePathSegment(text);
	}

	/**
	 * @return equation editor
	 */
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
    public Element getFrameElement() {
        return frame.getElement();
    }

    @Override
    public GeoGebraFrameW getAppletFrame() {
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

	/**
	 * @param mathField
	 *            equation editor
	 */
	public void setMathField(MathFieldW mathField) {
		this.mathField = mathField;
	}

	@Override
	public DialogManager getDialogManager() {
		return new DialogManagerSolver();
	}

	@Override
	public boolean isUnbundled() {
		return true;
	}

	@Override
	public double getWidth() {
    	return Window.getClientWidth();
	}

	@Override
	public void updateKeyboardHeight() {
		// do nothing yet
	}

	@Override
	public double getInnerWidth() {
		int width = Window.getClientWidth();
		if (width > 1300) {
			return width / 2.0;
		} else if (width > 650) {
			return 650;
		} else {
			return width;
		}
	}

	@Override
	public AppKeyboardType getKeyboardType() {
		return AppKeyboardType.SUITE;
	}

	@Override
	public boolean attachedToEqEditor() {
		return false;
	}

	@Override
	public AlgebraView getAlgebraView() {
		return null;
	}

	@Override
	public SignInControllerI getSignInController() {
		return new SignInController(this, 0, null);
	}
}
