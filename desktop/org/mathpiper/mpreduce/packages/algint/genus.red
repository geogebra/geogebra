module genus;

% Author: James H. Davenport.

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


fluid '(!*galois
        !*tra
        !*trmin
        gaussiani
        intvar
        listofallsqrts
        listofnewsqrts
        nestedsqrts
        previousbasis
        sqrt!-intvar
        sqrt!-places!-alist
        sqrtflag
        sqrts!-in!-integrand
        taylorasslist
        taylorvariable);

symbolic procedure simpgenus u;
begin
  scalar intvar,sqrt!-intvar,taylorvariable,taylorasslist;
  scalar listofnewsqrts,listofallsqrts,sqrt!-places!-alist;
  scalar sqrtflag,sqrts!-in!-integrand,tt,u,simpfn;
  tt:=readclock();
  sqrtflag:=t;
  taylorvariable:=intvar:=car u;
  simpfn:=get('sqrt,'simpfn);
  put('sqrt,'simpfn,'proper!-simpsqrt);
  sqrt!-intvar:=mvar !*q2f simpsqrti intvar;
  listofnewsqrts:= list mvar gaussiani; % Initialise the SQRT world.
  listofallsqrts:= list (argof mvar gaussiani . gaussiani);
  u:=for each v in cdr u
            collect simp!* v;
  sqrts!-in!-integrand:=sqrtsinsql(u,intvar);
  u:=!*n2sq length differentials!-1 sqrts!-in!-integrand;
  put('sqrt,'simpfn,simpfn);
  printc list('time,'taken,readclock()-tt,'milliseconds);
  return u
  end;
put('genus,'simpfn,'simpgenus);

symbolic procedure !*n2sq(u1);
if u1=0 then nil . 1 else u1 . 1;

symbolic procedure differentials!-1 sqrtl;
begin
  scalar asqrtl,faclist,places,v,nestedsqrts,basis,
         u,n,hard!-ones,sqrts!-in!-problem;
    % HARD!-ONES  A list of all the factors of our equations which do
    % not factor, and therefore such that we can divide the whole of
    % our INTBASIS by their product in order to get the true INTBASIS,
    % since these ones can cause no complications.
  asqrtl:=for each u in sqrtl
            collect !*q2f simp argof u;
  if !*tra or !*trmin then <<
    printc
      "Find the differentials of the first kind on curve defined by:";
    mapc(asqrtl,function printsf) >>;
  for each s in asqrtl do <<
    faclist:=for each u in jfactor(s,intvar)
               collect numr u;
    if !*tra then <<
      princ intvar;
      printc " is not a local variable at the roots of:";
      mapc(faclist,function printsf) >>;
    for each uu in faclist do <<
      v:=stt(uu,intvar);
      if 1 neq car v
        then hard!-ones:=uu.hard!-ones
        else <<
          u:=addf(uu,(mksp(intvar,1) .* (negf cdr v)) .+ nil) ./ cdr v;
          % U is now the value at which this SQRT has a zero.
          u:=list(list(intvar,'difference,intvar,prepsq u),
                  list(intvar,'expt,intvar,2));
          for each w in sqrtsign(for each w in union(delete(s,asqrtl),
                                                     delete(uu,faclist))
         conc sqrtsinsq(simpsqrtsq
      multsq(substitutesq(w ./ 1,u),
      1 ./ !*p2f mksp(intvar,2)),
                                      intvar),
                                 intvar)
            do places:=append(u,w).places >> >> >>;
  sqrts!-in!-problem:=nconc(for each u in hard!-ones
                              collect list(intvar.intvar,
                                    (lambda u;u.u) list('sqrt,prepf u)),
                            places);
  basis:=makeinitialbasis sqrts!-in!-problem;
                  % Bodge in any extra SQRTS that we will require later.
%  u:=1 ./ mapply(function multf,
%                for each u in sqrtl collect !*kk2f u);
%  basis:=for each v in basis collect multsq(u,v);
  basis:=integralbasis(mkvec basis,places,mkilist(places,-1),intvar);
  if not !*galois
    then basis:=combine!-sqrts(basis,
                               getsqrtsfromplaces sqrts!-in!-problem);
  if hard!-ones
    then <<
      v:=upbv basis;
      u:=1;
      for each v in hard!-ones do
        u:=multf(u,!*kk2f list('sqrt,prepf v));
      hard!-ones:=1 ./ u;
      for i:=0:v do
        putv(basis,i,multsq(getv(basis,i),hard!-ones)) >>;
  if not !*galois
    then basis:=modify!-sqrts(basis,sqrtl);
  v:=fractional!-degree!-at!-infinity sqrtl;
  if v iequal 1
    then n:=2
    else n:=2*v-1;
    % N  is the degree of the zero we need at INFINITY.
  basis:=normalbasis(basis,intvar,n);
  previousbasis:=nil;
    % it might have been set before, and we have changed its meaning.
  if !*tra or !*trmin then <<
    printc "Differentials are:";
    mapc(basis,function printsq) >>;
  return basis;
  end;

endmodule;

end;
