
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Test:");

//		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
//		
//		tree.insert(1, "a");
//		String test = Utils.outputTree(tree);
//		System.out.println("Test: "+test);
//		tree.insert(5, "b");
//		String test1 = Utils.outputTree(tree);
//		System.out.println("Test: "+test1);
//		tree.insert(8, "c");
//		String test2 = Utils.outputTree(tree);
//		System.out.println("Test: "+test2);
//		tree.insert(10, "c");
//		String test3 = Utils.outputTree(tree);
//		System.out.println("Test: "+test3);
//		tree.insert(12, "d");
//		String test4 = Utils.outputTree(tree);
//		System.out.println("Test: "+test4);
	
//		 Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		
		 Character alphabet[] = new Character[] { 'f','u','v','a','b','c','d','l','m','n','o','e','h','i','p','j','k','q','r','s','t'};
		 
		 for(int k = 1; k <= alphabet.length; k++){
			 String alphabetStrings[] = new String[k];
			 Character alphabetKeys[] = new Character[k];
			 for (int i = 0; i < k; i++) {
				 alphabetStrings[i] = (alphabet[i]).toString();
				 alphabetKeys[i] = (Character) alphabet[i];
			 }
			 BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
			 Utils.bulkInsert(tree, alphabetKeys, alphabetStrings);

			 String test = Utils.prettyOutputTree(tree);
			 
			 System.out.println("~~~~~~~~~~k = " + String.valueOf(k) + "~~~~~~~~~~");
			 System.out.print(test);
//			 System.out.println("Tree search " + alphabet[k/2] + " " + tree.search(alphabet[k/2]));
			 System.out.println('\n');
			 
			 
		 }
		 
	}

}
