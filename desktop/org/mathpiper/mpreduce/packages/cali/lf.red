module lf;

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
              ###############################
              ####                     ####
              ####  DUAL BASES APPROACH  ####
              ####                     ####
              ###############################

The general idea for the dual bases approach :

Given a finite collection of linear functionals L : M=S^n --> k^N, we
want to compute a basis for Ker L as in

[MMM] : Marinari et al., Proc. ISSAC'91, p. 55-63

This generalizes the approach from

[FGLM] : Faugere, Gianni, Lazard, Mora: JSC 16 (1993), 329 - 344.

L is given through values on the generators,
        {[e_i,L(e_i)], i=1 ... n},
and an evaluation function evlf([p,L(p)],x), that evaluates L(p*x)
from L(p) for p in M and the variable x .

We process a queue of elements of M with increasing leading terms,
evaluating each time L on them. Different to [MMM] the queue is stored
as

   {[p,L(p)], l=list of potential multipliers, lt(p*(x:=first l))}

for the potential evaluation of L(p*x) and sorted by the term order
wrt. the third slot. Since we proceed by increasing lt, Gaussian
elimination doesn't disturb leading terms. Hence leading terms of the
result are linearly independent and thus the result a Groebner basis.

This approach applies to very different problem settings, see
[MMM]. CALI manages this variety of applications through different
values on the property list of 'cali.

There are general entries with information about the computation
        'varlessp -- a sort predicate for lf variable names
        'evlf     -- the evaluation function
and special entries, depending on the problem to be solved.

[p,L(p)] is handled as data type lf (linear functions)
        < dpoly > . < list of (var. name).(base coeff.) >
The lf cdr list is the list of the values of the linear functionals
on the given car lf dpoly.

evlf(lf,var) evaluates lf*var and returns a new lf.

There are the following order functions :
        varlessp        = (cdr lf) variable order
        lf!=sort        = lf queue order
        term order      = (car lf) dpoly order

end comment;

symbolic procedure lf_dualbasis(q);
% q is the dual generator set given as a list of input lf values.
% l is the queue to be processed and updated, g the list of kernel
%       elements, produced so far.
  begin scalar g,l,q,r,p,v,u,vars,rf,q1;
  v:=ring_names cali!=basering;
  if null(rf:=get('cali,'evlf)) then
        rederr"For DUALBASIS no evaluation function defined";
  for each ev1 in q do
     if lf!=zero ev1 then
     << if cali_trace()>20 then dp_print2 car q; g:=car q . g >>
     else
     << vars:=v; q1:=ev1.q1;
        while vars do
        << l:={ev1, vars, mo_from_a car vars}.l; vars:=cdr vars >>;
     >>;
  q:=sort(q1,function lf!=less); % The reducer in triangular order.
  l:=sort(l, function lf!=sort); % The queue in increasing term order.
  while l do
  << r:=car l; l:=cdr l;
     vars:=second r; r:=car r;
     p:=lf!=reduce(apply2(rf,r,car vars),q);
     if lf!=zero p then
     << if cali_trace()>20 then dp_print2 car p; g:=car p . g >>
     else
     << q:=merge({p},q,function lf!=less);
        u:=nil; v:=dp_lmon car p;
        while vars do
        << u:={p,vars,mo_sum(v,mo_from_a car vars)}.u;
           vars:=cdr vars
        >>;
        l:=merge(sort(u,function lf!=sort),l,function lf!=sort);
     >>;
  >>;
  g:=bas_renumber bas_zerodelete for each x in g collect bas_make(0,x);
  return interreduce!* groeb_mingb dpmat_make(length g,0,g,nil,t);
  end;

symbolic procedure lf!=sort(a,b);
% Term order on the third slot. Niermann proposes another order here.
  mo_compare(third a,third b)=-1;

symbolic procedure lf_dualhbasis(q,s);
% The homogenized version.
% s is the length of the dual homogenized basis.
% For modules with column degrees not yet correct.
  begin scalar a,d,g,l,l1,r,p,v,u,vars,rf,q1;
  v:=ring_names cali!=basering; d:=0;
  if null(rf:=get('cali,'evlf)) then
        rederr"For DUALHBASIS no evaluation function defined";
  for each ev1 in q do
     if lf!=zero ev1 then
     << if cali_trace()>20 then dp_print2 car q; g:=car q . g >>
     else
     << vars:=v; q1:=ev1.q1;
        while vars do
        << l:={ev1, vars, mo_from_a car vars}.l; vars:=cdr vars >>;
     >>;
  q:=sort(q1,function lf!=less); % The reducer in triangular order.
  l1:=sort(l,function lf!=sort); % The queue in increasing term order.
  repeat
  << % Initialize the computation of the next degree.
     l:=l1; q:=l1:=nil; d:=d+1;
     while l do
     << r:=car l; l:=cdr l;
        vars:=second r; r:=car r;
        p:=lf!=reduce(apply2(rf,r,car vars),q);
        if lf!=zero p then
        << if cali_trace()>20 then dp_print2 car p;
           g:=bas_make(0,car p) . g
        >>
        else
        << q:=merge({p},q,function lf!=less);
           u:=nil; v:=dp_lmon car p;
           while vars do
           << u:={p,vars,mo_sum(v,mo_from_a car vars)}.u;
              vars:=cdr vars
           >>;
           l1:=merge(sort(u,function lf!=sort),l1,function lf!=sort);
        >>;
        g:=bas_renumber bas_zerodelete g;
        a:=dpmat_make(length g,0,g,nil,t);
     >>;
  >>
  until (d>=s) or ((dim!* a = 1) and (length q = s));
  return interreduce!* groeb_mingb a;
  end;

symbolic procedure lf!=compact u;
% Sort the cdr of the lf u and remove zeroes.
  sort(for each x in u join if not bc_zero!? cdr x then {x},
        function (lambda(x,y);
                     apply2(get('cali,'varlessp),car x,car y)));

symbolic procedure lf!=zero l; null cdr l;

symbolic procedure lf!=sum(a,b);
  dp_sum(car a,car b) . lf!=sum1(cdr a,cdr b);

symbolic procedure lf!=times_bc(z,a);
  dp_times_bc(z,car a) . lf!=times_bc1(z,cdr a);

symbolic procedure lf!=times_bc1(z,a);
  if bc_zero!? z then nil
  else for each x in a collect car x . bc_prod(z,cdr x);

symbolic procedure lf!=sum1(a,b);
  if null a then b
  else if null b then a
  else if equal(caar a,caar b) then
        (if bc_zero!? u then lf!=sum1(cdr a,cdr b)
        else (caar a . u).lf!=sum1(cdr a,cdr b))
        where u:=bc_sum(cdar a,cdar b)
  else if apply2(get('cali,'varlessp),caar a,caar b) then
        (car a).lf!=sum1(cdr a,b)
  else (car b).lf!=sum1(a,cdr b);

symbolic procedure lf!=simp a;
  if null cdr a then car dp_simp car a. nil
  else begin scalar z;
    if (z:=bc_inv lf!=lc a) then return lf!=times_bc(z,a);
    z:=dp_content car a;
    for each x in cdr a do z:=bc_gcd(z,cdr x);
    return (for each x in car a collect car x . bc_quot(cdr x,z)) .
        (for each x in cdr a collect car x . bc_quot(cdr x,z));
    end;

% Leading variable and coefficient assuming cdr a nonempty :

symbolic procedure lf!=lvar a; caadr a;
symbolic procedure lf!=lc a; cdadr a;

symbolic procedure lf!=less(a,b);
        apply2(get('cali,'varlessp),lf!=lvar a,lf!=lvar b);

symbolic procedure lf!=reduce(a,l);
  if lf!=zero a or null l or lf!=less(a, car l) then a
  else if (lf!=lvar a = lf!=lvar car l) then
    begin scalar z,z1,z2,b;
    b:=car l; z1:=bc_neg lf!=lc a; z2:=lf!=lc b;
    if !*bcsimp then
      << if (z:=bc_inv z1) then <<z1:=bc_fi 1; z2:=bc_prod(z2,z)>>
         else
           << z:=bc_gcd(z1,z2);
              z1:=bc_quot(z1,z);
              z2:=bc_quot(z2,z);
           >>;
      >>;
    a:=lf!=sum(lf!=times_bc(z2,a),lf!=times_bc(z1,b));
    if !*bcsimp then a:=lf!=simp a;
    return lf!=reduce(a,cdr l)
    end
  else lf!=reduce(a,cdr l);

% ------------ Application to point evaluation -------------------

% cali has additionally 'varnames and 'sublist.
% It works also for symbolic matrix entries.

symbolic operator affine_points;
symbolic procedure affine_points m;
% m is an algebraic matrix, which rows are the coordinates of points
% in the affine space with Spec = the current ring.
  if !*mode='algebraic then dpmat_2a affine_points!* reval m
  else affine_points!* m;

symbolic procedure affine_points!* m;
  begin scalar names;
  if length(names:=ring_names cali!=basering) neq length cadr m then
        typerr(m,"coordinate matrix");
  put('cali,'sublist,for each x in cdr m collect pair(names,x));
  put('cali,'varnames, names:=for each x in cdr m collect make_cali_varname());
  put('cali,'varlessp,'lf!=pointvarlessp);
  put('cali,'evlf,'lf!=pointevlf);
  return lf_dualbasis(
        { dp_fi 1 . lf!=compact
                for each x in names collect (x . bc_fi 1) });
  end;

symbolic operator proj_points;
symbolic procedure proj_points m;
% m is an algebraic matrix, which rows are the coordinates of _points
% in the projective space with Proj = the current ring.
  if !*mode='algebraic then dpmat_2a proj_points!* reval m
  else proj_points!* m;

symbolic procedure proj_points!* m;
% Points must be different in proj. space. This will not be tested !
  begin scalar u,names;
  if length(names:=ring_names cali!=basering) neq length cadr m then
        typerr(m,"coordinate matrix");
  put('cali,'sublist,u:=for each x in cdr m collect pair(names,x));
  put('cali,'varnames, names:=for each x in cdr m collect make_cali_varname());
  put('cali,'varlessp,'lf!=pointvarlessp);
  put('cali,'evlf,'lf!=pointevlf);
  return lf_dualhbasis(
        { dp_fi 1 . lf!=compact
                for each x in names collect (x . bc_fi 1) },
        length u);
  end;

symbolic procedure lf!=pointevlf(p,x);
   begin scalar q; p:=dp_2a (q:=dp_prod(car p,dp_from_a x));
   return q . lf!=compact
        pair(get('cali,'varnames),
        for each x in get('cali,'sublist) collect
                bc_from_a subeval1(x,p));
   end;

symbolic procedure lf!=pointvarlessp(x,y); not ordp(x,y);

% ------ Application to Groebner bases under term order change ----

% ----- The version with borderbases :

% cali has additionally 'oldborderbasis.

put('change_termorder,'psopfn,'lf!=change_termorder);
symbolic procedure lf!=change_termorder m;
  begin scalar c,r;
  if (length m neq 2) then
        rederr "Syntax : Change_TermOrder(dpmat identifier, new ring)";
  if (not idp car m) then typerr(m,"dpmat identifier");
  r:=ring_from_a reval second m;
  m:=car m; intf_get m;
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  c:=change_termorder!*(c,r);
  return dpmat_2a c;
  end;

symbolic procedure change_termorder!*(m,r);
% m must be a zerodimensional gbasis with respect to the current term
% order, r the new ring (with the same var. names).
% This procedure sets r as the current ring and computes a gbasis
% of m with respect to r.
  if (dpmat_cols m neq 0) or not dimzerop!* m then
        rederr("CHANGE_TERMORDER only for zerodimensional ideals")
  else if ring_names r neq ring_names cali!=basering then
        typerr(makelist ring_names r,"variable names")
  else begin scalar b;
  if cali_trace()>20 then print"Precomputing the border basis";
  b:=for each x in odim_borderbasis m collect bas_dpoly x;
  if cali_trace()>20 then print"Borderbasis computed";
  setring!* r;
  put('cali,'oldborderbasis, for each x in b collect
        {mo_neworder dp_lmon x, dp_lc x,dp_neg dp_neworder cdr x});
  put('cali,'varlessp,'lf!=tovarlessp);
  put('cali,'evlf,'lf!=toevlf);
  return lf_dualbasis({dp_fi 1 . dp_fi 1})
  end;

symbolic procedure lf!=tovarlessp(a,b); mo_compare(a,b)=1;

symbolic procedure lf!=toevlf(p,x);
  begin scalar a,b,c,d;
  x:=mo_from_a x; c:=get('cali,'oldborderbasis);
  p:=dp_times_mo(x,car p).dp_times_mo(x,cdr p);
        % Now reduce the terms in cdr p with the old borderbasis.
  for each x in cdr p do
      % b is the list of terms already in canonical form,
      % a is a list of (can. form) . (bc_quot), where bc_quot is
      %         a pair of bc's interpreted as a rational multiplier
      %         for the can. form.
      if d:=assoc(car x,c) then a:=(third d . (cdr x . second d)) .a
      else b:=x.b;
  a:=for each x in a collect car x . lf!=reducebc cdr x;
  d:=lf!=denom a;
  a:=for each x in a collect
                dp_times_bc(bc_quot(bc_prod(d,cadr x),cddr x),car x);
  b:=dp_times_bc(d,reversip b);
  for each x in a do b:=dp_sum(x,b);
  return dp_times_bc(d,car p) . b;
  end;

symbolic procedure lf!=reducebc z;
  begin scalar g;
  if g:=bc_inv cdr z then return bc_prod(g,car z) . bc_fi 1;
  g:=bc_gcd(car z,cdr z);
  return bc_quot(car z,g) . bc_quot(cdr z,g);
  end;

symbolic procedure lf!=denom a;
  if null a then bc_fi 1
  else if null cdr a then cddar a
  else bc_lcm(cddar a,lf!=denom cdr a);

% ----- The version without borderbases :

% cali has additionally 'oldring, 'oldbasis

put('change_termorder1,'psopfn,'lf!=change_termorder1);
symbolic procedure lf!=change_termorder1 m;
  begin scalar c,r;
  if (length m neq 2) then
        rederr "Syntax : Change_TermOrder1(dpmat identifier, new ring)";
  if (not idp car m) then typerr(m,"dpmat identifier");
  r:=ring_from_a reval second m;
  m:=car m; intf_get m;
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  c:=change_termorder1!*(c,r);
  return dpmat_2a c;
  end;

symbolic procedure change_termorder1!*(m,r);
% m must be a zerodimensional gbasis with respect to the current term
% order, r the new ring (with the same var. names).
% This procedure sets r as the current ring and computes a gbasis
% of m with respect to r.
  if (dpmat_cols m neq 0) or not dimzerop!* m then
        rederr("change_termorder1 only for zerodimensional ideals")
  else if ring_names r neq ring_names cali!=basering then
        typerr(makelist ring_names r,"variable names")
  else begin scalar c,d;
  c:=if dpmat_cols m=0 then {dp_fi 1}
        else for k:=1:dpmat_cols m collect dp_from_ei k;
  put('cali,'varlessp,'lf!=tovarlessp1);
  put('cali,'evlf,'lf!=toevlf1);
  put('cali,'oldring,cali!=basering);
  put('cali,'oldbasis,m);
  setring!* r;
  d:=if dpmat_cols m=0 then {dp_fi 1}
        else for k:=1:dpmat_cols m collect dp_from_ei k;
  return lf_dualbasis(pair(d,c))
  end;

symbolic procedure lf!=tovarlessp1(a,b);
  (mo_compare(a,b)=1)
  where cali!=basering=get('cali,'oldring);

symbolic procedure lf!=toevlf1(p,x);
% p = ( a . b ). Returns (c*a*x,d) where (d.c)=mod!*(b*x,m).
  begin scalar a,b,c,d;
  a:=dp_times_mo(mo_from_a x,car p);
  (<< b:=dp_times_mo(mo_from_a x,cdr p);
      b:=mod!*(b,get('cali,'oldbasis));
      d:=car b; c:=dp_lc cdr b;
   >>) where cali!=basering:=get('cali,'oldring);
  return dp_times_bc(c,a) . d;
  end;

endmodule; % lf

end;
