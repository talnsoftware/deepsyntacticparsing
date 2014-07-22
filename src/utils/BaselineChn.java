package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import treeTransducer.CoNLLHash;
import treeTransducer.CoNLLTreeConstructor;

public class BaselineChn {
	
public static void main(String[] args) {
		
		HashMap<Integer,ArrayList<String>> nodesToRemoveSecondPass=new HashMap<Integer,ArrayList<String>>();
		ArrayList<CoNLLHash> listOfConlls=CoNLLTreeConstructor.storeTreebank(args[0]);
		
		int i=0;
		try {
			BufferedReader br=new BufferedReader(new FileReader(args[0]));
			BufferedWriter bw=new BufferedWriter(new FileWriter(args[0]+"_id0.conll"));
			try {
				String concat="";
				boolean print=true;
				while(br.ready()) {
					String line=br.readLine();
					int cont=1;
					if (line.isEmpty()) {
						
						bw.write(concat+"\n");
						i++;
						concat="";
						cont=1;
					}
					else {
						String newline="";
						StringTokenizer st=new StringTokenizer(line);
						String id="";
						String pos="";
						String lemma="";
						String head="";
						String form="";
						while(st.hasMoreTokens()) {
							String s=st.nextToken("\t");
							if (cont==1) {
								id=s;
							}
							if (cont==5){
								pos=s;
							}
							if (cont==3){
								lemma=s;
							}
							
							if (cont==1){
								form=s;
							}
							
							
							if (cont==7) {
								s+="|id0="+id;
							}
							
							if (cont==9){
								head=s;
							}
							
							if ((cont==11)){//||(cont==12)) {
								
								
								
								/*if ((pos.equals("AS") && s.equals("asp"))||
										pos.equals("PU")||
										pos.equals("SP")||
										pos.equals("DEG")||
										pos.equals("DER")||
										pos.equals("DEV")||
										pos.equals("BA")||
										(pos.equals("P") && form.equals("就"))||
										(pos.equals("LB") && s.equals("pass"))||
										(pos.equals("SB") && s.equals("pass"))||
										(pos.equals("LC") && s.equals("plmod")))*/
											//s="null";
							    if (s.equals("punct")) print=false;
								
								if (s.equals("etc")||
										s.equals("prnmod"))
											s="APPEND";
								
								if (s.equals("advmod")||
										s.equals("amod")||
										s.equals("assm")||
										s.equals("assmod")||
 										s.equals("clf")||
 										s.equals("cop")||
 										s.equals("cpm")||
 										s.equals("dep")||
 										s.equals("det")||
 										s.equals("dvpmod")||
 										s.equals("loc")||
 										s.equals("mmod")||
 										s.equals("neg")||
 										s.equals("nn")||
 										s.equals("nummod")||
 										s.equals("ordmod")||
 										s.equals("prep")||
 										s.equals("prtmod")||
 										s.equals("rcmod")||
 										s.equals("tmod")||
 										s.equals("vmod"))
											s="ATTR";
								
								if (s.equals("cc")||
 										s.equals("comod")||
 										s.equals("rcomp"))
											s="COORD";
								
								if (s.equals("conj"))
									s="COORD_II";

								if (s.equals("nsubj")||
								s.equals("top")||
								s.equals("xsubj"))
									s="I";
								
								
								if(s.equals("attr")||
										s.equals("ccomp")||
										s.equals("dobj")||
										s.equals("lccomp")||
										s.equals("lobj")||
										s.equals("nsubjpass")||
										s.equals("pccomp")||
										s.equals("plmod")||
										s.equals("pobj")||
										s.equals("range"))
											s="II";
								
								
								if (s.equals("asp")||
										s.equals("ba")||
										s.equals("dvpm")||
										s.equals("pass")||
										s.equals("punct")||
										s.equals("root"))
											s="ROOT";

							}
							
							cont++;
							
							newline+=s+"\t";
							
						}
						if (print) {
							concat+=newline+"\n";
							newline="";
						}
						else {
							print=true;
							newline="";
						}
					}
				}
				bw.close();
				
				System.out.println(nodesToRemoveSecondPass);
				
				BufferedReader br2=new BufferedReader(new FileReader(args[0]+"_id0.conll"));
				BufferedWriter bw2=new BufferedWriter(new FileWriter(args[0]+"_baseline"));
				i=0;
				while(br2.ready()) {
					String line=br2.readLine();
					int cont=1;
					if (line.isEmpty()) {
						
						bw2.write(concat+"\n");
						i++;
						concat="";
						cont=1;
					}
					else {
						String newline="";
						StringTokenizer st=new StringTokenizer(line);
						String id="";
						String pos="";
						String lemma="";
						while(st.hasMoreTokens()) {
							String s=st.nextToken("\t");
							if (cont==1) {
								id=s;
							}
							if (cont==3){
								lemma=s;
							}
							if (cont==5){
								pos=s;
								//System.out.println(nodesToRemoveSecondPass.get(i));
								//if (!pos.equals("MD") && !lemma.equals("do")) {
								
									if (nodesToRemoveSecondPass.get(i)!=null && nodesToRemoveSecondPass.get(i).contains(id)){
										//System.out.println("hola");
										print=false;
									}
								//}
							}

							cont++;
							newline+=s+"\t";
						}
						if (print) {
							concat+=newline+"\n";
							newline="";
						}
						else {
							print=true;
							newline="";
						}
					}
							
				}
				bw2.close();
				br2.close();
				
				ArrayList<HashMap<String,String>> listMappings=new ArrayList<HashMap<String,String>>();
				BufferedReader br3=new BufferedReader(new FileReader(args[0]+"_baseline"));
				int tokenCounter=0;
				HashMap<String,String> mappingIds=new HashMap<String,String>();
				while(br3.ready()){
					String line=br3.readLine();
					if (line.isEmpty()){
						tokenCounter=0;
						listMappings.add(mappingIds);
						mappingIds=new HashMap<String,String>();
					}
					else {
						StringTokenizer st=new StringTokenizer(line);
						tokenCounter++;
						int cont=1;
						while(st.hasMoreTokens()) {
							String s=st.nextToken("\t");
							if (cont==1) {
								mappingIds.put(s,tokenCounter+"");
							}
							cont++;
						}
					}
					
				}
				br3.close();
				
				System.out.println(listMappings);
				
				br3=new BufferedReader(new FileReader(args[0]+"_baseline"));
				BufferedWriter bw3=new BufferedWriter(new FileWriter(args[0]+"_oldfinalBaseline"));
				int tok=0;
				i=0;
				mappingIds=listMappings.get(i);
				while(br3.ready()){
					String line=br3.readLine();
					int cont=1;
					if (line.isEmpty()) {
						
						bw3.write(concat+"\n");
						tok=0;
						i++;
						try{
							mappingIds=listMappings.get(i);
						}catch(Exception e){
							bw3.close();
							System.exit(0);
						}
						concat="";
						cont=1;
					}
					else {
						tok++;
						StringTokenizer st=new StringTokenizer(line);
						String newline="";
						while(st.hasMoreTokens()) {
							String s=st.nextToken("\t");
							if (cont==1) {
								newline+=tok+"\t";
							}
							else if (cont==9){// || cont==10){
								//find head
								String newHead=findHead(mappingIds,listOfConlls.get(i),s);
								newline+=newHead+"\t";
							}
							else if (cont==14){
								newline+=s;
							}
							else {
								newline+=s+"\t";
							}
							cont++;
							//newline+=s+"\t";
						}
						concat+=newline+"\n";
						newline="";
						
						
					}
				}
				bw3.close();
				br3.close();
				
				
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

private static String findHead(HashMap<String, String> mappingIds, CoNLLHash coNLLHash, String head) {
	// TODO Auto-generated method stub
	String newHead=null;
	String oldId=head;
	newHead=mappingIds.get(oldId);
	while(newHead==null){
		if (oldId.equals("0")) return "0";
		newHead=mappingIds.get(oldId);
		if (newHead==null) oldId=coNLLHash.getHead(oldId);
	}
	return newHead;
}



}