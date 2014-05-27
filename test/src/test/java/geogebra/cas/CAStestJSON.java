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
import geogebra.common.util.debug.Log;
import geogebra.main.AppD;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.JFrame;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class CAStestJSON {
	static public boolean silent = false;

	  static GeoGebraCasInterface cas;
	  static Kernel kernel;
	  static AppD app;
	  static CASTestLogger logger;
	  static HashMap<String, String> testcases = new HashMap<String,String>();

	
	@BeforeClass
	  public static void setupCas () {
		app = new AppD(new CommandLineArguments(silent ? new String[] { "--silent", "--giac" } : new String[] { "--giac" }), new JFrame(), false);

	    if (silent) {
	      Log.logger = null;
	    }

	    // Set language to something else than English to test automatic translation.
	    app.setLanguage(Locale.GERMANY);
	    // app.fillCasCommandDict();

	    kernel = app.getKernel();
	    cas = kernel.getGeoGebraCAS();
	    logger = new CASTestLogger();
		
		
		
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("../web/war/__giac.js"));
			JSONObject testsJSON =  new JSONObject(new String(encoded, "utf-8").substring("__giac = ".length()));
			int i = 1;
			while(testsJSON.has(""+i)){
				
			JSONObject test = testsJSON.getJSONObject(""+i);			
			i++;
			testcases.put(test.getString("cmd"),test.getString("result"));
			}
		} catch (Exception e) {
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
	          includesNumericCommand ? StringTemplate.testNumeric : StringTemplate.testTemplate) : f.getOutput(StringTemplate.testTemplate);
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
	
	private static void t (String input, String expectedResult, String ... validResults) {
	    ta(false, input, expectedResult, validResults);
	}
	
	@Test
	public void testExpressions(){
		for(String cmd:testcases.keySet()){
			t(cmd, testcases.get(cmd));
		}
	}
}
