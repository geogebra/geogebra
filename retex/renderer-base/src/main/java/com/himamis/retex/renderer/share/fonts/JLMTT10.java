package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

final class JLMTT10 extends FontInfo {

	JLMTT10(final String ttfPath) {
		super(0, ttfPath, 0, 333, 1000, 0);
	}

	@Override
	protected final void initMetrics() {
		setMetrics(33, 516, 441);

		setMetrics(36, 516, 441);

		setMetrics(34, 516, 441);

		setMetrics(35, 516, 441);

		setMetrics(37, 516, 695);

		setMetrics(38, 516, 695);

	}
}
