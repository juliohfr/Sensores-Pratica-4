package com.example.sensores;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private float lightValue;
    private float proximityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager    = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor      = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor  = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(this.proximitySensor != null){
            sensorManager.registerListener(this, this.proximitySensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if(this.lightSensor != null){
            sensorManager.registerListener(this, this.lightSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new LanternaHelper(this).desligar();
        new MotorHelper(this).pararVibracao();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent it) {
        if(it == null){
            return;
        }else if(requestCode == 10 && resultCode == 1){
            final String proximityLevel             = it.getStringExtra("proximityValue");
            final String lightLevel                 = it.getStringExtra("lightValue");
            final SwitchMaterial lanternSwitch      = this.findViewById(R.id.material_switch_vibration);
            final SwitchMaterial vibrationSwitch    = this.findViewById(R.id.material_switch_lantern);
            final LanternaHelper lanternHelper      = new LanternaHelper(this);
            final MotorHelper motorHelper           = new MotorHelper(this);


            if(proximityLevel != null){
                if(proximityLevel.equals("farDistance")) {
                    motorHelper.iniciarVibracao();
                    vibrationSwitch.setChecked(true);
                }else{
                    motorHelper.pararVibracao();
                    vibrationSwitch.setChecked(false);
                }
            }

            if(lightLevel != null){
                if(lightLevel.equals("lowLight")) {
                    lanternHelper.ligar();
                    lanternSwitch.setChecked(true);
                }else{
                    lanternHelper.desligar();
                    lanternSwitch.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            this.proximityValue = event.values[0];
        } else if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            this.lightValue = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //...
    }

    public void classificateReads(View view) {
        Intent it = new Intent("ACTION_CLASSIFICATE");
        it.putExtra("lightValue", lightValue);
        it.putExtra("proximityValue", proximityValue);

        startActivityForResult(it, 10);
    }
}