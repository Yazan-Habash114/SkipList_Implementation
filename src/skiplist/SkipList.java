package skiplist;

import java.util.Random;
import java.util.Scanner;

class Node {
        // Attributes
        public int key;
        public Node prev;
        public Node next;
        public Node above;
        public Node below;
        
       // Constructor
        public Node(int key){
            this.key = key;
            this.prev = null;
            this.next = null;
            this.above = null;
            this.below = null;
        }
}

public class SkipList {
        public static final int POSITIVE_INFINITY = 100000;
        public static final int NEGATIVE_INFINITY = -100000;
        
        private Node head, tail;
        private int size, maxLevel;

        public SkipList() {
            Node neg_infinity = new Node(NEGATIVE_INFINITY);
            Node pos_infinity = new Node(POSITIVE_INFINITY);
            
            neg_infinity.next = pos_infinity;
            pos_infinity.prev = neg_infinity; 
            
            this.head = neg_infinity;
            this.tail = pos_infinity;
            this.maxLevel = 1;  // Firstly use one level
            this.size = 0;  // Size of elements but infinity = 0
        }
        
        private int flipCoin() {
            return new Random().nextInt() % 2;
        }
        
        private Node determinePosition(int key) {
        Node walk = this.head;
        
        // Starting from head
        for (int n = 1; n <= this.maxLevel; ++n) {
            // If I can go right then go
            while (walk.next.key <= key && walk.next.key != this.POSITIVE_INFINITY)
                walk = walk.next;
            
            if (walk.below == null)     // Cannot go anymore
                break;
            else
                walk = walk.below; // If there is a below then go below
        }
        return walk;
    }
        
    public boolean searchNode(int key) {
        Node walk = this.head;
        // Starting from head
        for (int n = 1; n <= this.maxLevel; ++n) {
            // If I can go right then go
            while (walk.next.key <= key && walk.next.key != this.POSITIVE_INFINITY)
                walk = walk.next;
            
            if(walk.key == key)
                return true;
            
            if (walk.below == null)     // Cannot go anymore
                break;
            else
                walk = walk.below; // If there is a below then go below
        }
        return false;
    }
        
    public void insert(int key) {
        Node newNode = new Node(key);
        Node thisNode = determinePosition(key);
        int currentLevel = 0;
        
        // If the node does not exist before in the skip-list
        if (thisNode.key != key) {
            newNode.prev = thisNode;
            newNode.next = thisNode.next;
            thisNode.next = newNode;
            newNode.next.prev = newNode;
            
            currentLevel++; 
            while (flipCoin() == 1) {
                // When we flip coin and build new level, if level > max current level => build new express
                if (currentLevel >= maxLevel)
                    buildExpress();
                
                // We continuously go previou untill we access node that have above
                while (thisNode.above == null)
                    thisNode = thisNode.prev;
                // Go Above
                thisNode = thisNode.above;

                Node copyNode = new Node(key);  // Build copy node
                copyNode.prev = thisNode;
                copyNode.next = thisNode.next;
                thisNode.next.prev = copyNode;
                thisNode.next = copyNode;
                copyNode.below = newNode;
                newNode.above = copyNode;
                
                newNode = copyNode;  // Apply same steps on the above created node
                currentLevel++;
            }
        }
        else
            System.out.println("Already added");
    }
    
    public void delete(int key) {
        Node nodeToBeDeleted = determinePosition(key);
        if (nodeToBeDeleted.key == key) {
            while (nodeToBeDeleted != null) {  // Delete this node and connect nodes beside it
                nodeToBeDeleted.prev.next = nodeToBeDeleted.next;
                nodeToBeDeleted.next.prev = nodeToBeDeleted.prev;
                nodeToBeDeleted = nodeToBeDeleted.above;
            }
            System.out.println(key + " deleted");
        } else {
            System.out.println(key + " integer not found - delete not successful");
        }
    }
        
    public void printSkipList() {
        Node walkingNode = this.head;
        System.out.print("[");
        // Go to buttom
        while(walkingNode.below != null)
            walkingNode = walkingNode.below;
        // Go next
        while(walkingNode.next.key != this.tail.key) {
            walkingNode = walkingNode.next;
            System.out.print(walkingNode.key);
            if(walkingNode.next.key != this.tail.key)
                System.out.print(",");
        }
        System.out.println("]");
    }
    
    public int max() {
        Node walk = this.tail;
        while(walk.below != null)
            walk = walk.below;
        if(walk.prev.key != NEGATIVE_INFINITY)
            return walk.prev.key;
        return walk.key;
    }
    
    public int min() {
        Node walk = this.head;
        while(walk.below != null)
            walk = walk.below;
        if(walk.next.key != POSITIVE_INFINITY)
            return walk.next.key;
        return walk.key;
    }
        
    private void buildExpress() {
        Node newNegInf = new Node(NEGATIVE_INFINITY);
        Node newPosInf = new Node(POSITIVE_INFINITY);

        newPosInf.below = this.tail;
        newPosInf.prev = newNegInf;
        newNegInf.below = this.head;
        newNegInf.next = newPosInf;
        this.head.above = newNegInf;
        this.tail.above = newPosInf;

        this.head = newNegInf;
        this.tail = newPosInf;
        this.maxLevel++;
     }
 
}

class Main {
    public static void main(String [] args) {
        Scanner s = new Scanner(System.in);
        int numberOfInsertions, totalCommands;
        numberOfInsertions = s.nextInt();
        totalCommands = s.nextInt();
        String [] searchResults = new String[totalCommands];
        
        String [] commands = new String[totalCommands];
        int [] keys = new int[totalCommands];
        
        for(int m=0; m<totalCommands; ++m) {
            String line;
            line = new Scanner(System.in).nextLine();
            String [] let = line.split(" ");
            String command = let[1];
            int key = Integer.parseInt(let[3]);
            commands[m] = command;
            keys[m] = key;
        }
        for(int i=0; i<totalCommands; i++)
            System.out.println(commands[i] + "  " + keys[i]);
        
        SkipList skipList = new SkipList();
        for(int n=0; n<totalCommands; ++n)
            if(commands[n].equals("i"))
                skipList.insert(keys[n]);
            else if(commands[n].equals("d"))
                skipList.delete(keys[n]);
            else if(commands[n].equals("s")) {
                boolean exist = skipList.searchNode(keys[n]);
                if(exist)
                    System.out.println("[Yes]");
                else
                    System.out.println("[No]");
            }
        
        if(skipList.max() != SkipList.POSITIVE_INFINITY)
            System.out.println("[" + skipList.max() + "]");
        else
            System.out.println("[]");
        if(skipList.min() != SkipList.NEGATIVE_INFINITY)
            System.out.println("[" + skipList.min() + "]");
        else
            System.out.println("[]");
        
        skipList.printSkipList();
        System.gc();
        System.exit(0);
    }
}