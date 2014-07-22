/**
 * 
 */
package treeTransducer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Miguel Ballesteros
 *
 * Universitat Pompeu Fabra
 */
public class Candidates {

	
	private ArrayList<HashMap<String,ArrayList<String>>> candidates; 
	//conflictive nodes from surface and possible candidates in deep. Keys: surface nodes. Values: new deep ids.
	private ArrayList<HashMap<String,ArrayList<String>>> candidatesPatterns; 
	//the patterns of the candidates shown above. One by one, in the same order in the arraylist which is the value of the hash.
	
	private ArrayList<HashMap<String,ArrayList<String>>> candidatesPaths;
	
	private ArrayList<HashMap<String,ArrayList<String>>> selectedCandidates;
	
	private ArrayList<HashMap<String,ArrayList<String>>> selectedSiblingCandidates;
	//Example
	//{24=[1, 17, 18]}
	//{24=[ROOT+subj, ROOT+analyt_perf+analyt_pass, ROOT+adv]}
	//1 corresponds to "ROOT+subj"
	//17 corresponds to ROOT+analyt_perf+analyt_pass
	//18 corresponds to ROOT+adv
	
	
	
	
	public ArrayList<HashMap<String, ArrayList<String>>> getSelectedSiblingCandidates() {
		return selectedSiblingCandidates;
	}

	public void setSelectedSiblingCandidates(
			ArrayList<HashMap<String, ArrayList<String>>> selectedSiblingCandidates) {
		this.selectedSiblingCandidates = selectedSiblingCandidates;
	}

	public Candidates() {
		candidates=new ArrayList<HashMap<String,ArrayList<String>>>(); 
		candidatesPatterns=new ArrayList<HashMap<String,ArrayList<String>>>(); 
		
		selectedCandidates=new ArrayList<HashMap<String,ArrayList<String>>>();
		selectedSiblingCandidates=new ArrayList<HashMap<String,ArrayList<String>>>();
		
		candidatesPaths=new ArrayList<HashMap<String,ArrayList<String>>>(); 
	}

	public ArrayList<HashMap<String, ArrayList<String>>> getCandidates() {
		return candidates;
	}

	public ArrayList<HashMap<String, ArrayList<String>>> getCandidatesPatterns() {
		return candidatesPatterns;
	}

	public ArrayList<HashMap<String, ArrayList<String>>> getSelectedCandidates() {
		return selectedCandidates;
	}

	public void calculateCandidates(ArrayList<CoNLLHash> deepPartialTreebank, ArrayList<CoNLLHash> surfaceTreebank) {
		// TODO Auto-generated method stub
		for(int i=0;i<deepPartialTreebank.size();i++) {
			HashMap<String, ArrayList<String>> candSentence=new HashMap<String, ArrayList<String>>(); 
			HashMap<String, ArrayList<String>> candSentencePattern=new HashMap<String, ArrayList<String>>();
			HashMap<String, ArrayList<String>> candSentencePaths=new HashMap<String, ArrayList<String>>();
			CoNLLHash sentence=deepPartialTreebank.get(i);
			ArrayList<String> ids=sentence.getIds();
			Iterator<String> itIds=ids.iterator();
			boolean anyConflict=false;
			while(itIds.hasNext()) {
				String id=itIds.next();
				String form=sentence.getForm(id);
				String head= sentence.getHead(id);
				String deprel= sentence.getDeprel(id);
				String candidate=getDeepCandidate(head);
				String pattern=this.getPattern(deprel, head, i, surfaceTreebank);
				//String setOfNodes=this.getSetOfNodes(deprel, head, i, surfaceTreebank);
				
				
				//if (pattern!=null && !pattern.isEmpty()) System.out.println(id+"\t"+form+"\t --> "+pattern);
				if (candidate!=null) {
					//System.out.println("Hay conflicto: node:"+candidate +" --> (head) " + head);
					String path=head;
					anyConflict=true;
					ArrayList<String> spCandidates=candSentence.get(candidate);
					ArrayList<String> spPatterns=candSentencePattern.get(candidate);
					ArrayList<String> spPaths=candSentencePaths.get(candidate);
					if (spCandidates==null) {
						spCandidates=new ArrayList<String>();
						spPatterns=new ArrayList<String>();
						spPaths=new ArrayList<String>();
						spCandidates.add(id);
						spPatterns.add(pattern);
						spPaths.add(path);
						candSentence.put(candidate, spCandidates);
						candSentencePattern.put(candidate, spPatterns);
						candSentencePaths.put(candidate, spPaths);
						//System.out.println(candSentence);
					}
					else {
						spCandidates.add(id);
						spPatterns.add(pattern);
						spPaths.add(path);
						candSentence.remove(candidate);
						candSentence.put(candidate, spCandidates);
						candSentencePattern.remove(candidate);
						candSentencePattern.put(candidate, spPatterns);
						candSentencePaths.remove(candidate);
						candSentencePaths.put(candidate, spPaths);
						//System.out.println(candSentence);
					}
				}
				
				
			}
			if (!anyConflict) {
				candidates.add(null);
				candidatesPatterns.add(null);//add a null hashmap, because there are no conflicts.
				candidatesPaths.add(null);
			}
			else {
				candidates.add(candSentence);
				candidatesPatterns.add(candSentencePattern);
				candidatesPaths.add(candSentencePaths);
			}
		}
		/*System.out.println("Candidates:"+candidates);
		System.out.println("CandidatesPatterns:"+candidatesPatterns);
		System.out.println("CandidatesPaths:"+candidatesPaths);*/
		
	}
	
	//Auxiliar function that returns the "possible" corresponding surface ID. That is, the first node in the list in the head column (when there is a conflict) from deep_2_1.conll
	public String getDeepCandidate(String head) {
		String candidate=null;
		if (head.contains("_[")) {
			StringTokenizer st=new StringTokenizer(head);
			String listNodes="";
			int delim=0;
			for (int i=0;i<head.length();i++) {
				if (head.charAt(i)=='[') delim=i;
			}
			String headPath=head.substring(delim+1, head.length());
			StringTokenizer st2=new StringTokenizer(headPath);
			//System.out.println("HeadPath:"+headPath);
		
			ArrayList<String> listCand=new ArrayList<String>();
			String cand="";
			while (st2.hasMoreTokens()) { 
				cand=st2.nextToken("_");
				listCand.add(cand);
				
			}
			//System.out.println(listCand);
			for (int i=listCand.size()-1;i>=0;i--) {
				if (!listCand.get(i).equals("0")) {
					return listCand.get(i);
				}
			}
		}
		return candidate;
	}
	
	public String getPattern(String deprel, String head, int sentenceCounter, ArrayList<CoNLLHash> testSetHash) {
		String pattern="";
		if (head.contains("_[")) {
			int delim=0;
			for (int i=0;i<head.length();i++) {
				if (head.charAt(i)=='[') delim=i;
			}
			String headPath=head.substring(delim+1, head.length());
			StringTokenizer stok=new StringTokenizer(headPath);
			while(stok.hasMoreTokens()) {
				String next=stok.nextToken("_");
				if (stok.hasMoreTokens()) { /////// (last node no)
					String deprelNext=TransducerSurfToDeep.findDeprelGovernor(sentenceCounter, next, testSetHash);
					if (!(deprelNext.equals("ROOT") && pattern.contains("ROOT"))) {
						pattern=deprelNext+"+"+pattern;
					}
				}
			}
			
			pattern=pattern+deprel;
			
		}
		
		return pattern;
	}
	
	
	public String getSetOfNodes(String deprel, String head, int sentenceCounter, ArrayList<CoNLLHash> testSetHash) {
		String pattern="";
		if (head.contains("_[")) {
			int delim=0;
			for (int i=0;i<head.length();i++) {
				if (head.charAt(i)=='[') delim=i;
			}
			String headPath=head.substring(delim+1, head.length());
			StringTokenizer stok=new StringTokenizer(headPath);
			while(stok.hasMoreTokens()) {
				String next=stok.nextToken("_");
				String deprelNext=TransducerSurfToDeep.findDeprelGovernor(sentenceCounter, next, testSetHash);
				if (!(deprelNext.equals("ROOT") && pattern.contains("ROOT"))) {
					pattern=next+"+"+pattern;
				}
			}
			
			pattern=pattern+head;
			
		}
		
		return pattern;
	}
	

	/**
	 * This method check the patterns inferred in the training process and selects the best candidates.
	 * @param deepPartialTreebank
	 * @param testSetHash
	 */
	public void selectCandidates(ArrayList<CoNLLHash> deepPartialTreebank,
			ArrayList<CoNLLHash> testSetHash) {
		// TODO Auto-generated method stub
		
		for (int i=0;i<this.candidates.size();i++) {
			HashMap<String,ArrayList<String>> candSentence=candidates.get(i);
			HashMap<String,ArrayList<String>> candSentencePattern=candidatesPatterns.get(i);
			HashMap<String,ArrayList<String>> candSentencePaths=candidatesPaths.get(i);
			HashMap<String,ArrayList<String>> candidateSelected=new HashMap<String,ArrayList<String>>();
			HashMap<String,ArrayList<String>> noConflictsSelected=new HashMap<String,ArrayList<String>>();
			//HashMap<String,String> candidateSelected=new HashMap<String,String>();
			if (candSentence==null) {
				selectedCandidates.add(null);
				selectedSiblingCandidates.add(null);
				
			}
			else {
				Set<String> surfaceIds=candSentence.keySet();
				Iterator<String> itS=surfaceIds.iterator();
				while(itS.hasNext()) {
					String surfId=itS.next();
					ArrayList<String> candidatesId=candSentence.get(surfId);
					ArrayList<String> candidatesIdPattern=candSentencePattern.get(surfId);
					ArrayList<String> candidatesIdPaths=candSentencePaths.get(surfId);
					
					
					//check whether there is conflict or not
				
					
					
					
					//ArrayList<String> conflicts=checkConflict(candidatesIdPaths);
					
					//System.out.println(conflicts);
					
					ArrayList<String> noConflictiveNodes=checkConflicts(candidatesIdPaths,candidatesId);
				
					noConflictsSelected.put(surfId, noConflictiveNodes);
					
					
					//if (!conflict) attachNoConflictiveNodes();
					
					if (candidatesId.size()==1) {
						ArrayList<String> aux=new ArrayList<String>();
						aux.add(candidatesId.get(0));
						candidateSelected.put(surfId, aux); //there is no need to check the patterns
						noConflictsSelected.put(surfId, aux);
					}

					else {
						//CHECK THE PATTERNS FILE
						if (noConflictiveNodes.containsAll(candidatesId)) {
							ArrayList<String> aux=new ArrayList<String>();
							aux.addAll(noConflictiveNodes);
							noConflictsSelected.put(surfId, aux);
						}
						else {
							//System.out.println(candidatesId);
							//System.out.println(candidatesIdPaths);
							String selected=checkPatterns(candidatesId,candidatesIdPattern,candidatesIdPaths,noConflictiveNodes);
							//System.out.println(selected);
							if (!selected.isEmpty()) { 
								ArrayList<String> aux=new ArrayList<String>();
								aux.add(selected);
								candidateSelected.put(surfId, aux);
							}
							else {
								selected=checkPatternsApproximately(candidatesId,candidatesIdPattern,candidatesIdPaths,noConflictiveNodes);
								ArrayList<String> aux=new ArrayList<String>();
								aux.add(selected);
								candidateSelected.put(surfId, aux);
							}
							//PARCHE para caso MUY extraño: este caso era cuando hay 2 que si y 1 que no.
							//[12_[20_19, 12_[29_19, 12_[29_19] (en el development set)
							//Si se descomenta los system.out de algunas líneas arriba se ve.
								if (selected.isEmpty()) {
									selected=candidatesId.get(0);
									ArrayList<String> aux=new ArrayList<String>();
									aux.add(selected);
									candidateSelected.put(surfId, aux);
								}
						}
					}
				}
				selectedCandidates.add(candidateSelected);
				selectedSiblingCandidates.add(noConflictsSelected);
			}
		}
		//System.out.println(selectedCandidates);
		
	}

	private ArrayList<String> checkConflicts(
			ArrayList<String> paths, ArrayList<String> candidatesId) {
		// TODO Auto-generated method stub
		ArrayList<String> noConflicts=new ArrayList<String>();
		
		
		int count=1;
		
		boolean conflict=false;
		for (int i=0;i<paths.size();i++) {
			for (int j=0;j<paths.size();j++) {
				if (i!=j) {
					if (paths.get(i).equals(paths.get(j))) {
						conflict=true;
					}
					if (paths.get(i).charAt(0)=='0') {
						conflict=true;
					}
				}
			}
			if (!conflict) {
				noConflicts.add(candidatesId.get(i));
				conflict=false;
			}
		}
		
		return noConflicts;

	}

	/*private ArrayList<String> checkConflict(ArrayList<String> paths) {
		// TODO Auto-generated method stub
			
		boolean conflict=false;
		
		if (paths.get(0).charAt(0)=='0') return true;
		
		int count=1;
		
		for (int i=0;i<paths.size();i++) {
			for (int j=0;j<paths.size();j++) {
				if (i!=j) {
					if (paths.get(i).equals(paths.get(j))) {
						return true;
					}
				}
			}
		}
		
		return conflict;
	}*/

	private String checkPatterns(ArrayList<String> candidatesId,
			ArrayList<String> candidatesIdPattern, ArrayList<String> candidatesIdPaths, ArrayList<String> noConflictiveNodes) {
		// TODO Auto-generated method stub
		
		String best="";
		Integer bestFreqPattern=0;
		for (int i=0;i<candidatesId.size();i++) {
			String id=candidatesId.get(i);
			if (!noConflictiveNodes.contains(id)) {
				String pattern=candidatesIdPattern.get(i);
			
				Integer freq=findPattern(pattern);
				if (freq>bestFreqPattern) {
					best=id;
					bestFreqPattern=freq;
				}
			}
		}
		return best;
	}
	
	private String checkPatternsApproximately(ArrayList<String> candidatesId,
			ArrayList<String> candidatesIdPattern, ArrayList<String> candidatesIdPaths, ArrayList<String> noConflictiveNodes) {
		// TODO Auto-generated method stub
		
		String best="";
		Integer bestFreqPattern=0;
		for (int i=0;i<candidatesId.size();i++) {
			String id=candidatesId.get(i);
			if (!noConflictiveNodes.contains(id)) {
				String pattern=candidatesIdPattern.get(i);
			
				if (i==0) best=id;
			
				Integer freq=findApproximatePattern(pattern);
				if (freq>bestFreqPattern) {
					best=id;
					bestFreqPattern=freq;
				}
			//System.out.println(best);
			}
		}
		return best;
	}

	private Integer findPattern(String pattern) {
		// TODO Auto-generated method stub
		Integer freq=0;
		
		try {
			BufferedReader br=new BufferedReader(new FileReader("pathPatterns.txt"));
			try {
				while(br.ready()) {
					String line=br.readLine();
					String[] splittedLine=line.split("\t");
					if (pattern.equals(splittedLine[0])) {
						freq=Integer.parseInt(splittedLine[1]);
						return freq;
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
		
		//if (freq==0)
		
		return freq;
	}
	
	private Integer findApproximatePattern(String pattern) {
		// TODO Auto-generated method stub
		Integer freq=0;
		
		//System.out.println("\tAPROXIMADO:"+pattern);
		StringTokenizer st=new StringTokenizer(pattern);
		String regex=st.nextToken("+");
		String subPattern=pattern.substring(regex.length()+1);
		//System.out.println(subPattern);
		
		try {
			BufferedReader br=new BufferedReader(new FileReader("pathPatterns.txt"));
			try {
				while(br.ready()) {
					String line=br.readLine();
					String[] splittedLine=line.split("\t");
					/*int comp=pattern.compareToIgnoreCase(splittedLine[0]);
					if (comp>max) max=comp;*/
					if (subPattern.contains(splittedLine[0])) { //FIND SUBPATTERNS
						//System.out.println(splittedLine[0]);
						freq=Integer.parseInt(splittedLine[1]);
						return freq;
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
		
		if (subPattern.contains("+")) return findApproximatePattern(subPattern);
		return freq;
	}

	public String getCalculatedHead(String head) {
		// TODO Auto-generated method stub
		String headOutput=null;
		StringTokenizer st=new StringTokenizer(head);
		if (st.hasMoreTokens()) {
			headOutput=st.nextToken("_");
			return headOutput;
		}
		return null;
	}
	
	 
}


