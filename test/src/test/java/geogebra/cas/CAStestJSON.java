package geogebra.cas;

import static geogebra.test.util.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.junit.Assert.assertThat;
import geogebra.CommandLineArguments;
import geogebra.cas.logging.CASTestLogger;
import geogebra.common.kernel.GeoGebraCasInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.common.util.debug.Log;
import geogebra.main.AppD;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.JFrame;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CAStestJSON {

	  static GeoGebraCasInterface cas;
	  static Kernel kernel;
	  static AppD app;
	  static CASTestLogger logger;
	  static HashMap<String,HashMap<String, String>> testcases = new HashMap<String,HashMap<String,String>>();

	
	@BeforeClass
	  public static void setupCas () {
		app = new AppD(new CommandLineArguments(new String[] { "--giac" }), new JFrame(), false);

	    // Set language to something else than English to test automatic translation.
	    app.setLanguage(Locale.GERMANY);
	    // app.fillCasCommandDict();

	    kernel = app.getKernel();
	    cas = kernel.getGeoGebraCAS();
	    logger = new CASTestLogger();
		
		
		
		try {
			Log.debug("CAS: loading testcases");
			byte[] encoded = Files.readAllBytes(Paths.get("../web/war/__giac.js"));
			Log.debug("CAS: parsing testcases");
			JSONObject testsJSON =  new JSONObject(new String(encoded, "utf-8").substring("__giac = ".length()));
			Log.debug("CAS: testcases parsed");
			int i = 1;
			while(testsJSON.has(""+i)){
				
			JSONObject test = testsJSON.getJSONObject(""+i);			
			i++;
			String cat = "general";
			if(test.has("cat")){
				cat = test.getString("cat");
			}
			if(!testcases.containsKey(cat)){
				testcases.put(cat, new HashMap<String,String>());
			}
			testcases.get(cat).put(test.getString("cmd"),test.getString("result"));
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	private static void ta (boolean tkiontki, String input, String expectedResult, String ... validResults) {
	    String result;

	    try {
	      GeoCasCell f = new GeoCasCell(kernel.getConstruction());
	      kernel.getConstruction().addToConstructionList(f, false);

	      f.setInput(input);

	      if (tkiontki) {
	        f.setEvalCommand("KeepInput");
	      }

	      f.computeOutput();

	      boolean includesNumericCommand = false;
	      HashSet<Command> commands = new HashSet<Command>();

	      f.getInputVE().traverse(CommandCollector.getCollector(commands));

	      if (!commands.isEmpty()) {
	        for (Command cmd : commands) {
	          String cmdName = cmd.getName();
	          // Numeric used
	          includesNumericCommand = includesNumericCommand || ("Numeric".equals(cmdName) && cmd.getArgumentNumber() > 1);
	        }
	      }

	      result = f.getOutputValidExpression() != null ? f.getOutputValidExpression().toString(
	          includesNumericCommand ? StringTemplate.testNumeric : StringTemplate.testTemplateJSON) : f.getOutput(StringTemplate.testTemplate);
	    } catch (Throwable t) {
	      String sts = "";
	      StackTraceElement[] st = t.getStackTrace();

	      for (int i = 0; i < 10 && i < st.length; i++) {
	        StackTraceElement stElement = st[i];
	        sts += stElement.getClassName() + ":" + stElement.getMethodName() + stElement.getLineNumber() + "\n";
	      }

	      result = t.getClass().getName() + ":" + t.getMessage() + sts;
	    }

	    assertThat(result, equalToIgnoreWhitespaces(logger, input, expectedResult, validResults));
	  }
	
	private static void t (String input, String expectedResult) {
		String[] validResults = expectedResult.split("\\|OR\\|");
	    ta(false, input, validResults[0], validResults);
	}
	
	private static void testCat(String name){
		for(String cmd:testcases.get(name).keySet()){
			t(cmd, testcases.get(name).get(cmd));
		}
		Assert.assertNotEquals(0, testcases.get(name).size());
	}
	@Test
	public void testExpressions(){
		testCat("general");
	}
	
	@Test
	public void testLimit(){
		testCat("Limit");
	}
	
	@Test
	public void testLimitAbove(){
		testCat("LimitAbove");
	}
	
	@Test
	public void testDerivative(){
		testCat("Derivative");
	}
	@Test
	public void testIntegral(){
		testCat("Integral");
	}
}
