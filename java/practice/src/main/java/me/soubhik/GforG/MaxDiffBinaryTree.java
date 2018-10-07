package me.soubhik.GforG;

import java.util.Scanner;

/**
 * Created by sb8 on 10/7/18.
 * problem and driver code are from: https://practice.geeksforgeeks.org/problems/maximum-difference-between-node-and-its-ancestor/1
 */
public class MaxDiffBinaryTree {
    private static class Node {
        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    /*  Please note that it's Function problem i.e.
        you need to write your solution in the form of Function(s) only.
        Driver Code to call/invoke your function is mentioned above.*/

    //User function Template for Java
    //Back-end complete function Template for Java
    private static class GfG {
        private static class Candidate {
            int min;
            int max;
            int smallest;

            Candidate(int min, int max, int smallest) {
                this.min = min;
                this.max = max;
                this.smallest = smallest;
            }
        }

        int maxDiff(Node root)
        {
            //your code here
            Candidate candidate = minMax(root);

            return (candidate.max - candidate.min);
        }

        private Candidate minMax(Node node) {
            if ((node.left == null) && (node.right == null)) {
                //return new Candidate(node.data, node.data, node.data);
                return null;
            }

            int smallest = node.data;

            Candidate candidateLeft = null;
            if (node.left != null) {
                candidateLeft = minMax(node.left);
                if (candidateLeft == null) {
                    candidateLeft = new Candidate(node.left.data, node.data, node.left.data);
                }
                smallest = (candidateLeft.smallest < smallest) ?
                        candidateLeft.smallest :
                        smallest;
            }

            Candidate candidateRight = null;
            if (node.right != null) {
                candidateRight = minMax(node.right);
                if (candidateRight == null) {
                    candidateRight = new Candidate(node.right.data, node.data, node.right.data);
                }
                smallest = (candidateRight.smallest < smallest) ?
                        candidateRight.smallest :
                        smallest;
            }

            candidateLeft = expandCandidate(candidateLeft, candidateRight, node);
            candidateRight = expandCandidate(candidateRight, candidateLeft, node);

            Candidate candidate = findMax(candidateLeft, candidateRight);

            if ((candidateLeft != null) && (node.data - candidateLeft.smallest) > (candidate.max - candidate.min)) {
                candidate.max = node.data;
                candidate.min = candidateLeft.smallest;
            }
            if ((candidateRight != null) && (node.data - candidateRight.smallest) > (candidate.max - candidate.min)) {
                candidate.max = node.data;
                candidate.min = candidateRight.smallest;
            }

            candidate.smallest = smallest;

            return candidate;
        }

        private Candidate expandCandidate(Candidate candidate, Candidate other, Node node) {
            if (candidate != null) {
                if (node.data < candidate.min) {
                /*
                candidate.min = node.data;
                if (other != null) {
                    if (candidate.max < other.max) {
                        candidate.max = other.max;
                    }
                }
                */
                } else if (node.data > candidate.max) {
                    candidate.max = node.data;
                    if (other != null) {
                        if (candidate.min > other.min) {
                            candidate.max = other.max;
                        }
                    }
                }
            }

            return candidate;
        }

        private Candidate findMax(Candidate candidate1, Candidate candidate2) {
            if (candidate1 == null) {
                return candidate2;
            }
            if (candidate2 == null) {
                return candidate1;
            }
            //both candidate1 and candidate2 are not null
            if ((candidate1.max - candidate1.min) > (candidate2.max - candidate2.min)) {
                return candidate1;
            } else if ((candidate1.max - candidate1.min) < (candidate2.max - candidate2.min)) {
                return candidate2;
            } else if (candidate1.min < candidate2.min) {
                return candidate1;
            }

            return candidate2;
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
            System.out.println(g.maxDiff(root));

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
