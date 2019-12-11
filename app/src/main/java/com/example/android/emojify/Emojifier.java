/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.Locale;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    private static final float THRESHOLD_SMILING = 0.5f;
    private static final float THRESHOLD_EYE_OPEN = 0.5f;

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static void detectFaces(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);
                Emoji faceEmoji = whichEmoji(face);
            }

        }


        // Release the detector
        detector.release();
    }


    /**
     * Method for logging the classification probabilities.
     *
     * @param face The face to get the classification probabilities.
     */
    private static Emoji whichEmoji(Face face){
        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        boolean isSmiling = face.getIsSmilingProbability() > THRESHOLD_SMILING;
        boolean isLeftEyeOpen = face.getIsLeftEyeOpenProbability() > THRESHOLD_EYE_OPEN;
        boolean isRightEyeOpen = face.getIsRightEyeOpenProbability() > THRESHOLD_EYE_OPEN;

        // Determine and log the appropriate emoji
        Emoji faceEmoji;
        if (isSmiling) {
            if (isLeftEyeOpen && !isRightEyeOpen) {
                faceEmoji = Emoji.LEFT_WINK;
            } else if (!isLeftEyeOpen && isRightEyeOpen) {
                faceEmoji = Emoji.RIGHT_WINK;
            } else if (isLeftEyeOpen && isRightEyeOpen) {
                faceEmoji = Emoji.SMILING;
            } else {
                faceEmoji = Emoji.CLOSED_EYE_SMILING;
            }
        } else {
            if (isLeftEyeOpen && !isRightEyeOpen) {
                faceEmoji = Emoji.LEFT_WINK_FROWNING;
            } else if (!isLeftEyeOpen && isRightEyeOpen) {
                faceEmoji = Emoji.RIGHT_WINK_FROWNING;
            } else if (isLeftEyeOpen && isRightEyeOpen) {
                faceEmoji = Emoji.FROWNING;
            } else {
                faceEmoji = Emoji.CLOSED_EYE_FROWNING;
            }
        }

        // Log the classification probabilities for each face.
        Log.d(LOG_TAG, String.format(
                Locale.getDefault(),
                "Face=%s maps to Emoji=%s",
                face, faceEmoji.name()));
        return faceEmoji;
    }


    enum Emoji {
        SMILING,
        RIGHT_WINK,
        LEFT_WINK,

        FROWNING,
        LEFT_WINK_FROWNING,
        RIGHT_WINK_FROWNING,

        CLOSED_EYE_SMILING,
        CLOSED_EYE_FROWNING,
    }
}
