package com.bing.face_re;

import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;
import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;  
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1; 
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

public class MainActivity extends Activity {

	private static final String TAG="Face_Recognition";
	private CascadeClassifier mjavaClassifier;
	private Button faceButton;
	private FacePojo fp;  
	private List<FacePojo> faceList = new ArrayList<FacePojo>();  
	
	private List<FacePojo> faceList_new = new ArrayList<FacePojo>();  
	
	private Bitmap mBitmap,nBitmap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		faceButton=(Button)findViewById(R.id.recon);
		faceButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(faceRunnable).start();
			}
		});
		
		mBitmap=readBitMap(this, R.drawable.b);
		nBitmap=readBitMap(this, R.drawable.bnj);
		
	}

	
		
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
		
		LoadFaceData();
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {

		private File mCascadeFile;

		@Override
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				try {
                    // load cascade file from application resources
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
                    FileOutputStream os = new FileOutputStream(mCascadeFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();
				mjavaClassifier=new CascadeClassifier(mCascadeFile.getAbsolutePath());
				if (mjavaClassifier!=null) {
					Toast.makeText(MainActivity.this, "加载成功"+mjavaClassifier.toString(), Toast.LENGTH_LONG).show();
				}
				cascadeDir.delete();
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				break;

			default:
				break;
			}
			super.onManagerConnected(status);
		}
		
	};
	
	private String FACE=Environment.getExternalStorageDirectory()  
            + "/bing/刘兵.jpg";
	private String FACEDONE=Environment.getExternalStorageDirectory()  
            + "/bing/jjf.jpg";
	public void DetectFace() {  
		
		int picnum=faceList.size();
		
		/************遍历加载 ***********/
		for (int i = 0; i < picnum; i++) {
			 Mat image = Highgui.imread(faceList.get(i).getPath());  
		        MatOfRect faceDetections = new MatOfRect();  
		        mjavaClassifier.detectMultiScale(image, faceDetections);  
		        int k=0;
		        for (Rect rect : faceDetections.toArray()) {  
		            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x  
		                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));  

		            Mat sub = image.submat(rect);  
		            Mat mat = new Mat();  
		            Size size = new Size(100, 100);  
		            Imgproc.resize(sub, mat, size);  
		            boolean life= Highgui.imwrite(Environment.getExternalStorageDirectory()+"/FaceData/bing"+"_"+i
		            		+".jpg", mat);
		            Log.i(TAG, "mat:"+mat.toString());
		            Log.i(TAG, "保存:"+life);
		            k++;
		        }  
		}
//        Mat image = Highgui.imread(FACE);  
//        MatOfRect faceDetections = new MatOfRect();  
//        mjavaClassifier.detectMultiScale(image, faceDetections);  
//        int k=0;
//        for (Rect rect : faceDetections.toArray()) {  
//            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x  
//                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));  
//
//            Mat sub = image.submat(rect);  
//            Mat mat = new Mat();  
//            Size size = new Size(100, 100);  
//            Imgproc.resize(sub, mat, size);  
//            boolean life= Highgui.imwrite(FACEDONE, mat);
//            Log.i(TAG, "mat:"+mat.toString());
//            Log.i(TAG, "保存:"+life);
//            k++;
//        }  
//        Log.i(TAG, "数值:"+k);
//        if (k == 0) {  
//           writePhoto(BitmapFactory.decodeFile(FACE));  
//        } 
    }  
	
	
	 public int Identification() {  
	        FaceRecognizer fr = createFisherFaceRecognizer();  
	        MatVector mv = new MatVector(faceList.size());  
	        CvMat cvMat = CvMat.create(faceList.size(), 1, CV_32SC1);  
	        for (int i = 0; i < faceList.size(); i++) {  
	        	
	            IplImage img = cvLoadImage(faceList.get(i).path,  
	                    CV_LOAD_IMAGE_GRAYSCALE);  
	            mv.put(i, img);  
	            cvMat.put(i, 0, i);  
	            
	        }  
	        fr.train(mv, cvMat);  
	        IplImage testImage = cvLoadImage(  
	                Environment.getExternalStorageDirectory()  
	                        + "/FaceData/facedone.jpg", CV_LOAD_IMAGE_GRAYSCALE);  
	        int m=0;
	        m=fr.predict(testImage);
	        Log.i(TAG, "相似度:"+m);
	        return m;
	      	  
	    }  
	
	 
	 
	 public int Identification_facedata() {  
	        FaceRecognizer fr = createFisherFaceRecognizer();  
	        MatVector mv = new MatVector(faceList_new.size());  
	        CvMat cvMat = CvMat.create(faceList_new.size(), 1, CV_32SC1);  
//	        Log.i(TAG, "璧勬簮:"+cvMat);
	        for (int i = 0; i < faceList_new.size(); i++) {  
//	        	 Log.i(TAG, "图片数量:"+faceList.size());
//	        	 Log.i(TAG, "路径:"+faceList.get(i).getPath());
	        	
	            IplImage img = cvLoadImage(faceList_new.get(i).path,  
	                    CV_LOAD_IMAGE_GRAYSCALE);  
//	            Log.i(TAG, "璧勬簮:"+img);
	            mv.put(i, img);  
//	            Log.i(TAG, "璧勬簮:"+mv);
	            cvMat.put(i, 0, i);  
	            
	        }  
	        fr.train(mv, cvMat);  
	        IplImage testImage = cvLoadImage(  
	                Environment.getExternalStorageDirectory()  
	                        + "/FaceData/bing_4.jpg", CV_LOAD_IMAGE_GRAYSCALE);  
	        int m=0;
	        m=fr.predict(testImage);
	        Log.i(TAG, "相似度:"+m);
	        return m;
	      	  
	    }  
	
	
	
	public double CmpPic(String path) {  
        int l_bins = 20;  
        int hist_size[] = { l_bins };  
  
        float v_ranges[] = { 0, 100 };  
        float ranges[][] = { v_ranges };  
  
        IplImage Image1 = cvLoadImage(Environment.getExternalStorageDirectory()  
                + "/bing/BING1.jpg", CV_LOAD_IMAGE_GRAYSCALE);  
        IplImage Image2 = cvLoadImage(path, CV_LOAD_IMAGE_GRAYSCALE);  
  
        IplImage imageArr1[] = { Image1 };  
        IplImage imageArr2[] = { Image2 };  
  
        CvHistogram Histogram1 = CvHistogram.create(1, hist_size,  
                CV_HIST_ARRAY, ranges, 1);  
        CvHistogram Histogram2 = CvHistogram.create(1, hist_size,  
                CV_HIST_ARRAY, ranges, 1);  
  
        cvCalcHist(imageArr1, Histogram1, 0, null);  
        cvCalcHist(imageArr2, Histogram2, 0, null);  
  
        cvNormalizeHist(Histogram1, 1.0);  
        cvNormalizeHist(Histogram2, 1.0);  
        double ccc0=cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);  
        double ccc1=cvCompareHist(Histogram1, Histogram2, 1);  
        double ccc2=cvCompareHist(Histogram1, Histogram2, 2);  
        double ccc3=cvCompareHist(Histogram1, Histogram2, 3);  
        Log.i(TAG, "检测结果:"+ccc0+"::"
        		+ccc1+"::"+ccc2+"::"+ccc3);
        double bing=((1-ccc1)+(1-ccc3)+ccc0+ccc2)/4;
        Log.i(TAG, "最终结果:"+bing);
        return cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);  
    }  
	
	
	public double CmpPicBit(Bitmap mBitmap,Bitmap nBitmap) {  
		Bitmap mBitmap2=mBitmap.copy(Config.ARGB_8888, true);
		Bitmap nBitmap2=nBitmap.copy(Config.ARGB_8888, true);
        int l_bins = 20;  
        int hist_size[] = { l_bins };  
  
        float v_ranges[] = { 0, 100 };  
        float ranges[][] = { v_ranges };  
        IplImage Image1=IplImage.create(mBitmap2.getWidth(), 
        		mBitmap2.getHeight(), 
        		IPL_DEPTH_8U,
        		4);
        mBitmap2.copyPixelsToBuffer(Image1.getByteBuffer());
        
        IplImage Image2=IplImage.create(nBitmap2.getWidth(), 
        		nBitmap2.getHeight(), 
        		IPL_DEPTH_8U,
        		4);
        nBitmap2.copyPixelsToBuffer(Image2.getByteBuffer());
        
  
        IplImage imageArr1[] = { Image1 };  
        IplImage imageArr2[] = { Image2 };  
  
        Log.i(TAG, "Image1:"+Image1+"Image"+Image2);
        
        CvHistogram Histogram1 = CvHistogram.create(1, hist_size,  
                CV_HIST_ARRAY, ranges, 1);  
        CvHistogram Histogram2 = CvHistogram.create(1, hist_size,  
                CV_HIST_ARRAY, ranges, 1);  
        Log.i(TAG, "Histogram1:"+Histogram1+"Histogram2:"+Histogram2);
        cvCalcHist(imageArr1, Histogram1, 0, null);  
        cvCalcHist(imageArr2, Histogram2, 0, null);  
        Log.i(TAG, "Histogram1:"+Histogram1+"Histogram2:"+Histogram2);
        cvNormalizeHist(Histogram1, 1.0);  
        cvNormalizeHist(Histogram2, 1.0);  
        double ccc0=cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);  
        double ccc1=cvCompareHist(Histogram1, Histogram2, 1);  
        double ccc2=cvCompareHist(Histogram1, Histogram2, 2);  
        double ccc3=cvCompareHist(Histogram1, Histogram2, 3);  
        Log.i(TAG, "检测结果:"+ccc0+"::"
        		+ccc1+"::"+ccc2+"::"+ccc3);
        double bing=((1-ccc1)+(1-ccc3)+ccc0+ccc2)/4;
        Log.i(TAG, "最终结果:"+bing);
        mBitmap.recycle();
        mBitmap2.recycle();
        nBitmap.recycle();
        nBitmap2.recycle();
        System.gc();
        
        return cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);  
    }  
	
	Runnable faceRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			DetectFace();
//			for (int i = 0; i < faceList.size(); i++) {
//				CmpPic(faceList.get(i).getPath());
//				Log.i(TAG, "路径:"+faceList.get(i).getPath());
//			}
			
			CmpPicBit(mBitmap, nBitmap);
//			DetectFace();
//			LoadFaceData_new();
//			Identification_facedata();
//			Identification();
//			CmpPic("/storage/sdcard0/bing/IMG_20131108_164516.jpg");
//			Identification();
			Log.i("=============", "结束识别");
//			CmpPic(FACEDONE);
		}
	};
	
	/**
	 * 加载本地图片
	 */
	 public void LoadFaceData() {  
		 createPath("/sdcard/FaceData/");
	        File[] files = new File("/sdcard/bing/").listFiles();  
	        File f;  
	        String id;  
	        String name;  
	        String path;
	        FacePojo mPojo = new FacePojo();
	        faceList.clear();  
	        for (int i = 0; i < files.length; i++) {  
	            f = files[i];  
	            if (!f.canRead()) {  
	                return;  
	            }  
	            if (f.isFile()) {  
	                id = f.getName().split("_")[0];  
	                name = f.getName().substring(f.getName().indexOf("_") + 1,  
	                        f.getName().length() - 4);  
	                path=Environment.getExternalStorageDirectory()+"/bing/"+f.getName();
	                
	                if (!name.equals("bnj")) {
	                	 mPojo.id=id;
	 	                mPojo.name=name;
	 	                mPojo.path=Environment.getExternalStorageDirectory()+"/bing/"+f.getName();
	 	                Log.i(TAG, "名称:"+name);
	 	                Log.i(TAG, "路径:"+path);
	 	                faceList.add(mPojo);
					}	               
	                mPojo=null;
	                mPojo=new FacePojo();
//	                faceList.add(new FacePojo(id, name, Environment  
//	                        .getExternalStorageDirectory()  
//	                        + "/FaceData/"  
//	                        + f.getName()));  
	            }  
	        }  
	        mPojo=null;
	    }  
	 
	 /**
	  * 加载人脸图片
	  */
	 public void LoadFaceData_new() {  
	        File[] files = new File("/sdcard/FaceData/").listFiles();  
	        File f;  
	        String id;  
	        String name;  
	        String path;
	        FacePojo mPojo = new FacePojo();
	        faceList_new.clear();  
	        for (int i = 0; i < files.length; i++) {  
	            f = files[i];  
	            if (!f.canRead()) {  
	                return;  
	            }  
	            if (f.isFile()) {  
	                id = f.getName().split("_")[0];  
	                name = f.getName().substring(f.getName().indexOf("_") + 1,  
	                        f.getName().length() - 4);  
	                path=Environment.getExternalStorageDirectory()+"/FaceData/"+f.getName();
	                
	                if (!name.equals("bnj")) {
	                	mPojo.id=id;
	 	                mPojo.name=name;
	 	                mPojo.path=Environment.getExternalStorageDirectory()+"/FaceData/"+f.getName();
	 	                Log.i(TAG, "名称:"+name);
	 	                Log.i(TAG, "路径:"+path);
	 	                faceList_new.add(mPojo);
					}	               
	                mPojo=null;
	                mPojo=new FacePojo();
//	                faceList.add(new FacePojo(id, name, Environment  
//	                        .getExternalStorageDirectory()  
//	                        + "/FaceData/"  
//	                        + f.getName()));  
	            }  
	        }  
	        mPojo=null;
	    }  
	 
	
	 public void writePhoto(Bitmap bmp) {  
	        File file = new File("/sdcard/FaceDetect/faceDone.jpg");  
	        try {  
	            Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, 100, 100);  
	            BufferedOutputStream bos = new BufferedOutputStream(  
	                    new FileOutputStream(file));  
	            if (bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {  
	                bos.flush();  
	                bos.close();  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	 /**
	  * 创建目录
	  * @param path 创建路径
	  */
	 public static void createPath(String path) {
		    File file = new File(path);
		   if (!file.exists()) {
		       file.mkdir();
		    }
		}
	 
	 public void findfacebing() {  
			
			int picnum=faceList.size();
			
			/************遍历加载 ***********/
			for (int i = 0; i < picnum; i++) {
				
				if (faceList.get(i).getPath().equals("")) {
					 Mat image = Highgui.imread(faceList.get(i).getPath());  
				        MatOfRect faceDetections = new MatOfRect();  
				        mjavaClassifier.detectMultiScale(image, faceDetections);  
				        int k=0;
				        for (Rect rect : faceDetections.toArray()) {  
				            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x  
				                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));  

				            Mat sub = image.submat(rect);  
				            Mat mat = new Mat();  
				            Size size = new Size(100, 100);  
				            Imgproc.resize(sub, mat, size);  
				            boolean life= Highgui.imwrite(Environment.getExternalStorageDirectory()+"/FaceData/bing"+"_"+i
				            		+".jpg", mat);
				            Log.i(TAG, "mat:"+mat.toString());
				            Log.i(TAG, "保存:"+life);
				            k++;
				}
				
			        }  
			}
	    }  
	 
	 public static Bitmap readBitMap(Context context, int resId){  
         BitmapFactory.Options opt = new BitmapFactory.Options();  
         opt.inPreferredConfig = Bitmap.Config.RGB_565;   
         opt.inPurgeable = true;  
         opt.inInputShareable = true;  
         InputStream is = context.getResources().openRawResource(resId);  
         return BitmapFactory.decodeStream(is,null,opt);  
   }

	 
}
