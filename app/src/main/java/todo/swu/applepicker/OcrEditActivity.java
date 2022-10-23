package todo.swu.applepicker;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButtonToggleGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;


public class OcrEditActivity extends AppCompatActivity {
    //Access a Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Fragment fragmentDaily;
    Fragment fragmentSNS;
    Fragment fragmentOCR;
    private FragmentManager fragmentManager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_edit);

        Intent receive_intent = getIntent();

        String jsonResponse = receive_intent.getStringExtra("jsonResponse");
        String imagePath = receive_intent.getStringExtra("imagePath");
        List<String> inferTextList = new ArrayList<String>();
        Log.e("test", "json 파싱 실행 전");
        // OCR 글자들 모음 List
        inferTextList = jsonParsing(jsonResponse);
        Log.e("test", "json 파싱 실행 후");
        Log.e(imagePath.toString(), "imagePath");
        Log.e(inferTextList.toString(), "inferTextList");

        // OcrEditActivity, 데이터 보낼 때 flag true로 바꾸고 데이터 전달
        HashMap<Object, Object> dataPassingMap = new HashMap<>();
        dataPassingMap.put("flag", "True");

        db.collection("isDataPassing")
            .document("isDataPassing") //선택한 날짜에 해당하는 데이터 유무 확인
            .get()
            .addOnSuccessListener(snapShotData -> {
                if (snapShotData.exists()) {//저장된 데이터가 있는 경우
                    //Log.e("선택한 날짜에 저장된 데이터가 있는 경우 해당 data 갖고와서 화면에 뿌려줌.", dateToday);
                    //String flag = (String) snapShotData.getData().get("isDataPassing");
                    db.collection("isDataPassing").document("isDataPassing")
                            .update("flag", "True")
                            .addOnSuccessListener(documentReference -> {
                                Log.e(TAG, "DocumentSnapshot updated with ID: ");
                            }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating document", e);
                    });

                } else {
                    //해당하는 데이터가 없는 경우
                    //새로 만들어서 DB에 추가함
                    //Log.e("선택한 날짜에 해당하는 데이터가 없는 경우", dateToday);
                    db.collection("isDataPassing").document("isDataPassing")
                            .set(dataPassingMap)
                            .addOnSuccessListener(documentReference -> {
                                Log.e(TAG, "DocumentSnapshot added with ID: ");
                            }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding document", e);
                    });
                }
            }).addOnFailureListener(e -> e.printStackTrace());

        Intent intent = new Intent(OcrEditActivity.this, MainActivity.class);

        // mainActivity로 json 응답 데이터 전달하기
        intent.putExtra("jsonResponse", inferTextList.toString());

        startActivity(intent);

        //fragment 생성
//        FragmentDaily fragmentDaily = new FragmentDaily();
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction ft = manager.beginTransaction();;
        //one.setOnClickListener(this);

        //OcrEditActivity에 FragmentDaily를 띄워줌
//        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragmentDaily);
//        ft.addToBackStack(null);
//        ft.commit();
//

//        //번들객체 생성, text값 저장
//        Bundle bundle = new Bundle();
//        bundle.putString("inferTextListToDaily",inferTextList.toString());
//
//        //FragmentDaily로 번들 전달
//        fragmentDaily.setArguments(bundle);
    }

    public String removeChar(String str, Integer n) {
        String front = str.substring(0, n);
        String back = str.substring(n+1, str.length());
        return front + back;
    }

    private List jsonParsing(String json)
    {
        List<String> inferTextList = new ArrayList<String>();
        Log.e("in jsonParsing Test", "1");
        try{
            JSONObject imagesJsonObject = new JSONObject(json);
            JSONArray imagesArray = imagesJsonObject.getJSONArray("images");

            String imagesJson = imagesArray.toString();
            imagesJson = removeChar(imagesJson, 0);
            int len = imagesJson.length()-1;
            imagesJson = removeChar(imagesJson, len);

            JSONObject fieldsJsonObject = new JSONObject(imagesJson);
            JSONArray fieldsArray = fieldsJsonObject.getJSONArray("fields");

            for(int i=0; i<fieldsArray.length(); i++)
            {
                JSONObject fieldsObject = fieldsArray.getJSONObject(i);
                String inferText = fieldsObject.getString("inferText");

                if(inferText.length()>=2)
                    inferTextList.add(fieldsObject.getString("inferText"));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("in jsonParsing Test", "2");
        return inferTextList;
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