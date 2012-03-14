module smithex; % Computation of the Smithex normal form of a matrix.                                                              %


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

% The function smithex computes the Smith normal form S of an n by m
% rectangular matrix of univariate polynomials in x.
%
% Specifically:
%
% - smithex(A,x) will return {S,P,Pinv} where S, P, and Pinv
%   are such that P*S*Pinv = A.

symbolic procedure smithex(mat1,x);
  begin
    scalar A,Left,Right,tmp,isclear,g,L,R1,poly1,poly2,quo1,quo2,r,
           lc,tquo,q,full_coeff_list,rule_list,input_mode;
    integer i,j,n,m;

    matrix_input_test(mat1);

    input_mode := get(dmode!*,'dname);
    if input_mode = 'modular
     then rederr "ERROR: smithex does not work with modular on.";
    all_integer_entries_test(mat1);
    if input_mode neq 'arnum and input_mode neq 'rational
     then on rational;
    on combineexpt;

    tmp := nest_input_smith(mat1,x);
    A := car tmp;
    full_coeff_list := cadr tmp;

    n := car size_of_matrix(A); %  No. of rows.
    m := cadr size_of_matrix(A); %  No. of columns.

    Left := make_identity(n,n) ;
    Right := make_identity(m,m);

    for k:=1:min(n,m) do
    <<
      %
      %  Pivot selection from row k to column k.
      %
      i := k; while i <= n and getmat(A,i,k) = 0 do i := i+1;
      j := k; while j <= m and getmat(A,k,j) = 0 do j := j+1;

      if i > n and j > m then <<>>
      else
      <<
        %
        %  Select smallest non-zero entry as pivot.
        %
        for l:=i+1:n do
        <<
          if getmat(A,l,k) = 0 then l := l+1
          else if deg(getmat(A,l,k),x) < deg(getmat(A,i,k),x) then
          i := l;
        >>;

        for l:=j+1:m do
        <<
          if getmat(A,k,l) = 0 then l := l+1
          else if deg(getmat(A,k,l),x) < deg(getmat(A,k,j),x) then
          j := l;
        >>;

        if i <= n and (j > m or deg(getmat(A,i,k),x) <
                                 deg(getmat(A,k,j),x))
    then
        %
        %  Pivot is A(i,k), interchange row k,i if necessary.
        %
        <<
          if i neq k then
          <<

            for l:=k:m do
            <<
              tmp := getmat(A,i,l);
              setmat(A,i,l,getmat(A,k,l));
              setmat(A,k,l,tmp);
            >>;

            for l:=1:n do
            <<
              tmp := getmat(Left,l,i);
              setmat(Left,l,i,getmat(Left,l,k));
              setmat(Left,l,k,tmp);
            >>;

          >>
        >>
        else
        %
        %  Pivot is A(k,j), interchange column k,j if necessary.
        %
        <<
          if j neq k then
          <<

            for l:=k:n do
            <<
              tmp := getmat(A,l,j);
              setmat(A,l,j,getmat(A,l,k));
              setmat(A,l,k,tmp);
            >>;

            for l:=1:m do
            <<
              tmp := getmat(Right,j,l);
              setmat(Right,j,l,getmat(Right,k,l));
              setmat(Right,k,l,tmp);
            >>;

          >>;
        >>;

        isclear := nil;

        while not isclear do
        %
        %  0 out column k from k+1 to n.
        %
        <<
          for i:=k+1:n do
          <<
            if getmat(A,i,k) = 0 then <<>>
            else
            <<
              poly1 := getmat(A,k,k);
              poly2 := getmat(A,i,k);
              tmp := calc_exgcd(poly1,poly2,x);
              g := car tmp;
              L := cadr tmp;
              R1 := caddr tmp;
              quo1 := get_quo(poly1,g);
              quo2 := get_quo(poly2,g);

              for j:=k+1:m do
              <<
                tmp := {'plus,{'times,L,getmat(A,k,j)},{'times,R1,
                        getmat(A,i,j)}};
                setmat(A,i,j,{'plus,{'times,quo1,getmat(A,i,j)},{'times,
                       {'minus,quo2},getmat(A,k,j)}});
                setmat(A,k,j,tmp);
              >>;

              for j:=1:n do
              <<
                tmp := {'plus,{'times,quo1,getmat(Left,j,k)},
                        {'times,quo2,getmat(Left,j,i)}};
                setmat(Left,j,i,{'plus,{'times,{'minus,R1},
                       getmat(Left,j,k)},{'times,L,getmat(Left,j,i)}});
                setmat(Left,j,k,tmp);
              >>;

              setmat(A,k,k,g);
              setmat(A,i,k,0);
            >>;
          >>;

          isclear := t;
          %
          % 0 out row k from k+1 to m.
          %
          for i:=k+1:m do
          <<
            q := get_quo(getmat(A,k,i),getmat(A,k,k));
            setmat(A,k,i,get_rem(getmat(A,k,i),getmat(A,k,k)));

            for j:=1:m do
            <<
              setmat(Right,k,j,{'plus,getmat(Right,k,j),{'times,q,
                     getmat(Right,i,j)}});
            >>;

          >>;

          for i:=k+1:m do
          <<
            if getmat(A,k,i) = 0 then <<>>
            else
            <<
              poly1 := getmat(A,k,k);
              poly2 := getmat(A,k,i);
              tmp := calc_exgcd(poly1,poly2,x);
              g := car tmp;
              L := cadr tmp;
              R1 := caddr tmp;
              quo1 := get_quo(poly1,g);
              quo2 := get_quo(poly2,g);

              for j:=k+1:n do
              <<
                tmp := {'plus,{'times,L,getmat(A,j,k)},{'times,R1,
                        getmat(A,j,i)}};
                setmat(A,j,i,{'plus,{'times,quo1,getmat(A,j,i)},{'times,
                       {'minus,quo2},getmat(A,j,k)}});
                setmat(A,j,k,tmp);
              >>;

              for j:=1:m do
              <<
                tmp := {'plus,{'times,quo1,getmat(Right,k,j)},
                        {'times,quo2,getmat(Right,i,j)}};
                setmat(Right,i,j,{'plus,{'times,{'minus,R1},
                       getmat(Right,k,j)},
                       {'times,L,getmat(Right,i,j)}});
                setmat(Right,k,j,tmp);
              >>;

              setmat(A,k,k,g);
              setmat(A,k,i,0);
              isclear := nil;
            >>;
          >>;

        >>;
      >>;
    >>;

    r := 0;
    %
    %  At this point, A is diagonal: some A(i,i) may be zero.
    %  Move non-zero's up also making all entries unit normal.
    %
    for i:=1:min(n,m) do
    <<
      if getmat(A,i,i) neq 0 then
      <<
        r := r+1;
        % Watch out for integers giving lc = 0.
        if lcof(getmat(A,i,i),x) = 0  then lc := getmat(A,i,i)
        else lc := lcof(getmat(A,i,i),x);
        setmat(A,r,r,{'quotient,getmat(A,i,i),lc});

        if i = r then
        <<
          for j:=1:m do
          <<
            setmat(Right,i,j,{'times,getmat(Right,i,j),lc});
          >>;
        >>
        else
        <<
          setmat(A,i,i,0);
          for j:=1:n do
          <<
            tmp := getmat(Left,j,r);
            setmat(Left,j,r,getmat(Left,j,i));
            setmat(Left,j,i,tmp);
          >>;
          for j:=1:m do
          <<
            tmp := {'times,getmat(Right,i,j),lc};
            setmat(Right,i,j,{'quotient,getmat(Right,r,j),lc});
            setmat(Right,r,j,tmp);
          >>;
        >>;

      >>;
    >>;
    %
    %  Now make A(i,i) | A(i+1,i+1) for 1 <= i < r.
    %
    for i:=1:r-1 do
    <<
      j:=i+1;
      <<
        while getmat(A,i,i) neq 1 and j <= r do
        <<
          poly1 := getmat(A,i,i);
          poly2 := getmat(A,j,j);
          tmp := calc_exgcd(poly1,poly2,x);
          g := car tmp;
          L := cadr tmp;
          R1 := caddr tmp;
          quo1 := get_quo(poly1,g);
          quo2 := get_quo(poly2,g);

          setmat(A,i,i,g);
          setmat(A,j,j,{'times,quo1,getmat(A,j,j)});

          for k:=1:n do
          <<
            tmp := {'plus,{'times,quo1,getmat(Left,k,i)},{'times,quo2,
                    getmat(Left,k,j)}};
            setmat(Left,k,j,{'plus,{'times,{'minus,R1},
                   getmat(Left,k,i)},{'times,L,getmat(Left,k,j)}});
            setmat(Left,k,i,tmp);
          >>;

          for k:=1:m do
          <<
            tquo := {'times,R1,quo2};
            tmp := {'plus,{'times,{'plus,1,{'minus,tquo}},
                    getmat(Right,i,k)},{'times,tquo,getmat(Right,j,k)}};
            setmat(Right,j,k,{'plus,{'minus,getmat(Right,i,k)},
                   getmat(Right,j,k)});
            setmat(Right,i,k,tmp);
          >>;

          j := j+1;
        >>;
      >>;
    >>;
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
    A := de_nest_mat(A);
    Left := de_nest_mat(Left);
    Right := de_nest_mat(Right);
    clearrules rule_list;
    %
    % Return to original mode.
    %
    if input_mode neq 'rational and input_mode neq 'arnum then
    <<
      % onoff('nil,t) doesn't work so...
      if input_mode = 'nil then off rational
      else onoff(input_mode,t);
    >>;
    off combineexpt;

    return {'list,A,Left,Right};
  end;

flag ('(smithex),'opfn);  %  So it can be used from algebraic mode.



symbolic procedure get_coeffs_smith(poly,x);
  %
  % Gets all kernels in a poly.
  %
  % This is the version form smithex. The polynomials are
  % known to be in x (smithex(matrix,x)) so this is removed
  % from the output so as to not be nested in
  % nest_input_smith.
  %
  begin
    scalar ker_list_num,ker_list_den,new_list;

    ker_list_num := kernels !*q2f simp reval num poly;
    ker_list_den := kernels !*q2f simp reval den poly;

    ker_list_num := union(ker_list_num,ker_list_den);

    if ker_list_num = nil then new_list := nil
    else
    <<
      %  Remove x.
      for i:=1:length ker_list_num do
      <<
        if car ker_list_num = x then new_list := new_list
        else new_list := car ker_list_num.new_list;
        ker_list_num := cdr ker_list_num;
      >>;
    >>;

    return new_list;
  end;




symbolic procedure nest_input_smith(A,x);
  %
  % Takes a matrix and converts all elements into nested form.
  % Also finds all coefficients and returns them in a list.
  %
  % With Smithex all polynomials are in x (input by user) so
  % this is removed from the full_coeff_list (see get_coeffs),
  % and is thus not nested.
  %
  begin
    scalar tmp,coeff_list,full_coeff_list,AA;

    integer row_dim,col_dim;

    full_coeff_list := nil;
    coeff_list := nil;

    AA := copy_mat(A);

    row_dim := car size_of_matrix(AA);
    col_dim := cadr  size_of_matrix(AA);

    for i := 1:row_dim do
    <<
      for j := 1:col_dim do
      <<
        coeff_list := get_coeffs_smith(getmat(AA,i,j),x);
        if coeff_list = nil then <<>>
        else full_coeff_list := union(coeff_list,full_coeff_list);
        for each elt in coeff_list do
        <<
          tmp := {'co,2,elt};
          setmat(AA,i,j,algebraic (sub(elt=tmp,getmat(AA,i,j))));
        >>;
      >>;
    >>;


    return {AA,full_coeff_list};
  end;




switch int_test;
symbolic procedure all_integer_entries_test(mat1);
  begin
    on int_test;
    for i:=1:car size_of_matrix(mat1) do
    <<
      for j:=1:cadr size_of_matrix(mat1) do
      <<
        if not numberp getmat(mat1,i,j) and !*int_test
         then off int_test;
      >>;
    >>;
% Note that this is one of the very very few places in Reduce where
% a string with an embedded newline is used. I will leave it in so that
% it continues to test behaviour in that situation. But elsewhere people
% will have used two calls to prin2t rather than one!
    if !*int_test then prin2t
    "*** WARNING: all matrix entries are integers.
    If calculations in Z(the integers) are required, use smithex_int.";
    % system "sleep 3";
  end;

endmodule;

end;

