package org.fungo.common_core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.channels.FileChannel;

/**
 * 文件处理工具类
 */
public class FileUtil {
    public static File updateDir = null;
    public static File updateFile = null;

    public static final String ROOT_FOLDER = "yuntutv";
    public static final String FOLDER_CAMERA = "camera";
    public static final String FOLDER_CROP = "crop";        //放在camera目录下

    public static final String CAMERA_IMG_EXT = ".jpg";


    public static void init() {
        //创建相册目录
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            //创建相册目录
            File file = new File(getCropFolder());
            file.mkdirs();
        }
    }


    /**
     * yuntutv下面的一些子目录名称的获取
     */

    public static String getRootFolder() {
        return Environment.getExternalStorageDirectory()
                + "/" + ROOT_FOLDER;
    }

    /**
     * 获取应用相册目录
     *
     * @return
     */
    public static String getCameraFolder() {
        return getRootFolder() + "/" + FOLDER_CAMERA;
    }

    /**
     * 获取裁剪目录
     *
     * @return
     */
    public static String getCropFolder() {
        return getCameraFolder() + "/" + FOLDER_CROP;
    }

    /**
     * 按照时间生成一张图片路径
     *
     * @return
     */
    public static String getCameraPath() {
        return getCameraFolder() + "/" + System.currentTimeMillis() + CAMERA_IMG_EXT;
    }


    /**
     * 获取剪切路径
     *
     * @param path 图片原始路径
     * @return
     */
    public static String getCropPath(String path) {
        return getCropFolder() + "/" + getFileName(path);
    }

    /***
     * 创建文件
     */
    public static void createFile(String name) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory()
                    + "/" + ROOT_FOLDER);
            updateFile = new File(updateDir + "/" + name + ".apk");

            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 根据路径获取父目录
     *
     * @param path
     */
    public static String getFolder(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    /**
     * 根据路径获取文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /*
     * 递归删除目录
     */
    public static void deleteDirRecursive(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            } else {
                deleteDirRecursive(f);
            }
        }
        dir.delete();
    }

    /**
     * 递归删除当前目录下的空目录
     *
     * @param dir
     */
    public static void deleteEmptyDirsRecursive(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        if (files.length == 0) {
            dir.delete();
        } else {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteEmptyDirsRecursive(f);
                }
            }

            //子目录处理完，再处理本目录
            files = dir.listFiles();
            if (files != null && files.length == 0) {
                dir.delete();
            }
        }
    }

    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 将文件保存到本地
     */
    public static void saveFileCache(byte[] fileData,
                                     String folderPath, String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        OutputStream os = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            } catch (Exception e) {
                Logger.e(e);
            } finally {
                closeIO(is, os);
            }
        }
    }

    /**
     * 从指定文件夹获取文件
     *
     * @return 如果文件不存在则创建, 如果如果无法创建文件或文件名为空则返回null
     */
    public static File getSaveFile(String folderPath, String fileNmae) {
        File file = new File(getSavePath(folderPath) + File.separator
                + fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Logger.e(e);
        }
        return file;
    }

    /**
     * 从指定路径获取文件
     *
     * @return 如果文件不存在则创建, 如果如果无法创建文件或文件名为空则返回null
     */
    public static File getSaveFile(String path) {
        File file = new File(path);
        return file;
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        File file = new File(Environment
                .getExternalStorageDirectory().getAbsoluteFile()
                + File.separator + folderName + File.separator);
        file.mkdirs();
        return file;
    }

    /**
     * 输入流转byte[]<br>
     * <p/>
     * <b>注意</b> 你必须手动关闭参数inStream
     */
    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        byte[] in2b = null;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        try {
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            in2b = swapStream.toByteArray();
        } catch (IOException e) {
            Logger.e(e);
        } finally {
            closeIO(swapStream);
        }
        return in2b;
    }

    /**
     * 把uri转为File对象
     */
    public static File uri2File(Activity aty, Uri uri) {
        if (Build.VERSION.SDK_INT < 11) {
            // 在API11以下可以使用：managedQuery
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor actualimagecursor = aty.managedQuery(uri, proj,
                    null, null, null);
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor
                    .getString(actual_image_column_index);
            return new File(img_path);
        } else {
            // 在API11以上：要转为使用CursorLoader,并使用loadInBackground来返回
            String[] projection = {MediaStore.Images.Media.DATA};
            android.content.CursorLoader loader = new android.content.CursorLoader(aty, uri,
                    projection, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return new File(cursor.getString(column_index));
        }
    }

    /**
     * 复制文件
     *
     * @param from
     * @param to
     */
    public static void copyFile(File from, File to) {
        if (null == from || !from.exists()) {
            return;
        }
        if (null == to) {
            return;
        }
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(from);
            if (!to.exists()) {
                to.createNewFile();
            }
            os = new FileOutputStream(to);
            copyFileFast(is, os);
        } catch (Exception e) {
            Logger.e(e);
        } finally {
            closeIO(is, os);
        }
    }

    /**
     * 快速复制文件（采用nio操作）
     *
     * @param is 数据来源
     * @param os 数据目标
     * @throws IOException
     */
    public static void copyFileFast(FileInputStream is,
                                    FileOutputStream os) throws IOException {
        FileChannel in = is.getChannel();
        FileChannel out = os.getChannel();
        in.transferTo(0, in.size(), out);
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }

    /**
     * 关闭流
     */
    public static void closeSocket(Socket... socket) {
        if (null == socket || socket.length <= 0) {
            return;
        }
        for (Socket st : socket) {
            try {
                if (null == st) {
                    continue;
                }
                st.close();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
    }

    /**
     * 图片写入文件
     *
     * @param bitmap   图片
     * @param filePath 文件路径
     * @return 是否写入成功
     */
    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null) {
            return isSuccess;
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(
                    filePath), 8 * 1024);
            isSuccess = bitmap.compress(CompressFormat.PNG, 70, out);
        } catch (FileNotFoundException e) {
            Logger.e(e);
        } finally {
            closeIO(out);
        }
        return isSuccess;
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            Logger.e(e);
        }
        return inputStream2String(is);
    }

    /**
     * 从assets中读取文本
     *
     * @param name
     * @return
     */
    public static String readFileFromAssets(Context context,
                                            String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            Logger.e(e);
        }
        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
            Logger.e(ex);
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }

//    /**
//     * 纠正图片角度（有些相机拍照后相片会被系统旋转）
//     *
//     * @param path 图片路径
//     */
//    public static void correctPictureAngle(String path) {
//        int angle = BitmapOperateUtil.readPictureDegree(path);
//        if (angle != 0) {
//            Bitmap image = BitmapHelper.rotate(angle,
//                    BitmapCreate.bitmapFromFile(path, 1000, 1000));
//            bitmapToFile(image, path);
//        }
//    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * drawable 到本地目录,成功则返回图片地址, 否则返回null
     *
     * @param drawable
     * @return
     */
    public static String saveDrawable(Context context, Drawable drawable) {
        String filePath = getCameraPath();

        if (drawable != null) {
            FileOutputStream outStream = null;
            File file = new File(filePath);
            try {
                outStream = new FileOutputStream(file);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                bitmap.compress(CompressFormat.PNG, 100, outStream);
                outStream.flush();
                //bitmap.recycle();
                scanPhotos(context, filePath);
            } catch (Throwable e) {
                filePath = null;
                e.printStackTrace();
            } finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * drawable 到本地目录,成功则返回图片地址, 否则返回null
     *
     * @param bp
     * @return
     */
    public static String saveBitmap(Context context, Bitmap bp) {
        String filePath = getCameraPath();

        if (bp != null) {
            FileOutputStream outStream = null;
            File file = new File(filePath);
            try {
                outStream = new FileOutputStream(file);
                bp.compress(CompressFormat.PNG, 100, outStream);
                outStream.flush();
                //bitmap.recycle();
                scanPhotos(context, filePath);
            } catch (Throwable e) {
                filePath = null;
                e.printStackTrace();
            } finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * 扫描、刷新相册
     */
    public static void scanPhotos(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


    /**
     * 检测公共存储区域(SD卡, 或部分内置空间)是否可读写
     */
    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    public static boolean renameFile(File toFile, File fromeFile) {
        boolean rename = false;
        try {
            if (toFile.exists()) {
                boolean delete = toFile.delete();
                if (delete) {
                    rename = fromeFile.renameTo(toFile);
                }
            } else {
                rename = fromeFile.renameTo(toFile);
            }
        } catch (Throwable throwable) {
            Logger.e(throwable);
        }

        return rename;
    }


    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
