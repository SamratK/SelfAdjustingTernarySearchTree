package com.sam.trees;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Tree visualizer.
 * @author SamratK
 * https://github.com/SamratK
 */
public class TreeVisualizer extends Application{
	static GenericTreeNode root;
	public static void createTree(Object rootNode) {
		try {
			root = createGenericTreeNode(rootNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		launch(new String[]{});
	}

	static GenericTreeNode createGenericTreeNode(Object rootNode) throws Exception{
		if(rootNode==null){
			return null;
		}
		Class<?> classObj = rootNode.getClass();
		GenericTreeNode genericNode = null;
		
		Field dataField = classObj.getDeclaredField("data");
		dataField.setAccessible(true);
		char data = (Character)dataField.get(rootNode);
		genericNode = new GenericTreeNode(String.valueOf(data));
		
		Field left = classObj.getDeclaredField("left");
		left.setAccessible(true);
		Field right = classObj.getDeclaredField("right");
		right.setAccessible(true);
		Field eq = classObj.getDeclaredField("eq");
		eq.setAccessible(true);
		genericNode.left = createGenericTreeNode(left.get(rootNode));
		genericNode.right = createGenericTreeNode(right.get(rootNode));
		genericNode.eq = createGenericTreeNode(eq.get(rootNode));
		
		return genericNode;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setWidth(1000);
		stage.setHeight(800);
		Group group = null;
		group = getVisualElements(createVisualElements(), stage.getWidth());
		Scene scene = new Scene(group);
		stage.setScene(scene);
		stage.show();
	}
	
	ArrayList<VisualTreeNode> createVisualElements(){
		ArrayList<VisualTreeNode> elemList = new ArrayList<VisualTreeNode>();
		//TreeNode currNode = null;
		GenericTreeNode currNode = null;
		//Queue<TreeNode> queue = new LinkedList<TreeNode>();
		Queue<GenericTreeNode> queue = new LinkedList<GenericTreeNode>();
		queue.add(root);
		queue.add(null);
		Text parentNode = new Text(String.valueOf(root.data));	
		
		elemList.add(new VisualTreeNode(parentNode, null, false, true));
		elemList.add(null);
		int index=0;
		while(!queue.isEmpty()){
			currNode = queue.poll();
			if(currNode!=null){
				if(elemList.get(index)==null){
					parentNode = elemList.get(++index).text;
				}else{
					parentNode = elemList.get(index).text;
				}
				if(currNode.left!=null){
					queue.add(currNode.left);
					elemList.add(new VisualTreeNode(new Text(currNode.left.data), parentNode, true, false));	
				}
				if(currNode.eq!=null){
					queue.add(currNode.eq);
					elemList.add(new VisualTreeNode(new Text(currNode.eq.data), parentNode, false, true));	
				}
				if(currNode.right!=null){
					queue.add(currNode.right);
					elemList.add(new VisualTreeNode(new Text(currNode.right.data), parentNode, false, false));	
				}
				index++;
			}else{
				
				if(!queue.isEmpty()){
					queue.add(null);
					elemList.add(null);
				}
				
			}
		}
		return elemList;
	}
	
	Group getVisualElements(ArrayList<VisualTreeNode> list, double width){
		Group group = new Group();
		double rootCenterX = width/2;
		double rootCenterY = 40;
		Line line = null;
		double currentY=rootCenterY;
		double offsetX = 120;
		VisualTreeNode node  = list.get(0);
		double fontSize = 20;
		
		node.text.setLayoutX(rootCenterX);
		node.text.setLayoutY(currentY);
		node.text.setFont(new Font(fontSize));
		group.getChildren().add(node.circle);
		group.getChildren().add(node.text);
		
		for(int i=1;i<list.size();i++){
			node = list.get(i);
			if(node==null){
				currentY+=100;
				offsetX = offsetX - 10;
				continue;
			}
			
			if(node.isLeft){
				node.text.setLayoutX(node.parent.getLayoutX() - offsetX*5/i);//Make elements closer as the levels go down.
				node.text.setLayoutY(currentY);
			}
			else if(node.isEq){
				node.text.setLayoutX(node.parent.getLayoutX());
				node.text.setLayoutY(currentY);
			}			
			else{
				node.text.setLayoutX(node.parent.getLayoutX() + offsetX*5/i + 10);
				node.text.setLayoutY(currentY);
			}
			node.text.setFont(new Font(fontSize));
			line = new Line(node.parent.getLayoutX()+node.parent.getLayoutBounds().getWidth()/2, node.parent.getLayoutY()+14, 
					node.text.getLayoutX()+node.text.getLayoutBounds().getWidth()/2 , node.text.getLayoutY() - node.text.getLayoutBounds().getHeight());
			group.getChildren().add(node.circle);
			group.getChildren().add(node.text);
			group.getChildren().add(line);
		}
		
		return group;
	}
	
	class VisualTreeNode{
		Text text;
		Text parent;
		boolean isLeft;
		boolean isEq;
		Circle circle;
		VisualTreeNode(){
		}
		VisualTreeNode(final Text text, Text parent, boolean isLeft, boolean isEq){
			this.text = text;
			this.parent = parent;
			this.isLeft = isLeft;
			this.isEq = isEq;
			circle = new Circle(20);
			circle.layoutXProperty().bind(text.layoutXProperty());
			circle.layoutYProperty().bind(text.layoutYProperty());
			circle.setTranslateX(10);
			circle.setTranslateY(-5);
			circle.setFill(Color.WHITE);
			circle.setStroke(Color.BLACK);
			circle.setStrokeWidth(1);
		}
		
		public String toString(){
			if(parent==null){
				return "["+text.getText()+"]";
			}
			return "["+text.getText()+"("+parent.getText()+")"+"]";
		}
	}
	
	static class GenericTreeNode{
		GenericTreeNode left;
		GenericTreeNode right;
		GenericTreeNode eq;
		String data;
		GenericTreeNode(String data){
			this.data=data;
		}
	}
}