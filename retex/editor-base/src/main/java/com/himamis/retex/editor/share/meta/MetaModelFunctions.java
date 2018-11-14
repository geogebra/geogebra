package com.himamis.retex.editor.share.meta;

import java.util.ArrayList;
import java.util.List;

class MetaModelFunctions {

	private static MetaFunction createFunction(Tag name, String tex,
			char key, MetaParameter[] parameters) {
		return new MetaFunction(name, tex, key, parameters);
    }

	private static MetaFunction createFunction(Tag name, String tex,
			MetaParameter[] parameters) {
		char key = name.getKey();
		return createFunction(name, tex, key, parameters);
    }

	private static MetaFunction createFunction(Tag name) {
        return createFunction(name, new MetaParameter[]{createParameter("x", 0)});
    }

	private static MetaFunction createFunction(Tag name, String tex) {
		return createFunction(name, tex,
				new MetaParameter[] { createParameter("x", 0) });
    }

	private static MetaFunction createFunctionInitial(Tag name, String tex,
			int initial, MetaParameter[] parameters) {
		MetaFunction metaFunction = createFunction(name, tex, parameters);
        metaFunction.setInitialIndex(initial);
        return metaFunction;
    }

	private static MetaFunction createFunctionInsert(Tag name, String tex,
			int insert, MetaParameter[] parameters) {
		MetaFunction metaFunction = createFunction(name, tex, parameters);
        metaFunction.setInsertIndex(insert);
        return metaFunction;
    }

	private static MetaFunction createFunction(Tag name,
			MetaParameter[] parameters) {
		return createFunction(name, name.toString().toLowerCase(), parameters);
    }

	private static MetaFunction createFunctionParams(Tag name,
			String... parameterNames) {
        MetaParameter[] parameters = new MetaParameter[parameterNames.length];
        for (int i = 0; i < parameterNames.length; i++) {
            parameters[i] = createParameter(parameterNames[i], i);
        }
        return createFunction(name, parameters);
    }

    private static MetaParameter createParameter(String name, int order) {
        return new MetaParameter(name, order);
    }

    private static MetaParameter createParameterUp(String name, int order, int up) {
        MetaParameter parameter = createParameter(name, order);
        parameter.setUpIndex(up);
        return parameter;
    }

    private static MetaParameter createParameterDown(String name, int order, int down) {
        MetaParameter parameter = createParameter(name, order);
        parameter.setDownIndex(down);
        return parameter;
    }

	ListMetaGroup createGeneralFunctionsGroup() {
		List<MetaComponent> functions = new ArrayList<>();

		functions.add(createFunctionParams(Tag.SUBSCRIPT, "i"));
		functions.add(createFunctionParams(Tag.SUPERSCRIPT, "n"));

		functions.add(createFunctionInitial(Tag.FRAC, "\\frac", 1,
				new MetaParameter[] {
                createParameterDown("x", 0, 1),
                createParameterUp("y", 1, 0)
        }));

		functions.add(createFunction(Tag.SQRT, "\\sqrt", new MetaParameter[] {
                createParameter("x", 0)
        }));

		functions.add(
				createFunctionInsert(Tag.NROOT, "\\sqrt", 1,
						new MetaParameter[] {
                createParameterDown("n", 0, 0),
                createParameterUp("x", 1, 1)
        }));

		functions.add(createFunctionInsert(Tag.LOG, "log", 1,
				new MetaParameter[] { createParameter("n", 1),
						createParameter("x", 0) }));

		functions.add(
				createFunctionInsert(Tag.SUM, "\\sum", 3, new MetaParameter[] {
                createParameterUp("v", 0, 2),
                createParameterUp("fm", 1, 2),
                createParameterDown("to", 2, 0),
                createParameter("x", 3)
        }));

		functions.add(createFunctionInsert(Tag.PROD, "\\prod", 3,
				new MetaParameter[] {
                createParameterUp("v", 0, 2),
                createParameterUp("fm", 1, 2),
                createParameterDown("to", 2, 0),
                createParameter("x", 3)
        }));

		functions.add(
				createFunctionInsert(Tag.INT, "\\int", 2,
						new MetaParameter[] {
                createParameterUp("fm", 0, 1),
                createParameterDown("to", 1, 0),
                createParameter("x", 2),
                createParameter("v", 3)
        }));

		functions
				.add(createFunctionInsert(Tag.LIM, "\\lim", 2,
						new MetaParameter[] {
                createParameter("v", 0),
                createParameter("to", 1),
                createParameter("x", 2)
        }));

		functions.add(createFunction(Tag.ABS));
		functions.add(createFunction(Tag.FLOOR));
		functions.add(createFunction(Tag.CEIL));

		return new ListMetaGroup(functions);
    }
}
