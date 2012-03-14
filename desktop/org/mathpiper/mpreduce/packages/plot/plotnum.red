module plotnum; % Numeric evaluation of algebraic expressions.

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


fluid '(plotsynerr!* ploteval!-alist2!*);

global '(!*plotinterrupts);

flag('(plus plus2 difference times times2 quotient exp expt expt!-int
%      minus sin cos tan cot asin acos acot atan log),'plotevallisp);
       minus sin cos tan cot asin acos acot atan abs log),'plotevallisp);

symbolic procedure plotrounded d;
 % Switching rounded mode safely on and off.
  begin scalar o,!*protfg,c,r,!*msg;
   !*protfg:=t;
   if null d then
    <<c:=!*complex; r:=!*rounded;
      if c then <<setdmode('complex,nil); !*complex := nil>>;
      if not r and dmode!* then
          <<o:=get(dmode!*,'dname); setdmode(o,nil)>>;
      o:={o,r,!*roundbf,c,precision 0};
      if not r then <<!*rounded:=t; setdmode('rounded,t)>>;
      !*roundbf:=nil;
      if c then <<setdmode('complex,t); !*complex := t>>;
      return o;
    >>
   else
    <<
       % reconstruct the previous configuration.
      if !*complex then setdmode('complex,nil);
      if null(!*rounded:=cadr d) then setdmode('rounded,nil);
      !*roundbf:=caddr d;
      if car(d) then setdmode(car d,t);
      if !*complex then setdmode('complex,t);
      precision car cddddr d;
    >>;
   end;

symbolic procedure adomainp u;
 % numberp test in an algebraic form.
   numberp u or (pairp u and idp car u and get(car u,'dname))
             or eqcar(u,'minus) and adomainp cadr u;

symbolic procedure revalnuminterval(u,num);
 % Evaluate u as interval; numeric bounds required if num=T.
  begin scalar l;
    if not eqcar(u,'!*interval!*) then typerr(u,"interval");
    l:={reval cadr u,reval caddr u};
    if null num or(adomainp car l and adomainp cadr l)then return l;
    typerr(u,"numeric interval");
  end;

ploteval!-alist2!*:={{'x . 1},{'y . 2}};

symbolic procedure plot!-form!-prep(f,x,y);
  % f is a REDUCE function expression depending of x and y.
  % Compute a form which allows me to evaluate "f(x,y)" as
  % a LISP expr.
  {'lambda,'(!&1 !&2),
     {'plot!-form!-call2,mkquote rdwrap f,mkquote f,
                mkquote x,'!&1,
                mkquote y,'!&2}};

symbolic procedure plot!-form!-call2(ff,f,x,x0,y,y0);
 % Here I hack into the statically allocated a-list in order
 % to save cons cells.
  begin scalar a;
     a:=car ploteval!-alist2!*;
     car a := x; cdr a := x0;
     a:=cadr ploteval!-alist2!*;
     car a:= y; cdr a:= y0;
     return plotevalform(ff,f,ploteval!-alist2!*);
  end;

% The following routines are used to transform a REDUCE algebraic
% expression into a form which can be evaluated directly in LISP.

symbolic procedure rdwrap f;
  begin scalar r,!*msg,!*protfg;
    !*protfg:=t;
    r:=errorset({'rdwrap1,mkquote f},nil,nil);
    return if errorp r then 'failed else car r;
  end;

symbolic procedure rdwrap1 f;
if numberp f then float f
  else if f='pi then 3.141592653589793238462643
  else if f='e then 2.7182818284590452353602987
  else if f='i and !*complex then rederr "i not LISP evaluable"
  else if atom f then f
  else if get(car f,'dname) then rdwrap!-dm f
  else if eqcar(f, 'MINUS) then
    begin scalar x;
       x := rdwrap1 cadr f;
       return if numberp x then minus float x else {'MINUS, x}
    end
  else if eqcar(f,'expt) then rdwrap!-expt f
  else
   begin scalar a,w;
     if null getd car f or not flagp(car f,'plotevallisp)
                then typerr(car f,"Lisp arithmetic function (warning)");
     a:=for each c in cdr f collect rdwrap1 c;
     if (w:=atsoc(car f,'((plus.plus2)(times.times2))))
             then return rdwrapn(cdr w,a);
     return car f . a;
   end;

symbolic procedure rdwrapn(f,a);
  % Unfold a n-ary arithmetic call.
   if null cdr a then car a else {f,car a,rdwrapn(f,cdr a)};

symbolic procedure rdwrap!-dm f;
 % f is a domain element.
  if car f eq '!:RD!: then
          if atom cdr f then cdr f else bf2flr f
  else if car f eq '!:CR!: then rdwrap!-cr f
  else if car f eq '!:GI!:
   then rdwrap!-cmplex(f,float cadr f,float cddr f)
  else if car f eq '!:DN!: then rdwrap2 cdr f
  else << plotsynerr!*:=t;
       rerror(PLOTPACKAGE, 32, {get(car f, 'DNAME),
                                "illegal domain for PLOT"})
    >>;

symbolic procedure rdwrap!-cr f;
  begin scalar re,im;
    re := cadr f;
    if not atom re then re := bf2flr re;
    im := cddr f;
    if not atom im then im := bf2flr im;
    return rdwrap!-cmplx(f,re,im);
  end;

symbolic procedure rdwrap!-cmplx(f,re,im);
   if abs im * 1000.0 > abs re then typerr(f,"real number") else re;

symbolic procedure plotrepart u;
   if car u eq '!:rd!: then u else
   if car u memq '(!:gi!: !:cr!:) then '!:rd!: . cadr u;

symbolic procedure rdwrap!-expt f;
  % preserve integer second argument.
  if fixp caddr f then
     if caddr f>0 then {'expt!-int,rdwrap1 cadr f,caddr f}
       else {'quotient,1,{'expt!-int,rdwrap1 cadr f,-caddr f}}
    else {'expt,rdwrap1 cadr f, rdwrap caddr f};

symbolic procedure rdwrap2 f;
% Convert from domain to LISP evaluable value.
  if atom f then f else float car f * 10^cdr f;

symbolic procedure rdwrap!* f;
  % convert a domain element to float.
  if null f then 0.0 else rdwrap f;

symbolic procedure rdunwrap f;
    if f=0.0 then 0 else if f=1.0 then 1 else '!:rd!: . f;

symbolic procedure expt!-int(a,b); expt(a,fix b);

symbolic procedure plotevalform(ff,f,a);
  % ff is LISP evaluable,f REDUCE evaluable.
  begin scalar u,w,!*protfg,mn,r,!*msg;
    !*protfg := t;
%   if ff='failed then goto non_lisp;
    if ff eq 'failed or not atom ff and 'failed memq ff
      then goto non_lisp;
% WN 12. May.97  plot(e^(abs x)); bombed
    u:= errorset({'plotevalform1,mkquote ff,mkquote a},nil,nil);
    if not ploterrorp u and numberp (u:=car u) then
    <<if abs u > plotmax!* then return plotoverflow plotmax!* else
      return u;
    >>;
 non_lisp:
    w := {'simp, mkquote
     sublis(
       for each p in a collect (car p.rdunwrap cdr p),
       f)};
    u := errorset(w,nil,nil);
    if ploterrorp u or (u:=car u) eq 'failed then return nil;
    if denr u neq 1 then return nil;
    u:=numr u;
    if null u then return 0; % Wn
    if numberp u then <<r:=float u; goto x>>;
    if not domainp u or not memq(car u,'(!:rd!: !:gi!: !:cr!:))
          then return nil;
    if evalgreaterp(plotrepart u, fl2rd plotmax!*) then
     return plotoverflow plotmax!* else
    if evalgreaterp(fl2rd (-plotmax!*),plotrepart u) then
     return plotoverflow (-plotmax!*);
    r:=errorset({'rdwrap!-dm,mkquote u},nil,nil);
    if errorp r or(r:=car r) eq 'failed then return nil;
  x: return if mn then -r else r;
  end;

symbolic procedure plotoverflow u;
   <<if not !*plotoverflow then
      lprim "plot number range exceeded";
     !*plotoverflow := t;
     'overflow . u
   >> where !*protfg = nil;

symbolic procedure plotevalform0(f,a);
  (if ploterrorp u
       then typerr(f,"expression for plot") else car u)
     where u=
   errorset({'plotevalform1,mkquote f,mkquote a},nil,nil);

%symbolic procedure plotevalform1(f,a);
% begin scalar x,w;
%  if numberp f then return float f;
%  if (x:=assoc(f,a)) then return plotevalform1(cdr x,a);
%  if not atom f and flagp(car f,'plotevallisp) then
%    return eval
%      (car f . for each y in cdr f collect plotevalform1(y,a));
%  if not atom f then f :=
%    car f . for each y in cdr f collect prepf plotevalform2(y,a);
%  w:=simp f;
%  if denr w neq 1 or not domainp numr w then goto err;
%  return rdwrap!* numr w;
% err:
%  plotsynerr!*:=t;
%  typerr(prepsq w,"numeric value");
%end;

symbolic procedure plotevalform1(f,a);
 begin scalar x;
  if numberp f then return float f;
  if (x:=assoc(f,a)) then return plotevalform1(cdr x,a);
  if atom f then go to err;
  return if cddr f then
     idapply(car f,{plotevalform1(cadr f,a),plotevalform1(caddr f,a)})
    else
     idapply(car f,{plotevalform1(cadr f,a)});
 err:
  plotsynerr!*:=t;
  typerr(prepsq f,"numeric value");
end;


%symbolic procedure plotevalform2(f,a);
% begin scalar x,w;
%  if fixp f then return f;
%  if floatp f then return rdunwrap f;
%  if (x:=assoc(f,a)) then return plotevalform2(cdr x,a);
%  if not atom f and flagp(car f,'plotevallisp) then
%    return rdunwrap eval
%      (car f . for each y in cdr f collect plotevalform1(y,a));
%  if not atom f then f :=
%    car f . for each y in cdr f collect prepf plotevalform2(y,a);
%  w:=simp f;
%  if denr w neq 1 or not domainp numr w then goto err;
%  return numr w;
% err:
%  plotsynerr!*:=t;
%  typerr(prepsq w,"numeric value");
%end;

symbolic procedure ploterrorp u;
   if u member !*plotinterrupts
      then rederr prin2 "***** plot killed"
    else atom u or cdr u;

endmodule;

end;
