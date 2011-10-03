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

import java.io.StringReader;
import javax.swing.text.Document;
import javax.swing.text.Element;

%%

%public
%class MatchingBlockScanner
%final
%unicode
%char
%type int
%buffer 256

%switch

%{
    private Document doc;
    private Element elem;
    private int start;
    private int end;
    private int savePos;

    public MatchingBlockScanner(Document doc) {
        this.doc = doc;
        this.elem = doc.getDefaultRootElement();
    }

    public MatchingPositions getMatchingBlock(int pos, boolean lr) {
        int p1, s = 1;
        try {
            if (lr) {
                start =  pos;
                end = doc.getEndPosition().getOffset();
                yyreset(new StringReader(doc.getText(start, end - start)));
                yybegin(OPENCLOSE);
                if (yylex() != 1) {
                   return null;
                }

                p1 = pos + yylength();
                yybegin(LR);
            } else {
                 start = pos - 1;
                 end = 0;
                 String str = new StringBuilder(doc.getText(end, start - end + 1)).reverse().toString();
                 yyreset(new StringReader(str));
                 yybegin(CLOSEOPEN);
                 if (yylex() != 1) {
                    return null;
                 }
                 p1 = pos - yylength();
                 yybegin(RL);
            }

            do {
               if (yylex() == 0) {
                  s--;
               } else {
                  s++;
               }
            } while (zzMarkedPos != 0 && s != 0);
        } catch (Exception e) {
            return null;
        }
        if (s == 0) {
            if (lr) {
                return new MatchingPositions(pos, p1, pos + yychar, pos + yychar + yylength());
            } else {
            	return new MatchingPositions(p1, pos, pos - yychar - yylength(), pos - yychar);
            }
        }

        return null;
    }

    public final class MatchingPositions {
        public int firstB;
        public int firstE;
        public int secondB;
        public int secondE;

        private MatchingPositions(int x1, int x2, int y1, int y2) {
            firstB = x1;
            firstE = x2;
            secondB = y1;
            secondE = y2;
        }
    }
%}

%eofval{
  return -1;
%eofval}

/* main character classes */
eol = [\r\n]

char = [\u0000-\u0021\u0023-\uffff]        				    
string = "\"" {char}* "\""

openS = "(" | "[" | "{"
closeS =  ")" | "]" | "}"

%x LR, RL, OPENCLOSE, CLOSEOPEN

%%

<LR> {
  {string}                       { }

  {openS}                        {
                                   return 1;
                                 }

  {closeS}                       {
                                   return 0;
                                 }

  .                              |
  {eol}                          { }
}

<RL> {
  {string}                       { }

  {closeS}                       {
                                   return 1;
                                 }

  {openS}                        {
                                   return 0;
                                 }

  .                              |
  {eol}                          { }
}

<OPENCLOSE> {
  {openS}                        {
                                   return 1;
                                 }

  .                              |
  {eol}                          {
                                   return 0;
                                 }
}

<CLOSEOPEN> {
  {closeS}                       {
                                   return 1;
                                 }

  .                              |
  {eol}                          {
                                   return 0;
                                 }
}
