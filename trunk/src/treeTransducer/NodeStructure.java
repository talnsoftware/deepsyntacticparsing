/**
 * 
 */
package treeTransducer;

/**
 * @author Miguel Ballesteros
 * Universitat Pompeu Fabra.
 * Structure that stores the info associated to a Node.
 *
 */
public class NodeStructure {
	
	private String form; //word
	private String deprel; //dependency relation to this node from the head.
	
	private String tense;
	private String sLex;
	private String number;
	private String person;
	private String finiteness;
	private String dpos;
	private String mood;
	private String spos;
	private String lemma;
	private String id0;
	private String id1;
	private String id2;
	
	

	private String id;
	
	

	private String thematicity;
	private String pos;
	private String gender;
	
	private boolean first_conj;
	private boolean quotative;
	
	
	
	public NodeStructure() {
		this(null,null, null,null,null,null, null, null, null,null,null,null,null,null,null,null,null,null,false,false);
	}
	
	
	
	public NodeStructure(String tense, String sLex, String number,
			String person, String finiteness, String dpos, String mood,
			String spos, String lemma, String id0, String id1, String id2, String id,
			String thematicity, String pos, String gender, String form, String deprel, boolean first_conj,
			boolean quotative) {
		super();
		this.tense = tense;
		this.sLex = sLex;
		this.number = number;
		this.person = person;
		this.finiteness = finiteness;
		this.dpos = dpos;
		this.mood = mood;
		this.spos = spos;
		this.lemma = lemma;
		this.id0 = id0;
		this.id1 = id1;
		this.id2 = id2;
		this.id = id;
		this.thematicity = thematicity;
		this.pos = pos;
		this.gender = gender;
		this.first_conj = first_conj;
		this.quotative = quotative;
		this.form=form;
		this.deprel=deprel;
	}

	
	public String getForm() {
		return form;
	}



	public void setForm(String form) {
		this.form = form;
	}



	public String getDeprel() {
		return deprel;
	}



	public void setDeprel(String deprel) {
		this.deprel = deprel;
	}



	public String getId1() {
		return id1;
	}



	public void setId1(String id1) {
		this.id1 = id1;
	}



	public String getId2() {
		return id2;
	}



	public void setId2(String id2) {
		this.id2 = id2;
	}



	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getPos() {
		return pos;
	}


	public void setPos(String pos) {
		this.pos = pos;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	


	public String getTense() {
		return tense;
	}


	public void setTense(String tense) {
		this.tense = tense;
	}


	public String getsLex() {
		return sLex;
	}


	public void setsLex(String sLex) {
		this.sLex = sLex;
	}


	public String getNumber() {
		return number;
	}


	public void setNumber(String number) {
		this.number = number;
	}


	public String getPerson() {
		return person;
	}


	public void setPerson(String person) {
		this.person = person;
	}


	public String getFiniteness() {
		return finiteness;
	}


	public void setFiniteness(String finiteness) {
		this.finiteness = finiteness;
	}


	public String getDpos() {
		return dpos;
	}


	public void setDpos(String dpos) {
		this.dpos = dpos;
	}


	public String getMood() {
		return mood;
	}


	public void setMood(String mood) {
		this.mood = mood;
	}


	public String getSpos() {
		return spos;
	}


	public void setSpos(String spos) {
		this.spos = spos;
	}


	public String getLemma() {
		return lemma;
	}


	public void setLemma(String lemma) {
		this.lemma = lemma;
	}


	public String getId0() {
		return id0;
	}


	public void setId0(String id0) {
		this.id0 = id0;
	}


	public String getThematicity() {
		return thematicity;
	}


	public void setThematicity(String thematicity) {
		this.thematicity = thematicity;
	}


	public boolean isFirst_conj() {
		return first_conj;
	}


	public void setFirst_conj(boolean first_conj) {
		this.first_conj = first_conj;
	}


	public boolean isQuotative() {
		return quotative;
	}


	public void setQuotative(boolean quotative) {
		this.quotative = quotative;
	}
	
	
	

}
