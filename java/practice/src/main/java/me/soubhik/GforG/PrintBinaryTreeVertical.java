package me.soubhik.GforG;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by sb8 on 10/10/18.
 * from https://practice.geeksforgeeks.org/problems/print-a-binary-tree-in-vertical-order/1
 */
public class PrintBinaryTreeVertical {
    private static class Node {
        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    private static class GfG {
        private static class Pair {
            int min;
            int max;
        }

        private static Pair verticalOrderRecursive(Node node, int coordinate, Map<Integer, List<Integer>> items) {
            int min = coordinate;
            int max = coordinate;
            if (!items.containsKey(coordinate)) {
                items.put(coordinate, new ArrayList<Integer>());
            }
            items.get(coordinate).add(node.data);

            if (node.left != null) {
                Pair leftPair = verticalOrderRecursive(node.left, coordinate-1, items);
                min = (leftPair.min < min) ? leftPair.min : min;
                max = (leftPair.max > max) ? leftPair.max : max;
            }

            if (node.right != null) {
                Pair rightPair = verticalOrderRecursive(node.right, coordinate+1, items);
                min = (rightPair.min < min) ? rightPair.min : min;
                max = (rightPair.max > max) ? rightPair.max : max;
            }

            Pair pair = new Pair();
            pair.min = min;
            pair.max = max;
            return pair;
        }

        private static class NodeCoordinate {
            Node node;
            int coordinate;
        }

        private static Pair verticalOrderIterative(Node root, Map<Integer, List<Integer>> items) {
            Deque<NodeCoordinate> stack = new LinkedList<>();
            NodeCoordinate rootInfo = new NodeCoordinate();
            rootInfo.node = root;
            rootInfo.coordinate = 0;
            stack.push(rootInfo);
            int min = 0;
            int max = 0;

            while (!stack.isEmpty()) {
                NodeCoordinate nodeCoordinate = stack.pop();
                Node node = nodeCoordinate.node;
                int coordinate = nodeCoordinate.coordinate;

                min = (coordinate < min) ? coordinate : min;
                max = (coordinate > max) ? coordinate : max;

                if (!items.containsKey(coordinate)) {
                    items.put(coordinate, new ArrayList<Integer>());
                }
                items.get(coordinate).add(node.data);

                if (node.right != null) {
                    NodeCoordinate nodeCoordinateRight = new NodeCoordinate();
                    nodeCoordinateRight.node = node.right;
                    nodeCoordinateRight.coordinate = coordinate + 1;
                    stack.push(nodeCoordinateRight);
                }

                if (node.left != null) {
                    NodeCoordinate nodeCoordinateLeft = new NodeCoordinate();
                    nodeCoordinateLeft.node = node.left;
                    nodeCoordinateLeft.coordinate = coordinate - 1;
                    stack.push(nodeCoordinateLeft);
                }
            }

            Pair pair = new Pair();
            pair.min = min;
            pair.max = max;
            return pair;
        }

        public static void verticalOrder(Node root)
        {
            // add your code
            Map<Integer, List<Integer>> items = new HashMap<>();
            Pair pair = verticalOrderIterative(root, items);
            //Pair pair = verticalOrderRecursive(root, 0, items);
            for (int i = pair.min; i <= pair.max; i++) {
                for (int data: items.get(i)) {
                    System.out.print(data + " ");
                }
            }
            //System.out.print("\n");
        }
    }

    public static void main (String[] args)
    {
        Scanner sc=new Scanner(System.in);
        int t=sc.nextInt();

        while(t-->0){
            int n=sc.nextInt();
            if(n==0){
                System.out.println(0);
                continue;
            }
            Node root = null;
            for(int i=0;i<n;i++){
                int a=sc.nextInt();
                int a1=sc.nextInt();
                char lr=sc.next().charAt(0);
                if(i==0){
                    root=new Node(a);
                    switch(lr){
                        case 'L':root.left=new Node(a1);
                            break;
                        case 'R':root.right=new Node(a1);
                            break;
                    }
                }
                else{
                    insert(root,a,a1,lr);
                }
            }

            GfG g=new GfG();
            g.verticalOrder(root);

        }
    }
    public static void insert(Node root,int a,int a1,char lr){
        if(root==null){
            return;
        }
        if(root.data==a){
            switch(lr){
                case 'L':root.left=new Node(a1);
                    break;
                case 'R':root.right=new Node(a1);
                    break;
            }
            return;
        }
        insert(root.left,a,a1,lr);
        insert(root.right,a,a1,lr);
    }
}
