package io.github.alexlondon07.demodevices;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by alexlondon07 on 10/28/17.
 */

public class Permissions {

    public static boolean isGrantedPermissions(Context context, String permissionType){
        int permission = ActivityCompat.checkSelfPermission(context, permissionType);

        return  permission == PackageManager.PERMISSION_GRANTED;
    }

    public static void verifyPermissions(Activity activity, String[] permissionType){
        ActivityCompat.requestPermissions(activity, permissionType, Constants.REQUEST_CODE_PERMISSION);
    }
}
