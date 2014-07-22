package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Baseline {
	
public static void main(String[] args) {
		
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
						while(st.hasMoreTokens()) {
							String s=st.nextToken("\t");
							if (cont==1) {
								id=s;
							}
							if (cont==7) {
								s+="|id0="+id;
							}
							
							if ((cont==11)||(cont==12)) {
								
								
								
								//REMOVE NODES
								if (s.equals("analyt_fut")||
										s.equals("analyt_pass")||
										s.equals("analyt_perf")||
										s.equals("analyt_progr")||
										s.equals("aux_phras")||
										s.equals("aux_refl_lex")||
										s.equals("aux_refl_pass")||
										s.equals("punc")||
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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
