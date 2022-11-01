package todo.swu.applepicker;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButtonToggleGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

// 파일 입출력 관련
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class OcrEditActivity extends AppCompatActivity {
    //Access a Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Fragment fragmentDaily;
    Fragment fragmentSNS;
    Fragment fragmentOCR;
    private FragmentManager fragmentManager;

    private int WRITE_REQUEST_CODE = 43;
    private ParcelFileDescriptor pfd;
    private FileOutputStream fileOutputStream;
    private String resultData;

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
        resultData = inferTextList.toString();
        Log.e("test", "json 파싱 실행 후");
        Log.e(inferTextList.toString(), "inferTextList");
        Log.e(imagePath.toString(), "imagePath");


        //writeFile("testFile.txt", "testtttttttt");

        StartRecord();
        Log.e(inferTextList.toString(), "\nwriteFile 수행 후");

        // OcrEditActivity, 데이터 보낼 때 flag true로 바꾸고 데이터 전달
//        HashMap<Object, Object> dataPassingMap = new HashMap<>();
//        dataPassingMap.put("flag", "True");
//
//        db.collection("isDataPassing")
//            .document("isDataPassing") //선택한 날짜에 해당하는 데이터 유무 확인
//            .get()
//            .addOnSuccessListener(snapShotData -> {
//                if (snapShotData.exists()) {//저장된 데이터가 있는 경우
//                    //Log.e("선택한 날짜에 저장된 데이터가 있는 경우 해당 data 갖고와서 화면에 뿌려줌.", dateToday);
//                    //String flag = (String) snapShotData.getData().get("isDataPassing");
//                    db.collection("isDataPassing").document("isDataPassing")
//                            .update("flag", "True")
//                            .addOnSuccessListener(documentReference -> {
//                                Log.e(TAG, "DocumentSnapshot updated with ID: ");
//                            }).addOnFailureListener(e -> {
//                        Log.e(TAG, "Error updating document", e);
//                    });
//
//                } else {
//                    //해당하는 데이터가 없는 경우
//                    //새로 만들어서 DB에 추가함
//                    //Log.e("선택한 날짜에 해당하는 데이터가 없는 경우", dateToday);
//                    db.collection("isDataPassing").document("isDataPassing")
//                            .set(dataPassingMap)
//                            .addOnSuccessListener(documentReference -> {
//                                Log.e(TAG, "DocumentSnapshot added with ID: ");
//                            }).addOnFailureListener(e -> {
//                        Log.e(TAG, "Error adding document", e);
//                    });
//                }
//            }).addOnFailureListener(e -> e.printStackTrace());
//
//        Intent intent = new Intent(OcrEditActivity.this, MainActivity.class);
//
//        // mainActivity로 json 응답 데이터 전달하기
//        intent.putExtra("jsonResponse", inferTextList.toString());
//
//        startActivity(intent);

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void StartRecord(){
        try {

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow
                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatDate = sdfNow.format(date);

            /**
             * SAF 파일 편집
             * */
            String fileName = formatDate+".txt";

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain" );
            intent.putExtra(Intent.EXTRA_TITLE,fileName);

            startActivityForResult(intent, WRITE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                addText(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addText(Uri uri) throws IOException {
        try {
            String inAddTextStr = "TEST";
            pfd = this.getContentResolver().openFileDescriptor(uri, "w");
            fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            putString(resultData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeFile(String fileName, String msg) {
        try {
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(openFileOutput(fileName,
                    Context.MODE_PRIVATE));
            oStreamWriter.write(msg);
            oStreamWriter.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 이 메서드를 통해 기록
     */
    public void putString(String st) throws IOException {
        if(fileOutputStream!=null) fileOutputStream.write(st.getBytes());
    }

    public void FinishRecord() throws IOException {
        Toast.makeText(getApplicationContext(), "내용 기록완료.", Toast.LENGTH_LONG).show();
        fileOutputStream.close();
        pfd.close();

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