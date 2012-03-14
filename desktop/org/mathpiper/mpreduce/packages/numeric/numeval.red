module numeval; % numeric evaluation of algebraic expressions.

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


% Control of accuracy and precision.
%
%  precision:    number of digits used for computation,
%  accuracy:     target precision of the results;
%
%  precision might be modified automatically for reaching the
%  desired accuracy.

symbolic procedure accuracycontrol(u,da,di);
  % u is an evaluated parameter list. Extract
  % accuracy and iterations. If not present, take
  % given default values.
  begin scalar x,n,v;
   v:=u;
   accuracy!*:=da; iterations!*:=di;
   while v do
   <<x:=car v; v:= cdr v;
     if eqcar(x,'equal) and cadr x memq'(accuracy iterations) then
     <<u:=delete(x,u); n:=caddr x;
       if cadr x='accuracy then accuracy!*:=n
           else iterations!*:=n;
   >>>>;
   return u;
 end;

symbolic procedure update!-precision l;
  % l is a list of domain elements. IF the minimum of their absolute
  % values is smaller than 10**(precision*+3), the precision is
  % increased.
  begin scalar mn; integer zp,p;
   mn:=update!-precision1 l; % positive minimum.
   if null mn then return nil;
   p := precision 0; zp:=!:recip expt(10,p-3);
   dm!: <<
     if mn>zp then return nil;
     while mn<zp do <<p:=p+1;zp:=zp/10>>;
     >>;
   precmsg p;
  end;

symbolic procedure update!-precision1 l;
 dm!:( begin scalar x,y,z;
      while l do
      <<x:=car l; l:=cdr l;
        if not zerop x then
        <<y:=abs x; z:=if null z or y<z then y else z>>
      >>;
      return z;
     end );

% Switching of mode and introduction of specific simplifiction
% rules.

algebraic<<

  rules_rd :=
   {~u ** ~x => exp(log u * x) when not numberp x};

  procedure switch!-mode!-rd!-alg u;
   if u=0 then
     <<for all x clear exp x;
       let rules_rd;
     >> else <<
       for all x let exp x=e**x;
       clearrules rules_rd; >>;

>>;

symbolic procedure switch!-mode!-rd u;
  begin scalar oldmode,prec,ne;
  if null u then
    <<if not memq(dmode!*,'(!:rd!: !:cr))then
       <<oldmode:=t; setdmode('rounded,t)>>;
     ne := !*noequiv;
     !*noequiv:=t; prec:=precision 0;
     switch!-mode!-rd!-alg 0;
     return list(oldmode,prec,!*roundbf,ne)
    >> else <<
     if car u then setdmode('rounded,nil);
     precision cadr u;
     !*roundbf := caddr u;
     !*noequiv := cadddr u;
     switch!-mode!-rd!-alg 1;
    >>;
  end;

% Support for the numerical (=domain specific) evaluation of
% algebraic equations.
%
% Roughly equivalent:
%   evaluate(u,v,p) = numr subsq(simp u,pair(v,p))
% but avoiding resimplification as we know that all components
% must evaluate to domain elements.

fluid '(dmarith!* !*evaluateerror);

dmarith!*:= '(
   (difference . !:difference) (quotient . !:!:quotient)
   (minus . negf) (sqrt . num!-sqrtf)
   (expt . !:dmexpt) (min . dm!:min) (max . dm!:max));

symbolic procedure evaluate(u,v,p);
   begin scalar a,r,!*evaluateerror,msg;
    msg := not !*protfg;
    a:= pair(v,p);
    r := errorset(list('evaluate0,mkquote u,mkquote a),msg,nil)
           where !*msg=nil,!*protfg=t;
    if errorp r then rederr
        "error during function evaluation (e.g. singularity)";
    return car r;
  end;

symbolic procedure evaluate!*(u,v,p);
  % same as evaluate, but returning arithmetic (but not syntactical)
  % errors to the caller like errorset.
   begin scalar a,r,!*evaluateerror;
    a:= pair(v,p);
    r := errorset(list('evaluate0,mkquote u,mkquote a),nil,nil)
          where !*msg=nil,!*protfg=t;
    erfg!* := nil;
    if null !*evaluateerror then return r else
         evaluate0(u,a); % once more, but unprotected.
  end;

symbolic procedure evaluate0(u,v);
   evaluate1(evaluate!-horner u,v);

symbolic procedure evaluate1(u,v);
    if numberp u or null u then force!-to!-dm u else
    if pairp u and get(car u,'dname) then u else
     (if a then cdr a else
      if atom u then
             if u='i then
              if (u:=get(dmode!*,'ivalue)) then numr apply(u,'(nil))
                else rederr "i used as indeterminate value"
             else if u='e or u='pi then force!-to!-dm numr simp u
             else <<!*evaluateerror:=t; typerr(u,"number")>>
             else evaluate2(car u,cdr u,v)
                ) where a=assoc(u,v);

symbolic procedure evaluate2(op,pars,v);
 if op='!:dn!: then numr dn!:simp pars else
   <<pars:=for each p in pars collect evaluate1(p,v);
          % arithmetic operator.
     if op='plus then !:dmpluslst pars else
     if op='times then !:dmtimeslst pars else
     if(a:=assoc(op,dmarith!*)) then apply(cdr a,pars) else
          % elementary function, applicable for current dmode.
     if pairp car pars and (a:=get(op,caar pars)) then
          apply(a,pars) else
         % apply REDUCE simplifier otherwise
     force!-to!-dm numr simp (op . for each p in pars collect prepf p)
    >> where a=nil;

symbolic procedure list!-evaluate(u,v,p);
     for each r in u collect evaluate(r,v,p);

symbolic procedure matrix!-evaluate(u,v,p);
     for each r in u collect list!-evaluate(r,v,p);

symbolic procedure !:dmexpt(u,v);
    (if fixp n then !:expt(u,n) else
      <<u:=force!-to!-dm u; v:=force!-to!-dm v;
        if !*complex then crexpt!*(u,v) else rdexpt!*(u,v)
      >>) where n=!:dm2fix v;

symbolic procedure dm!:min(a,b);
   dm!:(if a > b then b else a);

symbolic procedure dm!:max(a,b);
   dm!:(if a > b then a else b);

symbolic procedure !:dm2fix u;
  if fixp u then u else
    (if fixp(u:=int!-equiv!-chk u) then u else
     if null u then 0 else
     if floatp cdr u and 0.0=cdr u-fix cdr u then fix cdr u
     else u) where !*noequiv=nil;

symbolic procedure !:dmtimeslst u;
    if null u then 1 else
    if null cdr u then car u else
       dm!:(car u * !:dmtimeslst cdr u);

symbolic procedure !:dmpluslst u;
    if null u then 0 else
    if null cdr u then car u else
       dm!:(car u + !:dmpluslst cdr u);

symbolic procedure !:!:quotient(u,v);
   !:quotient(u,if fixp v then i2rd!* v else v);

% vector/matrix arithmetic

symbolic procedure list!+list(l1,l2);
   if null l1 then nil else
       dm!: (car l1 + car l2) . list!+list(cdr l1,cdr l2);

symbolic procedure list!-list(l1,l2);
   if null l1 then nil else
       dm!: (car l1 - car l2) . list!-list(cdr l1,cdr l2);

symbolic procedure scal!*list(s,l);
  if null l then nil else
     dm!: (s * car l) . scal!*list(s,cdr l) ;

symbolic procedure innerprod(u,v);
     if null u then 0 else
     dm!: ( car u * car v + innerprod(cdr u,cdr v) );

symbolic procedure conjlist u;
dm!:(if not !*complex then u else
    for each x in u collect
       repartf x - numr apply(get(dmode!*,'ivalue),'(nil))*impartf x );

symbolic procedure normlist u;
  dm!:(sqrt innerprod(u, conjlist u));

symbolic procedure mat!*list(m,v);
    if null cdr m then scal!*list(car v,car m) else
    for each r in m collect innerprod(r,v);

symbolic procedure num!-sqrtf a;
   if domainp a then
       apply(get('sqrt,dmode!*),list force!-to!-dm a)
     else
     <<a:=simpsqrt list prepf a;
       if not onep denr a or not domainp numr a
         then rederr "sqrtf called in wrong mode"
       else numr a>>;

%*******************************************************************************

symbolic procedure force!-to!-dm a;
   if not domainp a then typerr(prepf a,"number") else
   if null a then force!-to!-dm 0 else
   if numberp a then apply(get(dmode!*,'i2d),list a) else
   if pairp a and car a = dmode!* then a else
    (if fcn then apply(fcn,list a) else
       rederr list("conversion error with ",a)
     ) where fcn=(pairp a and get(car a,dmode!*));

symbolic procedure printsflist(x);
   begin integer n;
    writepri("(",nil);
    for each y in x do
       <<if n>0 then writepri(" , ",nil);
         n:=n+1;
         writepri(mkquote prepf y,nil)>>;
    writepri(")",nil);
  end;

fluid '(horner!-cache!*);

symbolic procedure evaluate!-horner u;
  (if w then cdr w else
   <<w:=simp!* u;
     w:=prepsq(hornerf numr w ./ hornerf denr w);
     horner!-cache!* := (u.w). horner!-cache!*;
     w
   >>) where w=assoc(u,horner!-cache!*);

endmodule;

end;
