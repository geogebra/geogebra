package org.geogebra.web.editor;

import org.geogebra.common.util.AbstractSyntaxAdapter;

public class EditorSyntaxAdapter extends AbstractSyntaxAdapter {

	@Override
	public boolean isFunction(String casName) {
		return false;
	}

	@Override
	public boolean supportsMixedNumbers() {
		return true;
	}
}
