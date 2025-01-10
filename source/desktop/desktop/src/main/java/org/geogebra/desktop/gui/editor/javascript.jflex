//CHECKSTYLE:OFF

/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

*/

package geogebra.gui.editor;

import java.io.IOException;
import java.io.StringReader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;

%%

%public
%class JavascriptLexer
%extends Lexer
%implements JavascriptLexerConstants
%final
%unicode
%char
%type int
%switch

%{
    public int end;

    private JavascriptEditorKit.JavascriptDocument doc;
    private Element elem;

    public JavascriptLexer() { }

    public JavascriptLexer(Document doc) {
                this();
                setDocument(doc);
    }

    public void setDocument(Document doc) {
        this.doc = (JavascriptEditorKit.JavascriptDocument) doc;
        this.elem = doc.getDefaultRootElement();
    }

    public void setRange(int p0, int p1) {
        start = p0;
        end = p1;
        String str = "";
        int line = elem.getElementIndex(p0);
        try {
                str = doc.getText(p0, p1 - p0);
        } catch (BadLocationException e) { }
        yyreset(new StringReader(str));
        if (doc.isMultiLineCommented(line - 1)) {
                yybegin(MULTILINECOMMENTS);
        }
    }

    public int yychar() {
        return yychar;
    }

    public int scan() throws IOException {
        return yylex();
    }

    public int getKeyword(int pos, boolean strict) {
        int index = elem.getElementIndex(pos);
        Element line = elem.getElement(index);
        int end = line.getEndOffset();
        int tok = -1;
        start = line.getStartOffset();
        int startL = start;
        int s = -1;

        try {
           yyreset(new StringReader(doc.getText(start, end - start)));
           if (doc.isMultiLineCommented(index - 1)) {
                yybegin(MULTILINECOMMENTS);
				tok = JavascriptLexerConstants.MULTILINECOMMENTS;
           }

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
           return JavascriptLexerConstants.DEFAULT;
        }
     }
%}

/* main character classes */
eol = [\r\n]+

open = "[" | "{" | "("
close = "]" | "}" | ")"
openclose = {open} | {close}

fieldafterclose = [)\]]"."

insidecomments = ([^\r\n*]* | [^\r\n*]* "*"+ [^\r\n*/])+
linecomments = "/*" [^*] ~"*/" | "/*" "*"+ "/" | ("//" [^\r\n]*)

beginmultilinecomments = "/*" {insidecomments}

id = [_a-zA-Z][_a-zA-Z0-9]*

constantes = "true" | "false" | "null"

builtin = "Array" | "Boolean" | "Date" | "Function" | "Math" | "Number" | "Object" | "RegExp" | "String"
fielddef = {id} ":"
objectname = {id} "."

operator = [=;,.><!~?:+-*/%&|\^] | [=<>!+-*/&\^~%] "=" | "&&" | "++" | "||" | "--" | "!==" | (("<<" | ">>" | ">>>" | "==") "="?)

integer = "0" | ([1-9][0-9]*)
dec = (([0-9]+ "." [0-9]*) | ("." [0-9]+) | {integer}) ([eE]? [+-]? [0-9]+)? [fF]?
oct = "0" [0-7]+ [lL]?
hex = "0" [xX] [0-9a-fA-F]+ [lL]?
number = {integer} | {dec} | {oct} | {hex}

string = ("\"" ([^\r\n\\\"]* | "\\"[^\r\n])+ "\"") | ("'" "\\"? [^\r\n\t] "'")

keywords = "break" | "case" | "catch" | "continue" | "default" | "delete" | "do" | "else" | "finally" | "for" | "function"
                   | "if" | "in" | "instanceof" | "new" | "return" | "switch" | "this" | "throw" | "try" | "typeof"
                   | "var" | "void" | "while" | "with"

function = {id} [ \t]* "("

ggbspecial = "ggbApplet"

%x FIELD, FUNCTION, LINECOMMENTS, MULTILINECOMMENTS

%%

<YYINITIAL> {
  {linecomments}                 {
                                   return JavascriptLexerConstants.LINECOMMENTS;
                                 }

  {beginmultilinecomments}       {
                                   yybegin(MULTILINECOMMENTS);
                                   return JavascriptLexerConstants.MULTILINECOMMENTS;
                                 }

  {constantes}                   {
                                   return JavascriptLexerConstants.CONSTANTE;
                                 }

  {fieldafterclose}              {
                                   yypushback(1);
                                   return JavascriptLexerConstants.OPENCLOSE;
                                 }

  {openclose}                    {
                                   return JavascriptLexerConstants.OPENCLOSE;
                                 }

  {number}                       {
                                   return JavascriptLexerConstants.NUMBER;
                                 }

  {string}                       {
                                   return JavascriptLexerConstants.STRING;
                                 }

  {keywords}                     {
                                   return JavascriptLexerConstants.KEYWORD;
                                 }

  {builtin}                      {
                                   return JavascriptLexerConstants.BUILTINOBJECT;
                                 }

  {operator}                     {
                                   return JavascriptLexerConstants.OPERATOR;
                                 }

  {fielddef}                     {
                                   yypushback(1);
                                   return JavascriptLexerConstants.FIELDDEF;
                                 }

  {ggbspecial} "."               {
                                   yypushback(1);
                                   yybegin(FIELD);
                                   return JavascriptLexerConstants.GGBSPECIAL;
                                 }

  "this."                        {
                                   yypushback(1);
                                   yybegin(FIELD);
                                   return JavascriptLexerConstants.KEYWORD;
                                 }

  {builtin}"."                   {
                                   yypushback(1);
                                   yybegin(FIELD);
                                   return JavascriptLexerConstants.BUILTINOBJECT;
                                 }

  {objectname}                   {
                                   yypushback(1);
                                   yybegin(FIELD);
                                   return JavascriptLexerConstants.OBJECTNAME;
                                 }

  {ggbspecial}                   {
                                   return JavascriptLexerConstants.GGBSPECIAL;
                                 }

  {id}                           {
                                   return JavascriptLexerConstants.IDENTIFIER;
                                 }

  {function}                     {
                                   yypushback(yylength());
                                   yybegin(FUNCTION);
                                 }

  " "                            {
                                   return JavascriptLexerConstants.WHITE;
                                 }

  "\t"                           {
                                   return JavascriptLexerConstants.TAB;
                                 }

  .                              |
  {eol}                          {
                                   return JavascriptLexerConstants.DEFAULT;
                                 }
}

<FIELD> {
  "."                            {
                                   return JavascriptLexerConstants.OPERATOR;
                                 }

  {ggbspecial} "."               {
                                   yypushback(1);
                                   return JavascriptLexerConstants.GGBSPECIAL;
                                 }

  {objectname}                   {
                                   yypushback(1);
                                   return JavascriptLexerConstants.OBJECTNAME;
                                 }

  {ggbspecial}                   {
                                   return JavascriptLexerConstants.GGBSPECIAL;
                                 }

  {function}                     {
                                   yypushback(yylength());
                                   yybegin(FUNCTION);
                                 }

  {id}                           {
                                   return JavascriptLexerConstants.FIELD;
                                 }

  {eol}                          {
                                   return JavascriptLexerConstants.DEFAULT;
                                 }

  .                              {
                                   yypushback(1);
                                   yybegin(YYINITIAL);
                                 }
}

<FUNCTION> {
  {id}                           {
                                   return JavascriptLexerConstants.FUNCTION;
                                 }

  [ \t]*                         { }

  "("                            {
                                   yybegin(YYINITIAL);
                                   return JavascriptLexerConstants.OPENCLOSE;
                                 }
}

<LINECOMMENTS> {
  {eol}                          |
  .                              {
                                   return JavascriptLexerConstants.LINECOMMENTS;
                                 }
}

<MULTILINECOMMENTS> {
  "*/"                           {
                                   yybegin(YYINITIAL);
                                   return JavascriptLexerConstants.MULTILINECOMMENTS;
                                 }

  {insidecomments}               |
  {eol}                          |
  .                              {
                                   return JavascriptLexerConstants.MULTILINECOMMENTS;
                                 }
}

<<EOF>>                          {
                                   return JavascriptLexerConstants.EOF;
                                 }
