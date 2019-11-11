package slam.com.slamzoomimagelistview.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class object mush implement the Serializable.
 *
 * Whenever an object is passed from one activity to another via the Intent object,
 * it must be either Parcelable or Serializable object.
 *
 * Created by slam on 7/7/2018.
 */
public class HotelData implements Serializable {
    static private final String TAG = HotelData.class.getSimpleName();

    String imageUrl;
    String name;

    public String address;
    public String phoneNumber;
    public String website;
    public String priceRange;
    public String ratings;

    public HotelData(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }






    /************************************************************************
     * Added for testing the website, phone call, Map direction, Share etc...
     *
     * @param arrListHotels
     */
    static public void AddMoreHotelInfo(ArrayList<HotelData> arrListHotels) {
        addHotelInfo(arrListHotels.get(0),"12 4th St, San Francisco, CA 94103", "(415) 348-1111", "https://www.viceroyhotelsandresorts.com/en/zelos", "$229", "4.3");
        addHotelInfo(arrListHotels.get(1), "501 Geary St, San Francisco, CA 94102", "(415) 292-0100", "https://www.jdvhotels.com/", "$200", "4.2");
        addHotelInfo(arrListHotels.get(2), "450 Powell St, San Francisco, CA 94102", "(415) 392-7755", "http://www.sirfrancisdrake.com/", "$197", "3.9");
        addHotelInfo(arrListHotels.get(3), "2500 Mason St, San Francisco, CA 94133", "(415) 362-5500", "http://www.sheratonatthewharf.com/", "$239", "3.8");
        addHotelInfo(arrListHotels.get(4), "345 Stockton St, San Francisco, CA 94108", "(415) 398-1234", "https://sanfrancisco.grand.hyatt.com/en/hotel/home.html", "$229", "4.4");
        addHotelInfo(arrListHotels.get(5), "495 Geary St, San Francisco, CA 94102", "(415) 775-4700", "https://www.sonesta.com/", "$224", "4.1");
        addHotelInfo(arrListHotels.get(6), "1231 Market St, San Francisco, CA 94103", "(415) 626-8000", "http://hotelwhitcomb.com/main/WHITCOMB-Page.asp?p=1", "$169", "3.2");
        addHotelInfo(arrListHotels.get(7), "1804, 335 Powell St, San Francisco, CA 94102", "(415) 397-7000", "http://www.westinstfrancis.com/", "$195", "4.3");
        addHotelInfo(arrListHotels.get(8), "495 Jefferson St, San Francisco, CA 94109", "(415) 563-0800", "http://www.argonauthotel.com/", "$319", "4.5");
        addHotelInfo(arrListHotels.get(9), "417 Stockton St, San Francisco, CA 94108", "(415) 400-0500", "https://www.mystichotel.com/", "$225", "3.9");
    }

    static private void addHotelInfo(HotelData hotelData, String address, String phoneNumber, String website, String priceRange, String ratings) {
        hotelData.address = address;
        hotelData.phoneNumber = phoneNumber;
        hotelData.website = website;
        hotelData.priceRange = priceRange;
        hotelData.ratings = ratings;
    }
}
