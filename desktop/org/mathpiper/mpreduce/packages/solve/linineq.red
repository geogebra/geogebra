module linineq; % Linear inequalities and linear optimization.

% Author:       Herbert Melenk <melenk@zib.de>

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


% Version 1     January 1990
% Version 1.1   February 1990
%               added parameter "record=t"
% Version 2     May 1991
%               added Branch-and-Bound for Integer Prgramming
% Version 3     Dec 1994
%               added formal simplifier for MAX/MIN expressions.
%               Changed "inf" to "infinity".
%               Operator linineq_solve new.
% Version 4     Jan 95
%               use polytope points for the simplification of MAX/MIN
%               expressions.
% Version 5     Jul 2003
%               Adaptation of the actual REDUCE language standard.
%               Correction of the handling of an isolated linear
%               inequality (call "getrlist" only if the car of
%               the expression is "list").

%
% Solution of linear inequalities & equations with numerical
% coefficients.
%
%   Fourier(1826) /Motzkin(1936): George. B. Dantzig,
%                  Linear Programming and Extensions.


put('linineq,'psopfn,
       function (lambda(u);
                    rederr "USE simplex (package linalg) instead"));

global '(!*trlinineq !*trlinineqint !*prlinineq);
switch trlinineq,prlinineq,trlinineqint;
fluid '(linineqinterval!* linineqrecord!*);

fluid '(!*ineqerr);   % error code

symbolic procedure linineqeval u;
  % Interface for algebraic mode.
   begin scalar prob,equa,requa,vars,oldorder,res,u1,x,y,p,e,msg;
         scalar direction,rec,linineqrecord!*,r,intvars,w1,w2,op;
      msg:=!*prlinineq or !*trlinineq;
      !*ineqerr :=nil;
      u1:=reval car u;
      u1:= if car u1='list then getrlist u1 else {u1};
      u:=cdr u;
      if u then
      <<x:=reval car u;
        vars:=if eqcar(x,'list)then getrlist x else{x};
        u:=cdr u>>;
      while u do <<x:=reval car u; u:=cdr u;
                  if eqcar(x,'equal)and
                     ((cadr x='record and (rec:=t))or
                      (cadr x='int and (intvars:=getrlist caddr x)))
                  then t else
                  <<!*ineqerr:=2;
                    typerr(x,"illegal parameter")>> >>;
      x:=nil;
      for each u in vars do
       <<u:=reval u;
         if eqcar(u,'equal)then
           if  member(caddr u,'(min max))then
            <<direction:=(cadr u. caddr u).direction;
              u:=cadr u>> else
                <<!*ineqerr:=2;
                  rederr "illegal form in 2nd parameter">>;
         if smember(u,u1)and not member(u,x)then x:=u.x>>;
      x:=vars:=reversip x;
      while u1 do
      <<u:=reval car u1; u1:=cdr u1;
        if not pairp u or not (car u memq '(geq leq equal))then
             <<!*ineqerr:=2; typerr(u,"inequality")>>;
        op:=car u; w1:=reval cadr u; w2:=reval caddr u;
        if op='geq then
          if smemq('infinity,w2)then nil else
          if eqcar(w2,'max)then
             for each q in cdr w2 do
               u1:=append(u1,{{'geq,w1,q}})
          else prob:=(simp w1.simp w2).prob else
        if op='leq then
         if smemq('infinity,w2)then nil else
         if eqcar(w2,'min)then
             for each q in cdr w2 do
               u1:=append(u1,{{'leq,w1,q}})
          else prob:=(simp w2.simp w1).prob else
        if op='equal then
            if eqcar(w2,'!*interval!*)then
              u1:=append(u1,{{'geq,w1,cadr w2},{'leq,w1,caddr w2}})
            else
             equa:=(simp w1.simp w2).equa
         else
           <<!*ineqerr:=1; typerr(u,"inequality")>> >>;
        % control the linearity
      for each p in append(equa,prob)do
      <<if not domainp denr car p or not domainp denr cdr p
          then<<!*ineqerr:=1;
                rederr "unable to process nonlinear system">>;
        vars:=linineqevaltest(numr car p,
                        linineqevaltest(numr cdr p,vars))>>;
      if msg then <<prin2 "variables:"; prin2t vars>>;
      oldorder:=setkorder vars;
      prob:=for each p in prob collect
       (reorder numr car p./denr car p).
             (reorder numr cdr p./denr cdr p);
      equa:= for each p in equa collect
       (reorder numr car p./denr car p).
             (reorder numr cdr p./denr cdr p);
       % eliminate variables from equations
      while equa do
      <<e:=car equa; equa:=cdr equa;
        e:=addsq(car e,negsq cdr e);
        if domainp numr e then
        <<if numr e then  % nonzero constant equated to 0
          <<!*ineqerr:=0; rederr "equation part inconsistent">> >>
         else
        <<u:=    {(x:=mvar numr e).
                  prepsq(y:=multsq(negf red numr e ./ 1,
                                   invsq(lc numr e ./ 1)))};
                 if member(x,intvars)then
                          % Dont eliminate integer variables - represent
                          % equation by double inequality instead.
                 <<x:=simp x; prob:=append({x.y,y.x},prob)>>
                 else
                 <<
          prob:=for each p in prob collect
                        subsq(car p,u).subsq(cdr p,u);
          equa:=for each p in equa collect
                        subsq(car p,u).subsq(cdr p,u);
          requa:=append(u,requa);
          if msg then
            <<prin2 "         ";prin2 x;
              prin2 " eliminated by equation";
              terpri()>>;
          vars:=delete(x,vars);
                 >> >> >>;
      res:=if intvars
               then linineqint(prob,vars,msg,direction,rec,intvars)
             else linineq1(prob,vars,msg,direction,rec);
          % backsubstitution in equations;
      if null res then return '(list)else if res=t then res:=nil;
      for each e in requa do
      <<x:=prepsq subsq(y:=simp cdr e,res);
        res:=(car e.x).res;
        if rec then
        <<x:=prepsq y;
          linineqrecord!*:={x,x}.linineqrecord!*>> >>;
      setkorder oldorder;
      r:=if rec then for each p in
               liqsimp!-maxmin pair(res,linineqrecord!*)
            collect
                   {'list,{'equal,caar p,cdar p},cadr p,caddr p}
            else
           for each p in res collect {'equal,car p,cdr p};
      return 'list.r end;

% put('linineq_solve,'psopfn,'linineqseval);

symbolic procedure linineqseval u;
% neu (eine Zeile):
   'list.reversip for each q in
     cdr linineqeval append(u,'((equal record t)))
       collect {'equal,cadr cadr q,
        if caddr q = cadddr q then caddr q else '!*interval!*.cddr q};

symbolic procedure linineqevaltest(f,v);
   % Collect the variables in standard form f and control linearity.
     if domainp f then v else
     if not(ldeg f=1)then
           <<!*ineqerr:=1;
             rederr "unable to process nonlinear system">>
       else
     if member(mvar f,v)then linineqevaltest(red f,v)else
         linineqevaltest(red f,mvar f.v);

symbolic procedure linineq0(prob,vars,dir,rec);
  % Interface for symbolic mode.
  % Prob is a list (e1,e2,..)of algebraic expressions without
  % relational operators, which are interpreted as
  % set of inequalities ei >= 0. They are linear in the
  % variables vars.
  % Silent operation: result=nil if the system is inconsistent.
   begin scalar oldorder,res;
      linineqrecord!*:=nil;
      oldorder:=setkorder vars;
      prob:=for each u in prob collect simp u.(nil./1);
      res:=linineq1(prob,vars,nil,dir,rec);
      setkorder oldorder;
      return res end;

symbolic procedure linineqint(prob,vars,msg,dir,rec,intvars);
  begin scalar x,x0,y,y0,y1,z,w,problems,best,z,z0,zbest,zf,bestr;
       % test integer variables and adjust order;
    for each x in vars do
      if member(x,intvars)then<<w:=x.w;intvars:=delete(x,intvars)>>;
    if intvars
      then <<!*ineqerr:=2;typerr('list.intvars,"int variables")>>;
    intvars:=reversip w;
       % select primary optimization principle.
        if dir then<<z:=caar dir;zf:=if cdar dir='max then 1 else -1>>;
    problems:=list (nil.prob);
       % macro loop.
    while problems do
    <<z0:=caar problems; prob:=cdar problems; problems:=cdr problems;
      if msg or !*trlinineqint
        then linineqprint2("=== next integer subproblem",prob);
      w:=if best and not evalgreaterp({'times,zf,z0},{'times,zf,zbest})
          then nil  % skip problem with suboptimal bound.
         else linineq1(prob,vars,msg,dir,rec);
      if !*trlinineqint then linineqprint3("=== subresult",w);
      if w and dir then
      <<% is better than best so far?
        z0:=cdr assoc(z,w);
        if best and evalgreaterp({'times,zf,zbest},{'times,zf,z0})
           then w:=nil>>;
      if w then
      <<% test feasability;
        y:=list prob;
        for each x in intvars do
        <<x0:=cdr assoc(x,w);
          if not fixp x0 then  % branch and bound
          <<x:=simp x; y0:=simp{'ceiling,x0}; y1:=simp {'floor,x0};
            y:= for each q in y join {(x.y0).q, (y1.x).q};
            if msg or !*trlinineqint then
            <<writepri("branch and bound with",'first);
              writepri(mkquote{'list,{'geq,x:=prepsq x,prepsq y0},
                                     {'leq,x,prepsq y1}},'last)>> >> >>;
        if cdr y then
         problems:=append(problems,for each q in y collect z0.q)
        else
         <<zbest:=z0; best:=w; bestr:=linineqrecord!*;
           if !*trlinineqint then prin2t "===>  is feasable">>
       >>;  % if w
               % without target dont need additional result.
           if best and null dir then problems:=nil
     >>;  % while problems
   linineqrecord!*:=bestr;
   return best end;

symbolic procedure linineq1(prob,vars,msg,dir,rec);
  % Algebraic evaluation of a set of inequalities:
  % prob is a list of pairs of standard quotients,
  % (( p1.q1)(p2.q2) .. (pn.qn))
  % which are interpreted as inequalities:
  %     pi >= qi ;
  % vars is the list of (linear) variables.
  % dir  the direction of final optimization
  % rec  switch; if t, the record of inequatlities is produced
  % Result is NIL if the system has no solution; otherwise
  % the solution has the form of an association list
  %  ((v1.val1)(v2.val2) ... (vn.valn)),
  % where vi are the variables and vali are values in algebraic
  % form. NIL if the system has no solution.
  %
   begin scalar v,vq,lh,rh,x,y,z,prob1,prob2,prob3,prob4,nprob,sw,sol;
      if null vars then return linineq2(prob,msg);
      v:=car vars; vars:=cdr vars;
      vq:=mksq(v,1);
      if !*trlinineq then
       linineqprint2({"next variable:",v,"; initial system:"},prob);
      prob:=linineqnormalize prob;
      for each p in prob do
       <<lh:=car p; rh:=cdr p;
          % if v appears on the lhs, isolate it
         if not domainp numr lh and mvar numr lh = v then
         <<x:=invsq(lc numr lh ./ 1);
           sw:=(numr x < 0);
           lh:=multsq(lh,x); rh:=multsq(rh,x);
           rh:=addsq(rh,negf red numr lh ./ denr lh);
           if not sw then prob1:=(vq.rh).prob1 else
                          prob2:=(rh.vq).prob2;
         >>else if domainp numr rh and domainp numr lh then
                prob4:=(lh.rh).prob4 else
                prob3:=(lh.rh).prob3>>;
      if null prob1 and null prob2 and vars then
      << sol:=linineq1(prob,vars,msg,dir,rec);
         if rec then linineqrecord!* :=
             append(linineqrecord!*,'(((minus infinity) infinity)));
         return if sol then (v. 0).sol else nil>>;
      if !*trlinineq then
      <<linineqprint2("class 1:",prob1);
        linineqprint2("class 2:",prob2);
        linineqprint2("class 3:",prob3);
        linineqprint2("class 4:",prob4)>>;
      if rec then
      << x:=for each u in prob1 collect prepsq cdr u;
         y:=for each u in prob2 collect prepsq car u;
         x:=if null x then '(minus infinity)else
              if null cdr x then car x else 'max. x;
         y:=if null y then 'infinity else
              if null cdr y then car y else 'min.y;
         linineqrecord!*:=append(linineqrecord!*,{{x,y}})>>;
      if not linineq2(prob4,msg) then return nil;
      nprob:=append(prob3,
         for each x in prob1 join
           for each y in prob2 collect
             car y.cdr x);
      if vars then
       << if null(sol:=linineq1(nprob,vars,msg,dir,rec))then return nil>>
        else if not linineq2(nprob,msg)then return nil;
         % lower bound:
      x:=if null prob1 then nil else
         linineqevalmax for each p in prob1 collect
                subsq(cdr p,sol);
         % upper bound:
      y:=if null prob2 then nil else
         linineqevalmin for each p in prob2 collect
                subsq(car p,sol);
      if (z:=assoc(v,dir))then z:= cdr z;
      if msg then
      <<writepri("         ",'first);
        writepri(mkquote if x then prepsq x else '(minus infinity),nil);
        writepri(" <= ",nil);
        writepri(mkquote v,nil);
        writepri(" <= ",nil);
        writepri(mkquote if y then prepsq y else 'infinity,nil);
        writepri(";   ",nil)>>;
     linineqinterval!*:=x.y;
     if z='min and null x or z='max and null y then
      <<if msg then writepri( " max/min cannot be resolved",'last);
        return nil>>;
      if not(x=y)then
        if z='min then y:=nil else if z='max then x:=nil;
      if msg then
      << writepri(
        if null x and null y then " completely free: " else
        if null y then " minimum: " else
        if null x then " maximum: " else
        if x=y then " zero length interval: " else " middle: ",nil)>>;
      if null x and null y then x:=0 else % completely free
      if null x then x:=prepsq y else
      if null y then x:=prepsq x else
      if sqlessp(y,x)then
        <<prin2 "system inconsistent:";
          prin2 prepsq x; prin2 " not <= "; prin2t prepsq y;
          return nil>> else
          x:={'quotient,{'plus,prepsq x,prepsq y},2};
      x:=aeval x;
      if msg then
        writepri(mkquote {'equal,v,x},'last);
      return (v.x).sol end;

symbolic procedure linineq2(prob,msg);
   % All variables are elimitated. Control, if the
   % remaining numerical inequalities are consistent.
   begin scalar rh,lh;
 loop: if null prob then return t;
      lh:=caar prob; rh:=cdar prob;
      if not domainp numr rh or not domainp numr lh then
        <<!*ineqerr:=1;
          rederr{" non numeric:", rh, lh}>>;
      if sqlessp(lh,rh)then
        <<if msg then <<writepri("system inconsistent: ",'first);
                        writepri(mkquote prepsq lh,nil);
                        writepri(" not >= ",nil);
                        writepri(mkquote prepsq rh,'last)>>;
          return nil>>;
      prob:=cdr prob;
      goto loop end;

symbolic procedure linineqnormalize prob;
    % Normalize system: reform all inequalities such that they have
    % the canonical form %     polynomial >= constant
    %      (canonical restriction: absolute term of lhs=0,
    %                              denominator of lhs = 1).
    % and remove those, which have same lhs, but smaller rhs
    % (the latter are superfluous).
   begin scalar r,lh,rh,d,ab,x;
     for each p in prob do
     <<lh:=car p; rh:=cdr p;
         % arithmetic normalizaion
       lh:=addsq(lh,negsq rh);
       d:=denr lh;
       lh:=numr lh;
       ab:=lh; x:=if domainp lh then 1 else lc ab;
       while not domainp ab do <<x:=gcdf(x,lc ab);ab:=red ab>>;
       ab:=negf ab;
       lh:=multsq(addf(lh,ab)./1,1 ./ x);
       rh:=multsq(ab ./ 1, 1 ./ x);
         % removal of redundant elements
       x:=assoc(lh,r);
       if null x then r:=(lh.rh).r else
         if sqlessp(cdr x,rh)then rplacd(x,rh)>>;
     if !*trlinineq then
         linineqprint2("normalized and reduced:",r);
     return r end;

symbolic procedure linineqevalmin u;
   % Compute the minimum among the list u with sq's.
     linineqevalmin1(car u,cdr u);

symbolic procedure linineqevalmin1(q,u);
   if null u then q else
  (linineqevalmin1( if x and !:minusp x then q else car u, cdr u)
       )where x=numr addsq(q,negsq car u);

symbolic procedure linineqevalmax u;
   % compute the maximum among the list u with sq's
     linineqevalmax1(car u,cdr u);

symbolic procedure linineqevalmax1(q,u);
   if null u then q else
  (linineqevalmax1(
     if x and !:minusp x then car u else q, cdr u)
        )where x=numr addsq(q,negsq car u);

symbolic procedure sqlessp(q1,q2);
   (x and !:minusp x)where x=numr addsq(q1,negsq q2);

symbolic procedure liqsimp!-maxmin w;
   liqsimp2!-maxmin liqsimp1!-maxmin w;

endmodule;

end;
