package org.geogebra.cloud;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.media.EmbedURLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmbedCheckerTest {

	private static EmbedURLChecker checker;

	@BeforeClass
	public static void setup() {
		UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		checker = new EmbedURLChecker(
				"http://notes.dlb-dev01.alp-dlg.net/notes/api");
	}

	@Test
	public void echeck() {
		Assume.assumeNotNull(System.getProperty("marvl.auth.basic"));
		TestAsyncOperation<URLStatus> check = new TestAsyncOperation<>();
		checker.check("https://news.orf.at", check);
		Assert.assertEquals(check.await(5).getErrorKey(), null);
		check = new TestAsyncOperation<>();
		checker.check("https://edition.cnn.com", check);
		Assert.assertEquals(check.await(5).getErrorKey(), "FrameLoadError");
	}
}
