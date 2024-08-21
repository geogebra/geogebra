package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.*;
import static org.geogebra.common.contextmenu.InputContextMenuItem.*;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.scientific.LabelController;

public final class ContextMenuFactory {
	/**
	 * Builds the context menu for an item in the algebra view.
	 *
	 * @param geoElement An item in the algebra view.
	 * @param algebraProcessor Algebra processor responsible for handling the geo elements in the
	 *                         algebra view.
	 * @param appCode The active app code, one of {@link GeoGebraConstants#CAS_APPCODE},
	 *                {@link GeoGebraConstants#SCIENTIFIC_APPCODE},
	 *                {@link GeoGebraConstants#G3D_APPCODE},
	 *                {@link GeoGebraConstants#GRAPHING_APPCODE}
	 *                or {@link GeoGebraConstants#GEOMETRY_APPCODE}.
	 * @return List of context menu items.
	 */
	@Nonnull
	public static List<AlgebraContextMenuItem> makeAlgebraContextMenu(
			@CheckForNull GeoElement geoElement,
			@Nonnull AlgebraProcessor algebraProcessor,
			@Nonnull String appCode
	) {
		if (geoElement == null) {
			return makeDeleteAlgebraContextMenu();
		}

		CreateSlider createSlider = new CreateSlider(algebraProcessor, new LabelController());
		RemoveSlider removeSlider = new RemoveSlider(algebraProcessor);
		Suggestion statisticsSuggestion = SuggestionStatistics.get(geoElement);
		Suggestion specialPointsSuggestion = SuggestionRootExtremum.get(geoElement);
		Suggestion solveSuggestion = SuggestionSolveForSymbolic.isValid(geoElement)
				? SuggestionSolveForSymbolic.get(geoElement)
				: SuggestionSolve.get(geoElement);

		boolean showStatisticsSuggestion = statisticsSuggestion != null;
		boolean showDuplicateOutput = AlgebraItem.shouldShowBothRows(geoElement);
		boolean showSpecialPointsSuggestion = specialPointsSuggestion != null;
		boolean showCreateTableValues = geoElement.hasTableOfValues();
		boolean isAlgebraLabelVisible = geoElement.isAlgebraLabelVisible();
		boolean showCreateSlider = createSlider.isAvailable(geoElement);
		boolean showRemoveSlider = removeSlider.isAvailable(geoElement);
		boolean showSolveSuggestion = solveSuggestion != null;

		switch (appCode) {
		case GeoGebraConstants.CAS_APPCODE:
			return makeCasAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showCreateTableValues,
					isAlgebraLabelVisible,
					showCreateSlider,
					showRemoveSlider,
					showSolveSuggestion);
		case GeoGebraConstants.SCIENTIFIC_APPCODE:
			return makeScientificAlgebraContextMenu(
					isAlgebraLabelVisible,
					showDuplicateOutput);
		case GeoGebraConstants.G3D_APPCODE:
			return make3DAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showSolveSuggestion);
		case GeoGebraConstants.GRAPHING_APPCODE:
			return makeTableValuesAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showCreateTableValues);
		default:
			return makeDefaultAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput);
		}
	}

	/**
	 * Builds the context menu for a column in the table values view.
	 *
	 * @param geoEvaluatable Evaluatable in the given table value column.
	 * @param columnIndex Index of the table values column.
	 * @param tableValuesModel Table values model containing and handling the geoEvaluatable
	 *                         at the given column.
	 * @param isScientific Weather the current app or sub-app is Scientific calculator.
	 * @param isExamActive Weather the application is currently in exam mode.
	 * @return List of context menu items.
	 */
	@Nonnull
	public static List<TableValuesContextMenuItem> makeTableValuesContextMenu(
			@Nonnull GeoEvaluatable geoEvaluatable,
			int columnIndex,
			@Nonnull TableValuesModel tableValuesModel,
			boolean isScientific,
			boolean isExamActive
	) {
		if (isScientific) {
			return makeScientificTableValuesContextMenu();
		}

		String columnLabel = tableValuesModel.getHeaderAt(columnIndex);
		boolean showImportData = !isExamActive;
		boolean pointsVisible = geoEvaluatable.isPointsVisible();
		boolean showEdit = geoEvaluatable instanceof GeoFunctionable;
		boolean showStatistics = geoEvaluatable instanceof GeoList;

		if (columnIndex == 0) {
			return makeTableValuesContextMenuForFirstColumn(showImportData);
		} else {
			return makeTableValuesContextMenu(columnLabel, pointsVisible, showEdit, showStatistics);
		}
	}

	/**
	 * Builds the context menu for the empty algebra view input.
	 *
	 * @param includeHelpItem Weather the Help item is enabled in the app.
	 * @return List of context menu items.
	 */
	@Nonnull
	public static List<InputContextMenuItem> makeInputContextMenu(
			boolean includeHelpItem
	) {
		List<InputContextMenuItem> items = new ArrayList<>();
		items.add(Expression);
		items.add(Text);
		if (includeHelpItem) {
			items.add(Help);
		}
		return items;
	}

	/**
	 * Builds the context menu for the materials in exam mode.
	 *
	 * @return List of context menu items.
	 */
	@Nonnull
	public static List<MaterialContextMenuItem> makeMaterialContextMenu() {
		return List.of(MaterialContextMenuItem.Delete);
	}

	private static List<TableValuesContextMenuItem> makeTableValuesContextMenuForFirstColumn(
			boolean showImportData
	) {
		List<TableValuesContextMenuItem> items = new ArrayList<>();
		items.add(Edit.toContextMenuItem());
		items.add(ClearColumn.toContextMenuItem());
		if (showImportData) {
			items.add(ImportData.toContextMenuItem());
		}
		items.add(Separator.toContextMenuItem());
		items.add(Statistics1.toContextMenuItem(new String[] { "x" }));
		return items;
	}

	private static List<TableValuesContextMenuItem> makeTableValuesContextMenu(
			String columnLabel,
			boolean pointsVisible,
			boolean showEdit,
			boolean showStatistics
	) {
		List<TableValuesContextMenuItem> items = new ArrayList<>();
		items.add(pointsVisible ? HidePoints.toContextMenuItem() : ShowPoints.toContextMenuItem());
		if (showEdit) {
			items.add(Edit.toContextMenuItem());
		}
		items.add(RemoveColumn.toContextMenuItem());
		if (showStatistics) {
			items.add(Separator.toContextMenuItem());
			items.add(Statistics1.toContextMenuItem(new String[] { columnLabel }));
			items.add(Statistics2.toContextMenuItem(new String[] { "x " + columnLabel }));
			items.add(Regression.toContextMenuItem());
		}
		return items;
	}

	private static List<TableValuesContextMenuItem> makeScientificTableValuesContextMenu() {
		return List.of(Edit.toContextMenuItem(), ClearColumn.toContextMenuItem());
	}

	private static List<AlgebraContextMenuItem> makeCasAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues,
			boolean isAlgebraLabelVisible,
			boolean showCreateSlider,
			boolean showRemoveSlider,
			boolean showSolveSuggestion
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		if (showSolveSuggestion) {
			items.add(Solve);
		}
		if (showCreateTableValues) {
			items.add(CreateTableValues);
		}
		items.add(isAlgebraLabelVisible ? RemoveLabel : AddLabel);
		if (showStatisticsSuggestion) {
			items.add(Statistics);
		}
		if (showCreateSlider) {
			items.add(CreateSlider);
		}
		if (showRemoveSlider) {
			items.add(RemoveSlider);
		}
		items.add(DuplicateInput);
		if (showDuplicateOutput) {
			items.add(DuplicateOutput);
		}
		items.add(Delete);
		items.add(Settings);
		if (showSpecialPointsSuggestion) {
			items.add(SpecialPoints);
		}
		return items;
	}

	private static List<AlgebraContextMenuItem> makeScientificAlgebraContextMenu(
			boolean isLabelVisible,
			boolean showDuplicateOutput
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		items.add(isLabelVisible ? RemoveLabel : AddLabel);
		items.add(DuplicateInput);
		items.add(Delete);
		if (showDuplicateOutput) {
			items.add(DuplicateOutput);
		}
		return items;
	}

	private static List<AlgebraContextMenuItem> make3DAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showSolveSuggestion
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		if (showSolveSuggestion) {
			items.add(Solve);
		}
		if (showStatisticsSuggestion) {
			items.add(Statistics);
		}
		if (showSpecialPointsSuggestion) {
			items.add(SpecialPoints);
		}
		items.add(DuplicateInput);
		if (showDuplicateOutput) {
			items.add(DuplicateOutput);
		}
		items.add(Delete);
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeTableValuesAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		if (showCreateTableValues) {
			items.add(CreateTableValues);
		}
		if (showSpecialPointsSuggestion) {
			items.add(SpecialPoints);
		}
		if (showStatisticsSuggestion) {
			items.add(Statistics);
		}
		items.add(DuplicateInput);
		if (showDuplicateOutput) {
			items.add(DuplicateOutput);
		}
		items.add(Delete);
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeDefaultAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		if (showStatisticsSuggestion) {
			items.add(Statistics);
		}
		items.add(DuplicateInput);
		if (showDuplicateOutput) {
			items.add(DuplicateOutput);
		}
		items.add(Delete);
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeDeleteAlgebraContextMenu() {
		return List.of(Delete);
	}
}
