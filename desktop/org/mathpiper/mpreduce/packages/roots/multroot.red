module multroot;  % Code for solving polynomial sets solvable by
                  % backsubstitution.

% Author: Stanley L. Kameny <stan_kameny@rand.org>.

% Version and Date:  Mod 1.96, 30 March 1995.

% Copyright (c) 1994,1995.  Stanley L. Kameny.  All Rights Reserved.

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


Comment  modules allroot, bfauxil, bfdoer, bfdoer2, complxp, rootaux and
         realroot needed also;


fluid '(rterr!! mrerr!! rootacc!#!#);

switch fullprecision,compxroots;

put ('multroot,'psopfn,'multroot1);

symbolic procedure multroot1 u;
  if length u neq 2 then rederr
  "2 args required: pr=desired precision, pl=polynomial list"
  else multroot0(car u,cadr u);

symbolic procedure multroot0(pr,pl);
  begin scalar v,ans,pr1,c,r,rterr!!,ra;
    !*protfg := t; pr1 := precision 0; r := !*rounded; c := !*complex;
    ra := rootacc!#!#;
  v := errorset!*({'multroot2,{'multroot01,mkquote pr,mkquote pl}},nil);
    !*protfg := nil; rootacc!#!# := ra;
    return if errorp v then
      <<precision pr1;
        (<<if !*rounded then if not r then off rounded else nil
              else if r then on rounded;
           if !*complex then if not c then off complex else nil
              else if c then on complex>> where !*msg=nil);
           (if rterr!! then lprim
"for some root value(s), a variable depends on an arbitrary variable"
              else if mrerr!! then lprim mrerr!!) where !*msg=t;
        mk!*sq mksq({'multroot,pr,reval pl},1)>>
      else (<<v := car v; on rounded,complex; ans := aeval v;
              if !*rounded then if not r then off rounded else nil
                 else if r then on rounded;
              if !*complex then if not c then off complex else nil
                 else if c then on complex;
              ans>> where !*msg=nil) end;

share npoly!*,pr!*,pl!*;

algebraic procedure multroot01(pr,pl);
comment
pl is a list of n polynomials in a tree containing branches each one of
which contains one univariate polynomial, one bivariate polynomial, ...
one n-variable polynomial in which each successive polynomial adds one
additional variable.  These branches may branch, so that one variable
gives rise to several branches.  These polynomials are the standard
Groebner output.  All polynomials are real with integer coefficients.
pr is the desired minimum precision of the solution set for real roots.
This program will go through each branch by solving p_1 for the first
variable, then will test each succeeding polynomial for the precision of
each additional variable, returning to the initial solution at higher
precision when necessary, until the last polynomial's solution has been
obtained at precision >= pr.  The solutions are collected in the
variable solns!*.  They are then combined into the final output form by
the function combinesolns();
   begin scalar n,links,path,paths,fl,fl1,var,rts,solns; integer maxv;
     npoly!* := n := length pl - 1;
    % 0..n will be the indices of arrays.
     clear vll!#,vll2!#,varbl!#,poln!#,rtlst!#,derv!#,pra!#,fxer!#;
     array vll!#(n),vll2!#(n);
     pl!* := pl;
     pl := pl!* := reval cleardenr pl!*;
    % vll!# will be an array of {list of variables}
     for j := 0:n do vll!# j := getvars part(pl,j+1);
    % vll!# will be a useful tool for finding and tracing trees.
    % Trees might possibly be totally independent if there are more
    % than one of the vll!#(j) of length 1, or else trees might branch
    % if there is more than one way of extending the variable lists from
    % a given node.  The tree following algorithm must allow for any of
    % these.  It will be easiest to have a tree algorithm which simply
    % enumerates branches as a list of the j values which span the total
    % branch from root to tip.
     links := findlinks n;
     paths := links2paths links;
    % if paths = nil then multroot fails.
     lisp(mrerr!! := nil);
     if not paths then lisp(mrerr!! :=
       "multroot fails because no univariate polynomial was given.");
     lisp(if mrerr!! then rederr "1");
     fl := nil;
     for j := 0:n do
       <<fl1 := nil;
         for each path in paths do if member(j,path) then fl1 := t;
         if not fl1 then fl := t >>;
     if fl = t then lisp(mrerr!! :=
       "multroot failure: at least one polynomial has no single base.");
     lisp(if mrerr!! then rederr "2");
    % In multroot2, we solve for real roots under the condition that
    % the precision of each root >= pr;
     array varbl!#(n),poln!#(n),rtlst!#(n),derv!#(n),pra!#(n),fxer!#(n);
    % these arrays are used in solvepath, but we don't want them to be
    % redefined for different paths.  Maximum index will be <=n, but we
    % will be careful not to go out of bounds.
     vlist!* := rlist!* := solns!* := {};
     pr!* := pr;
     return paths end;

symbolic operator subsetp,algunion,cleardenr;

symbolic procedure cleardenr pl;
   begin scalar plo;
     for each pol in cdr pl do
        plo := (if eqcar(pol,'quotient) then cadr pol else pol) . plo;
     return 'list . reversip plo end;

symbolic procedure algunion(a,b); 'list . union(cdr a,cdr b);

algebraic procedure findlinks n;
   begin scalar links,fl,fl1,var;
     links := {};
    % we have in vll!# an array of lists of variables.  If they can form
    % a strict hierarchy, we have no problem in solving the polynomials.
    % But if they don't, we have to form an artificial hierarchy by
    % augmenting the lists.
     for m := 1:n do
       if m=1 then for j := 0:n do vll2!# j :=
         if length(fl := vll!# j)=1 then
           if not var then var := fl else vll!# j := append(fl,var)
           else fl
         else for k := 1:2 do
           for j := 0:n do
             if length(fl := vll!# j)=m then vll2!# j :=
               if length var<m then if subsetp(var,fl) then var := fl
                     else fl
                 else if fl=var then fl else vll!# j := algunion(fl,var)
               else fl;
     repeat <<fl := nil;
      for j := 0:n do
       <<fl1 := nil;
         for k := 0:n do
           if j neq k
            and length vll2!# j =1 and subset1(vll2!# j,vll2!# k) then
            <<links := append(links,{{j,k}}); fl1 := t>>;
         if fl1=t then
         <<fl := t;
           for k := 0:n do
           <<var := first vll2!# j;
            if j neq k then vll2!# k := delete(var,vll2!# k)>> >> >> >>
      until not fl ;
    return links end;

algebraic procedure multroot2 paths;
  begin scalar path,lfp,fl,soln1,soln2,pr0,nlst;
 lp: path := first paths; paths := rest paths;
     lfp := last path; fl := nil;
     for each path2 in paths do if last path2 = lfp then fl := t;
     if fl then <<lisp(mrerr!! :=
         "multroot failure: This error should not occur!");
       rederr "3">>;
    % this is the place where it is reasonable to eliminate spurious
    % real or imaginary parts of complex roots.
     soln1 := solvepath path;
     if lisp !*compxroots and (nlst := spurival soln1) neq {} then
       <<pr0 := precision 0; pr!* := pr!*+5;
         soln2 := solvepath path; precision pr0; pr!* := pr!*-5;
         soln1 := spurifix(nlst,soln1,soln2)>>;
     if not member(v0!*,vlist!*) then
       % here we save the initial roots or realroots answers so they
       % won't be computed redundantly.
       <<vlist!* := append(vlist!*,{v0!*});
         rlist!* := append(rlist!*,{r0!*})>>;
     if soln1={} then <<fl := t; go to cl>>;
     solns!* := append(solns!*,{soln1});
     if paths neq {} then go to lp;
 cl: clear vll!#,vll2!#,varbl!#,poln!#,rtlst!#,derv!#,pra!#,fxer!#;
     return if fl then {} else combinesolns() end;

algebraic procedure combinesolns();
comment
 We have all of the separate solutions in solns!* and a list of the
independent variables in vlist!* and the root values of the base
variables in the list rlist!*.  So we can use this information to
combine the separate solutions into an outer product of all solutions.
In doing this, we will first combine all terms with the same base
variable, then combine all of those outer products into one grand
product.  Finally, we sort the variables into standard order and sort
the values into order by the real part of the first variable.$
  begin scalar vlist,rlist,prod,solns,var,rts,grandprod;
     vlist := vlist!*; rlist := rlist!*; grandprod := {};
lp2: var := first vlist; vlist := rest vlist;
     rts := first rlist; rlist := rest rlist;
     prod := {}; solns := solns!*;
     if rts neq {} and solns neq {{}} then
       for each soln1 in solns do
         if isvar(first soln1,var)
           then prod := if prod = {} then soln1
             else outcombine1(rts,prod,soln1);
     grandprod := if grandprod = {} then prod
        else outcombine2(grandprod,prod);
     if vlist neq {} then go to lp2;
     grandsoln!* := grandprod;
     sortvars();
     screensolns1();
     return sortvals();
     end;

symbolic operator screensolns1;

symbolic procedure screensolns1();
   begin scalar inlist,outlist,termout,vr,vr1,vl,vl1,fl;
     inlist := reversip cdr algebraic varsortsolns!*;
     for each rts in inlist do
       <<vr := vr1 := fl := termout := nil;
         for each term in cdr rts do if not fl then
           <<vr1 := cadr term; vl1 := caddr term;
%            if not vr or vr neq vr1 then
             if not vr or algebraic lisp {'difference,vr,vr1} neq 0 then
                 <<vr := vr1; vl := vl1; termout := term . termout>>
%              else if vl1 neq vl then fl := t>>;
               else if algebraic lisp {'difference,vl1,vl} neq 0
                then fl := t>>;
        if not fl then outlist := ('list.reversip termout).outlist>>;
      return algebraic varsortsolns!* := ('list . outlist) end;

algebraic procedure solvepath path;
   begin scalar
    n,vl,vlll,m,s,rts0,fl,fl1,b,tst,f,ff,pr1,prf,strt,dfx,rtl,rt1,!*msg,
    zz,r,c;
     n := length path;
     if (r := lisp !*rounded) then off rounded;
     if (c := lisp !*complex) then off complex;
    % now we assemble the polynomial tree which represents this path.
     vl := for j := 1:n collect part(pl!*,part(path,j)+1);
    % we now have ordered the polynomials in order of the number of
    % variables.
     vlll := for j := 1:n collect vll!# part(path,j);
    % and have now ordered the variable list in increasing number of
    % variables, so that vlll correctly lists the variables in the
    % reordered list vl;
    % Now we solve for real roots under the condition that the precision
    % of each root >= pr!*;
     n := n-1; % this is done because arrays will have indices 0..n.
     pra!#(0) := pr!*+10; on rounded;
     for j := 1:n do pra!#(j) := pr!*;
    % a starting point: this may have to be increased if necessary.
     v0!* := varbl!#(0) := first first vlll;  strt := t;
str: precision pra!#(0);
     rts0 := if strt and member(v0!*,vlist!*)
       then (r0!* := part(rlist!*,membno(v0!*,vlist!*)))
       else if lisp !*compxroots then roots first vl
         else realroots first vl;
     r0!* := rts0;
     rtlst!#(0) := for each rt in rts0 collect {rt};
     m := 0; strt := nil;
nxt: fl := fl1 := b := 0;
     if (m := m+1) > n then <<m := m-1; go to ret>>;
     poln!#(m) := part(vl,m+1);
     rtl := {};
     for each rt in rtlst!#(m-1) do
       <<zz := sub(rt,poln!#(m));
        % zz is a polynomial, or possibly a constant.  If it is 0
        % then whatever variables it might represent are arbitrary, but
        % if is a nonzero constant, the path stops here.
         rt1 := if mainvar zz=0 then if zz=0 then {rt} else {}
          else combinerts(rt,
           <<lisp(rterr!! := t);
             zz := if lisp !*compxroots then roots zz else realroots zz;
             lisp(rterr!! := nil); zz>>);
         if rt1 neq {} then rtl := append(rtl,rt1)>>;
     rtlst!#(m) := rtl;
     s := length rtlst!#(m);
     varbl!#(m) := elim(part(vlll,m),part(vlll,m+1));
     derv!#(m) :=
       {-(df(poln!#(m),varbl!#(0))+
         if m<2 then 0 else for j := 1:(m-1) sum
           (df(poln!#(m),varbl!#(j))*first derv!#(j)/second derv!#(j)))
        ,df(poln!#(m),varbl!#(m))};
lp1: if (b := b+1) > s then
       <<prf := nil;
         if fl > 0 then
          <<fxer!#(m) := pfx(pr!*,fl); fl := 0;
            dfx := fxer!#(m) - fxer!#(m-1);
            for j := 0:m-1 do
             <<pr1 := pra!#(j) + dfx;
               if pr1>pra!#(j) then
                 <<prf := t; pra!#(j) := pr1>> >> >>;
         if prf then go to str else go to nxt>>
       else
         <<if (tst :=
             abs(10.0^(-pra!#(0))*cabs
                testsub(part(rtlst!#(m),b),derv!#(m),m)) )>10.0^-pr!*
             then fl1 := tst;
           if fl1 > fl then fl := fl1;
           go to lp1>>;
ret: if r then on rounded; if c then on complex;
     if not lisp !*fullprecision then go to rt2;
     precision pra!#(0); return
       if m=0 then rtlst!#(0)
       else for each rtl in rtlst!#(m) collect
         for j := 0:m collect roundroot(part(rtl,j+1),pra!#(j));
rt2: precision pr!*;
     return rtlst!#(m)
     end;

algebraic procedure testsub(lst,quotlst,m);
 % this substitutes the variable value list lst into the derivative
 % quotient quotlst, but avoids 0/0 errors.  For the purposes to which
 % we are putting testsub, infinity is replaced by 1.
  begin scalar nmr,dnr,dnv;
    nmr := first quotlst; dnr := second quotlst;
    ex!! := algebraic nmr/algebraic dnr;
    nmr := num ex!!; dnr := den ex!!;
    while (dnv := sub(lst,dnr))=0 do if sub(lst,nmr)=0 then
      <<nmr := df(nmr,varbl!#(m)); dnr := df(dnr,varbl!#(m));
        if dnr neq 0 then
          <<ex!! := algebraic nmr/algebraic dnr;
            nmr := num ex!!; dnr := den ex!!>>
          else (<<nmr := 1; dnr := 1;
                 lprim "stuffing 1 (dnr prob)">> where !*msg=t) >>
      else (<<nmr := 1; dnr := 1;
              lprim "stuffing 1 (nmr prob)">> where !*msg=t);
    return sub(lst,nmr)/dnv end;

symbolic procedure sortvals1(a,b);
   begin scalar c,d; a := cdr a; b := cdr b;
    % since some variables (and hence their values) may not occur...
 lp: if not a or not b then return nil;
     c := caddar a; d := caddar b;
     algebraic (c := repart c); algebraic (d := repart d);
     algebraic if c < d then return t else if c = d then go to tst;
     return nil;
tst: if (a := cdr a) then <<b := cdr b; go to lp>> end;

algebraic procedure getvars p;
   begin scalar vl,v,v1,lt,c,!*msg;
     c := lisp !*complex; if not c then on complex;
     vl := {};
lp1: if numberp(v := mainvar p) then go to ret
        else if not member(v,vl) then vl := v . vl;
     lt := lcof(p,v); p := reduct(p,v);
lp2: if numberp(v1 := mainvar lt) then goto lp1
        else if not member(v1,vl) then vl := v1 . vl;
     lt := lcof(lt,v1); go to lp2;
ret: if not c then off complex;
     return reverse vl end;

symbolic operator spurival,spurifix;
share val!$,val2!$,eps!$,pr!*;

symbolic procedure spurival vals;
  % produces a list of flattened index of suspicious terms (starting at
  % 1) or {}.  spurifix then checks the indexed items and trims spurious
  % values.
   begin scalar fl,r,c,!*msg; integer m; eps!$ := 10.0^-pr!*;
     r := !*rounded; c := !*complex; on rounded; off complex;
     for each rlst in cdr vals do for each val in cdr rlst do
        <<m := m+1; val!$ := prepsq simp!* caddr val;
          if eqcar(val!$,'plus) and algebraic testval1 val!$
             then fl := m . fl>>;
     if not r then off rounded; if c then on complex;
     return 'list . reversip fl end;

algebraic procedure testval1 val;
  begin scalar rl,im;
    rl := abs repart val; im := abs impart val;
    if rl>0 and im>0 and im<eps!$*rl or rl<eps!$*im then return t end;

symbolic procedure spurifix(ndx,soln1,soln2);
  % soln1 is vals used in spurival, soln2 is vals for higher precision,
  % and ndx is flattened index of those to be tested.  All indexed
  % pairs of roots will be checked and a new soln1 list is made with
  % unmatched small parts of complex roots stripped off.
   if null ndx or length soln1 neq length soln2 then soln1 else
   begin scalar sol0,soln3,lst1,lst2,lst3,val1,val2,eq1,eq2,
           eq3,r,c,!*msg,tri;
         integer m,m0;
      r := !*rounded; c := !*complex; on rounded; off complex;
      soln1 := cdr (sol0 :=soln1); soln2 := cdr soln2; ndx := cdr ndx;
      m0 := car ndx; ndx := cdr ndx;
 lp1: if not soln1 then <<soln3 := 'list . reversip soln3; go to ret>>;
      lst1 := cdar soln1; lst2 := cdar soln2;
      soln1 := cdr soln1; soln2 := cdr soln2;
      if length lst1 neq length lst2 then <<soln3 := sol0; go to ret>>;
 lp2: if not lst1 then
         <<soln3 := ('list . reversip lst3) . soln3;
           lst3 := nil; go to lp1>>;
      eq1 := car lst1; eq2 := car lst2; m := m+1;
      lst1 := cdr lst1; lst2 := cdr lst2;
      if not m0 or m<m0 then <<lst3 := eq1 . lst3; go to lp2>>;
      if not ndx then m0 := nil else
         <<m0 := car ndx; ndx := cdr ndx>>;
      eq1 := eq1;
      val!$ := prepsq simp!* (val1 := caddr eq1);
      val2!$ := prepsq simp!* (val2 := caddr eq2);
      tri := algebraic testval2(val!$,val2!$);
      if tri=aeval 'failed then
        <<((lprim
             "match failed! root stripping aborted: raw roots returned")
             where !*msg=t); soln3 := sol0; go to ret>>;
      eq3 := if tri=0 then eq1
         else {'equal,cadr eq1,
                if freeof(car(val1 := cdr val1),'i)
                  then if tri=1 then cadr val1 else car val1
                  else if tri=1 then car val1 else cadr val1};
      lst3 := eq3 . lst3; go to lp2;
 ret: if not r then off rounded; if c then on complex;
      return soln3 end;

algebraic procedure testval2(a,b);
   begin scalar rl1,rl2,im1,im2;
      rl1 := abs repart a; rl2 := abs repart b; im1 := abs impart a;
      im2 := abs impart b;
      return if rl1=rl2 then if im1=im2 then 0 else 2
                else if im1=im2 then 1 else failed end;

%%end;    %this is the end of the changed or added functions

symbolic procedure isvar(x,var);
  (if eqcar(x,'list) then cadadr x else cadr x)=var;

symbolic operator isvar;

algebraic procedure outcombine1(rts,p1,p2);
 % here p1 is an outerproduct, and p2 is another outerproduct with
 % the same first variable. from the two, we form a single outerproduct
 % by forming all lists with the first variable appearing only once.
 % rts is the root list for the base variable.  A complication is caused
 % by the possibility that one of more root values may be missing from
 % p1, p2, or both.
  begin scalar prod,p1strt,p1end,p2strt,p2end;
     prod := {};
     for each rt in rts do
   <<p1end := second(p1strt := findvals(p1,rt)); p1strt := first p1strt;
     p2end := second(p2strt := findvals(p2,rt)); p2strt := first p2strt;
     if p1strt > 0 and p2strt > 0 then
       for n1 := p1strt:p1end do for n2 := p2strt:p2end do
         prod := append(prod,{append(part(p1,n1),rest part(p2,n2))})>>;
     return prod end;

symbolic procedure findvals(p,rt);
   begin scalar val,pp,pp1; integer n,b,e;
     val := algebraic lisp caddr rt; pp := cdr p;
 lp: pp1 := car pp; pp := cdr pp; n := n+1;
%    if algebraic lisp caddr cadr pp1 = val then
     if algebraic lisp {'difference,caddr cadr pp1,val} = 0 then
       <<if b = 0 then b := n; e := n>>;
     if pp then go to lp;
     return 'list . {b,e} end;

symbolic operator findvals;

algebraic procedure outcombine2(p1,p2);
   begin scalar prod;
      prod := {};
      for each r1 in p1 do for each r2 in p2 do
         prod := append(prod,{append(r1,r2)});
      return prod end;

symbolic procedure sortvars();
   algebraic varsortsolns!* :=
     'list . for each rts in cdr algebraic grandsoln!*
       collect 'list . sort(cdr rts,
         function (lambda(a,b); ordop(cadr a,cadr b)));

symbolic operator sortvars;

symbolic procedure sortvals();
   algebraic sortvals!* := 'list . sort(cdr algebraic varsortsolns!*,
     function sortvals1);

symbolic operator sortvals;

algebraic procedure cabs x;
  if lisp !*compxroots then sqrt((repart x)^2 + (impart x)^2) else x;

algebraic procedure membno(n,l);
   if n=first l then 1
     else if rest l = 0 then 0 else 1 + membno(n,rest l);

algebraic procedure getpaths n;
  % this is used to call links2paths for testing purposes only.
   begin scalar links; links := {};
     for j := 0:n do for k := 0:n do
        if j neq k and subset1(vll!# j,vll!# k) then
        links := append(links,{{j,k}});
     return links2paths links end;

algebraic procedure links2paths links;
  begin scalar paths,paths2,bases,fl,fl2;
     paths := paths2 := {}; bases := root npoly!*;
    % multroot will not work if there are no bases;
     if bases = {} then return nil;
    % extend from bases if possible.
     for each base in bases do
       <<fl := nil;
         for each link in links do if base = first link then
           <<fl := t; paths2 := append(paths2,{link})>>;
         if not fl then paths := append(paths,{{base}})>>;
    % now extend each path in paths2 if possible.  When fully
    % extended, add path to paths.
ext: fl2 := nil;
     for each path in paths2 do
       <<fl := nil;
         for each link in links do if first link = last path then
           <<fl := t; fl2 := t;
             paths2 := append(paths2,{append(path,{second link})})>>;
        if not fl then paths := append(paths,{path});
        paths2 := delete(path,paths2)>>;
     if fl2 then go to ext;
     return paths end;

symbolic operator delete;

algebraic procedure last x; first reverse x;

symbolic procedure subset1(a,b);
   length b-length a=1 and subsetp(a,b);

symbolic operator subset1;

algebraic procedure root n;
   begin scalar trrt; trrt := {};
     for j := 0:n do if length vll!# j=1 then trrt := append(trrt,{j});
     return trrt end;

algebraic procedure pfx(pr,fl);
   begin scalar prf,f,ff;
     prf := fl*10.0^pr; ff := 1.0; f := 0;
     while prf*ff>1.0 do <<ff := ff/10; f := f+1>>;
     return f end;

symbolic procedure roundroot(a,p);
<<p := {'equal,cadr a, if caaddr a = 'minus then
  {'minus, rtrnda(cadr caddr a,p)}
   else rtrnda(caddr a,p)};
  algebraic p>>;

symbolic operator roundroot;

algebraic procedure elim(a,b);
  % compares list b with list a (whose length is shorter by 1) and
  % returns the unmatched member.
   begin scalar x;
     for each el in b do if not member(el,a) then x := el;
     return x end;

algebraic procedure combinerts(r0,r1);
   begin scalar xout; xout := {};
     return if r1 = {} then {} else
       <<for each rt1 in r1 do
           xout := append(xout,{append(r0,{rt1})});
         xout>> end;

endmodule;

end;
