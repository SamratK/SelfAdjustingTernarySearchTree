package com.sam.trees;

import java.util.LinkedList;
import java.util.Queue;
/**
 * Ternary search tree with splaying to bring the accessed node to the root.
 * @author SamratK
 * https://github.com/SamratK
 */
public class TenarySearchSplayTree {
	class TernaryTreeNode{
		char data;
		boolean isLeaf;
		TernaryTreeNode left, right, eq;
		TernaryTreeNode(char data){
			this.data = data;
		}
		public String toString(){
			return "["+data+"]";
		}
	}

	private TernaryTreeNode root;

	public void insert(String data){
		root = insert(root, data, 0);
	}

	public boolean search(String data){
		return search(root, data, 0);
	}

	private boolean search(TernaryTreeNode root, String data, int pos){
		if(pos == data.length()){
			return true;
		}
		
		if(root == null){
			return false;
		}
		
		if(root.data == data.charAt(pos)){
			boolean result = search(root.eq, data, pos+1);
			if(pos == data.length() -1){
				return result && root.isLeaf;
			}
			return result;
		}else if(root.data < data.charAt(pos)){
			return search(root.right, data, pos);
		}else{
			return search(root.left, data, pos);
		}
	}

	private TernaryTreeNode insert(TernaryTreeNode root, String data, int pos){
		if(pos == data.length()){
			return root;
		}
		
		if(root == null){
			root = new TernaryTreeNode(data.charAt(pos));
			root.eq = insert(root.eq, data, pos+1);
			if(pos == (data.length()-1)){
				root.isLeaf = true;
			}
		}else{
			if(root.data == data.charAt(pos)){
				root.eq = insert(root.eq, data, pos+1);
				if(pos == (data.length()-1)){
					root.isLeaf = true;
				}
			}
			else if(root.data < data.charAt(pos)){
				root.right = insert(root.right, data, pos);
			}else{
				root.left = insert(root.left, data, pos);
			}
		}
		return root;
	}

	public void levelOrder(){
		Queue<TernaryTreeNode> queue = new LinkedList<TernaryTreeNode>();
		queue.add(root);
		queue.add(null);
		TernaryTreeNode currNode = null;

		while(!queue.isEmpty()){
			currNode = queue.poll();

			if(currNode!=null){
				System.out.print(currNode.data+"->");
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

	TernaryTreeNode leftRotate(TernaryTreeNode x){
		TernaryTreeNode y = x.right;
		x.right = y.left;
		y.left = x;
		return y;
	}

	TernaryTreeNode rightRotate(TernaryTreeNode x){
		TernaryTreeNode y = x.left;
		x.left = y.right;
		y.right = x;
		return y;
	}
	
	boolean wordFound;
	public boolean splaySearch(String data){
		wordFound = false;
		root = splaySearch(root, data, 0);
		if(wordFound){
			return true;
		}

		return false;
	}

	private TernaryTreeNode splaySearch(TernaryTreeNode root, String data, int pos){
		if(pos == data.length() || root == null){
			return root;
		}

		//Key lies in left sub tree.
		if(root.data > data.charAt(pos)){
			if(root.left == null) 
				return root;//This makes the node with data closest to given key to be set as root.

			//Left - Left
			if(root.left.data > data.charAt(pos)){

				root.left.left = splaySearch(root.left.left, data, pos);

				//First rotate for root, second rotation is done before returning if possible.
				root = rightRotate(root);
				
			}else if(root.left.data < data.charAt(pos)){//Left - Right

				root.left.right= splaySearch(root.left.right, data, pos);

				if(root.left.right != null){
					root.left = leftRotate(root.left);
				}
			}else{
				root.left.eq = splaySearch(root.left.eq, data, pos+1);
			}

			//Do rotation for root.
			return root.left == null ? root : rightRotate(root);
		}
		else if(root.data < data.charAt(pos)){//Key lies in right subtree
			// Key is not in tree, return root;
			if (root.right == null) return root;

			// Right - Left
			if (root.right.data > data.charAt(pos))
			{
				root.right.left = splaySearch(root.right.left, data, pos);

				// Do rotation for root.right
				if (root.right.left != null)
					root.right = rightRotate(root.right);
			}
			else if (root.right.data < data.charAt(pos))//Right - Right
			{

				root.right.right = splaySearch(root.right.right, data, pos);
				
				root = leftRotate(root);
				
			}else{
				root.right.eq = splaySearch(root.right.eq, data, pos+1);
			}

			// Do rotation for root
			return (root.right == null)? root: leftRotate(root);
		}
		else{
			if(pos+1 != data.length()){
				root.eq = splaySearch(root.eq, data, pos+1);
			}

			if(pos == data.length()-1){    	    		
				if(root.isLeaf){
					wordFound = true;
				}
			}

			return root;
		}
	}    

	public static void main(String args[]){
		TenarySearchSplayTree ternarySearchTree = new TenarySearchSplayTree();
		ternarySearchTree.insert("font");
		ternarySearchTree.insert("ask");
		ternarySearchTree.insert("an");
		ternarySearchTree.insert("fork");
		ternarySearchTree.insert("for");
		ternarySearchTree.insert("rest");
		ternarySearchTree.insert("cap");
		ternarySearchTree.insert("or");

		ternarySearchTree.levelOrder();
		//Uncomment the below code to see visual representation of tree. Only one of them will run at a time as JavaFX Application's launch method can be called once for an execution.
		//TreeVisualizer.createTree(ternarySearchTree.root);
		
		System.out.println("Search font - "+ternarySearchTree.search("font"));
	    System.out.println("Search ask - "+ternarySearchTree.splaySearch("ask"));
	    System.out.println("Search an - "+ternarySearchTree.search("an"));
	    System.out.println("Search fork - "+ternarySearchTree.search("fork"));
	    System.out.println("Search for - "+ternarySearchTree.splaySearch("for"));
	    //TreeVisualizer.createTree(ternarySearchTree.root);
	    
	    System.out.println("Search font - "+ternarySearchTree.splaySearch("font"));
	    //TreeVisualizer.createTree(ternarySearchTree.root);
	    
	    System.out.println("Search rest - "+ternarySearchTree.search("rest"));
	    System.out.println("Search tap - "+ternarySearchTree.splaySearch("tap"));
	    //TreeVisualizer.createTree(ternarySearchTree.root);
	    
	    System.out.println("Search cap - "+ternarySearchTree.search("cap"));
	    System.out.println("Search or - "+ternarySearchTree.splaySearch("or"));
	    //TreeVisualizer.createTree(ternarySearchTree.root);
	    
	    System.out.println("Search fork - "+ternarySearchTree.splaySearch("fork"));
	    TreeVisualizer.createTree(ternarySearchTree.root);
	    
	    System.out.println("Searching the strings after splaying.");
	    System.out.println("Search font - "+ternarySearchTree.search("font"));
        System.out.println("Search ask - "+ternarySearchTree.search("ask"));
        System.out.println("Search an - "+ternarySearchTree.search("an"));
        System.out.println("Search fork - "+ternarySearchTree.search("fork"));
        System.out.println("Search for - "+ternarySearchTree.search("for"));
        System.out.println("Search rest - "+ternarySearchTree.search("rest"));
        System.out.println("Search tap - "+ternarySearchTree.search("tap"));
        System.out.println("Search cap - "+ternarySearchTree.search("cap"));
        System.out.println("Search or - "+ternarySearchTree.search("or"));
	}
}