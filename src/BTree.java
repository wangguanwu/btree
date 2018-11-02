class BTNode implements Comparable<BTNode>{
    int n ;
    private int key;
    private BTNode []childNodes;
    public BTNode( int key){
        childNodes = new BTNode[n];
        n = 0 ;
    }
    public int getChildNums(){
        return this.n;
    }
    @Override
    public int compareTo(BTNode o) {
        return this.key - o.key;
    }
}
public class BTree {

}
