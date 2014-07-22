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

public class BaselineEng {
	
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
							
							
							if (cont==7) {
								s+="|id0="+id;
							}
							
							if (cont==9){
								head=s;
							}
							
							if ((cont==11)){//||(cont==12)) {
								
								
								
								//REMOVE NODES
								if (s.equals("P")||
										pos.equals("TO")||
										pos.equals("HYPH")||
										(pos.equals("IN") && lemma.equals("that"))||
										(pos.equals("DT") && (lemma.equals("the")||lemma.equals("a"))))/*||
										(dep.s.equals("VC") & !dep.pos.equals("MD") & !dep.lemma.equals("do")))*/
											//s="null";
											print=false;
								
								
								if (s.equals("VC")){//  && !pos.equals("MD") && !lemma.equals("do")){
										//System.out.println("hola");
										ArrayList<String> a=nodesToRemoveSecondPass.get(i);
										if (a==null){
											a=new ArrayList<String>();
										}
										a.add(head);
										nodesToRemoveSecondPass.put(i, a);
								}


										if (s.equals("DEP")||
										s.equals("P")||
										s.equals("POSTHON")||
										s.equals("TITLE")||
										s.equals("VOC"))
											s="APPEND";

										if (s.equals("ADV")||
										s.equals("ADV-GAP")||
										s.equals("AMOD")||
										s.equals("AMOD-GAP")||
 										s.equals("APPO")||
 										s.equals("BNF")||
 										s.equals("DEP-GAP")||
 										s.equals("DIR-GAP")||
 										s.equals("DTV-GAP")||
 										s.equals("EXT-GAP")||
 										s.equals("EXTR-GAP")||
 										s.equals("GAP-LGS")||
 										s.equals("GAP-LOC")||
 										s.equals("GAP-LOC-PRD")||
 										s.equals("GAP-MNR")||
 										s.equals("GAP-NMOD")||
 										s.equals("GAP-OBJ")||
 										s.equals("GAP-OPRD")||
 										s.equals("GAP-PMOD")||
 										s.equals("GAP-PRD")||
 										s.equals("GAP-PRP")||
 										s.equals("GAP-PUT")||
 										s.equals("GAP-SBJ")||
 										s.equals("GAP-SUB")||
 										s.equals("GAP-TMP")||
 										s.equals("GAP-VC")||
 										s.equals("HMOD")||
 										s.equals("LOC")||
 										s.equals("LOC-MNR")||
 										s.equals("LOC-TMP")||
 										s.equals("MNR")||
 										s.equals("MNR-PRD")||
 										s.equals("MNR-TMP")||
 										s.equals("NMOD")||
 										s.equals("PRN")||
 										s.equals("PRP")||
 										s.equals("SUFFIX")||
 										s.equals("TMP"))
											s="ATTR";

										if (s.equals("COORD"))
											s="COORD";

										if (s.equals("EXTR")||
										s.equals("LGS")||
										s.equals("SBJ"))
											s="I";

										if(s.equals("CONJ")||
										s.equals("DIR")||
										s.equals("DIR-OPRD")||
										s.equals("DIR-PRD")||
										s.equals("LOC-PRD")||
										s.equals("OBJ")||
										s.equals("PMOD")||
										s.equals("PRD")||
										s.equals("PRD-PRP")||
										s.equals("PRD-TMP")||
										s.equals("SUB")||
										s.equals("VC"))
											s="II";

										if (s.equals("DTV")||
										s.equals("EXT")||
										s.equals("LOC-OPRD")||
										s.equals("OPRD"))
											s="III";
										
										if (s.equals("NAME")||
										s.equals("PRT")||
										s.equals("PUT"))
											s="NAME";
										
										
										
										
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
								if (!pos.equals("MD") && !lemma.equals("do")) {
								
									if (nodesToRemoveSecondPass.get(i)!=null && nodesToRemoveSecondPass.get(i).contains(id)){
										//System.out.println("hola");
										print=false;
									}
								}
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
				BufferedWriter bw3=new BufferedWriter(new FileWriter(args[0]+"_finalBaseline"));
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
							else if (cont==9 || cont==10){
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