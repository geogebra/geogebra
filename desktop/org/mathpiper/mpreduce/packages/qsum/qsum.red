module qsum; % summation of q-hypergeometric terms

% Authors: Wolfram Koepf, Harald Boeing
% Version 1.0, May 1997.

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


algebraic;

% ----------------------------------------------------------------------

share !*qsumrecursion!@sub;
lisp setq(!*qsumrecursion!@sub, list(!*redefmsg, !*echo, !*output));
lisp setq(!*redefmsg, nil);
off echo;
off output;

% ------------------------------ SWITCHES ------------------------------

switch qsum_nullspace;
switch qsum_trace;
switch qgosper_down;
switch qgosper_specialsol;

switch qsumrecursion_down;
switch qsumrecursion_exp;
switch qsumrecursion_certificate;

switch qsumrecursion_profile;
lisp setq(!*qsumrecursion_profile, nil);

lisp setq(!*qsum_nullspace, nil);
lisp setq(!*qsum_trace, nil);
lisp setq(!*qgosper_down, t);
lisp setq(!*qgosper_specialsol, t);

lisp setq(!*qsumrecursion_down, t);
lisp setq(!*qsumrecursion_exp, nil);
lisp setq(!*qsumrecursion_certificate, nil);

% ------------------------ GLOBAL VARIABLES ----------------------------

clear summ;
operator summ;
clear arbcomplex;
operator arbcomplex;

share qsumrecursion_recrange!*;
qsumrecursion_recrange!*:= {1,5};


% ======================================================================

for all x,n such that fixp(n/2) and not(lisp !*complex) let
        abs(x)^n=x^n;

% ======================================================================
% ----------------------------------------------------------------------

% BESCHREIBUNG:
%
%      new_simpexpt ist gedacht um das Fakorisieren von Exponenten
%      (bei on factor) zu verhindern.
%
%      Die alte Prozedure simpexpt wird vorher mittels
%           copyd('original_simpexpt, 'simpexpt)
%      gesichert. Anschlie"sen kann die neue Prozedur
%      mittels
%           copyd('simpexpt, 'new_simpexpt)
%      als neuer Standard gesetzt werden. Will man dies wieder
%      r"uckg"angig machen, so mu"s man die alte Prozedur mittels
%           copyd('simpexpt, 'original_simpexpt)
%      wieder als Standard defininieren.
%
%
%
%

lisp;
if null(getd 'original_simpexpt) then
        copyd('original_simpexpt, 'simpexpt);
algebraic;

% ----------------------------------------------------------------------

symbolic procedure new_simpexpt(u);
begin
        scalar !*PRECISE, !*FACTOR, !*EXP, !*MCD, !*ALLFAC, redefmode;

        % Schalte exp ein, damit die Exponenten expandiert werden.
        % Ausschalten von PRECISE um Vereinfachungen wie
        %  (x*y)^k => x^k*y^k zu erreichen.
        on EXP, MCD;  off PRECISE, ALLFAC;  % switch-setting

        if eqcar(car u, 'minus) then
                return multsq(original_simpexpt({{'minus,1},cadr(u)}),
                        new_simpexpt({cadar(u),cadr(u)}));

        % Rufe zun"achst die Original-Prozedur auf...
        % Da diese rekursive programmiert ist, kann sie sich selber wieder
        % aufrufen, so da"s sie zun"achst wieder als Standard
        % wiederherzustellen ist.
        % Zudem ist zu verhindern, da"s Warning-messages
        % Function has been redefined erscheinen...
        redefmode:= !*redefmsg;
        !*redefmsg:= nil;
        copyd('simpexpt, 'original_simpexpt);

        u:= simpexpt u;

        copyd('simpexpt, 'new_simpexpt);

        !*redefmsg:= redefmode;

        return u;
end;

% ----------------------------------------------------------------------
%  ----------------------------------------------------------------------

% some compatibility functions for Maple sources.
% by Winfried Neun

put('PolynomQQ,'psopfn,'polynomQQQ);

algebraic procedure polynomq4(expr1,k);
begin scalar !*exp;
on exp;
return polynomqq(expr1,k);
end;


% checks if expr is rational in var
algebraic procedure type_ratpoly(expr1,var);
begin
scalar deno, nume;
deno:=den expr1;
nume:=num expr1;
  if (PolynomQQ (deno,var) and PolynomQQ (nume,var))
    then return t else return nil;
end;
flag ('(type_ratpoly),'boolean);

symbolic procedure tttype_ratpoly(u,xx);
  ( if fixp xx then t else
        if not eqcar (xx , '!*sq) then  nil
          else and(polynomQQQ(list(mk!*sq (numr cadr xx ./ 1),
                                  reval cadr u))
                 ,polynomQQQ(list(mk!*sq (denr cadr xx ./ 1),
                                  reval cadr u)))
 ) where xx = aeval(car u);

flag ('(tttype_ratpoly),'boolean);

%checks if x is polynomial in var
symbolic procedure PolynomQ (x,var);

 if not fixp denr simp x then NIL else
 begin scalar kerns,kern,aa;

 kerns:=kernels !*q2f simp x;

 aa: if null kerns then return T;
     kern:=first kerns;
     kerns:=cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return NIL else go aa;
end;

flag('(PolynomQ),'opfn);

flag ('(PolynomQ type_ratpoly),'boolean);


symbolic procedure PolynomQQQ (x);

(if fixp xx then t else
 if not onep denr (xx:=cadr xx) then NIL
 else begin scalar kerns,kern,aa,var,fform,mvv,degg;

 fform:=sfp  mvar  numr xx;
 var:=reval cadr x;
 if fform then << xx:=numr xx;
    while (xx neq 1) do
     << mvv:=mvar  xx;
        degg:=ldeg  xx;
        xx:=lc  xx;
        if domainp mvv then <<if not freeof(mvv,var) then
                << xx:=1 ; kerns:=list list('sin,var) >> >> else
        kerns:=append ( append (kernels mvv,kernels degg),kerns) >> >>
   else kerns:=kernels !*q2f xx;

 aa: if null kerns then return T;
     kern:=first kerns;
     kerns:=cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return NIL else go aa;
end) where xx = aeval(car x);

put('PolynomQQ,'psopfn,'polynomQQQ);

symbolic procedure ttttype_ratpoly(u);
  ( if fixp xx then t else
        if not eqcar (xx , '!*sq) then nil
          else and(polynomQQQ(list(mk!*sq (numr cadr xx ./ 1), reval cadr u))
                  ,polynomQQQ(list(mk!*sq (denr cadr xx ./ 1), reval cadr u)))
 ) where xx = aeval(car u);

flag ('(type_ratpoly),'boolean);

put('type_ratpoly,'psopfn,'ttttype_ratpoly);


% ----------------------------------------------------------------------
% ----------------------------------------------------------------------

symbolic procedure start;
begin
        return (profile_time!*:= {'list, time(), gctime()});
end$

symbolic operator start;

% ----------------------------------------------------------------------

symbolic procedure stop;
begin
        scalar gct, cput;
        gct:= gctime() - caddr(profile_time!*);
        cput:= time() - cadr(profile_time!*) - gct;
        return {'list, cput, gct};
end$

symbolic operator stop;

% ----------------------------------------------------------------------

symbolic procedure showprofile;
begin
        scalar tim;
        prin2 "CPU: ";
        tim:= time() - cadr(profile_time!*);
        prin2 tim;
        tim:= gctime() - caddr(profile_time!*);
        if (tim=0) then return terpri();
        prin2 " ,  GC: ";
        prin2 tim;
        terpri();
end$

symbolic operator showprofile;

% ----------------------------------------------------------------------

operator timing!-cpu!+gc!*, timing!-gc!*;

algebraic procedure timing(n);
begin
        if (n=start) then return <<clear timing!-cpu!+gc!*, timing!-gc!*;
                operator timing!-cpu!+gc!*, timing!-gc!*;>>;
        if numberp(timing!-cpu!+gc!*(n)) then <<
                timing!-gc!*(n):= (lisp gctime()) - timing!-gc!*(n);
                timing!-cpu!+gc!*(n):= (lisp time()) - timing!-cpu!+gc!*(n);
        >> else <<
                timing!-gc!*(n):= (lisp gctime());
                timing!-cpu!+gc!*(n):= (lisp time());
        >>;
        return {timing!-cpu!+gc!*(n)-timing!-gc!*(n), timing!-gc!*(n)};
end$

% ----------------------------------------------------------------------

algebraic procedure showtiming(n);
        {timing!-cpu!+gc!*(n)-timing!-gc!*(n), timing!-gc!*(n)};

% ----------------------------------------------------------------------

algebraic procedure showcputiming(n);
        timing!-cpu!+gc!*(n) - timing!-gc!*(n);

% ----------------------------------------------------------------------

algebraic procedure showgctiming(n);
        timing!-gc!*(n);

% ----------------------------------------------------------------------
% ======================================================================

symbolic procedure product2list(term);
begin
        scalar !*FACTOR, !*EXP, !*LIMITEDFACTORS, !*MCD, l, z;
        on FACTOR, MCD;  off LIMITEDFACTORS;  % switch-setting
        term:= simp aeval(term);
        z:= numr term;
        l:= {};
        while pairp(z) and (red(z) eq nil) do begin
                l:= mk!*sq(((((mvar(z) . ldeg(z)) . 1) . nil)) . 1) . l;
                z:= lc(z);
        end;
        if not eqn(z,1) then l:= mk!*sq(z . 1) . l;
        z:= denr term;
        while pairp(z) and (red(z) eq nil) do begin
                l:= mk!*sq(((((mvar(z) . -ldeg(z)) . 1) . red(z))) . 1) . l;
                z:= lc(z);
        end;
        if not eqn(z,1) then l:= mk!*sq(1.z) . l;
        return 'list . l;
end$

symbolic operator product2list;

% ----------------------------------------------------------------------

symbolic procedure sum2list(z);
begin
        scalar !*FACTOR, !*EXP, !*MCD, !*ALLFAC, l, denom;
        on EXP, MCD; off ALLFAC;  % switch-setting
        z:= simp aeval(z);
        denom:= denr z;
        z:= numr z;
        if atom(z) or not(numberp(denom)) then
                return 'list . {mk!*sq(z . denom)};
        l:= {};
        repeat <<
                l:= mk!*sq(((((mvar(z) . ldeg(z)) . lc(z)) . nil)) . denom) . l;
                z:= red(z);
        >> until atom(z) or null(z);
        if not(null(z)) then l:= mk!*sq(z . 1) . l;
        return 'list . l;
end$

symbolic operator sum2list;

% ----------------------------------------------------------------------
% ======================================================================
% ----------------------------------------------------------------------

algebraic procedure laurentcoeff(p, x);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*DIV, np, dp;
        on EXP, MCD;  off DIV;  % switch-setting
        np:= coeff(num(p),x);
        dp:= sub(x=1, den(p));
        return (for each j in np collect (j/dp));
end$

% ----------------------------------------------------------------------

algebraic procedure laurentcoeffn(p, x, n);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*RATIONAL, DMODE!*, !*DIV, np, dp, d;
        on EXP, MCD; off RATIONAL;  % switch-setting
        dp:= den(p);
        d:= deg(dp, x);
        np:= num(p) / sub(x=1,dp);
        n:= n + d;
        if (n < 0) then return 0;
        return coeffn(np,x,n);
end;

% ----------------------------------------------------------------------

algebraic procedure laurentdegree(p, x);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*DIV, !*RATIONAL, DMODE!*;
        on EXP, MCD;  off DIV, RATIONAL;  % switch-setting
        return (deg(num(p),x) - deg(den(p),x));
end$

% ----------------------------------------------------------------------

algebraic procedure laurentldegree(p, x);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*DIV, !*RATIONAL, DMODE!*;
        on EXP, MCD;  off DIV, RATIONAL;  % switch-setting
        p:= sub(x=1/x, p);
        return (deg(den(p),x) - deg(num(p),x));
end$

% ----------------------------------------------------------------------
% ----------------------------------------------------------------------
% ----------------------------------------------------------------------

symbolic procedure nullspace_size(x);
begin
        if atom(x) then
                return 1
        else
                return (nullspace_size(car x) + nullspace_size(cdr x));
end$

% ----------------------------------------------------------------------

symbolic procedure nullspace_equations2sqmatrix(gls, var, m, n);
begin
        scalar a, gl;
        timing('nullspace_equations2sqmatrix);
        a:= mkvect(m);
        for j:=0:m do putv(a, j, mkvect(n+1));
        for row:=0:m do begin
                gl:= car(gls);
                if pairp(gl) and (car(gl) = 'equal) then
                        gl:= addsq(simp(cadr(gl)), negsq(simp(caddr(gl))))
                else
                        gl:= simp(gl);
                gls:= cdr(gls);
                for j:=0:n do begin
                        putv(getv(a,row), j, simp(coeffn(aeval mk!*sq gl, getv(var,j), 1)));
                        gl:= (subsq(gl, {getv(var,j) . 0}));
                end;
                putv(getv(a,row), n+1, gl);
        end;
        timing('nullspace_equations2sqmatrix);
        return a;
end$

% ----------------------------------------------------------------------

symbolic procedure nullspacesolve(a, var);
begin
        scalar !*FACTOR, !*EXP, !*GCD, !*MCD, !*LIMITEDFACTORS,
                        m, n, nr_pref_va, va;
        timing('nullspacesolve);
        on EXP, MCD;  off GCD, LIMITEDFACTORS;  % switch-setting
        % put equations into list and remove 'zeroe-entries'...
        if pairp(a) and (car(a) = 'list) then
                a:= cdr(a)
        else
                a:= (a . nil);
        m:= length(a);
        va:= nil;
        for j:=1:m do begin
                n:= car(a);
                a:= cdr(a);
                if (n neq 0) then va:= n . va;
        end;
        a:= va;
        % put variables in list and then into a vector
        if pairp(var) and (car(var) = 'list) then
                var:= cdr(var)
        else
                var:= (var . nil);
        m:= length(a) - 1;
        n:= length(var) - 1;
        nr_pref_va:= n;
        va:= mkvect(n);
        for j:=0:n do <<putv(va,j,car(var)); var:= cdr(var)>>;
        a:= nullspace_equations2sqmatrix(a, va, m, n);
        on FACTOR;  % switch-setting
        a:= a;
        a:= nullspace_triangulize(a, va, m, n+1, nr_pref_va);
        va:= cadr(a);
        a:= car(a);
        a:= nullspace_sort(a);
        a:= nullspace_matrix2solution(a, va);
        timing('nullspacesolve);
        return a;
end$

symbolic operator nullspacesolve;
% ----------------------------------------------------------------------

symbolic procedure nullspace_showmat(a);
begin
        scalar m, n;
        m:= upbv(a);
        n:= upbv(getv(a,1));
        for j:=0:m do begin
                prin2("{");
                for i:=0:n do begin
                        prin2(prepsq getv(getv(a,j),i));
                        prin2("  ");
                end;
                prin2t("}");
        end;
end$

% ----------------------------------------------------------------------

symbolic procedure nullspace_triangulize(a, var, m, n, nr_pref_va);
begin
        scalar tmp, c, not_changed, j, pivot;
        timing('nullspace_triangulize);
        % Determine number of equations and number of columns
        % Initialize vector c determines whether a row was "triangulized"
        c:= mkvect(m);
        for j:=0:m do putv(c,j,-1);
        not_changed:= (for j:=0:m collect j);
        % Start triangulization
        for k:=0:m do begin
                pivot:= nullspace_triangulize_pivot
                        (a, not_changed, m, n-1, k, nr_pref_va);
                if (pivot neq nil) then begin
                        j:= cadr(pivot);
                        % Exchange columns such that pivot-element is at column k
                        nullspace_triangulize_exchange_columns(a, j, k);
                        % Change variable order
                        tmp:= getv(var,j);
                        putv(var,j,getv(var,k));
                        putv(var,k,tmp);
                        j:= car(pivot);
                        pivot:= simp mk!*sq negsq(getv(getv(a,j),k));
                        for l:=0:n do
                                putv(getv(a,j), l, simp mk!*sq quotsq(getv(getv(a,j),l),pivot));
                        % Mark row j as 'used'
                        putv(c,j,k);
                        not_changed:= {};
                        for l:=0:m do
                                if (getv(c,l) < 0) then not_changed:= l.not_changed;
                        % Eliminate column-entry k in 'unused' rows
                        for each h in not_changed do begin
                                pivot:= getv(getv(a,h),k);
                                for l:=0:k-1 do <<
                                        tmp:= simp mk!*sq multsq(pivot,getv(getv(a,j),l));
                                        tmp:= simp mk!*sq addsq(getv(getv(a,h),l),tmp);
                                        putv(getv(a,h),l,tmp);
                                >>;
                                putv(getv(a,h),k,simp(0));
                                for l:=k+1:n do <<
                                        tmp:= simp mk!*sq multsq(pivot,getv(getv(a,j),l));
                                        tmp:= simp mk!*sq addsq(getv(getv(a,h),l),tmp);
                                        putv(getv(a,h),l,tmp);
                                >>;
                        end; % of for each h in not_changed
                end; % of if (pivot neq nil)
        end; % of for k:=0:n
        timing('nullspace_triangulize);
        return {a, var};
end$

% ----------------------------------------------------------------------

symbolic procedure
        nullspace_triangulize_pivot(a, not_changed, m, n, k, nr_pref_va);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*GCD,
                        row, pivot, pivotsize, l1, l2, tmp;
        timing('nullspace_triangulize_pivot);
        off FACTOR, EXP, MCD, GCD;  % switch-setting
        pivot:= nil;
        pivotsize:= {10^10, 10^10};
        for each j in not_changed do begin
                for h:=k:nr_pref_va do begin
                        row:= getv(a,j);
                        tmp:= getv(row,h);
                        if (tmp neq simp(0)) then begin
                                l1:= nullspace_size(tmp);
                                if (l1 < car(pivotsize)+10) then begin
                                        l2:= (for r:=k:n sum
                                                nullspace_size(quotsq(getv(row,r),tmp)));
                                        if (l2 < cadr(pivotsize)+100) then begin
                                                pivot:= {j, h};
                                                pivotsize:= {l1, l2};
                                        end;
                                end;
                        end;  % of if
                end;  % of for h:=k:nr_pref_va
        end; % of for each j
        timing('nullspace_triangulize_pivot);
        if (nr_pref_va < n) and (pivot = nil) then
                return nullspace_triangulize_pivot(a, not_changed, m, n, k, n);
        return pivot;
end$

% ----------------------------------------------------------------------

symbolic procedure nullspace_triangulize_exchange_columns(a, j, k);
begin
        scalar length_a, tmp;
        if (j = k) then return a;
        length_a:= upbv(a);
        for l:=0:length_a do begin
                tmp:= getv(getv(a,l), j);
                putv(getv(a,l), j, getv(getv(a,l),k));
                putv(getv(a,l), k, tmp);
        end;
        return a;
end$

% ----------------------------------------------------------------------

symbolic procedure nullspace_triangulize_exchange_rows(a, j, k);
begin
        scalar tmp;
        if (j = k) then return a;
        tmp:= getv(a, j);
        putv(a, j, getv(a,k));
        putv(a, k, tmp);
end$

% ----------------------------------------------------------------------

symbolic procedure nullspace_sort_comp(l1, l2);
begin
        scalar z1, z2, len1, len2, zeroe;
        zeroe:= simp(0);
        z1:= 0;
        len1:= upbv(l1);
        while (z1 <= len1) and (getv(l1,z1) = zeroe) do z1:= z1+1;
        z2:= 0;
        len2:= upbv(l2);
        while (z2 <= len2) and (getv(l2,z2) = zeroe) do z2:= z2+1;
        if (z1 > z2) then return t else return nil;
end$


% ----------------------------------------------------------------------

symbolic procedure nullspace_bubblesort(l,fn);
begin
   scalar ln, tmp;
   ln:= upbv(l);
   for i:=0:ln do
      for j:=i+1:ln do
         if (i neq j) and apply2(fn,getv(l,j),getv(l,i)) then begin
            tmp:= getv(l,i);
                                putv(l, i, getv(l,j));
                                putv(l, j, tmp);
         end;
   return l;
end$


% ----------------------------------------------------------------------

symbolic procedure nullspace_sort(a);
begin
        scalar n, zeroelist, l, sorted_a;
        timing('nullspace_sort);
        a:= nullspace_bubblesort(a, 'nullspace_sort_comp);
        l:= upbv(getv(a,0));
        zeroelist:= mkvect(l);
        for j:=0:l do putv(zeroelist, j, simp(0));
        n:= 0;
        l:= upbv(a);
        while (n <= l) and (getv(a,n) = zeroelist) do n:= n+1;
        sorted_a:= mkvect(l-n);
        for j:=n:l do putv(sorted_a,j-n,getv(a,j));
        timing('nullspace_sort);
        return sorted_a;
end$

% ----------------------------------------------------------------------

symbolic procedure nullspace_matrix2solution(a, var);
begin
        scalar m, n, solu, tmp, row;
        timing('nullspace_matrix2solution);
        m:= upbv(a);
        n:= upbv(var);
        % All rows with zeroe entries (only) have been cancelled.
        % If the first row has n zeroes as first entries, then the
        % last one has to be different from zeroe, i.e. there is no
        % solution!
        solu:= (for j:=0:n collect getv(getv(a,0),j));
        if (solu = (for j:=0:n collect simp(0))) then
                return <<timing('nullspace_matrix2solution); 'list . nil>>;
        % Backsubstitution...
        % Append 1 to variables for righhandside of equation.
        solu:= mkvect(n+1);
        for j:=0:n do putv(solu, j, simp(getv(var,j)));
        putv(solu, n+1, simp(1));
        for j:=m step (-1) until 0 do begin
                tmp:= simp(0);
                row:= getv(a,m-j);
                for h:=j+1:n+1 do
                        tmp:= addsq(tmp, multsq(negsq(getv(row,h)),getv(solu,h)));
                putv(solu, j, quotsq(tmp, getv(row,j)));
        end; % of for j
        solu:= (for j:=0:n collect
                {'equal, getv(var,j), mk!*sq(getv(solu,j))});
        timing('nullspace_matrix2solution);
        return ('list . solu);
end$

% ----------------------------------------------------------------------

algebraic procedure nullspace_profile();
begin
        write "nullspace_coefflist:        ",
                showcputiming(nullspace_equations2sqmatrix);
        write "nullspace_triangulize:      ",
                showcputiming(nullspace_triangulize);
        write "nullspace_triangulize_pivot:",
                showcputiming(nullspace_triangulize_pivot);
        write "nullspace_sort:             ",
                showcputiming(nullspace_sort);
        write "nullspace_matrix2solution:  ",
                showcputiming(nullspace_matrix2solution);
        write "nullspace:                  ",
                showcputiming(nullspacesolve), "   (", showgctiming(nullspacesolve), ")";
end$

% ----------------------------------------------------------------------
% ======================================================================

algebraic procedure trace_qsum(text, term);
begin
        if (lisp !*qsum_trace) then
                write text, "    ", (sub(!*qsumrecursion!@sub, term));
end$

% ======================================================================
% ----------------------------------------------------------------------

symbolic procedure qsumrecursion_number(n, d);
begin
        scalar l, b;
        l:= explode reval n;
        b:= d-length(l);
        if (b > 0) then for j:=1:b do prin2(" ");
        for each j in l do prin1 compress list(j);
end;

% ----------------------------------------------------------------------

symbolic procedure qsumrecursion_qprofile;
begin
        scalar qrat, qupd, qdis, qfin, qsol, qdeg, qsum, qsgc, maxt, lmax;
        qrat:= reval showcputiming('qratios);
        qupd:= reval showcputiming('qupdate);
        qdis:= reval showcputiming('qdispersionset);
        qfin:= reval showcputiming('qfindf);
        qsol:= reval showcputiming('solve);
        qdeg:= reval showcputiming('qdegreebound);
        qsum:= reval showcputiming('qsumrecursion);
        qsgc:= reval showgctiming('qsumrecursion);
        maxt:= length explode max(qrat,qupd,qdis,qsol,qdeg,qsum);
        lmax:= length explode max(qdis,qsol,qsgc);
        prin2t " ";
        prin2  " qratios:        ";
        qsumrecursion_number(qrat, maxt);
        prin2t "";
        prin2  " qupdate:        ";
        qsumrecursion_number(qupd, maxt);
        prin2  "     (";
        qsumrecursion_number(qdis, lmax);
        prin2t " qdispersionset)";
        prin2  " qfindf:         ";
        qsumrecursion_number(qfin, maxt);
        prin2  "     (";
        qsumrecursion_number(qsol, lmax);
        prin2  " solve,  ";
        prin2  qdeg; %qsumrecursion_number(qdeg, lmax);
        prin2t " qdegreebound)";
        prin2  " qsumrecursion:  ";
        qsumrecursion_number(qsum, maxt);
        prin2  "     (";
        qsumrecursion_number(qsgc, lmax);
        prin2t " gc-time)";
end$

symbolic operator qsumrecursion_qprofile;

% ----------------------------------------------------------------------
% ======================================================================

clear binomial, qpochhammer, qfac, qbinomial, qbrackets, qfactorial;
operator binomial, qpochhammer, qfac, qbinomial, qbrackets, qfactorial;

% ======================================================================

algebraic procedure qpsihyperterm(nu, de, q, z, n);
begin
        scalar  r, s;
   r:= length(nu);
   s:= length(de);
        nu:= (for each j in nu product qpochhammer(j,q,n));
        de:= (for each j in de product qpochhammer(j,q,n));
   nu:= nu * (-1)^((s-r)*n) * q^((s-r)*n*(n-1)/2) * z^n;
   return nu/de;
end$

% ----------------------------------------------------------------------

algebraic procedure qphihyperterm(nu, de, q, z, n);
begin
        scalar r, s;
   r:= length(nu);
   s:= length(de);
        nu:= (for each j in nu product qpochhammer(j,q,n));
        de:= (for each j in de product qpochhammer(j,q,n));
   nu:= nu * z^n * ((-1)^n*q^(n*(n-1)/2))^(1+s-r);
   return nu/(de * qpochhammer(q,q,n));
end$


% ======================================================================
% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_standard_integer_part_sf(f);
begin
        scalar l, tmp, z;
        l:= nil;
        while pairp(f) do <<
                tmp:= qsimpcomb_standard_integer_part_sf(lc f);
                z:= ((mvar f).(ldeg f));
                repeat <<
                        l:= (((z.car(tmp)).nil) . l);
                        tmp:= cdr(tmp);
                >> until null(tmp);
                f:= red f;
        >>;
        if not(null f) then l:= (f . l);
        return l;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_standard_integer_part(z);
begin
        scalar !*BALANCED_MOD, !*EXP, !*FACTOR, !*RATIONAL, !*DMODE,
                n, d, tmp;
        on EXP;  off BALANCED_MOD, RATIONAL;  % switch-setting
        z:= simp aeval mk!*sq z;
        n:= numr z;
        d:= denr z;
        n:= qsimpcomb_standard_integer_part_sf n;
        if null(n) then return 0;
        z:= simp 0;
        repeat <<
                tmp:= simp mk!*sq (car(n) . d);
                if (fixp numr tmp) and (fixp denr tmp) then z:= addsq(z, tmp);
                n:= cdr n;
        >> until null(n);
        if eqn(denr z,1) then
                if null(numr z) then return 0 else return (numr z);
        n:= numr z;
        d:= denr z;
        z:= (car qremf(n,d));
        if (null(z) and !:minusp(n)) or !:minusp(z) then
                z:= addf(z,-1);
        if null(z) then return 0 else return z;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_standard_qexp_part_sf(f,q);
begin
        scalar p, z;
        p:= simp nil;
        while pairp(f) and (null (red f)) do <<
                if (mvar(f) eq q) then
                        p:= addsq(p, simp(ldeg f))
                else
                        begin
                                z:= mvar f;
                                if pairp(z) and (car(z) eq 'expt) and (cadr(z) eq q) then
                                        p:= addsq(p, simp({'times,caddr z,ldeg(f)}));
                        end;
                f:= lc f;
        >>;
        return p;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_standard_qexp_part(a,q,qe);
begin
        scalar !*FACTOR, !*EXP, n, d;
        on FACTOR;  % switch-setting
        a:= simp aeval mk!*sq a;
        n:= numr a;
        d:= denr a;
        n:= qsimpcomb_standard_qexp_part_sf(n,q);
        d:= qsimpcomb_standard_qexp_part_sf(d,q);
        n:= subtrsq(n,d);
        n:= qsimpcomb_standard_integer_part(quotsq(n,(simp qe)));
        d:= simp {'expt,q,{'times,mk!*sq(simp n),qe}};
        if null(simp aeval mk!*sq(subtrsq(a, d))) then
                n:= !:difference(n,-1);
        return (n);
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qpochhammer_finite(u);
begin
        scalar k, f, f1, jj;
        k:= caddr(u);
        f:= simp(1);
        if !:zerop(k) then return f;
        jj:= gensym();
        f1:= simp({'difference,1,{'times,car(u),{'expt,cadr(u),jj}}});
        if !:minusp(k) then
                (for j:=k:-1 do f:= quotsq(f,subsq(f1,{jj.j})))
        else <<
                k:= reval({'difference,k,1});
                for j:=0:k do f:= multsq(f,subsq(f1,{jj.j}));
        >>;
        return f;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qpochhammer_infinity(u,a,q,qe,k,m);
begin
        scalar jj, f, f2;
        if (k eq simp({'minus,'infinity})) or !:zerop(m) then
                return mksq(('qpochhammer.u),1)
        else if (k neq simp('infinity)) then
                rederr "Invalid arguments in qpochhammer.";
        f:= simp(1);
        jj:= gensym();
        a:= prepsq quotsq(a, simp {'expt,q,{'times,qe,m}});
        f2:= simp {'difference,1,{'times,a,{'expt,q,{'times,qe,jj}}}};
        if !:minusp(m) then % (m < 0)
                for j:=m:-1 do f:= multsq(f, subsq(f2, {jj.j}))
        else % (m >= 0)
                for j:=0:m-1 do f:= quotsq(f, subsq(f2, {jj.j}));
        f:= multsq(f, mksq({'qpochhammer,a,cadr(u),caddr(u)},1));
        return f;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qpochhammer(u);
begin
        scalar a, q, qq, qe, k, n, m, f, jj, f1, f2;

        if not eqn(length u,3) then
                rederr "Invalid number of arguments in qpochhammer";

        if fixp(caddr u) then
                return qsimpcomb_qpochhammer_finite(u);

        a:= simp car u;
        qq:= simp cadr u;
        q:= qq;
        k:= simp caddr u;

        % Die vereinfachten Argumente wieder als Liste nach u,
        % damit der zur"uckgelieferte qpochhammer-Term
        % standardisierte Argumente besitzt. (Sonst k"urzen sich diese
        % unter Umst"anden nicht ordentlich weg...)
        u:= {prepsq(a), prepsq(qq), prepsq(k)};

        if idp(cadr u) then <<
                qe:= 1;
                q:= mvar(numr q);
        >> else if eqn(denr q,1) then <<
                q:= numr q;
                qe:= ldeg q;
                if not eqn(lc q,1) or not(idp(mvar q)) then
                        rederr "Invalid arguments in qpochhammer";
                q:= mvar q;
        >> else if eqn(numr q,1) then <<
                q:= denr q;
                qe:= -(ldeg q);
                if not eqn(lc q,1) or not(idp(mvar q)) then
                        rederr "Invalid arguments in qpochhammer.";
                q:= mvar q;
        >> else
                rederr "Invalid arguments in qpochhammer.";

        if null(a) then return (simp 1);

        if (a eq qq) then
                m:= 0
        else <<
                m:= qsimpcomb_standard_qexp_part(a,q,qe);
                if (a eq simp({'expt,q,{'times,qe,m}})) and !:minusp(!:minus(m)) then
                        m:= !:difference(m,1);
        >>;
        n:= qsimpcomb_standard_integer_part(k);

        if !:zerop(n) and !:zerop(m) then
                return mksq(('qpochhammer.u),1);

        if not(freeof(k,'infinity)) then
                return qsimpcomb_qpochhammer_infinity(u,a,q,qe,k,m);

        f:= simp 1;
        jj:= gensym();
        qq:= cadr u;
        a:= prepsq quotsq(a, simp {'expt,q,{'times,m,qe}});
        k:= prepsq subtrsq(k,simp(n));
        f1:= simp {'difference,1,{'times,a,{'expt,q,{'times,qe,{'plus,jj,k}}}}};
        f2:= simp {'difference,1,{'times,a,{'expt,q,{'times,qe,jj}}}};
        if !:minusp(!:plus(n,m)) then  % (m+n < 0)
                if !:minusp(m) then <<  % (m < 0)
                        for j:=m+n:-1 do f:= quotsq(f, subsq(f1, {jj.j}));
                        for j:=m:-1 do f:= multsq(f, subsq(f2, {jj.j}));
                >> else << % (m >= 0)
                        for j:=m+n:-1 do f:= quotsq(f, subsq(f1, {jj.j}));
                        for j:=0:m-1 do f:= quotsq(f, subsq(f2, {jj.j}));
                >>
        else % (m+n >= 0)
                if !:minusp(m) then <<  % (m < 0)
                        for j:=0:n+m-1 do f:= multsq(f, subsq(f1, {jj.j}));
                        for j:=m:-1 do f:= multsq(f, subsq(f2, {jj.j}));
                >> else << % (m >= 0)
                        for j:=0:n+m-1 do f:= multsq(f, subsq(f1, {jj.j}));
                        for j:=0:m-1 do f:= quotsq(f, subsq(f2, {jj.j}));
                >>;
        u:= multsq(f, mksq({'qpochhammer,a,qq,k},1));
        return u;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_binomial(u);
begin
        scalar f, n, k;
        if not(fixp(cadr(u)) and (cadr(u) >= 0)) then
                return mksq({'binomial,car u,cadr u},1);
        n:= simp(car u);
        k:= cadr u;
        if eqn(k,0) then return simp(1);
        f:= simp 1;
        for j:=0:(!:difference(k,1)) do f:= multsq(f, subtrsq(n,simp(j)));
        f:= quotsq(f, simp({'factorial,k}));
        return f;
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qbinomial(u);
begin
        scalar n, k, q;
        n:= car u;
        k:= cadr u;
        q:= caddr u;
        u:= {'quotient,{'qpochhammer,q,q,n},{'times,
                {'qpochhammer,q,q,k},{'qpochhammer,q,q,{'difference,n,k}}}};
        return mksq(u,1);
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qbrackets(u);
begin
        scalar n, q;
        n:= car u;
        q:= cadr u;
        u:= {'quotient,{'difference,{'expt,q,n},1},{'difference,q,1}};
        return mksq(u,1);
end;

% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qfactorial(u);
begin
        scalar n, q;
        n:= car u;
        q:= cadr u;
        u:= {'quotient,{'qpochhammer,q,q,n},{'expt,{'difference,1,q},n}};
        return mksq(u,1);
end;


% ----------------------------------------------------------------------

symbolic procedure qsimpcomb_qfac(u);
begin
        return mksq(('qpochhammer . u), 1);
end;
% ----------------------------------------------------------------------

symbolic procedure qsimplify(f);
begin
        scalar !*precise, !*factor, !*exp, !*mcd, !*gcd, !*rational,
                redefmode, orig_bino, orig_qbin, orig_qbra, orig_qfct,
                orig_qfac, orig_qpoc;
        on FACTOR, MCD, GCD;  off RATIONAL, PRECISE;  % switch-setting

        if (length(f) neq 1) then
                rederr "Wrong number of arguments in qsimp";

        % Install the procedure new_simpexpt, which does more rigid
        % simplifications of powers and save original one
        % AND prevent redefined-messages.
        redefmode:= !*redefmsg;
        !*redefmsg:= nil;
        copyd('simpexpt, 'new_simpexpt);
        orig_bino:= get('binomial,    'simpfn);
        put('binomial,    'simpfn, 'qsimpcomb_binomial);

        f:= aeval(car f);

        % Get old 'simplify-functions' for q-expressions
        orig_qbin:= get('qbinomial,   'simpfn);
        orig_qbra:= get('qbrackets,   'simpfn);
        orig_qfct:= get('qfactorial,  'simpfn);
        orig_qfac:= get('qfac,        'simpfn);
        orig_qpoc:= get('qpochhammer, 'simpfn);

        % Declare all 'simplify-functions' for q-expressions
        put('qbinomial,   'simpfn, 'qsimpcomb_qbinomial);
        put('qbrackets,   'simpfn, 'qsimpcomb_qbrackets);
        put('qfactorial,  'simpfn, 'qsimpcomb_qfactorial);
        put('qfac,        'simpfn, 'qsimpcomb_qpochhammer);
        put('qpochhammer, 'simpfn, 'qsimpcomb_qpochhammer);

        % Simplify expression
        rmsubs();
        f:= mk!*sq(simp(reval f));

        % Hide all 'simplify-functions
        put('binomial,    'simpfn, orig_bino);
        put('qbinomial,   'simpfn, orig_qbin);
        put('qbrackets,   'simpfn, orig_qbra);
        put('qfactorial,  'simpfn, orig_qfct);
        put('qfac,        'simpfn, orig_qfac);
        put('qpochhammer, 'simpfn, orig_qpoc);

        % Restore old simpexpt and former !*redefmsg-mode
        copyd('simpexpt, 'original_simpexpt);
        !*redefmsg:= redefmode;

        return f;
end;

put('qsimpcomb, 'psopfn, 'qsimplify);

% ----------------------------------------------------------------------
% ======================================================================

algebraic procedure down_qratio(a, k);
begin
   a:= qsimpcomb(a / sub(k=k-1,a));
        return a;
end$

% ----------------------------------------------------------------------

algebraic procedure up_qratio(a, k);
begin
   a:= qsimpcomb(sub(k=k+1,a) / a);
        return a;
end$

% ----------------------------------------------------------------------

algebraic procedure qratio(a, k);
begin
   a:= qsimpcomb(sub(k=k+1,a) / a);
        return a;
end$

% ======================================================================

% ----------------------------------------------------------------------

% select patch by W. Neun 12.96

symbolic procedure select!-eval u;
 % select from a list l members according to a boolean test.
 begin scalar l,w,v,r;
  l := reval cadr u; w := car u;
  if atom l or (car l neq'list and not flagp(car l,'nary)) then
           typerr(l,"select operand");
  if idp w and get(w,'number!-of!-args)=1 then w:={w,{'~,'!&!&}};
  if eqcar(w,'replaceby) then <<v:=cadr w;w:=caddr w>>;
  w:=freequote formbool(w,nil,'algebraic);
  if v then w:={'replaceby,v,w};
  r:=for each q in
        pair(cdr map!-eval1(l,w,function(lambda y;y),'lispeval),cdr l)
      join if car q and car q neq 0 then {cdr q};
  if r then return car l . r;
  if (r:=atsoc(car l,'((plus . 0)(times . 1)(and . 1)(or . 0))))
    then return cdr r
   %else rederr {"empty selection for operator ",car l}
    else return list('list);
end$

% ======================================================================

algebraic procedure type_homogeneous(f,z);
begin
        scalar !*EXP, !*FACTOR, !*MCD, c, deg_f;
        on EXP, MCD;  % switch-setting
        if not(type_ratpoly(f,z)) then return nil;
        deg_f:= laurentdegree(f,z);
        c:= laurentcoeffn(f,z,deg_f);
        if ((f - c*z^deg_f) = 0) and freeof(c,z) then return t;
        return nil;
end$

% ----------------------------------------------------------------------

algebraic procedure qgosper_qprimedispersion(f, g, q, qk);
begin
        scalar !*EXP, !*FACTOR, !*GCD, !*MCD, n, m, a, b, c, d, j;
        on EXP, MCD;  off GCD; % switch-setting
        f:= f;
        n:= laurentdegree(f,qk);
        if (n = 0) or (n neq laurentdegree(g,qk)) then return {};
        m:= laurentldegree(f, qk);
        if (m = n) or (m neq laurentldegree(g, qk)) then return {};
        a:= laurentcoeffn(f,qk,n);
        b:= laurentcoeffn(f,qk,m);
        c:= laurentcoeffn(g,qk,n);
        d:= laurentcoeffn(g,qk,m);
        on GCD;  % switch-setting
        j:= a*d / (b*c);
        off GCD;  % switch-setting
        if not type_homogeneous(j,q) then return {};
        j:= laurentdegree(j,q) / (n-m);
        if not(fixp(j) and (-1 < j)) then return {};
        m:= sub(qk=qk*q^j, g);
        c:= laurentcoeffn(m, qk, n);
        if ((c*f-a*m) = 0) then return j;
        return {};
end$

% ----------------------------------------------------------------------

algebraic procedure qgosper_qdispersionset_simple_factorlist(p, x);
begin
        scalar !*EXP, !*FACTOR, !*GCD, !*LIMITEDFACTORS, !*MCD;
        on FACTOR, MCD;  off GCD, LIMITEDFACTORS;  % switch-setting
        p:= product2list(p);
        p:= (for each j in p collect if (arglength(j)>-1) and
                (part(j,0)=expt) and (fixp(part(j,2))) then part(j,1) else j);
        p:= select(not freeof(~z,x), p);
        return p;
end$

% ----------------------------------------------------------------------

algebraic procedure qgosper_qdispersionset(qq, rr, q, qk);
begin
        scalar disp, j;
        timing(qdispersionset);
        qq:= qgosper_qdispersionset_simple_factorlist(qq, qk);
        rr:= qgosper_qdispersionset_simple_factorlist(rr, qk);
        disp:= {};
        for each f in qq do
                for each g in rr do begin
                        j:= qgosper_qprimedispersion(f,g,q,qk);
                        if (j neq {}) and not(j member disp) then disp:= j.disp;
                end;
        trace_qsum("dispersionset:", disp);
        timing(qdispersionset);
        return disp;
end$

% ======================================================================

algebraic procedure qgosper_qupdate(pp, qq, rr, q, qk);
begin
        scalar !*FACTOR, !*EXP, !*MCD, !*DIV, !*GCD, !*LIMITEDFACTORS, disp, g;
        timing(qupdate);
        on FACTOR, MCD, DIV;  off LIMITEDFACTORS;  % switch-setting
        disp:= qgosper_qdispersionset(qq, rr, q, qk);
        for each j in disp do begin
                on EXP;  % switch-setting;
                g:= gcd(qq, sub(qk=qk*q^j,rr));
                on FACTOR;  % switch-setting
                if not freeof(g, qk) then begin
                        qq:= qq / g;
                        rr:= rr / sub(qk=qk/q^j, g);
                        pp:= pp * (for l:=0:j-1 product sub(qk=qk/q^l, g));
                end;  % of if
        end;  % of for
        trace_qsum("q-Gosper representation:", {pp, qq, rr});
        timing(qupdate);
        return {pp, qq, rr};
end$

% ======================================================================

algebraic procedure qgosper_qdegreebound_q_exponent(f, q);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*GCD, !*COMBINELOGS, !*EXPANDLOGS;
        on EXPANDLOGS, EXP, MCD, GCD;  OFF COMBINELOGS;  % switch-setting
        return log(f)/log(q);
end$

% ----------------------------------------------------------------------

algebraic procedure qgosper_qdegreebound(pp, qq, rr, q, qk);
begin
        scalar !*MCD, !*FACTOR, !*EXP, !*GCD,
                        ldegpp,ldegqq,ldegrr,ldegff,dd,ee,degpp,degqq,degrr,degff;
        timing(qdegreebound);
        on EXP, MCD;  off GCD;  % switch-setting
        % untere Gradschranke
        ldegpp:= laurentldegree(pp, qk);
        ldegqq:= laurentldegree(qq, qk);
        ldegrr:= laurentldegree(rr, qk);
        if (ldegqq neq ldegrr) then
                ldegff:= ldegpp - min(ldegqq, ldegrr)
        else begin
                dd:= laurentcoeffn(qq, qk, ldegqq);
                ee:= laurentcoeffn(rr, qk, ldegqq);
                ee:= qgosper_qdegreebound_q_exponent(ee/dd, q);
                if fixp(ee) then
                        ldegff:= min(ee,ldegpp) - ldegqq
                else
                        ldegff:= ldegpp - ldegqq;
        end; % of else
        % obere Gradschranke
        degpp:= laurentdegree(pp, qk);
        degqq:= laurentdegree(qq, qk);
        degrr:= laurentdegree(rr, qk);
        if (degqq neq degrr) then
                degff:= degpp - max(degqq, degrr)
        else begin
                dd:= laurentcoeffn(qq, qk, degqq);
                ee:= laurentcoeffn(rr, qk, degqq);
                ee:= qgosper_qdegreebound_q_exponent(ee/dd, q);
                if fixp(ee) then
                        degff:= max(ee,degpp) - degqq
                else
                        degff:= degpp - degqq;
        end; % of else
        timing(qdegreebound);
        if (degff < ldegff) then return {};
        return {ldegff, degff};
end$

% ======================================================================

symbolic procedure qsumrecursion_inds2arbcmplx(u);
begin
        scalar solu, var, arbsubs, gl, tmp, j;
        solu:= car u;
        if not(freeof(solu, 'arbcomplex)) then return solu;
        if null(cdr(solu)) then return 'list.nil;
        if (caadr(solu) eq 'list) then solu:= 'list. cdadr(solu);
        solu:= cdr(solu);
        var:= cdr(reval cadr(u));
        arbsubs:= nil;
        for each gl in solu do <<
                tmp:= var;
                while (tmp neq nil) do <<
                        j:= car(tmp);
                        tmp:= cdr(tmp);
                        if pairp(gl) and not(freeof(caddr(gl),j)) then <<
                                arbsubs:= {'equal,j,prepsq(!*f2q(makearbcomplex()))}.arbsubs;
                                var:= delete(j, var);
                        >>;
                >>;
        >>;
        if (arbsubs eq nil) then return car u;
        arbsubs:= 'list . arbsubs;
        tmp:= nil;
        while (solu neq nil) do <<
                gl:= car(solu);
                solu:= cdr(solu);
                if pairp(gl) then
                        caddr(gl):= reval({'sub, arbsubs, caddr(gl)});
                tmp:= gl . tmp;
        >>;
        tmp:= 'list . tmp;
        return tmp;
end$

put('qsumrecursion_indets2arbcomplex, 'psopfn, 'qsumrecursion_inds2arbcmplx);

% ======================================================================

algebraic procedure qgosper_qfindf(pqr, q, qk);
begin
        scalar !*EXP, !*FACTOR, !*MCD, !*CRAMER,
                        pp, qq, rr, d, var, f, a, i, eqn, solu;
        timing(qfindf);
        on EXP, MCD;  % switch-setting
        pp:= part(pqr, 1);
        qq:= part(pqr, 2);
        rr:= part(pqr, 3);
        d:= qgosper_qdegreebound(pp, qq, rr, q, qk);
        trace_qsum("degreebounds:", d);
        if (d = {}) then return <<timing(qfindf); {}>>;
        var:= (for j:=part(d,1):part(d,2) collect (lisp gensym()));
        f:= (for j:=part(d,1):part(d,2) sum part(var,j-part(d,1)+1)*qk^j);
        eqn:= sub(qk=qk*q,qq)*f - rr*sub(qk=qk/q,f) - pp;
        eqn:= laurentcoeff(eqn,qk);
        on CRAMER;  % switch-setting
        timing(solve);
        if (lisp !*qsum_nullspace) then
                solu:= nullspacesolve(eqn, var)
        else
                solu:= solve(eqn, var);
        timing(solve);
        on FACTOR;  % switch-setting
        if (solu = {}) then return <<timing(qfindf); {}>>;
        solu:= qsumrecursion_indets2arbcomplex(solu, var);
        f:= sub(solu, f);
        for each j in var do if not(freeof(f,j)) then
                sub(j=(lisp mk!*sq !*f2q makearbcomplex()), f);
        timing(qfindf);
        return f;
end$

% ======================================================================

% Old Version with f as laurentpolynomial:
% f:= (for j:=part(d,1):part(d,2) sum part(var,j-part(d,1)+1)*qk^j);
% eqn:= sub(qk=qk*q,qq)*f - rr*sub(qk=qk/q,f) - pp;
% eqn:= laurentcoeff(eqn, qk);

algebraic procedure qsumrecursion_qfindf_equations
        (pp, qq, rr, d, q, qk, sigma_var);
begin
        scalar !*EXP, !*FACTOR, !*LIMITEDFACTORS, !*MCD, !*CRAMER,
                        var, f, eqn, solu, ld;
        on EXP, MCD;  % switch-setting
        var:= (for j:=part(d,1):part(d,2) collect (lisp gensym()));
        if (part(d,1) < 0) then begin
                f:= (for j:=0:part(d,2)-part(d,1) sum part(var,j+1)*qk^j);
                ld:= -part(d,1);
                eqn:= sub(qk=qk*q^2,qq)*sub(qk=qk*q,f) - sub(qk=qk*q,rr)*f*
                                q^ld - sub(qk=qk*q,pp)*qk^ld*q^ld;
                end
        else begin
      f:= (for j:=part(d,1):part(d,2) sum part(var,j+part(d,1)+1)*qk^j);
                eqn:= sub(qk=qk*q^2,qq)*sub(qk=qk*q,f) - sub(qk=qk*q,rr)*f -
                                sub(qk=qk*q,pp);
        end;

        var:= append(sigma_var, var);
        timing(solve);
        if (lisp !*qsum_nullspace) then begin
                eqn:= coeff(eqn, qk);
                for each i in var do factor i;
                on FACTOR, MCD;  % switch-setting
                eqn:= eqn;
                solu:= nullspacesolve(eqn, var);
                for each i in var do remfac i;
                end
        else begin
                on CRAMER;  % switch-setting
                eqn:= coeff(eqn, qk);
                solu:= solve(eqn, var);
        end; % of else
        timing(solve);
        if (solu = {}) then return {};
        solu:= qsumrecursion_indets2arbcomplex(solu, var);
        if (lisp !*qsumrecursion_certificate) then <<
                f:= sub(solu, f);
        >> else
                f:= nil;
        solu:= {f, select(qsumrecursion_has(~w,sigma_var), solu)};
        if (lisp !*qsumrecursion_exp) and not(lisp !*qsum_nullspace) then
                on EXP  % switch-setting
        else
                on FACTOR;  % switch-setting
        solu:= reval solu;
        return solu;
end$

% ======================================================================

symbolic procedure qsumrecursion_has(z, varlist);
begin
        scalar has;
        has:= nil;
        repeat <<
                varlist:= cdr varlist;
                has:= not freeof(z, car varlist);
        >> until null(cdr varlist) or has;
        return has;
end$

symbolic operator qsumrecursion_has$

% ======================================================================

algebraic procedure qsumrecursion_qfindf(pqr, q, qk, sigma_var);
begin
        scalar !*FACTOR, !*EXP, !*LIMITEDFACTORS, !*MCD, !*CRAMER,
                        pp, qq, rr, d, var, f, a, i, eqn, solu;
        timing(qfindf);
        on EXP, MCD;  % switch-setting
        pp:= part(pqr, 1);
        qq:= part(pqr, 2);
        rr:= part(pqr, 3);
        d:= qgosper_qdegreebound(pp, qq, rr, q, qk);
        trace_qsum("degreebounds:", d);
        if (d = {}) then return <<timing(qfindf); {}>>;
        solu:= qsumrecursion_qfindf_equations(pp, qq, rr, d, q, qk, sigma_var);
        timing(qfindf);
        return solu;
end$

% ======================================================================

symbolic procedure qsumrecursion_range(x);
begin
        scalar lo, hi;
        if (length(qsumrecursion_recrange!*) neq 3) or
                not(pairp(qsumrecursion_recrange!*) and
                (car(qsumrecursion_recrange!*) = 'list)) then <<
                write "Global variable qsumrecursion_recrange!* must be a list";
                write "of two positive integers: {lo,hi} with lo<=hi.";
                rederr "Invalid value of qsumrecursion_recrange!*";
        >>;
        lo:= cadr(qsumrecursion_recrange!*);
        hi:= caddr(qsumrecursion_recrange!*);
        if not(fixp(lo) and fixp(hi) and (0<lo) and (lo<=hi)) then <<
                write "Global variable qsumrecursion_recrange!* must be a list";
                write "of two positive integers: {lo,hi} with lo<=hi.";
                rederr "Invalid value of qsumrecursion_recrange!*";
        >>;
        if null(x) then return {'list, lo, hi};
        if (length(x) neq 1) then rederr "Wrong type of arguments.";
        x:= car(x);
        if (fixp(x)) and (x > 0) then return {'list, x, x};
        if atom(x) or (car(x) neq 'list) or (length(x) neq 3) then
                rederr "Wrong type of arguments.";
        x:= cdr(x);
        lo:= car(x);
        hi:= cdr(x);
        if not(fixp(lo) and fixp(hi) and (lo<=hi) and (0<lo)) then
                rederr "Wrong type of arguments.";
        return {'list, lo, hi};
end$

% ----------------------------------------------------------------------

symbolic procedure qsumrecursion_qhyper(arg);
begin
        scalar nu, de, q, z, n;
        if (length(arg) < 5) then return nil;
        nu:= car(arg);
        if atom(nu) or (car(nu) neq 'list) then return nil;
        de:= cadr(arg);
        if atom(de) or (car(de) neq 'list) then return nil;
        arg:= cddr(arg);
        q:= car(arg);
        if not(idp(q)) then
                if atom(q) and (car(q) neq 'expt) or not(idp(cadr(q))) or
                        not(fixp(caddr(q))) then return nil;
        z:= cadr(arg);
        n:= caddr(arg);
        if not(idp(n) or ((length(n) = 2) and
                idp(car n) and idp(cadr n))) then return nil;
        return t;
end$

% ----------------------------------------------------------------------

symbolic procedure qsumrecursion(arg);
begin
        scalar nargs, f, q, k, n, recrange, prefac, nu, de, z, func;
        arg:= (for each j in arg collect reval j);
        nargs:= length(arg);
        if (nargs < 4) or (7 < nargs) then
                rederr "Wrong number of arguments.";
        q:= cadr(arg);
        % Is it a call like qsumrecursion(f,q,k,func,n)?
        if idp(q) then begin
                f:= car(arg);
                arg:= cddr(arg);
                k:= car(arg);
                n:= cadr(arg);
                if not(idp(k)) and not(idp(n) or
                        ((length(n) = 2) and idp(car n) and idp(cadr n))) then
                        rederr "Wrong type of arguments.";
                if idp(n) then
                        func:= 'summ
                else begin
                        func:= car(n);
                        n:= cadr(n);
                end;  % of if
                recrange:= qsumrecursion_range(cddr(arg));
                end
        else if qsumrecursion_qhyper(arg) then begin
                nu:= car(arg);
                de:= cadr(arg);
                q:= caddr(arg);
                arg:= cdddr(arg);
                z:= car(arg);
                k:= gensym();
                n:= cadr(arg);
                if idp(n) then
                        func:= 'summ
                else begin
                        func:= car(n);
                        n:= cadr(n);
                end;  % of if
                f:= qphihyperterm(nu,de,q,z,k);
                if not(idp(q)) then q:= cadr(q);
                recrange:= qsumrecursion_range(cddr(arg));
                end
        else if qsumrecursion_qhyper(cdr arg) then begin
                prefac:= car(arg);
                arg:= cdr(arg);
      nu:= car(arg);
      de:= cadr(arg);
      q:= caddr(arg);
      arg:= cdddr(arg);
      z:= car(arg);
      k:= gensym();
                n:= cadr(arg);
                if idp(n) then
                        func:= 'summ
                else begin
                        func:= car(n);
                        n:= cadr(n);
                end;  % of if
                f:= qphihyperterm(nu,de,q,z,k);
      f:= aeval {'times, prefac, f};
                if not(idp(q)) then q:= cadr(q);
      recrange:= qsumrecursion_range(cddr(arg));
                end
        else
                rederr "Wrong type of arguments.";
        f:= qsumrecursion_eval(f,q,k,func,n,recrange);
        return f;
end$

put('qsumrecursion, 'psopfn, 'qsumrecursion);

% ----------------------------------------------------------------------

symbolic procedure qgosper(arg);
begin
        scalar f, q, k, m, n;
        arg:= (for each j in arg collect reval(j));
        if (length(arg) neq 3) and (length(arg) neq 5) then
                rederr "Wrong number of arguments.";
        f:= car(arg);
        q:= cadr(arg);
        k:= caddr(arg);
        if not(idp(q)) or not(idp(k)) then
                rederr "Wrong type of arguments.";
        if freeof(f,k) then <<
                write "WARNING: Summand is independent of summation variable.";
                rederr "No q-hypergeometric antidifference exists.";
        >>;
        arg:= cdddr(arg);
        if not(null(arg)) then begin
                m:= car(arg);
                n:= cadr(arg);
                %if not(freeof(m,k)) or not(freeof(n,k)) then
                %       rederr "Summation bounds contain the summation variable.";
        end;
        f:= qgosper_eval(f,q,k);
        if not(null(arg)) then begin
                f:= simp(f);
                if !*qgosper_down then
                        m:= aeval {'plus, m, list('minus, 1)}
                else
                        n:= aeval {'plus, n, 1};
                f:= subtrsq(subsq(f,{k . n}), subsq(f,{k . m}));
                f:= mk!*sq(f);
        end;  % of if
        return f;
end$

put('qgosper, 'psopfn, 'qgosper);

% ======================================================================

algebraic procedure qgosper_eval(a, q, k);
begin
        scalar !*PRECISE, !*EXP, !*FACTOR, !*MCD, qk, pqr, f, redefmode;
        on FACTOR, MCD; off PRECISE;  % switch-setting

        % Turn off function-has-been-redefined-messages.
        share redefmode;
        redefmode:= (lisp !*redefmsg);
        lisp (!*redefmsg:= nil);

        % Set new_simpexpt as standard which does more simplifications
        % on power-terms:
        copyd('simpexpt, 'new_simpexpt);

        qk:= (lisp gensym());
        f:= down_qratio(a,k);
% qsimpcomb_simpexpt shouldn't be necessary any longer (new_simpexpt!)
% f:= qsimpcomb_simpexpt(down_qratio(a,k), q);

        if (lisp !*qsum_trace) then
                write "Applied substitution: ", q^k=k;
        !*qsumrecursion!@sub:= {qk=k};
        trace_qsum("down ratio wrt. k:", sub(qk=k,f));
        f:= (f where (q^k=>qk));
        if not(freeof(f,k)) then
                rederr "Input term is probably not q-hypergeometric.";

        pqr:= qgosper_qupdate(1, num(f), den(f), q, qk);
        f:= qgosper_qfindf(pqr, q, qk);

        if (f = {}) then
                rederr "No q-hypergeometric antidifference exists.";
        if (lisp !*qgosper_down) then % Gosper downwards
                f:= sub(qk=q^(k+1), part(pqr,2)) * sub(qk=q^k, f/part(pqr,1)) * a
        else % Gosper upwards:
                f:= sub(qk=q^k, part(pqr,3)/part(pqr,1)) * sub(qk=q^(k-1), f) * a;

        if (lisp !*qgosper_specialsol) then
                f:= (f where (arbcomplex(~z) => 0));

        % restore simpexpt and proper redefmsg-mode...
        copyd('simpexpt, 'original_simpexpt);
        lisp (!*redefmsg:= redefmode);
        return f;
end$

% ======================================================================
% ======================================================================

algebraic procedure qsumrecursion_denom_lcm(dl);
begin
        scalar !*FACTOR, !*EXP, !*GCD, !*MCD, g;
        on FACTOR, MCD, GCD;  % switch-setting
        g:= (part(dl,1)*part(dl,2)/gcd(part(dl,1),part(dl,2)));
        if (length(dl) = 2) then return g;
        dl:= (for j:=3:length(dl) collect j);
        return qsumrecursion_denom_lcm(g . dl);
end$

% ======================================================================

algebraic procedure qsumrecursion_denom(req, vars);
begin
        scalar !*FACTOR, !*EXP, !*GCD, !*MCD, numer, denom;
        on FACTOR, MCD, GCD;  % switch-setting
        numer:= (for each j in vars collect coeffn(req,j,1)*j);
        denom:= (for each j in numer collect den(j));
        denom:= qsumrecursion_denom_lcm(denom);
        numer:= (for each j in numer collect j*denom);
        off FACTOR; off EXP; % lisp setq(!*really_off_exp,t);  % switch-setting
        return (for each j in numer sum j);
end$

% ======================================================================

algebraic procedure qsumrecursion_qratios(f, q, k, qk, n, qn);
begin
        scalar !*FACTOR, !*EXP, !*MCD, !*GCD, !*LIMITEDFACTORS, kn_ratio;
        on FACTOR, MCD;  off GCD, LIMITEDFACTORS;  % switch-setting
        timing(qratios);
        kn_ratio:= {down_qratio(f,k), qratio(f,n)};
        kn_ratio:= (kn_ratio where {q^k=>qk, q^n=>qn});
        !*qsumrecursion!@sub:= {qk=k, qn=n};
        if not freeof(kn_ratio,k) then
                %<<write kn_ratio; rederr "bad qratios...">>;
                rederr "Input term is probably not q-hypergeometric.";
        trace_qsum("Applied the substitutions:", {q^k=>k, q^n=>n});
        trace_qsum("down ratio wrt. k:", part(kn_ratio,1));
        trace_qsum("up ratio wrt. n:", part(kn_ratio,2));
        timing(qratios);
        return kn_ratio;
end$

% ======================================================================


algebraic procedure qsumrecursion_eval(f, q, k, summ, n, recrange);
begin
        scalar !*PRECISE, !*FACTOR, !*EXP, !*MCD, !*GCD, !*LIMITEDFACTORS,
                        redefmode, qk, qn, rk, rn, lo, hi, a, poly, sigmalist,
                        record, pqr, fpol, solu, cert;
        timing(start); timing(qsumrecursion);

        on FACTOR, MCD;  off PRECISE, GCD, LIMITEDFACTORS;  % switch-setting
        % Turn off function-has-been-redefined-messages.
        share redefmode;
        redefmode:= (lisp !*redefmsg);
        lisp (!*redefmsg:= nil);

        % Set new_simpexpt as standard which does more simplifications
        % on power-terms:
        copyd('simpexpt, 'new_simpexpt);

        lo:= part(recrange, 1);
        hi:= part(recrange, 2);
        qk:= (lisp gensym());
        qn:= (lisp gensym());
%clear sigma; operator sigma;
   rn:= qsumrecursion_qratios(f, q, k, qk, n, qn);
        rk:= part(rn, 1);
        if (lisp !*qsumrecursion_down) then
                rn:= 1 / sub(n=n-1, qn=qn/q, part(rn, 2))
        else
                rn:= part(rn, 2);
        poly:= 1;
        record:= 0;
        sigmalist:= {};
        repeat begin
                record:= record + 1;
                sigmalist:= append(sigmalist, {lisp intern gensym()});
%!*qsumrecursion!@sub:= append(!*qsumrecursion!@sub,
%       {first reverse sigmalist=sigma(record)});
                if (lisp !*qsumrecursion_down) then
                        a:= (for l:=0:record-1 product sub({n=n-l, qn=qn/q^l}, rn))
                else
                        a:= (for l:=0:record-1 product sub({n=n+l, qn=qn*q^l}, rn));
                on GCD;  % switch-setting???
                poly:= poly + part(sigmalist,record)*a;
                fpol:= {};
                if (record >= lo) then begin
                        a:= rk * sub(qk=qk/q, den(poly)) / den(poly);
                        off GCD;  % switch-setting???
%trace_qsum("rat:=", a);
                        pqr:= qgosper_qupdate(num(poly), num(a), den(a), q, qk);
                        fpol:= qsumrecursion_qfindf(pqr, q, qk, sigmalist);
                end;
        end until (fpol neq {}) or (record = hi);
        if (fpol = {}) then
                rederr "Found no recursion. Use higher order.";
        solu:= part(fpol, 2);
        fpol:= part(fpol, 1);
        if (lisp !*qsumrecursion_down) then
                rec:= summ(n) + (for j:=1:record sum part(sigmalist,j)*summ(n-j))
        else
                rec:= summ(n) + (for j:=1:record sum part(sigmalist,j)*summ(n+j));
        if (lisp !*qsumrecursion_exp) then
                on EXP  % switch-setting
        else
                on FACTOR;  % switch-setting
        factor summ;
        rec:= sub(solu, rec);
        if (lisp !*qsumrecursion_certificate) then begin
                pqr:= sub(solu, pqr);
                cert:= den(rec) * sub(solu, poly);
                if (lisp !*qgosper_down) then << % Gosper downwards
                        cert:= cert * sub(qk=qk*q,part(pqr,2))*fpol/part(pqr,1);
                        a:= downward_antidifference;
                >> else <<% Gosper upwards:
                        cert:= cert * part(pqr,3)/part(pqr,1)*sub(qk=qk/q,fpol);
                        a:= upward_antidifference;
                >>;
                rec:= {num rec, cert, f, k, a};
                end
        else
                rec:= num rec;
        timing(qsumrecursion);
        if (lisp !*qsumrecursion_profile) then qsumrecursion_qprofile();

        % restore original simpexpt and redefmsg-mode...
        copyd('simpexpt, 'original_simpexpt);
        lisp (!*redefmsg:= redefmode);

        return sub(qn=q^n, qk=q^k, rec);
end$

% ======================================================================
% ======================================================================


lisp setq(!*redefmsg, nth(!*qsumrecursion!@sub,1));
lisp setq(!*echo, nth(!*qsumrecursion!@sub,2));
lisp setq(!*output, nth(!*qsumrecursion!@sub,3));

endmodule;

$end$

