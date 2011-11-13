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
import javax.swing.text.Element;

%%

%public
%class LaTeXLexer
%extends Lexer
%implements LaTeXLexerConstants
%final
%unicode
%char
%type int
%switch

%{
    public int end;

    private Document doc;
    private Element elem;

    public LaTeXLexer() { }

    public LaTeXLexer(Document doc) {
                this();
                setDocument(doc);
    }

    public void setDocument(Document doc) {
        this.doc = doc;
        this.elem = doc.getDefaultRootElement();
    }

    public void setRange(int p0, int p1) {
        start = p0;
        end = p1;
        String str = "";
        try {
                str = doc.getText(p0, p1 - p0);
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
           return LaTeXLexerConstants.DEFAULT;
        }
     }
%}

/* main character classes */
eol = [\r\n]

open = "[" | "{"
close = "]" | "}"
openclose = {open} | {close}

sub = "_"
sup = "^"
amp = "&"
dollar = "$"

command = "\\".[a-zA-Z]*

number = [0-9]+

default = [^\[{\]}_\^&\$\\\r\n \t0-9]+

comments = "%".*{eol}

%%

<YYINITIAL> {
  {default}                      {
                                   return LaTeXLexerConstants.DEFAULT;
                                 }

  {amp}                          {
                                   return LaTeXLexerConstants.AMP;
                                 }

  {openclose}                    {
                                   return LaTeXLexerConstants.OPENCLOSE;
                                 }

  {number}                       {
                                   return LaTeXLexerConstants.NUMBER;
                                 }

  {sub}                          |
  {sup}                          {
                                   return LaTeXLexerConstants.SUBSUP;
                                 }

  {dollar}                       {
                                   return LaTeXLexerConstants.DOLLAR;
                                 }

  {command}                      {
                                   return LaTeXLexerConstants.COMMAND;
                                 }

  {comments}                     {
                                   return LaTeXLexerConstants.COMMENTS;
                                 }

  " "                            {
                                   return LaTeXLexerConstants.WHITE;
                                 }

  "\t"                           {
                                   return LaTeXLexerConstants.TAB;
                                 }

  .                              |
  {eol}                          {
                                   return LaTeXLexerConstants.DEFAULT;
                                 }
}

<<EOF>>                          {
                                   return LaTeXLexerConstants.EOF;
                                 }
