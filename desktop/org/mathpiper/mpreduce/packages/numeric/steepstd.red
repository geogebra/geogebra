module steepstd;  % Optimization and root finding with method of
                  % steepest descent.

% Author: H. Melenk, ZIB, Berlin.

% Copyright (c): ZIB Berlin 1991, all rights reserved.

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


fluid '(!*noequiv accuracy!*);
global '(iterations!* !*trnumeric);

symbolic procedure rdmineval u;
  begin scalar e,vars,x,y,z,oldmode,p,!*noequiv;
    oldmode:=steepdecedmode(nil,nil);
    u := for each x in u collect reval x;
    u := accuracycontrol(u,6,40);
    e :=car u; u :=cdr u;
    if eqcar(car u,'list) then
      u := for each x in cdar u collect reval x;
    for each x in u do
     <<if eqcar(x,'equal) then
       <<z:=cadr x; y:=caddr x>> else <<z:=x; y:=random 100>>;
       vars:=z . vars; p := y . p>>;
    vars := reversip vars; p := reversip p;
    x := steepdeceval1(e,vars,p,'num_min);
    steepdecedmode(t,oldmode);
    return list('list, car x, 'list.
       for each p in pair(vars,cdr x) collect
          list('equal,car p,cdr p));
  end;

put('num_min,'psopfn,'rdmineval);

symbolic procedure rdsolvestdeval u;
   % solving a system of equations via minimizing the
   % sum of sqares.
  begin scalar e,vars,x,y,z,oldmode,p,q,!*noequiv;
    oldmode:=steepdecedmode(nil,nil);
    u := for each x in u collect reval x;
    e :=car u; u :=cdr u;
      % construct the function to minimize.
    e:=if eqcar(e,'list) then cdr e else list e;
    q := 'plus . for each f in e collect
       list('expt,if eqexpr f then !*eqn2a f else f,2);
    q := prepsq simp q;
      % starting point & variables.
    if eqcar(car u,'list) then
      u := for each x in cdar u collect reval x;
    for each x in u do
     <<if eqcar(x,'equal) then
       <<z:=cadr x; y:=caddr x>> else <<z:=x; y:=random 100>>;
       vars:=z . vars; p := y . p>>;
    vars := reversip vars; p := reversip p;
    x := steepdeceval1(q,vars,p,'root);
    steepdecedmode(t,oldmode);
    if null x then rederr "no solution found";
    return 'list.
       for each p in pair(vars,cdr x) collect
          list('equal,car p,cdr p);
  end;

symbolic procedure steepdecedmode(m,oldmode);
  if not m then % initial call
   << if !*complex then rederr
      "steepest descent method not applicable under complex";
    if not (dmode!*='!:rd!:)then
       <<oldmode:=t; setdmode('rounded,t)>>;
    {oldmode,precision 0,!*roundbf}>>
  else % reset to old dmode.
    <<if car oldmode then setdmode('rounded,nil);
      precision cadr oldmode;
      !*roundbf:=caddr oldmode>>;

symbolic procedure steepdeceval1(f,vars,x,mode);
  begin scalar e,g,r,acc;
        integer n;
     n := length vars;
    % establish the target function and the gradient.
     e := prepsq simp f;
     if !*trnumeric then lprim "computing symbolic gradient";
     g := for each v in vars collect prepsq simp list('df,f,v);
     !*noequiv:=t;
     if !*trnumeric then
      lprim "starting Fletcher Reeves steepest descent iteration";
     acc := !:!:quotient(1,expt(10,accuracy!*));
     x := for each u in x collect force!-to!-dm numr simp u;
     r:= steepdec2(e,g,vars,acc,x,mode);
     r:= for each x in r collect prepf x;
     return r;
 end;

symbolic procedure steepdec2(f,grad,vars,acc,x,mode);
  % Algorithm for finding the minimum of function f
  % with technique of steepest descent, version Fletcher-Reeves.
  % f:     function to minimize (standard quotient);
  % grad:  symbolic gradient (list of standard quotients);
  % vars:  variables (list of id's);
  % acc:   target precision (e.g. 0.000001)
  % x:     starting point (list of numerical standard forms).
  % mode:  minimum / roots type of operation
 dm!:
  begin scalar e0,e00,e1,e2,a,a1,a1a1,a2,a2a2,x1,x2,g,h,dx,
      delta,limit,gold,gnorm,goldnorm,multi;
    integer count,k,n;
    n:=length grad; multi:=n>1; n:=10*n;
    e00 := e0 := evaluate(f,vars,x);
    a1 := 1;
  init:
    k:=0;
      % evaluate gradient (negative).
    g := for each v in grad collect -evaluate(v,vars,x);
    gnorm := normlist g; h:=g;
  loop:
    count := add1 count; k:=add1 k;
    if count>iterations!* then
    <<lprim "requested accuracy not reached within iteration limit";
      % mode := nil;
      goto ready>>;

      % quadratic interpolation in direction of h in order
      % to find the minimum value of f in that direction.
    a2 := nil;
   l1:
    x1 := list!+list(x, scal!*list(a1,h));
    e1 := evaluate(f,vars,x1);
    if e1>e0 then <<a2:=a1; x2:=x1; e2:=e1;
            a1 := a1/2; goto l1>>;
    if a2 then goto alph;
   l2:
    a2 := a1+a1;
    x2 := list!+list(x, scal!*list(a2,h));
    e2 := evaluate(f,vars,x2);
    if e2<e1 then <<a1:=a2; e1:=e2; goto l2>>;
   alph:   %compute lowest point of parabel
    if abs(e1-e2)<acc then goto ready; % constant?
    a1a1:=a1*a1; a2a2:=a2*a2;
    a:= (a1a1-a2a2)*e0 + a2a2*e1 - a1a1*e2 ;
    a:= a/((a1-a2)*e0 + a2*e1-a1*e2);
    a:= a/2;

     % new point
    dx:=scal!*list(a,h); x:=list!+list(x, dx);
    e0 := evaluate(f,vars,x);
    if e0>e1 then % a1 was better than interpolation.
    <<dx:=scal!*list(a1-a ,h); x:=list!+list(x,dx);
      e0 := e1; dx:=scal!*list(a1,h)>>;
    delta:= normlist dx;
    steepdecprintpoint(count,x,delta,e0,gnorm);
    update!-precision list(delta,e0,gnorm);

      % compute next direction;
    if k>n then goto init; % reinitialize time to time.
    gold := g; goldnorm:= gnorm;
    g := for each v in grad collect -evaluate(v,vars,x);
    gnorm := normlist g;
    if gnorm<limit then goto ready;
    h := if not multi then g else
        list!+list(g,scal!*list(gnorm/goldnorm,h));
    if mode='num_min and gnorm > acc or
       mode='root and e0 > acc then goto loop;

  ready:
    if mode='root and not(abs e0 < acc) then
     <<lprim "probably fallen into local minimum of f^2";
        return nil>>;
    return e0 . x;
  end;

symbolic procedure steepdecprintpoint(count,x,d,e0,ng);
 if !*trnumeric then
   begin integer n;
    writepri(count,'first);
    writepri(". residue=",nil);
    writepri(mkquote prepf e0,nil);
    writepri(", gradient length=",nil);
    writepri(mkquote prepf ng,'last);
    writepri(" at (",nil);
    for each y in x do
       <<if n>0 then writepri(" , ",nil);
         n:=n+1;
         writepri(mkquote prepf y,nil)>>;
    writepri(")",nil);
    writepri(", stepsize=",nil);
    writepri(mkquote prepf d,nil);
    writepri("",'last);
   end;

endmodule;

end;
