package slam.com.slamzoomimagelistview.Data;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {
    static private final String TAG = MemoryCache.class.getSimpleName();

    private Map<String, Bitmap> cache = Collections.synchronizedMap(
        new LinkedHashMap<String, Bitmap>(
            10,1.5f,true    // Last argument true for LRU ordering
        )
    );

    /**
     * Current allocated memory Size
     */
    private long _memorySize = 0;

    /**
     * max memory in bytes
     */
    private long _maxMemLimitInBytes = 1000000;


    public MemoryCache() {
        //use 25% of available heap _memorySize
        setMaxMemLimitInBytes(Runtime.getRuntime().maxMemory() / 4);
    }

    public void setMaxMemLimitInBytes(long newLimitInBytes) {
        _maxMemLimitInBytes = newLimitInBytes;
        Log.i(TAG, "*** MemoryCache will use up to " + _maxMemLimitInBytes / 1024. / 1024. + "MB");
    }

    public Bitmap get(String key) {
        try{
            if (!cache.containsKey(key)) {
                return null;
            }
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            return cache.get(key);
        }
        catch (NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

//    public void put(String key, Bitmap bitmap){
//        try{
//            if (cache.containsKey(key)) {
//                _memorySize -= getBitmapSizeInBytes(cache.get(key));
//            }
//            cache.put(key, bitmap);
//            _memorySize += getBitmapSizeInBytes(bitmap);
//            checkSize();
//        }
//        catch(Throwable th){
//            th.printStackTrace();
//        }
//    }

    public void put(String key, Bitmap bitmap){
        try{
            if (cache.containsKey(key)) {
               return;
            }
            cache.put(key, bitmap);
            _memorySize += getBitmapSizeInBytes(bitmap);
            checkSize();
        }
        catch(Throwable th){
            th.printStackTrace();
        }
    }

    private void checkSize() {
        Log.i(TAG, "cache siz e=" + _memorySize + " length=" + cache.size());

        if (_memorySize > _maxMemLimitInBytes) {
            // Least recently accessed item will be the first one iterated
            Iterator<Map.Entry<String, Bitmap>> iter = cache.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                _memorySize -= getBitmapSizeInBytes(entry.getValue());
                iter.remove();

                if (_memorySize <= _maxMemLimitInBytes) {
                    break;
                }
            }
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
            _memorySize = 0;
        }
        catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    long getBitmapSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}

