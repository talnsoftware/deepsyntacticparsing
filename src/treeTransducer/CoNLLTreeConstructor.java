/**
 * 
 */
package treeTransducer;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * @author Miguel Ballesteros
 * Universitat Pompeu Fabra
 * 
 * This object of this class would receive a file and then generates a Tree<T> that stores the info associated.
 *
 */
public class CoNLLTreeConstructor {
	
	
	public CoNLLTreeConstructor() {
		
	}
	
	
	/**
	 * This method stores a treebank in the return object
	 * @return Treebank in ArrayList<CoNLLHash> format
	 */
public static ArrayList<CoNLLHash> storeTreebank(String path) {
		
		ArrayList<CoNLLHash> list=new ArrayList<CoNLLHash>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			
			CoNLLHash hash = new CoNLLHash();
			
			try {
				int i=0;
				while(reader.ready()) {
					String aux = reader.readLine();
                    if (aux.isEmpty()) {
                    	list.add(hash);
                        hash = new CoNLLHash();
                        
                    }
                    else {
                    	i++;
                    	hash.addLine(aux);
                    }
                    //hash.addLine(aux);

                }
				//System.out.println(path+": "+i);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	

}
