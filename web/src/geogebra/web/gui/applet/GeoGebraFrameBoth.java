package geogebra.web.gui.applet;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.html5.js.ResourcesInjector;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.WebStatic;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWapplet;
import geogebra.web.main.AppWsimple;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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

	/**
	 * @param element
	 */
	public static void renderArticleElemnt(final Element element) {
		final ArticleElement article = ArticleElement.as(element);
		Date creationDate = new Date();
		element.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		final GeoGebraFrame inst = new GeoGebraFrameBoth();
		inst.ae = article;
		inst.createSplash(article);
		RootPanel.get(article.getId()).add(inst);
	}

	public void runAsyncAfterSplash() {
		final GeoGebraFrameBoth inst = this;
		final ArticleElement articleElement = ae;

		if (ae.getDataParamGuiOff()) {

			GWT.runAsync(new RunAsyncCallback() {

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

			GWT.runAsync(new RunAsyncCallback() {

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
}
