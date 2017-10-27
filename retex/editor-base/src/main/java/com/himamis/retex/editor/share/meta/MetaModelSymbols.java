package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaCharacter.OPERATOR;
import static com.himamis.retex.editor.share.meta.MetaCharacter.SYMBOL;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

class MetaModelSymbols {

    private static MetaComponent createSymbol(String name, String cas, String tex, char key, char unicode, int type) {
		return new MetaSymbol(name, cas, tex, key, unicode, type);
    }

    private static MetaComponent createOperator(String name, String cas, String tex, char key, char unicode) {
        return createSymbol(name, cas, tex, key, unicode, OPERATOR);
    }

    private static MetaComponent createOperator(String name, String cas, String tex, char unicode) {
        char key = name.length() == 1 ? name.charAt(0) : 0;

        return createOperator(name, cas, tex, key, unicode);
    }

    private static MetaComponent createOperator(String name, String cas, String tex) {
        char key = name.length() == 1 ? name.charAt(0) : 0;

        return createOperator(name, cas, tex, key, key);
    }

    private static MetaComponent createOperator(String name, String tex, char unicode) {
        return createOperator(name, name, tex, unicode);
    }

    private static MetaComponent createOperator(String name) {
        return createOperator(name, name, name);
    }

    private static MetaComponent createSymbol(String name, String cas, String tex, char unicode) {
        char key = name.length() == 1 ? name.charAt(0) : 0;

        return createSymbol(name, cas, tex, key, unicode, SYMBOL);
    }

    private static MetaComponent createSymbol(String name, String tex, char unicode) {
        return createSymbol(name, name, tex, unicode);
    }

    private static MetaComponent createSymbol(String name, String cas, String tex) {
        return createSymbol(name, cas, tex, '\0');
    }

	ListMetaGroup createOperators() {
        List<MetaComponent> operators = new ArrayList<MetaComponent>();

        operators.add(createOperator("-"));
		operators.add(createOperator(Unicode.MINUS + ""));
        operators.add(createOperator("+"));
        operators.add(createOperator("*", "\\cdot", '*'));
        operators.add(createOperator("/"));
        operators.add(createOperator("\\"));
        operators.add(createOperator("'"));
        operators.add(createOperator("!"));
        operators.add(createOperator(":"));
        operators.add(createOperator("="));
		operators.add(createOperator("<"));
		operators.add(createOperator(">"));
		operators.add(createOperator(","));
		operators.add(createOperator(";"));

        operators.add(createOperator("percent", "\\%", '%'));

		// GGB-2178 want $ to behave like a letter
		// so don't want this here
		// operators.add(createOperator("dollar", "\\$", '$'));

        operators.add(createOperator("times", "\\times", '*'));
        operators.add(createOperator("div", "\\div", '/'));
		operators.add(
				createOperator("ne", "!=", "\\ne", Unicode.NOTEQUAL));
		operators.add(createOperator("equal", "==", Unicode.QUESTEQ + "",
				Unicode.QUESTEQ));
        operators.add(createOperator("equiv", "NaN", "\\equiv", '\u2261'));

        operators.add(createOperator("neg", "!", "\\neg", '!'));
		operators
				.add(createOperator("vee", "|", "\\vee", Unicode.OR));
		operators.add(
				createOperator("wedge", "&", "\\wedge", Unicode.AND));
		operators.add(createOperator("implication", "->", "\\implies",
				Unicode.IMPLIES));

        operators.add(createOperator("<"));
        operators.add(createOperator(">"));
		operators.add(createOperator("leq", "<=", "\\leq", Unicode.LESS_EQUAL));
		operators.add(
				createOperator("geq", ">=", "\\geq", Unicode.GREATER_EQUAL));
		operators.add(createOperator("ll", "NaN", "\\ll", '\u226a'));
		operators.add(createOperator("gg", "NaN", "\\gg", '\u226b'));

		operators.add(createOperator("sim", "NaN", "\\sim", '\u223c'));
		operators.add(createOperator("approx", "NaN", "\\approx", '\u2248'));
		operators.add(createOperator("simeq", "NaN", "\\simeq", '\u2243'));
		operators.add(createOperator("propto", "NaN", "\\propto", '\u221d'));

		operators.add(createOperator("forall", "NaN", "\\forall", '\u2200'));
		operators.add(createOperator("exists", "NaN", "\\exists", '\u2203'));

		operators.add(createOperator("perpendicular", "\\perp",
				Unicode.PERPENDICULAR));
		operators.add(createOperator("\u27c2"));
		operators.add(
				createOperator("parallel", "\\parallel",
						Unicode.PARALLEL));

		operators.add(createOperator("subset", "NaN", "\\subset",
				Unicode.IS_SUBSET_OF_STRICT));
		operators.add(createOperator("supset", "NaN", "\\supset", '\u2283'));
		operators.add(createOperator("subseteq", "NaN", "\\subseteq",
				Unicode.IS_SUBSET_OF));
		operators
				.add(createOperator("supseteq", "NaN", "\\supseteq", '\u2287'));
		operators.add(createOperator("cup", "NaN", "\\cup", '\u222a'));
		operators.add(createOperator("cap", "NaN", "\\cap", '\u2229'));
		operators.add(
				createOperator("in", "NaN", "\\in", Unicode.IS_ELEMENT_OF));
		operators.add(createOperator("empty", "NaN", "\\emptyset", '\u2205'));

		operators.add(createOperator("pm", "NaN", "\\pm", Unicode.PLUSMINUS));
		operators.add(createOperator("prime", "NaN", "\\prime", '\u2032'));
		operators.add(createOperator("circ", "NaN", "\\circ", '\u2218'));
		operators.add(createOperator("partial", "NaN", "\\partial", '\u2202'));

		operators.add(
				createOperator("leftarrow", "NaN", "\\leftarrow", '\u2190'));
		operators.add(
				createOperator("rightarrow", "NaN", "\\rightarrow", '\u2192'));
		operators.add(createOperator("leftrightarrow", "NaN",
				"\\leftrightarrow", '\u2194'));
		operators.add(createOperator("notrightarrow", "NaN",
				"\\not\\rightarrow", '\u219b'));
		operators.add(createOperator("notleftrightarrow", "NaN",
				"\\not\\leftrightarrow", '\u21ae'));
		operators.add(createOperator("vectorprod", "\\times", '\u2a2f'));

		return new ListMetaGroup(operators);
    }

	ListMetaGroup createSymbols() {
        List<MetaComponent> symbols = new ArrayList<MetaComponent>();

		symbols.add(createSymbol("inf", "\\infty", Unicode.INFINITY));

        //symbols.add(createSymbol("vareps", "\\varepsilon", '\u03f5'));
        //symbols.add(createSymbol("varth", "\\vartheta", '\u03b8'));
		//symbols.add(createSymbol("varpi", "\\varpi", Unicode.varpi));
		//symbols.add(createSymbol("varrho", "\\varrho", Unicode.varrho));

		for (Greek ch : Greek.values()) {
			symbols.add(
					createSymbol(ch.name(), "\\" + ch.getLaTeX(), ch.unicode));
		}

        symbols.add(createSymbol("varsigma", "\\varsigma", '\u03c2'));
        symbols.add(createSymbol("varphi", "\\varphi", '\u03c6'));

		// symbols.add(createSymbol("alpha", "\\alpha", Unicode.alpha));
		// symbols.add(createSymbol("beta", "\\beta", Unicode.beta));
		// symbols.add(createSymbol("gamma", "\\gamma", Unicode.gamma));
		// symbols.add(createSymbol("delta", "\\delta", Unicode.delta));
		// symbols.add(createSymbol("epsilon", "\\epsilon", Unicode.epsilon));
		// symbols.add(createSymbol("zeta", "\\zeta", Unicode.zeta));
		// symbols.add(createSymbol("eta", "\\eta", Unicode.eta));
		// symbols.add(createSymbol("theta", "\\theta", Unicode.theta));
		// symbols.add(createSymbol("iota", "\\iota", Unicode.iota));
		// symbols.add(createSymbol("kappa", "\\kappa", Unicode.kappa));
		// symbols.add(createSymbol("lambda", "\\lambda", Unicode.lambda));
		// symbols.add(createSymbol("mu", "\\mu", Unicode.mu));
		// symbols.add(createSymbol("nu", "\\nu", Unicode.nu));
		// symbols.add(createSymbol("xi", "\\xi", Unicode.xi));
		// symbols.add(createSymbol("omicron", "\\omicron", Unicode.omicron));
		// symbols.add(createSymbol("pi", "\\pi", Unicode.pi));
		// symbols.add(createSymbol("rho", "\\rho", Unicode.rho));
		// symbols.add(createSymbol("sigma", "\\sigma", Unicode.sigma));
		// symbols.add(createSymbol("tau", "\\tau", Unicode.tau));
		// symbols.add(createSymbol("upsilon", "\\upsilon", Unicode.upsilon));
		// symbols.add(createSymbol("phi", "\\phi", Unicode.phi));
		// symbols.add(createSymbol("chi", "\\chi", Unicode.chi));
		// symbols.add(createSymbol("psi", "\\psi", Unicode.psi));
		// symbols.add(createSymbol("omega", "\\omega", Unicode.omega));

		// symbols.add(createSymbol("Gamma", "\\Gamma", Unicode.Gamma));
		// symbols.add(createSymbol("Delta", "\\Delta", Unicode.Delta));
		// symbols.add(createSymbol("Theta", "\\Theta", Unicode.Theta));
		// symbols.add(createSymbol("Lambda", "\\Lambda", Unicode.Lambda));
		// symbols.add(createSymbol("Xi", "\\Xi", Unicode.Xi));
		// symbols.add(createSymbol("Pi", "\\Pi", Unicode.Pi));
		// symbols.add(createSymbol("Sigma", "\\Sigma", Unicode.Sigma));
		// symbols.add(createSymbol("Upsilon", "\\Upsilon", Unicode.Upsilon));
		// symbols.add(createSymbol("Phi", "\\Phi", Unicode.Phi));
		// symbols.add(createSymbol("Psi", "\\Psi", Unicode.Psi));
		// symbols.add(createSymbol("Omega", "\\Omega", Unicode.Omega));

        symbols.add(createSymbol("nabla", "\\nabla", '\u2207'));
        symbols.add(createSymbol("hbar", "\\hbar", '\u0127'));
        symbols.add(createSymbol("ddagger", "\\ddagger", '\u2021'));
        symbols.add(createSymbol("paragraph", "paragraph", "\\paragraph"));

        symbols.add(createSymbol("otimes", "\\otimes", '\u2297'));
		symbols.add(createSymbol("degree", "\\degree", Unicode.DEGREE_CHAR));
        symbols.add(createSymbol("quotes", "\"", '"'));

		return new ListMetaGroup(symbols);
    }
}
