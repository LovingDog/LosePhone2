package com.example.wanghanp.losephone.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.losephone.MainActivity;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class CameraManager {

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private boolean mSafeTakePhotos = true;
    private TakePhotosListener mTakePhotosListener;
    private Context mContext;
    private File mPictureFile;
    private RemoveMoreFilesListener mRemoveMoreFileListener;

    public CameraManager(Context context, Camera camera, SurfaceHolder holder, TakePhotosListener takePhotosListener) {
        this.mContext = context;
        mCamera = camera;
        mHolder = holder;
        this.mTakePhotosListener = takePhotosListener;
    }


    public Camera getCamera() {
        return mCamera;
    }

    public void setmRemoveMoreFileListener(RemoveMoreFilesListener mRemoveMoreFileListener) {
        this.mRemoveMoreFileListener = mRemoveMoreFileListener;
        deleteMoreFile();
    }

    /**
     * 打开相机
     *
     * @param camera  照相机对象
     * @param holder  用于实时展示取景框内容的控件
     * @param tagInfo 摄像头信息，分为前置/后置摄像头 Camera.CameraInfo.CAMERA_FACING_FRONT：前置
     *                Camera.CameraInfo.CAMERA_FACING_BACK：后置
     * @return 是否成功打开某个摄像头
     */
    public boolean openCamera(int tagInfo) {
        // 尝试开启摄像头
        try {
            Log.d("wanghp007", "openCamera: getCameraId(tagInfo) == " + getCameraId(tagInfo));
            mCamera = Camera.open(getCameraId(tagInfo));
            mCamera.setDisplayOrientation(90);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.d("wanghp007", "openCamera: e == "+e);
//            Log.d("wanghp007", "openCamera: e == "+e);
            return false;
        }
        // 开启前置失败
        if (mCamera == null) {
            return false;
        }
        // 将摄像头中的图像展示到holder中
        try {
            // 这里的myCamera为已经初始化的Camera对象
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
            // 如果出错立刻进行处理，停止预览照片
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        // 如果成功开始实时预览
        mCamera.startPreview();
        mSafeTakePhotos = true;
        return true;
    }

    /**
     * @return 前置摄像头的ID
     */
    public int getFrontCameraId() {
        return getCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * @return 后置摄像头的ID
     */
    public int getBackCameraId() {
        return getCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * @param tagInfo
     * @return 得到特定camera info的id
     */
    private int getCameraId(int tagInfo) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        // 开始遍历摄像头，得到camera info
        int cameraId, cameraCount;
        for (cameraId = 0, cameraCount = Camera.getNumberOfCameras(); cameraId < cameraCount; cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);

            if (cameraInfo.facing == tagInfo) {
                break;
            }
        }
        return cameraId;
    }

    /**
     * 定义图片保存的路径和图片的名字
     */
    public final static String PHOTO_PATH = "mnt/sdcard/LosePhone/Camera/";

    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'LOCK'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    public void setmSafeTakePhotos(boolean mSafeTakePhotos) {
        this.mSafeTakePhotos = mSafeTakePhotos;
    }

    public boolean ismSafeTakePhotos() {
        return mSafeTakePhotos;
    }

    /**
     * 拍照成功回调
     */
    private FinishTakeListener mFinishTakeListener;
    public class PicCallback implements Camera.PictureCallback {
        private String TAG = getClass().getSimpleName();
        private Camera mCamera;

        public PicCallback(Camera camera) {
            // TODO 自动生成的构造函数存根
            mCamera = camera;
            Log.d("wanghp007", "PicCallback:mCamera == null "+(mCamera == null));
        }

        /* 
         * 将拍照得到的字节转为bitmap，然后旋转，接着写入SD卡
         * @param data 
         * @param camera
         */
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 将得到的照片进行270°旋转，使其竖直
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.preRotate(270);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            // 创建并保存图片文件
            File mFile = new File(PHOTO_PATH);
            if (!mFile.exists()) {
                mFile.mkdirs();
            }
            File pictureFile = new File(PHOTO_PATH, getPhotoFileName());
            Log.d("wanghp007", "拍摄成功！pictureFile = " + pictureFile.getAbsolutePath());
            mSafeTakePhotos = true;
            if (pictureFile == null) {

            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                bitmap.recycle();
                fos.close();
                Log.i(TAG, "拍摄成功！pictureFile = " + pictureFile.getAbsolutePath());

            } catch (Exception error) {
                Log.e(TAG, "拍摄失败");
                error.printStackTrace();
            } finally {
//                    mCamera.stopPreview();
//                    mCamera.release();
//                    mCamera = null;
            }
            mTakePhotosListener.takePhotosSuccessListener(pictureFile);
        }
    }

    public interface FinishTakeListener{
        void finishTakePhotoslistener();
    }

    public void ondestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public ArrayList<TakePhotoBean> getTakePhotosList() {
        mPictureFile = new File(PHOTO_PATH);
        TakePhotoBean takePhotoBean;
        ArrayList<TakePhotoBean> pathList = new ArrayList<>();
        if (mPictureFile.exists() && mPictureFile.isDirectory()) {
            File[] files = mPictureFile.listFiles();
            Log.d("wanghp007", "getTakePhotosList: file.length() = " + files.length);
            int length = files.length;
            for (int i = 0; i < length; i++) {
                takePhotoBean = new TakePhotoBean();
                File file = files[i];
                if (file.exists()) {
                    takePhotoBean.setPath(files[i].getAbsolutePath());
                    pathList.add(takePhotoBean);
                }
            }
        }
        return pathList;
    }

    public void deleteMoreFile() {
//        Toast.makeText(mContext.getApplicationContext() , "doInBackground: file.length() = " +files.length, Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... files) {
                while (true) {
                    if (mPictureFile.exists() && mPictureFile.isDirectory()) {
                        File[] photosList = mPictureFile.listFiles();
                        if (photosList.length > 30) {
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            File file = photosList[photosList.length - 1];
                            long lTime = file.lastModified();
                            File file2 = photosList[0];
                            long sTime = file2.lastModified();
                            if (sTime > lTime) {
                                file.delete();
                            } else {
                                file2.delete();
                            }
                        } else {
                            break;
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                mRemoveMoreFileListener.removeMoreSuccessListener();
            }
        }.execute();
    }

//    //鎵撳紑鍓嶇疆鎽勫儚澶�
//    public boolean openFacingFrontCamera() {
//
//
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
//            Camera.getCameraInfo(camIdx, cameraInfo);
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                try {
//                    Log.d("Demo", "tryToOpenFrontCamera");
//                    mCamera = Camera.open(camIdx);
//                } catch (RuntimeException e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//        }
//
//        //
//        if (mCamera == null) {
//            for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
//                Camera.getCameraInfo(camIdx, cameraInfo);
//                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    try {
//                        mCamera = Camera.open(camIdx);
//                    } catch (RuntimeException e) {
//                        return false;
//                    }
//                }
//            }
//        }
//
//        try {
//            //杩欓噷鐨刴yCamera涓哄凡缁忓垵濮嬪寲鐨凜amera瀵硅薄
//            mCamera.setPreviewDisplay(mHolder);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//        }
//
//        mCamera.startPreview();
//
//        return true;
//    }

    public interface TakePhotosListener {
        void takePhotosSuccessListener(File file);
    }

    public interface RemoveMoreFilesListener {
        void removeMoreSuccessListener();
    }
}