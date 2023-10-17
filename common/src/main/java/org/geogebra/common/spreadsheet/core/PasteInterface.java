package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

import com.himamis.retex.renderer.share.Row;

public interface PasteInterface {
void pasteInternal(TabularData tabularData, HasTabularValues buffer, TabularRange destination);
}
