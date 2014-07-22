/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Miguel Ballesteros
 *
 */
public class SentenceSplitter {
	
	public static void main(String[] args) {
		
		int i=0;
		try {
			BufferedReader br=new BufferedReader(new FileReader(args[0]));
			try {
				String concat="";
				while(br.ready()) {
					String line=br.readLine();
					if (line.isEmpty()) {
						BufferedWriter bw=new BufferedWriter(new FileWriter(args[0]+"_"+i+".conll"));
						bw.write(concat);
						bw.close();
						i++;
						concat="";
					}
					else {
						concat+=line+"\n";
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
