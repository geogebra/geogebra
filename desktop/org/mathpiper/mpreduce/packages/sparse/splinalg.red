%***********************************************************************
%=======================================================================
%
% Code for the Linear Algebra for Sparse Matrices Package.
%
% Author: Stephen Scowcroft.                           Date: June 1995
%
%=======================================================================
%***********************************************************************

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


module splinalg;

% This is the beginning of a Linear Algebra Package for Sparse Matrices.
% It stems from the present Linear Algebra Package for Matrices and
% will hopefully be incorporated into it to produce a new MATRIX
% package.

switch fast_la;  % If ON, then the following functions will be faster:

% spadd_columns    spadd_rows       spaugment_columns  spcolumn_dim    %
% spcopy_into      spmake_identity  spmatrix_augment   spmatrix_stack  %
% spminor          spmult_column    spmult_row         sppivot         %
% spremove_columns spremove_rows    sprows_pivot       spsquarep       %
% spstack_rows     spsub_matrix     spswap_columns     spswap_entries  %
% spswap_rows      spsymmetricp                                        %

% This is basically done by removing some error checking and doesn't
% speed things up too much. You'll need to be making alot of calls to
% see the difference. If you get strange error messages with fast_la
% ON then thoroughly check your input.

% Creates an identity matrix of dimension size.

symbolic procedure spmake_identity(size);
 begin scalar tm;
  if not !*fast_la and not fixp size then
     rederr "Error in spmake_identity: non integer input.";
  tm:=list('sparsemat,mkvect(size),list('spm,size,size));
    for i :=size step -1 until 1 do
    <<letmtr3(list(tm,i,i),1,tm,nil)
    >>;
  return tm;
 end;

flag('(spmake_identity),'opfn);

% Finds the row-wise dimension of a matrix.

symbolic procedure sprow_dim(u);
 begin scalar res;
  if not !*fast_la and not matrixp(u) then
     rederr "Error in sprow_dim: input should be a matrix.";
  res := cadr spmatlength(u);
  return res;
 end;

% Finds the column-wise dimension of a matrix.

symbolic procedure spcol_dim(u);
 begin scalar res;
  if not !*fast_la and not matrixp(u) then
     rederr "Error in spcol_dim: input should be a matrix.";
  res := caddr spmatlength(u);
  return res;
 end;

flag('(sprow_dim,spcol_dim),'opfn);

% Test if input is a matrix or sparse matrix (boolean)
% Whether id is a gerneric matrix.

symbolic procedure matrixp(u);
 begin;
  if not pairp u then u:=reval u;
  if not eqcar(u,'mat) and not eqcar(u,'sparsemat) then return nil
   else return t;
 end;

flag('(matrixp),'boolean);
flag('(matrixp),'opfn);

% Test to see if input is a sparse matrix or not. (boolean)

symbolic procedure sparsematp(u);
 if not eqcar(u,'sparsemat) then nil
  else t;

flag('(sparsematp),'boolean);
flag('(sparsematp),'opfn);

% Tests matrix is square. (boolean).

symbolic procedure squarep(u);
begin
    scalar tmp;
    if not !*fast_la and not matrixp(u) then
     rederr "Error in squarep: non matrix input";
    if sparsematp(u) then tmp := cdr spmatlength(u)
     else tmp:=size_of_matrix(u);
    if car tmp neq cadr tmp
     then return nil
      else return t;
  end;

flag('(squarep),'boolean);
flag('(squarep),'opfn);

% Checks input is symmetric. ie: transpose(A) = A. (boolean).

symbolic procedure symmetricp(u);
  if eqcar(u,'sparsemat) then
   if smtp (u,nil) neq u then nil else t
  else
  if algebraic (tp(u)) neq u then nil else t;

flag('(symmetricp),'boolean);
flag('(symmetricp),'opfn);

% Takes a constant (const) and an integer (mat_dim) and creates
% a jordan block of dimension mat_dim x mat_dim.

symbolic procedure spjordan_block(const,mat_dim);
 begin scalar tm;
  tm :=mkempspmat(mat_dim,list('spm,mat_dim,mat_dim));
  if not fixp mat_dim then rederr
     "Error in spjordan_block(second argument): should be an integer.";
  for i:=mat_dim step -1 until 1 do
    <<letmtr3(list(tm,i,i),const,tm,nil);
      if i<mat_dim then letmtr3(list(tm,i,i+1),1,tm,nil);
    >>;
    return tm;
  end;

flag ('(spjordan_block),'opfn);

% Removes row (row) and column (col) from in_mat.

symbolic procedure spminor(list,row,col);
 begin scalar len,lena,lenb,rlist;
  len:=caddr list;
  rlist :=copy_vect(list,nil);
  lena := cadr len;
  lenb := caddr len;
  if not matrixp(list) then
    rederr "Error in spminor(first argument): should be a matrix.";
  if not fixp row then
    rederr "Error in spminor(second argument): should be an integer.";
  if not fixp col then
    rederr "Error in spminor(third argument): should be an integer.";
  if not (row > 0 and row < lena + 1)
    then rerror(matrix,20,"Row number out of range");
  if not (col > 0 and col < lenb + 1)
   then rerror(matrix,21,"Column number out of range");

  spremrow(row,rlist);
  spremcol(col,rlist);
  rlist := rewrite(rlist,lena-1,row,col);
  return rlist;
 end;

flag('(spminor),'opfn);

% A square band matrix b is created. The elements of the diagonal
% are the middle element of elt_list. The elements to the left are
% used to fill the required number of subdiagonals and the elements
% to the right the superdiagonals.

symbolic procedure spband_matrix(elt_list,sq_size);
  begin scalar tm;
    integer i,j,it,no_elts,middle_pos;
    tm:=mkempspmat(sq_size,list('spm,sq_size,sq_size));
     if not fixp sq_size then rederr
     "Error in spband_matrix(second argument): should be an integer.";
    if atom elt_list then elt_list := {elt_list}
     else if car elt_list = 'list then elt_list := cdr elt_list
      else rederr
"Error in spband_matrix(first argument): should be single value or list.";
    no_elts := length elt_list;
    if evenp no_elts then rederr
"Error in spband matrix(first argument): number of elements must be odd.";
    middle_pos := reval{'quotient,no_elts+1,2};
    if my_reval middle_pos > sq_size then rederr
 "Error in spband_matrix: too many elements. Band matrix is overflowing.";
    it:=2;
    for i:=1:sq_size do
    << if i<=middle_pos then j:=1
        else j:=it;
       while middle_pos-i+j > 0 and
            j<=sq_size and middle_pos-i+j <= no_elts do
       <<
           letmtr3(list(tm,i,j),nth(elt_list,middle_pos-i+j),tm,nil);
           j:=j+1;
       >>;
        if i>middle_pos then it:=it+1;
    >>;
    return tm;
  end;

flag('(spband_matrix),'opfn);

% Stacks all rows pointed to in row_list to form a new matrix.
%
% row_list can be either an integer or a list of integers.

symbolic procedure spstack_rows(in_mat,row_list);
  begin scalar tl,rlist,res,list;
    integer rowdim,cnt,coldim;
    list:=in_mat;
    cnt:=1;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in spstack_rows(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in spstack_rows(second argument): ";
      prin2t "      should be either an integer or a list of integers.";
        return;
      >>;
    coldim := spcol_dim(in_mat);
    rowdim := sprow_dim(in_mat);
    tl:=list('smp,length(row_list), coldim);
    res:=mkempspmat(length(row_list),tl);
   for each elt in row_list do
    <<
      if not fixp elt then rederr
       "Error in spstack_rows(second argument): contains non integer.";
      if elt>rowdim or elt=0 then
      <<
        prin2  "***** Error in spstack_rows(second argument): ";
        rederr "contains row number which is out of range for input matrix.";
      >>;
      rlist:= findrow(list,elt);
       if rlist = nil then cnt := cnt + 1
        else
         << letmtr3(list(res,cnt),rlist,res,nil);
            cnt := cnt + 1
         >>;
      >>;
    return res;
  end;

% Augments all columns pointed to in col_list to form a new matrix.
%
% col_list can be either an integer or a list of integers.

symbolic procedure spaugment_columns(in_mat,col_list);
  begin
    integer cnt,coldim,rcnt,rowdim;
    scalar tl,rlist,res,list,rrlist,colist,val,res1;
    list:=in_mat;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in spaugment_columns(first argument): should be a matrix.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2 "***** Error in spaugment_columns(second argument): ";
        prin2t
         "     should be either an integer or a list of integers.";
        return;
      >>;
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    cnt:=1;
    rcnt:=1;
    tl := list('spm,rowdim,length(col_list));
    res:=mkempspmat(rowdim,tl);
    for each elt in col_list do
    << if not fixp elt then rederr
   "Error in spaugment_columns(second argument): contains non integer.";
       if elt>coldim or elt=0 then
       << prin2 "***** Error in spaugment_columns(second argument): ";
          rederr
       "contains column number which is out of range for input matrix.";
       >>;
     >>;
      for i:=1:rowdim do
      << rrlist:=findrow(list,i);
        if rrlist then
         <<for each elt in col_list do
           <<colist:=atsoc(elt,rrlist);
             if colist = nil then cnt := cnt + 1
              else << val := cdr colist;
                      res1:=(cnt . val);
                      rlist := res1 . rlist;
                       cnt := cnt + 1;
                   >>;
            >>;
            if rlist then
             letmtr3(list(res,i),list(nil) . reverse rlist,res,nil);
            rlist := nil;
            cnt := 1;
         >>;
        >>;
    return res;
  end;

flag('(spstack_rows,spaugment_columns),'opfn);


% Input is a matrix and either a single row number or a list of row
% numbers.
%
% Extracts either a single row or a number of rows and returns them
% in a list of row matrices.

symbolic procedure spget_rows(in_mat,row_list);
  begin scalar tl,he,rlist,res,list,rlist1;
    integer rowdim,cnt,coldim;
    coldim:= spcol_dim(in_mat);
    tl:=list('spm,length(row_list), coldim);
    list:=in_mat;
    cnt:=1;
    if not matrixp(in_mat) then
     rederr "Error in spget_rows(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in spget_rows(second argument): ";
      prin2t "      should be either an integer or a list of integers.";
        return;
      >>;
    rowdim := sprow_dim(in_mat);
   for each elt in row_list do
    <<
      if not fixp elt then rederr
       "Error in spget_rows(second argument): contains non integer.";
      if elt>rowdim or elt=0 then
      <<
        prin2  "***** Error in spget_rows(second argument): ";
        rederr "contains row number which is out of range for input matrix.";
      >>;
      rlist:= findrow(list,elt);
       if rlist = nil then nil
        else
         << rlist1:= mkempspmat(1,list('spm,1,coldim));
            letmtr3(list(rlist1,1),rlist,rlist1,nil);
            res:=append(res,{rlist1});
         >>;
      >>;

    return 'list.res;
  end;


% Input is a matrix and either a single column number or a list of
% column numbers.
%
% Extracts either a single column or a series of adjacent columns and
% returns them in a list of column matrices.

symbolic procedure spget_columns(in_mat,col_list);
  begin
    integer coldim,rcnt,rowdim;
    scalar tl,rlist,res,list,nlist,rrlist,colist,val,res1;
    rowdim:= sprow_dim(in_mat);
    tl := list('spm,rowdim,length col_list);
    list:=in_mat;
    if not matrixp(in_mat) then
     rederr "Error in spget_columns(first argument): should be a matrix.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2 "***** Error in spget_columns(second argument): ";
        prin2t
         "     should be either an integer or a list of integers.";
        return;
      >>;
    coldim := spcol_dim(in_mat);
    rcnt:=1;
    for each elt in col_list do
     << if not fixp elt then rederr
         "Error in spget_columns(second argument): contains non integer.";
        if elt>coldim or elt=0 then
          << prin2 "***** Error in get_columns(second argument): ";
              rederr
             "contains column number which is out of range for input matrix.";
          >>;
      >>;
     for each elt in col_list do
     <<rlist:=mkempspmat(coldim,list('spm,coldim,1));
       for i:=1:rowdim do
        << rrlist:=findrow(list,i);
          if rrlist then
             <<colist:=atsoc(elt,rrlist);
               if colist = nil then rcnt := rcnt + 1
               else<< val := cdr colist;
                      res1 := {1 . val};
                      letmtr3(list(rlist,rcnt),
                               list(nil) . res1,rlist,nil);
                       rcnt := rcnt + 1;
                   >>;
              >>;
          >>;
            res := append(res,{rlist});
            rlist := nil;
            rcnt := 1;
       >>;
    return 'list.res;
  end;

flag('(spget_rows,spget_columns),'opfn);

% Removes each row in row_list from in_mat.
%
% row_list can be either an integer or a list of integers.

symbolic procedure spremove_rows(in_mat,row_list);
  begin
    scalar unique_row_list,list,tl;
    integer rowdim,row,cnt;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in spremove_rows(first argument): non matrix input.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in spremove_rows(second argument): ";
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
    rowdim := sprow_dim(in_mat);
    if not !*fast_la then
    <<
      for each row in unique_row_list do if not fixp row then rederr
       "Error in spremove_rows(second argument): contains a non integer.";
      for each row in unique_row_list do if row>rowdim or row=0 then
       rederr
"Error in spremove_rows(second argument): out of range for input matrix.";
      if length unique_row_list = rowdim then
      <<
        prin2  "***** Warning in spremove_rows:";
        prin2t "      all the rows have been removed. Returning nil.";
        return nil;
      >>;
    >>;
       cnt:=0;
    tl:=list('spm,rowdim - length unique_row_list,spcol_dim(in_mat));
    list:=copy_vect(in_mat,tl);
     for each elt in unique_row_list do
      << spremrow(elt-cnt,list);
         list := rewrite(list,rowdim,elt-cnt,0);
         cnt := cnt + 1
       >>;
     return list;
  end;

% Removes each column in col_list from in_mat.
%
% col_list can be either an integer or a list of integers.

symbolic procedure spremove_columns(in_mat,col_list);
  begin
    scalar unique_col_list,tl,list;
    integer coldim,col,cnt;
    if  not !*fast_la and not matrixp(in_mat) then rederr
     "Error in spremove_columns(first argument): non matrix input.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2  "***** Error in spremove_columns(second argument): ";
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
    coldim := spcol_dim(in_mat);
   if not !*fast_la then
    <<
      for each col in unique_col_list do if not fixp col then rederr
    "Error in spremove_columns(second argument): contains a non integer.";
      for each col in unique_col_list do if col>coldim or col=0 then
       rederr
   "Error in spremove_columns(second argument): out of range for matrix.";
      if length unique_col_list = coldim then
      <<
        prin2  "***** Warning in spremove_columns: ";
       prin2t "      all the columns have been removed. Returning nil.";
        return nil;
      >>;
    >>;
     cnt:=0;
     tl:=list('spm,sprow_dim(in_mat), coldim - length unique_col_list);
     list := copy_vect(in_mat,tl);
     for each elt in unique_col_list do
      << spremcol(elt-cnt,list);
         list := rewrite(list,coldim,0,elt-cnt);
         cnt := cnt + 1;
      >>;
     return list;

  end;


flag('(spremove_rows,spremove_columns),'opfn);

  % Creates a matrix consisting of rows*cols matrices which are taken
  % sequentially from the mat_list.

symbolic procedure spblock_matrix(rows,cols,mat_list);
  begin
    scalar block_mat,row_list;
    integer rowdim,coldim,start_row,start_col,i,j;
    if not fixp rows then rederr
     "Error in block_matrix(first argument): should be an integer.";
    if rows=0 then
    <<
      prin2  "***** Error in spblock_matrix(first argument): ";
      prin2t "      should be an integer greater than 0.";
      return;
    >>;
    if not fixp cols then rederr
     "Error in spblock_matrix(second argument): should be an integer.";
    if cols=0 then
    <<
     prin2  "***** Error in spblock_matrix(second argument): ";
      prin2t "      should be an integer greater than 0.";
      return;
    >>;
    if matrixp mat_list then mat_list := {mat_list}
     else if pairp mat_list and car mat_list = 'list then
     mat_list := cdr mat_list
      else
      <<
        prin2  "***** Error in spblock_matrix(third argument): ";
        prin2t
        "      should be either a single matrix or a list of matrices.";
        return;
      >>;
    if rows*cols neq length mat_list then rederr
 "Error in spblock_matrix(third argument): Incorrect number of matrices.";
    row_list := spcreate_row_list(rows,cols,mat_list);
    rowdim := spcheck_rows(row_list);
    coldim := spcheck_cols(row_list);
    block_mat := mkempspmat(rowdim,list('spm,rowdim,coldim));
    start_row := 1; start_col := 1;
    for i:=1:length row_list do
    <<
      for j:=cols step -1 until 1 do
      <<
        block_mat := spcopy_into(nth(nth(row_list,i),j),block_mat,
                                start_row,start_col);
        start_col := start_col + spcol_dim(nth(nth(row_list,i),j));
      >>;
      start_col := 1;
      start_row := start_row + sprow_dim(nth(nth(row_list,i),1));
    >>;
    return block_mat;
  end;

flag('(spblock_matrix),'opfn);

symbolic procedure spcreate_row_list(rows,cols,mat_list);
  %
  % Takes mat_list and creates a list of rows elements each of which is
  % a list containing cols elements (ordering left to right).
  % eg: create_row_list(3,2,{a,b,c,d,e,f}) will return
  %     {{a,b},{c,d},{e,f}}.
  %
  begin
    scalar row_list,tmp_list,list;
    integer i,j,increment;
    increment := 1;
    for i:=1:rows do
    <<
      tmp_list := {};
      for j:=1:cols do
      <<list:=nth(mat_list,increment);
        if not sparsematp(list) then list:=sptransmat(list);
        tmp_list := append(tmp_list,{list});
        increment := increment + 1;
      >>;
      row_list := append(row_list,{tmp_list});
    >>;
    return row_list;
  end;


symbolic procedure spcheck_rows(row_list);
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
        if sprow_dim(nth(nth(row_list,i),j)) =
            sprow_dim(nth(nth(row_list,i),j+1)) then j := j+1
         else
         <<
           prin2  "***** Error in spblock_matrix: row dimensions of ";
           rederr "matrices into spblock_matrix are not compatible";
         >>;
      >>;
      rowdim := rowdim + sprow_dim(nth(nth(row_list,i),j));
      i := i+1;
    >>;
    return rowdim;
  end;

symbolic procedure spcheck_cols(row_list);
  %
  % Checks each element in row_list has same number of columns.
  % Returns this number.
  %
  begin
    integer i,listlen;
    i := 1;
    listlen := length(row_list);
    while i<listlen
    and spno_cols(nth(row_list,i)) = spno_cols(nth(row_list,i+1))
     do i:=i+1;
    if i=listlen then return spno_cols(nth(row_list,i))
     else
     <<
       prin2
        "***** Error in spblock_matrix: column dimensions of matrices ";
       prin2t "      into spblock_matrix are not compatible";
       return;
     >>;
  end;

symbolic procedure spno_rows(mat_list);
  %
  % Takes list of matrices and sums the no. of rows.
  %
  for each mat1 in mat_list sum sprow_dim(mat1);




symbolic procedure spno_cols(mat_list);
  %
  % Takes list of matrices and sums the no. of columns.
  %
  for each mat1 in mat_list sum spcol_dim(mat1);

% Copies matrix BB into AA with BB(1,1) at AA(p,q).

symbolic procedure spcopy_into(BB,AA,p,q);
  begin
    scalar A,B;
    integer m,n,r,c,val,j,col;
    if not !*fast_la then
    <<
      if not matrixp(BB) then rederr
       "Error in spcopy_into(first argument): should be a matrix.";
      if not matrixp(AA) then rederr
       "Error in spcopy_into(second argument): should be a matrix.";
      if not fixp p then rederr
       "Error in spcopy_into(third argument): should be an integer.";
      if not fixp q then rederr
       "Error in spcopy_into(fourth argument): should be an integer.";
      if p = 0 or q = 0 then
      <<
        prin2t
         "***** Error in spcopy_into: 0 is out of bounds for matrices.";
        prin2t
         "      The top left element is labelled (1,1) and not (0,0).";
        return;
      >>;
    >>;
    if not sparsematp(BB) then BB:=sptransmat(BB);
    m := sprow_dim(AA);
    n := spcol_dim(AA);
    r := sprow_dim(BB);
    c := spcol_dim(BB);
    if not !*fast_la and (r+p-1>m or c+q-1>n) then
    <<
      % Only print offending matrices if they're not too big.
      if m*n<26 and r*c<26 then
      <<
        prin2t "***** Error in spcopy_into: the matrix";
        myspmatpri2(BB);
        prin2t "      does not fit into";
        myspmatpri2(AA);
        prin2  "      at position ";
        prin2 p;
        prin2 ",";
        prin2 q;
        prin2t ".";
        return;
      >>
      else       <<
        prin2 "***** Error in spcopy_into: first matrix does not fit ";
        prin2 "      into second matrix at defined position.";
        return;
      >>;
    >>;
    a := copy_vect(aa,list('spm,m,n));
    for i:=r step -1 until 1 do
    << col:=findrow(bb,i);
       if col then
       <<for each xx in cdr col do
         << val:=cdr xx;
             j:=car xx;
            letmtr3(list(a,p+i-1,q+j-1),val,a,nil);
         >>;
       >>;
    >>;
    return a;
  end;

flag ('(spcopy_into),'opfn);

% Swaps row1 with row2.

symbolic procedure swaprow(ilist,row1,row2,len);
 begin scalar r1,r2,rlist,nlist,alist,a,b,cnt,aa,bb,list;
  list:=ilist;
  r1:=assoc(row1,list);
  r2:=assoc(row2,list);
  if r1=nil and not (r2 = nil)
    then << a:= row1; aa:=(row1 . cdr r2); b:=car r2>>
   else if r2 = nil and not (r1=nil)
     then << b:=row2; bb:=(row2 . cdr r1); a:=car r1>>
    else if not(r1=nil and r2=nil) then <<b:=car r2; a:=car r1>>
     else cnt :=len + 1;
   cnt := 1;
   while not (cnt = len +1) do
    << if list = nil then alist := list(list)
        else alist:=car list;
       if car alist = a then
        << if r2 = nil then
            << if cnt = b then <<nlist:=aa . nlist;
                                  list := cdr list>>
                else if cnt = a then << nlist := nlist;
                                       list:=cdr list>>;
            >>
            else
             << rlist := (a . cdr r2);
                nlist := rlist . nlist;
                list := cdr list
             >>;
        >>
       else if car alist = b then
        << if r1 = nil then
            << if cnt = a then <<nlist:=aa . nlist;
                                  list := cdr list>>
                else if cnt=b then << nlist := nlist;
                                       list:=cdr list>>;
            >>
            else
             << rlist := (b . cdr r1);
                nlist := rlist . nlist;
                list := cdr list
             >>;
        >>
       else if r1=nil and not(cnt = len+1) and cnt = a  then
        nlist := aa . nlist
       else if r2=nil and not (cnt = len+1) and cnt = b  then
        nlist := bb . nlist
       else
        << if alist = '(nil) then nil
           else << nlist := alist . nlist;
                    list := cdr list>>
        >>;
        cnt := cnt + 1;
      >>;
   if nlist = nil then return ilist
    else return reverse nlist;
 end;


symbolic procedure spswap_rows(in_mat,row1,row2);
  begin
    scalar new_mat,list,pp,r1,r2;
    integer rowdim;
    list := copy_vect(in_mat,nil);
   % if not !*fast_la then  use later
    <<
      if not matrixp in_mat then
       rederr "Error in spswap_rows(first argument): should be a matrix.";
      rowdim := sprow_dim(in_mat);
      if not fixp row1 then rederr
       "Error in spswap_rows(second argument): should be an integer.";
      if not fixp row2 then
     rederr "Error in spswap_rows(third argument): should be an integer.";
      if row1>rowdim or row1=0 then rederr
  "Error in spswap_rows(second argument): out of range for input matrix.";
      if row2>rowdim or row2=0 then rederr
  "Error in spswap_rows(third argument): out of range for input matrix.";
    >>;
    if row1 < row2 then nil else <<pp:=row1; row1:=row2; row2:=pp;>>;
    r1:=findrow(list,row1);
    r2:=findrow(list,row2);
    letmtr3(list(list,row1),r2,list,nil);
    letmtr3(list(list,row2),r1,list,nil);
    return list;
  end;

% Swaps col1 with col2.

symbolic procedure swapcol(ilist,col1,col2,len);
 begin scalar c1,c2,rlist,nlist,alist,a,b,aa,bb,cnt,rown,list,row;
  cnt := 1;
  for i:=len step -1 until 1 do
   << row:=findrow(ilist,i);
      if not (row=nil) then
      <<
      c1:=atsoc(col1,row);
      c2:=atsoc(col2,row);
      if c1=nil and not (c2 = nil)
        then << a:= col1; aa:=(col1 . cdr c2); b:=car c2>>
       else if c2 = nil and not (c1=nil)
        then << b:=col2; bb:=(col2 . cdr c1); a:=car c1>>
        else if not(c1=nil and c2=nil) then <<b:=car c2; a:=car c1>>
         else cnt :=len + 1;
      rown:=i;
      list :=cdr row;
   while not (cnt = len + 1) do
    << if list = nil then alist:=list(list)
        else alist:=car list;
       if car alist = a then
        << if c2 = nil then
            << if cnt = b then <<nlist := bb . nlist;
                                     list := cdr list>>
               else if cnt = a then <<nlist := nlist;
                                       list := cdr list >>;
            >>
            else
             <<  rlist := (a . cdr c2);
                 nlist := rlist . nlist;
                 list := cdr list
             >>
        >>
       else if car alist = b then
        << if c1 = nil then
            << if cnt = a then <<nlist := aa . nlist;
                                  list := cdr list >>
                else if cnt = b then <<nlist := nlist;
                                        list := cdr list>>;
             >>
            else
             << rlist := (b . cdr c1);
                nlist := rlist . nlist;
                list := cdr list
             >>
        >>
      else if c1 = nil and not(cnt = len+1) and cnt = a  then
        nlist := aa . nlist
       else if c2 = nil and not (cnt = len+1) and cnt = b  then
        nlist := bb . nlist
       else
        << if alist = '(nil) then nil
            else
             << nlist := alist . nlist;
                list := cdr list>>;
        >>;
        cnt:=cnt + 1;
      >>;
    if nlist = nil then letmtr3(list(ilist,rown),list(nil) . list,ilist,nil)
     else letmtr3(list(ilist,rown),list(nil) . reverse nlist,ilist,nil);
    nlist := nil;
    cnt :=1;
    >>;
   >>;
    return ilist;
  end;


symbolic procedure spswap_cols(in_mat,col1,col2);
  begin
    scalar new_mat,list,pp;
    integer coldim;
    list:=copy_vect(in_mat,nil);
    if not !*fast_la then
    <<
      if not matrixp in_mat then rederr
       "Error in spswap_columns(first argument): should be a matrix.";
      coldim := spcol_dim(in_mat);
      if not fixp col1 then rederr
       "Error in spswap_columns(second argument): should be an integer.";
      if not fixp col2 then rederr
       "Error in spswap_columns(third argument): should be an integer.";
      if col1>coldim or col1=0 then rederr
     "Error in spswap_columns(second argument): out of range for matrix.";
      if col2>coldim or col2=0 then rederr
"Error in spswap_columns(third argument): out of range for input matrix.";
    >>;
  if col1 < col2 then nil else <<pp:=col1; col1:=col2; col2:=pp;>>;
    new_mat := swapcol(list,col1,col2,caddr caddr in_mat);
    return new_mat;
  end;

% Swaps the two entries in in_mat.
%
% entry1 and entry2 must be lists of the form
% {row position,column position}.

symbolic procedure spswap_entries(in_mat,entry1,entry2);
  begin
    scalar new_mat;
    integer rowdim,coldim,val1,val2;
    if not matrixp(in_mat) then rederr
     "Error in spswap_entries(first argument): should be a matrix.";
    if atom entry1 or car entry1 neq 'list or length cdr entry1 neq 2
     then rederr
"Error in spswap_entries(second argument): should be list of 2 elements."
     else entry1 := cdr entry1;
     if atom entry2 or car entry2 neq 'list or length cdr entry2 neq 2
      then rederr
"Error in spswap_entries(third argument): should be a list of 2 elements."
      else entry2 := cdr entry2;
    if not !*fast_la then
    <<
      rowdim := sprow_dim(in_mat);
      coldim := spcol_dim(in_mat);
      if not fixp car entry1 then
      <<
        prin2  "***** Error in spswap_entries(second argument): ";
        prin2t "      first element in list must be an integer.";
        return;
      >>;
      if not fixp cadr entry1 then
      <<
        prin2  "***** Error in spswap_entries(second argument): ";
        prin2t "      second element in list must be an integer.";
        return;
      >>;
      if car entry1 > rowdim or car entry1 = 0 then
      <<
        prin2  "***** Error in spswap_entries(second argument): ";
        prin2t "      first element is out of range for input matrix.";
      return;
      >>;
      if cadr entry1 > coldim or cadr entry1 = 0 then
      <<
        prin2  "***** Error in spswap_entries(second argument): ";
        prin2t "      second element is out of range for input matrix.";
        return;
      >>;
      if not fixp car entry2 then
      <<
        prin2  "***** Error in spswap_entries(third argument): ";
        prin2t "      first element in list must be an integer.";
        return;
      >>;
      if not fixp cadr entry2 then
      <<
        prin2  "***** Error in spswap_entries(third argument): ";
        prin2t "      second element in list must be an integer.";
        return;
      >>;
      if car entry2 > rowdim or car entry2 = 0 then
      <<
        prin2  "***** Error in spswap_entries(third argument): ";
        prin2t "      first element is out of range for input matrix.";
        return;
      >>;
      if cadr entry2 > coldim then
      <<
        prin2  "***** Error in spswap_entries(third argument): ";
        prin2t "      second element is out of range for input matrix.";
        return;
      >>;
    >>;
    new_mat := copy_vect(in_mat,nil);
    val1:=findelem2(new_mat,car entry1,cadr entry1);
    val2:=findelem2(new_mat,car entry2,cadr entry2);
  %  if not (val2=0) then
    letmtr3(list(new_mat,car entry1,cadr entry1),val2,new_mat,nil);
   % if not (val1=0) then
    letmtr3(list(new_mat,car entry2,cadr entry2),val1,new_mat,nil);
  return new_mat;
  end;

flag('(spswap_rows,spswap_cols,spswap_entries),'opfn);

% Takes any number of matrices and joins them horizontally.
%
% Can take either a list of matrices or the matrices as seperate
% arguments.

% This function expands the columns of a matrix in ordere to augment it.

% A Further RE-WRITE function.
% Used to re-create elongated matrices.

symbolic procedure rewrite2(list,num);
 begin scalar val,oldcol,newcol,nlist;
  for each col in list do
   << val:=cdr col;
      oldcol:=car col;
      oldcol:=oldcol+num;
      newcol:=(oldcol . val);
      nlist:=newcol . nlist;
   >>;
  return reverse nlist;
  end;

symbolic procedure expan2(mlist,row,list);
 begin scalar rows,cols,rown,newcols,newrows,rlist,cnt,size;
  for i:=1:row do
  <<cnt:=0;
    for each mat1 in mlist do
    << size:=spcol_dim(mat1);
       rows:=findrow(mat1,i);
       if rows = nil then nil
        else << cols := cdr rows;
                rown:= i;
                if cnt=0 then <<newcols:=append(cols ,newcols);
                                cnt:=cnt + size>>
                else <<
                       cols:=rewrite2(cols,cnt);
                       newcols:=append(newcols,cols);
                       cnt:=cnt + size;
                     >>;
             >>;
    >>;
     if not (newcols=nil) then
      <<
         letmtr3(list(list,i),list(nil) . newcols,list,nil);
      >>;
     newcols:=nil;
   >>;
  return list;
 end;

put('spmatrix_augment,'psopfn,'spmatrix_augment1);

symbolic procedure spmatrix_augment1(matrices);
  begin
    scalar mat_list,mat1,new_list,he,tl,num,row,col,list;
    integer cnt;
    if pairp matrices and pairp car matrices and caar matrices = 'list
     then matrices := cdar matrices;
    if not !*fast_la then
    <<
      mat_list := for each elt in matrices collect
         <<if not sparsematp(list:=reval elt) then sptransmat list
            else list>>;
      for each elt in mat_list do
        if not matrixp(elt) then
         rederr "Error in spmatrix_augment: non matrix in input.";
    >>;
    spconst_rows_test(mat_list);
    mat1:=car mat_list;
    row:=sprow_dim(mat1);
    for each mat1 in mat_list do
    <<col:=spcol_dim(mat1);
      cnt:=cnt + col;
    >>;
    col:=cnt;
    list:=mkempspmat(row,list('spm,row,col));
    new_list:=expan2(mat_list,row,list);
    return new_list;
  end;

% Takes any number of matrices and joins them vertically.
%
% Can take either a list of matrices or the matrices as seperate
% arguments.

put('spmatrix_stack,'psopfn,'spmatrix_stack1);

symbolic procedure spmatrix_stack1(matrices);
  begin
    scalar mat_list,new_list,he,tl,nam,row,col,list;
    integer cnt;
    if pairp matrices and pairp car matrices and caar matrices = 'list
     then matrices := cdar matrices;
    if not !*fast_la then
    <<
      mat_list := for each elt in matrices collect
       <<if not sparsematp(list:=reval elt) then sptransmat list
            else list>>;
      for each elt in mat_list do
       if not matrixp(elt) then
        rederr "Error in spmatrix_stack: non matrix in input.";
    >>;
    spconst_columns_test(mat_list);
    col:=spcol_dim(car mat_list);
    for each mat1 in mat_list do
     << row:=sprow_dim(mat1);
        cnt := cnt + row;
     >>;
    row:=cnt;
    new_list:=mkempspmat(row,list('spm,row,col));
    cnt:=1;
    for each mat1 in mat_list do
    << row:=sprow_dim(mat1);
       for i:=1:row do
       << he:=findrow(mat1,i);
          if he then
            letmtr3(list(new_list,cnt),he,new_list,nil);
          cnt:=cnt+1;
        >>;
    >>;
    return new_list;
  end;

symbolic procedure spconst_rows_test(mat_list);
  %
  % Tests that each matrix in mat_list has the same number of rows
  % (otherwise augmentation not possible).
  %
  begin
    integer i,listlen,rowdim;
    listlen := length(mat_list);
    rowdim := sprow_dim(car mat_list);
    i := 1;
    while i<listlen and sprow_dim(car mat_list) = sprow_dim(cadr mat_list)
     do << i := i+1; mat_list := cdr mat_list; >>;
    if i=listlen then return rowdim
     else
     <<
       prin2  "***** Error in spmatrix_augment: ";
       rederr "all input matrices must have the same row dimension.";
     >>;
  end;



symbolic procedure spconst_columns_test(mat_list);
  %
  % Tests that each matrix in mat_list has the same number of columns
  % (otherwise stacking not possible).
  %
  begin
    integer i,listlen,coldim;
    listlen := length(mat_list);
    coldim := spcol_dim(car mat_list);
    i := 1;
    while i<listlen and spcol_dim(car mat_list) =
                         spcol_dim(cadr mat_list)
     do << i := i+1; mat_list := cdr mat_list; >>;
    if i=listlen then return coldim
     else
     <<
       prin2  "***** Error in spmatrix_stack: ";
       rederr
        "all input matrices must have the same column dimension.";
       return;
     >>;
  end;

% Extends in_mat by rows rows (!) and cols columns. New entries are
% initialised to entry.

symbolic procedure spextend(in_mat,rows,cols,entry);
  begin
    scalar ex_mat;
    integer rowdim,coldim,i,j;
    if not matrixp(in_mat) then
     rederr "Error in spextend(first argument): should be a matrix.";
    if not fixp rows then
     rederr "Error in spextend(second argument): should be an integer.";
    if not fixp cols then
     rederr "Error in spextend(third argument): should be an integer.";
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    ex_mat := mkempspmat(rowdim+rows,list('smp,rowdim+rows,coldim+cols));
    ex_mat := spcopy_into(in_mat,ex_mat,1,1);
    for i:=rowdim+rows step -1 until rowdim+1 do
    <<
      for j:=coldim+cols step -1 until coldim+1 do
      <<
        letmtr3(list(ex_mat,i,j),entry,ex_mat,nil);
      >>;
    >>;
    return ex_mat;
  end;

flag('(spextend),'opfn);

% Can take either a list of arguments or the arguments seperately.
%
% Takes any number of either scalar entries or square matrices and
% creates the diagonal.

put('spdiagonal,'psopfn,'spdiagonal1); % To allow variable input.

symbolic procedure spdiagonal1(mat_list);
  begin
    scalar diag_mat;
     if pairp mat_list and pairp car mat_list and caar mat_list = 'list
     then mat_list := cdar mat_list;
    mat_list := for each elt in mat_list collect
                 << if not (sparsematp(aeval elt)) and not numberp elt
                        then sptransmat elt
                     else reval elt
                  >>;
    for each elt in mat_list do
    <<
      if matrixp(elt) and not squarep(elt) then
      <<
        % Only print offending matrix if it's not too big.
        if sprow_dim(elt)<5 or spcol_dim(elt)> 5 then
        <<
          prin2t "***** Error in spdiagonal: ";
          myspmatpri2(elt);
          prin2t "      is not a square matrix.";
          rederr "";
        >>
        else
         rederr "Error in spdiagonal: input contains non square matrix.";
      >>;
    >>;
    diag_mat := spdiag({mat_list});
    return diag_mat;
  end;

symbolic procedure spdiag(uu);
  %
  % Takes square or scalar matrix entries and creates a matrix with
  % these matrices on the diagonal.
  %
  begin
    scalar bigA,arg,input,u,val,a,b,col,j;
    integer nargs,Aidx,stp,bigsize,smallsize;

    u := car uu;
    input := u;
    bigsize:=0;

    nargs:=length input;
    for i:=1:nargs do
    <<
      arg:=car input;
      % If scalar entry.
      if algebraic length(arg) = 1 then bigsize:=bigsize+1
      else
      <<
        bigsize:=bigsize+sprow_dim(arg);
      >>;
      input := cdr input;
    >>;

    bigA := mkempspmat(bigsize,list('spm,bigsize,bigsize));
    Aidx:=1;
    input := u;
    for k:=1:nargs do
    <<
      arg:=car input;
      % If scalar entry.
      if algebraic length(arg) = 1 then
      <<
        letmtr3(list(bigA,Aidx,Aidx),arg,bigA,nil);
        Aidx:=Aidx+1;
        input := cdr input;
      >>
      else
      <<
        smallsize:= sprow_dim(arg);
        stp:=smallsize+Aidx-1;
        a:=1;
        for i:=Aidx:stp do
        << col:=findrow(arg,a);
           if col then
           << for each xx in cdr col do
              << val:=cdr xx;
                   j:=(Aidx-1)+car xx;
                 letmtr3(list(bigA,i,j),val,bigA,nil);
              >>;
           >>;
               a:=a+1;
        >>;
        Aidx := Aidx+smallsize;
        input := cdr input;
      >>;
    >>;

    return biga;
  end;

% Replaces row2 (r2) by mult1*r1 + r2.

symbolic procedure spadd_rows(in_mat,r1,r2,mult1);
  begin
    scalar new_mat,val,val1,val2,row1,row2;
    integer i,rowdim,coldim;
    coldim := spcol_dim(in_mat);
    if not !*fast_la then
    <<
      if not matrixp in_mat then
       rederr "Error in spadd_rows(first argument): should be a matrix.";
      rowdim := sprow_dim(in_mat);
      if not fixp r1 then
     rederr "Error in spadd_rows(second argument): should be an integer.";
      if not fixp r2 then
     rederr "Error in spadd_rows(third argument): should be an integer.";
      if r1>rowdim or r1=0 then rederr
   "Error in spadd_rows(second argument): out of range for input matrix.";
      if r2>rowdim or r2=0 then rederr
   "Error in spadd_rows(third argument): out of range for input matrix.";
    >>;
    new_mat := copy_vect(in_mat,nil);
    % Efficiency.
    if (my_reval mult1) = 0 then return new_mat;
    row1:=findrow(in_mat,r1);
    row2:=findrow(in_mat,r2);
    for each xx in cdr row1 do
    << i:=car xx;
       val1:=cdr xx;
       val2:=atsoc(i,row2);
       val:=reval {'times,mult1,val1};
       if val2 then
        <<val:=reval {'plus,val,cdr val2};
          if not (val=0) then letmtr3(list(new_mat,r2,i),val,new_mat,nil);
        >>
        else letmtr3(list(new_mat,r2,i),val,new_mat,nil);
     >>;
    return new_mat;
  end;

% Replaces column2 (c2) by mult1*c1 + c2.

symbolic procedure spadd_columns(in_mat,c1,c2,mult1);
  begin
    scalar new_mat,val;
    integer i,rowdim,coldim;
    rowdim := sprow_dim(in_mat);
    if not !*fast_la then
    <<
      if not matrixp in_mat then
     rederr "Error in spadd_columns(first argument): should be a matrix.";
      coldim := spcol_dim(in_mat);
      if not fixp c1 then rederr
       "Error in spadd_columns(second argument): should be an integer.";
      if not fixp c2 then rederr
       "Error in spadd_columns(third argument): should be an integer.";
      if c1>coldim or c1=0 then rederr
"Error in spadd_columns(second argument): out of range for input matrix.";
      if c2>rowdim or c2=0 then rederr
 "Error in spadd_columns(third argument): out of range for input matrix.";
    >>;
    new_mat := copy_vect(in_mat,nil);
    if (my_reval mult1) = 0 then return new_mat;
    for i:=1:rowdim do
    <<val:=reval {'plus,{'times,mult1,
              findelem2(new_mat,i,c1)},findelem2(in_mat,i,c2)};
      if not (val=0) then
      letmtr3(list(new_mat,i,c2),val,new_mat,nil);
    >>;
    return new_mat;
  end;

flag('(spadd_rows,spadd_columns),'opfn);

% Adds value to each element in each row in row_list.
%
% row_list can be either an integer or a list of integers.

symbolic procedure spadd_to_rows(in_mat,row_list,value);
  begin
    scalar new_mat,col,val;
    integer i,rowdim,coldim;
    if not matrixp in_mat then
     rederr "Error in spadd_to_row(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in spadd_to_rows(second argument): ";
        prin2t "      should be either integer or a list of integers.";
        return;
      >>;
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    new_mat := copy_vect(in_mat,nil);
    for each row in row_list do
    <<
      if not fixp row then rederr
       "Error in spadd_to_row(second argument): should be an integer.";
      if row>rowdim or row=0 then
      <<
        prin2  "***** Error in spadd_to_rows(second argument): ";
        rederr "contains row which is out of range for input matrix.";
      >>;
     >>;
     for each row in row_list do
      <<for i:=coldim step -1 until 1 do
       letmtr3(list(new_mat,row,i),value,new_mat,nil);
       col:=findrow(in_mat,row);
       if col then
       <<for each xx in cdr col do
         <<i:=car xx;
           val:=cdr xx;
           letmtr3(list(new_mat,row,i),reval {'plus,val,value},new_mat,nil);
         >>;
       >>;
     >>;
    return new_mat;
  end;

symbolic procedure spadd_to_columns(in_mat,col_list,value);
  %
  % Adds value to each element in each column in col_list.
  %
  % col_list can be either an integer or a list of integers.
  %
  begin
    scalar new_mat,col,val;
    integer i,rowdim,coldim;
    if not matrixp in_mat then rederr
     "Error in spadd_to_columns(first argument): should be a matrix.";
    if atom col_list then col_list := {col_list}
     else if car col_list = 'list then col_list := cdr col_list
      else
      <<
        prin2  "***** Error in spadd_to_columns(second argument): ";
        prin2t "      should be either integer or list of integers.";
        return;
      >>;
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    new_mat := copy_vect(in_mat,nil);
    for each col in col_list do
    <<
      if not fixp col then rederr
      "Error in spadd_to_columns(second argument): should be an integer.";
      if col>coldim or col=0 then
      <<
        prin2  "***** Error in spadd_to_columns(second argument): ";
        rederr
         "contains column which is out of range for input matrix.";
      >>;
    >>;
      for i:=1:rowdim do
      <<col:=findrow(in_mat,i);
        for each xx in col_list do
        <<val:=atsoc(xx,col);
          if val then
           <<letmtr3(list(new_mat,i,xx),reval
                      {'plus,cdr val,value},new_mat,nil);
           >>
           else letmtr3(list(new_mat,i,xx),value,new_mat,nil);
       >>;
      >>;
    return new_mat;
  end;

flag('(spadd_to_rows,spadd_to_columns),'opfn);

% Replaces rows specified in row_list by row * mult1.

symbolic procedure spmult_rows(in_mat,row_list,mult1);
  begin
    scalar new_mat,col;
    integer i,rowdim,coldim,val;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in spmult_rows(first argument): should be a matrix.";
    if atom row_list then row_list := {row_list}
     else if car row_list = 'list then row_list := cdr row_list;
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    new_mat := copy_vect(in_mat,nil);
    for each row in row_list do
    <<
      if not !*fast_la and not fixp row then rederr
       "Error in spmult_rows(second argument): contains non integer.";
      if not !*fast_la and (row>rowdim or row=0) then
      <<
        prin2  "***** Error in spmult_rows(second argument): ";
        rederr "contains row that is out of range for input matrix.";
      >>;
      col:=findrow(in_mat,row);
      if col then
      <<for each xx in cdr col do
        <<i:=car xx;
          val:=cdr xx;
          letmtr3(list(new_mat,row,i),reval{'times,mult1,val},
                   new_mat,nil);
        >>;
      >>;
    >>;
    return new_mat;
  end;

% Replaces columns specified in column_list by column * mult1.

symbolic procedure spmult_columns(in_mat,column_list,mult1);
  begin
    scalar new_mat,col;
    integer i,rowdim,coldim,val;
    if not !*fast_la and not matrixp(in_mat) then
    rederr "Error in spmult_columns(first argument): should be a matrix.";
    if atom column_list then column_list := {column_list}
    else if car column_list = 'list then column_list := cdr column_list;
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    new_mat := copy_vect(in_mat,nil);
    for each column in column_list do
    <<
      if not !*fast_la and not fixp column then rederr
       "Error in spmult_columns(second argument): contains non integer.";
      if not !*fast_la and (column>coldim or column=0) then
      <<
        prin2  "***** Error in spmult_columns(second argument): ";
        rederr "contains column that is out of range for input matrix.";
      >>;
     >>;
      for i:=1:rowdim do
      << col:=findrow(in_mat,i);
         if col then
         <<for each xx in column_list do
           << val:=atsoc(xx,col);
              if val then
              letmtr3(list(new_mat,i,xx),reval
                         {'times,mult1,cdr val}, new_mat,nil);
           >>;
         >>;
       >>;
    return new_mat;
  end;

flag('(spmult_rows,spmult_columns),'opfn);

% Create characteristic matrix. ie: C := lmbda*I - in_mat.
% in_ mat must be square.

symbolic procedure spchar_matrix(in_mat,lmbda);
  begin
    scalar charmat;
    integer rowdim;
    if not matrixp(in_mat) then
     rederr "Error in spchar_matrix(first argument): should be a matrix.";
    if not squarep(in_mat) then rederr
     "Error in spchar_matrix(first argument): must be a square matrix.";
    rowdim := sprow_dim(in_mat);
    charmat := {'plus,{'times,lmbda,spmake_identity(rowdim)},
               {'minus,in_mat}};
    return charmat;
  end;

% Finds characteristic polynomial of matrix in_mat.
% ie: det(lmbda*I - in_mat).

symbolic procedure spchar_poly(in_mat,lmbda);
  begin
    scalar chpoly,carmat;
    if not matrixp(in_mat) then
     rederr "Error in spchar_poly(first argument): should be a matrix.";
    carmat := spchar_matrix(in_mat,lmbda);
    chpoly := algebraic det(carmat);
    return chpoly;
  end;

flag('(spchar_matrix,spchar_poly),'opfn);

% Computes the Hermitian transpose (HT say) of in_mat.
%
% The (i,j)'th element of HT = conjugate of the (j,i)'th element of
% in__mat.

symbolic procedure sphermitian_tp(in_mat);
  begin
    scalar h_tp,element;
    integer ii,row,col;
    if not matrixp(in_mat) then
     rederr "Error in sphermitian_tp: non matrix input.";
    h_tp := algebraic tp(in_mat);
    for row:=1:sprow_dim(h_tp) do
    <<col:=findrow(h_tp,row);
      if col then
      <<for each xx in cdr col do
        <<ii:=car xx;
           element := cdr xx;
        letmtr3(list(h_tp,row,ii),
               algebraic (repart(element) - i*impart(element)),h_tp,nil);
        >>;
      >>;
     >>;
    return h_tp;
  end;

flag('(sphermitian_tp),'opfn);

% Removes the sub_matrix from A consisting of the rows in row_list and
% the columns in col_list. (Both row_list and col_list can be single
% integer values).

symbolic procedure spsub_matrix(A,row_list,col_list);
  begin
    scalar new_mat;
    if not !*fast_la and not matrixp(A) then rederr
     "Error in spsub_matrix(first argument): should be a matrix.";
    new_mat := spstack_rows(A,row_list);
    new_mat := spaugment_columns(new_mat,col_list);
    return new_mat;
  end;

flag('(spsub_matrix),'opfn);

% Converts all elements in pivot column (apart from the one in pivot
% row) to 0.

symbolic procedure sppivot(in_mat,pivot_row,pivot_col);
  begin
    scalar piv_mat,ratio,val,col,val1,val2;
    integer i,j,rowdim,coldim;
    if not !*fast_la and not matrixp(in_mat) then
     rederr "Error in sppivot(first argument): should be a matrix.";
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    if not !*fast_la then
    <<
    if not fixp pivot_row then
     rederr "Error in sppivot(second argument): should be an integer.";
    if pivot_row>rowdim or pivot_row=0 then rederr
     "Error in sppivot(second argument): out of range for input matrix.";
    if not fixp pivot_col then rederr
     "Error in sppivot(third argument): should be an integer.";
    if pivot_col>coldim or pivot_col=0 then rederr
     "Error in sppivot(third argument): out of range for input matrix.";
    if findelem2(in_mat,pivot_row,pivot_col) = 0 then
     rederr "Error in sppivot: cannot pivot on a zero entry.";
    >>;
    piv_mat := copy_vect(in_mat,nil);
    val2:=findelem2(in_mat,pivot_row,pivot_col);
    for i:=1:rowdim do
    << col:=findrow(in_mat,i);
       val1:=atsoc(pivot_col,col);
       if val1 then val1:=cdr val1;
       ratio := reval {'quotient,val1,val2};
       if col then
       <<for each xx in cdr col do
         <<j:=car xx;
           val1:=cdr xx;
           if i = pivot_row then <<>>
           else
           <<val:=reval {'plus,val1,
                 {'minus,{'times,ratio,findelem2(in_mat,pivot_row,j)}
                      }};
             letmtr3(list(piv_mat,i,j),val,piv_mat,nil);
            >>;
        >>;
      >>;
    >>;
    return piv_mat;
  end;

% Same as pivot but only rows a .. to .. b, where row_list = {a,b},
% are changed.
%
% rows_pivot will work if row_list is just an integer.

symbolic procedure sprows_pivot(in_mat,pivot_row,pivot_col,row_list);
  begin
    scalar piv_mat,ratio,val,col,val1,val2;
    integer j,rowdim,coldim;
    rowdim := sprow_dim(in_mat);
    coldim := spcol_dim(in_mat);
    if not !*fast_la then
    <<
      if not matrixp(in_mat) then
      rederr "Error in sprows_pivot(first argument): should be a matrix.";
      if not fixp pivot_row then
       rederr "Error in sprows_pivot(second argument): should be an integer.";
      if pivot_row>rowdim or pivot_row=0 then rederr
 "Error in sprows_pivot(second argument): out of range for input matrix.";
      if not fixp pivot_col then
       rederr "Error in sprows_pivot(third argument): should be an integer.";
      if pivot_col>coldim or pivot_col=0 then rederr
  "Error in sprows_pivot(third argument): out of range for input matrix.";
    >>;
    if atom row_list then row_list := {row_list}
     else if pairp row_list and car row_list = 'list
     then row_list := cdr row_list
      else
      <<
        prin2  "***** Error in sprows_pivot(fourth argument): ";
        prin2t
         "      should be either an integer or a list of integers.";
        return;
      >>;
    if findelem2(in_mat,pivot_row,pivot_col) = 0 then
     rederr "Error in sprows_pivot: cannot pivot on a zero entry.";
    piv_mat := copy_vect(in_mat,nil);
     val2:=findelem2(in_mat,pivot_row,pivot_col);
    for each elt in row_list do
    <<
      if not !*fast_la then
      <<
        if not fixp elt then rederr
         "Error in sprows_pivot: fourth argument contains a non integer.";
        if elt>rowdim or elt=0 then
        <<
          prin2  "***** Error in sprows_pivot(fourth argument): ";
          rederr "contains row which is out of range for input matrix.";
        >>;
      >>;
      if elt = pivot_row then nil
       else ratio := reval {'quotient,findelem2(in_mat,elt,pivot_col),val2};
       col:=findrow(in_mat,elt);
       if col then
       <<for each xx in cdr col do
         <<j:=car xx;
           val1:=cdr xx;
           val:=reval {'plus,val1, {'minus,
                   {'times,ratio,findelem2(in_mat,pivot_row,j)}}};
               letmtr3(list(piv_mat,elt,j),val,piv_mat,nil);
         >>;
        >>;
      >>;
    return piv_mat;
  end;

flag('(sppivot,sprows_pivot),'opfn);

% jacobian(exp,var) computes the Jacobian matrix of exp w.r.t. var.
% The (i,j)'th entry is diff(nth(exp,i),nth(var,j)).

symbolic procedure spjacobian(exp_list,var_list);
  begin
    scalar jac,exp1,var1,val;
    integer i,j,rowdim,coldim;
    if atom exp_list then exp_list := {exp_list}
     else if car exp_list neq 'list then rederr
     "Error in spjacobian(first argument): expressions must be in a list."
      else exp_list := cdr exp_list;
    if atom var_list then var_list := {var_list}
     else if car var_list neq 'list then rederr
      "Error in jacobian(second argument): variables must be in a list."
      else var_list := cdr var_list;
    rowdim := length exp_list;
    coldim := length var_list;
    jac := mkempspmat(rowdim,list('spm,rowdim,coldim));
    for i:=1:rowdim do
    <<
      for j:=1:coldim do
      <<
        exp1 := nth(exp_list,i);
        var1 := nth(var_list,j);
        val:= algebraic df(exp1,var1);
        if val = 0 then nil
         else letmtr3(list(jac,i,j),val,jac,nil);
      >>;
    >>;
    return jac;
  end;

flag('(spjacobian),'opfn);

% variables can be either a list or a single variable.
%
% A Hessian matrix is a matrix whose (i,j)'th entry is
%       df(df(poly,nth(var,i)),nth(var,j))
%
% where df is the derivative.

symbolic procedure sphessian(poly,variables);
  begin
    scalar hess_mat,part1,part2,elt;
    integer row,col,sq_size;
    if atom variables then variables := {variables}
     else if car variables = 'list then variables := cdr variables
      else
      <<
        prin2  "***** Error in sphessian(second argument): ";
        prin2t
     "      should be either a single variable or a list of variables.";
        return;
      >>;
    sq_size := length variables;
    hess_mat := mkempspmat(sq_size,list('spm,sq_size,sq_size));
    for row:=1:sq_size do
    <<
      for col:=1:sq_size do
      <<
        part1 := nth(variables,row);
        part2 := nth(variables,col);
        elt := algebraic df(df(poly,part1),part2);
        if elt = 0 then nil
         else letmtr3(list(hess_mat,row,col),elt,hess_mat,nil);
      >>;
    >>;
    return hess_mat;
  end;

flag('(sphessian),'opfn);

% Given the system of linear equations, coeff_matrix returns {A,X,b}
% s.t. AX = b.
%
% Input can be either a list of linear equations or the linear
% equations as individual arguments.

% To allow variable input.
put('spcoeff_matrix,'psopfn,'spcoeff_matrix1);

symbolic procedure spcoeff_matrix1(equation_list);
  begin
    scalar variable_list,A,X,b;
    if pairp car equation_list and caar equation_list = 'list then
     equation_list := cdar equation_list;
    equation_list := remove_equals(equation_list);
    variable_list := get_variable_list(equation_list);
    if variable_list = nil then
     rederr "Error in spcoeff_matrix: no variables in input.";
    check_linearity(equation_list,variable_list);
    A := spget_A(equation_list,variable_list);
    X := spget_X(variable_list);
    b := spget_b(equation_list,variable_list);
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
       rederr "Error in spcoeff_matrix: the equations are not linear.";
    >>;
  >>;



symbolic procedure spget_A(equation_list,variable_list);
  begin
    scalar A,element,var_elt,val;
    integer row,col,length_equation_list,length_variable_list;
    length_equation_list := length equation_list;
    length_variable_list := length variable_list;
    A := mkempspmat(length equation_list,
             list('spm,length equation_list,length variable_list));
    for row:=1:length_equation_list do
    <<
      for col:=1:length_variable_list do
      <<
        element := nth(equation_list,row);
        var_elt := nth(variable_list,col);
        val:=algebraic coeffn(element,var_elt,1);
        if val = 0 then nil
         else letmtr3(list(A,row,col),val,A,nil);
      >>;
    >>;
    return A;
  end;

symbolic procedure spget_b(equation_list,variable_list);
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
    b := mkempspmat(length_integer_list,list('spm,length_integer_list,1));
    for row:=1:length_integer_list do
     letmtr3(list(b,row,1),-nth(integer_list,row),b,nil);
    return b;
  end;



symbolic procedure spget_X(variable_list);
  begin
    scalar X;
    integer row,length_variable_list;
    length_variable_list := length variable_list;
    X := mkempspmat(length_variable_list,list('spm,length_variable_list,1));
    for row := 1:length variable_list do
     letmtr3(list(X,row,1),nth(variable_list,row),x,nil);
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

% Takes as input a monic univariate polynomial in a variable x.
% Returns a companion matrix associated with the polynomial poly(x).
%
% If C := companion(p,x) and p is a0+a1*x+...+x^n (a univariate monic
% polynomial), them C(i,n) = -coeff(p,x,i-1), C(i,i-1) = 1 (i=2..n)
% and C(i,j) = 0 for all other i and j.

symbolic procedure spcompanion(poly,x);
  begin
    scalar mat1;
    integer n,val;
    n := deg(poly,x);
    if my_reval coeffn(poly,x,n) neq 1 then msgpri
      ("Error in spcompanion(first argument): Polynomial",
      poly, "is not monic.",nil,t);

    mat1 := mkempspmat(n,list('smp,n,n));
    val:=coeffn(poly,x,0);
    if val=0 then nil
     else letmtr3(list(mat1,1,n),{'minus,val},mat1,nil);
    for i:=2:n do
    <<
      letmtr3(list(mat1,i,i-1),1,mat1,nil);
    >>;
    for j:=2:n do
    << val:=coeffn(poly,x,j-1);
       if val = 0 then nil
        else letmtr3(list(mat1,j,n),{'minus,val},mat1,nil);
    >>;
    return mat1;
  end;

% Given a companion matrix, find_companion will return the associated
% polynomial.

symbolic procedure spfind_companion(R,x);
  begin
    scalar  p;
    integer rowdim,k;
    if not matrixp(R) then rederr
     {"Error in spfind_companion(first argument): should be a matrix."};
    rowdim := sprow_dim(R);
    k := 2;
    while k<=rowdim and findelem2(R,k,k-1)=1 do k:=k+1;
    p := 0;
    for j:=1:k-1 do
    <<
      p:={'plus,p,{'times,{'minus,findelem2(R,j,k-1)},{'expt,x,j-1}}};
    >>;
    p := {'plus,p,{'expt,x,k-1}};
    return p;
  end;

flag('(spcompanion,spfind_companion),'opfn);

endmodule;

end;

%***********************************************************************
%=======================================================================
%
% End of Code.
%
%=======================================================================
%***********************************************************************

%in "splu_decomp.red";
%in "spsvd.red";
%in "spcholesky.red";
%in "spgramshm.red";


