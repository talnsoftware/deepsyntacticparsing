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

public class BaselineEs {
	
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
													
								
								if (s.equals("analyt_fut")||
										s.equals("analyt_pass")||
										s.equals("analyt_perf")||
										s.equals("analyt_progr")){
										ArrayList<String> a=nodesToRemoveSecondPass.get(i);
										if (a==null){
											a=new ArrayList<String>();
										}
										a.add(head);
										nodesToRemoveSecondPass.put(i, a);
								}

								//REMOVE NODES
								if (s.equals("aux_phras")||
										s.equals("aux_refl_lex")||
										s.equals("aux_refl_pass")||
										s.equals("punc")||
										(s.equals("det")&&lemma.equals("un"))||
										(s.equals("det")&&lemma.equals("el"))||
										(s.equals("iobj") && pos.equals("IN"))||
										(s.equals("obl_obj") && pos.equals("IN"))||
										(s.equals("obl_compl") && pos.equals("IN"))||
										s.equals("punc_init"))
											//s="null";
											print=false;

										if (s.equals("juxtapos")||
												s.equals("prolep"))
											s="APPEND";


										if (s.equals("abbrev")||
										s.equals("abs_pred")||
										s.equals("adjunct")||
										s.equals("adv")||
										s.equals("adv_mod")||
										s.equals("appos")||
										s.equals("appos_descr")||
										s.equals("attr")||
										s.equals("attr_descr")||
										s.equals("bin_junct")||
										s.equals("compl_adnom")||
										s.equals("det")||
										s.equals("elect")||
										s.equals("modif")||
										s.equals("modif_descr")||
										s.equals("obj_copred")||
										s.equals("quant")||
										s.equals("quant_descr")||
										s.equals("relat")||
										s.equals("relat_descr")||
										s.equals("relat_expl")||
										s.equals("sequent")||
										s.equals("subj_copred"))
											s="ATTR";

										 
										if (s.equals("coord")||
										s.equals("num_junct")||
										s.equals("quasi_coord"))
											s="COORD";

										if (s.equals("agent")||
										s.equals("quasi_subj")||
										s.equals("subj"))
											s="I";

										if(s.equals("aux_refl_dir")||
										s.equals("compar")||
										s.equals("compar_conj")||
										s.equals("compl1")||
										s.equals("coord_conj")||
										s.contains("analyt_")||
										s.equals("copul")||
										s.equals("copul_clitic")||
										s.equals("copul_quot")||
										s.equals("iobj")||
										s.equals("iobj_clitic")||
										s.equals("obl_obj")||
										s.equals("obl_compl")||
										s.equals("dobj")||
										s.equals("dobj_clitic")||
										s.equals("dobj_quot")||
										s.equals("modal")||
										s.equals("obl_compl0")||
										s.equals("obl_compl1")||
										s.equals("obl_compl2")||
										s.equals("obl_compl3")||
										s.equals("obl_obj1")||
										s.equals("obl_obj2")||
										s.equals("obl_obj3")||
										s.equals("prepos")||
										s.equals("prepos_quot")||
										s.equals("sub_conj"))
											s="II";


										if (s.equals("aux_refl_indir")||
										s.equals("compl2")||
										s.equals("iobj_clitic1")||
										s.equals("iobj_clitic2")||
										s.equals("iobj_clitic3")||
										s.equals("iobj1")||
										s.equals("iobj2")||
										s.equals("iobj3"))
											s="III";
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