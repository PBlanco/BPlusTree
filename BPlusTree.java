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
	private static final int REDISTRIBUTION = -1;

	private T searchHelper(K key, Node<K,T> node){
		if(node == null) return null;

		if (node.isLeafNode){
			LeafNode<K, T> lnode = (LeafNode<K,T>)node;
			//iterate through keys to find a match
			int keyindex = lnode.keys.indexOf(key);
			if (keyindex == -1) return null;
			return lnode.values.get(keyindex);

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
				Entry<K, Node<K,T>> overflow = this.splitLeafNode(leafNode);
				//assigned the modified leafNode to node
				node = leafNode;

				return overflow;
			}
			//return null if insert is successful
			return null;

		} else {
			IndexNode<K,T> inode = (IndexNode<K,T>)node;
			for (int i=0; i< inode.keys.size(); i++){
				K fetchedKey = inode.keys.get(i);
				Entry<K, Node<K, T>> overflow = null;

				//if key is less than fetched then return fetched's left child
				int index = 0;
				Boolean inserted = false; 
				if (key.compareTo(fetchedKey) < 0){
					overflow = this.insertHelper(key, value, inode.children.get(i));
					index = i;
					inserted = true;
				}else if (i == inode.keys.size()-1) {
					overflow = this.insertHelper(key, value, inode.children.get(i+1));
					index = i+1; //want to add it to the right if you went down right child
					inserted = true;
				} 

				//value was inserted, check if it caused an overflow
				if(overflow != null){
					//insert the new pointer into the index before/after split pointer 
					inode.insertSorted(overflow, index);;
					//check if it has caused an overflow of the index
					if(inode.isOverflowed()){
						Entry<K, Node<K, T>> indexOverflow =  this.splitIndexNode(inode);
						node = inode;
						return indexOverflow;
					} else return null;
				} else if (inserted) return null;
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
		Entry<K, Node<K,T>> overflow = this.insertHelper(key, value, this.root);

		//overflow made it to root
		if (overflow != null){
			Node<K,T> rootIndex =  this.root;
			//create a new root with key as the split key, 
			//left child as the modified right split of root,
			//and right child as the left split of root node
			IndexNode<K,T> newRoot = new IndexNode<K,T>(overflow.getKey(), rootIndex, overflow.getValue());
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
		//		System.out.println("removed middle index: " + pointer);
		
		LeafNode<K,T> leftLeafNode = new LeafNode<K,T>(pointer, leaf.values.remove(middleIndex));
		int size =  leaf.values.size();

		//		TODO: improve this by not using insertSorted (slower runtime)
		//add values to right list while removing from leaf
		for (int i = middleIndex; i <size; i++){
			leftLeafNode.insertSorted(leaf.keys.remove(middleIndex), leaf.values.remove(middleIndex));
		}

		//Update leaf nodes node pointers
		leftLeafNode.nextLeaf = leaf.nextLeaf;
		leftLeafNode.previousLeaf = leaf;
		leaf.nextLeaf = leftLeafNode;

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
		int middleIndex = index.keys.size()/2;
		//get pointer (key we split on) to pass up
		K pointer = index.keys.remove(middleIndex);

		ArrayList<Node<K,T>> rightChildren = new ArrayList<Node<K,T>>();
		ArrayList<K> rightKeys = new ArrayList<K>();

		int size =  index.keys.size();

		//add values to right list while removing from original index
		for (int i = middleIndex; i <size; i++){
			rightKeys.add(index.keys.remove(middleIndex));
			rightChildren.add(index.children.remove(middleIndex+1));
		}

		//add last child
		rightChildren.add(index.children.remove(middleIndex+1));

		//add values to new right index to be passed back
		IndexNode<K,T>newIndexNode = new IndexNode<K,T>(rightKeys, rightChildren);

		//create entry object
		Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(pointer, newIndexNode);
		return entry;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {
		if (this.root == null) return;
		this.deleteHelper(null, this.root, key);

	}

	private void deleteHelper(IndexNode<K,T> parentpointer, Node<K,T> nodepointer, K key){
		if (!nodepointer.isLeafNode){
			IndexNode<K,T> indexNode = (IndexNode<K,T>)nodepointer;
			for (int i=0; i< indexNode.keys.size(); i++){
				K fetchedKey = indexNode.keys.get(i);
				if (key.compareTo(fetchedKey) < 0){ //key less then fetched key (traverse left child)
					this.deleteHelper(indexNode, indexNode.children.get(i), key);
					break;
				}else if (i == indexNode.keys.size()-1) { //you're on the last key (traverse right child)
					this.deleteHelper(indexNode, indexNode.children.get(i+1), key);
					break;
				}
			}

			//check if nodepointer is root and if so is the root empty because of a merge 
			if (parentpointer == null){
				if (indexNode.keys.size() == 0){ //root is empty
					this.root = indexNode.children.remove(0); //set root to its merged child
				}
				return;
			} else if (indexNode.isUnderflowed()){
				//get sibling
				IndexNode<K,T> sibling = null;
				int splitkey = -1;
				int indexNodeIndex = parentpointer.children.indexOf(nodepointer); //Make sure this works
				if (indexNodeIndex == 0){
					sibling = (IndexNode<K, T>) parentpointer.children.get(1); //edge case where key is less then all keys in index
					splitkey = this.handleIndexNodeUnderflow(indexNode, sibling, parentpointer);
				} else{ 
					sibling = (IndexNode<K, T>) parentpointer.children.get(indexNodeIndex -1); //get left sibling 
					splitkey = this.handleIndexNodeUnderflow(sibling, indexNode, parentpointer);
				}
				//if you merge, make sure parent isn't root before deleting. If it is then you need to merge up and make it the root
			}

		} else {
			LeafNode<K,T> leafNode = (LeafNode<K,T>)nodepointer;
			Boolean deleteSuccess = leafNode.deleteValueForKey(key);

			//if the key did not exists, or it was inserted and there is no overflow then return null
			if(!deleteSuccess || !leafNode.isUnderflowed()) return;

			//special case where leaf is root node so underflow doesnt matter
			if (this.root.equals(nodepointer)){
				if (leafNode.keys.size() == 0) this.root = null;
				return; 
			}

			//get sibling
			LeafNode<K,T> sibling = null;
			int splitkey;

			int nodepointerIndex = parentpointer.children.indexOf(nodepointer); //Make sure this works
			if (nodepointerIndex == 0){
				sibling = (LeafNode<K, T>) parentpointer.children.get(1); //edge case where key is less then all keys in index
				splitkey = this.handleLeafNodeUnderflow(leafNode, sibling, parentpointer);
			} else{ 
				sibling = (LeafNode<K, T>) parentpointer.children.get(nodepointerIndex -1); //get left sibling 
				splitkey = this.handleLeafNodeUnderflow(sibling, leafNode, parentpointer);
			}

			//if the underflow was merged
			if (splitkey != REDISTRIBUTION){
				parentpointer.keys.remove(splitkey); // remove the key that pointed you right
				parentpointer.children.remove(splitkey + 1); //remove the right child
			}
		}

		return; 
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
		if(left.keys.size() + right.keys.size() <= 2*D){ //merge nodes
			//merge right into left
			left.keys.addAll(right.keys);
			left.values.addAll(right.values);

			left.nextLeaf = right.nextLeaf;
			//get split key
			int splitkey = parent.children.indexOf(right) - 1;
			parent.children.set(splitkey, left); //set the node as left
			return splitkey;
		} 
		// else redistribute
		int parentIndex = parent.children.indexOf(left);

		if(left.keys.size() > right.keys.size()){ //redistribute to right node
			//redistribute to right
			while(right.isUnderflowed()){
				right.keys.add(0, left.keys.remove(left.keys.size()-1));
				right.values.add(0, left.values.remove(left.values.size()-1));
			}
		} else{ //redistribute left
			while(left.isUnderflowed()){
				left.keys.add(right.keys.remove(0));
				left.values.add(right.values.remove(0));
			}
		}
		//change parent index to smallest right key
		parent.keys.set(parentIndex, right.keys.get(0));

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
		int splitkey = parent.children.indexOf(leftIndex);
		if(leftIndex.keys.size() + 1 + rightIndex.keys.size() <= 2*D){ //merge nodes
			//merge right into left
			//pull down splitkey key from parent 
			leftIndex.keys.add(parent.keys.remove(splitkey));

			//add merge
			leftIndex.keys.addAll(rightIndex.keys);
			leftIndex.children.addAll(rightIndex.children);

			//delete right index from parent
			parent.children.remove(rightIndex);

			parent.children.set(splitkey, leftIndex); //set the node as modified leftIndex
//			TODO: handle propagation up if parent now underflowed
			return splitkey;
		} 
		// else redistribute

		if(leftIndex.keys.size() > rightIndex.keys.size()){ //redistribute to right node
			//pull down splitkey from parent and add child from left
			rightIndex.keys.add(0,parent.keys.get(splitkey));
			rightIndex.children.add(0, leftIndex.children.remove(leftIndex.keys.size()-1));
			
			while(rightIndex.isUnderflowed()){
				rightIndex.keys.add(0, leftIndex.keys.remove(leftIndex.keys.size()-1));
				rightIndex.children.add(0, leftIndex.children.remove(leftIndex.children.size()-1));
			}
			
			//write over original splitkey value to new one
			parent.keys.set(splitkey, leftIndex.keys.remove(leftIndex.keys.size()-1));
			
		} else{ //redistribute left
			//pull down parent splitkey key
			leftIndex.keys.add(parent.keys.get(splitkey));
			leftIndex.children.add(rightIndex.children.remove(0));

			while(leftIndex.isUnderflowed()){
				leftIndex.keys.add(rightIndex.keys.remove(0));
				leftIndex.children.add(rightIndex.children.remove(0));
			}
			//write over original splitkey value to new one
			parent.keys.set(splitkey, rightIndex.keys.remove(0));
		}

		return -1;
	}


}
