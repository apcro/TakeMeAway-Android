package io.takemeaway.takemeaway.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.MainActivity;
import io.takemeaway.takemeaway.activities.ProfileFilterActivity;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.responsemodels.UserSettings;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class FragmentFilters extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentFilters.class.getSimpleName();

    private String token;

    private TextView budgetValue;
    private TextView peopleValue;

    private TextView mThisWeekendDates;
    private TextView mNextWeekendDates;
    private TextView mTwoWeekendDates;
    private TextView mLeaveText, mReturnText;   // for custom dates

    private RadioGroup mTravellingGroup;
    private RadioGroup mLeavingGroup;
    private RadioGroup mReturningGroup;
    private RadioGroup mCurrencyGroup;
    String mCurrency;

    private Backend mBackend;
    private Context mContext;

    private Locale currentLocale;

    private UserDetails mCurrentUser;

    private UserSettings mUserSettings;

    private int MINBUDGET = 300;
    private int MAXBUDGET = 2000;
    private int BUDGETSTEPS = 50;

    private View mView;

    private int tabToShow = 0;

    private long mLeaveDateTime, mReturnDateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        mView = inflater.inflate(R.layout.fragment_filters, parent, false);

        mBackend = TakeMeAway.getBackend();

        mContext = this.getContext();

        currentLocale = getResources().getConfiguration().locale;

        mCurrentUser = mBackend.getCurrentUser();
        mCurrency = mBackend.CurrencySymbol();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        token = mBackend.getUserToken();

        // set custom dates to 0
        mLeaveDateTime = mCurrentUser.getLeaveDate();
        mReturnDateTime = mCurrentUser.getReturnDate();

        SeekBar budgetSeekbar = (SeekBar) mView.findViewById(R.id.budgetSeekbar);
        SeekBar peopleSeekbar = (SeekBar) mView.findViewById(R.id.peopleSeekbar);
        budgetValue = (TextView) mView.findViewById(R.id.budgetValue);
        peopleValue = (TextView) mView.findViewById(R.id.peopleValue);

        mCurrencyGroup = mView.findViewById(R.id.currencyGroup);
        String currency = mCurrentUser.getCurrencyCode();
        mCurrencyGroup.clearCheck();
        if (currency.equals("EUR")) {
            mCurrencyGroup.check(R.id.eur);
            mCurrency = "EUR";
        } else if (currency.equals("USD")) {
            mCurrencyGroup.check(R.id.usd);
            mCurrency = "USD";
        } else if (currency.equals("CAD")) {
            mCurrencyGroup.check(R.id.cad);
            mCurrency = "CAD";
        } else {
            mCurrencyGroup.check(R.id.gbp);
            mCurrency = "GBP";
        }

        // actual setting values
        budgetSeekbar.setProgress(((mCurrentUser.getBudget()-MINBUDGET)/BUDGETSTEPS));
        budgetSeekbar.setProgress(((mCurrentUser.getBudget()-MINBUDGET)/BUDGETSTEPS));
        budgetValue.setText(Utilities.getCurrencySymbol(mCurrency) + String.valueOf(mCurrentUser.getBudget()));
        peopleSeekbar.setProgress(mCurrentUser.getPeople()-1);
        peopleValue.setText(String.valueOf(mCurrentUser.getPeople()));

        budgetSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int budget = (progress * BUDGETSTEPS) + MINBUDGET;
                budgetValue.setText(Utilities.getCurrencySymbol(mCurrency) + String.valueOf(budget));
                mCurrentUser.setBudget(budget);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        peopleSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                peopleValue.setText(String.valueOf(progress+1));
                mCurrentUser.setPeople(progress+1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        String hotelTypes = mCurrentUser.getHotelTypes();
        if (hotelTypes == null) {
            hotelTypes = "201,204,208,219,203";
        }
        List<String> switchFilterlist = Arrays.asList(hotelTypes.split("\\s*,\\s*"));

        Switch switch201 = mView.findViewById(R.id.switch201);
        Switch switch204 = mView.findViewById(R.id.switch204);
        Switch switch208 = mView.findViewById(R.id.switch208);
        Switch switch219 = mView.findViewById(R.id.switch219);
        Switch switch203 = mView.findViewById(R.id.switch203);

        switch201.setChecked(false);
        switch204.setChecked(false);
        switch208.setChecked(false);
        switch219.setChecked(false);
        switch203.setChecked(false);

        for (int i = 0; i < switchFilterlist.size(); i++) {
            String setting = switchFilterlist.get(i);
            if (setting.equals("201")) {
                switch201.setChecked(true);
            }
            if (setting.equals("204")) {
                switch204.setChecked(true);
            }
            if (setting.equals("208")) {
                switch208.setChecked(true);
            }
            if (setting.equals("219")) {
                switch219.setChecked(true);
            }
            if (setting.equals("203")) {
                switch203.setChecked(true);
            }
        }

        LinearLayout travelling = mView.findViewById(R.id.travelling);
        LinearLayout travellingCustom = mView.findViewById(R.id.travellingCustom);

        travelling.setVisibility(View.VISIBLE);
        travellingCustom.setVisibility(View.GONE);

        TabLayout dateTabs = mView.findViewById(R.id.dateTabs);
        dateTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    travelling.setVisibility(View.VISIBLE);
                    travellingCustom.setVisibility(View.GONE);
                    mLeaveDateTime = 0;
                    mReturnDateTime = 0;
                } else {
                    travelling.setVisibility(View.GONE);
                    travellingCustom.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final Calendar myCalendar = Calendar.getInstance();
        mLeaveText = mView.findViewById(R.id.manualLeaveOn);
        mReturnText = mView.findViewById(R.id.manualReturnOn);
        String myFormat = "dd/MM/yyyy"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        if (mLeaveDateTime != 0 && mReturnDateTime != 0) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(mLeaveDateTime * 1000L);
            String date = DateFormat.format("dd/MM/yyyy", cal).toString();
            mLeaveText.setText(date);

            cal.setTimeInMillis(mReturnDateTime * 1000L);
            date = DateFormat.format("dd/MM/yyyy", cal).toString();
            mReturnText.setText(date);
        }

        mLeaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog.OnDateSetListener dateSelectedListener = (mView, year, monthOfYear, dayOfMonth) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    mLeaveText.setText(sdf.format(myCalendar.getTime()));
                    mLeaveDateTime = myCalendar.getTimeInMillis()/1000;
                };

                if (mLeaveDateTime != 0) {
                    myCalendar.setTimeInMillis(mLeaveDateTime * 1000L);
                }

                int year = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dp = new DatePickerDialog(mContext, dateSelectedListener, year, month, day);
                dp.setTitle("Select leaving date");
                dp.setIcon(R.drawable.ic2_takeoff);
                dp.show();
            }
        });

        mReturnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog.OnDateSetListener dateSelectedListener = (mView, year, monthOfYear, dayOfMonth) -> {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    mReturnText.setText(sdf.format(myCalendar.getTime()));
                    mReturnDateTime = myCalendar.getTimeInMillis()/1000L ;
                };

                if (mLeaveDateTime != 0) {
                    myCalendar.setTimeInMillis(mLeaveDateTime * 1000L);
                }

                int year = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dp = new DatePickerDialog(mContext, dateSelectedListener, year, month, day);
                dp.setTitle("Select return date");
                dp.show();
            }
        });

        mTravellingGroup = mView.findViewById(R.id.travellingGroup);
        String travelDates = mCurrentUser.getTravel_dates();
        mTravellingGroup.clearCheck();
        if (travelDates.equals("thisweekend")) {
            mTravellingGroup.check(R.id.thisWeekend);
        } else if (travelDates.equals("in2weeks")) {
            mTravellingGroup.check(R.id.weekendAfter);
        } else {
            mTravellingGroup.check(R.id.nextWeekend);
        }

        Map<String, String> travelDateStrings = Utilities.getTravelDates(mCurrentUser.getLeaveDay(), mCurrentUser.getReturnDay());

        mThisWeekendDates = mView.findViewById(R.id.thisWeekendDates);
        mNextWeekendDates = mView.findViewById(R.id.nextWeekendDates);
        mTwoWeekendDates = mView.findViewById(R.id.weekendAfterDates);

        mLeavingGroup = mView.findViewById(R.id.leaveGroup);
        String leavingDay = mCurrentUser.getLeaveDay();
        mLeavingGroup.clearCheck();
        if (leavingDay.equals("thursday")) {
            mLeavingGroup.check(R.id.leaveThursday);
        } else if (leavingDay.equals("saturday")) {
            mLeavingGroup.check(R.id.leaveSaturday);
        } else {
            // default
            mLeavingGroup.check(R.id.leaveFriday);
        }

        mLeavingGroup.setOnCheckedChangeListener((radioGroup, i) -> {

            int sel = mLeavingGroup.getCheckedRadioButtonId();

            switch (sel) {
                case R.id.leaveThursday:
                    mCurrentUser.setLeaveDay("thursday");
                    break;
                case R.id.leaveFriday:
                    mCurrentUser.setLeaveDay("friday");
                    break;
                case R.id.leaveSaturday:
                    mCurrentUser.setLeaveDay("saturday");
                    break;
            }

            Map<String, String> travelDateStrings1 = Utilities.getTravelDates(mCurrentUser.getLeaveDay(), mCurrentUser.getReturnDay());
            mThisWeekendDates.setText(travelDateStrings1.get("thisweekend"));
            mNextWeekendDates.setText(travelDateStrings1.get("nextweekend"));
            mTwoWeekendDates.setText(travelDateStrings1.get("in2weeks"));
        });

        mReturningGroup = mView.findViewById(R.id.returnGroup);
        String returnDay = mCurrentUser.getReturnDay();
        mReturningGroup.clearCheck();
        if (returnDay.equals("tuesday")) {
            mReturningGroup.check(R.id.returnTuesday);
        } else if (returnDay.equals("monday")) {
            mReturningGroup.check(R.id.returnMonday);
        } else {
            // default
            mReturningGroup.check(R.id.returnSunday);
        }

        mReturningGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int sel = mReturningGroup.getCheckedRadioButtonId();

                switch (sel) {
                    case R.id.returnSunday:
                        mCurrentUser.setReturnDay("sunday");
                        break;
                    case R.id.returnMonday:
                        mCurrentUser.setReturnDay("monday");
                        break;
                    case R.id.returnTuesday:
                        mCurrentUser.setReturnDay("tuesday");
                        break;
                }

                Map<String, String> travelDateStrings = Utilities.getTravelDates(mCurrentUser.getLeaveDay(), mCurrentUser.getReturnDay());
                mThisWeekendDates.setText(travelDateStrings.get("thisweekend"));
                mNextWeekendDates.setText(travelDateStrings.get("nextweekend"));
                mTwoWeekendDates.setText(travelDateStrings.get("in2weeks"));
            }
        });

        mCurrencyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                int selectedCurrency = mCurrencyGroup.getCheckedRadioButtonId();

                if (selectedCurrency == R.id.eur) {
                    mCurrency = "EUR";
                } else mCurrency = "GBP";

                budgetValue.setText(Utilities.getCurrencySymbol(mCurrency) + String.valueOf(mCurrentUser.getBudget()));
            }
        });
        Button saveButton = mView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mThisWeekendDates.setText(travelDateStrings.get("thisweekend"));
        mNextWeekendDates.setText(travelDateStrings.get("nextweekend"));
        mTwoWeekendDates.setText(travelDateStrings.get("in2weeks"));


        mThisWeekendDates.setTextColor(getActivity().getColor(R.color.brand_dark));
        mNextWeekendDates.setTextColor(getActivity().getColor(R.color.brand_dark));
        mTwoWeekendDates.setTextColor(getActivity().getColor(R.color.brand_dark));
        mThisWeekendDates.setTypeface(null, Typeface.ITALIC);
        mNextWeekendDates.setTypeface(null, Typeface.ITALIC);
        mTwoWeekendDates.setTypeface(null, Typeface.ITALIC);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.saveButton:
                saveSettings();
                break;
            case R.id.resetTutorial:
                mBackend.setSharedPreferences("firstrun", "yes");
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        mBackend = TakeMeAway.getBackend();
        mCurrentUser = mBackend.getCurrentUser();
        token = mBackend.getUserToken();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void saveSettings() {

        final ProgressDialog progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        String mFilters;

        ArrayList<String> filters = new ArrayList<>();

        int selectedId = mTravellingGroup.getCheckedRadioButtonId();
        String mDates;
        if (selectedId == R.id.thisWeekend) {
            mDates = "thisweekend";
        } else if (selectedId == R.id.weekendAfter) {
            mDates = "in2weeks";
        } else mDates = "nextweekend";

        int selectedCurrency = mCurrencyGroup.getCheckedRadioButtonId();
        String mCurrency;
        if (selectedCurrency == R.id.eur) {
            mCurrency = "EUR";
        } else if (selectedCurrency == R.id.usd) {
            mCurrency = "USD";
        } else if (selectedCurrency == R.id.cad) {
            mCurrency = "CAD";
        } else {
            mCurrency = "GBP";
        }


        Switch fSwitch = (Switch) mView.findViewById(R.id.switch201);
        if (fSwitch.isChecked()) {
            filters.add("201");
        }

        fSwitch = (Switch) mView.findViewById(R.id.switch204);
        if (fSwitch.isChecked()) {
            filters.add("204");
        }

        fSwitch = (Switch) mView.findViewById(R.id.switch208);
        if (fSwitch.isChecked()) {
            filters.add("208");
        }

        fSwitch = (Switch) mView.findViewById(R.id.switch219);
        if (fSwitch.isChecked()) {
            filters.add("219");
        }

        fSwitch = (Switch) mView.findViewById(R.id.switch203);
        if (fSwitch.isChecked()) {
            filters.add("203");
        }

        mFilters = android.text.TextUtils.join(",", filters);

        mCurrentUser.setHotelTypes(mFilters);
        mCurrentUser.setTravel_dates(mDates);

        String mLocale = currentLocale.getDefault().toString();
        mCurrentUser.setLocale(mLocale);
        mCurrentUser.setCurrencyCode(mCurrency);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiService.updateSettings(token,
                mCurrentUser.getBudget(),
                mCurrentUser.getPeople(),
                50, // mCurrentUser.getSplit(),
                mDates, //dates
                mCurrentUser.getLeaveDay(),
                mCurrentUser.getReturnDay(),
                mFilters,
                mCurrency,
                mLocale,
                mLeaveDateTime,
                mReturnDateTime
        );
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {

                if (response.body() != null) {
                    int status = response.body().getStatus();

                    if (status == 0) {
                        // we have an error
                        String errormessage = response.body().getErrorMessage();
                        Toast.makeText(mContext, errormessage, Toast.LENGTH_SHORT).show();
                    } else {
                        mBackend.saveCurrentUser(mCurrentUser);
                        progressDialog.dismiss();
                        goHomeWithRecycle();
                    }
                } else {
                    // @TODO deal with a null body on filters call
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Sorry");
                    builder.setMessage("Something went wrong. We've made a note of it.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }

    private void goHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
    }

    private void goHomeWithRecycle() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("recycle", 1);
        getActivity().startActivity(intent);
    }


}