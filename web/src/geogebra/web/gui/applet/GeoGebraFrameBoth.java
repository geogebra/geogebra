package geogebra.web.gui.applet;

import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.WebStatic;
import geogebra.web.gui.layout.DockGlassPaneW;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWsimple;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoGebraFrameBoth extends GeoGebraFrame {

	private AppletFactory factory;
	private DockGlassPaneW glass;

	public GeoGebraFrameBoth(AppletFactory factory) {
		super();
		this.factory = factory;
	}

	protected AppW createApplication(ArticleElement ae, GeoGebraFrame gf) {
		AppW app = factory.getApplet(ae, gf);
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
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags, AppletFactory factory) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrameBoth(factory);
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
	public static void renderArticleElement(Element el, AppletFactory factory) {
		GeoGebraFrame.renderArticleElementWithFrame(el, new GeoGebraFrameBoth(factory));
	}
	
	public Object getGlassPane(){
		return this.glass;
	}
}
