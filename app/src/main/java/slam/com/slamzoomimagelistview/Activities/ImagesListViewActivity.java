package slam.com.slamzoomimagelistview.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import slam.com.slamzoomimagelistview.Adapters.HotelListAdapter;
import slam.com.slamzoomimagelistview.Data.HotelData;
import slam.com.slamzoomimagelistview.Data.MemoryCache;
import slam.com.slamzoomimagelistview.Network.ImageDownloaderAyncTask;
import slam.com.slamzoomimagelistview.R;
import slam.com.slamzoomimagelistview.Securities.AppPermissions;
import slam.com.slamzoomimagelistview.Utils.UIHelper;

/**
 * Created by slam on 7/7/2018.
 */
public class ImagesListViewActivity extends AppCompatActivity {
    static private final String TAG = ImagesListViewActivity.class.getSimpleName();

    /**
     * EnumActions { USE_ASYNC_DOWNLOADER, USE_GLIDE_LIBRARY }
     */
    private MainActivity.EnumActions _imageDownloadMethod;
    private AppCompatActivity _activity;
    private ArrayList<HotelData> _arrListHotels;

    private MemoryCache _imageMemoryCache = new MemoryCache();

    private ImageView _imageHotelWebsite;
    private ImageView _imageHotelPhone;
    private ImageView _imageHotelDirections;
    private ImageView _imageHotelShare;
    private ImageView _imageHotelVideo;

    private boolean _isShowingListView = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotels_list_view);

        _activity = this;

        Intent intent = getIntent();
        _arrListHotels = (ArrayList<HotelData>) intent.getSerializableExtra("HotelsData");

        _imageDownloadMethod = (MainActivity.EnumActions)intent.getSerializableExtra("ImageDownloadMethod");

        // UIHelper.showActionBar(this, false);
        UIHelper.setActionBarText(_activity, _imageDownloadMethod.toString());

        final ListView listView = (ListView) findViewById(R.id.listViewHotels);
        final HotelListAdapter hotelListAdapter = new HotelListAdapter(this, _arrListHotels, _imageMemoryCache, _imageDownloadMethod);
        listView.setAdapter(hotelListAdapter);

        final LinearLayout fullImageViewLayout = (LinearLayout)findViewById(R.id.fullImageViewLayout);

        /**
         * When a user fires a "fling MotionEvent" (https://developer.android.com/reference/android/view/GestureDetector.OnGestureListener),
         * application is downloading images, it needs to stop, concentrate of scrolling, and then get back
         * to downloading images again as soon as motion stops. The effects of doing this is almost magical.
         *
         * @param shouldProcessQueue
         */
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView listView, int scrollState) {
                // Pause disk cache access to ensure smoother scrolling
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Log.e(TAG, "-------------------- stopProcessingQueue");
                    // imageLoader.stopProcessingQueue();
                    hotelListAdapter.shouldProcessAsyncImageDownloader(false);
                } else {
                    Log.e(TAG, "++++++++++++++++++++ startProcessingQueue");
                    // imageLoader.startProcessingQueue();
                    hotelListAdapter.shouldProcessAsyncImageDownloader(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * @param adapterView - the ListView object "android:id="@+id/listViewHotels"
             * @param v
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                /*
                 * Show the ActionBar and display the hotel name on the ActionBar
                 */
                HotelData hotelData = (HotelData) listView.getItemAtPosition(position);
                UIHelper.setActionBarText(_activity, hotelData.getName());

                /*
                 * Set the ImageViews (Website, Phone, Directions, Share, Video) with the hotelData.
                 * When the ImageView is clicked, getTag() will return the hotelData.
                 */
                _imageHotelWebsite.setTag(hotelData);
                _imageHotelPhone.setTag(hotelData);
                _imageHotelDirections.setTag(hotelData);
                _imageHotelShare.setTag(hotelData);
                _imageHotelVideo.setTag(hotelData);

                /*
                 * When an imageView on the ListView is tapped, the ListView will be set INVISIBLE
                 * and the View to show that image in fullscreen is set VISIBLE
                 */
                adapterView.setVisibility(View.INVISIBLE);
                fullImageViewLayout.setVisibility(View.VISIBLE);

                /*
                 * Download the image to the ImageView from image URL.
                 */
                ImageView imageFullView = fullImageViewLayout.findViewById(R.id.imageFullView);


                // Supports Pinch Zoom in ImageView/PhotoView
                //
                // Requires (implementation 'com.github.chrisbanes:PhotoView:2.1.3') in build.gradle (app)
                //
                // And the following setting in build.gradle (project)
                //  allprojects {
                //     repositories {
                //        maven { url "https://jitpack.io" }
                //     }
                //  }
                //
                PhotoViewAttacher photoAttacher;
                photoAttacher = new PhotoViewAttacher(imageFullView);
                photoAttacher.update();

                /*
                 * There are 3 different implementations here to show the ImageView with the image URL
                 */
                if (imageFullView != null) {
                    switch (_imageDownloadMethod) {
                        case USE_ASYNC_DOWNLOADER:
                            new ImageDownloaderAyncTask(imageFullView).execute(hotelData.getImageUrl());
                            break;

                        case USE_ASYNC_DOWNLOADER_WITH_CACHE:
                            Bitmap bitmap = _imageMemoryCache.get(hotelData.getImageUrl());
                            if (bitmap != null) {
                                imageFullView.setImageBitmap(bitmap);
                            }
                            else {
                                new ImageDownloaderAyncTask(imageFullView).execute(hotelData.getImageUrl());
                            }
                            break;

                        case USE_GLIDE_LIBRARY:
                            /*
                             * The Glide library has the best performance.
                             */
                            Glide.with(_activity)
                                .load(hotelData.getImageUrl())
                                .into(imageFullView);
                            break;

                        case USE_PICASSO_LIBRARY:
                            Picasso.with(getApplicationContext())
                                .load(hotelData.getImageUrl())
                                .into(imageFullView);
                            break;
                    }
                }

                photoAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
                    /**
                     * A callback to receive where the user taps on a photo. You will only receive a callback if
                     * the user taps on the actual photo, tapping on 'whitespace' will be ignored.
                     *
                     * @param view ImageView the user tapped.
                     * @param x    where the user tapped from the of the Drawable, as percentage of the
                     *             Drawable width.
                     * @param y    where the user tapped from the top of the Drawable, as percentage of the
                     */
                    @Override
                    public void onPhotoTap(ImageView view, float x, float y) {
                        /*
                         * Tapping on the fullscreen image will switch to the listView.
                         */
                        ListView listView = (ListView)UIHelper.findSiblingView(fullImageViewLayout, R.id.listViewHotels);
                        switchToListView(listView, fullImageViewLayout);
                    }
                });
                // Toast.makeText(ImagesListViewActivity.this, "Selected :" + " " + hotelData.getName(), Toast.LENGTH_LONG).show();
            }
        });

        ImageView imageFullView = fullImageViewLayout.findViewById(R.id.imageFullView);
        imageFullView.setOnClickListener(new View.OnClickListener() {
            /**
             * Note: PhotoView supports pinch-zoom, and will on invoke the ImageView OnClickListener
             * callback.  So instead, for PhotoView, implement the photoAttacher.setOnPhotoTapListener().
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                /*
                 * Tapping on the fullscreen image will switch to the listView.
                 */
                ListView listView = (ListView)UIHelper.findSiblingView(fullImageViewLayout, R.id.listViewHotels);
                switchToListView(listView, fullImageViewLayout);
            }
        });

        /*
         * Show the website (if available) of the selected hotel.
         */
        _imageHotelWebsite = fullImageViewLayout.findViewById(R.id.image_hotel_website);
        _imageHotelWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelData hotelData = (HotelData)v.getTag();
                if (hotelData.website == null || hotelData.website.isEmpty()) {
                    Toast.makeText(ImagesListViewActivity.this, R.string.msg_website_not_available, Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(hotelData.website));
                    startActivity(i);
                }
                Toast.makeText(ImagesListViewActivity.this, "Hotel: " + hotelData.getName() + "\n[Show Website>", Toast.LENGTH_LONG).show();
            }
        });

        /*
         * Launch the phone dial pad to call the selected hotel (if phone number available)
         */
        _imageHotelPhone = fullImageViewLayout.findViewById(R.id.image_hotel_phone);
        _imageHotelPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelData hotelData = (HotelData)v.getTag();

                if (hotelData.phoneNumber == null || hotelData.phoneNumber.isEmpty()) {
                    Toast.makeText(ImagesListViewActivity.this, R.string.msg_phone_number_not_available, Toast.LENGTH_LONG).show();
                    return;
                }
                if (! AppPermissions.hasCallPhonePermission(ImagesListViewActivity.this)) {
                    ActivityCompat.requestPermissions(
                        ImagesListViewActivity.this, new String[] {Manifest.permission.CALL_PHONE},
                        AppPermissions.REQUEST_CODE_CALL_PHONE
                    );
                }
                else {
                    if (ActivityCompat.checkSelfPermission(ImagesListViewActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        String uri = "tel:" + hotelData.phoneNumber;
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                }
                Toast.makeText(ImagesListViewActivity.this, "Hotel: " + hotelData.getName() + "\n<Call Reservation>", Toast.LENGTH_LONG).show();
            }
        });
        /*
         * Launch the Google Map to show the location (if available) of the selected hotel.
         */
        _imageHotelDirections = fullImageViewLayout.findViewById(R.id.image_hotel_directions);
        _imageHotelDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelData hotelData = (HotelData)v.getTag();
                if (hotelData.address == null || hotelData.address.isEmpty()) {
                    Toast.makeText(ImagesListViewActivity.this, R.string.msg_address_not_available, Toast.LENGTH_LONG).show();
                    return;
                }
                Uri uri = Uri.parse("geo:0,0?q=" + hotelData.address);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                Toast.makeText(ImagesListViewActivity.this, "Hotel: " + hotelData.getName() + "\n<Show Map Directions>", Toast.LENGTH_LONG).show();
            }
        });
        /*
         * Share the selected hotel via social apps (e.g. Messenger, Whats App, etc.)
         */
        _imageHotelShare = fullImageViewLayout.findViewById(R.id.image_hotel_share);
        _imageHotelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelData hotelData = (HotelData)v.getTag();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, hotelData.getName());
                if (hotelData.address == null || hotelData.address.isEmpty()) {
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "No hotel information to share!");               }
                else {
                    sharingIntent.putExtra(
                        android.content.Intent.EXTRA_TEXT, hotelData.getName() + "\n" +
                        hotelData.address + "\n" + hotelData.phoneNumber + "\n" + hotelData.website
                    );
                }
                String shareVia = getResources().getString(R.string.hotel_share_via, hotelData.getName());
                startActivity(Intent.createChooser(sharingIntent, shareVia));
                Toast.makeText(ImagesListViewActivity.this, "Hotel: " + hotelData.getName() + "\n<Share Information with Friends>", Toast.LENGTH_LONG).show();
            }
        });
        /*
         *  Play the video (not yet implement.)
         */
        _imageHotelVideo = fullImageViewLayout.findViewById(R.id.image_hotel_video);
        _imageHotelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HotelData hotelData = (HotelData)v.getTag();

                Intent intent = new Intent(_activity, VideoViewActivity.class);
                intent.putExtra("HotelData", hotelData);
                intent.putExtra("urlVideoClip", "https://s3-us-west-1.amazonaws.com/com-nearchus-media/LetsMeet2EatVideoDemo.mp4");

                _activity.startActivity(intent);

                Toast.makeText(ImagesListViewActivity.this, "Hotel: " + hotelData.getName() + "\n<Play Video about the Hotel>", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * onBackPressed()
     *
     * Override the default action. If it is a fullscreen image view, it will switch to the
     * ListView.  If it is on the ListView, then it will navigate to the home screen view.
     */
    @Override
    public void onBackPressed() {
        /*
         * On the ListView Taping on the BACK button will have the default
         * behavior of navigating back to the screen in the activity stack.
         *
         * If the actionBar is showing, the activity is showing the ImageView in fullscreen.
         * Therefore, if the BACK button is tapped, then navigate back to the ListView.
         */
        ListView listView = (ListView)findViewById(R.id.listViewHotels);

        if (listView.isShown()) {
            /*
             * On the ListView Taping on the BACK button will have the default
             * behavior of navigating back to the screen in the activity stack.
             */
            super.onBackPressed();
        }
        else {
            /*
             * The activity is showing the ImageView in fullscreen.
             * Tap on the BACK button will show the ListView.
             */
            LinearLayout fullImageViewLayout = (LinearLayout)findViewById(R.id.fullImageViewLayout);
            switchToListView(listView, fullImageViewLayout);

            /*
             * Replace the fullscreen view hotel image with the temporary place holder image.
             */
            ImageView imageFullView = fullImageViewLayout.findViewById(R.id.imageFullView);
            imageFullView.setImageResource(R.drawable.ic_image_place_holder);
        }
    }

    /**
     * Switch the view from image fullscreen view to the list view.
     * This
     *
     * @param listView
     * @param fullImageViewLayout
     */
    private void switchToListView(ListView listView, View fullImageViewLayout) {

        _isShowingListView = true;

        // UIHelper.showActionBar(_activity, false);
        UIHelper.setActionBarText(_activity, _imageDownloadMethod.toString());

        /*
         * Replace the fullscreen view hotel image with the temporary place holder image.
         */
        ImageView imageFullView = fullImageViewLayout.findViewById(R.id.imageFullView);
        imageFullView.setImageResource(R.drawable.ic_image_place_holder);

        fullImageViewLayout.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);

        _imageMemoryCache.clear();
    }
}

