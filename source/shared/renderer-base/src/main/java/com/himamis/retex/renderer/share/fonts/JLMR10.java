package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMR10 extends FontInfo {

	JLMR10(final String ttfPath) {
		super(0, ttfPath, 431, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 552, 485);

		setMetrics(36, 552, 485);

		setMetrics(34, 441, 485);

		setMetrics(35, 441, 485);

		setMetrics(37, 1159, 751);

		setMetrics(38, 1501, 751);

	}
}
