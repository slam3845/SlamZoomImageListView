package slam.com.slamzoomimagelistview.Network;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import slam.com.slamzoomimagelistview.Constants;
import slam.com.slamzoomimagelistview.Data.HotelData;

/**
 * Created by slam on 7/7/2018.
 */
public class GetHotelDataAsyncTask extends AsyncTask<String, Void, String> {
    static private final String TAG = GetHotelDataAsyncTask.class.getSimpleName();

    private Activity _activity;
    private Handler _handler;

    public GetHotelDataAsyncTask(Activity activity, Handler handler) {
        this._activity = activity;
        this._handler = handler;
    }

    /**
     * @param params
     * @return
     */
    protected String doInBackground(String... params) {
        try {
            String urlHotels = params[0];
            RequestURL requestURL = new RequestURL();

            /*
             * Use either sendGetRequest() or readUrl(), both method work!
             */
            // String hotelsData = requestURL.sendGetRequest(urlHotels);
            String hotelsData = requestURL.readUrl(urlHotels);
            return hotelsData;
        }
        catch (Exception ex) {
            Log.e(TAG,".doInBackground() Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * @param jsonHotelsData
     */
    protected void onPostExecute(String jsonHotelsData) {

        ArrayList<HotelData> arrListHotels = new ArrayList<HotelData>();

        /*
         * Parse the JSON data into ArrayList<HotelData> object.
         */
        if (jsonHotelsData != null && _handler != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonHotelsData);
                JSONArray jsonArrayHotels = jsonObject.getJSONArray("hotels");

                if (jsonArrayHotels != null) {
                    int hotelsCount = jsonArrayHotels.length();

                    for (int iX = 0; iX < hotelsCount; iX++) {
                        JSONObject jsonHotel = (JSONObject) jsonArrayHotels.get(iX);
                        String imageUrl = jsonHotel.optString("image_url");
                        String name = jsonHotel.optString("name");

                        HotelData hotelData = new HotelData(imageUrl, name);
                        arrListHotels.add(hotelData);
                    }
                }
                /*
                 * Return the ArrayList<HotelData> object to the caller via the handler.
                 */
                Message message = _handler.obtainMessage(
                    Constants.HANDLER_MSG_GET_HOTELS_DATA, 0, 0, arrListHotels
                );
                _handler.sendMessage(message);
            }
            catch (JSONException e) {
                Log.e(TAG, ".onPostExecute() parses JSON data Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}