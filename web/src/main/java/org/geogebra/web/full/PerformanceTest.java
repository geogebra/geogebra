package org.geogebra.web.full;

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
import org.geogebra.web.html5.gui.GeoGebraFrameSimple;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.AppWsimple;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.debug.GeoGebraProfilerW;
import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PerformanceTest implements EntryPoint {

	/**
	 * set true if Google Api Js loaded
	 */

	@Override
	@SuppressFBWarnings({ "RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT",
			"object adds itself to construction" })
	public void onModuleLoad() {
		GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.getInstance().profile();
		NodeList<Element> nodes = Dom
		        .getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		Log.setLogger(new LoggerW());
		ArticleElement ae = ArticleElement.as(nodes.getItem(0));
		GeoGebraFrameSimple gfs = new GeoGebraFrameSimple(ae);
		ae.setId("ggbPerfTest");
		gfs.setComputedWidth(800);
		gfs.setComputedHeight(600);
		RootPanel.get(ae.getId()).add(gfs);
		AppW app = new AppWsimple(ae, gfs, false);
		gfs.setApplication(app);
		gfs.setWidth(800);
		gfs.setHeight(600);
		Kernel kernel = app.getKernel();
		app.setLabelingStyle(2);
		Construction cons = kernel.getConstruction();

		/** Construction start */
		GeoPoint ptA = new GeoPoint(cons, 0, 0, 1);
		ptA.setLabel("A");

		ExpressionNode exB = new MyVecNode(kernel, ptA.wrap()
		        .apply(Operation.XCOORD).wrap().plus(5), ptA.wrap().apply(
		        Operation.YCOORD)).wrap();
		GeoPoint ptB = new AlgoDependentPoint(cons, "B", exB, false).getPoint();

		ExpressionNode exC = new MyVecNode(kernel, ptB.wrap()
		        .apply(Operation.XCOORD).wrap(), ptB.wrap()
		        .apply(Operation.YCOORD).wrap().plus(3)).wrap();
		GeoPoint ptC = new AlgoDependentPoint(cons, "C", exC, false).getPoint();

		ExpressionNode exD = new MyVecNode(kernel, ptA.wrap()
		        .apply(Operation.XCOORD).wrap(), ptA.wrap()
		        .apply(Operation.YCOORD).wrap().plus(3)).wrap();
		GeoPoint ptD = new AlgoDependentPoint(cons, "D", exD, false).getPoint();

		GeoPoint ptE = new AlgoMidpoint(cons, ptB, ptC).getPoint();
		ptE.setLabel("E");

		GeoSegment a = new AlgoJoinPointsSegment(cons, "a", ptE, ptC).getSegment();

		GeoPointND ptF = new AlgoPointOnPath(cons, a, 5, 2).getP();
		ptF.setLabel("F");

		AlgoPolygonRegular regPoly1 = new AlgoPolygonRegular(cons,
		        new String[] { "poly1", "f", "c", "g", "h", "G", "H" }, ptF, ptC,
		        new GeoNumeric(cons, 4));
		GeoPoint ptG = regPoly1.getPoly().getPoint(2);
		GeoPoint ptH = regPoly1.getPoly().getPoint(3);

		new AlgoJoinPointsSegment(cons, "b", ptD, ptA).getSegment();

		GeoSegment d = new AlgoJoinPointsSegment(cons, "d", ptA, ptB).getSegment();

		GeoSegment e = new AlgoJoinPointsSegment(cons, "e", ptB, ptC).getSegment();

		GeoSegment i = new AlgoJoinPointsSegment(cons, "i", ptC, ptD).getSegment();

		GeoConic k = new AlgoCirclePointRadius(cons, ptD,
		        new AlgoDistancePoints(cons, ptC, ptF).getDistance()).getCircle();
		k.setLabel("k");

		GeoConic p = new AlgoCirclePointRadius(cons, ptA,
		        new AlgoDistancePoints(cons, ptC, ptF).getDistance()).getCircle();
		p.setLabel("p");
		GeoConic q = new AlgoCirclePointRadius(cons, ptB,
		        new AlgoDistancePoints(cons, ptC, ptF).getDistance()).getCircle();
		q.setLabel("q");
		GeoPoint ptL = new AlgoIntersectSingle("L", new AlgoIntersectLineConic(
		        cons, i, k), 0).getPoint();

		GeoPoint ptM = new AlgoIntersectSingle("M", new AlgoIntersectLineConic(
		        cons, d, p), 0).getPoint();

		GeoPoint ptN = new AlgoIntersectSingle("L", new AlgoIntersectLineConic(
		        cons, e, q), 0).getPoint();

		AlgoPolygonRegular regPoly2 = new AlgoPolygonRegular(cons,
		        new String[] { "poly2", "j", "l", "o", "n", "O", "P" }, ptL, ptD,
		        new GeoNumeric(cons, 4));
		GeoPoint ptO = regPoly2.getPoly().getPoint(2);
		GeoPoint ptP = regPoly2.getPoly().getPoint(3);

		AlgoPolygonRegular regPoly3 = new AlgoPolygonRegular(cons,
		        new String[] { "poly3", "m", "r", "s", "t", "Q", "R" }, ptA, ptM,
		        new GeoNumeric(cons, 4));
		GeoPoint ptQ = regPoly3.getPoly().getPoint(2);
		GeoPoint ptR = regPoly3.getPoly().getPoint(3);

		AlgoPolygonRegular regPoly4 = new AlgoPolygonRegular(cons,
		        new String[] { "poly4", "a1", "b1", "c1", "d1", "S", "T" }, ptB,
		        ptN, new GeoNumeric(cons, 4));
		GeoPoint ptS = regPoly4.getPoly().getPoint(2);
		GeoPoint ptT = regPoly4.getPoly().getPoint(3);

		new AlgoPolygon(cons, null, new GeoPointND[] { ptQ, ptM, ptT, ptS })
				.getOutput();

		new AlgoPolygon(cons, null, new GeoPointND[] { ptS, ptN, ptF, ptH })
				.getOutput();

		new AlgoPolygon(cons, null, new GeoPointND[] { ptH, ptG, ptL, ptP })
				.getOutput();

		new AlgoPolygon(cons, null, new GeoPointND[] { ptO, ptR, ptQ, ptP })
				.getOutput();

		new AlgoPolygon(cons, null, new GeoPointND[] { ptQ, ptS, ptH, ptP })
				.getOutput();

		new AlgoJoinPointsSegment(cons, "e1", ptQ, ptM)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "f1", ptM, ptT)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "g1", ptT, ptS)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "h1", ptA, ptM)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "i1", ptA, ptR)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "j1", ptT, ptB)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "k1", ptB, ptN)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "l1", (GeoPoint) ptF, ptC)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "m1", ptC, ptG)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "n1", ptL, ptD)
		        .getSegment();

		new AlgoJoinPointsSegment(cons, "p1", ptD, ptO)
		        .getSegment();

		/** Construction end */
		app.getEuclidianView1().getGraphicsForPen()
		        .setCoordinateSpaceSize(800, 600);
		app.afterLoadFileAppOrNot(false);
		GeoGebraProfiler.getInstance().profileEnd();
	}

	/**
	 * @param geoGebraMobileTags
	 *            article tags
	 */
	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
		GeoGebraFrameSimple.main(geoGebraMobileTags);
	}

}
