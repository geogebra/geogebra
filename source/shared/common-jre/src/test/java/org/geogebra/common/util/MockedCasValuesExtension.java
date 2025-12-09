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

package org.geogebra.common.util;

import static java.util.Map.entry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.cas.MockedCasGiac;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Extension that checks for {@link MockedCasValues} and memorizes the provided values
 * using a {@link MockedCasGiac} that should be provided as a field named "mockedCasGiac"
 * either in the current class or in a superclass.
 */
public final class MockedCasValuesExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		// Check for test methods annotated with @MockedCasValues
		MockedCasValues mockedCasValuesAnnotation = context.getTestMethod().get()
				.getAnnotation(MockedCasValues.class);
		if (mockedCasValuesAnnotation == null) {
			return;
		}

		// Parse the values of @MockedCasValues
		List<Map.Entry<String, String>> inputOutputPairs =
				Arrays.stream(mockedCasValuesAnnotation.value()).map(line -> {
					String[] parts = line.split(mockedCasValuesAnnotation.delimiter());
					if (parts.length != 2) {
						throw new Error("\"" + line + "\" is not in a valid @MockedCasValues "
								+ "value format. It should include the input and the mocked output "
								+ "divided by delimiter "
								+ "\"" + mockedCasValuesAnnotation.delimiter() + "\".");
					}
					return entry(parts[0].trim(), parts[1].trim());
				}).collect(Collectors.toList());

		// Search for MockedCasGiac mockedCasGiac field and its memorize method
		Object testInstance = context.getRequiredTestInstance();
		Field mockedCasGiacField = findMockedCasGiacField(testInstance.getClass());
		mockedCasGiacField.setAccessible(true);
		Object mockedCasGiacInstance = mockedCasGiacField.get(testInstance);
		Method memorizeMethod = mockedCasGiacInstance.getClass()
				.getMethod("memorize", String.class, String.class);

		// Call MockedCasGiac::memorize on the parsed input-output pairs
		for (Map.Entry<String, String> casValue : inputOutputPairs) {
			memorizeMethod.invoke(mockedCasGiacInstance, casValue.getKey(), casValue.getValue());
		}
	}

	private Field findMockedCasGiacField(Class<?> clazz) {
		Class<?> currentClass = clazz;
		while (currentClass != null && currentClass != Object.class) {
			try {
				Field mockedCasGiacField = currentClass.getDeclaredField("mockedCasGiac");
				if (!mockedCasGiacField.getType().equals(MockedCasGiac.class)) {
					throw new Error("Field named \"mockedCasGiac\" "
							+ "required by @MockedCasValues should be a MockedCasGiac.");
				}
				return mockedCasGiacField;
			} catch (NoSuchFieldException noSuchFieldException) {
				currentClass = currentClass.getSuperclass();
			}
		}
		throw new Error("There is no field named \"mockedCasGiac\" required by @MockedCasValues.");
	}
}
