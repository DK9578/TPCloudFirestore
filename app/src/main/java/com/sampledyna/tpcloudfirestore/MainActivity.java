package com.sampledyna.tpcloudfirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**Attribut TAG en autocomplétion avec logt**/

    private static  final String TAG ="MainActivity";

    /**Variables Globales des clés de notre bases**/

    private static final String KEY_TITRE= "titre";
    private static final String KEY_NOTE = "note";

    /** Attributs globaux**/

    private EditText et_titre, et_note;
    private TextView tv_saveNote, tv_showNote;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference noteRef = db.document("listeDeNotes/Ma première note");

    public void initUI(){
        et_titre = (EditText) findViewById(R.id.et_titre);
        et_note = (EditText) findViewById(R.id.et_note);
        tv_saveNote = (TextView) findViewById(R.id.tv_saveNote);
        tv_showNote = (TextView) findViewById(R.id.tv_showNote);
    }



    public void saveNote(View view){
        String titre = et_titre.getText().toString();
        String note = et_note.getText().toString();


        Map<String, Object> contenuNote = new HashMap<>();
        contenuNote.put(KEY_TITRE, titre);
        contenuNote.put(KEY_NOTE, note);

            noteRef.set(contenuNote)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "Note entregistrée", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Erreur lors de l'envoi !", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
    }


    public void showNote(View view){
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists()){
                            String titre = documentSnapshot.getString(KEY_TITRE);
                            String note = documentSnapshot.getString(KEY_NOTE);

                            tv_saveNote.setText("Titre de la note " + titre + "\n" + "Note : " + note);
                        }else{
                            Toast.makeText(MainActivity.this, "Le document n'existe pas !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Erreur de lecture", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    protected void onStart(){
        super.onStart();

        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /** On vérifie qu'il  n'y ai pas d'erreur **/

                if (error != null){
                    Toast.makeText(MainActivity.this, "Erreur au chargement !", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, error.toString());
                    return; // pour quitter la méthode s'il y a une erreur
                }

                if (value.exists()) {
                    String titre = value.getString(KEY_TITRE);
                    String note = value.getString(KEY_NOTE);
                    tv_showNote.setText("Titre de la note : " +  titre + "\n" +  "Note : " + note );
                    //partie ajoutée pour ne pas afficher null dans le champ
                }
                else {
                    tv_showNote.setText("");
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }
}