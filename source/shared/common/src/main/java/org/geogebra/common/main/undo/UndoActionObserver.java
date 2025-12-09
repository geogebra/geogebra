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

package org.geogebra.common.main.undo;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.properties.PropertyValueObserver;
import org.geogebra.common.properties.ValuedProperty;

public class UndoActionObserver
		implements PropertyValueObserver<Object> {

	private final List<GeoElement> styleableGeos;
	private final UndoManager undoManager;
	private final List<GeoInline> inlines;
	private UpdateStyleActionStore store;
	private UpdateContentActionStore contentStore;
	private boolean batchMode;

	/**
	 * @param geos selected elements
	 * @param type undo action type
	 */
	public UndoActionObserver(List<GeoElement> geos, UndoActionType type) {
		this.undoManager = geos.get(0).getConstruction().getUndoManager();
		Predicate<GeoElement> inlineFilter;
		if (type == UndoActionType.STYLE_OR_TABLE_CONTENT) {
			inlineFilter = geo -> geo instanceof GeoInlineTable;
		} else if (type == UndoActionType.STYLE_OR_CONTENT) {
			inlineFilter = geo -> geo instanceof HasTextFormatter;
		} else {
			inlineFilter = geo -> false;
		}
		this.styleableGeos = skipTextObjects(geos, inlineFilter);
		this.inlines = keepOnlyTextObjects(geos, inlineFilter);
	}

	private static List<GeoElement> skipTextObjects(List<GeoElement> geos,
			Predicate<GeoElement> filter) {
		return geos.stream().filter(geo -> !filter.test(geo))
				.collect(Collectors.toList());
	}

	private static List<GeoInline> keepOnlyTextObjects(List<GeoElement> geos,
			Predicate<GeoElement> filter) {
		return geos.stream()
				.filter(filter)
				.map(geo -> (GeoInline) geo).collect(Collectors.toList());
	}

	@Override
	public void onBeginSetValue(ValuedProperty<Object> property) {
		batchMode = true;
	}

	@Override
	public void onWillSetValue(ValuedProperty<Object> property) {
		if (store == null && !styleableGeos.isEmpty()) {
			store = new UpdateStyleActionStore(styleableGeos, undoManager);
		}
		if (contentStore == null && !inlines.isEmpty()) {
			contentStore = new UpdateContentActionStore(inlines);
		}
	}

	@Override
	public void onEndSetValue(ValuedProperty<Object> property) {
		batchMode = false;
		storeUndoAndReset();
	}

	private void storeUndoAndReset() {
		if (store != null && store.needUndo()) {
			store.storeUndo();
		}
		if (contentStore != null) {
			contentStore.storeUndo();
		}
		store = null;
	}

	@Override
	public void onDidSetValue(ValuedProperty<Object> property) {
		if (batchMode) {
			return;
		}
		storeUndoAndReset();
	}
}
