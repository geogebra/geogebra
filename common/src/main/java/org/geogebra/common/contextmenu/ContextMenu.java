package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.*;
import static org.geogebra.common.contextmenu.InputContextMenuItem.*;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.*;

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

public final class ContextMenu<T extends ContextMenuItem> {
	private final Delegate<T> delegate;
	private final List<T> items;

	public interface Delegate<T> {
		void onItemSelected(@Nonnull T selection);
	}

	private ContextMenu(@Nonnull Delegate<T> delegate) {
		this.delegate = delegate;
		this.items = new ArrayList<>();
	}

	@Nonnull
	public List<T> getItems() {
		return items;
	}

	@Nonnull
	public Delegate<T> getDelegate() {
		return delegate;
	}

	/**
	 * Creates the context menu for the algebra view value for the context menu button at the end
	 * of a non-empty algebra view input
	 *
	 * @param geoElement Current value in the algebra view
	 * @param algebraProcessor Algebra processor responsible for handling the geo elements in the
	 *                         algebra view
	 * @param appCode The active app code, one of {@link GeoGebraConstants#CAS_APPCODE},
	 *                {@link GeoGebraConstants#SCIENTIFIC_APPCODE},
	 *                {@link GeoGebraConstants#G3D_APPCODE},
	 *                {@link GeoGebraConstants#GRAPHING_APPCODE}
	 *                or {@link GeoGebraConstants#GEOMETRY_APPCODE}
	 * @param delegate Delegate that responds to an item selection in the context menu
	 * @return Context menu for the algebra value
	 */
	public static ContextMenu<AlgebraContextMenuItem> makeAlgebraContextMenu(
			@CheckForNull GeoElement geoElement,
			@Nonnull AlgebraProcessor algebraProcessor,
			@Nonnull String appCode,
			@Nonnull Delegate<AlgebraContextMenuItem> delegate
	) {
		if (geoElement == null) {
			return makeDeleteAlgebraContextMenu(delegate);
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
					showSolveSuggestion,
					delegate);
		case GeoGebraConstants.SCIENTIFIC_APPCODE:
			return makeScientificAlgebraContextMenu(
					isAlgebraLabelVisible,
					showDuplicateOutput,
					delegate);
		case GeoGebraConstants.G3D_APPCODE:
			return make3DAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showSolveSuggestion,
					delegate);
		case GeoGebraConstants.GRAPHING_APPCODE:
			return makeTableValuesAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					showSpecialPointsSuggestion,
					showCreateTableValues,
					delegate);
		default:
			return makeDefaultAlgebraContextMenu(
					showStatisticsSuggestion,
					showDuplicateOutput,
					delegate);
		}
	}

	/**
	 * Creates the context menu for the table value columns
	 * for the context menu buttons in the header
	 *
	 * @param geoEvaluatable Evaluatable in the given table value column
	 * @param columnIndex Index of the table values column where the context menu button takes place
	 * @param tableValuesModel Table values model containing and handling the geoEvaluatable
	 *                         at the given column
	 * @param isScientific Weather the current app or sub-app is Scientific calculator
	 * @param isExamActive Weather the application is currently in exam mode
	 * @param delegate Delegate that responds to an item selection in the context menu
	 * @return Context menu for the table value columns
	 */
	public static ContextMenu<TableValuesContextMenuItem> makeTableValuesContextMenu(
			@Nonnull GeoEvaluatable geoEvaluatable,
			int columnIndex,
			@Nonnull TableValuesModel tableValuesModel,
			boolean isScientific,
			boolean isExamActive,
			@Nonnull Delegate<TableValuesContextMenuItem> delegate
	) {
		if (isScientific) {
			return makeScientificTableValuesContextMenu(delegate);
		}

		String columnLabel = tableValuesModel.getHeaderAt(columnIndex);
		boolean showImportData = !isExamActive;
		boolean pointsVisible = geoEvaluatable.isPointsVisible();
		boolean showEdit = geoEvaluatable instanceof GeoFunctionable;
		boolean showStatistics = geoEvaluatable instanceof GeoList;

		if (columnIndex == 0) {
			return makeTableValuesContextMenuForFirstColumn(showImportData, delegate);
		} else {
			return makeTableValuesContextMenu(columnLabel, pointsVisible, showEdit, showStatistics, delegate);
		}
	}

	/**
	 * Creates the context menu for the algebra view input button
	 * at the start of an empty algebra view input
	 *
	 * @param includeHelpItem Weather the Help item is enabled in the app
	 * @param delegate Delegate that responds to an item selection in the context menu
	 * @return Context menu for algebra view input button
	 */
	public static ContextMenu<InputContextMenuItem> makeInputContextMenu(
			boolean includeHelpItem,
			@Nonnull Delegate<InputContextMenuItem> delegate
	) {
		ContextMenu<InputContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(Expression);
		contextMenu.items.add(Text);
		if (includeHelpItem) contextMenu.items.add(Help);
		return contextMenu;
	}

	/**
	 * Creates the context menu for the materials in exam mode
	 *
	 * @param delegate Delegate that responds to an item selection in the context menu
	 * @return Context menu for the materials
	 */
	public static ContextMenu<MaterialContextMenuItem> makeMaterialContextMenu(
			@Nonnull Delegate<MaterialContextMenuItem> delegate
	) {
		ContextMenu<MaterialContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(MaterialContextMenuItem.Delete);
		return contextMenu;
	}

	private static ContextMenu<TableValuesContextMenuItem> makeTableValuesContextMenuForFirstColumn(
			boolean showImportData,
			Delegate<TableValuesContextMenuItem> delegate
	) {
		ContextMenu<TableValuesContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(Edit);
		contextMenu.items.add(ClearColumn);
		if (showImportData) contextMenu.items.add(ImportData);
		contextMenu.items.add(Separator);
		contextMenu.items.add(makeStatistics1Item("x"));
		return contextMenu;
	}

	private static ContextMenu<TableValuesContextMenuItem> makeTableValuesContextMenu(
			String columnLabel,
			boolean pointsVisible,
			boolean showEdit,
			boolean showStatistics,
			Delegate<TableValuesContextMenuItem> delegate
	) {
		ContextMenu<TableValuesContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(pointsVisible ? HidePoints : ShowPoints);
		if (showEdit) contextMenu.items.add(Edit);
		contextMenu.items.add(RemoveColumn);
		if (showStatistics) {
			contextMenu.items.add(Separator);
			contextMenu.items.add(makeStatistics1Item(columnLabel));
			contextMenu.items.add(makeStatistics2Item("x " + columnLabel));
			contextMenu.items.add(Regression);
		}
		return contextMenu;
	}

	private static ContextMenu<TableValuesContextMenuItem> makeScientificTableValuesContextMenu(
			Delegate<TableValuesContextMenuItem> delegate
	) {
		ContextMenu<TableValuesContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(Edit);
		contextMenu.items.add(ClearColumn);
		return contextMenu;
	}

	private static ContextMenu<AlgebraContextMenuItem> makeCasAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues,
			boolean isAlgebraLabelVisible,
			boolean showCreateSlider,
			boolean showRemoveSlider,
			boolean showSolveSuggestion,
			Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		if (showStatisticsSuggestion) contextMenu.items.add(Statistics);
		contextMenu.items.add(isAlgebraLabelVisible ? RemoveLabel : AddLabel);
		if (showCreateSlider) contextMenu.items.add(CreateSlider);
		if (showRemoveSlider) contextMenu.items.add(RemoveSlider);
		contextMenu.items.add(DuplicateInput);
		if (showDuplicateOutput) contextMenu.items.add(DuplicateOutput);
		contextMenu.items.add(Delete);
		contextMenu.items.add(Settings);
		if (showSpecialPointsSuggestion) contextMenu.items.add(SpecialPoints);
		if (showCreateTableValues) contextMenu.items.add(CreateTableValues);
		if (showSolveSuggestion) contextMenu.items.add(Solve);
		return contextMenu;
	}

	private static ContextMenu<AlgebraContextMenuItem> makeScientificAlgebraContextMenu(
			boolean isLabelVisible,
			boolean showDuplicateOutput,
			Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(isLabelVisible ? RemoveLabel : AddLabel);
		contextMenu.items.add(DuplicateInput);
		contextMenu.items.add(Delete);
		if (showDuplicateOutput) contextMenu.items.add(DuplicateOutput);
		return contextMenu;
	}

	private static ContextMenu<AlgebraContextMenuItem> make3DAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showSolveSuggestion,
			Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		if (showSolveSuggestion) contextMenu.items.add(Solve);
		if (showStatisticsSuggestion) contextMenu.items.add(Statistics);
		contextMenu.items.add(DuplicateInput);
		if (showDuplicateOutput) contextMenu.items.add(DuplicateOutput);
		contextMenu.items.add(Delete);
		contextMenu.items.add(Settings);
		if (showSpecialPointsSuggestion) contextMenu.items.add(SpecialPoints);
		return contextMenu;
	}

	private static ContextMenu<AlgebraContextMenuItem> makeTableValuesAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues,
			Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		if (showCreateTableValues) contextMenu.items.add(CreateTableValues);
		if (showSpecialPointsSuggestion) contextMenu.items.add(SpecialPoints);
		if (showStatisticsSuggestion) contextMenu.items.add(Statistics);
		contextMenu.items.add(DuplicateInput);
		if (showDuplicateOutput) contextMenu.items.add(DuplicateOutput);
		contextMenu.items.add(Delete);
		contextMenu.items.add(Settings);
		return contextMenu;
	}

	private static ContextMenu<AlgebraContextMenuItem> makeDefaultAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		if (showStatisticsSuggestion) contextMenu.items.add(Statistics);
		contextMenu.items.add(DuplicateInput);
		if (showDuplicateOutput) contextMenu.items.add(DuplicateOutput);
		contextMenu.items.add(Delete);
		contextMenu.items.add(Settings);
		return contextMenu;
	}

	private static ContextMenu<AlgebraContextMenuItem> makeDeleteAlgebraContextMenu(
			Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.items.add(Delete);
		return contextMenu;
	}

	private static TableValuesContextMenuItem makeStatistics1Item(String columnLabel) {
		TableValuesContextMenuItem item = Statistics1;
		item.setTranslationPlaceholderValues(new String[] { columnLabel });
		return item;
	}

	private static TableValuesContextMenuItem makeStatistics2Item(String columnLabel) {
		TableValuesContextMenuItem item = Statistics2;
		item.setTranslationPlaceholderValues(new String[] { columnLabel });
		return item;
	}
}
