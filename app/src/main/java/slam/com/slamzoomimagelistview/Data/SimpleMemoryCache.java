package slam.com.slamzoomimagelistview.Data;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleMemoryCache {
    static private final String TAG = SimpleMemoryCache.class.getSimpleName();

    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>());

    public Bitmap get(String key){
        try{
            if (cache.containsKey(key)) {
                return cache.get(key);
            }
            // NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            return null;
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String key, Bitmap bitmap){
        try{
            if (!cache.containsKey(key)) {
                cache.put(key, bitmap);
            }
        }
        catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
        }
        catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }
}