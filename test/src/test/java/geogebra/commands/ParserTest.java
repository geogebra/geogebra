package geogebra.commands;

import geogebra.CommandLineArguments;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.util.Locale;

import javax.swing.JFrame;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class ParserTest {
	static AppD app;
	static AlgebraProcessor ap;

	@BeforeClass
	public static void setupCas() {
		app = new AppD(new CommandLineArguments(
				new String[]{}), new JFrame(), false);
		app.setLanguage(Locale.US);
	}
	
	@Test
	public void testBrackets(){
		try {
			
			long l = System.currentTimeMillis();
			app.getKernel().getParser().
			parseGeoGebraExpression("{{{{{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}}}}");
			app.getKernel().getParser().
			parseGeoGebraExpression("(((((((((((((((((((((((1)))))))))))))))))))))))");
			app.getKernel().getParser().
			parseGeoGebraExpression("If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,42" +
					"]]]]]]]]]]]]]]]]]]]]]]]]");
			l = System.currentTimeMillis() -l;
			App.debug("TIME"+l);
			Assert.assertTrue("Too long:"+l, l<100);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}