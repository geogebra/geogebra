package org.geogebra.web.util.file;
import static org.hamcrest.core.IsEqual.equalTo;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class FileIOTest {

	@Test
	public void load() {
		String pathString = "src/test/java/org/geogebra/web/util/file/helloWorld.txt";
		String fileContent = FileIO.load(pathString);
		MatcherAssert.assertThat(fileContent, equalTo("Hello World!"));
	}
}