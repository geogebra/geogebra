module solvealg;  % Solution of equations and systems which can
                  % be lifted to algebraic (polynomial) systems.

% Author: Herbert Melenk.

% Copyright (c) 1992 The RAND Corporation and Konrad-Zuse-Zentrum.
% All rights reserved.

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

% August 1992: added material for
%    rule set for reduction of trig. polynomial terms to
%      elementary expressions in sin and cos,
%    constant expressions in sin, cos and constant roots,
%    closed form results for trigonometric systems.
%    general exponentials.
%    avoiding false solutions with surds.
%
% May 1993: better handling of products of exponentials
%       with common base,
%    additional computation branch for linear parts of
%       nonlinear systems.
% July 1996: safe handling of twice (or more) the same input
%     (not handling the case, that one equation is a multiple
%      of an other one)
% January 2001: improved "solvenonlnrsyslin" (inhibiting an infinite
%      recursion.


fluid '(!*expandexpt);   % from simp.red

fluid '( system!*        % system to be solved
         osystem!*       % original system on input
         uv!*            % user supplied variables
         iv!*            % internal variables
         fv!*            % restricted variables
         kl!*            % kernels to be investigated
         sub!*           % global substitutions
         inv!*           % global inverse substitutions
         depl!*          % reduce dependency list
         !*solvealgp     % true if using this module
         solvealgdb!*    % collecting some data
         last!-vars!*    % collection of innermost aux variables
         const!-vars!*   % variables representing constants
         root!-vars!*    % variables representing root expressions
         !*expli         % local switch: explicit solution
         groebroots!*    % predefined roots from input surds
         !*test_solvealg % debugging support
         !*arbvars
         !*varopt
         solve!-gensymcounter
      	 solve!-gensymprefix
       );

fluid'(!*trnonlnr);
  % If set on, the modified system and the Groebner result
  % or the reason for the failure are printed.

global'(loaded!-packages!* !!arbint);

switch trnonlnr;

!*solvealgp:=t;

%     Solvenonlnrsys receives a system of standard forms and
%     a list of variables from 'solve'. The system is lifted to
%     a polynomial system (if possible) in substituting the
%     non-atomic kernels by new variables and appending additonal
%     relations, e.g.
%         replace                        add
%       sin u,cos u  -> su,cu        su^2+cu^2-1
%         u^(1/3)    -> v            v^3 - u
%          ...
%     in a recursive style. If completely successful, the
%     system definitely can be treated by Groebner or any
%     other polynomial system solver.
%
%     Return value is a pair
%          (tag . res)
%     where 'res' is nil or a structure for !*solvelist2solveeqlist
%     and 'tag' is one of the following:
%
%       t             a satisfactory solution was generated,
%
%       failed        the algorithm cannot be applied ('res=nil')
%
%       inconsistent  the algorithm could prove that the
%                     the system has no solution ('res=nil')
%
%       nil           the complexity of the system could
%                     be reduced, but some (or all) relations
%                     remain still implicit.

% Rules to be applied locally for converting composite transcendental
% function forms into simpler ones

algebraic <<
solvealg!-rules1:=
{ sin(~alpha + ~beta) => sin(alpha)*cos(beta) + cos(alpha)*sin(beta),
  cos(~alpha + ~beta) => cos(alpha)*cos(beta) - sin(alpha)*sin(beta),
  sin(~n*~alpha) => sin(alpha)*cos((n-1)*alpha)
                  + cos(alpha)*sin((n-1)*alpha) when fixp n,
  cos(~n*~alpha) => cos(alpha)*cos((n-1)*alpha)
                  - sin(alpha)*sin((n-1)*alpha) when fixp n,
  sin(~alpha)**2 => 1 - cos(alpha)**2,
  sinh(~alpha+~beta) => sinh(alpha)*cosh(beta) + cosh(alpha)*sinh(beta),
  cosh(~alpha+~beta) => cosh(alpha)*cosh(beta) + sinh(alpha)*sinh(beta),
  sinh(~n*~alpha) => sinh(alpha)*cosh((n-1)*alpha)
                   + cosh(alpha)*sinh((n-1)*alpha) when fixp n,
  cosh(~n*~alpha) => cosh(alpha)*cosh((n-1)*alpha)
                   + sinh(alpha)*sinh((n-1)*alpha) when fixp n,
  sinh(~alpha)**2 => cosh(alpha)**2 - 1};

solvealg!-rules2:=
{ tan(~alpha) => sin(alpha)/cos(alpha),
  cot(~alpha) => cos(alpha)/sin(alpha),
  tanh(~alpha) => sinh(alpha)/cosh(alpha),
  coth(~alpha) => cosh(alpha)/sinh(alpha) } ;

solvealg!-rules3:=
{ sin(~alpha)**2 => 1 - cos(alpha)**2,
  sinh(~alpha)**2 => cosh(alpha)**2 - 1};

% Artificial operator for matching powers in a product.

operator my!-expt;

solvealg!-rules4:=
 {my!-expt(~a,~b)*my!-expt(a,~c) => my!-expt(a,b+c),
  my!-expt(~a,~b)*a => my!-expt(a,b+1)
 %my!-expt(~a,~b)/my!-expt(a,~c) => my!-expt(a,b-c)
  };

>>;

symbolic procedure solvenonlnrsys(sys,uv);
  % Interface to algebraic system solver.
  % Factorize the system and collect solutions.
  % After factoring we resimplify with *expandexpt off
  % in order to have exponentials to one basis
  % collected.
solvenonlnrsys0(sys,uv,nil);

symbolic procedure solvenonlnrsys0(sys,uv,lvars);
  begin scalar q,r,s,tag,!*expandexpt;
   s:=sys;sys:=nil;
   for each x in s do sys:=union(sys,{x});
   s:='(nil);
   if solve!-psysp(sys,uv) then s:={sys} else
      for each p in sys do
         <<r:=nil;
           for each q in cdr fctrf p do
              if topkernlis(car q,uv)
                then for each u in s do r:=(car q.u).r;
          s:=r>>;
   tag:='failed;r:=nil;
   for each u in s do
      <<% Collect exponentials with same base.
        u:=solvenonlnrcollectexpt u;
        q:=solvenonlnrsys1(u,uv);
        if eqcar(q,'failed) then q:=solvenonlnrsyssep(u,uv);
        if eqcar(q,'failed) then q:=solvenonlnrsyslin(u,uv,nil,lvars);
        if eqcar(q,'not) then q:=solvenonlnrsyslin(u,uv,t,lvars);
        if eqcar(q,'not) then q:='(failed);
        if car q and car q neq 'failed then tag:=car q;
        q:=if car q neq 'failed then cdr q else
                for each j in u collect {{j ./ 1},nil,1};
        r:=union(q,r)>>;
   return if tag eq 'inconsistent or tag eq 'failed then {tag}
       else tag.r end;

symbolic procedure topkernlis(u,v);
   v and (topkern(u,car v) or topkernlis(u,cdr v));

symbolic procedure solvenonlnrcollectexpt u;
  % 'u' is a list of standard forms. Reform these
  % such that products of exponentials with same basis
  % are collected.
   if not smemq('expt,u) then u else
   <<eval'(let0 '(solvealg!-rules4));
     u:=for each q in u collect
        numr simp subst('expt,'my!-expt,
          reval prepf subst('my!-expt,'expt,q));
     eval'(clearrules '(solvealg!-rules4));u>>;

symbolic procedure solvenonlnrsyslin(eqs,vars,mode,lvars);
 % 'eqs' is a system of equations (standard forms,
 % implicitly equated to zero); this routine tries
 % to reduce the system recursively by separation,
 % if one variable occurs in one equation only linearly.
 % Mode=nil: simple version: only pure linear variables
 %           are substituted.
 %      t:   extended version: replacing variables with
 %           degree 1 and potentially complicated
 %           coefficients.
 % Returns solution or
 %     '(not)   if not applicable
 %     '(failed)if applicable but solution failed.
begin scalar d,e,e1,lx,n,s,q,x,v,w,w1,neqs,nvars;
 v:=vars;
 var_loop:if null v then return'(not);x:=car v;v:=cdr v;w:=eqs;
 if x member lvars then go to var_loop;lvars:=x.lvars;lx:={x};
 eqn_loop:if null w then goto var_loop;e:=car w;w:=cdr w;
   if null e then goto eqn_loop;
   if domainp e then return'(inconsistent);
   e1:=reorder e where kord!*=lx;
   if not(mvar e1=x) or ldeg e1>1 or
     smemq(x,d:=lc e1)or smemq(x,n:=red e1)then goto eqn_loop;
   if not mode then
   <<w:=nil;for each y in vars do w:=w or smemq(y,d);
     if w then return'(not)>>;
    % Linear form found: 'd*x+n=0'. This is basis for a solution
    % 'x=-n/d'. In a second branch the case'{n=0,d=0}'has to
    % be considered if 'd' is not a constant.
   n:=reorder n;d:=reorder d;
    % Step 1: Substitute in remaining equations, solve
    % and add linear formula to the result.
   s:=quotsq(negf n ./ 1, d ./ 1);
   neqs:=for each eqn in delete(e,eqs)join
     <<q:=numr subf(eqn,{x.prepsq s});if q then{q}>>;
   nvars:=for each y in delete(x,vars)join if smemq(y,neqs)then{y};
   w:=if null neqs then'(t(nil nil 1))else
    if null nvars then'(inconsistent)else
    if cdr neqs then solvenonlnrsys0(neqs,nvars,lvars)
    else solvenonlnrsysone(car neqs,car nvars);
   if car w eq'failed then return w;
   w:=add!-variable!-to!-tagged!-solutions(x,s,w);
   if domainp d or not mode then return w;
    % Step 2: Add an eventual solution for'n=0,d=0'.
   w1:=solvenonlnrsys0(n.d.eqs,vars,lvars);
   return merge!-two!-tagged!-solutions(w,w1)end;

symbolic procedure solvenonlnrsysone(f,x);
 % Equation system has been reduced to one. Using 'solvesq'.
  begin scalar w;w:=solvesq(f ./ 1,x,1);
   if null w then return'(inconsistent)
   else if null cadr car w then return'(failed);
%  if not smemq('root_of,w) then goto ret;
%    % here we try to find out whether a root_of
%    % expression is a useful information or whether
%    % it is simply an echo of the input.
%  if cdr w then goto ret; % multiple branches: good.
%  q := prepsq caar car w;
%  if not eqcar(q,'root_of) % not on top level: good.
%     then goto ret;
%  q:=subst(x,caddr q,cadr q);
%  if f = numr simp q then return '(failed);
%ret:
   return t.w end;

symbolic procedure add!-variable!-to!-tagged!-solutions(x,s,y);
  % 'y' is a tagged solution. Add equation 'x=s' to all members.
  if eqcar(y,'inconsistent) then y else
  if null y or null cdr y then{t,{{s},{x},1}}else
  car y.for each q in cdr y collect
   % Put new solution into the last position.
     {append(car q,{s}),append(cadr q,{x}),caddr q};

symbolic procedure merge!-two!-tagged!-solutions(w1,w2);
  % 'w1' and 'w2' are tagged solution sets. Merge these and
  % eliminate inconsistent cases.
  if car w1='failed or car w2='failed then'(failed)else
  if car w1='inconsistent then w2 else
  if car w2='inconsistent then w1 else
  car w1.append(cdr w1,cdr w2);

symbolic procedure solvenonlnrsyssep(eqs,vars);
 % 'eqs' is a system of equations (standard forms,
 % implicitly equated to zero); this routine tries
 % to reduce the system recursively by separation,
 % if one variable occurs only in one equation.
  begin scalar y,r,s,r0,u,w,tag;
   if null vars then return'(failed) else
   if null cdr eqs then
   <<if not smember(car vars,car eqs) then
        return solvenonlnrsyssep(eqs,cdr vars);
     r:=solvesq(!*f2q car eqs,car vars,1);
     return if r and cadr car r then 't.r else'(failed)>>;
   for each x in vars do if null y then
   <<r:=nil;
     for each u in eqs do if smember(x,u) then r:=u.r;
     if r and null cdr r then y:=x>>;
   if null y then return'(failed);
   r:=car r;
   s:=solvenonlnrsys(delete(r,eqs),delete(y,vars));
   if car s='failed then return s else s:=cdr s;
   tag:=t;
   u:=for each s0 in s join
   <<w:=for each q in pair(cadr s0,car s0) join
      if not smemq('root_of,cdr q) then{car q.prepsq cdr q};
     r0:=subf(r,w);
     r0:=solvesq(r0,y,caddr s0);
     if null r0 or null cadr car r0 then tag:='failed;
     for each r1 in r0 collect{caar r1. car s0,y.cadr s0,caddr r1}>>;
   return tag.u end;

symbolic procedure solve!-psysp(s,uv);
  % 't' if 's' is a pure polynomial system.
 null s or (solve!-psysp1(car s,uv) and solve!-psysp(cdr s,uv));

symbolic procedure solve!-psysp1(f,uv);
  domainp f or
  ((member(mvar f,uv) or solve!-psysp2(mvar f,uv))
     and solve!-psysp1(lc f,uv) and solve!-psysp1(red f,uv));

symbolic procedure solve!-psysp2(v,uv);
  % 't' if there is no interaction between 'v' and 'uv'.
 null uv or (not smember(car uv,v) and solve!-psysp2(v,cdr uv));

symbolic procedure solvenonlnrsys1(system!*,uv!*);
  % Solve one system.
  begin scalar r,rules;
    osystem!*:=system!*;
    if solvealgtrig0 system!* then rules:='(solvealg!-rules1);
    if smemq('tan,system!*) or smemq('cot,system!*) or
       smemq('tanh,system!*) or smemq('coth,system!*) then
      rules:='solvealg!-rules2.rules;
    r:=evalletsub2({rules,'(solvenonlnrsyspre)},nil);
    if errorp r then return '(failed) else system!*:=car r;
    r:=solvenonlnrsys2();
    return r end;

symbolic procedure solvenonlnrsyspre();
   (for each p in system!* collect numr simp prepf p)
      where dmode!*=nil;

symbolic procedure solvenlnrsimp(u);
 % A prepsq including resimplification with additional rules.
% begin scalar r;
%   r := evalletsub2({'(solvealg!-rules3),
%                      {'simp!* ,mkquote u}},nil);
%   if errorp r then error(99,list("error during postprocessing simp"));
%   return car r;
% end;
      simp!* u;

symbolic procedure solvenonlnrsys2();
  % Main driver. We need non-local exits here
  % because of possibly hidden non algebraic variable
  % dependencies.
  if null !*solvealgp then system!*:='(failed) else % against recursion.
  (begin scalar iv!*,kl!*,inv!*,fv!*,r,w,!*solvealgp,solvealgdb!*,sub!*;
         scalar last!-vars!*,groebroots!*,const!-vars!*,root!-vars!*;
         % preserving the variable sequence if *varopt is off
      if not !*varopt then depl!*:=
        append(pair(uv!*,append(cdr uv!*,{gensym()})),depl!*);
         % hiding dmode because exponentials need integers.
      for each f in system!* do solvealgk0
         (if dmode!* then numr subf(f,nil) where dmode!*=nil else f);
      if !*trnonlnr then print list("original kernels:",kl!*);
      if null cdr system!* then
          if (smemq('sin,system!*)or smemq('cos,system!*)) and
             (r:=solvenonlnrtansub(prepf(w:=car system!*),car uv!*))
             and car r
            then return solvenonlnrtansolve(r,car uv!*,w)
           else if (smemq('sinh,system!*)or smemq('cosh,system!*)) and
             (r:=solvenonlnrtanhsub(prepf(w:=car system!*),car uv!*))
             and car r
            then return solvenonlnrtanhsolve(r,car uv!*,w);
      if atom (errorset('(solvealgk1),!*trnonlnr,nil)) where dmode!*=nil
         then return (system!*:='(failed));
      system!*:='list.for each p in system!* collect prepf p;
      if not('groebner memq loaded!-packages!*)
        then load!-package 'groebner;
      for each x in iv!* do if not member(x,last!-vars!*) then
        for each y in last!-vars!* do depend1(x,y,t);
      iv!* := sort(iv!*,function (lambda(a,b);depends(a,b)));
      if !*trnonlnr then
      <<  prin2t "Entering Groebner for system";
          writepri(mkquote system!*,'only);
          writepri(mkquote('list.iv!*),'only)>>;
      r:={system!*,'list.iv!*};
      r:=groesolveeval r;
      if !*trnonlnr then
      <<prin2t "leaving Groebner with intermediate result";
        writepri(mkquote r,'only);terpri(); terpri()>>;
      if 'sin memq solvealgdb!* then r:=solvealgtrig2 r;
      if 'sinh memq solvealgdb!* then r:=solvealghyp2 r;
      r:= if r='(list) then '(inconsistent) else solvealginv r;
      system!*:=r;  % set value aside
      return r end)where depl!*=depl!* ;

symbolic procedure solvealgk0 p;
   % Extract new top level kernels from form 'p'.
    if domainp p then nil else
    <<if not member(mvar p,kl!*) and not member(mvar p,iv!*)
             then kl!*:=mvar p.kl!*;
      solvealgk0(lc p);solvealgk0(red p)>>;

symbolic procedure solvealgk1();
   % Process all kernels in 'kl!*'. Note that 'kl!*' might
   % change during processing.
    begin scalar k,kl0,kl1;
      k:=car kl!*;
      while k do
       <<kl0:=k.kl0;
         solvealgk2(k);
         kl1:=kl!*;k:=nil;
         while kl1 and null k do
          if not member(car kl1,kl0)then k:=car kl1
              else kl1:=cdr kl1>>end;

symbolic procedure solvealgk2 k;
   % Process one kernel.
     (if member(k,uv!*) then solvealgvb0 k and (iv!*:= k.iv!*) else
      if atom k then t else
      if eq(car k,'expt) then solvealgexpt(k,x) else
      if memq(car k,'(sin cos tan cot)) then solvealgtrig(k,x) else
      if memq(car k,'(sinh cosh tanh coth)) then solvealghyp(k,x) else
      if null x then t else solvealggen(k,x)) where x=solvealgtest(k);

symbolic procedure solvealgtest k;
  % Test if the arguments of a composite kernel interact with
  % the variables known so far.
   if atom k then nil else solvealgtest0 k;

symbolic procedure solvealgtest0 k;
  % Test if kernel 'k' interacts with the known variables.
   solvealgtest1(k,iv!*) or solvealgtest1(k,uv!*);

symbolic procedure solvealgtest1(k,kl);
  % List of those kernels in list 'kl', which occur somewhere
  % in the composite kernel 'k'.
  if null kl then nil else
  if member(k,kl)then{k}else
  if atom k then nil else
  union(if smember(car kl,cdr k) then list car kl else nil,
        solvealgtest1(k,cdr kl));

symbolic procedure solvealgvb k;
  % Restricted variables are those which might establish
  % non-algebraic relations like e.g. 'x + e**x'. Test 'k'
  % and add it to the list.
       fv!*:=append(solvealgvb0 k,fv!*);

symbolic procedure solvealgvb0 k;
 % Test for restricted variables.
   begin scalar ak;
    ak:=allkernels(k,nil);
    if intersection(ak,iv!*) or intersection(ak,fv!*) then
     error(99,list("transcendental variable dependency from",k));
    return ak end;

symbolic procedure allkernels(a,kl);
  % 'a' is an algebraic expression. Extract all possible inner
  % kernels of a and collect them in 'kl'.
   if numberp a then kl else
   if atom a or a member uv!*
        then if not member(a,kl) then a.kl else kl else
   <<for each x in cdr a do
       kl:=allkernels1(numr s,allkernels1(denr s,kl)) where s=simp x;
     kl>>;

symbolic procedure allkernels1(f,kl);
   if domainp f then kl else
   <<if not member(mvar f,kl) then
       kl:=allkernels(mvar f,mvar f.kl);
     allkernels1(lc f, allkernels1(red f,kl))>>;

symbolic procedure solvealgexpt(k,x);
   % Kernel 'k' is an exponential form.
  (if eqcar(m,'quotient) and fixp caddr m then
         if cadr m=1 then solvealgrad(cadr k,caddr m,x)
            else solvealgradx(cadr k,cadr m,caddr m,x)
    else if null x then solvealgid k
    else if ((null intersection(w,uv!*) and
              null intersection(w,iv!*) and
              null intersection(w,fv!*))
         where w=allkernels(m,nil))
      then solvealggen(k,x)
    else solvealgexptgen(k,x)
    )where m=caddr k;

symbolic procedure solvealgexptgen(k,x);
   % Kernel 'k' is a general exponentiation 'u**v'.
  begin scalar bas,xp,nv;
     bas:=cadr k;xp:=caddr k;
     if solvealgtest1(xp,uv!*) then return solvealgexptgen1(k,x)
       else if solvealgtest1(bas,uv!*) then return solvealggen(k,x);
     % Remaining case: "constant" exponential expression to
     % replaced by an id for syntatical reasons
   nv:='(
       % old kernel
      ((expt !&alpha n))
       % new variable
      (!&beta)
       % substitution
      (((expt !&alpha n).!&beta))
       % inverse
      ((!&beta(expt !&alpha n)!&))
       % new equations
      nil
          );
    nv:=subst(bas,'!&alpha,nv);
    nv:=subst(solve!-gensym(),'!&beta,nv);
    nv:=subst(xp,'n,nv);
    return solvealgupd(nv,nil)end;

symbolic procedure solve!-gensym();
  begin scalar w;
    w:=explode solve!-gensymcounter;
    solve!-gensymcounter:=solve!-gensymcounter+1;
    while length w < 4 do w:='!0 .w;
    % If users have things to solve with names like 'G0001' in them, there
    % could be confusion.
    return compress(append(solve!-gensymprefix,w))end;

symbolic procedure solvealgexptgen1(k,x);
   % Kernel 'k' is a general exponentiation 'u**v'.
   % where 'v' is an expression in a solution variable, 'u'
   % is constant. Transform all kernels with same basis
   % and compatible exponent to common exponent denominator
   % form.
  begin scalar bas,xp,xpl,q,r,nk,sub;
     bas:=cadr k;xp:=caddr k;
      % Collect all exponentials with this basis.
     xpl:={(1 ./ 1).xp};
     for each k in kl!* do
       if eqcar(k,'expt) and cadr k=bas and
        <<q:=simp{'quotient,r:=caddr k,xp};
          fixp numr q and fixp denr q>> then
       <<kl!*:=delete(k,kl!*); xpl:=(q.r).xpl>>;
       % compute common denominator.
     q:=1;for each e in xpl do q:=lcm(q,denr car e);
       % the new artificial kernel.
     nk:=reval{'expt,bas,{'quotient,xp,q}};
     sub:=for each e in xpl collect
       {'expt,bas,cdr e}.
       {'expt,nk,numr car e*q/denr car e};
     system!*:=sublis(sub,system!*);
     return solvealggen(nk,x)end;

symbolic procedure solvealgradx(x,m,n,y);
   %   error(99,"forms e**(x/2) not yet implemented");
   solvealgexptgen1({'expt,x,{'quotient,m,n}},y);

symbolic procedure solvealgrad(x,n,y);
  % 'k' is a radical exponentiation expression 'x**1/n'.
  begin scalar nv,m,!&beta;
    !&beta:=solve!-gensym();
    nv:='(
       % old kernel
      ((expt !&alpha(quotient 1 !&n)))
       % new variable
      (!&beta)
       % substitution
      (((expt !&alpha(quotient 1 !&n)).!&beta))
       % inverse
  %   ((!&beta !&alpha (expt !& !&n)))
      nil
       % new equation
      ((difference(expt !&beta !&n)!&alpha))
          );
    m:={'!&alpha.x,'!&beta.!&beta,'!&n.n};
    nv:=subla(m,nv);
    root!-vars!*:=!&beta.root!-vars!*;
      % prepare roots for simple surds.
    if null y or y={x} then groebroots!*:=
     ({'plus,{'expt,!&beta,n},reval{'minus,x}}
       .{{{'equal,!&beta,{'expt,x,{'quotient,1,n}}}}}).groebroots!*;
    if null y then last!-vars!*:=!&beta.last!-vars!*;
    return solvealgupd(nv,y)end;

symbolic procedure solvealgtrig0 f;
  % Examine if 'sin/cos' identies must be applied.
  begin scalar args,r,c;
   args:=for each a in solvealgtrig01(f,nil)collect
     (union(kernels numr q,kernels denr q)where q=simp a);
   while args do
   <<c:=car args;args:=cdr args;
     for each q in args do r:=r or intersection(c,q)>>;
   return r end;

symbolic procedure solvealgtrig01(f,args);
  if atom f then args else
  if memq(car f,'(sin cos tan cot sinh cosh tanh coth)) then
     if constant_exprp cadr f then args else union({cadr f},args)
  else solvealgtrig01(cdr f,solvealgtrig01(car f,args));

algebraic <<
   operator p_sign,the_1;
   let p_sign(~x) => if sign(x)=0 then 1 else sign(x);
   let the_1(~x) =>1 >>;

symbolic procedure solvealgtrig(k,x);
  % 'k' is a trigonometric function call.
  begin scalar nv,m,s,!&alpha,!&beta;
    solvealgdb!*:=union('(sin),solvealgdb!*);
    if x then
      if cdr x then error(99,"too many variables in trig. function")
      else x:=car x;
    solvealgvb k;
    nv:='(
       % old kernels
      ((sin !&alpha)(cos !&alpha)(tan !&alpha)(cot !&alpha))
       % new variables
      ((sin !&beta)(cos !&beta))
       % substitutions
      (((sin !&alpha).(sin !&beta))
        ((cos !&alpha).(cos !&beta))
        %%% these should be handled now by the ruleset.
        %%% ((tan !&alpha).(quotient(sin !&beta)(cos !&beta)))
        %%% ((cot !&alpha).(quotient(cos !&beta)(sin !&beta)))
      )
       % inverses
      (
            ((sin !&beta)
             (cond ((and !*expli (test_trig))
                         '(!&loc (p_sign (!&!& !&))))
                   (t    '(!&x (!&!& (root_of (equal (sin !&alpha)
                                        !&) !&x))))))
            ((cos !&beta)
             (cond ((and !*expli (test_trig))
                         '(!&x (plus (!&!& (times !&loc (acos !&)))
                           (times 2 pi !&arb))))
                   (t    '(!&x (!&!& (root_of (equal (cos !&alpha)
                                               !&) !&x))))))
      )
       % new equation
      ((plus(expt(sin !&beta)2)(expt(cos !&beta)2)-1))
          );
     % invert the inner expression.
    s:=if x then solvealginner(cadr k,x) else 'the_1;
    !&beta:=solve!-gensym();
    m:={'!&alpha.(!&alpha:=cadr k),
              '!&beta.!&beta,
              '!&loc.solve!-gensym(),
              '!&arb.{'arbint,!!arbint:=!!arbint+1},
              '!&x.x,
              '!&!&.s};
    nv:=sublis!-pat(m,nv);
    if x then last!-vars!*:=
        append(last!-vars!*,{{'sin,!&beta},{'cos,!&beta}})
      else const!-vars!*:=
        append(const!-vars!*,{{'sin,!&beta}.{'sin,!&alpha},
                  {'cos,!&beta}.{'cos,!&alpha}});
    return solvealgupd(nv,nil)end;

symbolic procedure solvealghyp(k,x);
  % 'k' is a hyperbolic function call.
  begin scalar nv,m,s,!&alpha,!&beta;
    solvealgdb!*:=union('(sinh),solvealgdb!*);
    if x then
      if cdr x then
       error(99,"too many variables in hyp. function")
      else x:=car x;
    solvealgvb k;
    nv:='(
       % old kernels
      ((sinh !&alpha)(cosh !&alpha)(tanh !&alpha)(coth !&alpha))
       % new variables
      ((sinh !&beta)(cosh !&beta))
       % substitutions
      (((sinh !&alpha).(sinh !&beta))
       ((cosh !&alpha).(cosh !&beta))
      )
       % inverses
      (
            ((sinh !&beta)
             (cond ((and !*expli (test_hyp))
                         '(!&loc (p_sign (!&!& !&))))
                   (t    '(!&x (!&!& (root_of (equal (sinh !&alpha)
                                        !&) !&x))))))
            ((cosh !&beta)
             (cond ((and !*expli (test_hyp))
                         '(!&x (plus (!&!& (times !&loc (acosh !&)))
                           (times 2 pi i !&arb))))
                   (t    '(!&x (!&!& (root_of (equal (cosh !&alpha)
                                               !&) !&x))))))
      )
       % new equation
      ((plus(minus(expt(sinh !&beta)2))(expt(cosh !&beta)2)-1))
          );
     % invert the inner expression.
    s:=if x then solvealginner(cadr k,x)else'the_1;
    !&beta:=solve!-gensym();
    m:=list('!&alpha.(!&alpha:=cadr k),
            '!&beta.!&beta,
            '!&loc.solve!-gensym(),
            '!&arb.{'arbint,!!arbint:=!!arbint+1},
            '!&x.x,
            '!&!&.s);
    nv:=sublis!-pat(m,nv);
    if x then last!-vars!*:=
        append(last!-vars!*,{{'sinh,!&beta},{'cosh,!&beta}})
      else const!-vars!*:=
        append(const!-vars!*,{{'sinh,!&beta}.{'sinh,!&alpha},
                  {'cosh,!&beta}.{'cosh,!&alpha}});
    return solvealgupd(nv,nil)end;

symbolic procedure solvealgtrig2 u;
  % 'r' is a result from goesolve; remove trivial relations
  % like 'sin^2 + cos^2 = 1'.
  begin scalar r,w,op,v,rh;
   for each s in cdr u do
   <<w:=nil;
     for each e in s do
         % delete 'sin u = sqrt(-cos u^2+1)' etc.
      if eqcar(e,'equal) and
         (eqcar(cadr e,'sin) or eqcar(cadr e,'cos)) and
         (op:=caadr e)and(v:=cadr cadr e)and
         member(if eqcar(rh:=caddr e,'!*sq!*) then cadr rh else rh,
          subst({if op='sin then 'cos else 'sin,v},'!-form!-,
               '((minus (sqrt (plus (minus (expt !-form!- 2)) 1)))
                 (sqrt (plus (minus (expt !-form!- 2)) 1)))))
          then nil
       else w:=e.w;
     w:=reverse w;
     if not member(w,r)then r:=w.r>>;
    return 'list.reverse r end;

symbolic procedure solvealghyp2 u;
  % 'r' is a result from goesolve; remove trivial relations
  % like 'cosh^2 - sinh^2 = 1'.
  begin scalar r,w,op,v,rh;
   for each s in cdr u do
   <<w:=nil;
     for each e in s do
        % delete 'sinh u = sqrt(cosh u^2-1)','cosh u = sqrt(sinh u^2+1)'.
      if eqcar(e,'equal) and
         (eqcar(cadr e,'sinh) or eqcar(cadr e,'cosh)) and
         (op:=caadr e) and (v:=cadr cadr e) and
         member(if eqcar(rh:=caddr e,'!*sq!*) then cadr rh else rh,
          if op='sinh then
            subst({'cosh,v},'!-form!-,
                  '((minus (sqrt (plus (expt !-form!- 2) 1)))
                    (sqrt (plus (expt !-form!- 2) 1))))
           else
            subst({'sinh,v},'!-form!-,
                  '((minus (sqrt (plus (expt !-form!- 2) (minus 1))))
                    (sqrt (plus (expt !-form!- 2) (minus 1))))))
         then nil
       else w:=e.w;
     w:=reverse w;
     if not member(w,r) then r:=w.r>>;
    return 'list.reverse r end;

symbolic procedure solvealggen(k,x);
  % 'k' is a general function call; processable if solve
  % can invert the function.
  begin scalar nv,m,s;
    if cdr x then
       error(99,"too many variables in function expression");
    x:=car x;
    solvealgvb k;
    nv:='(
       % old kernels
      (!&alpha)
       % new variables
      (!&beta)
       % substitutions
      ((!&alpha.!&beta))
       % inverses
      ((!&beta'(!&x(!&!& !&))))
       % new equation
      nil);
     % invert the kernel expression.
    s:=solvealginner(k,x);
    m:={'!&alpha.k,
        '!&beta.solve!-gensym(),
        '!&x.x,
        '!&!&.s};
    nv:=sublis!-pat(m,nv);
    return solvealgupd(nv,nil)end;

symbolic procedure solvealgid k;
  % 'k' is a constant kernel, however in a syntax unprocessable
  % for Groebner (e.g. 'expt(a/2)'); replace temporarily
  begin scalar nv,m;
    nv:='(
       % old kernels
      (!&alpha)
       % new variables
      ()
       % substitutions
      ((!&alpha.!&beta))
       % inverses
      ((!&beta nil.!&alpha))
       % new equation
      nil);
     % invert the kernel expression.
    m:={'!&alpha. k,'!&beta.solve!-gensym()};
    nv:=sublis(m,nv);
    return solvealgupd(nv,nil)end;

symbolic procedure solvealginner(s,x);
  <<s:=solveeval1{{'equal,s,'!#},{'list,x}};
    s:=reval cadr s;
    if not eqcar(s,'equal) or not equal(cadr s,x) then
        error (99,"inner expression cannot be inverted");
    {'lambda,'(!#),caddr s}>>;

symbolic procedure solvealgupd(u,innervars);
  % Update the system and the structures.
  begin scalar ov,nv,sub,inv,neqs;
    ov:=car u;u:=cdr u;nv:=car u;u:=cdr u;
    sub:=car u;u:=cdr u;inv:=car u;u:=cdr u;
    neqs:=car u;u:=cdr u;for each x in ov do kl!*:=delete(x,kl!*);
    for each x in innervars do
      for each y in nv do depend1(y,x,t);
    sub!*:=append(sub,sub!*);
    iv!*:=append(nv,iv!*);
    inv!*:=append(inv,inv!*);
    system!*:=append(
      for each u in neqs collect
        <<u:=numr simp u;solvealgk0 u;u>>,
      for each u in system!* collect numr subf(u,sub));
    return t end;

symbolic procedure solvealginv u;
  % Reestablish the original variables, produce inverse
  % mapping and do complete value propagation.
 begin scalar v,r,s,m,lh,rh,y,z,tag,sub0,sub,!*expli,noarb,arbs;
       scalar abort;integer n;
 sub0:=for each p in sub!* collect(cdr p.car p);
 tag:=t;
 r:=for each sol in cdr u join
 <<sub:=sub0;abort:=v:=r:=s:=noarb:=arbs:=nil;
    if !*test_solvealg then
    <<prin2t "================================";
      prin2t const!-vars!*;
      prin2t " next basis:";
      writepri(mkquote sol,'only)>>;
    for each eqn in reverse cdr sol do
    <<lh:=cadr eqn;rh:=subsq(simp!* caddr eqn,s);
      if !*test_solvealg then writepri(mkquote {'equal,lh,prepsq rh},'only);
      !*expli:=member(lh,iv!*);
        % Look for violated constant relations.
      if(y:=assoc(lh,const!-vars!*))and constant_exprp prepsq rh
         and numr subtrsq(rh,simp cdr y) then abort:=t;
        % Look for a 'negative' root.
      if memq(lh,root!-vars!*)and numberp(y:=reval{'sign,prepsq rh})
        and y<0 then abort:=t;
      if not !*expli then noarb:=t;
      if !*expli and not noarb then
      << % Assign value to free variables;
        for each x in uv!* do
         if !*arbvars and solvealgdepends(rh,x) and not member(x,fv!*)
             and not member(x,arbs) then
           <<z:=mvar makearbcomplex();
             y:=z;v:=x.v;r:=simp y.r;
          %  rh:=subsq(rh,list(x.y));
          %  s:=(x.y).s;
             arbs:=x.arbs>>;
       if not smemq('root_of,rh) then s:=(lh.prepsq rh).s
           else fv!*:=lh.fv!*>>;
      if(m:=assoc(lh,inv!*))then
      <<m:=cdr m;lh :=car m;kl!*:=eqn;
        if eqcar(lh,'cond) or eqcar(lh,'quote) then
              lh:=car(m:=eval lh);
        rh:=solvenlnrsimp subst(prepsq rh,'!&,cadr m)>>;
          % If local variable, append to substitution.
      if not member(lh,uv!*) and !*expli then
      <<sub:=append(sub,{lh.(z:=prepsq subsq(rh,sub))});
        if smember(lh,r) then r:=subst(z,lh,r)>>;
          % Append to the final output.
      if (member(lh,uv!*) or not !*expli)
               % Inhibit repeated same values.
            and not<<z:=subsq(rh,sub);
                     n:=length member(z,r);
                     n>0 and lh=nth(v,length v + 1 - n)>>
         then<<r:=z.r;v:=lh.v;>> >>;
      % Classify result.
  % for each x in uv!* do
  %   if tag and not member(x,v) and smember(x,r) then tag:=nil;
    if !*test_solvealg then
     if abort then yesp "aborted" else
    <<prin2t " --------> ";
      writepri(mkquote ('list.for each u in pair(v,r) collect
                {'equal,car u,prepsq cdr u}),'only);
      prin2t "================================";
      yesp "continue?">>;
    if not abort then{reverse r.reverse v}>>;
  return solvealg!-verify(tag,r)end;

symbolic procedure solvealgdepends(u,x);
   % Inspect u for explicit dependency of 'x', being careful for
   % 'root_of' subexpressions.
  if u=x then t else if atom u then nil else
  if eqcar(u,'root_of) then
      if x=caddr u then nil else solvealgdepends(cadr u,x) else
    solvealgdepends(car u,x) or solvealgdepends(cdr u,x);

symbolic procedure test_trig();
  begin scalar lh,rh,r;
   lh:=cadr kl!*;rh:=caddr kl!*;
   if member(lh.nil,solvealgdb!*) then return nil;
   r:=not !*complex and not smemq('i,kl!*) and
      not smemq('!:gi!:,kl!*) and not smemq('!:cr!:,kl!*) and
      not smemq('root_of,kl!*);
   if not r then solvealgdb!*:=
     append(solvealgdb!*,{('sin.cdr lh).nil,('cos.cdr lh).nil});
   return r end;

symbolic procedure test_hyp();
  begin scalar lh,rh,r;
   lh:=cadr kl!*;rh:=caddr kl!*;
   if member(lh.nil,solvealgdb!*) then return nil;
   r:=not !*complex and not smemq('i,kl!*) and
      not smemq('!:gi!:,kl!*) and not smemq('!:cr!:,kl!*) and
      not smemq('root_of,kl!*);
   if not r then solvealgdb!*:=
     append(solvealgdb!*,{('sinh.cdr lh).nil,('cosh.cdr lh).nil});
   return r end;

fluid '(!*solvealg_verify);

% The idea of the following procedure is to exclude isolated
% solutions which give a substantial residue when subsituted
% into the equation system under "on rounded"; as long as no
% good criterion for a residue to be small has been found, this
% step is disabled.

symbolic procedure solvealg!-verify(tag,r);
  <<if !*rounded and !*solvealg_verify then
    begin scalar min,s,cmpl,!*msg;
     % Exclude solutions with a residue substantially
     % above the minimum of all nonzero residues.
      cmpl:=!*complex;
      if not cmpl then setdmode('complex,!*complex:=t);
      s:=for each u in r collect solvealg!-verify1 u.u;
      min:=simp'(quotient 1 100);
      r:=for each u in s join
       if null car u or minusf numr subtrsq(car u,min) then {cdr u};
      if not cmpl then
       <<setdmode('complex,nil);!*complex:=nil>>end;
    tag.for each q in r collect car q.cdr q.{1}>>;

symbolic procedure solvealg!-verify1 s;
  % Verify solution 's' for the current equation system.
  begin scalar sub,nexpli,x,y,sum,fail;
   sub:=for each u in pair(cdr s,car s) collect if not nexpli then
   <<y:=prepsq cdr u;
     if not (domainp y or constant_exprp y) then nexpli:=t;
     car u.y>>;
     % A non explicit solution cannot be tested.
   if nexpli then return nil;
   sum:=nil ./ 1;
   for each u in osystem!* do if not fail then
    <<x:=subf(u,sub);
      if domainp numr x then
      sum:=addsq(sum,absf numr x ./ denr x)
        else fail:=t>>;
   return if fail then nil else sum end;

symbolic procedure sublis!-pat(a,u);
  % Like sublis, but replace lambda expressions by matching their
  % actual arguments.
  begin scalar v;
   if atom u then return
   <<v:=assoc(u,a);if v then sublis!-pat(a,cdr v) else u>>;
   v:=assoc(car u,a);
   if v and (v:=cdr v) and eqcar(v,'lambda) then return
      sublis!-pat((caadr v.cadr u).a,caddr v);
   return sublis!-pat1(a,u)end;

symbolic procedure sublis!-pat1(a,l);
  if null l then nil else
  if atom l then sublis!-pat(a,l) else
  sublis!-pat(a,car l).sublis!-pat1(a,cdr l);

%----------------------------------------------------------------
% Section for single trigonometric polynomials
%----------------------------------------------------------------

symbolic procedure solvenonlnrtansub(p,x);
  % Perform tangent substitution.
  if not smemq('sin,p) and not smemq('cos,p) then
    if smemq(x,p) then nil else nil.p
  else if car p='cos then
    if smemq(x,cdr p) then (cdr p).
       '(quotient (difference 1(expt tg!- 2)) (plus 1(expt tg!- 2)))
        else nil.p
  else if car p='sin then
    if smemq(x,cdr p) then (cdr p).
           '(quotient (times 2 tg!-) (plus 1(expt tg!- 2)))
        else nil.p
  else
  (if ca and cd and
     (car ca = car cd or null car ca or null car cd)
         then (car ca or car cd).(cdr ca.cdr cd))
       where ca=solvenonlnrtansub(car p,x),
             cd=solvenonlnrtansub(cdr p,x);

symbolic procedure solvenonlnrtansolve(u,x,w);
  begin scalar v,s,z,r,y;
    integer ar;
    % We reset arbint for each solve call such that equal forms can
    % be recognized by the function union.
   ar:=!!arbint;
   v:=caar u;u:=prepf numr simp cdr u;
   s:=solveeval{u,'tg!-};
   !!arbint:=ar;
   for each q in cdr s do
   <<z:=reval caddr q;
     z:=reval sublis(solvenonlnrtansolve1 z,z);
     !!arbint:=ar;
     y:=solve0({'equal,{'tan,{'quotient,V,2}},z},x);
     r:=union(y,r)>>;
    % Test for the special cases x=pi(not covered by tangent substitution).
   y := errorset2 {'subf,mkquote w,mkquote{x . 'pi}};
   if null errorp y and null numr y
     then <<!!arbint:=ar; r:=union(solve0({'equal,{'cos,x},-1},x),r)>>;
   return t.r end;

symbolic procedure solvenonlnrtansolve1 u;
  % Find all 'cos**2'.
  if atom u then nil else
  if car u='expt and eqcar(cadr u,'cos) and caddr u=2 then
     {u.{'difference,1,{'expt,{'sin,cadr cadr u},2}}}
  else union(solvenonlnrtansolve1 car u,solvenonlnrtansolve1 cdr u);

%----------------------------------------------------------------
% Section for single hyperbolic polynomials
%----------------------------------------------------------------

symbolic procedure solvenonlnrtanhsub(p,x);
  % Perform hyperbolic tangent substitution.
  if not smemq('sinh,p) and not smemq('cosh,p) then
    if smemq(x,p) then nil else nil.p
  else if car p='cosh then
    if smemq(x,cdr p) then (cdr p).
       '(quotient(plus 1(expt tgh!- 2))(difference 1(expt tgh!- 2)))
        else nil.p
  else if car p='sinh then
    if smemq(x,cdr p) then (cdr p).
           '(quotient(times 2 tgh!-)(difference 1(expt tgh!- 2)))
        else nil.p
  else
  (if ca and cd and
     (car ca = car cd or null car ca or null car cd)
         then (car ca or car cd).(cdr ca.cdr cd))
       where ca=solvenonlnrtanhsub(car p,x),
             cd=solvenonlnrtanhsub(cdr p,x);

symbolic procedure solvenonlnrtanhsolve(u,x,w);
  begin scalar v,s,z,r,y,ar;
   ar:=!!arbint;
   v:=caar u;u:=prepf numr simp cdr u;
   s:=solveeval{u,'tgh!-};
   ar:=!!arbint;
   for each q in cdr s do
   <<z:=reval caddr q;
     z:=reval sublis(solvenonlnrtanhsolve1 z,z);
     !!arbint:=ar;
     y:=solve0({'equal,{'tanh,{'quotient,v,2}},z},x);
     r:=union(y,r)>>;
   if !*complex and null numr subf(w,{x.'(times pi i)})
     then <<!!arbint:=ar; r:=union(solve0({'equal,{'cosh,x},-1},x),r)>>;
   return t.r end;

symbolic procedure solvenonlnrtanhsolve1 u;
  % Find all 'cosh**2'.
  if atom u then nil else
  if car u='expt and eqcar(cadr u,'cosh) and caddr u=2 then
     {u.{'plus,1,{'expt,{'sinh,cadr cadr u},2}}}
  else union(solvenonlnrtanhsolve1 car u,solvenonlnrtanhsolve1 cdr u);

endmodule;;end;
