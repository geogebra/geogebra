//CHECKSTYLE:OFF

/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

*/

package geogebra.gui.editor;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

%%

%public
%class GeoGebraLexer
%extends Lexer
%implements GeoGebraLexerConstants
%final
%unicode
%char
%type int
%switch

%{
    public int end;
    public Set<String> commands;
    public Set<String> variables;

    private Document doc;
    private Element elem;

    public GeoGebraLexer(Application app) {
        variables = new HashSet<String>();
        Iterator<GeoElement> iter = app.getKernel().getConstruction().getGeoSetLabelOrder().iterator();
        while (iter.hasNext()) {
                GeoElement g = iter.next();
                if (g.isLabelSet()) {
                        variables.add(g.getLabel());
                }
        }
        commands = new HashSet();
        commands.addAll(app.getCommandDictionary().values());
    }

    public GeoGebraLexer(Document doc, Application app) {
                this(app);
        setDocument(doc);
    }

        public void setDocument(Document doc) {
                this.doc = doc;
                this.elem = doc.getDefaultRootElement();
        }

    public void setRange(int p0, int p1) {
        this.start = p0;
        this.end = p1;
        String str = "";
        try {
                str = doc.getText(start, end - start);
        } catch (BadLocationException e) { }
        yyreset(new StringReader(str));
    }

    public int yychar() {
        return yychar;
    }

    public int scan() throws IOException {
        return yylex();
    }

    public int getKeyword(int pos, boolean strict) {
        Element line = elem.getElement(elem.getElementIndex(pos));
        int end = line.getEndOffset();
        int tok = -1;
        start = line.getStartOffset();
        int startL = start;
        int s = -1;

        try {
           yyreset(new StringReader(doc.getText(start, end - start)));
           if (!strict) {
              pos++;
           }

           while (startL < pos && s != startL) {
               s = startL;
               tok = yylex();
               startL = start + yychar + yylength();
           }

           return tok;
        } catch (Exception e) {
           return GeoGebraLexerConstants.DEFAULT;
        }
     }
%}

/* main character classes */
eol = [\r\n]+

open = "[" | "(" | "{"
close = "]" | ")" | "}"
openclose = {open} | {close}

undefined = "?" | "\ufffd" | "Undefined" | "NaN" | "und"
constantes = "%pi" | "pi" | "Pi" | "\u03c0" | "%e" | "\u212f" | "inf" | "minf" | "Infinity" | "infinity" | "\u221e" | "true" | "True" | "false" | "False" | "rad" | {undefined}

assignment = ":="
vertical_bar = "|"
not = "\u00ac"
or = "||" | "\u2228"
and = "&&" | "\u2227"
eq_bool = "==" | "\u225f"
not_eq_bool = "!=" | "\u2260"
is_element_of = "\u2208"
contains = "\u2286"
contains_strict = "\u2282"
set_diff = "\\"
less = "<"
greater = ">"
leq = "<=" | "\u2264"
geq = ">=" | "\u2265"
inequality = {less} | {greater} | {leq} | {geq}
parallel = "\u2225"
perpendicular = "\u22a5"
equal = "="
plus = "+"
minus = "-"  | "\u2013" | "\u2212"
multiply = "*" | "\u22c5"
vectorproduct = "\u2297"
divide = "/"
power = "^"
factorial = "!"
superscript_minus = "\u207b"
indexdigit = "\u2070" | [\u2074-\u2079] | "\u00b9" | "\u00b2" | "\u00b3"
powern = {superscript_minus}? {indexdigit}+
degre = "\u00b0"

operator = {assignment} | {vertical_bar} | {not} | {or} | {and} | {eq_bool} | {not_eq_bool} |
                   {is_element_of} | {contains} | {contains_strict} | {set_diff} | {inequality} |
                   {parallel} | {perpendicular} | {equal} | {plus} | {minus} | {multiply} |
                   {vectorproduct} | {divide} | {power} | {factorial} | {powern} | {degre}

decimal_point = "." | "\u066b"
digit = [\u0030-\u0039] | // Roman
        [\u0660-\u0669] | // Arabic-Indic
        [\u06f0-\u06f9] | // Extended Arabic-Indic
        [\u0966-\u096f] | // Devanagari (Hindi)
        [\u09e6-\u09ef] | // Bengali
        [\u0a66-\u0a6f] | // Gurmukhi (Punjabi)
        [\u0ae6-\u0aef] | // Gujurati
        [\u0b66-\u0b6f] | // Oryia
        [\u0be6-\u0bef] | // Tamil
        [\u0c66-\u0c6f] | // Telugu
        [\u0ce6-\u0cef] | // Kannada
        [\u0d66-\u0d6f] | // Malayalam
        [\u0e50-\u0e59] | // Thai
        [\u0ed0-\u0ed9] | // Lao
        [\u1040-\u1049] | // Myanmar (Burmese)
        [\u0f20-\u0f29] | // Tibetan
        [\u1b50-\u1b59] | // Balinese
        [\u1bb0-\u1bb9] | // Sudanese
        [\u1c40-\u1c49] | // Lepcha
        [\u1c50-\u1c59] | // Ol Chiki
        [\u17e0-\u17e9] | // Khmer
        [\u1810-\u1819] | // Mongolian
        [\ua8d0-\ua8d9]   // Saurashtra
integer = {digit}+
float = ({integer} ({decimal_point} {integer})?) | ({decimal_point} {integer}) | ({integer} {decimal_point})
efloat = {float} "E" ({plus} | {minus})? {integer}
percentage = {float} "%"

number = {integer} | {float} | {efloat} | {percentage}

letter = "$" |
         [\u0041-\u005a] |      // upper case (A-Z)
         [\u0061-\u007a] |      // lower case (a-z)
         "\u00b7"        |      // middle dot (for Catalan)
         [\u00c0-\u00d6] |      // accentuated letters
         [\u00d8-\u01bf] |      // accentuated letters
         [\u01c4-\u02a8] |      // accentuated letters
         [\u0391-\u03f3] |      // Greek
         [\u0401-\u0481] |      // Cyrillic
         [\u0490-\u04f9] |      // Cyrillic
         [\u0531-\u1ffc] |      // a lot of signs (Arabic, accentuated, ...)
         [\u3041-\u3357] |      // Asian letters
         [\u4e00-\ud7a3] |      // Asian letters
         [\uf71d-\ufa2d] |      // Asian letters
         [\ufb13-\ufdfb] |      // Armenian, Hebrew, Arabic
         [\ufe80-\ufefc] |      // Arabic
         [\uff66-\uff9d] |      // Katakana
         [\uffa1-\uffdc]
char = [\u0000-\u0021\u0023-\uffff]
string = "\"" {char}* "\""
index = "_" ({char} | ("{" [^\}]+ "}"))
//spreadsheet_label =  ("$")? [A-Za-z]+ ("$")? [0-9]+
label = {letter} ({letter} | {digit} | "'")* {index}? ({letter} | {digit})*
varfoo = [xyz]

builtin_functions = "x(" | "xcoord(" | "y(" | "ycoord(" | "y(" | "ycoord(" |
                    (("cos" | "Cos" | "sin" | "Sin" | "tan" | "Tan" | "csc" | "Csc" | "sec" | "Sec" |
                      "cot" | "Cot" | "csch" | "Csch" | "sech" | "Sech" | "coth" | "Coth" |
                      "cosh" | "Cosh" | "sinh" | "Sinh" | "tanh" | "Tanh") {powern}? "(") |
                    "acos(" | "arccos(" | "arcos(" | "ArcCos(" | "asin(" | "arcsin(" | "ArcSin(" |
                    "atan(" | "arctan(" | "ArcTan(" | "atan2(" | "arctan2(" | "ArcTan2(" |
                    "acosh(" | "arccosh(" | "arcosh(" | "ArcCosh(" | "asinh(" | "arcsinh(" | "ArcSinh(" |
                    "atanh(" | "arctanh(" | "ArcTanh(" | "exp(" | "Exp(" | "log(" | "ln(" | "Ln(" |
                    "ld(" | "lg(" | "sqrt(" | "Sqrt(" | "cbrt(" | "abs(" | "Abs(" | "sgn(" | "sign(" | "Sign(" |
                    "floor(" | "Floor(" | "ceil(" | "Ceil(" | "conjugate(" | "Conjugate(" | "arg(" | "Arg(" |
                    "round(" | "Round(" | "gamma(" | "Gamma(" | "random(" | "Exp(" | "Deriv("

functions = {label} "("
commands = {label} "["
//spreadsheet_commands = {spreadsheet_label} "("

%%

<YYINITIAL> {
  {operator}                     {
                                   return GeoGebraLexerConstants.OPERATOR;
                                 }

  {number}                       {
                                   return GeoGebraLexerConstants.NUMBER;
                                 }

  {openclose}                    {
                                   return GeoGebraLexerConstants.OPENCLOSE;
                                 }

  {constantes}                   {
                                   return GeoGebraLexerConstants.CONSTANTE;
                                 }

  {builtin_functions}            {
                                   yypushback(1);
                                   return GeoGebraLexerConstants.BUILTINFUNCTION;
                                 }

/*  {spreadsheet_commands}       {
                                   yypushback(1);
                                   return GeoGebraLexerConstants.SPREADSHEETCOMMANDS;
                                 } */

  {functions}                    {
                                   yypushback(1);
                                   return GeoGebraLexerConstants.FUNCTION;
                                 }

  {commands}                     {
                                   yypushback(1);
                                   String com = yytext();
                                   if (commands.contains(com)) {
                                      return GeoGebraLexerConstants.COMMAND;
                                   }

                                   return GeoGebraLexerConstants.UNKNOWN;
                                 }

  {string}                       {
                                   return GeoGebraLexerConstants.STRING;
                                 }

  {varfoo}                       {
                                   return GeoGebraLexerConstants.VARIABLE;
                                 }

  {label}                        {
                                   String lab = yytext();
                                   if (variables.contains(lab)) {
                                      return GeoGebraLexerConstants.VARIABLE;
                                   }

                                   return GeoGebraLexerConstants.UNKNOWN;
                                 }

/*  {spreadsheet_label}          {
                                   return GeoGebraLexerConstants.SPREADSHEET_LABEL;
                                 } */

  " "                            {
                                   return GeoGebraLexerConstants.WHITE;
                                 }

  "\t"                           {
                                   return GeoGebraLexerConstants.TAB;
                                 }

  .                              |
  {eol}                          {
                                   return GeoGebraLexerConstants.DEFAULT;
                                 }
}

<<EOF>>                          {
                                   return GeoGebraLexerConstants.EOF;
                                 }
