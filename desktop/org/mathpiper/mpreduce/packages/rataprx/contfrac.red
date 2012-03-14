module contfrac;  % Continued fractions.

% Author: Lisa Temme

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


% Date:   August 1995.

% Code to check for rational polynomials.

% polynomials and rational functions
% by Winfried Neun

symbolic procedure PolynomQQQ (x);

(if fixp xx then 1 else
 if not onep denr (xx := cadr xx) then NIL
 else begin scalar kerns,kern,aa,var,fform,mvv,degg;

 fform := sfp  mvar  numr xx;
 var := reval cadr x;
 if fform then << xx := numr xx;
    while (xx neq 1) do
     << mvv :=  mvar  xx;
        degg := ldeg  xx;
        xx   := lc  xx;
        if domainp mvv then <<if not freeof(mvv,var) then
                << xx := 1 ; kerns := list list('sin,var) >> >> else
        kerns := append ( append (kernels mvv,kernels degg),kerns) >> >>
   else kerns := kernels !*q2f xx;

 aa: if null kerns then return 1;
     kern := first kerns;
     kerns := cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return NIL else go aa;
end) where xx = aeval(car x);

put('PolynomQQ,'psopfn,'polynomQQQ);

symbolic procedure ttttype_ratpoly(u);
  ( if fixp xx then 1 else
        if not eqcar (xx , '!*sq) then nil
          else and(polynomQQQ(list(mk!*sq (numr cadr xx ./ 1),
                   reval cadr u)),
                  polynomQQQ(list(mk!*sq (denr cadr xx ./ 1),
                   reval cadr u)))
 ) where xx = aeval(car u);

flag ('(type_ratpoly),'boolean);

put('type_ratpoly,'psopfn,'ttttype_ratpoly);

symbolic procedure type_ratpoly(f,z);
    ttttype_ratpoly list(f,z);


%% To combine number, rational and non-rational approaches
%% (including truncated versions) include the following
%% boolean returns and the cfracrules rulelist.

flag ('(vari),'boolean);

symbolic procedure vari(x);
   idp x;

procedure polynomialp(u,x);
  if den u = 1 and (freeof (u,x) or deg(u,x) >= 1 ) then t else nil;

flag ('(polynomialp),'boolean);

algebraic;

operator cfrac;

operator contfrac;

procedure a_constant (x);
  lisp constant_exprp (x);

cfracrules :=
{ cfrac (~x) => (begin scalar cf, pt2, q, res;
                       cf  := continued_fraction x;
                       pt2 := part(cf,2);
                       res := for q := 2:(length pt2)
                              collect append({1},{part(pt2,q)});
                       return contfrac(part(cf,1),
                                   append({part(pt2,1)},res));
                 end)
                when a_constant(x),

  cfrac (~x,~s) => (begin scalar kk, cf, cf1, pt2, cf2, cf3,
                                 bs, m, p, q, res;
                          cf := continued_fraction(x);
                          pt2 := part(cf,2);
                          if s>=length(part(cf,2))
                          then
                          << cf1 :=
                                for q:=2:(length pt2)
                                collect append({1},{part(pt2,q)});
                             res := contfrac(part(cf,1),
                                             append({part(pt2,1)},cf1));
                          >>
                          else
                          << cf2 :=
                              for kk:=1:s+1
                              collect part(pt2,kk);
                             bs  := part(cf2,s+1);
                              for m:= s step -1 until 1
                              do bs := part(cf2,m)+1/bs;
                             cf3 :=
                              for p:=2:(length cf2)
                              collect append({1},{part(cf2,p)});
                             res:=contfrac(bs,append({part(cf2,1)},cf3))
                          >>;
                      %%  res := continued_fraction(x,s);
                          return res;
                    end)
                   when a_constant(x) and numberp s,

  cfrac (~x,~s) => (begin scalar cf, pt2, q, r, res;
                          cf  := cfracall(x,s);
                          pt2 := part(cf,2);
                          if type_ratpoly(x,s)
                          then
                          <<res :=
                               for r:=2:(length pt2)
                               collect append({1},{part(pt2,r)})
                          >>
                          else
                          <<res :=
                               for q:=2:(length pt2)
                               collect list(num(part(pt2,q)),
                                            den(part(pt2,q)))
                          >>;
                          return contfrac(part(cf,1),
                                      append({part(pt2,1)},res));
                    end)
                   when not numberp x and vari s,

  cfrac(~a,~b,~c) => (begin scalar cf, pt2, q, res;
                            cf  := cfrac_ratpoly(a,b,c);
                            pt2 := part(cf,2);
                            res :=
                               for q:=2:(length pt2)
                               collect append({1},{part(pt2, q)});
                            return contfrac(part(cf,1),
                                        append({part(pt2,1)},res));
                      end)
                     when numberp c and  vari b
                      and type_ratpoly(a,b),

  cfrac(~a,~b,~c) => (begin scalar cf, pt2, q, res;
                          cf  := cfrac_nonratpoly(a,b,c);
                          pt2 := part(cf, 2);
                          res :=
                             for q:=2:length(pt2)
                             collect list(num(part(pt2,q)),
                                          den(part(pt2,q)));
                          return contfrac(part(cf,1),
                                      append({part(pt2,1)},res));
                      end)
                     when numberp c and  vari b
                      and NOT(type_ratpoly(a,b))%,

};

let cfracrules;

% LOAD Taylor Package for non-rationals

load taylor;


%INPUT my code for rational polynomials

procedure cfracall(rat_poly,var);
  begin
    scalar top_poly, bot_poly, euclidslist, ld_return;

    if type_ratpoly(rat_poly,var)
    then
      << top_poly := num rat_poly;
         bot_poly := den rat_poly;
         euclidslist := {};

         while part(longdiv(top_poly, bot_poly, var),2) neq 0 do
         <<
            ld_return := longdiv(top_poly, bot_poly, var);
            top_poly := bot_poly;
            bot_poly := part(ld_return,2);
            euclidslist := part(ld_return,1).euclidslist;
         >>;
         euclidslist := part(longdiv(top_poly, bot_poly, var),1)
                           . euclidslist;
         return list(inv_cfracall(reverse(euclidslist)),
                     reverse(euclidslist));
      >>
    else
      << return cfrac_nonratpoly(rat_poly,var,5)
      >>;
  end;


%************
%INPUT my code for rational polynomials (truncated)

procedure cfrac_ratpoly(rat_poly,var,number);
  begin
    scalar top_poly, bot_poly, euclidslist, ld_return, k;

    if type_ratpoly(rat_poly,var)
    then
      << top_poly := num rat_poly;
         bot_poly := den rat_poly;
         euclidslist := {};

         k:=number; %-1;
         while part(longdiv(top_poly, bot_poly, var),2) neq 0
               and k neq 0
         do
         <<
            ld_return := longdiv(top_poly, bot_poly, var);
            top_poly := bot_poly;
            bot_poly := part(ld_return,2);
            euclidslist := part(ld_return,1).euclidslist;
            k := k-1;
         >>;
         euclidslist := part(longdiv(top_poly, bot_poly, var),1)
                             . euclidslist;
         return list(inv_cfracall(reverse(euclidslist)),
                     reverse(euclidslist));
      >>
    else
      << return cfrac_nonratpoly(rat_poly,var,number)
      >>;
  end;



procedure longdiv(poly1, poly2,x);
  begin
    scalar numer, denom, div, div_list, elmt, flag, rem, answer;
%longdiv called by cfracall so poly2 will never be zero.

    %on rounded;
    numer := poly1;
    denom := poly2;
    div_list := {};
    div := 0;
    flag := 0;
    answer := 0;

    if   longdivdeg(numer,x) < longdivdeg(denom,x)
    then rem := numer
    else
    <<
    while (longdivdeg(numer,x) >= longdivdeg(denom,x)) AND flag neq 1 do
     <<
        if longdivlterm(numer,x) = 0
        then
          << div := numer/denom;
             rem :=0;
             flag :=1;
          >>
        else
          << div := longdivlterm(numer,x)/longdivlterm(denom,x);
             numer := numer - denom*div;
             rem := numer;
          >>;
        div_list := div.div_list;
     >>;
    answer := for each elmt in div_list sum elmt;
    >>;
    return list(answer,rem)
  end;


procedure longdivdeg(i_p,i_p_var);
  begin scalar a;
    a:= if   numberp(den(i_p))
        then deg(i_p*den(i_p),i_p_var);
    return a
  end;


procedure longdivlterm(i_p,i_p_var);
  begin scalar b;
    b := if   numberp(den(i_p))
         then lterm(den(i_p)*i_p,i_p_var)/den(i_p);
    return b
 end;
%****************



%Check for a polynomial
%%  flag ('(type_poly),'boolean);
%%  put('type_poly,'psopfn,'PolynomQQQ);


%INPUT my code for non-rationals

procedure cfrac_nonratpoly(nonrat,x,n);
  begin
    scalar hh,g, a_0, a_1, coeff_list, flag1, flag2,
           k, j, h, oneplus, xover;

    g := taylor(nonrat,x,0,2*n);

    h := 1;
    k:=n;
    if taylorp(taylortostandard g)
    then rederr "not yet implemented"
    else
    <<
%%CHANGE TO: if not type_poly then ERROR
%Include error here so that COEFF can be used in while condition
    if not type_ratpoly(taylortostandard g,x)
       or (type_ratpoly(taylortostandard g,x) and
           not(freeof(den(taylortostandard g),x)))
    then
       rederr "not yet implemented";
    while (length(coeff(taylortostandard g, x)) >1 and k>=0) do %0) do
     <<
%%CHANGE TO: if not type_poly then ERROR
%Include error here so that each time a new "g" is generated
% it will be checked to see if it is a polynomial
       if not type_ratpoly(taylortostandard g,x)
           or (type_ratpoly(taylortostandard g,x) and
               not(freeof(den(taylortostandard g),x)))
       then
           rederr "not yet implemented";
       a_0 :=  first coeff(taylortostandard g, x);
       a_1 := second coeff(taylortostandard g, x);

       if   flag1 =0
       then
         << coeff_list := {a_0};
            flag1 := 1;
         >>
       else
         << if a_1 neq 0
            then
              << g := taylorcombine(a_1*taylor(x^h,x,0,2*n)/(g - a_0));
                 coeff_list := (a_1*x^h).coeff_list;
              >>
            else
              << j := 2;
                 while j <= length coeff(taylortostandard g, x)
                    and flag2=0 do
                 <<
                    if coeffn(taylortostandard g, x, j) neq 0
                    then << a_n := coeffn(taylortostandard g, x, j);
                            flag2 := 1;
                         >>
                    else j := j+1;

                 >>;
                 coeff_list := (a_n*x^j).coeff_list;
                 g := taylorcombine(a_n*taylor(x^j,x,0,2*n)/(g - a_0));
                 flag2 := 0;
                 h := j
              >>;
          >>;
        k := k-1;
     >>;
%% %"1+" form
%%     oneplus := list(inv_cfrac_nonratpoly1(reverse(coeff_list)),
%%                                      reverse(coeff_list));

%"x/" form
    xover:= list(inv_cfrac_nonratpoly2(adaptcfrac(reverse(coeff_list))),
                                   adaptcfrac(reverse(coeff_list)));
    return xover  %% list(oneplus,xover)
    >>;
  end;
%***************




%INPUT my code for different representation of cfrac_nonratpoly


procedure adaptcfrac(l_list);
  begin
    scalar h, l, k, n, m, new_list;

    new_list := {};
    if length l_list < 3 then return l_list
    else
      << h := first l_list;
         l := second l_list;
         k := 2;
         while length l_list >= k do
         <<
            n := num l;
            d := den l;
            new_list := (n/d).new_list;
            k := k+1;
            if length l_list >= k
            then
              << l := part(l_list, k);
                 l := d*l
              >>;
         >>;
      >>;
    return h.reverse(new_list)
  end;



procedure inv_cfrac_nonratpoly1(c_list);
  begin
    scalar ans, j, expan;

    j := length c_list;
    if j < 3
    then
      << ans := for each m in c_list sum m;
         return ans;
      >>
    else
      << for k:=j step -1 until 2
         do <<   if k=j
               then expan := part(c_list,k)
               else expan := part(c_list,k) / (1 + expan);
            >>;
         expan := part(c_list,1) + expan;

         return expan
      >>;
  end;



procedure inv_cfrac_nonratpoly2(c_list);
  begin
    scalar ans, j, expan;

    j := length c_list;
    if j < 3
    then
      << ans := for each m in c_list sum m;
         return ans;
      >>
    else
      << for k:=j step -1 until 2
         do <<   if k=j
               then expan := part(c_list,k)
               else expan :=
                    num(part(c_list,k)) / (den(part(c_list,k)) + expan);
            >>;
         expan := part(c_list,1) + expan;

         return expan
      >>;
  end;

procedure inv_cfracall(c_list);
  begin
    scalar ans, j;

    j := length c_list;
    if j=0 then return {}
    else
    <<   if j=1
       then ans := part(c_list,1)
       else
         << ans := part(c_list,j);
            for k:=j-1 step -1 until 1
             do << ans := part(c_list,k) + 1/ans >>;
         >>;
    >>;
    return ans
  end;

symbolic procedure print!-contfract(x);

% printing continued fractions

  begin scalar xx,xxx;
    if null !*nat or atom x or length x < 3
        or not eqcar(caddr x,'list)
        then return 'failed;
    xx := reverse cddr caddr x;
    if length xx > 12 then  return 'failed;
    if xx then
     <<xxx := list('quotient ,cadr first xx,caddr first xx);
       for each tt in rest xx do
         xxx := list('quotient ,cadr tt,list('plus,caddr tt,xxx));
       if cadr caddr x = 0 then maprin list('list,cadr x,xxx) else
        maprin list('list,cadr x,list ('plus,cadr caddr x ,xxx));
     >> else maprin list('list,cadr x,cadr caddr x);
    return t;
   end;

put('contfrac,'prifn,'print!-contfract);

endmodule;

end;

