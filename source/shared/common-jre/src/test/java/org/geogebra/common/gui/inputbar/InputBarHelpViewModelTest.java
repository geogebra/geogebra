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

package org.geogebra.common.gui.inputbar;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.io.EditorTyper;
import org.geogebra.common.io.FactoryProviderCommon;
import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.util.ManualPage;
import org.geogebra.common.util.MatchedString;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

class InputBarHelpViewModelTest extends BaseExamTestSetup {

	private InputBarHelpViewModel viewModel;
	private MathFieldCommon mathField;
	private GuiManager guiManager;
	private final List<InputBarHelpViewModel.Insertion> insertions = new ArrayList<>();

	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@Test
	void categoriesStartWithMathFunctionsAndAllCommandsThenSortedByName() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		List<CommandCategory> categories = rootPage().categories();
		List<String> categoryNames = categories.subList(2, categories.size()).stream()
				.map(category -> getLocalization().getMenu(category.getTranslationKey()))
				.collect(Collectors.toList());
		assertAll(
				() -> assertEquals(CommandCategory.MATH_FUNCTIONS, categories.get(0)),
				() -> assertEquals(CommandCategory.ALL_COMMANDS, categories.get(1)),
				() -> assertFalse(categoryNames.isEmpty()),
				() -> assertEquals(
						categoryNames.stream().sorted().collect(Collectors.toList()), categoryNames));
	}

	@Test
	void emptyCategoriesAreHidden() {
		setup(SuiteSubApp.SCIENTIFIC);
		open("");
		List<CommandCategory> categories = rootPage().categories();
		for (CommandCategory category : categories) {
			assertFalse(commandsOf(category).isEmpty(), category.name());
		}
		assertFalse(categories.contains(CommandCategory.GEOMETRY));
	}

	@ParameterizedTest
	@EnumSource(SuiteSubApp.class)
	void threeDCategoryIsOnlyShownInThe3DApp(SuiteSubApp subApp) {
		setup(subApp);
		open("");
		boolean is3DApp = subApp == SuiteSubApp.G3D;
		assertEquals(is3DApp, rootPage().categories().contains(CommandCategory.THREE_D));
	}

	@ParameterizedTest
	@EnumSource(SuiteSubApp.class)
	void casCategoryIsOnlyShownInTheCasApp(SuiteSubApp subApp) {
		setup(subApp);
		open("");
		boolean isCasApp = subApp == SuiteSubApp.CAS;
		assertEquals(isCasApp, rootPage().categories().contains(CommandCategory.CAS));
	}

	@ParameterizedTest
	@EnumSource(value = SuiteSubApp.class, names = "CAS", mode = EnumSource.Mode.EXCLUDE)
	void casCommandsOutsideTheCasAppAreListedInOtherCategories(SuiteSubApp subApp) {
		setup(subApp);
		open("");
		assertEquals(List.of(), listedOnlyIn(CommandCategory.CAS));
	}

	@ParameterizedTest
	@EnumSource(value = SuiteSubApp.class, names = "G3D", mode = EnumSource.Mode.EXCLUDE)
	void threeDCommandsOutsideThe3DAppAreListedInOtherCategoriesUnlessTheyAre3DOnly(
			SuiteSubApp subApp) {
		setup(subApp);
		open("");
		for (Commands command : listedOnlyIn(CommandCategory.THREE_D)) {
			assertTrue(isOnlyIn3DCategory(command), command.name());
		}
	}

	@Test
	void threeDCommandsIn2DAppAreListedInOtherCategories() {
		setupGraphingApp();
		viewModel = getApp().appScope.inputBarHelpViewModel;
		mathField = new MathFieldCommon(new TemplateCatalog(), null);
		open("");
		assertEquals(List.of(), listedOnlyIn(CommandCategory.THREE_D));
	}

	@Test
	void mathFunctions() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		List<String> functions = commandsOf(CommandCategory.MATH_FUNCTIONS);
		assertAll(
				() -> assertTrue(functions.contains("sqrt")),
				() -> assertFalse(functions.contains("Length")),
				() -> assertTrue(commandsOf(CommandCategory.ALL_COMMANDS).contains("sqrt")),
				() -> assertEquals(List.of("sqrt( <x> )"), syntaxesOf("sqrt").syntaxes()));
	}

	@Test
	void mathFunctionWithSeveralSyntaxesIsListedOnce() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		List<String> functions = commandsOf(CommandCategory.MATH_FUNCTIONS);
		viewModel.setSearchText("log");
		List<String> results = names(rootPage().searchResults());
		assertAll(
				() -> assertEquals(1, functions.stream().filter("log"::equals).count()),
				() -> assertEquals(1, results.stream().filter("log"::equals).count()),
				() -> assertEquals(
						List.of("log( <b> , <x> )", "log( <x> )"), syntaxesOf("log").syntaxes()));
	}

	@Test
	void mathFunctionAndCommandDifferingOnlyInCaseAreBothListed() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		List<String> allCommands = commandsOf(CommandCategory.ALL_COMMANDS);
		assertAll(
				() -> assertTrue(allCommands.contains("gamma")),
				() -> assertTrue(allCommands.contains("Gamma")),
				() -> assertTrue(syntaxesOf("Gamma").syntaxes().stream()
						.allMatch(syntax -> syntax.startsWith("Gamma"))));
	}

	@Test
	void namesAreLocalized() {
		setup(SuiteSubApp.GRAPHING);
		getApp().setLocale(new Locale("de"));
		open("");
		viewModel.openCategory(CommandCategory.VECTOR_AND_MATRIX);
		assertAll(
				() -> assertEquals("Vektor & Matrix", topPage().title()), // NO-TYPO
				() ->
						assertTrue(names(((InputBarHelpPage.Commands) topPage()).commands()).contains("Länge")),
				() -> assertTrue(commandsOf(CommandCategory.GEOMETRY).contains("Senkrechte")));
	}

	@Test
	void navigation() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		viewModel.openCategory(CommandCategory.VECTOR_AND_MATRIX);
		InputBarHelpPage.Syntaxes syntaxes = syntaxesOf("Length");
		assertAll(
				() -> assertEquals(3, navigationStack().size()),
				() -> assertEquals("Length", syntaxes.title()),
				() -> assertEquals(autocompleteProvider.getSyntaxes("Length"), syntaxes.syntaxes()));
		viewModel.navigateBack();
		assertInstanceOf(InputBarHelpPage.Commands.class, topPage());
		assertTrue(viewModel.isOpen());
		viewModel.close();
		assertFalse(viewModel.isOpen());
		assertTrue(navigationStack().isEmpty());
		assertTrue(insertions.isEmpty());
	}

	@Test
	void openingCategoryOrCommandWhileHiddenDoesNothing() {
		setup(SuiteSubApp.GRAPHING);
		viewModel.openCategory(CommandCategory.TEXT);
		viewModel.openCommand("Length");
		assertTrue(navigationStack().isEmpty());
		open("");
		viewModel.close();
		viewModel.openCategory(CommandCategory.TEXT);
		viewModel.openCommand("Length");
		assertTrue(navigationStack().isEmpty());
	}

	@Test
	void searchOnRootCoversAllCommands() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		assertTrue(rootPage().searchResults().isEmpty());
		viewModel.setSearchText("SUM");
		List<String> results = names(rootPage().searchResults());
		assertAll(
				() -> assertTrue(results.contains("Sum")),
				() -> assertTrue(results.contains("LeftSum")),
				() -> assertTrue(results.indexOf("Sum") < results.indexOf("LeftSum")));
	}

	@Test
	void searchMatchesMathFunctionsByName() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		viewModel.setSearchText("sq");
		assertTrue(names(rootPage().searchResults()).contains("sqrt"));
		viewModel.setSearchText("x");
		assertFalse(names(rootPage().searchResults()).contains("sqrt"));
	}

	@Test
	void searchIgnoresAccents() {
		setup(SuiteSubApp.GRAPHING);
		getApp().setLocale(new Locale("de"));
		open("");
		viewModel.setSearchText("lange");
		assertTrue(rootPage().searchResults().contains(new MatchedString("Länge", 0, 5)));
	}

	@Test
	void searchResultsHaveTheMatchedPart() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		viewModel.setSearchText(" sum ");
		List<MatchedString> results = rootPage().searchResults();
		assertAll(
				() -> assertTrue(results.contains(new MatchedString("Sum", 0, 3))),
				() -> assertTrue(results.contains(new MatchedString("LeftSum", 4, 7))));
		viewModel.setSearchText("sq");
		assertTrue(rootPage().searchResults().contains(new MatchedString("sqrt", 0, 2)));
	}

	@Test
	void categoryPageHasTheMatchedPartOnlyWhenSearching() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		viewModel.openCategory(CommandCategory.TEXT);
		InputBarHelpPage.Commands page = (InputBarHelpPage.Commands) topPage();
		assertTrue(page.commands().stream().allMatch(command -> command.from() == command.to()));
		viewModel.setSearchText("ength");
		assertEquals(
				List.of(new MatchedString("Length", 1, 6)),
				((InputBarHelpPage.Commands) topPage()).commands());
	}

	@Test
	void searchOnCategoryPageCoversOnlyCategory() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		viewModel.openCategory(CommandCategory.TEXT);
		viewModel.setSearchText("L");
		InputBarHelpPage.Commands page = (InputBarHelpPage.Commands) topPage();
		assertAll(
				() -> assertTrue(names(page.commands()).contains("Length")),
				() -> assertFalse(names(page.commands()).contains("Line")),
				() -> assertEquals("L", page.searchText()),
				() -> assertEquals("", rootPage().searchText()));
	}

	@Test
	void searchStartsWithWordAroundCursor() {
		setup(SuiteSubApp.GRAPHING);
		EditorTyper typer = new EditorTyper(mathField);
		typer.type("1+Len");
		typer.typeKey(JavaKeyCodes.VK_LEFT);
		viewModel.open(mathField.getInternal().getEditorState());
		assertEquals("Len", rootPage().searchText());
		InputBarHelpViewModel.Insertion insertion = select("Length( <Object> )");
		assertAll(
				() -> assertTrue(insertion.replacesWord()),
				() -> assertEquals(2, insertion.charsBeforeCursor()),
				() -> assertEquals(1, insertion.charsAfterCursor()),
				() -> assertEquals("Length( <Object> )", insertion.syntax()));
		insertion.insertInto(mathField);
		assertEquals("1+Length()", mathField.getInternal().getText());
	}

	@Test
	void mathFunctionIsReplaced() {
		setup(SuiteSubApp.GRAPHING);
		open("log");
		assertEquals("log", rootPage().searchText());
		InputBarHelpViewModel.Insertion insertion = select("log( <b> , <x> )");
		assertAll(
				() -> assertTrue(insertion.replacesWord()),
				() -> assertEquals(3, insertion.charsBeforeCursor()),
				() -> assertEquals(0, insertion.charsAfterCursor()));
		insertion.insertInto(mathField);
		assertEquals("log(,)", mathField.getInternal().getText());
	}

	@ParameterizedTest
	@ValueSource(strings = {"x", "deg", "abc"})
	void reservedWordOrExistingLabelIsNotReplaced(String word) {
		setup(SuiteSubApp.GRAPHING);
		evaluate("abc = 1");
		open(word);
		assertEquals("", rootPage().searchText());
		InputBarHelpViewModel.Insertion insertion = select("Length( <Object> )");
		assertAll(
				() -> assertFalse(insertion.replacesWord()),
				() -> assertEquals(0, insertion.charsBeforeCursor()),
				() -> assertEquals(0, insertion.charsAfterCursor()));
		insertion.insertInto(mathField);
		assertEquals(word + " Length()", mathField.getInternal().getText());
	}

	@Test
	void clearingSearchInsertsAfterSpace() {
		setup(SuiteSubApp.GRAPHING);
		open("Len");
		viewModel.clearSearch();
		assertEquals("", rootPage().searchText());
		assertFalse(select("Length( <Object> )").replacesWord());
	}

	@Test
	void clearingSearchOnEmptyWordInsertsAtCursor() {
		setup(SuiteSubApp.GRAPHING);
		open("1+");
		viewModel.setSearchText("Len");
		viewModel.clearSearch();
		InputBarHelpViewModel.Insertion insertion = select("Length( <Object> )");
		insertion.insertInto(mathField);
		assertEquals("1+Length()", mathField.getInternal().getText());
	}

	@Test
	void selectingSyntaxClosesHelpAndAddsRecentSyntax() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		viewModel.openCommand("Length");
		select("Length( <Object> )");
		assertTrue(navigationStack().isEmpty());
		open("");
		assertEquals(List.of("Length( <Object> )"), rootPage().recentSyntaxes());
	}

	@Test
	void recentSyntaxesKeepFiveNewestFirst() {
		setup(SuiteSubApp.GRAPHING);
		for (int i = 1; i <= 6; i++) {
			select("S" + i);
		}
		open("");
		assertEquals(List.of("S6", "S5", "S4", "S3", "S2"), rootPage().recentSyntaxes());
	}

	@Test
	void selectingRecentSyntaxAgainMovesItToTop() {
		setup(SuiteSubApp.GRAPHING);
		select("S1");
		select("S2");
		select("S1");
		open("");
		assertEquals(List.of("S1", "S2"), rootPage().recentSyntaxes());
	}

	@Test
	void recentSyntaxesAreClearedOnExamStartAndEnd() {
		setup(SuiteSubApp.GRAPHING);
		select("S1");
		examController.prepareExam();
		startExam(ExamType.IB);
		open("");
		assertTrue(rootPage().recentSyntaxes().isEmpty());
		select("S1");
		examController.finishExam();
		open("");
		assertTrue(rootPage().recentSyntaxes().isEmpty());
	}

	@Test
	void recentSyntaxesAreSeparatePerSubApp() {
		setup(SuiteSubApp.GRAPHING);
		InputBarHelpViewModel graphing = viewModel;
		select("Length( <Object> )");
		setup(SuiteSubApp.GEOMETRY);
		open("");
		assertTrue(rootPage().recentSyntaxes().isEmpty());
		viewModel = graphing;
		open("");
		assertEquals(List.of("Length( <Object> )"), rootPage().recentSyntaxes());
	}

	@Test
	void manualUrl() {
		setup(SuiteSubApp.GRAPHING);
		when(guiManager.getHelpURL(ManualPage.COMMAND, "Length")).thenReturn("manual/Length");
		when(guiManager.getHelpURL(ManualPage.OPERATORS, null)).thenReturn("manual/Operators");
		open("");
		assertAll(
				() -> assertEquals("manual/Length", syntaxesOf("Length").manualUrl()),
				() -> assertEquals("manual/Operators", syntaxesOf("sqrt").manualUrl()));
	}

	@Test
	void examRestrictions() {
		setup(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		startExam(ExamType.IB);
		open("");
		assertAll(
				() ->
						assertFalse(commandsOf(CommandCategory.FUNCTIONS_AND_CALCULUS).contains("Derivative")),
				() -> assertTrue(commandsOf(CommandCategory.FUNCTIONS_AND_CALCULUS).contains("Integral")),
				() -> assertNull(syntaxesOf("Length").manualUrl()));
	}

	@Test
	void commandsWithoutSyntaxesAreHidden() {
		setup(SuiteSubApp.GRAPHING);
		examController.prepareExam();
		startExam(ExamType.VLAANDEREN);
		open("");
		for (CommandCategory category : rootPage().categories()) {
			for (String command : commandsOf(category)) {
				assertFalse(syntaxesOf(command).syntaxes().isEmpty(), command);
				viewModel.navigateBack();
			}
		}
		assertFalse(commandsOf(CommandCategory.ALGEBRA).contains("Expand"));
	}

	@Test
	void mathFunctionsFollowExamRestrictions() {
		setup(SuiteSubApp.GRAPHING);
		open("");
		assertTrue(commandsOf(CommandCategory.MATH_FUNCTIONS).contains("fractionalPart"));
		examController.prepareExam();
		startExam(ExamType.IB);
		open("");
		List<String> functions = commandsOf(CommandCategory.MATH_FUNCTIONS);
		assertAll(
				() -> assertFalse(functions.contains("fractionalPart")),
				() -> assertTrue(functions.contains("sqrt")),
				() -> assertFalse(commandsOf(CommandCategory.ALL_COMMANDS).contains("fractionalPart")));
	}

	private void setup(SuiteSubApp subApp) {
		setupApp(subApp);
		guiManager = mock(GuiManager.class);
		when(guiManager.getHelpURL(any(), any())).thenReturn("manual");
		getApp().setGuiManager(guiManager);
		viewModel = getApp().appScope.inputBarHelpViewModel;
		viewModel.syntaxSelected.addListener(insertions::add);
		mathField = new MathFieldCommon(new TemplateCatalog(), null);
	}

	private void open(String input) {
		new EditorTyper(mathField).type(input);
		viewModel.open(mathField.getInternal().getEditorState());
	}

	private List<InputBarHelpPage> navigationStack() {
		return viewModel.getNavigationStack().get();
	}

	private InputBarHelpPage topPage() {
		return navigationStack().get(navigationStack().size() - 1);
	}

	private InputBarHelpPage.CommandCategories rootPage() {
		return assertInstanceOf(
				InputBarHelpPage.CommandCategories.class, navigationStack().get(0));
	}

	private List<String> commandsOf(CommandCategory category) {
		viewModel.openCategory(category);
		List<String> commands = names(((InputBarHelpPage.Commands) topPage()).commands());
		viewModel.navigateBack();
		return commands;
	}

	private static List<String> names(List<MatchedString> commands) {
		return commands.stream().map(MatchedString::content).collect(Collectors.toList());
	}

	/**
	 * @param hiddenCategory a category that is not shown in the current app
	 * @return the commands of the category that the app allows, but no shown category lists
	 */
	private List<Commands> listedOnlyIn(CommandCategory hiddenCategory) {
		Set<String> inShownCategories = new HashSet<>();
		for (CommandCategory category : rootPage().categories()) {
			if (category != CommandCategory.ALL_COMMANDS) {
				inShownCategories.addAll(commandsOf(category));
			}
		}
		List<String> allCommands = commandsOf(CommandCategory.ALL_COMMANDS);
		List<Commands> result = new ArrayList<>();
		for (Commands command : Commands.values()) {
			String name =
					getLocalization().getCommand(Commands.englishToInternal(command).name());
			if (hiddenCategory.contains(command)
					&& allCommands.contains(name)
					&& !inShownCategories.contains(name)) {
				result.add(command);
			}
		}
		return result;
	}

	private static boolean isOnlyIn3DCategory(Commands command) {
		for (CommandCategory category : CommandCategory.values()) {
			if (category != CommandCategory.THREE_D && category.contains(command)) {
				return false;
			}
		}
		return true;
	}

	private InputBarHelpPage.Syntaxes syntaxesOf(String command) {
		viewModel.openCommand(command);
		return assertInstanceOf(InputBarHelpPage.Syntaxes.class, topPage());
	}

	private InputBarHelpViewModel.Insertion select(String syntax) {
		viewModel.selectSyntax(syntax);
		return insertions.get(insertions.size() - 1);
	}
}
