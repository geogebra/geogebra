package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMI10 extends FontInfo {

	JLMI10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 557, 485, 0, 140);

		setMetrics(36, 557, 485, 0, -4);

		setMetrics(34, 456, 485, 0, 52);

		setMetrics(35, 456, 485, 0, -6);

		setMetrics(37, 1115, 751, 0, 32);

		setMetrics(38, 1432, 751, 0, 32);

	}
}
