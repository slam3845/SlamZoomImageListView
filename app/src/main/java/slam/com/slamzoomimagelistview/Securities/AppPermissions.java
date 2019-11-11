/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package slam.com.slamzoomimagelistview.Securities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import slam.com.slamzoomimagelistview.R;


/**
 * Access runtime permissions.
 *
 * https://blog.xamarin.com/requesting-runtime-permissions-in-android-marshmallow/
 *
 *------------------------------------------------------------------------------------------------
 *  https://stackoverflow.com/questions/32714787/android-m-permissions-onrequestpermissionsresult-not-being-called
 * If you set the activity that you call requestPermissions or AppCompatActivity.requestPermissions with
 *      android:noHistory="true"
 *      android:excludeFromRecents="true"
 *  in your AndroidManifest.xml then onRequestPermissionsResult() will not be called.
 *  This is true if your Activity is derived from Activity or AppCompatActivity.
 *------------------------------------------------------------------------------------------------
 * Created by slam on 7/7/2018
 */
public abstract class AppPermissions {

    static public final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;  // Must be grant before the app can run
    static public final int REQUEST_CODE_READ_CONTACTS = 2;         // Ask for permission when needed.
    static public final int REQUEST_CODE_CALL_PHONE = 3;            // Ask for permission when needed.
    static public final int REQUEST_CODE_SEND_SMS = 4;              // Ask for permission when needed.

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     *
     *  Manifest.permission.READ_CONTACTS
     *  Manifest.permission.ACCESS_FINE_LOCATION
     *  Manifest.permission.ACCESS_COARSE_LOCATION
     *  Manifest.permission.SEND_SMS
     *  Manifest.permission.CALL_PHONE
     *  Manifest.permission.READ_PHONE_STATE
     *  Manifest.permission.INTERNET
     *
     * @param activity
     * @param requestCode
     * @param appPermission (Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS)
     * @param finishActivity
     */
    public static void requestPermission(
            AppCompatActivity activity, int requestCode, String appPermission, boolean finishActivity
    ) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, appPermission)) {
            // Display a dialog with rationale.
            RationaleDialog.newInstance(requestCode, appPermission, finishActivity)
                    .show(activity.getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, new String[]{appPermission}, requestCode);
        }
    }

    static public boolean hasReadContactsPermission(Context context) {
        // checkSelfPermission() require Android API level 23
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    static public boolean hasCallPhonePermission(Context context) {
        // checkSelfPermission() require Android API level 23
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    static public boolean hasSendSmsPermission(Context context) {
        // checkSelfPermission() require Android API level 23
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessarypermission.
     * <p>
     * The activity should implement
     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
     * to handle permit or denial of this permission request.
     */
    public static class RationaleDialog extends DialogFragment {

        private static final String ARG_PERMISSION_REQUEST_CODE = "argRequestCode";
        private static final String ARG_APP_PERMISSION_REQUEST = "argAppPermissionRequest";
        private static final String ARG_FINISH = "argFinish";

        private boolean mFinishActivity = false;

        /**
         * Creates a new instance of a dialog displaying the rationale for the use of the permission.
         * <p>
         * The permission is requested after clicking 'ok'.
         *
         *
         * @param requestCode  - Id of the request that is used to request the permission. It is returned to the {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
         * @param appPermissionRequest - Manifest.permission.READ_CONTACTS, .ACCESS_FINE_LOCATION, .ACCESS_COARSE_LOCATION, .CALL_PHONE, .SEND_SMS
         * @param finishActivity - Whether the calling Activity should be finished if the dialog is cancelled.
         * @return
         */
        public static RationaleDialog newInstance(int requestCode, String appPermissionRequest, boolean finishActivity) {
            Bundle arguments = new Bundle();

            /**
             * See AppPermissions for the definition of requestCodes:
             *    NC_PERMISSION_REQUEST_CODE_READ_CONTACTS
             *    NC_PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION
             *    NC_PERMISSION_REQUEST_CODE_ACCESS_COARSE_LOCATION
             *    NC_PERMISSION_REQUEST_CODE_CALL_PHONE
             *    NC_PERMISSION_REQUEST_CODE_SEND_SMS
             */
            arguments.putInt(ARG_PERMISSION_REQUEST_CODE, requestCode);
            arguments.putString(ARG_APP_PERMISSION_REQUEST, appPermissionRequest);
            arguments.putBoolean(ARG_FINISH, finishActivity);

            RationaleDialog dialog = new RationaleDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle arguments = getArguments();
            final int requestCode = arguments.getInt(ARG_PERMISSION_REQUEST_CODE);
            final String appPermissionRequest = arguments.getString(ARG_APP_PERMISSION_REQUEST);
            mFinishActivity = arguments.getBoolean(ARG_FINISH);

            int resIdPermissionRequest = 0;

            switch (requestCode) {
                case AppPermissions.REQUEST_CODE_READ_CONTACTS:
                     // appPermission = Manifest.permission.READ_CONTACTS;
                    resIdPermissionRequest = R.string.permission_location_required;
                    break;

                case AppPermissions.REQUEST_CODE_ACCESS_FINE_LOCATION:
                    // appPermission = Manifest.permission.ACCESS_FINE_LOCATION;
                    resIdPermissionRequest = R.string.permission_location_required;
                    break;

                case AppPermissions.REQUEST_CODE_CALL_PHONE:
                    // appPermission = Manifest.permission.CALL_PHONE;
                    resIdPermissionRequest = R.string.permission_location_required;
                    break;

                case AppPermissions.REQUEST_CODE_SEND_SMS:
                    // appPermission = Manifest.permission.SEND_SMS;
                    resIdPermissionRequest = R.string.permission_location_required;
                    break;
            }

            return new AlertDialog.Builder(getActivity())
                    .setMessage(resIdPermissionRequest)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Important Note 1 for using requestPermissions():
                            // When using the Support library, you have to use the correct method calls.
                            //    - For AppCompatActivity, you should use ActivityCompat.requestPermissions;
                            //    - For android.support.v4.app.Fragment, you should use simply requestPermissions
                            //      (this is an instance method of android.support.v4.app.Fragment)
                            // ---------------------------------------------------------------------
                            // Important Note 2 for using requestPermissions():
                            // If you set the activity that you call requestPermissions or
                            // AppCompatActivity.requestPermissions with
                            //      android:noHistory="true"
                            //      android:excludeFromRecents="true"
                            // in your AndroidManifest.xml then onRequestPermissionsResult() will
                            // not be called. This is true if your Activity is derived from Activity
                            // or AppCompatActivity.
                            //

                            // Request the permission by showing a Permission Dialog.
                            // After the user response to the permission dialog, then the callback
                            // onRequestPermissionsResult() in the activity will be invoked.
                            //
                            ActivityCompat.requestPermissions(getActivity(), new String[]{appPermissionRequest}, requestCode);
                            mFinishActivity = false; // Do not finish the Activity while requesting permission.
                        }
                    })
                    // .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (mFinishActivity) {
                // getActivity().finish();
            }
        }
    }


    /**
     * A dialog that displays a permission denied message.
     */
    public static class PermissionDeniedDialog extends DialogFragment {

        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";

        private boolean mFinishActivity = false;

        /**
         * Creates a new instance of this dialog and optionally finishes the calling Activity
         * when the 'Ok' button is clicked.
         */
        public static PermissionDeniedDialog newInstance(boolean finishActivity) {
            Bundle arguments = new Bundle();
            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);

            PermissionDeniedDialog dialog = new PermissionDeniedDialog();
            dialog.setArguments(arguments);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mFinishActivity = getArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY);

            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.location_permission_denied)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (mFinishActivity) {
//                Toast.makeText(getActivity(), R.string.permission_required_toast,
//                        Toast.LENGTH_SHORT).show();
//                getActivity().finish();
            }
        }
    }
}
