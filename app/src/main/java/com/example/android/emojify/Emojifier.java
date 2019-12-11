package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.Locale;

public class Emojifier {

    public static final String TAG = Emojifier.class.getSimpleName();

    public static void detectFaces(Context context, Bitmap bitmap) {
        // Create the face detector, disable tracking and enable classifications
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build frame to detection
        Frame frame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        // Detect faces on frame
        SparseArray<Face> faces = faceDetector.detect(frame);

        // Log detected faces
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);
            Log.i(TAG, String.format(
                    Locale.getDefault(),
                    "Detected face at position: %s",
                    face.getPosition().toString()));
        }

        // Release the detector
        faceDetector.release();
    }
}
