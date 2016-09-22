package com.wenruisong.basestationmap.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.view.animation.AccelerateInterpolator;

import com.wenruisong.basestationmap.BasestationMapApplication;

/**
 * Created by wen on 2016/1/16.
 */
public class DirectionHelper {
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private float mLastDirection;
    private float mTargetDirection;
    private MySensorEventListener mSensorListener;
    public float mCurrentDirection;
    private final float MAX_ROATE_DEGREE = 0.1f;
    private AccelerateInterpolator mInterpolator;
    private OnDirectionChanged onDirectionChangedListener;
    private static DirectionHelper instance;
    public static  DirectionHelper getInstance()
    {
        if (instance==null)
            instance=new DirectionHelper();
        return instance;
    }
    DirectionHelper()
    {
        mCurrentDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mSensorListener = new MySensorEventListener();
        // sensor manager
        mSensorManager=(SensorManager)  BasestationMapApplication.getContext().getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        // location manager
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        if(mSensorManager.registerListener(mSensorListener, mOrientationSensor, SensorManager.SENSOR_DELAY_FASTEST)){
            //buildUsageStats(UsageStatsUtils.COMPASS_DIRECTION);
        }
    }
    public void setDirectionChangedListener(OnDirectionChanged directionChangedListener)
    { onDirectionChangedListener = directionChangedListener;}
    private float normalizeDegree(float degree) {
        return (degree + 360) % 360;
    }

    private class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_ORIENTATION) {
                float direction = event.values[0];
                mTargetDirection = normalizeDegree(direction);
            }


                // calculate the short routine
                float to = mTargetDirection;
                if (to - mCurrentDirection > 180) {
                    to -= 360;
                } else if (to - mCurrentDirection < -180) {
                    to += 360;
                }

                //限制最大步进
                float distance = to - mCurrentDirection;
                if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                    distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                }

                //调节步进
                mCurrentDirection = normalizeDegree(mCurrentDirection
                        + ((to - mCurrentDirection) * mInterpolator.getInterpolation(Math
                        .abs(distance) >= MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
                if(Math.abs(mLastDirection - mCurrentDirection) > 0.05){
                    mLastDirection = mCurrentDirection;
                }
            if(onDirectionChangedListener!=null)
            onDirectionChangedListener.onGetNewDirection(mCurrentDirection);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

   public interface OnDirectionChanged{
       void onGetNewDirection(float direction);
   }
}
