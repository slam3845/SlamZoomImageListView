package slam.com.slamzoomimagelistview.Network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import slam.com.slamzoomimagelistview.Data.MemoryCache;
import slam.com.slamzoomimagelistview.R;

/**
 * Created by slam on 7/7/2018.
 */
public class ImageDownloaderAyncTask extends AsyncTask<String, Void, Bitmap> {
    static private final String TAG = ImageDownloaderAyncTask.class.getSimpleName();

    private final WeakReference<ImageView> _imageViewReference;
    private String _imageURL = null;
    private MemoryCache _imageMemoryCache = null;

    public ImageDownloaderAyncTask(ImageView imageView) {
        _imageViewReference = new WeakReference<ImageView>(imageView);
    }

    public ImageDownloaderAyncTask(ImageView imageView, MemoryCache imageMemoryCache) {
        _imageViewReference = new WeakReference<ImageView>(imageView);
        _imageMemoryCache = imageMemoryCache;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        _imageURL = params[0];
        return downloadBitmap1(_imageURL);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (_imageViewReference != null && bitmap != null) {
            ImageView imageView = _imageViewReference.get();
            if (imageView != null) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    if (_imageMemoryCache != null) {
                        _imageMemoryCache.put(_imageURL, bitmap);
                    }
                }
                else {
                    Drawable imagePlaceholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_image_place_holder);
                    imageView.setImageDrawable(imagePlaceholder);
                }
            }
        }
    }

    /**
     * Version 1 - downloadBitmap1
     *
     * @param imageURL
     * @return
     */
    private Bitmap downloadBitmap1(String imageURL) {
        InputStream in = null;
        try {
            in = new java.net.URL(imageURL).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();
            return bitmap;
        }
        catch (Exception ex) {
            Log.e(TAG, "downloadBitmap2() Exception: " + imageURL);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Version 2 - downloadBitmap2
     *
     * @param imageURL
     * @return
     */
    private Bitmap downloadBitmap2(String imageURL) {
        HttpURLConnection httpUrlConnection = null;
        try {
            URL uri = new URL(imageURL);
            httpUrlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = httpUrlConnection.getResponseCode();
            // if (statusCode != HttpStatus.SC_OK) {
            if (statusCode != 200) {
                return null;
            }

            InputStream inputStream = httpUrlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        }
        catch (Exception ex) {
            Log.e(TAG, "downloadBitmap2() Exception: " + imageURL);
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