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

package org.geogebra.web.full.euclidian.inline;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoStickyNote;
import org.gwtproject.dom.client.Element;
import org.jspecify.annotations.NonNull;

public final class StickyNoteControllerW extends InlineTextControllerW {
	private final GeoStickyNote stickyNote;
	private boolean adjustingFontSize = false;
	private boolean automaticFontSize;
	private static final int[] AUTO_FONT_SIZES = {120, 96, 72, 60, 48, 36, 24, 16, 12, 8, 4};

	/**
	 * @param stickyNote StickyNote geo
	 * @param view euclidian view
	 * @param parent parent div
	 */
	public StickyNoteControllerW(GeoStickyNote stickyNote, EuclidianView view, Element parent) {
		super(stickyNote, view, parent);
		this.stickyNote = stickyNote;
		automaticFontSize = stickyNote.isAutoFontSize();
	}

	@Override
	protected void applyFormat(@NonNull String key, @NonNull Object value) {
		if ("size".equals(key)) {
			disableAutoFontSize();
			formatDocument(key, value);
		} else {
			super.applyFormat(key, value);
		}
	}

	@Override
	public void setWidth(int width) {
		boolean switchedToAuto = stickyNote.isAutoFontSize() && !automaticFontSize;
		automaticFontSize = stickyNote.isAutoFontSize();
		if (!automaticFontSize) {
			disableAutoFontSize();
		}
		super.setWidth(width);
		if (switchedToAuto) {
			onEditorInput();
		}
	}

	@Override
	protected void onEditorInput() {
		if (!stickyNote.isAutoFontSize()) {
			disableAutoFontSize();
			return;
		}
		if (adjustingFontSize) {
			return;
		}

		adjustingFontSize = true;
		try {
			adjustFontSize();
		} finally {
			adjustingFontSize = false;
		}
	}

	@Override
	public void onResizeEnd(double scale) {
		Number fontSize = getFormat("size", (Number) stickyNote.defaultFontSize());
		adjustingFontSize = true;
		try {
			setHeight((int) stickyNote.getContentHeight() - 2 * stickyNote.defaultPadding());
			super.setWidth((int) stickyNote.getContentWidth() - 2 * stickyNote.defaultPadding());
			if (stickyNote.isAutoFontSize()) {
				adjustFontSize();
			} else {
				double roundedSize = Math.max(1, Math.round(fontSize.doubleValue() * scale));
				formatDocument("size", roundedSize);
			}
			saveContent();
		} finally {
			adjustingFontSize = false;
		}
	}

	@Override
	public boolean fitsAtFontSize(double fontSize) {
		return fitsAtFontSize(
				fontSize, stickyNote.getContentHeight() - 2 * stickyNote.defaultPadding());
	}

	private void adjustFontSize() {
		double availableHeight = stickyNote.getContentHeight() - 2 * stickyNote.defaultPadding();
		enableAutoFontSize(AUTO_FONT_SIZES, availableHeight);
	}
}
