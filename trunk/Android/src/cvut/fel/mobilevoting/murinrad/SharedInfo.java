package cvut.fel.mobilevoting.murinrad;

public class SharedInfo {
	private int logins = 0;

	public int getLogins() {
		return logins;
	}

	public void incrementLogin() {
		logins++;
	}

	private static final SharedInfo sharedInfo = new SharedInfo();

	private SharedInfo() {

	}
	
	public static SharedInfo getInstance() {
		return sharedInfo;
	}

}
