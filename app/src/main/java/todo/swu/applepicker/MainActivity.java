package todo.swu.applepicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Fragment fragmentDaily;
    Fragment fragmentSNS;
    Fragment fragmentOCR;
    TextView tv_title;

    FirebaseFirestore db;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.e("mainActivity", "test1");
        db = FirebaseFirestore.getInstance();
        //isDataPassing > isDataPassing > flag == true 일 경우 프래그먼트에서 데이터 받아오기
//        db.collection("isDataPassing")
//                .document("isDataPassing") //선택한 날짜에 해당하는 데이터 유무 확인
//                .get()
//                .addOnSuccessListener(snapShotData -> {
//                    if (snapShotData.exists()) {//선택한 날짜에 저장된 데이터가 있는 경우 해당 data 갖고와서 화면에 뿌려줌.
//                        //Log.e("선택한 날짜에 저장된 데이터가 있는 경우 해당 data 갖고와서 화면에 뿌려줌.", dateToday);
//
//                        String flag = (String) snapShotData.getData().get("flag");
//                        Log.e("flag 값: ", flag);
//                        try {
//                            Thread.sleep(5000);
//                            if(flag.equals("True")) {
//                                Log.e("if문 안의 flag 값: ", flag);
//                                Intent receive_intent = getIntent();
//                                String jsonResponse = receive_intent.getStringExtra("jsonResponse");
//                                Log.e("메인 액티비티", "if문 안");
//                                Log.e("전달 데이터", jsonResponse);
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).addOnFailureListener(e -> e.printStackTrace());


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        Log.e(this.getClass().getName(), "mainActivity 실행");
        //Log.e("0:", "test mainActivity2");

        //Log.e("mainActivity", "test mainActivity3");
        tv_title = (TextView)findViewById(R.id.tv_title);
        fragmentDaily = new FragmentDaily();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragmentDaily, "fragment_daily").commit();
        // 초기화면 설정

        //Log.e("0:", "test mainActivity4");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.tab_daily:
                            onMoveDaily();
                            tv_title.setText("Daily");
                            return true;

                        case R.id.tab_sns:
                            onMoveSNS();
                            tv_title.setText("SNS");
                            return true;

                        case R.id.tab_ocr:
                            onMoveOCR();
                            tv_title.setText("OCR");
                            return true;
                    }
                    return false;
                });
    }

    private void onMoveDaily(){
        if(fragmentDaily == null){
            fragmentDaily = new FragmentDaily();
            fragmentManager.beginTransaction().add(R.id.container, fragmentDaily, "fragment_daily").commit();
        }
        if(fragmentDaily != null)fragmentManager.beginTransaction().show(fragmentDaily).commit();
        if(fragmentSNS != null)fragmentManager.beginTransaction().hide(fragmentSNS).commit();
        if(fragmentOCR != null)fragmentManager.beginTransaction().hide(fragmentOCR).commit();
    }

    private void onMoveSNS(){
        if(fragmentSNS == null){
            fragmentSNS = new FragmentSNS();
            fragmentManager.beginTransaction().add(R.id.container, fragmentSNS).commit();
        }
        if(fragmentDaily != null)fragmentManager.beginTransaction().hide(fragmentDaily).commit();
        if(fragmentSNS != null)fragmentManager.beginTransaction().show(fragmentSNS).commit();
        if(fragmentOCR != null)fragmentManager.beginTransaction().hide(fragmentOCR).commit();
    }

    private void onMoveOCR(){
        if(fragmentOCR == null){
            fragmentOCR = new FragmentOCR();
            fragmentManager.beginTransaction().add(R.id.container, fragmentOCR).commit();
        }
        if(fragmentDaily != null)fragmentManager.beginTransaction().hide(fragmentDaily).commit();
        if(fragmentSNS != null)fragmentManager.beginTransaction().hide(fragmentSNS).commit();
        if(fragmentOCR != null)fragmentManager.beginTransaction().show(fragmentOCR).commit();
    }
}
