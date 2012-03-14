module ratjord; %Computation of rational Jordan normal form of a matrix.

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


% The function ratjordan computes the rational Jordan normal form R of
% a matrix A, the transformation matrix P and its inverse P^(-1).
%
% Specifically:
%
% - ratjordan(A) will return {R,P,Pinv} where R, P, and Pinv
%   are such that P*R*Pinv = A.

% Global description of the algorithm:
%
% For a given n by n matrix A over a field K, we first compute the
% Frobenius normal form F of A. Then we compute the rational Jordan
% normal form of F, which is also the rational Jordan normal form of A.
% If F=diag(C1,..,Cr), where Ci is the companion matrix associated with
% a polynomial pi in K[x], we first compute the rational Jordan normal
% form of C1 to Cr. From these we then extract the rational Jordan
% normal form of F.

null(load!-package 'specfn);  % To use binomial, but not load during
                              % compilation.

symbolic procedure ratjordan(A);
  begin
    scalar AA,tmp,ans,P,Pinv,full_coeff_list,rule_list,input_mode;

    matrix_input_test(A);
    if (car size_of_matrix(A)) neq (cadr size_of_matrix(A))
     then rederr "ERROR: expecting a square matrix. ";

    input_mode := get(dmode!*,'dname);
    %
    % If modular or arnum are on then we keep them on else we want
    % rational on.
    %
    if input_mode neq 'modular and input_mode neq 'arnum and
     input_mode neq 'rational then on rational;
    on combineexpt;

    tmp := nest_input(A);
    AA := car tmp;
    full_coeff_list := cadr tmp;

    tmp := ratjordanform(AA,full_coeff_list);
    ans := car tmp;
    P := cadr tmp;
    Pinv := caddr tmp;
    %
    %  Set up rule list for removing nests.
    %
    rule_list := {'co,2,{'~,'int}}=>'int when numberp(int);
    for each elt in full_coeff_list do
    <<
      tmp := {'co,2,{'~,elt}}=>elt;
      rule_list := append (tmp,rule_list);
    >>;
    %
    %  Remove nests.
    %
    let rule_list;
    ans := de_nest_mat(ans);
    P := de_nest_mat(P);
    Pinv := de_nest_mat(Pinv);
    clearrules rule_list;
    %
    % Return to original mode.
    %
    if input_mode neq 'modular and input_mode neq 'arnum and
     input_mode neq 'rational then
       <<
         % onoff('nil,t) doesn't work so ...
         if input_mode = 'nil then off rational
         else onoff(input_mode,t);
       >>;
    off combineexpt;

    return {'list,ans,P,Pinv};
  end;

flag ('(ratjordan),'opfn);  %  So it can be used from
                            %  algebraic mode.



symbolic procedure ratjordanform(A,full_coeff_list);
  begin
    scalar tmp,F,TT,Tinv,prim_inv,S,Sinv,P,Pinv,x;

    x := mkid('x,0);

    tmp := frobeniusform(A);
    F := car tmp;
    TT := cadr tmp;
    Tinv := caddr tmp;

    tmp := frobenius_to_ratjordan(F,full_coeff_list,x);
    prim_inv := car tmp;
    S := cadr tmp;
    Sinv := caddr tmp;

    P := reval {'times,TT,S};
    Pinv := reval {'times,Sinv,Tinv};

    prim_inv := priminv_to_ratjordan(prim_inv,x);

    return {prim_inv,P,Pinv};
  end;




% companion_to_ratjordan computes the rational Jordan normal form of a
% matrix C which is the companion matrix of a polynomial p. Since the
% factors of p are known, the rational Jordan normal form of C is also
% known, so in fact we only have to compute the transition matrix.

% Global description of the algorithm:
%
% car consider the case where p=q^e, q irreducible. Let n=degree(p).
% Then we have the following diagram:
%
%                           ~
%                   K^n <------- K[x]/q^e
%
%                    |               |
%                    |               |
%                    |C              |x
%                    |               |
%                    |               |
%                   \ /             \ /
%                           ~
%                   K^n <------- K[x]/q^e
%
% We look for a K-basis (b1,..,bn) of K[x]/q^e such that we get the
% following diagram:
%
%                       ~                ~
%               K^n <------- K[x]/q^e -------> K^n
%
%                |               |              |
%                |               |              |
%                |C              |x             |ratj(q,e)
%                |               |              |
%                |               |              |
%               \ /             \ /            \ /
%                       ~                ~
%               K^n <------- K[x]/q^e -------> K^n
%
% Let q=x^d+q(d-1)*x^(d-1)+..+q1*x+q0. It follows that b1,..,bn must
% satisfy the following relations:
%
% x*b1      = b2
% x*b2      = b3
% ...
% x*bd      = -q0*b1-q1*b2-..-q(d-1)*bd
% x*b(d+1)  = b(d+2)+b1
% x*b(d+2)  = b(d+3)+b2
% ...
% x*b(2d)   = -q0*b(d+1)-q1*b(d+2)-..-q(d-1)*b(2d)+bd
% x*b(2d+1) = b(2d+2)+b(d+1)
% ...
% x*bn      = -q0*b(n-d+1)-q1*b(n-d+2)-..-q(d-1)*bn+b(n-d)
%
% From this we deduce that b1,b(d+1),b(2d+1),... must satisfy the
% following relations:
%
% q*b1      = 0
% q*b(d+1)  = q'*b1
% q*b(2d+1) = q'*b(d+1)-1/2*q''*b1
% q*b(3d+1) = q'*b(2d+1)-1/2*q''*b(d+1)+1/6*q'''*b1
% q*b(4d+1) = q'*b(3d+1)-1/2*q''*b(2d+1)+1/6*q'''*b(d+1)-1/24*q''''*b1
% ...
%
% where ' stands for taking the derivative with respect to x.
% If we choose b1=q^(e-1) we can compute b2,..,bn from the relations
% above. We assume that K is a perfect field, so q' is not zero. From
% this we see that q^(e-i-1) divides b(id+1) while q^(e-i) does not
% divide b(di+1). In particular we have gcd(b((e-1)i+1),q)=1.
% Notice also the following relations which can be easily proved:
%
% x^i*b1      = b(i+1)
% x^i*b(d+1)  = b(d+i+1)+binomial(i,1)*bi
% x^i*b(2d+1) = b(2d+i+1)+binomial(i,1)*b(d+i)+binomial(i,2)*b(i-1)
% ...
%
% Now the general case where p=q1^e1*q2^e2*..*qr^er. To compose the
% partial results we use the following diagram:
%
%      ~       ~                            ~
% K^n<--K[x]/p-->K[x]/q1^e1 X..X K[x]/qr^er-->K^n1 X......X K^nr
%
%  |       |         |               |          |             |
%  |       |         |               |          |             |
%  |C      |x        |x              |x         |  ratj       | ratj
%  |       |         |               |          |( q1,e1)     |(qr,er)
%  |       |         |               |          |             |
% \ /     \ /       \ /             \ /        \ /           \ /
%
%      ~       ~                            ~
% K^n<--K[x]/p-->K[x]/q1^e1 X..X K[x]/qr^er-->K^n1 X......X K^nr
%
% In order to compose the K_bases of K[x]/q1^e1 through K[x]/qr^er to
% a K-basis of K[x]/p we compute polynomials u1,..,ur such that
% (ui mod qi^ei)=1 and (ui mod qj^ej)=0.


symbolic procedure companion_to_ratjordan(fact_list,f,x);
  begin
    scalar g_list,u_list,bbasis,q1,e,qpower,diffq,part_basis,
           ratj_basis,s,tt,g,rowQinv,pol_lincomb,qq,rr,lincomb,index1,v,
           u,a,tmp,Qinv,Q,sum1;
    integer r,n,d;

    r := length fact_list;
    n := deg(f,x);

    g_list := for i:=1:r collect reval{'expt,nth(nth(fact_list,i),1),
                                       nth(nth(fact_list,i),2)};

    %%%%%%%%%%%%%%%%%%%
    % Compute u1,..,ur.
    %%%%%%%%%%%%%%%%%%%
    u_list := mkvect(r);
    if r=1 then putv(u_list,1,1)
    else
    <<
      tmp := calc_exgcd(nth(g_list,1),nth(g_list,2),x);
      s := cadr tmp;
      tt := caddr tmp;
      putv(u_list,1,{'times,tt,nth(g_list,2)});
      putv(u_list,2,{'times,s,nth(g_list,1)});
      g := {'times,nth(g_list,1),nth(g_list,2)};
      for i:=3:r do
      <<
        tmp := calc_exgcd(g,nth(g_list,i),x);
        s := cadr tmp;
        tt := caddr tmp;
        for j:=1:i-1 do
        <<
          putv(u_list,j,get_rem({'times,getv(u_list,j),tt,nth(g_list,i)}
               ,f));
        >>;
        putv(u_list,i,{'times,s,g});
        g := {'times,g,nth(g_list,i)};
      >>;
    >>;
    %%%%%%%%%%%%%%%%%%%

    bbasis := {};    %  Basis will contain a K-basis of K[x]/f.
    rowQinv := 0;

    Q := mkmatrix(n,n);
    Qinv := mkmatrix(n,n);

    for i:=1:r do
    <<
      q1 := nth(nth(fact_list,i),1);
      e := reval nth(nth(fact_list,i),2);
      d := deg(q1,x);

      qpower := mkvect(e+1);
      putv(qpower,1,1);
      for j:=2:e+1 do
      <<
        putv(qpower,j,{'times,q1,getv(qpower,j-1)});
      >>;

      if e>1 then
      <<
        diffq := mkvect(e-1);
        putv(diffq,1,reval algebraic df(q1,x));
        for j:=2:e-1 do
        <<
          tmp := reval getv(diffq,j-1);
          putv(diffq,j,reval algebraic df(tmp,x));
        >>;
      >>;

      %%%%%%%%%%%%%%%%%%%
      % Compute b1,b(d+1),b(2d+1),...
      %%%%%%%%%%%%%%%%%%%
      part_basis := mkvect(e);
      putv(part_basis,1,reval {'expt,q1,e-1});

      for j:=2:e do
      <<
        sum1 := 0;
        for k:=1:j-1 do
        <<
          tmp := reval{'times, reval {'quotient,reval {'expt,-1,k-1},
                       reval{'factorial,k}},reval getv(diffq,k),
                       reval getv(part_basis,j-k)};
          sum1 := reval{'plus,sum1,tmp};
        >>;
        putv(part_basis,j,reval{'quotient,sum1,q1});
      >>;
      %%%%%%%%%%%%%%%%%%%

      %%%%%%%%%%%%%%%%%%%
      % Compute b1,..,bni.
      %%%%%%%%%%%%%%%%%%%
      ratj_basis := mkvect(e*d);
      putv(ratj_basis,1,getv(part_basis,1));

      for k:=2:d do
      <<
        putv(ratj_basis,k,{'times,x,getv(ratj_basis,k-1)});
      >>;

      for j:=2:e do
      <<
        putv(ratj_basis,(j-1)*d+1,getv(part_basis,j));
        for k:=2:d do
        <<
          putv(ratj_basis,(j-1)*d+k,{'plus,{'times,x,getv(ratj_basis,
               (j-1)*d+k-1)},{'minus,getv(ratj_basis,(j-2)*d+k-1)}});
        >>;
      >>;

      %%%%%%%%%%%%%%%%%%%

      %%%%%%%%%%%%%%%%%%%
      % Complete basis.
      %%%%%%%%%%%%%%%%%%%
      for k:=1:e*d do
      <<
        tt := get_rem({'times,getv(u_list,i),getv(ratj_basis,k)},f);
        bbasis := append(bbasis,{tt});
      >>;
      %%%%%%%%%%%%%%%%%%%

      %%%%%%%%%%%%%%%%%%%
      % Compute next e*d rows of Qinv (see diagram above).
      %%%%%%%%%%%%%%%%%%%

      %%%%%%%%%%%%%%%%%%%
      % Compute coordinates of 1 with respect to basis (b1,..,bn).
      % Use fact that q1^(e-i-1) divides b(id+1) and gcd(b((e-1)d+1),q1)
      %  = 1
      %%%%%%%%%%%%%%%%%%%
      pol_lincomb := mkvect(e);
      for j:=1:e do putv(pol_lincomb,j,0);
      tmp := calc_exgcd(getv(part_basis,e),getv(qpower,e+1),x); % =1
      s := cadr tmp;
      tt := caddr tmp;
      putv(pol_lincomb,e,s);

      for j:=e step -1 until 1 do
      <<
        qq := get_quo(getv(pol_lincomb,j),q1);
        rr := get_rem(getv(pol_lincomb,j),q1);
        putv(pol_lincomb,j,rr);

        for k:=1:j-1 do
        <<
          putv(pol_lincomb,j-k,get_rem({'plus,getv(pol_lincomb,j-k),
               {'times,qq,getv(diffq,k),{'expt,-1,{'quotient,k,
               {'factorial,k}}}}},getv(qpower,j+1)));
        >>;

      >>;

      lincomb := mkvect(e*d);
      for j:=1:e do
      <<
        for k:=1:d do
        <<
          index1 := (j-1)*d+k;
          putv(lincomb,index1,coeffn(getv(pol_lincomb,j),x,k-1));

          for v:=1:min(j-1,k-1) do
          <<
            putv(lincomb,index1-v*d-v,reval{'plus,getv(lincomb,
                 index1-v*d-v),{'times,coeffn(getv(pol_lincomb,j),x,k-1)
                 ,binomial(k-1,v)}});
          >>;

        >>;
      >>;

      for u:=1:e*d do
      <<
        rowQinv:=rowQinv+1;
        setmat(Qinv,rowQinv,1,getv(lincomb,u));
      >>;
      %%%%%%%%%%%%%%%%%%%

      %%%%%%%%%%%%%%%%%%%
      % Compute coordinates of x^v with respect to basis (b1,..,bn).
      %%%%%%%%%%%%%%%%%%%
      for v:=2:n do
      <<
        %
        % a := copy(lincomb).
        %
        a := mkvect(upbv lincomb);
        for i:=1:upbv lincomb do
        <<
          putv(a,i,getv(lincomb,i));
        >>;

        index1 := 0;
        for j:=1:e-1 do
        <<
          index1 := index1 + 1;
          putv(lincomb,index1,reval{'plus,{'times,
               {'minus,coeffn(q1,x,0)},getv(a,j*d)},getv(a,j*d+1)});

          for k:=2:d do
          <<
            index1 := index1+1;
            putv(lincomb,index1,reval{'plus,{'plus,getv(a,(j-1)*d+k-1),
                 {'times,{'minus,coeffn(q1,x,k-1)},getv(a,j*d)},
                 getv(a,j*d+k)}});
          >>;

        >>;

        index1 := index1 + 1;
        putv(lincomb,index1,reval{'times,{'minus,coeffn(q1,x,0)},
             reval getv(a,e*d)});

        for k:=2:d do
        <<
          index1 := index1 + 1;
          putv(lincomb,index1,reval{'plus,getv(a,(e-1)*d+k-1),{'times,
               {'minus,coeffn(q1,x,k-1)},getv(a,e*d)}});
        >>;

        rowQinv := rowQinv-e*d;
        for u:=1:e*d do
        <<
          rowQinv := rowQinv +1;
          setmat(Qinv,rowQinv,v,getv(lincomb,u));
        >>;

      >>;
      %%%%%%%%%%%%%%%%%%%

      %%%%%%%%%%%%%%%%%%%
    >>;

    %%%%%%%%%%%%%%%%%%%
    % Compute Q (see diagram above).
    %%%%%%%%%%%%%%%%%%%
    for j:=1:n do
    <<
      for k:=1:n do
      <<
        setmat(Q,k,j,coeffn(nth(bbasis,j),x,k-1));
      >>;
    >>;
    %%%%%%%%%%%%%%%%%%%

    return {Q,Qinv};
  end;




symbolic procedure convert_to_mult(faclist,x);
  %
  % This function takes as input a list of factors from factorize
  % and converts it to a list as follows: {{fac,mult},{fac,mult}...},
  % where mult is the multiplicity of that factorial.
  %
  % No need to deal with cases such as {x,x,x,x+1,x+1,x,x,x,x+1}
  % (for example) as factorize groups factorials together.
  %
  % Note that {x,-x} will give {{x,2}}.
  %
  % The factorials are normalised w.r.t. x. ie: 5*x^2 -> x^2.
  % NB: This does not normalise multivariate polynomials as completely
  %     as the maple "factors" does. This may cause a bug in the matrix
  %     normforms but all cases tried so far seem to work.
  %
  begin
    scalar multlist,z;
    integer mult1;

    faclist := cdr faclist; % Remove 'list that is added by factorize.

    %  Remove non polynomial (integer) factor if it's there.
    if numberp car faclist then faclist := cdr faclist;

    multlist := {};

    for i:=2:length faclist+1 do
    <<
      mult1 := 1;

      % While we're in faclist and abs value of adjacent elt's is equal.
      while i<= length faclist and numberp(z := reval {'quotient,
            nth(faclist,i-1),nth(faclist,i)}) and abs z = 1 do
      <<
        mult1 := mult1+1;
        i := i+1;
      >>;
      %
      %  Normalise list so that lcof of each elt wrt x is +1.
      %  NB: no need to consider case where lcof(int,x) gives 0 as
      %  faclist will never contain integers.
      %
      if numberp off_mod_lcof(nth(faclist,i-1),x) and
         off_mod_lcof(nth(faclist,i-1),x) neq 0 then
      <<
          multlist := append(multlist,{{reval {'quotient,
                             nth(faclist,i-1),off_mod_lcof
                             (nth(faclist,i-1),x)},mult1}});
      >>
      % Make -elt -> elt.
      else if car nth(faclist,i-1) = 'minus then
      <<
        multlist := append(multlist,{{cadr nth(faclist,i-1),mult1}});
      >>
      else multlist := append(multlist,{{nth(faclist,i-1),mult1}});

    >>;

    return multlist;
  end;




symbolic procedure copyinto(BB,AA,p,q);
  %
  % Copies matrix BB into AA with BB(1,1) at AA(p,q).
  % Returns newly formed matrix A.
  %
  % Can be used independently from algebraic mode.
  %
  begin
    scalar A,B;
    integer m,n,r,c;

    matrix_input_test(AA);
    matrix_input_test(BB);

    if p = 0 or q = 0 then
     rederr "     0 is out of bounds for matrices.
     The top left element is labelled (1,1) and not (0,0).";

    m := car size_of_matrix(AA);
    n := cadr size_of_matrix(AA);
    r := car size_of_matrix(BB);
    c := cadr size_of_matrix(BB);

    if r+p-1>m or c+q-1>n then rederr
     {"The matrix",BB,"does not fit into",AA,"at position",p,q,"."};

    A := mkmatrix(m,n);
    B := mkmatrix(r,c);

    for i:=1:m do
    <<
      for j:=1:n do
      <<
        setmat(A,i,j,getmat(AA,i,j));
      >>;
    >>;

    for i:=1:r do
    <<
      for j:=1:c do
      <<
        setmat(B,i,j,getmat(BB,i,j));
      >>;
    >>;

    for i:=1:r do
    <<
      for j:=1:c do
      <<
        setmat(A,p+i-1,q+j-1,getmat(B,i,j));
      >>;
    >>;

    return A;
  end;

flag ('(copyinto),'opfn);  %  So it can be used independently
                           %  from algebraic mode.



symbolic procedure de_nest_list(input,full_coeff_list);
  %
  % Takes as input a list of nested polys and de-nests them all.
  %
  begin
    scalar tmp,copy,rule_list;

    if full_coeff_list = nil then copy := input
    else
    <<
      copy := input;
      %
      %  Set up rule list for removing nests.
      %
      rule_list := {'co,2,{'~,'int}}=>'int when numberp(int);
      for each elt in full_coeff_list do
      <<
        tmp := {'co,2,{'~,elt}}=>elt;
        rule_list := append (tmp,rule_list);
      >>;
      %
      %  Remove nests.
      %
      let rule_list;
      if atom copy then copy := reval copy
      else copy := for each elt in copy collect reval elt;
      clearrules rule_list;
    >>;

    return copy;
  end;




symbolic procedure deg_sort(l,x);
  %
  % Takes a list of polys and sorts them into increasing order.
  %
  % Has been written so that it can also be run independently
  % from algebraic mode.
  %
  begin
    scalar ll,alg;
    integer n;
    %
    %  If input from algebraic mode then car is 'list. In the normal
    %  forms, l in entered without the 'list.
    %
    if car l = 'list then
    <<
      ll := cdr l;
      alg := t;
    >>
    else ll := l;

    %  Get no of elts.
    n := length ll;

    for i:=1:n-1 do
    <<
      for j:=i+1:n do
      <<
        if deg(nth(ll,j),x) < deg(nth(ll,i),x) then
        <<
          ll := append(append(append(for k:=1:i-1 collect nth(ll,k),
                       {nth(ll,j)}),for k:=i:j-1 collect nth(ll,k)),
                       for k:=j+1:n collect nth(ll,k));
        >>;
      >>;
    >>;

    %  If input from algebraic mode then make output algebraic
    %  compatible.
    if alg then ll := append({'list},ll);

    return ll;
  end;

flag ('(deg_sort),'opfn);  %  So it can be used independently from
                           %  algebraic mode.



symbolic procedure frobenius_to_invfact(F,x);
  %
  % For a matrix F in Frobenius normal form, frobenius_to_invfact(F,x)
  % computes inv_fact := {p1,..,pr} such that
  % F=invfact_to_frobenius(plist,x).
  %
  begin
    scalar p,inv_fact;
    integer row_dim,m,k;

    row_dim := car size_of_matrix(F);
    inv_fact := {};
    k := 1;

    while k<=row_dim do
    <<
      p := 0;
      m := k+1;
      while m<=row_dim and getmat(F,m,m-1)=1 do m:=m+1;
      for j:=k:m-1 do
      <<
        p := reval{'plus,p,{'times,{'minus,getmat(F,j,m-1)},
                   {'expt,x,j-k}}};
      >>;
      p := reval{'plus,p,{'expt,x,m-k}};
      inv_fact := append(inv_fact,{p});
      k := m;
    >>;

    return inv_fact;
  end;




symbolic procedure frobenius_to_ratjordan(F,full_coeff_list,x);
  %
  % frobenius_to_ratjordan computes the rational Jordan form R of a
  % matrix F which is in Frobenius normal form. Say F=diag(C1,..,Cr),
  % where Ci is the companion matrix associated with the polynomial pi.
  % first we determine the irreducible factors p1,..,pN which appear
  % in p1 through pr and build a matrix fact_mat such that pi=
  % product(Pj^fact_mat(i,j),j=1..N). This matrix is used a several
  % places in the algorithm.
  % In fact we can immediately extract from fact_mat the rational
  % Jordan normal of F. We compute the transformation matrix by
  % rearranging the former results.
  % If R is the matrix in rational Jordan normalform corresponding to
  % prim_inv:=[[q1,[e11,e12,...]],[q2,[e21,e22,...]],....], then
  % prim_inv is returned by frobenius_to_ratjordan.
  %
  begin
    scalar inv_fact,gg,l,m,h,p,Fact_mat,G,ii,pp,ff,j,t_list,tinv_list,
           facts,tmp,q,qinv,degp,D,TT,S,cols,count,Tinv,Sinv,exp_list,
           prim_inv,nn,prod;
    integer r,n;

    %  Compute p1,..,pr.
    inv_fact := frobenius_to_invfact(F,x);
    r := length inv_fact;

    %%%%%%%%%%%%%%%%%%%
    % Compute fact_mat
    %%%%%%%%%%%%%%%%%%%
    gg := append({nth(inv_fact,1)},for i:=2:r collect
    get_quo(nth(inv_fact,i),nth(inv_fact,i-1)));

    l := {};
    for i:=1:r do
    <<

      % the problem is that den co(2,?) gives 1 and not ? so we
      % have to de_nest it first (then use co(2,m) later).
      prod := 1;
      for j:=0:deg(nth(gg,i),x) do
      <<
        %
        %  In the following code we take the denominator of a
        %  polynomial.
        %  There are two problems:
        %  1) den co(2,?) gives 1 and not ?.
        %  2) with rational on den(1/3) = 1 (we require 3).
        %  To solve problem 1 we de_nest the polynomial.
        %  To solve problem 2 the easy solution would be to turn
        %  rational off. Unfortunately arnum may be on so we can't do
        %  this. Thus we test to see if poly is a number and then a
        %  quotient. If it is we take the den using get_den. If poly is
        %  not a  number then problem 2 does not apply.
        %
        tmp := de_nest(reval coeffn(nth(gg,i),x,j));
        if evalnumberp tmp then
        <<
          if quo_test(tmp) then tmp := get_den(tmp)
          else tmp := 1;
        >>
        %  else coeffn is a poly in which case den will work.
        else
        <<
          tmp := den(tmp);
        >>;
        prod := reval {'times,tmp,prod};
      >>;
      m := prod;

      %  Next lines not necessary but quicker.
      if m = 1 and nth(gg,i) = 1 then h := {}
      else if m = 1 then
      <<
        tmp := de_nest_list(nth(gg,i),full_coeff_list);
        tmp := old_factorize(tmp);
        tmp := re_nest_list(tmp,full_coeff_list);
        h := (convert_to_mult(tmp,x));
      >>
      else
      <<
        tmp := reval{'times,{'co,2,m},nth(gg,i)};
        tmp := de_nest_list(tmp,full_coeff_list);
        tmp := old_factorize(tmp);
        tmp := re_nest_list(tmp,full_coeff_list);
        h := (convert_to_mult(tmp,x));
      >>;
      l := append(l,(for j:=1:length h collect {i,{'quotient,
                  nth(nth(h,j),1),off_mod_lcof(nth(nth(h,j),1),x)},
                  nth(nth(h,j),2)}));
    >>;

    p := deg_sort(for i:=1:length l collect nth(nth(l,i),2),x);
    n := length p;
    G := mkmatrix(r,n);
    Fact_mat := mkmatrix(r,n);

    for k:=1:length l do
    <<
      ii := nth(nth(l,k),1);
      pp := nth(nth(l,k),2);
      ff := nth(nth(l,k),3);
      j := 1;
      while pp neq nth(p,j) and j<=n do j:=j+1;
      setmat(G,ii,j,ff);
    >>;

    for j:=1:n do setmat(Fact_mat,1,j,getmat(G,1,j));
    for i:=2:r do
    <<
      for j:=1:n do
      <<
        setmat(Fact_mat,i,j,{'plus,getmat(Fact_mat,i-1,j),
               getmat(G,i,j)});
      >>;
    >>;
    %%%%%%%%%%%%%%%%%%%

    %%%%%%%%%%%%%%%%%%%
    % Compute transition matrix for C1 through Cr.
    %%%%%%%%%%%%%%%%%%%
    t_list := {};
    tinv_list := {};
    for i:=1:r do
    <<
      facts := {};

      for j:=1:n do
      <<
        if getmat(Fact_mat,i,j) neq 0 then
        <<
          facts := append(for each elt in facts collect elt,
                          {{nth(p,j),getmat(Fact_mat,i,j)}});
        >>;
      >>;

      tmp := companion_to_ratjordan(facts,nth(inv_fact,i),x);
      Q := car tmp;
      Qinv := cadr tmp;
      tinv_list := append(tinv_list,{Qinv});
      t_list := append(t_list,{Q});
    >>;
    %%%%%%%%%%%%%%%%%%%

    %%%%%%%%%%%%%%%%%%%
    % Compute transition matrix by permuting diag(t_list(1),..,
    % t_list(r)).
    %%%%%%%%%%%%%%%%%%%
    D := mkmatrix(r,n);
    degp := mkvect(r);
    for i:=1:r do
    <<
      for j:=1:n do
      <<
        setmat(d,i,j,{'times,deg(nth(p,j),x),getmat(fact_mat,i,j)});
      >>;
      putv(degp,i,for j:=1:n sum off_mod_reval(getmat(d,i,j)));
    >>;

    cols := {};
    for j:=1:n do
    <<
      for i:=1:r do
      <<
        count := reval{'plus,for k:=1:i-1 sum off_mod_reval
                       (getv(degp,k)),for k:=1:j-1 sum reval
                       getmat(d,i,k)};
        for h:=1:off_mod_reval(getmat(d,i,j)) do
        <<
          cols := append(cols,{reval{'plus,count,h}});
        >>;
      >>;
    >>;

    TT := reval{'diagi,t_list};
    nn := car size_of_matrix(TT);
    S := mkmatrix(nn,nn);
    for i:=1:nn do
    <<
      for j:=1:nn do
      <<
        setmat(S,i,j,getmat(TT,i,nth(cols,j)));
      >>;
    >>;

    Tinv := reval{'diagi,tinv_list};
    Sinv := mkmatrix(nn,nn);
    for i:=1:nn do
    <<
      for j:=1:nn do
      <<
        setmat(Sinv,i,j,getmat(Tinv,nth(cols,i),j));
      >>;
    >>;
    %%%%%%%%%%%%%%%%%%%

    %%%%%%%%%%%%%%%%%%%
    % Compute prim_inv.
    %%%%%%%%%%%%%%%%%%%
    prim_inv := {};
    for j:=1:n do
    <<
      exp_list:={};
      for i:=1:r do
      <<
        if getmat(fact_mat,i,j) neq 0
         then exp_list := append(exp_list,{getmat(fact_mat,i,j)});
      >>;
      prim_inv := append(prim_inv,{{nth(p,j),exp_list}});
    >>;
    %%%%%%%%%%%%%%%%%%%

    return {prim_inv,S,Sinv};
  end;

symbolic procedure get_den(input);
  %
  % Gets denominator, ignoring sign.
  %
  begin
    scalar denom,copy;
    copy := input;
    if car copy = 'minus then copy := cadr copy;
    denom := caddr copy;
    return denom;
  end;

symbolic procedure make_ratj_block(p,e,x);
  %
  % For a monic polynomial p in x and a positive integer e,
  % make_ratj_block(p,e,x) returns the matrix ratj(p,e).
  %
  begin
    scalar C,J_block;
    integer d,n;

    C := companion(p,x);

    d := deg(p,x);
    e := off_mod_reval(e);
    n := d*e;
    J_block  := mkmatrix(n,n);

    for i:=1:e do
    <<
      J_block := copyinto(C,J_block,(i-1)*d+1,(i-1)*d+1);
    >>;

    for i:=1:n-d do
    <<
      setmat(J_block,i,i+d,1);
    >>;

    return J_block;
  end;




symbolic procedure priminv_to_ratjordan(prim_inv,x);
  %
  % For a primary invariant prim_inv, priminv_to_ratjordan(prim_inv,x)
  % returns the matrix R in rational Jordan normal form corresponding to
  % prim_inv.
  %
  begin
    scalar p,exp_list,block_list;
    integer r;

    r := length prim_inv;
    block_list := {};

    for i:=1:r do
    <<
      p := nth(nth(prim_inv,i),1);
      exp_list := nth(nth(prim_inv,i),2);
      for j:=1:length exp_list do
      <<
        block_list := append(block_list,{make_ratj_block(p,
    nth(exp_list,j),x)});
      >>;
    >>;

    return reval{'diagi,block_list};
  end;




symbolic procedure quo_test(input);
  %
  % Tests for quotient returning t or nil;
  %
  begin
    scalar boolean,copy;
    copy := input;
    if atom copy then <<>> else
    <<
      if car copy = 'minus then copy := cadr copy;
      if car copy := 'quotient then boolean := t else boolean := nil;
    >>;
    return boolean;
  end;




symbolic procedure re_nest_list(input,full_coeff_list);
  %
  % Re_nests the list that has been de_nested.
  %
  begin
    scalar tmp,copy;

    copy := input;

    for each elt in full_coeff_list do
    <<
      tmp := {'co,2,elt};
      copy := algebraic (sub(elt=tmp,copy));
    >>;

    return copy;
  end;

endmodule;

end;

