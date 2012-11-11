module linalg;   % The Linear Algebra package.

%**********************************************************************%
%                                                                      %
% Author: Matt Rebbeck, March-July 1994 (at ZIB).                      %
% Modifications by: Walter Tietze.
%                                                                      %
% Please report bugs to: Winfried Neun,                                %
%                        Konrad-Zuse-Zentrum                           %
%                           fuer Informationstechnik Berlin            %
%                        Heilbronner Str. 10                           %
%                        10711 Berlin - Wilmersdorf                    %
%                        Federal Republic of Germany                   %
%                                                                      %
%                (email) neun@sc.ZIB-Berlin.de                         %
%                                                                      %
%                                                                      %
%                                                                      %
% This package provides a selection of useful functions in the field   %
% of linear algebra:                                                   %
%                                                                      %
%   add_columns      add_rows       add_to_columns    add_to_rows      %
%   augment_columns  band_matrix    block_matrix      char_matrix      %
%   char_poly        cholesky       coeff_matrix      column_dim       %
%   companion        copy_into      diagonal          extend           %
%   get_columns      get_rows       gram_schmidt      hermitian_tp     %
%   hessian          hilbert        mat_jacobian      jordan_block     %
%   lu_decom         make_identity  matrix_augment    matrixp          %
%   matrix_stack     minor          mult_column       mult_row         %
%   pivot            pseudo_inverse random_matrix     remove_columns   %
%   remove_rows      row_dim        rows_pivot        simplex          %
%   squarep          stack_rows     sub_matrix        svd              %
%   swap_columns     swap_entries   swap_rows         symmetricp       %
%   toeplitz         vandermonde    kronecker_product                  %
%                                                                      %
%                                                                      %
%                                                                      %
% The package implements the following switches:                       %
%                                                                      %
%  imaginary     \                                                     %
%  lower_matrix   \                                                    %
%  not_negative    )  for details see the random_matrix comments.      %
%  only_integer   /                                                    %
%  upper_matrix  /                                                     %
%                                                                      %
%  fast_la  (see below).                                               %
%                                                                      %
%                                                                      %
%                                                                      %
% For further details about the linear algebra package see the         %
% linear_algebra.tex file.                                             %
%                                                                      %
%                                                                      %
%                                                                      %
% NB: The functions in this package are written to be used from the    %
% user level. Some of them may well need a bit of fiddling with to get %
% them to work from symbolic mode.                                     %
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


load_package matrix;

create!-package('(linalg lamatrix gramschm ludecom cholesky svd
                  simplex tadjoint), '(contrib linalg));

switch fast_la;  % If ON, then the following functions will be faster:

%   add_columns      add_rows         augment_columns  column_dim      %
%   copy_into        make_identity    matrix_augment   matrix_stack    %
%   minor            mult_column      mult_row         pivot           %
%   remove_columns   remove_rows      rows_pivot       squarep         %
%   stack_rows       sub_matrix       swap_columns     swap_entries    %
%   swap_rows        symmetricp                                        %

% This is basically done by removing some error checking and doesn't
% speed things up too much. You'll need to be making alot of calls to
% see the difference. If you get strange error messages with fast_la
% ON then thoroughly check your input.



symbolic smacro procedure my_reval(n);
  %
  % Only revals if it needs to.
  %
  if fixp(n) then n else reval(n);



symbolic procedure swap_elt(in_list,elt1,elt2);
  %
  % Swaps elt elt1 with elt elt2 in in_list.
  %
  % NB: destructive.
  %
  begin
    scalar bucket;
    bucket := nth(in_list,elt1);
    nth(in_list,elt1) := nth(in_list,elt2);
    nth(in_list,elt2) := bucket;
  end;



symbolic procedure row_dim(in_mat);
  %
  % Finds row dimension of a matrix.
  %
  begin
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in row_dim: input should be a matrix.";
    return first size_of_matrix(in_mat);
  end;



symbolic procedure column_dim(in_mat);
  %
  % Finds column dimension of a matrix.
  %
  begin
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in column_dim: input should be a matrix.";
    return second size_of_matrix(in_mat);
  end;

flag('(row_dim,column_dim),'opfn);



symbolic procedure matrixp(A);
  %
  % Tests if input is a matrix (boolean).
  %
  if not eqcar(A,'mat) then nil else t;

flag('(matrixp),'boolean);
flag('(matrixp),'opfn);



symbolic procedure size_of_matrix(A);
  %
  % Takes matrix and returns list {no. of rows, no. of columns}.
  %
  begin
    integer row_dim,column_dim;
    row_dim := -1 + length A;
    column_dim := length cadr A;
    return {row_dim,column_dim};
  end;



symbolic procedure companion(poly,x);
  %
  % Takes as input a monic univariate polynomial in a variable x.
  % Returns a companion matrix associated with the polynomial poly(x).
  %
  % If C := companion(p,x) and p is a0+a1*x+...+x^n (a univariate monic
  % polynomial), them C(i,n) = -coeff(p,x,i-1), C(i,i-1) = 1 (i=2..n)
  % and C(i,j) = 0 for all other i and j.
  %
  begin
    scalar mat1;
    integer n;
    n := deg(poly,x);
    if my_reval coeffn(poly,x,n) neq 1 then msgpri
      ("Error in companion(first argument): Polynomial",
      poly, "is not monic.",nil,t);

    mat1 := mkmatrix(n,n);
    setmat(mat1,1,n,{'minus,coeffn(poly,x,0)});
    for i:=2:n do
    <<
      setmat(mat1,i,i-1,1);
    >>;
    for j:=2:n do
    <<
      setmat(mat1,j,n,{'minus,coeffn(poly,x,j-1)});
    >>;
    return mat1;
  end;



symbolic procedure find_companion(R,x);
  %
  % Given a companion matrix, find_companion will return the associated
  % polynomial.
  %
  begin
    scalar  p;
    integer rowdim,k;
    if not matrixp(R) then rederr
     {"Error in find_companion(first argument): should be a matrix."};
    rowdim := row_dim(R);
    k := 2;
    while k<=rowdim and getmat(R,k,k-1)=1 do k:=k+1;
    p := 0;
    for j:=1:k-1 do
    <<
      p:={'plus,p,{'times,{'minus,getmat(R,j,k-1)},{'expt,x,j-1}}};
    >>;
    p := {'plus,p,{'expt,x,k-1}};
    return p;
  end;

flag('(companion,find_companion),'opfn);



symbolic procedure jordan_block(const,mat_dim);
  %
  % Takes a constant (const) and an integer (mat_dim) and creates
  % a jordan block of dimension mat_dim x mat_dim.
  %
  begin
    scalar JB;
    if not fixp mat_dim then rederr
     "Error in jordan_block(second argument): should be an integer.";
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

flag ('(jordan_block),'opfn);



symbolic procedure sub_matrix(A,row_list,col_list);
  %
  % Removes the sub_matrix from A consisting of the rows in row_list and
  % the columns in col_list. (Both row_list and col_list can be single
  % integer values).
  %
  begin
    scalar new_mat;
    if not !*fast_la and not matrixp(A) then rederr
     "Error in sub_matrix(first argument): should be a matrix.";
    new_mat := stack_rows(A,row_list);
    new_mat := augment_columns(new_mat,col_list);
    return new_mat;
  end;

% flag('(sub_matrix),'opfn);

rtypecar sub_matrix;


symbolic procedure copy_into(BB,AA,p,q);
  %
  % Copies matrix BB into AA with BB(1,1) at AA(p,q).
  %
  begin
    scalar A,B;
    integer m,n,r,c;
    if not !*fast_la then
    <<
      if not matrixp(BB) then rederr
       "Error in copy_into(first argument): should be a matrix.";
      if not matrixp(AA) then rederr
       "Error in copy_into(second argument): should be a matrix.";
      if not fixp p then rederr
       "Error in copy_into(third argument): should be an integer.";
      if not fixp q then rederr
       "Error in copy_into(fourth argument): should be an integer.";
      if p = 0 or q = 0 then
      <<
        prin2t
         "***** Error in copy_into: 0 is out of bounds for matrices.";
        prin2t
         "      The top left element is labelled (1,1) and not (0,0).";
        return;
      >>;
    >>;
    m := row_dim(AA);
    n := column_dim(AA);
    r := row_dim(BB);
    c := column_dim(BB);
    if not !*fast_la and (r+p-1>m or c+q-1>n) then
    <<
      % Only print offending matrices if they're not too big.
      if m*n<26 and r*c<26 then
      <<
        prin2t "***** Error in copy_into: the matrix";
        matpri(BB);
        prin2t "      does not fit into";
        matpri(AA);
        prin2  "      at position ";
        prin2 p;
        prin2 ",";
        prin2 q;
        prin2t ".";
        return;
      >>
      else
      <<
        prin2 "***** Error in copy_into: first matrix does not fit ";
        prin2 "      into second matrix at defined position.";
        return;
      >>;
    >>;
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

flag ('(copy_into),'opfn);



symbolic procedure copy_mat(u);
   if pairp u then cons (copy_mat car u, copy_mat cdr u) else u;



put('diagonal,'psopfn,'diagonal1); % To allow variable input.
symbolic procedure diagonal1(mat_list);
  %
  % Can take either a list of arguments or the arguments seperately.
  %
  % Takes any number of either scalar entries or square matrices and
  % creates the diagonal.
  %
  begin
    scalar diag_mat;
    if pairp mat_list and pairp car mat_list and caar mat_list = 'list
     then mat_list := cdar mat_list;
    mat_list := for each elt in mat_list collect reval elt;
    for each elt in mat_list do
    <<
      if matrixp(elt) and not squarep(elt) then
      <<
        % Only print offending matrix if it's not too big.
        if row_dim(elt)<5 or column_dim(elt)> 5 then
        <<
          prin2t "***** Error in diagonal: ";
          matpri(elt);
          prin2t "      is not a square matrix.";
          rederr "";
        >>
        else
         rederr "Error in diagonal: input contains non square matrix.";
      >>;
    >>;
    diag_mat := diag({mat_list});
    return diag_mat;
  end;



symbolic procedure diag(uu);
  %
  % Takes square or scalar matrix entries and creates a matrix with
  % these matrices on the diagonal.
  %
  % What a horrible way to do it!
  %
  begin
    scalar bigA,arg,input,u;
    integer nargs,n,Aidx,stp,bigsize,smallsize;

    u := car uu;
    input := u;
    bigsize:=0;

    nargs:=length input;
    for i:=1:nargs do
    <<
      arg:=car input;
      % If scalar entry.
      if algebraic length(arg) = 1 or eqcar(arg,'quotient)
                 then bigsize:=bigsize+1
      else
      <<
        bigsize:=bigsize+row_dim(arg);
      >>;
      input := cdr input;
    >>;

    bigA := mkmatrix(bigsize,bigsize);
    Aidx:=1;
    input := u;
    for k:=1:nargs do
    <<
      arg:=car input;
      % If scalar entry.
      if algebraic length(arg) = 1 or eqcar(arg,'quotient) then
      <<
        setmat(bigA,Aidx,Aidx,arg);
        Aidx:=Aidx+1;
        input := cdr input;
      >>
      else
      <<
        smallsize:= row_dim(arg);
        stp:=smallsize+Aidx-1;
        for i:=Aidx:stp do
        <<
          for j:=Aidx:stp do
          <<
            arg:=car input;
            % Find (i-Aidx+1)'th row.
            arg := cdr arg;
            <<
              n:=1;
              while n < (i-Aidx+1) do
              <<
                arg := cdr arg;
                n:=n+1;
              >>;
            >>;
            arg := car arg;
            %
            %  Find (j-Aidx+1)'th column elt of i'th row.
            %
            <<
              n:=1;
              while n < (j-Aidx+1) do
              <<
                arg := cdr arg;
                n:=n+1;
              >>;
            >>;
            arg := car arg;

            setmat(bigA,i,j,arg);
          >>;
        >>;
        Aidx := Aidx+smallsize;
        input := cdr input;
      >>;
    >>;

    return biga;
  end;



symbolic procedure band_matrix(elt_list,sq_size);
  %
  % A square band matrix b is created. The elements of the diagonal
  % are the middle element of elt_list. The elements to the left are
  % used to fill the required number of subdiagonals and the elements
  % to the right the superdiagonals.
  %
  begin
    scalar band_matrix;
    integer i,j,no_elts,middle_pos;
    if not fixp sq_size then rederr
     "Error in band_matrix(second argument): should be an integer.";
    if atom elt_list then elt_list := {elt_list}
     else if car elt_list = 'list then elt_list := cdr elt_list
      else rederr
"Error in band_matrix(first argument): should be single value or list.";
    no_elts := length elt_list;
    if evenp no_elts then rederr
"Error in band matrix(first argument): number of elements must be odd.";
    middle_pos := reval{'quotient,no_elts+1,2};
    if my_reval middle_pos > sq_size then rederr
 "Error in band_matrix: too many elements. Band matrix is overflowing."
     else band_matrix := mkmatrix(sq_size,sq_size);
    for i:=1:sq_size do
    <<
      for j:=1:sq_size do
      <<
        if middle_pos-i+j > 0 and middle_pos-i+j <= no_elts then
        setmat(band_matrix,i,j,nth(elt_list,middle_pos-i+j));
      >>;
    >>;
    return band_matrix;
  end;

flag('(band_matrix),'opfn);



symbolic procedure make_identity(sq_size);
  %
  % Creates identity matrix.
  %
  if not !*fast_la and not fixp sq_size then
   rederr "Error in make_identity: non integer input."
   else 'mat. (for i:=1:sq_size collect
                 for j:=1:sq_size collect if i=j then 1 else 0);

flag('(make_identity),'opfn);



symbolic procedure squarep(in_mat);
  %
  % Tests matrix is square. (boolean).
  %
  begin
    scalar tmp;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in squarep: non matrix input";
    tmp := size_of_matrix(in_mat);
    if first tmp neq second tmp
     then return nil
      else return t;
  end;

flag('(squarep),'boolean);
flag('(squarep),'opfn);



symbolic procedure swap_rows(in_mat,row1,row2);
  %
  % Swaps row1 with rows.
  %
  begin
    scalar new_mat;
    integer rowdim;
    if not !*fast_la then
    <<
      if not matrixp in_mat then
       rederr "Error in swap_rows(first argument): should be a matrix.";
      rowdim := row_dim(in_mat);
      if not fixp row1 then rederr
       "Error in swap_rows(second argument): should be an integer.";
      if not fixp row2 then
     rederr "Error in swap_rows(third argument): should be an integer.";
      if row1>rowdim or row1=0 then rederr
  "Error in swap_rows(second argument): out of range for input matrix.";
      if row2>rowdim or row2=0 then rederr
  "Error in swap_rows(third argument): out of range for input matrix.";
    >>;
    new_mat := copy_mat(in_mat);
    swap_elt(cdr new_mat,row1,row2);
    return new_mat;
  end;



symbolic procedure swap_columns(in_mat,col1,col2);
  %
  % Swaps col1 with col2.
  %
  begin
    scalar new_mat;
    integer coldim;
    if not !*fast_la then
    <<
      if not matrixp in_mat then rederr
       "Error in swap_columns(first argument): should be a matrix.";
      coldim := column_dim(in_mat);
      if not fixp col1 then rederr
       "Error in swap_columns(second argument): should be an integer.";
      if not fixp col2 then rederr
       "Error in swap_columns(third argument): should be an integer.";
      if col1>coldim or col1=0 then rederr
     "Error in swap_columns(second argument): out of range for matrix.";
      if col2>coldim or col2=0 then rederr
"Error in swap_columns(third argument): out of range for input matrix.";
    >>;
    new_mat := copy_mat(in_mat);
    for each row in cdr new_mat do swap_elt(row,col1,col2);
    return new_mat;
  end;



symbolic procedure swap_entries(in_mat,entry1,entry2);
  %
  % Swaps the two entries in in_mat.
  %
  % entry1 and entry2 must be lists of the form
  % {row position,column position}.
  %
  begin
    scalar new_mat;
    integer rowdim,coldim;
    if not matrixp(in_mat) then rederr
     "Error in swap_entries(first argument): should be a matrix.";
    if atom entry1 or car entry1 neq 'list or length cdr entry1 neq 2
     then rederr
"Error in swap_entries(second argument): should be list of 2 elements."
     else entry1 := cdr entry1;
     if atom entry2 or car entry2 neq 'list or length cdr entry2 neq 2
      then rederr
"Error in swap_entries(third argument): should be a list of 2 elements."
      else entry2 := cdr entry2;
    if not !*fast_la then
    <<
      rowdim := row_dim(in_mat);
      coldim := column_dim(in_mat);
      if not fixp car entry1 then
      <<
        prin2  "***** Error in swap_entries(second argument): ";
        prin2t "      first element in list must be an integer.";
        return;
      >>;
      if not fixp cadr entry1 then
      <<
        prin2  "***** Error in swap_entries(second argument): ";
        prin2t "      second element in list must be an integer.";
        return;
      >>;
      if car entry1 > rowdim or car entry1 = 0 then
      <<
        prin2  "***** Error in swap_entries(second argument): ";
        prin2t "      first element is out of range for input matrix.";
      return;
      >>;
      if cadr entry1 > coldim or cadr entry1 = 0 then
      <<
        prin2  "***** Error in swap_entries(second argument): ";
        prin2t "      second element is out of range for input matrix.";
        return;
      >>;
      if not fixp car entry2 then
      <<
        prin2  "***** Error in swap_entries(third argument): ";
        prin2t "      first element in list must be an integer.";
        return;
      >>;
      if not fixp cadr entry2 then
      <<
        prin2  "***** Error in swap_entries(third argument): ";
        prin2t "      second element in list must be an integer.";
        return;
      >>;
      if car entry2 > rowdim or car entry2 = 0 then
      <<
        prin2  "***** Error in swap_entries(third argument): ";
        prin2t "      first element is out of range for input matrix.";
        return;
      >>;
      if cadr entry2 > coldim then
      <<
        prin2  "***** Error in swap_entries(third argument): ";
        prin2t "      second element is out of range for input matrix.";
        return;
      >>;
    >>;
    new_mat := copy_mat(in_mat);
    setmat(new_mat,car entry1,cadr entry1,
            getmat(in_mat,car entry2,cadr entry2));
    setmat(new_mat,car entry2,cadr entry2,
            getmat(in_mat,car entry1,cadr entry1));
  return new_mat;
  end;

% flag('(swap_rows,swap_columns,swap_entries),'opfn);

rtypecar swap_rows,swap_columns,swap_entries;


symbolic procedure get_rows(in_mat,row_list);
  %
  % Input is a matrix and either a single row number or a list of row
  % numbers.
  %
  % Extracts either a single row or a number of rows and returns them
  % in a list of row matrices.
  %
  begin
    integer rowdim,coldim;
    scalar ans,tmp;
    if not matrixp(in_mat) then
     rederr "Error in get_rows(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in get_rows(second argument): ";
      prin2t "      should be either an integer or a list of integers.";
        return;
      >>;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    for each elt in row_list do
    <<
      if not fixp elt then rederr
       "Error in get_rows(second argument): contains non integer.";
      if elt>rowdim or elt=0 then
      <<
        prin2  "***** Error in get_rows(second argument): ";
        rederr
         "contains row number which is out of range for input matrix.";
      >>;
      tmp := 'mat.{nth(cdr in_mat,elt)};
      ans := append(ans,{tmp});
    >>;
    return 'list.ans;
  end;



symbolic procedure get_columns(in_mat,col_list);
  %
  % Input is a matrix and either a single column number or a list of
  % column numbers.
  %
  % Extracts either a single column or a series of adjacent columns and
  % returns them in a list of column matrices.
  %
  begin
    integer rowdim,coldim;
    scalar ans,tmp;
    if not matrixp(in_mat) then
     rederr "Error in get_columns(first argument): should be a matrix.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2 "***** Error in get_columns(second argument): ";
        prin2t
         "     should be either an integer or a list of integers.";
        return;
      >>;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    for each elt in col_list do
    <<
      if not fixp elt then rederr
       "Error in get_columns(second argument): contains non integer.";
      if elt>coldim or elt=0 then
      <<
        prin2 "***** Error in get_columns(second argument): ";
        rederr
       "contains column number which is out of range for input matrix.";
      >>;
      tmp := 'mat.for each row in cdr in_mat collect {nth(row,elt)};
      ans := append(ans,{tmp});
    >>;
    return 'list.ans;
  end;

flag('(get_rows,get_columns),'opfn);



symbolic procedure stack_rows(in_mat,row_list);
  %
  % Stacks all rows pointed to in row_list to form a new matrix.
  %
  % row_list can be either an integer or a list of integers.
  %
  begin
    if not !*fast_la and not matrixp in_mat then
     rederr "Error in stack_rows(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list;
    return 'mat.for each elt in row_list collect nth(cdr in_mat,elt);
  end;



symbolic procedure augment_columns(in_mat,col_list);
  %
  % Augments all columns pointed to in col_list to form a new matrix.
  %
  % col_list can be either an integer or a list of integers.
  %
  begin
    if not !*fast_la and not matrixp in_mat then rederr
     "Error in augment_columns(first argument): should be a matrix.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list;
    return 'mat.for each row in cdr in_mat collect
                for each elt in col_list collect nth(row,elt);
  end;

% flag('(stack_rows,augment_columns),'opfn);

rtypecar stack_rows,augment_columns;


symbolic procedure add_rows(in_mat,r1,r2,mult1);
  %
  % Replaces row2 (r2) by mult1*r1 + r2.
  %
  begin
    scalar new_mat;
    integer i,rowdim,coldim;
    coldim := column_dim(in_mat);
    if not !*fast_la then
    <<
      if not matrixp in_mat then
       rederr "Error in add_rows(first argument): should be a matrix.";
      rowdim := row_dim(in_mat);
      if not fixp r1 then
     rederr "Error in add_rows(second argument): should be an integer.";
      if not fixp r2 then
     rederr "Error in add_rows(third argument): should be an integer.";
      if r1>rowdim or r1=0 then rederr
   "Error in add_rows(second argument): out of range for input matrix.";
      if r2>rowdim or r2=0 then rederr
   "Error in add_rows(third argument): out of range for input matrix.";
    >>;
    new_mat := copy_mat(in_mat);
    % Efficiency.
    if (my_reval mult1) = 0 then return new_mat;
    for i:=1:coldim do
     setmat(new_mat,r2,i,reval {'plus,{'times,mult1,
                          getmat(new_mat,r1,i)},getmat(in_mat,r2,i)});
    return new_mat;
  end;



symbolic procedure add_columns(in_mat,c1,c2,mult1);
  %
  % Replaces column2 (c2) by mult1*c1 + c2.
  %
  begin
    scalar new_mat;
    integer i,rowdim,coldim;
    rowdim := row_dim(in_mat);
    if not !*fast_la then
    <<
      if not matrixp in_mat then
     rederr "Error in add_columns(first argument): should be a matrix.";
      coldim := column_dim(in_mat);
      if not fixp c1 then rederr
       "Error in add_columns(second argument): should be an integer.";
      if not fixp c2 then rederr
       "Error in add_columns(third argument): should be an integer.";
      if c1>coldim or c1=0 then rederr
"Error in add_columns(second argument): out of range for input matrix.";
      if c2>rowdim or c2=0 then rederr
 "Error in add_columns(third argument): out of range for input matrix.";
    >>;
    new_mat := copy_mat(in_mat);
    % Why not be efficient.
    if (my_reval mult1) = 0 then return new_mat;
    for i:=1:rowdim do
      setmat(new_mat,i,c2,{'plus,{'times,mult1,getmat(new_mat,i,c1)},
                           getmat(in_mat,i,c2)});
    return new_mat;
  end;

% flag('(add_rows,add_columns),'opfn);

rtypecar add_rows,add_columns;


symbolic procedure add_to_rows(in_mat,row_list,value);
  %
  % Adds value to each element in each row in row_list.
  %
  % row_list can be either an integer or a list of integers.
  %
  begin
    scalar new_mat;
    integer i,rowdim,coldim;
    if not matrixp in_mat then
     rederr "Error in add_to_row(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in add_to_rows(second argument): ";
        prin2t "      should be either integer or a list of integers.";
        return;
      >>;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    new_mat := copy_mat(in_mat);
    for each row in row_list do
    <<
      if not fixp row then rederr
       "Error in add_to_row(second argument): should be an integer.";
      if row>rowdim or row=0 then
      <<
        prin2  "***** Error in add_to_rows(second argument): ";
        rederr "contains row which is out of range for input matrix.";
      >>;
      for i:=1:coldim do
       setmat(new_mat,row,i,{'plus,getmat(new_mat,row,i),value});
    >>;
    return new_mat;
  end;



symbolic procedure add_to_columns(in_mat,col_list,value);
  %
  % Adds value to each element in each column in col_list.
  %
  % col_list can be either an integer or a list of integers.
  %
  begin
    scalar new_mat;
    integer i,rowdim,coldim;
    if not matrixp in_mat then rederr
     "Error in add_to_columns(first argument): should be a matrix.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2  "***** Error in add_to_columns(second argument): ";
        prin2t "      should be either integer or list of integers.";
        return;
      >>;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    new_mat := copy_mat(in_mat);
    for each col in col_list do
    <<
      if not fixp col then rederr
      "Error in add_to_columns(second argument): should be an integer.";
      if col>coldim or col=0 then
      <<
        prin2  "***** Error in add_to_columns(second argument): ";
        rederr
         "contains column which is out of range for input matrix.";
      >>;
      for i:=1:rowdim do
       setmat(new_mat,i,col,{'plus,getmat(new_mat,i,col),value});
    >>;
    return new_mat;
  end;

% flag('(add_to_rows,add_to_columns),'opfn);

rtypecar add_to_rows,add_to_columns;


symbolic procedure mult_rows(in_mat,row_list,mult1);
  %
  % Replaces rows specified in row_list by row * mult1.
  %
  begin
    scalar new_mat;
    integer i,rowdim,coldim;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in mult_rows(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    new_mat := copy_mat(in_mat);
    for each row in row_list do
    <<
      if not !*fast_la and not fixp row then rederr
       "Error in mult_rows(second argument): contains non integer.";
      if not !*fast_la and (row>rowdim or row=0) then
      <<
        prin2  "***** Error in mult_rows(second argument): ";
        rederr "contains row that is out of range for input matrix.";
      >>;
      for i:=1:coldim do
      <<
        setmat(new_mat,row,i,reval {'times,mult1,getmat(in_mat,row,i)});
      >>;
    >>;
    return new_mat;
  end;


symbolic procedure mult_columns(in_mat,column_list,mult1);
  %
  % Replaces columns specified in column_list by column * mult1.
  %
  begin
    scalar new_mat;
    integer i,rowdim,coldim;
    if not !*fast_la and not matrixp(in_mat) then
    rederr "Error in mult_columns(first argument): should be a matrix.";
    if atom column_list then column_list := {column_list}
    else if car column_list = 'list then column_list := cdr column_list;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    new_mat := copy_mat(in_mat);
    for each column in column_list do
    <<
      if not !*fast_la and not fixp column then rederr
       "Error in mult_columns(second argument): contains non integer.";
      if not !*fast_la and (column>coldim or column=0) then
      <<
        prin2  "***** Error in mult_columns(second argument): ";
        rederr "contains column that is out of range for input matrix.";
      >>;
      for i:=1:rowdim do
      <<
        setmat(new_mat,i,column,
               reval {'times,mult1,getmat(in_mat,i,column)});
      >>;
    >>;
    return new_mat;
  end;

% flag('(mult_rows,mult_columns),'opfn);

rtypecar mult_rows,mult_columns;


%%%%%%%%%%%%%%%%%%%%% matrix_augment/matrix_stack %%%%%%%%%%%%%%%%%%%%%%
put('matrix_augment,'psopfn,'matrix_augment1);
symbolic procedure matrix_augment1(matrices);
  %
  % Takes any number of matrices and joins them horizontally.
  %
  % Can take either a list of matrices or the matrices as seperate
  % arguments.
  %
  begin
    scalar mat_list,new_list,new_row;
    if pairp matrices and pairp car matrices and caar matrices = 'list
     then matrices := cdar matrices;
    if not !*fast_la then
    <<
      mat_list := for each elt in matrices collect reval elt;
      for each elt in mat_list do
        if not matrixp(elt) then
         rederr "Error in matrix_augment: non matrix in input.";
    >>;
    const_rows_test(mat_list);
    for i:=1:row_dim(first mat_list) do
    <<
      new_row := {};
      for each mat1 in mat_list do
       new_row := append(new_row,nth(cdr mat1,i));
      new_list := append(new_list,{new_row});
    >>;
    return 'mat.new_list;
  end;



put('matrix_stack,'psopfn,'matrix_stack1);
symbolic procedure matrix_stack1(matrices);
  %
  % Takes any number of matrices and joins them vertically.
  %
  % Can take either a list of matrices or the matrices as seperate
  % arguments.
  %
  begin
    scalar mat_list,new_list;
    if pairp matrices and pairp car matrices and caar matrices = 'list
     then matrices := cdar matrices;
    if not !*fast_la then
    <<
      mat_list := for each elt in matrices collect reval elt;
      for each elt in mat_list do
       if not matrixp(elt) then
        rederr "Error in matrix_stack: non matrix in input.";
    >>;
    const_columns_test(mat_list);
    for each mat1 in mat_list do new_list := append(new_list,cdr mat1);
    return 'mat.new_list;
  end;




symbolic procedure no_rows(mat_list);
  %
  % Takes list of matrices and sums the no. of rows.
  %
  for each mat1 in mat_list sum row_dim(mat1);




symbolic procedure no_cols(mat_list);
  %
  % Takes list of matrices and sums the no. of columns.
  %
  for each mat1 in mat_list sum column_dim(mat1);



symbolic procedure const_rows_test(mat_list);
  %
  % Tests that each matrix in mat_list has the same number of rows
  % (otherwise augmentation not possible).
  %
  begin
    integer i,listlen,rowdim;
    listlen := length(mat_list);
    rowdim := row_dim(car mat_list);
    i := 1;
    while i<listlen and row_dim(car mat_list) = row_dim(cadr mat_list)
     do << i := i+1; mat_list := cdr mat_list; >>;
    if i=listlen then return rowdim
     else
     <<
       prin2  "***** Error in matrix_augment: ";
       rederr "all input matrices must have the same row dimension.";
     >>;
  end;



symbolic procedure const_columns_test(mat_list);
  %
  % Tests that each matrix in mat_list has the same number of columns
  % (otherwise stacking not possible).
  %
  begin
    integer i,listlen,coldim;
    listlen := length(mat_list);
    coldim := column_dim(car mat_list);
    i := 1;
    while i<listlen and column_dim(car mat_list) =
                         column_dim(cadr mat_list)
     do << i := i+1; mat_list := cdr mat_list; >>;
    if i=listlen then return coldim
     else
     <<
       prin2  "***** Error in matrix_stack: ";
       rederr
        "all input matrices must have the same column dimension.";
       return;
     >>;
  end;

%%%%%%%%%%%%%%%%%%%% end matrix_augment/matrix_stack %%%%%%%%%%%%%%%%%%%



%%%%%%%%%%%%%%%%%%%%%%%%%%% block_matrix %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure block_matrix(rows,cols,mat_list);
  %
  % Creates a matrix consisting of rows*cols matrices which are taken
  % sequentially from the mat_list.
  %
  begin
    scalar block_mat,row_list;
    integer rowdim,coldim,start_row,start_col,i,j;
    if not fixp rows then rederr
     "Error in block_matrix(first argument): should be an integer.";
    if rows=0 then
    <<
      prin2  "***** Error in block_matrix(first argument): ";
      prin2t "      should be an integer greater than 0.";
      return;
    >>;
    if not fixp cols then rederr
     "Error in block_matrix(second argument): should be an integer.";
    if cols=0 then
    <<
     prin2  "***** Error in block_matrix(second argument): ";
      prin2t "      should be an integer greater than 0.";
      return;
    >>;
    if matrixp mat_list then mat_list := {mat_list}
     else if pairp mat_list and car mat_list = 'list then
     mat_list := cdr mat_list
      else
      <<
        prin2  "***** Error in block_matrix(third argument): ";
        prin2t
        "      should be either a single matrix or a list of matrices.";
        return;
      >>;
    if rows*cols neq length mat_list then rederr
 "Error in block_matrix(third argument): Incorrect number of matrices.";
    row_list := create_row_list(rows,cols,mat_list);
    rowdim := check_rows(row_list);
    coldim := check_cols(row_list);
    block_mat := mkmatrix(rowdim,coldim);
    start_row := 1; start_col := 1;
    for i:=1:length row_list do
    <<
      for j:=1:cols do
      <<
        block_mat := copy_into(nth(nth(row_list,i),j),block_mat,
                                start_row,start_col);
        start_col := start_col + column_dim(nth(nth(row_list,i),j));
      >>;
      start_col := 1;
      start_row := start_row + row_dim(nth(nth(row_list,i),1));
    >>;
    return block_mat;
  end;

flag('(block_matrix),'opfn);



symbolic procedure create_row_list(rows,cols,mat_list);
  %
  % Takes mat_list and creates a list of rows elements each of which is
  % a list containing cols elements (ordering left to right).
  % eg: create_row_list(3,2,{a,b,c,d,e,f}) will return
  %     {{a,b},{c,d},{e,f}}.
  %
  begin
    scalar row_list,tmp_list;
    integer i,j,increment;
    increment := 1;
    for i:=1:rows do
    <<
      tmp_list := {};
      for j:=1:cols do
      <<
        tmp_list := append(tmp_list,{nth(mat_list,increment)});
        increment := increment + 1;
      >>;
      row_list := append(row_list,{tmp_list});
    >>;
    return row_list;
  end;



symbolic procedure check_cols(row_list);
  %
  % Checks each element in row_list has same number of columns.
  % Returns this number.
  %
  begin
    integer i,listlen;
    i := 1;
    listlen := length(row_list);
    while i<listlen
    and no_cols(nth(row_list,i)) = no_cols(nth(row_list,i+1))
     do i:=i+1;
    if i=listlen then return no_cols(nth(row_list,i))
     else
     <<
       prin2
        "***** Error in block_matrix: column dimensions of matrices ";
       prin2t "      into block_matrix are not compatible";
       return;
     >>;
  end;



symbolic procedure check_rows(row_list);
  %
  % Checks all matrices in each element in row_list contains same
  % amount of rows.
  % Returns the sum of all of these row numbers (ie: number of rows
  % required in the block matrix).
  %
  begin
    integer i,listlen,rowdim,eltlen,j;
    i := 1;
    listlen := length(row_list);
    while i<=listlen do
    <<
      eltlen := length nth(row_list,i);
      j := 1;
      while j<eltlen do
      <<
        if row_dim(nth(nth(row_list,i),j)) =
            row_dim(nth(nth(row_list,i),j+1)) then j := j+1
         else
         <<
           prin2  "***** Error in block_matrix: row dimensions of ";
           rederr "matrices into block_matrix are not compatible";
         >>;
      >>;
      rowdim := rowdim + row_dim(nth(nth(row_list,i),j));
      i := i+1;
    >>;
    return rowdim;
  end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%% end block_matrix %%%%%%%%%%%%%%%%%%%%%%%%%%


put('vandermonde,'psopfn,'vandermonde1);
symbolic procedure vandermonde1(variables);
  %
  % Input can be either a list or individual arguments.
  %
  % Creates the Vandermonde matrix.
  % ie: the square matrix in which the (i,j)'th entry is
  %     nth(variables,i)^(j-1).
  %
  begin
    scalar vand,in_list;
    integer i,j,sq_size;
    if pairp variables and pairp car variables
        and caar variables = 'list then variables := cdar variables;
    in_list := for each elt in variables collect my_reval elt;
    sq_size := length in_list;
    vand := mkmatrix(sq_size,sq_size);
    for i:=1:sq_size do
    <<
      setmat(vand,i,1,1);
      for j:=2:sq_size do
      <<
        setmat(vand,i,j,
               reval{'expt,nth(in_list,i),{'plus,j,{'minus,1}}});
      >>;
    >>;
    return vand;
  end;



put('toeplitz,'psopfn,'toeplitz1);
symbolic procedure toeplitz1(variables);
  %
  % Input can be either a list or individual arguments.
  %
  % Creates the Toeplitz matrix.
  % ie: the square matrix in which the first element is placed on the
  % diagonal and the nth(variables,i) element is placed on the (i-1)
  % sub and super diagonals.
  %
  begin
    scalar toep,in_list;
    integer i,j,sq_size;
    if pairp variables and pairp car variables
        and caar variables = 'list then variables := cdar variables;
    in_list := for each elt in variables collect my_reval elt;
    sq_size := length in_list;
    toep := mkmatrix(sq_size,sq_size);
    for i:=1:sq_size do
    <<
      for j:=0:i-1 do
      <<
        setmat(toep,i,i-j,nth(in_list,j+1));
        setmat(toep,i-j,i,nth(in_list,j+1));
      >>;
    >>;
    return toep;
  end;

%%%%%%%%%%%%%%%%%%%%%%%%% kronecker_product %%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure kronecker_product(AA,BB);
  %
  % Copies matrix BB into AA with BB(1,1) at AA(p,q).
  %
  begin
    scalar A,B; integer m,n,r,c;
    if not !*fast_la then
    <<
      if not matrixp(aa) then rederr
     "Error in kronecker_product (first argument): should be a matrix.";
      if not matrixp(bb) then rederr
    "Error in kronecker_product (second argument): should be a matrix.";
    >>;
    m := row_dim(AA);
    n := column_dim(AA);
    r := row_dim(BB);
    c := column_dim(BB);

    A := mkmatrix(m*r,n*c);
    for i:=1:m do
     for j:=1:n do <<
         B := getmat(AA,i,j);
         for ii:=1:c do
           for jj := 1 : r do
                setmat(A,(i-1)*r+jj,(j-1)*c+ii,
                   reval list('times,b, getmat(bb,jj,ii)));
                >>;
    return A;
  end;

% flag('(kronecker_product),'opfn);

rtypecar kronecker_product;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% minor %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure minor(in_mat,row,col);
  %
  % Removes row (row) and column (col) from in_mat.
  %
  begin
    scalar min;
    if not !*fast_la then
    <<
      if not matrixp(in_mat) then
       rederr "Error in minor(first argument): should be a matrix.";
      if not fixp row then
       rederr "Error in minor(second argument): should be an integer.";
      if row>row_dim(in_mat) or row=0 then rederr
      "Error in minor(second argument): out of range for input matrix.";
      if not fixp col then
       rederr "Error in minor(third argument): should be an integer.";
      if col>column_dim(in_mat) or col=0 then rederr
      "Error in minor(second argument): out of range for input matrix.";
    >>;
    min := remove_rows(in_mat,row);
    min := remove_columns(min,col);
    return min;
  end;



symbolic procedure remove_rows(in_mat,row_list);
  %
  % Removes each row in row_list from in_mat.
  %
  % row_list can be either an integer or a list of integers.
  %
  begin
    scalar unique_row_list,new_list;
    integer rowdim,row;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in remove_rows(first argument): non matrix input.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in remove_rows(second argument): ";
        prin2t
         "      should be either an integer or a list of integers.";
        return;
      >>;
    % Remove any repititions in row_list (I'm assuming here that if the
    % user has inputted the same row more than once then the meaning
    % is to only remove that row once).
    unique_row_list := {};
    for each row in row_list do
    <<
      if not intersection({row},unique_row_list) then
       unique_row_list := append(unique_row_list,{row});
    >>;
    rowdim := row_dim(in_mat);
    if not !*fast_la then
    <<
      for each row in unique_row_list do if not fixp row then rederr
       "Error in remove_rows(second argument): contains a non integer.";
%      rowdim := row_dim(in_mat);
%      coldim := column_dim(in_mat);
      for each row in unique_row_list do if row>rowdim or row=0 then
       rederr
"Error in remove_rows(second argument): out of range for input matrix.";
      if length unique_row_list = rowdim then
      <<
        prin2  "***** Warning in remove_rows:";
        prin2t "      all the rows have been removed. Returning nil.";
        return nil;
      >>;
    >>;
    for row:=1:rowdim do if not intersection({row},unique_row_list)
     then new_list := append(new_list,{nth(cdr in_mat,row)});
    return 'mat.new_list;
  end;




symbolic procedure remove_columns(in_mat,col_list);
  %
  % Removes each column in col_list from in_mat.
  %
  % col_list can be either an integer or a list of integers.
  %
  begin
    scalar unique_col_list,new_list,row_list;
    integer coldim,row,col;
    if not !*fast_la and not matrixp(in_mat) then rederr
     "Error in remove_columns(first argument): non matrix input.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2  "***** Error in remove_columns(second argument): ";
        prin2t
         "      should be either an integer or a list of integers.";
        return;
      >>;

    % Remove any repititions in col_list (I'm assuming here that if the
    % user has inputted the same column more than once then the meaning
    % is to only remove that column once).
    unique_col_list := {};
    for each col in col_list do
    <<
      if not intersection({col},unique_col_list) then
       unique_col_list := append(unique_col_list,{col});
    >>;
    coldim := column_dim(in_mat);
    if not !*fast_la then
    <<
      for each col in unique_col_list do if not fixp col then rederr
    "Error in remove_columns(second argument): contains a non integer.";
      for each col in unique_col_list do if col>coldim or col=0 then
       rederr
   "Error in remove_columns(second argument): out of range for matrix.";
      if length unique_col_list = coldim then
      <<
        prin2  "***** Warning in remove_columns: ";
       prin2t "      all the columns have been removed. Returning nil.";
        return nil;
      >>;
    >>;
    for each row in cdr in_mat do
    <<
      row_list := {};
      for col:=1:coldim do
      <<
        if not intersection({col},unique_col_list)
         then row_list := append(row_list,{nth(row,col)});
      >> ;
      new_list := append(new_list,{row_list});
    >>;
    return 'mat.new_list;
  end;

flag('(minor,remove_rows,remove_columns),'opfn);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end  minor %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



%%%%%%%%%%%%%%%%% begin random_matrix/im_random_matrix %%%%%%%%%%%%%%%%%

switch imaginary;     % If ON, then random_matrix creates a random
                      % matrix with imaginary entries.

switch not_negative;  % If ON, then the random matrix functions create
                      % matrices with only positive entries. In the
                      % imaginary case, each entry x+iy will have x and
                      % y both > 0. Not that this really means a great
                      % deal mathematically apart from each guy sitting
                      % right up there in the top right hand corner of
                      % the argand plane but oh well.

switch only_integer;  % If ON, then the random matrix functions will
                      % create matrices with only integer entries. In
                      % the imaginary case, each entry x+iy will have
                      % x and y as integers.

switch symmetric;     % If ON, random matrix is symmetric.

switch upper_matrix;  % If ON, then the random matrix is an upper
                      % triagonal matrix.

switch lower_matrix;  % If ON, then the random matrix is a lower
                      % triagonal matrix.



symbolic procedure random_minus(limit);
  %
  % Creates random number in the range -limit < number < limit.
  %
  begin
    scalar r;
    r := random(limit);
    if evenp random(1000) then r := {'minus,r};
    return r
  end;



symbolic procedure random_make_minus(u);
  %
  % Randomly makes u negative.
  %
  if evenp random(1000) then {'minus,u} else u;



symbolic procedure random_matrix(rowdim,coldim,limit);
  %
  % Creates an rowdim by coldim matrix with random entries in the bound
  % -limit < entry < limit.
  %
  begin
    scalar randmat,random_decimal;
    integer i,j,start,current_precision;
    if !*lower_matrix and !*upper_matrix then
    <<
      prin2  "***** Error in random matrix: ";
     prin2t "      both upper_matrix and lower_matrix switches are on.";
      return;
    >>;
    if !*upper_matrix and !*symmetric then
    <<
      prin2  "***** Error in random_matriix: ";
      prin2t "      both upper_matrix and symmetric switches are on.";
      return;
    >>;
    if !*lower_matrix and !*symmetric then
    <<
      prin2  "***** Error in random_matrix: ";
      prin2t "      both lower_matrix and symmetric switches are on.";
      return;
    >>;
    if not fixp limit then limit := algebraic floor(abs(limit));
    if not fixp rowdim then rederr
     "Error in random_matrix(first argument): should be an integer.";
    if rowdim=0 then rederr
  "Error in random_matrix(first argument): should be integer > than 0.";
    if not fixp coldim then rederr
     "Error in random_matrix(second argument): should be an integer.";
    if coldim=0 then
    <<
      prin2  "***** Error in random_matrix(second argument): ";
      prin2t "      should be an integer greater than 0.";
      return;
    >>;
    current_precision := precision 0;
    if !*imaginary then randmat := im_random_matrix(rowdim,coldim,limit)
    else
    <<
      start := 1;
      randmat := mkmatrix(rowdim,coldim);
      for i:=1:rowdim do
     <<
        if !*symmetric or !*lower_matrix then coldim := i
        else if !*upper_matrix then start := i;
        for j:=start:coldim do
        begin
          scalar r1, r2;
          r1 := random(limit);
          r2 := random(10^current_precision);
          random_decimal := {'plus,r1,{'quotient,
                             r2,
                             10^current_precision}};
          if !*only_integer and !*not_negative then
             setmat(randmat,i,j,random(limit))
          else if !*only_integer then
             setmat(randmat,i,j,random_minus(limit))
          else if !*not_negative then
             setmat(randmat,i,j,random_decimal)
          else setmat(randmat,i,j,random_make_minus(random_decimal));
          if !*symmetric then setmat(randmat,j,i,getmat(randmat,i,j));
        end;
      >>;
    >>;
    return randmat;
  end;

flag('(random_matrix),'opfn);



symbolic procedure im_random_matrix(rowdim,coldim,limit);
  %
  % Creates an rowdim by coldim matrix with random imaginary entries.
  % The entrirs are of the form x+iy where x and y are in the bound
  % -limit < x,y < limit.
  %
  begin
    scalar randmat,random_decimal,im_random_decimal;
    integer i,j,start,current_precision;
    start := 1;
    current_precision := precision 0;
    randmat := mkmatrix(rowdim,coldim);
    for i:=1:rowdim do
    <<
      if !*symmetric or !*lower_matrix then coldim := i
       else if !*upper_matrix then start := i;
      for j:=start:coldim do
      begin
        scalar r1, r2;
        r1 := random(limit);
        r2 := random(10^current_precision);
        random_decimal := {'plus,1,{'quotient,
                            r2,
                            10^current_precision}};
        r1 := random(limit);
        r2 := random(10^current_precision);
        im_random_decimal := {'plus,r1,{'quotient,
                               r2,
                               10^current_precision}};
        if !*only_integer and !*not_negative then <<
           r1 := random(limit); r2 := random(limit);
           setmat(randmat,i,j,{'plus,r1, {'times,'i,r2}}) >>
        else if !*only_integer then <<
           r1 := random_minus(limit); r2 := random_minus(limit);
           setmat(randmat,i,j,{'plus,r1, {'times,'i,r2}}) >>
        else if !*not_negative then
           setmat(randmat,i,j,{'plus,random_decimal,
                                 {'times,'i,im_random_decimal}})
        else <<
           r1 := random_make_minus(random_decimal);
           r2 := random_make_minus(im_random_decimal);
           setmat(randmat,i,j,{'plus,r1, {'times,'i,r2}}) >>;
        if !*symmetric then setmat(randmat,j,i,getmat(randmat,i,j));
      end;
    >>;
    return randmat;
  end;

% flag('(im_random_matrix),'opfn);

rtypecar im_random_matrix;

%%%%%%%%%%%%%%%%%% end random_matrix/im_random_matrix %%%%%%%%%%%%%%%%%%



symbolic procedure extend(in_mat,rows,cols,entry);
  %
  % Extends in_mat by rows rows (!) and cols columns. New entries are
  % initialised to entry.
  %
  begin
    scalar ex_mat;
    integer rowdim,coldim,i,j;
    if not matrixp(in_mat) then
     rederr "Error in extend(first argument): should be a matrix.";
    if not fixp rows then
     rederr "Error in extend(second argument): should be an integer.";
    if not fixp cols then
     rederr "Error in extend(third argument): should be an integer.";
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    ex_mat := mkmatrix(rowdim+rows,coldim+cols);
    ex_mat := copy_into(in_mat,ex_mat,1,1);
    for i:=1:rowdim+rows do
    <<
      for j:=1:coldim+cols do
      <<
        if i<=rowdim and j<=coldim then <<>>
        else setmat(ex_mat,i,j,entry);
      >>;
    >>;
    return ex_mat;
  end;

flag('(extend),'opfn);

rtypecar extend;


%%%%%%%%%%%%%%%%%%%%% begin char_matrix/char_poly %%%%%%%%%%%%%%%%%%%%%%

symbolic procedure char_matrix(in_mat,lmbda);
  %
  % Create characteristic matrix. ie: C := lmbda*I - in_mat.
  % in_ mat must be square.
  %
  begin
    scalar carmat;
    integer rowdim;
    if not matrixp(in_mat) then
     rederr "Error in char_matrix(first argument): should be a matrix.";
    if not squarep(in_mat) then rederr
     "Error in char_matrix(first argument): must be a square matrix.";
    rowdim := row_dim(in_mat);
    carmat := {'plus,{'times,lmbda,make_identity(rowdim)},
               {'minus,in_mat}};
    return carmat;
  end;



symbolic procedure char_poly(in_mat,lmbda);
  %
  % Finds characteristic polynomial of matrix in_mat.
  % ie: det(lmbda*I - in_mat).
  %
  begin
    scalar chpoly,carmat;
    if not matrixp(in_mat) then
     rederr "Error in char_poly(first argument): should be a matrix.";
    carmat := char_matrix(in_mat,lmbda);
    chpoly := algebraic det(carmat);
    return chpoly;
  end;

flag('(char_matrix char_poly),'opfn);

%%%%%%%%%%%%%%%%%%%%%%%% end char_matrix/char_poly %%%%%%%%%%%%%%%%%%%%%



%%%%%%%%%%%%%%%%%%%%%%%%%% begin pivot/rows_pivot %%%%%%%%%%%%%%%%%%%%%%

symbolic procedure pivot(in_mat,pivot_row,pivot_col);
  %
  % Converts all elements in pivot column (apart from the one in pivot
  % row) to 0.
  %
  begin
    scalar piv_mat,ratio;
    integer i,j,rowdim,coldim;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in pivot(first argument): should be a matrix.";
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    if not !*fast_la then
    <<
    if not fixp pivot_row then
     rederr "Error in pivot(second argument): should be an integer.";
    if pivot_row>rowdim or pivot_row=0 then rederr
     "Error in pivot(second argument): out of range for input matrix.";
    if not fixp pivot_col then rederr
     "Error in pivot(third argument): should be an integer.";
    if pivot_col>coldim or pivot_col=0 then rederr
     "Error in pivot(third argument): out of range for input matrix.";
    if getmat(in_mat,pivot_row,pivot_col) = 0 then
     rederr "Error in pivot: cannot pivot on a zero entry.";
    >>;
    piv_mat := copy_mat(in_mat);
    piv_mat := copy_mat(in_mat);
    for i:=1:rowdim do
    <<
      for j:=1:coldim do
      <<
        if i = pivot_row then <<>>
        else
        <<
           ratio := {'quotient,getmat(in_mat,i,pivot_col),
                     getmat(in_mat,pivot_row,pivot_col)};
           setmat(piv_mat,i,j,{'plus,getmat(in_mat,i,j),{'minus,
                  {'times,ratio,getmat(in_mat,pivot_row,j)}}});
        >>;
      >>;
    >>;
    return piv_mat;
  end;



symbolic procedure rows_pivot(in_mat,pivot_row,pivot_col,row_list);
  %
  % Same as pivot but only rows a .. to .. b, where row_list = {a,b},
  % are changed.
  %
  % rows_pivot will work if row_list is just an integer.
  %
  begin
    scalar piv_mat,ratio;
    integer j,rowdim,coldim;
    rowdim := row_dim(in_mat);
    coldim := column_dim(in_mat);
    if not !*fast_la then
    <<
      if not matrixp(in_mat) then
      rederr "Error in rows_pivot(first argument): should be a matrix.";
      rowdim := row_dim(in_mat);
      coldim := column_dim(in_mat);
      if not fixp pivot_row then
       rederr "Error in pivot(second argument): should be an integer.";
      if pivot_row>rowdim or pivot_row=0 then rederr
 "Error in rows_pivot(second argument): out of range for input matrix.";
      if not fixp pivot_col then
       rederr "Error in pivot(third argument): should be an integer.";
      if pivot_col>coldim or pivot_col=0 then rederr
  "Error in rows_pivot(third argument): out of range for input matrix.";
    >>;
    if atom row_list then row_list := {row_list}
     else if pairp row_list and car row_list = 'list
     then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in rows_pivot(fourth argument): ";
        prin2t
         "      should be either an integer or a list of integers.";
        return;
      >>;
    if getmat(in_mat,pivot_row,pivot_col) = 0 then
     rederr "Error in rows_pivot: cannot pivot on a zero entry.";
    piv_mat := copy_mat(in_mat);
    for each elt in row_list do
    <<
      if not !*fast_la then
      <<
        if not fixp elt then rederr
         "Error in rows_pivot: fourth argument contains a non integer.";
        if elt>rowdim or elt=0 then
        <<
          prin2  "***** Error in rows_pivot(fourth argument): ";
          rederr "contains row which is out of range for input matrix.";
        >>;
      >>;
      for j:=1:coldim do
      <<
        if elt = pivot_row then <<>>
        else
        <<
           ratio := {'quotient,getmat(in_mat,elt,pivot_col),
                     getmat(in_mat,pivot_row,pivot_col)};
           setmat(piv_mat,elt,j,{'plus,getmat(in_mat,elt,j),{'minus,
                  {'times,ratio,getmat(in_mat,pivot_row,j)}}});
        >>;
      >>;
    >>;
    return piv_mat;
  end;

flag('(pivot,rows_pivot),'opfn);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%% end pivot/rows_pivot %%%%%%%%%%%%%%%%%%%%%



symbolic procedure mat_jacobian(exp_list,var_list);
  %
  % mat_jacobian(exp,var) computes the Jacobian matrix of exp w.r.t. var.
  % The (i,j)'th entry is diff(nth(exp,i),nth(var,j)).
  %
  begin
    scalar jac,exp1,var1;
    integer i,j,rowdim,coldim;
    if atom exp_list then exp_list := {exp_list}
     else if car exp_list neq 'list then rederr
     "Error in mat_jacobian(first argument): expressions must be in a list."
      else exp_list := cdr exp_list;
    if atom var_list then var_list := {var_list}
     else if car var_list neq 'list then rederr
      "Error in mat_jacobian(second argument): variables must be in a list."
      else var_list := cdr var_list;
    rowdim := length exp_list;
    coldim := length var_list;
    jac := mkmatrix(rowdim,coldim);
    for i:=1:rowdim do
    <<
      for j:=1:coldim do
      <<
        exp1 := nth(exp_list,i);
        var1 := nth(var_list,j);
        setmat(jac,i,j,algebraic df(exp1,var1));
      >>;
    >>;
    return jac;
  end;

flag('(mat_jacobian),'opfn);



symbolic procedure hessian(poly,variables);
  %
  % variables can be either a list or a single variable.
  %
  % A Hessian matrix is a matrix whose (i,j)'th entry is
  %       df(df(poly,nth(var,i)),nth(var,j))
  %
  % where df is the derivative.
  %
  begin
    scalar hess_mat,part1,part2,elt;
    integer row,col,sq_size;
    if atom variables then variables := {variables}
     else if car variables = 'list then variables := cdr variables
      else
      <<
        prin2  "***** Error in hessian(second argument): ";
        prin2t
     "      should be either a single variable or a list of variables.";
        return;
      >>;
    sq_size := length variables;
    hess_mat := mkmatrix(sq_size,sq_size);
    for row:=1:sq_size do
    <<
      for col:=1:sq_size do
      <<
        part1 := nth(variables,row);
        part2 := nth(variables,col);
        elt := algebraic df(df(poly,part1),part2);
        setmat(hess_mat,row,col,elt);
      >>;
    >>;
    return hess_mat;
  end;

flag('(hessian),'opfn);



symbolic procedure hermitian_tp(in_mat);
  %
  % Computes the Hermitian transpose (HT say) of in_mat.
  %
  % The (i,j)'th element of HT = conjugate of the (j,i)'th element of
  % in__mat.
  %
  begin
    scalar h_tp,element;
    integer row,col;
    if not matrixp(in_mat) then
     rederr "Error in hermitian_tp: non matrix input.";
    h_tp := algebraic tp(in_mat);
    for row:=1:row_dim(h_tp) do
    <<
      for col:=1:column_dim(h_tp) do
      <<
        element := getmat(h_tp,row,col);
        setmat(h_tp,row,col,
               algebraic (repart(element) - i*impart(element)));
      >>;
    >>;
    return h_tp;
  end;

flag('(hermitian_tp),'opfn);



symbolic procedure hilbert(sq_size,value);
  %
  % The Hilbert matrix is symmetric and the (i,j)'th entry in
  % 1/(i+j-x).
  %
  begin
    scalar hil_mat,denom;
    integer row,col;
    if not fixp sq_size or sq_size<1 then rederr
     "Error in hilbert(first argument): must be a positive integer.";
    hil_mat := mkmatrix(sq_size,sq_size);
    for row:=1:sq_size do
    <<
      for col:=1:sq_size do
      <<
        if (denom := reval{'plus,row,col,{'minus,value}}) = 0 then
         rederr "Error in hilbert: division by zero."
         else setmat(hil_mat,row,col,{'quotient,1,denom});
      >>;
    >>;
    return hil_mat;
  end;

flag('(hilbert),'opfn);



%%%%%%%%%%%%%%%%%%%%%%%% begin coeff_matrix  %%%%%%%%%%%%%%%%%%%%%%%%%%%

put('coeff_matrix,'psopfn,'coeff_matrix1); % To allow variable input.
symbolic procedure coeff_matrix1(equation_list);
  %
  % Given the system of linear equations, coeff_matrix returns {A,X,b}
  % s.t. AX = b.
  %
  % Input can be either a list of linear equations or the linear
  % equations as individual arguments.
  %
  begin
    scalar variable_list,A,X,b;
    if pairp car equation_list and caar equation_list = 'list then
     equation_list := cdar equation_list;
    equation_list := remove_equals(equation_list);
    variable_list := get_variable_list(equation_list);
    if variable_list = nil then
     rederr "Error in coeff_matrix: no variables in input.";
    check_linearity(equation_list,variable_list);
    A := get_A(equation_list,variable_list);
    X := get_X(variable_list);
    b := get_b(equation_list,variable_list);
    return {'list,A,X,b};
  end;



symbolic procedure remove_equals(equation_list);
  %
  % If any of the equations are equalities the equalities are removed
  % to leave a list of polynomials.
  %
  begin
     equation_list := for each equation in equation_list collect
      if pairp equation and car equation = 'equal then
       reval{'plus,cadr equation,{'minus,caddr equation}}
        else equation;
     return equation_list;
  end;



symbolic procedure get_variable_list(equation_list);
  %
  % Gets hold of all variables from the equations in equation_list.
  %
  begin
    scalar variable_list;
    for each equation in equation_list do
     variable_list := union(get_coeffs(equation),variable_list);
    return reverse variable_list;
  end;



symbolic procedure check_linearity(equation_list,variable_list);
  %
  % Checks that we really are dealing with a system of linear equations.
  %
  for each equation in equation_list do
  <<
    for each variable in variable_list do
    <<
      if deg(equation,variable) > 1 then
       rederr "Error in coeff_matrix: the equations are not linear.";
    >>;
  >>;



symbolic procedure get_A(equation_list,variable_list);
  begin
    scalar A,element,var_elt;
    integer row,col,length_equation_list,length_variable_list;
    length_equation_list := length equation_list;
    length_variable_list := length variable_list;
    A := mkmatrix(length equation_list,length variable_list);
    for row:=1:length_equation_list do
    <<
      for col:=1:length_variable_list do
      <<
        element := nth(equation_list,row);
        var_elt := nth(variable_list,col);
        setmat(A,row,col,algebraic coeffn(element,var_elt,1));
      >>;
    >>;
    return A;
  end;



symbolic procedure get_b(equation_list,variable_list);
  %
  % Puts the integer parts of all the equations into a column matrix.
  %
  begin
    scalar substitution_list,integer_list,b;
    integer length_integer_list,row;
    substitution_list :=
     'list.for each variable in variable_list collect
                                               {'equal,variable,0};
    integer_list := for each equation in equation_list collect
      algebraic sub(substitution_list,equation);
    length_integer_list := length integer_list;
    b := mkmatrix(length_integer_list,1);
    for row:=1:length_integer_list do
     setmat(b,row,1,-nth(integer_list,row));
    return b;
  end;



symbolic procedure get_X(variable_list);
  begin
    scalar X;
    integer row,length_variable_list;
    length_variable_list := length variable_list;
    X := mkmatrix(length_variable_list,1);
    for row := 1:length variable_list do
     setmat(X,row,1,nth(variable_list,row));
    return X;
  end;



symbolic procedure get_coeffs(poly);
  %
  % Gets all kernels in a poly.
  %
  begin
    scalar ker_list_num,ker_list_den;
    ker_list_num := kernels !*q2f simp reval num poly;
    ker_list_den := kernels !*q2f simp reval den poly;
    ker_list_num := union(ker_list_num,ker_list_den);
    return ker_list_num;
  end;

%%%%%%%%%%%%%%%%%%%%%%%%%% end coeff_matrix  %%%%%%%%%%%%%%%%%%%%%%%%%%%

% Smacro used in other modules.

symbolic smacro procedure my_revlis(u);
  %
  % As my_reval but for lists.
  %
  for each j in u collect my_reval(j);



endmodule;  %linear algebra.

end;
