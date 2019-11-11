package slam.com.slamzoomimagelistview.Utils;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by slam on 7/7/2018.
 */
public class UIHelper {

    static public void showActionBar(AppCompatActivity appCompatActivity, boolean show) {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            if (show) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    /**
     * setActionBarText
     *
     * @param appCompatActivity
     * @param actionBarTitle
     */
    static public void setActionBarText(
        AppCompatActivity appCompatActivity, String actionBarTitle
    ) {
        setActionBarText(appCompatActivity, actionBarTitle, 14, Color.YELLOW);
    }

    /**
     * setActionBarText
     *
     * @param appCompatActivity
     * @param actionBarTitle
     * @param textSize
     * @param color
     */
    static public void setActionBarText(
        AppCompatActivity appCompatActivity, String actionBarTitle, int textSize, int color
    ) {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();

        if (! actionBar.isShowing()) {
            showActionBar(appCompatActivity, true);
        }

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,   // Width of TextView
            RelativeLayout.LayoutParams.WRAP_CONTENT    // Height of TextView
        );

        TextView tv = new TextView(appCompatActivity);
        tv.setLayoutParams(lp); // Apply the layout parameters to TextView widget

        tv.setText(actionBarTitle);  // Set actionBarTitle to display in TextView
        tv.setTextColor(color); // Change the ActionBar title text color
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);  // Change the ActionBar title text size

        // Set the ActionBar display option
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Finally, set the newly created TextView as ActionBar custom view
        actionBar.setCustomView(tv);

//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.drawable.ic_hotel_1a);
    }

    static public View findSiblingView(View view, int siblingViewResId) {
        try {
            ViewParent viewParent = view.getParent();
            ViewGroup viewGroup = (ViewGroup) viewParent;
            View viewSibling = viewGroup.findViewById(siblingViewResId);
            return viewSibling;
        }
        catch (Exception ex) {
            return null;
        }
    }
}
