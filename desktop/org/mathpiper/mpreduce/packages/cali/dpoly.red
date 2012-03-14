module dpoly;

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


COMMENT

               ##################
               ##              ##
               ## POLYNOMIALS  ##
               ##              ##
               ##################

Polynomial vectors and polynomials are handled in a unique way using
the module component of monomials to store the vector component. If
the component is 0, we have a polynomial, otherwise a vector. They
are represented in a distributive form (dpoly for short).

Informal syntax of (vector) polynomials :

   <dpoly>   ::= list of <term>s
   <term>    ::= ( <monomial> . <base coefficient> )

END COMMENT;

% ----------- constructors and selectors -------------------

symbolic procedure dp_lc p;
% Leading base coefficient of the dpoly p.
   cdar p;

symbolic procedure dp_lmon p;
% Leading monomial of the dpoly p.
   caar p;

symbolic procedure dp_term (a,e);
%  Constitutes a term from a:base coeff. and e:monomial.
   (e . a);

symbolic procedure dp_from_ei n;
% Returns e_i as dpoly.
  list dp_term(bc_fi 1,mo_from_ei n);

symbolic procedure dp_fi n;
% dpoly from integer
   if n=0 then nil else
   list dp_term(bc_fi n,mo_zero());

symbolic procedure dp_fbc c;
% Converts the base coefficient c into a dpoly.
   if bc_zero!? c then nil else
   list dp_term(c,mo_zero());

% ------------  dpoly arithmetics ---------------------------

symbolic procedure dp!=comp(i,v);
   if null v then nil
   else if eqn(mo_comp dp_lmon v,i) then car v . dp!=comp(i,cdr v)
   else dp!=comp(i,cdr v);

symbolic procedure dp_comp(i,v);
% Returns the (polynomial) component i of the vector v.
   for each x in dp!=comp(i,v) collect (mo_deletecomp car x) . cdr x;

symbolic procedure dp!=mocompare (t1,t2);
% true <=> term t1 is smaller than term t2 in the current term order.
     eqn(mo_compare (car t1, car t2),1);

symbolic procedure dp_neworder p;
% Returns reordered dpoly p after change of the term order.
   sort(for each x in p collect (mo_neworder car x) . cdr x,
            function dp!=mocompare);

symbolic procedure dp_neg p;
% Returns - p  for the dpoly p.
   for each x in p collect (car x .  bc_neg cdr x);

symbolic procedure dp_times_mo (mo,p);
% Returns p * x^mo for the dpoly p and the monomial mo.
   for each x in p collect (mo_sum(mo,car x) .  cdr x);

symbolic procedure dp_times_bc (bc,p);
% Returns p * bc for the dpoly p and the base coeff. bc.
   for each x in p collect (car x .  bc_prod(bc,cdr x));

symbolic procedure dp_times_bcmo (bc,mo,p);
% Returns p * bc * x^mo for the dpoly p, the monomial mo and the base
% coeff. bc.
   for each x in p collect (mo_sum(mo,car x) .  bc_prod(bc,cdr x));

symbolic procedure dp_times_ei(i,p);
% Returns p * e_i for the dpoly p.
   dp_neworder for each x in p collect (mo_times_ei(i,car x) . cdr x);

symbolic procedure dp_project(p,k);
% Delete all terms x^a*e_i with i>k.
  for each x in p join if mo_comp car x <= k then {x};

symbolic procedure dp_content p;
% Returns the leading coefficient, if invertible, or the content of
% p.
  if null p then bc_fi 0
  else begin scalar w;
        w:=dp_lc p; p:=cdr p;
        while p and not bc_inv w do
         << w:=bc_gcd(w,dp_lc p); p:=cdr p >>;
        return w
        end;

symbolic procedure dp_mondelete(p,s);
% Returns (p.m) with common monomial factor m with support in the
% var. list s deleted.
   if null p or null s then (p . mo_zero()) else
   begin scalar cmf;
     cmf:=dp!=cmf(p,s);
     if mo_zero!? cmf then return (p . cmf)
     else return
       cons(for each x in p collect mo_diff(car x,cmf) . cdr x,cmf)
   end;

symbolic procedure dp!=cmf(p,s);
   begin scalar a;
        a:=mo_seed(dp_lmon p,s); p:=cdr p;
        while p and (not mo_zero!? a) do
                << a:=mo_gcd(a,mo_seed(dp_lmon p,s)); p:=cdr p >>;
        return a
   end;

symbolic procedure dp_unit!? p;
% Tests whether lt p of the dpoly p is a unit.
% This means : p is a unit, if the t.o. is noetherian
%       or   : p is a local unit, if the t.o. is a tangentcone order.
   p and (mo_zero!? dp_lmon p);

symbolic procedure dp_simp pol;
% Returns (pol_new . z) with
%       pol_new having leading coefficient 1 or
%       dp_content pol canceled out
% and pol_old = z * dpoly_new .

  if null pol then pol . bc_fi 1
  else begin scalar z,z1;
    if (z:=bc_inv (z1:=dp_lc pol)) then
        return dp_times_bc(z,pol) . z1;

    % -- now we assume that base coefficients are a gcd domain ----

    z:=dp_content pol;
    if bc_minus!? z1 then z:=bc_neg z;
    pol:=for each x in pol collect
                car x . car bc_divmod(cdr x,z);
    return pol . z;
    end;

symbolic procedure dp_prod(p1,p2);
% Returns p1 * p2 for the dpolys p1 and p2.
    if length p1 <= length p2 then dp!=prod(p1,p2)
                              else dp!=prod(p2,p1);

symbolic procedure dp!=prod(p1,p2);
  if null p1 or null p2 then nil
  else
     begin scalar v;
       for each x in p1 do
                v:=dp_sum( dp_times_bcmo(cdr x,car x, p2 ),v);
       return v;
     end;

symbolic procedure dp_sum(p1,p2);
% Returns p1 + p2 for the dpolys p1 and p2.
    if null p1 then p2
    else if null p2 then p1
    else begin scalar sl,al;
        sl := mo_compare(dp_lmon p1, dp_lmon p2);
        if sl = 1 then return car p1 . dp_sum(cdr p1, p2);
        if sl = -1 then return car p2 . dp_sum(p1, cdr p2);
        al := bc_sum(dp_lc p1, dp_lc p2);
        if bc_zero!? al then return dp_sum(cdr p1, cdr p2)
        else return dp_term(al,dp_lmon p1) . dp_sum(cdr p1, cdr p2)
        end;

symbolic procedure dp_diff(p1,p2);
% Returns p1 - p2 for the dpolys p1 and p2.
    dp_sum(p1, dp_neg p2);

symbolic procedure dp_power(p,n);
% Returns p^n for the dpoly p.
  if (not fixp n) or (n < 0) then typerr(n," exponent")
  else if n=0 then dp_fi 1
  else if n=1 then p
  else if null cdr p then dp!=power1(p,n)
  else dp!=power(p,n);

symbolic procedure dp!=power1(p,n); % For monomials.
  list dp_term(bc_power(dp_lc p,n),mo_power(dp_lmon p,n));

symbolic procedure dp!=power(p,n);
  if n=1 then p
  else if evenp n then dp!=power(dp_prod(p,p),n/2)
  else dp_prod(p,dp!=power(dp_prod(p,p),n/2));

symbolic procedure dp_tcpart p;
% Return the homogeneous degree part of p of highest degree.
  if null p then nil
  else begin scalar d,u; d:=car mo_deg caar p;
  while p and (d=car mo_deg caar p) do
     << u:=car p . u; p:=cdr p >>;
  return reversip u;
  end;

symbolic procedure dp_deletecomp p;
% delete the component part from all terms.
  dp_neworder for each x in p collect mo_deletecomp car x . cdr x;

symbolic procedure dp_factor p;
  for each y in cdr ((fctrf numr simp dp_2a p) where !*factor=t)
            collect dp_from_a prepf car y;

% ------ Converting prefix forms into dpolys ------------------

symbolic procedure dp_from_a u;
% Converts the algebraic (prefix) form u into a dpoly.
   if eqcar(u,'list) or eqcar(u,'mat) then typerr(u,"dpoly")
   else if atom u then dp!=a2dpatom u
   else if not atom car u or not idp car u
                then typerr(car u,"dpoly operator")
   else (if x='dp!=fnpow then dp!=fnpow(dp_from_a cadr u,caddr u)
      else if x then
            apply(x,list for each y in cdr u collect dp_from_a y)
      else dp!=a2dpatom u)
        where x = get(car u,'dp!=fn);

symbolic procedure dp!=a2dpatom u;
% Converts the atom (or kernel) u into a dpoly.
   if u=0 then nil
   else if numberp u or not member(u, ring_all_names cali!=basering)
                then list dp_term(bc_from_a u,mo_zero())
   else list dp_term(bc_fi 1,mo_from_a u);

symbolic procedure dp!=fnsum u;
% U is a list of dpoly expressions. The result is the dpoly
% representation for the sum. Analogously for the other symbolic
% procedures below.
   (<<for each y in cdr u do x := dp_sum(x,y); x>>) where x = car u;

put('plus,'dp!=fn,'dp!=fnsum);

put('plus2,'dp!=fn,'dp!=fnsum);

symbolic procedure dp!=fnprod u;
   (<<for each y in cdr u do x := dp_prod(x,y); x>>) where x = car u;

put('times,'dp!=fn,'dp!=fnprod);

put('times2,'dp!=fn,'dp!=fnprod);

symbolic procedure dp!=fndif u; dp_diff(car u, cadr u);

put('difference,'dp!=fn,'dp!=fndif);

symbolic procedure dp!=fnpow(u,n); dp_power(u,n);

put('expt,'dp!=fn,'dp!=fnpow);

symbolic procedure dp!=fnneg u;
   ( if null v then v else dp_term(bc_neg dp_lc v,dp_lmon v) . cdr v)
        where v = car u;

put('minus,'dp!=fn,'dp!=fnneg);

symbolic procedure dp!=fnquot u;
   if null cadr u or not null cdadr u
         or not mo_zero!? dp_lmon cadr u
      then typerr(dp_2a cadr u,"distributive polynomial denominator")
    else dp!=fnquot1(car u,dp_lc cadr u);

symbolic procedure dp!=fnquot1(u,v);
   if null u then u
    else dp_term(bc_quot(dp_lc u,v), dp_lmon u) .
            dp!=fnquot1(cdr u,v);

put('quotient,'dp!=fn,'dp!=fnquot);

% -------- Converting dpolys into prefix forms -------------
% ------ Authors: R. Gebauer, A. C. Hearn, H. Kredel -------

symbolic procedure dp_2a u;
% Returns the prefix equivalent of the dpoly u.
   if null u then 0 else dp!=replus dp!=2a u;

symbolic procedure dp!=2a u;
   if null u then nil
    else ((if bc_minus!? x then
                        list('minus,dp!=retimes(bc_2a bc_neg x . y))
           else dp!=retimes(bc_2a x . y))
          where x = dp_lc u, y = mo_2a dp_lmon u)
                 . dp!=2a cdr u;

symbolic procedure dp!=replus u;
   if atom u then u else if null cdr u then car u else 'plus . u;

symbolic procedure dp!=retimes u;
% U is a list of prefix expressions the first of which is a number.
% The result is the prefix representation for their product.
   if car u = 1 then if cdr u then dp!=retimes cdr u else 1
    else if null cdr u then car u
    else 'times . u;

% ----------- Printing routines for dpolys --------------
% ---- Authors: R. Gebauer, A. C. Hearn, H. Kredel ------

symbolic procedure dp_print u;
% Prints a distributive polynomial in infix form.
   << terpri();  dp_print1(u,nil); terpri(); terpri() >>;

symbolic procedure dp_print1(u,v);
% Prints a dpoly in infix form.
% U is a distributive form. V is a flag which is true if a term
% has preceded current form.
   if null u then if null v then print_lf 0 else nil
    else begin scalar bool,w;
       w := dp_lc u;
       if bc_minus!? w then <<bool := t; w := bc_neg w>>;
       if bool then print_lf " - " else if v then print_lf " + ";
       ( if not bc_one!? w or mo_zero!? x then
            << bc_prin w; mo_prin(x,t)>>
         else mo_prin(x,nil))
           where x = dp_lmon u;
       dp_print1(cdr u,t)
     end;

symbolic procedure dp_print2 u;
% Prints a dpoly with restricted number of terms.
  (if c and (length u>c) then
        begin scalar i,v,x;
        v:=for i:=1:c collect <<x:=car u; u:=cdr u; x>>;
        dp_print1(v,nil); write" + # ",length u," terms #"; terpri();
        end
  else << dp_print1(u,nil); terpri() >>)
        where c:=get('cali,'printterms);

% -------------- Auxiliary dpoly operations -------------------

symbolic procedure dp_ecart p;
% Returns the ecart of the dpoly p.
   if null p then 0 else (dp!=ecart p) - (mo_ecart dp_lmon p);

symbolic procedure dp!=ecart p;
   if null p then 0
   else max2(mo_ecart dp_lmon p,dp!=ecart cdr p);

symbolic procedure dp_homogenize(p,x);
% Homogenize (according to mo_ecart) the dpoly p using the variable x.
  if null p then p
  else begin integer maxdeg;
    maxdeg:=0;
    for each y in p do maxdeg:=max2(maxdeg,mo_ecart car y);
    return dp!=compact dp_neworder for each y in p collect
                mo_inc(car y,x,maxdeg-mo_ecart car y) . cdr y;
    end;

symbolic procedure dp_seed(p,s);
% Returns the dpoly p with all vars outside the list s set equal to 1.
  if null p then p
  else dp!=compact dp_neworder
                for each x in p collect mo_seed(car x,s).cdr x;

symbolic procedure dp!=compact p;
% Collect equal terms in the sorted dpoly p.
  if null p then p else dp_sum(list car p,dp!=compact cdr p);

symbolic procedure dp_xlt(p,x);
% x is the main variable. Returns the leading term of p wrt. x or p,
% if p is free of x.
  if null p then p
  else begin scalar d,m;
    d:=mo_varexp(x,dp_lmon p);
    if d=0 then return p;
    return for each m in p join
        if mo_varexp(x,car m)=d then {mo_inc(car m,x,-d) . cdr m};
    end;

% -- dpoly operations based on computation with ideal bases.

symbolic procedure dp_pseudodivmod(g,f);
% Returns a dpoly list {q,r,z} such that z * g = q * f + r and
% z is a dpoly unit. Computes redpol({[f.e_1]},[g.0]).
% g, f and r must belong to the same free module.
   begin scalar u;
   f:=list bas_make1(1,f,dp_from_ei 1);
   g:=bas_make(0,g);
   u:=red_redpol(f,g);
   return {dp_neg dp_deletecomp bas_rep car u,bas_dpoly car u,cdr u};
   end;

symbolic operator dpgcd;
symbolic procedure dpgcd(u,v);
  if !*mode='algebraic then dp_2a dpgcd!*(dp_from_a u,dp_from_a v)
  else dpgcd!*(u,v);

symbolic procedure dpgcd!*(u,v);
% Compute the gcd of two polynomials by the syzygy method :
% 0 = u*u1 + v*v1 => gcd = u/v1 = -v/u1 .
  if dp_unit!? u or dp_unit!? v then dp_fi 1
  else begin scalar w;
    w:=bas_dpoly first dpmat_list
       syzygies!* dpmat_make(2,0,{bas_make(1,u),bas_make(2,v)},nil,nil);
    return car dp_pseudodivmod(u,dp_comp(2,w));
    end;

endmodule; % dpoly

end;
