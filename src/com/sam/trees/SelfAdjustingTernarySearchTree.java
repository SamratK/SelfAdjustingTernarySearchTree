package com.sam.trees;

import java.util.LinkedList;
import java.util.Queue;
/**
 * Ternary search tree with conditional splaying to bring the accessed node to the root.
 * Conditional rotation heuristics is used to optimize rotations.
 * @author SamratK
 * https://github.com/SamratK
 */
public class SelfAdjustingTernarySearchTree {

    class TernaryTreeNode{
        char data;
        boolean isLeaf;
        
        //Stores number of access to the current node.
        int alpha;
        
        //Stores number of access to the subtrees of current node.
        //If current node is i and its left and right children are iL and iR respectively,
        //then tou(i) = alpha(i) + tou(iL) + tou(iR);
        int tou;
        
        TernaryTreeNode left, right, eq;
        TernaryTreeNode(char data){
        	this.data = data;
        }
        
        public String toString(){
        	return "["+data+"]";
        }
    }
    
    private TernaryTreeNode root = null;
    
    public void insert(String data){
    	TernaryTreeNode root = insert(this.root,data,0);
        this.root = root;
    }
    
    private TernaryTreeNode insert(TernaryTreeNode root,String data,int pos){
        if(pos == data.length()){
            return root;
        }
        if(root == null){
            root = new TernaryTreeNode(data.charAt(pos));
            root.eq = insert(root.eq,data,pos+1);
            if(pos == (data.length()-1)){
                root.isLeaf = true;
            }
        }else{
            if(root.data == data.charAt(pos)){
                root.eq = insert(root.eq,data,pos+1);
                if(pos == (data.length()-1)){
                    root.isLeaf = true;
                }
            }
            else if(root.data < data.charAt(pos)){
                root.right = insert(root.right,data,pos);
            }else{
                root.left = insert(root.left,data,pos);
            }
        }
        return root;
    }
    
    public boolean searchWithoutSplaying(String data){
        return searchWithoutSplaying(root, data, 0);
    }
    
    /*
     * Searches the string, data without splaying the nodes during the traversal.
     */
    private boolean searchWithoutSplaying(TernaryTreeNode root, String data, int pos){
        if(pos == data.length()){
            return true;
        }
        
        if(root == null){
            return false;
        }
        
        if(root.data == data.charAt(pos)){
            boolean result = searchWithoutSplaying(root.eq, data, pos+1);
            if(pos == data.length() -1){
                return result && root.isLeaf;
            }
            return result;
        }else if(root.data < data.charAt(pos)){
            return searchWithoutSplaying(root.right, data, pos);
        }else{
            return searchWithoutSplaying(root.left, data, pos);
        }
    }
    
    //Variable to hold the result of current text search.
    private boolean textFound;
    
    /*
     * Calls the search method which will return closest node and updates the variable, textFound.
     */
    public boolean search(String data){
        textFound = false;
        root = search(root, data, 0);
        if(textFound){
        	return true;
        }
    	
    	return false;
    }
    
    /*
     * Utility method to get tou value.
     */
    private int tou(TernaryTreeNode node){
    	if(node==null)
    		return 0;
    	return node.tou;
    }
    
    /*
     * Searches the given text using splaying with conditional rotations heuristics. A node which is found as part of search is splayed based on psi value.
     * Let i be the current node, iP its parent node, iL its left child node and iR, the right child node then psi is defined as follows :-
     * If i is the left child of iP :-
     * psi = 2*tou(i) - tou(iR) - tou(iP);
     * 
     * If i is the right child of iP :-
     * psi = 2*tou(i) - tou(iL) - tou(iP);
     * 
     * If i is the middle child of iP :-
     * psi = 0;
     * 
     * If psi > 0 then rotations are performed. If it is not greater than zero then splaying is not done.
     * 
     */
    private TernaryTreeNode search(TernaryTreeNode root, String data, int pos){
        if(root == null){
            return null;
        }
      
        //Update the alpha and tou values.
        root.alpha = root.alpha + 1;
        root.tou = root.tou + 1;

        //Key lies in left sub tree.
    	if(root.data > data.charAt(pos)){
    			if(root.left == null) 
    				return root;//Return the closest node.
    			
    			//Left - Left case.
    			if(root.left.data > data.charAt(pos)){
    				
    				root.left.left = search(root.left.left, data, pos);
    				
    				//Calculate psi value.
    				int psi = 2*tou(root.left.left) - tou(root.left.left.right) - tou(root.left);
    				
    				if(psi > 0){
    					//As it is Left - Left case, do a right rotation.
    					//Note that only one right rotation is done unlike splay tree for Left - Left case. Rotation at the parent node is based on psi calculated at its level.
    					root.left = rightRotate(root.left);
    					
    					//Update the tou values.
    					root.left.right.tou = root.left.right.alpha + tou(root.left.right.left) + tou(root.left.right.right);
    					root.left.tou = root.left.alpha + tou(root.left.left) + tou(root.left.right);
    				}
    			}
    			else if(root.left.data < data.charAt(pos)){//Left - Right case.

    				root.left.right= search(root.left.right, data, pos);
    				
    				//Calculate psi value.
    				int psi = 2*tou(root.left.right) - tou(root.left.right.left) - tou(root.left);
    				
    				if(psi > 0){
    					//As it is Left - Right case, do a left rotation.
    					root.left = leftRotate(root.left);
    					
    					root.left.left.tou = root.left.left.alpha + tou(root.left.left.left) + tou(root.left.left.right);
    					root.left.tou = root.left.alpha + tou(root.left.left) + tou(root.left.right);
    				}
    			}else{
    				//Equal case.
    				root.left = search(root.left, data, pos);

    				//i is the left child of iP. Here i is root.left and parent is root.
    				int psi = 2*tou(root.left) - tou(root.left.right) - tou(root);
    				
    				if(psi > 0){
    					TernaryTreeNode rotatedNode = rightRotate(root); 
    					
    					root.tou = root.alpha + tou(root.left) + tou(root.right);
    					rotatedNode.tou = rotatedNode.alpha + tou(rotatedNode.left) + tou(rotatedNode.right);
    					
    					return rotatedNode;
    				}
    			}
    			
    			return root;
    		}
    		else if(root.data < data.charAt(pos)){//Key lies in right subtree
    	        // Key is not in tree, return the closest node.
    	        if (root.right == null) return root;
    	 
    	        //Right - Left case.
    	        if(root.right.data > data.charAt(pos)){
    				
    				root.right.left = search(root.right.left, data, pos);
    				
    				//Calculate psi value.
    				int psi = 2*tou(root.right.left) - tou(root.right.left.right) - tou(root.right);
    				
    				if(psi > 0){
    					//As it is Right - Left case, do a right rotation.
    					root.right = rightRotate(root.right);
    					
    					//Update the tou values.
    					root.right.right.tou = root.right.right.alpha + tou(root.right.right.left) + tou(root.right.right.right);
    					root.right.tou  = root.right.alpha + tou(root.right.left) + tou(root.right.right);
    				}
    			}
    	        else if(root.right.data < data.charAt(pos)){//Right - Right case.

    				root.right.right= search(root.right.right, data, pos);
    				
    				//Calculate psi value.
    				int psi = 2*tou(root.right.right) - tou(root.right.right.left) - tou(root.right);
    				
    				if(psi > 0){
    					//As it is Right - Right case, do a left rotation.
    					root.right = leftRotate(root.right);
    					
    					//Update the tou values.
    					root.right.left.tou = root.right.left.alpha + tou(root.right.left.left) + tou(root.right.left.right);
    					root.right.tou  = root.right.alpha + tou(root.right.left) + tou(root.right.right);
    				}
    			}else{
    				//Equal case.
    				root.right = search(root.right, data, pos);
    				
    				//i is the right child of iP. Here i is root.right and parent is root.
    				int psi = 2*tou(root.right) - tou(root.right.left) - tou(root);
    				
    				if(psi > 0){
    					TernaryTreeNode rotatedNode = leftRotate(root);
    					
    					root.tou = root.alpha + tou(root.left) + tou(root.right);
    					rotatedNode.tou = rotatedNode.alpha + tou(rotatedNode.left) + tou(rotatedNode.right);
    					
    					return rotatedNode;
    				}
    			}
	
    	        return root;
    	    }

        else{
        	//Current node matches with a character in the search text.
	    	//Search for next characters only if pos is not the last index of the search text. 
	    	if(pos+1 != data.length()){
	    		root.eq = search(root.eq, data, pos+1);
	    	}
	    	
	    	//If all characters are matched and pos is the last index of search text then update textFound as true.
	    	if(pos == data.length()-1){    	    		
	    		if(root.isLeaf){
	    			textFound = true;
	    		}
	    	}
	    	
	    	return root;
	    }
    }
    
    /*
     * Utility method to perform left rotation at the given node.
     */
    private TernaryTreeNode leftRotate(TernaryTreeNode x){
    	TernaryTreeNode y = x.right;
    	x.right = y.left;
    	y.left = x;
    	return y;
    }
    
    /*
     * Utility method to perform right rotation at the given node.
     */
    private TernaryTreeNode rightRotate(TernaryTreeNode x){
    	TernaryTreeNode y = x.left;
    	x.left = y.right;
    	y.right = x;
    	return y;
    }
    
    private void levelOrder(){
    	Queue<TernaryTreeNode> queue = new LinkedList<TernaryTreeNode>();
    	queue.add(root);
    	queue.add(null);
    	TernaryTreeNode currNode = null;
    	
    	while(!queue.isEmpty()){
    		currNode = queue.poll();
    		
    		if(currNode!=null){
    			System.out.print(currNode+"tou("+currNode.tou+")"+"->");
    			System.out.print(currNode.left==null?"[]":currNode.left);
    			System.out.print(currNode.eq==null?"[]":currNode.eq);
    			System.out.print(currNode.right==null?"[]":currNode.right);
    			System.out.println();
    			if(currNode.left!=null){
    				queue.add(currNode.left);
    			}
    			if(currNode.eq!=null){
    				queue.add(currNode.eq);
    			}
    			if(currNode.right!=null){
    				queue.add(currNode.right);
    			}
    		}else{
    			System.out.println();
    			if(!queue.isEmpty()){
    				queue.add(null);
    			}
    		}
    	}
    }
    
    /*
     * Below steps demonstrate the way the TST is restructured based on access of the nodes.
     */
    public static void main(String args[]){
    	
    	SelfAdjustingTernarySearchTree ternarySearchTree = new SelfAdjustingTernarySearchTree();
    	
    	//Insert string into the tree.
        ternarySearchTree.insert("font");
        ternarySearchTree.insert("ask");
        ternarySearchTree.insert("an");
        ternarySearchTree.insert("fork");
        ternarySearchTree.insert("for");
        ternarySearchTree.insert("rest");
        ternarySearchTree.insert("cap");
        ternarySearchTree.insert("or");
 
        //Search for the strings using searchWithoutSplaying.
        System.out.println("Search font - "+ternarySearchTree.searchWithoutSplaying("font"));
        System.out.println("Search ask - "+ternarySearchTree.searchWithoutSplaying("ask"));
        System.out.println("Search an - "+ternarySearchTree.searchWithoutSplaying("an"));
        System.out.println("Search fork - "+ternarySearchTree.searchWithoutSplaying("fork"));
        System.out.println("Search for - "+ternarySearchTree.searchWithoutSplaying("for"));
        System.out.println("Search rest - "+ternarySearchTree.searchWithoutSplaying("rest"));
        System.out.println("Search tap - "+ternarySearchTree.searchWithoutSplaying("tap"));
        System.out.println("Search cap - "+ternarySearchTree.searchWithoutSplaying("cap"));
        System.out.println("Search or - "+ternarySearchTree.searchWithoutSplaying("or"));
        
        //Print level order traversal of tree.
        ternarySearchTree.levelOrder();
        
        //Uncomment the below code to see visual representation of tree. Only one of them will run at a time as JavaFX Application's launch method can be called once for an execution. 
        //TreeVisualizer.createTree(ternarySearchTree.root);        
        
        //Search the text with conditional splaying.
        System.out.println("Search cap "+ternarySearchTree.search("cap"));//Tree structure is not changed. f is the root. tou(f) is 1.
        //TreeVisualizer.createTree(ternarySearchTree.root);
               
        
        System.out.println("Search cap "+ternarySearchTree.search("cap"));//Tree structure is changed. c is the root. tou(c) is 4.
        //TreeVisualizer.createTree(ternarySearchTree.root);
        
        System.out.println("Search rest "+ternarySearchTree.search("rest"));//Tree structure is not changed. c is still the root. tou(c) is 5.
        System.out.println("Search rest "+ternarySearchTree.search("rest"));//Tree structure is not changed. c is still the root. tou(c) is 6.
        System.out.println("Search for "+ternarySearchTree.search("for"));//Tree structure is not changed. c is still the root. tou(c) is 7.
        
        System.out.println("Search for "+ternarySearchTree.search("for"));//Tree structure is changed. f is the root. tou(f) is 12.
        //TreeVisualizer.createTree(ternarySearchTree.root);
                
        System.out.println("Search for "+ternarySearchTree.search("for"));//Tree structure is not changed. f is still the root.
        System.out.println("Search for "+ternarySearchTree.search("for"));//Tree structure is not changed. f is still the root. tou(f) is 14.
        System.out.println("Search rest "+ternarySearchTree.search("rest"));//Tree structure is not changed. f is still the root. tou(f) is 15. tou(r) is 3.
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));
        System.out.println("Search rest "+ternarySearchTree.search("rest"));//Tree structure is not changed. f is still the root. tou(f) is 24. tou(r) is 12.
        
        System.out.println("Search rest "+ternarySearchTree.search("rest"));//Tree structure is changed. r is the root. tou(r) is 36. tou(f) is 23. 
        //TreeVisualizer.createTree(ternarySearchTree.root);
        
        System.out.println("Search rest "+ternarySearchTree.search("rest"));//Tree structure is not changed. r is the root. tou(r) is 37.
        
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));//Tree structure is not changed. r is the root. tou(r) is 42. tou(f) is 23. tou(a) is 5.
        
        //Tree structure is changed. r is still the root. tou(r) is 43. subtree of ask comes closer to the root by rotating f. tou(c) is 35. tou(f) is 17. 
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        //TreeVisualizer.createTree(ternarySearchTree.root);
        
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));
        System.out.println("Search ask "+ternarySearchTree.search("ask"));//c is rotated. r is still the root. tou(r) is 55. tou(c) is 29. tou(a) is 47.
        //TreeVisualizer.createTree(ternarySearchTree.root);
        
        System.out.println("Search ask "+ternarySearchTree.search("ask"));//a is the root. tou(a) is 81.
        //TreeVisualizer.createTree(ternarySearchTree.root);
        
        System.out.println("Search cap "+ternarySearchTree.search("cap"));
        System.out.println("Search cap "+ternarySearchTree.search("cap"));
        System.out.println("Search cap "+ternarySearchTree.search("cap"));        
        System.out.println("Search cap "+ternarySearchTree.search("cap"));        
        System.out.println("Search font "+ternarySearchTree.search("font"));//c is rotated with f and then r is rotated with f.
        //TreeVisualizer.createTree(ternarySearchTree.root);
        
        System.out.println("Search font "+ternarySearchTree.search("font"));//f is the root.
        ternarySearchTree.levelOrder();

        System.out.println("Searching the strings after splaying.");
        System.out.println("Search font - "+ternarySearchTree.searchWithoutSplaying("font"));
        System.out.println("Search ask - "+ternarySearchTree.searchWithoutSplaying("ask"));
        System.out.println("Search an - "+ternarySearchTree.searchWithoutSplaying("an"));
        System.out.println("Search fork - "+ternarySearchTree.searchWithoutSplaying("fork"));
        System.out.println("Search for - "+ternarySearchTree.searchWithoutSplaying("for"));
        System.out.println("Search rest - "+ternarySearchTree.searchWithoutSplaying("rest"));
        System.out.println("Search tap - "+ternarySearchTree.searchWithoutSplaying("tap"));
        System.out.println("Search cap - "+ternarySearchTree.searchWithoutSplaying("cap"));
        System.out.println("Search or - "+ternarySearchTree.searchWithoutSplaying("or"));
        TreeVisualizer.createTree(ternarySearchTree.root);
    }
}