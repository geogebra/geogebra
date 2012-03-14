module newton;  % root finding with generalized Newton methods.

%-------------------------------------------------------------------------------
% Author: H. Melenk, ZIB, Berlin, Germany

% Copyright (c): ZIB Berlin 1992, all rights resrved
% Version 2: termination and damping criterion modified to affine
%            invariance.
% Nov. 94:   Avoid symbolic inversion of Jacobian.
% Version 3:
% Jul. 05:   the result of "normlist" may be a bigfloat.
% Aug. 05    replace "normlist" by "max_abs_number" for large variable
%            numbers.
% Nov. 05    The mapping of rdnewton2 to REDUCE-arithmetic (macro "dm!:")
%            is given up.
%            Acc in rdnewton2 is converted to a standard float (call of
%            function "rounded!-float").

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


fluid '(!*noequiv accuracy!* !*invjacobi);
global '(iterations!* !*trnumeric erfg!*);
% imports precision,matrix,setmatelem,reval,rederr,prepf,lprim,mkquote,
%         writepri;

symbolic procedure rdnewton0(e,vars,p,n);
    %preparations for Newton iteration.
 (begin scalar jac,x,oldmode,!*noequiv;
    integer prec;
    if not memq(dmode!*,'(!:rd!: !:cr!:))then
       <<oldmode:=t; setdmode('rounded,t)>>;
    prec:=precision 0;
    p:=for each x in p collect
         force!-to!-dm numr simp x;
    if !*trnumeric then lprim "computing symbolic Jacobian";
    eval list('matrix,mkquote list list('jacobian,n,n));
    for i:=1:n do for j:=1:n do
        setmatelem(list('jacobian,i,j),
         reval list('df,nth(e,i),nth(vars,j)));
    if !*trnumeric and !*invjacobi
      then lprim "inverting symbolic Jacobian";
    jac:=cdr reval
      if !*invjacobi then '(quotient 1 jacobian) else 'jacobian;
    jac:=for each r in jac collect
       for each c in r collect reval c;
    !*noequiv:=t;
    x:=rdnewton1(e,jac,vars,p,'root);
    if oldmode then setdmode('rounded,nil);
    precision prec;
    if null x then rederr "no solution found";
    return 'list.
       for each p in pair(vars,x) collect
          list('equal,car p,cdr p);
  end) where !*roundbf=!*roundbf;

symbolic procedure rdnewton1(f,jac,vars,x,mode);
  begin scalar r,acc;
     if !*trnumeric then lprim "starting Newton iteration";
     acc:=!:!:quotient(1,expt(10,accuracy!*));
     r:=rdnewton2(f,jac,vars,acc,x,mode,nil,nil);
     r:=for each x in r collect prepf x;
     return r;
 end;

symbolic procedure rdnewton2(f,jac,vars,acc,x,mode,low,high);
  % Algorithm for finding the root function system f
  % with technique of adaptively damped Newton.
  % f:     function to minimize (list of algebraic exprs);
  % jac:   Jacobian, symbolically inverted if *invjacobi is t;
  % vars:  variables (list of id's);
  % acc:   requested accuracy (e.g. 0.0000001)
  % x:     starting point (list of domain elements).
%**** modification nov. 2005
% dm!:
%*******************
  begin scalar n0,n1,e0,e1,dx,dx2,x1,g,dmp,delta,h;
        scalar dxold,dx2old,dmpold;
    integer count;
%***** modification nov. 2005
    acc:=rounded!-float acc;
%*******************
    if !*trnumeric then lprim "Newton iteration";
    mode:=nil;
    if !*trnumeric then lprim "evalute function in the initial point";
    e0:=list!-evaluate(f,vars,x);
  loop:
    count:=add1 count;
    if count>iterations!* then
    <<lprim "requested accuracy not reached within iteration limit";
      goto ready>>;

      % evaluate Jacobian.
    if !*trnumeric then lprim "evaluate Jacobian (or its inverse)";
    g:=matrix!-evaluate(jac,vars,x);
      % the newton step.
    if !*trnumeric then lprim "compute the next point";
    dx:=if !*invjacobi then mat!*list(g,e0) else rdsolvelin(g,e0);
    if null dx then goto jacerr;
    n0:=max_abs_number dx;
    dmp:=1;

  step:
      % evaluate function at new point.
    x1:=list!-list(x,scal!*list(dmp,dx));
    if !*trnumeric then lprim "evalute function in the next point";
    e1 := errorset({'list!-evaluate,mkquote f,
              mkquote vars,mkquote x1},nil,nil)
                       where !*msg=nil,!*protfg=t;
    if errorp e1 then goto contract else e1:=car e1;

        % anticipated next (simplified) Newton step.a
    if !*trnumeric then lprim "compute the point difference";
    dx2:=if !*invjacobi then mat!*list(g,e1) else rdsolvelin(g,e1);
    if null dx2 then goto contract;
    if !*trnumeric then lprim "compute the size of the point difference";
    n1:=max_abs_number dx2;
    if n1=0 or n1<n0 then goto accept;
    if null dmpold then goto contract;

%-------------------------------------------------------------------------------
       % predict optimal damping factor
    h:= dmpold*(max_abs_number list!-list(dx2old,dx)* max_abs_number dx)
        /(max_abs_number dxold*max_abs_number dx2old);
    if h>1 then
%*** modification nov. 2005: 1/10 replaced by 0.1
    << dmp:=if h<10 then 1/h else 0.1;
%***************
       dmpold:=nil;goto step>>;

  contract:
    if !*trnumeric then
         lprim "reduce the difference limititeration to its half";
%*** modification  nov. 2005: /2 replaced by *0.5
    dmp:=dmp*0.5;
%***************
    if dmp<acc then rederr "Newton method does not converge";
    goto step;

  accept:
%******* 8.1.2006: "'!:rd!: ." added.
    delta:='!:rd!: . (dmp*n0);
%***************
    x:=x1;e0:=e1;n0:=n1;
    if low and high and (low>car x or high<car x) then return nil;
    dmpold:=dmp;dxold:=dx;dx2old:=dx2;
    rdnewtonprintpoint(count,x,delta,e0);
    if n1>acc or dmp<1 then<<update!-precision(delta.e0); goto loop>>;

  ready:
    x:=list!-list(x,dx2);
    return x;

  jacerr:rederr "singular Jacobian";
  end;

symbolic procedure rdnewtonprintpoint(count,x,dx,e0);
  if !*trnumeric then
   begin
    writepri(count,'first);
    writepri(". residue=",nil);
    printsflist e0;
    writepri(", step length=",nil);
    writepri(mkquote prepf dx,'last);
    writepri(" at ",nil);
    printsflist x;
    writepri(" ",'last);
   end;

%***** modification aug. 2005: added routines
%*****     max_abs_number positive-rounded-float rounded-float

symbolic procedure max_abs_number v;
% v: is a list of function values which must be  numbers(real or complex).
% max_abs_number deliveres the maximum number of "v", where in any
% case a floating point number is returned. Negative values in v are
% converted to positive on the fly. Big numbers are
% truncated to floating point values of machine size, using the
% function "xbf2flr". A domain element is returned;
 begin scalar m,y;
  for each x in v do
  <<y:=positive!-rounded!-float x;
    if null y then
    <<if !*msg then
      <<writepri("***** max_abs_number, test:",'only);
        writepri(mkquote mkquote x,'only);
        writepri("***** objects:",'only);
        for each z in v do witepri(mkquote mkquote z,'only)>>;
      rederr "compute the (positive) maximum of numbers">>;
    if null m then m:=y else if y>m then m:=y>>;
  return m
 end;

symbolic procedure positive!-rounded!-float x;
% return a system float; if x<0, return -x-
 <<x:=rounded!-float x; if null x then x else if x<0 then -x else x>>;

symbolic procedure rounded!-float x;
% Return a float if x is a (possibly complex) number.
   if null x then 0.0 else
   begin scalar y,z;
    if pairp x and car x eq '!:rd!: then x:=cdr x;
    if floatp x then t else
    if fixp x then x:=float x else
    if pairp x then
    <<y:=car x;z:=cdr x;
      if numberp y and numberp cdr x then
             x:=if z>100 then 1.0e100 else y*(2.0**z) else
      if y eq '!:cr!: then x:=max(positive!-rounded!-float cadr x,
                                  positive!-rounded!-float cddr x) else
      if y eq '!*sq then x:=positive!-rounded!-float caadr x/
                            positive!-rounded!-float cdadr x else x:=nil>>;
    return x
   end;

endmodule;

end;
