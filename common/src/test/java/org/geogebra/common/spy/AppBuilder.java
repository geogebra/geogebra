package org.geogebra.common.spy;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.NumberFormatAdapter;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.util.Vector;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppBuilder {

	private App app;
	private FormatFactory formatFactory;

	App getApp() {
		if (app == null) {
			app = createApp();
		}
		return app;
	}

	private App createApp() {
		mockFormatFactory();
		final App app = Mockito.mock(App.class);
		app.images = new Vector<>();
		when(app.getConfig()).then(new Answer<AppConfig>() {
			@Override
			public AppConfig answer(InvocationOnMock invocation) {
				return mock(AppConfig.class);
			}
		});
		when(app.getParserFunctions()).then(new Answer<ParserFunctions>() {
			@Override
			public ParserFunctions answer(InvocationOnMock invocation) {
				return mock(ParserFunctions.class);
			}
		});
		when(app.getLocalization()).then(new Answer<Localization>() {
			@Override
			public Localization answer(InvocationOnMock invocation) {
				return mock(Localization.class);
			}
		});
		return app;
	}

	private void mockFormatFactory() {
		PowerMockito.mockStatic(FormatFactory.class);
		FormatFactory formatFactory = getFormatFactory();
		given(FormatFactory.getPrototype()).willReturn(formatFactory);
	}

	private FormatFactory getFormatFactory() {
		if (formatFactory == null) {
			formatFactory = createFormatFactory();
		}
		return formatFactory;
	}

	private FormatFactory createFormatFactory() {
		final FormatFactory formatFactory = Mockito.mock(FormatFactory.class);
		when(formatFactory.getNumberFormat(anyInt())).then(new Answer<NumberFormatAdapter>() {
			@Override
			public NumberFormatAdapter answer(InvocationOnMock invocationOnMock) {
				return Mockito.mock(NumberFormatAdapter.class);
			}
		});
		return formatFactory;
	}

}
