module mo;

COMMENT

               ##################
               ##              ##
               ##  MONOMIALS   ##
               ##              ##
               ##################



Monomials are of the form x^a*e_i with a multipower x^a and a module
component e_i. They belong either to the base ring R (i=0) or to a
free module R^c (c >= i > 0).

All computations are performed with respect to a "current module"
over a "current ring" (=cali!=basering).

To each module component e_i of the current module we assign a
"column degree", i.e. a monomial representing a certain multidegree
of the basis vector e_i. See the module dpmat for more details.
The column degrees of the current module are stored in the assoc.
list cali!=degrees.


Informal syntax :

  <monomial> ::= (<exponential part> . <degree part>)
  < .. part> ::= list of integer

Here exponent lists may have varying length since trailing zeroes are
assumed to be omitted. The zero component of <exp. part> contains the
module component. It correspond to the phantom var. name cali!=mk.

END COMMENT; 

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

% ----------- manipulations of the degree part --------------------

symbolic procedure mo!=sprod(a,b);
% Scalar product of integer lists a and b .
    if not a or not b then 0
    else (car a)#*(car b) #+ mo!=sprod(cdr a,cdr b);

symbolic procedure mo!=deglist(a);
% a is an exponent list. Returns the degree list of a.
  if null a then
      for each x in ring_degrees cali!=basering collect 0
  else (mo!=sum(
      for each x in ring_degrees cali!=basering collect
                mo!=sprod(cdr a,x),
      if b then cddr b else nil)
      where b = assoc(car a,cali!=degrees));

symbolic procedure mo_neworder m;
% Deletes trailing zeroes and returns m with new degree part.
    (m1 . mo!=deglist m1) where m1 =mo!=shorten car m;

symbolic procedure mo_degneworder l;
% New degree parts in the degree list l.
  for each x in l collect car x . mo_neworder cdr x;

symbolic procedure mo!=shorten m;
    begin scalar m1;
    m1:=reverse m;
    while m1 and eqn(car m1,0) do m1:=cdr m1;
    return reversip m1;
    end;

% ------------- comparisions of monomials -----------------

symbolic procedure mo_zero;  nil . mo!=deglist nil;
% Returns the unit monomial x^0.

symbolic procedure mo_zero!? u; mo!=zero car u;

symbolic procedure mo!=zero u;
    null u or car u = 0 and mo!=zero cdr u;

symbolic procedure mo_equal!?(m1,m2);
% Test whether m1 = m2.
    equal(mo!=shorten car m1,mo!=shorten car m2);

symbolic procedure mo_divides!?(m1,m2);
% m1,m2:monomial. true :<=> m1 divides m2
    mo!=modiv1(car m1,car m2);

symbolic procedure mo!=modiv1(e1,e2);
    if not e1 then t else if not e2 then nil
    else leq(car e1,car e2) and mo!=modiv1(cdr e1, cdr e2);

symbolic procedure mo_compare(m1,m2);
% compare (m1,m2) . m1 < m2 => -1 | m1 = m2 => 0 | m1 > m2 => +1
   begin scalar x;
        x:=mo!=degcomp(cdr m1,cdr m2);
        if x=0 then
            x:=if equal(ring_tag cali!=basering,'revlex) then
                       mo!=revlexcomp(car m1, car m2)
               else mo!=lexcomp(car m1,car m2);
        return x;
   end;

symbolic procedure mo_dlexcomp(a,b); mo!=lexcomp(car a,car b)=1;
% Descending lexicographic order, first by mo_comp.

symbolic procedure mo!=degcomp(d1,d2);
    if null d1 then 0
    else if car d1 = car d2 then mo!=degcomp(cdr d1,cdr d2)
    else if car d1 #< car d2 then -1
    else 1;

symbolic procedure mo!=revlexcomp(e1,e2);
    if length e1 #> length e2 then -1
    else if length e2 #> length e1 then 1
    else - mo!=degcomp(reverse e1,reverse e2);

symbolic procedure mo!=lexcomp(e1,e2);
    if null e1 then
        if null e2 then 0 else mo!=lexcomp('(0),e2)
    else if null e2 then mo!=lexcomp(e1,'(0))
    else if car e1 = car e2 then mo!=lexcomp(cdr e1,cdr e2)
    else if car e1 #> car e2 then 1
    else -1;

% ---------- manipulation of the module component --------

symbolic procedure mo_comp v;
% Retuns the module component of v.
    if null car v then 0 else caar v;

symbolic procedure mo_from_ei i;
% Make e_i.
    if i=0 then mo_zero() else (x . mo!=deglist x) where x =list(i);

symbolic procedure mo_vdivides!?(v1,v2);
% Equal module component and v1 divides v2.
   eqn(mo_comp v1,mo_comp v2) and mo_divides!?(v1,v2);

symbolic procedure mo_deletecomp v;
% Delete component part.
   if null car v then v
   else if null cdar v then (nil . mo!=deglist nil)
   else ((x . mo!=deglist x) where x=cons(0,cdar v));

symbolic procedure mo_times_ei(i,m);
% Returns m * e_i or n*e_{i+k}, if m=n*e_k.
   (x . mo!=deglist x)
   where x=if null car m then list(i) else cons(i #+ caar m,cdar m);

symbolic procedure mo_deg m; cdr m;
% Returns the degree part of m.

symbolic procedure mo_getdegree(v,l);
% Compute the (virtual) degree of the monomial v with respect to the
% assoc. list l of column degrees.
  mo_deletecomp(if a then mo_sum(v,cdr a) else v)
  where a =assoc(mo_comp(v),l);

% --------------- monomial arithmetics -----------------------

symbolic procedure mo_lcm (m1,m2);
%  Monomial least common multiple.
    begin scalar x,e1,e2;
        e1:=car m1; e2:=car m2;
        while e1 and e2 do
            <<x := (if car e1 #> car e2 then car e1 else car e2) . x;
              e1 := cdr e1; e2 := cdr e2>>;
        x:=append(reversip x,if e1 then e1 else e2);
        return (mo!=shorten x) . (mo!=deglist x);
    end;

symbolic procedure mo_gcd (m1,m2);
%  Monomial greatest common divisor.
   begin scalar x,e1,e2;
      e1:=car m1; e2:=car m2;
      while e1 and e2 do
         <<x := (if car e1 #< car e2 then car e1 else car e2) . x;
           e1 := cdr e1; e2 := cdr e2>>;
      x:=reversip x; return (mo!=shorten x) . (mo!=deglist x);
   end;

symbolic procedure mo_neg v;
% Return v^-1.
  (for each x in car v collect -x).(for each x in cdr v collect -x);

symbolic procedure mo_sum(m1,m2);
%  Monomial product.
   ((mo!=shorten x) . (mo!=deglist x))
                        where x =mo!=sum(car m1,car m2);

symbolic procedure mo!=sum(e1,e2);
    begin scalar x;
      while e1 and e2 do
        <<x := (car e1 #+ car e2) . x; e1 := cdr e1; e2 := cdr e2>>;
      return append(reversip x,if e1 then e1 else e2);
 end;

symbolic procedure mo_diff (m1,m2); mo_sum(m1,mo_neg m2);

symbolic procedure mo_qrem(m,n);
% m,n monomials. Returns (q . r) with m=n^q*r.
    begin scalar m1,n1,q,q1;
    q:=-1; m1:=cdar m; n1:=cdar n;
    while m1 and n1 and (q neq 0) do
        << if car n1 > 0 then
            << q1:=car m1 / car n1;
               if (q=-1) or (q>q1) then q:=q1;
            >>;
           n1:=cdr n1; m1:=cdr m1;
        >>;
    if n1 or (q=-1) then q:=0;
    return q . mo_diff(m,mo_power(n,q));
    end;

symbolic procedure mo_power(mo,n);
% Monomial power mo^n.
   (for each x in car mo collect n #* x) .
                (for each x in cdr mo collect n #* x);

symbolic procedure mo!=pair(a,b);
  if null a or null b then nil
  else (car a . car b) . mo!=pair(cdr a,cdr b);

symbolic procedure mo_2list m;
% Returns a list (var name . exp) for the monomial m.
  begin scalar k; k:=car m;
  return for each x in
    mo!=pair(ring_names cali!=basering, if k then cdr k else nil)
        join if cdr x neq 0 then {x};
  end;


symbolic procedure mo_varexp(var,m);
% Returns the exponent of var:var. name in the monomial m.
    if not member(var,ring_names cali!=basering) then
                        typerr(var,"variable name")
  else begin scalar c;
    c:=assoc(var,mo_2list m);
    return if c then cdr c else 0
    end;

symbolic procedure mo_inc(m,x,j);
% Return monomial m with power of var. x increased by j.
  begin scalar n,v;
  if not member(x,v:=ring_all_names cali!=basering) then
    typerr(x,"dpoly variable");
  m:=car m;
  while x neq car v do
    << if m then <<n:=car m . n; m:=cdr m>> else n:=0 . n;
       v:=cdr v;
    >>;
  if m then
    << n:=(car m #+ j).n; if m:=cdr m then n:=nconc(reverse m,n) >>
  else n:=j . n;
  while n and (car n = 0) do n:=cdr n;
  n:=reversip n;
  return n . mo!=deglist n
  end;

symbolic procedure mo_linear m;
% Test whether the monomial m is linear and return the corresponding
% variable or nil.
  (if (length u=1 and cdar u=1) then caar u else nil)
  where u=mo_2list m;

symbolic procedure mo_ecart m;
% Returns the ecart of the monomial m.
    if null car m then 0
    else mo!=sprod(cdar (if a then mo_sum(cdr a,m) else m),
        ring_ecart cali!=basering)
        where a:=atsoc(mo_comp m,cali!=degrees);

symbolic procedure mo_radical m;
% Returns the radical of the monomial m.
   (x . mo!=deglist x)
        where x = for each y in car m collect if y=0 then 0 else 1;

symbolic procedure mo_seed(m,s);
% Set var's outside the list s equal to one.
  begin scalar m1,x,v;
  if not subsetp(s,v:=ring_all_names cali!=basering) then
    typerr(s,"dpoly name's list");
  m1:=car m;
  while m1 and v do
    << x:=cons(if member(car v,s) then car m1 else 0,x);
       m1:=cdr m1; v:=cdr v
    >>;
  while x and eqn(car x,0) do x:=cdr x;
  x:=reversip x;
  return  x . mo!=deglist x;
  end;

symbolic procedure mo_wconvert(m,w);
% Conversion of monomials for weighted Hilbert series.
% w is a list of (integer) weight lists.
   ( x . mo!=deglist x) where
        x = mo!=shorten(0 . for each x in w collect
                (if car m then mo!=sprod(cdar m,x) else 0));

% ---------------- monomial interface ---------------

symbolic procedure mo_from_a u;
% Convert a kernel to a monomial.
   if not(u member ring_all_names cali!=basering) then
                typerr(u,"dpoly variable")
   else begin scalar x,y;
          y:=mo!=shorten
              for each x in ring_all_names cali!=basering collect
                            if x equal u then 1 else 0;
          return  y . mo!=deglist y;
        end;

symbolic procedure mo_2a e;
% Convert a monomial to part of algebraic prefix form of a dpoly.
   mo!=expvec2a1(car e,ring_all_names cali!=basering);

symbolic procedure mo!=expvec2a1(u,v);
    if null u then nil
    else if car u = 0 then mo!=expvec2a1(cdr u,cdr v)
    else if car u = 1 then car v . mo!=expvec2a1(cdr u,cdr v)
    else list('expt,car v,car u) . mo!=expvec2a1(cdr u,cdr v);


symbolic procedure mo_prin(e,v);
% Print monomial e in infix form. V is a boolean variable which is
% true if an element in a product has preceded this one
    mo!=dpevlpri1(car e,ring_all_names cali!=basering,v);

symbolic procedure mo!=dpevlpri1(e,u,v);
    if null e then nil
    else if car e = 0 then mo!=dpevlpri1(cdr e,cdr u,v)
    else <<if v then print_lf "*";
           print_lf car u;
           if car e #> 1 then <<print_lf "^"; print_lf car e>>;
           mo!=dpevlpri1(cdr e,cdr u,t)>>;

symbolic procedure mo_support m;
% Returns the support of the monomial m as a list of var. names
% in the correct order.
   begin scalar u;
   for each x in ring_names cali!=basering do
    if mo_divides!?(mo_from_a x,m) then u:=x . u;
   return reversip u;
   end;

endmodule;  % mo

end;
