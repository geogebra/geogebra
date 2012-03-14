module rungeku;  % Numeric solution of ODE's with Runge-Kutta.
   % Version 2: supporting ODE systems

% Author: H. Melenk, ZIB, Berlin

% Copyright (c): ZIB Berlin 1993, all rights resrved

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

put ('num_odesolve,'psopfn,'rungekuttaeval);

symbolic procedure rungekuttaeval u;
     % interface function;
  begin scalar e,f,x,y,sx,sy,en,d,v,w,q;
    u := for each x in u collect reval x;
    u := accuracycontrol(u,20,6);

     % equations
    e :=car u; u :=cdr u;
    e := if eqcar(e,'list) then cdr e else {e};

     % dependent variable
    q :=car u; u :=cdr u;
    q := if eqcar(q,'list) then cdr q else {q};
    for each yy in q do
    <<if not eqcar(yy,'equal) or not idp cadr yy
     then typerr(yy,"expression `dep. variable=starting value'");
     sy:=caddr yy.sy; y := cadr yy.y;
    >>;
    sy:=reversip sy; y:=reversip y;
    if length sy neq length e then
      rederr "number of equations and variables don't correspond";

      % independent variable
    x :=car u; u :=cdr u;
    if not eqcar(x,'equal) or not idp cadr x
       or null (w:=revalnuminterval(caddr x,t))
        then typerr(x,"expression `indep. variable=interval'");
    sx:=car w; en:=cadr w; x := cadr x;

      % convert expressions to explicit ODE system.
    q := for each yy in y collect {'df,yy,x};
    v:=cdr solveeval list('list. e, 'list . q);
    if cdr v then
      ruksyserr(nil,"cannot convert to explicit ODE system");
    f := for each d in q collect rukufindrhs(d,v);
    return rungekutta1(f,x,y,sx,en,sy);
end;

symbolic procedure rukufindrhs(d,e);
  % Find the righthand side for d in list e.
  if atom e then
      ruksyserr(d," cannot be moved to lhs of system") else
  if eqcar(car e,'equal) and cadr car e = d then
           reval caddr car e else
  if pairp e and eqcar(car e,'list) then  rukufindrhs(d,cdar e)
  else rukufindrhs(d,cdr e);

symbolic procedure ruksyserr(u,m);
  <<if u then writepri(mkquote u,'first);
    writepri(mkquote m,'last);
    rederr "Runge-Kutta failed";
  >>;

symbolic procedure rungekutta1(f,x,y,sx,ex,sy);
    %preparations for rungekutta iteration.
 (begin scalar acc,r,oldmode,!*noequiv;
    integer prec;
    if not memq(dmode!*,'(!:rd!: !:cr))then
       <<oldmode:=t; setdmode('rounded,t)>>;
    !*noequiv:=t; prec:=precision 0;
    sx := force!-to!-dm numr simp sx;
    ex := force!-to!-dm numr simp ex;
    sy := for each z in sy collect force!-to!-dm numr simp z;
    acc := !:!:quotient(1,expt(10,accuracy!*));
    if !*trnumeric then prin2t "starting rungekutta iteration";
    r := rungekutta2(f,x,y,sx,ex,sy,acc);
    if oldmode then setdmode('rounded,nil);
    precision prec;
    if null r then rederr "no solution found";
    return 'list.r;
  end) where !*roundbf=!*roundbf;

symbolic procedure rungekutta2(f,xx,yy,xs,xe,ys,acc);
  % Algorithm for numeric ODE solution
  % f(xx,yy): rhs;
  % x:     independent variable;
  % y:     dependent variable;
  % s:     starting point;
  % e:     endpoint;
  % acc:   required accuracy
  % f,yy,ys are vectors (lists).
 dm!:
  begin scalar h,h2,h4,x,y,r,w1,w2,vars,dir;
    integer count,st;
    vars := xx.yy;
    x:=xs; y:=ys;
    h:=(xe - xs)/iterations!*;
     dir := h>0; st:= iterations!*;
     % compute initial stepsize
  adapt:
    h2:=h/2; h4:=h/4;
     % full stepsize.
    w1:=rungekuttastep(f,vars,x,y,h,h2);
     % same with half stepsize.
    w2:=rungekuttastep(f,vars,x,y,h2,h4);
    w2:=rungekuttastep(f,vars,x+h2,w2,h2,h4);
      % abs(w2 - w1)
    if normlist list!-list(w2,w1) > acc then
    <<h:=h2; st:=st+st; goto adapt>>;
    if !*trnumeric and not(st=iterations!*) then
      <<prin2
         "*** RungeKutta increased number of steps to ";
        prin2t st>>;
  loop:
    if (dir and x>xe) or (not dir and x<xe) then goto finis;
    r:=('list.x.y) . r;
    count:=add1 count;
    y:= rungekuttastep(f,vars,x,y,h,h2);
      % Advance solution.
    x:=x+h;
    goto loop;
  finis:
    r:= ('list.xx.yy).('list.xs.ys). rungekuttares(reversip r,st);
    return r;
  end;

symbolic procedure rungekuttares(l,st);
   % eliminate intermediate points.
     if st=iterations!* then l else
     << for i:=1:iterations!* collect
         <<for j:=1:m do l:= cdr l; car l>>
     >> where m=st/iterations!*;

symbolic procedure rungekuttastep(f,vars,x,y,h,h2);
  dm!:
   begin scalar d1,d2,d3,d4;
             %  f(x,y)
    d1:= list!-evaluate(f,vars,x.y);
             %  f(x+h2,y+h2*d1)
    d2:= list!-evaluate(f,vars,
          (x+h2). list!+list(y,scal!*list(h2,d1)));
             %  f(x+h2,y+h2*d2)
    d3:= list!-evaluate(f,vars,
          (x+h2). list!+list(y,scal!*list(h2,d2)));
             %  f(x+h,y+h*d3)
    d4:= list!-evaluate(f,vars,
          (x+h).list!+list(y,scal!*list(h,d3)));
      % find y(x+h) = y+h*(d1 + 2*d2 + 2*d3 + d4)/6.
    y:=list!+list(y,
       scal!*list(!:!:quotient(h,6),
         list!+list(d1,
          list!+list(scal!*list(2,d2),
           list!+list(scal!*list(2,d3),
            d4)))));
    return y;
  end;

endmodule;

end;
