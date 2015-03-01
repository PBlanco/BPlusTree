import java.util.AbstractMap;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;
	private LeafNode<K,T> lNode;

//	TODO: make sure to handle cases where the num might not be in the list and causes it to go out of bounds of array
	private T searchHelper(K key, Node<K,T> node){
		if(node == null) return null;
		
		if (node.isLeafNode){
			LeafNode<K, T> lnode = (LeafNode<K,T>)node;
			//iterate through keys to find a match
			for (int i=0; i < lnode.keys.size(); i++){
				K fetchedKey = lnode.keys.get(i);
				if (key.compareTo(fetchedKey) == 0){
					return lnode.values.get(i);
				// Break if the key is smaller then fetched (this means no matches)
				} else if (key.compareTo(fetchedKey) > 0 ){ 
					return null;
				}
			}
		} else {
			IndexNode<K,T> inode = (IndexNode<K,T>)node;
			for (int i=0; i< inode.keys.size(); i++){
				K fetchedKey = inode.keys.get(i);
				//if key is less than fetched then return fetched's left child
				if (key.compareTo(fetchedKey) < 0){
					return searchHelper(key, inode.children.get(i));
				//if your on last key in node (and larger) traverse right child
				}else if (i == inode.keys.size()-1) {
					return searchHelper(key, inode.children.get(i+1));
				}
			}
		}
		return null;
	}
	
	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		return this.searchHelper(key, this.root);
	}
	
	private void insertHelper(K key, T value, Node<K,T> node, Node<K,T>parentNode ){
		if (node.isLeafNode){
			LeafNode<K, T> leafNode = (LeafNode<K, T>) node;
			leafNode.insertSorted(key, value);
			//check if this overflowed node
			if(leafNode.isOverflowed()){
				Entry<K, Node<K,T>> splitObj = this.splitLeafNode(leafNode);
				K splitKey = splitObj.getKey();
				Node<K,T> rightLeaf = splitObj.getValue();
				
				//make left side split node leaf
				//add that new split key value and children pointers to parent index (if one exists)
				
				//is the parent index overflowed? then split it and repeat
			}
			return;
			
		} else {
			
		}
		
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		//Lazy instantiation of root node
		if (this.root == null){
			LeafNode<K,T> lNode = new LeafNode(key, value);
			this.root = lNode;
			return;
		}
		this.insertHelper(key, value, this.root, null);
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

}
