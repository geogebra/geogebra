module tadjoint;

%**********************************************************************%
%                                                                      %
% Calculation of the Triangularizing Adjoint for a given matrix A. The %
% Triangularizing Adjoint F is a lower triangular matrix.              %
%                                                                      %
% Author: Walter Tietze, July 1998                                  %
%                                                                      %
% The algorithm is due to Arne Storjohann, The Triangularizing Adjoint,%
% Institut f"ur Wissenschaftliches Rechnen, ETH Z"urich, Switzerland,  %
% http://www.inf.ethz.ch/personal/storjoha                              %
% Ref: See Arne's paper in the ISSAC 1997 proceedings                   %
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


symbolic smacro procedure mksq!*mat in_mat;
%
% Converts entries in matrix to standard quotients.
%
begin scalar tmp_mat,out_mat;
   tmp_mat:= cdr in_mat;
   out_mat:= for each u in tmp_mat collect
               for each v in u collect
                 if atom v then v
                 else mk!*sq v;
   return 'mat . out_mat;
end;


symbolic smacro procedure reval!*mat in_mat;
%
% Revals the entries in matrix.
%
begin scalar tmp_mat,out_mat;
   tmp_mat:= cdr in_mat;
   out_mat:= for each u in tmp_mat collect
               for each v in u collect
                  my_reval v;
   return 'mat . out_mat;
end;


symbolic procedure triang_adjoint in_mat;
%
% Due to the algorithm of Arne Storjohann this function
% calculates the lower triangular matrix, the so-called
% triangularizing adjoint.
%
begin scalar u;
      if eqcar(u:=aeval(in_mat),'matrix) then u:=cadr u
      else u:=reval in_mat;
      if not matrixp(u) then
        rederr "Error in triang_adjoint: non matrix input."
      else if not squarep(u) then
        rederr "Error in triang_adjoint: input matrix should be square."
      else return matsm adtriang!* u;
end;

put('triang_adjoint,'rtypefn,'getrtypecar);


symbolic procedure adtriang!* in_mat;
%
% Strategy : Calculate the triangularizing adjoint by
% transforming in_mat to a matrix with lowest 2-potential
% dimension greater or equal to the dimension of in_mat.
%
(begin scalar mat_dim,tmp_mat,l,base;
      integer dim;
      on fast_la;
      dim:=cadr matlength in_mat;
      base:=logb(dim,2);
      mat_dim:= if base-floor(base)=0 then fix base
                else (floor(base) + 1);
      mat_dim:=2**mat_dim;
      if mat_dim>dim then
         tmp_mat:=extend(in_mat,mat_dim - dim,mat_dim - dim,0)
      else tmp_mat:=in_mat;
      tmp_mat:=adjoint!*lu(tmp_mat);
      l:= for i:=1:dim collect i;
      tmp_mat:=sub_matrix(tmp_mat,l,l);
 return tmp_mat;
end) where !*fast_la = !*fast_la;


symbolic procedure adjoint!*lu(in_mat);
%
% This function calculates iteratively the triangularizing
% adjoint.
%
begin scalar a1,a_tmp, a_tmp1, f1, a4!*,f4!*, subdim0, subdim1, l;
      integer determinant, dim, crrnt_dim;
      dim := cadr matlength in_mat;
      if dim < 2 then return 'mat . list({1});
      crrnt_dim := 1;
      f1:=list('mat,{1});
      while crrnt_dim < dim do
      begin
          subdim0:= for i:=1:crrnt_dim                 collect i;
          subdim1:= for i:=(crrnt_dim+1):(2*crrnt_dim) collect i;
          a1:=sub_matrix(in_mat,subdim0,subdim0);
          a1:=  reval!*mat a1;
          determinant:=0;
          for j:=1:(crrnt_dim) do
              determinant:={'plus,my_reval determinant, my_reval{'times,
                             getmat(f1,crrnt_dim,j),getmat(a1,j,crrnt_dim)}};
          if my_reval determinant = 0 then
              << a_tmp := sub_matrix(in_mat,append(subdim0,subdim1),subdim0);
                 if rank!-eval(list(a_tmp)) < crrnt_dim then
                   << f1:=extend(f1,crrnt_dim,crrnt_dim,0);
                      crrnt_dim:= 2*crrnt_dim;
                   >>
                 else
                   << if crrnt_dim=1 then
                         <<f1:=extend(f1,1,1,0);
                           setmat(f1,2,1,{'times, -1, getmat(in_mat,2,1)});
                           crrnt_dim:=2*crrnt_dim;>>
                      else <<f1:=compose!*mat(in_mat,f1,subdim0,crrnt_dim);
                             crrnt_dim:=2*crrnt_dim;>>;
                   >>;
               >>
          else
            <<
               a4!*:= matinverse matsm a1;
               a_tmp:=sub_matrix(in_mat,subdim1,subdim0);
               a4!*:= multm(matsm a_tmp, a4!*);
               a4!* := for each u in a4!* collect
                          for each v in u collect
                             v:= negsq v;
               a_tmp1:='mat . a4!*;
               a_tmp:=sub_matrix(in_mat,subdim0,subdim1);
               a4!*:= multm(a4!*, matsm a_tmp);
               a4!*:= addm(a4!*,matsm sub_matrix(in_mat,subdim1,subdim1));
               a4!*:= 'mat . a4!*;
               f4!* := adjoint!*lu reval!*mat mksq!*mat a4!*;
               l:= for i:=1:crrnt_dim collect i;
               a_tmp := mult_rows(f4!*,l,determinant);
               a_tmp:=reval!*mat a_tmp;
               f1:=extend(f1,crrnt_dim,crrnt_dim,0);
               f1:=copy_into(a_tmp,f1,crrnt_dim+1,crrnt_dim+1);
               a_tmp:='mat . multm(matsm a_tmp,matsm mksq!*mat a_tmp1);
               a_tmp:=mksq!*mat a_tmp;
               f1:=copy_into(a_tmp,f1,crrnt_dim+1,1);
               crrnt_dim:=crrnt_dim*2;
            >>;
      end;
      return f1;
end;


symbolic procedure compose!*mat(in_mat,f1,subdim0,crrnt_dim);
%
% Due to the algorithm of Arne Storjohann this function
% determines the rows of the triangularizing adjoint which
% can be set equal to zero.
%
begin scalar tmp_mat,tmp_row,k;
      for i:=(2*crrnt_dim) step (-1) until crrnt_dim do
      begin
       k:= for j:=1:i collect j;
       if rank!-eval {sub_matrix(in_mat,k,subdim0)} < crrnt_dim then
          << f1:=extend(f1,crrnt_dim,crrnt_dim,0);
             for j:=(i+1):(2*crrnt_dim) do
             begin
                k:= append(k,{j});
                tmp_mat:=sub_matrix(in_mat,k,k);
                tmp_row:=adjoint_lst_row!*(tmp_mat,j);
                for l:=1:j do
                   setmat(f1,j,l,nth(cadr tmp_row,l));
             end;
             i:=crrnt_dim-1;
          >>
      end;
 return f1;
end;


symbolic procedure adjoint_lst_row!*(in_mat,len);
%
% Calculates one row for the triangularizing adjoint in
% last row of submatrix(len,len) of matrix in_mat.
%
begin
   scalar tmp_mat, adj_row,det;
   integer sign;
   if len=1 then return in_mat;
   for j:=1:len do
      begin
        sign := (-1)**(len+j);
        tmp_mat := minor(in_mat, j, len);
        if sign = -1 then
           det:= mk!*sq negsq(simpdet({tmp_mat}))
        else
           det:= mk!*sq simpdet({tmp_mat});
        adj_row:=append(adj_row,{det});
      end;
 return 'mat . {adj_row}
end;

endmodule;

end;
