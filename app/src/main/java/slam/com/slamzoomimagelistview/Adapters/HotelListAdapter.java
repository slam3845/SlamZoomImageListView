package slam.com.slamzoomimagelistview.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import slam.com.slamzoomimagelistview.Activities.MainActivity;
import slam.com.slamzoomimagelistview.Data.HotelData;
import slam.com.slamzoomimagelistview.Data.MemoryCache;
import slam.com.slamzoomimagelistview.Network.ImageDownloaderAyncTask;
import slam.com.slamzoomimagelistview.R;

/**
 * Created by slam on 7/7/2018.
 */
public class HotelListAdapter extends BaseAdapter {
    static private final String TAG = HotelListAdapter.class.getSimpleName();

    /**
     * EnumActions { USE_ASYNC_DOWNLOADER, USE_GLIDE_LIBRARY }
     */
    private MainActivity.EnumActions _imageDownloadMethod;

    private Context _context;
    private ArrayList<HotelData> _arrListHotels;
    private LayoutInflater _layoutInflater;
    private boolean _shouldProcessAsyncImageDownloader = true;

//    private Map<ImageView, String> imageViews= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
//    private HashMap<String, Bitmap> _mapCacheHotelImages<String, Bitmap> =

    private MemoryCache _imageMemoryCache;

    public HotelListAdapter(
        Context context, ArrayList<HotelData> listData, MemoryCache imageMemoryCache,
        MainActivity.EnumActions imageDownloadMethod
    ) {
        this._context = context;
        this._arrListHotels = listData;
        this._imageMemoryCache = imageMemoryCache;
        this._imageDownloadMethod = imageDownloadMethod;

        _layoutInflater = LayoutInflater.from(context);

//        int iX = 0;
//        Log.e(TAG, "HotelsListAdapter: _arrListHotels");
//        for (HotelData hotelData : this._arrListHotels) {
//            Log.e(TAG,  iX++ + ") \"" + hotelData.getName() + "\"; " + hotelData.getImageUrl());
//        }
    }


    /**
     * When a user fires a "fling MotionEvent" (https://developer.android.com/reference/android/view/GestureDetector.OnGestureListener),
     * application is downloading images, it needs to stop, concentrate of scrolling, and then get back
     * to downloading images again as soon as motion stops. The effects of doing this is almost magical.
     *
     * @param shouldProcessAsyncImageDownloader
     */
    public void shouldProcessAsyncImageDownloader(boolean shouldProcessAsyncImageDownloader) {
        this._shouldProcessAsyncImageDownloader = shouldProcessAsyncImageDownloader;
        if (shouldProcessAsyncImageDownloader) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (_arrListHotels != null) {
            return _arrListHotels.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (_arrListHotels != null) {
            return _arrListHotels.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (_arrListHotels == null) {
            return convertView;
        }
        HotelData hotelData = _arrListHotels.get(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = _layoutInflater.inflate(R.layout.activity_hotels_list_row_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageViewHotel = (ImageView) convertView.findViewById(R.id.image_view_hotel);
            viewHolder.textViewHotelName = (TextView)convertView.findViewById(R.id.tv_hotel_name);
            viewHolder.textViewRatings = (TextView)convertView.findViewById(R.id.tv_ratings);
            viewHolder.textViewPriceRange = (TextView)convertView.findViewById(R.id.tv_price_range);
            convertView.setTag(viewHolder);
            // Log.e(TAG, position + "): > \"" + hotelData.getName() + "\"; " + hotelData.getImageUrl());

        } else {
            viewHolder = (ViewHolder)convertView.getTag();
            // Log.e(TAG, position + "): >> \"" + hotelData.getName() + "\"; " + hotelData.getImageUrl());
        }

        /*
         * Set the hotel name
         */
        viewHolder.textViewHotelName.setText(hotelData.getName());

        if (hotelData.ratings != null && ! hotelData.ratings.isEmpty()) {
            viewHolder.textViewRatings.setText(hotelData.ratings);
        }
        else {
            viewHolder.textViewRatings.setText("N/A");
        }
        if (hotelData.priceRange != null && ! hotelData.priceRange.isEmpty()) {
            viewHolder.textViewPriceRange.setText(hotelData.priceRange);
        }
        else {
            viewHolder.textViewPriceRange.setText("N/A");
        }

        /*
         * Download the image from the url and set the ImageView.
         */
        if (viewHolder.imageViewHotel != null) {
            switch (_imageDownloadMethod) {
                case USE_ASYNC_DOWNLOADER:
                    if (!_shouldProcessAsyncImageDownloader) {
                        /*
                         * To reduce the downloading of images while the ListView is
                         * scrolling, and then get back to downloading images again
                         * as soon as motion stops.
                         * https://leftshift.io/6-ways-to-make-your-lists-scroll-faster-than-the-wind
                         */
                        break;
                    }
                    new ImageDownloaderAyncTask(viewHolder.imageViewHotel).execute(hotelData.getImageUrl());
                    break;

                case USE_ASYNC_DOWNLOADER_WITH_CACHE:
                    if (!_shouldProcessAsyncImageDownloader) {
                        /*
                         * To reduce the downloading of images while the ListView is
                         * scrolling, and then get back to downloading images again
                         * as soon as motion stops.
                         * https://leftshift.io/6-ways-to-make-your-lists-scroll-faster-than-the-wind
                         */
                        break;
                    }
                    Bitmap bitmap = _imageMemoryCache.get(hotelData.getImageUrl());
                    if (bitmap != null) {
                        viewHolder.imageViewHotel.setImageBitmap(bitmap);
                        // Log.d(TAG, ">>> Existing Bitmap for " + hotelData.getName());
                    }
                    else {
                        new ImageDownloaderAyncTask(
                            viewHolder.imageViewHotel, _imageMemoryCache).execute(hotelData.getImageUrl()
                        );
                        // Log.d(TAG, "!!! New Bitmap for " + hotelData.getName());
                    }
                    break;

                case USE_GLIDE_LIBRARY:
                    Glide.with(_context)
                        .load(hotelData.getImageUrl())
                        .into(viewHolder.imageViewHotel);
                    break;

                case USE_PICASSO_LIBRARY:
                    Picasso.with(_context)
                        .load(hotelData.getImageUrl())
                        .into(viewHolder.imageViewHotel);
                    break;
            }
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewHotel;
        TextView textViewHotelName;
        TextView textViewRatings;
        TextView textViewPriceRange;
    }
}
