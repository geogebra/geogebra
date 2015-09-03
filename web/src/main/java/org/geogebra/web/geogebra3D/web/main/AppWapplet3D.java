package org.geogebra.web.geogebra3D.web.main;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.geogebra3D.util.CopyPaste3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.GLFactoryW;
import org.geogebra.web.geogebra3D.web.gui.GuiManager3DW;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.dialog.DialogManager3DW;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.main.AppWapplet;
import org.geogebra.web.web.main.BrowserDevice;

public class AppWapplet3D extends AppWapplet {
	private EuclidianView3DW euclidianView3D;
	private EuclidianController3DW euclidianController3D;

	public AppWapplet3D(ArticleElement ae, GeoGebraFrameBoth gf,
	        GLookAndFeel laf) {
		super(ae, gf, 3, laf);
	}

	@Override
	protected Kernel newKernel(App this_app) {
		return App3DW.newKernel(this_app);
	}

	@Override
	public boolean hasEuclidianView3D() {
		return true;
	}

	@Override
	public EuclidianView3DInterface getEuclidianView3D() {
		// Window.alert("getEuclidianView3D()");
		if (this.euclidianView3D == null) {
			euclidianController3D = App3DW.newEuclidianController3DW(kernel);
			euclidianView3D = App3DW.newEuclidianView3DW(euclidianController3D,
			        getSettings().getEuclidian(3));
		}
		return euclidianView3D;
	}


	@Override
	protected GuiManagerW newGuiManager() {
		return App3DW.newGuiManager(this, new BrowserDevice());
	}

	private GCheckBoxMenuItem itemEuclidian3D;

	@Override
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_EUCLIDIAN3D) {
			return !getArticleElement().getDataParamNo3D();
		}
		return super.supportsView(viewID);
	}

	@Override
	public void recalculateEnvironments() {

		super.recalculateEnvironments();

		if (getEuclidianView3D() != null) {
			getEuclidianView3D().getEuclidianController()
			        .calculateEnvironment();
		}

		((App3DCompanionW) companion).recalculateEnvironments();
	}

	@Override
	protected void initFactories() {

		super.initFactories();
		org.geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory.prototype = new GLFactoryW();
		org.geogebra.common.util.CopyPaste.INSTANCE = new CopyPaste3D();
	}

	@Override
	public void updateViewSizes() {
		super.updateViewSizes();
		if (((GuiManager3DW) getGuiManager()).getEuclidian3DPanel() != null) {
			((GuiManager3DW) getGuiManager()).getEuclidian3DPanel()
			        .deferredOnResize();
		}
		((App3DCompanionW) companion).updateViewSizes();
	}

	@Override
	public void updateStyleBars() {
		super.updateStyleBars();
		if (showView(App.VIEW_EUCLIDIAN3D)) {
			getEuclidianView3D().getStyleBar().updateStyleBar();
		}
	}

	@Override
	public boolean isEuclidianView3Dinited() {
		return euclidianView3D != null;
	}

	@Override
	public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel,
	        EuclidianController ec, boolean[] showAxes, boolean showGrid,
	        int id, EuclidianSettings settings) {
		return App3DW.newEuclidianView(evPanel, ec, showAxes, showGrid, id,
		        settings);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel) {
		return App3DW.newEuclidianController(kernel);

	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionW(this);
	}

	@Override
	public void setCurrentFile(HashMap<String, String> file) {
		super.setCurrentFile(file);
		if (this.isEuclidianView3Dinited()) {
			((EuclidianView3DW) getEuclidianView3D()).setCurrentFile(file);
		}
	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManager3DW(this);
		}
		return dialogManager;
	}

	@Override
	public void syncAppletPanelSize(int widthDiff, int heightDiff, int evno) {
		if (evno == EuclidianView.EVNO_3D && hasEuclidianView3D()
		        && getEuclidianView3D().isShowing()) {// or the EuclidianView 2
			if (getSplitLayoutPanel() != null)
				getSplitLayoutPanel().setPixelSize(
				        getSplitLayoutPanel().getOffsetWidth() + widthDiff,
				        getSplitLayoutPanel().getOffsetHeight() + heightDiff);
		} else {
			super.syncAppletPanelSize(widthDiff, heightDiff, evno);
		}
	}

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	public String getCompleteUserInterfaceXML(boolean asPreference) {
		StringBuilder sb = new StringBuilder();

		// save super settings
		sb.append(super.getCompleteUserInterfaceXML(asPreference));

		// save euclidianView3D settings
		if (isEuclidianView3Dinited()) {
			euclidianView3D.getXML(sb, asPreference);
		}

		// save euclidian views for plane settings
		((App3DCompanion) companion).addCompleteUserInterfaceXMLForPlane(sb,
		        asPreference);

		return sb.toString();
	}

	@Override
	public void ggwGraphicsView3DDimChanged(int width, int height) {
		App3DW.ggwGraphicsView3DDimChanged(this, width, height);
	}
}
