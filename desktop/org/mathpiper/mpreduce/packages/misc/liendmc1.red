module liendmc1; % N-dimensional Lie algebras with 1-dimensional derived
                 % algebra.
% Author: Carsten Schoebel.
% e-mail: cschoeb@aix550.informatik.uni-leipzig.de .
% Copyright (c) 1993 The Leipzig University, Computer Science Dept.
% All Rights Reserved.

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


algebraic;
operator heisenberg,commutative,lie_algebra;

algebraic procedure liendimcom1(n);
begin
 if (not(symbolic fixp(n)) or n<2) then
 symbolic rederr "dimension out of range";
 symbolic (if gettype 'lienstrucin neq 'ARRAY then
          rederr "lienstrucin not ARRAY");
 if length lienstrucin neq {n+1,n+1,n+1} then
          symbolic rederr "dimension of lienstrucin out of range";
 matrix lientrans(n,n);
   array lie_cc(n,n,n);
 lieninstruc(n);
 lienjactest(n);if lie_jtest neq 0 then
  <<clear lie_cc,lie_jtest;symbolic rederr "not a Lie algebra">>;
 <<liendimcom(n);
 if lie_dim=0 then
       <<if symbolic !*tr_lie then
        write "The given Lie algebra is commutative";
       lientrans:=lientrans**0;lie_list:={commutative(n)}>> else
  if lie_dim=1 then <<if lie_help=0 then
                           liencentincom(n,lie_tt,lie_p,lie_q)
                     else liencentoutcom(n,lie_tt,lie_s);
     if symbolic !*tr_lie then
      lienoutform(lientrans,n,lie_help,2*lie_kk!*+1);
   if lie_help=1 then lie_list:={lie_algebra(2),commutative(n-2)} else
       lie_list:={heisenberg(2*lie_kk!*+1),commutative(n-2*lie_kk!*-1)}
            >>else
    <<clear lie_dim,lie_help,lie_p,lie_q,lie_tt,lie_s,lie_kk!*,
    lie_jtest,lie_cc;
    symbolic rederr "dimension of derived algebra out of range">>;
 clear lie_dim,lie_help,lie_p,lie_q,lie_tt,lie_s,lie_kk!*,lie_control>>;
 clear lie_jtest,lie_cc;return lie_list
end;

algebraic procedure lieninstruc(n);
begin
 for i:=1:n-1 do for j:=i+1:n do for k:=1:n do
 <<lie_cc(i,j,k):=lienstrucin(i,j,k);
 lie_cc(j,i,k):=-lienstrucin(i,j,k)>>
end;

algebraic procedure lienjactest(n);
begin
 lie_jtest:=0;
 for i:=1:n-2 do
  for j:=i+1:n-1 do
   for k:=j+1:n do
    for l:=1:n do
    if (for r:=1:n sum
           lie_cc(j,k,r)*lie_cc(i,r,l)+lie_cc(i,j,r)*lie_cc(k,r,l)+
           lie_cc(k,i,r)*lie_cc(j,r,l)) neq 0 then <<lie_jtest:=1;
                      i:=n-1;j:=n;k:=n+1;l:=n+1>>
end;

algebraic procedure liendimcom(n);
begin integer r;
      scalar he;
 lie_dim:=0;
 for i:=1:n-1 do
  for j:=i:n do
   for k:=1:n do
   if lie_cc(i,j,k) neq 0 then
    <<lie_dim:=1;lie_p:=i;lie_q:=j;r:=k;i:=n;j:=k:=n+1>>;
 if lie_dim neq 0 then
 <<for i:=1:n-1 do
    for j:=1:n do
    <<he:=lie_cc(i,j,r)/lie_cc(lie_p,lie_q,r);
      for k:=1:n do
       if lie_cc(i,j,k) neq (he*lie_cc(lie_p,lie_q,k)) then
       <<lie_dim:=2;i:=n;j:=n+1;k:=n+1>>>>;
 if lie_dim=1 then
 <<lie_help:=0;
   for i:=1:n do
    for j:=1:n do
    if (for k:=1:n sum (lie_cc(lie_p,lie_q,k)*lie_cc(k,i,j))) neq 0
     then
    <<lie_help:=1;lie_s:=i;r:=j;i:=j:=n+1>>;
   for i:=1:n do lientrans(1,i):=lie_cc(lie_p,lie_q,i);
   if lie_help=0 then
   <<lientrans(2,lie_p):=lientrans(3,lie_q):=1;lie_kk!*:=1;
     for i:=1:n do <<if
         (lie_cc(lie_p,lie_q,i) neq 0 and i neq lie_p and i neq lie_q)
          then
                   <<lie_tt:=i;i:=n+1>>>>>> else
   <<lientrans(2,lie_s):=
     lie_cc(lie_p,lie_q,r)/(for k:=1:n sum
           (lie_cc(lie_p,lie_q,k)*lie_cc(k,lie_s,r)));
     for i:=1:n do <<if (lie_cc(lie_p,lie_q,i) neq 0 and i neq lie_s)
      then
                   <<lie_tt:=i;i:=n+1>>>>>>>>>>;
end;

algebraic procedure liencentincom(n,tt,p,q);
begin integer con1,con2;
      matrix lie_lamb(n,n);
 lie_control:=0;
 con1:=con2:=0;
 for i:=4:n do
  if (i neq tt and i neq p and i neq q) then
  lientrans(i,i):=1 else
   if (tt neq 1 and p neq 1 and q neq 1 and con1 neq 1) then
   <<lientrans(i,1):=1;con1:=1>> else
    if (tt neq 2 and p neq 2 and q neq 2 and con2 neq 1) then
    <<lientrans(i,2):=1;con2:=1>> else lientrans(i,3):=1;
if n>3 then <<liennewstruc(n,2,tt);
 if n>4 then
  for i:=4 step 2 until n do if (i+1)=n then <<lienfindpair(n,i);
                 if lie_control=1 then lie_kk!*:=lie_kk!*+1>> else
       if i+1<n then <<lienfindpair(n,i);if lie_control=1 then
               <<liennewstruc(n,i,tt),lie_kk!*:=lie_kk!*+1>>else
                       i:=n+1>>>>
end;

algebraic procedure lienfindpair(n,m);
begin scalar he;
      matrix lie_a(n,n);
 lie_control:=0;
 for i:=m:n-1 do
  for j:=i+1:n do
  <<if lie_lamb(i,j) neq 0 then
   <<lie_control:=1;
     lie_a(i,m):=lie_a(m+1,j):=lie_a(j,m+1):=1;
     lie_a(m,i):=1/lie_lamb(i,j);
     for k:=1:n do
      if (k neq i and k neq j and k neq m and k neq (m+1)) then
       lie_a(k,k):=1;
     lientrans:=lie_a*lientrans;i:=n;j:=n+1>>>>;clear lie_a
end;

algebraic procedure liennewstruc(n,m,tt);
begin matrix lie_a(n,n);
 lie_a:=lie_a**0;
 for i:=m:n-1 do
  for j:=i+1:n do
  lie_lamb(i,j):=(for k:=1:n sum for l:=1:n sum
         lientrans(i,k)*lientrans(j,l)*lie_cc(k,l,tt))/lientrans(1,tt);
 for i:=m+2:n do
 <<lie_a(i,m+1):=-lie_lamb(m,i);lie_a(i,m):=lie_lamb(m+1,i)>>;
 lientrans:=lie_a*lientrans;
 for i:=m+2:n-1 do
  for j:=i+1:n do
  lie_lamb(i,j):=(for k:=1:n sum for l:=1:n sum
    lientrans(i,k)*lientrans(j,l)*lie_cc(k,l,tt))/lientrans(1,tt);
    clear lie_a
end;

algebraic procedure liencentoutcom(n,tt,s);
begin integer pp,qq;
      matrix lie_lamb(2,n),lie_a(n,n);
 for i:=3:n do
 <<lientrans(i,i):=1;lie_lamb(1,i):=(for j:=1:n sum
    lientrans(1,j)*lie_cc(j,i,tt))/lientrans(1,tt);
        lie_lamb(2,i):=lie_cc(s,i,tt)*lientrans(2,s)/lientrans(1,tt)>>;
 if (tt>2 and s>2) then
   <<lientrans(tt,tt):=lientrans(s,s):=0;
   lientrans(tt,1):=lientrans(s,2):=1;
     lie_lamb(1,tt):=(for j:=1:n sum
        lientrans(1,j)*lie_cc(j,1,tt)/lientrans(1,tt));
     lie_lamb(1,s):=(for j:=1:n sum
        lientrans(1,j)*lie_cc(j,2,tt)/lientrans(1,tt));
     lie_lamb(2,tt):=lie_cc(s,1,tt)*lientrans(2,s)/lientrans(1,tt);
     lie_lamb(2,s):=lie_cc(s,2,tt)*lientrans(2,s)/lientrans(1,tt)
   >> else if (tt>2 or s>2) then
 <<if tt>2 then <<pp:=3-s;qq:=tt>> else <<pp:=3-tt;qq:=s>>;
 lientrans(qq,qq):=0;lientrans(qq,pp):=1;
 lie_lamb(1,qq):=(for j:=1:n sum
    lientrans(1,j)*lie_cc(j,pp,tt))/lientrans(1,tt);
 lie_lamb(2,qq):=lie_cc(s,pp,tt)*lientrans(2,s)/lientrans(1,tt)>>;
 lie_a:=lie_a**0;
 for i:=3:n do
 <<lie_a(i,2):=-lie_lamb(1,i);lie_a(i,1):=lie_lamb(2,i)>>;
 lientrans:=lie_a*lientrans;clear lie_lamb,lie_a
end;

algebraic procedure lienoutform(at,n,lhelp,kk);
begin operator y;
lie_a:=at;
 if lhelp=1 then
 <<write
     "Your Lie algebra is the direct sum of the Lie algebra L(2) and";
 write "the ",n-2,"-dimensional commutative Lie algebra, where L(2) is";
 write
    "2-dimensional and there exists a basis {X(1),X(2)} in L(2) with";
 write "[X(1),X(2)]=X(1).">>else
 <<write
      "Your Lie algebra is the direct sum of the Lie algebra H(",kk,")";
  write "and the ",n-kk,"-dimensional commutative Lie algebra, where";
  write "H(",kk,") is ",kk,"-dimensional and there exists a basis";
  write "{X(1),...,X(",kk,")} in H(",kk,") with:";
  write "[X(2),X(3)]=[X(2*i),X(2*i+1)]=...=[X(",kk-1,"),X(",kk,")]=X(1)"
     >>;
  write "The transformation into this form is:";
 for i:=1:n do write "X(",i,"):=",for j:=1:n sum
      lie_a(i,j)*y(j);clear y,lie_a
end;

endmodule;

end;
