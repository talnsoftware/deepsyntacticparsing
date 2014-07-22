/**
 * 
 */
package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import treeTransducer.CoNLLHash;
import treeTransducer.CoNLLTreeConstructor;
import treeTransducer.TransducerSurfToDeep;

/**
 * @author Miguel Ballesteros
 * Universitat Pompeu Fabra
 *
 */
public class Evaluation {
	
	/*private String goldStandard;
	private String inputSurfaceTreebank;
	private String output;*/
	
	private double precision;
	private double recall;
	private double f;
	
	private double LAS;
	private double UAS;
	private double LA;
	
	



	private ArrayList<CoNLLHash> surfaceHash;
	private ArrayList<CoNLLHash> goldStandardHash;
	private ArrayList<CoNLLHash> outputHash;
	
	public Evaluation (String goldStandard, String output) {
		//surfaceHash= CoNLLTreeConstructor.storeTreebank(inputSurface);
		
		goldStandardHash= CoNLLTreeConstructor.storeTreebank(goldStandard);
		outputHash= CoNLLTreeConstructor.storeTreebank(output);
		System.out.println("\n-----------Evaluation-------------\n");
	}
	
	private String getId0(String feats) {
		String id0="";
		StringTokenizer st=new StringTokenizer(feats);
		while(st.hasMoreTokens()) {
			String feat=st.nextToken("|");
			if (feat.contains("id0=")) {
				id0=feat.substring(4, feat.length());
			}
		}
		return id0;
	}
	
	
	public void HyperNodeAccuracy() {
		
		int nodesGs=0;
		int nodesDetected=0;
		int nodesCorrectlyDetected=0;
		
		for (int i=0;i<outputHash.size();i++) {
			CoNLLHash outputSentence=outputHash.get(i);
			CoNLLHash goldSentence=goldStandardHash.get(i);
			
			ArrayList<String> outputIds=outputSentence.getIds();
			//nodesDetected+=outputIds.size();
			nodesDetected+=countNodes(outputIds,outputSentence);
			
			
			ArrayList<String> goldIds=goldSentence.getIds();
			nodesGs+=countNodes(goldIds,goldSentence); //this method counts the nodes removing the correferences.
			//nodesGs+=goldIds.size();
			
			for(int i_id=0;i_id<outputIds.size();i_id++) {
				String id=outputIds.get(i_id);
				//String form=outputSentence.getForm(id);
				String feats=outputSentence.getFEAT(id);
				String id0=this.getId0(feats);
				//System.out.println(id0);
				if (!id0.contains("_")) {
					if (findNode(id0,goldSentence)) {
						nodesCorrectlyDetected++;
					}
				}
				/*else {
					System.out.println(id+"\t"+form);
				}*/
				
			}
			
		}
		
		
		System.out.println("----HyperNode Detection:----");
		
		
		precision=(double)nodesCorrectlyDetected/(double)nodesDetected*100;
		recall=(double)nodesCorrectlyDetected/(double)nodesGs*100;
		f=(2*precision*recall)/(precision+recall);

		System.out.println("Precision:"+precision);
		System.out.println("Recall:"+recall);
		System.out.println("F1:"+f);
		
		System.out.println(" #Nodes detected by the system: "+nodesDetected);
		System.out.println(" #Nodes in the gold-standard: "+nodesGs +" (by removing corefs)");
		System.out.println(" #Nodes correctly detected: "+nodesCorrectlyDetected);
	}
	
	public void nodeLabelAndAttachment() {
		int nodesGs=0;
		int nodesDetected=0;
		int nodesCorrectlyAttached=0;
		int nodesCorrectlyLabelled=0;
		int nodesCorrectlyAttachedAndLabelled=0;
		
		
		int contSentences=0;
		int completeAttachment=0;
		int completeLabelAttachment=0;
		
		boolean errorLabel=false;
		boolean errorAttach=false;
		for (int i=0;i<outputHash.size();i++) {
			
			
			CoNLLHash outputSentence=outputHash.get(i);
			CoNLLHash goldSentence=goldStandardHash.get(i);
			
			ArrayList<String> outputIds=outputSentence.getIds();
			//nodesDetected+=outputIds.size();
			nodesDetected+=countNodes(outputIds,outputSentence);
			
			ArrayList<String> goldIds=goldSentence.getIds();
			nodesGs+=countNodes(goldIds,goldSentence); //this method counts the nodes removing the correferences.
			//nodesGs+=goldIds.size();


			for(int i_id=0;i_id<outputIds.size();i_id++) {
				
			
				
				String id=outputIds.get(i_id);
				
				//System.out.println("Node: "+form);
				
				String feats=outputSentence.getFEAT(id);
				String id0=this.getId0(feats);
				/*System.out.println("");
				System.out.println(form);
				System.out.println(id0);*/
				String goldNode=this.returnNode(id0, goldSentence); 
				
				if (!id0.contains("_")) {
				
				if (goldNode!=null && !goldNode.isEmpty()) {
					
					//System.out.println("GoldNode: "+goldNode);
					
					//
					String formG=goldSentence.getForm(goldNode);
					//System.out.println(goldNode+" "+formG);
					//
				
					
					String label=outputSentence.getDeprel(id);
					
					//System.out.println("Label:"+label);
					
					String labelGold=goldSentence.getDeprel(goldNode);
					//System.out.println("LabelGold:"+labelGold);
					
					boolean labelOk=false;
					if (labelGold.equals(label)) {
						nodesCorrectlyLabelled++;
						labelOk=true;
					}
					else {
						errorLabel=true;
					}
					
					String head=outputSentence.getHead(id);
					//System.out.println("Head:"+head);

					String headGold=goldSentence.getHead(goldNode);
					//System.out.println("HeadGold:"+headGold);
					
					if (head.equals("0")||(headGold.equals("0"))) {
						if (head.equals(headGold)) {
							nodesCorrectlyAttached++;
							if (labelOk) {
								nodesCorrectlyAttachedAndLabelled++;
							}
						}
					}
					else {
							if (outputIds.contains(head)) { 
								
								String featsHead=outputSentence.getFEAT(head);
								String featsHeadGold=goldSentence.getFEAT(headGold);
						
								String id0Head=getId0(featsHead);
								String id0HeadGold=getId0(featsHeadGold);
						
								if (id0Head.equals(id0HeadGold)) {
									nodesCorrectlyAttached++;
									if (labelOk) {
										nodesCorrectlyAttachedAndLabelled++;
									}
								}
								else {
									//System.out.println(id+"\t"+form+"\tHead:"+head +"(surf:"+id0Head+")\tHeadGold:"+headGold+"(surf:"+id0HeadGold+") (sentenceCounter:"+i+")");
									errorAttach=true;
								}
							}
						}
					}
				
				}
			}
			
			if (errorAttach) {
				//System.out.println("Sentence counter: "+i);
				//System.out.println("-----------------------");
			}
			
			if (!errorAttach) {
				completeAttachment++;
			}
			
			if (!errorAttach && !errorLabel) {
				completeLabelAttachment++;
			}
			
			errorAttach=false;
			errorLabel=false;
			
			/*
			 * numberErrorsSentenceAttachment=new HashMap<Integer,Integer>();
		HashMap<Integer, Integer> numberErrorsSentenceLabelling=new HashMap<Integer,Integer>();
		HashMap<Integer, Integer> numberErrorsSentenceLabellingAttachment=new HashMap<Integer,Integer>();
			 */
			
		}
		
		/*
		 * precision=(double)nodesCorrectlyDetected/(double)nodesDetected;
		recall=(double)nodesCorrectlyDetected/(double)nodesGs;
		f=(2*precision*recall)/(precision+recall);
		 */
		
		
		
		System.out.println("----Labelling and Attachment:----");
		double preLas=(double)nodesCorrectlyAttachedAndLabelled/(double)nodesDetected*100;
		double preUas=(double)nodesCorrectlyAttached/(double)nodesDetected*100;
		double preLA=(double)nodesCorrectlyLabelled/(double)nodesDetected*100;
		
		double recLas=(double)nodesCorrectlyAttachedAndLabelled/(double)nodesGs*100;
		double recUas=(double)nodesCorrectlyAttached/(double)nodesGs*100;
		double recLA=(double)nodesCorrectlyLabelled/(double)nodesGs*100;
		
		System.out.println("Precision LAS:"+preLas + " ("+nodesCorrectlyAttachedAndLabelled+"/"+nodesDetected+")");
		System.out.println("Precision UAS:"+preUas + " ("+nodesCorrectlyAttached+"/"+nodesDetected+")");
		System.out.println("Precision LA:"+preLA + " ("+nodesCorrectlyLabelled+"/"+nodesDetected+")");
		System.out.println("-------------------");
		System.out.println("Recall LAS:"+recLas + " ("+nodesCorrectlyAttachedAndLabelled+"/"+nodesGs+")");
		System.out.println("Recall UAS:"+recUas + " ("+nodesCorrectlyAttached+"/"+nodesGs+")");
		System.out.println("Recall LA:"+recLA + " ("+nodesCorrectlyLabelled+"/"+nodesGs+")");
		
		contSentences=outputHash.size();
		
		double ucm=(double)completeAttachment/(double)contSentences*100;
		double lcm=(double)completeLabelAttachment/(double)contSentences*100;
		System.out.println("-------------------");
		System.out.println("UCM:"+ucm + " ("+completeAttachment+"/"+contSentences+")");
		System.out.println("LCM:"+lcm + " ("+completeLabelAttachment+"/"+contSentences+")");
		
	}
	
	
	
	/**
	 * 
	 * SO FAR, IT DOES NOT COUNT NODES THAT ARE COREF NODES (FIRST VERSION!)
	 * 
	 * @param goldIds
	 * @param goldSentence
	 * @return
	 */
	private int countNodes(ArrayList<String> ids, CoNLLHash goldSentence) {
		// TODO Auto-generated method stub
		int cont=0;
		
		for(int i_id=0;i_id<ids.size();i_id++) {
			String id=ids.get(i_id);
			String form=goldSentence.getForm(id);
			String feats=goldSentence.getFEAT(id);
			String id0=this.getId0(feats);
			/*System.out.println("");
			System.out.println(form);
			System.out.println(id0);*/
			//if ((!id0.contains("_"))&&(!feats.contains("coref"))) {  //IT DOES NOT COUNT NODES THAT ARE COREF NODES (FIRST VERSION!)
			if (!(id0.contains("_"))) {  //IT DOES NOT COUNT NODES THAT ARE COREF NODES (FIRST VERSION!)
				cont++;
			}
			
		}
		return cont;
	}

	private boolean findNode(String id0, CoNLLHash goldSentence) {
		// TODO Auto-generated method stub
		ArrayList<String> goldIds=goldSentence.getIds();
		
		for(int i_id=0;i_id<goldIds.size();i_id++) {
			String id=goldIds.get(i_id);
			String feats=goldSentence.getFEAT(id);
			String id0Gold=this.getId0(feats);
			//System.out.print(id0Gold+ " ");
			if (id0Gold.equals(id0)) {
				//System.out.println("Found");
				return true;
			}
		}

		return false;
	}
	
	private String returnNode(String id0, CoNLLHash goldSentence) {
		// TODO Auto-generated method stub
		ArrayList<String> goldIds=goldSentence.getIds();
		String node="";
		
		for(int i_id=0;i_id<goldIds.size();i_id++) {
			String id=goldIds.get(i_id);
			String feats=goldSentence.getFEAT(id);
			String id0Gold=this.getId0(feats);
			//System.out.print(id0Gold+ " ");
			if (id0Gold.equals(id0)) {
				//System.out.println("Found");
				return id;
			}
		}
		
		return node;
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public double getF() {
		return f;
	}
	

	public double getLAS() {
		return LAS;
	}

	public double getUAS() {
		return UAS;
	}

	public double getLA() {
		return LA;
	}
	
	public static void main(String[] args) {
		
		
		Option gsOpt = OptionBuilder.withArgName("Gold standard")
                .hasArg(true)
                .isRequired(false)
                .withDescription("Dsynt Gold")
                .withLongOpt("gsdsynt")
                .create("g");
        
        Option soOpt = OptionBuilder.withArgName("System output")
                .hasArg(true)
                .isRequired(false)
                .withDescription("dsynt treebank")
                .withLongOpt("tdsynt")
                .create("s");
		
        Options options = new Options();
		//options.addOption(sentenceOpt);
		//options.addOption(testingOpt);
		options.addOption(gsOpt);
		options.addOption(soOpt);
		
		 CommandLineParser parser = new BasicParser();
	        try {
	            // parse the command line arguments
	        	CommandLine line = parser.parse( options, args );
	        	//"dsynt_test.conll","dsynt_partial_output_3.conll"
	        	 
	     		Evaluation e=new Evaluation(line.getOptionValue("g"),line.getOptionValue("s"));
	     		e.HyperNodeAccuracy();
	     		e.nodeLabelAndAttachment();
	     		
	            

	            /*String sentenceFilePath = line.getOptionValue("s");
	            String transitionsFilePath = line.getOptionValue("t");
	            String outputFileName = line.getOptionValue("o");
	            
	            Integer sentenceChoice = 0;
	            if (line.hasOption("c")) {
	                sentenceChoice = Integer.parseInt(line.getOptionValue("c")) - 1;
	            }
	            boolean allowRoot = !line.hasOption("nar");
	            boolean verbose = line.hasOption("v");*/
	            
	            //run(sentenceFilePath, transitionsFilePath, outputFileName, allowRoot, sentenceChoice, verbose);
	        }
	        catch( ParseException exp ) {
	            // oops, something went wrong
	            System.err.println(exp.getMessage());
	            
	            // automatically generate the help statement
	            HelpFormatter formatter = new HelpFormatter();
	            formatter.printHelp("Evaluation Tree Transducer", options, true);
	       }
		
		
	       
	}
}

