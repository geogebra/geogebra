package org.geogebra.common.euclidian;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Objects;
import java.util.TreeSet;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoScriptAction;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.test.LocalizationCommonUTF;
import org.junit.Assert;
import org.junit.Test;

public class DrawablesTest extends BaseUnitTest {

	private GGraphicsCommon graphics;

	@Override
	public AppCommon createAppCommon() {
		graphics = spy(new GGraphicsCommon());
		return new AppCommon3D(new LocalizationCommonUTF(3),
				new AwtFactoryCommon()) {
			@Override
			protected GGraphics2D createGraphics() {
				return graphics;
			}
		};
	}

	@Test
	public void checkDrawables() {
		final String[] def = new String[] { "(1,1)", "Angle[x^2=y^2]", "true",
				"Button[]", "InputBox[]", "x^2+y^2/3=1",
				"Semicircle[(0,0),(1,1)]", "xx", "1<x<2", "x=y", "{(1,1)}",
				"ConvexHull[(0,0),(0,1),(1,0)]", "7",
				"Polygon[(0,0),(0,1),(1,0)]", "Polyline[(0,0),(0,1),(1,0)]",
				"PenStroke[(0,0),(0,1),(1,0)]", "Ray[(0,0),(2,3)]",
				"Segment[(0,0),(2,3)]", "Vector[(0,0),(2,3)]",
				"FormulaText[x^2]", "(t,t^3)", "x^4+y^4=1", "x>y",
				"Spline[(0,0),(0,1),(1,0),(2,3)]", "Turtle[]", "(1,1,0)",
				"Vector[(1,1,0)]", "Segment[(1,1,0),(1,1,1)]",
				"Line[(1,1,0),(1,1,1)]", "Ray[(1,1,0),(1,1,1)]",
				"Ellipse[(2,3,0),(1,1,0),(1,0,0)]",
				"Polygon[(0,0),(0,1),(1,0,0)]", "PolyLine[(0,0),(0,1),(1,0,0)]",
				"Angle[(1,1,0)]", "Net[Cube[(0,0),(1,1)],1]", "xAxis", "zAxis",
				"cub(t)=(t,t,t^3)", "x+y=z", "xx+yy+zz=1", "Cube[(0,0),(1,1)]",
				"Surface[(u,v,u+v),u,0,1,v,0,1]", "x^3=z^3",
				"Cone[(0,0,0),(0,0,1),1]", "Side[Cone[(0,0,0),(0,0,1),1]]",
				"IntersectRegion(x+y+0z=0,Cone[(0,0,0),(0,0,1),1])", "toolPic",
				"PieChart({1,2,3})",
				"audio", "video", "embed", "symbolic", "inlinetext",
				"formula", "table", "mindMap" };

		add("toolPic=ToolImage[2]");
		Construction construction = getKernel().getConstruction();
		GeoAudio au = new GeoAudio(construction);
		au.setLabel("audio");
		GeoVideo video = new GeoVideo(construction);
		video.setLabel("video");
		GeoEmbed embed = new GeoEmbed(construction);
		embed.setLabel("embed");
		GeoFormula formula = new GeoFormula(construction, null);
		formula.setContent("\\frac{a}{b}");
		formula.setLabel("formula");
		GeoSymbolic symbolic = new GeoSymbolic(construction);
		symbolic.setLabel("symbolic");
		GeoInlineText text = new GeoInlineText(construction, new GPoint2D());
		text.setLabel("inlinetext");
		GeoInlineTable table = new GeoInlineTable(construction, new GPoint2D());
		table.setLabel("table");
		GeoMindMapNode mindMap = new GeoMindMapNode(construction, new GPoint2D());
		mindMap.setLabel("mindMap");
		TreeSet<GeoClass> types = new TreeSet<>();
		for (String s : def) {
			GeoElementND geo = add(s);
			DrawableND draw = getApp().getEuclidianView1().newDrawable(geo);
			assertEquals(geo.getDefinitionForInputBar(),
					expectDrawableFor(geo), draw != null);
			types.add(geo.getGeoClassType());
		}
		XmlTestUtil.checkCurrentXML(getApp());
		for (GeoClass type : GeoClass.values()) {
			Assert.assertTrue(type + "", types.contains(type)
					|| GeoClass.IMPLICIT_SURFACE_3D == type
					|| GeoClass.SURFACECARTESIAN == type
					|| GeoClass.CAS_CELL == type || GeoClass.SPACE == type
					|| GeoClass.DEFAULT == type
					|| GeoClass.CLIPPINGCUBE3D == type
					|| GeoClass.INLINE_TEXT == type);
		}

	}

	@Override
	protected GeoElementND add(String s) {
		AlgebraProcessor ap = getKernel().getAlgebraProcessor();
		GeoElementND[] ret = ap.processAlgebraCommand(s, false);
		return ret.length > 0 ? ret[0] : new GeoScriptAction(getKernel().getConstruction());
	}

	@Test
	public void testHatching() {
		GeoElementND poly = add("Polygon(O,O+1,4)");
		poly.setFillType(FillType.HATCH);
		poly.updateVisualStyleRepaint(GProperty.HATCHING);
		verify(graphics, atLeastOnce()).fill(notNull());
	}

	@Test
	public void testLabelPosition() {
		GeoElementND f = add("f:y=100x");
		GeoElementND g = add("g:x=100y");
		f.setLabelVisible(true);
		g.setLabelVisible(true);
		f.update();
		g.update();
		getApp().getEuclidianView1().setRealWorldCoordSystem(-1,
				1, -100, 100);
		// f is diagonal, g close to x-axis
		assertEquals(new GPoint(8, 583), getLabelPosition(f));
		assertEquals(new GPoint(8, 292), getLabelPosition(g));
		getApp().getEuclidianView1().setRealWorldCoordSystem(-120,
				120, -1, 1);
		// g is diagonal, f close to y-axis
		assertEquals(new GPoint(407, 592), getLabelPosition(f));
		assertEquals(new GPoint(83, 592), getLabelPosition(g));
	}

	@Test
	public void testTracing() {
		GeoElementND pt = add("A=(1,1)");
		((Traceable) pt).setTrace(true);
		pt.updateRepaint();
		EuclidianView view = getApp().getActiveEuclidianView();
		assertThat(view.isTraceDrawn(), equalTo(true));
		pt.setEuclidianVisible(false);
		pt.updateRepaint();
		assertThat("trace still drawn but draw trace flag was reset",
				view.isTraceDrawn(), equalTo(true));
		view.zoomAroundCenter(2);
		assertThat(view.isTraceDrawn(), equalTo(false));
	}

	private GPoint getLabelPosition(GeoElementND f) {
		DrawableND draw = Objects.requireNonNull(getDrawable(f));
		return new GPoint((int) draw.getLabelX(), (int) draw.getLabelY());
	}

	private static boolean expectDrawableFor(GeoElementND type) {
		switch (type.getGeoClassType()) {
		case NET:
		case POLYHEDRON:
		case PLANE3D:
		case QUADRIC:
		case QUADRIC_PART:
		case QUADRIC_LIMITED:
		case SURFACECARTESIAN3D:
		case IMPLICIT_SURFACE_3D:
		case AXIS:
		case AXIS3D:
			return false;
		}
		return true;
	}
}
