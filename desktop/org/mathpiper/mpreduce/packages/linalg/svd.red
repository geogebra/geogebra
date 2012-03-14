module svd;

%**********************************************************************%
%                                                                      %
% Computation of the Singular Value Decomposition of dense matrices    %
% containing numeric entries. Uses specific rounded number routines to %
% speed things up.                                                     %
%                                                                      %
% Author: Matt Rebbeck, June 1994.                                     %
%                                                                      %
% The algorithm was taken from "Linear Algebra" - J.H.Wilkinson        %
%                                                  & C. Reinsch        %
%                                                                      %
%**********************************************************************%

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




symbolic smacro procedure my_minus(u);
  %
  % Efficiently performs reval({'minus,u}).
  %
  if atom u then {'minus,u}
   else if car u = 'minus then cadr u
    else {'minus,u};



symbolic procedure svd(A);
  %
  % Computation of the singular values and complete orthogonal
  % decomposition of a real rectangular matrix A.
  %
  %      A = tp(U) diag(q) V,   U tp(U) = V tp(V) = I,
  %
  % and q contains the singular values along the diagonal.
  % (tp => transpose).
  %
  % Algorithm taken from "Linear Algebra"
  %                      J.H.Wilkinson & C.Reinsch
  %
  begin
    scalar ee,U,V,g,x,eps,tolerance,q,s,f,h,y,test_f_splitting,
           cancellation,test_f_convergence,convergence,c,z,denom,q_mat,
           I_rounded_turned_on,trans_done;
    integer i,j,k,l,l1,m,n;
    trans_done := I_rounded_turned_on := nil;
    if not !*rounded then << on rounded; I_rounded_turned_on := t; >>;

    if not matrixp(A) then
     rederr "Error in svd: non matrix input.";

    % The value of eps can be decreased to increase accuracy.
    % As usual, doing this will slow things down (and vice versa).
    % It should not be made smaller than the value of rd!-tolerance!*.
    eps := get_num_part(my_reval({'times,1.5,{'expt,10,-8}}));
    tolerance := get_num_part(my_reval({'expt,10,-31}));

    % Algorithm requires m >= n. If this is not the case then transpose
    % the input and swap U and V in the output (as A = tp(U) diag(q) V
    % but tp(A) = tp(V) diag(q) U  ).
    if row_dim(A) < column_dim(A) then
    << A := algebraic tp(A); trans_done := t; >>;

    m := row_dim(A);
    n := column_dim(A);

    U := rd_copy_mat(A);
    V := mkmatrix(n,n);
    ee := mkvect(n);
    q := mkvect(n);

    % Householder's reduction to bidiagonal form:
    g := x := 0;
    for i:=1:n do
    <<
      putv(ee,i,g);
      s := 0;
      l := i+1;
      for j:=i:m do s := specrd!:plus(s,specrd!:expt(getmat(U,j,i),2));
      if get_num_part(s) < tolerance then g := 0
      else
      <<
        f := getmat(U,i,i);
        if get_num_part(f)<0 then g := specrd!:sqrt(s)
         else g := my_minus(specrd!:sqrt(s));
        h := specrd!:plus(specrd!:times(f,g),my_minus(s));
        setmat(U,i,i,specrd!:plus(f,my_minus(g)));
        for j:=l:n do
        <<
          s := 0;
          for k:=i:m do
           s := specrd!:plus(s,specrd!:times(getmat(U,k,i),
                                             getmat(U,k,j)));
          f := specrd!:quotient(s,h);
          for k:=i:m do
           setmat(U,k,j,specrd!:plus(getmat(U,k,j),
                        specrd!:times(f,getmat(U,k,i))));
        >>;
      >>;
      putv(q,i,g);
      s := 0;
      for j:=l:n do s := specrd!:plus(s,specrd!:expt(getmat(U,i,j),2));
      if get_num_part(s) < tolerance then g := 0
      else
      <<
        f := getmat(U,i,i+1);
        if get_num_part(f)<0 then g := specrd!:sqrt(s)
         else g := my_minus(specrd!:sqrt(s));
        h := specrd!:plus(specrd!:times(f,g),my_minus(s));
        setmat(U,i,i+1,specrd!:plus(f,my_minus(g)));
        for j:=l:n do putv(ee,j,specrd!:quotient(getmat(U,i,j),h));
        for j:=l:m do
        <<
          s := 0;
          for k:=l:n do
           s := specrd!:plus(s,specrd!:times(getmat(U,j,k),
                                             getmat(U,i,k)));
          for k:=l:n do
           setmat(U,j,k,specrd!:plus(getmat(U,j,k),
                        specrd!:times(s,getv(ee,k))));
        >>;
      >>;
      y := specrd!:plus(abs(get_num_part(getv(q,i))),
                        abs(get_num_part(getv(ee,i))));
      if get_num_part(y) > get_num_part(x) then x := y;
    >>;

    % Accumulation of right hand transformations:
    for i:=n step -1 until 1 do
    <<
      if get_num_part(g) neq 0 then
      <<
        h := specrd!:times(getmat(U,i,i+1),g);
        for j:=l:n do setmat(V,j,i,specrd!:quotient(getmat(U,i,j),h));
        for j:=l:n do
        <<
          s := 0;
          for k:=l:n do
           s := specrd!:plus(s,specrd!:times(getmat(U,i,k),
                                             getmat(V,k,j)));
          for k:=l:n do
           setmat(V,k,j,specrd!:plus(getmat(V,k,j),
                        specrd!:times(s,getmat(V,k,i))));
        >>;
      >>;
      for j:=l:n do << setmat(V,i,j,0); setmat(V,j,i,0); >>;
      setmat(V,i,i,1);
      g := getv(ee,i);
      l := i;
    >>;

    % Accumulation of left hand transformations:
    for i:=n step -1 until 1 do
    <<
      l := i+1;
      g := getv(q,i);
      for j:=l:n do setmat(U,i,j,0);
      if get_num_part(g) neq 0 then
      <<
        h := specrd!:times(getmat(U,i,i),g);
        for j:=l:n do
        <<
          s := 0;
          for k:=l:m do
           s := specrd!:plus(s,specrd!:times(getmat(U,k,i),
                                             getmat(U,k,j)));
          f := specrd!:quotient(s,h);
          for k:=i:m do
           setmat(U,k,j,specrd!:plus(getmat(U,k,j),
                        specrd!:times(f,getmat(U,k,i))));
        >>;
        for j:=i:m do setmat(U,j,i,specrd!:quotient(getmat(U,j,i),g));
      >>
      else for j:=i:m do setmat(U,j,i,0);
      setmat(U,i,i,specrd!:plus(getmat(U,i,i),1));
    >>;

    % Diagonalisation of the bidiagonal form:
    eps := get_num_part(specrd!:times(eps,x));
    test_f_splitting := t;
    k := n;
    while k>=1 do
    <<
      convergence := nil;
      if test_f_splitting then
      <<
        l := k;
        test_f_convergence := cancellation := nil;
        while l>=1 and not (test_f_convergence or cancellation) do
        <<
          if abs(get_num_part(getv(ee,l))) <= eps
           then test_f_convergence := t
          else if abs(get_num_part(getv(q,l-1))) <= eps
           then cancellation := t
          else l := l-1;
        >>;
      >>;

      % Cancellation of e[l] if l>1:
      if not test_f_convergence then
      <<
        c := 0; s := 1; l1 := l-1;
        i := l;
        while i<=k and not test_f_convergence do
        <<
          f := specrd!:times(s,getv(ee,i));
          putv(ee,i,specrd!:times(c,getv(ee,i)));
          if abs(get_num_part(f)) <= eps then
           test_f_convergence := t
          else
          <<
            g := getv(q,i);
            h := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),
                                           specrd!:times(g,g)));
            putv(q,i,h);
            c := specrd!:quotient(g,h);
            s := specrd!:quotient(my_minus(f),h);
            for j:=1:m do
            <<
              y := getmat(U,j,l1);
              z := getmat(U,j,i);
              setmat(U,j,l1,specrd!:plus(specrd!:times(y,c),
                                         specrd!:times(z,s)));
              setmat(U,j,i,specrd!:difference(specrd!:times(z,c),
                                              specrd!:times(y,s)));
            >>;
            i := i+1;
          >>;
        >>;
      >>;
      z := getv(q,k);
      if l = k then convergence := t;

      if not convergence then
      <<
        % Shift from bottom 2x2 minor:
        x := getv(q,l);
        y := getv(q,k-1);
        g := getv(ee,k-1);
        h := getv(ee,k);
        f := specrd!:quotient(specrd!:plus(specrd!:times(
              specrd!:plus(y,my_minus(z)),specrd!:plus(y,z)),
               specrd!:times(specrd!:plus(g,my_minus(h)),
                specrd!:plus(g,h))),specrd!:times(
                 specrd!:times(2,h),y));
        g := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),1));
        % Needed to change < here to <=.
        if get_num_part(f)<=0 then
        denom := specrd!:plus(f,my_minus(g))
         else denom := specrd!:plus(f,g);
        f := specrd!:quotient(specrd!:plus(specrd!:times(
              specrd!:plus(x,my_minus(z)),specrd!:plus(x,z)),
               specrd!:times(h,specrd!:quotient(y,
                specrd!:plus(denom,my_minus(h))))),x);

        % Next QR transformation:
        c := s := 1;
        for i:=l+1:k do
        <<
          g := getv(ee,i);
          y := getv(q,i);
          h := specrd!:times(s,g);
          g := specrd!:times(c,g);
          z := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),
                                         specrd!:times(h,h)));
          putv(ee,i-1,z);
          c := specrd!:quotient(f,z);
          s := specrd!:quotient(h,z);
          f := specrd!:plus(specrd!:times(x,c),specrd!:times(g,s));
          g := specrd!:plus(specrd!:times(my_minus(x),s),
                            specrd!:times(g,c));
          h := specrd!:times(y,s);
          y := specrd!:times(y,c);
          for j:=1:n do
          <<
            x := getmat(V,j,i-1);
            z := getmat(V,j,i);
            setmat(V,j,i-1,specrd!:plus(specrd!:times(x,c),
                                        specrd!:times(z,s)));
            setmat(V,j,i,specrd!:difference(specrd!:times(z,c),
                                            specrd!:times(x,s)));
          >>;
          z := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),
                                         specrd!:times(h,h)));
          putv(q,i-1,z);
          c := specrd!:quotient(f,z);
          s := specrd!:quotient(h,z);
          f := specrd!:plus(specrd!:times(c,g),specrd!:times(s,y));
          x := specrd!:plus(specrd!:times(my_minus(s),g),
                            specrd!:times(c,y));
          for j:=1:m do
          <<
                y := getmat(U,j,i-1);
                z := getmat(U,j,i);
            setmat(U,j,i-1,specrd!:plus(specrd!:times(y,c),
                                        specrd!:times(z,s)));
            setmat(U,j,i,specrd!:difference(specrd!:times(z,c),
                                            specrd!:times(y,s)));
          >>;
        >>;
        putv(ee,l,0);
        putv(ee,k,f);
        putv(q,k,x);
      >>
      else % convergence:
      <<
        if get_num_part(z)<0 then
        <<
          % q[k] is made non-negative:
          putv(q,k,my_minus(z));
          for j:=1:n do setmat(V,j,k,my_minus(getmat(V,j,k)));
        >>;
        k := k-1;
      >>;
    >>;

    q_mat := q_to_diag_matrix(q);
    if I_rounded_turned_on then off rounded;
    if trans_done then
     return {'list,algebraic tp V,q_mat,algebraic tp U}
      else return {'list,algebraic tp U,q_mat,algebraic tp V};
  end;

flag('(svd),'opfn); % To make it available from algebraic (user) mode.



symbolic procedure q_to_diag_matrix(q);
  %
  % Converts q (a vector) to a diagonal matrix with the elements of
  % q on the diagonal.
  %
  begin
    scalar q_mat;
    integer i,sq_dim_q;
    sq_dim_q := upbv(q);
    q_mat := mkmatrix(sq_dim_q,sq_dim_q);
    for i:=1:sq_dim_q do setmat(q_mat,i,i,getv(q,i));
    return q_mat;
  end;


symbolic procedure pseudo_inverse(in_mat);
  %
  % Also known as the Moore-Penrose Inverse.
  %
  % Given the singular value decomposition A := tp(U) diag(q) V
  % the pseudo inverse A^(-1) is defined as
  %
  %   A^(-1) = tp(V) (diag(q))^(-1) U.
  %
  % NB: this can be quite handy as we can take the inverse of non
  % square matrices (A * pseudo_inverse(A) = identity).
  %
  begin
    scalar psu_inv,svd_list;
    svd_list := svd(in_mat);
    psu_inv := algebraic
                (tp(third svd_list)*(1/second svd_list)*first svd_list);
    return psu_inv;
  end;

flag('(pseudo_inverse),'opfn);



symbolic procedure rd_copy_mat(A);
  %
  % Creates a copy of the input matrix and returns it aswell as
  % reval-ing each elt to get them in !:rd!: form;
  %
  begin
    scalar C;
    integer row_dim,column_dim;
    row_dim := first size_of_matrix(A);
    column_dim := second size_of_matrix(A);
    C := mkmatrix(row_dim,column_dim);
    for i:=1:row_dim do
    <<
      for j:=1:column_dim do
      <<
        setmat(C,i,j,my_reval(getmat(A,i,j)));
      >>;
    >>;
    return C;
  end;



%
% All computation is done with rounded mode on and with all numbers
% in !:rd!: form. The following specrd!: functions makes the algebraic
% computation of these numbers very efficient.
%
symbolic procedure specrd!:times(u,v);
  begin
    scalar negsign;
    u := add_minus(u);
    v := add_minus(v);
    if eqcar(u,'minus) then << u := cadr u; negsign := t>>;
    if eqcar(v,'minus) then << v := cadr v; negsign := not negsign>>;
    if atom u then u := mkround float u;
    if atom v then v := mkround float v;
    return if negsign then list('minus,rd!:times(u,v))
            else rd!:times(u,v);
  end;



symbolic procedure specrd!:quotient(u,v);
  begin
    scalar negsign;
    u := add_minus(u);
    v := add_minus(v);
    if eqcar(u,'minus) then << u := cadr u; negsign := t>>;
    if eqcar(v,'minus) then << v := cadr v; negsign := not negsign>>;
    if atom u then u := mkround float u;
    if atom v then v := mkround float v;
    return if negsign then list('minus,rd!:quotient(u,v))
                else rd!:quotient(u,v);
  end;



symbolic procedure specrd!:expt(u,v);
  begin
    if (u = '(!:rd!: . 0.0) or u = 0) then return '(!:rd!: . 0.0);
    if eqcar(u,'minus) then u := ('!:rd!: . -cdadr u);
    if eqcar(v,'minus) then v := ('!:rd!: . -cdadr v);
    if atom u then u := mkround float u;
    if atom v then v := mkround float v;
    return rdexpt!*(u,v);
  end;

symbolic procedure specrd!:sqrt u;
  begin
    if u = '(!:rd!: . 0.0) or u = 0 then return '(!:rd!: . 0.0);
    if eqcar(u, 'minus) then u := '!:rd!: . -cdadr u;
    if atom u then u := mkround float u;
    return rdsqrt!* u;
  end;

symbolic procedure specrd!:plus(u,v);
  begin
    scalar negsign;
    negsign := 0;
    u := add_minus(u);
    v := add_minus(v);
    if eqcar(u,'minus) then << u := cadr u; negsign := 1>>;
    if eqcar(v,'minus) then << v := cadr v; negsign := negsign +2>>;
    if atom u then u := mkround float u;
    if atom v then v := mkround float v;
    return if negsign = 0 then rd!:plus(u,v)
            else if negsign = 3 then list('minus,rd!:plus(u,v))
             else if negsign =2 then rd!:difference (u,v)
               else rd!:difference(v,u);
  end;


symbolic procedure specrd!:difference(u,v);
  begin
    scalar negsign;
    negsign := 0;
    u := add_minus(u);
    v := add_minus(v);
    if eqcar(u,'minus) then << u := cadr u; negsign := 1>>;
    if eqcar(v,'minus) then << v := cadr v; negsign := negsign +2>>;
    if atom u then u := mkround float u;
    if atom v then v := mkround float v;
    return if negsign = 0 then rd!:difference(u,v)
            else if negsign = 3 then list('minus,rd!:difference(u,v))
             else if negsign =2 then rd!:plus (u,v)
              else list('minus,rd!:plus(v,u));
  end;



symbolic procedure add_minus(u);
  %
  % Things like (!:rd!: . -0.12345) can cause problems as negsign does
  % not notice the negative. This function converts that to
  % {'minus,(!:rd!: . 0.12345)}. Unfortunately it slows things down but
  % it works.
  %
  begin
    if atom u then return u
    else if car u = '!:rd!: and cdr u >= 0 then return u
    else if car u = '!:rd!: and cdr u < 0 then
     return {'minus,('!:rd!: . abs(cdr u))}
    else if car u = 'minus and numberp cadr u then return u
    else if car u = 'minus and cdadr u < 0 then
     return ('!:rd!: . abs(cdadr u))
    else if car u = 'minus then return u
    else if cdr u < 0 then return {'minus,('!:rd!: . abs(cdr u))}
    else return u;
  end;



endmodule;  % svd.

end;
