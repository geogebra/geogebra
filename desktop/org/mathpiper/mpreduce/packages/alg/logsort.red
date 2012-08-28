module logsort;  % Combine sums of logs.

% Author:  Stanley L. Kameny.

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


global '(domainlist!*);

fluid '(!*div factors!* !*combinelogs !*noneglogs !*expandlogs
        !*uncached);

switch combinelogs,expandlogs;

% !*combinelogs := t;   % Default value is ON.

symbolic procedure clogsq!* x;
   begin scalar !*div,!*combinelogs,!*expandlogs;
      !*div := !*expandlogs := t;
      x:= simp prepsq x where !*uncached=t; !*expandlogs := nil;
      return simp!* comblog prepsq!* x end;

symbolic procedure logsort x; % combines log sums at all levels.
   begin scalar !*div,!*combinelogs,!*expandlogs,!*noneglogs;
      !*div := !*expandlogs := !*noneglogs := t;
      x:= simp x where !*uncached=t; !*expandlogs := nil;
      return comblog prepsq!* x end;

% symbolic procedure logsorta a; aeval logsort a;

% symbolic operator logsorta;

symbolic procedure comblog x;
   if atom x or car x memq domainlist!* then x
   else if car x eq 'plus
     or car x eq 'times and
         ((not domainp y and eqcar(mvar y,'log))
           where y=numr simp!* x)
       then prepsq!* clogsq simp!* x
   else (comblog car x) . comblog cdr x;

symbolic procedure clogsq x; clogf numr x ./ clogf denr x;

symbolic procedure clogf u;
   begin scalar x,y;
      x := kernels u;
      for each j in x do if eqcar(j,'log) then y := j . y;
      if null y then return u;
      x := setdiff(x,y);
      x := setkorder nconc(x,y);
      u := clogf1 reorder u;
      setkorder x;
      return reorder u
   end;

symbolic procedure clogf1 x;
   if domainp x then x
    else if eqcar(mvar x,'log) then clogf2 x
    else addf(multpf(lpow x,clogf1 lc x),clogf1 red x);
%   else ((if null z then x else
%          addf(if atom y then list lt x else numr simp!* comblog y,z))
%      where y=prepsq!*(list lt x ./ 1),z=clogf1 red x);

symbolic procedure clogf2 x; % does actual log combining.
   begin scalar y,z,r,s,g,a,b,c,d,w,xx; integer k;
      xx := x;
  st: if domainp x then <<w := addf(w,x); go to ret>>
       else if not eqcar(mvar x,'log) or ldeg x neq 1
        then <<w := addf(w,list lt x); x := red x; go to st>>;
      y := list lt x;
      if not domainp(z := red x) then go to lp;
     % g := coefgcd(c := lc y,0); a := quotf(c,g);
     % y := multf(a,numr simp!* list('log,logarg(cadr mvar y,g)));
      go to ret;
     % in this loop, y is a log term, r is a term, and z the reductum.
  lp: if domainp z then go to ret;
      r := list lt z; z := red z;
      if eqcar(mvar r,'log) and ldeg r=1 then go to a2;
  a1: s := addf(r,s); go to lp;
  a2: b := coefgcd(a := lc r,0); a := quotf(a,b);
      d := coefgcd(c := lc y,0); c := quotf(c,d);
      g := gcdf(a,c); a := quotf(a,g); c := quotf(c,g);
      if not domainp a or not domainp c then go to a1
       else if numberp a and numberp c then go to a3
       else if quotf(a,c)=-1 then
         <<g := multf(a,b) ./ 1;
           k := 1;
           a := list('quotient,cadr mvar r,cadr mvar y);
           go to a4>>
       else go to a1;
  a3: % a := list('times,logarg(cadr mvar r,multf(a,b)),
      %    logarg(cadr mvar y,multf(c,d))); g := g ./ 1;
      b := multf(a,b); d := multf(c,d);
      k := gcdf(k,gcdf(b,d)); b := quotf(b,k); d := quotf(d,k);
      % Only combine a log if at most one of the arguments is complex.
      % Otherwise you can finish up on the wrong sheet.
      if !*precise and not one_complexlist {cadr mvar r,cadr mvar y}
        then return xx;
      a := list('times,logarg(cadr mvar r,b),
         logarg(cadr mvar y,d)); g := g ./ 1;
  a4: a := prepsq simp!* a;
      y := numr simp!* list('times,k,
         if eqcar(a,'quotient) and cadr a=1
            then list('minus,list('log,caddr a)) else list('log,a),
         prepsq g);
      go to lp;
 ret: return addf(w,addf(y,addf(z,clogf1 s))) end;

symbolic procedure logarg(a,c);
   if c=1 then a else list('expt,a,c);

symbolic procedure coefgcd(u,g);
   if domainp u then gcdf(u,g) else coefgcd(lc u,coefgcd(red u,g));

endmodule;

end;
