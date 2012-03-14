module matrices;

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


% Almost generic definition (i.e. independent of datastructure of
% elements). With this it should be possible to have matrices
% inside matrices.

symbolic procedure mkn_matrix u;
   if length u neq 2 then rederr "Incorrect matrix arguments"
    else nlist(nlist(mkobject(0,'zero),cadr u),car u);

remprop('matrix,'stat);

ranks print u : {matrix} -> matrix;

symbolic procedure print_matrix u;
   matpri u;

put('matrix,'stat,'rlis);

put('matrix,'n_formfn,'n_formmatr);

symbolic procedure n_formmatr(u,vars);
   n_formstructure(u,vars,'matrix);

put('matrix,'getfn,'getell);

put('matrix,'putfn,'setell);

endmodule;

end;

symbolic procedure matprixxx(u,v); matpri1(u,v,nil);

% SYMBOLIC PROCEDURE nFORMMAT(U,VARS,MODE);
%    'LIST . MKQUOTE '!*MATrix!* . list('list .
%       FOR EACH X IN CDR U COLLECT
%            ('LIST . nFORMLIS(X,VARS,MODE)));

% PUT('MAT,'nFORMFN,'nFORMMAT);

%symbolic procedure nformmatrix(u,vars,mode);
%    if mode eq 'symbolic then rederr "no symbolic matrices supported"
%     else list('matrixfn,'list . for each j in cdr u collect
%                             <<if atom j then put(j,'rtype,'mat)
%                                else put(car j,'rtype,'mat);
%                               if null atom j and cdddr j
%                                   then rederr "wrong number of indices";
%                                if atom j then mkquote j
%                                 else mkmatel(j,vars,mode)>>);

% SYMBOLIC PROCEDURE MATRIXfn U;
%    for each j in u do
%     if null atom j then
%        begin scalar v,w;
%              v := cdr j;
%              FOR N := 1:CAR V DO W := NtaggedZERO CADR V . W;
%              return PUT(CAR J,'matVALUE,list('!*matrix!*,W))
%        end;

   %declares list U as matrices;
%  BEGIN SCALAR V,W,X;
%       FOR EACH J IN U DO
%       IF ATOM J THEN IF NULL (X := rTYPE J)
%                            THEN PUT(J,'RTYPE,'MAT)
%                           ELSE IF X EQ 'MAT
%                THEN <<LPRIM LIST(X,J,"redefined");
%                                   PUT(J,'RTYPE,'MAT)>>
%               ELSE TYPERR(LIST(X,J),"matrix")
%            ELSE IF NOT IDP CAR J
%             THEN ERRPRI2(J,'HOLD)
%        ELSE IF NOT (X := rTYPE CAR J) OR X EQ 'MAT
%             THEN <<v := nformmatel(j,vars,mode);
%                    v := list(lispeval cadadr v,lispeval car cdaddr v);
%                    W := NIL;
%                    FOR N := 1:CAR V DO W := NtaggedZERO CADR V . W;
%                    PUT(CAR J,'RTYPE,'MAT);
%            PUT(CAR J,'matVALUE,'!*matrix!* . W)>>
%        ELSE TYPERR(LIST(X,CAR J),"matrix")
%  END;

% SYMBOLIC PROCEDURE NtaggedZERO N;
%    % Returns a list of N zeros.
%    IF N=0 THEN NIL ELSE list('!*integer!*,0) . NtaggedZERO(N-1);

% put('matrix,'nformfn,'nformmatrix);


%The *matrix* structure is no yet given an environment field.

% symbolic procedure !*matrix!* u;
%    begin
%      if atom u then return get(u,'matvalue);
%      return mateval list('!*matrix!*,u)
%    end;

% put('mat,'generic,'!*matrix!*);

% FLAG('(MAT),'STRUCT);      % for parsing

% PUT('MAT,'RTYPEFN,'(LAMBDA (X) 'MAT));

% PUT('!*MATrix!*,'RTYPEFN,'(LAMBDA (X) 'MAT));

put('!*matrix!*,'rtype,'mat);


symbolic procedure !*matrixplus2!*(u,v);
   list('!*matrix!*,for each j in
                        addm1(rvalue u,rvalue v,function cons)
                      collect addm1(car j,cdr j,
                                    function !*plus2!*));

symbolic procedure !*matrixdifference!*(u,v);
   list('!*matrix!*,for each j in
                        addm1(rvalue u,rvalue v,function cons)
                      collect addm1(car j,cdr j,
                                    function !*difference!*));

SYMBOLIC PROCEDURE ADDM(U,V);
   %returns sum of two matrix canonical forms U and V;
   FOR EACH J IN ADDM1(U,V,FUNCTION CONS)
      COLLECT ADDM1(CAR J,CDR J,FUNCTION ADDSQ);

SYMBOLIC PROCEDURE ADDM1(U,V,W);
   IF NULL U AND NULL V THEN NIL
    ELSE IF NULL U OR NULL V THEN REDERR "Matrix mismatch"
    ELSE APPLY(W,LIST(CAR U,CAR V)) . ADDM1(CDR U,CDR V,W);

symbolic procedure !*matrixtimes2!*(u,v);
   list('!*matrix!*,for each j in rvalue u collect
                      for each k in x collect scalprod(j,k))
    where x = tp1 rvalue v;

symbolic procedure scalprod(u,v);
   if null u and null v then '(!*integer!* 0)
    else if null u or null v then rederr "Matrix missmatch"
    else !*plus2!*(!*times2!*(car u,car v),
                   scalprod(cdr u,cdr v));

symbolic procedure !*scalar!*matrix!*(u,v);
   list('!*matrix!*,for each j in rvalue v collect
                      for each k in j collect !*times2!*(u,k));

symbolic procedure !*matrixexptn!*(u,n);
   %for now.
   if n < 0 then !*matrixexptn!*(mk!*matrix!* matinv mat2q u,-n)
    else begin scalar x,y;
           x := u;
           y := mateval u;
           while (n := n - 1) > 0 do
              x := !*matrixtimes2!*(mateval x,y);
           return x
         end;

% symbolic procedure !*dfmat!*(u,v);
%    list('!*matrix!*,for each j in rvalue mateval u collect
%                        for each k in j collect
%                             processdf list(k,v));

add!-generic!-table!-entry('difference,'(!*matrix!* !*matrix!*),
          '(lambda (x1 x2)
            (!*matrixdifference!* (mateval x1) (mateval x2))));

add!-generic!-table!-entry('expt,'(!*matrix!* !*integer!*),
          '(lambda (x1 x2)
            (!*matrixexptn!* x1 (rvalue x2))));

add!-generic!-table!-entry('minus,'(!*matrix!*),
          '(lambda (x1)
            (!*scalar!*matrix!* '(!*integer!* -1) (mateval x1))));

add!-generic!-table!-entry('plus2,'(!*matrix!* !*matrix!*),
          '(lambda (x1 x2)
            (!*matrixplus2!* (mateval x1) (mateval x2))));

%The next four are not generic definitions.

add!-generic!-table!-entry('quotient,'(!*matrix!* !*matrix!*),
          '(lambda (x1 x2)
            (mk!*matrix!* (multm (mat2q x1) (matinv (mat2q x2))))));

add!-generic!-table!-entry('quotient,'(!*integer!* !*matrix!*),
          '(lambda (x1 x2)
            (mk!*matrix!* (multsm (!*i2q (rvalue x1))
                                  (matinv (mat2q x2))))));

add!-generic!-table!-entry('quotient,'(!*kernel!* !*matrix!*),
          '(lambda (x1 x2)
            (mk!*matrix!* (multsm (kchk2q x1) (matinv (mat2q x2))))));

add!-generic!-table!-entry('quotient,'(!*sq !*matrix!*),
          '(lambda (x1 x2)
            (mk!*matrix!* (multsm (schk x1) (matinv (mat2q x2))))));

add!-generic!-table!-entry('quotient,'(!*matrix!* !*integer!*),
          '(lambda (x1 x2)
            (!*scalar!*matrix!*
                      (mk!*sq!* (canonsq (cons 1 (rvalue x2))))
                      (mateval x1))));

add!-generic!-table!-entry('quotient,'(!*matrix!* !*kernel!*),
          '(lambda (x1 x2)
            (!*scalar!*matrix!* (mk!*sq!* (invsq (kchk2q x2)))
                                (mateval x1))));

add!-generic!-table!-entry('quotient,'(!*matrix!* !*sq),
          '(lambda (x1 x2)
            (!*scalar!*matrix!* (mk!*sq!* (invsq (schk x2)))
                                (mateval x1))));

add!-generic!-table!-entry('times2,'(!*integer!* !*matrix!*),
          '(lambda (x1 x2) (!*scalar!*matrix!* x1 (mateval x2))));

add!-generic!-table!-entry('times2,'(!*matrix!* !*matrix!*),
          '(lambda (x1 x2)
            (!*matrixtimes2!* (mateval x1) (mateval x2))));

add!-generic!-table!-entry('times2,'(!*kernel!* !*matrix!*),
          '(lambda (x1 x2)
            (!*scalar!*matrix!* x1 (mateval x2))));

add!-generic!-table!-entry('times2,'(!*matrix!* !*kernel!*),
          '(lambda (x1 x2) (!*scalar!*matrix!* x2 x1)));

add!-generic!-table!-entry('times2,'(!*matrix!* !*integer!*),
          '(lambda (x1 x2) (!*scalar!*matrix!* x2 x1)));

add!-generic!-table!-entry('times2,'(!*sq !*matrix!*),
          '(lambda (x1 x2) (!*scalar!*matrix!* x1 x2)));

add!-generic!-table!-entry('times2,'(!*matrix!* !*sq),
          '(lambda (x1 x2) (!*scalar!*matrix!* x2 x1)));


%add!-generic!-table!-entry('print,'(!*matrix!*),
%          '(lambda (x1) (matpri!*2 x1 nil nil)));

add!-generic!-table!-entry('df,'(!*matrix!* !*kernel!*),
          '(lambda (x1 x2) (!*dfmat!* x1 x2)));
%this consruct is not easy to use and very restrictive.

put('!*matrix!*,'prefixfn,function (lambda x; x));

symbolic procedure mateval u;   %this is pretty bad!
   list('!*matrix!*,for each j in rvalue u collect
                      for each k in j collect
                        if eqcar(k,'!*matrix!*) then mateval k
                         else if eqcar(k,'!*integer!*) then k
                         else if renvironment k then k
                         else eval nform(mkprefix k,nil,'algebraic));

symbolic procedure mk!*matrix!* u;
   list('!*matrix!*,for each j in u collect
                      for each k in j collect mk!*sq!* k);

symbolic procedure matpri2(u,v);
   <<matprixxx(for each j in u collect
              for each k in j collect sq k,v); u>>;

symbolic procedure matpri!*2(u,v,w);
   matpri2(rvalue u,if v then eval car v else nil);

make!-generic!-definition('det,1);

put('!*det!*,'generic!-function,'processdet);

symbolic procedure processdet u;
  mk!*sq!* detq mat2q u;

symbolic procedure mat2q u;
   for each j in rvalue u collect
     for each k in j collect schk sq k;

SYMBOLIC PROCEDURE TP1 U;
   %returns transpose of the matrix canonical form U;
   %U is destroyed in the process;
   BEGIN SCALAR V,W,X,Y,Z;
    V := W := LIST NIL;
    WHILE CAR U DO
     <<X := U;
       Y := Z := LIST NIL;
       WHILE X DO
         <<Z := CDR RPLACD(Z,LIST CAAR X);
           X := CDR RPLACA(X,CDAR X)>>;
       W := CDR RPLACD(W,LIST CDR Y)>>;
    RETURN CDR V
   END;


add!-generic!-table!-entry('print,'(setmat),'(lambda (x)
  (matpri2 (rvalue (caddr x)) (cadr x))));

 add!-generic!-table!-entry('print,'(!*matrix!*),
      '(lambda (x) (matpri2 (rvalue x) "MAT")));

symbolic procedure !*matelem!* u;
   getmatelem u;

symbolic procedure setmatelem(u,v);
   'setmatel . u . letmtr(u,v,get(car u,'matvalue));

put('!*matelem!*,'rtype,'matelem);

put('matelem,'setfn,'setmatelem);

symbolic procedure nformmatelem (u,vars,mode);
   if cdddr u then errpri2(u,t)
    else list('!*matelem!*,mkmatel(u,vars,mode));

symbolic procedure mkmatel(u,vars,mode);
   'list . mkquote car u .
       for each x in cdr u collect list('mkinteger,nform(x,vars,mode));



SYMBOLIC PROCEDURE GETMATELEM U;
   BEGIN SCALAR X;
      X := rvalue GET(CAR U,'matVALUE);
      IF NOT x THEN REDERR LIST("Matrix",CAR U,"not set");
      u := cdr u;
      RETURN NTH(NTH(X,CAR U),CADR U)
   END;

SYMBOLIC PROCEDURE LETMTR(U,V,Y);
   %substitution for matrix elements;
   BEGIN SCALAR Z;
        z := cdr u;
    IF NOT Y THEN REDERR LIST("Matrix",CAR U,"not set");
    RPLACA(PNTH(NTH(rvalue Y,CAR Z),CADR Z),V);
        return v
   END;

symbolic procedure setmat(u,v);
   'setmat . u . list put(u,'matvalue,mateval v);

put('mat,'setfn,'setmat);

