module simplede;

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


fluid '(!*precise fps_search_depth !*protfg ps!:order!-limit);

global '(inconsistent!*);

share fps_search_depth;

fps_search_depth := 5; %the default

switch traceFPS;

algebraic <<  operator BA; operator infsum ;
                array DFF(50) >>;

put('simplede,'psopfn,'simpledeeval);

symbolic procedure simpledeeval(u);
   begin scalar res,usevar;
     if length u = 2 then
        << usevar := 'y;
           res := int_simplede(car u,cadr u);
           if eq(res,-1) then return simpledeexit(car u,cadr u,'y);
        >>
        else if length u = 3 then
                << usevar := caddr u;
                res := int_simplede(car u,cadr u);
                if eq(res,-1) then
                         return simpledeexit(car u,cadr u,usevar);
                >>
                else rederr("Wrong number of Arguments for simplede");
     res := sublis('((oddexpt . expt)(ba . a)(nn . k)),res);
     return if reval usevar = usevar then
                     sublis(list('ff . usevar),res)
                else sublis(list('ff . intern gensym()),res);
        end;

algebraic procedure int_simplede(f,x);

begin scalar cap_a,degree0fde,cap_f,j,cap_J,nnn,s,ind,deq,eqq,reqq,
        ak,terms,list1,list2,Nmax,cap_m,cap_R,ii,m,leadcoeff,m0,
        len,cap_S,result,parameters,solved,!*allfac,!*protfg;

        !*protfg := t;
        Nmax :=fps_search_depth;
        clear a;
        operator A; off allfac;
        depend ff,x;

        DFF(0) := f;

% start search for a simple DE

        for degreeofDE:=1:Nmax do
            <<  DFF(degreeofDE) := df(DFF(degreeofDE-1),x);
                eqq :=  DFF(degreeofDE) +
                       for j:=0:(degreeofDE-1) sum A(j) * DFF(j);
                eqq := RecursionSimplify(eqq);
                eqq := num eqq;
                terms := {};
                list1 := converttolist (eqq,degreeofDE+1);
                while list1 neq {} do
                     << list2 := {}; j := fastpart(list1 ,1);
                        cap_j := j; len := fastlength list1;
                        for i:=2:len do
                           if type_ratpoly(j/fastpart(list1,i),x) then
                                cap_J := cap_J + fastpart(list1,i)
                           else list2 := fastpart(list1,i) . list2;
                        terms := cap_J . terms;
                        list1 := reverse list2;
                     >>;

                ind := for j:=0:degreeofDE-1 collect a(j);

                s := savesolve(terms,ind);
                if s = {} then NIL else
                << if symbolic !*traceFPS then write "Solution: ",s;
                        result := degreeofDE; Nmax := 0 >>;
            >>;

        degreeofDE := result;

        if Nmax = 0 then << if symbolic !*traceFPS then
                         write " successful search for DE">>
          else return -1;

        for each ss in first s do << ss := sub(a(degreeofDE)=1,ss);
                                        setze(lhs ss,rhs ss)>>;

% setting up the Differential equation

        on factor;
        deq := df(ff,x,degreeofDE) +
                 for j:=0:(degreeofDE-1) sum a(j)*df(ff,x,j);
        off factor;
        deq := num deq;
        return deq;
end;

put('FPS,'psopfn,'fpseval);

symbolic procedure FPSeval(u);
   begin scalar gens,res,!*factor,!*precise;
     if length u = 2 then
        << res := PSalg(car u,cadr u);
           if eq(res,-1) then return FPSexit(car u,cadr u,0);
        return sublis('((oddexpt . expt)(ba . a) (nn . k)),res)
        >>
        else if length u = 3 then
               << gens := gensym();
               res := PSalg(sublis(list( cadr u . gens),car u),gens);
               if eq(res,-1) then return FPSexit(car u,cadr u,caddr u);
               res := sublis('((oddexpt . expt)(ba . a) (nn . k)),res);
               res := subf(caadr res, list list(gens,'plus,cadr u,
                         list('minus,caddr u)));
               return mk!*sq res;
               >>
                else rederr("Wrong number of Arguments for FPS");
        end;

algebraic procedure AsymptPowerSeries (f,x);
  sub(x=1/x,fps(sub(x=1/x,f),x));

symbolic procedure FPSexit(a,b,z);
 << erfg!* := nil; list ('FPS,a,b, z) >>$

symbolic procedure simpledeexit(a,b,z);
 << erfg!* := nil; list ('simplede,a,b, z) >>$

algebraic procedure PSalg(f,x);

begin scalar cap_a,degree0fde,cap_f,j,cap_J,nnn,s,ind,deq,eqq,reqq,
        ak,terms,list1,list2,Nmax,cap_m,cap_R,ii,m,leadcoeff,m0,
        len,cap_S,result,parameters,solved,!*allfac,!*protfg;

        f := Recursionsimplify f;
        !*protfg := t;
        Nmax :=fps_search_depth;
        clear a;
        operator A; off allfac;
        depend ff,x;

        DFF(0) := f;

% special cases

        if PolynomQ(f,x) then return f;
        if type_ratpoly(f,x) then return ratalgo(f,x);

% start search for a simple DE

        clearrules special!*pochhammer!*rules;
        clearrules spec_factorial;
        clearrules spec_pochhammer;

        for degreeofDE:=1:Nmax do
            <<  DFF(degreeofDE) := df(DFF(degreeofDE-1),x);
                eqq :=  DFF(degreeofDE) +
                       for j:=0:(degreeofDE-1) sum A(j) * DFF(j);
                eqq := RecursionSimplify(eqq);
                eqq := num eqq;
                terms := {};
                list1 := converttolist (eqq,degreeofDE+1);
                while list1 neq {} do
                     << list2 := {}; j := fastpart(list1,1); cap_j := j;
                        len := fastlength list1;
                        for i:=2:len do
                                if type_ratpoly(j/fastpart(list1,i),x)
                                  then cap_J:= cap_J + fastpart(list1,i)
                                else list2 := fastpart(list1,i) . list2;
                        terms := cap_J . terms;
                        list1 := reverse list2;
                     >>;

                ind := for j:=0:degreeofDE-1 collect a(j);

                s := savesolve(terms,ind);
                if s = {} then NIL else
                << if symbolic !*traceFPS then write "Solution: ",s;
                        result := degreeofDE; Nmax := 0 >>;
            >>;

        degreeofDE := result;

        if Nmax = 0 then << if symbolic !*traceFPS then
                         write " successful search for DE">>
          else return -1;

        for each ss in first s do << ss := sub(a(degreeofDE)=1,ss);
                                        setze(lhs ss,rhs ss)>>;

% setting up the Differential equation

        on factor;
        deq := df(ff,x,degreeofDE) +
                 for j:=0:(degreeofDE-1) sum a(j)*df(ff,x,j);
        off factor;
        deq := num deq;
        if symbolic !*traceFPS
          then write("Differential equation is: ", deq);

% transforming into Recurrence equation

        factor ba;
        let subst_rules;
        req := pssubst(deq,x,ba,nn);
        clearrules subst_rules;

        if symbolic !*traceFPS
          then write("Recurrence equation is :",req);

        ind := {};
        for ii:=-50 : 50 do
           if not(coeffn(req,ba(nn+ii),1) =0) then ind := ii . ind;
        cap_M := first ind;
        if symbolic !*traceFPS then
          write(" M, ind, parameters : ",cap_M,",",ind,",", parameters);

        leadcoeff := num coeffn(req,ba(nn+cap_M),1);
        nnn := fastlength ind;

        let special!*pochhammer!*rules;
        let spec_factorial;
        let spec_pochhammer;

        result := 0;

        if (nnn = 1) then <<
                % functions with finite representation
                if symbolic !*traceFPS then
                  write
                     "fps with finite number of non-zero coefficients";
                cap_R := sub(nn=nn+(1-cap_M),- reduct(req,ba(nn+cap_M)))
                        /(sub(nn=nn+(1-cap_M),lcof(req,ba(nn+cap_M)))
                         * Ba(nn));
                leadcoeff:= sub(nn=nn+(1-cap_M),leadcoeff);
                result := constantRE(cap_R,leadcoeff,0,nn,x);
                if result = failed then result :=0;
                >>;

% try hypergeometric case

        if (nnn = 2) then <<
                m := abs(first ind  - second ind);
                cap_R := sub(nn=nn+(m-cap_M),- reduct(req,ba(nn+cap_M)))
                        /(sub(nn=nn+(m-cap_M),lcof(req,ba(nn+cap_M)))
                          * Ba(nn));
                leadcoeff:= sub(nn=nn+(m-cap_M),leadcoeff);
                result := hypergeomRE(m,cap_R,leadcoeff,0,nn,x)
                >>;

        if result =0 then
        <<
% test for constant coefficients

        terms := for j:=0:(degreeofDE-1) join
                         if freeof(a(j),x) then {} else {T};
        if terms = {} then <<
                req := ba(k+degreeofDE) +
                         for j:=0:(degreeofDE-1) sum ba(k+j)*a(j);
                if symbolic !*traceFPS  then
                 << write("DE has constant coefficients");
                    write("DE = ",deq);
                    write("RE = ",req);
                >>;
                s := 0;
                iii := 0;
                while freeof(req,ba(k + iii)) do <<
                        s := s + limit(dff(iii),x,0) * x^iii;
                        iii := iii + 1 >>;
                m0 := iii;
        if symbolic !*traceFPS  then write "i was found : ",iii;
        if m0 <= degreeofDE-1 then
        << s := solve_lin_rec(req,for i:=m0:(degreeofDE-1) collect
                                ba(i) = limit(dff(i),x,0));
           if symbolic !*traceFPS  then write("solution : ",s);
           s:=sub(n=nn,s);
           result := infsum(s/(factorial nn) *  x^nn,nn,0,infinity)
        >> else result := s;
       >>;
      >>;

   if result = 0 or not(freeof(result,failed)) then return (-1);
   lisp (erfg!* := nil);
   result:= result;
   let hgspec_pochhammer;
   result:= result;
   clearrules  hgspec_pochhammer;
   result := verbessere (result,nil);
   return result;
end;

flag ('(verbessere), 'opfn);

symbolic procedure verbessere (x,uu);
<< if eqcar (x,'plus) then 'plus .
               foreach xx in cdr x collect verbessere(xx , nil)
  else if not (eqcar (x,'infsum)) then x
  else
  <<
   if eqcar (x,'infsum) and eqcar(cadr x,'QUOTIENT) then
     x := list('infsum ,list('QUOTIENT,
                simplify_expt cadr cadr x,simplify_expt caddr cadr x));
       uu := cadr x;
   if eqcar (x,'infsum) and eqcar(cadr x,'QUOTIENT) then
      << uu := int_simplify_factorial auxcopy cadr x >>;
   list('infsum  , uu,'nn,0,'infinity)>> >>;

symbolic procedure  zerlege u;
 if fixp u and u>0 and  (u<10000 or !*ifactor)
     then <<
        u := zfactor u;
        for each j in u join for jj :=1:cdr j collect (car j) >>
     else list(u);

symbolic procedure simplify_expt u;
  begin scalar uu,exptlist,nonexptlist,asso,numb,expo;
  uu := u;
  if eqcar(u,'times) then u := cdr u;
  while u do
      << if pairp car u and (eq (caar u,'expt)  or eq (caar u,'sqrt))
                then <<
                if numberp cadar u then numb := zerlege (cadar u)
                                else numb := list cadar u;
                expo := if eq (caar u,'sqrt) then '((quotient 1 2))
                                else cddar u;
                while numb do <<
                        if asso:= atsoc (car numb,exptlist) then
                         exptlist := (car numb .
                                list list('plus,car expo,cadr asso)) .
                                        delasc (car numb,exptlist)
                        else
                        exptlist := ((car numb) . expo) . exptlist ;
                        numb := cdr numb;
                             >>;
                     >>
         else if and(idp car u,asso := atsoc (car u,exptlist)) then
                << exptlist := (car u .
                                list list('plus,1,cadr asso)) .
                                        delasc (car u,exptlist) >>
         else nonexptlist := (car u) . nonexptlist;
        u := cdr u;
       >>;
  if null exptlist then return uu;

  for each x in exptlist do
         nonexptlist :=  ('oddexpt . x ) . nonexptlist;
  return (car uu) . nonexptlist;
  end;

fluid ('(rsolve!*!*));

algebraic procedure hypergeomRE(m,cap_R,leadcoeff,dffpointer,k,x);

% solve the hypergeometric Recurrence Equation
%
%   a(k+m) = cap_R(k) * a(k)
%
%  where leadcoeff is the leading coefficient of the RE
%  and DF is a table where DF(dffpointer+i) = df(f,x,i)

  begin scalar denr,fract,ii,m0,m1,c0,ck,S,c,df2,q,r2,lterm,nn,
        s0, leadcoeff2;
        denr := solve(leadcoeff,k);
        m0 := {};
        foreach xx in denr do
                if type_rational rhs xx then  m0 := ((rhs xx)+1) . m0;
        if not(m0 = {}) then m0 := max(m0) else m0 := 0;

        if symbolic !*traceFPS then
                 << write "RE is of hypergeometric type";
                    write "Symmetry number mm := ",m;
                    write "RE: for all k >= ",m0,": a (k + ",m,") = "
                        ,cap_R * a(k);
                    write "leadcoeff := ",leadcoeff; >>;

        fract := {};
        foreach xx in denr do
                if type_fraction(rhs xx)
                  then fract := den(rhs xx) . fract;
        if not(fract = {}) then
                << q := first fract;
                   dff(dffpointer + 10) := sub(x=x^q,dff(dffpointer));
                   if symbolic !*traceFPS then <<
                        write "RE modified to nn= ",k/q;
                        write "=> f := ",dff(dffpointer + 10)>>;
                   S := hypergeomRE(q*m,sub(k=k/q,cap_R),
                        sub(k=k/q,leadcoeff),dffpointer + 10,k,x);
                   return sub(x=x^(1/q),S); >>;

        if m0 < 0  then <<
                nn:= -m0 + remainder(-m0,m);
                dff(dffpointer + 10) := df2 := x^nn * dff(dffpointer);
                if symbolic !*traceFPS then <<
                        write "working with ",x^nn,"*f";
                        write "=> f :=" ,df2 >>;
                S := hypergeomRE(m,sub(k=k-nn,cap_R),
                                   sub(k=k-nn,leadcoeff),
                                   dffpointer + 10,k,x);
                return update_coeff(S,x,-nn) >>;

        if m0 > 0  then <<
         m1 := {};
         foreach xx in denr do if type_rational rhs xx then
                         m1 := append(list (rhs xx +1),m1);
         m1 := min m1;
         if m1 > 0 then <<
                dff(dffpointer + 10) := df2 := x^(-m1)*dff(dffpointer);
                if symbolic !*traceFPS then <<
                        write"a(k) = 0 for k < ",m1;
                        write "working with ",x^(-m1),"*f";
                        write "=> f :=" ,df2 >>;
                S := hypergeomRE(m,sub(k=k+m1,cap_R),
                                   sub(k=k+m1,leadcoeff),
                                        dffpointer + 10,k,x);
                return update_coeff(S,x,m1);
                        >> >>;

     % logarithmic singularity Baustelle
        If lisp pairp
                 errorset!*(list ('simptaylor,mkquote list(
                                        mkquote list('dff,dffpointer),
                                        mkquote x,0,1)),nil) then
        <<
        lterm := num taylortostandard(taylor(dff(dffpointer),x,0,1));
        nn := 0;
        if lisp(if  member('(log x) ,kernels !*q2f simp lterm)
                         then t else nil) % Comments?
                then <<
                        dff(dffpointer + 10):=dff(dffpointer) -lterm;
                        if symbolic !*traceFPS then
                                write "=> f :=",dff(dffpointer + 10);
                        S := hypergeomRE(m, R, leadcoeff*(k-nn),
                                dffpointer + 10,k,x);
                        RETURN(lterm+S);
                     >>;
        >>;

        S := 0; S0 := 0;
        for i:=0:(m0+m-1) do <<
           if i > 0 then
             dff(dffpointer + i) := df(dff(dffpointer + i-1),x);
           c0 := limit(dff(dffpointer + i),x,0);

           if (lisp listp reval c0 and fastpart(c0,0) = limit) then
                <<  if symbolic !*traceFPS
                        then write "Could not find the limit of: "
                        ,dff(dffpointer + i),",",x,",",0;
                    rederr("Problem using limit operator") >> else
                <<
                c0 := c0/factorial (i);
                if symbolic !*traceFPS then write " a(",i,") = ",c0;
                if not (c0 =0) then
                     << s0 := s0+c0*x^i;
                        if i < m0 then S := S + c0 * x^i % single terms
                        else
                     << ck := hypergeomRsolve(sub(k=M*k+i,cap_R),k,c0);
                        if symbolic !*traceFPS then write " ck = ",ck;
                        c :=1;
                        ck := ck/C;
                        let hgspec_pochhammer;
                        ck := ck;
                        clearrules hgspec_pochhammer;
                        if ck = 0 then S := S + c0*x^i else
                         if Rsolve!*!* = finite then S := S +
                                C*sum(ck*x^(m*k+i), k)
                         else
                         S := S + C * infsum(ck*x^(m*k+i)) ;
                        if symbolic !*traceFPS then write " S = ",s;
                >> >> >> >>;
  return (S);
end;

algebraic << let INFSUM(0) = 0>>;

% some compatibility functions for Maple sources.

symbolic flag('(savesolve type_fraction type_rational),'opfn);

algebraic procedure converttolist (express,len);
 <<
  len := fastlength express;
  for i:=1:len collect fastpart(express , i)>>;

symbolic procedure type_fraction (num);
  (if pairp num1 and fixp car num1 and fixp cdr num1
        and not onep cdr num1
   then num else nil) where num1 := simp num;

symbolic procedure type_rational(num);
 (if pairp num1 and (fixp car num1 or null car num1) and fixp cdr num1
   then t else nil) where num1 := simp num;

algebraic procedure type_ratpoly(exprn,var);
  if (PolynomQ (den exprn,var) and PolynomQ (num exprn,var))
    then t else nil;

symbolic procedure savesolve (x,y);

begin scalar !*cramer;
on cramer; % this is a temporary fix for solve !!
           % check with  fps(sin(x)^2 * cos(x)^2,x); !!
return
<< switch solveinconsistent;
   on solveinconsistent;
   inconsistent!* := NIL;
   if pairp (x := errorset!*(list ('solveeval,mkquote list(x,y)),nil))
        and not inconsistent!*
        then << x :=car x;
                if x = '(list) then x else
                if eqcar(cadr x,'equal) then % one element solution
                        list('list,x) else x>>
        else list('list) >>;
end;

algebraic procedure setze(x,y);
  let x = y;

symbolic procedure PolynomQ (x,var);

 if not fixp denr simp x then NIL else
 begin scalar kerns,kern;

 kerns := kernels !*q2f simp x;

 aa: if null kerns then return T;
     kern := first kerns;
     kerns := cdr kerns;
     if not(eq (kern, var)) and depends(kern,var)
                then return NIL else go aa;
end;

flag('(PolynomQ),'opfn);

flag ('(PolynomQ type_ratpoly),'boolean);


algebraic << operator update_coeff;

update_coeff_rules := {

 update_coeff (~a + ~b,~x,~m) => update_coeff(a,~x,~m)
                                + update_coeff(b,~x,~m),
 update_coeff (~c * ~a,~x,~m) => c * update_coeff(a,~x,~m)
        when freeof(c,x),
 update_coeff ( -  ~a,~x,~m) =>  - update_coeff(a,~x,~m),
 update_coeff (~a/~c,~x,~m) => update_coeff(a,~x,~m) /c
        when freeof(c,x) and c neq 1,
 update_coeff (~x,~x,~m) => x^(m + 1),
 update_coeff (~c,~x,~m) => c * x^m  when freeof(c,x),
 update_coeff (infsum(~xx),~x,~m) => infsum update_coeff(xx,x,m),
 update_coeff (~x^~j*~xx,~x,~m) => x^(j + m + 1)when x = xx,
 update_coeff (~x^~j*~xx^~jj,~x,~m) => x^(j + jj + m) when x = xx,
 update_coeff (~x^~j,~x,~m) => x^(j + m)}$

let update_coeff_rules >>$

endmodule;

end;




