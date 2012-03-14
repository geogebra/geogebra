module sum2; % Auxiliary package for summation in finite terms.

% Authors: K.Yamamoto, K.Kishimoto & K.Onaga  Hiroshima Univ.
% Modified by: F.Kako                         Hiroshima Univ.
%         Fri Sep. 19, 1986
%         Mon Sep. 7, 1987   added PROD operator (by F. Kako)
% e-mail: kako@kako.math.sci.hiroshima-u.ac.jp
%         or D52789%JPNKUDPC.BITNET

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


% Usage:
%         sum(expression,variable[,lower[,upper]]);
%         lower and upper are optionals.

%         prod(expression,variable[,lower[,upper]]);
%         returns product of expression.

fluid '(!*trsum);               % trace switch;

fluid '(sum_last_attempt_rules!*);

switch trsum;

symbolic procedure simp!-sum0(u,y);
  begin scalar v,upper,lower,lower1,dif;
      if not atom cdr y then <<
          lower := cadr y;
          lower1 := if numberp lower then lower - 1
                     else list('plus,lower,-1);
          upper := if not atom cddr y then caddr y else car y;
          dif := addsq(simp!* upper, negsq simp!* lower);
          if denr dif = 1 then
              if null numr dif
                then return subsq(u,list(!*a2k car y . upper))
                else if fixp numr dif then dif := numr dif
                else dif := nil
           else dif := nil;
          if dif and dif <= 0 then return nil ./ 1;
          if atom cddr y then upper := nil>>;
      v := !*a2k car y;
      return simp!-sum1(u,v,y,upper,lower,lower1,dif)
   end;

symbolic procedure simp!-sum1(u,v,y,upper,lower,lower1,dif);
   begin scalar w,lst,x,z,flg;
      lst := sum!-split!-log(u,v);
      w := car lst;
      lst := cdr lst;
      u := nil ./ 1;
    a:
      if null w then go to b;
      x := multsq(caar w,
                  simp!-prod1(cdar w,v,y,upper,lower,lower1,dif));
      u := addsq(u,simp!* list('log, prepsq x));
      w := cdr w;
      go to a;
    b:
       if null lst then return u;
            flg := nil;
            z := car lst;
            if !*trsum then <<
               prin2!* "Summation ";sqprint z;prin2!*  " w.r.t ";
               xprinf(!*k2f v,nil,nil);terpri!* t >>;
%           z := reorder numr z ./ reorder denr z;
            w := sum!-sq(z,v);
            if w = 'failed then <<
                if !*trsum then <<
                     prin2!* "UMM-SQ failed. Trying SUM-TRIG";
                     terpri!* t>>;
                w := sum!-trig(z,v);
                if w = 'failed then <<
                     if !*trsum then <<
                         prin2!* "SUM-TRIG failed.";
                         terpri!* t>>;
                     w := sum!-unknown(z,v,y,lower,dif);
                     flg := car w;
                     w := cdr w>>>>;
            if !*trsum then <<
                 prin2!* "Result  = "; sqprint w; terpri!* t >>;
            if flg then goto c;
            if upper then
                w := addsq(subsq(w,list(v . upper)),
                           negsq subsq(w,list(v . lower1)))
             else if lower then
                w := addsq(w , negsq subsq(w, list(v . lower1)));
   c:
            u := addsq(u,w);
            lst := cdr lst;
            goto b
   end;


%*********************************************************************
%       Case of trigonometric or other functions
%       Trigonometric functions are expressed in terms of exponetials.
%       Pattern matching to get the summation in closed form.
%********************************************************************;

global '(!*trig!-to!-exp);              % variable to indicate
                                        % that the expression contains
                                        % some trig. functions.

symbolic procedure sum!-trig(u,v);
   begin scalar lst,w;  % z;
      !*trig!-to!-exp := nil;           % trig. to exponential.
      u := trig!-to!-expsq(u,v);
      if not !*trig!-to!-exp then return 'failed;
      lst := sum!-term!-split(u,v);
      u := nil ./ 1;
   a:
      if null lst then return exp!-to!-trigsq u;
%     z := reorder numr car lst ./ reorder denr car lst;
%     w := sum!-sq(z,v);
      w := sum!-sq(car lst,v);
      if w = 'failed then return 'failed;
%      w := exp!-to!-trigsq w;     % exponential to trig. function.
      u := addsq(u,w);
      lst := cdr lst;
      goto a
   end;

   sum_last_attempt_rules!* :=
    algebraic  <<
    { sum(~f + ~g,~n,~anf,~ende) => sum(f,n,anf,ende) +
                                       sum(g,n,anf,ende)
                when or (part(sum(f,n,anf,ende),0) neq sum ,
                         part(sum(g,n,anf,ende),0) neq sum ),
      sum((~f+~g)/~dd,~n,~anf,~ende) => sum(f/dd,n,anf,ende) +
                                        sum(g/dd,n,anf,ende)
                when or (part(sum(f/dd,n,anf,ende),0) neq sum ,
                         part(sum(g/dd,n,anf,ende),0) neq sum ),
      sum(~c*~f,~n,~anf,~ende) => c* sum(f,n,anf,ende)
                         when freeof(c,n) and c neq 1,
      sum(~c/~f,~n,~anf,~ende) => c* sum(1/f,n,anf,ende)
                         when freeof(c,n) and c neq 1,
      sum(~c*~f/~g,~n,~anf,~ende) => c* sum(f/g,n,anf,ende)
                          when freeof(c,n) and c neq 1} >>$


symbolic procedure sum!-unknown(u,v,y,lower,dif);
   begin scalar z,w;
     if null dif then <<
          z := 'sum . (prepsq u . list car y);
          if w := opmtch z then return (nil . simp w)
           else if null cdr y then return (t . mksq(z,1));
          z := 'sum . (prepsq u . y);
          let sum_last_attempt_rules!*;
          w:= opmtch z;
          rule!-list (list sum_last_attempt_rules!*,nil);
          return (t . if w then simp w else mksq(z,1))>>;
%         return (t . if w := opmtch z then simp w else mksq(z,1))>>;
     z := nil ./ 1;
  a:
     if dif < 0 then return (t . z);
     z := addsq(z,subsq(u,list(v . list('plus,lower,dif))));
     dif := dif - 1;
     go to a
   end;

symbolic procedure sum!-subst(u,x,a);
   if u = x then a
    else if atom u then u
    else sum!-subst(car u, x, a) . sum!-subst(cdr u, x, a);

symbolic procedure sum!-df(u,y);
   begin scalar w,z,upper,lower,dif;
      dif := nil;
      if length(y) = 3 then  <<
         lower := cadr y;
         upper := caddr y;
         dif := addsq(simp!* upper, negsq simp!* lower);
         if denr dif = 1 then
            if null numr dif
               then return simp!* sum!-subst(u, car y, upper)
               else if fixp numr dif then dif := numr dif
               else dif := nil
           else dif := nil;
         if dif and dif <= 0 then return nil ./ 1 >>;
     if null dif then <<
          z := 'sum . (u . y);
          let sum_last_attempt_rules!*;
          w:= opmtch z;
          rule!-list (list sum_last_attempt_rules!*,nil);
          return if w then simp w else mksq(z,1)>>;
     z := nil ./ 1;
  a: if dif < 0 then return z;
     z := addsq(z,simp!* sum!-subst(u, car y, list('plus,lower,dif)));
     dif := dif - 1;
     go to a
   end;


%*********************************************************************
%       Summation by Gosper's algorithm.
%********************************************************************;

symbolic procedure sum!-sq(u,v);
   %Argument U : expression of s-q;
   %         V : kernel;
   %value        : expression of sq (result of summation.);
   begin scalar gn,fn,pn,rn,qn,z,k,x;
      if null numr u then return nil ./ 1;
      x := setkorder list v;
      z := reorder numr u;
      u := z ./ reorder denr u;
      if !*trsum then <<
            prin2t " *** Summation by Gosper's algorithm ***";
            prin2!* "    A(n) = "; sqprint u;terpri!* t;
            terpri!* t>>;
      if domainp z or not (mvar z eq v) or red z then
            <<pn := 1; z := u>>
       else <<pn := !*p2f lpow z;
              z := lc z ./ denr u>>;
      z := quotsq(z,nsubsq(z,v, - 1));
      gn := gcdf!*(numr z,denr z);
      if !*trsum then <<
         prin2!* "A(n)/A(n-1) = ";sqprint z;terpri!* t;
         prin2!* "GN = ";xprinf(gn,nil,nil);terpri!* t>>;
      qn := quotf!*(numr z, gn);
      rn := quotf!*(denr z, gn);
      if nonpolyp(qn,v) or nonpolyp(rn,v) then go to fail;
      if !*trsum then <<
         prin2!* "Initial qn, rn and pn are "; terpri!* t;
         prin2!* "QN = ";xprinf(qn,nil,nil);terpri!* t;
         prin2!* "RN = ";xprinf(rn,nil,nil);terpri!* t;
         prin2!* "PN = ";xprinf(pn,nil,nil);terpri!* t>>;
      k := compress explode '!+j;
      z := integer!-root(resultant(qn,nsubsf(rn,v,k),v),k);
      if !*trsum then <<
         prin2 "Root of resultant(q(n),r(n+j)) are "; prin2t z >>;
      while z do <<
            k := car z;
            gn := gcdf!*(qn,nsubsf(rn,v,k));
            qn := quotf!*(qn,gn);
            rn := quotf!*(rn,nsubsf(gn,v, -k));
            while (k := k - 1)>=0 do
               pn := multf(pn,nsubsf(gn,v, -k));
            z := cdr z>>;
      if !*trsum then <<
         prin2!* "Shift free  qn, rn and pn are";terpri!* t;
         prin2!* "QN = ";xprinf(qn,nil,nil);terpri!* t;
         prin2!* "RN = ";xprinf(rn,nil,nil);terpri!* t;
         prin2!* "PN = ";xprinf(pn,nil,nil);terpri!* t>>;
      qn := nsubsf(qn,v,1);
      if (k := degree!-bound(pn,addf(qn,rn),addf(qn,negf rn),v)) < 0
         then go to fail;
      if !*trsum then <<
         prin2 "DEGREE BOUND is "; prin2t k >>;
      if not(fn := solve!-fn(k,pn,qn,rn,v)) then go to fail;
      if !*trsum then <<
         prin2!* "FN = ";sqprint fn;terpri!* t >>;
      u := multsq(multsq(qn ./ 1,fn), multsq(u, 1 ./ pn));
      z := gcdf!*(numr u, denr u);
      u := quotf!*(numr u, z) ./ quotf!*(denr u,z);
      if !*trsum then <<
            prin2t " *** Gosper's algorithm completed ***";
            prin2!* "    S(n) = "; sqprint u;terpri!* t;
            terpri!* t>>;
      setkorder x;
      return (reorder numr u ./ reorder denr u);
  fail:
      if !*trsum then <<
            prin2t " *** Gosper's algorithm failed ***";
            terpri!* t>>;
      setkorder x;
      return 'failed
   end;

%*********************************************************************
%       integer root isolation
%********************************************************************;

symbolic procedure integer!-root(u,v);
% Produce a list of all positive integer root of U;
   begin scalar x,root,n,w;
        x := setkorder list v;
        u := reorder u;
        if domainp u or not(mvar u eq v) then go to a;
        u := numr cancel(u ./ lc u);
        w := u;                                 % get trailing term;
        while not domainp w and mvar w eq v and cdr w do
            w := cdr w;
        if (n := degr(w,v)) > 0 then <<
            w := lc w;
            while n > 0 do <<
               root := 0 . root;
               n := n - 1>>>>;
        n := dfactors lowcoef w;                % factor tail coeff.
        w := (v . 1) . 1;
        while n do <<
            if not testdivide(u,v,car n) then <<
                root := car n . root;
                u := quotf!*(u, (w . - car n))>>
             else n := cdr n>>;
  a:
        setkorder x;
        return root
   end;


symbolic procedure lowcoef u;
   begin scalar lst,m;
        lst := dcoefl u;
        m := 0;
  a:
        if null lst then return m;
        m := gcdn(m,car lst);
        if m = 1 then return 1;
        lst := cdr lst;
        go to a
   end;


symbolic procedure dcoefl u;
   if domainp u then if fixp u then list abs u else nil
    else nconc(dcoefl lc u , dcoefl red u);

symbolic procedure testdivide(u,v,n);
% Evaluate U at integer point (V = N);
   begin scalar x;
 a:
       if domainp u or not(mvar u eq v) then return addf(u,x);
       x := addf(multd(expt(n,ldeg u),lc u),x);
       if (u := red u) then go to a;
       return x
   end;


%*********************************************************************
%********************************************************************;

symbolic procedure degree!-bound(pn,u,v,kern);
% degree bound for fn;
%       u: q(n+1) + r(n);
%       v: q(n+1) - r(n);
   begin scalar lp,l!+, l!-, x,m,k;
      x := setkorder list kern;
      u := reorder u;
      v := reorder v;
      pn := reorder pn;
      l!+ := if u then degr(u,kern) else -1;
      l!- := if v then degr(v,kern) else -1;
      lp := if pn then degr(pn,kern) else -1;
      if l!+ <= l!- then <<k := lp - l!-;go to  a>>;
      k := lp - l!+ + 1;
      if l!+ > 0 then u := lc u;
      if l!- > 0 then v := lc v;
      if l!+ = l!- + 1 and fixp(m := quotf1(multd(-2,v),u)) then
        k := max(m,k)
       else if lp = l!- then k := max(k,0);
   a:
      setkorder x;
      return k
   end;

%*********************************************************************
%       calculate polynomial f(n) such that
%       p(n) - q(n+1)*f(n) + r(n)*f(n-1) = 0;
%********************************************************************;

symbolic procedure solve!-fn(k,pn,qn,rn,v);
   begin scalar i,fn,x,y,z,u,w,c,clst,flst;
      c := makevar('c,0);
      clst := list c;
      fn := !*k2f c;
      i := 0;
      while (i := i + 1) <= k do <<
         c := makevar('c,i);
         clst := c . clst;
         fn := ((v . i) . !*k2f c) . fn>>;
      z :=
       addf(pn,
            addf(negf multf(qn,fn),
                 multf(rn,nsubsf(fn,v, - 1))));
      x := setkorder (v . clst);
      z := reorder z;
      c := clst;
      if !*trsum then <<
         prin2!* "C Equation is";terpri!* t;
         xprinf(z,nil,nil);terpri!* t >>;
  a:
      if domainp z or
         domainp (y := if mvar z eq v then lc z else z) then
           go to fail;
      w := mvar y;
      if not(w memq clst) then go to fail;
      if !*trsum then <<
         prin2!* "C Equation to solve is ";xprinf(y,nil,nil);terpri!* t;
         prin2!* " w.r.t ";xprinf(!*k2f w,nil,nil);terpri!* t >>;
      u := gcdf!*(red y , lc y);
      u := quotf!*(negf red y, u) ./ quotf!*(lc y, u);
      flst := (w . u) . flst;
      z := subst!-cn(z,w,u);
      if !*trsum then <<
         xprinf(!*k2f w,nil,nil);prin2!* " := ";sqprint u;terpri!* t >>;
      clst := deleteq(clst,w);
      if z then go to a;
      setkorder c;
      fn := reorder fn;
      u := 1;
      while not domainp fn and mvar fn memq c do <<
         w := mvar fn;
         z := atsoc(w,flst);
         fn := subst!-cn(fn,w,if z then cdr z);
         if z then u := multf(u,denr cdr z);
         z := gcdf!*(fn,u);
         fn := quotf!*(fn,z);
         u := quotf!*(u,z)>>;
      setkorder x;
      return cancel(reorder fn ./ reorder u);
 fail:
     if !*trsum then <<
        prin2t "Fail to solve C equation.";
        prin2!* "Z := ";xprinf(z,nil,nil);terpri!* t >>;
     setkorder x;
     return nil
   end;


symbolic procedure subst!-cn(u,v,x);
   begin scalar z;
      z := setkorder list v;
      u := reorder u;
      if not domainp u and mvar u eq v then
         if x then u := addf(multf(lc u,reorder numr x),
                             multf(red u,reorder denr x))
          else u := red u;
      setkorder z;
      return reorder u
   end;



symbolic procedure makevar(id,n);
  compress nconc(explode id, explode n);

symbolic procedure deleteq(u,x);
   if null u then nil
    else if car u eq x then cdr u
    else car u . deleteq(cdr u, x);


symbolic procedure nsubsf(u,kern,i);
% ARGUMENT U : expression of sf;
%          KERN : kernel;
%          I : integer or name of integer variable;
% value        : expression of sf;
   begin scalar x,y,z,n;
      if null i or i = 0 then return u;
      x := setkorder list kern;
      u := reorder u;
      y := addf(!*k2f kern,
                if fixp i then i else !*k2f i);
      z := nil;
   a:
      if domainp u or not(mvar u eq kern) then goto b;
      z := addf(z,lc u);
      n := degr(u,kern) - degr(red u,kern);
      u := red u;
   a1:
      if n <= 0 then goto a;
      z := multf(z,y);
      n := n - 1;
      go to a1;
   b:
      z := addf(z,u);
      setkorder x;
      return reorder z
   end;


symbolic procedure nsubsq(u,kern,i);
% ARGUMENT U : expression of sq;
%       KERN : kernel;
%          I : integer or name of integer variable;
% value        : expression of sq;
   subsq(u,list(kern . list('plus, kern, i)));


%*********************************************************************
%       dependency check
%********************************************************************;

symbolic procedure nonpolyp(u,v);
% check U is not a polynomial of V;
   if domainp u then nil
    else (not(mvar u eq v) and depend!-p(mvar u,v))
         or nonpolyp(lc u,v) or nonpolyp(red u,v);


symbolic procedure depend!-sq(u,v);
   depend!-f(numr u,v) or depend!-f(denr u,v);


symbolic procedure depend!-f(u,v);
   if domainp u then nil
    else depend!-p(mvar u,v) or
         depend!-f(lc u,v) or
         depend!-f(red u,v);


symbolic procedure depend!-p(u,v);
   if u eq v then t
    else if atom u then nil
    else if not atom car u then depend!-f(u,v)
    else if car u eq '!*sq then depend!-sq(cadr u, v)
    else depend!-l(cdr u, v);

symbolic procedure depend!-l(u,v);
   if null u then nil
    else if depend!-sq(simp car u, v) then t
    else depend!-l(cdr u,v);


%*********************************************************************
%       term splitting
%********************************************************************;

symbolic procedure sum!-term!-split(u,v);
   begin scalar y,z,klst,lst,x;
      x := setkorder list v;
      z := qremf(reorder numr u, y := reorder denr u);
      klst := kern!-list(car z,v);
      lst := termlst(car z, 1 ./ 1, klst);
      klst := kern!-list(cdr z,v);
      if depend!-f(y,v) then klst := deleteq(klst,v);
      lst := append(lst, termlst(cdr z, 1 ./ y, klst));
      setkorder x;
      return lst
   end;


symbolic procedure kern!-list(u,v);
% Returns list of kernels that depend on V;
   begin scalar x;
      for each j in kernels u do if depend!-p(j,v) then x := j . x;
      return x
   end;

symbolic procedure termlst(u,v,klst);
   begin scalar x,kern,lst;
      if null u then return nil
       else if null klst or domainp u
        % Preserve order for noncom.
        then return list multsq(v,!*f2q u);
      kern := car klst;
      klst := cdr klst;
      x := setkorder list kern;
      u := reorder u;
      v := reorder(numr v) ./ reorder(denr v);
      while not domainp u and mvar u eq kern do <<
         lst := nconc(termlst(lc u, multsq(!*p2q lpow u, v),klst),lst);
         u := red u>>;
      if u then lst := nconc(termlst(u,v,klst),lst);
      setkorder x;
      return lst
   end;


%*********************************************************************
%       Express trigonometric functions (such as sin, cos ..)
%       by exponentials.
%********************************************************************;

symbolic procedure trig!-to!-expsq(u,v);
   multsq(trig!-to!-expf(numr u,v),
          invsq trig!-to!-expf(denr u,v));


symbolic procedure trig!-to!-expf(u,v);
   if domainp u then u ./ 1
    else addsq(multsq(trig!-to!-expp(lpow u,v),
                      trig!-to!-expf(lc u,v)),
               trig!-to!-expf(red u,v));


symbolic procedure trig!-to!-expp(u,v);
   begin scalar !*combineexpt,w,x,z,n,wi;
      % We don't want to combine expt terms here, since the code
      % depends on the terms being separate.
      n := cdr u;          % integer power;
      z := car u;          % main variable;
      if atom z or not atom (x := car z) or not depend!-p(z,v) then
           return !*p2q u;
      if x memq '(sin cos tan sec cosec cot) then <<
          !*trig!-to!-exp := t;
          w := multsq(!*k2q 'i, simp!* cadr z);
          w := simp!* list('expt,'e, mk!*sq w);
%         W := SIMP LIST('EXPT,'E, 'TIMES . ( 'I . CDR Z));
          wi := invsq w;
          if x eq 'sin then
              w := multsq(addsq(w ,negsq wi),
                          1 ./ list(('i .** 1) .* 2))
           else if x eq 'cos then
              w := multsq(addsq(w, wi), 1 ./ 2)
           else if x eq 'tan then
              w := multsq(addsq(w,negsq wi),
                          invsq addsq(w,wi))
           else if x eq 'sec then
              w := multsq(2 ./ 1, invsq addsq(w, wi))
           else if x eq 'cosec then
              w := multsq(list(('i .** 1) .* 2),
                          invsq addsq(w, negsq wi))
           else
              w := multsq(addsq(w, wi),
                          invsq addsq(w, negsq wi))
                     >>
       else if x memq '(sinh cosh tanh sech cosech coth) then <<
          !*trig!-to!-exp := t;
          w := simp!* list('expt,'e,cadr z);
          wi := invsq w;
          if x eq 'sinh then
              w := multsq(addsq(w,negsq wi), 1 ./ 2)
           else if x eq 'cosh then
              w := multsq(addsq(w,wi), 1 ./ 2)
           else if x eq 'tanh then
              w := multsq(addsq(w,negsq wi),
                          invsq addsq(w,wi))
           else if x eq 'sech then
              w := multsq(2 ./ 1, invsq addsq(w, wi))
           else if x eq 'cosech then
              w := multsq(2 ./ 1, invsq addsq(w, negsq wi))
           else
              w := multsq(addsq(w,wi),
                          invsq addsq(w, negsq wi))
                 >>
       else return !*p2q u;
      return exptsq(w,n)
   end;

%*********************************************************************
%       Inverse of trig!-to!-exp.
%       Express exponentials in terms of trigonometric functions
%                (sin, cos, sinh and cosh)
%                               Wed Dec. 17, 1986 by F. Kako;
%********************************************************************;

symbolic procedure exp!-to!-trigsq u;
   multsq(exp!-to!-trigf numr u,
          invsq exp!-to!-trigf denr u);

symbolic procedure exp!-to!-trigf u;
   begin scalar v,v1,x,y,n;
      u := termlst1(u,1,nil ./1);
      v := nil;
   a:
      if null u then go to b;
      x := caar u;
      y := cdar u;
      u := cdr u;
   a1:
      if u and y = cdar u then <<
         x := addf(x,caar u);
         u := cdr u;
         go to a1>>;
      v := (x . y) . v;
      go to a;
   b:
      v1 := reverse v;
      n := length v;
      u := nil ./ 1;
   c:
      if n = 0 then return u
       else if n = 1 then
          return addsq(u,
                       multsq(!*f2q caar v,
                              simp!* list('expt,'e,mk!*sq cdar v)));
      u := addsq(u,exp!-to!-trigl(caar v1,caar v,cdar v1,cdar v));
      v := cdr v;
      v1 := cdr v1;
      n := n - 2;
      go to c
   end;

symbolic procedure exp!-to!-trigl(a,b,c,d);
%       A*E**C + B*E**D
%               -->
%       ((A+B)*COSH((C-D)/2)+(A-B)*SINH((C-D)/2))*E**((C+D)/2);
%       A, B: sf;
%       C, D: sq;
   begin scalar x,y,z;
      x := !*f2q addf(a,b);
      y := !*f2q addf(a, negf b);
      z := multsq(addsq(c,negsq d), 1 ./ 2);
      z := real!-imag!-sincos z;
      return multsq(simp!* list('expt,'e,
                                mk!*sq multsq(addsq(c,d),1 ./ 2)),
                    addsq(multsq(x, cdr z),
                          multsq(y, car z)))
   end;


symbolic procedure termlst1(u,v,w);
%ARGUMENT U : sf;
%         V : sf;
%         W : sq;
%value      : list of (sf . sq);
   begin scalar x,y;
      if null u then return nil
       else if domainp u then return list (multf(u,v) . w);
      x := mvar u;
      y := if atom x or not(car x eq 'expt) or not(cadr x eq 'e) then
              termlst1(lc u,multf(!*p2f lpow u,v),w)
            else termlst1(lc u,v,addsq(w,multsq(simp!* caddr x,
                                                ldeg u ./ 1)));
      return nconc(y,termlst1(red u,v,w))
   end;


% These can be found in Abramowitz-Stegun (27.8.6 Summable Series), and
% were suggested by Winfried Neun.

algebraic;

let {sum(sin(~n*~tt)/n,~n,1,infinity) => (pi - tt)/2,
     sum(sin(~n*~tt)/(~n)^3,~n,1,infinity) =>
             pi^2*tt/6 - pi*tt^2/4 + tt^3/12,
     sum(sin(~n*~tt)/(~n)^5,~n,1,infinity) =>
             pi^4*tt/90 - pi^2*tt^3/36 + pi*tt^4/48-tt^5/240}$

let {sum(cos(~n*~tt)/(~n),~n,1,infinity) =>  -log(2*sin(tt/2)),
     sum(cos(~n*~tt)/(~n)^2,~n,1,infinity) =>
             pi^2/6 - pi*tt/2 + tt^2/4,
     sum(cos(~n*~tt)/(~n)^4,~n,1,infinity) =>
             pi^4/90 - pi^2*tt^2/12 + pi*tt^3/12-tt^4/48}$

endmodule;

end;
