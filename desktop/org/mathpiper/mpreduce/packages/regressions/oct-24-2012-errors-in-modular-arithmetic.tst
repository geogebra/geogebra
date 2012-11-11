on modular;
setmod 2;
% The following input caused nil to be called as function
sqrt(1-x^2);

% another error...
setmod 11;
off precise;
sqrt(10*(x^2+10));

% Check that domain mode is reset during definite integration

int(sqrt(1-x^2),x,0,1);

int(sqrt(10*(x^2+10)),x,0,1);

end;
