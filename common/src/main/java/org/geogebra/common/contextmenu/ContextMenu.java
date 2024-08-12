package org.geogebra.common.contextmenu;

import static org.geogebra.common.contextmenu.AlgebraContextMenuItem.*;
import static org.geogebra.common.contextmenu.InputContextMenuItem.*;
import static org.geogebra.common.contextmenu.TableValuesContextMenuItem.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.scientific.LabelController;

public class ContextMenu<T extends ContextMenuItem> {
	public interface Delegate<T> {
		void onItemSelected(T selection);
	}

	private final Delegate<T> delegate;
	private final ArrayList<T> items;

	public ContextMenu(Delegate<T> delegate) {
		this.delegate = delegate;
		this.items = new ArrayList<>();
	}

	public List<T> getItems() {
		return items;
	}

	private void addItem(T item, boolean predicate) {
		if (predicate) {
			items.add(item);
		}
	}

	private void addItem(T item) {
		items.add(item);
	}

	public static ContextMenu<AlgebraContextMenuItem> makeAlgebraContextMenu(
			@Nullable GeoElement geoElement,
			AlgebraProcessor algebraProcessor,
			String appCode,
			ContextMenu.Delegate<AlgebraContextMenuItem> delegate
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

	public static ContextMenu<TableValuesContextMenuItem> makeTableValuesContextMenu(
			GeoEvaluatable geoEvaluatable,
			int column,
			TableValuesModel tableValuesModel,
			boolean isScientific,
			boolean isExamActive,
			ContextMenu.Delegate<TableValuesContextMenuItem> delegate
	) {
		if (isScientific) {
			return makeScientificTableValuesContextMenu(delegate);
		} else {
			String header = "x " + tableValuesModel.getHeaderAt(column);
			boolean showImportData = !isExamActive;
			boolean pointsVisible = geoEvaluatable.isPointsVisible();
			boolean showEdit = geoEvaluatable instanceof GeoFunctionable;
			boolean showStatistics = geoEvaluatable instanceof GeoList;

			if (column == 0) {
				return makeTableValuesContextMenuForFirstColumn(showImportData, delegate);
			} else {
				return makeTableValuesContextMenu(header, pointsVisible, showEdit, showStatistics, delegate);
			}
		}
	}

	public static ContextMenu<InputContextMenuItem> makeInputContextMenu(
			boolean includeHelpItem,
			ContextMenu.Delegate<InputContextMenuItem> delegate
	) {
		ContextMenu<InputContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Expression);
		contextMenu.addItem(Text);
		contextMenu.addItem(Help, includeHelpItem);
		return contextMenu;
	}

	public static ContextMenu<MaterialContextMenuItem> makeMaterialContextMenu(
			ContextMenu.Delegate<MaterialContextMenuItem> delegate
	) {
		ContextMenu<MaterialContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(MaterialContextMenuItem.Delete);
		return contextMenu;
	}

	private static ContextMenu makeTableValuesContextMenuForFirstColumn(
			boolean showImportData,
			ContextMenu.Delegate<TableValuesContextMenuItem> delegate
	) {
		ContextMenu<TableValuesContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Edit);
		contextMenu.addItem(ClearColumn);
		contextMenu.addItem(ImportData, showImportData);
		contextMenu.addItem(Statistics1);
		return contextMenu;
	}

	private static ContextMenu makeTableValuesContextMenu(
			String header,
			boolean pointsVisible,
			boolean showEdit,
			boolean showStatistics,
			ContextMenu.Delegate<TableValuesContextMenuItem> delegate
	) {
		ContextMenu<TableValuesContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(pointsVisible ? HidePoints : ShowPoints);
		contextMenu.addItem(Edit, showEdit);
		contextMenu.addItem(RemoveColumn);
		contextMenu.addItem(Statistics1, showStatistics);
		TableValuesContextMenuItem statistics2 = Statistics2;
		statistics2.setTranslationParameters(List.of(header));
		contextMenu.addItem(statistics2, showStatistics);
		contextMenu.addItem(Regression, showStatistics);
		return contextMenu;
	}

	private static ContextMenu makeScientificTableValuesContextMenu(
			ContextMenu.Delegate<TableValuesContextMenuItem> delegate
	) {
		ContextMenu<TableValuesContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Edit);
		contextMenu.addItem(ClearColumn);
		return contextMenu;
	}

	private static ContextMenu makeCasAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues,
			boolean isAlgebraLabelVisible,
			boolean showCreateSlider,
			boolean showRemoveSlider,
			boolean showSolveSuggestion,
			ContextMenu.Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Statistics, showStatisticsSuggestion);
		contextMenu.addItem(DuplicateInput);
		contextMenu.addItem(DuplicateOutput, showDuplicateOutput);
		contextMenu.addItem(Delete);
		contextMenu.addItem(Settings);
		contextMenu.addItem(SpecialPoints, showSpecialPointsSuggestion);
		contextMenu.addItem(CreateTableValues, showCreateTableValues);
		contextMenu.addItem(isAlgebraLabelVisible ? RemoveLabel : AddLabel);
		contextMenu.addItem(CreateSlider, showCreateSlider);
		contextMenu.addItem(RemoveSlider, showRemoveSlider);
		contextMenu.addItem(Solve, showSolveSuggestion);
		return contextMenu;
	}

	private static ContextMenu makeScientificAlgebraContextMenu(
			boolean isLabelVisible,
			boolean showDuplicateOutput,
			ContextMenu.Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(isLabelVisible ? RemoveLabel : AddLabel);
		contextMenu.addItem(DuplicateInput);
		contextMenu.addItem(Delete);
		contextMenu.addItem(DuplicateOutput, showDuplicateOutput);
		return contextMenu;
	}

	private static ContextMenu make3DAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showSolveSuggestion,
			ContextMenu.Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Statistics, showStatisticsSuggestion);
		contextMenu.addItem(DuplicateInput);
		contextMenu.addItem(DuplicateOutput, showDuplicateOutput);
		contextMenu.addItem(Delete);
		contextMenu.addItem(Settings);
		contextMenu.addItem(SpecialPoints, showSpecialPointsSuggestion);
		contextMenu.addItem(Solve, showSolveSuggestion);
		return contextMenu;
	}

	private static ContextMenu makeTableValuesAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			boolean showSpecialPointsSuggestion,
			boolean showCreateTableValues,
			ContextMenu.Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Statistics, showStatisticsSuggestion);
		contextMenu.addItem(DuplicateInput);
		contextMenu.addItem(DuplicateOutput, showDuplicateOutput);
		contextMenu.addItem(Delete);
		contextMenu.addItem(Settings);
		contextMenu.addItem(SpecialPoints, showSpecialPointsSuggestion);
		contextMenu.addItem(CreateTableValues, showCreateTableValues);
		return contextMenu;
	}

	private static ContextMenu makeDefaultAlgebraContextMenu(
			boolean showStatisticsSuggestion,
			boolean showDuplicateOutput,
			ContextMenu.Delegate<AlgebraContextMenuItem> delegate
	) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Statistics, showStatisticsSuggestion);
		contextMenu.addItem(DuplicateOutput, showDuplicateOutput);
		contextMenu.addItem(Delete);
		contextMenu.addItem(Settings);
		return contextMenu;
	}

	private static ContextMenu makeDeleteAlgebraContextMenu(ContextMenu.Delegate<AlgebraContextMenuItem> delegate) {
		ContextMenu<AlgebraContextMenuItem> contextMenu = new ContextMenu<>(delegate);
		contextMenu.addItem(Delete);
		return contextMenu;
	}
}
