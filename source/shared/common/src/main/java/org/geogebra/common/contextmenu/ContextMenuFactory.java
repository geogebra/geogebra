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

package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.AddLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateSlider;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.CreateTableValues;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Delete;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateInput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.DuplicateOutput;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveLabel;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.RemoveSlider;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Settings;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Solve;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.SpecialPoints;
import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.Statistics;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Expression;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Help;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Image;
import static org.geogebra.common.contextmenu.InputContextMenuItem.Text;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.ClearColumn;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Edit;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.HidePoints;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.ImportData;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Regression;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.RemoveColumn;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Separator;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.ShowPoints;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics1;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.Item.Statistics2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionIntersectExtremum;
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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.scientific.LabelController;

/**
 * Factory for creating context menu items.
 */
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
	 * @param algebraSettings algebra settings
	 * @param filters context menu item filters (can be empty)
	 * @return List of context menu items.
	 */
	public static @Nonnull List<AlgebraContextMenuItem> makeAlgebraContextMenu(
			@CheckForNull GeoElement geoElement,
			@Nonnull AlgebraProcessor algebraProcessor,
			@Nonnull String appCode,
			@Nonnull AlgebraSettings algebraSettings,
			@Nonnull Set<ContextMenuItemFilter> filters
	) {
		if (geoElement == null) {
			return filter(makeDeleteAlgebraContextMenu(), filters);
		}
		CreateSlider createSlider = new CreateSlider(algebraProcessor, new LabelController());
		RemoveSlider removeSlider = new RemoveSlider(algebraProcessor);
		Suggestion statisticsSuggestion = SuggestionStatistics.get(geoElement);
		Suggestion specialPointsSuggestion = SuggestionIntersectExtremum.get(geoElement);
		Suggestion solveSuggestion = SuggestionSolveForSymbolic.isValid(geoElement)
				? SuggestionSolveForSymbolic.get(geoElement)
				: SuggestionSolve.get(geoElement);

		boolean showStatisticsSuggestion = statisticsSuggestion != null;
		boolean showDuplicateOutput = AlgebraItem.shouldShowBothRows(geoElement, algebraSettings);
		boolean showSpecialPointsSuggestion = specialPointsSuggestion != null;
		boolean showCreateTableValues = hasTableOfValues(geoElement);
		boolean isAlgebraLabelVisible = geoElement.isAlgebraLabelVisible();
		boolean showCreateSlider = createSlider.isAvailable(geoElement);
		boolean showRemoveSlider = removeSlider.isAvailable(geoElement);
		boolean showSolveSuggestion = solveSuggestion != null;
		boolean showDelete = !geoElement.isProtected(EventType.REMOVE);
		// not the same as showRemoveSlider: arbitrary constants from integral
		// are sliders but do NOT allow removing sliders
		boolean isSlider = geoElement.isGeoNumeric()
				&& ((GeoNumeric) geoElement).isAVSliderOrCheckboxVisible();

		switch (appCode) {
		case GeoGebraConstants.CAS_APPCODE:
			boolean showAddRemoveLabel = !isSlider && isNotTableColumn(geoElement);
			return filter(makeCasAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showCreateTableValues,
					isAlgebraLabelVisible,
					showCreateSlider,
					showRemoveSlider,
					showSolveSuggestion,
					showAddRemoveLabel,
					showDelete), filters);
		case GeoGebraConstants.SCIENTIFIC_APPCODE:
			return filter(makeScientificAlgebraContextMenu(
					isAlgebraLabelVisible,
					showDuplicateOutput), filters);
		case GeoGebraConstants.G3D_APPCODE:
			return filter(make3DAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showSolveSuggestion,
					showCreateSlider,
					showRemoveSlider,
					showDelete), filters);
		case GeoGebraConstants.GRAPHING_APPCODE:
			return filter(makeTableValuesAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showCreateTableValues,
					showCreateSlider,
					showRemoveSlider,
					showDelete), filters);
		default:
			return filter(makeDefaultAlgebraContextMenu(
					showSpecialPointsSuggestion,
					showStatisticsSuggestion,
					showDuplicateOutput,
					showCreateSlider,
					showRemoveSlider,
					showDelete), filters);
		}
	}

	private static boolean hasTableOfValues(GeoElement geoElement) {
		return geoElement.hasTableOfValues() && isNotTableColumn(geoElement);
	}

	private static boolean isNotTableColumn(GeoElement geoElement) {
		boolean valueOrPointColumn = geoElement instanceof GeoList
				&& ((GeoList) geoElement).isTableValuesOrPointList();
		boolean standardColumn = geoElement instanceof GeoEvaluatable
				&& ((GeoEvaluatable) geoElement).getTableColumn() >= 0;
		return !valueOrPointColumn && !standardColumn;
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
	 * @param filters context menu item filters (can be empty)
	 * @return List of context menu items.
	 */
	public static @Nonnull List<TableValuesContextMenuItem> makeTableValuesContextMenu(
			@Nonnull GeoEvaluatable geoEvaluatable,
			int columnIndex,
			@Nonnull TableValuesModel tableValuesModel,
			boolean isScientific,
			boolean isExamActive,
			@Nonnull Set<ContextMenuItemFilter> filters
	) {
		if (isScientific) {
			return filter(makeScientificTableValuesContextMenu(), filters);
		}

		String columnLabel = tableValuesModel.getHeaderAt(columnIndex);
		boolean showImportData = !isExamActive;
		boolean pointsVisible = geoEvaluatable.isPointsVisible();
		boolean showEdit = geoEvaluatable instanceof GeoFunctionable;
		boolean showStatistics = geoEvaluatable instanceof GeoList;

		if (columnIndex == 0) {
			return filter(makeTableValuesContextMenuForFirstColumn(showImportData), filters);
		} else {
			return filter(makeTableValuesContextMenu(columnLabel,
					pointsVisible, showEdit, showStatistics), filters);
		}
	}

	/**
	 * Builds the context menu for the empty algebra view input.
	 *
	 * @param includeHelpItem Weather the Help item is enabled in the app.
	 * @param filters context menu item filters (can be empty)
	 * @return List of context menu items.
	 */
	public static @Nonnull List<InputContextMenuItem> makeInputContextMenu(
			boolean includeHelpItem, boolean includeImageItem,
			@Nonnull Set<ContextMenuItemFilter> filters
	) {
		List<InputContextMenuItem> items = new ArrayList<>();
		items.add(Expression);
		items.add(Text);
		if (includeImageItem) {
			items.add(Image);
		}
		if (includeHelpItem) {
			items.add(Help);
		}
		return filter(items, filters);
	}

	/**
	 * Create input context menu.
	 * @param includeHelpItem whether to include the help item
	 * @param filters context menu item filters (can be empty)
	 * @return context menu items
	 */
	public static @Nonnull List<InputContextMenuItem> makeInputContextMenu(
			boolean includeHelpItem,
			@Nonnull Set<ContextMenuItemFilter> filters
	) {
		return makeInputContextMenu(includeHelpItem, false, filters);
	}

	/**
	 * Builds the context menu for the materials in exam mode.
	 *
	 * @param filters context menu item filters (can be empty)
	 * @return List of context menu items.
	 */
	public static @Nonnull List<MaterialContextMenuItem> makeMaterialContextMenu(
			@Nonnull Set<ContextMenuItemFilter> filters
	) {
		return filter(List.of(MaterialContextMenuItem.Delete), filters);
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
			boolean showSolveSuggestion,
			boolean showAddRemoveLabel,
			boolean showDelete) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		if (showSolveSuggestion) {
			items.add(Solve);
		}
		if (showCreateTableValues) {
			items.add(CreateTableValues);
		}
		if (showAddRemoveLabel) {
			items.add(isAlgebraLabelVisible ? RemoveLabel : AddLabel);
		}
		if (showSpecialPointsSuggestion) {
			items.add(SpecialPoints);
		}
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
		if (showDelete) {
			items.add(Delete);
		}
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeScientificAlgebraContextMenu(
			boolean isLabelVisible,
			boolean showDuplicateOutput
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		items.add(isLabelVisible ? RemoveLabel : AddLabel);
		items.add(DuplicateInput);
		if (showDuplicateOutput) {
			items.add(DuplicateOutput);
		}
		items.add(Delete);
		return items;
	}

	private static List<AlgebraContextMenuItem> make3DAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showSolveSuggestion,
			boolean showCreateSlider,
			boolean showRemoveSlider,
			boolean showDelete
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
		if (showDelete) {
			items.add(Delete);
		}
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeTableValuesAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues,
			boolean showCreateSlider,
			boolean showRemoveSlider,
			boolean showDelete
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
		if (showDelete) {
			items.add(Delete);
		}
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeDefaultAlgebraContextMenu(
			boolean showSpecialPointsSuggestion,
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showCreateSlider,
			boolean showRemoveSlider,
			boolean showDelete
	) {
		List<AlgebraContextMenuItem> items = new ArrayList<>();
		if (showSpecialPointsSuggestion) {
			items.add(SpecialPoints);
		}
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
		if (showDelete) {
			items.add(Delete);
		}
		items.add(Settings);
		return items;
	}

	private static List<AlgebraContextMenuItem> makeDeleteAlgebraContextMenu() {
		return List.of(Delete);
	}

	private static <I extends ContextMenuItem> List<I> filter(@Nonnull List<I> items,
			@Nonnull Set<ContextMenuItemFilter> filters) {
		if (filters.isEmpty()) {
			return items;
		}
		// Keep only those items that are allowed by all of the filters
		List<I> filteredItems = items.stream()
				.filter(item -> filters.stream()
						.allMatch(filter -> filter.isAllowed(item)))
				.collect(Collectors.toList());

		// Remove unnecessary separators once some of the items are potentially removed
		return IntStream.range(0, filteredItems.size()).filter(index -> {
			// Remove separators
			if (Separator.isSameItemAs(filteredItems.get(index))) {
				// If they are the first/last in the list
				if (index == 0 || index == filteredItems.size() - 1) {
					return false;
				}

				// Or if there are multiple separators after each other
				if (Separator.isSameItemAs(filteredItems.get(index + 1))) {
					return false;
				}
			}

			return true;
		}).mapToObj(filteredItems::get).collect(Collectors.toList());
	}

	/**
	 * Prevent instantiation
	 */
	private ContextMenuFactory() {
	}
}
