/**
 * 
 */
package transducer_svm_models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import treeTransducer.CoNLLHash;

/**
 * * @author Miguel Ballesteros
 * Universitat Pompeu Fabra. 
 *
 */
public class ModelLabellingClassification {
	
	int next=0;
	int nextDeprel=0;
	
	private HashMap<String, String> featureTranslation=new HashMap<String,String>();
	private HashMap<String, String> deprelTranslation=new HashMap<String,String>();
	private BufferedWriter bw;
	private BufferedWriter bw2;
	
	public ModelLabellingClassification() {
		try {
			bw=new BufferedWriter(new FileWriter("ssynt_labelling_svm.svm"));
			bw2=new BufferedWriter(new FileWriter("ssynt_labelling_svm_test.svm"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getDeepDeprel(String svmDeprel) {
		//it is the other way around.. we should find the deprel that corresponds to the svmDeprel. Hashmap process backwards.
		Set<String> deprels=deprelTranslation.keySet();
		Iterator<String> dpIt=deprels.iterator();
		while (dpIt.hasNext()) {
			String deprel=dpIt.next();
			String svmAux=deprelTranslation.get(deprel);
			if (svmAux.equals(svmDeprel)) return deprel; //if it finds the deprel that corresponds to svmDeprel, it returns the deprel.
		}
		return null;
	}
	
	
	
	public void closeBuffer(boolean train){
		try {
			if (train) {
				bw.close();
			}
			else {
				bw2.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void addNewFeature (String feature){
		
		String svmFeat=featureTranslation.get(feature);
		if (svmFeat==null) {
			next++;
			featureTranslation.put(feature,""+next+":1");
		}
		
	}
	
	private void addNewDeprel (String deprel){
		
		String svmFeat=deprelTranslation.get(deprel);
		if (svmFeat==null) {
			nextDeprel++;
			deprelTranslation.put(deprel,""+nextDeprel);
			//System.out.println(deprelTranslation);
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param form
	 * @param lemma
	 * @param pos
	 * @param feats
	 * @param deprel
	 * @param deepDeprel
	 * @param surfaceSentence 
	 * @param surfaceId 
	 * @param hypernode
	 * @param train
	 */
	public void addLine(String form, String lemma, String pos, String feats, String deprel, String deepDeprel, String surfaceId, CoNLLHash surfaceSentence, boolean hypernode, boolean train) {
		
		String line="";
		if (hypernode) {
			boolean write=false;
			if (train) {
				if (!deepDeprel.equals("ROOT")) {
					write=true;
					
					addNewDeprel(deepDeprel);
					line+=deprelTranslation.get(deepDeprel); //Esto solo para train, se ponen las etiquetas bien.
				
					
					
					
					this.addNewFeature("dep="+deprel);
					line+=" "+featureTranslation.get("dep="+deprel);
					
					/*this.addNewFeature("pos="+pos);
					line+=" "+featureTranslation.get("pos="+pos);*/
					
					/*StringTokenizer st=new StringTokenizer(feats);
					while(st.hasMoreTokens()) {
						String s=st.nextToken("|");
						if (s.contains("spos")) {
							this.addNewFeature(s);
							line+=" "+featureTranslation.get(s);
						}
					}*/
					
					this.addNewFeature("lemma="+lemma);
					line+=" "+featureTranslation.get("lemma="+lemma);
					
					
					String head=surfaceSentence.getHead(surfaceId);
					//list of deprels of siblings.
					ArrayList<String> siblings=surfaceSentence.getSiblings(head);
					Iterator<String> itSib=siblings.iterator();
					while(itSib.hasNext()) {
						String sib=itSib.next();
						if (!sib.equals(surfaceId)) {
							String sibDeprel=surfaceSentence.getDeprel(sib);
							
							this.addNewFeature("sibDeprel="+sibDeprel);
							line+=" "+featureTranslation.get("sibDeprel="+sibDeprel);

						}
					}
					
					if (head.equals("0")) {
						this.addNewFeature("govDeprel="+"ROOT");
						line+=" "+featureTranslation.get("govDeprel="+"ROOT");
					}
					else {
						String govDeprel=surfaceSentence.getDeprel(head);
							
						this.addNewFeature("govDeprel="+govDeprel);
						line+=" "+featureTranslation.get("govDeprel="+govDeprel);
					}	
				}
				
			}
			else {
				write=true;
				
				line+="1";//Esto solo para testing. Se ponen las etiquetas para su clasificación, todo 1s, se hará la clasificación luego.
				
				this.addNewFeature("dep="+deprel);
				line+=" "+featureTranslation.get("dep="+deprel);
				
				
				this.addNewFeature("lemma="+lemma);
				line+=" "+featureTranslation.get("lemma="+lemma);
				
				
				String head=surfaceSentence.getHead(surfaceId);
				
				
				//list of deprels of siblings.
				ArrayList<String> siblings=surfaceSentence.getSiblings(head);
				Iterator<String> itSib=siblings.iterator();
				while(itSib.hasNext()) {
					String sib=itSib.next();
					if (!sib.equals(surfaceId)) {
						String sibDeprel=surfaceSentence.getDeprel(sib);
						
						this.addNewFeature("sibDeprel="+sibDeprel);
						line+=" "+featureTranslation.get("sibDeprel="+sibDeprel);

					}
				}
				
				
				if (head.equals("0")) {
					this.addNewFeature("govDeprel="+"ROOT");
					line+=" "+featureTranslation.get("govDeprel="+"ROOT");
				}
				else {
					//System.out.println(surfaceSentence.getDeprel(head));
					String govDeprel=surfaceSentence.getDeprel(head); //...
						
					this.addNewFeature("govDeprel="+govDeprel);
					line+=" "+featureTranslation.get("govDeprel="+govDeprel);
					
					String govFeats=surfaceSentence.getFEAT(head);
					
				}

			}
				
	
				try {
				if (train) {
					if (write) 
						bw.append(line+"\n");
					}
					else {
					if (write) 
						bw2.append(line+"\n");
				}
				} catch (IOException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		//}
	}
	
	private ArrayList<String> getSiblings(String head, CoNLLHash surfaceSentence) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return this.featureTranslation.toString() + deprelTranslation.toString();
	
	}

}
