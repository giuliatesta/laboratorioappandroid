package it.giuliatesta.udrive.dataProcessing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import it.giuliatesta.udrive.accelerometer.AccelerometerDataEventListener;
import it.giuliatesta.udrive.accelerometer.CoordinatesDataEvent;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static it.giuliatesta.udrive.accelerometer.CoordinatesDataEvent.lowPassFiltering;
import static it.giuliatesta.udrive.dataProcessing.DataProcessor.AnalyzeResult.PROCESSED;

/**
 Classe per la gestione degli eventi causati dalla modifica dei valori del sensore
 */

public class DataManager implements SensorEventListener {

    private Sensor accelerometer;
    private SensorManager sensorManager;
    private final Context context;
    private DataProcessor accelerometerDataProcessor;
    private ArrayList<CoordinatesDataEvent> coordinatesDataEventArrayList;
    private static DataManager dataManager = null;
    private final StorageListener storageListener;

    /**
        Costruttore singleton
     */
    private DataManager(Context context) {
        this.context = context;
        sensorSettings();
        storageListener = new StorageListener(context);
        this.registerListener(storageListener);
        storageListener.startWritingStorageFile();
    }

    /**
     * Metodo get che resistuisce il listener legato allo storage
     * @return  storageListener
     */
    public StorageListener getStorageListener() {
        return storageListener;
    }

    public Sensor getAccelerometer() {
        return accelerometer;
    }

    /**
     * Metodo per le impostazioni del sensore: scelgo il tipo di sensore, registro
     * il suo listener e credo un nuovo DataProcessor per manipolare i dati
     */
    private void sensorSettings() {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(TYPE_LINEAR_ACCELERATION);
        accelerometerDataProcessor = new DataProcessor();
        coordinatesDataEventArrayList = new ArrayList<>();

    }

    /**
        Metodo per restituire l'unica instanza di DataManager se è già stata creata una volta
        oppure la crea.
     */
    public static DataManager getInstance(Context context) {
        if(dataManager == null) {
            dataManager = new DataManager(context);
        }
        return dataManager;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         if(event.sensor.getType()==accelerometer.getType()) {       //Se gli eventi sono dell'accelerometro
            analyzeSensorEvent(event);
        }
    }

    private void analyzeSensorEvent(SensorEvent event) {
        float accelerometerValues[] = new float[3];
        accelerometerValues = lowPassFiltering(event.values.clone(), accelerometerValues);
        coordinatesDataEventArrayList.add(0, new CoordinatesDataEvent(accelerometerValues[0], accelerometerValues[1], accelerometerValues[2]));
        Log.d("DataManager", "onSensorChanged: x:" + (accelerometerValues[0]) + "\t y:" + (accelerometerValues[1]) + "\t z:" + (accelerometerValues[2]));
        DataProcessor.AnalyzeResult result = accelerometerDataProcessor.analyze(coordinatesDataEventArrayList);
        if (result == PROCESSED) {
            coordinatesDataEventArrayList.clear();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
        Metodo per registrare il listener
     */
    public void registerListener(AccelerometerDataEventListener accelerometerDataEventListener) {
        accelerometerDataProcessor.registerListener(accelerometerDataEventListener);
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

}
