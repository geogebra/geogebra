package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaCharacter.OPERATOR;
import static com.himamis.retex.editor.share.meta.MetaCharacter.SYMBOL;

import java.util.ArrayList;
import java.util.List;

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

    MetaGroup createOperators() {
        List<MetaComponent> operators = new ArrayList<MetaComponent>();

        operators.add(createOperator("-"));
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
        operators.add(createOperator("dollar", "\\$", '$'));
        operators.add(createOperator("times", "\\times", '*'));
        operators.add(createOperator("div", "\\div", '/'));
        operators.add(createOperator("ne", "!=", "\\ne", '\u2260'));
        operators.add(createOperator("equal", "==", "\u225f", '\u225f'));
        operators.add(createOperator("equiv", "NaN", "\\equiv", '\u2261'));

        operators.add(createOperator("neg", "!", "\\neg", '!'));
        operators.add(createOperator("vee", "|", "\\vee", '\u2228'));
        operators.add(createOperator("wedge", "&", "\\wedge", '\u2227'));
        operators.add(createOperator("implication", "->", "\\implies", '\u21D2'));

        operators.add(createOperator("<"));
        operators.add(createOperator(">"));
        operators.add(createOperator("leq", "<=", "\\leq", '\u2264'));
        operators.add(createOperator("geq", ">=", "\\leq", '\u2264'));
        operators.add(createOperator("ll", "NaN", "\\ll", '\u226a'));
        operators.add(createOperator("gg", "NaN", "\\gg", '\u226b'));

        operators.add(createOperator("sim", "NaN", "\\sim", '\u223c'));
        operators.add(createOperator("approx", "NaN", "\\approx", '\u2248'));
        operators.add(createOperator("simeq", "NaN", "\\simeq", '\u2243'));
        operators.add(createOperator("propto", "NaN", "\\propto", '\u221d'));

        operators.add(createOperator("forall", "NaN", "\\forall", '\u2200'));
        operators.add(createOperator("exists", "NaN", "\\exists", '\u2203'));

        operators.add(createOperator("perpendicular", "\\perp", '\u27c2'));
        operators.add(createOperator("parallel", "\\parallel", '\u2225'));

        operators.add(createOperator("subset", "NaN", "\\subset", '\u2282'));
        operators.add(createOperator("supset", "NaN", "\\supset", '\u2283'));
        operators.add(createOperator("subseteq", "NaN", "\\subseteq", '\u2286'));
        operators.add(createOperator("supseteq", "NaN", "\\supseteq", '\u2287'));
        operators.add(createOperator("cup", "NaN", "\\cup", '\u222a'));
        operators.add(createOperator("cap", "NaN", "\\cap", '\u2229'));
        operators.add(createOperator("in", "NaN", "\\in", '\u2208'));
        operators.add(createOperator("empty", "NaN", "\\emptyset", '\u2205'));

        operators.add(createOperator("pm", "NaN", "\\pm", '\u00b1'));
        operators.add(createOperator("prime", "NaN", "\\prime", '\u2032'));
        operators.add(createOperator("circ", "NaN", "\\circ", '\u2218'));
        operators.add(createOperator("partial", "NaN", "\\partial", '\u2202'));

        operators.add(createOperator("leftarrow", "NaN", "\\leftarrow", '\u2190'));
        operators.add(createOperator("rightarrow", "NaN", "\\rightarrow", '\u2192'));
        operators.add(createOperator("leftrightarrow", "NaN", "\\leftrightarrow", '\u2194'));
        operators.add(createOperator("notrightarrow", "NaN", "\\not\\rightarrow", '\u219b'));
        operators.add(createOperator("notleftrightarrow", "NaN", "\\not\\leftrightarrow", '\u21ae'));
        operators.add(createOperator("vectorprod", "\\times", '\u2a2f'));

        return new ListMetaGroup(MetaModel.OPERATORS, MetaModel.OPERATORS, operators);
    }

    MetaGroup createSymbols() {
        List<MetaComponent> symbols = new ArrayList<MetaComponent>();

        symbols.add(createSymbol("inf", "\\infty", '\u221e'));

        symbols.add(createSymbol("alpha", "\\alpha", '\u03b1'));
        symbols.add(createSymbol("beta", "\\beta", '\u03b2'));
        symbols.add(createSymbol("gamma", "\\gamma", '\u03b3'));
        symbols.add(createSymbol("delta", "\\delta", '\u03b4'));
        symbols.add(createSymbol("epsilon", "\\epsilon", '\u03b5'));
        symbols.add(createSymbol("vareps", "\\varepsilon", '\u03f5'));
        symbols.add(createSymbol("zeta", "\\zeta", '\u03b6'));
        symbols.add(createSymbol("eta", "\\eta", '\u03b7'));
        symbols.add(createSymbol("theta", "\\theta", '\u03b8'));
        symbols.add(createSymbol("varth", "\\vartheta", '\u03b8'));
        symbols.add(createSymbol("iota", "\\iota", '\u03b9'));
        symbols.add(createSymbol("kappa", "\\kappa", '\u03ba'));
        symbols.add(createSymbol("lambda", "\\lambda", '\u03bb'));
        symbols.add(createSymbol("mu", "\\mu", '\u03bc'));
        symbols.add(createSymbol("nu", "\\nu", '\u03bd'));
        symbols.add(createSymbol("xi", "\\xi", '\u03be'));
        symbols.add(createSymbol("omicron", "\\omicron", '\u03bf'));
        symbols.add(createSymbol("pi", "\\pi", '\u03c0'));
        symbols.add(createSymbol("varpi", "\\varpi", '\u03c0'));
        symbols.add(createSymbol("rho", "\\rho", '\u03c1'));
        symbols.add(createSymbol("varrho", "\\varrho", '\u03c1'));
        symbols.add(createSymbol("sigma", "\\sigma", '\u03c3'));
        symbols.add(createSymbol("varsigma", "\\varsigma", '\u03c2'));
        symbols.add(createSymbol("tau", "\\tau", '\u03c4'));
        symbols.add(createSymbol("upsilon", "\\upsilon", '\u03c5'));
        symbols.add(createSymbol("phi", "\\phi", '\u03d5'));
        symbols.add(createSymbol("varphi", "\\varphi", '\u03c6'));
        symbols.add(createSymbol("chi", "\\chi", '\u03c7'));
        symbols.add(createSymbol("psi", "\\psi", '\u03c8'));
        symbols.add(createSymbol("omega", "\\omega", '\u03c9'));

        symbols.add(createSymbol("Gamma", "\\Gamma", '\u0393'));
        symbols.add(createSymbol("Delta", "\\Delta", '\u0394'));
        symbols.add(createSymbol("Theta", "\\Theta", '\u0398'));
        symbols.add(createSymbol("Lambda", "\\Lambda", '\u039b'));
        symbols.add(createSymbol("Xi", "\\Xi", '\u039e'));
        symbols.add(createSymbol("Pi", "\\Pi", '\u03a0'));
        symbols.add(createSymbol("Sigma", "\\Sigma", '\u03a3'));
        symbols.add(createSymbol("Upsilon", "\\Upsilon", '\u03a5'));
        symbols.add(createSymbol("Phi", "\\Phi", '\u03a6'));
        symbols.add(createSymbol("Psi", "\\Psi", '\u03a8'));
        symbols.add(createSymbol("Omega", "\\Omega", '\u03a9'));

        symbols.add(createSymbol("nabla", "\\nabla", '\u2207'));
        symbols.add(createSymbol("hbar", "\\hbar", '\u0127'));
        symbols.add(createSymbol("ddagger", "\\ddagger", '\u2021'));
        symbols.add(createSymbol("paragraph", "paragraph", "\\paragraph"));

        symbols.add(createSymbol("otimes", "\\otimes", '\u2297'));
        symbols.add(createSymbol("degree", "\\degree", '\u00b0'));
        symbols.add(createSymbol("quotes", "\"", '"'));

        return new ListMetaGroup(MetaModel.SYMBOLS, MetaModel.SYMBOLS, symbols);
    }
}
