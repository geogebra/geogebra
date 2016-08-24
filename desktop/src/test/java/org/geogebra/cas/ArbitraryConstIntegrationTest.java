package org.geogebra.cas;

import static org.geogebra.test.util.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.cas.logging.CASTestLogger;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.main.AppD;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class ArbitraryConstIntegrationTest {

	static public boolean silent = false;
	static GeoGebraCasInterface cas;
	static Kernel kernel;
	static AppD app;

	/**
	 * Logs all tests which don't give the expected but a valid result.
	 */
	static CASTestLogger logger;

	@BeforeClass
	public static void setupCas() {
		app = new AppD(
				new CommandLineArguments(
						silent ? new String[] { "--silent", "--giac" }
								: new String[] { "--giac" }),
				new JFrame(), false);

		if (silent) {
			Log.logger = null;
		}

		// Set language to something else than English to test automatic
		// translation.
		app.setLanguage(Locale.GERMANY);
		// app.fillCasCommandDict();

		kernel = app.getKernel();
		cas = kernel.getGeoGebraCAS();
		logger = new CASTestLogger();

		// Setting the general timeout to 9 seconds. Feel free to change this.
		kernel.getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(9000);
	}

	/**
	 * Handles the logs about test warnings.
	 */
	@AfterClass
	public static void handleLogs() {
		if (!silent)
			logger.handleLogs();
	}

	/**
	 * Before every test: Clear the construction list to make sure there is
	 * nothing already defined.
	 */
	@Before
	public void beforeTest() {
		kernel.clearConstruction(true);
	}

	/**
	 * @param input
	 *            The input.
	 * @param expectedResult
	 *            The expected result.
	 * @param validResults
	 *            Valid, but undesired results.
	 */
	private static void ta(String input, String expectedResult,
			String... validResults) {
		String result;

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(input);

			f.computeOutput();

			result = getOutput(f);
		} catch (Throwable t) {
			String sts = "";
			StackTraceElement[] st = t.getStackTrace();

			for (int i = 0; i < 10 && i < st.length; i++) {
				StackTraceElement stElement = st[i];
				sts += stElement.getClassName() + ":"
						+ stElement.getMethodName() + stElement.getLineNumber()
						+ "\n";
			}

			result = t.getClass().getName() + ":" + t.getMessage() + sts;
		}

		assertThat(result, equalToIgnoreWhitespaces(logger, input,
				expectedResult, validResults));
	}

	@Rule
	public Timeout globalTimeout = new Timeout(1000000); // 10 seconds max
															// per method
															// tested

	@Test
	public void SolveODE_0() {
		ta("SolveODE[y'=7y^2x^3]", "y = 4 / (4 * c_1 - 7 * x^(4))");
	}

	@Test
	public void SolveODE_1() {
		ta("SolveODE[y''-5y'+6y=0]", "y = c_1 * " + Unicode.EULER_STRING
				+ "^(3 * x) + c_2 * " + Unicode.EULER_STRING + "^(2 * x)");
	}

	@Test
	public void SolveODE_2() {
		ta("SolveODE[y'=5y-3]",
				"y = c_1 * " + Unicode.EULER_STRING + "^(5*x) + 3 / 5");
	}

	@Test
	public void SolveODE_3() {
		ta("SolveODE[y'+y=10]",
				"y = c_1 * " + Unicode.EULER_STRING + "^(-x) + 10");
	}

	@Test
	public void SolveODE_4() {
		ta("SolveODE[y' = (3 - y) / 2]",
				"y = c_1 * " + Unicode.EULER_STRING + "^((-x)/ 2) + 3");
	}

	@Test
	public void SolveODE_5() {
		ta("SolveODE[y' = -2 + y]",
				"y = c_1 * " + Unicode.EULER_STRING + "^(x) + 2");
	}

	@Test
	public void SolveODE_6() {
		ta("SolveODE[y' = y(y - 2)]",
				"y = (-2) / (c_1 *" + Unicode.EULER_STRING + "^(2*x) - 1)");
	}

	@Test
	public void SolveODE_7() {
		ta("SolveODE[y''=y]", "y = c_1 *" + Unicode.EULER_STRING
				+ "^(x) + c_2 *" + Unicode.EULER_STRING + "^(-x)");
	}

	@Test
	public void SolveODE_8() {
		ta("SolveODE[2y''+y'-y=0]", "y = c_1 *" + Unicode.EULER_STRING
				+ "^(-x) + c_2 *" + Unicode.EULER_STRING + "^(x/2)");
	}

	@Test
	public void SolveODE_9() {
		ta("SolveODE[y''-5y=0]",
				"y = c_1 *" + Unicode.EULER_STRING + "^(sqrt(5) * x) + c_2 *"
						+ Unicode.EULER_STRING + "^(-sqrt(5) * x)");
	}

	@Test
	public void SolveODE_10() {
		ta("SolveODE[2y''+3y'=0]",
				"y = c_1 *" + Unicode.EULER_STRING + "^(-3 * x / 2) + c_2");
	}

	@Test
	public void SolveODE_11() {
		ta("SolveODE[y''+2y' + 101y = 0]",
				"y=c_1 * cos(10 * x) *" + Unicode.EULER_STRING
						+ "^(-x) + c_2 * " + Unicode.EULER_STRING
						+ "^(-x)* sin(10 * x)");
	}

	@Test
	public void SolveODE_12() {
		ta("SolveODE[y'' + 4y' + 4y = 0]",
				"y=c_1 * x * " + Unicode.EULER_STRING + "^(-2 * x) + c_2 * "
						+ Unicode.EULER_STRING + "^(-2 * x)");
	}

	@Test
	public void SolveODE_13() {
		ta("SolveODE[y''=2y]",
				"y = c_1 * " + Unicode.EULER_STRING + "^(sqrt(2) * x) + c_2 * "
						+ Unicode.EULER_STRING + "^(-sqrt(2) * x)");
	}

	@Test
	public void Integral_1() {
		ta("Integral[(x+1)/(x+2*sqrt(x)-3)]",
				"15 * log(sqrt(x) + 3) + log(abs(sqrt(x) - 1)) + x - 4*sqrt(x) + c_1");
	}

	@Test
	public void Integral_2() {
		ta("Integral[2sin(x)cos(x)]", "sin(x)^(2) + c_1");
	}

	@Test
	public void Integral_3() {
		ta("Integral[ " + Unicode.EULER_STRING + "^x/(1+ "
				+ Unicode.EULER_STRING + "^(2x))]",
				"arctan(" + Unicode.EULER_STRING + "^(x)) + c_1");
	}

	@Test
	public void Integral_4() {
		ta("Integral[sin(x)(4*cos(x)) " + Unicode.EULER_STRING
				+ "^(2*cos(x)+1)]",
				"-(2*cos(x) - 1) * " + Unicode.EULER_STRING
						+ "^(2*cos(x) + 1) + c_1");
	}

	@Test
	public void Integral_5() {
		ta("Integral[x * cos(a * x)]",
				"cos(a * x) / a^(2) + x * sin(a * x) / a + c_1");
	}

	@Test
	public void Integral_6() {
		ta("Integral[ln(x)/x]", "1 / 2 * log(x)^(2) + c_1");
	}

	@Test
	public void Integral_7() {
		ta("Integral[cos(x)^2 sin(x)]", "(-1) / 3 * cos(x)^(3) + c_1");
	}

	@Test
	public void Integral_8() {
		ta("Integral[(x^5+x^4+2 x^3+2 x^2+5x+9)/(x^2+1)^3]",
				"1 / 4 * (12*x^(3) + 20*x - 4) / (x^(2) + 1)^(2) + 4*arctan(x) + 1 / 2 * log(x^(2) + 1) + c_1");
	}

	@Test
	public void Integral_9() {
		ta("Integral[x/(1-sqrt(2+x))]",
				"-2 * (1 / 3 * sqrt(x + 2) * (x + 2) + 1 / 2 * (x + 2) - sqrt(x + 2) - log(abs(sqrt(x + 2) - 1))) + c_1");
	}

	@Test
	public void Integral_10() {
		ta("Integral[1 / sqrt(x - x^2)]", "arcsin(2*x - 1) + c_1");
	}

	/**
	 * @param input
	 *            The input.
	 * @param inputUpdate
	 *            The input to update the cell.
	 * @param expectedResult
	 *            The expected result.
	 * @param validResults
	 *            Valid, but undesired results.
	 */
	private static void casCellupdate(String input, String inputUpdate,
			String expectedResult, String... validResults) {
		String result;

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(input);
			f.computeOutput();

			f.setInput(inputUpdate);
			f.computeOutput();

			result = getOutput(f);
		} catch (Throwable t) {
			String sts = "";
			StackTraceElement[] st = t.getStackTrace();

			for (int i = 0; i < 10 && i < st.length; i++) {
				StackTraceElement stElement = st[i];
				sts += stElement.getClassName() + ":"
						+ stElement.getMethodName() + stElement.getLineNumber()
						+ "\n";
			}

			result = t.getClass().getName() + ":" + t.getMessage() + sts;
		}

		assertThat(result, equalToIgnoreWhitespaces(logger, input,
				expectedResult, validResults));
	}

	@Test
	public void ArbConst_Integration_1() {
		casCellupdate("Integral[x]", "SolveODE[2y''+3y'=0]",
				"y = c_1 *" + Unicode.EULER_STRING + "^(-3 * x / 2) + c_2");
	}

	@Test
	public void ArbConst_Integration_2() {
		casCellupdate("SolveODE[y''+9y=0]", "SolveODE[y''+4y=0]",
				"y = c_1 * cos(2 * x) + c_2 * sin(2 * x)");
	}

	/**
	 * Note that first cell is updated after second cell.
	 * 
	 * @param cell1Input
	 *            The input of first cell.
	 * @param cell2Input
	 *            The input of second cell.
	 * @param cell1InputUpdate
	 *            The input to update first cell.
	 * @param cell2InputUpdate
	 *            The input to update second cell.
	 * @param expectedResult
	 *            The expected result.
	 * @param validResults
	 *            Valid, but undesired results.
	 */
	private static void casCellupdate2(String cell1Input, String cell2Input,
			String cell1InputUpdate, String cell2InputUpdate,
			String expectedResult1, String expectedResult2) {
		String result1, result2;

		try {
			GeoCasCell f1 = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f1, false);

			f1.setInput(cell1Input);
			f1.computeOutput();

			GeoCasCell f2 = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f2, false);

			f2.setInput(cell2Input);
			f2.computeOutput();

			f2.setInput(cell2InputUpdate);
			f2.computeOutput();

			result2 = getOutput(f2);

			f1.setInput(cell1InputUpdate);
			f1.computeOutput();

			result1 = getOutput(f1);

		} catch (Throwable t) {
			String sts = "";
			StackTraceElement[] st = t.getStackTrace();

			for (int i = 0; i < 10 && i < st.length; i++) {
				StackTraceElement stElement = st[i];
				sts += stElement.getClassName() + ":"
						+ stElement.getMethodName() + stElement.getLineNumber()
						+ "\n";
			}

			result1 = t.getClass().getName() + ":" + t.getMessage() + sts;
			result2 = t.getClass().getName() + ":" + t.getMessage() + sts;
		}
		String[] alternatives = new String[0];
		assertThat(result1, equalToIgnoreWhitespaces(logger, cell1Input,
				expectedResult1, alternatives));
		assertThat(result2, equalToIgnoreWhitespaces(logger, cell2Input,
				expectedResult2, alternatives));
	}

	private static String getOutput(GeoCasCell f2) {
		HashSet<Command> commands = new HashSet<Command>();

		f2.getInputVE().traverse(CommandCollector.getCollector(commands));
		boolean includesNumericCommand = false;
		if (!commands.isEmpty()) {
			for (Command cmd : commands) {
				String cmdName = cmd.getName();
				// Numeric used
				includesNumericCommand = includesNumericCommand
						|| ("Numeric".equals(cmdName)
								&& cmd.getArgumentNumber() > 1);
			}
		}

		return f2.getOutputValidExpression() != null
				? f2.getOutputValidExpression()
						.toString(includesNumericCommand
								? StringTemplate.testNumeric
								: StringTemplate.testTemplate)
				: f2.getOutput(StringTemplate.testTemplate);

	}

	/**
	 * Before redefinition: c_1 in first row, c_2 in second.
	 * 
	 * After redefine 2: c_2 and c_3 in second
	 * 
	 * After redefine 1: c_1 and c_4 in first
	 */
	@Test
	public void ArbConst_Integration_3() {
		casCellupdate2("Integral[x]", "Integral[sin(x)]", "SolveODE[y''+9y=0]",
				"SolveODE[y''+4y=0]", "y = c_1 * cos(3 * x) + c_4 * sin(3 * x)",
				"y = c_2 * cos(2 * x) + c_3 * sin(2 * x)");
	}

	/**
	 * Add first cell, update it, add second cell, update it.
	 * 
	 * @param cell1Input
	 *            The input of first cell.
	 * @param cell2Input
	 *            The input of second cell.
	 * @param cell1InputUpdate
	 *            The input to update first cell.
	 * @param cell2InputUpdate
	 *            The input to update second cell.
	 * @param expectedResult
	 *            The expected result.
	 * @param validResults
	 *            Valid, but undesired results.
	 */
	private static void casCellupdate3(String cell1Input, String cell2Input,
			String cell1InputUpdate, String cell2InputUpdate,
			String expectedResult1, String expectedResult2,
			String... validResults) {
		String result1, result2;

		try {
			GeoCasCell f1 = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f1, false);

			f1.setInput(cell1Input);
			f1.computeOutput();

			f1.setInput(cell1InputUpdate);
			f1.computeOutput();



			result1 = getOutput(f1);

			GeoCasCell f2 = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f2, false);

			f2.setInput(cell2Input);
			f2.computeOutput();

			f2.setInput(cell2InputUpdate);
			f2.computeOutput();

			result2 = getOutput(f2);

		} catch (Throwable t) {
			String sts = "";
			StackTraceElement[] st = t.getStackTrace();

			for (int i = 0; i < 10 && i < st.length; i++) {
				StackTraceElement stElement = st[i];
				sts += stElement.getClassName() + ":"
						+ stElement.getMethodName() + stElement.getLineNumber()
						+ "\n";
			}

			result1 = t.getClass().getName() + ":" + t.getMessage() + sts;
			result2 = t.getClass().getName() + ":" + t.getMessage() + sts;
		}

		assertThat(result1, equalToIgnoreWhitespaces(logger, cell1Input,
				expectedResult1, validResults));
		assertThat(result2, equalToIgnoreWhitespaces(logger, cell2Input,
				expectedResult2, validResults));
	}

	@Test
	/**
	 * First cell: c_1 before redefine, c_1 and c_2 after
	 * 
	 * Second cell: c_3 before redefine, c_3 and c_4 after
	 */
	public void ArbConst_Integration_4() {
		casCellupdate3("Integral[x]", "Integral[sin(x)]", "SolveODE[y''+9y=0]",
				"SolveODE[y''+4y=0]", "y = c_1 * cos(3 * x) + c_2 * sin(3 * x)",
				"y = c_3 * cos(2 * x) + c_4 * sin(2 * x)");
	}

	@Test
	public void ConstMulti() {
		ta("Simplify[SolveODE[ y*ln(2)]]", "y = c_1 * 2^(x)");
		Assert.assertEquals(1, app.getGgbApi().getValue("c_1"), 0.01);
		ta("SolveODE[ x]", "y = c_2 + 1 / 2 * x^(2)");
		Assert.assertEquals(0, app.getGgbApi().getValue("c_2"), 0.01);
	}

	@Test
	public void ReloadTest() {
		ta("f(x):=sin(x)", "sin(x)");
		ta("F(x):=Integral[sin(x)]", "-cos(x) + c_1");
		for (int i = 0; i < 2; i++) {
			app.getKernel().getAlgebraProcessor()
					.processAlgebraCommand("P=(1,1)", true);
			app.getKernel().getAlgebraProcessor()
					.processAlgebraCommand("Q=(1,1)", true);
			app.getGgbApi().undo(true);
			app.getGgbApi().undo(true);
		}
		Assert.assertEquals(app.getGgbApi().getValueString("$2"),
				"F(x):=-cos(x) + c_1");
		String base64 = app.getGgbApi().getBase64();
		app.getKernel().clearConstruction(true);
		app.getGgbApi().setBase64(base64);
		Assert.assertEquals(app.getGgbApi().getValueString("$2"),
				"F(x):=-cos(x) + c_1");
	}

}


