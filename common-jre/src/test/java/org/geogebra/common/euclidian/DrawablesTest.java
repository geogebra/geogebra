package org.geogebra.common.euclidian;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.TreeSet;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoFormula;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DrawablesTest {
	private AppCommon3D app;
	private GGraphicsCommon graphics;

	@Before
	public void setupApp() {
		graphics = spy(new GGraphicsCommon());
		app = new AppCommon3D(new LocalizationCommon(3),
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
				"Polyline[(0,0),(0,1),(1,0),true]", "Ray[(0,0),(2,3)]",
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
				"audio", "video", "embed", "symbolic", "inlinetext", "formula", "table" };
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		ap.processAlgebraCommand("toolPic=ToolImage[2]", false);
		Construction construction = app.getKernel().getConstruction();
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
		TreeSet<GeoClass> types = new TreeSet<>();
		for (int i = 0; i < def.length; i++) {
			GeoElementND geo = ap.processAlgebraCommand(def[i], false)[0];
			DrawableND draw = app.getEuclidianView1().newDrawable(geo);
			Assert.assertEquals(geo.getDefinitionForInputBar(),
					expectDrawableFor(geo), draw != null);
			types.add(geo.getGeoClassType());
		}
		XmlTestUtil.testCurrentXML(app);
		for (GeoClass type : GeoClass.values()) {
			Assert.assertTrue(type + "", types.contains(type)
					|| (GeoClass.IMPLICIT_SURFACE_3D == type
							&& !app.has(Feature.IMPLICIT_SURFACES))
					|| GeoClass.SURFACECARTESIAN == type
					|| GeoClass.CAS_CELL == type || GeoClass.SPACE == type
					|| GeoClass.DEFAULT == type
					|| GeoClass.CLIPPINGCUBE3D == type
					|| GeoClass.INLINE_TEXT == type);
		}

	}

	@Test
	public void testHatching() {
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		GeoElementND poly = ap.processAlgebraCommand("Polygon(O,O+1,4)", false)[0];
		poly.setFillType(FillType.HATCH);
		poly.updateVisualStyleRepaint(GProperty.HATCHING);
		verify(graphics, atLeastOnce()).fill(notNull());
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
