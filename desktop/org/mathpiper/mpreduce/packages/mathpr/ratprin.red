module ratprin;   % Printing standard quotients.

% Author: Eberhard Schruefer.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


% Modifications by: Anthony C. Hearn & A. C. Norman.

fluid  '(!*fort
         !*list
         !*mcd
         !*nat
         !*ratpri
         dmode!*
         ycoord!*
         ymin!*
         ymax!*
         orig!*
         pline!*
         posn!*
         p!*!*);

global '(spare!*);

switch ratpri;

!*ratpri := t;   % default value if this module is loaded.

put('quotient,'prifn,'quotpri);
put('quotpri, 'expt, 'inbrackets);

symbolic procedure quotpri u;
   % *mcd is included here since it uses rational domain elements.
   begin scalar dmode;
      if null !*ratpri or null !*nat or !*fort or !*list or null !*mcd
           then return 'failed
       else if flagp(dmode!*,'ratmode)
        then <<dmode := dmode!*; dmode!* := nil>>;
      u := ratfunpri1 u;
      if dmode then dmode!* := dmode;
      return u
   end;

symbolic procedure ratfunpri1 u;
   begin scalar x,y,ch,pln,pld;
         integer heightnum,heightden,orgnum,orgden,fl,w;
     spare!* := spare!* + 2;
     if (pln := layout!-formula(cadr u, 0, nil)) and
        (pld := layout!-formula(caddr u, 0, nil)) then <<
         spare!* := spare!* - 2;
         fl := 2 + max(cdar pln, cdar pld);
         if fl>(linelength nil - spare!* - posn!*) then terpri!* t;
         w := (cdar pln - cdar pld);   % Width difference num vs. den
         if w > 0 then << orgnum := 0; orgden := w / 2 >>
          else << orgnum := (-w) / 2; orgden := 0 >>;
         heightnum := cddr pln - cadr pln + 1;
         heightden := cddr pld - cadr pld + 1;
         pline!* :=
            append(
               update!-pline(orgnum + posn!* + 1 - orig!*,
                             1 - cadr pln + ycoord!*,
                             caar pln),
               append(update!-pline(orgden + posn!* + 1 - orig!*,
                                    ycoord!* - cddr pld - 1,
                                    caar pld),
                      pline!*));
         ymin!* := min(ymin!*, ycoord!* - heightden);
         ymax!* := max(ymax!*, ycoord!* + heightnum);
         ch := symbol 'bar;
         for j := 1:fl do prin2!* ch >>
      else <<
         % Here the miserable thing will not fit on one line
         spare!* := spare!* - 2;    % Restore
         u :=  cdr u;
         x := get('quotient,'infix);
         if p!*!* then y := p!*!*>x else y := nil;
         if y then prin2!* "(";
         maprint(car u,x);
         oprin 'quotient;
         maprint(negnumberchk cadr u,x);
         if y then prin2!* ")">>
    end;

symbolic procedure layout!-formula(u, p, op);
% This procedure forms a pline!* structure for an expression that
% will fit upon a single line. It returns the pline* together with
% height, depth and width information. If the line would not fit
% it returns nil. Note funny treatment of orig!* and width here.
% If op is non-nil oprin it too - if it is 'inbrackets do that.
   begin
      scalar ycoord!*, ymin!*, ymax!*, posn!*, pline!*,
             testing!-width!*, overflowed!*;
      pline!* := overflowed!* := nil;
      ycoord!* := ymin!* := ymax!* := 0;
      posn!* := orig!*;
      testing!-width!* := t;
      if op then <<
         if op = 'inbrackets then prin2!* "("
          else oprin op >>;
      maprint(u, p);
      if op = 'inbrackets then prin2!* ")";
      if overflowed!* then return nil
       else return (pline!* . (posn!* - orig!*)) . (ymin!* . ymax!*)
   end;

symbolic procedure update!-pline(x,y,pline);
   % Adjusts origin of expression in pline by (x,y).
   if x=0 and y=0 then pline
    else for each j in pline collect
            (((caaar j #+ x) . (cdaar j #+ x)) . (cdar j #+ y)) . cdr j;

symbolic procedure prinfit(u, p, op);
% Display u (as with maprint) with op in front of it, but starting
% a new line before it if there would be overflow otherwise.
   begin
      scalar w;
      if not !*nat or testing!-width!* then <<
         if op then oprin op;
         return maprint(u, p) >>;
      w := layout!-formula(u, p, op);
      if w = nil then <<
         if op then oprin op;
         return maprint(u, p) >>;
      putpline w
   end;

symbolic procedure putpline w;
   begin
      if posn!* #+ cdar w > linelength nil #- spare!* then terpri!* t;
      pline!* :=
         append(update!-pline(posn!* #- orig!*, ycoord!*, caar w),
                pline!*);
      posn!* := posn!* #+ cdar w;
      ymin!* := min(ymin!*, cadr w #+ ycoord!*);
      ymax!* := max(ymax!*, cddr w #+ ycoord!*)
  end;

endmodule;

end;
