package com.example.socialcompass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationService implements SensorEventListener {
    private static OrientationService instance;
    private final SensorManager sensorManager;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private MutableLiveData<Float> azimuth;

    protected OrientationService(Activity activity){
        this.azimuth = new MutableLiveData<>();
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.registerSensorListeners();
    }

    private void registerSensorListeners(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static OrientationService singleton(Activity activity){
        if(instance == null){
            instance = new OrientationService(activity);
        }
        return instance;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometerReading = event.values;
        }

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magnetometerReading = event.values;
        }
        if(accelerometerReading!=null && magnetometerReading!=null){
            onBothSensorDataAvailable();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void onBothSensorDataAvailable(){
        if(accelerometerReading == null || magnetometerReading== null){
            throw new IllegalArgumentException("Both sensors must be available yo compute orientation.");
        }

        float[] r = new float[9];
        float[] i = new float[9];

        boolean success = SensorManager.getRotationMatrix(r, i, accelerometerReading, magnetometerReading);

        if (success){
            float[] orientation = new float[3];
            SensorManager.getOrientation(r, orientation);

            this.azimuth.postValue(orientation[0]);
        }
    }

    public void unregisterSensorListeners(){
        sensorManager.unregisterListener(this);
    }

    public LiveData<Float> getOrientation(){
        return this.azimuth;
    }

    public void setMockOrientationSource(MutableLiveData<Float> mockDataSource){
        unregisterSensorListeners();
        this.azimuth = mockDataSource;
    }

    protected void onPause(){
        this.unregisterSensorListeners();
    }

    public void setMockOrientationData(MutableLiveData<Float> mockData) {
        unregisterSensorListeners();
        this.azimuth = mockData;
    }


}
