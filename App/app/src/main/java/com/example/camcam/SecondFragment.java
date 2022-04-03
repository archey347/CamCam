package com.example.camcam;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.camcam.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.nio.channels.AsynchronousByteChannel;
import java.util.List;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}