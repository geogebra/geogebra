package geogebra.web.gui.applet;

import geogebra.common.main.App;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.MyRunAsyncCallback;
import geogebra.html5.util.RunAsync;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.WebStatic;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWapplet;
import geogebra.web.main.AppWsimple;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoGebraFrameBoth extends GeoGebraFrame {

	public GeoGebraFrameBoth() {
		super();
	}

	protected AppW createApplication(ArticleElement ae, GeoGebraFrame gf) {
		AppW app = new AppWapplet(ae, gf);
		WebStatic.lastApp = app;
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
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrameBoth();
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


	public void runAsyncAfterSplash() {
		final GeoGebraFrameBoth inst = this;
		final ArticleElement articleElement = ae;

		if (ae.getDataParamGuiOff()) {

			RunAsync.INSTANCE.runAsyncCallback(new MyRunAsyncCallback() {

				public void onSuccess() {
					ResourcesInjector.injectResources();

					inst.app = inst.createApplicationSimple(articleElement, inst);

					inst.app.setCustomToolBar();
					//useDataParamBorder(articleElement, inst);
					//inst.add(inst.app.buildApplicationPanel());
					inst.app.buildApplicationPanel();
				    // need to call setLabels here
					// to print DockPanels' titles
					inst.app.setLabels();
				}
			
				public void onFailure(Throwable reason) {
					App.debug("Async load failed");
				}
			});

		} else {

			RunAsync.INSTANCE.runAsyncCallback(new MyRunAsyncCallback() {

				public void onSuccess() {
					ResourcesInjector.injectResources();

					inst.app = inst.createApplication(articleElement, inst);

					inst.app.setCustomToolBar();
					//useDataParamBorder(articleElement, inst);
				    //inst.add(inst.app.buildApplicationPanel());
					inst.app.buildApplicationPanel();
					    // need to call setLabels here
					// to print DockPanels' titles
					inst.app.setLabels();
				}
				
				public void onFailure(Throwable reason) {
					App.debug("Async load failed");
				}
			});
		}
	}
	
	/**
	 * @param el html element to render into
	 */
	public static void renderArticleElement(Element el) {
		GeoGebraFrame.renderArticleElemntWithFrame(el, new GeoGebraFrameBoth());
	}
}
