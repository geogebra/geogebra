module jordan; % Computation of the Jordan normal form of a matrix.                                                              %

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


load!-package 'matrix;  %  Otherwise setmat can fail (letmtr undefined).

load!-package 'arnum; % So we can test whether it's in use or not.

% The following functions may be useful by themselves. They can be     %
% called from algebraic mode: companion, copyinto, deg_sort,           %
% jordanblock, submatrix.                                              %
%                                                                      %

%**********************************************************************%

% jordan(A) computes the Jordan normal form of a square matrix A.
% First jordansymbolic is applied to A, then the symbolic zeroes of the
% characteristic polynomial are replaced by the actual zeroes. The
% zeroes of the characteristic polynomial of A are computed exactly if
% possible. The zeroes which cannot be computed exactly are
% approximated by floating point numbers.

% Specifically:
%
% - jordan(A) will return {J,P,Pinv} where J, P, and Pinv
%   are such that P*J*Pinv = A.
%

% For more details of the algorithm and general workings of jordan
% see jordansymbolic.


symbolic procedure jordan(A);
  begin
    scalar AA,l,tmp,P,Pinv,ans,ans_mat,full_coeff_list,rule_list,
           input_mode;

    matrix_input_test(A);
    if (car size_of_matrix(A)) neq (cadr size_of_matrix(A))
     then rederr "ERROR: expecting a square matrix. ";

    input_mode := get(dmode!*,'dname);
    if input_mode = 'modular then rederr
    "ERROR: jordan does not work with modular on. Try jordansymbolic. ";
    %
    % If arnum is on then we keep it on else we want
    % rational on.
    %
    if input_mode neq 'arnum and input_mode neq 'rational
     then on rational;
    on combineexpt;

    tmp := nest_input(A);
    AA := car tmp;
    full_coeff_list := cadr tmp;

    l := jordansymbolicform(AA, full_coeff_list);

    tmp := jordanform(l, full_coeff_list);
    ans := car tmp;
    P := cadr tmp;
    Pinv := caddr tmp;
    %
    %  Set up rule list for removing nests.
    %
    rule_list := {'co, 2,{'~, 'int}}=>'int when numberp(int);
    for each elt in full_coeff_list do
    <<
      tmp := {'co, 2, {'~, elt}}=>elt;
      rule_list := append (tmp, rule_list);
    >>;
    %
    %  Remove nests.
    %
    let rule_list;
    ans_mat := de_nest_mat(ans);
    P := de_nest_mat(P);
    Pinv := de_nest_mat(Pinv);
    clearrules rule_list;
    %
    % Return to original mode.
    %
    if input_mode neq 'arnum and input_mode neq 'rational then
    <<
      % onoff('nil,t) doesn't work so...
      if input_mode = 'nil then off rational
      else onoff(input_mode,t);
    >>;
    off combineexpt;

    return {'list, ans_mat, P, Pinv};
  end;

flag ('(jordan),'opfn);  %  So it can be used from algebraic mode.



symbolic procedure jordanform(l, full_coeff_list);
  begin
    scalar jj,z,zeroes,P,Pinv,x,tmp,tmp1,de_nest;
    integer n,d;

    P := nth(l,3);
    Pinv := nth(l,4);

    n := length nth(nth(l,2),1);
    x := nth (nth(l,2),2);
    jj := nth(l,1);

    for i:=1:n do
    <<
      d := deg(nth(nth(nth(l,2),1),i),x);
      if d>1 then
      <<
        %
        %  Determine zeroes.
        %
        z := nth(nth(nth(l,2),1),i);
        zeroes := {};
        %  de-nest as solve sometimes fails with nests.
        de_nest := de_nest_list(z,full_coeff_list);
        tmp := algebraic solve(de_nest,x);
        tmp := cdr tmp;  %  Remove algebraic 'list.
        for j:=1:length tmp do
        <<
          if test_for_root_of(nth(tmp,j)) then
          <<
            %  If poly is univariate then can solve using roots.
            if length get_coeffs(de_nest) = 1 then
            <<
              on complex;
              tmp1 := algebraic roots(z);
              off complex;
            >>
            else
            <<
              on fullroots;
              prin2t "***** WARNING: fullroots turned on.";
              prin2t "               May take a while.";
              % system "sleep 3";
              tmp1 := algebraic solve(de_nest,x);
              off fullroots;
              tmp1 := re_nest_list(tmp1,full_coeff_list);
            >>;
            zeroes := append(zeroes,tmp1);
            zeroes := cdr zeroes;
          >>
         else
          <<
            tmp1 := algebraic solve(z,x);
            tmp1 := cdr tmp1;
            zeroes := append(zeroes,{nth(tmp1,j)});
          >>;
        >>;
        %
        %  Substitute zeroes for symbolic names.
        %
        for j:=1:length zeroes do
        <<
          tmp := nth(zeroes,j);
          tmp := caddr tmp;
          jj := algebraic sub(mkid(x,off_mod_reval{'plus,
                              {'times,10,i},j})=tmp, jj);
        >>;

        for j:=1:length zeroes do
        <<
          tmp := nth(zeroes,j);
          tmp := caddr tmp;
          P := algebraic sub(mkid(x,off_mod_reval{'plus,
                             {'times,10,i},j})=tmp,P);
        >>;

        for j:=1:length zeroes do
        <<
          tmp := nth(zeroes,j);
          tmp := caddr tmp;
          Pinv := algebraic sub(mkid(x,off_mod_reval{'plus,
                                {'times,10,i},j})= tmp, Pinv);
        >>;
      >>;
    >>;

    return {jj,P,Pinv};
  end;




symbolic procedure test_for_root_of(input);
  %
  % Goes through a list testing to see if there is a 'root-of
  % contained within it.
  %
  begin
    scalar tmp,copy_input,boolean,tmp1;

    boolean := nil;
    copy_input := input;

    if atom copy_input then <<>>
    else if car copy_input = 'root_of
     then boolean := t
    else while copy_input and boolean = nil do
    <<
      tmp := copy_input;
      tmp :=  car copy_input;
      if tmp = 'root_of then boolean := t
      else while pairp tmp and boolean = nil do
      <<
        tmp1 := tmp;
        if car tmp1 = 'root_of then boolean := t
        else if atom tmp1 then <<>>
        else while pairp tmp1 and boolean = nil do
        <<
          if car tmp1 = 'root_of then boolean := t
          else tmp1 := car tmp1;
        >>;
        tmp := cdr tmp;
      >>;
      copy_input := cdr copy_input;
    >>;

    return boolean;
  end;

flag ('(test_for_root_of),'boolean);

endmodule;

end;

