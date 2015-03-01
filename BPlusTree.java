import java.util.AbstractMap;
import java.util.ArrayList;
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

	
	private Entry<K, Node<K,T>> insertHelper(K key, T value, Node<K,T> node){
		if (node.isLeafNode){
			LeafNode<K, T> leafNode = (LeafNode<K, T>) node;
			leafNode.insertSorted(key, value);
			//check if this overflowed node
			if(leafNode.isOverflowed()){
				//there was an overflow so return split leaf
				Entry<K, Node<K,T>> overflowed = this.splitLeafNode(leafNode);
				//assigned the modified leafNode to node
				node = leafNode;
				return overflowed;
			}
			//return null if insert is successful
			return null;
			
		} else {
			IndexNode<K,T> inode = (IndexNode<K,T>)node;
			for (int i=0; i< inode.keys.size(); i++){
				K fetchedKey = inode.keys.get(i);
				Entry<K, Node<K, T>> overflowed = null;
				
				//if key is less than fetched then return fetched's left child
				if (key.compareTo(fetchedKey) < 0){
					overflowed = this.insertHelper(key, value, inode.children.get(i));
				//if you're on last key in node (and larger) traverse right child
				}else if (i == inode.keys.size()-1) {
					overflowed = this.insertHelper(key, value, inode.children.get(i+1));
				}
				
				//value was inserted, check if it caused an overflow
				if(overflowed != null){
					//insert the new pointer into the index after the split pointer who is at index i
					inode.insertSorted(overflowed, i+1);;
					//check if it has caused an overflow of the index
					if(inode.isOverflowed()){
						Entry<K, Node<K, T>> indexOverflow =  this.splitIndexNode(inode);
						node = inode;
						return indexOverflow;
					}
				}
			}
		}
		return null;
		
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
		Entry<K, Node<K,T>> overflowed = this.insertHelper(key, value, this.root);
		
		//overflow made it to root
		if (overflowed != null){
			IndexNode<K,T> rootIndex = (IndexNode<K, T>) this.root;
			//create a new root with key as the split key, 
			//left child as the modified right split of root,
			//and right child as the left split of root node
			IndexNode<K,T> newRoot = new IndexNode<K,T>(overflowed.getKey(), rootIndex, overflowed.getValue());
			this.root = newRoot;
		}
		
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<splitingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
		int middleIndex = leaf.values.size()/2;
		//get pointer (key we split on) to pass up
		K pointer = leaf.keys.remove(middleIndex);
		LeafNode<K,T> leftLeafNode = new LeafNode<K,T>(pointer, leaf.values.remove(middleIndex));
		int size =  leaf.values.size();
		
		//add values to right list while removing from leaf
		for (int i = middleIndex; i <size; i++){
			leftLeafNode.insertSorted(leaf.keys.get(middleIndex), leaf.values.get(middleIndex));
		}
		//create entry object
		Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(pointer, leftLeafNode);
		return entry;
	}
 
	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<splitingKey, RightNode>
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
