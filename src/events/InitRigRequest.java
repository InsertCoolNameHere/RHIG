package events;

public class InitRigRequest implements Event{
	
	private String fsName;
	private boolean use_local = false;
	private String local_dir = "";
	
	public String getFsName() {
		return fsName;
	}
	public void setFsName(String fsName) {
		this.fsName = fsName;
	}
	
	
	public InitRigRequest(String fsName) {
		this.fsName = fsName;
	}
	
	public InitRigRequest(String fsName, boolean use_local, String local_dir) {
		this.fsName = fsName;
		this.use_local = use_local;
		this.local_dir = local_dir;
	}
	public boolean isUse_local() {
		return use_local;
	}
	public void setUse_local(boolean use_local) {
		this.use_local = use_local;
	}
	public String getLocal_dir() {
		return local_dir;
	}
	public void setLocal_dir(String local_dir) {
		this.local_dir = local_dir;
	}
	
	
}
