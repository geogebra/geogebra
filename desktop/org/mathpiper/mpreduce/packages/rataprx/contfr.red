module contfr;  % Simultaneous approximation of a real number by a
                % continued fraction and a rational number with optional
                % user controlled precision (upper bound for numerator).

% Author: Herbert Melenk.

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


symbolic procedure contfract2 (u,b1);
 % compute continued fraction until either numerator exceeds b1
 % or approximation has reached system precision.
  begin scalar b0,l,a,b,g,gg,ggg,h,hh,hhh;
    b:= u; g:=0; gg:=1; h:=1; hh:=0;
    if null b1 then b0:= absf !:times(b,!:expt(10,- precision 0));
  loop:
    a:=rd!-fix b;
    ggg:=a*gg + g;
    hhh:=a*hh + h;
    if b1 and abs hhh > b1 then goto ret;
    g := gg; gg:=ggg; h:=hh; hh:=hhh;
    l:=a.l;
    b:=!:difference(b,a);
    if null b
      or !:lessp(absf !:difference(!:quotient(i2rd!* gg,i2rd!* hh),u),
                 b0)
         then go to ret;
    b:=!:quotient(1,b);
    go to loop;
  ret:
    return  (gg . hh) . reversip l
  end;

symbolic procedure !:lessp(u,v); !:minusp !:difference(u,v);

symbolic procedure rd!-fix u;
   if atom cdr u then fix cdr u else ashift(cadr u,cddr u);

symbolic procedure contfract1(u,b);
  begin scalar oldmode,v;
   if eqcar(v:=u,'!:rd!:) then goto c;
   oldmode := get(dmode!*,'dname).!*rounded;
   if car oldmode then setdmode(car oldmode,nil);
   setdmode('rounded,t); !*rounded := t;
   v:=reval u;
   setdmode('rounded,nil);
   if car oldmode then setdmode(car oldmode,t);
   !*rounded:=cdr oldmode;
   if eqcar(v,'minus) and (numberp cadr v or eqcar(cadr v,'!:rd!:))
     then v:=!:minus cadr v;
   if fixp v then return (v . 1).{v};
   if not eqcar(v,'!:rd!:) then
     typerr(u,"continued fraction argument");
 c:
   return contfract2(v,b);
  end;

symbolic procedure cont!-fract u;
   <<u:=contfract1(car u,if cdr u then ieval cadr u);
     {'list,{'quotient,caar u,cdar u},'list . cdr u}>>;

put('continued_fraction,'psopfn,'cont!-fract);

endmodule;

end;
