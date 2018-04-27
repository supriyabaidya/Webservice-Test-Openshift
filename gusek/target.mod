param n, integer; 
param m, integer ;

#set A:={i in 1..n,j in 1..m};
set I, dimen 2;
set J, dimen 1;

param S{I};
 table tin IN "CSV" "coverageR.csv" :  # coverageR_IPP.csv
 I<-[i,j], S; 
 
 param y{J};
 table tin IN "CSV" "subsetC.csv" :
 J<-[i], y; 

var x{i in 1..n}, binary; #assignment

#############################################
minimize Cost: (sum{i in 1..n} x[i]); 
#############################################
subject to first {j in 1..m}: (sum{i in 1..n} S[i,j]*x[i])>=y[j];

solve;
printf "\n";
printf{i in 1..n} "%d,", x[i];
table result{i in 1..n} OUT "CSV" "cov_node.csv":
 i~i, x[i]~x;

data;

param n:= 600;  # number of sensors
param m:= 200;  #total number of target

end; 
 

