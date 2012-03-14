module scripts;

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

               ######################
               ##                  ##
               ##     ADVANCED     ##
               ##   APPLICATIONS   ##
               ##                  ##
               ######################

This module contains several additional advanced applications of
standard basis computations, inspired partly by the scripts
distributed with the commutative algebra package MACAULAY
(Bayer/Stillman/Eisenbud).

The following topics are currently covered :
        - [BGK]'s heuristic variable optimization
        - certain stuff on maps (preimage, ratpreimage)
        - ideals of points (in affine and proj. spaces)
        - ideals of (affine and proj.) monomial curves
        - General Rees rings, associated graded rings, and related
                topics (analytic spread, symmetric algebra)
        - several short scripts (minimal generators, symbolic powers
                of primes, singular locus)


END COMMENT;

%---------- [BGK]'s heuristic variable optimization ----------

symbolic operator varopt;
symbolic procedure varopt m;
  if !*mode='algebraic then makelist varopt!* dpmat_from_a m
  else varopt!* m;

symbolic procedure varopt!* m;
% Find a heuristically optimal variable order.
  begin scalar c; c:=mo_zero();
  for each x in dpmat_list m do
    for each y in bas_dpoly x do c:=mo_lcm(c,car y);
  return
    for each x in
        sort(mo_2list c,function(lambda(x,y); cdr x>cdr y)) collect
        car x;
  end;

% ----- Certain stuff on maps -------------

% A ring map is represented as a list
%   {preimage_ring, image_ring, subst_list},
% where subst_list is a substitution list {v1=ex1,v2=ex2,...} in
% algebraic prefix form, i.e. looks like (list (equal var image) ...)

symbolic operator preimage;
symbolic procedure preimage(m,map);
% Compute the preimage of an ideal m under a (polynomial) ring map.
  if !*mode='algebraic then
  begin map:=cdr reval map;
     return preimage!*(reval m,
        {ring_from_a first map, ring_from_a second map, third map});
  end
  else preimage!*(m,map);

symbolic procedure preimage!*(m,map);
% m and the result are given and returned in algebraic prefix form.
  if not !*noetherian then
        rederr"PREIMAGE only for noetherian term orders"
  else begin scalar u,oldring,newring,oldnames;
  if not eqcar(m,'list) then rederr"PREIMAGE only for ideals";
  oldring:=first map; newring:=second map;
  oldnames:=ring_names oldring;
  setring!* ring_sum(newring,oldring);
  u:=bas_renumber for each x in cdr third map collect
  << if not member(second x,oldnames) then
            typerr(second x,"var. name");
     bas_make(0,dp_diff(dp_from_a second x,dp_from_a third x))
  >>;
  m:=matsum!* {dpmat_from_a m,dpmat_make(length u,0,u,nil,nil)};
  m:=dpmat_2a eliminate!*(m,ring_names newring);
  setring!* oldring;
  return m;
  end;

symbolic operator ratpreimage;
symbolic procedure ratpreimage(m,map);
% Compute the preimage of an ideal m under a rational ring map.
  if !*mode='algebraic then
  begin map:=cdr reval map;
  return ratpreimage!*(reval m,
        {ring_from_a first map, ring_from_a second map, third map});
  end
  else ratpreimage!*(m,map);

symbolic procedure ratpreimage!*(m,map);
% m and the result are given and returned in algebraic prefix form.
  if not !*noetherian then
        rederr"RATPREIMAGE only for noetherian term orders"
  else begin scalar u,oldring,newnames,oldnames,f,g,v,g0;
  if not eqcar(m,'list) then rederr"RATPREIMAGE only for ideals";
  oldring:=first map; v:=make_cali_varname();
  newnames:=v . ring_names second map;
  oldnames:=ring_names oldring; u:=append(oldnames,newnames);
  setring!* ring_define(u,nil,'lex,for each x in u collect 1);
  g0:=dp_fi 1;
  u:=bas_renumber for each x in cdr third map collect
  << if not member(second x,oldnames) then
            typerr(second x,"var. name");
     f:=simp third x; g:=dp_from_a prepf denr f;
     f:=dp_from_a prepf numr f; g0:=dp_prod(g,g0);
     bas_make(0,dp_diff(dp_prod(g,dp_from_a second x),f))
  >>;
  u:=bas_make(0,dp_diff(dp_prod(g0,dp_from_a v),dp_fi 1)) . u;
  m:=matsum!* {dpmat_from_a m,dpmat_make(length u,0,u,nil,nil)};
  m:=dpmat_2a eliminate!*(m,newnames);
  setring!* oldring;
  return m;
  end;

% ---- The ideals of affine resp. proj. points. The old stuff, but the
% ---- algebraic interface now uses the linear algebra approach.

symbolic procedure affine_points1!* m;
  begin scalar names;
  if length(names:=ring_names cali!=basering) neq length cadr m then
        typerr(m,"coordinate matrix");
  m:=for each x in cdr m collect
         'list . for each y in pair(names,x) collect
                {'plus,car y,{'minus,reval cdr y}};
  m:=for each x in m collect dpmat_from_a x;
  m:=matintersect!* m;
  return m;
  end;

symbolic procedure scripts!=ideal u;
  'list . for each x in cali_choose(u,2) collect
        {'plus,{'times, car first x,cdr second x},
        {'minus,{'times, car second x,cdr first x}}};

symbolic procedure proj_points1!* m;
  begin scalar names;
  if length(names:=ring_names cali!=basering) neq length cadr m then
        typerr(m,"coordinate matrix");
  m:=for each x in cdr m collect scripts!=ideal pair(names,x);
  m:=for each x in m collect interreduce!* dpmat_from_a x;
  m:=matintersect!* m;
  return m;
  end;

% ----- Affine and proj. monomial curves ------------

symbolic operator affine_monomial_curve;
symbolic procedure affine_monomial_curve(l,R);
% l is a list of integers, R contains length l ring var. names.
% Returns the generators of the monomial curve (t^i : i\in l) in R.
  if !*mode='algebraic then
        dpmat_2a affine_monomial_curve!*(cdr reval l,cdr reval R)
  else affine_monomial_curve!*(l,R);

symbolic procedure affine_monomial_curve!*(l,R);
  if not numberlistp l then typerr(l,"number list")
  else if length l neq length R then
        rederr"number of variables doesn't match"
  else begin scalar u,t0,v;
    v:=list make_cali_varname();
    r:=ring_define(r,{l},'revlex,l);
    setring!* ring_sum(r,ring_define(v,degreeorder!* v,'lex,'(1)));
    t0:=dp_from_a car v;
    u:=bas_renumber for each x in pair(l,ring_names r) collect
        bas_make(0,dp_diff(dp_from_a cdr x,dp_power(t0,car x)));
    u:=dpmat_make(length u,0,u,nil,nil);
    u:=(eliminate!*(u,v) where cali!=monset=ring_names cali!=basering);
    setring!* r;
    return dpmat_neworder(u,dpmat_gbtag u);
    end;

symbolic operator proj_monomial_curve;
symbolic procedure proj_monomial_curve(l,R);
% l is a list of integers, R contains length l ring var. names.
% Returns the generators of the monomial curve
% (s^(d-i)*t^i : i\in l) in R where d = max { x : x \in l}
  if !*mode='algebraic then
        dpmat_2a proj_monomial_curve!*(cdr reval l,cdr reval R)
  else proj_monomial_curve!*(l,R);

symbolic procedure proj_monomial_curve!*(l,R);
  if not numberlistp l then typerr(l,"number list")
  else if length l neq length R then
        rederr"number of variables doesn't match"
  else begin scalar u,t0,t1,v,d;
    t0:=make_cali_varname(); t1:=make_cali_varname(); v:={t0,t1};
    d:=listexpand(function max2,l);
    r:=ring_define(r,degreeorder!* r,'revlex,for each x in r collect 1);
    setring!* ring_sum(r,ring_define(v,degreeorder!* v,'lex,'(1 1)));
    t0:=dp_from_a t0; t1:=dp_from_a t1;
    u:=bas_renumber for each x in pair(l,ring_names r) collect
        bas_make(0,dp_diff(dp_from_a cdr x,
                dp_prod(dp_power(t0,car x),dp_power(t1,d-car x))));
    u:=dpmat_make(length u,0,u,nil,nil);
    u:=(eliminate!*(u,v) where cali!=monset=ring_names cali!=basering);
    setring!* r;
    return dpmat_neworder(u,dpmat_gbtag u);
    end;

% -- General Rees rings, associated graded rings, and related topics --

symbolic operator blowup;
symbolic procedure blowup(m,n,vars);
% vars is a list of var. names for the ring R
%       of the same length as dpmat_list n.
% Returns an ideal J such that (S+R)/J == S/M [ N.t ]
%       ( with S = the current ring )
% is the blow up ring of the ideal N over S/M.
% (S+R) is the new current ring.
  if !*mode='algebraic then
        dpmat_2a blowup!*(dpmat_from_a reval m,dpmat_from_a reval n,
                cdr reval vars)
  else blowup!*(M,N,vars);

symbolic procedure blowup!*(M,N,vars);
  if (dpmat_cols m > 0)or(dpmat_cols n > 0) then
        rederr"BLOWUP defined only for ideals"
  else if not !*noetherian then
        rederr"BLOWUP only for noetherian term orders"
  else begin scalar u,s,t0,v,r1;
    if length vars neq dpmat_rows n then
        rederr {"ring must have",dpmat_rows n,"variables"};
    u:=for each x in dpmat_rowdegrees n collect mo_ecart cdr x;
    r1:=ring_define(vars,list u,'revlex,u);
    s:=ring_sum(cali!=basering,r1); v:=list(make_cali_varname());
    setring!* ring_sum(s,ring_define(v,degreeorder!* v,'lex,'(1)));
    t0:=dp_from_a car v;
    n:=for each x in
            pair(vars,for each y in dpmat_list n collect bas_dpoly y)
            collect dp_diff(dp_from_a car x,
                            dp_prod(dp_neworder cdr x,t0));
    m:=bas_renumber append(bas_neworder dpmat_list m,
            for each x in n collect bas_make(0,x));
    m:=(eliminate!*(interreduce!* dpmat_make(length m,0,m,nil,nil),v)
        where cali!=monset=nil);
    setring!* s;
    return dpmat_neworder(m,dpmat_gbtag m);
    end;

symbolic operator assgrad;
symbolic procedure assgrad(m,n,vars);
% vars is a list of var. names for the ring T
%       of the same length as dpmat_list n.
% Returns an ideal J such that (S+T)/J == (R/N + N/N^2 + ... )
%       ( with R=S/M and S the current ring )
% is the associated graded ring of the ideal N over R.
% (S+T) is the new current ring.
  if !*mode='algebraic then
        dpmat_2a assgrad!*(dpmat_from_a reval m,dpmat_from_a reval n,
                cdr reval vars)
  else assgrad!*(M,N,vars);

symbolic procedure assgrad!*(M,N,vars);
  if (dpmat_cols m > 0)or(dpmat_cols n > 0) then
        rederr"ASSGRAD defined only for ideals"
  else begin scalar u;
    u:=blowup!*(m,n,vars);
    return matsum!* {u,dpmat_neworder(n,nil)};
    end;

symbolic operator analytic_spread;
symbolic procedure analytic_spread m;
% Returns the analytic spread of the ideal m.
  if !*mode='algebraic then analytic_spread!* dpmat_from_a reval m
  else analytic_spread!* m;

symbolic procedure analytic_spread!* m;
   if (dpmat_cols m>0) then rederr"ANALYTIC SPREAD only for ideals"
   else (begin scalar r,m1,vars;
   r:=ring_names cali!=basering;
   vars:=for each x in dpmat_list m collect make_cali_varname();
   m1:=blowup!*(dpmat_from_dpoly nil,m,vars);
   return dim!* gbasis!* matsum!*{m1,dpmat_from_a('list . r)};
   end) where cali!=basering=cali!=basering;

symbolic operator sym;
symbolic procedure sym(M,vars);
% vars is a list of var. names for the ring R
%       of the same length as dpmat_list M.
% Returns an ideal J such that (S+R)/J == Sym(M)
%       ( with S = the current ring )
% is the symmetric algebra of M over S.
% (S+R) is the new current ring.
  if !*mode='algebraic then
        dpmat_2a sym!*(dpmat_from_a M,cdr reval vars)
  else sym!*(m,vars);

symbolic procedure sym!*(m,vars);
% The symmetric algebra of the dpmat m.
   if not !*noetherian then
        rederr"SYM only for noetherian term orders"
   else begin scalar n,u,r1;
    if length vars neq dpmat_rows m then
        rederr {"ring must have",dpmat_rows m,"variables"};
    cali!=degrees:=dpmat_coldegs m;
    u:=for each x in dpmat_rowdegrees m collect mo_ecart cdr x;
    r1:=ring_define(vars,list u,'revlex,u); n:=syzygies!* m;
    setring!* ring_sum(cali!=basering,r1);
    return mat2list!* interreduce!*
                dpmat_mult(dpmat_neworder(n,nil),
                        ideal2mat!* dpmat_from_a('list . vars));
    end;

% ----- Several short scripts ----------

% ------ Minimal generators of an ideal or module.
symbolic operator minimal_generators;
symbolic procedure minimal_generators m;
  if !*mode='algebraic then
        dpmat_2a minimal_generators!* dpmat_from_a reval m
  else minimal_generators!* m;

symbolic procedure minimal_generators!* m;
  car groeb_minimize(m,syzygies!* m);

% ------- Symbolic powers of prime (or unmixed) ideals
symbolic operator symbolic_power;
symbolic procedure symbolic_power(m,d);
  if !*mode='algebraic then
        dpmat_2a symbolic_power!*(dpmat_from_a m,reval d)
  else symbolic_power!*(m,d);

symbolic procedure symbolic_power!*(m,d);
  eqhull!* idealpower!*(m,d);

% ---- non zero divisor property -----------

put('nzdp,'psopfn,'scripts!=nzdp);
symbolic procedure scripts!=nzdp m;
  if length m neq 2 then rederr"Syntax : nzdp(dpoly,dpmat)"
  else begin scalar f,b;
    f:=reval car m; intf_get second m;
    if null(b:=get(second m,'gbasis)) then
        put(second m,'gbasis,b:=gbasis!* get(second m,'basis));
    return if nzdp!*(dp_from_a f,b) then 'yes else 'no;
    end;

symbolic procedure nzdp!*(f,m);
% Test dpoly f for a non zero divisor on coker m. m must be a gbasis.
  submodulep!*(matqquot!*(m,f),m);

endmodule; % scripts

end;
