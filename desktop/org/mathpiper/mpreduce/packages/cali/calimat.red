module calimat;

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


Comment

                #######################
                #                     #
                #  MATRIX SUPPLEMENT  #
                #                     #
                #######################

Supplement to the REDUCE matrix package.
Matrices are transformed into nested lists of s.q.'s.

end comment;

% ------ The Jacobian matrix -------------

symbolic operator matjac;
symbolic procedure matjac(m,l);
% Returns the Jacobian matrix from the ideal m in prefix form
% (given as an algebraic mode list) with respect to the var. list l.
   if not eqcar(m,'list) then typerr(m,"ideal basis")
   else if not eqcar(l,'list) then typerr(l,"variable list")
   else 'mat . for each x in cdr l collect
        for each y in cdr m collect prepsq difff(numr simp reval y,x);

% ---------- Random linear forms -------------

symbolic operator random_linear_form;
symbolic procedure random_linear_form(vars,bound);
% Returns a random linear form in algebraic prefix form.
  if not eqcar(vars,'list) then typerr(vars,"variable list")
  else 'plus . for each x in cdr vars collect
        {'times,random(2*bound)-bound,x};

% ----- Singular locus -----------

symbolic operator singular_locus;
symbolic procedure singular_locus(m,c);
  if !*mode='algebraic then
        (if not numberp c then
        rederr"Syntax : singular_locus(polynomial list, codimension)"
        else dpmat_2a singular_locus!*(m,c))
  else singular_locus!*(m,c);

symbolic procedure singular_locus!*(m,c);
% m must be a complete intersection of codimension c given as a list
% of polynomials in prefix form. Returns the singular locus computing
% the corresponding jacobian.
  matsum!* {dpmat_from_a m, mat2list!* dpmat_from_a
        minors(matjac(m,makelist ring_names cali!=basering),c)};

% ------------- Minors --------------

symbolic operator minors;
symbolic procedure minors(m,k);
% Returns the matrix of k-minors of the matrix m.
  if not eqcar(m,'mat) then typerr(m,"matrix")
  else begin scalar r,c;
  m:=for each x in cdr m collect for each y in x collect simp y;
  r:=cali_choose(for i:=1:length m collect i,k);
  c:=cali_choose(for i:=1:length car m collect i,k);
  return 'mat . for each x in r collect for each y in c collect
        mk!*sq detq calimat!=submat(m,x,y);
  end;

symbolic operator ideal_of_minors;
symbolic procedure ideal_of_minors(m,k);
% The ideal of the k-minors of the matrix m.
  if !*mode='algebraic then dpmat_2a ideal_of_minors!*(m,k)
  else ideal_of_minors!*(m,k);

symbolic procedure ideal_of_minors!*(m,k);
  if not eqcar(m,'mat) then typerr(m,"matrix") else
  interreduce!* mat2list!* dpmat_from_a minors(m,k);

symbolic procedure calimat!=submat(m,x,y);
  for each a in x collect for each b in y collect nth(nth(m,a),b);

symbolic procedure calimat!=sum(a,b);
  for each x in pair(a,b) collect
  for each y in pair(car x,cdr x) collect addsq(car y,cdr y);

symbolic procedure calimat!=neg a;
  for each x in a collect for each y in x collect negsq y;

symbolic procedure calimat!=tp a;
  tp1 append(a,nil); % since tp1 is destructive.

symbolic procedure calimat!=zero!? a;
  begin scalar b; b:=t;
  for each x in a do for each y in x do b:=b and null car y;
  return b;
  end;

% -------------- Pfaffians ---------------

symbolic procedure calimat!=skewsymmetric!? m;
  calimat!=zero!? calimat!=sum(m,calimat!=tp m);

symbolic operator pfaffian;
symbolic procedure pfaffian m;
% The pfaffian of a skewsymmetric matrix m.
  if not eqcar(m,'mat) then typerr(m,"matrix") else
  begin scalar m1;
  m1:=for each x in cdr m collect for each y in x collect simp y;
  if not calimat!=skewsymmetric!? m1
    then typerr(m,"skewsymmetic matrix");
  return mk!*sq calimat!=pfaff m1;
  end;

symbolic procedure calimat!=pfaff m;
  if length m=1 then nil . 1
  else if length m=2 then cadar m
  else begin scalar a,b,p,c,d,ind,sgn;
      b:=for each x in cdr m collect cdr x;
      a:=cdar m; ind:=for i:=1:length a collect i;
      p:=nil . 1;
      for i:=1:length a do
      << c:=delete(i,ind); d:=calimat!=pfaff calimat!=submat(b,c,c);
         if sgn then d:=negsq d; sgn:=not sgn;
         p:=addsq(p,multsq(nth(a,i),d));
      >>;
      return p;
      end;

symbolic operator ideal_of_pfaffians;
symbolic procedure ideal_of_pfaffians(m,k);
% The ideal of the 2k-pfaffians of the skewsymmetric matrix m.
  if !*mode='algebraic then dpmat_2a ideal_of_pfaffians!*(m,k)
  else ideal_of_pfaffians!*(m,k);

symbolic procedure ideal_of_pfaffians!*(m,k);
% The same, but for a dpmat m.
  if not eqcar(m,'mat) then typerr(m,"matrix") else
  begin scalar m1,u;
  m1:=for each x in cdr m collect for each y in x collect simp y;
  if not calimat!=skewsymmetric!? m1
    then typerr(m,"skewsymmetic matrix");
  u:=cali_choose(for i:=1:length m1 collect i,2*k);
  return interreduce!* dpmat_from_a makelist
        for each x in u collect
                prepsq calimat!=pfaff calimat!=submat(m1,x,x);
  end;

endmodule; % calimat

end;
