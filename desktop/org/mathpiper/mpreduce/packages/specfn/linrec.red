module linrec;

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


% solves (simple in) homogenous linear recursion relation REC which
% has to have the form var(n)... with initial conditions
% IC in the form of list {var(1)=?,var(2)=...}
% the method used - substitution s(n)=x^n
% like e^(lambda*x) in linear homogenous ODE.

% The inhomogenous term has to be a constant.

% The following code has been developed by Richard Liska from Prague
% for the "Barry Simon Tests for PC Magazine".
% Some checks and generalizations included by W. Neun , ZIB Berlin

% the switch trlinrec turns the verbose mode on.

switch trlinrec;

algebraic procedure rsolve (rec,ic);

begin scalar mvar1,inde,constpart,lowestind,modic,modrec,indis;

mvar1 := part(mainvar rec ,0);
inde  := mainvar part(mainvar rec,1);
indis := the_indices(foreach kk in ic collect lhs kk,mvar1);
highestind := if indis neq {} then max(indis) else NIL;
nindis := the_indices(rec,mvar1);

           % applying rule : rec where r(~n) => 0

constpart := lisp aeval list('whereexp,
                list('list,list('replaceby,list(mvar1,list('!~,'n)),0)),
                rec);

if not freeof(constpart,inde) then rederr
   ("Cant solve recurrence equations with non-constant coefficients");

if eqn(constpart,0) then return
        solve_lin_rec(rec,ic)
  else
        <<  modrec := sub (inde = inde +1,rec);
            modrec := modrec -rec;
            if (highestind) then <<
% Propagate the recursion to get additional start value
            modic := sub(inde=inde -max(nindis)+ highestind+1,rec);
            for each aa in ic do modic := sub(aa,modic);
            modic := (first solve(modic,mainvar modic)) . ic >>
            else modic := ic;
            return solve_lin_rec(modrec,modic);
         >>;
end;


fluid '(linrecx!* linrecvar!*);

algebraic procedure solve_lin_rec(rec,ic);
   % solves homogenous linear recursion relation REC which
   % has to have the form var(n)... with initial conditions
   % IC in the form of list {var(1)=?,var(2)=...}
   % the method used - substitution s(n)=x^n
   % like e^(lambda*x) in linear homogenous ODE.
   % Of course some checking should be added. (Done WN)
   begin
     scalar lrec,sol,msol,gsol,j,flagg,c,linrecvar!*,errflag,nsave;
     clear n;
     linrecvar!* := part (mainvar rec,0);
     linrecx!* := lisp gensym();
     c:= lisp mkquote gensym(); %this is the dirty part. WN
     operator c;
     if(part(rec,0) eq linrecvar!*) and arglength(ic)=1 and
                part(mainvar lhs first ic,0) = linrecvar!*
                then return rhs (first ic);
      if(part(rec,0) neq plus) then return  rederr
       "Cant solve recurrence equations with non-constant coefficients";
      lrec := arglength rec;
      lrec := part(rec,lrec);
     for all n let linrecvar!*(n) = linrecx!*^n;
     lrec := lrec;
     rec:=rec /lrec;
     for all n clear linrecvar!* (n);
     rec:=num rec;
     for each j in coeff(rec,linrecx!*) do
                if (not freeof(j,part(part (part rec,1),1)))
                        then errflag := 17; %???
     if (errflag = 17) then return rederr
       "Cant solve recurrence equations with non-constant coefficients";
     j:=1;
     for each a in solve(rec,linrecx!*) do
       <<a:=rhs a^n;
         gsol:=gsol+c(j)*a;
         j:=j+1;
         msol:=first multiplicities!*;
         multiplicities!*:=rest multiplicities!*;
         while msol>1 do
           <<a:=n*a;
             gsol:=gsol+c(j)*a;
             j:=j+1;
             msol:=msol-1 >> >>;
     if lisp !*trlinrec then write "General solution: ",linrecvar!*
                ,"(N) := ",gsol;
     if ic = {} then sol := {} else
     sol:=solve(for each a in ic collect
                        sub(n=part(lhs a,1),gsol)=rhs a,
                for i:=1:arglength ic collect c(i));
   % If some c(i) remains it can be arbitrary  complex;
     sol := lisp subla('((equal . replaceby)),sol);
     sol := lisp subla('((equal . replaceby)),sol);
     let sol;
     gsol:=gsol;
     clearrules sol;
     let moivre_expt;
     gsol:=gsol;
     clearrules moivre_expt;

     for i:=1:j do  if coeff(gsol,lisp list(c, i)) = list(gsol) then nil
        else gsol:=
           sub(lisp list(c, i) = lisp caaar makearbcomplex(),gsol);
     return gsol
   end;

% (1 + i)**n =>     %applying Moivre's formula for complex numbers

%                N/2       N*PI          N*PI
%               2   *(COS(------) + SIN(------)*I)
%                           4             4

algebraic (moivre_expt := { (~z)^(~k) =>
              Moivre(z,k) when not  freeof(z,i)});

algebraic procedure Moivre(z,k);

  begin scalar rho,phi; % what ( will happen
  rho := sqrt( (repart z)^2 + (impart z)^2);
  if repart  z = 0 then phi := pi/2 else
          phi := atan((impart z)/(repart  z));
  return rho^k *(cos(k*phi) + i * sin (k*phi));
  end;

algebraic procedure the_indices(ex,mvar);

if part(ex,0) = list then for each kk in ex join the_indices(kk,mvar)
else
begin scalar eqq,L1,L2,kern;
  eqq := ex;
  lisp (kern := union (kernels !*q2f  (numr simp eqq ./ 1),
                        kernels !*q2f (denr simp eqq ./ 1)));

  L1 := 'list . lisp  foreach k in kern join
         if atom k then nil else if eqcar(k,mvar) then
                                list cadr k else nil;

  return L1;
end;
endmodule;
end;


