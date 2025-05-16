package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.contextmenu.TableValuesContextMenuItem;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.gui.view.algebra.SuggestionIntersectExtremum;
import org.geogebra.common.gui.view.table.InvalidValuesException;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.Regression;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class ExamTypeTests {

	private final AppCommon app = AppCommonFactory.create3D();
	CommandDispatcher commandDispatcher =
			app.getKernel().getAlgebraProcessor().getCommandDispatcher();
	ContextMenuFactory contextMenuFactory = new ContextMenuFactory();
	private TableValuesView tableValuesView;

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void solveAvailabilityInExams(ExamType t) {
		applyRestrictions(ExamRestrictions.forExamType(t));
		Set<Commands> all = Set.of(Commands.CSolve, Commands.CSolutions,
				Commands.NSolve, Commands.NSolutions,
				Commands.Solve, Commands.Solutions);
		assertEquals(expectedCommands(t),
				all.stream().filter(commandDispatcher::isAllowedByCommandFilters)
						.collect(Collectors.toSet()));
	}

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void tableSupported(ExamType t) {
		if (!ExamType.WTR.equals(t)) {
			assertTrue(commandDispatcher.isAllowedByCommandFilters(Commands.ParseToNumber));
		}
	}

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void regressionConsistent(ExamType t) throws InvalidValuesException {
		applyRestrictions(ExamRestrictions.forExamType(t));

		List<TableValuesContextMenuItem> menuItems = prepareMenuItems();
		boolean regressionInMenu = menuItems.stream().anyMatch(
				TableValuesContextMenuItem.Item.Regression::isSameItemAs);
		// WTR exam does not have the table view at all
		if (regressionInMenu && !ExamType.WTR.equals(t)) {
			Set<Commands> all = new HashSet<>();
			all.add(Commands.RemoveUndefined);
			Arrays.stream(Regression.values()).forEach(r -> all.add(r.getCommand()));
			assertEquals(all, all.stream().filter(commandDispatcher::isAllowedByCommandFilters)
					.collect(Collectors.toSet()));
		}
	}

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void statsIn1VarConsistent(ExamType t) throws InvalidValuesException {
		applyRestrictions(ExamRestrictions.forExamType(t));

		List<TableValuesContextMenuItem> menuItems = prepareMenuItems();
		boolean statsInMenu = menuItems.stream().anyMatch(
				TableValuesContextMenuItem.Item.Statistics1::isSameItemAs);
		if (statsInMenu && !ExamType.WTR.equals(t)) {
			Set<Commands> all = Set.of(Commands.Sum, Commands.Mean, Commands.SigmaXX,
					Commands.SampleSD, Commands.SD, Commands.Length, Commands.Min, Commands.Q1,
					Commands.Median, Commands.Q3, Commands.Max);
			assertEquals(expected1var(t), tableValuesView.getStatistics1Var(1).size());
			assertEquals(expected1var(t),
					all.stream().filter(commandDispatcher::isAllowedByCommandFilters).count());
		}
	}

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void statsIn2VarConsistent(ExamType t) throws InvalidValuesException {
		applyRestrictions(ExamRestrictions.forExamType(t));

		List<TableValuesContextMenuItem> menuItems = prepareMenuItems();
		boolean statsInMenu = menuItems.stream().anyMatch(
				TableValuesContextMenuItem.Item.Statistics1::isSameItemAs);
		if (statsInMenu && !ExamType.WTR.equals(t)) {
			List<Commands> all2 = List.of(Commands.Sum, Commands.Mean, Commands.SigmaXX,
					Commands.SampleSD, Commands.SD, Commands.Sum, Commands.Mean, Commands.SigmaXX,
					Commands.SampleSD, Commands.SD, Commands.Length, Commands.Min, Commands.Max,
					Commands.Min, Commands.Max,
					Commands.SigmaXY, Commands.PMCC, Commands.Covariance);
			assertEquals(expected2var(t), tableValuesView.getStatistics2Var(1).size());
			assertEquals(expected2var(t), all2.stream().filter(
					commandDispatcher::isAllowedByCommandFilters).count());
		}
	}

	@ParameterizedTest
	@EnumSource(ExamType.class)
	public void specialPointsConsistent(ExamType t) {
		applyRestrictions(ExamRestrictions.forExamType(t));
		GeoElementND function = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand("sin(x)", false)[0];
		Objects.requireNonNull(SuggestionIntersectExtremum.get(function.toGeoElement()))
				.execute(function);
		assertEquals(expectedSpecialPoints(t), app.getKernel().getConstructionStep());
	}

	private int expectedSpecialPoints(ExamType t) {
		switch (t) {
		case IB:
			return 1;
		case CVTE:
			return 2;
		case WTR:
		case MMS:
			return 0;
		default:
			return 3;
		}
	}

	private int expected1var(ExamType t) {
		switch (t) {
		case IB:
			return 9;
		case MMS:
			return 4;
		default: return 11;
		}
	}

	private int expected2var(ExamType t) {
		switch (t) {
		case IB:
			return 13;
		case MMS:
			return 8;
		default: return 18;
		}
	}

	private List<TableValuesContextMenuItem> prepareMenuItems() throws InvalidValuesException {
		tableValuesView = new TableValuesView(app.getKernel());
		app.getKernel().attach(tableValuesView);
		tableValuesView.setValues(0, 5, 1);
		GeoList geo = new GeoList(app.getKernel().getConstruction());
		for (int i = 0; i < 3; i++) {
			geo.add(new GeoNumeric(app.getKernel().getConstruction(), 1));
		}
		geo.setTableColumn(1);
		tableValuesView.add(geo);
		return contextMenuFactory.makeTableValuesContextMenu(geo, 1,
						tableValuesView.getTableValuesModel(), false, false);
	}

	private void applyRestrictions(ExamRestrictions restrictions) {
		restrictions.applyTo(new ExamController.ContextDependencies(
				app,
				app.getKernel().getAlgoDispatcher(),
				commandDispatcher,
				app.getKernel().getAlgebraProcessor(),
				app.getLocalization(),
				app.getSettings(),
				new StatisticGroupsBuilder(),
				null,
				null,
				null,
				null
		), null,
		null,
		contextMenuFactory);
	}

	private static Set<Commands> expectedCommands(ExamType t) {
		switch (t) {
		case GENERIC:
		case IB:
		case BAYERN_CAS: return Set.of(
				Commands.CSolve, Commands.CSolutions,
				Commands.NSolve, Commands.NSolutions,
				Commands.Solve, Commands.Solutions);
		case MMS:
		case NIEDERSACHSEN: return Set.of(
				Commands.NSolve, Commands.NSolutions,
				Commands.Solve, Commands.Solutions);
		default:
			return Set.of();
		}
	}
}
