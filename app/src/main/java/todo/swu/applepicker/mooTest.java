/*
package todo.swu.applepicker;

import java.io.File;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final TextView textView = (TextView) findViewById(R.id.textView);
        final TextView readData = (TextView) findViewById(R.id.readData);
        final TextView ocrData = (TextView) findViewById(R.id.ocrView);


        Button saveBtn = (Button) findViewById(R.id.saveBtn) ;
        Button readBtn = (Button) findViewById(R.id.readBtn) ;
        Button ocrBtn = (Button) findViewById(R.id.ocrBtn) ;

        // 특정 데이터를 저장
        File[] fileImg = getExternalFilesDirs("/storage/emulated/0/Pictures/ocr_test01.png");
        String imgPath = fileImg[0].getPath();
        // dirPath.setText(imgPath);
        ///storage/emulated/0/Pictures/ocr_test01.png

        readBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                readData.setText(document.getId()+"=>"+document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
            }});

        saveBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Red");

                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("first", "kim");
                user.put("last", "hyerin");
                user.put("born", 1999);

                // Add a new document with a generated ID
                db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                    }

                });
            }
        });

        //String apiURL = "https://nxie8hhe8q.apigw.ntruss.com/custom/v1/15052/d9ca1275cb57d960372ef532fa2ff936a1f9e789c8af3e55617a0bd60c605d31/general";
        //String secretKey = "UUNjZ1p4ZXRMRk1VZlpramFkY1lwSFBrck5DS3hFdWw=";
        //String imageFile = "C:\\Users\\USER\\Desktop\\ocr_test01.png";

    }


}*/