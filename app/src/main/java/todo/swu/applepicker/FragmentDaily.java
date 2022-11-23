package todo.swu.applepicker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class FragmentDaily extends Fragment {
    ImageButton iButton_calendar;
    TextView tv_date;
    EditText edit_memo;

    ImageButton iButton_memo_add;

    FirebaseFirestore db;
    Map<String, Object> dailyMap;

    static String currentDate;

    RecyclerView memoRecyclerView;

    ArrayList<MemoItem> memoItemList;

    MemoAdapter memoAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_daily, container, false);

        //이 버튼을 클릭하면 달력이 호출됨.
        iButton_calendar = (ImageButton) myView.findViewById(R.id.iButton_calendar);
        edit_memo = (EditText) myView.findViewById(R.id.edit_memo);
        tv_date = (TextView) myView.findViewById(R.id.tv_date);

        iButton_memo_add = (ImageButton) myView.findViewById(R.id.iButton_memo_add);

        //Access a Firestore
        db = FirebaseFirestore.getInstance();

        //Memo RecyclerView에 표시할 데이터 리스트 생성.
        memoItemList = new ArrayList<MemoItem>();

        //RecyclerView에 LinearLayoutManager 객체 지정, 어댑터 연결.
        RecyclerView memoRecyclerView = (RecyclerView) myView.findViewById(R.id.recyclerView_memo);
        memoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        memoAdapter = new MemoAdapter(memoItemList);
        memoRecyclerView.setAdapter(memoAdapter);

        //오늘날짜에 해당하는 데이터 불러와 화면에 보여줌.
        initFragment();

        //오늘날짜에 해당하는 OCR 응답 데이터 화면에 보여줌.
        db.collection("daily/"+currentDate+"/memoItem")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                String resultStr = "";
                                Log.e(TAG, document.getId() + " => " + document.getData());
                                // get data 예시
                                edit_memo.setText(null);

                                resultStr = document.getData().toString();
                                resultStr = resultStr.replace("{memo=","");
                                resultStr = resultStr.replace("}","");
                                //Log.e("resultStr[0]", resultStr.indexOf());

                                memoItemList.add(new MemoItem(resultStr));
                                memoAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

        iButton_calendar.setOnClickListener(v -> {
            DialogFragment dateFragment = new DatePickerFragment();
            dateFragment.show(getActivity().getSupportFragmentManager(), "dateFragment");
        });

        edit_memo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                edit_memo.setBackground(null);
            }
        });

        iButton_memo_add.setOnClickListener(v -> {

            memoItemList.add(new MemoItem(""));
            memoAdapter.notifyDataSetChanged();

            edit_memo.setText(null);
        });

        return myView;

    } //onCreateView End.

    public String removeChar(String str, Integer n) {
        String front = str.substring(0, n);
        String back = str.substring(n+1, str.length());
        return front + back;
    }

    //오늘날짜에 해당하는 데이터 불러와 화면에 보여줌.
    @SuppressLint("LongLogTag")
    public void initFragment() {
        //오늘 날짜 얻고 date 표시함.

        tv_date.setText(getCurrentDate());
        Date now = new Date();
        String dateToday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now);
        currentDate = dateToday;
        Log.e(" onCreateView 안의 currentDate 변수", currentDate);

        //Create field
        dailyMap = new HashMap<>();
        dailyMap.put("date", dateToday);


        db.collection("daily")
                .document(dateToday)//선택한 날짜에 해당하는 데이터 유무 확인
                .get()
                .addOnSuccessListener(snapShotData -> {

                    Log.e("선택한 날짜에 해당하는 데이터가 없는 경우", dateToday);
                    db.collection("daily").document(dateToday)
                            .set(dailyMap)
                            .addOnSuccessListener(documentReference -> {
                                Log.e(TAG, "DocumentSnapshot added with ID: ");
                            }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding document", e);
                    });

                }).addOnFailureListener(e -> e.printStackTrace());
    }




    @SuppressLint("LongLogTag")
    public void processDatePickerResult(String year, String month, String day, String day_of_week, String datePicked) {
        tv_date.setText(month + "/" + day + "(" + day_of_week + ")");
        currentDate = datePicked;

        //Create field
        dailyMap = new HashMap<>();
        dailyMap.put("date", datePicked);


    }


    //오늘 날짜 얻기.
    public String getCurrentDate() {
        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int dayNum = now.get(Calendar.DAY_OF_WEEK);
        //요일 구함
        String day_of_week = "";
        switch (dayNum) {
            case 1:
                day_of_week = "일";
                break;
            case 2:
                day_of_week = "월";
                break;
            case 3:
                day_of_week = "화";
                break;
            case 4:
                day_of_week = "수";
                break;
            case 5:
                day_of_week = "목";
                break;
            case 6:
                day_of_week = "금";
                break;
            case 7:
                day_of_week = "토";
                break;
        }
        return month + "/" + day + "(" + day_of_week + ")";
    }
}
