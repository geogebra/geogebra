load_package redlog;
rlset mri;
on rlverbose;

mrireal x,y;

wex := ex(x,0<y-x and 4(y-x)<1 and cong(2x,1,2));

rlqe wex$

rlexpand ws;

end;
