package todo.swu.applepicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        FragmentDaily fragment = (FragmentDaily) getParentFragmentManager().findFragmentByTag("fragment_daily");
        try {
            String year_string = Integer.toString(year);
            String month_string = Integer.toString(month + 1);
            String day_string = Integer.toString(day);
            String date_string = (year_string + "-" + month_string + "-" + day_string);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date nDate = dateFormat.parse(date_string);
            String datePicked = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(nDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(nDate);

            //요일 구함
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
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

            fragment.processDatePickerResult(year_string, month_string, day_string, day_of_week, datePicked);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}