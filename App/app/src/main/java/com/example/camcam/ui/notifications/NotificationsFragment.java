package com.example.camcam.ui.notifications;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.camcam.R;
import com.example.camcam.databinding.FragmentNotificationsBinding;

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

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ((Button)  root.findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) root.findViewById(R.id.editTextTextPersonName2)).setText("");
            }
        });


        ((Button)  root.findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((TextView) root.findViewById(R.id.textView2)).setText("Status: LOADING");

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

                        String location = ((EditText) root.findViewById(R.id.editTextTextPersonName2)).getText().toString();

                        try {
                            data.put("location", location);
                            data.put("stations", stations);
                        } catch (Exception e) {
                            ((TextView) root.findViewById(R.id.textView2)).setText("JSON FAILED");
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
                                    ((TextView) root.findViewById(R.id.textView2)).setText("Status: SENDING FAILED");
                                    return;
                                }

                                if(response.code() == 200) {
                                    ((TextView) root.findViewById(R.id.textView2)).setText("Status: DONE");
                                } else {
                                    ((TextView) root.findViewById(R.id.textView2)).setText("Status: NON-200");
                                }
                            }
                        };

                        t.execute(call);
                    }

                    @Override
                    public void handleFailure() {
                        ((TextView) root.findViewById(R.id.textView2)).setText("Status: FAIL");
                    }
                });

                wifi.scan();
            }
        });

        return root;
    }

    private NavController getNavController()
    { // See https://stackoverflow.com/questions/14287093/navigate-from-one-fragment-to-another-on-click-of-a-button
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (!(fragment instanceof NavHostFragment)) {
            throw new IllegalStateException("Activity " + this + " does not have a NavHostFragment");
        }
        return ((NavHostFragment) fragment).getNavController();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}