%**********************************************************************%
%======================================================================%
%                                                                      %
% Computation of the LU decomposition of sparse unsymmetric matrices   %
% containing either numeric entries or complex numbers with numeric    %
% coefficients.                                                        %
%                                                                      %
% Author: Stephen Scowcroft                       Date: June 1995.     %
%          (based on code by Matt Rebbeck.)                            %
%                                                                      %
% The algorithm was taken from "Linear Algebra" - J.H.Wilkinson        %
%                                                  & C. Reinsch        %
%                                                                      %
%                                                                      %
% NB: By using the same rounded number techniques as used in spsvd this%
%     could be made a lot faster.                                      %
%                                                                      %
%======================================================================%
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


module spludcmp;

symbolic procedure splu_decom(in_mat);
  %
  % Runs the show!
  %
  begin
    scalar ans,I_turned_rounded_on;
    integer sq_size;
    if not matrixp(in_mat) then
     rederr "Error in splu_decom: non matrix input.";
    if not squarep(in_mat) then
     rederr "Error in splu_decom: input matrix should be square.";
    if not !*rounded then << I_turned_rounded_on := t; on rounded; >>;
    sq_size := sprow_dim(in_mat);
    if spcx_test(in_mat,sq_size) then ans := spcompdet(in_mat)
     else ans := spunsymdet(in_mat);
    if I_turned_rounded_on then off rounded;
    return ans;
  end;

flag('(splu_decom),'opfn);  % So it can be used from algebraic mode.

symbolic procedure spcx_test(in_mat,sq_size);
  %
  % Tests to see if any elts are complex. (boolean).
  %
  begin
    scalar bool,elt,col,val;
    integer i;
    i := 1;
    while not bool and i<=sq_size do
    << col:=findrow(in_mat,i);
      if not (col=nil) then
      << for each xx in cdr col do
          << elt := cdr xx;
             val:=algebraic impart(elt);
          if val neq 0 then <<bool := t; xx:=nil>>;
           >>;
      >>;
      i := i+1;
    >>;
    return bool;
  end;

flag('(spcx_test),'boolean);

symbolic procedure spunsymdet(mat1);
  %
  % LU decomposition is performed on the unsymmetric matrix A.
  % ie: A := LU.
  % A record of any interchanges made to the rows of A is kept in
  % int_vec[i] (i=1...n) such that the i'th row and the int_vec[i]'th
  % row were interchanged at the i'th step.The procedure will fail if A,
  % modified by rounding errors, is singular or singular within the
  % bounds of the machine accuracy (ie: acc s.t. 1+acc > 1).
  %
  begin
    scalar x,y,in_mat,tmp,int_vec,L,U,col,tp_mat1,tp_mat2,val,col2;
    integer i,j,k,l,n;
    j := 1;
    in_mat := copy_vect(mat1,nil);
    n := sprow_dim(in_mat);
    int_vec := mkvect(n-1);
    for i:=1:n do
    << col:=findrow(in_mat,i);
      if col=nil then col:=list(nil);
      y := spinnerprod(1,1,n,0,col,col);
      putv(int_vec,i-1,{'quotient,1,{'sqrt,y}});
    >>;
    for k:=1:n do
    << tp_mat1:=copy_vect(smtp (in_mat,nil),nil);
      l := k;
      x := 0;
      col:=findrow(tp_mat1,k);
      if not (col=nil) then
      <<for each xx in cdr col do
      <<i:=car xx;
        val:=cdr xx;
        if i>=k then
        << y := spinnerprod(1,1,k-1,{'minus,val},findrow(in_mat,i),col);
        letmtr3(list(in_mat,i,k),reval {'minus,y},in_mat,nil);
        y := abs(get_num_part(reval{'times,y,getv(int_vec,i-1)}));
        if y>get_num_part(my_reval(x)) then
        <<
          x := y;
          l := i;
        >>;
        >>;
       >>;
       >>;
      if l neq k then
      << col:=findrow(in_mat,k);
         letmtr3(list(in_mat,k),findrow(in_mat,l),in_mat,nil);
         letmtr3(list(in_mat,l),col,in_mat,nil);
         putv(int_vec,l-1,getv(int_vec,k-1));
      >>;
      putv(int_vec,k-1,l);
      if get_num_part(my_reval(x)) <
          get_num_part(reval{'times,8,rd!-tolerance!*}) then rederr
"Error in splu_decom: matrix is singular. LU decomposition not possible.";
      x := {'quotient,{'minus,1},findelem2(in_mat,k,k)};
      tp_mat1:=copy_vect(smtp (in_mat,nil),nil);
      col:=findrow(in_mat,k);
      for each xx in cdr col do
      << j:=car xx;
         val := cdr xx;
         if j>=k+1 then
         <<y := spinnerprod(1,1,k-1,{'minus,val},col,findrow(tp_mat1,j));
        letmtr3(list(in_mat,k,j),reval {'times,x,y},in_mat,nil)>>;
      >>;
    >>;
    tmp := spget_l_and_u(in_mat,n);
    L := car tmp;
    U := cadr tmp;
    return {'list,L,U,int_vec};
  end;

symbolic procedure spinnerprod(l,s,u,c1,rowa,rowb);
  %
  % This procedure accumulates the sum of products vec_a*vec_b and adds
  % it to the initial value c1.  (ie: the scalar product).
  %
  begin
    scalar s1,d1,val1,val2,j;
    s1 := c1;
    d1 := s1;
    for each xx in cdr rowa do
    << j:=car xx;
       if j=nil then j:=0;
       val1:=cdr xx;
       if val1=nil or val1=list(nil) then val1:=0;
       if j<=u then
       << val2:=atsoc(j,rowb);
          if val2=nil or (val2=list(nil)) then nil
           else
          << s1 := {'plus,s1,{'times,val1,cdr val2}};
             d1:=s1;
          >>;
        >>;
     >>;
    return d1;
  end;



symbolic procedure spget_l_and_u(in_mat,sq_size);
  %
  % Takes the combined LU matrix and returns L and U.
  % sq_size is the no of rows (and columns) of in_mat.
  %
  begin
    scalar L,U,col;
    integer i,j,val;
    L := mkempspmat(sq_size,list('spm,sq_size,sq_size));
    U := mkempspmat(sq_size,list('spm,sq_size,sq_size));
    for i:=1:sq_size do
    << letmtr3(list(U,i,i),1,U,nil);
       col:=findrow(in_mat,i);
       for each xx in cdr col do
       << j:=car xx;
         val:=cdr xx;
         if j<=i then
         << letmtr3(list(L,i,j),val,L,nil)>>
         else if j>=i+1 then
         << letmtr3(list(U,i,j),val,U,nil)>>;
       >>;
    >>;
    return {L,U};
  end;


symbolic procedure spcompdet(mat1);
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
    scalar x,y,in_mat,tmp,int_vec,L,U,p,pp,v,w,z,col,tp_mat1,rcol,recol,
           re,icol,imcol,im,rval,ival,rl,il,cl;
    integer i,j,k,l,n;
    if algebraic (det(mat1)) = 0 then rederr
"Error in splu_decom: matrix is singular. LU decomposition not possible.";
    j := 1;
    n := sprow_dim(mat1);
    in_mat := spim_uncompress(mat1,n);
    int_vec := mkvect(n-1);
    for i:=1:n do
    <<col:=findrow(in_mat,i);
       if not (col=nil) then
         putv(int_vec,i-1,spinnerprod(1,1,n+n,0,col,col));
    >>;
    for k:=1:n do
    <<tp_mat1:=copy_vect(smtp (in_mat,'cx),nil);
      l := k;
      p := k+k;
      pp := p-1;
      z := 0;
      recol:=findrow(tp_mat1,pp);
      imcol:=findrow(tp_mat1,p);
      if not (recol=nil) or not(imcol=nil) then
      << recol:=cdr recol;
         imcol:=cdr imcol;
          rl:=recol;
          il:=imcol;
       while recol and imcol  do
       <<rcol:=car recol;
           re:=car rcol;
         rval:=cdr rcol;
         if rval=list nil then rval:=0;
         icol:=car imcol;
         im:=car icol;
         ival:=cdr icol;
         if ival=list nil then ival:=0;
         i:=re;
         col:=findrow(in_mat,i);
         if i>=k then
         << tmp := spcxinnerprod(1,1,k-1,rval,ival,spre_row_vec(cdr col),
                    spcx_row_vec(cddr col),findrow(tp_mat1,pp),
                    findrow(tp_mat1,p));
        x := car tmp;
        y := cadr tmp;
        letmtr3(list(in_mat,i,pp), reval x,in_mat,'cx);
        letmtr3(list(in_mat,i,p),reval y,in_mat,'cx);
        x := {'quotient,{'plus,{'expt,x,2},{'expt,y,2}},
                   getv(int_vec,i-1)};
        if get_num_part(reval(x))>get_num_part(reval(z)) then
        <<
          z := x;
          l := i;
        >>;
       >>;
        recol:=cdr recol;
        imcol:=cdr imcol;
      >>;
      >>;
      if l neq k then
      << col:=findrow(in_mat,k);
         letmtr3(list(in_mat,k),findrow(in_mat,l),in_mat,'cx);
         letmtr3(list(in_mat,l),col,in_mat,'cx);
         putv(int_vec,l-1,getv(int_vec,k-1));;
      >>;
      putv(int_vec,k-1,l);
      col:=findrow(in_mat,k);
      if col then col:=cdr col;
      tp_mat1:=copy_vect(smtp (in_mat,'cx),nil);
      x := atsoc(pp,col);
      if x then x:=cdr x;
       if x=list nil then x:=0;
      y := atsoc(p,col);
      if y then y:=cdr y;
       if y=list nil then y:=0;
      z := {'plus,{'expt,x,2},{'expt,y,2}};
      cl:=col;
      while col do
      << rcol:= car col;
           re:= car rcol;
         rval:= cdr rcol;
         if rval=list nil then rval:=0;
         icol:=cadr col;
           im:=car icol;
         ival:=cdr icol;
         if ival=list nil then ival:=0;
            j:=im / 2;
        if j>=k+1 then
       << p := j+j;
          pp := p-1;
         tmp := spcxinnerprod(1,1,k-1,rval,ival,
                  spre_row_vec(cl),spcx_row_vec(cdr cl),
                   findrow(tp_mat1,pp),findrow(tp_mat1,p));
        v := car tmp;
        w := cadr tmp;
        letmtr3(list(in_mat,k,pp), reval {'quotient,{'plus,{'times,v,x},
               {'times,w,y}},z},in_mat,'cx);
        letmtr3(list(in_mat,k,p), reval {'quotient,{'plus,{'times,w,x},
               {'minus,{'times,v,y}}},z},in_mat,'cx);
      >>;
      col:=cddr col;
    >>;
   >>;
    in_mat := spim_compress(in_mat,n);
    tmp := spget_l_and_u(in_mat,n);
    L := car tmp;
    U := cadr tmp;
    return {'list,L,U,int_vec};
  end;

symbolic procedure spcxinnerprod(l,s,u,cr,ci,vec_ar,vec_ai,vec_br,vec_bi);
  %
  % Computes complex innerproduct.
  %
  begin
    scalar h,dr,di;
    h := spinnerprod(l,s,u,{'minus,cr},vec_ar,vec_br);
    dr := spinnerprod(l,s,u,{'minus,h},vec_ai,vec_bi);
    h := spinnerprod(l,s,u,{'minus,ci},vec_ai,vec_br);
    di := {'minus,spinnerprod(l,s,u,h,vec_ar,vec_bi)};
    return {dr,di};
  end;



symbolic procedure spcx_row_vec(list);
  %
  % Takes uncompressed matrix and creates a list consisting of the
  % complex elements of a row.
  %
  begin
    scalar imcol,nlist,val;
    integer coln;
    while list do
    << imcol:=car list;
         val:=cdr imcol;
        coln:=car imcol;
        coln:=coln / 2;
       imcol:= coln . val;
       nlist := imcol . nlist;
        if cdr list then list := cddr list
         else list:=cdr list;
     >>;
    return list(nil) . reverse nlist;
  end;



symbolic procedure spre_row_vec(list);
  %
  % Takes uncompressed matrix and creates a list consisting of the
  % real elements a row.
  %
  begin
    scalar recol,nlist,val;
    integer coln;
    while list do
    << recol:=car list;
        coln:=car recol;
        coln:= (coln + 1) / 2;
         val:=cdr recol;
       recol:=coln . val;
       nlist:=recol . nlist;
        list:=cddr list;
    >>;
    return list(nil) . reverse nlist;
  end;

symbolic procedure spim_uncompress(in_mat,n);
  %
  % Takes square(nXn) matrix containing imaginary elements and creates
  % a new nX2n matrix s.t. in_mat(i,j) is cx_mat(i,2j-1)+i*cx_mat(i,2j).
  %
  begin
    scalar cx_mat,tmp,col,val1,val2;
    integer i,j;
    cx_mat := mkempspmat(n,list('spm,n,2*n));
    for i:=1:n do
    << col:=findrow(in_mat,i);
       for each xx in cdr col do
       << j:=car xx;
          tmp:=cdr xx;
          val1:=algebraic repart(tmp);
          val2:=algebraic impart(tmp);
        letmtr3(list(cx_mat,i,2*j-1),val1,cx_mat,'cx);
        letmtr3(list(cx_mat,i,2*j),val2,cx_mat,'cx);
      >>;
    >>;
    return cx_mat;
  end;



symbolic procedure spim_compress(cx_mat,n);
  %
  % Performs the opposite to im_uncompress.
  %
  begin
    scalar comp_mat,col,val1,val2,col1,col2;
    integer i,j;
    comp_mat := mkempspmat(n,list('spm,n,n));
    for i:=1:n do
    << col:=findrow(cx_mat,i);
       if col then col:=cdr col;
       while col do
       <<col1:=car col;
         col2:=cadr col;
         val1:=cdr col1;
         val2:=cdr col2;
           j:=car col2 / 2;
        letmtr3(list(comp_mat,i,j),reval {'plus, val1,
                      {'times,'i, val2}},comp_mat,nil);
         col:=cddr col;
       >>;
    >>;
    return comp_mat;
  end;

symbolic procedure spconvert(in_mat,int_vec);
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
    new_mat := copy_vect(in_mat,nil);
    for i:=1:upbv(int_vec)+1 do
    <<
      if getv(int_vec,i-1) neq i then
       new_mat := spswap_rows(new_mat,i,getv(int_vec,i-1));
    >>;
    return new_mat;
  end;

flag('(spconvert),'opfn);

endmodule;

end;

%***********************************************************************
%=======================================================================
%
% End of Code.
%
%=======================================================================
%***********************************************************************

