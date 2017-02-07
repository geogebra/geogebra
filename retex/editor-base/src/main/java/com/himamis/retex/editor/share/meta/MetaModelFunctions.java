package com.himamis.retex.editor.share.meta;

import java.util.ArrayList;
import java.util.List;

class MetaModelFunctions {

    private static MetaFunction createFunction(String name, String cas, String tex, char key, MetaParameter[] parameters) {
        return new MetaFunction(name, cas, tex, key, parameters);
    }

    private static MetaFunction createFunction(String name, String cas, String tex, MetaParameter[] parameters) {
        char key = name.length() == 1 ? name.charAt(0) : 0;
        return createFunction(name, cas, tex, key, parameters);
    }

    private static MetaFunction createFunction(String name) {
        return createFunction(name, new MetaParameter[]{createParameter("x", 0)});
    }

    private static MetaFunction createFunction(String name, String tex) {
        return createFunction(name, name, tex, new MetaParameter[]{createParameter("x", 0)});
    }

    private static MetaFunction createFunction(String name, String cas, String tex) {
        return createFunction(name, cas, tex, new MetaParameter[]{createParameter("x", 0)});
    }

    private static MetaFunction createFunctionInitial(String name, String cas, String tex, int initial, MetaParameter[] parameters) {
        MetaFunction metaFunction = createFunction(name, cas, tex, parameters);
        metaFunction.setInitialIndex(initial);
        return metaFunction;
    }

    private static MetaFunction createFunctionInsert(String name, String cas, String tex, int insert, MetaParameter[] parameters) {
        MetaFunction metaFunction = createFunction(name, cas, tex, parameters);
        metaFunction.setInsertIndex(insert);
        return metaFunction;
    }

    private static MetaFunction createFunction(String name, MetaParameter[] parameters) {
        return createFunction(name, name, name, parameters);
    }

    private static MetaFunction createFunctionParams(String name, String... parameterNames) {
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

    MetaGroup createGeneralFunctionsGroup() {
        List<MetaComponent> functions = new ArrayList<MetaComponent>();

        functions.add(createFunctionParams("_", "i"));
        functions.add(createFunctionParams("^", "n"));

        functions.add(createFunctionInitial("frac", "/", "\\frac", 1, new MetaParameter[]{
                createParameterDown("x", 0, 1),
                createParameterUp("y", 1, 0)
        }));

        functions.add(createFunction("sqrt", "sqrt", "\\sqrt", new MetaParameter[]{
                createParameter("x", 0)
        }));

        functions.add(createFunctionInsert("nroot", "nroot", "\\sqrt", 1, new MetaParameter[]{
                createParameterDown("n", 0, 0),
                createParameterUp("x", 1, 1)
        }));

        functions.add(createFunctionInsert("sum", "sum", "\\sum", 3, new MetaParameter[]{
                createParameterUp("v", 0, 2),
                createParameterUp("fm", 1, 2),
                createParameterDown("to", 2, 0),
                createParameter("x", 3)
        }));

        functions.add(createFunctionInsert("prod", "prod", "\\prod", 3, new MetaParameter[]{
                createParameterUp("v", 0, 2),
                createParameterUp("fm", 1, 2),
                createParameterDown("to", 2, 0),
                createParameter("x", 3)
        }));

        functions.add(createFunction("nint", "NaN", "\\int", new MetaParameter[]{
                createParameter("x", 0),
                createParameter("v", 1)
        }));

        functions.add(createFunctionInsert("int", "quad", "\\int", 2, new MetaParameter[]{
                createParameterUp("fm", 0, 1),
                createParameterDown("to", 1, 0),
                createParameter("x", 2),
                createParameter("v", 3)
        }));

        functions.add(createFunctionInsert("lim", "NaN", "lim", 2, new MetaParameter[]{
                createParameter("v", 0),
                createParameter("to", 1),
                createParameter("x", 2)
        }));

        functions.add(createFunctionInsert("function", "function", "function", 2, new MetaParameter[]{
                createParameter("name", 0),
                createParameter("v", 1),
                createParameter("x", 2)
        }));

        functions.add(createFunction("abs"));

        return new ListMetaGroup(MetaModel.FUNCTIONS, MetaModel.GENERAL, functions);
    }

    MetaGroup createFunctions() {
        List<MetaComponent> functions = new ArrayList<MetaComponent>();

        functions.add(createFunction("sin"));
        functions.add(createFunction("cos"));
        functions.add(createFunction("tan"));

        functions.add(createFunction("asin", "sin^{-1}"));
        functions.add(createFunction("acos", "cos^{-1}"));
        functions.add(createFunction("atan", "tan^{-1}"));

        functions.add(createFunction("arcsin", "asin", "sin^{-1}"));
        functions.add(createFunction("arccos", "acos", "cos^{-1}"));
        functions.add(createFunction("arctan", "atan", "tan^{-1}"));

        functions.add(createFunction("sinh"));
        functions.add(createFunction("cosh"));
        functions.add(createFunction("tanh"));

        functions.add(createFunction("asinh", "sinh^{-1}"));
        functions.add(createFunction("acosh", "cosh^{-1}"));
        functions.add(createFunction("atanh", "tanh^{-1}"));

        functions.add(createFunction("ln", "log", "ln"));
        functions.add(createFunction("log10", "log10", "log_{10}"));
        functions.add(createFunction("log"));
        functions.add(createFunction("exp"));

        // Statistics

        functions.add(createFunction("corrcoef", "x", "y"));

        functions.add(createFunction("cov", "x", "y"));

        functions.add(createFunction("median", "a"));
        functions.add(createFunction("mean", "a"));
        functions.add(createFunction("std", "a"));
        functions.add(createFunction("var", "a"));
        functions.add(createFunction("cov", "a"));

        // Miscellaneous Functions

        functions.add(createFunction("mod", "n", "m"));

        functions.add(createFunction("floor"));
        functions.add(createFunction("ceil"));
        functions.add(createFunction("sign"));

        functions.add(createFunctionParams("gcd", "x", "y"));
        functions.add(createFunctionParams("lcm", "x", "y"));

        functions.add(createFunction("rand", new MetaParameter[0]));
        functions.add(createFunction("random", new MetaParameter[0]));

        // Matrix operations

        functions.add(createFunctionParams("eye", "n", "m"));
        functions.add(createFunctionParams("transpose", "a"));
        functions.add(createFunctionParams("inverse", "a"));
        functions.add(createFunctionParams("det", "a"));
        functions.add(createFunctionParams("min", "a"));
        functions.add(createFunctionParams("max", "a"));
        functions.add(createFunctionParams("rows", "a"));
        functions.add(createFunctionParams("columns", "a"));

        return new ListMetaGroup(MetaModel.FUNCTIONS, MetaModel.FUNCTIONS, functions);
    }
}
