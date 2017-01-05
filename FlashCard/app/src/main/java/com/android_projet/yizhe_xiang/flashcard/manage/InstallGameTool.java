package com.android_projet.yizhe_xiang.flashcard.manage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android_projet.yizhe_xiang.flashcard.database.FlashCardSQLite;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by xiangli on 15/12/16.
 */

public class InstallGameTool {
    Context context;
    public InstallGameTool(Context context){
        this.context=context;
    }
    public int installInternetGame(String mySQL){
        Log.d("mySql",mySQL);
        String sqls[]=mySQL.split(";");
        SQLiteDatabase db= FlashCardSQLite.getInstance(context).getWritableDatabase();
        try {
            for (int i=0;i<sqls.length;i++){
                db.execSQL(sqls[i]);
            }
            //db.execSQL(mySQL);
        }catch (android.database.sqlite.SQLiteConstraintException e){
            return 1;
        }finally {
            db.close();
        }

        Log.d("execSQL","execsql finish");
        return 0;
    }
    /**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     * @throws Exception
     */
    public static int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
        //public static void upZipFile() throws Exception{
        ZipFile zfile=new ZipFile(zipFile);
        Enumeration zList=zfile.entries();
        ZipEntry ze=null;
        byte[] buf=new byte[1024];
        while(zList.hasMoreElements()){
            ze=(ZipEntry)zList.nextElement();
            if(ze.isDirectory()){
                Log.d("upZipFile", "ze.getName() = "+ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = "+dirstr);
                File f=new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = "+ze.getName());
            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
            int readLen=0;
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finish!!!!!!unzip!!!!!!!!!!!!!!!!");
        return 0;
    }
    public static File getRealFileName(String baseDir, String absFileName)
    {
        String[] dirs=absFileName.split("/");
        String lastDir=baseDir;
        if(dirs.length>1)
        {
            for (int i = 0; i < dirs.length-1;i++)
            {
                lastDir +=(dirs[i]+"/");
                File dir =new File(lastDir);
                if(!dir.exists())
                {
                    dir.mkdirs();
                    Log.d("getRealFileName", "create dir = "+(lastDir+"/"+dirs[i]));
                }
            }
            File ret = new File(lastDir,dirs[dirs.length-1]);
            Log.d("upZipFile", "2ret = "+ret);
            return ret;
        }
        else
        {

            return new File(baseDir,absFileName);

        }

    }
}
