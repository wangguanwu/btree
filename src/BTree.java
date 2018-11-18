class DiskValue implements Comparable<DiskValue> { //磁盘数据
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

class BTNode implements Comparable<BTNode> {
    int t;//度数
    int n; // key的数目 ， 孩子的节点数目 n + 1
    int cindex;
    private DiskValue[] keyNodes; // 关键字节点
    private BTNode parent;//父母节点
    private BTNode[] childNodesPtr; //子节点
    private boolean isLeaf;//是否是叶子节点


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

    public BTNode(int key, int t) {
        n = 0;
        this.t = t;
        isLeaf = true;
        this.childNodesPtr = new BTNode[2 * t];
        this.keyNodes = new DiskValue[2 * t - 1];
        this.cindex = 0;
    }

    public BTNode(int t) {
        this(0, t);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public int size() {
        return this.n;
    }

    public int getKeyCounts() {
        return this.n;
    }

    public int getChildCounts() {
        return this.n + 1;
    }

    public DiskValue getNodeKey(int index) {
        if (index < n) {
            return this.keyNodes[index];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public BTNode getChildItem(int index) {
        if (index < this.getChildCounts()) {
            return this.childNodesPtr[index];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setKeyNode(int index, DiskValue key) {
        if (index < n + 1) {
            this.keyNodes[index] = key;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void addKey(DiskValue key) {
        this.keyNodes[n++] = key;
    }

    public void addChild(BTNode c) {
        this.childNodesPtr[cindex++] = c;
    }

    public int getChildNums() {
        return this.n;
    }

    public void setChildNode(int index, BTNode node) {
        if (index < n) {
            this.childNodesPtr[index] = node;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int compareTo(BTNode o) {

        if (o.n > 0) {
            if (this.n > 0) {
                return this.keyNodes[0].getKey() - o.keyNodes[o.n - 1].getKey();
            }
        } else {
            return 1;
        }
        return 0;
    }
}

public class BTree {
    private BTNode root;
    private int t;//最小度数，例如t==2时，关键字个数可以为2-3-4,称为2-3-4B树

    public void BTreeCreate(int t) {
        if (root == null) {
            this.t = t;
            root = new BTNode(t);
        }
    }

    public void BTreeCreate() {
        BTreeCreate(2);
    }

    public void btreeSplitChild(BTNode x, int i) {
        BTNode z = new BTNode(t);
        z.setLeaf(false);
        BTNode y = x.getChildItem(i); //找到x节点第i+1个孩子指针
        z.n = t - 1; //修改z的孩子个数
        for (int j = 0; j < t - 1; j++) {
            z.addKey(y.getNodeKey(j));
        }
        if (y.isLeaf() == false) {
            for (int j = 0; j < t; j++) {
                z.addChild(y.getChildItem(j + t));
            }
        }
        y.n = t - 1;
        for (int j = x.n; j > i + 1; j--) {
            x.setChildNode(j, x.getChildItem(j - 1));
        }
        x.setChildNode(i, z);
        for (int j = x.n - 1; j > i; j--) {
            x.setKeyNode(j, x.getNodeKey(j - 1));
        }
        x.setKeyNode(i, y.getNodeKey(i));
        x.n++;
    }

    private BTree() {

    }

    public void btreeInsert(DiskValue k) {
        BTNode r = this.root;
        if (r.n == 2 * this.t - 1) {
            BTNode btNode = new BTNode(this.t);
            this.root = btNode;
            this.root.setLeaf(false);
            this.root.setChildNode(0, r);
            this.btreeSplitChild(this.root, 0);//根节点没有父母节点，但是可以假定根节点默认是父母节点的下标为0的
            // 满子节点
            this.btreeInsertNonfull(this.root, k);
        } else {
            this.btreeInsertNonfull(this.root, k);
        }
    }

    public void btreeInsertNonfull(BTNode x, DiskValue k) {//将关键字插入,核心部分
        int i = x.n - 1;
        if (x.isLeaf()) {
            while (i >= 0 && k.getKey() < x.getNodeKey(i).getKey()) { // x.key(i+1) < x.key(i)
                x.setKeyNode(i, x.getNodeKey(i - 1));
                i--;
            }
            x.setKeyNode(i + 1, k);
            x.n++;
        } else {
            while (i >= 0 && k.getKey() < x.getNodeKey(i).getKey()) {
                i--;
            }
            i = i + 1;
            if (x.getChildItem(i).getChildCounts() == 2 * t - 1) {
                this.btreeSplitChild(x, i);
                if (k.compareTo(x.getNodeKey(i)) > 0) {//经过分裂，x.key(i)是新移上来的值，需要与key及进行比较，
                    // 确定向哪个子树下移
                    i = i + 1;
                }
                btreeInsertNonfull(x.getChildItem(i), k); //尾递归,可以通过while循环实现
            }
        }

    }

    public DiskValue btreeSearch(BTNode x, int k) { // b树查找，可以看到时间复杂度非常低O(tlogt(h))
        int i = 0;
        while (i < x.n && k > x.getNodeKey(i).getKey()) {
            i++;
        }
        if (i < x.n && k == x.getNodeKey(i).getKey()) {
            return x.getNodeKey(i);
        } else {
            if (x.isLeaf()) {
                return null;
            } else {
                btreeSearch(x.getChildItem(i), k);
            }
        }
        return null;
    }

    public static BTree getInstance() {
        BTree tree = new BTree();
        tree.BTreeCreate(4);
        return tree;
    }

    public void btreeDelete(BTNode x, int key) {

        // if the key is in current node
        int i;
        if (x.getKeyCounts() < t) {
            throw new IllegalStateException("节点的关键字个数至少为t");
        }
        for (i = 0; i < x.getKeyCounts(); i++) { //to-do可以用二分法查找
            DiskValue bt = x.getNodeKey(i);
            if (bt.getKey() == key || bt.getKey() > key ) {

                break;
            }
        }
        if (i < x.getKeyCounts() && key ==x.getNodeKey(i).getKey() ) { // key is in current node
            if (x.isLeaf()) { // if current node is leaf ,we can delete the node directly
                for (int j = i; j < x.getKeyCounts() - 1; j++) {
                    x.setKeyNode(j, x.getNodeKey(j + 1));//x.key(i) = x.key(i+1);
                }
                x.n--;
            } else {
                BTNode previousNode = x.getChildItem(i);//Child node in front of the K node
                if (previousNode.getKeyCounts() >= t) { // previous node contains at least three keywords
                    DiskValue dv = previousNode.getNodeKey(previousNode.getKeyCounts() - 1);
                    x.setKeyNode(i, dv);//x.k = x.k'
                    btreeDelete(previousNode, dv.getKey());//recursively delete the k' key in the child node previous
                } else {


                    BTNode postNode = x.getChildItem(i + 1);
                    if (postNode.getKeyCounts() >= t) {
                        DiskValue dv = postNode.getNodeKey(postNode.getKeyCounts() - 1);
                        x.setKeyNode(i, dv);
                        btreeDelete(postNode, dv.getKey());
                    } else {
                        //append the k key word to the tail of k's child node y,and then append the z node to the
                        // tail of the x node ,lastly recursively delete the k  in the y node

                        //delete the k from the x node
                        DiskValue keyToDelete = x.getNodeKey(i);
                        // ajust the key word in the x node
                        for (int q = i; q < x.getKeyCounts() - 1; q++) {
                            x.setKeyNode(i, x.getNodeKey(i + 1));
                        }
                        // ajust  the childnode pointer in the x node
                        for (int q = i; q < x.getChildCounts() - 1; q++) {
                            x.setChildNode(i, x.getChildItem(i + 1));
                        }
                        // update the size of the x node
                        x.n--;
                        // current index of the y node
                        int curIndex = previousNode.n;
                        // update the size of the y node
                        previousNode.n++ ;
                        //apend the k  to be deleted  to the tail of the y node
                        previousNode.setKeyNode(curIndex, keyToDelete);
                        // calculate the size of the y node (= previous node )
                        int nLength = previousNode.getKeyCounts() + postNode.getKeyCounts()+1;
                        //merge the key of the postnode into the previousNode
                        int childIndex = previousNode.getChildCounts() -1 ;

                        // merge the y (left child) node and the z node (right child or named postNode)
                        for (int k = previousNode.getKeyCounts()-1, pindex = 0; k < nLength; k++, pindex++) {
                            previousNode.n++ ;
                            previousNode.setKeyNode(k, postNode.getNodeKey(pindex));
                        }
                        //merge the childnode of postnode into the previousNode

                        for(int q = childIndex ,z = 0 ; z< postNode.getChildCounts() ; z++){
                                previousNode.setChildNode(q , postNode.getChildItem(z));
                        }
                        //recursively delete the key node in childnode previouseNode
                        btreeDelete( previousNode , key);
                    }
                }
            }
        } else {// key isn't in current node
            // case a :如果x.ci只有t-1个关键字，.ci的左右孩子中某一个节点数至少为t，可以将x.ci的某个关键字移动到
            //x.ci中，孩子数至少为t的相邻兄弟节点必须将某个关键字移到x中，并把关键字的一个孩子节点移到x.ci中
            DiskValue dk = x.getNodeKey(i);
            BTNode left = null ,right = null;
            BTNode xci = x.getChildItem(i);
            if(i > 0 ){
                left = x.getChildItem(i-1);//x.ci的相邻左兄弟节点
            }
            if(i < x.getChildCounts()-1){
                right = x.getChildItem(i+1);//x.ci的相邻右兄弟节点
            }
            if(  x.getChildItem(i).getKeyCounts() == t-1 ){//if x.ci only has t-1 key words
                BTNode bigNode = left ,smallNode = right ;
                if(left!=null&&left.getKeyCounts() != t-1){
                    if(right!=null&&right.getKeyCounts() >= t){
                        bigNode = right;
                        smallNode= left ;
                    }else {
                        bigNode= null ;
                        smallNode= null ;
                    }
                }
                if( bigNode!=null){// brother node has at least t key words
                    // movedown a key of the x to  the x.ci
                    xci.n++;
                    if(bigNode == left){
                        //增加xci的个数
                        for(int q = xci.getKeyCounts() -1 ; q > 0 ; q--){
                            DiskValue da =xci.getNodeKey(q-1);
                            xci.setKeyNode(q , da);
                        }
                        //将x中的某个键移动到x.ci中
                        DiskValue tempK = x.getNodeKey(i-1);
                        xci.setKeyNode(0 , tempK);
                        //将兄弟节点的某个键移动到x中
                        x.setKeyNode(i-1  , bigNode.getNodeKey(bigNode.n-1));
                        // 将兄弟节点的孩子指针移动到x.c(i)中
                        xci.setChildNode(0 , bigNode.getChildItem(bigNode.n));
                        bigNode.n -- ;
                    }else{// 右兄弟节点的key个数至少是t
                        //将x的某个键放在xci中，放在数组后面
                        DiskValue tempK = x.getNodeKey(i);
                        xci.setKeyNode(xci.n-1 ,tempK );
                        //将右边兄弟孩子节点放到xci中
                        xci.setChildNode( xci.n , bigNode.getChildItem(0));
                        //将右边兄弟移到x中
                        DiskValue ctk = bigNode.getNodeKey(0);
                        x.setKeyNode(i , ctk);
                        //调整bigNode
                        for(int q = 0; q < bigNode.getKeyCounts()-1;q++){
                            bigNode.setKeyNode(q,bigNode.getNodeKey(q+1));
                            bigNode.setChildNode(q , bigNode.getChildItem(q+1));
                        }
                        bigNode.setChildNode(bigNode.n-1 , bigNode.getChildItem(bigNode.n));
                        bigNode.n--;
                    }
                }else{ //case 3  所有相邻的兄弟节点只有t-1个键，合并两个兄弟节点，并且将x中的某个键移动到新的节点中


                    if(left!=null&&right!=null&&left.getKeyCounts()==t-1 && right.getKeyCounts()==t-1){
                        if (x.getChildCounts() == 1) {
                            this.root = left ;
                        }
                        //merge the x.key(i) and left ,这是可以随意选择一个相邻兄弟节点合并的
                        xci.n++;
                        DiskValue moveDown = x.getNodeKey(i);
                        if(x.getKeyCounts()>1){
                            for(int q = i ; q < x.getKeyCounts()-1;q++){
                                x.setKeyNode(q , x.getNodeKey(q+1));
                                x.setChildNode(q , x.getChildItem(q+1));
                            }
                            x.setChildNode(x.n-1 , x.getChildItem(x.n));
                        }
                        xci.setKeyNode(xci.n -1 , moveDown);
                        int indexOfChild = xci.n ;
                        //merge the xci and the rightChild
                        int indexOfKey = xci.n ;
                        for(int f = 0 ; f < right.getKeyCounts() ;f++){
                            xci.n++;
                            xci.setKeyNode(indexOfKey ++, right.getNodeKey(f));
                            xci.setChildNode(indexOfChild ++, right.getChildItem(f));
                        }
                        xci.setChildNode(xci.n ,right.getChildItem(right.n));


                    }else {
                        DiskValue dvd = x.getNodeKey(i);
                        if (left != null && right == null && left.getKeyCounts() == t - 1) {
                            //merge the left child and x's key
                            if (x.getKeyCounts() == 1) {
                                this.root = left;
                            } else {
                                arrayStepback(i, x);
                            }
                            left.n++;
                            left.setKeyNode(left.n - 1, dvd);
                            aAppendToB(xci, 0, left, left.n);
                        } else if (left == null && right != null && right.getKeyCounts() == t - 1) {
                            if(x.getKeyCounts() == 1){
                                this.root =xci ;
                            }else{
                                arrayStepback(i , x );
                            }
                            xci.n++;
                            xci.setKeyNode(xci.n-1 , dvd);
                            aAppendToB(right , 0 , xci , xci.n);
                        }
                    }
                }
                if(x.getKeyCounts() == 1){// delete the root node becuase it just has only a key word
                    this.root = xci ;
                }
            }

        }
    }
    static void arrayStepback(int index , BTNode btNode ){//将数组向前移动
        for(int i = index ; i < btNode.getKeyCounts() -1 ;i++){
            btNode.setKeyNode(i ,btNode.getNodeKey(i+1) );
            btNode.setChildNode(i , btNode.getChildItem(i+1));
        }
        btNode.setChildNode(btNode.n-1 , btNode.getChildItem(btNode.n));
        btNode.n -- ;

    }
    static void aAppendToB(BTNode source ,int startPos1 , BTNode dest ,int startPos2){
        for(int i = startPos1 ,j = startPos2 ; i < source.getKeyCounts() ;i++,j++){
            dest.n++;
            dest.setKeyNode(j,source.getNodeKey(i));
            dest.setChildNode(j,source.getChildItem(i));
        }
        dest.setChildNode(dest.n , source.getChildItem(source.n));
    }

    public void btreeDelete(int key) {
        this.btreeDelete(this.root, key);

    }

    public void traverse(BTNode x) { // 遍历b树
        int i = 0;
        while (i < x.n) {
            System.out.print(x.getNodeKey(i).getKey() + " ");
            i++;
        }
        System.out.println();
        if (x.isLeaf()) {
            return;
        }
        for (int j = 0; j < x.n; j++) {
            traverse(x.getChildItem(j));
        }
    }

    public void traverse() {
        this.traverse(this.root);
    }

    public static void main(String args[]) {
        BTree bTree = BTree.getInstance();
        bTree.btreeInsert(new DiskValue(10, 10));
        bTree.btreeInsert(new DiskValue(20, 20));
        bTree.traverse();

    }

}
