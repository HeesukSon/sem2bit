package heesuk.percom.sherlock.io.msg;

public class ModificationCandidate{
	private String field;
	private String update;
	private float prob;
	
	public ModificationCandidate(){
		
	}
	
	public ModificationCandidate(String field, String update){
		this();
		this.field = field;
		this.update = update;
		this.prob = 0f;
	}
	
	public ModificationCandidate(String field, String update, float prob){
		this(field, update);
		this.prob = prob;
	}
	
	public ModificationCandidate(ModificationCandidate candidate){
		this(candidate.getField(), candidate.getUpdate(), candidate.getProb());
	}
	
	public boolean sameWith(ModificationCandidate candidate){
		if(this.field.equals(candidate.getField()) && this.update.equals(candidate.getUpdate()))
			return true;
		else
			return false;
	}
	
	public String toString(){
		return "<"+update+" "+field+" : "+prob+">";
	}
	
	public String toStringWithoutWeight(){
		return "<"+update+" "+field+">";
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
