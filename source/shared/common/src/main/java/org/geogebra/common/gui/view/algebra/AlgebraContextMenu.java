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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.gui.view.algebra;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.contextmenu.AlgebraContextMenuItem;
import org.geogebra.common.contextmenu.ContextMenuActionHandler;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.CreateSlider;
import org.geogebra.common.gui.view.algebra.contextmenu.impl.RemoveSlider;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.scientific.LabelController;

public final class AlgebraContextMenu {

	/**
	 * Provides context menu action handlers for all AV context menu items, except
	 * <ul>
	 *     <li>{@link AlgebraContextMenuItem#DuplicateInput}</li>
	 *     <li>{@link AlgebraContextMenuItem#DuplicateOutput}</li>
	 *     <li>{@link AlgebraContextMenuItem#Settings}</li>
	 *     <li>{@link AlgebraContextMenuItem#CreateTableValues}</li>
	 * </ul>
	 * because those require access to platform-specific parts of the app.
	 * @param app The current app.
	 * @return A handler for context menu actions.
	 */
	public static @Nonnull ContextMenuActionHandler<AlgebraContextMenuItem> makeActionHandler(
			App app) {
		return (geo, selectedItem) -> {
			if (geo != null) {
				switch (selectedItem) {
				case AddLabel:
					addLabel(geo);
					break;
				case RemoveLabel:
					removeLabel(geo, app);
					break;
				case CreateSlider:
					createSlider(geo, app);
					break;
				case RemoveSlider:
					removeSlider(geo, app);
					break;
				case Delete:
					deleteItem(geo, app);
					break;
				case SpecialPoints:
					addSpecialPoints(geo);
					break;
				case Solve:
					addSolution(geo);
					break;
				case Statistics:
					addStatistics(geo);
					break;
				default:
					break;
				}
			}
		};
	}

	private static void deleteItem(@Nonnull GeoElement geo, @Nonnull App app) {
		geo.remove();
		app.storeUndoInfo();
	}

	private static void addStatistics(@Nonnull GeoElement geo) {
		SuggestionStatistics.get(geo).execute(geo);
	}

	private static void addSpecialPoints(@Nonnull GeoElement geo) {
		SuggestionIntersectExtremum.get(geo).execute(geo);
	}

	private static void addSolution(@Nonnull GeoElement geo) {
		Suggestion suggestion;
		if (SuggestionSolveForSymbolic.isValid(geo)) {
			suggestion = SuggestionSolveForSymbolic.get(geo);
		} else {
			suggestion = SuggestionSolve.get(geo);
		}
		if (suggestion != null) {
			suggestion.execute(geo);
		}
	}

	private static void createSlider(@Nonnull GeoElement geo, @Nonnull App app) {
		new CreateSlider(app.getKernel().getAlgebraProcessor(), new LabelController())
				.execute(geo);
	}

	private static void removeSlider(@Nonnull GeoElement geo, @Nonnull App app) {
		new RemoveSlider(app.getKernel().getAlgebraProcessor())
				.execute(geo);
	}

	private static void addLabel(@Nonnull GeoElement geo) {
		new LabelController().showLabel(geo);
	}

	private static void removeLabel(@Nonnull GeoElement geo, @Nonnull App app) {
		new LabelController().hideLabel(geo);
		geo.removeDependentAlgos();
		app.storeUndoInfo();
	}

	/**
	 * Provides the LaTeX source to be inserted into the AV when the
	 * {@link AlgebraContextMenuItem#DuplicateInput} item is selected.
	 * @param geo The selected {@code GeoElement}
	 * @return The LaTeX source to be inserted into the AV
	 */
	public static @CheckForNull String getDuplicateInputLaTeX(@Nonnull GeoElement geo) {
		return AlgebraItem.getDuplicateFormulaForGeoElement(geo);
	}

	/**
	 * Provides the LaTeX source to be inserted into the AV when the
	 * {@link AlgebraContextMenuItem#DuplicateOutput} item is selected.
	 * @param geo The selected {@code GeoElement}
	 * @param app The current app.
	 * @return to be inserted into the AV
	 */
	public static @CheckForNull String getDuplicateOutputLaTeX(@Nonnull GeoElement geo,
			@Nonnull App app) {
		return app.getGeoElementValueConverter().toOutputValueString(geo,
				StringTemplate.algebraTemplate);
	}
}
