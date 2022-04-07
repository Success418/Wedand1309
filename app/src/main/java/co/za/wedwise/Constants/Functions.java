package co.za.wedwise.Constants;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import co.za.wedwise.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by za on 10/20/2018.
 */

public class Functions {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void Opendate_picker(Context context, final TextView TextView){
        final SimpleDateFormat format= new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());



        String dateString= TextView.getText().toString();
        if(dateString.equals("")){
            dateString="01/01/2000";
        }

        String[] parts = dateString.split("/");

        DatePickerDialog mdiDialog =new DatePickerDialog(context, R.style.datepicker_style,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                Date chosenDate = cal.getTime();
                TextView.setText(format.format(chosenDate));

            }
        }, Integer.parseInt(parts[2]),Integer.parseInt(parts[0])-1,Integer.parseInt(parts[1]));
        mdiDialog.show();
    }



}