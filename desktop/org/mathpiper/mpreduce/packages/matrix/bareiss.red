module bareiss; % Inversion routines using the Bareiss 2-step method.

% Author: Anthony C. Hearn.
% Modifications by: David Hartley.

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


% This module is rife with essential references to RPLAC-based
% functions.

fluid '(!*exp asymplis!* subfg!* wtl!* !*trsparse powlis!* powlis1!*
        bareiss!-step!-size!*);   % !*solveinconsistent

global '(assumptions requirements);

bareiss!-step!-size!* := 2;     % Seems fastest on average.

symbolic procedure matinverse u;
   lnrsolve(u,generateident length u);

symbolic procedure lnrsolve(u,v);
   %U is a matrix standard form, V a compatible matrix form.
   %Value is U**(-1)*V.
   begin scalar temp,vlhs,vrhs,ok,
                !*exp,!*solvesingular;
   if !*ncmp then return clnrsolve(u,v);
   !*exp := t;
   if asymplis!* or wtl!* then
    <<temp := asymplis!* . wtl!*;
      asymplis!* := wtl!* := nil>>;
   vlhs := for i:=1:length car u collect intern gensym();
   vrhs := for i:=1:length car v collect intern gensym();
   u := car normmat augment(u,v);
   v := append(vlhs,vrhs);
   ok := setkorder v;
   u := foreach r in u collect prsum(v,r);
   v := errorset!*({function solvebareiss, mkquote u,mkquote vlhs},t);
   if caar v memq {'singular,'inconsistent} then
      <<setkorder ok; rerror(matrix,13,"Singular matrix")>>;
   v := pair(cadr s,car s) where s = cadar v;
   u := foreach j in vlhs collect
           coeffrow(negf numr q,vrhs,denr q) where q = cdr atsoc(j,v);
   setkorder ok;
   if temp then <<asymplis!* := car temp; wtl!* := cdr temp>>;
   return for each j in u collect
             for each k in j collect
                if temp then resimp k else cancel k;
   end;

symbolic procedure prsum(kl,cl);
   % kl: list of kernel, cl: list of sf -> prsum: sf
   % kl and cl assumed to have same length
   if null kl then nil
   else if null car cl then prsum(cdr kl,cdr cl)
   else car kl .** 1 .* car cl .+ prsum(cdr kl,cdr cl);

symbolic procedure solvebareiss(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> solvebareiss: tagged solution list
   % Solve linear system exlis for variables in varlis using multi-step
   % Bareiss elimination and fraction-free back-substitution.  The
   % equations in exlis are not converted to a matrix, but kept as
   % (sparse) standard forms.
   begin
   % if asymplis!* or wtl!* then
   %   <<temp := asymplis!* . wtl!*; asymplis!* := wtl!* := nil>>;
   exlis := sparse_bareiss(exlis,varlis,bareiss!-step!-size!*);
   if car exlis = 'inconsistent then return 'inconsistent . nil;
   exlis := cdr exlis;
   if not !*solvesingular and length exlis < length varlis then
      return 'singular . nil;
   if !*trsparse then
      solvesparseprint("Reduced system",reverse exlis,varlis);
   exlis := sparse_backsub(exlis,varlis);
   varlis := foreach p in exlis collect car p;
   exlis := foreach p in exlis collect cdr p;
   % if temp then <<asymplis!* := car temp; wtl!* := cdr temp>>;
   exlis := for each ex in exlis collect resimp subs2!* ex;
   return t . {{exlis,varlis,1}};
   end;

symbolic procedure coeffrow(u,v,d);
   % u:sf, v:list of kernel, d:sf -> coeffrow: list of sq
   % u is linear homogeneous in the kernels in v
   if null v then nil
   else if null u or mvar u neq car v
    then (nil ./ 1) . coeffrow(u,cdr v,d)
   else (lc u ./ d) . coeffrow(red u,cdr v,d);


symbolic procedure augment(u,v);
   if null u then nil else append(car u,car v) . augment(cdr u,cdr v);

symbolic procedure generateident n;
  %returns matrix canonical form of identity matrix of order N.
   begin scalar u,v;
        for i := 1:n do
         <<u := nil;
           for j := 1:n do u := ((if i=j then 1 else nil) . 1) . u;
           v := u . v>>;
        return v
   end;

symbolic procedure normmat u;
   %U is a matrix standard form.
   %Value is dotted pair of matrix polynomial form and factor.
   begin scalar x,y,z;
      x := 1;
      for each v in u do
         <<y := 1;
           for each w in v do y := lcm(y,denr w);
           z := (for each w in v
                    collect multf(numr w,quotf(y,denr w)))
              . z;
           x := multf(y,x)>>;
      return reverse z . x
   end;

symbolic procedure sparse_bareiss(u,v,k);
   % u: list of sf, v: list of kernel, k: posint
   % -> sparse_bareiss: (t|'inconsistent) . list of sf
   % Multi-step Bareiss elimination using exterior multiplication to
   % calculate and organise determinants efficiently. Individual blocks
   % are solved using Cramer's rule. Exterior forms are decomposed into
   % {constant,linear} parts in non-pivot variables (non-linear part is
   % not needed). The leading coefficient of the first expression
   % returned is the determinant of the system.
   begin scalar p,d,w,pivs,s,asymplis!*,powlis!*,powlis1!*,wtl!*;
   d := 1;
   u := foreach f in u join if f then {!*sf2ex(f,v)};
   while p := choose_pivot_rows(u,v,k,d) do
      begin
      u := car p; v := cadr p;  % throws out free vars as well
      p := cddr p;
      pivs := lpow car p;       % pivot variables
      u := foreach r in u join  % multi-step elim. on remaining rows
              begin
              r := splitup(r,v);
              r := extadd(extmult(cadr r,car p),extmult(car r,cadr p));
              if null (r := subs2chkex r) then return nil;
              r := innprodpex(pivs,quotexf!*(r,d));
              % since we did r := r^pivs and then r := pivs _| r,
              % sign has changed if degree(pivs) is odd
              if not evenp length pivs then r := negex r;
              return {r};
              end;
      d := lc car p;            % update divisor
      assumptions := 'list . mk!*sq !*f2q d .
                            (pairp assumptions and cdr assumptions);
      p := extadd(car p,cadr p);% recombine pivot rows
      s := evenp length pivs;
      foreach x in pivs do      % Cramer's rule on pivot rows
         w := if (s := not s) then innprodpex(delete(x,pivs),p) . w
              else negex innprodpex(delete(x,pivs),p) . w;
      end;
   foreach f in u do % inconsistent system
      requirements := 'list . mk!*sq !*f2q !*ex2sf f .
                          (pairp requirements and cdr requirements);
   return if u then 'inconsistent . nil
          else t . foreach f in w collect !*ex2sf f;
   end;

symbolic procedure choose_pivot_rows(u,v,k,d);
   % u: list of ex, v: list of kernel, k: posint, d: sf
   % -> choose_pivot_rows: nil or (list of ex).(list of kernel).ex
   % Choose pivots in the first k variables from v (or the first k-1
   % variables from the first pivot variable in v). If k pivots can't be
   % found, don't waste time looking in further columns (so number of
   % pivot rows is <= k).  If pivots found, return remaining rows,
   % remaining variables and decomposed exterior product of pivot rows.
   if null u or null v then nil else
   begin scalar w,s,ss,p,x,y,rows,pivots;
   w := u;
   for i:=1:k do if v then v := cdr v;
   while k neq 0 do
      if null u then % ran out of rows before finding k pivots
         if null v or null w or pivots then k := 0
         else % skip k more variables and reset everything
          << for i:=1:k do if v then v := cdr v; s := nil; u := w>>
      else if car(x := splitup(car u,v)) and
              (y := if null pivots then car x
                    else subs2chkex extmult(car x,car pivots)) then
         begin % found one
         rows := x . rows;
         pivots := (if null pivots then y else quotexf!*(y,d)) . pivots;
         % if # rows skipped is odd, then reverse sign
         if s then ss := not ss;
         w := delete(car u,w); u := cdr u; k := k - 1;
         end
      else <<u := cdr u; s := not s>>; % skip row
   if null pivots then return; % couldn't find any pivots
   % next line adjusts sign to return row1^...^rowk
   % instead of rowk^...^row1
   if remainder(length lpow car pivots,4) member {2,3} then
      ss := not ss;
   rows := reverse rows;       % calculate dets along pivot rows
   pivots := reverse pivots;
   p := car rows;
   foreach r in cdr rows do
      p := {car(pivots := cdr pivots),
            quotexf!*(extadd(extmult(cadr r,car p),
                             extmult(car r,cadr p)),d)};
   return w . v . if ss then {negex car p,negex cadr p} else p;
   end;

symbolic procedure sparse_backsub(exlis,varlis);
   % exlis: list of sf, varlis: list of kernel
   % -> sparse_backsub: list of kernel.sq
   % Fraction-free back-substitution for exlis, where reverse exlis is
   % a list of rows of an upper-triangular matrix wrt varlis.  Since
   % exlis has been produced in a fraction-free way, the leading
   % coefficient of the first row is the determinant of the system.
   begin scalar d,z,c;
   if null exlis then return nil;       % trivial case
   d := lc car exlis;                  % determinant
   foreach x in exlis do % Almost redundant for first x.
      begin scalar s,p,v,r;
      p := lc x;   % pivot.
      v := mvar x;
      x := red x;
      while not domainp x and mvar x member varlis do
          <<if (c := atsoc(mvar x,z)) then % back-substitute
               s := addf(multf(lc x,cdr c),s)
            else % move free variable terms to rhs
               r := addf(!*t2f lt x,r);
            x := red x>>;
      % Used to be quotf!*, but that could give a terminal error.
      s := negf quotff(addf(multf(addf(r,x),d),s),p);
      z := (v . s) . z;
      end;
   for each p in z do cdr p := cancel(cdr p ./ d);
   return z
   end;

symbolic procedure quotff(u,v);
   % We do the rationalizesq step to allow for surd divisors.
   if null u then nil
    else (if x then x
           else (if denr y = 1 then numr y
                  else rederr "Invalid division in backsub")
                 where y=rationalizesq(u ./ v))
          where x=quotf(u,v);

endmodule;

end;
