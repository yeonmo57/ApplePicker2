package todo.swu.applepicker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
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
    FirebaseFirestore db;

    private int WRITE_REQUEST_CODE = 43;
    private ParcelFileDescriptor pfd;
    private FileOutputStream fileOutputStream;
    private String resultData;
    Map<String, Object> dateTodayMap;
    String currentDate;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_edit);
        TextView saved_memo = (TextView)findViewById(R.id.saved_memo);
        saved_memo.setText("");
        db = FirebaseFirestore.getInstance();

        Intent receive_intent = getIntent();

        String jsonResponse = receive_intent.getStringExtra("jsonResponse");
        String imagePath = receive_intent.getStringExtra("imagePath");
        List<String> inferTextList = new ArrayList<String>();
        Log.e("test", "json 파싱 실행 전");
        // OCR 글자들 모음 List
        inferTextList = jsonParsing(jsonResponse);
        resultData = inferTextList.toString();
        
        // 대괄호 없애기
        resultData = removeChar(resultData, 0);
        resultData = removeChar(resultData, resultData.length()-1);

        // 쉼표 없애기
        resultData= resultData.replace(",", "");

        Log.e("test", "json 파싱 실행 후");
        Log.e(inferTextList.toString(), "inferTextList");
        Log.e(imagePath.toString(), "imagePath");

        //StartRecord();

        Map<String, Object> memoMap = new HashMap<>();
        memoMap.put("memo", resultData);

        Date now = new Date();
        String dateToday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
        currentDate = dateToday;


        //Create field
        dateTodayMap = new HashMap<>();
        dateTodayMap.put("date", dateToday);

        db.collection("daily")
                .document(dateToday)//dateToday 오늘 날짜가 있는 경우
                .get()
                .addOnSuccessListener(snapShotData -> {
                    if (snapShotData.exists()) {
                        //선택한 날짜에 저장된 데이터가 있는 경우 메모 데이터 저장
                        db.collection("/daily/"+dateToday+"/memoItem")
                                .add(memoMap)
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

                    } else {//선택한 날짜에 해당하는 데이터가 없는 경우
                        //새로 만들어서 DB에 추가함
//                        Log.e("선택한 날짜에 해당하는 데이터가 없는 경우", dateToday);
//                        db.collection("daily").document(dateToday)
//                                .set(dailyMap)
//                                .addOnSuccessListener(documentReference -> {
//                                    Log.e(TAG, "DocumentSnapshot added with ID: ");
//                                }).addOnFailureListener(e -> {
//                            Log.e(TAG, "Error adding document", e);
//                        });
                    }
                }).addOnFailureListener(e -> e.printStackTrace());


        db = FirebaseFirestore.getInstance();
        //isDataPassing > isDataPassing > flag == true 일 경우 프래그먼트에서 데이터 받아오기
        db.collection("isDataPassing")
                .document("isDataPassing") //선택한 날짜에 해당하는 데이터 유무 확인
                .get()
                .addOnSuccessListener(snapShotData -> {
                    if (snapShotData.exists()) {//선택한 날짜에 저장된 데이터가 있는 경우 해당 data 갖고와서 화면에 뿌려줌.
                        //Log.e("선택한 날짜에 저장된 데이터가 있는 경우 해당 data 갖고와서 화면에 뿌려줌.", dateToday);

                        String flag = (String) snapShotData.getData().get("flag");
                        Log.e("flag 값: ", flag);
                        try {
                            Thread.sleep(5000);
                            if(flag.equals("True")) {
                                Log.e("if문 안의 flag 값: ", flag);

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).addOnFailureListener(e -> e.printStackTrace());
        saved_memo.setText("메모 저장 완료!");
    }


    public String removeChar(String str, Integer n) {
        String front = str.substring(0, n);
        String back = str.substring(n+1, str.length());
        return front + back;
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void StartRecord(){
//        try {
//
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfNow
//                    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String formatDate = sdfNow.format(date);
//
//            /**
//             * SAF 파일 편집
//             * */
//            String fileName = formatDate+".txt";
//
//            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("text/plain" );
//            intent.putExtra(Intent.EXTRA_TITLE,fileName);
//
//            startActivityForResult(intent, WRITE_REQUEST_CODE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Uri uri = data.getData();
//            try {
//                addText(uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public void addText(Uri uri) throws IOException {
//        try {
//            String inAddTextStr = "TEST";
//            pfd = this.getContentResolver().openFileDescriptor(uri, "w");
//            fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
//            putString(resultData);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void writeFile(String fileName, String msg) {
//        try {
//            OutputStreamWriter oStreamWriter = new OutputStreamWriter(openFileOutput(fileName,
//                    Context.MODE_PRIVATE));
//            oStreamWriter.write(msg);
//            oStreamWriter.close();
//        } catch(FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 이 메서드를 통해 기록
//     */
//    public void putString(String st) throws IOException {
//        if(fileOutputStream!=null) fileOutputStream.write(st.getBytes());
//    }
//
//    public void FinishRecord() throws IOException {
//        Toast.makeText(getApplicationContext(), "내용 기록완료.", Toast.LENGTH_LONG).show();
//        fileOutputStream.close();
//        pfd.close();
//
//    }

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


}