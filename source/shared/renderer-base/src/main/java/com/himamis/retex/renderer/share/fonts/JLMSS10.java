package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMSS10 extends FontInfo {

	JLMSS10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 662, 448);

		setMetrics(36, 662, 448);

		setMetrics(34, 441, 448);

		setMetrics(35, 441, 448);

		setMetrics(37, 1159, 752);

		setMetrics(38, 1533, 752);

	}
}
