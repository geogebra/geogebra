package geogebra.web;


import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;
import geogebra.common.util.debug.Log;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.gui.applet.GeoGebraFrameSimple;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppWsimple;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PerformanceTest implements EntryPoint {

	private static ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Date creationDate = new Date();
			nodes.getItem(i).setId(GeoGebraConstants.GGM_CLASS_NAME+i+creationDate.getTime());
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}

	/**
	 * set true if Google Api Js loaded
	 */

	public void onModuleLoad() {
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		Log.logger = new GeoGebraLogger();
		ArticleElement ae = ArticleElement.as(nodes.getItem(0));
		GeoGebraFrameSimple gfs = new GeoGebraFrameSimple();
		ae.setId("ggbPerfTest");
		gfs.setComputedWidth(800);
		gfs.setComputedHeight(600);
		RootPanel.get(ae.getId()).add(gfs);
		AppWsimple app = new AppWsimple(ae, gfs, false);
		App.debug("n"+app.getEuclidianView1().getWidth());
		gfs.setApplication(app);
		gfs.setWidth(800);
		gfs.setHeight(600);
		App.debug("r"+app.getEuclidianView1().getWidth());
		Kernel kernel = app.getKernel();
		Construction cons = kernel.getConstruction();
		GeoPoint A = new GeoPoint(cons,0,0,1);
		A.setLabel("A");
		ExpressionNode exB = new MyVecNode(kernel,A.wrap().apply(Operation.XCOORD).wrap().plus(5),A.wrap().apply(Operation.YCOORD)).wrap();
		AlgoDependentPoint adp = new AlgoDependentPoint(cons, exB, false);
		GeoPoint B = adp.getPoint();
		B.setLabel("B");
		App.debug("x"+app.getEuclidianView1().getWidth());
		app.getEuclidianView1().getGraphicsForPen().setCoordinateSpaceSize(800, 600);
		app.afterLoadFileAppOrNot();
		//use GeoGebraProfilerW if you want to profile, SilentProfiler  for production
		//GeoGebraProfiler.init(new GeoGebraProfilerW());
	}

	public static void loadAppletAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				startGeoGebra(getGeoGebraMobileTags());
			}
			
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.applet.GeoGebraFrameSimple.main(geoGebraMobileTags);
	    
    }
	
	private native void exportGGBElementRenderer() /*-{
 		$wnd.renderGGBElement = $entry(@geogebra.web.gui.applet.GeoGebraFrameSimple::renderArticleElement(Lcom/google/gwt/dom/client/Element;));
	}-*/;

}
