module table1;

% Author: Anthony C. Hearn.

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


fluid '(zero);

%---------

subtypes sint bint < int, zero nzint < sint;

ranks abs u: {int} -> int,
      u < v: {int,int} -> bool,
      u <= v: {int,int} -> bool,
      u > v: {int,int} -> bool,
      u >= v: {int,int} -> bool,
      u:int -> sint when abs u < 5000,
      u:int -> bint when abs u >= 5000,
      u:sint -> zero when u = 0,
      u:sint -> nzint when not(u=0); % not a good type name.

symbolic procedure abs_int u; mkobject(abs value u,'int);

symbolic procedure lessp_int_int(u,v); mkobject(value u < value v,'bool);

symbolic procedure leq_int_int(u,v); mkobject(value u<=value v,'bool);

symbolic procedure greaterp_int_int(u,v); mkobject(value u >= value v,'bool);

symbolic procedure geq_int_int(u,v); mkobject(value u >= value v,'bool);

symbolic procedure equal_int_int(u,v); mkobject(value u = value v,'bool);

%---------

remflag('(and or),'nary);

ranks u and v : {bool,bool} -> bool,
      u or v : {bool,bool} -> bool,
      not u : {bool} -> bool;

symbolic procedure and_bool_bool(u,v); mkobject(value u and value v,'bool);

symbolic procedure or_bool_bool(u,v); mkobject(value u or value v,'bool);

symbolic procedure not_bool u; mkobject(null value u,'bool);

%---------

subtypes zero int kernel xpoly < poly, sint bint < int, zero nzint < sint,
         variable xkernel < kernel, zero poly xratpol < ratpol;

ranks fixp u : {poly} -> bool,
      idp u : {poly} -> bool,
      u:poly -> zero when u=0,
      u:poly -> int when fixp u,
      u:poly -> kernel when kernelp u,
      u:poly -> xpoly when not fixp u and not idp u and not kernelp u;

ranks u:kernel -> variable when idp u;

symbolic procedure kernelp u;
   null domainp u and null red u and lc u =1 and ldeg u = 1;

symbolic procedure fixp_poly u; mkobject(fixp value u,'bool);

symbolic procedure idp_poly u; mkobject(idp value u,'bool);

symbolic procedure poly!>kernel u;
   mkobject(mvar value u,'kernel);


% --------------- rational --------------------

ranks den u : {ratpol} -> poly;  % should be strengthened

ranks u:ratpol -> poly when denr u = 1,
      u:ratpol -> xratpol when denr u neq 1;

symbolic procedure den_ratpol u; mkobject(denr value u,'poly);

symbolic procedure ratpol!>poly u;
   mkobject(numr value u,'poly);


% -----  +  -----

ranks u + v : {sint,sint} -> int,
      u + v : {int,int} -> int,
      u + v : {int,poly} -> poly,
      u + v : {poly,int} -> poly,
      u + v : {kernel,kernel} -> poly,
      u + v : {xpoly,xpoly} -> poly,
      u + v : {kernel,poly} -> poly,
      u + v : {poly,kernel} -> poly;

symbolic procedure plus_sint_sint(u,v);
   mkobject(value u #+ value v,'int);

symbolic procedure plus_int_int(u,v);
  mkobject(value u+value v,'int);

symbolic procedure plus_poly_poly(u,v);
   if type u eq 'zero then v else
      if type v eq 'zero then u else
      if xtype(u,'int) then plus_int_poly(u,v) else
      if xtype(v,'int) then plus_int_poly(v,u) else
      if xtype(u,'kernel) then plus_kernel_poly(u,v) else
      if xtype(v,'kernel) then plus_kernel_poly(v,u) else
      plus_xpoly_xpoly(u,v);

symbolic procedure plus_xpoly_xpoly(u,v);
   mkobject(addf!*(value u,value v),'poly);

symbolic procedure addf!*(u,v);
   (if null x then 0 else x) where x=addf(u,v);

symbolic procedure plus_int_poly(u,v);
   if type u eq 'zero then v else mkobject(addd!*(u,v),'poly);

symbolic procedure plus_poly_int(u,v);
   plus_int_poly(v,u);

symbolic procedure addd!*(u,v);
   if xtype(v,'kernel) then addd(value u,!*k2f value v) else
    addd(value u,value v);

symbolic procedure plus_kernel_kernel(u,v);
   mkobject(addf(!*k2f value u,!*k2f value v),'poly);

symbolic procedure plus_kernel_poly(u,v);
   mkobject(addf!*(!*k2f value u,value v),'poly);


ranks u + v : {xratpol,xratpol} -> ratpol,
      u + v : {poly,xratpol} -> ratpol,
      u + v : {xratpol,poly} -> ratpol;


symbolic procedure plus_xratpol_poly(u,v);
   plus_poly_xratpol(v,u);

symbolic procedure plus_poly_xratpol(u,v);
   % Add a polynomial to non-zero rational.
   begin scalar x,y,z;
      x := mkobject(denr value v,'poly);
      y := times_poly_poly(u,x);
      z := plus_poly_poly(y,mkobject(numr value v,'poly));
      return mkobject(value z ./value x,'xratpol)
   end;

symbolic procedure plus_xratpol_xratpol(u,v);
   mkobject(xaddsq(value u,value v),'ratpol);

symbolic procedure xaddsq(u,v);
   % U and V are non-zero standard quotients.
   % Value is canonical sum of U and V.
   begin scalar x,y,z;
        if null !*exp then <<u := numr u ./ mkprod denr u;
                             v := numr v ./ mkprod denr v>>;
        if !*lcm then x := gcdf!*(denr u,denr v)
         else x := gcdf(denr u,denr v);
        z := canonsq(quotf(denr u,x) ./ quotf(denr v,x));
        y := addf(multf(denr z,numr u),multf(numr z,numr v));
        if null y then return nil ./ 1;
        z := multf(denr u,denr z);
        return if x=1 or (x := gcdf(y,x))=1 then y ./ z
                else canonsq(quotf(y,x) ./ quotf(z,x))
    end;

% -----  -  -----

ranks - u : {int} -> int,
      - u : {kernel} -> poly,
      - u : {poly} -> poly,
      - u : {ratpol} -> ratpol,
      u - v : {int,int} -> int,
      u - v : {int,poly} -> poly,
      u - v : {poly,int} -> poly,
      u - v : {kernel,kernel} -> poly,
      u - v : {poly,poly} -> poly,
      u - v : {ratpol,ratpol} -> ratpol;


symbolic procedure minus_int u; mkobject(-value u,'int);

symbolic procedure difference_int_int(u,v);
   mkobject(value u-value v,'int);

symbolic procedure difference_poly_poly(u,v);
   if type u eq 'zero then minus_poly v else
      if type v eq 'zero then u else
%     if xtype(u,'int) then difference_int_poly(u,v) else
%     if xtype(v,'int)
%       then rapply('minus,list difference_int_poly(v,u)) else
      if xtype(u,'kernel) then difference_kernel_poly(u,v) else
      if xtype(v,'kernel)
        then rapply('minus,list difference_kernel_poly(v,u)) else
      difference_xpoly_xpoly(u,v);

symbolic procedure difference_int_poly(u,v);
   if type u eq 'zero then minus_poly v else
   mkobject(difference!*(u,v),'poly);

symbolic procedure difference_poly_int(u,v);
   if type v eq 'zero then u else
   mkobject(negf difference!*(v,u),'poly);

symbolic procedure difference!*(u,v);
   if xtype(v,'kernel) then addd(value u,negf !*k2f value v) else
    addd(value u,negf value v);

symbolic procedure difference_kernel_kernel(u,v);
   (if null x then ZERO else
     mkobject(x,'poly)) where x=addf(!*k2f value u,negf !*k2f value v);

symbolic procedure difference_kernel_poly(u,v);
   plus_kernel_poly(u,minus_poly v);

symbolic procedure minus_ratpol u;
      if xtype(u,'poly) then minus_poly u else minus_xratpol u;

symbolic procedure minus_xratpol u;
      mkobject(negsq value u,'xratpol);

symbolic procedure minus_poly u;
   if type u eq 'zero then u else
      if xtype(u,'int) then minus_int u else
      if xtype(u,'kernel) then minus_kernel u else
      minus_xpoly u;

symbolic procedure minus_kernel u;
   mkobject(negf !*a2f value u,'xpoly);

symbolic procedure minus_xpoly u;
   mkobject(negf value u,'poly);

symbolic procedure difference_xpoly_xpoly(u,v);
   mkobject(addf!*(value u,negf value v),'poly);

symbolic procedure difference_ratpol_ratpol(u,v);
   if type u eq 'zero then minus_ratpol v else
   if type v eq 'zero then u else
   if xtype(u,'poly)
    then plus_poly_xratpol(u,minus_xratpol v) else
   if xtype(v,'poly) then plus_poly_xratpol(minus_poly v,u) else
   plus_xratpol_xratpol(u,minus_xratpol v);

% -----  *  -----

ranks u*v : {sint,sint} -> int,
      u*v : {int,int} -> int,
      u*v : {int,poly} -> poly,
      u*v : {poly,int} -> poly,
      u*v : {poly,poly} -> poly,
      u*v : {xpoly,xpoly} -> poly,
      u*v : {kernel,kernel} -> poly,
      u*v : {ratpol,ratpol} -> ratpol;


symbolic procedure times_sint_sint(u,v);
   mkobject(value u * value v,'int);   % #* would be better.

symbolic procedure times_int_int(u,v);
  mkobject(value u*value v,'int);

symbolic procedure times_int_poly(u,v);
   if type u eq 'zero then u else mkobject(multd!*(u,v),'poly);

symbolic procedure times_poly_int(u,v);
   times_int_poly(v,u);

symbolic procedure multd!*(u,v);
   if xtype(v,'kernel) then multd(value u,!*k2f value v) else
    multd(value u,value v);

symbolic procedure times_kernel_kernel(u,v);
   mkobject(multf(!*k2f value u,!*k2f value v),'poly);

symbolic procedure times_kernel_poly(u,v);
   mkobject(multf(!*k2f value u,value v),'poly);

symbolic procedure times_poly_poly(u,v);
   if type u eq 'zero then u else
      if type v eq 'zero then v else
      if xtype(u,'int) then times_int_poly(u,v) else
      if xtype(v,'int) then times_int_poly(v,u) else
      if xtype(u,'kernel) then times_kernel_poly(u,v) else
      if xtype(v,'kernel) then times_kernel_poly(v,u) else
      times_xpoly_xpoly(u,v);

symbolic procedure times_xpoly_xpoly(u,v);
   mkobject(multf(value u,value v),'poly);


symbolic procedure times_ratpol_ratpol(u,v);
   % Note that if u is a poly,  v must be a xratpol, since a poly would
   % be caught by times_poly_poly.
   if type u eq 'zero then u else
   if type v eq 'zero then v else
   if xtype(u,'poly) then times_poly_xratpol(u,v) else
   if xtype(v,'poly) then times_poly_xratpol(v,u) else
   times_xratpol_xratpol(u,v);

symbolic procedure times_poly_xratpol(u,v);
   if xtype(u,'kernel)
     then mkobject(xmultsq(!*k2q value u,value v),'ratpol) else
   % Next line catches other poly cases (int and xpoly)
    mkobject(xmultsq(!*f2q value u,value v),'ratpol);

symbolic procedure times_xratpol_xratpol(u,v);
   mkobject(xmultsq(value u,value v),'ratpol);

symbolic procedure xmultsq(u,v);
   % Doesn't need zero etc check.
   multsq(u,v);

% -----  ^  -----

ranks u^n : {int,int} -> int,    % n should be restricted to posint
      u^n : {poly,int} -> poly,
      u^n : {xratpol,int} -> ratpol,
      u^v : {ratpol,ratpol} -> ratpol;

symbolic procedure expt_int_int(u,v); mkobject(value u**value v,'int);

fluid '(ONE);

ONE := mkobject(1,'nzint);

symbolic procedure expt_poly_int(u,v);
   if type v eq 'zero then ONE else
    if xtype(u,'int) then expt_int_int(u,v) else
    if xtype(u,'kernel) then
     mkobject(!*q2f exptsq(!*k2q value u,value v),'poly) else
     mkobject(!*q2f exptsq(value u ./ 1,value v),'xpoly);

symbolic procedure expt_xratpol_int(u,v);
   % Poly case handled by expt_poly_int.
   if type v eq 'zero then ONE else
    mkobject(exptsq(value u,value v),'ratpol);

symbolic procedure expt_ratpol_ratpol(u,v);
   simp4 {'expt,svalue u,svalue v};

symbolic procedure svalue u;
   (if x then apply1(x,y) else y)
    where x=get(type u,'prefix_convert), y=value u;


% ------------- / ----------------

ranks u/v : {ratpol,nzint} -> ratpol,   % the following are too liberal.
      u/v : {ratpol,xpoly} -> ratpol,
      u/v : {ratpol,kernel} -> ratpol,
      u/v : {ratpol,xratpol} -> ratpol;


symbolic procedure quotient_ratpol_nzint(u,v);
   if type u = 'zero then u
    else if xtype(u,'kernel)
     then mkobject(quotsq(!*k2f value u ./ 1,value v ./ 1),'ratpol)
    else if xtype(u,'xpoly) or xtype(u,'int)
     then mkobject(quotsq(value u ./ 1,value v ./ 1),'ratpol)
    else mkobject(quotsq(value u,value v ./ 1),'ratpol);

symbolic procedure quotient_ratpol_xpoly(u,v);
   quotient_ratpol_nzint(u,v);

symbolic procedure quotient_ratpol_kernel(u,v);
   if type u = 'zero then u
    else if xtype(u,'kernel)
     then mkobject(quotsq(!*k2f value u ./ 1,!*k2f value v ./ 1),'ratpol)
    else if xtype(u,'xpoly) or xtype(u,'int)
     then mkobject(quotsq(value u ./ 1,!*k2f value v ./ 1),'ratpol)
    else mkobject(quotsq(value u,!*k2f value v ./ 1),'ratpol);

symbolic procedure quotient_ratpol_xratpol(u,v);
   if type u = 'zero then u
    else if xtype(u,'kernel)
     then mkobject(quotsq(!*k2f value u ./ 1,value v),'ratpol)
    else if xtype(u,'xpoly) or xtype(u,'int)
     then mkobject(quotsq(value u ./ 1,value v),'ratpol)
    else mkobject(quotsq(value u,value v),'ratpol);

% ----- df -----

ranks df(u,v) : {poly,kernel} -> poly,
      df(u,v) : {xratpol,kernel} -> ratpol;

symbolic procedure df_poly_kernel(u,v);
   if type u eq 'zero then u else
    if xtype(u,'int) then ZERO else
    if xtype(u,'kernel) then
      mkobject(diffp(value u .** 1,value v),'ratpol) else
      mkobject(difff(value u,value v),'ratpol);

symbolic procedure df_xratpol_kernel(u,v);
   mkobject(diffsq(value u,value v),'ratpol);


% ----- int (integration) -----

ranks int(u,v) : {poly,kernel} -> ratpol,
      int(u,v) : {xratpol,kernel} -> ratpol;

symbolic procedure int_poly_kernel(u,v);
   if type u eq 'zero then u else
    if xtype(u,'int) then ZERO else
    if xtype(u,'kernel) then
      mkobject(simpint{value u,value v},'ratpol) else
      mkobject(simpint{prepf value u,value v},'ratpol);

symbolic procedure int_xratpol_kernel(u,v);
   mkobject(simpint{prepsq value u,value v},'ratpol);


% -----  list  ---

%ranks u . v : {generic,list} -> non_empty_list,
%      first u : {non_empty_list} -> generic,    % Hmmmmm.
%      rest u : {non_empty_list} -> list,
%      reverse u : {list} -> list;

%symbolic procedure cons_generic_list(u,v);
%   mkobject(u . value v,'non_empty_list);

%symbolic procedure first_non_empty_list u; car value u;

%symbolic procedure rest_non_empty_list u; mkobject(cdr value u,'list);

%symbolic procedure reverse_list u;
%   mkobject(reverse value u,'list);


% -----  setq  -----

ranks u := v : {kernel,poly} -> poly,
      u := v : {kernel,xratpol} -> xratpol;

symbolic procedure setq_kernel_poly(u,v);
   % Make an assignment of v to u.
   if type u eq 'variable then putobject(value u,value v,type v) else
    if xtype(u,'kernel) then nil else
    typerr(u,"assignment");

symbolic procedure setq_kernel_xratpol(u,v);
   setq_kernel_poly(u,v);


% -----  equal neq lessp greaterp  ---

ranks u < v : {int,int} -> bool,
      u > v : {int,int} -> bool,
      u neq v : {int,int} -> bool,
      u = v : {int,int} -> bool;

% These need to check sub-cases.

symbolic procedure greaterp_int_int(u,v);
   mkobject(value u>value v,'bool);

symbolic procedure lessp_int_int(u,v);
   mkobject(value u<value v,'bool);

% symbolic procedure equal_int_int(u,v);
%    mkobject(eqn(value u,value v),'bool);

symbolic procedure neq_int_int(u,v);
   mkobject(not eqn(value u,value v),'bool);


% ----- quote -----

ranks 'u : {generic} -> generic;

symbolic procedure quote_generic u; u;

flag('(quote),'non_form);


% These need to be merged.

symbolic procedure equal_kernel_kernel(u,v);
   mkobject(value u = value v,'bool);

symbolic procedure equal_power_power(u,v);
   mkobject(value u = value v,'bool);

symbolic procedure greaterp_xpower_xpower(u,v);
    greaterp_power_power(u,v);

symbolic procedure greaterp_power_power(u,v);
  mkobject(ordpp(cadar x . cadadr x, cadar y . cadadr y),'bool)
     where x = cdadr u,y = cdadr v;

symbolic procedure greaterp_kernel_kernel(u,v);
   mkobject((value u neq value v) and ordop(value u,value v),'bool);

addrank0('in,'(list),'((x1) (t (quote (in_non_empty_list noval)))));
addrank0('out,'(list),'((x1) (t (quote (out_non_empty_list noval)))));
addrank0('shut,'(list),'((x1) (t (quote (shut_non_empty_list noval)))));

endmodule;

end;
