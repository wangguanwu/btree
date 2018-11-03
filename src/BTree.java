class DiskValue implements Comparable<DiskValue>{ //磁盘数据
    private int key;
    private int value;

    @Override
    public int compareTo(DiskValue o) {
        return this.key - o.key;
    }

    public DiskValue() {
    }

    public DiskValue(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "DiskValue{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
class BTNode implements Comparable<BTNode>{
    int t ;//度数
    int n ; // key的数目 ， 孩子的节点数目 n + 1
    int cindex ;
    private DiskValue []keyNodes; // 关键字节点
    private BTNode parent ;//父母节点
    private BTNode []childNodesPtr; //子节点
    private boolean isLeaf ;//是否是叶子节点


    public DiskValue[] getKeyNodes() {
        return keyNodes;
    }

    public void setKeyNodes(DiskValue[] keyNodes) {
        this.keyNodes = keyNodes;
    }

    public BTNode getParent() {
        return parent;
    }

    public void setParent(BTNode parent) {
        this.parent = parent;
    }

    public BTNode[] getChildNodesPtr() {
        return childNodesPtr;
    }

    public void setChildNodesPtr(BTNode[] childNodesPtr) {
        this.childNodesPtr = childNodesPtr;
    }

    public BTNode(int key , int t){
        n = 0 ;
        this.t = t ;
        isLeaf =true ;
        this.childNodesPtr = new BTNode[2*t];
        this.keyNodes = new DiskValue[2*t-1];
        this.cindex =  0 ;
    }
    public BTNode(int t){
        this(0 , t);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int size(){
        return this.n;
    }
    public int getKeyCounts(){
        return this.n;
    }
    public int getChildCounts(){
        return this.n+1;
    }
    public DiskValue getNodeKey(int index){
        if(index < n){
            return this.keyNodes[index];
        }else{
            throw new IllegalArgumentException();
        }
    }
    public BTNode getChildItem(int index){
        if(index<this.getChildCounts()){
            return this.childNodesPtr[index];
        }else{
            throw new IllegalArgumentException();
        }
    }
    public void setKeyNode(int index , DiskValue key){
        if(index < n+1){
            this.keyNodes[index] = key ;
        }else{
            throw new IllegalArgumentException();
        }
    }
    public void addKey(DiskValue key){
        this.keyNodes[n++] = key ;
    }
    public void addChild(BTNode c){
        this.childNodesPtr[cindex++] = c ;
    }
    public int getChildNums() {
        return this.n;
    }
    public void setChildNode(int index , BTNode node){
        if(index < n){
            this.childNodesPtr[index] = node ;
        }else{
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int compareTo(BTNode o) {

        if(o.n > 0){
            if(this.n>0){
                return this.keyNodes[0].getKey() - o.keyNodes[o.n-1].getKey();
            }
        }else{
            return 1;
        }
        return 0 ;
    }
}
public class BTree {
    private BTNode root ;
    private  int t ;//最小度数，例如t==2时，关键字个数可以为2-3-4,称为2-3-4B树
    public void BTreeCreate(int t) {
        if (root == null){
            this.t = t;
             root = new BTNode(t);
      }
    }
    public void BTreeCreate(){
        BTreeCreate(2);
    }
    public void  btreeSplitChild(BTNode x , int i){
        BTNode z = new BTNode(t);
        z.setLeaf(false);
        BTNode y = x.getChildItem(i); //找到x节点第i+1个孩子指针
        z.n = t-1; //修改z的孩子个数
        for(int j = 0 ; j < t-1 ; j++){
            z.addKey(y.getNodeKey(j));
        }
        if(y.isLeaf()==false){
            for(int j = 0 ; j < t ;j++){
               z.addChild(y.getChildItem(j+t));
            }
        }
        y.n = t-1 ;
        for(int j = x.n ;  j>i+1; j--){
            x.setChildNode( j ,x.getChildItem(j-1));
        }
        x.setChildNode(i , z);
        for(int j = x.n-1 ; j> i ; j--){
            x.setKeyNode(j , x.getNodeKey(j-1));
        }
        x.setKeyNode(i , y.getNodeKey(i));
        x.n++;
    }
    private BTree(){

    }
    public void btreeInsert(DiskValue k ){
        BTNode r = this.root ;
        if(r.n == 2*this.t-1){
            BTNode btNode = new BTNode(this.t);
            this.root = btNode;
            this.root.setLeaf(false);
            this.root.setChildNode(0 , r);
            this.btreeSplitChild(this.root , 0);//根节点没有父母节点，但是可以假定根节点默认是父母节点的下标为0的
                                                    // 满子节点
            this.btreeInsertNonfull(this.root , k);
        }else{
            this.btreeInsertNonfull(this.root , k);
        }
    }
    public void btreeInsertNonfull(BTNode x , DiskValue k ){//将关键字插入,核心部分
        int i = x.n-1 ;
        if(x.isLeaf()){
            while( i >0 && k.getKey() < x.getNodeKey(i).getKey()){ // x.key(i+1) < x.key(i)
                x.setKeyNode(i , x.getNodeKey(i-1));
                i--;
            }
            x.setKeyNode(i , k);
            x.n++ ;
        }else{
            while( i>= 0 && k.getKey() < x.getNodeKey(i).getKey()){
                i--;
            }
            i = i + 1 ;
            if(x.getChildItem(i).getChildCounts() == 2*t-1){
                this.btreeSplitChild(x , i );
                if(k.compareTo(x.getNodeKey(i))>0){//经过分裂，x.key(i)是新移上来的值，需要与key及进行比较，
                                                    // 确定向哪个子树下移
                    i = i+1;
                }
                btreeInsertNonfull(x.getChildItem(i) , k); //尾递归,可以通过while循环实现
            }
        }

    }
    public DiskValue btreeSearch(BTNode x , int k ){ // b树查找，可以看到时间复杂度非常低O(tlogt(h))
        int i = 0 ;
        while( i < x.n && k > x.getNodeKey(i).getKey()){
            i++;
        }
        if(i < x.n && k== x.getNodeKey(i).getKey()){
            return x.getNodeKey(i);
        }else{
            if(x.isLeaf()){
                return null ;
            }else{
                btreeSearch(x.getChildItem(i),k);
            }
        }
        return null ;
    }
    public static BTree getInstance(){
        BTree tree  =new BTree();
        tree.BTreeCreate(4);
        return tree ;
    }
    public void traverse(BTNode x){
        int i = 0 ;
        while(i < x.n){
            System.out.print(x.getNodeKey(i).getKey()+ " ");
        }
        System.out.println();
        if(x.isLeaf()){
            return ;
        }
        for(int j = 0 ; j <x.n+1;j++){
            traverse(x.getChildItem(j));
        }
    }
    public void traverse(){
        this.traverse(this.root);
    }
    public static void main(String args){
        BTree bTree = BTree.getInstance();
        bTree.BTreeCreate();
        bTree.btreeInsert(new DiskValue(10,10));
        bTree.btreeInsert(new DiskValue(20,20));
        bTree.traverse();

    }

}
