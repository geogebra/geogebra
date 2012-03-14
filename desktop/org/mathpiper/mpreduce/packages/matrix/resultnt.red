module resultnt;

% Author: Eberhard Schruefer.

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


% Modifications by: Anthony C. Hearn, Winfried Neun.

%**********************************************************************
%                                                                     *
% The resultant function defined here has the following properties:   *
%                                                                     *
%                           degr(p1,x)*degr(p2,x)                     *
%  resultant(p1,p2,x) = (-1)                     *resultant(p2,p1,x)  *
%                                                                     *
%                         degr(p2,x)                                  *
%  resultant(p1,p2,x) = p1             if p1 free of x                *
%                                                                     *
%  resultant(p1,p2,x) = 1  if p1 free of x and p2 free of x           *
%                                                                     *
%**********************************************************************

%exports resultant;

%imports reorder,setkorder,degr,addf,negf,multf,multpf;

load_package polydiv;

fluid '(!*bezout !*exp kord!*);

switch bezout;

put('resultant,'simpfn,'simpresultant);

symbolic procedure simpresultant u;
   if length u neq 3
     then rerror(matrix,19,
                   "Resultant called with wrong number of arguments")
    else resultantsq(simp!* car u,simp!* cadr u,!*a2k caddr u)
         where !*exp = t;

symbolic procedure resultant(u,v,var);
   % Kept for compatibility with old code.
   if domainp u and domainp v then 1
    else begin scalar x;
       kord!* := var . kord!*;  % updkorder can't be used here.
                                % See sum test.
       if null domainp u and null(mvar u eq var) then u := reorder u;
       if null domainp v and null(mvar v eq var) then v := reorder v;
       x := if !*bezout then bezout_resultant(u,v,var)
             else polyresultantf(u,v,var);
       setkorder cdr kord!*;
       return x
   end;

symbolic procedure resultantsq(u,v,var);
   % Uses resultant(a*P,b*Q,var) = a^ldeg Q*b^ldeg P*resultant(P,Q,var).
   if domainp numr u and domainp numr v and denr u = 1 and denr v = 1
     then 1 ./ 1
    else begin scalar x,y,z;
       kord!* := var . kord!*;  % updkorder can't be used here.
                                % See sum test.
       if null domainp numr u and null(mvar numr u eq var)
         then u := reordsq u;
       if null domainp numr v and null(mvar numr v eq var)
         then v := reordsq v;
       if (y := denr u) neq 1
          and smember(var,y) then typerr(prepf y,'polynomial)
        else if (z := denr v) neq 1
          and smember(var,z) then typerr(prepf z,'polynomial);
       u := numr u;
       v := numr v;
       if smember(var,coefflist(u,var)) then typerr(prepf u,'polynomial)
        else if smember(var,coefflist(v,var))
         then typerr(prepf v,'polynomial);
       x := if !*bezout then bezout_resultant(u,v,var)
             else polyresultantf(u,v,var);
       if y neq 1 then y := exptf(y,degr(v,var));
       if z neq 1 then y := multf(y,exptf(z,degr(u,var)));
       setkorder cdr kord!*;
       return x ./ y
   end;

symbolic procedure coefflist(u,var);
   % Returns list of pairs of degrees and coefficients of var in u.
   begin scalar z;
      while not domainp u and mvar u=var
         do <<z := (ldeg u . lc u) . z; u := red u>>;
      return if null u then z else (0 . u) . z
   end;

symbolic procedure polyresultantf(u,v,var);
   % Algorithm is from M. Bronstein, Symbolic Integration I -
   % Transcendental Functions Algorithms and Computation in Mathematics,
   % Vol. 1 Springer, Heidelberg, ISBN 3-540-60521-5.
   % Note var is assumed to be the leading term in kord!* and the main
   % variable in u and v.
   begin scalar beta,cd,cn,delta,gam,r,s,temp,x;
      cd := cn := r := s := 1;
      gam := -1;
      if domainp u or domainp v then return 1
       else if ldeg u<ldeg v
        then <<s := (-1)^(ldeg u*ldeg v); temp := u; u := v; v := temp>>;
      while v do
       <<delta := ldeg u-ldegr(v,var);
         beta := negf(multf(r,exptf(gam,delta)));
         r := lcr(v,var);
         if delta neq 0
          then gam := quotf!*(exptf(negf r,delta),exptf(gam,delta-1));
         temp := u;
         u := v;
         if not evenp ldeg temp and not evenp ldegr(u,var) then s := -s;
         v := quotf(pseudo_remf(temp,v,var),beta);
         if v   % We assume that u has var as its main variable.
           then <<cn := multf(cn,exptf(beta,ldeg u));
                  cd := multf(cd,
                     exptf(r,(1+delta)*ldeg u-ldeg temp+ldegr(v,var)));
                  if (x := quotf(cd,cn)) then <<cn := 1; cd := x>>>>>>;
      return if not domainp u and mvar u eq var then nil
              else if ldeg temp neq 1
               then quotf(multf(s,multf(cn,exptf(u,ldeg temp))),cd)
              else u
   end;

symbolic procedure lcr(u,var);
   if domainp u or mvar u neq var then u else lc u;

symbolic procedure ldegr(u,var);
   if domainp u or mvar u neq var then 0 else ldeg u;

symbolic procedure pseudo_remf(u,v,var);
   !*q2f simp pseudo!-remainder {mk!*sq(u ./ 1),mk!*sq(v ./ 1),var};

symbolic procedure bezout_resultant(u,v,w);
   % U and v are standard forms. Result is resultant of u and v
   % w.r.t. kernel w. Method is Bezout's determinant using exterior
   % multiplication for its calculation.
   begin integer n,nm; scalar ap,ep,uh,ut,vh,vt;
     if domainp u or null(mvar u eq w)
        then return if not domainp v and mvar v eq w
                       then exptf(u,ldeg v)
                     else 1
      else if domainp v or null(mvar v eq w)
        then return if mvar u eq w then exptf(v,ldeg u) else 1;
     n := ldeg v - ldeg u;
     if n < 0 then return multd((-1)**(ldeg u*ldeg v),
                                bezout_resultant(v,u,w));
     ep := 1;
     nm := ldeg v;
     uh := lc u;
     vh := lc v;
     ut := if n neq 0 then multpf(w to n,red u) else red u;
     vt := red v;
     ap := addf(multf(uh,vt),negf multf(vh,ut));
     ep := b!:extmult(!*sf2exb(ap,w),ep);
     for j := (nm - 1) step -1 until (n + 1) do
        <<if degr(ut,w) = j then
             <<uh := addf(lc ut,multf(!*k2f w,uh));
               ut := red ut>>
           else uh := multf(!*k2f w,uh);
          if degr(vt,w) = j then
             <<vh := addf(lc vt,multf(!*k2f w,vh));
               vt := red vt>>
           else vh := multf(!*k2f w,vh);
          ep := b!:extmult(!*sf2exb(addf(multf(uh,vt),
                                    negf multf(vh,ut)),w),ep)>>;
      if n neq 0
         then <<ep := b!:extmult(!*sf2exb(u,w),ep);
                for j := 1:(n-1) do
                ep := b!:extmult(!*sf2exb(multpf(w to j,u),w),ep)>>;
      return if null ep then nil else lc ep
   end;

symbolic procedure !*sf2exb(u,v);
   %distributes s.f. u with respect to powers in v.
   if degr(u,v)=0 then if null u then nil
                        else list 0 .* u .+ nil
    else list ldeg u .* lc u .+ !*sf2exb(red u,v);

%**** Support for exterior multiplication ****
% Data structure is lpow ::= list of degrees in exterior product
%                   lc   ::= standard form

symbolic procedure b!:extmult(u,v);
   %Special exterior multiplication routine. Degree of form v is
   %arbitrary, u is a one-form.
   if null u or null v then  nil
    else if v = 1 then u
    else (if x then cdr x .* (if car x then negf multf(lc u,lc v)
                   else multf(lc u,lc v))
              .+ b!:extadd(b!:extmult(!*t2f lt u,red v),
                    b!:extmult(red u,v))
       else b!:extadd(b!:extmult(red u,v),
              b!:extmult(!*t2f lt u,red v)))
      where x = b!:ordexn(car lpow u,lpow v);

symbolic procedure b!:extadd(u,v);
   if null u then v
    else if null v then u
    else if lpow u = lpow v then
            (lambda x,y; if null x then y else lpow u .* x .+ y)
        (addf(lc u,lc v),b!:extadd(red u,red v))
    else if b!:ordexp(lpow u,lpow v) then lt u .+ b!:extadd(red u,v)
    else lt v .+ b!:extadd(u,red v);

symbolic procedure b!:ordexp(u,v);
   if null u then t
    else if car u > car v then t
    else if car u = car v then b!:ordexp(cdr u,cdr v)
    else nil;

symbolic procedure b!:ordexn(u,v);
   %u is a single integer, v a list. Returns nil if u is a member
   %of v or a dotted pair of a permutation indicator and the ordered
   %list of u merged into v.
   begin scalar s,x;
     a: if null v then return(s . reverse(u . x))
     else if u = car v then return nil
     else if u and u > car v then
                 return(s . append(reverse(u . x),v))
         else  <<x := car v . x;
                 v := cdr v;
                 s := not s>>;
         go to a
   end;

endmodule;

end;
