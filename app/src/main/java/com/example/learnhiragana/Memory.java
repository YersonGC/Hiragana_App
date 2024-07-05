package com.example.learnhiragana;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.learnhiragana.R;
import com.example.learnhiragana.adapters.PalabrasAdapter;

import java.util.ArrayList;

public class Memory extends AppCompatActivity {
    String[] hiragana = {
            "あい (ai) - amor",
            "いぬ (inu) - perro",
            "ねこ (neko) - gato",
            "さる (saru) - mono",
            "たま (tama) - bola",
            "はな (hana) - flor o nariz",
            "みず (mizu) - agua",
            "き (ki) - árbol",
            "やま (yama) - montaña",
            "かわ (kawa) - río",
            "そら (sora) - cielo",
            "うみ (umi) - mar",
            "あめ (ame) - lluvia",
            "つき (tsuki) - luna",
            "ひ (hi) - fuego o día",
            "ゆき (yuki) - nieve",
            "ひと (hito) - persona",
            "くるま (kuruma) - coche",
            "ひかり (hikari) - luz",
            "ほし (hoshi) - estrella"
    };
    ArrayList<String> hiraganaWords = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memory);


        RecyclerView resview = findViewById(R.id.recy_01);// Obtener la referencia del ListView+}
        resview.setLayoutManager(new LinearLayoutManager(this));
        for(int i = 0; i < hiragana.length; i++){
            this.hiraganaWords.add(hiragana[i]);
        }
        PalabrasAdapter adapter = new PalabrasAdapter(hiraganaWords, this);
        resview.setAdapter(adapter); // Ahora es compatible




    }
}