/**
 * 
 */
package treeTransducer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import evaluation.Evaluation;

import svm_utils.svm_predict;
import svm_utils.svm_train;
import transducer_svm_models.ModelHyperNodeClassification;
import transducer_svm_models.ModelLabellingClassification;

/**
 * @author Miguel Ballesteros
 * Universitat Pompeu Fabra.
 * A tree transducer: Transforming from Surface Syntax to Deep Syntax Automatically.
 * which may be called as a grammar induction system.
 *
 * It is similar to a graph-based parsing strategy. 
 * In which the arcs of the trees are scored to be removed or to be added.
 * 
 * The process is the following: the system has the original Surface tree in which all the arcs are highly scored.
 * Then, the system adds new arcs that are susceptible of remain as permanents in the Syntax tree, however, 
 * they are initially scored differently.
 *  
 *  TODO Keep defining the system.
 *  
 *  
 *  
 *  
 *  What if instead of defining a tree transducer, I basically define a tagset transformation
 *     -> Three possible cases.
 *        * Direct transformation of label: 
 *              such as.  aux_refl	-> INVERT-I/II|ADD-SE-LEMMA|II+ADD-COREF-I|III+ADD-COREF-I
 *		  *  Remove a node:
 *               label --> NULL. Then, all the nodes with label NULL will be removed in postprocessing
 *        *  Add a node (separate in two or more nodes)
 *               refrescarse -- > refrescar | se (two or more nodes) 
 *               (it will basically provide a label that is the two new labels... then just separate it in postprocessing)
 *               
 *    In this way, there is only one thing to solve: a classifier that provides these transformations.
 *    
 *    
 *    En el fondo esto se puede definir de la siguiente manera:
 *    
 *    Transformaciones
 *      nodo --> nodo.
 *      hipernodo --> nodo.
 *      nodo --> hipernodo.
 *      
 *   Lo que nos lleva a tener:
 *      hipernodo-->hipernodo.
 *      
 *   Definiendo todo como hipernodo, tanto los nodos, como los hipernodos per se.
 *   
 *   
 *   Problema: ¿cómo detectar si un nodo es ya un hiper nodo o o es un nodo que debe integrarse como nodo?, se me ocurre que se deben usar dos libsvms.
 *   
 *     Esa información está en el training set. Se encuentra en los nodos dsynt 
 *     1. En Los id0---idn indican a los nodos que corresponden en el ssynt. 
 *     Y además la información está en el lemma. Que encaja directamente con algunos de los idI.
 *     
 *     El problema es que en tiempo de test también se haga. Sólo se tiene la información de los test sets que es un ssynt.
 *     Hay que incorporar un libsvm que sea capaz de detectar si un nodo es o no es ya "final".
 *     Para eso hay que detectarlo en el training set previamente.
 *     
 *     y el sistema ML que hace eso se puede testear, (con el corpus de test) con lo cual está bien. Y creo que se puede hacer en menos tiempo.
 *     
 *     Segunda parte del problema:
 *     
 *     2. Libsvms que transforma los hipernodos en los hipernodos (o nodos de salida). 
 *     Es una clasificación más complicada pero se puede hacer con las transformaciones que se ven arriba, y centrándose en las etiquetas.
 *     Es decir:
 *       det subj (+ features) --> I
 *       etc.
 *       
 *       
 *     Esto lo convierte en 2 sistemas ML juntos: 2 libsvms, basicamente:
 *     1- libsvm que clasifica si un nodo es o no es final, y cuando lo clasifica lo hace de esa manera.
 *     2- libsvm que dado un hiper nodo genera el hiper nodo de salida. Su etiquetado vaya.
 *     
 *     
 *     A libsvm for each sentence, or a libsvm for each data set?
 *
 */
public class TransducerSurfToDeep {
	
	public static boolean needToTrain=true;
	
	private String pathSurface;
	private String pathDeep;
	
	private String pathTestSurface;
	//private String pathTestDeep;
	
	
	
	
	private ArrayList<HashMap<Integer,Boolean>> hyperNodeMapping;
	
	private ModelHyperNodeClassification mdclass;
	private ModelLabellingClassification mdLabelclass;
	
	private long tiempoInicial=0;
	
	private String trainedModel;
	private String trainedLabellerModel;
	
	public TransducerSurfToDeep(String pathSurface, String pathDeep, String pathTestSurface) {
		this.pathDeep=pathDeep;
		this.pathSurface=pathSurface;
		
		this.pathTestSurface=pathTestSurface;
		//this.pathTestDeep=pathTestDeep;
		
		this.trainedLabellerModel="ssynt_labelling_svm.svm.model";
		this.trainedModel="ssynt_svm.svm.model";
		mdclass=new ModelHyperNodeClassification();
		mdLabelclass=new ModelLabellingClassification();
	}
	
	public void setPathSurface(String pathSurface){
		this.pathSurface=pathSurface;
	}
	
	public void setPathDeep(String pathDeep){
		this.pathDeep=pathDeep;
	}
	
	public void setPathTestSurface(String pathTestSurface){
		this.pathTestSurface=pathTestSurface;
	}
	
	
	
	public void training() {
		
		
		
		Date d=new Date();
		tiempoInicial=d.getTime();
		
		System.out.println("Training process started: "+d.toString());

		
		System.out.print("Processing surface syntax treebank ... ");
		
		ArrayList<CoNLLHash> surfaceTreebank = CoNLLTreeConstructor.storeTreebank(pathSurface);
			d=new Date();
			long tiempoActual=d.getTime();
			long contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
		System.out.print("Processing deep syntax treebank ... ");
		ArrayList<CoNLLHash> deepTreebank = CoNLLTreeConstructor.storeTreebank(pathDeep);
			
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
		
		System.out.print("Detecting hyper-nodes and preparing libsvm model... ");
		storingPatterns(surfaceTreebank,deepTreebank);
		detectHyperNodes(surfaceTreebank,deepTreebank, true); //it also generates the svm files for training the model
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
			if (needToTrain) System.out.println("Training LibSvm model for hypernode detection... (may take a while) ");
			String[] args =new String[28];
			args[0]="-s";
			args[1]="0";
			args[2]="-t";
			args[3]="1";
			args[4]="-d";
			args[5]="2";
			args[6]="-g";
			args[7]="0.2";
			args[8]="-r";
			args[9]="0.0";
			args[10]="-n";
			args[11]="0.5";
			args[12]="-n";
			args[13]="0.5";
			args[14]="-m";
			args[15]="100";
			args[16]="-c";
			args[17]="1100.0";
			args[18]="-e";
			args[19]="1.0";
			args[20]="-p";
			args[21]="0.1";
			args[22]="-h";
			args[23]="1";
			args[24]="-b";
			args[25]="0";
			args[26]="-q";
			
			
			
			args[27]="ssynt_svm.svm";
			try {
				if (needToTrain) svm_train.main(args);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
			if (needToTrain) System.out.println("Training LibSvm for labelling classification ... ");
			//prepareDatasets (done above)
			args[2]="-t";
			args[3]="0";
			args[4]="-d";
			args[5]="1";

			
		
			args[27]="ssynt_labelling_svm.svm";
			try {
				if (needToTrain) svm_train.main(args);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
			
	}
	

	private void storingPatterns(ArrayList<CoNLLHash> surfaceTreebank,ArrayList<CoNLLHash> deepTreebank) {
		// TODO Auto-generated method stub
		
		HashMap<String,Integer> patternsFreq=new HashMap<String,Integer>();
		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter("pathPatterns.txt"));
			for (int i=0;i<deepTreebank.size();i++) {
				CoNLLHash deepSentence=deepTreebank.get(i);
				CoNLLHash surfaceSentence=surfaceTreebank.get(i);
				
				ArrayList<String> ids=deepSentence.getIds();
				Iterator<String> it=ids.iterator();
				while(it.hasNext()) {
					String itt=it.next();
					//System.out.println(itt);
					String deepFeats=deepSentence.getFEAT(itt);
					//System.out.println(deepFeats);
					if (deepFeats.contains("id1")){
						//System.out.println(deepFeats);
						StringTokenizer st=new StringTokenizer(deepFeats);
						ArrayList<String> surfaceNodes=new ArrayList<String>();
						
						String id0="";
						
						while(st.hasMoreTokens()) {
							String feat=st.nextToken("|");
							if (feat.contains("id")&&(!feat.contains("id0"))&&(!feat.contains("coref"))&&(!feat.contains("prosubj"))) {
								String idSurface=feat.substring(4, feat.length());
								surfaceNodes.add(idSurface);
							}
							else if ((feat.contains("id0")) && (!feat.contains("coref"))&&(!feat.contains("prosubj"))) {
								id0=feat.substring(4, feat.length());
							}
						}
						if (!id0.isEmpty()){
							Integer id0Int=Integer.parseInt(id0);
							String path="";
							Iterator<String> itIds=surfaceNodes.iterator();
							boolean interestingPattern=false;
							//System.out.println("SentenceCounter:"+i);
							while(itIds.hasNext()) {
								String id=itIds.next();
								//System.out.println(id);
								Integer idInt=Integer.parseInt(id);
							
								if (idInt<id0Int) interestingPattern=true;
								if (path.isEmpty()) {
									
									path=surfaceSentence.getDeprel(id);
								}
								else {
									path=path+"+"+surfaceSentence.getDeprel(id);
								}
							}
							if (interestingPattern) {
								//bw.write(path+"\n");
								Integer freq=patternsFreq.get(path);
								if (freq!=null) {
									freq++;
									patternsFreq.remove(path);
									patternsFreq.put(path, freq);
								}
								else {
									patternsFreq.put(path, new Integer(1));
								}
							}
						}
					}
				}
				
			}
			
			Set<String> keysPatterns=patternsFreq.keySet();
			Iterator<String> itKeys=keysPatterns.iterator();
			while(itKeys.hasNext()) {
				String pattern=itKeys.next();
				bw.write(pattern+"\t"+patternsFreq.get(pattern)+"\n");
			}
			
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	private void testing() {
		// TODO Auto-generated method stub
		System.out.println("----------");
		Date d=new Date();
		tiempoInicial=d.getTime();
		
		System.out.println("Testing process started: "+d.toString());

		System.out.print("Processing surface syntax treebank ... ");
		
		ArrayList<CoNLLHash> surfaceTestTreebank = CoNLLTreeConstructor.storeTreebank(pathTestSurface);
			d=new Date();
			long tiempoActual=d.getTime();
			long contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			//System.out.print("Processing deep syntax treebank ... ");
		//ArrayList<CoNLLHash> deepTestTreebank = CoNLLTreeConstructor.storeTreebank(pathTestDeep);
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
		
		System.out.print("Detecting hyper-nodes and loading libsvm model... ");
		
		//detectHyperNodes(surfaceTestTreebank,deepTestTreebank,false); //it also generates the svm files for testing the model
		//detectHyperNodes(surfaceTestTreebank,null,false); //it also generates the svm files for testing the model
		detectHyperNodesTest(surfaceTestTreebank);
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
		System.out.println("Testing the hypernode classifier (libsvm) loaded... (may take a while) ");
			String[] args =new String[4];
			args[1]="ssynt_svm_test.svm";
			args[2]=this.trainedModel;
			args[3]="ssynt_output.svm";
			args[0]="-q";
			try {
				svm_predict.main(args);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
		System.out.println("Producing partial output ...  ");
			
			String path="ssynt_output.svm";
			//System.out.println("parece que faltan alguna frases...");
			String outputPartialPath=producePartialOutput(path,this.pathTestSurface); 
			String updatedPartialPath=updateIds(outputPartialPath);
			System.out.println("Partial output stored in "+ updatedPartialPath);
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
		//System.out.println("Testing the (GOLD-STANDARD input (no debe ser asi, aunque sirve como test y como datos de paper) labeller (libsvm) loaded... (may take a while) ");
		System.out.println("Testing the labeller (libsvm) loaded... (may take a while) ");
		
		ArrayList<CoNLLHash> surfaceTestTreebank2nd = CoNLLTreeConstructor.storeTreebank(updatedPartialPath);
		//System.out.println(surfaceTestTreebank2nd.size());
		prepareHyperNodeLabelling(surfaceTestTreebank2nd);
		String[] args2 =new String[4];
			args2[1]="ssynt_labelling_svm_test.svm";
			args2[2]=this.trainedLabellerModel;
			args2[3]="ssynt_output_labeller.svm";
			args2[0]="-q";
			try {
				svm_predict.main(args2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");
			
		System.out.println("Producing output once the labelling is calculated ...");
			String labelledPartialPath=updateLabels(updatedPartialPath, "ssynt_output_labeller.svm"); 
			//System.out.println("Partial output stored in "+ labelledPartialPath);
			System.out.println("Final output stored in "+ labelledPartialPath);
			d=new Date();
			tiempoActual=d.getTime();
			contTiempo=tiempoActual-tiempoInicial;
			//contTiempo=contTiempo/1000;
			tiempoInicial=tiempoActual;
			System.out.println("Done. "+ contTiempo+"ms");

	}


	/**
	 * This method updates the labels considering the outcomes of the svm classifier. It also sets to ROOT the nodes that have head=0
	 * @param partialPath
	 * @param svmOutput
	 * @return
	 */
	private String updateLabels(String partialPath, String svmOutput) {
		// TODO Auto-generated method stub
		//String output="dsynt_partial_output_3.conll";
		String output="dsynt_final_output.conll";
		try {
			BufferedReader brP=new BufferedReader(new FileReader(svmOutput));
			BufferedReader brTest=new BufferedReader(new FileReader(partialPath));
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));

			try {
				while(brTest.ready()) {
					String line=brTest.readLine(); 
					if (line!=null && !(line.equals(""))) {
						String svmLine=brP.readLine();
						//System.out.println(svmLine);
						StringTokenizer st=new StringTokenizer(svmLine);
						if (st.hasMoreTokens()) {
							svmLine=st.nextToken(".");
						}
						String label=mdLabelclass.getDeepDeprel(svmLine);
						StringTokenizer st2=new StringTokenizer(line);
						int cont=1;
						String newLine="";
						String head="";
						while(st2.hasMoreTokens()) {
							String tok=st2.nextToken("\t");
							if (cont==11 || cont==12) {
								if (head.equals("0")) label="ROOT";
								newLine+=label+"\t";
							}
							else if (cont==14)
								newLine+=tok+"\n";
							else newLine+=tok+"\t";
							
							if (cont==9) {
								head=tok;
							}
							cont++;
						}
						bw.write(newLine);
					}
					else {
						bw.write("\n");
					}
					
				}
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return output;
	}



	private String updateIds(String outputPartialPath) {
		// TODO Auto-generated method stub
		
			String output="dsynt_partial_output_2_1.conll";
			ArrayList<HashMap<String, String>> matchingIds=new ArrayList<HashMap<String,String>>();
			ArrayList<HashMap<String, String>> reverseMatchingIds=new ArrayList<HashMap<String,String>>();
			
			ArrayList<CoNLLHash> testSetHash=CoNLLTreeConstructor.storeTreebank(this.pathTestSurface);
			 
			 HashMap<String,String> sentenceIdMatch=new HashMap<String,String>();
			 HashMap<String,String> reverseSentenceIdMatch=new HashMap<String,String>();
			 int sentenceCounter=0;
			 int idCounter=1;
			 ArrayList<HashMap<String,String>> paths=new ArrayList<HashMap<String,String>>(); //this will be for the efficient version
			
			try {
				BufferedReader brP=new BufferedReader(new FileReader(outputPartialPath));
				BufferedWriter brW=new BufferedWriter(new FileWriter(output));
				try {
					while(brP.ready()) {
						String line=brP.readLine();
						StringTokenizer st=new StringTokenizer(line);
						if ((line!=null) && (!line.equals(""))) {
							//System.out.println(line);
							if (st.hasMoreTokens()) {
								String id=st.nextToken("\t");

								
								sentenceIdMatch.put(id, idCounter+"");
								reverseSentenceIdMatch.put(idCounter+"",id);
								//System.out.println(line);
								//System.out.println(id);
								//System.out.println(sentenceIdMatch.get(id));
								idCounter++;
							}
						}
						else {
							//System.out.println(sentenceIdMatch);
							HashMap<String,String> sentenceIdMatchClone=(HashMap<String, String>) sentenceIdMatch.clone();
							HashMap<String,String> reverseSentenceIdMatchClone=(HashMap<String, String>) reverseSentenceIdMatch.clone();
							//System.out.println(sentenceIdMatchClone);
							matchingIds.add(sentenceCounter,sentenceIdMatchClone);
							reverseMatchingIds.add(sentenceCounter,reverseSentenceIdMatchClone);
							sentenceIdMatch=new HashMap<String,String>();
							reverseSentenceIdMatch=new HashMap<String,String>();
							sentenceCounter++;
							idCounter=1;
						}
						
					}
					
					
					//Now, I have all the ids that exist in the list of hashmaps (one hashmap for each sentence)
					
					/*System.out.println(matchingIds);
					System.out.println(reverseMatchingIds);*/
					
					brP=new BufferedReader(new FileReader(outputPartialPath));
					sentenceCounter=0;
					sentenceIdMatch=matchingIds.get(sentenceCounter);
					reverseSentenceIdMatch=reverseMatchingIds.get(sentenceCounter);
					
					
					
					String newSentence="";			
					
					HashMap<String,String> sentencePath=new HashMap<String,String>();
					while(brP.ready()) {
						String line=brP.readLine();
						String newLine="";
						String id="";
						String oldId="";
						StringTokenizer st=new StringTokenizer(line);
						if (line!=null && !line.equals("")) {
							int cont=0;
							while(st.hasMoreTokens()) {
								String tok=st.nextToken("\t");
								
								if ((cont==0) || (cont==8)) {
									String value=sentenceIdMatch.get(tok);
									if (cont==0) {
										id=value;
										oldId=tok;
									}
									
									if (value==null) {
										//several REASONS:... we should fix this. There are more than one.
										if ((cont==8)) {
											if (tok.equals("0")) {
												value="0"; //it was the root node. Okay. Solved.
											}
											//else if //OTHERWISE the node does not exist. then it should be the governor
										
											else {
											//must find the correct head in the surface test set.
												String aux=tok;
												
												//System.out.println(line);
												//System.out.println(aux);
												
												String governor=findGovernor(sentenceCounter, tok,testSetHash); //search in the test set for the correc head. Which is basically the head of tok
												String deprel=findDeprelGovernor(sentenceCounter, tok,testSetHash);
												
												
												
												String auxDeprels="("+tok+")_Deprels:"+deprel;
												
												
												aux+="_"+governor;
												String correctHead=sentenceIdMatch.get(governor);
												if (correctHead!=null) { //ESTE SECTOR DE  CODIGO ES PARA METER PATRONES DE 2.
													correctHead+="_["+aux;
												}

												int cont2=0;
												while (correctHead==null) {
													cont2++;
													governor=findGovernor(sentenceCounter,governor,testSetHash);
													//auxDeprels+="_"+findDeprelGovernor(sentenceCounter, tok,testSetHash);
													auxDeprels+="_"+findDeprelGovernor(sentenceCounter, governor,testSetHash);
													aux+="_"+governor;
													
													/*if (governor==null) {
														correctHead="null_"+tok;

														//complete=false;
														//System.out.println(governor);
														//System.out.println(line);
														//System.out.println(tok);
														//System.out.println("Sale Null:"+line+"BUSQUEDA BACKWARDS <veremos como>");
													}
													else*/ 
													if (governor.equals("0")) {
														correctHead="0"+"_["+aux;
													}
													else {
														correctHead=sentenceIdMatch.get(governor);
														if (correctHead!=null) correctHead+="_["+aux;
													}
												}
												value=correctHead;
											}
										}
									}
									newLine+=value+"\t";
								}
								/*else if (cont==2) {
									System.out.println(tok);
								}*/
								else if (cont==6) {
									
									//String newTok=tok+"|id0="+reverseSentenceIdMatch.get(oldId);
									String newTok=tok+"|id0="+oldId;
									newLine+=newTok+"\t";
								}
								else if (cont==13) {
									newLine+=tok+"\n";
									//brW.write(newLine);
									
									sentencePath.put(id, newLine);
									
									newSentence+=newLine;
									newLine="";
								}
								else {
									newLine+=tok+"\t";
								}
								cont++;
							}
						}
							/*System.out.println(line);
							System.out.println(newLine);*/
							
						else {
							//ArrayList<String>
							paths.add(sentencePath);
							sentencePath=new HashMap<String,String>();
							brW.write(newSentence);
														
							newSentence="";

							sentenceCounter++;
							if (sentenceCounter<matchingIds.size()) {
								sentenceIdMatch=matchingIds.get(sentenceCounter);
								reverseSentenceIdMatch=reverseMatchingIds.get(sentenceCounter);
								brW.append("\n");
							}
							newLine="";
							
						}
						
					}
					brW.append("\n");
					brW.close();
					brP.close();
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		
			}	
		
		String finalOutput=solveInconsistencies(output,testSetHash,matchingIds);
		
		return finalOutput;
	}



	//very inefficient version, just for testing.
	private String solveInconsistencies(String previousOutput, ArrayList<CoNLLHash> testSetHash, ArrayList<HashMap<String, String>> matchingIds) {
		// TODO Auto-generated method stub
		
		String finalOutput="dsynt_partial_output_2.conll";
		
		ArrayList<CoNLLHash> deepPartialTreebank = CoNLLTreeConstructor.storeTreebank(previousOutput);
		
		Candidates candidates=new Candidates();
		candidates.calculateCandidates(deepPartialTreebank,testSetHash);
		candidates.selectCandidates(deepPartialTreebank,testSetHash);
		ArrayList<HashMap<String,ArrayList<String>>> selectedCandidates=candidates.getSelectedCandidates();
		ArrayList<HashMap<String,ArrayList<String>>> selectedSiblingCandidates=candidates.getSelectedSiblingCandidates();
		
		try {
			BufferedReader brP=new BufferedReader(new FileReader(previousOutput));
			try {
				BufferedWriter brW2=new BufferedWriter(new FileWriter(finalOutput));
				
				int sentenceCounter=0;
				while(brP.ready()) {
					String line=brP.readLine();
					String newLine="";
					
					if (line!=null && !line.equals("")) {
						String id="";
						int cont=0;
						StringTokenizer st=new StringTokenizer(line);

						String newDeprel=null;
						String newHead=null;
						while(st.hasMoreTokens()) {
							String tok=st.nextToken("\t");
							if (cont==0) {
								id=tok;
								newLine+=tok+"\t";
							}
							else if (cont==6) {
								newLine+=tok+"\t";
							}
							else if (cont==8) {
								if (tok.contains("_[")) {
									String calculatedHead=candidates.getCalculatedHead(tok);
									String surfaceHeadNode=candidates.getDeepCandidate(tok);
									HashMap<String,ArrayList<String>> localCandidates=selectedCandidates.get(sentenceCounter);
									HashMap<String,ArrayList<String>> localSiblingCandidates=selectedSiblingCandidates.get(sentenceCounter);
									ArrayList<String> selected=localCandidates.get(surfaceHeadNode);
									ArrayList<String> selectedSiblings=localSiblingCandidates.get(surfaceHeadNode);
									
									if (selectedSiblings.contains(id)) {
										newLine+=calculatedHead+"\t";
									}
									else {
										if (!selected.get(0).equals(id)) {
											newLine+=selected.get(0)+"\t";
										}
										else {
										
											String hyperNodeContent=generateHyperNodeIdsContent(tok);//TODO
											//System.out.println(hyperNodeContent);
										
											newLine+=calculatedHead+"\t";
										}
									}
								}
								//recalculate and check pattern if it is the case
								else  {
									newLine+=tok+"\t";
								}
							}
							else if (cont==10) {
								if (newDeprel!=null) {
									newLine+=newDeprel+"\t";
								}
								else {
									newLine+=tok+"\t";
								}
								newDeprel=null;
								
							}
							else if (cont==13) {
								newLine+=tok+"\n";
								brW2.write(newLine);
								newLine="";
							
							}
							else {
								newLine+=tok+"\t";
							}
							cont++;
						}
					}
					else {
						brW2.write("\n");
						newLine="";
						sentenceCounter++;
					}
					
					
				}
				
				
				brW2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return finalOutput;
	}

	//TODO
	private String generateHyperNodeIdsContent(String tok) {
		// TODO Auto-generated method stub
		//System.out.println(tok);
		return null;
	}



	public static String findGovernor(int sentenceCounter, String tok, ArrayList<CoNLLHash> testConllHash) {
		
		CoNLLHash sentence=testConllHash.get(sentenceCounter);
		if (tok.equals("0")) return "0";
		String governor=sentence.getHead(tok); //En el caso de heroe enfrentado (primera frase) no es el HEAD de tok si no el "hijo" de tok: ERROR
		return governor;
		
	}
	
	public static String findDeprelGovernor(int sentenceCounter, String tok, ArrayList<CoNLLHash> testConllHash) {
		
		CoNLLHash sentence=testConllHash.get(sentenceCounter);
		if (tok.equals("0")) return "ROOT";
		String governor=sentence.getDeprel(tok); //AQUI SE PODRIA HEREDAR EL DEPREL
		return governor;
	}


	private String producePartialOutput(String svmOutputPath, String pathTestSurface2) {
		// TODO Auto-generated method stub
		
		String output="dsynt_partial_output_1.conll";
		try {
			BufferedReader brP=new BufferedReader(new FileReader(svmOutputPath));
			BufferedReader brTest=new BufferedReader(new FileReader(pathTestSurface2));
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));

			try {
				int sentenceCounter=0;
				while(brTest.ready()) {
					String line=brTest.readLine(); 
					//if (line!=null && !(line.equals(""))) {
					if (!line.isEmpty()) {
						String svmLine=brP.readLine();
						//System.out.println(svmLine);
						if (svmLine.equals("1.0")) {
							bw.write(line+"\n");
						}
					}
					else {
						bw.write("\n");
						sentenceCounter++;
					}
					
				}
				bw.close();
				//while(brP.ready()) System.out.println(brP.readLine());
				//System.out.println(#number of sentences:sentenceCounter);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return output;
		
	}
	
	private void prepareHyperNodeLabelling(ArrayList<CoNLLHash> surfacePrunedTreebank) {
		
		//System.out.println();
		//System.out.println("---");
		
		//System.out.println(mdclass.toString()); //just to check if the model is properly loaded.
		
		Iterator<CoNLLHash> its=surfacePrunedTreebank.iterator(); 
		
		while(its.hasNext()) {
			CoNLLHash surfaceSentence=its.next();
			
			ArrayList<String> idsSurface=surfaceSentence.getIds();
			//System.out.println(idsSurface);
			
			Iterator<String> itSentence=idsSurface.iterator();
			while(itSentence.hasNext()) {
				String surfaceId=itSentence.next();
				//System.out.println(surfaceId+" "+ surfaceSentence.getForm(surfaceId));
				
				mdLabelclass.addLine(surfaceSentence.getForm(surfaceId), surfaceSentence.getLemma(surfaceId), surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), null, surfaceId, surfaceSentence, true, false);
			}	
		}
		
		mdLabelclass.closeBuffer(false);
	}



	private void detectHyperNodes(ArrayList<CoNLLHash> surfaceTreebank, ArrayList<CoNLLHash> deepTreebank, boolean train) {
		
		//this method is always for training, but anyway.
		//System.out.println();
		//System.out.println("---");
		
		//System.out.println(mdclass.toString()); //just to check if the model is properly loaded.
		
		int cont=0;
		int cont2=0;
		
		if (train) {
			
			if (surfaceTreebank.size()!=deepTreebank.size()) {
				try {
					throw new Exception("Error: Number of sentences do not match");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error: Number of sentences do not match");
				}	
			}
		}
		
		if (!train) {
			
			System.out.println("blind test set");
			detectHyperNodesTest(surfaceTreebank);
		}
		else {
		
		
		
		Iterator<CoNLLHash> its=surfaceTreebank.iterator();
		
		Iterator<CoNLLHash> itd=deepTreebank.iterator();
		
		while(its.hasNext() && itd.hasNext()) {
			
			CoNLLHash surfaceSentence=its.next();
			CoNLLHash deepSentence=itd.next();
			
			ArrayList<String> idsSurface=surfaceSentence.getIds();
			//Set<String> idsSurface=surfaceSentence.getIds();
			//System.out.println(idsSurface);
			//Set<Integer> idsSurfaceInt=generateSortedSet(idsSurface);
			//System.out.println(idsSurfaceInt);
			
			ArrayList<String> idsDeep=deepSentence.getIds();
			//Set<String> idsDeep=deepSentence.getIds();
			
			Iterator<String> itS=idsSurface.iterator();
			while(itS.hasNext()) {
			
				String surfaceId=itS.next();
				cont2++;
				//System.out.println(surfaceId+"");
				String lemma=surfaceSentence.getLemma(surfaceId.toString());
				
				
				Iterator<String> itD=idsDeep.iterator();
				boolean encontrado=false;
				while(itD.hasNext()) {
					String deepId=itD.next();
					String formDeep_lemma=deepSentence.getForm(deepId);
					String featsDeep=deepSentence.getFEAT(deepId);
					
					
					if (featsDeep.contains("id0="+surfaceId.toString())) {
					//if (formDeep_lemma.contains(lemma)) {
						
						StringTokenizer featsTokenizer=new StringTokenizer(featsDeep);
						while(featsTokenizer.hasMoreTokens()) {
							
							String feat=featsTokenizer.nextToken("|");
							if (feat.contains("id0")) {
								String idValue=feat.replaceAll("id0=","");
									if (idValue.contains("_")) {
										//String 
										int regex=0;
										for (int i=0; i<idValue.length();i++) {
											char c=idValue.charAt(i);
											if (c=='_') regex=i;
										}
										idValue=idValue.substring(0, regex);
										//System.out.println("--->"+idValue);
									}
									//System.out.println(surfaceId);
									//System.out.println(idValue);
									//FALTA VER QUE HACER CON LAS CORREFERENCIAS
								if (surfaceId.toString().equals(idValue) && !encontrado) {
									//System.out.println("hypernode("+surfaceId+"):"+lemma+" ---> (deepId: "+deepId+")");
									cont++;
									mdclass.addLine(surfaceSentence.getForm(surfaceId), surfaceSentence.getLemma(surfaceId), surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), surfaceId, surfaceSentence, true, train);
									if (train) mdLabelclass.addLine(surfaceSentence.getForm(surfaceId), surfaceSentence.getLemma(surfaceId), surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), deepSentence.getDeprel(deepId), surfaceId, surfaceSentence, true, train);
									encontrado=true;
									
								}
								
							}
						}
					}
					
				}
				if (!encontrado) {
									
					itD=idsDeep.iterator();
					int belongs=0;
					boolean found=false;
					while(itD.hasNext()) {

						String deepId=itD.next();
						String formDeep_lemma=deepSentence.getForm(deepId);
						String featsDeep=deepSentence.getFEAT(deepId);

						//else System.out.println("********"+surfaceId.charAt(1));
						if (featsDeep.contains("id1")&&featsDeep.contains(surfaceId)) {
							belongs=0;
							belongs=checkNodeBelonging(surfaceId,featsDeep);
							if ((belongs!=0)&&(!found)) {
								//System.out.println("Node ("+surfaceId+"):"+lemma +" -> hypernode:("+belongs+"):"+formDeep_lemma+" ---> (deepId: "+deepId+")");
								cont++;
								mdclass.addLine(surfaceSentence.getForm(surfaceId),surfaceSentence.getLemma(surfaceId),surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), surfaceId, surfaceSentence, false, train);
								//mdLabelclass.addLine(surfaceSentence.getForm(surfaceId),surfaceSentence.getLemma(surfaceId),surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), deepSentence.getDeprel(deepId),false, train);
								found=true;
							}
							else if (belongs!=0 && found) {
								//System.out.println("Error:"+cont2+" " + surfaceId+ "\t"+surfaceSentence.getForm(surfaceId) +"\t" + surfaceSentence.getHead(surfaceId) +"\t" + surfaceSentence.getDeprel(surfaceId));
							}
						}	
					}
					if (!found) {
						cont++;
						mdclass.addLine(surfaceSentence.getForm(surfaceId), surfaceSentence.getLemma(surfaceId),surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), surfaceId, surfaceSentence, false, train);
						/*if (t) {
							//System.out.println("jarl!");
						} else {
							t = true;
						}*/
						found=true;
						//mdLabelclass.addLine(surfaceSentence.getForm(surfaceId), surfaceSentence.getLemma(surfaceId),surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), deepSentence.getDeprel(deepId),false, train);
						//System.out.println("Node ("+surfaceId+"):"+lemma +" -> there is no hypernode");
					}
				}
			}
			//System.out.println("---");
			//System.out.println("");
		}
		//System.out.println("\ncont:"+cont);
		//System.out.println("cont2:"+cont2);
		mdclass.closeBuffer(train);
		}
	}

	private void detectHyperNodesTest(ArrayList<CoNLLHash> surfaceTreebank) {
		
		//System.out.println();
		//System.out.println("---");
		
		//System.out.println(mdclass.toString()); //just to check if the model is properly loaded.
		
		Iterator<CoNLLHash> its=surfaceTreebank.iterator();
		
		while(its.hasNext()) {
			
			CoNLLHash surfaceSentence=its.next();
			
			ArrayList<String> idsSurface=surfaceSentence.getIds();
			//Set<String> idsSurface=surfaceSentence.getIds();
			//Set<Integer> idsSurfaceInt=generateSortedSet(idsSurface);
			//System.out.println(idsSurfaceInt);
			
			//Set<String> idsDeep=deepSentence.getIds();
			
			Iterator<String> itS=idsSurface.iterator();
			while(itS.hasNext()) {
			
				String surfaceId=itS.next();
				//System.out.println(surfaceId+"");
				String lemma=surfaceSentence.getLemma(surfaceId.toString());
				
				
				//Iterator<String> itD=idsDeep.iterator();
				boolean encontrado=false;
							//System.out.println("hypernode("+surfaceId+"):"+lemma+" ---> (deepId: "+deepId+")");
				mdclass.addLine(surfaceSentence.getForm(surfaceId), surfaceSentence.getLemma(surfaceId), surfaceSentence.getPOS(surfaceId), surfaceSentence.getFEAT(surfaceId), surfaceSentence.getDeprel(surfaceId), surfaceId, surfaceSentence, true, false);
			
							
			}
		}
		//System.out.println("\ncont:"+cont);
		//System.out.println("cont2:"+cont2);
		mdclass.closeBuffer(false);
	}

	private int checkNodeBelonging(String surfaceId, String feats) {
		// TODO Auto-generated method stub
		StringTokenizer st=new StringTokenizer(feats);
		
		int id0=0;
		while(st.hasMoreTokens()) {
			String feat=st.nextToken("|");
			if (feat.contains("id0")) {
				String idValue=feat.replaceAll("id0=","");
					if (idValue.contains("_")) {
						//String 
						int regex=0;
						for (int i=0; i<idValue.length();i++) {
							char c=idValue.charAt(i);
							if (c=='_') regex=i;
						}
						idValue=idValue.substring(0, regex);
					}
					id0=Integer.parseInt(idValue);
		
					for (int i=0;i<5;i++) {
						//System.out.println("id"+i+"="+surfaceId+"|");
						if (feats.contains("id"+i+"="+surfaceId+"|"))
							return id0;
					}
				}
			}
			return 0;
		}
	
	
	
	private void postProcessing(String output) {
		// TODO Auto-generated method stub
		String postOutput="dsynt_final_output_post.conll";
		
		try {
			BufferedReader br=new BufferedReader(new FileReader(output));
			BufferedWriter bw=new BufferedWriter(new FileWriter(postOutput));
			
			ArrayList<CoNLLHash> deepOutput = CoNLLTreeConstructor.storeTreebank(output);
			ArrayList<CoNLLHash> surfaceInput = CoNLLTreeConstructor.storeTreebank(this.pathTestSurface);
			
			ArrayList<String> zeroSubjects=new ArrayList<String>();
			boolean isZero=false;
			int tokenCounter=1;
			int sentenceCounter=0;
			while (br.ready()) {
			
				String line=br.readLine();
				if (line.isEmpty()) {
					
						Iterator<String> itZSubj=zeroSubjects.iterator();
						while(itZSubj.hasNext()) {
							String newSubject=tokenCounter+"\t"+itZSubj.next();
							bw.write(newSubject+"\n");
							tokenCounter++;
						}
						zeroSubjects=new ArrayList<String>();
						sentenceCounter++;
						tokenCounter=1;
						bw.write("\n");
				}
				else {
					
					
					String feats=deepOutput.get(sentenceCounter).getFEAT(tokenCounter+"");
					String id0=CoNLLHash.getSubFeat(feats, "id0");
					CoNLLHash surfaceSentence=surfaceInput.get(sentenceCounter);
					String ssyntdeprel=surfaceSentence.getDeprel(id0);
					
					line=putLemmaInForm(line,surfaceSentence.getLemma(id0));
					
					if (ssyntdeprel.equals("analyt_fut")) {
						line=addFeats(line,"tense=FUT");
					}
					
					if (ssyntdeprel.equals("analyt_pass")) {
						line=addFeats(line,"voice=PASS");
					}
					if (ssyntdeprel.equals("analyt_perf")) {
						line=addFeats(line,"tense=PAST");
					}
					if (ssyntdeprel.equals("analyt_progr")) {
						line=addFeats(line,"tem_constituency=PROGR");
					}
					if (ssyntdeprel.equals("analyt_refl_pass")) {
						line=addFeats(line,"voice=PASS");
					}
					if (ssyntdeprel.equals("analyt_refl_lex")) {
						line=addReflexiveSe(line);
					}
					
					
					String child=detChild(surfaceSentence,id0);
					if (child!=null) {
					 	if (child.contains("un")) {
					 		line=addFeats(line,"definiteness=INDEF");
					 	}
					 	else {
					 		line=addFeats(line,"definiteness=DEF");
					 	}
					}
					
					
					if (line.contains("VV") && line.contains("number") && line.contains("person")) {
						//it is a verb, let's check whether there are zero subjects.
						
						//String feats=deepOutput.get(sentenceCounter).getFEAT(tokenCounter+"");
						//String id0=CoNLLHash.getSubFeat(feats, "id0");
						//CoNLLHash surfaceSentence=surfaceInput.get(sentenceCounter);
						isZero=areZeroSubjects(surfaceSentence,id0);
						if (isZero) {
							String pers=CoNLLHash.getSubFeat(feats, "person");
							String numb=CoNLLHash.getSubFeat(feats, "number");
							String newSubject="pers"+pers+"_"+"num"+numb+"\t"+"pers"+pers+"_"+"num"+numb+"\t"+"_"+"\t"+"NN"+"\t"+"NN"+"\t"+"dpos=N|"+"id0="+id0+"_prosubj|"+"number_coref="+numb+"|spos_coref=noun"+"\t"+"_"+"\t"+tokenCounter+"\t"+"_"+"\t"+"I\tI\t_\t_";
							zeroSubjects.add(newSubject);
							//bw.write(line+"\n");
						}
						else {
							//bw.write(line+"\n");
						}
						
					}
					bw.write(line+"\n");
					tokenCounter++;
				}
					
			}
			bw.close();
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private String putLemmaInForm(String line, String lemma) {
		// TODO Auto-generated method stub
		String output="";
		StringTokenizer st=new StringTokenizer(line);
		int cont=1;
		while(st.hasMoreTokens()) {
			String col=st.nextToken("\t");
			if (cont==2) {
				output+=lemma+"\t";
			}
			else if (cont==14) {
				output+=col;
			}
			else {
				output+=col+"\t";
			}
			
			cont++;
		}
		
		return output;
	}



	private String detChild(CoNLLHash surfaceSentence, String id0) {
		// TODO Auto-generated method stub
		
		ArrayList<String> ids=surfaceSentence.getUnsortedIds();
		Iterator<String> itId=ids.iterator();
		while(itId.hasNext()) {
			String id=itId.next();
			String headId=surfaceSentence.getHead(id);
			if (headId.equals(id0)) {
				String deprel=surfaceSentence.getDeprel(id);
				if (deprel.equals("det")) return surfaceSentence.getForm(id);
			}
		}
		return null;
	}



	private String addReflexiveSe(String line) {
		// TODO Auto-generated method stub
		String output="";
		StringTokenizer st=new StringTokenizer(line);
		int cont=1;
		while(st.hasMoreTokens()) {
			String col=st.nextToken("\t");
			if ((cont==2)||(cont==3)) {
				output+=col+"\t";
				//output+=col+"se"\t"; //UNCOMENT THIS ONE IF WE WANT TO ADD THE REFLEXIVE SE
			}
			else if (cont==14) {
				output+=col;
			}
			else {
				output+=col+"\t";
			}
			
			cont++;
		}
		
		return output;
	}



	private String addFeats(String line, String string) {
		// TODO Auto-generated method stub
		String output="";
		StringTokenizer st=new StringTokenizer(line);
		int cont=1;
		while(st.hasMoreTokens()) {
			String col=st.nextToken("\t");
			if (cont==7) {
				output+=string+"|"+col+"\t";
			}
			else if (cont==14) {
				output+=col;
			}
			else {
				output+=col+"\t";
			}
			
			cont++;
		}
		
		return output;
		
	}


	/*private Set<Integer> generateSortedSet(Set<String> idsSurface) {
		// TODO Auto-generated method stub
			
		Set<Integer> out= new TreeSet<Integer>();
		Iterator<String> it=idsSurface.iterator();
		
		
		while(it.hasNext()) {
			String aux=it.next();
			
			try{
				int i2=Integer.parseInt(aux);
				out.add(new Integer(i2));
			}
			catch(NumberFormatException e) {
				//e.printStackTrace();
			}
			//Integer i=Integer.valueOf(aux);
			
		}
		return out;
	}*/



	



	private boolean isReflexiveSe(CoNLLHash surfaceSentence, String id0) {
		// TODO Auto-generated method stub
		ArrayList<String> siblings=surfaceSentence.getSiblings(id0);
		//System.out.println("SurfId:"+id0);
		//System.out.println(siblings);
		Iterator<String> itSib=siblings.iterator();
		boolean thereIsSubject=false;
		while(itSib.hasNext()) {
			String sib=itSib.next();
			String form=surfaceSentence.getForm(sib);
			String deprel=surfaceSentence.getDeprel(sib);
			//System.out.println("   "+sib+" "+deprel);
			if (form.equals("se")&&(deprel.equals("aux_refl_lex"))) {
				return true;
			}
		}	
		return false;
	}



	private boolean areZeroSubjects(CoNLLHash surfaceSentence, String id0) {
		// TODO Auto-generated method stub
		
		ArrayList<String> siblings=surfaceSentence.getSiblings(id0);
		//System.out.println("SurfId:"+id0);
		//System.out.println(siblings);
		Iterator<String> itSib=siblings.iterator();
		boolean thereIsSubject=false;
		while(itSib.hasNext()) {
			String sib=itSib.next();
			String deprel=surfaceSentence.getDeprel(sib);
			//System.out.println("   "+sib+" "+deprel);
			if (deprel.equals("subj")) {
				thereIsSubject=true;
			}
		}	
		return !thereIsSubject;
	}



	public static void main(String [] args) {
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("                     Tree-Transducer 1.0                             ");
		System.out.println("            From Surface Representation to Deep Representation                             ");
		System.out.println("-----------------------------------------------------------------------------");
		//System.out.println("                     Miguel Ballesteros and Leo Wanner                           ");
		//System.out.println("                     @TALN Research Group                             ");
		//System.out.println("                     taln.upf.edu                             ");
		//System.out.println("                     @Pompeu Fabra University                             ");
		//System.out.println("-----------------------------------------------------------------------------");
		
		 
	        /*Option testingOpt = OptionBuilder.withArgName("testing")
	                .hasArg(false)
	                .isRequired(false)
	                .withDescription("testing option")
	                .withLongOpt("testing")
	                .create("p");*/

	        Option ssOpt = OptionBuilder.withArgName("ssynt-treebank")
	                .hasArg(true)
	                .isRequired(false)
	                .withDescription("ssynt treebank")
	                .withLongOpt("tssynt")
	                .create("s");
	        
	        Option dsOpt = OptionBuilder.withArgName("dsynt-treebank")
	                .hasArg(true)
	                .isRequired(false)
	                .withDescription("dsynt treebank")
	                .withLongOpt("tdsynt")
	                .create("d");
	        
	        Option ssTOpt = OptionBuilder.withArgName("ssynt-test")
	                .hasArg(true)
	                .isRequired(false)
	                .withDescription("ssyntest treebank")
	                .withLongOpt("tssyntest")
	                .create("st");
	        
	        Option tOpt = OptionBuilder.withArgName("training")
	                .hasArg(true)
	                .isRequired(false)
	                .withDescription("training")
	                .withLongOpt("training")
	                .create("t");
	        
	        /*Option dsTOpt = OptionBuilder.withArgName("dsynt-test")
	                .hasArg(true)
	                .isRequired(false)
	                .withDescription("dsyntest treebank")
	                .withLongOpt("tdsyntest")
	                .create("dt");*/


			// create Options object
			Options options = new Options();
			//options.addOption(sentenceOpt);
			//options.addOption(testingOpt);
			options.addOption(ssOpt);
			options.addOption(dsOpt);
			options.addOption(ssTOpt);
			options.addOption(tOpt);
			//options.addOption(dsTOpt);
			
		
			 // create the command line parser
	        CommandLineParser parser = new BasicParser();
	        try {
	            // parse the command line arguments
	            CommandLine line = parser.parse( options, args );
	            boolean training=true;
	            if (line.getOptionValue("t").equals("0")){
	            	TransducerSurfToDeep.needToTrain=false;
	            }
	            
	            if (training) {
	            	TransducerSurfToDeep transducer=new TransducerSurfToDeep(line.getOptionValue("s"),line.getOptionValue("d"), line.getOptionValue("st"));
	            	//System.out.println(line.getOptionValue("s") +" " +line.getOptionValue("d") +" " + line.getOptionValue("st") +" " + line.getOptionValue("dt"));
	        		transducer.training();
	        		transducer.testing();
	        		//String goldStandard, String inputSurface, String output
	        		/*Evaluation e=new Evaluation("dsynt_test.conll","dsynt_partial_output_3.conll");
	        		e.HyperNodeAccuracy();
	        		e.nodeLabelAndAttachment();*/
	        		
	        		
	        		transducer.postProcessing("dsynt_final_output.conll"); //SPANISH CASE
	        		
	            }
	            /*else if (testing){
	            	TransducerSurfToDeep transducer=new TransducerSurfToDeep(line.getOptionValue("s"),line.getOptionValue("d"), line.getOptionValue("m"));
	        		transducer.testing();
	            }*/
	            else {
	            	throw new ParseException("-");
	            }
	            

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
	            formatter.printHelp("Tree Transducer", options, true);
	       }
			
		//(if (args)
		
		//transducer.training();
		
		//
		//transducer.test();
		
	}



	




}
