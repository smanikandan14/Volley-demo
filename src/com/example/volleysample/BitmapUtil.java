package com.example.volleysample;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapUtil {

	public static Bitmap downSizeBitmap(Bitmap bitmap,int reqSize)  {
		
		int width = bitmap.getWidth();
		 
		int height = bitmap.getHeight();
		 
		float scaleWidth = ((float) reqSize) / width;
		 
		float scaleHeight = ((float) reqSize) / height;
		 
		// CREATE A MATRIX FOR THE MANIPULATION
		 
		Matrix matrix = new Matrix();
		 
		// RESIZE THE BIT MAP
		 
		matrix.postScale(scaleWidth, scaleHeight);
		 
		// RECREATE THE NEW BITMAP
		 
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
		 
		return resizedBitmap;
		 
		/*if(bitmap.getWidth() < reqSize) {
			return bitmap;
		} else {
			return Bitmap.createScaledBitmap(bitmap, reqSize, reqSize, false);
		} */
	}
}
