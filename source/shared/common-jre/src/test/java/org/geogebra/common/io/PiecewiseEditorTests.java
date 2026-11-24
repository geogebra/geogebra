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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.contextmenu.MathTemplateContextMenuItem;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.keyboard.LocalizedTemplateKeyProvider;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.controller.CursorController;
import org.geogebra.editor.share.controller.PiecewiseController;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.SequenceNode;
import org.geogebra.editor.share.util.PiecewiseControl;
import org.geogebra.editor.share.util.PiecewiseHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

class PiecewiseEditorTests {
	private static final String TEMPLATE_KEY = Tag.PIECEWISE.getKey();
	private static final String EMPTY_TEMPLATE = "SequenceNode[FnPIECEWISE["
			+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[]]]";

	// caret paths for a piecewise function that is the first element of the input
	private static final Integer[] BEFORE = {0};
	private static final Integer[] AFTER = {1};

	private AppCommon app;
	private EditorChecker checker;

	@BeforeAll
	static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	@BeforeEach
	void setUp() {
		app = AppCommonFactory.create();
		checker = new EditorChecker(app);
	}

	@AfterEach
	void tearDown() {
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

	private static Integer[] expression(int caseIndex, int offset) {
		return new Integer[] {offset, 2 * caseIndex, 0};
	}

	private static Integer[] condition(int caseIndex, int offset) {
		return new Integer[] {offset, 2 * caseIndex + 1, 0};
	}

	private EditorChecker insertTemplate() {
		return checker.pressSingleKey(TEMPLATE_KEY);
	}

	private MathFieldInternal getMathFieldInternal() {
		return checker.getMathField().getInternal();
	}

	private PiecewiseController getController() {
		return getMathFieldInternal().getPiecewiseController();
	}

	/** @return the first piecewise function at the top level of the input */
	private FunctionNode getPiecewise() {
		for (org.geogebra.editor.share.tree.Node node :
				getMathFieldInternal().getFormula().getRootNode()) {
			if (PiecewiseHelper.isPiecewise(node)) {
				return (FunctionNode) node;
			}
		}
		throw new AssertionError("No piecewise function in the input");
	}

	/** @return the button of the given kind that the editor currently wants shown, or null */
	private PiecewiseController.Button findButton(PiecewiseControl.Type type, int caseIndex) {
		for (PiecewiseController.Button button : getController().getState().buttons()) {
			if (button.control().type() == type
					&& (type == PiecewiseControl.Type.ADD_CASE
							|| button.control().caseIndex() == caseIndex)) {
				return button;
			}
		}
		return null;
	}

	private PiecewiseController.Button addCaseButton() {
		return findButton(PiecewiseControl.Type.ADD_CASE, -1);
	}

	private PiecewiseController.Button removeCaseButton(int caseIndex) {
		return findButton(PiecewiseControl.Type.REMOVE_CASE, caseIndex);
	}

	private void press(PiecewiseController.Button button) {
		assertNotNull(button, "button is not shown");
		getController().performAction(button.control());
	}

	/** @return a point inside the row of a case, at the start of its expression */
	private int[] pointInCase(int caseIndex) {
		double[] caret = getHitTestCaret(PiecewiseHelper.getExpression(getPiecewise(), caseIndex), 0);
		return new int[] {(int) caret[0] + 1, (int) caret[1] + 3};
	}

	private void hoverCase(int caseIndex) {
		int[] point = pointInCase(caseIndex);
		getMathFieldInternal().onHover(point[0], point[1]);
	}

	private List<Integer> shownRemoveButtons() {
		List<Integer> cases = new ArrayList<>();
		for (PiecewiseController.Button button : getController().getState().buttons()) {
			if (button.control().type() == PiecewiseControl.Type.REMOVE_CASE) {
				cases.add(button.control().caseIndex());
			}
		}
		return cases;
	}

	// Creation

	@Test
	void templateShouldHaveTwoEmptyCasesWithCaretInFirstExpression() {
		insertTemplate().checkCaret(expression(0, 0)).checkRaw(EMPTY_TEMPLATE);
	}

	@Test
	void templateShouldKeepSurroundingInput() {
		checker.type("f(x)=");
		insertTemplate().type("x");
		checker.checkAsciiMath("f(x)=$piecewise(x,,)");
	}

	@Test
	void templateShouldUseSelectionAsFirstExpression() {
		checker.type("x+1").select(0, 3);
		insertTemplate().checkAsciiMath("$piecewise(x+1,,)");
	}

	// Serialization

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"$piecewise(x)				-> $piecewise(x)",
				"$piecewise(x,x<1)			-> $piecewise(x,x<1)",
				"$piecewise(x,x<1,4)		-> $piecewise(x,x<1,4)",
				"$piecewise(x,x<1,4,x>2)	-> $piecewise(x,x<1,4,x>2)",
				"$piecewise(x,x<1,4,)		-> $piecewise(x,x<1,4)",
				"$piecewise()				-> $piecewise()",
				"$piecewise(,,)				-> $piecewise(,,)",
				"$piecewise(x,,4)			-> $piecewise(x,,4)",
				"$piecewise(x,x<1,)			-> $piecewise(x,x<1,)",
				"f(x)=$piecewise(Min(x,2),x<1,x^2)	-> f(x)=$piecewise(Min(x,2),x<1,x^(2))",
			})
	void editorSyntaxShouldRoundTrip(String input, String expected) {
		checker.parse(input).checkAsciiMath(expected);
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"$piecewise(x)				-> 1",
				"$piecewise(x,x<1)			-> 1",
				"$piecewise(x,x<1,4)		-> 2",
				"$piecewise(x,x<1,4,x>2)	-> 2",
				"$piecewise(1,x<1,2,x<2,3)	-> 3",
			})
	void parserShouldCreateOneCasePerExpression(String input, int caseCount) {
		checker.parse(input);
		assertEquals(caseCount, PiecewiseHelper.getCaseCount(getPiecewise()));
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"$piecewise(x)				-> (x)",
				"$piecewise(x,x<1)			-> If(x<1,x)",
				"$piecewise(x,x<1,4)		-> If(x<1,x,4)",
				"$piecewise(x,x<1,4,x>2)	-> If(x<1,x,x>2,4)",
			})
	void copyShouldUsePlainSyntax(String input, String expected) {
		checker.parse(input).checkCopy(expected);
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				"$piecewise(x,x<1,4)		-> If(x<1,x,4)",
				"f(x)=$piecewise(x,x<1,4)	-> f(x)=If(x<1,x,4)",
			})
	void plainTextShouldUsePlainSyntax(String input, String expected) {
		checker.parse(input);
		assertEquals(expected, getMathFieldInternal().getPlainText());
	}

	// Horizontal navigation

	@Test
	void rightShouldVisitExpressionThenConditionThenLeave() {
		checker.parse("$piecewise(x,x<1,4)");
		checker.typeKey(VK_HOME);
		checker
				.checkCaret(BEFORE)
				.right(1)
				.checkCaret(expression(0, 0))
				.right(1)
				.checkCaret(expression(0, 1))
				.right(1)
				.checkCaret(condition(0, 0))
				.right(3)
				.checkCaret(condition(0, 3))
				.right(1)
				.checkCaret(AFTER);
	}

	@Test
	void leftShouldVisitConditionThenExpressionThenLeave() {
		checker.parse("$piecewise(x,x<1,4,x>2)");
		checker
				.checkCaret(AFTER)
				.left(1)
				.checkCaret(condition(1, 3))
				.left(3)
				.checkCaret(condition(1, 0))
				.left(1)
				.checkCaret(expression(1, 1))
				.left(1)
				.checkCaret(expression(1, 0))
				.left(1)
				.checkCaret(BEFORE);
	}

	@Test
	void reversingAnExitShouldReenterTheSameCase() {
		checker.parse("$piecewise(x,x<1,4,x>2,5)");
		checker
				.left(1)
				.checkCaret(condition(2, 0))
				.typeKey(VK_UP)
				.checkCaret(condition(1, 0))
				.right(4)
				.checkCaret(AFTER)
				.left(1)
				.checkCaret(condition(1, 3))
				.left(6)
				.checkCaret(BEFORE)
				.right(1)
				.checkCaret(expression(1, 0));
	}

	@Test
	void enteringFromTheOtherSideShouldNotUseTheCaseJustLeft() {
		checker.parse("$piecewise(x,x<1,4,x>2,5)");
		checker
				.left(1)
				.typeKey(VK_UP)
				.checkCaret(condition(1, 0))
				.right(4)
				.checkCaret(AFTER)
				.typeKey(VK_HOME)
				.checkCaret(BEFORE)
				.right(1)
				.checkCaret(expression(0, 0));
	}

	// Vertical navigation

	@Test
	void upAndDownShouldKeepZoneAndOffset() {
		checker.parse("$piecewise(x+1,x<1,22,x>2,5)");
		checker
				.left(2)
				.checkCaret(expression(2, 1))
				.typeKey(VK_UP)
				.checkCaret(expression(1, 1))
				.typeKey(VK_UP)
				.checkCaret(expression(0, 1))
				.right(2)
				.checkCaret(expression(0, 3))
				.typeKey(VK_DOWN)
				.checkCaret(expression(1, 2))
				.typeKey(VK_DOWN)
				.checkCaret(expression(2, 1))
				.right(1)
				.checkCaret(condition(2, 0))
				.typeKey(VK_UP)
				.checkCaret(condition(1, 0));
	}

	// Deletion

	@Test
	void backspaceShouldDeleteCharactersThenActLikeLeft() {
		checker.parse("$piecewise(xy,x<1,4)");
		checker
				.left(1)
				.typeKey(VK_UP)
				.left(1)
				.checkCaret(expression(0, 2))
				.typeKey(VK_BACK_SPACE)
				.checkCaret(expression(0, 1))
				.typeKey(VK_BACK_SPACE)
				.checkCaret(expression(0, 0))
				.typeKey(VK_BACK_SPACE)
				.checkCaret(BEFORE)
				.checkAsciiMath("$piecewise(,x<1,4)");
	}

	@Test
	void backspaceAtConditionStartShouldMoveToExpression() {
		checker.parse("$piecewise(x,x<1,4)");
		checker
				.left(1)
				.typeKey(VK_UP)
				.checkCaret(condition(0, 0))
				.typeKey(VK_BACK_SPACE)
				.checkCaret(expression(0, 1))
				.checkAsciiMath("$piecewise(x,x<1,4)");
	}

	@Test
	void deleteShouldDeleteCharactersThenActLikeRight() {
		checker.parse("$piecewise(x,y,4)");
		checker.typeKey(VK_HOME);
		checker
				.right(1)
				.typeKey(VK_DELETE)
				.checkCaret(expression(0, 0))
				.typeKey(VK_DELETE)
				.checkCaret(condition(0, 0))
				.typeKey(VK_DELETE)
				.checkCaret(condition(0, 0))
				.typeKey(VK_DELETE)
				.checkCaret(AFTER)
				.checkAsciiMath("$piecewise(,,4)");
	}

	@Test
	void backspaceInsideEmptyStructureShouldRemoveIt() {
		checker.type("2+");
		insertTemplate().type("x");
		checker
				.typeKey(VK_BACK_SPACE)
				.checkRaw("SequenceNode[2, +, FnPIECEWISE["
						+ "SequenceNode[], SequenceNode[], SequenceNode[], SequenceNode[]]]");
		checker.typeKey(VK_BACK_SPACE).checkCaret(2).checkAsciiMath("2+");
	}

	@Test
	void deleteInsideEmptyStructureShouldRemoveIt() {
		insertTemplate().right(1).typeKey(VK_DELETE).checkCaret(0).checkAsciiMath("");
	}

	@Test
	void backspaceAfterEmptyStructureShouldRemoveIt() {
		checker.type("2+");
		insertTemplate().right(2);
		checker
				.type("+3")
				.left(2)
				.checkCaret(3)
				.typeKey(VK_BACK_SPACE)
				.checkCaret(2)
				.checkAsciiMath("2++3");
	}

	@Test
	void deleteBeforeEmptyStructureShouldRemoveIt() {
		insertTemplate().left(1).checkCaret(BEFORE).typeKey(VK_DELETE).checkCaret(0).checkAsciiMath("");
	}

	@Test
	void backspaceAfterPopulatedStructureShouldEnterIt() {
		checker.parse("$piecewise(x,x<1,4)");
		checker
				.typeKey(VK_BACK_SPACE)
				.checkCaret(condition(1, 0))
				.checkAsciiMath("$piecewise(x,x<1,4)");
	}

	@Test
	void deleteBeforePopulatedStructureShouldEnterIt() {
		checker.parse("$piecewise(x,x<1,4)");
		checker.typeKey(VK_HOME);
		checker.typeKey(VK_DELETE).checkCaret(expression(0, 0)).checkAsciiMath("$piecewise(x,x<1,4)");
	}

	// Typing

	@Test
	void closingBracketShouldNotLeaveStructure() {
		insertTemplate().type("x+1)").checkAsciiMath("$piecewise((x+1),,)");
	}

	// Add case

	@Test
	void addCaseButtonShouldOnlyShowWhileCursorIsInStructure() {
		checker.parse("f(x)=$piecewise(x,x<1,4)+1");
		checker.checkCaret(5);
		assertNull(addCaseButton(), "after the input");
		checker.left(2).checkCaret(3);
		assertNull(addCaseButton(), "right after the structure");
		checker.left(1).checkCaret(0, 3, 2);
		assertNotNull(addCaseButton(), "in a condition");
		checker.left(2).checkCaret(0, 2, 2);
		assertNotNull(addCaseButton(), "in an expression");
		checker.left(1).checkCaret(2);
		assertNull(addCaseButton(), "right before the structure");
	}

	@Test
	void hoveringARowShouldNotShowAddCaseButton() {
		checker.parse("$piecewise(x,x<1,4)");
		checker.checkCaret(AFTER);
		hoverCase(0);
		assertEquals(List.of(0), shownRemoveButtons());
		assertNull(addCaseButton());
	}

	@Test
	void addCaseButtonShouldBeBelowTheCases() {
		checker.parse("f(x)=$piecewise(x,x<1,4)");
		checker.left(1);
		PiecewiseController.Bounds add = addCaseButton().bounds();
		PiecewiseController.Bounds lastRow = removeCaseButton(1).bounds();
		assertTrue(add.top() > lastRow.top() + lastRow.height(), "below the last case");
		assertTrue(add.left() < lastRow.left(), "starts at the brace");
		// 24 pixels at the default font size of 16 pixels
		assertEquals(24, add.height(), 0.01);
	}

	@Test
	void addCaseShouldAppendEmptyCaseAndFocusItsExpression() {
		checker.parse("$piecewise(x,x<1,4)");
		checker.left(1).typeKey(VK_UP).checkCaret(condition(0, 0));
		press(addCaseButton());
		checker.checkCaret(expression(2, 0));
		checker.checkAsciiMath("$piecewise(x,x<1,4,,)");
	}

	@Test
	void addCaseShouldWorkRepeatedlyWithIncompleteCases() {
		insertTemplate();
		press(addCaseButton());
		press(addCaseButton());
		checker.checkCaret(expression(3, 0));
		assertEquals(4, PiecewiseHelper.getCaseCount(getPiecewise()));
	}

	@Test
	void otherwiseShouldMoveToTheNewFinalCase() {
		checker.parse("$piecewise(x,x<1,4)");
		assertTrue(PiecewiseHelper.isOtherwise(PiecewiseHelper.getCondition(getPiecewise(), 1)));
		checker.left(1);
		press(addCaseButton());
		assertFalse(PiecewiseHelper.isOtherwise(PiecewiseHelper.getCondition(getPiecewise(), 1)));
		assertTrue(PiecewiseHelper.isOtherwise(PiecewiseHelper.getCondition(getPiecewise(), 2)));
	}

	@Test
	void singleCaseShouldNeverBeOtherwise() {
		checker.parse("$piecewise(x)");
		assertFalse(PiecewiseHelper.isOtherwise(PiecewiseHelper.getCondition(getPiecewise(), 0)));
	}

	@Test
	void labelsShouldUseTheLocalizationOfTheEditor() {
		List<String> keys = new ArrayList<>();
		getMathFieldInternal().setLocalization((key, fallback) -> {
			keys.add(key);
			return fallback;
		});
		checker.parse("$piecewise(x,x<1,4)").left(1);
		assertTrue(keys.contains("otherwise"));
		assertTrue(keys.contains(PiecewiseControl.ADD_CASE_KEY));
	}

	// Remove case

	@Test
	void removeButtonShouldShowForCaseWithCursor() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		assertEquals(List.of(), shownRemoveButtons());
		checker.left(1).checkCaret(condition(2, 0));
		assertEquals(List.of(2), shownRemoveButtons());
		checker.left(1).checkCaret(expression(2, 1));
		assertEquals(List.of(2), shownRemoveButtons());
		checker.typeKey(VK_UP);
		assertEquals(List.of(1), shownRemoveButtons());
	}

	@Test
	void removeButtonShouldAlsoShowForHoveredCase() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		checker.left(1).checkCaret(condition(2, 0));
		hoverCase(0);
		assertEquals(List.of(0, 2), shownRemoveButtons());
		hoverCase(1);
		assertEquals(List.of(1, 2), shownRemoveButtons());
		hoverCase(2);
		assertEquals(List.of(2), shownRemoveButtons());
		getMathFieldInternal().onHoverEnd();
		assertEquals(List.of(2), shownRemoveButtons());
	}

	@Test
	void hoveringTheSpaceLeadingToTheButtonShouldShowIt() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		checker.left(1).typeKey(VK_UP).typeKey(VK_UP).checkCaret(condition(0, 0));
		PiecewiseController.Bounds button = removeCaseButton(0).bounds();
		double conditionEnd = getHitTestCaret(PiecewiseHelper.getCondition(getPiecewise(), 0), 3)[0];
		checker.typeKey(VK_DOWN).typeKey(VK_DOWN);
		assertEquals(List.of(2), shownRemoveButtons());
		int y = (int) (button.top() + button.height() / 2);
		for (int x = (int) conditionEnd; x <= button.left() + button.width(); x++) {
			getMathFieldInternal().onHover(x, y);
			assertEquals(List.of(0, 2), shownRemoveButtons(), "x = " + x);
		}
	}

	@Test
	void movingThePointerBetweenCasesShouldAlwaysHoverOne() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		checker.left(1).checkCaret(condition(2, 0));
		int[] first = pointInCase(0);
		int[] second = pointInCase(1);
		assertTrue(second[1] - first[1] > 20, "cases are below each other");
		for (int y = first[1]; y <= second[1]; y++) {
			getMathFieldInternal().onHover(first[0], y);
			assertEquals(2, shownRemoveButtons().size(), "y = " + y);
		}
	}

	@Test
	void removeButtonsShouldShareAColumn() {
		checker.parse("$piecewise(1,x<1,22,x-2<1678,3)");
		checker.left(1);
		hoverCase(0);
		PiecewiseController.Bounds first = removeCaseButton(0).bounds();
		PiecewiseController.Bounds last = removeCaseButton(2).bounds();
		assertEquals(first.left(), last.left(), 0.01);
		// 24 pixels wide at the default font size of 16 pixels
		assertEquals(24, first.width(), 0.01);
		assertTrue(last.top() > first.top() + first.height());
	}

	@Test
	void removeCaseShouldFocusNextSurvivingExpression() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		checker.left(1).typeKey(VK_UP).typeKey(VK_UP).checkCaret(condition(0, 0));
		press(removeCaseButton(0));
		checker.checkCaret(expression(0, 2));
		checker.checkAsciiMath("$piecewise(22,x<2,3)");
	}

	@Test
	void removingLastCaseShouldFocusPreviousExpression() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		checker.left(1).checkCaret(condition(2, 0));
		press(removeCaseButton(2));
		checker.checkCaret(expression(1, 2));
		checker.checkAsciiMath("$piecewise(1,x<1,22,x<2)");
	}

	@Test
	void hoveredCaseShouldBeRemovableWithoutCursor() {
		checker.parse("$piecewise(1,x<1,22,x<2,3)");
		checker.left(1).checkCaret(condition(2, 0));
		hoverCase(0);
		press(removeCaseButton(0));
		checker.checkAsciiMath("$piecewise(22,x<2,3)");
	}

	@Test
	void buttonOfCaseThatMovedUnderPointerShouldShowAfterRemoval() {
		checker.parse("$piecewise(1,x<1,22,x<2,3,x<3,4)");
		checker.left(1).checkCaret(condition(3, 0));
		hoverCase(1);
		press(removeCaseButton(1));
		// the cursor is in the case that took the place of the removed one
		checker.checkCaret(expression(1, 1));
		assertEquals(List.of(1), shownRemoveButtons());
		checker.typeKey(VK_DOWN);
		assertEquals(List.of(1, 2), shownRemoveButtons());
	}

	@Test
	void lastRemainingCaseShouldNotBeRemovable() {
		checker.parse("$piecewise(x,x<1)");
		checker.left(1).checkCaret(condition(0, 3));
		assertFalse(removeCaseButton(0).enabled());
		press(removeCaseButton(0));
		assertEquals(1, PiecewiseHelper.getCaseCount(getPiecewise()));
		assertFalse(getController().removeCase(getPiecewise(), 0));
	}

	@Test
	void removeButtonsShouldBeEnabledWithSeveralCases() {
		checker.parse("$piecewise(x,x<1,4)");
		checker.left(1);
		assertTrue(removeCaseButton(1).enabled());
		assertTrue(addCaseButton().enabled());
	}

	// State

	@Test
	void listenersShouldBeNotifiedWhenButtonsChange() {
		List<PiecewiseController.State> states = new ArrayList<>();
		PiecewiseController.StateListener listener = states::add;
		getController().addListener(listener);
		checker.parse("$piecewise(x,x<1,4,x>2)");
		assertEquals(0, states.size(), "no buttons without cursor in the structure");
		checker.left(1);
		assertEquals(1, states.size());
		assertEquals(2, states.get(0).buttons().size());
		// moving inside the same case changes nothing
		checker.left(2).checkCaret(condition(1, 1));
		assertEquals(1, states.size());
		hoverCase(0);
		assertEquals(2, states.size());
		assertEquals(3, states.get(1).buttons().size());
		checker.right(3).checkCaret(AFTER);
		getMathFieldInternal().onHoverEnd();
		assertEquals(0, states.get(states.size() - 1).buttons().size());

		getController().removeListener(listener);
		int notifications = states.size();
		checker.left(1);
		assertEquals(notifications, states.size());
	}

	@Test
	void readOnlyEditorShouldHaveNoButtons() {
		checker.parse("$piecewise(x,x<1,4)");
		getMathFieldInternal().setReadOnly(true);
		hoverCase(0);
		assertEquals(0, getController().getState().buttons().size());
	}

	// Pointer

	@ParameterizedTest
	@CsvSource({"0, 0", "0, 1", "1, 0", "1, 3", "2, 1", "3, 0", "3, 6", "4, 1", "5, 0"})
	void clickShouldPlaceCaretWhereItIsDrawn(int zone, int offset) {
		checker.parse("f(x)=$piecewise(x,x<1,22,x-2<16,x^2)");
		MathFieldInternal mathFieldInternal = getMathFieldInternal();
		SequenceNode root = mathFieldInternal.getFormula().getRootNode();
		SequenceNode field = getPiecewise().getChild(zone);
		// Hit testing lays the formula out again from its TeX form, which has to agree with
		// what is drawn. Measure from the start of the structure: what comes before it is not
		// laid out by the code under test.
		double[] drawnOrigin = getDrawnCaret(root, 2);
		double[] drawn = getDrawnCaret(field, offset);
		double[] hitTestOrigin = getHitTestCaret(root, 2);
		double[] hitTest = getHitTestCaret(field, offset);
		assertEquals(
				drawn[0] - drawnOrigin[0],
				hitTest[0] - hitTestOrigin[0],
				1.5,
				"x of zone " + zone + " offset " + offset);
		assertEquals(
				drawn[1] - drawnOrigin[1],
				hitTest[1] - hitTestOrigin[1],
				1.5,
				"y of zone " + zone + " offset " + offset);

		checker.typeKey(VK_HOME);
		checker.click((int) Math.round(hitTest[0]), (int) Math.round(hitTest[1]));
		checker.checkCaret(offset, zone, 2);
	}

	@ParameterizedTest
	@CsvSource({
		"'f(x)=$piecewise(x,x<1,22,x-2<16,x^2)+1'",
		"'f(x)=$piecewise(x,x<1,22,x-2<16,x^2,x>5)+1'",
		"'f(x)=$piecewise(x,x<1,x^2)+1'",
	})
	void caretAfterStructureShouldBeHitWhereItIsDrawn(String input) {
		checker.parse(input);
		SequenceNode root = getMathFieldInternal().getFormula().getRootNode();
		double drawnWidth = getDrawnCaret(root, 3)[0] - getDrawnCaret(root, 2)[0];
		double hitTestWidth = getHitTestCaret(root, 3)[0] - getHitTestCaret(root, 2)[0];
		assertEquals(drawnWidth, hitTestWidth, 1.5);
	}

	@Test
	void clickOnOtherwiseShouldFocusFinalCondition() {
		checker.parse("f(x)=$piecewise(x,x<1,x^2)");
		checker.left(1).checkCaret(0, 3, 2);
		PiecewiseController.Bounds button = removeCaseButton(1).bounds();
		checker.typeKey(VK_HOME);
		// the right end of the label is closer to the position after the structure
		checker.click((int) button.left() - 2, (int) (button.top() + button.height() / 2));
		checker.checkCaret(0, 3, 2);
	}

	@Test
	void clickInRowShouldStayInThatRow() {
		checker.parse("f(x)=$piecewise(1,x<1,2,x<2,3)");
		checker.left(1);
		hoverCase(0);
		PiecewiseController.Bounds firstButton = removeCaseButton(0).bounds();
		int[] lastCase = pointInCase(2);
		getMathFieldInternal().onHoverEnd();
		checker.typeKey(VK_HOME);
		checker.click(
				(int) (firstButton.left() + firstButton.width()) - 2, (int) firstButton.top() + 2);
		checker.checkCaret(3, 1, 2);
		checker.click(lastCase[0] - 3, lastCase[1] + 8);
		checker.checkCaret(0, 4, 2);
	}

	// Evaluation

	private GeoElement evaluate(String input) {
		GeoElementND[] result =
				app.getKernel().getAlgebraProcessor().processAlgebraCommand(input, false);
		return result == null ? null : result[0].toGeoElement();
	}

	/** Start editing an object the way the algebra view does. */
	private void edit(GeoElement element) {
		checker.convertFormulaForAV(element.getDefinitionForEditor());
		CursorController.lastField(getMathFieldInternal().getEditorState());
	}

	private GeoElement submit() {
		return evaluate(getMathFieldInternal().getText());
	}

	@Test
	void menuItemShouldInsertTemplate() {
		checker.pressSingleKey(new LocalizedTemplateKeyProvider(app)
				.getEditorInput(MathTemplateContextMenuItem.PiecewiseFunction));
		checker.checkCaret(expression(0, 0)).checkRaw(EMPTY_TEMPLATE);
	}

	@Test
	void newFunctionShouldBeCreatedFromTemplate() {
		insertTemplate().type("x^2").right(2).type("x<1").typeKey(VK_DOWN).left(1).type("4");
		checker.checkCaret(expression(1, 1));
		GeoElement function = submit();
		assertEquals("f(x) = If(x < 1, x², 4)", function.getDefinitionForInputBar());
	}

	@Test
	void existingFunctionShouldOpenAsPiecewise() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		edit(evaluate("f(x)=If(x<0, 2x+5, If(x<3, 3, -x))"));
		assertEquals(3, PiecewiseHelper.getCaseCount(getPiecewise()));
		assertTrue(PiecewiseHelper.isOtherwise(PiecewiseHelper.getCondition(getPiecewise(), 2)));
		assertEquals("f(x) = If(x < 0, 2x + 5, x < 3, 3, -x)", submit().getDefinitionForInputBar());
	}

	@Test
	void existingSingleConditionFunctionShouldOpenWithOneCase() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		edit(evaluate("f(x)=x, x<1"));
		assertEquals(1, PiecewiseHelper.getCaseCount(getPiecewise()));
		checker.left(1).checkCaret(3, 1, 2);
		assertEquals("f(x) = If(x < 1, x)", submit().getDefinitionForInputBar());
	}

	@Test
	void existingFunctionShouldBeEditable() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		edit(evaluate("f(x)=If(x<1, x, 4)"));
		checker.left(1).type("x<3");
		press(addCaseButton());
		checker.type("7");
		GeoFunction function = (GeoFunction) submit();
		assertEquals("f(x) = If(x < 1, x, x < 3, 4, 7)", function.getDefinitionForInputBar());
		assertEquals(7, function.value(5), 1E-10);

		edit(function);
		checker.left(1).typeKey(VK_UP);
		press(removeCaseButton(1));
		assertEquals("f(x) = If(x < 1, x, 7)", submit().getDefinitionForInputBar());
	}

	@Test
	void removingConditionOfSingleCaseShouldGiveUnrestrictedFunction() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		edit(evaluate("f(x)=x, x<1"));
		checker.left(1).repeatKey(VK_BACK_SPACE, 3);
		assertEquals("f(x) = x", submit().getDefinitionForInputBar());
	}

	@ParameterizedTest
	@CsvSource(
			delimiterString = "->",
			value = {
				// empty template
				"f(x)=$piecewise(,,)",
				// missing expression
				"f(x)=$piecewise(x,x<1,)",
				"f(x)=$piecewise(,x<1,4)",
				// missing condition that is not the final one
				"f(x)=$piecewise(x,,4)",
				"f(x)=$piecewise(x,x<1,4,,5)",
			})
	void incompleteDefinitionShouldNotBeAccepted(String input) {
		checker.convertFormulaForAV(input);
		assertNull(submit());
		assertNull(app.getKernel().lookupLabel("f"));
		// the input stays as it is for correction
		assertEquals(input, getMathFieldInternal().getText());
	}

	@Test
	void rejectedInputShouldBeRestoredFromItsText() {
		insertTemplate().type("x").right(1).typeKey(VK_DOWN);
		String text = getMathFieldInternal().getText();
		assertEquals("$piecewise(x,,)", text);
		getMathFieldInternal().parse(text);
		checker.checkRaw("SequenceNode[FnPIECEWISE["
				+ "SequenceNode[x], SequenceNode[], SequenceNode[], SequenceNode[]]]");
	}

	// Screen reader

	@Test
	void screenReaderShouldReadCasesButNoButtons() {
		checker.parse("$piecewise(x,x<1,4)");
		checker.left(1).checkCaret(condition(1, 0));
		assertNotNull(addCaseButton());
		checker.typeKey(VK_HOME);
		String description = getMathFieldInternal()
				.getEditorStateDescription(ScreenReader.getExpressionReader(app))
				.replaceAll(" +", " ");
		assertEquals(
				"start of formula open brace table with 2 rows and 3 columns"
						+ " The 1st row is x : x<1 The 2nd row is 4 : otherwise end of table",
				description);
	}

	private double[] getDrawnCaret(SequenceNode field, int offset) {
		MathFieldInternal mathFieldInternal = getMathFieldInternal();
		mathFieldInternal.getEditorState().setCurrentNode(field);
		mathFieldInternal.getEditorState().setCurrentOffset(offset);
		mathFieldInternal.update();
		return new double[] {
			checker.getMathField().getTeXIcon().getCursorX(),
			checker.getMathField().getTeXIcon().getCursorY()
		};
	}

	private double[] getHitTestCaret(SequenceNode field, int offset) {
		MathFieldInternal mathFieldInternal = getMathFieldInternal();
		mathFieldInternal
				.getMathFieldController()
				.updateCursorPosition(mathFieldInternal.getFormula(), field, offset);
		return new double[] {CursorBox.startX, CursorBox.startY};
	}
}
