package com.example.camcam.ui.map;

import androidx.lifecycle.ViewModelProvider;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camcam.R;
import com.example.camcam.ml.Model;
import com.example.camcam.ui.notifications.WiFi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment {

    private MapViewModel mViewModel;
    private static Long[] macAddresses = null;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        WiFi wifi = new WiFi(getContext(), new WiFi.WiFiReciever() {
            @Override
            public void handleSuccess(List<ScanResult> results)
            {
                HashMap<Long, Integer> scanResults = new HashMap<Long, Integer>();
                for(ScanResult result: results)
                {
                    long bssid = Integer.parseInt(result.BSSID.replace(":",""), 16);
                    scanResults.put(bssid, result.level);
                }

                if (macAddresses == null)
                {
                    macAddresses = getMacAddresses();
                }

                int[] inputs = new int[macAddresses.length];
                for (int i = 0; i < macAddresses.length; i++)
                {
                    inputs[i] = scanResults.getOrDefault(macAddresses[i], -100);
                }

                try
                {
                    Model model = Model.newInstance(getContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature = TensorBuffer.createFixedSize(inputs, DataType.FLOAT32);
                    inputFeature.loadBuffer(ByteBuffer.allocate(inputs.length));

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature);
                    TensorBuffer outputFeature = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();
                }
                catch (IOException e)
                {
                    // TODO Handle the exception
                }
            }

            @Override
            public void handleFailure()
            {
                Toast.makeText(getContext(), "Failed to scan...", Toast.LENGTH_SHORT).show();
            }
        });

        wifi.scan();

        //////////
//        if (macAddresses == null)
//        {
//            macAddresses = getMacAddresses();
//        }
//
//        int[] inputs = new int[macAddresses.length];
//        for (int i = 0; i < macAddresses.length; i++)
//        {
//            inputs[i] = scanResults.getOrDefault(macAddresses[i], -100);
//        }

//        try
//        {
//            Model model = Model.newInstance(getContext());
//
//            // Creates inputs for reference.
//            TensorBuffer inputFeature = TensorBuffer.createFixedSize(inputs, DataType.FLOAT32);
//            inputFeature.loadBuffer(ByteBuffer.allocate(inputs.length));
//
//            // Runs model inference and gets result.
//            Model.Outputs outputs = model.process(inputFeature);
//            TensorBuffer outputFeature = outputs.getOutputFeature0AsTensorBuffer();
//
//            // Releases model resources if no longer used.
//            model.close();
//        }
//        catch (IOException e)
//        {
//            // TODO Handle the exception
//        }

        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        // TODO: Use the ViewModel
    }

    private Long[] getMacAddresses()
    {
        Long[] macAddresses = null;
        String line;
        StringBuilder fileContents = new StringBuilder();

        InputStream inputStream = getResources().openRawResource(R.raw.mac_addresses);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            while ((line = reader.readLine()) != null)
            {
                fileContents.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            macAddresses = objectMapper.readValue(fileContents.toString(), Long[].class);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        return macAddresses;
    }
}