gestures4vr
===================

Hand gestures for smartphone VR apps using the phone camera. For smartphone VR headsets like Google cardboard or Gear VR, use information about how many fingers in a hand are in front of the headset as input for your VR worlds. 

This project uses code from [hand_finger_recognition_android project](https://github.com/h3ct0r/hand_finger_recognition_android) to figure out the pose of the hand. This requires OpenCV. Download OpenCV4Android and install the package manager that comes with it (under /apk) or alternatively you can download it from the play store.  

The Android app gives a demo of how the recognition works. First step is to calibrate-- tap on the ImageView where you see your hand.  It then starts tracking your hand and updating the finger count on the TextView. 


## Using with Unity ##
For use with Unity, remove MainActivity.java and change `apply plugin: 'com.android.application'` in build.gradle for app to `apply plugin: 'com.android.library'`. Make project and Android Studio will generate  `.aar` packages for the gesture project and the opencv library. Unzip them and copy the two classes.jar files to Assets/Plugins/Android folder. You will also need the jumpchat sdk .jar file. Jumpchat sdk code is used for processing video frames in the background.

If you used OpenCV package manager from Play Store, it's probably got the 64-bit version of the openCV shared library object. But Unity builds 32-bit apks. So you'll have to install the 32-bit version manually (like armeabi-v7a) by installing the 32-bit package manager apk from OpenCV4Android package. 

To access the gesture recognition engine from Unity, use the static class VividVRPlugin that calls into the respective Java class.


----------
This project was built at [/hack](https://www.hackerearth.com/slash-hack/) for the Gear VR. We used the recognition engine in a demo where opening your hand pushed an object.


![Alt text](/demo.jpeg?raw=true "/hack)

