package com.rowfis.shareme.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Media;
import android.provider.Settings.Secure;

import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.rowfis.shareme.BuildConfig;
import com.rowfis.shareme.R;
import com.rowfis.shareme.constants.AppConstants;
import com.rowfis.shareme.model.AppInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;




/**
 * Created by Dan Chumo on 11/28/2017.
 */

public class Utility {
    public static File ext_dir = Environment.getExternalStorageDirectory();
    public static String backup_path = (ext_dir.getAbsoluteFile() + "/bluetooth_apk_sharer");


    public static Cursor getVideosCursor(Activity activity, String order) {
        return activity.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, null, "duration <> 0", null, order);

    }

    private static Cursor getPhotosCursor(Activity activity, String order) {
        return activity.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, null, null, null, order);

    }

    private static Cursor getAudioCursor(Activity activity, String order) {
        return activity.getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI, null, "is_music = 1 AND mime_type <> 'application/ogg' AND duration <> 0", null, order);
    }

    private static String getOrder(int i) {
        String order = "date_added DESC";
        switch (i) {
            case 0:
                return "title ASC";
            case 1:
                return "title DESC";
            case 2:
                return "_size ASC";
            case 3:
                return "_size DESC";
            case 4:
                return "date_added ASC";
            case 5:
                return "date_added DESC";
            default:
                return order;
        }
    }

    public static boolean checkPkgInstalled(Context context, String pkgName) {
        try {
            if (context.getPackageManager().getPackageInfo(pkgName, 0) != null) {
                return true;
            }
            return false;
        } catch (NameNotFoundException localNameNotFoundException) {
            localNameNotFoundException.printStackTrace();
            return false;
        }
    }

    public static String convertSize(long size) {
        if (size < 1048576) {
            return String.valueOf(size / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB";
        }
        if (size < 1073741824) {
            return String.valueOf(size / 1048576) + " MB";
        }
        return String.valueOf(size / 1073741824) + " GB";
    }
    public static boolean deleteFile(Context context, String path) {
        boolean status = true;
        if (path == null) {
            return false;
        }
        File mFile = new File(path);
        if (mFile.exists() && mFile.isFile()) {
            status = mFile.delete();
        } else if (!mFile.exists()) {
            status = true;
        }
        if (status) {
            deleteFileFromMediaStore(context.getContentResolver(), mFile);
        }
        return status;
    }

    private static void deleteFileFromMediaStore(ContentResolver contentResolver, File mFile) {

        String canonicalPath;
        try {
            canonicalPath = mFile.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = mFile.getAbsolutePath();
        }
        Uri uri = Files.getContentUri("external");
        if (contentResolver.delete(uri, "_data=?", new String[]{canonicalPath}) == 0) {
            if (!mFile.getAbsolutePath().equals(canonicalPath)) {
                contentResolver.delete(uri, "_data=?", new String[]{mFile.getAbsolutePath()});
            }
        }

    }

    public static void downloadApkFromMarket(Context context, String pkgName) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(AppConstants.MARKET_APP_PREFIX + pkgName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatTime(long time) {
        try {
            return new SimpleDateFormat("HH:mm:ss").format(Long.valueOf(time - ((long) TimeZone.getDefault().getRawOffset())));
        } catch (Exception e) {
            return null;
        }
    }


    public static String getAnroidId(Context context) {
        return "" + Secure.getString(context.getContentResolver(), "android_id");
    }

    public static String getNameFromFilepath(String path) {
        int i = path.lastIndexOf(47);
        if (i != -1) {
            return path.substring(i + 1);
        }
        return "";
    }

    public static int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public static String getScreenSizeInches(Context context) {
        try {
            DisplayMetrics localDisplayMetrics = context.getResources().getDisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
            return BigDecimal.valueOf(Math.sqrt(Math.pow((double) (((float) localDisplayMetrics.widthPixels) / localDisplayMetrics.xdpi), 2.0d) + Math.pow((double) (((float) localDisplayMetrics.heightPixels) / localDisplayMetrics.ydpi), 2.0d))).setScale(1, RoundingMode.HALF_UP).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static void hideSoftInput(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void installAPK(Context context, String apkPath) {
        try {
            Intent installIntent = new Intent();
            installIntent.setAction("android.intent.action.VIEW");
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            installIntent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
            context.startActivity(installIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void installApk(Context context, File mFile) {
        Exception e;
        Intent intent = null;
        try {
            Intent intent2 = new Intent("android.intent.action.VIEW");
            try {
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
                List<ResolveInfo> localList = context.getPackageManager().queryIntentActivities(intent2, 0);
                if (!localList.isEmpty()) {
                    ArrayList arrayList = new ArrayList();
                    for (ResolveInfo resolveInfo : localList) {
                        ActivityInfo localActivityInfo = resolveInfo.activityInfo;
                        if (!localActivityInfo.packageName.equals(context.getPackageName())) {
                            intent2.setClassName(localActivityInfo.packageName, localActivityInfo.name);
                            arrayList.add(intent2);
                        }
                    }
                }
                intent = intent2;
            } catch (Exception e2) {
                e = e2;
                intent = intent2;
                e.printStackTrace();
                if (intent == null) {
                    context.startActivity(intent);
                }
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            if (intent == null) {
                context.startActivity(null);
            }
        }
        if (intent == null) {
            context.startActivity(null);
        }
    }


    public static boolean isApkInstalled(Context context, String pkgName) {
        try {
            context.getPackageManager().getPackageInfo(pkgName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static void openAPK(Context context, String pkgName) {
        try {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(pkgName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void openGooglePlay(Context context, String pkgName) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(AppConstants.MARKET_APP_PREFIX + pkgName));
            // intent.setClassName(zze.GOOGLE_PLAY_STORE_PACKAGE, "com.android.vending.AssetBrowserActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            downloadApkFromMarket(context, pkgName);
        }
    }

    public static void openURI(Context context, String url) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<AppInfo> getInstalledApps(Activity activity, int type) {
        ArrayList<String> installedPkgNames = new ArrayList();
        ArrayList<AppInfo> allApps = new ArrayList();
        try {
            PackageManager packageManager = activity.getPackageManager();
            if (packageManager != null) {
                Intent intent = new Intent("android.intent.action.MAIN", null);
                intent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);
                if (!(list == null || list.size() == 0)) {
                    for (ResolveInfo resolveInfo : list) {
                        if (resolveInfo != null) {
                            ActivityInfo activityInfo = resolveInfo.activityInfo;
                            if (activityInfo != null) {
                                ApplicationInfo applicationInfo = activityInfo.applicationInfo;
                                if (applicationInfo != null) {
                                    String pkgName = applicationInfo.packageName;
                                    if (!(pkgName == null || packageManager.getLaunchIntentForPackage(pkgName) == null || !installedPkgNames.add(pkgName))) {
                                        AppInfo appInfo = getAppInfo(activity, pkgName);
                                        if (appInfo.getType() == type) {
                                            allApps.add(appInfo);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return allApps;
    }

    public static String getReadableFileSize(long size) {
        DecimalFormat dec = new DecimalFormat("###.#");
        String KILOBYTES = " KB";
        String MEGABYTES = " MB";
        String GIGABYTES = " GB";
        float fileSize = 0.0f;
        String suffix = " KB";
        if (size > PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            fileSize = (float) (size / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
            if (fileSize > 1024.0f) {
                fileSize /= 1024.0f;
                if (fileSize > 1024.0f) {
                    fileSize /= 1024.0f;
                    suffix = " GB";
                } else {
                    suffix = " MB";
                }
            }
        }
        return String.valueOf(dec.format((double) fileSize) + suffix);
    }

    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }
        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        }
        return "";
    }

    public static boolean isLocal(String url) {
        if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
            return false;
        }
        return true;
    }

    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static File getPathWithoutFilename(File file) {
        if (file == null) {
            return null;
        }
        if (file.isDirectory()) {
            return file;
        }
        String filename = file.getName();
        String filepath = file.getAbsolutePath();
        String pathWithoutName = filepath.substring(0, filepath.length() - filename.length());
        if (pathWithoutName.endsWith("/")) {
            pathWithoutName = pathWithoutName.substring(0, pathWithoutName.length() - 1);
        }
        return new File(pathWithoutName);
    }

    public static String getMimeType(File file) {
        String extension = getExtension(file.getName());
        if (extension.length() > 0) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }
        return "application/octet-stream";
    }

    public static Bitmap decodeFile(String filePath, int WIDTH, int HEIGHT) {
        Bitmap bitmap = null;
        try {
            File f = new File(filePath);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            int REQUIRED_WIDTH = WIDTH;
            int REQUIRED_HIGHT = HEIGHT;
            int scale = 1;
            while ((options.outWidth / scale) / 2 >= REQUIRED_WIDTH && (options.outHeight / scale) / 2 >= REQUIRED_HIGHT) {
                scale *= 2;
            }
            Options opn = new Options();
            opn.inSampleSize = scale;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, opn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void shareApk(Context context, String pkgName) {
        try {
            String apk_path = context.getPackageManager().getApplicationInfo(pkgName, 0).sourceDir;
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("*/*");
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(apk_path)));
            context.startActivity(Intent.createChooser(intent, "Share via:-"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKey(Activity activity) {
        if (activity != null) {
            try {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendLink(Activity activity, AppInfo app) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.SUBJECT", activity.getResources().getString(R.string.link_share_subject));
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", AppConstants.APP_LINK_PREFIX + app.getPackageName());
        activity.startActivity(Intent.createChooser(intent, "Select app to share"));
    }


    public static void sendApk(Activity activity, AppInfo app) {
        ArrayList<Uri> files = new ArrayList();

        files.add(FileProvider.getUriForFile(activity, BuildConfig.CONTENT_PROVIDER_AUTHORITY, new File(app.getApkPath())));
       // files.add(Uri.fromFile(new File(app.getApkPath())));
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.putExtra("android.intent.extra.SUBJECT", activity.getResources().getString(R.string.apk_share_subject));
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.EMAIL", new String[]{""});
        intent.putExtra("android.intent.extra.SUBJECT", activity.getResources().getString(R.string.app_name));
        intent.putExtra("android.intent.extra.TEXT", "");
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", files);
        activity.startActivity(Intent.createChooser(intent, "Select app to share"));
    }

    public static void appInfo(Activity activity, AppInfo app) {
        Intent intent;
        try {
            intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + app.getPackageName()));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(Intent.createChooser(intent, "Select app to share"));
        }
    }

    public static void uninstallApp(Activity activity, AppInfo app) {
        Intent uninstallIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + app.getPackageName()));
        if (!activity.getPackageName().equals(app.getPackageName())) {
            activity.startActivity(uninstallIntent);
        }
    }

    public static boolean isAppExist(Activity activity, String pkg) {
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(pkg, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    public static AppInfo getAppInfo(Context context, String pkgName) {
        if (context == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        AppInfo appInfo = new AppInfo();
        appInfo.setPackageName(pkgName);
        try {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, 0);
                appInfo.setSoftName((String) packageManager.getApplicationLabel(packageInfo.applicationInfo));
                appInfo.setVersion(packageInfo.versionName);
                appInfo.setDrawable(getApplicationIcon(context, pkgName));
                appInfo.setApkPath(packageInfo.applicationInfo.sourceDir);
                File file = new File(packageInfo.applicationInfo.sourceDir);
                if (file.exists()) {
                    appInfo.setAppSize(formartSize(file.length()));
                    appInfo.setAppSizeValue(file.length())  ;
                    appInfo.setAppLastModified(formatDate(file.lastModified())) ;
                    appInfo.setAppLastModifiedValue(file.lastModified()) ;
                }
                int i = 1;
                if ((packageInfo.applicationInfo.flags & 1) > 0) {

                    i = 2;
                }
                appInfo.setType(i);
                return appInfo;


            } catch (NameNotFoundException e) {
                return null;
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            return null;

        }


    }

    private static String formatDate(long date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(date));


    }

    private static String formartSize(long size) {
        StringBuffer localStringBuffer = new StringBuffer();
        if (size >= 1073741824) {
            return String.valueOf(formatDecimal(((double) size) / 1.073741824E9d)) + "GB";
        }
        if (size >= 1048576) {
            return String.valueOf(formatDecimal(((double) size) / 1048576.0d)) + "MB";
        }
        if (size >= 1048) {
            return String.valueOf(formatDecimal(((double) size) / 1024.0d)) + "KB";
        }
        return "";


    }

    private static String formatDecimal(double v) {
        BigDecimal bigDecimal = new BigDecimal(v).setScale(1, 5);
        if (bigDecimal == null) {
            return EnvironmentCompat.MEDIA_UNKNOWN;
        }
        return bigDecimal.toString();

    }

    private static Drawable getApplicationIcon(Context context, String pkgName) {
        try {
            return context.getPackageManager().getApplicationIcon(pkgName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<AppInfo> getQueryIntentActivities(Activity activity, Intent intent) {
        ArrayList<AppInfo> appInfos = new ArrayList();
        AppInfo bluetooth_app_info = null;

        for (ResolveInfo resolveInfo : activity.getPackageManager().queryIntentActivities(intent, 0)) {
            String pakgNam = resolveInfo.activityInfo.packageName;
            AppInfo appInfo = getAppInfo(activity, pakgNam);
            if (pakgNam.equals("com.android.bluetooth") || pakgNam.equals("com.mediatek.bluetooth")) {
                bluetooth_app_info = appInfo;
            } else {
                appInfos.add(appInfo);
            }
        }
        if (bluetooth_app_info != null) {
            appInfos.add(0, bluetooth_app_info);
        }
        return appInfos;

    }


    public static String backup(String originalPath, String fileName) {

        File backup_dir = new File(backup_path);
        if (originalPath == null || fileName == null) {
            return "";
        }
        String strReplace = fileName.replaceAll(" ", "").replace("/", "");
        if (!backup_dir.exists()) {
            backup_dir.mkdir();
        }

        try {
            File original_apk_path = new File(originalPath);
            File backup_app_path = new File(backup_path, "/" + strReplace + ".apk");
            if (backup_app_path.exists()) {
                if (backup_app_path.length() == original_apk_path.length()) {
                    return backup_app_path.getAbsolutePath();
                }
                backup_app_path.delete();
                backup_app_path = new File(backup_dir, "/" + strReplace + ".apk");
            }
            FileInputStream fileInputStream = new FileInputStream(original_apk_path);
            FileOutputStream fileOutputStream = new FileOutputStream(backup_app_path);

            byte[] arrayOfByte = new byte[1024];
            while (true) {
                int i = fileInputStream.read(arrayOfByte);
                if (i <= 0) {
                    fileInputStream.close();
                    fileOutputStream.close();
                    return backup_app_path.getAbsolutePath();
                }
                fileOutputStream.write(arrayOfByte, 0, i);
            }

        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isExistBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }


    private Intent getBluetoothShareIntent(Context ctx, Intent intent) {
        List<ResolveInfo> list = ctx.getPackageManager().queryIntentActivities(intent, 0);
        if (list.isEmpty()) {
            for (ResolveInfo resolveInfo : list) {
                ActivityInfo mActivityInfo = resolveInfo.activityInfo;
                if (!mActivityInfo.packageName.equals("com.android.bluetooth")) {
                    if (mActivityInfo.packageName.equals("com.mediatek.bluetooth")) {
                    }
                }

                // Do a research on this
                resolveInfo.loadLabel(ctx.getPackageManager()).toString();
                intent.setPackage(mActivityInfo.packageName);
                return intent;

            }
        }

        return null;
    }



}
