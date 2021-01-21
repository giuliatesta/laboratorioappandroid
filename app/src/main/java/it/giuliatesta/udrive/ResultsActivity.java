package it.giuliatesta.udrive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import it.giuliatesta.udrive.accelerometer.AccelerometerDataEvent;

import static android.graphics.Color.WHITE;

/**
 * Classe per la terza Activity --> da usare quando si termina la guida
 */
public class ResultsActivity extends AppCompatActivity {
    private ArrayList<AccelerometerDataEvent> accelerometerDataEventArrayList;
    private ArrayList<Integer> percentageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Impostazioni per le percentuali
        Intent intent = getIntent();
        accelerometerDataEventArrayList = (ArrayList<AccelerometerDataEvent>) intent.getSerializableExtra("percentageList");
        percentageList = extractPercentageValues(accelerometerDataEventArrayList);
        textViewSettings();

        // Impostazioni del listener
        listenerSettings();

        //Impostazioni dell'immagine
        imageSettings();
    }

    private ArrayList<Integer> extractPercentageValues(ArrayList<AccelerometerDataEvent> accelerometerDataEventArrayList) {
        ArrayList<Integer> percentages = new ArrayList<>();
        for(AccelerometerDataEvent event : accelerometerDataEventArrayList) {
            switch (event.getType()) {
                case VERTICAL_MOTION_EVENT:
                    percentages.add(0, event.getVerticalMotionPercentage());
                    break;
                case DIRECTION_EVENT:
                    percentages.add(0, event.getDirectionPercentage());
                    break;
                case BOTH:
                    percentages.add(0, event.getDirectionPercentage());
                    percentages.add(0, event.getVerticalMotionPercentage());
                    break;
            }
        }
        return percentages;
    }

    /**
     * Impostazioni per il textView: associa il textView e imposta il messaggio da mostrare
     */
    private void textViewSettings() {
        TextView textResult = findViewById(R.id.txt_results);
        int meanValue = getMeanValue(percentageList);
        String message = "Punteggio: " + meanValue + "/100";
        textResult.setText(message);
    }

    /**
     * Calcola la media tra i valori della lista
     * @param percentageList lista di percentuali
     * @return media delle percentuali = voto finale
     */
    private int getMeanValue(ArrayList<Integer> percentageList) {
        int mean = 0;
        for(int percentage: percentageList) {
            mean += percentage;
        }
        return (int) mean/percentageList.size();
    }

    /**
     * Metodo per le impostazioni dell'immagine: associo l'immagine e le applico un filtro colore
     *
     */
    private void imageSettings() {
        ImageView img_thumbs_up = findViewById(R.id.img_thumbs_up);
        img_thumbs_up.setColorFilter(WHITE);
    }

    /**
     * Metodo per le impostazioni dell listener: associo il bottone e registro il listener
     */
    private void listenerSettings() {
        Button btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(v);
            }
        });
    }

    /**
     *  Metodo per cambiare activity e tornare alla Main
     */
    public void changeActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

