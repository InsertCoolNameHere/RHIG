package events;

public class InitRigRequest implements Event{
	
	private String fsName;
	
	public String getFsName() {
		return fsName;
	}
	public void setFsName(String fsName) {
		this.fsName = fsName;
	}
	
	
	public InitRigRequest(String fsName) {
		this.fsName = fsName;
	}
	
	
}
