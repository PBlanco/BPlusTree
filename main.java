import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Test:");

//		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
//		
//		//Delete Tests
//		tree.insert(1, "a");
//		String test = Utils.prettyOutputTree(tree);
//		System.out.print(test);
//		tree.insert(5, "b");
//		String test1 = Utils.prettyOutputTree(tree);
//		System.out.print(test1);
//		tree.delete(1);
//		String del1 = Utils.prettyOutputTree(tree);
//		System.out.print(del1);
		
//
//	
//		//insert tests
//		
//		 //Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
//		
//		 Character alphabet[] = new Character[] { 'f','u','v','a','b','c','d','l','m','n','o','e','h','i','p','j','k','q','r','s','t'};
//		 
//		 for(int k = 1; k <= alphabet.length; k++){
//			 String alphabetStrings[] = new String[k];
//			 Character alphabetKeys[] = new Character[k];
//			 for (int i = 0; i < k; i++) {
//				 alphabetStrings[i] = (alphabet[i]).toString();
//				 alphabetKeys[i] = (Character) alphabet[i];
//			 }
//			 BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
//			 Utils.bulkInsert(tree, alphabetKeys, alphabetStrings);
//
//			 String test = Utils.prettyOutputTree(tree);
//			 
//			 System.out.println("~~~~~~~~~~k = " + String.valueOf(k) + "~~~~~~~~~~");
//			 System.out.print(test);
//			 System.out.println("Tree search " + alphabet[k/2] + " " + tree.search(alphabet[k/2]));
//			 System.out.println('\n');
//			 
//			 
//		 }
//		 
		
		Integer testNumbers[] = new Integer[] { 2,3,7,14,16,19,20,22,24,27,29,33,34,38,39};
	    String testNumberStrings[] = new String[testNumbers.length];
	    for (int i = 0; i < testNumbers.length; i++) {
	      testNumberStrings[i] = (testNumbers[i]).toString();
	    }
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, testNumbers, testNumberStrings);
		System.out.println(Utils.prettyOutputTree(tree));

		/* PASSED REDISTIBUTIONA AND MERGE FOR LEFTMOST LEAF DELETES
		tree.insert(25, "25");
		tree.insert(26, "26");
		System.out.println(Utils.prettyOutputTree(tree));

		//test leftmost leaf delete leading to redistribution
		tree.delete(22);
		System.out.println(Utils.prettyOutputTree(tree));

		//test merge on leftmost leaf
		tree.delete(20);
		System.out.println(Utils.prettyOutputTree(tree));
		*/
		
		/* PASSED RIGHTMOST EDGE CASE FOR MERGE AND REDISTRIBUTION
		//test right most delete to merge
//		tree.delete(39);
//		tree.delete(34);
//		System.out.println(Utils.prettyOutputTree(tree));
		
		//test right most redistribution
		tree.insert(30, "30");
		tree.insert(31, "30");
		tree.delete(34);
		tree.delete(39);
		System.out.println(Utils.prettyOutputTree(tree));
		*/
		
		Collections.shuffle(Arrays.asList(testNumbers));
		for (int i = 0; i < testNumbers.length -1; i++) {
			tree.delete(testNumbers[i]);
			System.out.println(Utils.prettyOutputTree(tree));
		}
	}	
}
