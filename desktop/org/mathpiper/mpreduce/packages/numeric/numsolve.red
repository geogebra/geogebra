module numsolve;  % root finding.

% Author: H. Melenk, ZIB, Berlin

% Copyright (c): ZIB Berlin 1991, all rights resrved
%    the starting valies are converted to simple floating point numbers
%    if they are zeroies, scaled integers or domain elements. HM 16.11.2005.

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


fluid '(!*noequiv accuracy!* !*exptexpand);
global '(iterations!* !*trnumeric erfg!*);

symbolic procedure rdsolveeval u;
     % interface function;
  begin scalar e,vars,y,z,p,r,erfg,mode,all,!*exptexpand;
        integer n;
    erfg:= erfg!*; erfg!* := nil;
    u := for each x in u collect reval x;
    u:=accuracycontrol(u,6,50);
    if (all:='all memq u) then u:=delete('all,u);
    e :=car u; u :=cdr u;
      % construct the function vector.
    e:=if eqcar(e,'list) then cdr e else list e;
    e := for each f in e collect
     if eqexpr (f:=reval f) then !*eqn2a f else f;
    n := length e;
      % starting point & variables.
    if eqcar(car u,'list) then
      u := for each x in cdar u collect reval x;
    for each x in u do
     <<if eqcar(x,'equal) then
       <<z:=cadr x; y:=caddr x;
         if eqcar(y,'!*interval!*) then mode:='i;
       >> else <<z:=x; y:=random 100>>;
       vars:=z . vars; p := y . p>>;
    vars := reversip vars; p := reversip p;
    if not(n=length vars) then
    <<
   %  lprim "numbers of variables and functions don't match;";
   %  lprim "using steepest descent method instead of Newton";
   %  return rdsolvestdeval list ('list.e,'list.u,
   %                      {'equal,'accuracy,accuracy!*},
   %                      {'equal,'iterations,iterations!*});
      rederr "numbers of variables and functions don't match"
    >>;
    if mode='i and length vars>1 or mode neq 'i and all then
           rederr "mode for num_solve not implemented";
    r:=if mode='i then rd!-solve!-interv(e,vars,p,n,all)
        else rdnewton0(e,vars,p,n);
    erfg!* := erfg;
    return r;
  end;

put('num_solve,'psopfn,'rdsolveeval);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%  finding root in an interval (secant method and related)
%

symbolic procedure rd!-solve!-interv(e,vars,p,n,all);
 (begin scalar u,fu,l,fl,x,acc,r,de,w,oldmode;
    n := nil;
    if length p>1 then typerr('list . p,"single interval");
    p:=car p; e:=car e; x:=car vars;
    w := {'boundseval,mkquote {e,{'equal,x,p}}};
   if not memq(dmode!*,'(!:rd!: !:cr!:))then
       <<oldmode:=t; setdmode('rounded,t)>>;
    if errorp errorset(w,nil,nil) then
     typerr(e,"bounded expression");
    acc := !:!:quotient(1,expt(10,accuracy!*));
    l:=evaluate(cadr p,nil,nil); u:=evaluate(caddr p,nil,nil);
    fl:=evaluateuni(e,x,l); fu:=evaluateuni(e,x,u);
    if not all then
    dm!:(if zerop fl then return prepf l else
         if zerop fu then return prepf u else
         if fl*fu<0 then
          r := regula!-falsi(e,x,l,fl,u,fu,acc));
    if null r then de := reval {'df,e,x};
    if null r and not all then
       r:=rd!-solve!-trynewton(e,de,x,l,fl,u,fu,acc);
    if null r then
       r:=rd!-solve!-hardcase(e,de,x,l,fl,u,fu,acc,all);
    if oldmode then setdmode('rounded,nil);
    if r then return r;
    rederr "not applicable";
   end) where !*roundbf=!*roundbf;

symbolic procedure make!-rdsolve!-result1(x,u);
   'list.for each r in u collect {'equal,x,prepf r};

symbolic procedure rd!-solve!-trynewton(e,de,x,l,fl,u,fu,acc);
  begin scalar r,invde;
    invde := {'quotient,1,de};
  dm!: <<
    if fu*evaluateuni(de,x,u) >0 then
      r := rdnewton2({e},{{invde}},{x},acc,{u},'root,l,u);
    if null r and fl*evaluateuni(de,x,l) <0 then
      r := rdnewton2({e},{{invde}},{x},acc,{l},'root,l,u);
    if r and (r:=car r) and l <= r and r <= u then
         r := make!-rdsolve!-result1(x,{r}) else r:=nil;
     >>;
    return r;
  end;

symbolic procedure regula!-falsi(e,x,l,fl,u,fu,acc);
   make!-rdsolve!-result1(x,
       {regula!-falsi1(e,x,l,fl,u,fu,acc,0,1)});

symbolic procedure regula!-falsi1(e,x,l,fl,u,fu,acc,mode1,mode2);
  % modified regula falsi: using bisection in order to
  % traverse root as often as possible.
 dm!: begin scalar m,fm;
     if mode1=mode2 then m:=(u+l)/2
        else m := l*fu/(fu-fl) + u*fl/(fl-fu);
     if (u-l) < acc then return m;
     fm := evaluateuni(e,x,m);
     if zerop fm then return m;
     return if fl*fm<0
       then regula!-falsi1(e,x,l,fl,m,fm,acc,nil,mode1)
       else regula!-falsi1(e,x,m,fm,u,fu,acc,t,mode1);
    end;

symbolic procedure mkminus u; {'minus,u};

symbolic procedure evaluateuni(e,x,p);
     evaluate(e,{x},{p});

symbolic procedure dmboundsuni(e,x,l,u);
  begin scalar i;
     i:=boundseval {e,{'equal,x,{'!*interval!*,prepf l,prepf u} }};
     return numr simp cadr i . numr simp caddr i;
  end;

symbolic procedure rd!-solve!-hardcase(e,de,x,l,fl,u,fu,acc,all);
 dm!: begin scalar il1,il2,pt,iv,r,rr,b,b1,q,d;
     integer lev;
   il1:={(l.fl) .(u.fu)};
  main_loop:
    lev:=lev+1; il2:=nil;
    if null il1 then goto done;
    il1 := reverse il1;
    rd!-solve!-hardcase!-print(lev,il1);
  loop:
    if null il1 then goto bottom;
    iv :=car il1; il1:= cdr il1;
    l:=caar iv; fl:=cdar iv; u:=cadr iv; fu:=cddr iv;
    if (d:=u-l) <0 then goto loop; %empty
    if abs fl<acc then<<pt:=l;goto root_found>>;
    if abs fu<acc then<<pt:=u;goto root_found>>;
    b:=dmboundsuni(de,x,l,u);
      % left boundary of interval
    if (fl>0 and not((b1:=car b)<0))
     or(fl<0 and not((b1:=cdr b)>0))
     or not((pt:=l-fl/b1)<u)  then goto loop;
    if pt=l then goto precerr;
    q:=evaluateuni(e,x,pt);
    if not all and q*fl<0 then return regula!-falsi(e,x,l,fl,pt,q,acc);
    if abs q<acc then goto root_found;
    l:=pt; fl:=q;
      % right boundary of interval
    if (fu>0 and not((b1:=cdr b)>0))
     or(fu<0 and not((b1:=car b)<0))
     or not((pt:=u-fu/b1)>l) then goto loop;
    if pt=u then goto precerr;
    q:=evaluateuni(e,x,pt);
    if not all and q*fu<0 then return regula!-falsi(e,x,pt,q,u,fu,acc);
    if abs q<acc then goto root_found;
    u:=pt; fu:=q;
      % new point
    pt :=(l+u)/2;  %midpoint
    q:=evaluateuni(e,x,pt);
    if not all and q*fu<0 then return regula!-falsi(e,x,l,fl,pt,q,acc);
       % generate new intervals
    if not(abs q<acc) then
     <<il2 :=find!-root!-addinterval(pt.q,u.fu,
               find!-root!-addinterval(l.fl,pt.q,il2));
       goto loop;
     >>;
  root_found:
    r:=pt.r; if not all then goto done;
    rr:=find!-root!-range(e,x,pt,acc);
    il2 :=find!-root!-addinterval(cdr rr,u.fu ,
               find!-root!-addinterval(l.fl,car rr,il2));
    goto loop;
  bottom:
    il1 :=il2;
    goto main_loop;
  done:
    r:=sort(r,function lessp);
    return make!-rdsolve!-result1(x,r);
  precerr:
    rederr "precision not sufficient for finding all roots";
  end;

symbolic procedure rd!-solve!-hardcase!-print(lev,il1);
 if !*trnumeric then
 << prin2t {"recursion level:",lev,"remaining intervals:",length il1};
    writepri( mkquote( 'list.
           for each i in il1 collect
             {'list,{'!*interval!*,prepf caar i,prepf cadr i},
                               dm!: prepf(cadr i-caar i)}),
            'only);
 >>;

symbolic procedure find!-root!-addinterval(l,h,il);
 dm!:  <<if car l < car h then il:=(l.h).il; il>>;

symbolic procedure find!-root!-range(e,x,p,acc);
  % p is a point in (l .. u) where abs e(x)<acc.
  % Find the next interval where e(x) > acc.
dm!:<<
     while not(abs(fl:=evaluateuni(e,x,pl:=pl-acc/2))>acc) do;
     while not(abs(fu:=evaluateuni(e,x,pu:=pu+acc/2))>acc) do;
     (pl.fl).(pu.fu)
    >> where pl=p,pu=p,fl=nil,fu:=nil;

if errorp errorset('(!:difference nil nil),nil,nil) then
<<
symbolic procedure !:difference(u,v);
   if null u then !:minus v else if null v then u
    else if u=v then nil
    else if atom u and atom v then u-v
    else dcombine(u,v,'difference);

symbolic procedure !:quotient(u,v);
   if null u or u=0 then nil
    else if null v or v=0 then rerror(poly,12,"Zero divisor")
    else if atom u and atom v
     % We might also check that remainder is zero in integer case.
     then if null dmode!* then u/v else dcombine(u,!:recip v,'times)
    else dcombine(u,v,'quotient);

>>;

endmodule;

end;
