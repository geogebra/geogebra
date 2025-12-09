/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;
import org.junit.Test;

public class AppConfigTest {

	@Test
	public void serializeGraphing() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		AppConfigGraphing obj = new AppConfigGraphing();
		obj.getCommandFilter();
		obj.getExpressionFilter();
		os.writeObject(obj);
		os.close();
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		Object after = is.readObject();
		is.close();
		assertThat(after, instanceOf(AppConfigGraphing.class));
		assertThat(((AppConfig) after).getEquationBehaviour(),
				instanceOf(DefaultEquationBehaviour.class));
	}
}
