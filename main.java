
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("hello");

		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		
		tree.insert(1, "a");
		String test = Utils.outputTree(tree);
		System.out.println("Test: "+test);
		tree.insert(5, "b");
		String test1 = Utils.outputTree(tree);
		System.out.println("Test: "+test1);
		tree.insert(8, "c");
		String test2 = Utils.outputTree(tree);
		System.out.println("Test: "+test2);
		tree.insert(10, "c");
		String test3 = Utils.outputTree(tree);
		System.out.println("Test: "+test3);
		tree.insert(12, "d");
		String test4 = Utils.outputTree(tree);
		System.out.println("Test: "+test4);
	
			
	}

}
