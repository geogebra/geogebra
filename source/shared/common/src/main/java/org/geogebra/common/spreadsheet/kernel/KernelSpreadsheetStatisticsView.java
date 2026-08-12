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

package org.geogebra.common.spreadsheet.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.StatisticsReferenceDelegate;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Base class that adapts between the kernel and {@link SpreadsheetStatisticsView}.
 * Subclasses only need to override the abstract methods.
 * @param <I> The input type.
 */
public abstract class KernelSpreadsheetStatisticsView<I extends SpreadsheetStatistics.Input>
		implements SpreadsheetStatisticsView<I>, View {

	private final @NonNull String titleLocalizationKey;
	protected final @NonNull Kernel kernel;
	protected final @NonNull StatisticGroupsBuilder statisticGroupsBuilder;
	private @NonNull I input;
	private SpreadsheetStatistics.@Nullable DataRange focusedDataRange;
	private SpreadsheetStatistics.@Nullable Result result;
	private @Nullable Consumer<@NonNull I> inputChangeListener;
	private @Nullable Consumer<SpreadsheetStatistics.@NonNull Result> resultChangeListener;
	private @Nullable StatisticsReferenceDelegate statisticsReferenceDelegate;

	protected KernelSpreadsheetStatisticsView(@NonNull Kernel kernel,
			@NonNull StatisticGroupsBuilder statisticGroupsBuilder, @NonNull I input,
			@NonNull String titleLocalizationKey,
			@NonNull StatisticsReferenceDelegate statisticsReferenceDelegate) {
		this.kernel = kernel;
		this.statisticGroupsBuilder = statisticGroupsBuilder;
		this.input = input;
		this.titleLocalizationKey = titleLocalizationKey;
		this.statisticsReferenceDelegate = statisticsReferenceDelegate;
		kernel.attach(this);
	}

	/**
	 * Check if an element is relevant for the calculation.
	 * @param element An element.
	 * @return {@code true} if the element falls within the range of any involved
	 * {@link AlgoCellRange}.
	 */
	protected abstract boolean isWithinAlgoRange(@NonNull GeoElement element);

	/**
	 * Validate the input and compute the result.
	 * @param input The input (needs to be validated).
	 * @return An error if the input failed validation, or the result of the calculation.
	 */
	protected abstract SpreadsheetStatistics.@NonNull Result calculate(@NonNull I input);

	protected final @NonNull AlgoCellRange setCellRange(@NonNull SpreadsheetReference cellRange,
			@Nullable AlgoCellRange algo) {
		SpreadsheetCellReference fromCell = cellRange.fromCell;
		SpreadsheetCellReference toCell = cellRange.toCell == null ? fromCell : cellRange.toCell;
		if (algo == null) {
			return new AlgoCellRange(kernel.getConstruction(),
					fromCell.toString(), toCell.toString());
		}
		algo.setRange(new TabularRange(fromCell.rowIndex, fromCell.columnIndex,
				toCell.rowIndex, toCell.columnIndex));
		return algo;
	}

	protected final boolean isElementInRange(@NonNull GeoElement element,
			@NonNull SpreadsheetReference cellRange) {
		SpreadsheetCoords coords = element.getSpreadsheetCoords();
		if (coords == null) {
			return false;
		}
		SpreadsheetCellReference fromCell = cellRange.fromCell;
		SpreadsheetCellReference toCell = cellRange.toCell == null ? fromCell : cellRange.toCell;
		int minCol = Math.min(fromCell.columnIndex, toCell.columnIndex);
		int maxCol = Math.max(fromCell.columnIndex, toCell.columnIndex);
		int minRow = Math.min(fromCell.rowIndex, toCell.rowIndex);
		int maxRow = Math.max(fromCell.rowIndex, toCell.rowIndex);
		return coords.row >= minRow && coords.row <= maxRow
				&& coords.column >= minCol && coords.column <= maxCol;
	}

	protected final void recalculate() {
		setResult(calculate(input));
	}

	private void notifyStatisticsReferencesChanged() {
		if (focusedDataRange == null) {
			if (statisticsReferenceDelegate != null) {
				statisticsReferenceDelegate.statisticsReferencesChanged(null, null);
			}
			return;
		}
		if (input instanceof SpreadsheetStatistics.Input.OneVarInput oneVarInput) {
			publishStatisticsReferences(oneVarInput.cellRange(), null);
		} else if (input instanceof SpreadsheetStatistics.Input.TwoVarInput twoVarInput) {
			publishStatisticsReferences(twoVarInput.cellRangeX(), twoVarInput.cellRangeY());
		} else if (input instanceof SpreadsheetStatistics.Input.RegressionInput regressionInput) {
			publishStatisticsReferences(regressionInput.cellRangeX(), regressionInput.cellRangeY());
		}
	}

	private void publishStatisticsReferences(@Nullable SpreadsheetReference statisticsReferenceX,
			@Nullable SpreadsheetReference statisticsReferenceY) {
		List<SpreadsheetReference> unfocusedStatisticsReferences = new ArrayList<>(1);
		SpreadsheetReference focusedStatisticsReference = null;
		if (statisticsReferenceX != null) {
			if (focusedDataRange == SpreadsheetStatistics.DataRange.X) {
				focusedStatisticsReference = statisticsReferenceX;
			} else {
				unfocusedStatisticsReferences.add(statisticsReferenceX);
			}
		}
		if (statisticsReferenceY != null) {
			if (focusedDataRange == SpreadsheetStatistics.DataRange.Y) {
				focusedStatisticsReference = statisticsReferenceY;
			} else {
				unfocusedStatisticsReferences.add(statisticsReferenceY);
			}
		}
		if (statisticsReferenceDelegate != null) {
			statisticsReferenceDelegate.statisticsReferencesChanged(
					focusedStatisticsReference, unfocusedStatisticsReferences);
		}
	}

	private void setResult(SpreadsheetStatistics.@NonNull Result result) {
		this.result = result;
		if (resultChangeListener != null) {
			resultChangeListener.accept(result);
		}
	}

	protected SpreadsheetStatistics.Result.Invalid newInvalidResult(
			SpreadsheetStatistics.@NonNull Error error,
			SpreadsheetStatistics.@Nullable DataRange range) {
		// Only focus user attention to the data range, if this is the first result.
		return new SpreadsheetStatistics.Result.Invalid(error, result == null ? range : null);
	}

	// -- SpreadsheetStatisticsView --

	@Override
	public @NonNull String getTitleLocalizationKey() {
		return titleLocalizationKey;
	}

	@Override
	public @NonNull I getInput() {
		return input;
	}

	@Override
	public void setInput(@NonNull I input) {
		this.input = input;
		notifyStatisticsReferencesChanged();
		if (inputChangeListener != null) {
			inputChangeListener.accept(input);
		}
	}

	@Override
	public void commitInput() {
		recalculate();
	}

	@Override
	public void setFocusedDataRange(SpreadsheetStatistics.@Nullable DataRange focusedDataRange) {
		this.focusedDataRange = focusedDataRange;
		notifyStatisticsReferencesChanged();
	}

	@Override
	public SpreadsheetStatistics.@Nullable DataRange getFocusedDataRange() {
		return focusedDataRange;
	}

	@Override
	public final SpreadsheetStatistics.@NonNull Result getResult() {
		// lazy evaluation
		if (result == null) {
			recalculate();
		}
		assert result != null;
		return result;
	}

	@Override
	public void setInputChangeListener(@Nullable Consumer<@NonNull I> listener) {
		inputChangeListener = listener;
	}

	@Override
	public void setResultChangeListener(
			@Nullable Consumer<SpreadsheetStatistics.@NonNull Result> listener) {
		resultChangeListener = listener;
	}

	@Override
	public void tearDown() {
		statisticsReferenceDelegate = null;
		inputChangeListener = null;
		resultChangeListener = null;
		kernel.detach(this);
	}

	// -- View

	@Override
	public void add(GeoElement geo) {
		if (geo == null) {
			return;
		}
		if (isWithinAlgoRange(geo)) {
			recalculate();
		}
	}

	@Override
	public void remove(GeoElement geo) {
		if (geo == null) {
			return;
		}
		if (isWithinAlgoRange(geo)) {
			recalculate();
		}
	}

	@Override
	public void rename(GeoElement geo) {
		// TODO not needed?
	}

	@Override
	public void update(GeoElement geo) {
		if (geo == null) {
			return;
		}
		if (isWithinAlgoRange(geo)) {
			recalculate();
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		// not needed
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// not needed
	}

	@Override
	public void repaintView() {
		// TODO not needed?
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public void reset() {
		recalculate();
	}

	@Override
	public void clearView() {
		reset();
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// ignore
	}

	@Override
	public abstract int getViewID();

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// ignore
	}
}
