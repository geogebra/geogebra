module ludecom;

%**********************************************************************%
%                                                                      %
% Computation of the LU decomposition of dense unsymmetric matrices    %
% containing either numeric entries or complex numbers with numeric    %
% coefficients.                                                        %
%                                                                      %
% Author: Matt Rebbeck, June 1994.                                     %
%                                                                      %
% The algorithm was taken from "Linear Algebra" - J.H.Wilkinson        %
%                                                  & C. Reinsch        %
%                                                                      %
%                                                                      %
% NB: By using the same rounded number techniques as used in svd this  %
%     could be made a lot faster.                                      %
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




%%%%%%%%%%%%%%%%%%%%%%%%%%% begin get_num_part %%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
% This bit of code is used in lu_decom, cholesky, and svd.             %
%                                                                      %

symbolic procedure get_num_part f;
  %
  % When comparing (ie: a < b) we need to get hold of the actual
  % numerical values. That's what this does.
  %
  % Nicked from H. Melenk's gnuplot code.
  %
  if f = 0 then f
   else if numberp f then float f
%   else if f='pi then 3.141592653589793238462643
%   else if f='e then 2.7182818284590452353602987
   else if atom f then f
   else if eqcar(f, '!:RD!:) then
          if atom cdr f then cdr f else bf2flr f
   else if eqcar(f, '!:DN!:) then rdwrap2 cdr f
   else if eqcar(f, 'MINUS) then
    begin scalar x;
       x := get_num_part cadr f;
       return if numberp x then minus float x else {'MINUS,x}
    end
   else if eqcar(f,'expt) then rdwrap!-expt f
   else get_num_part car f . get_num_part cdr f;



symbolic procedure rdwrap!-expt f;
% preserve integer second argument.
  if fixp caddr f then {'expt!-int,get_num_part cadr f,caddr f}
   else  {'expt,get_num_part cadr f, get_num_part caddr f};



symbolic procedure rdwrap2 f;
% Convert from domain to LISP evaluable value.
  if atom f then f else float car f * 10^cdr f;



symbolic procedure rdwrap!* f;
% convert a domain element to float.
  if null f then 0.0 else get_num_part f;



symbolic procedure expt!-int(a,b); expt(a,fix b);

%                                                                      %
%                                                                      %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%% end get_num_part %%%%%%%%%%%%%%%%%%%%%%%%%%%



symbolic procedure lu_decom(in_mat);
  %
  % Runs the show!
  %
  begin
    scalar ans,I_turned_rounded_on;
    integer sq_size;
    if not matrixp(in_mat) then
     rederr "Error in lu_decom: non matrix input.";
    if not squarep(in_mat) then
     rederr "Error in lu_decom: input matrix should be square.";
    if not !*rounded then << I_turned_rounded_on := t; on rounded; >>;
    sq_size := first size_of_matrix(in_mat);
    if cx_test(in_mat,sq_size) then ans := compdet(in_mat)
     else ans := unsymdet(in_mat);
    if I_turned_rounded_on then off rounded;
    return ans;
  end;

flag('(lu_decom),'opfn);  % So it can be used from algebraic mode.



symbolic procedure cx_test(in_mat,sq_size);
  %
  % Tests to see if any elts are complex. (boolean).
  %
  begin
    scalar bool,elt;
    integer i,j;
    i := 1;
    while not bool and i<=sq_size do
    <<
      j := 1;
      while not bool and j<=sq_size do
      <<
        elt := getmat(in_mat,i,j);
        if algebraic(impart(elt)) neq 0 then bool := t;
        j := j+1;
      >>;
      i := i+1;
    >>;
    return bool;
  end;

flag('(cx_test),'boolean);



symbolic procedure unsymdet(mat1);
  %
  % LU decomposition is performed on the unsymmetric matrix A.
  % ie: A := LU.
  % The determinant (d1*2^d2) of A is also computed as a by product but
  % has been commented out as it is not necessary. A record of any
  % interchanges made to the rows of A is kept in int_vec[i] (i=1...n)
  % such that the i'th row and the int_vec[i]'th row were interchanged
  % at the i'th step.The procedure will fail if A, modified by rounding
  % errors, is singular or singular within the bounds of the machine
  % accuracy (ie: acc s.t. 1+acc > 1).
  %
  begin
    scalar x,y,in_mat,tmp,int_vec,L,U; %d1,d2,det;
    integer i,j,k,l,n;
    j := 1;
    in_mat := copy_mat(mat1);
    n := first size_of_matrix(in_mat);
    int_vec := mkvect(n-1);
    for i:=1:n do
    <<
      y := innerprod(1,1,n,0,row_vec(in_mat,i,n),row_vec(in_mat,i,n));
      putv(int_vec,i-1,{'quotient,1,{'sqrt,y}});
    >>;
%   d1 := 1;
%   d2 := 0;
    for k:=1:n do
    <<
      l := k;
      x := 0;
      for i:=k:n do
      <<
        y := innerprod(1,1,k-1,{'minus,getmat(in_mat,i,k)},
                       row_vec(in_mat,i,n),col_vec(in_mat,k,n));
        setmat(in_mat,i,k,{'minus,y});
        y := abs(get_num_part(reval{'times,y,getv(int_vec,i-1)}));
        if y>get_num_part(my_reval(x)) then
        <<
          x := y;
          l := i;
        >>;
      >>;
      if l neq k then
      <<
%       d1 := {'minus,d1};
        for j:=1:n do
        <<
          y := getmat(in_mat,k,j);
          setmat(in_mat,k,j,getmat(in_mat,l,j));
          setmat(in_mat,l,j,y);
        >>;
        putv(int_vec,l-1,getv(int_vec,k-1));;
      >>;
      putv(int_vec,k-1,l);
%     d1 := {'times,d1,getmat(in_mat,k,k)};
      if get_num_part(my_reval(x)) <
          get_num_part(reval{'times,8,rd!-tolerance!*}) then rederr
"Error in lu_decom: matrix is singular. LU decomposition not possible.";
%    while abs(get_num_part(reval(d1))) >= 1 do
%    <<
%      d1 := {'times,d1,0.0625};
%      d2 := d2+4;
%    >>;
%   while abs(get_num_part(reval(d1))) < 0.0625 do
%      <<
%        d1 := {'times,d1,16};
%        d2 := d2-4;
%      >>;
      x := {'quotient,{'minus,1},getmat(in_mat,k,k)};
      for j:=k+1:n do
      <<
        y := innerprod(1,1,k-1,{'minus,getmat(in_mat,k,j)},
                       row_vec(in_mat,k,n),col_vec(in_mat,j,n));
        setmat(in_mat,k,j,{'times,x,y});
      >>;
    >>;
    tmp := get_l_and_u(in_mat,n);
    L := first tmp;
    U := second tmp;
    % Compute determinant.
    %det := {'times,d1,{'expt,2,d2}};
    return {'list,L,U,int_vec};
  end;



symbolic procedure innerprod(l,s,u,c1,vec_a,vec_b);
  %
  % This procedure accumulates the sum of products vec_a*vec_b and adds
  % it to the initial value c1.  (ie: the scalar product).
  %
  begin
    scalar s1,d1;
    s1 := c1;
    d1 := s1;
    for k:=l step s until u do
    <<
      s1 := {'plus,s1,{'times,getv(vec_a,k),getv(vec_b,k)}};
      d1 := s1;
    >>;
    return d1;
  end;



symbolic procedure row_vec(in_mat,row_no,length_of);
  %
  % Converts matrix row into vector.
  %
  begin
    scalar row_vec;
    integer i;
    row_vec := mkvect(length_of);
    for i:=1:length_of do putv(row_vec,i,getmat(in_mat,row_no,i));
    return row_vec;
  end;



symbolic procedure col_vec(in_mat,col_no,length_of);
  %
  % Converts matrix column into vector.
  %
  begin
    scalar col_vec;
    integer i;
    col_vec := mkvect(length_of);
    for i:=1:length_of do putv(col_vec,i,getmat(in_mat,i,col_no));
    return col_vec;
  end;



symbolic procedure get_l_and_u(in_mat,sq_size);
  %
  % Takes the combined LU matrix and returns L and U.
  % sq_size is the no of rows (and columns) of in_mat.
  %
  begin
    scalar L,U;
    integer i,j;
    L := mkmatrix(sq_size,sq_size);
    U := mkmatrix(sq_size,sq_size);
    for i:=1:sq_size do
    <<
      for j:=1:i do
      <<
        setmat(L,i,j,getmat(in_mat,i,j));
      >>;
    >>;
    for i:=1:sq_size do
    <<
      setmat(U,i,i,1);
      for j:=i+1:sq_size do
      <<
        setmat(U,i,j,getmat(in_mat,i,j));
      >>;
    >>;
    return {L,U};
  end;



symbolic procedure compdet(mat1);
  %
  % LU decomposition is performed on the complex unsymmetric matrix A.
  % ie: A := LU.
  %
  % The calculation is computed in the nX2n matrix so that the general
  % element is a[i,2j-1]+i*a[i,2j]. A record of any interchanges made
  % to the rows of A is kept in int_vec[i] (i=1...n) such that the i'th
  % row and the int_vec[i]'th row were interchanged at the i'th step.
  % The determinant (detr+i*deti)*2^dete of A is also computed but has
  % been comented out as it is not necessary. The procedure will fail
  % if A, modified by rounding errors, is singular.
  %
  begin
    scalar x,y,in_mat,tmp,int_vec,L,U,p,pp,v,w,z; %detr,deti,dete,det;
    integer i,j,k,l,n;
    if algebraic (det(mat1)) = 0 then rederr
"Error in lu_decom: matrix is singular. LU decomposition not possible.";
    j := 1;
    n := first size_of_matrix(mat1);
    in_mat := im_uncompress(mat1,n);
    int_vec := mkvect(n-1);
    for i:=1:n do
    <<
      putv(int_vec,i-1,innerprod(1,1,n+n,0,row_vec(in_mat,i,n+n),
                                          row_vec(in_mat,i,n+n)));
    >>;
%    detr := 1;
%    deti := 0;
%    dete := 0;
    for k:=1:n do
    <<
      l := k;
      p := k+k;
      pp := p-1;
      z := 0;
      for i:=k:n do
      <<
        tmp := cxinnerprod(1,1,k-1,getmat(in_mat,i,pp),
               getmat(in_mat,i,p),re_row_vec(in_mat,i,n),
               cx_row_vec(in_mat,i,n),col_vec(in_mat,pp,n),
               col_vec(in_mat,p,n));
        x := first tmp;
        y := second tmp;
        setmat(in_mat,i,pp,x);
        setmat(in_mat,i,p,y);
        x := {'quotient,{'plus,{'expt,x,2},{'expt,y,2}},
                   getv(int_vec,i-1)};
        if get_num_part(reval(x))>get_num_part(reval(z)) then
        <<
          z := x;
          l := i;
        >>;
      >>;
      if l neq k then
      <<
%        detr := {'minus,detr};
%        deti := {'minus,deti};
        for j:=n+n step -1 until 1 do
        <<
          z := getmat(in_mat,k,j);
          setmat(in_mat,k,j,getmat(in_mat,l,j));
          setmat(in_mat,l,j,z);
        >>;
        putv(int_vec,l-1,getv(int_vec,k-1));;
      >>;
      putv(int_vec,k-1,l);
      x := getmat(in_mat,k,pp);
      y := getmat(in_mat,k,p);
      z := {'plus,{'expt,x,2},{'expt,y,2}};
%      w := {'plus,{'times,x,detr},{'minus,{'times,y,deti}}};
%      deti := {'plus,{'times,x,deti},{'times,y,detr}};
%      detr :=  w;
%      if abs(get_num_part(reval(detr)))<abs(get_num_part(reval(deti)))
%       then w := deti;
%     if w=0 then rederr{"Matrix ",mat1," is singular. LU decomposition
%                           is not possible."};
%      if abs(get_num_part(reval(x))) >= 1 then
%      <<
%        w := {'times,w,0.0625};
%        detr := {'times,detr,0.0625};
%        deti := {'times,deti,0.0625};
%        dete := {'plus,dete,4};
%      >>;
%      while abs(get_num_part(reval(w))) < 0.0625 do
%      <<
%        w := {'times,w,16};
%        detr := {'times,detr,16};
%        deti := {'times,deti,16};
%        dete := {'plus,dete,-4};
%      >>;
      for j:=k+1:n do
      <<
        p := j+j;
        pp := p-1;
        tmp := cxinnerprod(1,1,k-1,getmat(in_mat,k,pp),
               getmat(in_mat,k,p),re_row_vec(in_mat,k,n),
               cx_row_vec(in_mat,k,n),col_vec(in_mat,pp,n),
               col_vec(in_mat,p,n));
        v := first tmp;
        w := second tmp;
        setmat(in_mat,k,pp,{'quotient,{'plus,{'times,v,x},
               {'times,w,y}},z});
        setmat(in_mat,k,p,{'quotient,{'plus,{'times,w,x},
               {'minus,{'times,v,y}}},z});
      >>;
    >>;
    in_mat := im_compress(in_mat,n);
    tmp := get_l_and_u(in_mat,n);
    L := first tmp;
    U := second tmp;
    % Compute determinant.
    %det := {'times,{'plus,detr,{'times,'i,deti}},{'expt,2,dete}};
    return {'list,L,U,int_vec};
  end;



symbolic procedure cxinnerprod(l,s,u,cr,ci,vec_ar,vec_ai,vec_br,vec_bi);
  %
  % Computes complex innerproduct.
  %
  begin
    scalar h,dr,di;
    h := innerprod(l,s,u,{'minus,cr},vec_ar,vec_br);
    dr := innerprod(l,s,u,{'minus,h},vec_ai,vec_bi);
    h := innerprod(l,s,u,{'minus,ci},vec_ai,vec_br);
    di := {'minus,innerprod(l,s,u,h,vec_ar,vec_bi)};
    return {dr,di};
  end;



symbolic procedure cx_row_vec(in_mat,row_no,length_of);
  %
  % Takes uncompressed matrix and creates a vector consisting of the
  % complex elements of row (row_no).
  %
  begin
    scalar cx_row_vec;
    integer i;
    cx_row_vec := mkvect(length_of);
    for i:=1:length_of do putv(cx_row_vec,i,getmat(in_mat,row_no,2*i));
    return cx_row_vec;
  end;



symbolic procedure re_row_vec(in_mat,row_no,length_of);
  %
  % Takes uncompressed matrix and creates a vector consisting of the
  % real elements of row (row_no).
  %
  begin
    scalar re_row_vec;
    integer i;
    re_row_vec := mkvect(length_of);
    for i:=1:length_of do
     putv(re_row_vec,i,getmat(in_mat,row_no,2*i-1));
    return re_row_vec;
  end;



symbolic procedure im_uncompress(in_mat,n);
  %
  % Takes square(nXn) matrix containing imaginary elements and creates
  % a new nX2n matrix s.t. in_mat(i,j) is cx_mat(i,2j-1)+i*cx_mat(i,2j).
  %
  begin
    scalar cx_mat,tmp;
    integer i,j;
    cx_mat := mkmatrix(n,2*n);
    for i:=1:n do
    <<
      for j:=1:n do
      <<
        tmp := getmat(in_mat,i,j);
        setmat(cx_mat,i,2*j-1,algebraic repart(tmp));
        tmp := getmat(in_mat,i,j);
        setmat(cx_mat,i,2*j,algebraic impart(tmp));
      >>;
    >>;
    return cx_mat;
  end;



symbolic procedure im_compress(cx_mat,n);
  %
  % Performs the opposite to im_uncompress.
  %
  begin
    scalar comp_mat;
    integer i,j;
    comp_mat := mkmatrix(n,n);
    for i:=1:n do
    <<
      for j:=1:n do
      <<
        setmat(comp_mat,i,j,{'plus,getmat(cx_mat,i,2*j-1),
               {'times,'i,getmat(cx_mat,i,2*j)}});
      >>;
    >>;
    return comp_mat;
  end;




symbolic procedure convert(in_mat,int_vec);
  %
  % The lu decomposition algorithm may swap some of the rows of A such
  % that L * U does not equal A but a row rearrangement of A. The
  % lu_decom returns as a third argument a vector that describes which
  % rows have been swapped.
  %
  % Given a matrix A, then
  %  convert(first lu_decom(A) * second lu_decom(A),third lu_decom(A))
  % will return A.
  %
  % convert(A,third lu_decom(A)) will give you L * U.
  %
  begin
    scalar new_mat;
    integer i;
    if not matrixp(in_mat) then
     rederr "Error in convert(first argument): should be a matrix.";
    new_mat := copy_mat(in_mat);
    for i:=1:upbv(int_vec)+1 do
    <<
      if getv(int_vec,i-1) neq i then
       new_mat := swap_rows(new_mat,i,getv(int_vec,i-1));
    >>;
    return new_mat;
  end;

flag('(convert),'opfn);


endmodule;  % lu_decom.

end;
