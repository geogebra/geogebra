module odim;

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

                ##########################################
                ##                                      ##
                ##   Applications to zerodimensional ##
                ##      ideals and modules.               ##
                ##                                      ##
                ##########################################

getkbase returns a k-vector space basis of S^c/M,
odim_borderbasis computes a borderbasis of M,
odim_up finds univariate polynomials in zerodimensional ideals.

END COMMENT;

% -------------- Test for zero dimension -----------------
% For a true answer m must be a gbasis.

put('dimzerop,'psopfn,'odim!=zerop);
symbolic procedure odim!=zerop m;
  begin scalar c;
  intf_test m; intf_get(m:=car m);
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  if dimzerop!* c then return 'yes else return 'no;
  end;

symbolic procedure dimzerop!* m; null odim_parameter m;

symbolic procedure odim_parameter m;
% Return a parameter of the dpmat m or nil, if it is zerodimensional
% or (1).
  odim!=parameter moid_from_dpmat m;

symbolic procedure odim!=parameter m;
  if null m then nil
  else odim!=parameter1 cdar m or odim!=parameter cdr m;

symbolic procedure odim!=parameter1 m;
  if null m then
        ((if u then car u else u)
        where u:= reverse ring_names cali!=basering)
  else if mo_zero!? car m then nil
  else begin scalar b,u;
  u:=for each x in m join if length(b:=mo_support x)=1 then b;
  b:=reverse ring_names cali!=basering;
  while b and member(car b,u) do b:=cdr b;
  return if b then car b else nil;
  end;

% --- Get a k-base of F/M as a list of monomials ----
% m must be a gbasis for the correct result.

put('getkbase,'psopfn,'odim!=evkbase);
symbolic procedure odim!=evkbase m;
  begin scalar c;
  intf_test m; intf_get(m:=car m);
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  return moid_2a getkbase!* c;
  end;

symbolic procedure getkbase!* m;
  if not dimzerop!* m then rederr"dpmat not zerodimensional"
  else for each u in moid_from_dpmat m join
        odim!=kbase(mo_from_ei car u,ring_names cali!=basering,cdr u);

symbolic procedure odim!=kbase(mo,n,m);
  if moid_member(mo,m) then nil
  else mo . for each x on n join
                odim!=kbase(mo_inc(mo,car x,1),append(x,nil),m);

% --- Produce an univariate polynomial inside the ideal m ---

symbolic procedure odim_up(a,m);
% Returns a univariate polynomial (of smallest possible degree if m
% is a gbasis) in the variable a inside the zerodimensional ideal m.
% Uses Buchberger's approach.
  if dpmat_cols m>0 or not dimzerop!* m then
      rederr"univariate polynomials only for zerodimensional ideals"
  else if not member(a,ring_names cali!=basering) then
    typerr(a,"variable name")
  else if dpmat_unitideal!? m then dp_fi 1
  else begin scalar b,v,p,l,q,r;
    % l is a list of ( p(a) . NF p(a) ), sorted by lt NF p(a)
    p:=(dp_fi 1 . dp_fi 1); b:=dpmat_list m;  v:=mo_from_a a;
    while cdr p do
      << l:=merge(list p,l,function odim!=greater);
         q:=dp_times_mo(v,car p);
         r:=red_redpol(b,bas_make(0,dp_times_mo(v,cdr p)));
         p:=odim!=reduce(dp_prod(cdr r,q) . bas_dpoly car r,l);
      >>;
    return
    if !*bcsimp then car dp_simp car p
    else car p;
    end;

symbolic procedure odim!=greater(a,b);
    mo_compare(dp_lmon cdr a,dp_lmon cdr b)=1;

symbolic procedure odim!=reduce(a,l);
  if null cdr a or null l or odim!=greater(a, car l) then a
  else if mo_equal!?(dp_lmon cdr a,dp_lmon cdar l) then
    begin scalar z,z1,z2,b;
    b:=car l; z1:=bc_neg dp_lc cdr a; z2:=dp_lc cdr b;
    if !*bcsimp then
      << if (z:=bc_inv z1) then <<z1:=bc_fi 1; z2:=bc_prod(z2,z)>>
         else
           << z:=bc_gcd(z1,z2);
              z1:=car bc_divmod(z1,z);
              z2:=car bc_divmod(z2,z);
           >>;
      >>;
    a:=dp_sum(dp_times_bc(z2,car a),dp_times_bc(z1,car b)) .
           dp_sum(dp_times_bc(z2,cdr a),dp_times_bc(z1,cdr b));
    return odim!=reduce(a,cdr l)
    end
  else odim!=reduce(a,cdr l);

% ------------------------- Borderbasis -----------------------

symbolic procedure odim_borderbasis m;
% Returns a border basis of the zerodimensional dpmat m as list of
% base elements.
  if not !*noetherian then
        rederr"BORDERBASIS only for non noetherian term orders"
  else if not dimzerop!* m then
        rederr"BORDERBASIS only for zerodimensional ideals or modules"
  else begin scalar b,v,u,mo,bas;
  bas:=bas_zerodelete dpmat_list m;
  mo:=for each x in bas collect dp_lmon bas_dpoly x;
  v:=for each x in ring_names cali!=basering collect mo_from_a x;
  u:=for each x in bas collect
        {dp_lmon bas_dpoly x,red_tailred(bas,x)};
  while u do
  << b:=append(b,u);
     u:=listminimize(
        for each x in u join
            for each y in v join
                (begin scalar w; w:=mo_sum(first x,y);
                if not listtest(b,w,function(lambda(x,y);car x=y))
                        and not odim!=interior(w,mo) then
                        return {{w,y,bas_dpoly second x}}
                end),
        function(lambda(x,y);car x=car y));
     u:=for each x in u collect
        {first x,
        red_tailred(bas,bas_make(0,dp_times_mo(second x,third x)))};
  >>;
  return bas_renumber for each x in b collect second x;
  end;

symbolic procedure odim!=interior(m,mo);
% true <=> monomial m is in the interior of the moideal mo.
  begin scalar b; b:=t;
  for each x in mo_support m do
        b:=b and moid_member(mo_diff(m,mo_from_a x),mo);
  return b;
  end;

endmodule; % odim

end;
