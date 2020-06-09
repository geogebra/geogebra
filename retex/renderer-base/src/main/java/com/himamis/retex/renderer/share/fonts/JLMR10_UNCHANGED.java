package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMR10_UNCHANGED extends FontInfo {

	JLMR10_UNCHANGED(final String ttfPath) {
		super(0, ttfPath, 431, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(126, 1389, 1900);

		setMetrics(127, 1389, 800);

	}
}
