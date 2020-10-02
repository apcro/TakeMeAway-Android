package io.takemeaway.takemeaway.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.MainActivity;
import io.takemeaway.takemeaway.activities.WelcomeActivity;
import io.takemeaway.takemeaway.adapters.AirportSpinnerAdaptor;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.Airports;
import io.takemeaway.takemeaway.datamodels.SingleAirport;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.AvatarResponse;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.responsemodels.NearestAirportResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.app.Activity.RESULT_OK;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class FragmentProfile extends Fragment implements View.OnClickListener {

    private static final String TAG = FragmentProfile.class.getSimpleName();
    private int RESULT_LOAD_IMAGE;

    private String token;

    private EditText usernameEdit;
    private EditText emailaddressEdit;
    private EditText firstnameEdit;
    private EditText lastnameEdit;

    private UserDetails mCurrentUser;
    private ImageView mAvatarImage;

    private ArrayList<SingleAirport> homeValues;
    private SingleAirport selectedHome;
    private AirportSpinnerAdaptor homeAdaptor;
    private String home_airport = "LON";

    public ArrayList<SingleAirport> mAirports;
    private FusedLocationProviderClient mFusedLocationClient;

    private TextView mSelectedHomeAirport;

    private Bitmap mProfileBitmap = null;
    private boolean mAvatarChanged = false;

    private float lat, lon;

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 3;

    private Backend mBackend;
    private Context mContext;

    private ApiInterface mApiService;

    private View mView;

    private boolean mInitSpinner = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_profile, parent, false);

        mBackend = TakeMeAway.getBackend();
        mContext = this.getContext();

        token = mBackend.getUserToken();

        mApiService = ApiClient.getClient().create(ApiInterface.class);

        Button saveButton = mView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        Button resetTutorial = mView.findViewById(R.id.tutorialButton);
        resetTutorial.setOnClickListener(this);

        // do we already have the mAirports?
        SetDefaultAirports();

        Spinner homeSpinner = (Spinner) mView.findViewById(R.id.homeSpinner);
        homeAdaptor = new AirportSpinnerAdaptor(mContext, android.R.layout.simple_spinner_item, homeValues);
        homeAdaptor.setDropDownViewResource(R.layout.spinner_airportlist);
        homeSpinner.setAdapter(homeAdaptor);
        homeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mInitSpinner) {
                    mInitSpinner = true;
                    return;
                }
                selectedHome = (SingleAirport) parent.getItemAtPosition(position);
                mSelectedHomeAirport.setText(selectedHome.getCity() + ",??? " + selectedHome.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedHomeAirport.setText(mCurrentUser.getHome_airport());
            }
        });

        getUserSettings();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        ImageView findMe = (ImageView) mView.findViewById(R.id.iconFindme);
        findMe.setOnClickListener(this);

        mAvatarImage = (ImageView) mView.findViewById(R.id.avatarImage);
        mAvatarImage.setOnClickListener(this);

        Picasso.Builder mPicasso = new Picasso.Builder(mContext);
        mPicasso.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });

        setAvatarImage();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        return mView;

    }

    /**
     *
     */
    private void getAirports() {

        float lat = mCurrentUser.getLat();
        float lon = mCurrentUser.getLon();

        Call<Airports> call = mApiService.getAirports(String.valueOf(lat), String.valueOf(lon));
        call.enqueue(new Callback<Airports>() {
            @Override
            public void onResponse(Call<Airports>call, Response<Airports> response) {
                mAirports = response.body().getAirports();

                int status = response.body().getStatus();

                if (status == 1) {
                    homeAdaptor.setAirport(mAirports);
                    homeAdaptor.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<Airports> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.avatarImage: {
                ChangeAvatar();
                break;
            }

            case R.id.saveButton: {
                saveSettings();
                getActivity().onBackPressed();
                break;
            }

            case R.id.iconFindme: {
                int fineLocation = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                if (fineLocation == PackageManager.PERMISSION_GRANTED) {
                    FindMe();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
                break;
            }

            case R.id.tutorialButton: {
//                PreferencesManager prefManager = new PreferencesManager(this);
//                prefManager.setFirstTimeLaunch(true);
                TakeMeAway.getBackend().setFirstTimeLaunch(true);
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                getActivity().startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();
            }
            break;

        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    FindMe();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }

            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    ChangeAvatar();
                } else {
                    // we'll need to use a popup, or set the default avatar
                    Toast.makeText(mContext, "Read storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     *
     */
    @SuppressWarnings({"MissingPermission"})
    private void FindMe() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = (float) location.getLatitude();
                            lon = (float) location.getLongitude();
                            GetNearestAirport(lat, lon);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mBackend = TakeMeAway.getBackend();
        getUserSettings();
        token = mBackend.getUserToken();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void saveSettings() {
        mCurrentUser = mBackend.getCurrentUser();
        mCurrentUser.setUsername(usernameEdit.getText().toString());
        mCurrentUser.setSurname(lastnameEdit.getText().toString());
        mCurrentUser.setName(firstnameEdit.getText().toString());
        mCurrentUser.setMail(emailaddressEdit.getText().toString());
        mCurrentUser.setLat(lat);
        mCurrentUser.setLon(lon);
        mCurrentUser.setHome_airport(home_airport);

        mBackend.saveCurrentUser(mCurrentUser);

        saveUserSettings();
    }

    private void getUserSettings() {
        mCurrentUser = mBackend.getCurrentUser();

        if (mAirports == null) {
            getAirports();
        } else {
            homeValues = mAirports;
        }

        usernameEdit = (EditText) mView.findViewById(R.id.name);
        emailaddressEdit = (EditText) mView.findViewById(R.id.emailaddress);
        firstnameEdit = (EditText) mView.findViewById(R.id.firstname);
        lastnameEdit = (EditText) mView.findViewById(R.id.lastname);
        mSelectedHomeAirport = (TextView) mView.findViewById(R.id.selectedhometext);

        usernameEdit.setText(mCurrentUser.getUsername());
        emailaddressEdit.setText(mCurrentUser.getMail());
        lastnameEdit.setText(mCurrentUser.getSurname());
        firstnameEdit.setText(mCurrentUser.getName());
        mSelectedHomeAirport.setText(mCurrentUser.getHome_airport());

        // get airport details - we don't store these
        Call<SingleAirport> call = mApiService.getAirportDetails(token, mCurrentUser.getHome_airport());
        call.enqueue(new Callback<SingleAirport>() {
            @Override
            public void onResponse(Call<SingleAirport>call, Response<SingleAirport> response) {

                int status = response.body().getStatus();

                if (status == 1) {
                    mSelectedHomeAirport.setText(response.body().getCity() + " " + response.body().getName());
                }

            }

            @Override
            public void onFailure(Call<SingleAirport> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });




    }

    private void saveUserSettings() {

        final ProgressDialog progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving");
        progressDialog.show();

        Call<BaseResponse> call = mApiService.updateProfile(token,
                mCurrentUser.getUsername(),
                mCurrentUser.getName(),
                mCurrentUser.getSurname(),
                mCurrentUser.getMail(),
                mCurrentUser.getHome_airport(),
                mCurrentUser.getLat(),
                mCurrentUser.getLon()
        );
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {

                int status = response.body().getStatus();

                if (status == 0) {
                    // we have an error
                    String errormessage = response.body().getErrorMessage();
                    Toast.makeText(mContext, errormessage, Toast.LENGTH_SHORT).show();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    getActivity().onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

        // upload new avatar, store on server
        if (mProfileBitmap != null) {

            try {
                uploadAvatar();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     *
     */
    private void goHome() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        finish();
    }

    private void GetNearestAirport(float lat, float lon) {

        final ProgressDialog progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Searching...");
        progressDialog.show();

        Call<NearestAirportResponse> call = mApiService.getNearestAirport(token, String.valueOf(lat), String.valueOf(lon));
        call.enqueue(new Callback<NearestAirportResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<NearestAirportResponse>call, Response<NearestAirportResponse> response) {
                int status = response.body().getStatus();
                if (status == 1) {
                    progressDialog.dismiss();
                    SingleAirport airport = response.body().getAirport();
                    String iata = airport.getIata();
                    mSelectedHomeAirport = (TextView) mView.findViewById(R.id.selectedhometext);
                    mSelectedHomeAirport.setText(airport.getName() + ",!! " + airport.getCity());
                    home_airport = iata;

                    mCurrentUser = mBackend.getCurrentUser();
                    mCurrentUser.setLat(airport.getLat());
                    mCurrentUser.setLon(airport.getLon());
                    mCurrentUser.setHome_airport(home_airport);

                    mBackend.saveCurrentUser(mCurrentUser);

                    getAirports();
                }
            }

            @Override
            public void onFailure(Call<NearestAirportResponse>call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void SetDefaultAirports() {
        selectedHome = new SingleAirport("LON", "London All", "London");
        homeValues = new ArrayList<>();
        homeValues.add(new SingleAirport("LHR", "Heathrow", "London"));
        homeValues.add(new SingleAirport("CDG", "Charles de Gaulle", "Paris"));
        homeValues.add(new SingleAirport("BER", "Berlin", "Berlin"));
        homeValues.add(new SingleAirport("AMS", "Schipohl", "Amsterdam"));
    }

    private void setAvatarImage() {

        String avatarUri = mBackend.getCurrentUser().getAvatar();
        if (avatarUri.equals("")) {
            mAvatarImage.setImageResource(R.drawable.ic_account_circle_white_24dp);
        } else {
            mAvatarImage.setImageBitmap(mBackend.getCurrentUser().getAvatarBitmap());
        }
    }

    private void ChangeAvatar() {

        int fineLocation = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        }

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();

                if (imageUri != null) {
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    mProfileBitmap = BitmapFactory.decodeStream(imageStream);
                    mAvatarImage.setImageBitmap(mProfileBitmap);
                    mAvatarChanged = true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadAvatar() throws IOException {
        if (mAvatarChanged) {
            String filename = "avatar.png";
            File f = new File(mContext.getCacheDir(), filename);
            //noinspection ResultOfMethodCallIgnored
            f.createNewFile();

            // Convert bitmap to byte array
            Bitmap bitmap = mProfileBitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            // write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/png"), f);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", f.getName(), reqFile);

            Call<AvatarResponse> call = mApiService.updateAvatar(token, body);
            call.enqueue(new Callback<AvatarResponse>() {
                @Override
                public void onResponse(Call<AvatarResponse> call, Response<AvatarResponse> response) {
                    int status = response.body().getStatus();
                    if (status == 1) {
                        String avatar = response.body().getAvatar();
                        UserDetails user = mBackend.getCurrentUser();
                        user.setAvatar(avatar);
                        user.setAvatarBitmap(mProfileBitmap);
                        mBackend.saveCurrentUser(user);
                    }
                }

                @Override
                public void onFailure(Call<AvatarResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
            mAvatarChanged = false;
        }

    }

    public void updateHomeAirport(String text) {
        this.mSelectedHomeAirport.setText(text);
    }

}