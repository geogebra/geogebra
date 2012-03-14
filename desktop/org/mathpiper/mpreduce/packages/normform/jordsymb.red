module jordsymb; % Computation of the Jordan normal form of a matrix.                                                              %

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


% The function jordansymbolic computes the Jordan normal form J of a
% matrix A, the transformation matrix P and its inverse P^(-1).
% Here symbolic names are used for the zeroes of the characteristic
% polynomial p not in K. Also a list of irreducible factors of p is
% returned.
%
% Specifically:
%
% - jordansymbolic(A) will return {J,l,P,Pinv} where J, P, and
%   Pinv are such that P*J*Pinv = A. J is calculated with symbolic
%   names if necessary. l is {ll,x} where x is a name and ll
%   is a list of irreducible factors of p(x). If symbolic names are
%   used then xij is a zero of ll(i).

% Global description of the algorithm:
%
% For a given n by n matrix A over a field K, we car compute the
% rational Jordan normal form R of A. Then we compute the Jordan normal
% form of R, which is also the Jordan normal form of A.
% car consider the case where R=C(p), the companion matrix of the
% monic, irreducible polynomial p=x^n+p(n-1)*x^(n-1)+..+p1*x+p0.
% If y is a zero of p then
% (y^(n-1)+p(n-1)*y^(n-2)+..+p2*y+p1,y^(n-2)+p(n-1)*y^(n-3)+..+p3*y+p2,
% ....,y^2+p(n-1)*y+p(n-2),y+p(n-1),1)
% is an eigenvector of R with eigenvalue y.
%
% Let v1     = x^(n-1)+p(n-1)*x^(n-2)+..+p2*x+p1,
%     v2     = x^(n-2)+p(n-1)*x^(n-3)+..+p3*x+p2,
%     ...
%     v(n-2) = x^2+P(n-1)*x+p(n-2),
%     v(n-1) = x+p(n-1),
%     vn     = 1.
%
% If y1,..,yn are the different zeroes of p in a splitting field of p
% over K (we asssume that p is separable, this is always true if K is a
% perfect field) we get:
%
%       inverse(V)*R*V=diag(y1,..,yn),
%
% where
%
%          [ v1(y1) v1(y2) ... v1(yn) ]
%          [ v2(y1) v2(y2) ... v2(yn) ]
%      V = [  ...    ...   ...  ...   ]
%          [  ...    ...   ...  ...   ]
%          [ vn(y1) vn(y2) ... vn(yn) ]
%
%
% One can prove that
%
%      [1 y1 ... y1^(n-1)] [v1(y1) v1(y2) ... v1(yn)]
%      [1 y2 ... y2^(n-1)] [v2(y1) v2(y2) ... v2(yn)]
%      [.................] [........................] =
%      [.................] [........................]
%      [1 yn ... yn^(n-1)] [vn(y1) vn(y2) ... vn(yn)]
%
%    = diag(diff(p,x)(y1),diff(p,x)(y2),...,diff(p,x)(yn)).
%
% If s and t are such that s*p+t*diff(p,x)=1 then we get
%
%                                           [1 y1 ... y1^(n-1) ]
%                                           [1 y2 ... y2^(n-1) ]
%    inverse(V)=diag(t(y1),t(y2),...,t(yn))*[................. ]
%                                           [................. ]
%                                           [1 yn ... yn^(n-1) ]
%
% Let Y=diag(y1,..,yn). From V^(-1)*R*V=Y it follows that
%
%                          [C(p)  I             ]
%                          [    C(p)  I         ]
%   diag(V^(-1),..,V^(-1))*[          .   .     ]*diag(V,..,V)=
%                          [            C(p)  I ]
%                          [                C(p)]
%
%          [ Y I       ]
%          [   Y I     ]
%        = [     . .   ]
%          [       Y I ]
%          [         Y ]
%
% It is now easy to see that to get our general result, we only have to
% permute diag(V,..,V) and diag(V^(-1),..,V^(-1)).

% looking_good controls formating output to print the greek character
% xi instead of lambda. At present xr does not support indexing of
% lambda but it does for all other greek letters, which is the reason
% for this switch.
%
% Only  helpful when using xr.

switch looking_good;


switch balanced_was_on;
symbolic procedure jordansymbolic(A);
  begin
    scalar AA,P,Pinv,tmp,ans_mat,ans_list,full_coeff_list,rule_list,
           output,input_mode;

    matrix_input_test(A);
    if (car size_of_matrix(A)) neq (cadr size_of_matrix(A))
     then rederr "ERROR: expecting a square matrix. ";

    if !*balanced_mod then
    <<
      off balanced_mod;
      on balanced_was_on;
    >>;

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

    tmp := jordansymbolicform(AA,full_coeff_list);
    ans_mat := car tmp;
    ans_list := cadr tmp;
    P := caddr tmp;
    Pinv := caddr rest tmp;
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
    % Remove nests.
    %
    let rule_list;
    ans_mat := de_nest_mat(ans_mat);
    car ans_list := append({'list},car ans_list);
    ans_list := append({'list},ans_list);
    cadr ans_list := for each elt in cadr ans_list collect reval elt;
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
    if !*balanced_was_on then on balanced_mod;
    off combineexpt;

    output := {'list,ans_mat,ans_list,P,Pinv};
    if !*looking_good then output := looking_good(output);
    return output;
  end;

flag ('(jordansymbolic),'opfn);  %  So it can be used from
                                 %  algebraic mode.



symbolic procedure jordansymbolicform(A,full_coeff_list);
  begin
    scalar l,R,TT,Tinv,S,Sinv,tmp,P,Pinv,invariant;

    tmp := ratjordanform(A,full_coeff_list);
    R := car tmp;
    TT := cadr tmp;
    Tinv := caddr tmp;

    tmp := ratjordan_to_jordan(R);
    l := car tmp;
    S := cadr tmp;
    Sinv := caddr tmp;

    P := off_mod_reval {'times,TT,S};
    Pinv := off_mod_reval {'times,Sinv,Tinv};

    invariant := invariant_to_jordan(nth(l,1));

    return {invariant,nth(l,2),P,Pinv};
  end;




symbolic procedure find_companion(R,rr,x);
  begin
    scalar  p;
    integer row_dim,k;

    row_dim := car size_of_matrix(R);
    k := rr+1;

    while k<=row_dim and getmat(R,k,k-1)=1 do k:=k+1;

    p := 0;
    for j:=rr:k-1 do
    <<
      p:={'plus,p,{'times,{'minus,getmat(R,j,k-1)},{'expt,x,j-rr}}};
    >>;
    p := {'plus,p,{'expt,x,k-rr}};

    return p;
  end;




symbolic procedure find_ratjblock(R,rr,x);
  begin
    scalar p,continue;
    integer k,e,row_dim;

    row_dim := car size_of_matrix(R);
    p := reval find_companion(R,rr,x);
    e := 1;
    k:= off_mod_reval({'plus,rr,deg(p,x)});

    continue := t;
    while continue do
    <<
      if k>row_dim then continue := nil;
      if identitymatrix(R,k-deg(p,x),k,deg(p,x)) then
      <<
        e:=e+1;
        k:=k+deg(p,x);
      >>
      else
      <<
        continue := nil;
      >>;
    >>;

    return({p,e});
  end;




symbolic procedure identitymatrix(A,i,j,m);
  %
  % Tests if the submatrix of A, starting at position (i,j) and of
  % square size m, is an identity matrix.
  %
  begin
    integer row_dim;

    row_dim := car size_of_matrix(A);
    if i+m-1>row_dim or j+m-1>row_dim then return nil
    else
    <<
      if submatrix(A,{i,i+m-1},{j,j+m-1}) = make_identity(m,m) then
         return t
      else return nil;
    >>;
  end;

flag ('(identitymatrix),'boolean);



symbolic procedure invariant_to_jordan(invariant);
  begin
    scalar block_list;
    integer n,m;

    n := length invariant;
    block_list := {};

    for i:=1:n do
    <<
      m := length nth(nth(invariant,i),2);
      for j:=1:m do
      <<
        block_list := append(block_list,
                      {jordanblock(nth(nth(invariant,i),1),
                      nth(nth(nth(invariant,i),2),j))});
      >>;
    >>;
    return (reval {'diagi,block_list});
  end;




symbolic procedure jordanblock(const,mat_dim);
  %
  % Takes a constant (const) and an integer (mat_dim) and creates
  % a jordan block of dimension mat_dim x mat_dim.
  %
  % Can be used independently from algebraic mode.
  %
  begin
    scalar JB;

    JB := mkmatrix(mat_dim,mat_dim);
    for i:=1:mat_dim do
    <<
      for j:=1:mat_dim do
      <<
        if i=j then
        <<
          setmat(JB,i,j,const);
          if i<mat_dim then setmat(JB,i,j+1,1);
        >>;
      >>;
    >>;

    return JB;
  end;

flag ('(jordanblock),'opfn); %  So it can be used independently
                             %  from algebraic mode.



switch mod_was_on;
symbolic procedure ratjordan_to_jordan(R);
  begin
    scalar prim_inv,TT,Tinv,Tinvlist,Tlist,exp_list,invariant,p,partT,
           partTinv,s1,t1,v,w,sum1,tmp,S,Sinv,x;
    integer nn,n,d;
    %
    % lambda would be better but as yet reduce can't output lambda with
    % indices (under discussion), so we use xi. If it is decided that
    % lambda can be used with indices then change xi to lambda both
    % here and in the functions looking_good and make_rule.
    %
    if !*looking_good then x := 'xi
    else x := 'lambda;

    prim_inv := ratjordan_to_priminv(R,x);

    invariant := {};
    Tlist := {};
    Tinvlist := {};

    nn := length prim_inv;
    for i:=1:nn do
    <<
      p := nth(nth(prim_inv,i),1);
      exp_list := nth(nth(prim_inv,i),2);
      d := off_mod_reval(deg(p,x));
      if d=1 then
      <<
        invariant := append(invariant,{{reval {'minus,coeffn(p,x,0)},
                            exp_list}});
      >>
      else
      <<
        for j:=1:d do
        <<
          invariant := append(invariant,{{mkid(x,off_mod_reval{'plus,
                              {'times,10,i},j}),exp_list}});
        >>;
      >>;
      %
      % Compute eigenvector of C(p) with eigenvalue x.
      %
      v := mkvect(d);
      putv(v,d,1);
      for j:=d-1 step -1 until 1 do
      <<
        tmp := 0;
        sum1 := 0;
        for k:=j:d-1 do
        <<
          tmp := reval{'times,coeffn(p,x,k),{'expt,x,k-j}};
          sum1 := reval{'plus,sum1,tmp};
        >>;
        putv(v,j,reval {'plus,sum1,{'expt,x,d-j}});
      >>;

      sum1 := 0;
      for j:=1:length exp_list do
      <<
        tmp := reval nth(exp_list,j);
        sum1 := reval {'plus,sum1,tmp};
      >>;
      n := sum1;

      partT := mkmatrix(n*d,n);
      for j:=1:n do
      <<
        for k:=1:d do
        <<
          setmat(partT,(j-1)*d+k,j,getv(v,k));
        >>;
      >>;

      TT := mkmatrix(n*d,n*d);
      if d=1 then
      <<
        %
        % off modular else the mkid number part will be calculated
        % in modular mode.
        %
        if !*modular then << off modular; on mod_was_on; >>;
        TT := copyinto(algebraic (sub({x=-coeffn(p,x,0)},partT)),
                       TT,1,1);
        if !*mod_was_on then << on modular; off mod_was_on; >>;
      >>
      else for j:=1:d do
      <<
        %
        % off modular else the mkid number part will be calculated
        % in modular mode.
        %
        if !*modular then << off modular; on mod_was_on; >>;
        TT := copyinto(algebraic sub(x=mkid(x,off_mod_reval{'plus,
                       {'times,10,i},j}),partT),TT,1,(j-1)*n+1);
        if !*mod_was_on then << on modular; off mod_was_on; >>;
      >>;
      Tlist := append(Tlist,{TT});

      tmp := algebraic df(p,x);
      tmp := calc_exgcd(p,tmp,x);
      s1 := cadr tmp;
      t1 := caddr tmp;
      w := mkvect(d);
      putv(w,1,t1);

      for j:=2:d do
      <<
        putv(w,j,get_rem(reval{'times,x,getv(w,j-1)},p));
      >>;

      partTinv := mkmatrix(n,n*d);
      for j:=1:n do
      <<
        for k:=1:d do
        <<
          setmat(partTinv,j,(j-1)*d+k,getv(w,k));
        >>;
      >>;

      Tinv := mkmatrix(n*d,n*d);
      if d=1 then
      <<
        %
        % off modular else the mkid number part will be calculated
        % in modular mode.
        %
        if !*modular then << off modular; on mod_was_on; >>;
        Tinv := reval copyinto(algebraic sub(x=-coeffn(p,x,0),partTinv)
                               ,Tinv,1,1);
        if !*mod_was_on then << on modular; off mod_was_on; >>;
      >>
      else for j:=1:d do
      <<
        %
        % off modular else the mkid number part will be calculated
        % in modular mode.
        %
        if !*modular then << off modular;  on mod_was_on; >>;
        Tinv := reval copyinto(algebraic sub(x=mkid(x,off_mod_reval
                {'plus,{'times,10,i},j}),partTinv),Tinv,(j-1)*n+1,1);
        if !*mod_was_on then << on modular; off mod_was_on; >>;
      >>;
      Tinvlist := append(Tinvlist,{Tinv});

    >>;

    S := reval{'diagi,Tlist};
    Sinv := reval{'diagi,Tinvlist};
    tmp := {for i:=1:nn collect nth(nth(prim_inv ,i),1)};
    tmp := append(tmp,{x});
    tmp := append({invariant},{tmp});

    return {tmp,S,Sinv};
  end;




symbolic procedure ratjordan_to_priminv(R,x);
  %
  % ratjordan_to_priminv(R,x) computes the primary invariant of a matrix
  % R which is in rational Jordan normal form.
  %
  begin
    scalar p,plist,exp_list,l,prim_inv;
    integer n,rr,ii,nn;

    n := car size_of_matrix(R);
    rr := 1;

    plist := {};
    while rr<=n do
    <<
      l := find_ratjblock(R,rr,x);
      plist := append(plist,{l});
      rr := off_mod_reval({'plus,rr,{'times,nth(l,2),deg(nth(l,1),x)}});
    >>;

    p := reval nth(nth(plist,1),1);
    exp_list := {nth(nth(plist,1),2)};
    prim_inv := {};
    nn := length plist;
    ii := 2;
    while ii<=nn do
    <<
      if reval nth(nth(plist,ii),1) = p then
      <<
        exp_list := append(exp_list,{nth(nth(plist,ii),2)})
      >>
      else
      <<
        prim_inv := append(prim_inv,{{p,exp_list}});
        p := reval nth(nth(plist,ii),1);
        exp_list := {nth(nth(plist,ii),2)};
      >>;
      ii := ii+1;
    >>;
    prim_inv := append(prim_inv,{{p,exp_list}});

    return prim_inv;
  end;




symbolic procedure submatrix(A,row_list,col_list);
  %
  % Creates the submatrix of A from rows  p to q (row_list = {p,q})
  % and columns r to s (col_list = {r,s}).
  %
  % Can be used independently from algebraic mode.
  %
  begin
    scalar AA;
    integer row_dim,col_dim,car_row,last_row,car_col,last_col,
            A_row_dim,A_col_dim;

    matrix_input_test(A);

    %  If algebraic input remove 'list.
    if car row_list = 'list then row_list := cdr row_list;
    if car col_list = 'list then col_list := cdr col_list;

    car_row := car row_list;
    last_row := cadr row_list;
    row_dim := last_row - car_row + 1;
    car_col := car col_list;
    last_col := cadr col_list;
    col_dim := last_col - car_col + 1;

    A_row_dim := car size_of_matrix(A);
    A_col_dim := cadr size_of_matrix(A);
    if car_row = 0 or last_row = 0 then rederr
     {"0 is out of range for ",A,". The car row is labelled 1."};
    if car_col = 0 or last_col = 0 then rederr
     {"0 is out of range for",A,". The car column is labelled 1."};
    if car_row > A_row_dim then rederr
     {A,"doesn't have",car_row,"rows."};
    if last_row > A_row_dim then rederr
     {A,"doesn't have",last_row,"rows."};
    if car_col > A_col_dim then rederr
     {A,"doesn't have",car_col,"columns."};
    if last_col > A_col_dim then rederr
     {A,"doesn't have",last_col,"columns."};

    AA := mkmatrix(row_dim,col_dim);
    for i:=1:row_dim do
    <<
      for j:=1:col_dim do
      <<
        setmat(AA,i,j,getmat(A,i+car_row-1,j+car_col-1));
      >>;
    >>;

    return AA;
  end;

flag ('(submatrix),'opfn);  %  So it can be used independently
                            %  from algebraic mode.



symbolic procedure looking_good(output);
  %
  % Converts output for correct display of indices with greek
  % font. Only used when switch looking_good is on, which is only on
  % when using xr.
  %
  % xiab => xi(a,b) is used instead of xiab => xi(ab) to reduce problems
  % when using modular arithmetic. In mod 17 (for example) xi(21) will
  % get converted to xi(2,1) but the alternative would give xi(4) -
  % wrong! Unfortunately the alternative probably looks a bit nicer but
  % there you go. If the modulus is <= 7 then this may give problems,
  % eg: xi55 in mod 5 will give xi(0,0). These cases will be very rare
  % but if they occur turn OFF looking_good.
  %
  begin
    scalar rule_list;

    algebraic operator xi;
    algebraic print_indexed(xi);
    %
    % Create rule list to convert xiab => xi(a,b) for a,b:=1:9.
    %
    rule_list := make_rule();

    let rule_list;
    output := reval output;
    clearrules rule_list;

    return output;
  end;




symbolic procedure make_rule();
  begin
    scalar rule_list,tmp,tmp1;

    rule_list := {};
    for i:=1:9 do
      <<
        for j:=1:9 do
        <<
          tmp1 := reval mkid('xi,i);
          tmp1 := reval mkid(tmp1,j);
          tmp := {'replaceby,tmp1,{'xi,i,j}};
          rule_list := append(rule_list,{tmp});
        >>;
      >>;
      rule_list := append({'list},rule_list);
      return rule_list;
  end;

endmodule;

end;

