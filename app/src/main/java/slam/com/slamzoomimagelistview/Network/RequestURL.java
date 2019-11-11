package slam.com.slamzoomimagelistview.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by slam on 7/7/2018.
 */
public class RequestURL {
    static private final String TAG = RequestURL.class.getSimpleName();

    private URL _url;
    private int _status;

    public URL getUrl() {
        return _url;
    }

    public int getStatus() {
        return _status;
    }

    public String readUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
//            String urlEncoded = URLEncoder.encode(strUrl, "UTF-8");
//            _url = new URL(urlEncoded);

            // https://hipmunk.com/mobile/coding_challenge
            _url = new URL(strUrl);

            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.connect();    // Connecting to url

            _status = urlConnection.getResponseCode();
            Log.d(TAG,"status = " + _status + "; RequestURL.readUrl(" + _url + ")");

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d(TAG,"!@# " + TAG + ".readUrl()" + data.toString());
            br.close();
        }
        catch (MalformedURLException ex) {
            Log.e(TAG, ".readUrl() MalformedURLException: " + ex.toString());
            ex.printStackTrace();
        }
        catch (Exception ex) {
            Log.e(TAG, ".readUrl() Exception: " + ex.toString());
            ex.printStackTrace();
        }
        finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public String sendGetRequest(String strUrl) {
        HttpURLConnection httpUrlConnection = null;
        try {
            _url = new URL(strUrl);
            httpUrlConnection = (HttpURLConnection)_url.openConnection();

            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = httpUrlConnection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(httpUrlConnection.getInputStream())
                );

                String responseLine;
                StringBuffer response = new StringBuffer();

                while ((responseLine = responseReader.readLine()) != null) {
                    response.append(responseLine + "\n");
                }
                responseReader.close();
                return response.toString();
            }
        }
        catch (MalformedURLException ex) {
            Log.e(TAG, ".readUrl() MalformedURLException: " + ex.toString());
            ex.printStackTrace();
        }
        catch (Exception ex) {
            Log.e(TAG, ".readUrl() Exception: " + ex.toString());
            ex.printStackTrace();
        }
        finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }
        return null;
    }
}
