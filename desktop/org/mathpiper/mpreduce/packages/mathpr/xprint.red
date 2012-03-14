module xprint; % Display sums, products and integrals in 2D.

% Author: A C Norman, 1992 (and various much earlier occasions).

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


% Print some things using PC characters that will make some things
% look really pretty.  Note that the effect can depend on you having
% a suitable code-page selected - if you have trouble you can always
% go "off msdos;" and use the default display.

fluid '(!*csl);

switch msdos;

remflag('(symbol), 'lose); % Defined in mathpr.red; this version updates

symbolic procedure symbol x;
  begin
    scalar y;
    if !*msdos then y := get(x, 'msdos!-character);
    if y = nil then y := get(x, 'symbol!-character);
    return y
  end;

symbolic procedure character u;
   string!-compress list u;

symbolic procedure string!-compress u;
   % In CSL when compress is given an integer in its list it treats it
   % as a character code.  PSL needs a more complicated construction.
  if !*csl then compress u else
   begin scalar n,v,c;
     n:=length u;
     v := mkstring(n-1);
     for i:=0:(n-1) do
      setf(strbyt(strinf(v),i),
       if numberp(c:=nth(u,n)) then c else id2int c);
     return v;
   end;


% When nat is enabled I use some programmable characters to
% draw pi, fraction bars and integral signs. (symbol 's) returns
% a character-object, and I use
%   .pi         pi
%   bar         solid horizontal bar                    -
%   int-top     top hook of integral sign               /
%   int-mid     vertical mid-stroke of integral sign    |
%   int-low     lower hook of integral sign             /
%   d           curly-d for use with integral display   d
%   sqrt        square root sign                        sqrt
%   sum-top     ---
%   sum-mid     >              for summation
%   sum-low     ---
%   prod-top             ---
%   prod-mid             | |   for products
%   prod-low             | |
%   infinity    infinity sign
%   mat!-top!-l               for display of matrices
%   mat!-top!-r
%   mat!-mid!-l
%   mat!-mid!-r
%   mat!-low!-l
%   mat!-low!-r
%   vbar            |


<< put('!.pi, 'msdos!-character, character 227);
   put('bar, 'msdos!-character, character 196);
   put('int!-top, 'msdos!-character, character 244);
   put('int!-mid, 'msdos!-character, character 179);
   put('int!-low, 'msdos!-character, character 245);
   put('d, 'msdos!-character, character 235);
   put('sqrt, 'msdos!-character, character 251);
   put('vbar, 'msdos!-character, character 179);
   put('sum!-top, 'msdos!-character, string!-compress '(196 196 196));
   put('sum!-mid, 'msdos!-character, ">  ");
   put('sum!-low, 'msdos!-character, string!-compress '(196 196 196));
   put('prod!-top, 'msdos!-character, string!-compress '(194 196 194));
   put('prod!-mid, 'msdos!-character, string!-compress '(179 !! !  179));
   put('prod!-low, 'msdos!-character, string!-compress '(179 !! !  179));
   put('infinity, 'msdos!-character, character 236);

   put('mat!-top!-l, 'msdos!-character, character 218);
   put('mat!-top!-r, 'msdos!-character, character 191);
   put('mat!-mid!-l, 'msdos!-character, character 179);
   put('mat!-mid!-r, 'msdos!-character, character 179);
   put('mat!-low!-l, 'msdos!-character, character 192);
   put('mat!-low!-r, 'msdos!-character, character 217);

   put('!.pi, 'symbol!-character, 'pi);
   put('bar, 'symbol!-character, '!-);
   put('int!-top, 'symbol!-character, '!/);
   put('int!-mid, 'symbol!-character, '!|);
   put('int!-low, 'symbol!-character, '!/);
   put('d, 'symbol!-character, '!d);   % This wants to remain lower case
   put('vbar, 'symbol!-character, '!|);
   put('sum!-top, 'symbol!-character, "---");
   put('sum!-mid, 'symbol!-character, ">  ");
   put('sum!-low, 'symbol!-character, "---");
   put('prod!-top, 'symbol!-character, "---");
   put('prod!-mid, 'symbol!-character, "| |");
   put('prod!-low, 'symbol!-character, "| |");
   put('infinity, 'symbol!-character, 'infinity);
      % In effect nothing special

   put('mat!-top!-l, 'symbol!-character, '![);
   put('mat!-top!-r, 'symbol!-character, '!]);
   put('mat!-mid!-l, 'symbol!-character, '![);
   put('mat!-mid!-r, 'symbol!-character, '!]);
   put('mat!-low!-l, 'symbol!-character, '![);
   put('mat!-low!-r, 'symbol!-character, '!]) >>;

fluid  '(!*fort
         !*nat
         ycoord!*
         ymin!*
         ymax!*
         posn!*
         orig!*
         pline!*);

global '(spare!*);

load_package matrix;         % Load before redefining bits of this.
remflag('(matpri1), 'lose);  % Was in matrix.red - redefined here.

symbolic procedure matpri1(u,x);
   % Prints a matrix canonical form U with name X.
   % Tries to do fancy display if nat flag is on.
   begin scalar m,n,r,l,w,e,ll,ok,name,nw,widths,firstflag,toprow,lbar,
                rbar,realorig;
      if !*fort
        then <<m := 1;
               if null x then x := "MAT";
               for each y in u do
                  <<n := 1;
                    for each z in y do
                       <<assgnpri(z,list list(x,m,n),'only);
                         n := n+1>>;
                    m := m+1>>;
               return nil>>;
      terpri!* t;
      if x and !*nat then <<
         name := layout!-formula(x, 0, nil);
         if name then <<
           nw := cdar name + 4;
           ok := !*nat >>>>
       else <<nw := 0; ok := !*nat>>;
      ll := linelength nil - spare!* - orig!*;
      m := length car u;
      widths := mkvect(1 + m);
      for i := 1:m do putv(widths, i, 1);
      % Collect sizes for all elements to see if it will fit in
      % displayed matrix form.
      % We need to compute things wrt a zero orig for the following
      % code to work properly.
      realorig := orig!*;
      orig!* := 0;
      if ok then for each y in u do
       <<n := 1;
         l := nil;
         w := 0;
         if ok then for each z in y do if ok then <<
            e := layout!-formula(z, 0, nil);
              if null e then ok := nil
              else begin
                scalar col;
                col := max(getv(widths, n), cdar e);
% this allows for 2 blanks between cols, and also 2 extra chars, one
% for the left-bar and one for the right-bar.
                if (w := w + col + 2) > ll then ok := nil
                else <<
                  l := e . l;
                  putv(widths, n, col) >> end;
            n := n+1>>;
         r := (reverse l) . r >>;
         if ok then <<
         % Matrix will fit in displayed representation.
         % Compute format with respect to 0 posn.
         firstflag := toprow := t;
         r := for each py on reverse r collect begin
            scalar y, ymin, ymax, pos, pl, k, w;
            ymin := ymax := 0;
            pos := 1;    % Since "[" is of length 1.
            k := 1;
            pl := nil;
            y := car py;
            for each z in y do <<
               w := getv(widths, k);
               pl := append(update!-pline(pos+(w-cdar z)/2,0,caar z),
                            pl);      % Centre item in its field
               pos := pos + w + 2;    % 2 blanks between cols
               k := k + 1;
               ymin := min(ymin, cadr z);
               ymax := max(ymax, cddr z) >>;
            k := nil;
            if firstflag then firstflag := nil
             else ymax := ymax + 1;   % One blank line between rows
            for h := ymax step -1 until ymin do <<
%              if toprow then <<
%                 lbar := symbol 'mat!-top!-l;
%                 rbar := symbol 'mat!-top!-r;
%                 toprow := nil >>
%               else if h = ymin and null cdr py then <<
%                 lbar := symbol 'mat!-low!-l;
%                 rbar := symbol 'mat!-low!-r >>
%               else
                << lbar := symbol 'mat!-mid!-l;
                   rbar := symbol 'mat!-mid!-r>>;
               pl := ((((pos - 2) . (pos - 1)) . h) . rbar) . pl;
               k := (((0 . 1) . h) . lbar) . k >>;
            return (append(pl, k) . pos) . (ymin . ymax) end;
         orig!* := realorig;
         w := 0;
         for each y in r do w := w + (cddr y - cadr y + 1);
               % Total height.
         n := w/2;  % Height of mid-point.
         u := nil;
         for each y in r do <<
            u := append(update!-pline(0, n - cddr y, caar y), u);
            n := n - (cddr y - cadr y + 1) >>;
         if x then <<maprin x; oprin 'setq >>;
         pline!* := append(update!-pline(posn!*,ycoord!*,u),
                           pline!*);
         ymax!* := max(ycoord!* + w/2, ymax!*);
         ymin!* := min(ycoord!* + w/2 - w, ymin!*);
         terpri!*(not !*nat)>>
      else <<if x then <<maprin x; oprin 'setq>>; matpri2 u>>
   end;

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
      scalar w1,w2,w3,o1,o2,o3,o4,m,ll,bkt;
      if null (u := cdr u) then return 'failed;
% Format is
%   (sum body var low high)
      o1 := car u;     % The body
      if null (u := cdr u) then return 'failed;
      o2 := car u;     % The variable involved
      if null (u := cdr u) then return 'failed;
      o3 := car u;     % The low limit
      if null (u := cdr u) then return 'failed;
      o4 := car u;     % The high limit
      if (u := cdr u) then return 'failed;
      w2 := list('equal, o2, o3);
      w1 := o4;
      u := o1;
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

operator fact, sum, product;

endmodule;

end;

