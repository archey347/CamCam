package com.example.camcam;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.camcam.databinding.FragmentSecondBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import android.widget.Toast;

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

        ((Button)  getView().findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((TextView) getView().findViewById(R.id.textView)).setText("LOADING");

                WiFi wifi = new WiFi(getContext(), new WiFi.WiFiReciever() {
                    @Override
                    public void handleSuccess(List<ScanResult> list) {

                        for(ScanResult result: list) {
                            String text = result.SSID + " (" + result.BSSID + "=" + Integer.toString(result.level) + ")";

                            Snackbar.make(getView(), text, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();


                        }


                        ((TextView) getView().findViewById(R.id.textView)).setText("DONE");
                    }

                    @Override
                    public void handleFailure() {
                        ((TextView) getView().findViewById(R.id.textView)).setText("FAIL");
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