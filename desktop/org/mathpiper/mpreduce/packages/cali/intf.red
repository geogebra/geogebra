module intf;

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

            #####################################
            ###                               ###
            ###  INTERFACE TO ALGEBRAIC MODE  ###
            ###                               ###
            #####################################


  There are two types of procedures :

  The first type takes polynomial lists or polynomial matrices as
  input, converts them into dpmats, computes the result and
  reconverts it to algebraic mode.

  The second type is property driven, i.e. Basis, Gbasis, Syzygies
  etc. are attached via properties to an identifier.
  For them, the 'ring property watches, that cali!=basering hasn't
  changed (including the term order). Otherwise the results must be
  reevaluated using setideal(name,name) or setmodule(name,name) since
  otherwise results may become wrong.

   The switch "noetherian" controls whether the term order satisfies
   the chain condition (default is "on") and chooses either the
   groebner algorithm or the local standard basis algorithm.

END COMMENT;

% ----- The properties managed upto now ---------

fluid '(intf!=properties);

intf!=properties:='(basis ring gbasis syzygies resolution hs
                        independentsets);

% --- Some useful common symbolic procedures --------------

symbolic procedure intf!=clean u;
% Removes all properties.
  for each x in intf!=properties do remprop(u,x);

symbolic procedure intf_test m;
  if (length m neq 1)or(not idp car m) then typerr(m,"identifier");

symbolic procedure intf_get m;
% Get the 'basis.
  begin scalar c;
  if not (c:=get(m,'basis)) then typerr(m,"dpmat variable");
  if not equal(get(m,'ring),cali!=basering) then
                rederr"invalid base ring";
  cali!=degrees:=dpmat_coldegs c;
  return c;
  end;

symbolic procedure intf!=set(m,v);
% Attach the dpmat value v to the variable m.
  << put(m,'ring,cali!=basering);
     put(m,'basis,v);
     if dpmat_cols v = 0 then
       << put(m,'rtype,'list); put(m,'avalue,'list.{dpmat_2a v})>>
     else
       <<put(m,'rtype,'matrix); put(m,'avalue,'matrix.{dpmat_2a v})>>;
  >>;

% ------ setideal -------------------

put('setideal,'psopfn,'intf!=setideal);
symbolic procedure intf!=setideal u;
% setideal(name,base list)
  begin scalar l;
  if length u neq 2 then rederr "Syntax : setideal(identifier,ideal)";
  if not idp car u then typerr(car u,"ideal name");
  l:=reval cadr u;
  if not eqcar(l,'list) then typerr(l,"ideal basis");
  intf!=clean(car u);
  put(car u,'ring,cali!=basering);
  put(car u,'basis,l:=dpmat_from_a l);
  put(car u,'avalue,'list.{l:=dpmat_2a l});
  put(car u,'rtype,'list);
  return l;
  end;

% --------------- setmodule -----------------------

put('setmodule,'psopfn,'intf!=setmodule);
symbolic procedure intf!=setmodule u;
% setmodule(name,matrix)
  begin scalar l;
  if length u neq 2 then
        rederr "Syntax : setmodule(identifier,module basis)";
  if not idp car u then typerr(car u,"module name");
  l:=reval cadr u;
  if not eqcar(l,'mat) then typerr(l,"module basis");
  intf!=clean(car u);
  put(car u,'ring,cali!=basering);
  put(car u,'basis,dpmat_from_a l);
  put(car u,'avalue,'matrix.{l});
  put(car u,'rtype,'matrix);
  return l;
  end;

% ------------ setring ------------------------

put('setring,'psopfn,'intf!=setring);
% Setring(vars,term order degrees,tag <,ecart>) sets the internal
% variable cali!=basering. The term order is at first by the degrees
% and then by the tag. The tag must be LEX or REVLEX.
% If ecart is not supplied the ecart is set to the default, i.e. the
% first degree vector (noetherian degree order) or to (1 1 .. 1).
% The ring may also be supplied as a list of its arguments as e.g.
% output by "getring".
symbolic procedure intf!=setring u;
  begin
  if length u = 1 then u:=cdr reval car u;
  if not(length u member '(3 4)) then
    rederr "Syntax : setring(vars,term order,tag[,ecart])";
  setring!* ring_from_a ('list . u);
  return ring_2a cali!=basering;
  end;

% ----------- getring --------------------

put('getring,'psopfn,'intf!=getring);
% Get the base ring of an object as the algebraic list
% {vars,tord,tag,ecart}.

symbolic procedure intf!=getring u;
  if null u then ring_2a cali!=basering
  else begin scalar c; c:=get(car u,'ring);
    if null c then typerr(car u,"dpmat variable");
    return ring_2a c;
    end;


% ------- The algebraic interface -------------

symbolic operator ideal2mat;
symbolic procedure ideal2mat m;
% Convert the list of polynomials m into a matrix column.
  if !*mode='symbolic then rederr"only for algebraic mode"
  else if not eqcar(m,'list) then typerr(m,'list)
  else 'mat . for each x in cdr m collect {x};

symbolic operator mat2list;
symbolic procedure mat2list m;
% Flatten the matrix m.
  if !*mode='symbolic then rederr"only for algebraic mode"
  else if not eqcar(m,'mat) then typerr(m,'matrix)
  else 'list . for each x in cdr m join for each y in x collect y;

put('setgbasis,'psopfn,'intf!=setgbasis);
symbolic procedure intf!=setgbasis m;
% Say that the basis is already a Gbasis.
  begin scalar c;
  intf_test m; m:=car m; c:=intf_get m;
  put(m,'gbasis,c);
  return reval m;
  end;

symbolic operator setdegrees;
symbolic procedure setdegrees m;
% Set a term list as actual column degrees. Execute this before
% setmodule to supply a module with prescribed column degrees.
  if !*mode='symbolic then rederr"only for algebraic mode"
  else begin scalar i,b;
  b:=moid_from_a reval m; i:=0;
  cali!=degrees:= for each x in b collect <<i:=i+1; i . x>>;
  return moid_2a for each x in cali!=degrees collect cdr x;
  end;

put('getdegrees,'psopfn,'intf!=getdegrees);
symbolic procedure intf!=getdegrees m;
  begin
  if m then <<intf_test m; intf_get car m>>;
  return moid_2a for each x in cali!=degrees collect cdr x
  end;

symbolic operator getecart;
symbolic procedure getecart;
  if !*mode='algebraic then makelist ring_ecart cali!=basering
  else ring_ecart cali!=basering;

put('gbasis,'psopfn,'intf!=gbasis);
symbolic procedure intf!=gbasis m;
  begin scalar c,c1;
  intf_test m; m:=car m; c1:=intf_get m;
  if (c:=get(m,'gbasis)) then return dpmat_2a c;
  c:=gbasis!* c1;
  put(m,'gbasis,c);
  return dpmat_2a c;
  end;

symbolic operator setmonset;
symbolic procedure setmonset m;
  if !*mode='algebraic then makelist setmonset!* cdr reval m
  else setmonset!* m;

symbolic procedure setmonset!* m;
  if subsetp(m,ring_names cali!=basering) then cali!=monset:=m
  else typerr(m,"monset list");

symbolic operator getmonset;
symbolic procedure getmonset(); makelist cali!=monset;

put('resolve,'psopfn,'intf!=resolve);
symbolic procedure intf!=resolve m;
  begin scalar c,c1,d;
  intf_test m; if length m=2 then d:=reval cadr m else d:=10;
  m:=car m; c1:=intf_get m;
  if ((c:=get(m,'resolution)) and (car c >= d)) then
        return makelist for each x in cdr c collect dpmat_2a x;
  c:=Resolve!*(c1,d);
  put(m,'resolution,d.c);
  if not get(m,'syzygies) then put(m,'syzygies,cadr c);
  return makelist for each x in c collect dpmat_2a x;
  end;

put('syzygies,'psopfn,'intf!=syzygies);
symbolic procedure intf!=syzygies m;
  begin scalar c,c1;
  intf_test m; m:=car m; c1:=intf_get m;
  if (c:=get(m,'syzygies)) then return dpmat_2a c;
  c:=syzygies!* c1;
  put(m,'syzygies,c);
  return dpmat_2a c;
  end;

put('indepvarsets,'psopfn,'intf!=indepvarsets);
symbolic procedure intf!=indepvarsets m;
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if (c:=get(m,'independentsets)) then
    return makelist for each x in c collect makelist x;
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  c:=indepvarsets!* c;
  put(m,'independentsets,c);
  return makelist for each x in c collect makelist x;
  end;

put('getleadterms,'psopfn,'intf_getleadterms);
symbolic procedure intf_getleadterms m;
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if not (c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  c:=getleadterms!* c;
  return dpmat_2a c;
  end;

put('hilbertseries,'psopfn,'intf!=hilbertseries);
symbolic procedure intf!=hilbertseries m;
% Returns the Hilbert series of m.
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if (c:=get(m,'hs)) then return mk!*sq c;
  if not(c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  put(m,'hs,c:=hilbertseries!* c);
  return mk!*sq c;
  end;

put('degree,'psopfn,'intf_getmult);
symbolic procedure intf_getmult m;
% Returns the multiplicity of m.
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if (c:=get(m,'hs)) then return hf_mult c;
  if not(c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  put(m,'hs,c:=hilbertseries!* c);
  return hf_mult c;
  end;

put('dim,'psopfn,'intf!=dim);
put('codim,'psopfn,'intf!=codim);
symbolic procedure intf!=dim m;
% Returns the dimension of coker m.
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if (c:=get(m,'hs)) then return hf_dim c;
  if (c:=get(m,'independentsets)) then return length moid_max c;
  if not(c:=get(m,'gbasis)) then
        put(m,'gbasis,c:=gbasis!* get(m,'basis));
  c:=indepvarsets!* c; put(m,'independentsets,c);
  return length moid_max c;
  end;

symbolic procedure intf!=codim m;
% Returns the codimension of coker m.
  length ring_names cali!=basering - intf!=dim m;

put('BettiNumbers,'psopfn,'intf!=BettiNumbers);
symbolic procedure intf!=BettiNumbers m;
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if (c:=get(m,'resolution)) then return makelist BettiNumbers!* cdr c
  else rederr"Compute a resolution first";
  end;

put('GradedBettiNumbers,'psopfn,'intf!=GradedBettiNumbers);
symbolic procedure intf!=GradedBettiNumbers m;
  begin scalar c;
  intf_test m; m:=car m; intf_get m;
  if (c:=get(m,'resolution)) then return
    makelist for each x in GradedBettiNumbers!* cdr c collect makelist x
  else rederr"Compute a resolution first";
  end;

put('degsfromresolution,'psopfn,'intf!=degsfromresolution);
symbolic procedure intf!=degsfromresolution m;
  begin scalar c;
  intf_test m; m:=car m;
  if not equal(get(m,'ring),cali!=basering) then
        rederr"invalid base ring";
  if not (c:=get(m,'resolution)) then
        rederr"compute a resolution first";
  return makelist for each x in cdr c collect
            moid_2a for each y in dpmat_coldegs x collect cdr y;
  end;

symbolic operator sieve;
symbolic procedure sieve(m,vars);
% Sieve out all base elements from m containing one of the variables
% in vars in their leading term.
  if !*mode='algebraic then
        dpmat_2a dpmat_sieve(dpmat_from_a reval m,cdr vars,nil)
  else dpmat_sieve(m,vars,nil);

endmodule; % intf

end;
