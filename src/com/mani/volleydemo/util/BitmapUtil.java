package com.mani.volleydemo.util;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;

public class BitmapUtil {

	public static Bitmap downSizeBitmap(Bitmap bitmap,int reqSize)  {
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		 
		float scaleWidth = ((float) reqSize) / width;
		float scaleHeight = ((float) reqSize) / height;
		 
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		 
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
		return resizedBitmap;
		 
		/*if(bitmap.getWidth() < reqSize) {
			return bitmap;
		} else {
			return Bitmap.createScaledBitmap(bitmap, reqSize, reqSize, false);
		} */
	}
	
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
	        bitmap.copyPixelsToBuffer(buffer);
	        return buffer.array();
      } else {
    	  	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	  	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	        byte[] data = baos.toByteArray();
	        return data;
      }
    }

}
