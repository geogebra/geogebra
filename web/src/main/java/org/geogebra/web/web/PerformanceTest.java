package org.geogebra.web.web;

import java.util.ArrayList;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoDistancePoints;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoIntersectSingle;
import org.geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.algos.AlgoPolygonRegular;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.GeoGebraProfiler;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.js.JavaScriptInjector;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.debug.GeoGebraLogger;
import org.geogebra.web.web.util.debug.GeoGebraProfilerW;

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

	/**
	 * set true if Google Api Js loaded
	 */

	public void onModuleLoad() {
		GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.getInstance().profile();
		NodeList<Element> nodes = Dom
		        .getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		Log.logger = new GeoGebraLogger();
		ArticleElement ae = ArticleElement.as(nodes.getItem(0));
		GeoGebraFrameSimple gfs = new GeoGebraFrameSimple();
		ae.setId("ggbPerfTest");
		gfs.setComputedWidth(800);
		gfs.setComputedHeight(600);
		RootPanel.get(ae.getId()).add(gfs);
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE
		        .propertiesKeysJS());
		AppW app = new AppWsimple(ae, gfs, false);
		gfs.setApplication(app);
		gfs.setWidth(800);
		gfs.setHeight(600);
		Kernel kernel = app.getKernel();
		app.setLabelingStyle(2);
		Construction cons = kernel.getConstruction();

		/** Construction start */
		GeoPoint A = new GeoPoint(cons, 0, 0, 1);
		A.setLabel("A");

		ExpressionNode exB = new MyVecNode(kernel, A.wrap()
		        .apply(Operation.XCOORD).wrap().plus(5), A.wrap().apply(
		        Operation.YCOORD)).wrap();
		GeoPoint B = new AlgoDependentPoint(cons, "B", exB, false).getPoint();

		ExpressionNode exC = new MyVecNode(kernel, B.wrap()
		        .apply(Operation.XCOORD).wrap(), B.wrap()
		        .apply(Operation.YCOORD).wrap().plus(3)).wrap();
		GeoPoint C = new AlgoDependentPoint(cons, "C", exC, false).getPoint();

		ExpressionNode exD = new MyVecNode(kernel, A.wrap()
		        .apply(Operation.XCOORD).wrap(), A.wrap()
		        .apply(Operation.YCOORD).wrap().plus(3)).wrap();
		GeoPoint D = new AlgoDependentPoint(cons, "D", exD, false).getPoint();

		GeoPoint E = new AlgoMidpoint(cons, "E", B, C).getPoint();

		GeoSegment a = new AlgoJoinPointsSegment(cons, "a", E, C).getSegment();

		GeoPointND F = new AlgoPointOnPath(cons, "F", a, 5, 2).getP();

		AlgoPolygonRegular regPoly1 = new AlgoPolygonRegular(cons,
		        new String[] { "poly1", "f", "c", "g", "h", "G", "H" }, F, C,
		        new GeoNumeric(cons, 4));
		GeoPoint G = regPoly1.getPoly().getPoint(2);
		GeoPoint H = regPoly1.getPoly().getPoint(3);

		GeoSegment b = new AlgoJoinPointsSegment(cons, "b", D, A).getSegment();

		GeoSegment d = new AlgoJoinPointsSegment(cons, "d", A, B).getSegment();

		GeoSegment e = new AlgoJoinPointsSegment(cons, "e", B, C).getSegment();

		GeoSegment i = new AlgoJoinPointsSegment(cons, "i", C, D).getSegment();

		GeoConic k = new AlgoCirclePointRadius(cons, "k", D,
		        new AlgoDistancePoints(cons, C, F).getDistance()).getCircle();

		GeoConic p = new AlgoCirclePointRadius(cons, "p", A,
		        new AlgoDistancePoints(cons, C, F).getDistance()).getCircle();

		GeoConic q = new AlgoCirclePointRadius(cons, "q", B,
		        new AlgoDistancePoints(cons, C, F).getDistance()).getCircle();

		GeoPoint L = new AlgoIntersectSingle("L", new AlgoIntersectLineConic(
		        cons, i, k), 0).getPoint();

		GeoPoint M = new AlgoIntersectSingle("M", new AlgoIntersectLineConic(
		        cons, d, p), 0).getPoint();

		GeoPoint N = new AlgoIntersectSingle("L", new AlgoIntersectLineConic(
		        cons, e, q), 0).getPoint();

		AlgoPolygonRegular regPoly2 = new AlgoPolygonRegular(cons,
		        new String[] { "poly2", "j", "l", "o", "n", "O", "P" }, L, D,
		        new GeoNumeric(cons, 4));
		GeoPoint O = regPoly2.getPoly().getPoint(2);
		GeoPoint P = regPoly2.getPoly().getPoint(3);

		AlgoPolygonRegular regPoly3 = new AlgoPolygonRegular(cons,
		        new String[] { "poly3", "m", "r", "s", "t", "Q", "R" }, A, M,
		        new GeoNumeric(cons, 4));
		GeoPoint Q = regPoly3.getPoly().getPoint(2);
		GeoPoint R = regPoly3.getPoly().getPoint(3);

		AlgoPolygonRegular regPoly4 = new AlgoPolygonRegular(cons,
		        new String[] { "poly4", "a1", "b1", "c1", "d1", "S", "T" }, B,
		        N, new GeoNumeric(cons, 4));
		GeoPoint S = regPoly4.getPoly().getPoint(2);
		GeoPoint T = regPoly4.getPoly().getPoint(3);

		AlgoPolygon ptPoly5 = new AlgoPolygon(cons, null, new GeoPointND[] { Q,
		        M, T, S });

		AlgoPolygon ptPoly6 = new AlgoPolygon(cons, null, new GeoPointND[] { S,
		        N, F, H });

		AlgoPolygon ptPoly7 = new AlgoPolygon(cons, null, new GeoPointND[] { H,
		        G, L, P });

		AlgoPolygon ptPoly8 = new AlgoPolygon(cons, null, new GeoPointND[] { O,
		        R, Q, P });

		AlgoPolygon ptPoly9 = new AlgoPolygon(cons, null, new GeoPointND[] { Q,
		        S, H, P });

		GeoSegment e1 = new AlgoJoinPointsSegment(cons, "e1", Q, M)
		        .getSegment();

		GeoSegment f1 = new AlgoJoinPointsSegment(cons, "f1", M, T)
		        .getSegment();

		GeoSegment g1 = new AlgoJoinPointsSegment(cons, "g1", T, S)
		        .getSegment();

		GeoSegment h1 = new AlgoJoinPointsSegment(cons, "h1", A, M)
		        .getSegment();

		GeoSegment i1 = new AlgoJoinPointsSegment(cons, "i1", A, R)
		        .getSegment();

		GeoSegment j1 = new AlgoJoinPointsSegment(cons, "j1", T, B)
		        .getSegment();

		GeoSegment k1 = new AlgoJoinPointsSegment(cons, "k1", B, N)
		        .getSegment();

		GeoSegment l1 = new AlgoJoinPointsSegment(cons, "l1", (GeoPoint) F, C)
		        .getSegment();

		GeoSegment m1 = new AlgoJoinPointsSegment(cons, "m1", C, G)
		        .getSegment();

		GeoSegment n1 = new AlgoJoinPointsSegment(cons, "n1", L, D)
		        .getSegment();

		GeoSegment p1 = new AlgoJoinPointsSegment(cons, "p1", D, O)
		        .getSegment();

		/** Construction end */
		app.getEuclidianView1().getGraphicsForPen()
		        .setCoordinateSpaceSize(800, 600);
		app.afterLoadFileAppOrNot();
		GeoGebraProfiler.getInstance().profileEnd();
		// use GeoGebraProfilerW if you want to profile, SilentProfiler for
		// production
		// GeoGebraProfiler.init(new GeoGebraProfilerW());
	}

	public static void loadAppletAsync() {
		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {
				startGeoGebra(ArticleElement.getGeoGebraMobileTags());
			}

			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub

			}
		});
	}

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {

		org.geogebra.web.html5.gui.GeoGebraFrameSimple.main(geoGebraMobileTags);

	}

	private native void exportGGBElementRenderer() /*-{
   		$wnd.renderGGBElement = $entry(@org.geogebra.web.html5.gui.GeoGebraFrameSimple::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
   		@org.geogebra.web.html5.gui.GeoGebraFrame::renderGGBElementReady()();
   	}-*/;

}
