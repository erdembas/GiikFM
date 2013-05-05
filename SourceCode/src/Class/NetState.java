package Class;

import android.R.bool;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetState {
	public static Boolean Kontrol(Context CTX) {
		ConnectivityManager connectivity = (ConnectivityManager) CTX.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) 
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) 
                for (int i = 0; i < info.length; i++) 
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
	}
	
}
