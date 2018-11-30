
public class Candidate implements Comparable<Candidate>{
	private String name;
	private int vote = 0;
	public Candidate(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCount() {
		return vote;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public void addVote() {
		vote ++;
	}
	
	@Override
	public String toString() {
		return this.name +", " + vote;
	}
	
	@Override
	public int compareTo(Candidate o) {
		return (this.getCount() - o.getCount());
	}
	
	

}
