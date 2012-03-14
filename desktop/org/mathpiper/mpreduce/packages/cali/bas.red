module bas;

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

              #######################
              ####               ####
              ####  IDEAL BASES  ####
              ####               ####
              #######################


Ideal bases are lists of vector polynomials (with additional
information), constituting the rows of a dpmat (see below).  In a
rep. part there can be stored vectors representing each base element
according to a fixed basis. Usually rep=nil.

Informal syntax :

 <bas>          ::= list of base elements
 <base element> ::= list(nr dpoly length ecart rep)

END COMMENT;


% -------- Reference operators for the base element b ---------

symbolic procedure bas_dpoly b; cadr b;
symbolic procedure bas_dplen b; caddr b;
symbolic procedure bas_nr b; car b;
symbolic procedure bas_dpecart b; cadddr b;
symbolic procedure bas_rep b; nth(b,5);

% ----- Elementary constructors for the base element be --------

symbolic procedure bas_newnumber(nr,be);
% Returns be with new number part.
   nr . cdr be;

symbolic procedure bas_make(nr,pol);
% Make base element with rep=nil.
   list(nr,pol, length pol,dp_ecart pol,nil);

symbolic procedure bas_make1(nr,pol,rep);
% Make base element with prescribed rep.
   list(nr,pol, length pol,dp_ecart pol,rep);

symbolic procedure bas_getelement(i,bas);
% Returns the base element with number i from bas (or nil).
  if null bas then list(i,nil,0,0,nil)
  else if eqn(i,bas_nr car bas) then car bas
  else bas_getelement(i,cdr bas);

% ---------- Operations on base lists ---------------

symbolic procedure bas_sort b;
% Sort the base list b.
  sort(b,function red_better);

symbolic procedure bas_print u;
% Prints a list of distributive polynomials using dp_print.
  begin terpri();
     if null u then print 'empty
     else for each v in u do
            << write bas_nr v, " -->  "; dp_print2 bas_dpoly v >>
  end;

symbolic procedure bas_renumber u;
% Renumber base list u.
  if null u then nil
  else begin scalar i; i:=0;
      return for each x in u collect <<i:=i+1; bas_newnumber(i,x) >>
  end;

symbolic procedure bas_setrelations u;
% Set in the base list u the relation part rep of base element nr. i
% to e_i (provided i>0).
  for each x in u do
        if bas_nr x > 0 then rplaca(cddddr x, dp_from_ei bas_nr x);

symbolic procedure bas_removerelations u;
% Remove relation parts.
  for each x in u do rplaca(cddddr x, nil);

symbolic procedure bas_getrelations u;
% Returns the relations of the base list u as a separate base list.
  begin scalar w;
  for each x in u do w:=bas_make(bas_nr x,bas_rep x) . w;
  return reversip w;
  end;

symbolic procedure bas_from_a u;
% Converts the algebraic (prefix) form u to a base list clearing
% denominators. Only for lists.
   bas_renumber for each v in cdr u collect
        bas_make(0,dp_from_a prepf numr simp v);

symbolic procedure bas_2a u;
% Converts the base list u to its algebraic prefix form.
    append('(list),for each x in u collect dp_2a bas_dpoly x);

symbolic procedure bas_neworder u;
% Returns reordered base list u (e.g. after change of term order).
    for each x in u collect
        bas_make1(bas_nr x,dp_neworder bas_dpoly x,
                                dp_neworder bas_rep x);

symbolic procedure bas_zerodelete u;
% Returns base list u with zero elements deleted but not renumbered.
    if null u then nil
    else if null bas_dpoly car u then bas_zerodelete cdr u
    else car u.bas_zerodelete cdr u;

symbolic procedure bas_simpelement b;
% Returns (b_new . z) with
%       bas_dpoly b_new having leading coefficient 1 or
%       gcd(dp_content bas_poly,dp_content bas_rep) canceled out
% and dpoly_old = z * dpoly_new , rep_old= z * rep_new.

  if null bas_dpoly b then b . bc_fi 1
  else begin scalar z,z1,pol,rep;
    if (z:=bc_inv (z1:=dp_lc bas_dpoly b)) then
        return bas_make1(bas_nr b,
                dp_times_bc(z,bas_dpoly b),
                dp_times_bc(z,bas_rep b))
                        . z1;

    % -- now we assume that base coefficients are a gcd domain ----

    z:=bc_gcd(dp_content bas_dpoly b,dp_content bas_rep b);
    if bc_minus!? z1 then z:=bc_neg z;
    pol:=for each x in bas_dpoly b collect
                car x . car bc_divmod(cdr x,z);
    rep:=for each x in bas_rep b collect
                car x . car bc_divmod(cdr x,z);
    return bas_make1(bas_nr b,pol,rep) . z;
    end;

symbolic procedure bas_simp u;
% Applies bas_simpelement to each dpoly in the base list u.
   for each x in u collect car bas_simpelement x;

symbolic procedure bas_zero!? b;
% Test whether all base elements are zero.
   null b or (null bas_dpoly car b and bas_zero!? cdr b);

symbolic procedure bas_sieve(bas,vars);
% Sieve out all base elements from the base list bas with leading
% term containing a variable from the list of var. names vars and
% renumber the result.
   begin scalar m;  m:=mo_zero();
   for each x in vars do
       if member(x,ring_names cali!=basering) then
            m:=mo_sum(m,mo_from_a x)
       else typerr(x,"variable name");
   return bas_renumber for each x in bas_zerodelete bas join
        if mo_zero!? mo_gcd(m,dp_lmon bas_dpoly x) then {x};
   end;

symbolic procedure bas_homogenize(b,var);
% Homogenize the base list b using the var. name var.
% Note that the rep. part is correct only upto a power of var !
  for each x in b collect
      bas_make1(bas_nr x,dp_homogenize(bas_dpoly x,var),
                dp_homogenize(bas_rep x,var));

symbolic procedure bas_dehomogenize(b,var);
% Set the var. name var in the base list b equal to one.
  begin scalar u,v;
  if not member(var,v:=ring_all_names cali!=basering) then
    typerr(var,"dpoly variable");
  u:=setdiff(v,list var);
  return for each x in b collect
                bas_make1(bas_nr x,dp_seed(bas_dpoly x,u),
                            dp_seed(bas_rep x,u));
  end;

% ---------------- Special tools for local algebra -----------

symbolic procedure bas!=factorunits p;
  if null p then nil
    else bas!=delprod
        for each y in cdr (fctrf numr simp dp_2a p where !*factor=t)
            collect (dp_from_a prepf car y . cdr y);

symbolic procedure bas!=delprod u;
  begin scalar p; p:=dp_fi 1;
     for each x in u do
        if not dp_unit!? car x then p:=dp_prod(p,dp_power(car x,cdr x));
     return p
  end;

symbolic procedure bas!=detectunits p;
  if null p then nil
  else if listtest(cdr p,dp_lmon p,
        function(lambda(x,y);not mo_vdivides!?(y,car x))) then p
  else list dp_term(bc_fi 1,dp_lmon p);

symbolic procedure bas_factorunits b;
  bas_make(bas_nr b,bas!=factorunits bas_dpoly b);

symbolic procedure bas_detectunits b;
  bas_make(bas_nr b,bas!=detectunits bas_dpoly b);


endmodule;  % bas

end;
