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
import java.util.StringTokenizer;

import treeTransducer.CoNLLHash;

/**
 * @author Miguel Ballesteros
 * * @author Miguel Ballesteros
 * Universitat Pompeu Fabra. 
 *
 */
public class ModelHyperNodeClassification {
	
	int next=0;
	
	private HashMap<String, String> featureTranslation=new HashMap<String,String>();
	private BufferedWriter bw;
	private BufferedWriter bw2;
	
	int cont=0;
	
	public ModelHyperNodeClassification() {
		try {
			bw=new BufferedWriter(new FileWriter("ssynt_svm.svm"));
			bw2=new BufferedWriter(new FileWriter("ssynt_svm_test.svm"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public void addLine(String form, String lemma, String pos, String feats, String deprel, String surfaceId, CoNLLHash surfaceSentence, boolean hypernode, boolean train) {
		
		String line="";
		if (train) {
			if (hypernode) {
				line+="+1";
			}
			else {
				line+="-1";
			}
		}
		else {
			line+="1";
		}
		
		//////////////////////////////////////
		//LOCAL FEATURES/////////////////////
		////////////////////////////////////
		
		
		/*this.addNewFeature("form="+form);
		line+=" "+featureTranslation.get("form="+form);*/
		
		this.addNewFeature("lemma="+lemma);
		line+=" "+featureTranslation.get("lemma="+lemma);

		/*this.addNewFeature("pos="+pos);
		line+=" "+featureTranslation.get("pos="+pos);*/
		
		
		this.addNewFeature("dep="+deprel);
		line+=" "+featureTranslation.get("dep="+deprel);
		
		/*this.addNewFeature("feats="+feats);
		line+=" "+featureTranslation.get("feats="+feats);*/
		
		StringTokenizer st=new StringTokenizer(feats);
		while(st.hasMoreTokens()) {
			String s=st.nextToken("|");
			if (s.contains("spos")) {
				this.addNewFeature(s);
				line+=" "+featureTranslation.get(s);
			}
		}
		
		///////////////////////////////////////
		//GOVERNOR FEATURES///////////////////
		/////////////////////////////////////
		
		String head=surfaceSentence.getHead(surfaceId);
		//deprel of the governor.
		if (head.equals("0")) {
			this.addNewFeature("govdep="+"ROOT");
			//line+=" "+featureTranslation.get("govdep="+"ROOT");
		}
		else {
			String govDeprel=surfaceSentence.getDeprel(head);
		
			this.addNewFeature("govdep="+govDeprel);
			//line+=" "+featureTranslation.get("govdep="+govDeprel);
			
			String govFeats=surfaceSentence.getFEAT(head);
			
			StringTokenizer st2=new StringTokenizer(govFeats);
			while(st2.hasMoreTokens()) {
				String s=st2.nextToken("|");
				if (s.contains("spos")) {
					this.addNewFeature("sposhead="+s);
					line+=" "+featureTranslation.get("sposhead="+s);
				}
			}

			
		}
		
		
		//list of deprels of siblings.
		/*ArrayList<String> siblings=surfaceSentence.getSiblings(head);
		Iterator<String> itSib=siblings.iterator();
		while(itSib.hasNext()) {
			String sib=itSib.next();
			if (!sib.equals(surfaceId)) {
				String sibDeprel=surfaceSentence.getDeprel(sib);
				
				this.addNewFeature("sibDeprel="+sibDeprel);
				line+=" "+featureTranslation.get("sibDeprel="+sibDeprel);
				
				/*String sibLemma=surfaceSentence.getLemma(sib);
				
				this.addNewFeature("sibLemma="+sibLemma);
				line+=" "+featureTranslation.get("sibLemma="+sibLemma);
				
				/*String sibPos=surfaceSentence.getPOS(sib);
				
				this.addNewFeature("sibPos="+sibPos);
				line+=" "+featureTranslation.get("sibPos="+sibPos);
				
			}
		}*/
		
		
		
		try {
			if (train) {
				bw.append(line+"\n");
			}
			else {
				bw2.append(line+"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return this.featureTranslation.toString();
	
	}
	
	
	
	
	

}
