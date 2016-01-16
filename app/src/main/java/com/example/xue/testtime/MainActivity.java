package com.example.xue.testtime;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private Button button;
    private String packName = "com.example.multisync";
    private String mainClass = "com.example.multisync.MainActivity";
    private int i = 0 ;
    private List<ActivityManager.RunningAppProcessInfo> mlistAppInfo = null;
    private PackageManager pm ;
    private int SyncPid ;
    private final Intent intent = new Intent(Intent.ACTION_MAIN);
    private final ComponentName cn = new ComponentName(packName,mainClass);
    private android.os.Process localProcess ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //下载删除
                    createFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(cn);
                startActivity(intent);
                while (i < 1000)
                    try {
                        querySyncAppInfo();
                        TimeUnit.SECONDS.sleep(600);
                        android.os.Process.killProcess(SyncPid);
                        //下载删除该行
                        createFile();
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                i++;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void querySyncAppInfo() {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));// 排序

        // 保存所有正在运行的包名 以及它所在的进程信息
        Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();

        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            int pid = appProcess.pid; // pid
            String processName = appProcess.processName; // 进程名
            Log.i(null, "processName: " + processName + "  pid: " + pid);

            String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包

            // 输出所有应用程序的包名
            for (int i = 0; i < pkgNameList.length; i++) {
                String pkgName = pkgNameList[i];
                if (packName.equals(pkgName)){
                    System.out.println("packageName " + pkgName + " at index " + i + " in process " + pid);
                    SyncPid = pid ;
                }

            }
        }
    }

    /*
    每次同步完成则删除原有的文件,产生新的文件
     */
    private void createFile() throws IOException {
        final String syncPath = Environment.getExternalStorageDirectory()+"/sync";
        System.out.println(Environment.getExternalStorageDirectory());
        final String syncFile1 = "tmp.1M";
        final String syncFile10 = "tmp.10M";
        final String syncFile20 = "tmp.20M";
        final String syncFile50 = "tmp.50M";
        File file = new File(Environment.getExternalStorageDirectory(),"sync");
        String[] files = file.list();
        for (String s :files){
            System.out.println("Files "+s);
            switch (s){
                case syncFile1:
                    newFile(syncPath+'/'+syncFile1,1);
                    break;
                case syncFile10:
                    newFile(syncPath +'/'+ syncFile10, 10);
                    break;
                case syncFile20:
                    newFile(syncPath+'/'+syncFile20,20);
                    break;
                case syncFile50:
                    newFile(syncPath+'/'+syncFile50,50);
                    break;
                default:
                    break;
            }
        }

    }

    private void newFile(String fileName ,int size) throws IOException {
        System.out.println(fileName);
        File file = new File(fileName) ;
        file.delete();
        file.createNewFile();

        String str = "012345678vasdjhklsadfqwiurewopt";
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            int len = str.length() ;
            int loop = size*1024/10;
            for (int i = 0; i < loop; i++)
            {
                StringBuilder s = new StringBuilder();
                for (int j = 0; j < 10240; j++)
                {
                    s.append(str.charAt((int)(Math.random()*len)));
                }
                pw.println(s.toString());
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        }
    }

