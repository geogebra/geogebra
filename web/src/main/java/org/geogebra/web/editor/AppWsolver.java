package org.geogebra.web.editor;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.geos.inputbox.InputBoxType;
import org.geogebra.common.main.AppKeyboardType;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.debug.Log;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.keyboard.web.HasKeyboard;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.factories.NoCASFactory;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.gui.laf.SignInControllerI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.FontManagerW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.shared.GlobalHeader;
import org.geogebra.web.shared.ShareLinkDialog;
import org.geogebra.web.shared.SignInController;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.ggtapi.LoginOperationW;
import org.gwtproject.dom.client.Element;

import com.himamis.retex.editor.web.MathFieldW;

import elemental2.core.Global;
import elemental2.dom.DomGlobal;

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
    public AppWsolver(GeoGebraElement ae, AppletParameters parameters, GeoGebraFrameW gf) {
        super(ae, parameters, 2, null);
        this.frame = gf;
        setAppletHeight(frame.getComputedHeight());
        setAppletWidth(frame.getComputedWidth());

        this.useFullGui = false;

        Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
                + GeoGebraConstants.BUILD_DATE);
        initCommonObjects();
        initing = true;

        initCoreObjects();

		getSettingsUpdater().getFontSettingsUpdater().resetFonts();
		Browser.removeDefaultContextMenu(getGeoGebraElement().getElement());

		initSignInEventFlow(new LoginOperationW(this));

		GlobalHeader.INSTANCE.addSignIn(this);
		addShareButton();
		frame.setApplication(this);
    }

	private void addShareButton() {
		GlobalHeader.INSTANCE.initShareButton(share -> {
			String url = DomGlobal.location.href.replaceAll("\\?.*", "")
					+ getRelativeURLforEqn(getMathField().getText());
			DialogData data = new DialogData("Share", null, null);
			ShareLinkDialog sd = new ShareLinkDialog(this, data, url,
					share);
			sd.setVisible(true);
			sd.center();
		});
	}

	public static String getRelativeURLforEqn(String text) {
		return "?i=" + Global.encodeURIComponent(text);
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
		kernel.setAngleUnit(kernel.getApplication().getConfig().getDefaultAngleUnit());
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
		// not needed in solver for now
	}

	@Override
	public void copyGraphicsViewToClipboard() {
		// not needed in solver
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
		return NavigatorUtil.getWindowWidth();
	}

	@Override
	public void updateKeyboardHeight() {
		// do nothing yet
	}

	@Override
	public double getInnerWidth() {
		int width = NavigatorUtil.getWindowWidth();
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
	public InputBoxType getInputBoxType() {
		return null;
	}

	@Override
	public List<String> getInputBoxFunctionVars() {
		return Collections.emptyList();
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

	@Override
	public void initFactories() {
		super.initFactories();
		if (!CASFactory.isInitialized()) {
			CASFactory.setPrototype(new NoCASFactory());
		}
	}
}
