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

package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

public class TeXAtomSerializerTest {

	@Test
	public void allAtomsPrint() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		List<Class<? extends Atom>> cls = new ArrayList<>();
		findClasses(Paths.get("../../shared/renderer-base/src"), cls);
		List<String> missing = new ArrayList<>();
		for (Class<? extends Atom> clazz: cls) {
			try {
				String s = new TeXAtomSerializer(null).serialize(mock(clazz));
				if ("?".equals(s)) {
					missing.add(clazz.getSimpleName());
				}
			} catch (RuntimeException ex) {
				// mock not serializable, OK
			}
		}
		Collections.sort(missing);
		assertEquals(Arrays.asList("Atom", "CharSymbol", // abstract
				"FixedCharAtom", // only used in dummy
				"MhchemBondAtom"), // advanced
				missing);
	}

	@SuppressWarnings("unchecked")
	private void findClasses(Path path, List<Class <? extends Atom>> classNames) {
		if (Files.isDirectory(path)) {
			try (Stream<Path> list = Files.list(path)) {
				list.forEach(p -> findClasses(p, classNames));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			String cname = path.toString().split(".java")[1].substring(1)
					.replace("/", ".").replace("\\", ".");
			try {
				Class<?> clazz = Class.forName(cname);
				if (Atom.class.isAssignableFrom(clazz)) {
					classNames.add((Class<? extends Atom>) clazz);
				}
			} catch (ClassNotFoundException ce) {
				throw new RuntimeException("Problem with " + cname, ce);
			}
		}
	}
}

