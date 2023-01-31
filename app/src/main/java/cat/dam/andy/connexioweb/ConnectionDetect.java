package cat.dam.andy.connexioweb;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectionDetect {

    private final Context mContext;

    public ConnectionDetect(Context context) {
        this.mContext = context;
    }

    public boolean haveConnection() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            //Per versions igual o superiors a Lollipop podem utilitzar el mètode getActiveNetwork() i la classe NetworkCapabilities
            final Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                final NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        }
        return false;
    }
}
