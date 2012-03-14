module quot;

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

                #################
                #               #
                #   QUOTIENTS   #
                #               #
                #################


This module contains algorithms for different kinds of quotients of
ideals and modules.

END COMMENT;

% -------- Quotient of a module by a polynomial -----------
% Returns m : (f) for a polynomial f.

symbolic operator matquot;
symbolic procedure matquot(m,f);
  if !*mode='algebraic then
     if eqcar(f,'list) or eqcar(f,'mat) then
                rederr("Syntax : matquot(dpmat,dpoly)")
     else dpmat_2a matquot!*(dpmat_from_a reval m,dp_from_a reval f)
  else matquot!*(m,f);

symbolic procedure matquot!*(m,f);
  if dp_unit!? f then m
  else if dpmat_cols m=0 then mat2list!* quot!=quot(ideal2mat!* m,f)
  else quot!=quot(m,f);

symbolic procedure quot!=quot(m,f);
% Note that, if a is a gbasis, then also b.
  begin scalar a,b;
  a:=matintersect!* {m,
    dpmat_times_dpoly(f,dpmat_unit(dpmat_cols m,dpmat_coldegs m))};
  b:=for each x in dpmat_list a collect
    bas_make(bas_nr x,car dp_pseudodivmod(bas_dpoly x,f));
  return dpmat_make(dpmat_rows a,dpmat_cols a,b,
                dpmat_coldegs m,dpmat_gbtag a);
  end;

% -------- Quotient of a module by an ideal -----------
% Returns m:n as a module.

symbolic operator idealquotient;
symbolic procedure idealquotient(m,n);
  if !*mode='algebraic then
        dpmat_2a idealquotient2!*(dpmat_from_a reval m,
                            dpmat_from_a reval n)
  else idealquotient2!*(m,n);

% -------- Quotient of a module by another module  -----------
% Returns m:n as an ideal in S. m and n must be submodules of a common
% free module.

symbolic operator modulequotient;
symbolic procedure modulequotient(m,n);
  if !*mode='algebraic then
        dpmat_2a modulequotient2!*(dpmat_from_a reval m,
                            dpmat_from_a reval n)
  else modulequotient2!*(m,n);

% ---- The annihilator of a module, i.e. Ann coker M := M : F ---

symbolic operator annihilator;
symbolic procedure annihilator m;
  if !*mode='algebraic then
        dpmat_2a annihilator2!* dpmat_from_a reval m
  else annihilator2!* m;

% ---- Quotients as M:N = \intersect { M:f | f \in N } ------

symbolic procedure idealquotient2!*(m,n);
  if dpmat_cols n>0 then rederr"Syntax : idealquotient(dpmat,ideal)"
  else if dpmat_cols m=0 then modulequotient2!*(m,n)
  else if dpmat_cols m=1 then
        ideal2mat!* modulequotient2!*(m,ideal2mat!* n)
  else matintersect!* for each x in dpmat_list n collect
        quot!=quot(m,bas_dpoly x);

symbolic procedure modulequotient2!*(m,n);
  (begin scalar c;
  if not((c:=dpmat_cols m)=dpmat_cols n) then rederr
    "MODULEQUOTIENT only for submodules of a common free module";
  if not equal(dpmat_coldegs m,dpmat_coldegs n) then
          rederr"matrices don't match for MODULEQUOTIENT";
  if (c=0) then << m:=ideal2mat!* m; n:=ideal2mat!* n >>;
  cali!=degrees:=dpmat_coldegs m;
  n:=for each x in dpmat_list n collect matop_pseudomod(bas_dpoly x,m);
  n:=for each x in n join if x then {x};
  return if null n then dpmat_from_dpoly dp_fi 1
  else matintersect!* for each x in n collect quot!=mquot(m,x);
  end) where cali!=degrees:=cali!=degrees;

symbolic procedure quot!=mquot(m,f);
  begin scalar a,b;
  a:=matintersect!*
    {m,dpmat_make(1,dpmat_cols m,list bas_make(1,f),dpmat_coldegs m,t)};
  b:=for each x in dpmat_list a collect
    bas_make(bas_nr x,car dp_pseudodivmod(bas_dpoly x,f));
  return dpmat_make(dpmat_rows a,0,b,nil,nil);
  end;

symbolic procedure annihilator2!* m;
  if dpmat_cols m=0 then m
  else if dpmat_cols m=1 then mat2list!* m
  else modulequotient2!*(m,dpmat_unit(dpmat_cols m,dpmat_coldegs m));

% -------- Quotients by the general element method --------

symbolic procedure idealquotient1!*(m,n);
  if dpmat_cols n>0 then rederr "second parameter must be an ideal"
  else if dpmat_cols m=0 then modulequotient1!*(m,n)
  else if dpmat_cols m=1 then
        ideal2mat!* modulequotient1!*(m,ideal2mat!* n)
  else (begin scalar u1,u2,f,v,r,m1;
  v:=list make_cali_varname(); r:=cali!=basering;
  setring!* ring_sum(r,ring_define(v,degreeorder!* v,'revlex,'(1)));
  cali!=degrees:=mo_degneworder dpmat_coldegs m;
  n:=for each x in dpmat_list n collect dp_neworder x;
  u1:=u2:=dp_from_a car v; f:=car n;
  for each x in n do
        << f:=dp_sum(f,dp_prod(u1,x)); u1:=dp_prod(u1,u2) >>;
  m1:=dpmat_sieve(gbasis!* quot!=quot(dpmat_neworder(m,nil),f),v,t);
  setring!* r; cali!=degrees:=dpmat_coldegs m;
  return dpmat_neworder(m1,t);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure modulequotient1!*(m,n);
  (begin scalar c,u1,u2,f,v,r,m1;
  if not((c:=dpmat_cols m)=dpmat_cols n) then rederr
    "MODULEQUOTIENT only for submodules of a common free module";
  if not equal(dpmat_coldegs m,dpmat_coldegs n) then
          rederr"matrices don't match for MODULEQUOTIENT";
  if (c=0) then << m:=ideal2mat!* m; n:=ideal2mat!* n >>;
  cali!=degrees:=dpmat_coldegs m;
  n:=for each x in dpmat_list n collect matop_pseudomod(bas_dpoly x,m);
  n:=for each x in n join if x then {x};
  if null n then return dpmat_from_dpoly dp_fi 1;
  v:=list make_cali_varname(); r:=cali!=basering;
  setring!* ring_sum(r,ring_define(v,degreeorder!* v,'revlex,'(1)));
  cali!=degrees:=mo_degneworder cali!=degrees;
  u1:=u2:=dp_from_a car v; f:=dp_neworder car n;
  for each x in n do
     << f:=dp_sum(f,dp_prod(u1,dp_neworder x));
        u1:=dp_prod(u1,u2)
     >>;
  m1:=dpmat_sieve(gbasis!* quot!=mquot(dpmat_neworder(m,nil),f),v,t);
  setring!* r; cali!=degrees:=dpmat_coldegs m;
  return dpmat_neworder(m1,t);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

symbolic procedure annihilator1!* m;
  if dpmat_cols m=0 then m
  else if dpmat_cols m=1 then m
  else modulequotient1!*(m,dpmat_unit(dpmat_cols m,dpmat_coldegs m));

% --------------- Stable quotients ------------------------

symbolic operator matqquot;
symbolic procedure matqquot(m,f);
% Stable quotient of dpmat m with respect to a polynomial f, i.e.
% m : <f> = { v \in F | \exists n : f^n*v \in m }
  if !*mode='algebraic then
     if eqcar(f,'list) or eqcar(f,'mat) then
                rederr("Syntax : matquot(dpmat,dpoly)")
     else dpmat_2a matqquot!*(dpmat_from_a reval m,dp_from_a reval f)
  else matqquot!*(m,f);

symbolic procedure matqquot!*(m,f);
  if dp_unit!? f then m
  else if dpmat_cols m=0 then
        mat2list!* quot!=stabquot(ideal2mat!* m,{f})
  else quot!=stabquot(m,{f});

symbolic operator matstabquot;
symbolic procedure matstabquot(m,f);
% Stable quotient of dpmat m with respect to an ideal f.
  if !*mode='algebraic then dpmat_2a
        matstabquot!*(dpmat_from_a reval m,dpmat_from_a reval f)
  else matstabquot!*(m,f);

symbolic procedure matstabquot!*(m,f);
  if dpmat_cols f > 0 then rederr "stable quotient only by ideals"
  else begin scalar c;
    if (c:=dpmat_cols m)=0 then
        << f:=for each x in dpmat_list f collect
                    matop_pseudomod(bas_dpoly x,m);
           f:=for each x in f join if x then {x}
        >>
    else f:=for each x in dpmat_list f collect bas_dpoly x;
    if null f then return
        if c=0 then dpmat_from_dpoly dp_fi 1
        else dpmat_unit(c,dpmat_coldegs m);
    if dp_unit!? car f then return m;
    if c=0 then return mat2list!* quot!=stabquot(ideal2mat!* m,f)
    else return quot!=stabquot(m,f);
    end;

symbolic procedure quot!=stabquot(m,f);
% m must be a module.
  if dpmat_cols m=0 then rederr"quot_stabquot only for cols>0"
  else (begin scalar m1,p,p1,p2,v,v1,v2,c;
    v1:=make_cali_varname(); v2:=make_cali_varname(); v:={v1,v2};
    setring!* ring_sum(c:=cali!=basering,
        ring_define(v,degreeorder!* v,'lex,'(1 1)));
    cali!=degrees:=mo_degneworder dpmat_coldegs m;
    p1:=p2:=dp_from_a v1;
    f:=for each x in f collect dp_neworder x;
    p:=car f;
    for each x in cdr f do
    << p:=dp_sum(dp_prod(p1,x),p); p1:=dp_prod(p1,p2) >>;
    p:=dp_diff(dp_fi 1,dp_prod(dp_from_a v2,p));
        % p = 1 - v2 * \sum{f_i * v1^i}
    m1:=matsum!* {dpmat_neworder(m,nil),
        dpmat_times_dpoly(p,
                dpmat_unit(dpmat_cols m,cali!=degrees))};
    m1:=dpmat_sieve(gbasis!* m1,v,t);
    setring!* c; cali!=degrees:=dpmat_coldegs m;
    return dpmat_neworder(m1,t);
   end)
   where cali!=degrees:=cali!=degrees,
                cali!=basering:=cali!=basering;

endmodule; % quot

end;
