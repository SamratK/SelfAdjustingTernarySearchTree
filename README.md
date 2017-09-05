# SelfAdjustingTernarySearchTree
A Ternary Search Tree that can restructure itself so as to yield the best access or retrieval time based on conditional rotations heuristic.

# Description
A Ternary Search Tree (TST) is a highly efficient dynamic dictionary structure applicable for strings and textual
data. They are more space efficient compared to standard prefix trees, at the cost of speed. The aim of this project is to implement
an algorithm to restructure the TST so as to yield the best access or retrieval time. Unlike the case of lists and binary search trees,
where numerous methods have been proposed, in the case of the TST, currently, the number of reported adaptive
schemes are few. This implementation is based on the paper by Ghada Hany Badr and B. John Oommen for self-adjusting ternary search trees.

# TST Features
Few points about TSTs before we discuss the restructuring schemes :-
<ul>
<li>TSTs are efficient and easy to implement. They combine the low space overhead of BSTs and the character-based time efficiency of tries.</li>
<li>TSTs are usually substantially faster than hash tables for unsuccessful searches because hash tables must compare the entire string rather than just the first few characters. This also depends on the load factor of hash tables. Hash tables also frequently use more memory than ternary search trees but not as much as tries.</li>
<li>TSTs grow and shrink gracefully whereas hash tables need to be rebuilt after large size changes.</li>
<li>TSTs support advanced searches, such as partial-matches and near-neighbor searches. They support many other operations, such as traversal, so as to report the items in a sorted order.</li>
</ul>

# Self-Adjusting Heuristics
Coming to the schemes, below three are proposed :-
1. Splaying for TST - This is search based restructure. Similar to SplayTree, each node accessed is splayed upwards to make the first matched character node as root or closest character node as root in case of unsuccessful search.
2. Randomized TST - This is insert based restructure. Similar to Treap, each node consists of a priority variable which is given a random value at the time of creation. Then the node is compared with
its parent and rotations are done recursively to maintain the heap property.
3. Conditional Rotations for TST - This is search based restructure. In this heuristic, the rotation is not done on every data access operation unlike the Splay tree.
The main concept used is locality of reference. In a relatively small duration, if a node is accessed number of times, it is moved close to the root to make further access faster which refers to temporal locality.
As the movement of the node is done through rotations maintaining the BST property, the nodes close to it also move along with it which refers to the spatial locality.
Rotation is performed if and only if the overall WPL (Weighted Path Length) of the entire BST rooted at the current middle node decreases.
Any rotation performed on any node in the current BST, i, will cause the weighted path length of i to decrease, and so this will decrease the accumulated WPL of the middle nodes along the search path of the entire TST.
A function psi is defined to compare the cost of tree restructuring operation. It anticipates whether a rotation will globally minimize the cost of the overall TST or not.

<h2>Conditional Rotations for TST</h2>
Below are the details of the implementation.<pre>
TST node has a data variable to store the character value. Three variables, left, middle and right to point
to left, middle and right children respectively. In addition, two variables are added to each node :-
1. alpha(i) - Total number of accesses of a node i.
2. tou(i) - Total number of accesses of subtree of a node i.  
Let i be the current node, iP its parent node, iL its left child node and iR, the right child node then psi
is defined as follows :-

If i is the left child of iP :-
psi = 2*tou(i) - tou(iR) - tou(iP);

If i is the right child of iP :-
psi = 2*tou(i) - tou(iL) - tou(iP);

If i is the middle child of iP :-
psi = 0;

If psi > 0 then rotations are performed. If it is not greater than zero then splaying is not done. In this
way rotations are optimized. The implementation could be done with one additional variable alone, tou.
alpha value can be calculated as alpha(i) = tou(i) - tou(iL) - tou(iR). It is used for clarity.
</pre>

<h2>API</h2><pre>
Components:-
<b>TenarySearchSplayTree.java</b> - Implementation of Splaying scheme.
<b>SelfAdjustingTernarySearchTree.java</b> - Implementation of Conditional Rotations based Splaying scheme.
<b>TreeVisualizer.java</b> - JavaFX component to visualize the tree.
</pre>

# Summary
Conditional TST is the best scheme that can be used to improve the performance of the original TST and it has the ability to learn when to stop “self-adjusting” whenever the tree will not need further adjustments.
More details of the concept can be found in the paper by aforementioned authors.

<h2>Notes</h2>
<ul>
<li>tou values can be normalized after number of searches.</li>
<li>Tree restructuring can be avoided for unsuccessful searches.</li>
<li>TreeVisualizer can be improved to better display the nodes.</li>
</ul>
