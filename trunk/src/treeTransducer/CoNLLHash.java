/**
 * 
 */
package treeTransducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * @author Miguel Ballesteros
 * Universitat Pompeu Fabra
 * 
 * 
 * CoNLL 2009 format.
 * ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs
 */
public class CoNLLHash {

    private HashMap<String, ArrayList<String>> hash;
    
    //private HashMap<String, Boolean> hyperNodes=new HashMap<String, Boolean>();

    public CoNLLHash() {
        hash = new HashMap<String, ArrayList<String>>();
    }

    public void addLine(String aux) {

        StringTokenizer st = new StringTokenizer(aux);
        ArrayList<String> columns = new ArrayList<String>();
        if (st.hasMoreTokens()) {
            String id = st.nextToken("\t");
            columns.add(id);
            hash.put(id, columns);
            while (st.hasMoreTokens()) {
                columns.add(st.nextToken("\t"));
            }
        }
    }
    
    public String getId(String idW) {
    	
        return hash.get(idW.toString()).get(0);
    }

    public String getForm(String idW) {
    	if (hash.get(idW.toString())==null) return "";
        return hash.get(idW.toString()).get(1);
    }
    
    public String getLemma(String idW) {

        return hash.get(idW.toString()).get(2);
    }
    
    public String getPLemma(String idW) {

        return hash.get(idW.toString()).get(3);
    }
    
    public String getPOS(String idW) {

        return hash.get(idW.toString()).get(4);
    }
    
    public String getPPOS(String idW) {

        return hash.get(idW.toString()).get(5);
    }
    
    public String getFEAT(String idW) {
    	
    	if (hash.get(idW.toString())==null) return "";
        return hash.get(idW.toString()).get(6);
    }
    
    public String getPFEAT(String idW) {

        return hash.get(idW.toString()).get(7);
    }
    

    public String getHead(String idW) {
    	try{
    		return hash.get(idW.toString()).get(8);
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public String getPHead(String idW) {

        return hash.get(idW.toString()).get(9);
    }

    public String getDeprel(String idW) {
    	//if (hash.get(idW.toString())!=null)
    	//System.out.println(this.getIds());
    	try{
    	return hash.get(idW.toString()).get(10);
    	}catch(Exception e){
    		System.out.println(idW);
    		System.out.println(hash);
    		e.printStackTrace();
    		return null;
    	}
    	//return "";
    }
    
    public String getPDeprel(String idW) {

        return hash.get(idW.toString()).get(11);
    }
    
    //THE SEMANTIC ATTRIBUTES WILL BE INCLUDED IN THE FUTURE.

    public boolean hasLine(String id) {
        return hash.containsKey(id);
    }

    public ArrayList<String> getIds() {
        Set<String> keys = hash.keySet();
        ArrayList<String> keysInt = new ArrayList<String>();
        Iterator<String> it = keys.iterator();
        Integer max=0;
        while (it.hasNext()) {
            //keysInt.add(it.next());
        	String s=it.next();
        	Integer sI=Integer.parseInt(s);
        	if (sI>max) max=sI;
        }
        for(int i=1;i<=max;i++){
        	keysInt.add(""+i);
        	
        }
        //System.out.println(keysInt);
        return keysInt;
    }
    
    public ArrayList<String> getUnsortedIds() {
        Set<String> keys = hash.keySet();
        ArrayList<String> keysInt = new ArrayList<String>();
        Iterator<String> it = keys.iterator();
        Integer max=0;
        while (it.hasNext()) {
            keysInt.add(it.next());
        }
        return keysInt;
    }

	public ArrayList<String> getSiblings(String head) {
		// TODO Auto-generated method stub
		ArrayList<String> ids=this.getIds();
		ArrayList<String> siblings=new ArrayList<String>();
		Iterator<String> it=ids.iterator();
		while(it.hasNext()) {
			String id=it.next();
			String headId=this.getHead(id);
			if (headId.equals(head)) siblings.add(id);
		}
		return siblings;
	}
	  
    /*public Set<String> getIds() {
    Set<String> keysInt = new Set<String>();
    Iterator<String> it = keys.iterator();
    Integer max=0;
    while (it.hasNext()) {
        keysInt.add(it.next());

    }
    //System.out.println(keysInt);
    return keysInt;
    }*/
	

	
	public static String getSubFeat(String feats, String subFeat) {
		String id0="";
		StringTokenizer st=new StringTokenizer(feats);
		while(st.hasMoreTokens()) {
			String tok=st.nextToken("|");
			if (tok.contains(subFeat+"=")) {
				String[] split=tok.split("=");
				id0=split[1];
			}
		}
		
		return id0;
	}
	

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return hash.isEmpty();
	}   
}
