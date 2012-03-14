%**********************************************************************%
%                                                                      %
% Computation of the Cholesky decomposition of sparse positive definite%
% matrices containing numeric entries.                                 %
%                                                                      %
% Author: Stephen Scowcroft                       Date:  June 1995     %
%           (based on code by Matt Rebbeck)                            %
%                                                                      %
% The algorithm was taken from "Linear Algebra" - J.H.Wilkinson        %
%                                                  & C. Reinsch        %
%                                                                      %
%                                                                      %
% NB: By using the same rounded number techniques as used in spsvd this%
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


module spchlsky;

symbolic procedure spcholesky(mat1);
  %
  % A must be a positive definite symmetric matrix.
  %
  % LU decomposition of matrix A. ie: A=LU, where U is the transpose
  % of L. The procedure will fail if A is unsymmetric.
  % It will also fail if A, modified by rounding errors, is not positive
  % definite.
  %
  % The reciprocals of the diagonal elements are stored in p and the
  % matrix is then 'dragged' out and 'glued' back together in get_l.
  %
  %
  begin
    scalar col,x,p,in_mat,L,U,I_turned_rounded_on,val;
    integer i,j,n;
    if not !*rounded then << I_turned_rounded_on := t; on rounded; >>;
    if not matrixp(mat1) then
     rederr "Error in spcholesky:  non matrix input.";
    if not symmetricp(mat1) then
     rederr "Error in spcholesky: input matrix is not symmetric.";
    in_mat := copy_vect(mat1,nil);
    n := sprow_dim(in_mat);
    p := mkvect(n);
    for i:=1:n do
    << col:=findrow(in_mat,i);
       if col=nil then col:=list(list(nil),list(nil));
       for each xx in cdr col do
       << if xx='(nil) then <<j:=i; val:=findelem2(in_mat,i,i)>>
           else << j:=car xx; val:=cdr xx;>>;
        if j>=i then
        << x := spinnerprod(1,1,i-1,{'minus,val},col,findrow(in_mat,j));
           x := reval{'minus,x};
           if j=i then
           <<
             if get_num_part(my_reval(x))<=0 then rederr
              "Error in spcholesky: input matrix is not positive definite.";
             putv(p,i,reval{'quotient,1,{'sqrt,x}});
           >>
           else
           <<
             letmtr3(list(in_mat,j,i),reval {'times,x,getv(p,i)},in_mat,nil);
           >>;
         >>;
       >>;
      >>;
    L := spget_l(in_mat,p,n);
    U := algebraic tp(L);
    if I_turned_rounded_on then off rounded;
    return {'list,L,U};
  end;

flag('(spcholesky),'opfn);  % So it can be used from algebraic mode.



symbolic procedure spget_l(in_mat,p,sq_size);
  %
  % Pulls out L from in_mat and p.
  %
  begin
    scalar L,col;
    integer i,j,val;
    L := mkempspmat(sq_size,list('spm,sq_size,sq_size));
    for i:=1:sq_size do
    <<
      letmtr3(list(L,i,i), reval {'quotient,1,getv(p,i)},L,nil);
      col:=findrow(in_mat,i);
      for each xx in cdr col do
       << j:=car xx;
          val:=cdr xx;
          if j<i then <<if val = 0 then nil
                          else letmtr3(list(L,i,j),val,L,nil);>>;
       >>;
    >>;
    return L;
  end;

endmodule;

end;

%***********************************************************************
%=======================================================================
%
% End of Code.
%
%=======================================================================
%***********************************************************************

