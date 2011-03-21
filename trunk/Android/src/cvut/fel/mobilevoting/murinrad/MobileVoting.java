package cvut.fel.mobilevoting.murinrad;

import android.app.Application;

public class MobileVoting extends Application {
	public static MobileVoting app;
	private MobileVoting() {
		super();
		
	}
	@Override
	public  void onCreate() {
		app = this;
		
	}

}
