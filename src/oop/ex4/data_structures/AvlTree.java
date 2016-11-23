package oop.ex4.data_structures;

import java.lang.Iterable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class represents AvlTree with all of his methods (delete, insert, contains and more)
 * I used nested class design: The AVLTree is the outer class and Node is the inner class.
 */
public class AvlTree implements Iterable<Integer> {

    // properties of the AVL Tree

    private Node root = null;
    private int size;
    private static final int NOT_FOUND = -1;
    private static final int UNBALANCED_TREE = 2;




    // static inner class of Node in AVL Tree
    private class Node {
        private int key;
        private int balance;
        private Node left, right, parent;

        Node(int k, Node p) {
            key = k;
            parent = p;
        }
    }

    /** constructors */


    /** The default constructor */
    public AvlTree(){
        root = null;
        size = 0;
    }

    /** A constructor that builds a new AVL tree containing all unique values in the input array.
     * @param data - the values to add to tree */
    public AvlTree(int [] data){
        /** 1. creating an empty tree */
        root = null;
        /** 2. adding unique elements one by one from array */
        for (int value: data){
            this.add(value);
        }
    }

    /** A copy constructor that creates a deep copy of the given AvlTree. the new tree contains all the values
     * of the given tree, but not necessarily in the same structure.
     * @param avlTree - an AVL tree. */
    public AvlTree(AvlTree avlTree){
        this();
        // traverse of all of the keys in the tree and add a new node with this key to the new avl tree.
        for (Integer key: avlTree){
            add(key);
        }
    }



    /** methods */


    /**
     * This method calculate the balance for each node, balance is the difference between the height
     * of the left subtree of the node and the right subtree
     * @param nodes - list of nodes params
     */
    private void setBalance(Node... nodes) {
        for (Node n : nodes)
            n.balance = height(n.right) - height(n.left);
    }


    /** Add a new node with the given key to the tree.
     * @param key - the value of the new node to add.
     * @return - true if the value to add is not already in the tree and it was successfully added,
     * false otherwise. */
    public boolean add(int key) {
        if (root == null)
            root = new Node(key, null);
        else {
            Node n = root;
            Node parent;
            while (true) {
                // if there is a node with this key so return false (avoid duplication)
                if (n.key == key)
                    return false;

                parent = n;

                // if the key of node is greater than the key we want to add we will go left to lower keys
                // (As we know - In BST the left subtree has lower keys than the right subtree)
                boolean goLeft = n.key > key;
                n = goLeft ? n.left : n.right;

                // if n == null we dove in the tree till the leaf level - now it is the time to create
                // new node with this key and attach him to the tree.
                if (n == null) {
                    // create this node as left child if goLeft is true, otherwise, as right child.
                    if (goLeft) {
                        parent.left = new Node(key, parent);
                    } else {
                        parent.right = new Node(key, parent);
                    }
                    // check for violation as a result of addition of a new node (Possible Violations:
                    // "LL", "RR", "LR", "RL"
                    rebalance(parent);
                    break;
                }
                // if n is not null we will go to another iteration of the while.
            }
        }
        // increase the size data member of the tree by one and return true
        size ++;
        return true;
    }



    /**
     * Rotate binary tree node with right child (rotate left)
     * For AVL trees, this is a single rotation for case "RR"
     * Update heights, then return new root.
     * @param pivot -  Root of tree we are rotating.
     * @return New root
     */
    private Node rotateLeft(Node pivot) {

        Node newRoot = pivot.right;
        newRoot.parent = pivot.parent;
        pivot.right = newRoot.left;

        if (pivot.right != null)
            pivot.right.parent = pivot;

        newRoot.left = pivot;
        pivot.parent = newRoot;

        if (newRoot.parent != null) {
            if (newRoot.parent.right == pivot) {
                newRoot.parent.right = newRoot;
            } else {
                newRoot.parent.left = newRoot;
            }
        }

        // update the new balance for pivot and newRoot as a result of the rotation
        setBalance(pivot, newRoot);

        return newRoot;
    }

    /**
     * Rotate binary tree node with left child (rotate right)
     * For AVL trees, this is a single rotation for case "LL"
     * Update heights, then return new root.
     * @param pivot -  Root of tree we are rotating.
     * @return New root
     */
    private Node rotateRight(Node pivot) {

        Node newRoot = pivot.left;
        newRoot.parent = pivot.parent;
        pivot.left = newRoot.right;

        if (pivot.left != null)
            pivot.left.parent = pivot;

        newRoot.right = pivot;
        pivot.parent = newRoot;

        if (newRoot.parent != null) {
            if (newRoot.parent.left == pivot) {
                newRoot.parent.left = newRoot;
            } else {
                newRoot.parent.right = newRoot;
            }
        }

        // update the new balance for pivot and newRoot as a result of the rotation
        setBalance(pivot, newRoot);

        return newRoot;
    }

    /**
     * this method check the balance as a result of actions such as add new node or delete node
     * from the Avl Tree.
     * Doing so by calling the suitable rotations methods.
     * @param n - node to check is balance
     */
    private void rebalance(Node n) {
        setBalance(n);

        // call rotate right if left is heavier than right by more than two which is a case of UNBALANCED_TREE
        if (n.balance == UNBALANCED_TREE * - 1) {
            if (height(n.left.left) >= height(n.left.right))
                n = rotateRight(n);
            else
                n = rotateLeftThenRight(n);

        }
        // call rotate left if right is heavier than left by more than two which is a case of UNBALANCED_TREE
        else if (n.balance == UNBALANCED_TREE) {
            if (height(n.right.right) >= height(n.right.left))
                n = rotateLeft(n);
            else
                n = rotateRightThenLeft(n);
        }

        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }

    /**
     * This method calculates the height of a node in AvlTree/
     * @param n - The node
     * @return - Height of the node
     */
    private int height(Node n) {
        if (n == null)
            return -1;
        return 1 + Math.max(height(n.left), height(n.right));
    }

    /**
     * Double Rotating - first left and then right.
     * @param n - The root node
     * @return - The new root node after rotations
     */
    private Node rotateLeftThenRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }

    /**
     * Double Rotating - first right and then left.
     * @param n - The root node
     * @return - The new root node after rotations
     */
    private Node rotateRightThenLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }


    /** Check whether the tree contains the given input value
     * @param searchVal - the value to search for.
     * @return - the depth of the node (0 for the root) with the given value if it was found in
     * the tree, −1 otherwise. */

    public int contains(int searchVal){
        // call binarySearch to return the depth of the node if he found, otherwise, return -1
        return binarySearch(root, searchVal, 0);
    }

    /**
     * Help method for contains method.
     * This is a recursive method which search for the key in the AVL by binary search
     * @param node - root of the tree
     * @param key - the value we are searching for
     * @param depth - count the depth in the tree
     * @return - depth of the the node with this key in the AVL Tree.
     */
    private int binarySearch(Node node, int key, int depth) {

        // base case of recursion - return -1 if the root is null
        if (node == null){
            return NOT_FOUND;
        }

        // recursive part - call the method with left child and with right child

        // go to left side of the tree because the key we are searching for is lower than the node value
        else if (key < node.key) {
            return binarySearch(node.left, key, depth + 1);
        }
        // go to right side of the tree because the key we are searching for is greater than the node value
        else if (key > node.key) {
            return binarySearch(node.right, key, depth + 1);
        }
        // Can only reach here if node was found - return the depth
        return depth;
    }





    /**
     * This method is responsible to delete a node in the AVL Tree and take care of the violations
     * of balance if exist by call rotations methods.
     * @param toDelete - The value of a node we want to delete from AVL Tree.
     * @return - False if this value is not exist in the tree, True, If we deleted successfully.
     */
    public boolean delete(int toDelete) {
        boolean result = deleteNode(toDelete);
        // update the size data member of AVL Tree to be lower by one because we removed one node from tree
        // return true as we succeeded to delete the node from tree.
        if (result){
            size--;
            return true;
        }
        return false;
    }


    /**
     * This is a help method which is execute the delete of a node.
     * @param delKey - the key of the node we want to delete.
     */



    private boolean deleteNode(int delKey) {
        if (root == null)
            return false;
        Node n = root;
        Node parent = root;
        Node delNode = null;
        Node child = root;

        // run till no child
        while (child != null) {
            parent = n;
            n = child;
            // if the key of the node we want to delete is greater so we should go to right subtree
            child = delKey >= n.key ? n.right : n.left;
            // we found a node with this key
            if (delKey == n.key)
                delNode = n;
        }

        // if we found a node to delete
        if (delNode != null) {
            delNode.key = n.key;

            // child will be the left child of the node we want to delete if there is a left child
            child = n.left != null ? n.left : n.right;

            // if the root has key like the key of the node we want to delete root will be the child
            if (root.key == delKey) {
                root = child;
            }

            else {
                // left child of the parent of node we want to delete will get the child by doing so
                // we eliminate the node we want to delete (his parent points to his left child)
                if (parent.left == n) {
                    parent.left = child;
                }
                // the same on right child
                else {
                    parent.right = child;
                }
                // call re balance which will take care about rotations as we deleted a node and maybe
                // we should do rotations to save the tree as AVL tree.
                rebalance(parent);
            }
        }
        else{
            return false;
        }
        return true;

    }



    /** @return the number of nodes in the tree. */
    public int size(){
        return this.size;
    }



    /** @return - an iterator for the Avl Tree. The returned iterator iterates over the tree nodes
     * in an ascending order, and does NOT implement the remove() method. */
    public Iterator<Integer> iterator(){
        return new Iterator<Integer>() {

            // index is the number of times we want to call successor (index = 2 will sign that we want
            // the node "Successor of Successor" from the node with the minimum key
            private int index = 0;
            @Override
            // check if there is more elements

            public boolean hasNext() {
                return index < size();
            }
            @Override

             // if there are more elements to iterate returns the next element in line, else throws
             // "NoSuchElementException".

            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                // findKthSuccessor help method will call findSuccessor index times to give us the key
                // of the requested node.
                int element = findKthSuccessor(index).key;
                // increase the index so next time we will go to the successor node.
                index ++;
                return element;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * calling findSuccessor k times in order to return the node which is the Kth Successor from
     * the node with the minimum key.
     * @param k - the index I explained before
     * @return - the Kth Successor
     */
    private Node findKthSuccessor(int k){
        // minimum key is the anchor node.
        Node n;
        n = findMin(root);
        // k times call findSuccessor and let n be the new node
        for (int i = 0 ; i < k; i++){
            n = findSuccessor(n);
        }
        // in the end n is the requested Kth Successor node.
        return n;

    }

    /** static methods */

    /** Calculates the minimum number of nodes in an AVL tree of height h.
     * doing so by calculating series which is shifting of Fibonacci known series (by a closed formula)
     * Complexity: O(1)
     * @param h - the height of the tree (a non−negative number) in question.
     * @return the minimum number of nodes in an AVL tree of the given height. */
    public static int findMinNodes(int h){
        return (int)(Math.round(((Math.sqrt(5)+2)/
                Math.sqrt(5))*Math.pow((1+
                Math.sqrt(5))/2,h)-1));

    }


    /**
     * This method returns the successor of a given node - successor is the node with the minimum key
     * which is still greater than the given node key. (minimum of greater values)
     * @param node - node we want to find his sucessor
     * @return - Node which is a successor of given node
     */
    private static Node findSuccessor(Node node) {

        if (node == null)
            return null;

        // if we have right child so the greater keys of nodes in the tree are in the right subtree
        // and we want the minimum of them.
        if (node.right != null)
            return findMin(node.right);

        Node y = node.parent;
        Node x = node;
        while (y != null && x == y.right) {
            x = y;
            y = y.parent;
        }
        // as we traverse left up the tree we traverse smaller values
        // The first node on the right is the next larger number
        return y;
    }

    /**
     * Static method to find the minimum key in a AVL Tree which is a BST.
     * @param node - the root node
     * @return - the node with the minimum key in the tree
     */
    private static Node findMin(Node node){
        while (node.left != null){
            node = node.left;
        }
        return node;
    }


}


