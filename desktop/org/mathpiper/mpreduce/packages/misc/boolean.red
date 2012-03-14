module boolean; % Propositional calculus support.

% Author: Herbert Melenk
%         Konrad Zuse Zentrum Berlin

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


% A form in propositional calculus is transformed to a canonical DNF
% (disjuinct normal form) and then converted back to a or-not-form.
% Polynomials are used as intermediate form mapping or to plus and
% and to times. The variables are embedded in kernels prop* and
% negated variables are represented as not_prop* operators.

create!-package('(boolean),'(contrib misc));

algebraic operator prop!*, not_prop!*;

algebraic infix implies;
algebraic infix equiv;
algebraic precedence equiv,=>;
algebraic precedence implies,equiv;

algebraic
let prop!*(~x)*prop!*(x)=>prop!*(x),
    not_prop!*(~x)*not_prop!*(x)=>not_prop!*(x),
    prop!*(~x)*not_prop!*(x)=>0;

fluid '(propvars!* !'and !'or !'true !'false);

symbolic procedure simp!-prop u;
  begin scalar propvars!*,w,opt;
      % convert to intermediate standard form.
    opt:=for each f in cdr u collect reval f;
    if member('and,opt) then
    <<!'and:='or; !'or:='and; !'true:=0; !'false:=1;>>
      else
    <<!'and:='and; !'or:='or; !'true:=1; !'false:=0;>>;
    w:=reval prepf simp!-prop1(car u,t);
    if w=0 then return simp !'false;
      % add for each variable a true value "and (x or not x)".
    for each x in propvars!* do
      w:=reval{'times,w,prepf simp!-prop1({!'or,x,{'not,x}},t)};
         % transform to distributive.
    w:=simp!-prop!-dist w;
    if not member('full,opt) then w:=simp!-prop2 w;
    w :=simp!-prop!-form w;
    if numberp w then return w ./ 1;
    if not atom w then w:={'boolean,w};
    return (w .**1 .*1 .+nil) ./ 1;
  end;

put('boolean,'simpfn,'simp!-prop);

symbolic procedure simp!-prop1(u,m);
 % Convert logical form to polynomial.
 begin scalar w;
  if atom u then goto z;
  if car u = !'and and m or car u=!'or and not m then
    <<w:=1; for each q in cdr u do
        w:=multf(w,simp!-prop1(q,m))>>
  else if car u=!'or and m or car u=!'and and not m then
   <<w:=nil; for each q in cdr u do
      w:=addf(w,simp!-prop1(q,m))>>
  else if car u='not then w:=simp!-prop1(cadr u,not m)
  else if car u ='implies then
    (if m then w:=simp!-prop1({'or,{'not,cadr u},caddr u},t) else
               w:=simp!-prop1({'or,{'not,caddr u},cadr u},t))
  else if car u= 'equiv then w:=simp!-prop1(
     {'or,{'and,cadr u,caddr u},{'and,{'not,cadr u},{'not,caddr u}}},m)
  else goto z1;
  return w;
 z:
  if u=1 or u=t or u='true then u:=m else
  if u=0 or u=nil or u='false then u:=not m;
  if u=t then return simp!-prop1('(or !*true (not !*true)),t);
  if u=nil then return simp!-prop1('(and !*true (not !*true)),t);
 z1:
  u:=reval u;
  if eqcar(u,'boolean) then return simp!-prop1(cadr u,m);
  w:= numr simp{if m then 'prop!* else 'not_prop!*,u};
  if not member(u,propvars!*) then propvars!*:=u.propvars!*;
  return w;
 end;


symbolic procedure simp!-prop2 w;
 % Remove redundant elements, convert back.
  begin scalar y,z,o,q1,q2,term,old;
   for each x in propvars!* do
   <<old:=nil;
    while w do
    <<term := car w; w := cdr w;
      q1:={'prop!*,x}; q2:={'not_prop!*,x};
      if not member(q1,term) then <<y:=q2;q2:=q1;q1:=y>>;
      z:=subst(q2,q1,term);
      old:=term.old;
      if (o:=member(z,w)) then
        << if o then <<w:=delete(car o,w); old:=car o . old>>;
           term:=delete(q1,term);
           old:=union({term},old);
        >>;
    >>;
    w:=old;
   >>;
   return simp!-prop!-condense w;
  end;

symbolic procedure simp!-prop!-condense u;
  begin scalar w,r;
   u:=sort(u,function(lambda(v1,v2);length(v1)<length(v2)));
   while u do
   <<w:=car u; u:=cdr u; r:=w.r;
     for each q in u do
      if subsetp(w,q) then u:=delete(q,u);
   >>;
   return ordn r;
 end;

symbolic procedure simp!-prop!-dist w;
  % convert to a distributive form.
  <<if eqcar(w,'plus) then w :=cdr w else w:={w};
    w:=for each term in w collect
    <<term:=if eqcar(term,'times) then cdr term else {term};
      if numberp car term then term:=cdr term;
      sort(term,function(lambda(p1,p2);  ordp(cadr p1,cadr p2)))
    >>;
    sort(w,function  simp!-prop!-order)
  >>;

symbolic procedure simp!-prop!-order(a,b);
  if null a then nil else
  if caar a = caar b then simp!-prop!-order(cdr a,cdr b) else
  if caar a = 'prop!* then t else nil;

symbolic procedure simp!-prop!-form u;
  if u='(nil) then !'true else
  <<u:=for each term in u collect
    <<term := for each x in term collect
       if eqcar(x,'not_prop!*) then {'not,cadr x} else cadr x;
      if cdr term then !'and . term else car term>>;
    if cdr u then !'or . u else car u
  >>;

fluid '(bool!-break!*);

%symbolic procedure boolean!-eval u;
%   <<v:=boolean!-eval1 u;
%     if bool!-break!* then
%       rederr(u,"boolean evaluation");
%     v>> where v=nil,bool!-break!*=nil;
%
%put('boolean,'boolfn,'boolean!-eval);

symbolic procedure test!-bool u;
  mk!*sq simp!-prop list boolean!-eval1 car u;

put('testbool,'psopfn,'test!-bool);

symbolic procedure boolean!-eval1 u;
  begin scalar v;
  return
   if eqcar(u,'sq!*) and cddr u and eqcar(v:=prespsq cadr u,'boolean)
       then boolean!-eval2 cadr v
   else boolean!-eval2 prepf numr simp!-prop list u;
  end;

symbolic procedure boolean!-eval2 u;
  if eqcar(u,'boolean) then boolean!-eval2 cadr u else
  if eqcar(u,'and) or eqcar(u,'or) or eqcar(u,'not)
     then car u. for each x in cdr u collect boolean!-eval2 x
  else
   <<r:=errorset(formbool(u,nil,'algebraic),nil,nil)
          where !*protfg=t;
     if errorp r then <<bool!-break!*:=t;erfg!*:=nil; u>>
                 else car r>>
       where r=nil;


put('and,'prtch,"/\");
put('or,'prtch," \/ ");

endmodule;

end;

