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

package org.geogebra.common.gui.view.algebra;

import java.util.Objects;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.AlgebraSettings;

/**
 * An item in the Algebra View UI that
 * <ul>
 * <li>wraps a {@code GeoElement}</li>
 * <li>encapsulates all the item logic, providing the UI state in easy-to-consume form</li>
 * <li>has a stable id (for identification and changeset computation)</li>
 * <li>can employ lazy evaluation for state that's not used by all clients</li>
 * </ul>
 */
public final class AlgebraViewItem {

	public final GeoElement geo;
	public final Integer id;
	int index;
	private HeaderState header;
	private InputRowState inputRow;
	private SliderRowState sliderRow;
	private OutputRowState outputRow;

	AlgebraViewItem(GeoElement geo) {
		this(geo, -1);
	}

	AlgebraViewItem(GeoElement geo, Integer id) {
		this.geo = geo;
		this.id = id;
	}

	/**
	 * @return A stable integer id for this {@code AlgebraViewItem} (e.g., for id-based diffing).
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return The item's (0-based) index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the item's header state.
	 */
	public @Nonnull HeaderState getHeader() {
		if (header == null) {
			header = new HeaderState();
			if (geo.isEuclidianVisible()) {
				header.marbleState = MarbleState.ACTIVE;
			} else if (geo.isEuclidianToggleable()) {
				header.marbleState = MarbleState.ENABLED;
			} else {
				header.marbleState = MarbleState.DISABLED;
			}
			header.marbleOutlineColorARGB = geo.getAlgebraColor().getARGB();
			header.marbleFillColorARGB = geo.getObjectColor().getARGB();
			if (AlgebraItem.isTextItem(geo) && geo.isIndependent()) {
				header.marbleIcon = MarbleIcon.QUOTE;
			}
		}
		return header;
	}

	/**
	 * @return the item's input row state.
	 */
	public @Nonnull InputRowState getInputRow() {
		if (inputRow == null) {
			inputRow = new InputRowState();
			boolean showingOnlyOutput = AlgebraItem.isCompactItem(geo) && geo.getApp()
					.getAlgebraOutputFilter().isAllowed(geo);
			inputRow.isVisible = !showingOnlyOutput;
			if (inputRow.isVisible) {
				inputRow.isTextCell = AlgebraItem.isTextItem(geo) && geo.isIndependent();
				inputRow.previewLaTex = AlgebraItem.getPreviewLatexForGeoElement(geo);
				if (inputRow.isTextCell) {
					inputRow.editorLaTeX = geo.getDefinitionForEditor();
				} else {
					String editorLaTeX = isOneOfMultipleOutputs()
							? geo.getParentAlgorithm()
								.getDefinition(StringTemplate.editorTemplate)
							: AlgebraItem.getDefinitionLatexForGeoElement(geo);
					inputRow.editorLaTeX = editorLaTeX;
					// TODO also parse LaTeX into Formula here?
				}
				inputRow.isMoreButtonVisible = true;
			}
		}
		return inputRow;
	}

	/**
	 * @return the item's slider row state.
	 */
	public @Nonnull SliderRowState getSliderRow() {
		if (sliderRow == null) {
			sliderRow = new SliderRowState();
			sliderRow.isVisible = AlgebraItem.shouldShowSlider(geo);
			if (sliderRow.isVisible) {
				GeoNumeric geoNumeric = (GeoNumeric) geo;
				double min = clamp(geoNumeric.getIntervalMin(), -5.0);
				double max = clamp(geoNumeric.getIntervalMax(), 5.0);
				double step = geoNumeric.getAnimationStep();
				sliderRow.min = Math.min(min, max);
				sliderRow.max = Math.max(min, max);
				sliderRow.step = Math.abs(step);
				sliderRow.value = geoNumeric.getValue();
				sliderRow.isPlaying = geo.isAnimating()
						&& geo.getKernel().getAnimationManager().isRunning();
			}
		}
		return sliderRow;
	}

	/**
	 * @return the item's output row state.
	 */
	public @Nonnull OutputRowState getOutputRow() {
		if (outputRow == null) {
			outputRow = new OutputRowState();
			boolean showSlider = AlgebraItem.shouldShowSlider(geo);
			boolean showBothRows = AlgebraItem.shouldShowBothRows(geo, geo.getApp()
					.getSettings().getAlgebra()) && !showSlider;
			boolean showingOnlyOutput = AlgebraItem.isCompactItem(geo) && geo.getApp()
					.getAlgebraOutputFilter().isAllowed(geo);
			outputRow.isVisible = !showSlider && (showBothRows || showingOnlyOutput);
			if (outputRow.isVisible) {
				String outputText = AlgebraItem.getOutputTextForGeoElement(geo);
				if (outputText != null && AlgebraItem.isTextItem(geo)) {
					outputRow.laTeX = "\\text{" + outputText + "}";
				} else {
					outputRow.laTeX = outputText;
				}
				outputRow.outputFormat = showingOnlyOutput
						? null : AlgebraOutputFormat.getOutputOperator(geo);
				outputRow.nextOutputFormat = getNextOutputFormat(geo);
				outputRow.isMoreButtonVisible = showingOnlyOutput;
			}
		}
		return outputRow;
	}

	/**
	 * Discards cached data.
	 */
	void reset() {
		header = null;
		inputRow = null;
		sliderRow = null;
		outputRow = null;
	}

	/**
	 * Update the output format (called by
	 * {@link AlgebraViewItems#outputFormatToggleButtonPressed(AlgebraViewItem)}).
	 */
	void updateOutputFormat() {
		getOutputRow(); // make sure outputRow is initialized
		outputRow.outputFormat = AlgebraOutputFormat.getOutputOperator(geo);
		outputRow.nextOutputFormat = getNextOutputFormat(geo);
	}

	private boolean isOneOfMultipleOutputs() {
		return geo.getParentAlgorithm() != null && geo.getParentAlgorithm().getOutputLength() > 1;
	}

	private AlgebraOutputFormat getNextOutputFormat(GeoElement geo) {
		AlgebraSettings algebraSettings = geo.getApp().getSettings().getAlgebra();
		boolean isEngineeringNotationEnabled = algebraSettings.isEngineeringNotationEnabled();
		Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters =
				algebraSettings.getAlgebraOutputFormatFilters();
		AlgebraOutputFormat nextOutputFormat = AlgebraOutputFormat.getNextFormat(geo,
				isEngineeringNotationEnabled, algebraOutputFormatFilters);
		return nextOutputFormat;
	}

	private double clamp(double value, double edge) {
		return Double.isNaN(value) ? edge : value;
	}

	// -- Object --

	/**
	 * Note: equality for {@code AlgebraViewItem}s is defined as
	 * "references same {@code GeoElement}"!
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AlgebraViewItem)) {
			return false;
		}
		AlgebraViewItem other = (AlgebraViewItem) object;
		return geo == other.geo;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(geo);
	}

	// Nested Types

	public enum MarbleState {
		/** disabled (grayed out) */
		DISABLED,
		/** enabled but not active */
		ENABLED,
		/** enabled and active (filled) */
		ACTIVE
	}

	public enum MarbleIcon {
		/** No icon */
		NONE,
		/** Used for text cells */
		QUOTE
	}

	public static final class HeaderState {
		/** marble state */
		@Nonnull MarbleState marbleState = MarbleState.DISABLED;
		/** #ARGB (A is most significant byte) */
		int marbleOutlineColorARGB;
		int marbleFillColorARGB;
		/** marble icon (overlay) */
		@Nonnull MarbleIcon marbleIcon = MarbleIcon.NONE;

		public @Nonnull MarbleState getMarbleState() {
			return marbleState;
		}

		public int getMarbleOutlineColorARGB() {
			return marbleOutlineColorARGB;
		}

		public int getMarbleFillColorARGB() {
			return marbleFillColorARGB;
		}

		public @Nonnull MarbleIcon getMarbleIcon() {
			return marbleIcon;
		}
	}

	public static final class InputRowState {
		/** input row visible? */
		boolean isVisible;
		/** plain text, or formula? */
		boolean isTextCell;
		/** preview LaTeX (read-only) */
		@CheckForNull String previewLaTex;
		/** editor LaTeX */
		@CheckForNull String editorLaTeX;
		/** more button visible? */
		boolean isMoreButtonVisible;

		public boolean isVisible() {
			return isVisible;
		}

		public boolean isTextCell() {
			return isTextCell;
		}

		public @CheckForNull String getPreviewLaTex() {
			return previewLaTex;
		}

		public @CheckForNull String getEditorLaTeX() {
			return editorLaTeX;
		}

		public boolean isMoreButtonVisible() {
			return isMoreButtonVisible;
		}
	}

	public static final class SliderRowState {
		/** slider row visible? */
		boolean isVisible;
		/** min/max raw */
		double min, max;
		/** value/step raw */
		double value, step;
		/** play button state (play/pause) */
		boolean isPlaying;

		public boolean isVisible() {
			return isVisible;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}

		public double getValue() {
			return value;
		}

		public double getStep() {
			return step;
		}

		public boolean isPlaying() {
			return isPlaying;
		}
	}

	public static final class OutputRowState {
		/** output row visible? */
		boolean isVisible;
		/** output format icon (equal, approximately equal) */
		@CheckForNull AlgebraOutputOperator outputFormat;
		/** output format toggle button icon (=next value). null means "hide button" */
		@CheckForNull AlgebraOutputFormat nextOutputFormat;
		/** output LaTeX */
		@CheckForNull String laTeX;
		/** more button visible */
		boolean isMoreButtonVisible;

		public boolean isVisible() {
			return isVisible;
		}

		public @CheckForNull AlgebraOutputOperator getOutputFormat() {
			return outputFormat;
		}

		public @CheckForNull AlgebraOutputFormat getNextOutputFormat() {
			return nextOutputFormat;
		}

		public @CheckForNull String getLaTeX() {
			return laTeX;
		}

		public boolean isMoreButtonVisible() {
			return isMoreButtonVisible;
		}
	}
}
