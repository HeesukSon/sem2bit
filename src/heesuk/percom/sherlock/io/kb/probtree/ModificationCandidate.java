package heesuk.percom.sherlock.io.kb.probtree;

public class ModificationCandidate{
	private String field;
	private String update;
	private float prob;
	
	public ModificationCandidate(){
		
	}
	
	public ModificationCandidate(String field, String update, float prob){
		this();
		this.field = field;
		this.update = update;
		this.prob = prob;
	}
	
	public String toString(){
		return "<"+update+" "+field+" : "+prob+">";
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public float getProb() {
		return prob;
	}

	public void setProb(float prob) {
		this.prob = prob;
	}
}
