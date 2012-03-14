module groebfac; % Factorization of polynomials during Groebner calc'n.

imports factor;

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


symbolic procedure groebfactorize (h,abort1,g,g99);
  begin scalar r,tim,gctim,h1,groebactualg99!*,groebfabort!*,test;
    scalar s;
    s:=!*gsugar and gsugar h;
    groebactualg99!* := g99; groebactualg!* := g;
    groebfabort!* := abort1;
    if vdpgetprop(h,'irreducible) then return groebfactorize3 h;
    tim := time();
    gctim := gctime();
         !*trgroeb and groebmess7 h;
    r := if r := vdpgetprop(h,'factors) then r
     else if !*groebrm then groebfactorize1 h
     else if not !*vdpmodular then groebfactorize2 h
     else nil;
    factortime!* := factortime!* + time() - tim -(gctime()-gctim);
    if null r then <<vdpputprop(h,'irreducible,t);
                     return groebfactorize3 h>>;
               if cdr r then !*trgroeb and groebmess14 (h,r);
    vdpputprop(h,'factors,r);
    for each p in r do
       if vdpmember(car p,g) then test:= car p;
    if test then
       <<!*trgroeb and groebmess27a(h,test); return 'zero>>;
    h1 := car r;
    for each p in r do
       if vdpmember(car p,abort1) then
            <<r := delete(p,r); !*trgroeb and groebmess27 car p >>
       else vdpputprop(car p,'irreducible,t);
    if null r then r := list h1;     % at least one
    if null cdr r then groebfactorize3 caar r;
       % inherit sugar if no substantial factor.
    if !*gsugar then
     if null cdr r then gsetsugar(caar r,s) else
       for each p in r do gsetsugar(car p,vdptdeg car p);
    return 'factor . r end;

symbolic procedure groebfactorize1 h;
  % factorize: separate monomial factors which were detected already;
   begin scalar monf,vp,n,e,h1,h2,vp2;
        monf := vdpgetprop(h,'monfac);
        if null monf then
             return if not !*vdpmodular then groebfactorize2 h
                                       else nil;     % no factor
        h2 := vdpdivmon (h,vbcfi 1,monf);

       if groebmonfac neq 0 then
       <<                                 % now build a polynomial from
        n := 0;                           % each variable in MONFAC
        for each x in monf do
            <<n := n#+1;
              if x neq 0 then
                <<e := list x;
                  for i:=2:n do e := 0 . e; % prefix with n-1 zeros.
                  vp := vdpfmon(a2vbc 1,e) . vp >> >> >>
        else
        !*trgroeb and groebmess15 monf;
                  % append body of orig. poly, factorized
        if not vdpzero!? h2 and
           not vevzero!? vdpevlmon h2 then
               <<if not !*vdpmodular then vp2 := groebfactorize2 h2;
                 vp2 := if not vp2 then list h2
                     else for each v in vp2 collect car v;
                 vp := nconc(vp,vp2)>>;
                   % ascending sorting
    %   if length vp = 1 then return nil;
        h1 := vp;
        return reverse  for each x in h1 collect list vdpenumerate x end;

symbolic procedure groebfactorize2 h;
  % tries to factorize a h-polynomial via REDUCE factorizer
   begin scalar h1,h2,!*factor; !*factor := t;
        h1 := groefctrf vdp2f h;
        if null cdr h1 then return nil;
        if null cddr h1      % only one element in factorization list
           and cdr cadr h1 = 1     % and multiplicity = 1
                then return nil;
        h2 := for each l in cdr h1 join
            for i:=1:cdr l collect car l;
        h2 := vdplsort for each p in h2 collect vdpsimpcont f2vdp p;
        return for each x in h2 collect list vdpenumerate x end;

symbolic procedure groefctrf p;
   (fctrf p) where !*factor=t,current!-modulus = current!-modulus;

symbolic procedure groebfactorize3 h;
  % additional efforts to factor something.
 <<h := nil; nil>>;

endmodule;;end ;
