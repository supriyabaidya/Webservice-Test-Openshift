param n, integer; 
param m, integer ;
param k, integer;
#set A:={i in 1..n,j in 1..m};
set I, dimen 2;


/*param A{I};
table tin IN "CSV" "adjacent.csv": 
I<-[i,j], A;*/
 
 param S{I};
 table tin IN "CSV" "coverage.csv" :
 I<-[i,j], S; 
 

var x{i in 1..n}, binary; #assignment
 
minimize Cost: (sum{i in k+1..n} x[i]);


subject to first {j in 1..m}: (sum{i in 1..n} S[i,j]*x[i])>=1;

solve;
printf "\n";
printf{i in 1..n} "%d,", x[i];
/* Displaying results */
#display x{i in 1..n};
#display{(i,j) in I}:S[i,j];#: x[i,j]=1 }: x[i,j];*/
#display 'total costs=',sum{j in V}(sum{i in V} (c[i,j]*x[i,j]))
data;
param n:= 500;
param m:= 93;
param k:= 5;
/*param p:=6;
param S:=[1,1] 1 [1,2] 0 [1,3] 1 [2,1] 0 [2,2] 1 [2,3] 0 [3,1] 0 [3,2] 0 [3,3] 1 [4,1] 1 [4,2] 0 [4,3] 0 ;*/
end; 
 
