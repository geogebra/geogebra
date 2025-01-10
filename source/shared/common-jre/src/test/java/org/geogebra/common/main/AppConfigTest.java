package org.geogebra.common.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.Test;

public class AppConfigTest {

	@Test
	public void serializeGraphing() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		AppConfigGraphing obj = new AppConfigGraphing();
		obj.getCommandFilter();
		os.writeObject(obj);
		os.close();
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Object after = is.readObject();
		is.close();
		assertThat(after, instanceOf(AppConfigGraphing.class));
	}
}
