package org.geogebra.common.kernel.geos;

import com.himamis.retex.renderer.share.serialize.DefaultBracketsAdapter;

public class ScreenReaderBracketsAdapter extends DefaultBracketsAdapter {

	@Override
	public String subscriptContent(String sub) {
		return "_" + sub;
	}
}
