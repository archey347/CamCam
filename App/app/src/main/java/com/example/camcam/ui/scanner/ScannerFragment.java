package com.example.camcam.ui.scanner;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.camcam.R;
import com.example.camcam.ui.notifications.WiFi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScannerFragment newInstance(String param1, String param2) {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((Button)  getView().findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) getView().findViewById(R.id.editTextTextPersonName)).setText("");
            }
        });


        ((Button)  getView().findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((TextView) getView().findViewById(R.id.textView)).setText("Status: LOADING");

                WiFi wifi = new WiFi(getContext(), new WiFi.WiFiReciever() {
                    @Override
                    public void handleSuccess(List<ScanResult> list) {

                        JSONArray stations = new JSONArray();

                        for(ScanResult result: list) {

                            JSONArray station = new JSONArray();

                            station.put(result.BSSID);
                            station.put(result.level);

                            stations.put(station);

                            //String text = result.SSID + " (" + result.BSSID + "=" + Integer.toString(result.level) + ")";

                            //Snackbar.make(getView(), text, Snackbar.LENGTH_LONG)
                            //        .setAction("Action", null).show();
                        }

                        JSONObject data = new JSONObject();

                        String location = ((EditText) getView().findViewById(R.id.editTextTextPersonName)).getText().toString();

                        try {
                            data.put("location", location);
                            data.put("stations", stations);
                        } catch (Exception e) {
                            ((TextView) getView().findViewById(R.id.textView)).setText("JSON FAILED");
                            return;
                        }

                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add("data", data.toString())
                                .build();

                        // Send to server
                        Request req = new Request.Builder()
                                .url("https://camcam.wwlrc.co.uk/?key=JayDoesn'tKnowWhatATomTomIs")
                                .post(formBody)
                                .build();

                        Call call = client.newCall(req);
                        Response response;

                        AsyncTask<Call, Void, Response> t = new AsyncTask<Call, Void, Response>() {
                            @Override
                            protected Response doInBackground(Call... calls) {
                                try {
                                    return call.execute();
                                } catch (IOException e) {
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(Response response) {
                                if (response == null) {
                                    ((TextView) getView().findViewById(R.id.textView)).setText("Status: SENDING FAILED");
                                    return;
                                }

                                if(response.code() == 200) {
                                    ((TextView) getView().findViewById(R.id.textView)).setText("Status: DONE");
                                } else {
                                    ((TextView) getView().findViewById(R.id.textView)).setText("Status: NON-200");
                                }
                            }
                        };

                        t.execute(call);
                    }

                    @Override
                    public void handleFailure() {
                        ((TextView) getView().findViewById(R.id.textView)).setText("Status: FAIL");
                    }
                });

                wifi.scan();
            }
        });



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }
}