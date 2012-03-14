module specprin;   % Printing other special forms.

% Author: A. C. Norman.

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


fluid  '(!*fort
         !*nat
         ycoord!*
         ymin!*
         ymax!*
         posn!*
         orig!*
         pline!*);

global '(spare!*);

symbolic procedure intprint u;
   if not !*nat or !*fort then 'failed
   else begin
      scalar m;
      prin2!* symbol 'int!-mid;
      m := posn!* - 1;
      pline!* := (((m . posn!*) . (ycoord!* + 1)) .
                      symbol 'int!-top) . pline!*;
      pline!* := (((m . posn!*) . (ycoord!* - 1)) .
                      symbol 'int!-low) . pline!*;
      if ycoord!*+1>ymax!* then ymax!* := ycoord!*+1;
      if ymin!*>ycoord!*-1 then ymin!* := ycoord!*-1;
      maprin cadr u;
      prin2!* " ";
      prin2!* symbol 'd;
      maprin caddr u
   end;

put('int, 'prifn, 'intprint);

symbolic procedure sqrtprint u;
   if not !*nat or !*fort then 'failed
   else begin
      scalar m;
      m := symbol 'sqrt;
% The square-root sign may not be available as a symbol - if it is
% not then I will not do anything special here
      if m=nil then return 'failed;
      prin2!* m;
      u := cadr u;
      if not atom u or (numberp u and u < 0) then <<
          prin2!* "(";
          m := t >>
       else m := nil;
      maprin u;
      if m then prin2!* ")"
   end;

put('sqrt, 'prifn, 'sqrtprint);

symbolic procedure sumprint(u,p);
   sppri(u, p, symbol 'sum!-top, symbol 'sum!-mid, symbol 'sum!-low);

symbolic procedure prodprint(u,p);
   sppri(u, p, symbol 'prod!-top, symbol 'prod!-mid, symbol 'prod!-low);

symbolic procedure sppri(u, p, top, mid, low);
   if not !*nat or !*fort then 'failed
   else begin
      scalar w1,w2,w3,o1,o2,o3,m,ll,bkt;
      if null (u := cdr u) then return 'failed;
      w2 := car u;          % low limit
      if null (u := cdr u) then <<
          u := w2;          % Only a body - no limits
          w2 := nil >>
      else <<
          w1 := car u;      % high limit
          if null (u := cdr u) then <<
              u := w1;      % no high limit
              w1 := nil >>
          else u := car u >>;
      ll := linelength nil - spare!*;
      spare!* := spare!* + ll/2;
      if w1 then <<
          if null (w1 := layout!-formula(w1, 0, nil)) then <<
             spare!* := spare!* - ll/2;
             return 'failed >> >>
      else w1 := (nil . 0) . (0 . -1);
      if w2 then <<
          if null (w2 := layout!-formula(w2, 0, nil)) then <<
             spare!* := spare!* - ll/2;
             return 'failed >> >>
      else w2 := (nil . 0) . (0 . -1);
      spare!* := spare!* - ll/2;
      m := 0 . 3;
      w3 := list(((m . 1) . top),
                 ((m . 0) . mid),
                 ((m . -1) . low));  % Pline structure for big symbol;
      m := max(cdar w1, cdar w2, 3);
% Here I decide if the entire sum needs to be put in parens. I am
% not at present certain that I have this test just the way it
% ought to be, but at least this is an approximation.
      bkt := p >= get('plus, 'infix);
      if bkt then m := m + 1;
      if posn!* + m > ll then terpri!* t;
      if bkt then prin2!* "(";
      o1 := (m - cdar w1)/2 + posn!* - orig!*;
      o2 := (m - cdar w2)/2 + posn!* - orig!*;
      o3 := (m - 3)/2 + posn!*;
      pline!* := append(
         update!-pline(o3, ycoord!*, w3),
         append(update!-pline(o1, ycoord!* + 2 - cadr w1, caar w1),
                append(update!-pline(o2, ycoord!* - 2 - cddr w2,
                                     caar w2), pline!*)));
      ymax!* := max(ymax!*, ycoord!* + 2 + cddr w1 - cadr w2);
      ymin!* := min(ymin!*, ycoord!* - 2 - cddr w2 + cadr w2);
      posn!* := posn!* + m;
      maprint(u, get('minus, 'infix));
      if bkt then prin2!* ")"
   end;

put('sum, 'pprifn, 'sumprint);
put('product, 'pprifn, 'prodprint);

put('sumprint, 'expt, 'inbrackets);
put('prodprint, 'expt, 'inbrackets);

symbolic procedure factpri u;
   if not !*nat or !*fort then 'failed
   else <<
      maprint(cadr u, 100);
      prin2!* "!" >>;

put('fact, 'prifn, 'factpri);

algebraic;

% sum(low-limit, high-limit, body)
% product(low-limit, high-limit, body);
%
% degenerate cases display as if
% sum(low-limit, body)
% sum(body)

% fact n     factorial function


operator fact, sum, product;

for all n such that numberp n and fixp n and n >= 0
   let fact n = for i := 1:n product i;

endmodule;

end;
