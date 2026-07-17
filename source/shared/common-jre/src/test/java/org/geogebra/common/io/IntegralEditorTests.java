/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.io;

import static org.geogebra.editor.share.util.JavaKeyCodes.VK_BACK_SPACE;
import static org.geogebra.editor.share.util.JavaKeyCodes.VK_DELETE;
import static org.geogebra.editor.share.util.JavaKeyCodes.VK_DOWN;
import static org.geogebra.editor.share.util.JavaKeyCodes.VK_HOME;
import static org.geogebra.editor.share.util.JavaKeyCodes.VK_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.util.CommandSyntaxLookupImpl;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.IntegralHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

// Tabs in CsvSources
@SuppressWarnings("checkstyle:MixedTabs")
class IntegralEditorTests {
	// Integral caret paths
	private static final Integer[] BEFORE_INTEGRAL = {0};
	private static final Integer[] AFTER_INTEGRAL = {1};
	private static final Integer[] LOWER_LIMIT_START = {0, 0, 0};
	private static final Integer[] LOWER_LIMIT_END = {1, 0, 0};
	private static final Integer[] UPPER_LIMIT_START = {0, 1, 0};
	private static final Integer[] UPPER_LIMIT_END = {1, 1, 0};
	private static final Integer[] INTEGRAND_START = {0, 2, 0};
	private static final Integer[] INTEGRAND_END = {1, 2, 0};
	private static final Integer[] VARIABLE_START = {0, 3, 0};
	private static final Integer[] VARIABLE_END = {1, 3, 0};

	private EditorChecker checker;
	private EditorChecker casChecker;
	private EditorChecker localizedChecker;

	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@BeforeEach
	void setUp() {
		AppCommon app = AppCommonFactory.create();
		checker = new EditorChecker(app);
		checker.getMathField().getInternal().getInputController()
				.setCommandSyntaxLookup(new CommandSyntaxLookupImpl(app));

		AppCommon casApp = AppCommonFactory.create(new AppConfigCas());
		casChecker = new EditorChecker(casApp);
		casChecker.getMathField().getInternal().getInputController()
				.setCommandSyntaxLookup(new CommandSyntaxLookupImpl(casApp));

		AppCommon localizedApp = AppCommonFactory.create();
		localizedApp.setLocale(Locale.ITALIAN);
		localizedChecker = new EditorChecker(localizedApp);
		localizedChecker.setFormatConverter(new SyntaxAdapterImpl(localizedApp.getKernel()));
		localizedChecker.getMathField().getInternal().getInputController()
				.setCommandSyntaxLookup(new CommandSyntaxLookupImpl(localizedApp));
	}

	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			"INTEGRAL;			f;		x;	'';	'';	true;	false;	Integral(f)",
			"INTEGRAL;			f;		t;	'';	'';	false;	false;	Integral(f,t)",
			"INTEGRAL;			f;		'';	'';	'';	false;	false;	Integral(f)",
			"INTEGRAL;			x^2;	x;	0;	1;	true;	false;	Integral(x^(2),0,1)",
			"INTEGRAL;			t^2;	t;	0;	1;	false;	false;	Integral(t^(2),t,0,1)",
			"INTEGRAL;			f;		'';	0;	1;	false;	false;	Integral(f,0,1)",
			"INTEGRAL;			f;		x;	'';	'';	true;	true;	Integral(f)",
			"INTEGRAL;			x^2;	x;	0;	1;	true;	true;	Integral(x^(2),0,1)",
			"N_INTEGRAL;		f;		x;	'';	'';	true;	false;	NIntegral(f)",
			"N_INTEGRAL;		f;		t;	'';	'';	false;	false;	NIntegral(f,t)",
			"N_INTEGRAL;		x^2;	x;	0;	1;	true;	false;	NIntegral(x^(2),0,1)",
			"N_INTEGRAL;		t^2;	t;	0;	1;	false;	false;	NIntegral(t^(2),t,0,1)",
			"N_INTEGRAL;		f;		x;	'';	'';	true;	true;	NIntegral(f)",
			"INTEGRAL_SYMBOLIC;	f;		x;	'';	'';	true;	false;	IntegralSymbolic(f)",
			"INTEGRAL_SYMBOLIC;	f;		t;	'';	'';	false;	false;	IntegralSymbolic(f,t)",
			"INTEGRAL_SYMBOLIC;	f;		'';	'';	'';	false;	false;	IntegralSymbolic(f)",
			"INTEGRAL_SYMBOLIC;	t^2;	t;	0;	1;	false;	false;	IntegralSymbolic(t^(2),t)",
			"INTEGRAL_SYMBOLIC;	t^2;	t;	0;	1;	false;	true;	IntegralSymbolic(t^(2),t)",
	})
	void testIntegralSerialization(Tag tag, String integrand, String variable,
			String lowerLimit, String upperLimit, boolean autoDefaultVariable,
			boolean limitsVisible, String expected) throws ParseException {
		TemplateCatalog catalog = new TemplateCatalog();
		Parser parser = new Parser(catalog);
		FunctionNode integral = new FunctionNode(catalog.getGeneral(tag));
		integral.setIntegralAutoDefaultVariable(autoDefaultVariable);
		integral.setIntegralLimitsVisible(limitsVisible);
		integral.setChild(0, parser.parse(lowerLimit).getRootNode());
		integral.setChild(1, parser.parse(upperLimit).getRootNode());
		integral.setChild(2, parser.parse(integrand).getRootNode());
		integral.setChild(3, parser.parse(variable).getRootNode());
		SequenceNode root = new SequenceNode();
		root.addChild(integral);

		assertEquals(expected, new GeoGebraSerializer(null).serialize(new Formula(catalog, root)));
	}

	@SuppressWarnings("checkstyle:LineLength")
	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			"Integral(<Function>);											INTEGRAL;	x;	true;	false",
			"Integral(<Function>, <Variable>);								INTEGRAL;	'';	true;	false",
			"Integral(<Function>, <Start x-Value>, <End x-Value>);			INTEGRAL;	x;	true;	true",
			"NIntegral(<Function>);											N_INTEGRAL;	x;	true;	false",
			"NIntegral(<Function>, <Start x-Value>, <End x-Value>);			N_INTEGRAL;	x;	true;	true",
			"IntegralSymbolic(<Function>);							INTEGRAL_SYMBOLIC;	x;	true;	false",
			"IntegralSymbolic(<Function>, <Variable>);				INTEGRAL_SYMBOLIC;	'';	true;	false",
	})
	void testInitialInsertedIntegralStructureFromSyntax(String syntax, Tag tag,
														String variable, boolean autoDefaultVariable, boolean limitsVisible) {
		KeyboardInputAdapter.onKeyboardInput(checker.getMathField().getInternal(), syntax);
		FunctionNode integral = getInsertedFunction(checker);
		assertEquals(tag, integral.getName());
		assertEquals(variable, new GeoGebraSerializer(null).serialize(
				integral.getChild(IntegralHelper.VARIABLE), new StringBuilder()).toString());
		assertEquals(autoDefaultVariable, integral.isIntegralAutoDefaultVariable());
		assertEquals(limitsVisible, integral.isIntegralLimitsVisible());
		assertEquals(limitsVisible, IntegralHelper.shouldRenderLimits(integral, null));
	}

	@SuppressWarnings("checkstyle:LineLength")
	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			"Integral(<Function>);											INTEGRAL;	x;	true;	false",
			"Integral(<Function>, <Variable>);								INTEGRAL;	'';	true;	false",
			"Integral(<Function>, <Start x-Value>, <End x-Value>);			INTEGRAL;	x;	true;	true",
			"Integral(<Function>, <Variable>, <Start Value>, <End Value>);	INTEGRAL;	'';	false;	true",
			"NIntegral(<Function>, <Start x-Value>, <End x-Value>);			N_INTEGRAL;	x;	true;	true",
			"NIntegral(<Function>, <Variable>, <Start Value>, <End Value>);	N_INTEGRAL;	'';	false;	true",
			"IntegralSymbolic(<Function>);							INTEGRAL_SYMBOLIC;	x;	true;	false",
			"IntegralSymbolic(<Function>, <Variable>);				INTEGRAL_SYMBOLIC;	'';	true;	false",
	})
	void testInitialInsertedCasIntegralStructureFromSyntax(String syntax, Tag tag,
														   String variable, boolean autoDefaultVariable, boolean limitsVisible) {
		KeyboardInputAdapter.onKeyboardInput(casChecker.getMathField().getInternal(), syntax);
		FunctionNode integral = getInsertedFunction(casChecker);
		assertEquals(tag, integral.getName());
		assertEquals(variable, new GeoGebraSerializer(null).serialize(
				integral.getChild(IntegralHelper.VARIABLE), new StringBuilder()).toString());
		assertEquals(autoDefaultVariable, integral.isIntegralAutoDefaultVariable());
		assertEquals(limitsVisible, integral.isIntegralLimitsVisible());
		assertEquals(limitsVisible, IntegralHelper.shouldRenderLimits(integral, null));
	}

	@Test
	void testKeyboardIntegralCommandCreatesInlineIntegral() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.checkRaw("SequenceNode[FnINTEGRAL["
				+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[x]]]");
	}

	@Test
	void testIntegralWithEvaluateFromSuggestionStaysGenericCommand() {
		KeyboardInputAdapter.onKeyboardInput(checker.getMathField().getInternal(),
				"Integral( <Function>, <Start x-Value>, <End x-Value>, <Boolean Evaluate> )");
		assertEquals(Tag.APPLY, getInsertedFunction(checker).getName());
	}

	@Test
	void testExcludedIntegralSyntaxDoesNotStartWithEmptyArgument() {
		checker.convertFormulaForAV("a=Integral(x,1,2,true)")
				.checkRaw("SequenceNode[a, =, FnAPPLY[SequenceNode[I, n, t, e, g, r, a, l], "
						+ "SequenceNode[x], SequenceNode[1], SequenceNode[2], "
						+ "SequenceNode[t, r, u, e]]]");
	}

	@Test
	void testNIntegralCurveFromSuggestionStaysGenericCommand() {
		KeyboardInputAdapter.onKeyboardInput(checker.getMathField().getInternal(),
				"NIntegral( <Function>, <Start x-Value>, <Start y-Value>, <End x-Value> )");
		assertEquals(Tag.APPLY, getInsertedFunction(checker).getName());
	}

	@Test
	void testCasIntegralFromSuggestionCreatesInlineIntegral() {
		KeyboardInputAdapter.onKeyboardInput(casChecker.getMathField().getInternal(),
				"Integral( <Function>, <Variable>, <Start Value>, <End Value> )");
		assertEquals(Tag.INTEGRAL, getInsertedFunction(casChecker).getName());
	}

	@Test
	void testCasNIntegralFromSuggestionCreatesInlineIntegral() {
		KeyboardInputAdapter.onKeyboardInput(casChecker.getMathField().getInternal(),
				"NIntegral( <Function>, <Variable>, <Start Value>, <End Value> )");
		assertEquals(Tag.N_INTEGRAL, getInsertedFunction(casChecker).getName());
	}

	@Test
	void testIntegralWithEmptyVariableFromSuggestionEvaluatesWithDefaultVariable() {
		KeyboardInputAdapter.onKeyboardInput(checker.getMathField().getInternal(),
				"Integral( <Function>, <Variable> )");
		checker.type("x");
		assertEquals("Integral(x)", new GeoGebraSerializer(null)
				.serialize(checker.getMathField().getInternal().getFormula()));
	}

	@Test
	void testVisitedEmptyVariableFromSuggestionEvaluatesWithDefaultVariable() {
		KeyboardInputAdapter.onKeyboardInput(checker.getMathField().getInternal(),
				"Integral( <Function>, <Variable> )");
		checker.right(1).checkCaret(VARIABLE_START)
				.left(1).checkCaret(INTEGRAND_START)
				.type("x");
		assertEquals("Integral(x)", new GeoGebraSerializer(null)
				.serialize(checker.getMathField().getInternal().getFormula()));
	}

	@Test
	void testLocalizedIntegralWithIntegrandAndVariableFromSuggestionCreatesInlineIntegral() {
		KeyboardInputAdapter.onKeyboardInput(localizedChecker.getMathField().getInternal(),
				"Integrale( <Funzione>, <Variabile> )");
		assertEquals(Tag.INTEGRAL, getInsertedFunction(localizedChecker).getName());
	}

	@Test
	void testLocalizedIntegralWithEvaluateFromSuggestionStaysGenericCommand() {
		KeyboardInputAdapter.onKeyboardInput(localizedChecker.getMathField().getInternal(),
				"Integrale( <Funzione>, <x iniziale>, <x finale>, <Booleano Calcola> )");
		assertEquals(Tag.APPLY, getInsertedFunction(localizedChecker).getName());
	}

	@Test
	void testLocalizedKeyboardIntegralCommandCreatesInlineIntegral() {
		KeyboardInputAdapter.onCommandInput(
				localizedChecker.getMathField().getInternal(), "Integrale");
		localizedChecker.checkRaw("SequenceNode[FnINTEGRAL["
				+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[x]]]");
	}

	@Test
	void testEnglishKeyboardIntegralCommandWorksWithLocalizedCommands() {
		KeyboardInputAdapter.onCommandInput(
				localizedChecker.getMathField().getInternal(), "Integral");
		localizedChecker.checkRaw("SequenceNode[FnINTEGRAL["
				+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[x]]]");
	}

	@Test
	void testTypingLocalizedIntegralCommandCreatesInlineIntegral() {
		localizedChecker.type("Integrale(");
		localizedChecker.checkRaw("SequenceNode[FnINTEGRAL["
				+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[x]]]");
	}

	@ParameterizedTest
	@CsvSource(delimiterString = "->", value = {
			"Integrale(x^2)				-> Integral(x^(2))",
			"Integrale(x^2,0,1)			-> Integral(x^(2),0,1)",
			"IntegraleN(x^2,0,1)		-> NIntegral(x^(2),0,1)",
			"IntegraleSimbolico(t^2,t)	-> IntegralSymbolic(t^(2),t)",
			"Integrale(f,0,1,true)		-> Integral(f,0,1,true)",
	})
	void testInsertingLocalizedIntegralCommand(String input, String expected) {
		localizedChecker.getMathField().getInternal().insertString(input);
		localizedChecker.checkAsciiMath(expected);
	}

	@Test
	void testReplacingVariableBySelectingAndTyping() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.type("sin(t)")
				.right(1).checkCaret(VARIABLE_START)
				.shiftOn().right(1).setModifiers(0)
				.type("t")
				.checkAsciiMath("Integral(sin(t),t)");
	}

	@Test
	void testKeyboardNIntegralCommandCreatesInlineIntegral() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "NIntegral");
		checker.checkRaw("SequenceNode[FnN_INTEGRAL["
				+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[x]]]");
	}

	@Test
	void testSelectedTextIsMovedToIntegrand() {
		checker.type("x^2").select(0, 1);
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.checkRaw("SequenceNode[FnINTEGRAL[SequenceNode[], SequenceNode[], SequenceNode[x], "
				+ "SequenceNode[x]], FnSUPERSCRIPT[SequenceNode[2]]]");
	}

	@ParameterizedTest
	@CsvSource(delimiterString = "->", value = {
			"Integral(f)				-> Integral(f)",
			"Integral(f,t)				-> Integral(f,t)",
			"Integral(x^2,0,1)			-> Integral(x^(2),0,1)",
			"Integral(t^2,t,0,1)		-> Integral(t^(2),t,0,1)",
			"NIntegral(f)				-> NIntegral(f)",
			"NIntegral(x^2,0,1)			-> NIntegral(x^(2),0,1)",
			"NIntegral(t^2,t,0,1)		-> NIntegral(t^(2),t,0,1)",
			"IntegralSymbolic(f)		-> IntegralSymbolic(f)",
			"IntegralSymbolic(t^2,t)	-> IntegralSymbolic(t^(2),t)",
			"Integral(f,0,1,true)		-> Integral(f,0,1,true)",
			"Integral(f,a,b,true)		-> Integral(f,a,b,true)",
			"NIntegral(f,0,1,2)			-> NIntegral(f,0,1,2)",
			"IntegralSymbolic(f,t,0)	-> IntegralSymbolic(f,t,0)",
	})
	void testParserPreservesSupportedAndUnsupportedIntegralCommands(
			String input, String expected) throws ParseException {
		assertEquals(expected, new GeoGebraSerializer(null)
				.serialize(new Parser(new TemplateCatalog()).parse(input)));
	}

	@Test
	void testFourArgumentIntegralWithBooleanFlagRemainsGenericCommand()
			throws ParseException {
		FunctionNode command = (FunctionNode) new Parser(new TemplateCatalog())
				.parse("Integral(f,a,b,true)").getRootNode().getChild(0);
		assertEquals(Tag.APPLY, command.getName());
	}

	@Test
	void testTeXSerializerHidesIndefiniteIntegralLimits() throws ParseException {
		assertEquals("\\int{}f\\,\\mathrm{d}x", new TeXSerializer()
				.serialize(new Parser(new TemplateCatalog()).parse("Integral(f)")));
	}

	@Test
	void testTeXSerializerShowsDefiniteIntegralLimits() throws ParseException {
		assertEquals("\\int\\limits_{0}^{1}f\\,\\mathrm{d}x", new TeXSerializer()
				.serialize(new Parser(new TemplateCatalog()).parse("Integral(f,0,1)")));
	}

	@Test
	void testTeXSerializerUsesFunctionVariableForDefiniteIntegral() throws ParseException {
		assertEquals("\\int\\limits_{1}^{2}{{{\\mathrm{cos}}\\left(t\\right)}}\\,\\mathrm{d}t",
				new TeXSerializer().serialize(new Parser(new TemplateCatalog())
						.parse("Integral(cos(t),1,2)")));
	}

	@Test
	void testTeXSerializerNeverShowsSymbolicIntegralLimits() throws ParseException {
		TemplateCatalog catalog = new TemplateCatalog();
		Parser parser = new Parser(catalog);
		Formula formula = new Formula(catalog, new SequenceNode());
		FunctionNode integral = new FunctionNode(catalog.getGeneral(Tag.INTEGRAL_SYMBOLIC));
		integral.setChild(0, parser.parse("0").getRootNode());
		integral.setChild(1, parser.parse("1").getRootNode());
		integral.setChild(2, parser.parse("f").getRootNode());
		integral.setChild(3, parser.parse("x").getRootNode());
		formula.getRootNode().addChild(integral);

		assertEquals("\\int{}f\\,\\mathrm{d}x", new TeXSerializer().serialize(formula));
	}

	@Test
	void testFocusingEmptyLimitRevealsLimitsWithInvisiblePlaceholder()
			throws ParseException {
		TemplateCatalog catalog = new TemplateCatalog();
		Parser parser = new Parser(catalog);
		Formula formula = new Formula(catalog, new SequenceNode());
		FunctionNode integral = new FunctionNode(catalog.getGeneral(Tag.INTEGRAL));
		integral.setChild(0, parser.parse("").getRootNode());
		integral.setChild(1, parser.parse("").getRootNode());
		integral.setChild(2, parser.parse("f").getRootNode());
		integral.setChild(3, parser.parse("x").getRootNode());
		formula.getRootNode().addChild(integral);

		assertEquals("\\int\\limits_"
						+ "{{\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{\\phantom{g}}}\\jlmcursor{0.9}}"
						+ "^{{\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{\\phantom{g}}}}f\\,\\mathrm{d}x",
				new TeXSerializer().serialize(formula, integral.getChild(0), 0));
	}

	@Test
	void testRightArrowNavigatesIntegralPrimaryFields() {
		checker.parse("Integral(f)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(1).checkCaret(UPPER_LIMIT_START)
				.right(1).checkCaret(INTEGRAND_START)
				.right(1).checkCaret(INTEGRAND_END)
				.right(1).checkCaret(VARIABLE_START)
				.right(1).checkCaret(VARIABLE_END)
				.right(1).checkCaret(AFTER_INTEGRAL);
	}

	@Test
	void testLeftArrowNavigatesIntegralPrimaryFieldsInReverse() {
		checker.parse("Integral(f)").checkCaret(AFTER_INTEGRAL)
				.left(1).checkCaret(VARIABLE_END)
				.left(1).checkCaret(VARIABLE_START)
				.left(1).checkCaret(INTEGRAND_END)
				.left(1).checkCaret(INTEGRAND_START)
				.left(1).checkCaret(UPPER_LIMIT_START)
				.left(1).checkCaret(BEFORE_INTEGRAL);
	}

	@Test
	void testLeftArrowFromIntegrandStartMovesAfterUpperLimit() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(3).checkCaret(INTEGRAND_START)
				.left(1).checkCaret(UPPER_LIMIT_END);
	}

	@Test
	void testLowerLimitLeftArrowExitsIntegral() {
		checker.parse("Integral(f)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_START)
				.left(1).checkCaret(BEFORE_INTEGRAL);
	}

	@Test
	void testLowerLimitRightArrowMovesToIntegrand() {
		checker.parse("Integral(f)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_START)
				.right(1).checkCaret(INTEGRAND_START);
	}

	@Test
	void testVerticalNavigationMovesBetweenIntegralLimits() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_START)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_END)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_END);
	}

	@Test
	void testUpArrowFromIntegrandStartMovesToUpperLimit() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(3).checkCaret(INTEGRAND_START)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_END);
	}

	@Test
	void testDownArrowFromIntegrandStartMovesToLowerLimit() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(3).checkCaret(INTEGRAND_START)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_END);
	}

	@Test
	void testDownArrowFromBeforeIntegralMovesBeforeLowerLimit() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_START)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_END);
	}

	@Test
	void testDownArrowFromIntegrandEndDoesNotMoveToLowerLimit() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(4).checkCaret(INTEGRAND_END)
				.typeKey(VK_DOWN).checkCaret(INTEGRAND_END);
	}

	@Test
	void testDownArrowFromVariableEndDoesNotMoveToLowerLimit() {
		checker.parse("Integral(f,t,0,1)").checkCaret(AFTER_INTEGRAL)
				.left(1).checkCaret(VARIABLE_END)
				.typeKey(VK_DOWN).checkCaret(VARIABLE_END);
	}

	@Test
	void testNavigatingToHiddenLimitRevealsLimits() {
		checker.parse("Integral(f)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_START)
				.checkLaTeX("\\int\\limits_{{\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{"
						+ "\\phantom{g}}}}^{{\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{"
						+ "\\phantom{g}}}}f\\,\\mathrm{d}x");
	}

	@Test
	void testSymbolicIntegralNavigationSkipsLimits() {
		checker.parse("IntegralSymbolic(f)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(1).checkCaret(INTEGRAND_START)
				.right(1).checkCaret(INTEGRAND_END)
				.right(1).checkCaret(VARIABLE_START)
				.typeKey(VK_UP).checkCaret(VARIABLE_START)
				.left(1).checkCaret(INTEGRAND_END)
				.left(1).checkCaret(INTEGRAND_START)
				.left(1).checkCaret(BEFORE_INTEGRAL);
	}

	@Test
	void testBackspaceNavigatesTemplateWithDefaultVariable() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.typeKey(VK_BACK_SPACE).checkCaret(UPPER_LIMIT_START);
	}

	@Test
	void testBackspaceNavigatesTemplateWithDefaultVariableAfterCaret() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.right(3).checkCaret(AFTER_INTEGRAL)
				.typeKey(VK_BACK_SPACE).checkCaret(VARIABLE_END);
	}

	@Test
	void testDeleteNavigatesTemplateWithDefaultVariable() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "NIntegral");
		checker.typeKey(VK_DELETE).checkCaret(VARIABLE_START);
	}

	@Test
	void testBackspaceDeletesActuallyEmptyIntegralTemplate() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.typeKey(VK_DELETE).checkCaret(VARIABLE_START)
				.typeKey(VK_DELETE).checkCaret(VARIABLE_START)
				.typeKey(VK_BACK_SPACE).checkCaret(BEFORE_INTEGRAL)
				.checkRaw("SequenceNode[]");
	}

	@Test
	void testBackspaceDeletesActuallyEmptyIntegralTemplateAfterCaret() {
		KeyboardInputAdapter.onCommandInput(checker.getMathField().getInternal(), "Integral");
		checker.typeKey(VK_DELETE).checkCaret(VARIABLE_START)
				.typeKey(VK_DELETE).checkCaret(VARIABLE_START)
				.right(1).checkCaret(AFTER_INTEGRAL)
				.typeKey(VK_BACK_SPACE).checkCaret(BEFORE_INTEGRAL)
				.checkRaw("SequenceNode[]");
	}

	@Test
	void testDeleteRemovesLowerLimitCharacter() {
		checker.parse("Integral(f,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_START)
				.typeKey(VK_DELETE).checkCaret(LOWER_LIMIT_START)
				.checkRaw("SequenceNode[FnINTEGRAL["
						+ "SequenceNode[], SequenceNode[1], SequenceNode[f], SequenceNode[x]]]");
	}

	@Test
	void testDeleteRemovesUpperLimitCharacter() {
		checker.parse("Integral(f,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_START)
				.typeKey(VK_DELETE).checkCaret(UPPER_LIMIT_START)
				.checkRaw("SequenceNode[FnINTEGRAL["
						+ "SequenceNode[0], SequenceNode[], SequenceNode[f], SequenceNode[x]]]");
	}

	@Test
	void testDeleteRemovesIntegrandCharacter() {
		checker.parse("Integral(f,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(3).checkCaret(INTEGRAND_START)
				.typeKey(VK_DELETE).checkCaret(INTEGRAND_START)
				.checkRaw("SequenceNode[FnINTEGRAL["
						+ "SequenceNode[0], SequenceNode[1], SequenceNode[], SequenceNode[x]]]");
	}

	@Test
	void testDeleteRemovesVariableCharacter() {
		checker.parse("Integral(f,t)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(4).checkCaret(VARIABLE_START)
				.typeKey(VK_DELETE).checkCaret(VARIABLE_START)
				.checkRaw("SequenceNode[FnINTEGRAL["
						+ "SequenceNode[], SequenceNode[], SequenceNode[f], SequenceNode[]]]");
	}

	@Test
	void testDeleteAtVariableEndMovesCaretInsideVariable() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(4).checkCaret(INTEGRAND_END)
				.typeKey(VK_DELETE).checkCaret(VARIABLE_START);
	}

	@Test
	void testBackspaceAtVariableEndMovesCaretInsideIntegrand() {
		checker.parse("Integral(f,t)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(4).checkCaret(VARIABLE_START)
				.typeKey(VK_BACK_SPACE).checkCaret(INTEGRAND_END);
	}

	@Test
	void testBackspaceAtIntegrandEndMovesCaretInsideUpperLimit() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.right(3).checkCaret(INTEGRAND_START)
				.typeKey(VK_BACK_SPACE).checkCaret(UPPER_LIMIT_END);
	}

	@Test
	void testBackspaceAtUpperLimitStartMovesCaretBeforeIntegral() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_UP).checkCaret(UPPER_LIMIT_START)
				.typeKey(VK_BACK_SPACE).checkCaret(BEFORE_INTEGRAL);
	}

	@Test
	void testDeleteAtLowerLimitEndMovesCaretToIntegrand() {
		checker.parse("Integral(f,t,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_START)
				.typeKey(VK_DELETE).checkCaret(LOWER_LIMIT_START)
				.typeKey(VK_DELETE).checkCaret(INTEGRAND_START);
	}

	@Test
	void testDeleteBeforeIntegralNavigatesToUpperLimitWhenSupported() {
		checker.parse("Integral(x)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DELETE).checkCaret(UPPER_LIMIT_START);
	}

	@Test
	void testDeleteBeforeIntegralNavigatesToIntegrandWhenLimitsAreUnsupported() {
		checker.parse("IntegralSymbolic(x)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DELETE).checkCaret(INTEGRAND_START);
	}

	@Test
	void testRemovingLimitContentKeepsVisibleEmptyPlaceholders() {
		checker.parse("Integral(f,0,1)")
				.typeKey(VK_HOME).checkCaret(BEFORE_INTEGRAL)
				.typeKey(VK_DOWN).checkCaret(LOWER_LIMIT_START)
				.typeKey(VK_DELETE).checkCaret(LOWER_LIMIT_START)
				.checkLaTeX("\\int\\limits_{{\\bgcolor{#e6e6eb}\\scalebox{1}[1.6]{"
						+ "\\phantom{g}}}}^{1}f\\,\\mathrm{d}x");
	}

	@Test
	void testVisibleLowerLimitCanBeSelectedByClick() {
		checker.parse("Integral(f,0,1)");
		clickAtPath(LOWER_LIMIT_END);
		checker.checkCaret(LOWER_LIMIT_END);
	}

	private void clickAtPath(Integer... path) {
		MathFieldInternal mathField = checker.getMathField().getInternal();
		CursorController.setPath(new ArrayList<>(Arrays.asList(path)), mathField.getEditorState());
		mathField.getMathFieldController().updateCursorPosition(mathField.getFormula(),
				mathField.getEditorState().getCurrentNode(),
				mathField.getEditorState().getCurrentOffset());
		// Click one pixel inside the lower-left cursor anchor to avoid rounded edge coordinates.
		checker.click((int) Math.floor(CursorBox.startX) + 1,
				(int) Math.floor(CursorBox.startY) + 1);
	}

	private static FunctionNode getInsertedFunction(EditorChecker checker) {
		return (FunctionNode) checker.getMathField().getInternal()
				.getFormula().getRootNode().getChild(0);
	}
}
