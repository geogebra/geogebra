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

import java.util.function.Consumer;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

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
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;

/**
 * Base class that adapts between the kernel and {@link SpreadsheetStatisticsView}.
 * Subclasses only need to override the abstract methods.
 * @param <I> The input type.
 */
public abstract class KernelSpreadsheetStatisticsView<I extends SpreadsheetStatistics.Input>
		implements SpreadsheetStatisticsView<I>, View {

	private final @Nonnull String titleLocalizationKey;
	protected final @Nonnull Kernel kernel;
	protected final @Nonnull StatisticGroupsBuilder statisticGroupsBuilder;
	private @Nonnull I input;
	private @CheckForNull SpreadsheetStatistics.Result result;
	private @CheckForNull Consumer<SpreadsheetStatistics.Result> changeListener;

	protected KernelSpreadsheetStatisticsView(@Nonnull Kernel kernel,
			@Nonnull StatisticGroupsBuilder statisticGroupsBuilder, @Nonnull I input,
			@Nonnull String titleLocalizationKey) {
		this.kernel = kernel;
		this.statisticGroupsBuilder = statisticGroupsBuilder;
		this.input = input;
		this.titleLocalizationKey = titleLocalizationKey;
		kernel.attach(this);
	}

	/**
	 * Check if an element is relevant for the calculation.
	 * @param element An element.
	 * @return {@code true} if the element falls within the range of any involved
	 * {@link AlgoCellRange}.
	 */
	protected abstract boolean isWithinAlgoRange(@Nonnull GeoElement element);

	/**
	 * Validate the input and compute the result.
	 * @param input The input (needs to be validated).
	 * @return An error if the input failed validation, or the result of the calculation.
	 */
	protected abstract @Nonnull SpreadsheetStatistics.Result calculate(@Nonnull I input);

	protected final @Nonnull AlgoCellRange setCellRange(@Nonnull SpreadsheetReference cellRange,
			@CheckForNull AlgoCellRange algo) {
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

	protected final boolean isElementInRange(@Nonnull GeoElement element,
			@Nonnull SpreadsheetReference cellRange) {
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

	private void recalculate() {
		setResult(calculate(input));
	}

	private void setResult(@CheckForNull SpreadsheetStatistics.Result result) {
		this.result = result;
		if (changeListener != null) {
			changeListener.accept(result);
		}
	}

	protected SpreadsheetStatistics.Result.Invalid newInvalidResult(
			@Nonnull SpreadsheetStatistics.Error error,
			@CheckForNull SpreadsheetStatistics.DataRange range) {
		// Only focus user attention to the data range, if this is the first result.
		return new SpreadsheetStatistics.Result.Invalid(error, result == null ? range : null);
	}

	// -- SpreadsheetStatisticsView --

	@Override
	public @Nonnull String getTitleLocalizationKey() {
		return titleLocalizationKey;
	}

	@Override
	public @Nonnull I getInput() {
		return input;
	}

	@Override
	public void setInput(@Nonnull I input) {
		this.input = input;
		recalculate();
	}

	@Override
	public final @Nonnull SpreadsheetStatistics.Result getResult() {
		// lazy evaluation
		if (result == null) {
			recalculate();
		}
		assert result != null;
		return result;
	}

	@Override
	public void setChangeListener(@CheckForNull Consumer<SpreadsheetStatistics.Result> listener) {
		changeListener = listener;
	}

	@Override
	public void tearDown() {
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
		setResult(null);
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
