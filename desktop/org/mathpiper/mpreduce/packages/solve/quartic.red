module quartic;  % Procedures for solving cubic, quadratic and quartic
                 % eqns.

% Author: Anthony C. Hearn.
% Modifications by: Stanley L. Kameny, Eberhard Schruefer.

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


fluid '(!*sub2 !*rounded !*trigform dmode!*);

!*trigform := t;   % Default value.

switch trigform;

symbolic procedure multfq(u,v);
   % Multiplies standard form U by standard quotient V.
   begin scalar x;
      x := gcdf(u,denr v);
      return multf(quotf(u,x),numr v) ./ quotf(denr v,x)
   end;

symbolic procedure quotsqf(u,v);
   % Forms quotient of standard quotient U and standard form V.
   begin scalar x;
      x := gcdf(numr u,v);
      return quotf(numr u,x) ./ multf(quotf(v,x),denr u)
   end;

symbolic procedure cubertq u;
   % Rationalizing the value in this and the following function leads
   % usually to neater results.
%  rationalizesq
      simpexpt list(mk!*sq subs2!* u,'(quotient 1 3));
   % simprad(u,3);

symbolic procedure sqrtq u;
%  rationalizesq
      simpexpt list(mk!*sq subs2!* u,'(quotient 1 2));
   % simprad(u,2);

% symbolic procedure subs2!* u; <<!*sub2 := t; subs2 u>>;

symbolic procedure solvequadratic(a2,a1,a0);
   % A2, a1 and a0 are standard quotients.
   % Solves a2*x**2+a1*x+a0=0 for x.
   % Returns a list of standard quotient solutions.
   % Modified to use root_val to compute numeric roots.  SLK.
   if !*rounded and numcoef a0 and numcoef a1 and numcoef a2
      then for each z in cdr root_val list mkpolyexp2(a2,a1,a0)
         collect simp!* (if eqcar(z,'equal) then caddr z
                          else errach {"Quadratic confusion",z})
    else begin scalar d;
      d := sqrtq subtrsq(quotsqf(exptsq(a1,2),4),multsq(a2,a0));
      a1 := quotsqf(negsq a1,2);
      return list(subs2!* quotsq(addsq(a1,d),a2),
                  subs2!* quotsq(subtrsq(a1,d),a2))
   end;

symbolic procedure numcoef a; denr a = 1 and domainp numr a;

symbolic procedure mkpolyexp2(a2,a1,a0);
  % The use of 'x is arbitrary here, since it is not used by root_val.
   <<a0 := numr a0;
     if numr a1 then a0 := (('x . 1) . numr a1) . a0;
     mk!*sq(((('x . 2) . numr a2) . a0) . 1)>>;

symbolic procedure solvecubic(a3,a2,a1,a0);
   % A3, a2, a1 and a0 are standard quotients.
   % Solves a3*x**3+a2*x**2+a1*x+a0=0 for x.
   % Returns a list of standard quotient solutions.
   % See Abramowitz and Stegun, Sect. 3.8.2, for solutions in
   % terms of surds and Bronstein for solutions in terms of
   % trig functions.
   begin scalar q,r,sm,sp,s1,s2,x;
      a2 := quotsq(a2,a3);
      a1 := quotsq(a1,a3);
      a0 := quotsq(a0,a3);
      q := subtrsq(quotsqf(a1,3),quotsqf(exptsq(a2,2),9));
      r := subtrsq(quotsqf(subtrsq(multsq(a1,a2),multfq(3,a0)),6),
                   quotsqf(exptsq(a2,3),27));
      if null numr q or not !*trigform or not all_real(a0,a1,a2)
        then go to cbr;
    % this section uses trig functions, but only when a0,a1,a2 are real.
      s2 := sqrtq simp {'abs,prepsq q};
      if pos_num r then s2 := negsq s2;
      if pos_num q
         then <<s1 := quotsqf(trigsq(quotsq(negsq r,exptsq(s2,3)),'asinh),3);
                sp := trigsq(s1,'sinh);
                sm := multsq(simp '(times (sqrt 3) i),trigsq(s1,'cosh))>>
       else if pos_num addsq(exptsq(q,3),exptsq(r,2))
         then <<s1 := quotsqf(trigsq(quotsq(negsq r,exptsq(s2,3)),'acosh),3);
                sp := trigsq(s1,'cosh);
                sm := multsq(simp '(times (sqrt 3) i),trigsq(s1,'sinh))>>
       else   <<s1 := quotsqf(trigsq(quotsq(negsq r,exptsq(s2,3)),'acos),3);
                sp := trigsq(s1,'cos);
                sm := multsq(simp '(sqrt 3),trigsq(s1,'sin))>>;
      return {subs2!* subtrsq(multsq(s2,multsq(-2 ./ 1,sp)),quotsqf(a2,3)),
              subs2!* subtrsq(multsq(s2,addsq(sp,sm)),quotsqf(a2,3)),
              subs2!* subtrsq(multsq(s2,subtrsq(sp,sm)),quotsqf(a2,3))};
 cbr: x := sqrtq addsq(exptsq(q,3),exptsq(r,2));
      s1 := cubertq addsq(r,x);
      s2 := if numr s1 then negsq quotsq(q,s1)
             else cubertq subtrsq(r,x);
         % This optimization only works if s1 is non zero.
      sp := addsq(s1,s2);
      sm := quotsqf(multsq(simp '(times i (sqrt 3)),subtrsq(s1,s2)),2);
 com: x := subtrsq(sp,quotsqf(a2,3));
      sp := negsq addsq(quotsqf(sp,2),quotsqf(a2,3));
      return list(subs2!* x,subs2!* addsq(sp,sm),
                  subs2!* subtrsq(sp,sm))
   end;

symbolic procedure pos_num a;
   begin scalar dmode,!*msg,!*numval;
      dmode := dmode!*;
      !*numval := t;
      on rounded,complex;
      a := resimp a;
      a := real_1 a and (numr simp list('sign,mk!*sq a)=1);
      off rounded,complex;
      if dmode then onoff(get(dmode,'dname),t);
      return a
   end;

symbolic procedure trigsq(a,fn);
   simpiden list(fn,mk!*sq subs2!* a);

symbolic procedure all_real(a,b,c);
   begin scalar dmode,!*ezgcd,!*msg,!*numval;
      % If ezgcd is on, modular arithmetic with rounded numbers can be
      % attempted.
      dmode := dmode!*;
      !*numval := t;
      on complex,rounded;
      % We should probably put an errorset here, so mode is correctly
      % reset with an error.
      a := real_1(a := resimp a) and real_1(b := resimp b)
           and real_1(c := resimp c);
      off rounded,complex;
      if dmode then onoff(get(dmode,'dname),t);
      return a
   end;

symbolic procedure real_1 x;
   numberp denr x and domainp numr x and null numr impartsq x;

symbolic procedure one_real a;
   begin scalar dmode,!*msg,!*numval;
      dmode := dmode!*;
      !*numval := t;
      on complex,rounded;
      a := real_1 resimp a;
      off rounded,complex;
      if dmode then onoff(get(dmode,'dname),t);
      return a end;

symbolic procedure solvequartic(a4,a3,a2,a1,a0);
   % Solve the quartic equation a4*x**4+a3*x**3+a2*x**2+a1*x+a0 = 0,
   % where the ai are standard quotients, using technique described in
   % Section 3.8.3 of Abramowitz and Stegun;
   begin scalar x,y,yy,cx,z,s,l,zz1,zz2,dmode,neg,!*msg,!*numval,
                a1cr,a2cr,a3cr,xcr,ycr,zcr;
      % Convert equation to monomial form.
      dmode := dmode!*;
      a3 := quotsq(a3,a4);
      a2 := quotsq(a2,a4);
      a1 := quotsq(a1,a4);
      a0 := quotsq(a0,a4);
      % Build and solve the resultant cubic equation.  We select the
      % real root if there is only one; or if there are three, we choose
      % one that yields real coefficients for the quadratics.  If no
      % roots are known to be real, we use an arbitrary one.
      yy := subtrsq(exptsq(a3,2),multfq(4,a2));
      x := solvecubic(!*f2q 1,
                      negsq a2,
                      subs2!* subtrsq(multsq(a1,a3),multfq(4,a0)),
                      subs2!* negsq addsq(exptsq(a1,2),
                                          multsq(a0,yy)));
      cx := car x;
      % Now check for real roots of the cubic.
      for each rr in x do if one_real rr then s := append(s,list rr);
      x := if (l := length s)=1 then car s else cx;
      % Now solve the two equivalent quadratic equations.
      a3 := quotsqf(a3,2); yy := quotsqf(yy,4);
      % select real coefficient for quadratic if possible.
      y := addsq(yy,x);
      if l<2 then go to zz;
loop: if not pos_num negsq y then go to zz else if l=1 then
        <<x := cx; y := addsq(yy,x); go to zz>>;
      l := l-1; s := cdr s; x := car s;
      y := addsq(yy,x); go to loop;
  zz: y := sqrtq y;
      x := quotsqf(x,2);
      z := sqrtq subtrsq(exptsq(x,2),a0);
     % the following test is needed, according to some editions of
     % Abramowitz and Stegun, to select the correct signs
     % (for the terms z) in the quadratics to produce correct roots.
     % Unfortunately, this test may fail for coefficients which are not
     % numeric because of the inability to recognize zero.
      !*numval := t;
      on rounded,complex;
      a1cr := resimp a1; a2cr := resimp a2; a3cr := resimp a3;
      xcr := resimp x; ycr := resimp y; zcr := resimp z;
      if null numr
         (zz1 :=
           resimp subtrsq(a1cr,addsq(multsq(subtrsq(a3cr,ycr),addsq(xcr,zcr)),
                 multsq(addsq(a3cr,ycr),subtrsq(xcr,zcr))))) then go to rst;
      if null numr
         (zz2 :=
           resimp subtrsq(a1cr,addsq(multsq(subtrsq(a3cr,ycr),subtrsq(xcr,zcr)),
                 multsq(addsq(a3cr,ycr),addsq(xcr,zcr)))))
         then <<neg := t; go to rst>>;
      if domainp numr zz1 and domainp numr zz2
             and numberp denr zz1 and numberp denr zz2 and
         numr simp list('sign,list('difference,list('norm,mk!*sq zz1),
           list('norm,mk!*sq zz2)))=1 then neg := t;
 rst: off rounded,complex;
      if dmode then onoff(get(dmode,'dname),t);
      if neg then z := negsq z;
      return append(solvequadratic(!*f2q 1,subtrsq(a3,y),subtrsq(x,z)),
                    solvequadratic(!*f2q 1,addsq(a3,y),addsq(x,z)))
   end;

endmodule;

end;
