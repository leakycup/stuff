package me.soubhik;

public class MeetingLinkedLists<T> {
    private MyList<T> first;
    private MyList<T> second;

     public static class Node<T> {
        Node<T> next;
        T item;
    }

     public static class MyList<T> {
        Node<T> head;
    }

    public MeetingLinkedLists(MyList<T> first, MyList<T> second) {
        this.first = first;
        this.second = second;
    }

    public Node<T> meetingPoint() {
        int len1 = length(first); //length of first list

        int len2 = length(second); //length of the second list

        //let x and y be number of nodes upto and including the meeting point,
        // in first and second lists respectively. let z be the remaining nodes.
        // len1 = x + z
        // len2 = y + z
        // len1 + len2 = (x + y) + 2*z
        //if all links of first are reversed, a traversal from head of second list will end at the head of the first.
        // number of nodes traversed is len3 = (x + y -1).
        // thus, z = (len1 + len2 - len3 -1)/2
        // thus, x = len1 - z and y = len2 - z

        //reverse first list
        Node<T> newHead = reverse(first.head);

        int len3 = length(second);

        int nodesAfterMeetingPoint = (len1 + len2 - len3 - 1)/2;
        int meetingPointInSecondList = len2 - nodesAfterMeetingPoint;

        reverse(newHead);

        Node<T> n = second.head;
        for (int i = 1; i < meetingPointInSecondList; i++) {
            n = n.next;
        }

        return n;
    }

    private Node<T> reverse(Node<T> head) {
        if (head == null) {
            return head;
        }

        Node<T> one = head;
        Node<T> two = one.next;
        one.next = null;
        while (two != null) {
            Node<T> three = two.next;
            two.next = one;
            one = two;
            two = three;
        }

        return one;
    }

    private int length(MyList<T> list) {
        int len = 0;
        Node<T> n = list.head;
        while (n != null) {
            len++;
            n = n.next;
        }

        return len;
    }

    public static void main(String[] args) {
        MyList<Integer> list1 = new MyList<Integer>();
        Node<Integer> n = new Node<Integer>();
        n.item = 1;
        list1.head = n;
        Node<Integer> meetingPoint = null;
        for (int i=2; i <= 8; i++) {
            n.next = new Node<Integer>();
            n = n.next;
            n.item = i;
            if (i == 5) {
                meetingPoint = n;
            }
        }
        n.next = null;

        MyList<Integer> list2 = new MyList<Integer>();
        n = new Node<Integer>();
        n.item = 10;
        list2.head = n;
        for (int i=2; i <= 3; i++) {
            n.next = new Node<Integer>();
            n = n.next;
            n.item = i*10;
        }
        n.next = meetingPoint;

        System.out.println("Meeting point : " + new MeetingLinkedLists<Integer>(list1, list2).meetingPoint().item);

        n.next = list1.head;
        System.out.println("Meeting point : " + new MeetingLinkedLists<Integer>(list1, list2).meetingPoint().item);
    }
}