package cn.xueximiao.tv.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.xueximiao.tv.R;
import cn.xueximiao.tv.activity.ContactUsActivity;
import cn.xueximiao.tv.activity.MeowApplication;
import cn.xueximiao.tv.entity.GetQRCodeEntity;
import cn.xueximiao.tv.entity.VersionCheckEntity;
import cn.xueximiao.tv.http.HttpAddress;
import cn.xueximiao.tv.http.HttpRequest;

/**
 * 检查版本更新，辅助类
 *
 * @author Administrator
 */
public class VersionCheckUtils {
    private final String TAG = VersionCheckUtils.class.getSimpleName();
    private final String DESTFILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/android/data/" + MeowApplication.getContext().getPackageName() + "/apk/";
    private final String DESTFILE_NAME = "xueximiao.apk";//默认保存的文件名，实际会先根据url截取最后一个"/"之后的部分作为文件名
    private final static int WHAT_PERCENT = 100;
    private final static int WHAT_DOWNLOAD_FAIL = WHAT_PERCENT + 1;

    private AlertDialog alertDialog;
    private Context context;
    long myDownloadReference;
    String myDownloadUrl;
    private Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    public static String VERSIONNAME;
    public static int VERSIONCODE;
    private boolean isMustUpdate;
    private String fileName;

    private boolean isFirstPage = false;//是否是首页在调用，首页不需要每次都弹出对话框


    /**
     * @param context
     */
    public VersionCheckUtils(Context context,boolean isFirstPage) {
        this.context = context;
        this.isFirstPage = isFirstPage;
    }

    /**
     * 获取手机的版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int version = 1;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public void checkFromServer() {
            String  updateTime = SharePrefUtil.getString(context,SharePrefUtil.KEY_UPDATE_TIME,"");
            String today = getTodayDay();
            if(isFirstPage && today.equals(updateTime)){//首页今天已经取消过一次更新对话框
                return;
            }
            HttpRequest.get(HttpAddress.getUpdateUrl(), null, VersionCheckUtils.this, "checkVersionBack", null, context, VersionCheckEntity.class);
        }

        public void checkVersionBack(VersionCheckEntity entity,String result) {
            if(entity != null && entity.status) {
                String url = entity.data.url;
                String versionName = entity.data.versionName;
                String desc = entity.data.describe;
                String time = entity.data.updatetime;
                boolean isMust = entity.data.isMustUpdate;

                if (isMust) {
                    //  强制更新
                    showCheckVersionAlerDialog(true, desc, url, versionName);
                } else {
                    //非强制更新
                    showCheckVersionAlerDialog(false, desc, url, versionName);

                }
            } else if(entity.status == false){
                //已经是最新
                ToastUtil.showShort(context,"已经是最新版本");
            }

    }

    Dialog nbDialog;
    View root;

    private void showCheckVersionAlerDialog(final boolean isMust, final String content, final String url, String version_name) {
        isMustUpdate = isMust;
        if (nbDialog == null) {
            nbDialog = new Dialog(context, R.style.UpdateDialog);
            manager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
            root = LayoutInflater.from(context).inflate(R.layout.update_dialog, null);

            TextView contentTv = (TextView) root.findViewById(R.id.content);
            contentTv.setText(content);
            contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
            final Button cancelBtn = (Button) root.findViewById(R.id.leftbutton);
            final Button exitBtn = (Button) root.findViewById(R.id.cancel);
            ((TextView) root.findViewById(R.id.title)).setText("检测到新版本".concat(version_name));
            ((TextView) root.findViewById(R.id.title2)).setText("升级".concat(version_name));
            cancelBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if(focus) {
                        view.setSelected(true);
                    } else {
                        view.setSelected(false);
                    }
                }
            });
            exitBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if(focus) {
                        view.setSelected(true);
                    } else {
                        view.setSelected(false);
                    }
                }
            });
            if (isMust) {
                cancelBtn.setVisibility(View.GONE);
                nbDialog.setCanceledOnTouchOutside(false);
                nbDialog.setCancelable(false);
                cancelBtn.setText("退出");
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nbDialog.dismiss();
                        exit();
                    }
                });
                exitBtn.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  manager.remove(myDownloadReference);
                                                  nbDialog.dismiss();
                                                  System.exit(0);
                                              }
                                          }
                );

            } else {
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nbDialog.dismiss();
                        SharePrefUtil.saveString(context,SharePrefUtil.KEY_UPDATE_TIME,getTodayDay());
                    }
                });
                exitBtn.setOnClickListener(
                        new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  manager.remove(myDownloadReference);
                                  nbDialog.dismiss();
                                  SharePrefUtil.saveString(context,SharePrefUtil.KEY_UPDATE_TIME,getTodayDay());
                              }
                        }
                );
                cancelBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelBtn.requestFocus();
                    }
                },200);
            }
            final Button commitBtn = (Button) root.findViewById(R.id.rightbutton);
            commitBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if(focus) {
                        view.setSelected(true);
                    } else {
                        view.setSelected(false);
                    }
                }
            });
            commitBtn.setNextFocusLeftId(cancelBtn.getId());
            cancelBtn.setNextFocusRightId(commitBtn.getId());
            commitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    root.findViewById(R.id.msg1).setVisibility(View.GONE);
                    root.findViewById(R.id.msg2).setVisibility(View.VISIBLE);
                    String localPath = queryFinishedDownloadTask(url);
                    if(localPath != null) {
                        nbDialog.dismiss();
                        installAPK(localPath);
                        if(isMust) {
                            exit();
                        }
                    } else {
                        downLoadAPK(url);
                        exitBtn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                exitBtn.setSelected(true);
                                exitBtn.requestFocus();
                            }
                        },200);
                    }

                }
            });

            nbDialog.show();
            nbDialog.setContentView(root);

            commitBtn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    commitBtn.setSelected(true);
                    commitBtn.requestFocus();
                }
            },200);
        }

        if (!nbDialog.isShowing()) {
            nbDialog.show();
        }
    }

    private void exit() {
        if(context == null) {
            return;
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private DownloadChangeObserver downloadObserver;
    DownloadManager manager;

    protected void downLoadAPK(String url) {
        try {
            Uri uri = Uri.parse(url);
            DownloadManager.Request down = new DownloadManager.Request(uri);
//            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
//            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            down.setShowRunningNotification(true);
            // 显示下载界面
            down.setVisibleInDownloadsUi(true);
            // 设置下载后文件存放的位置
            fileName = uri.getLastPathSegment();
            fileName =  fileName == null ? DESTFILE_NAME : fileName;
            File path = new File(DESTFILE_PATH);
            if(!path.exists()) {
                path.mkdirs();
            }
            File file = new File(DESTFILE_PATH + fileName);
            Uri dstUri = Uri.fromFile(file);
            down.setDestinationUri(dstUri);
            // 将下载请求放入队列
            myDownloadReference = manager.enqueue(down);
            myDownloadUrl = url;
            downloadObserver = new DownloadChangeObserver(handlerDownload);
            context.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
        } catch (Exception e) {
//            if (e.toString().indexOf("Unknown URL content://downloads/my_downloads") != -1)
            ToastUtil.showShort(context,"下载失败");
            e.printStackTrace();
            goToBrowseToDownload(url);
            if (nbDialog.isShowing()) {
                nbDialog.dismiss();
            }
        }
    }

    private void goToBrowseToDownload(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handlerDownload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_PERCENT:
                    updatePercent(msg);
                    break;
                case WHAT_DOWNLOAD_FAIL:
                    String reason = (String) msg.obj;
                    ToastUtil.showShort(context,reason);
                    break;
            }
        }
    };

    private void updatePercent(Message msg) {
        if(msg.arg2 == 0) {
            return;
        }
        int progress = msg.arg1 * 100 / msg.arg2;
        ((ProgressBar) root.findViewById(R.id.progress)).setProgress(progress);
        ((TextView) root.findViewById(R.id.progressnum)).setText(progress + "%");
        if (msg.arg1 == msg.arg2) {
            nbDialog.dismiss();
            step(myDownloadReference);
        }
    }

    class DownloadChangeObserver extends ContentObserver {

        Handler h;

        public DownloadChangeObserver(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
            h = handler;
        }


        @Override
        public void onChange(boolean selfChange) {
            queryDownloadStatus(h);
        }
    }

    private void queryDownloadStatus(Handler h) {
        Log.d(TAG,"queryDownloadStatus");
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(myDownloadReference);
        Cursor c = manager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
            int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
            int fileSizeIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesDLIdx =
                    c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int uriIdx = c.getColumnIndex(DownloadManager.COLUMN_URI);
            String title = c.getString(titleIdx);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);
            float percent = bytesDL * 100f / fileSize;
            Log.d(TAG,"percent:" + percent);
            if(uriIdx >= 0) {
                String uri = c.getString(uriIdx);
                Log.d(TAG,"uri:" + uri);
            }
            Message msg = new Message();
            // Display the status
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                    //正在下载，不做任何事情
                case DownloadManager.STATUS_SUCCESSFUL:
                    msg.arg1 = bytesDL;
                    msg.arg2 = fileSize;
                    msg.what = WHAT_PERCENT;
                    h.sendMessage(msg);
                    //完成
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                    if(reasonIdx >= 0) {
                        String failReason = c.getString(reasonIdx);
                        msg.what = WHAT_DOWNLOAD_FAIL;
                        msg.obj = failReason;
                        h.sendMessage(msg);
                    }
                    break;
            }
            c.close();
        }
    }


    public void step(long reference) {
        if (reference != myDownloadReference || myDownloadUrl == null)
            return;
        String path = "file://" + DESTFILE_PATH + fileName;

        String localPath = queryFinishedDownloadTask(myDownloadUrl);
        if(localPath != null) {
            nbDialog.dismiss();
            installAPK(localPath);
            if(isMustUpdate) {
                System.exit(0);
            }
        }
    }

    private void installAPK(String path) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        //加上这个Flag才能保证安装完成后显示安装完成界面，否则安装过程直接在原app task中，一安装完成就被连带和原app一起被系统kill掉
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    public void deleteAPK(String packageName) {
        if (!packageName.equals(context.getApplicationContext().getPackageName()))
            return;
        File file = new File(DESTFILE_PATH + fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 查找是否之前已经有下载成功了的apk，要求任务相同，已经下载完成，并且本地文件还存在
     * @param url
     * @return
     */
    private String queryFinishedDownloadTask(String url) {
        if(url == null) {
            return null;
        }
        Log.d(TAG, "url:" + url);
        String localPath = null;
        String tmpPath = null;
        File file = null;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = manager.query(query);
        if (c != null && c.moveToFirst()) {
            while(!c.isAfterLast()) {
                int filePathIdx =
                        c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                int uriIdx = c.getColumnIndex(DownloadManager.COLUMN_URI);
                int desIdx = c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);
                if (uriIdx >= 0) {
                    String uri = c.getString(uriIdx);
                    String des = c.getString(desIdx);
                    Log.d(TAG, "uri:" + uri);
                    if(!url.equals(uri)) {
                        Log.d(TAG,"url not match or md5 not match.");
                        c.moveToNext();
                        continue;
                    } else {
                        Log.d(TAG,"url and md5 match.");
                    }
                }
                if (filePathIdx >= 0) {
                    tmpPath = c.getString(filePathIdx);
                    Log.d(TAG, "tmpPath:" + tmpPath);
                    try {
                        file = new File(new URI(tmpPath));
                        if (file.exists() && parseAPK(tmpPath)) {
                            localPath = tmpPath;
                            break;
                        } else {
                            Log.d(TAG, "downloaded local file not exist or parse failed or md5 not match. ");
                        }
                    } catch (Exception ue){
                        ue.printStackTrace();
                    }
                }
                c.moveToNext();
            }
            c.close();
        }
        return localPath;
    }

    private boolean parseAPK(String path) {
        if(!TextUtils.isEmpty(path)) {
            path = path.replace("file://","");
        }
        PackageManager pm = MeowApplication.getContext().getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path,0);
        if(packageInfo != null) {
            Log.d(TAG,"parse apk success.");
            return true;
        }
        Log.d(TAG,"parse apk failed.");
        return false;
    }

    public void destroy() {
        if(nbDialog != null) {
            nbDialog.dismiss();
        }
    }

    private static String getTodayDay() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(new Date(System.currentTimeMillis()));
    }
}
