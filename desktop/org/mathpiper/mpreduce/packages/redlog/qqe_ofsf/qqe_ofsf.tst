load_package redlog;
rlset(qqe,ofsf);
off rlverbose;

% Examples from C. Strasser's diploma thesis
% Ch.10. Software Verifikation, p.171
f := ex({q},((j > 5 and radd(a,q) == qepsilon) or (radd(a,q) <<>>
qepsilon and lhead(radd(a,q)) > 23 + j)) and lhead(radd(a,q)) = x)$

rlqe f;

f := ex({a}, ((j > 5 and radd(a,q) == qepsilon) or (radd(a,q) <<>>
qepsilon and lhead(radd(a,q)) > 23 + j)) and lhead(radd(a,q)) = x)$

rlqe f;

% 2-periodic queue of odd length with prefix [0,0] and postfix [1,1]:
p2 := ex(qp,q == ladd(0,ladd(0,radd(1,radd(1,qp)))) and
ex({x,y},x <> y and ladd(y,ladd(x,qp)) == radd(y,radd(x,qp))))$

rlqe p2;

end;
