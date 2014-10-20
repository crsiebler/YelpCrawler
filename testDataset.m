clear all;

disp('Processing Edge-List File')

A = csvread('/home/reza/Desktop/dataset.csv');

dim = max(max(A));
[E_Size, junk] = size(A);

sprintf('The dataset has %d nodes and %d edges',dim, E_Size)

disp('Filling Adjanceny Matrix')

adj = sparse(A(:,1), A(:,2), ones(E_Size,1), dim, dim, E_Size);

% This next few lines can take forever if your dataset is large. 
% You can comment them in case your dataset is large or use adj-adj' to test if your graph is undirected 

if(adj==adj')
    disp('Symmetric Adjacency Matrix - Undirected Graph')
else
    disp('Assymmetric Adjacency Matrix - Directed Graph')
end


