%**********************************************************************%
%                                                                      %
% Computation of the Singular Value Decomposition of sparse matrices   %
% containing numeric entries. Uses specific rounded number routines to %
% speed things up.                                                     %
%                                                                      %
% Author: Stephen Scowcroft.                   Date: June 1995         %
%          (based on code by Matt Rebbeck)                             %
%                                                                      %
% The algorithm was taken from "Linear Algebra" - J.H.Wilkinson        %
%                                                  & C. Reinsch        %
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


module spsvd;

symbolic procedure spsvd(A);
  %
  % Computation of the singular values and complete orthogonal
  % decomposition of a real rectangular matrix A.
  %
  %      A = tp(U) diag(q) V,   U tp(U) = V tp(V) = I,
  %
  % and q contains the singular values along the diagonal.
  % (tp => transpose).
  %
  %
  begin
    scalar ee,U,V,g,x,eps,tolerance,q,s,f,h,y,test_f_splitting,
           cancellation,test_f_convergence,convergence,c,z,denom,q_mat,
           I_rounded_turned_on,trans_done,val,val2,cols,cols2,tmpu
           ,tmpv;
    integer i,j,k,l,l1,m,n;
    trans_done := I_rounded_turned_on := nil;
    if not !*rounded then << on rounded; I_rounded_turned_on := t; >>;

    if not matrixp(A) then
     rederr "Error in spsvd: non matrix input.";

    % The value of eps can be decreased to increase accuracy.
    % As usual, doing this will slow things down (and vice versa).
    % It should not be made smaller than the value of rd!-tolerance!*.
    eps := get_num_part(my_reval({'times,1.5,{'expt,10,-8}}));
    tolerance := get_num_part(my_reval({'expt,10,-31}));

    % Algorithm requires m >= n. If this is not the case then transpose
    % the input and swap U and V in the output (as A = tp(U) diag(q) V
    % but tp(A) = tp(V) diag(q) U  ).
    if sprow_dim(A) < spcol_dim(A) then
    << A := algebraic tp(A); trans_done := t; >>;

    m := sprow_dim(A);
    n := spcol_dim(A);

    U := copy_vect(A,nil);
    V := mkempspmat(n,list('spm,n,n));
    ee := mkvect(n);
    q := mkvect(n);

    % Householder's reduction to bidiagonal form:
    g := x := 0;
    for i:=1:n do
    <<tmpu:=copy_vect(smtp (u,nil),nil);
      putv(ee,i,g);
      s := 0;
      l := i+1;
      cols:=findrow(tmpu,i);
      if cols then
      <<for each xx in cdr cols do
        <<j:=car xx;
          val:=cdr xx;
          if j>=i and j<=m then
          s := specrd!:plus(s,specrd!:expt(val,2));
        >>;
      >>;
      if get_num_part(s) < tolerance then g := 0
      else
      <<
        f := findelem2(U,i,i);
        if get_num_part(f)<0 then g := specrd!:sqrt(s)
           else g := my_minus(specrd!:sqrt(s));
          h := specrd!:plus(specrd!:times(f,g),my_minus(s));
          letmtr3(list(U,i,i),specrd!:plus(f,my_minus(g)),u,nil);
         tmpu:=copy_vect(smtp (u,nil),nil);
         cols:=findrow(tmpu,i);
         for j:=l:n do
         <<cols2:=findrow(tmpu,j);
           s := 0;
           for each xx in cdr cols do
           <<val:=cdr xx;
               k:=car xx;
              if k>=i and k<=m then
              <<val2:=atsoc(k,cols2);
                if val2 then
                  s := specrd!:plus(s,specrd!:times(val,cdr val2));
              >>;
           >>;
           f := specrd!:quotient(s,h);
           for each xx in cdr cols do
           <<val:=cdr xx;
               k:=car xx;
              if k>=i and k<=m then
              <<val2:=atsoc(k,cols2);
                if val2=nil then val2:=(0 . 0);
                if not (f='(!:rd!: . 0.0)) then
                 letmtr3(list(U,k,j),specrd!:plus(cdr val2,
                      specrd!:times(f,val)),u,nil);
               >>;
            >>;
           >>;
          >>;
      putv(q,i,g);
      s := 0;
      cols:=findrow(u,i);
      for each xx in cdr cols do
      <<j:=car xx;
        val:=cdr xx;
        if j>=l and j<=n then
          s := specrd!:plus(s,specrd!:expt(val,2));
      >>;
      if get_num_part(s) < tolerance then g := 0
      else
      <<f := findelem2(U,i,i+1);
        if get_num_part(f)<0 then g := specrd!:sqrt(s)
         else g := my_minus(specrd!:sqrt(s));
        h := specrd!:plus(specrd!:times(f,g),my_minus(s));
        letmtr3(list(U,i,i+1),specrd!:plus(f,my_minus(g)),u,nil);
        cols:=findrow(u,i);
        for each xx in cdr cols do
        <<j:=car xx;
          val:=cdr xx;
          if j>=l and j<=n then
            putv(ee,j,specrd!:quotient(val,h));
         >>;
          for j:=l:m do
          <<cols2:=findrow(u,j);
             s := 0;
            for each xx in cdr cols do
            <<val:=cdr xx;
                k:=car xx;
                if k>=l and k<=n then
                <<val2:=atsoc(k,cols2);
                  if val2 then
                   s := specrd!:plus(s,specrd!:times(val,cdr val2));
                >>;
            >>;
            for each xx in cdr cols2 do
            <<k:=car xx;
              val2:=cdr xx;
              if k>=l and k<=n then
              <<val:=getv(ee,k);
                if val=nil then val:=0;
               letmtr3(list(U,j,k),specrd!:plus(val2,
                        specrd!:times(s,val)),u,nil);
               >>;
            >>;
           >>;
         >>;
      y := specrd!:plus(abs(get_num_part(getv(q,i))),
                        abs(get_num_part(getv(ee,i))));
      if get_num_part(y) > get_num_part(x) then x := y;
     >>;

    % Accumulation of right hand transformations:
    for i:=n step -1 until 1 do
    << cols:=findrow(u,i);
      if get_num_part(g) neq 0 then
      <<val:=findelem2(u,i,i+1);
         h := specrd!:times(val,g);
         for each xx in cdr cols do
         <<j:=car xx;
           val:=cdr xx;
           if j>=l and j<=n then
            letmtr3(list(V,j,i),specrd!:quotient(val,h),v,nil);
         >>;
        cols:=findrow(u,i);
        tmpv:=copy_vect(smtp(v,nil),nil);
        for j:=l:n do
        <<cols2:=findrow(tmpv,j);
          s:=0;
          for each xx in cdr cols do
          <<k:=car xx;
            val:=cdr xx;
            if k>=l and k<=n then
            <<val2:=atsoc(k,cols2);
              if val2 then
               s := specrd!:plus(s,specrd!:times(val,cdr val2));
            >>;
          >>;
           cols:=findrow(tmpv,i);
           for each xx in cdr cols do
           <<k:=car xx;
             val:=cdr xx;
             if k>=l and k<=n then
             <<val2:=atsoc(k,cols2);
               if val2=nil then val2:=(0 . 0);
                letmtr3(list(V,k,j),specrd!:plus(cdr val2,
                        specrd!:times(s,val)),v,nil);
             >>;
           >>;
         >>;
        >>;
       for j:=l:n do
        << letmtr3(list(V,i,j),0,v,nil);
           letmtr3(list(V,j,i),0,v,nil);
        >>;
      letmtr3(list(V,i,i),1,v,nil);
      g := getv(ee,i);
      l := i;
    >>;
    % Accumulation of left hand transformations:
    for i:=n step -1 until 1 do
    <<tmpu:=copy_vect(smtp (u,nil),nil);
      tmpv:=copy_vect(smtp (v,nil),nil);
      l := i+1;
      g := getv(q,i);
      cols:=findrow(u,i);
      for each xx in cdr cols do
      <<j:=car xx;
         if j>=l and j<=n then letmtr3(list(U,i,j),0,u,nil);
      >>;
      if get_num_part(g) neq 0 then
      <<h := specrd!:times(findelem2(U,i,i),g);
        tmpu:=copy_vect(smtp (u,nil),nil);
        cols:=findrow(tmpu,i);
        for j:=l:n do
        <<cols2:=findrow(tmpu,j);
          s := 0;
          for each xx in cdr cols do
           <<val:=cdr xx;
             k:=car xx;
             if k>=l and k<=m then
             <<val2:=atsoc(k,cols2);
               if val2 then
                 s := specrd!:plus(s,specrd!:times(val,cdr val2));
             >>;
           >>;
           f := specrd!:quotient(s,h);
            for each xx in cdr cols do
           <<val:=cdr xx;
             k:=car xx;
             if k>=i and k<=m then
             <<val2:=atsoc(k,cols2);
               if val2=nil then val2:=(0 . 0);
               if not (f='(minus (!:rd!: . 0.0))) then
                letmtr3(list(U,k,j),specrd!:plus(cdr val2,
                 specrd!:times(f,val)),u,nil);
              >>;
            >>;
           >>;
           tmpu:=copy_vect(smtp (u,nil),nil);
            cols:=findrow(tmpu,i);
           for each xx in cdr cols do
           <<j:=car xx;
             val:=cdr xx;
             if j>=i and j<=m then
               letmtr3(list(U,j,i),specrd!:quotient(val,g),u,nil);
           >>;
         >>
       else for each xx in cdr cols do
         << j:=car xx;
            if j>=i and j<=m then letmtr3(list(U,j,i),0,u,nil);
         >>;
 letmtr3(list(U,i,i),specrd!:plus(findelem2(U,i,i),1),u,nil);
>>;

    % Diagonalisation of the bidiagonal form:
    eps := get_num_part(specrd!:times(eps,x));
    test_f_splitting := t;
    k := n;
    while k>=1 do
    <<
      convergence := nil;
      if test_f_splitting then
      <<
        l := k;
        test_f_convergence := cancellation := nil;
        while l>=1 and not (test_f_convergence or cancellation) do
        <<
          if abs(get_num_part(getv(ee,l))) <= eps
           then test_f_convergence := t
          else if abs(get_num_part(getv(q,l-1))) <= eps
           then cancellation := t
          else l := l-1;
        >>;
      >>;
     tmpu:=copy_vect(smtp (u,nil),nil);
      % Cancellation of e[l] if l>1:
      if not test_f_convergence then
      <<
        c := 0; s := 1; l1 := l-1;
        i := l;
        while i<=k and not test_f_convergence do
        <<cols:=findrow(tmpu,i);
          f := specrd!:times(s,getv(ee,i));
          putv(ee,i,specrd!:times(c,getv(ee,i)));
          if abs(get_num_part(f)) <= eps then
           test_f_convergence := t
          else
          <<
            g := getv(q,i);
            h := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),
                                           specrd!:times(g,g)));
            putv(q,i,h);
            c := specrd!:quotient(g,h);
            s := specrd!:quotient(my_minus(f),h);
            for each xx in cdr cols do
            <<j:=car xx;
              val:=cdr xx;
              if j<=m then
              << y := findelem2(U,j,l1);
                 letmtr3(list(U,j,l1),specrd!:plus(specrd!:times(y,c),
                                         specrd!:times(val,s)),u,nil);
              >>;
            >>;
            i := i+1;
          >>;
        >>;
      >>;
      z := getv(q,k);
      if l = k then convergence := t;

      if not convergence then
      <<
        % Shift from bottom 2x2 minor:
        x := getv(q,l);
        y := getv(q,k-1);
        g := getv(ee,k-1);
        h := getv(ee,k);
        f := specrd!:quotient(specrd!:plus(specrd!:times(
              specrd!:plus(y,my_minus(z)),specrd!:plus(y,z)),
               specrd!:times(specrd!:plus(g,my_minus(h)),
                specrd!:plus(g,h))),specrd!:times(
                 specrd!:times(2,h),y));
        g := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),1));
        % Needed to change < here to <=.
        if get_num_part(f)<=0 then
        denom := specrd!:plus(f,my_minus(g))
         else denom := specrd!:plus(f,g);
        f := specrd!:quotient(specrd!:plus(specrd!:times(
              specrd!:plus(x,my_minus(z)),specrd!:plus(x,z)),
               specrd!:times(h,specrd!:quotient(y,
                specrd!:plus(denom,my_minus(h))))),x);

        % Next QR transformation:
        c := s := 1;
        for i:=l+1:k do
        <<g := getv(ee,i);
          y := getv(q,i);
          h := specrd!:times(s,g);
          g := specrd!:times(c,g);
          z := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),
                                         specrd!:times(h,h)));
          putv(ee,i-1,z);
          c := specrd!:quotient(f,z);
          s := specrd!:quotient(h,z);
          f := specrd!:plus(specrd!:times(x,c),specrd!:times(g,s));
          g := specrd!:plus(specrd!:times(my_minus(x),s),
                            specrd!:times(g,c));
          h := specrd!:times(y,s);
          y := specrd!:times(y,c);
            for j:=1:m do
            << z := findelem2(V,j,i);
               x :=findelem2(v,j,i-1);
               letmtr3(list(V,j,i-1),specrd!:plus(specrd!:times(x,c),
                                      specrd!:times(z,s)),v,nil);
               letmtr3(list(V,j,i),specrd!:difference(specrd!:times
                                    (z,c), specrd!:times(x,s)),v,nil);
            >>;
          z := specrd!:sqrt(specrd!:plus(specrd!:times(f,f),
                                         specrd!:times(h,h)));
          putv(q,i-1,z);
          c := specrd!:quotient(f,z);
          s := specrd!:quotient(h,z);
          f := specrd!:plus(specrd!:times(c,g),specrd!:times(s,y));
          x := specrd!:plus(specrd!:times(my_minus(s),g),
                            specrd!:times(c,y));
          for j:=1:m do
          <<  y := findelem2(U,j,i-1);
              z := findelem2(u,j,i);
              letmtr3(list(U,j,i-1),specrd!:plus(specrd!:times(y,c),
                                        specrd!:times(z,s)),u,nil);
              letmtr3(list(U,j,i),specrd!:difference(specrd!:times(z,c),
                                            specrd!:times(y,s)),u,nil);
           >>;
        >>;
        putv(ee,l,0);
        putv(ee,k,f);
        putv(q,k,x);
      >>
      else % convergence:
      <<tmpv:=copy_vect(smtp (v,nil),nil);
        if get_num_part(z)<0 then
        <<
          % q[k] is made non-negative:
          putv(q,k,my_minus(z));
          cols:=findrow(tmpv,k);
          for each xx in cdr cols do
          <<j:=car xx;
            val:=cdr xx;
            if j<=n then
              letmtr3(list(V,j,k),my_minus(val),v,nil);
          >>;
         >>;
        k := k-1;
      >>;
    >>;

    q_mat := spq_to_diag_matrix(q);
    if I_rounded_turned_on then off rounded;
    v:=spden_to_sp(v); % to print it out in Sparse manner
    u:=spden_to_sp(u);
    if trans_done then
     return {'list,algebraic tp V,q_mat,algebraic tp U}
      else return {'list,algebraic tp U,q_mat,algebraic tp V};
  end;

flag('(spsvd),'opfn); % To make it available from algebraic (user) mode.

symbolic procedure spq_to_diag_matrix(q);
  %
  % Converts q (a vector) to a diagonal matrix with the elements of
  % q on the diagonal.
  %
  begin
    scalar q_mat;
    integer i,sq_dim_q,val;
    sq_dim_q := upbv(q);
    q_mat := mkempspmat(sq_dim_q,list('spm,sq_dim_q,sq_dim_q));
    for i:=1:sq_dim_q do
     << val:=getv(q,i);
        if val='(!:rd!: . 0.0) then nil
         else letmtr3(list(q_mat,i,i),val,q_mat,nil);
     >>;
    return q_mat;
  end;

% The lists are then re-written into desired sparse list format ready
% for printing.

symbolic procedure spden_to_sp(list);
 begin scalar tl,nmat,val,cols,j;
  tl:=caddr list;
  nmat:=mkempspmat(cadr tl,tl);
  for i:=1:cadr tl do
  << cols:=findrow(list,i);
     for each xx in cdr cols do
     <<j:=car xx;
       val:=reval cdr xx;
       if val='(!:rd!: . 0.0) then nil
        else letmtr3(list(nmat,i,j),val,nmat,nil);
     >>;
  >>;
  return nmat;
 end;


symbolic procedure sprd_copy_mat(A);
  %
  % Creates a copy of the input matrix and returns it as well as
  % reval-ing each elt to get them in !:rd!: form;
  %
  begin
    scalar C;
    integer row_dim,column_dim;
    row_dim := sprow_dim(A);
    column_dim := spcol_dim(A);
    C := list('sparsemat,list('u,row_dim,column_dim));
    for i:=1:row_dim do
    <<
      for j:=1:column_dim do
      <<
        letmtr3(list(C,i,j),my_reval(findelem2(A,i,j)),c,nil);
      >>;
    >>;
    return C;
  end;


symbolic procedure sppseudo_inverse(in_mat);
  %
  % Also known as the Moore-Penrose Inverse.
  %
  % Given the singular value decomposition A := tp(U) diag(q) V
  % the pseudo inverse A^(-1) is defined as
  %
  %   A^(-1) = tp(V) (diag(q))^(-1) U.
  %
  % NB: this can be quite handy as we can take the inverse of non
  % square matrices (A * pseudo_inverse(A) = identity).
  %
  begin
    scalar psu_inv,svd_list,a,b,c;
    svd_list := cdr spsvd(in_mat);
         a:=car svd_list;
         c:=caddr svd_list;
         b:=cadr svd_list;
         c:=algebraic tp c;
         b:=algebraic (1/b);
    psu_inv := algebraic (c * b * a);
    return psu_inv;
  end;

flag('(sppseudo_inverse),'opfn);

endmodule;

end;

%***********************************************************************
%=======================================================================
%
% End of Code.
%
%=======================================================================
%***********************************************************************

