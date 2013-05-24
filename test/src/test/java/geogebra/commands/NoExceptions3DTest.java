package geogebra.commands;

import geogebra.CommandLineArguments;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.main.AppD;
import geogebra3D.App3D;

import java.util.Locale;

import javax.swing.JFrame;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NoExceptions3DTest {
	static AppD app;
	static AlgebraProcessor ap;
	
	@Before
	public void resetSyntaxes(){
		NoExceptionsTest.syntaxes = -1000;
	}
	@After
	public void checkSyntaxes(){
		Assert.assertTrue("unchecked syntaxes: "+NoExceptionsTest.syntaxes,NoExceptionsTest.syntaxes<=0);
	}
	
	private static void  t(String s){
		//NoExceptionsTest.testSyntax(s,app,ap);
	}
	
	//@BeforeClass
	public static void setupApp() {
		app = new App3D(new CommandLineArguments(
				new String[]{"--silent"}), new JFrame(), false);
		app.setLanguage(Locale.US);
		// app.getKernel()
		ap = app.getKernel().getAlgebraProcessor();
	}
	
	@Test
	public void cmdSurfaceCartesian() {
		t("Surface[u*v,u+v,u^2+v^2,u,-1,1,v,1,3]");
	}

	@Test
	public void cmdEnds() {
		// TODO write test
	}

	@Test
	public void cmdCone() {
		// TODO write test
	}

	@Test
	public void cmdCube() {
		// TODO write test
	}

	@Test
	public void cmdBottom() {
		// TODO write test
	}

	@Test
	public void cmdDodecahedron() {
		// TODO write test
	}

	@Test
	public void cmdCylinder() {
		// TODO write test
	}

	@Test
	public void cmdIcosahedron() {
		// TODO write test
	}

	@Test
	public void cmdInfiniteCylinder() {
		// TODO write test
	}

	@Test
	public void cmdInfiniteCone() {
		// TODO write test
	}

	@Test
	public void cmdOctahedron() {
		// TODO write test
	}

	@Test
	public void cmdPerpendicularPlane() {
		// TODO write test
	}
	
	@Test
	public void cmdPlane() {
		// TODO write test
	}
	
	@Test
	public void cmdPlaneBisector() {
		// TODO write test
	}

	@Test
	public void cmdPolyhedron() {
		// TODO write test
	}

	@Test
	public void cmdPyramid() {
		// TODO write test
	}

	@Test
	public void cmdQuadricSide() {
		// TODO write test
	}

	@Test
	public void cmdPrism() {
		// TODO write test
	}
	
	@Test
	public void cmdSphere() {
		// TODO write test
	}
	
	@Test
	public void cmdSurface() {
		// TODO write test
	}

	@Test
	public void cmdTetrahedron() {
		// TODO write test
	}

	@Test
	public void cmdTop() {
		// TODO write test
	}
	
	@Test
	public void cmdIntersectionPaths() {
		// TODO write test
	}
	
	@Test
	public void cmdOrthogonalLine3D() {
		// TODO write test
	}
	
	@Test
	public void cmdIntersectPath() {
		// TODO write test
	}
	
	@Test
	public void cmdIntersectCircle(){
		// TODO write test
	}
	@Test
	public void cmdVolume(){
		// TODO write test
	}	

}
