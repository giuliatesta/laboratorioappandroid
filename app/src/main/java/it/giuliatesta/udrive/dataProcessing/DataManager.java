package it.giuliatesta.udrive.dataProcessing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

import it.giuliatesta.udrive.accelerometer.AccelerometerDataEventListener;
import it.giuliatesta.udrive.accelerometer.CoordinatesDataEvent;
import it.giuliatesta.udrive.dataProcessing.DataProcessor.AnalyzeResult;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
import static it.giuliatesta.udrive.dataProcessing.DataProcessor.AnalyzeResult.PROCESSED;

/**
 Classe per la gestione degli eventi causati dalla modifica dei valori del sensore
 */

public class DataManager implements SensorEventListener {

    private Sensor accelerometer;
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
        storageListener = new StorageListener(context);
        sensorSettings();
        this.registerListener(storageListener);
    }

    /**
     * Metodo get che resistuisce il listener legato allo storage
     * @return  storageListener
     */
    public StorageListener getStorageListener() {
        return storageListener;
    }

    /**
     * Metodo per le impostazioni del sensore: scelgo il tipo di sensore, registro
     * il suo listener e credo un nuovo DataProcessor per manipolare i dati
     */
    private void sensorSettings() {
        SensorManager manager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometer, SENSOR_DELAY_NORMAL);
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
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            coordinatesDataEventArrayList.add(0, new CoordinatesDataEvent(x, y, z));
            AnalyzeResult result = accelerometerDataProcessor.analyze(coordinatesDataEventArrayList);
            if (result == PROCESSED) {
                coordinatesDataEventArrayList.clear();
            }
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
}
