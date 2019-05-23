package org.geogebra.web.full.main;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.test.AppMocker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class })
public class Graphing3DTest {
	private static AppW app;
	@Before
	public void rootPanel() {
		this.getClass().getClassLoader().setDefaultAssertionStatus(false);
	}

	@Test
	public void startApp() {
		app = AppMocker
				.mockApplet(new TestArticleElement("prerelease", "3d"));
	}

}
