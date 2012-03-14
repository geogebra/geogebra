module cholesky;

%**********************************************************************%
%                                                                      %
% Computation of the Cholesky decomposition of dense positive definite %
% matrices containing numeric entries.                                 %
%                                                                      %
% Author: Matt Rebbeck, May 1994.                                      %
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




symbolic procedure cholesky(mat1);
  %
  % A must be a positive definite symmetric matrix.
  %
  % LU decomposition of matrix A. ie: A=LU, where U is the transpose
  % of L. The determinant of A is also computed as a side effect but
  % has been commented out as it is not necessary. The procedure will
  % fail if A is unsymmetric. It will also fail if A, modified by
  % rounding errors, is not positive definite.
  %
  % The reciprocals of the diagonal elements are stored in p and the
  % matrix is then 'dragged' out and 'glued' back together in get_l.
  %
  %
  begin
    scalar x,p,in_mat,L,U,I_turned_rounded_on;  % d1,d2;
    integer i,j,n;
    if not !*rounded then << I_turned_rounded_on := t; on rounded; >>;
    if not matrixp(mat1) then
     rederr "Error in cholesky:  non matrix input.";
    if not symmetricp(mat1) then
     rederr "Error in cholesky: input matrix is not symmetric.";
    in_mat := copy_mat(mat1);
    n := first size_of_matrix(in_mat);
    p := mkvect(n);
%    d1 := 1;
%    d2 := 0;
    for i:=1:n do
    <<
      for j:=i:n do
      <<
        x := innerprod(1,1,i-1,{'minus,getmat(in_mat,i,j)},
                       row_vec(in_mat,i,n),row_vec(in_mat,j,n));
        x := reval{'minus,x};
        if j=i then
        <<
%          d1 := reval{'times,d1,x};
          if get_num_part(my_reval(x))<=0 then rederr
           "Error in cholesky: input matrix is not positive definite.";
%          while abs(get_num_part(d1)) >= 1 do
%          <<
%            d1 := reval{'times,d1,0.0625};
%            d2 := d2+4;
%          >>;
%          while abs(get_num_part(d1)) < 0.0625 do
%          <<
%            d1 := reval{'times,d1,16};
%            d2 := d2-4;
%          >>;
          putv(p,i,reval{'quotient,1,{'sqrt,x}});
        >>
        else
        <<
          setmat(in_mat,j,i,reval{'times,x,getv(p,i)});
        >>;
      >>;
    >>;
    L := get_l(in_mat,p,n);
    U := algebraic tp(L);
    if I_turned_rounded_on then off rounded;
    return {'list,L,U};
  end;

flag('(cholesky),'opfn);  % So it can be used from algebraic mode.



symbolic procedure get_l(in_mat,p,sq_size);
  %
  % Pulls out L from in_mat and p.
  %
  begin
    scalar L;
    integer i,j;
    L := mkmatrix(sq_size,sq_size);
    for i:=1:sq_size do
    <<
      setmat(L,i,i,{'quotient,1,getv(p,i)});
      for j:=1:i-1 do
      <<
        setmat(L,i,j,getmat(in_mat,i,j));
      >>;
    >>;
    return L;
  end;



symbolic procedure symmetricp(in_mat);
  %
  % Checks input is symmetric. ie: transpose(A) = A. (boolean).
  %
  if algebraic (tp(in_mat)) neq in_mat then nil else t;

flag('(symmetricp),'boolean);
flag('(symmetricp),'opfn);


endmodule;  % cholesky.

end;
