package slam.com.slamzoomimagelistview.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import slam.com.slamzoomimagelistview.Constants;
import slam.com.slamzoomimagelistview.Data.HotelData;
import slam.com.slamzoomimagelistview.Network.GetHotelDataAsyncTask;
import slam.com.slamzoomimagelistview.R;
import slam.com.slamzoomimagelistview.Utils.UIHelper;

/**
 * Created by slam on 7/7/2018.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Glide works with Android’s ImageView, and it can load the image in one line.
     *
     * Glide uses HttpUrlConnection as its default network stack.  The two key benefits of
     * Glide are:
     *  - smoother loading when scrolling through a view that contains images.
     *  - Perhaps more importantly, animated GIF support.
     *
     * Glide caches both a version of the image in its original size as well as one that's
     * the size of its intended ImageView. This means Glide loads the image noticeably faster.
     */
    public enum EnumActions {
        USE_ASYNC_DOWNLOADER(0), USE_ASYNC_DOWNLOADER_WITH_CACHE(1), USE_GLIDE_LIBRARY(2), USE_PICASSO_LIBRARY(3);

        int _value;
        private EnumActions(int value) {
            this._value = value;
        }

        @Override
        public String toString() {
            switch (_value) {
                case 0: return "Method: Async Downloader";
                case 1: return "Method: Async Downloader with Cache";
                case 2: return "Method: Glide Library";
                case 3: return "Method: Picasso Library";
            }
            return "Unknown";
        }
    }

    private Activity _activity;
    private ArrayList<HotelData> _arrListHotels = null;

    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HANDLER_MSG_GET_HOTELS_DATA:
                    // The ArrayList<HotelData> object coming from
                    // GetHotelDataAsyncTask.onPostExecute()
                    //
                    _arrListHotels = (ArrayList<HotelData>)msg.obj;

                    HotelData.AddMoreHotelInfo(_arrListHotels);

                    // Enable or disable the UI buttons accordingly.
                    Button btnAyncDownloader = findViewById(R.id.btn_use_aync_downloader);
                    Button btnGlideLibrary = findViewById(R.id.btn_use_glide_library);
                    btnAyncDownloader.setEnabled(_arrListHotels != null && !_arrListHotels.isEmpty());
                    btnGlideLibrary.setEnabled(_arrListHotels != null && !_arrListHotels.isEmpty());
                    break;
            }
        }
    };

    public MainActivity() {
        String[] dataTransfer = new String[1];
        dataTransfer[0] = Constants.HOTELS_DATA_URL;

        // The Async method GetHotelDataAsyncTask() gets the hotel info (image + hotel name)
        // as JSON data.  The JSON data will be parsed in GetHotelDataAsyncTask.onPostExecute()
        // and stored in an ArrayList<HotelData>.
        //
        // After the JSON data has done parsing, the ArrayList<HotelData> object will be sent
        // back to this MainActivity via the handler with Constants.HANDLER_MSG_GET_HOTELS_DATA
        // for further process.
        //
        GetHotelDataAsyncTask getHotelDataAsyncTask = new GetHotelDataAsyncTask(this, _handler);
        getHotelDataAsyncTask.execute(dataTransfer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _activity = this;
        UIHelper.setActionBarText(this, "Hotels List", 18, Color.YELLOW);

        /*
         * Download the image with the Aync Downloader
         */
        Button btnAyncDownloader = findViewById(R.id.btn_use_aync_downloader);
        btnAyncDownloader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(_activity, ImagesListViewActivity.class);

                // HotelData class must implement Serializable in order to pass it to
                // another activity via the Intent object.
                //
                intent.putExtra("HotelsData", _arrListHotels);
                intent.putExtra("ImageDownloadMethod", EnumActions.USE_ASYNC_DOWNLOADER);
                startActivity(intent);
            }
        });

        /*
         * Download the image with the Aync Downloader with caching
         */
        Button btnAyncDownloaderWithCache = findViewById(R.id.btn_use_aync_downloader_with_cache);
        btnAyncDownloaderWithCache.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(_activity, ImagesListViewActivity.class);

                // HotelData class must implement Serializable in order to pass it to
                // another activity via the Intent object.
                //
                intent.putExtra("HotelsData", _arrListHotels);
                intent.putExtra("ImageDownloadMethod", EnumActions.USE_ASYNC_DOWNLOADER_WITH_CACHE);
                startActivity(intent);
            }
        });

        /*
         * Download the image with the Glide library (the best performance and preferred method)
         */
        Button btnGlideLibrary = findViewById(R.id.btn_use_glide_library);
        btnGlideLibrary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(_activity, ImagesListViewActivity.class);

                // HotelData class must implement Serializable in order to pass it to
                // another activity via the Intent object.
                //
                intent.putExtra("HotelsData", _arrListHotels);
                intent.putExtra("ImageDownloadMethod", EnumActions.USE_GLIDE_LIBRARY);
                startActivity(intent);
            }
        });

        /*
         * Download the image with the Glide library (the best performance and preferred method)
         */
        Button btnPicassoLibrary = findViewById(R.id.btn_use_picasso_library);
        btnPicassoLibrary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(_activity, ImagesListViewActivity.class);

                // HotelData class must implement Serializable in order to pass it to
                // another activity via the Intent object.
                //
                intent.putExtra("HotelsData", _arrListHotels);
                intent.putExtra("ImageDownloadMethod", EnumActions.USE_PICASSO_LIBRARY);
                startActivity(intent);
            }
        });

        // Enable or disable the UI buttons accordingly. Most likely, the hotel data
        // are not yet available at this point when the buttons are created.
        //
        // Until the Async method GetHotelDataAsyncTask() has done getting and parsing the
        // hotel JSON data, then GetHotelDataAsyncTask.onPostExecute() will send the hotel
        // ArrayList<HotelData> object to this MainActivity class via the handler through
        // the message Constants.HANDLER_MSG_GET_HOTELS_DATA.
        //
        btnAyncDownloader.setEnabled(_arrListHotels != null && !_arrListHotels.isEmpty());
        btnGlideLibrary.setEnabled(_arrListHotels != null && !_arrListHotels.isEmpty());

        Button btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatemen
        switch (id) {
            case R.id.action_settings:
                break;

            case R.id.action_hipproblem:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("URL", "https://github.com/Hipmunk/hipproblems/tree/master/hotelslist");
                startActivity(intent);
                break;

            case R.id.action_about:
                new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setMessage("Developer: Sang K. Lam\n\nDate: 07/07/2018\n\nCity: San Jose, California, USA")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
