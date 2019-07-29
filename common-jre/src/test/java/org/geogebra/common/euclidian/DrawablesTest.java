package org.geogebra.common.euclidian;

import java.util.TreeSet;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DrawablesTest {
	private static AppCommon3D app;

	@BeforeClass
	public static void setupApp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	@Test
	public void checkDrawables() {
		String[] def = new String[] { "(1,1)", "Angle[x^2=y^2]", "true",
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
				"audio", "video", "embed", "symbolic" };
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		ap.processAlgebraCommand("toolPic=ToolImage[2]", false);
		GeoAudio au = new GeoAudio(app.getKernel().getConstruction());
		au.setLabel("audio");
		GeoVideo video = new GeoVideo(app.getKernel().getConstruction());
		video.setLabel("video");
		GeoEmbed embed = new GeoEmbed(app.getKernel().getConstruction());
		embed.setLabel("embed");
		GeoSymbolic symbolic = new GeoSymbolic(
				app.getKernel().getConstruction());
		symbolic.setLabel("symbolic");
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
					|| GeoClass.CLIPPINGCUBE3D == type);
		}

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
