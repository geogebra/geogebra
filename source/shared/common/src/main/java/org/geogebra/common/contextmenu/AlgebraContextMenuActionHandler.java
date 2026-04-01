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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionIntersectExtremum;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionSolveForSymbolic;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.PropertyViewFactory;
import org.geogebra.common.scientific.LabelController;

import com.google.j2objc.annotations.Weak;

/** Action handler for algebra context menu items. */
public final class AlgebraContextMenuActionHandler {
	private final App app;
	private final TableValues tableValues;
	private final GeoElement geoElement;
	private final @Weak Delegate delegate;

	/** Delegate interface to perform UI-related and platform-specific operations. */
	public interface Delegate {
		/** Clears the input on the algebra view item for which the context menu was opened. */
		void clearAlgebraInput();

		/**
		 * Shows the dialog for creating table value items.
		 * @param geoElement element used to create the table values
		 */
		void showTableValuesDialog(GeoElement geoElement);

		/** Scrolls the table values view to show the column with the given index. */
		void scrollToTableValuesColumn(int columnIndex);

		/** Shows the table values tab. */
		void showTableValuesView();

		/**
		 * Adds the given formula to a new algebra view item for editing
		 * @param formula the formula to add
		 */
		void addFormulaToAlgebraView(@Nonnull String formula);

		/**
		 * Displays the object settings view for legacy object properties.
		 * @apiNote If the method was called, it was already verified
		 * that {@link PreviewFeature#SETTINGS_VIEW} is disabled.
		 */
		void showOldObjectProperties();

		/**
		 * Displays the object settings view for the new object properties.
		 * @param tabbedPageSelector the root {@link PropertyView} for the view to display
		 * @apiNote If the method was called, it was already verified
		 * that {@link PreviewFeature#SETTINGS_VIEW} is enabled.
		 */
		void showObjectProperties(@Nonnull PropertyView.TabbedPageSelector tabbedPageSelector);
	}

	/**
	 * Constructs the action handler for algebra context menu items.
	 * @param app the active app
	 * @param tableValues the table values of the active app
	 * @param geoElement the element in the algebra view for which the context menu was open
	 * @param delegate the delegate for the platform-specific operations
	 */
	public AlgebraContextMenuActionHandler(@Nonnull App app, @Nonnull TableValues tableValues,
			@CheckForNull GeoElement geoElement, @Nonnull Delegate delegate) {
		this.app = app;
		this.tableValues = tableValues;
		this.geoElement = geoElement;
		this.delegate = delegate;
	}

	/**
	 * Perform the action for the selected context menu item.
	 * @param selectedItem the selected context menu item
	 */
	public void handleSelectedItem(@Nonnull AlgebraContextMenuItem selectedItem) {
		if (geoElement == null) {
			if (selectedItem == AlgebraContextMenuItem.Delete) {
				delegate.clearAlgebraInput();
			}
			return;
		}
		switch (selectedItem) {
			case Statistics -> executeStatisticsSuggestion();
			case Delete -> delete();
			case DuplicateInput -> duplicateInput();
			case DuplicateOutput -> duplicateOutput();
			case Settings -> showSettings();
			case SpecialPoints -> showSpecialPoints();
			case CreateTableValues -> createTableValues();
			case RemoveLabel -> removeLabel();
			case AddLabel -> addLabel();
			case CreateSlider -> createSlider();
			case RemoveSlider -> removeSlider();
			case Solve -> solve();
		}
	}

	private void executeStatisticsSuggestion() {
		Suggestion suggestion = SuggestionStatistics.get(geoElement);
		if (suggestion != null) {
			suggestion.execute(geoElement);
		}
	}

	private void delete() {
		geoElement.remove();
		app.storeUndoInfo();
	}

	private void duplicateInput() {
		delegate.addFormulaToAlgebraView(AlgebraItem.getDuplicateFormulaForGeoElement(geoElement));
	}

	private void duplicateOutput() {
		delegate.addFormulaToAlgebraView(app.getGeoElementValueConverter()
				.toOutputValueString(geoElement, StringTemplate.algebraTemplate));
	}

	private void showSettings() {
		app.getSelectionManager().clearSelectedGeos();
		app.getSelectionManager().addSelectedGeo(geoElement);
		if (PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)) {
			delegate.showObjectProperties(PropertyViewFactory.propertyViewOfObjectSettings(app));
		} else {
			delegate.showOldObjectProperties();
		}
	}

	private void showSpecialPoints() {
		SuggestionIntersectExtremum.get(geoElement).execute(geoElement);
	}

	private void createTableValues() {
		delegate.showTableValuesView();
		if (tableValues.isEmpty()) {
			delegate.showTableValuesDialog(geoElement);
		}
		app.getEventDispatcher().dispatchEvent(EventType.ADD_TV, geoElement);
		int columnIndex = tableValues.getColumn((GeoEvaluatable) geoElement);
		if (columnIndex >= 0) {
			delegate.scrollToTableValuesColumn(columnIndex);
		} else {
			tableValues.showColumn((GeoEvaluatable) geoElement);
		}
	}

	private void removeLabel() {
		new LabelController().hideLabel(geoElement);
		geoElement.removeDependentAlgos();
		app.storeUndoInfo();
	}

	private void addLabel() {
		new LabelController().showLabel(geoElement);
		app.storeUndoInfo();
	}

	private void createSlider() {
		new CreateSlider(app.getKernel().getAlgebraProcessor(), new LabelController())
				.execute(geoElement);
	}

	private void removeSlider() {
		new RemoveSlider(app.getKernel().getAlgebraProcessor()).execute(geoElement);
	}

	private void solve() {
		Suggestion suggestion = SuggestionSolveForSymbolic.isValid(geoElement)
				? SuggestionSolveForSymbolic.get(geoElement)
				: SuggestionSolve.get(geoElement);
		if (suggestion != null) {
			suggestion.execute(geoElement);
		}
	}
}
