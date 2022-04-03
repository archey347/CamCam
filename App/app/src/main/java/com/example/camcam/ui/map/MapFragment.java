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
import com.example.camcam.databinding.MapFragmentBinding;
import com.example.camcam.databinding.SearchFragmentBinding;
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
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment {

    private MapViewModel mViewModel;
    private MapFragmentBinding binding;
    private static Long[] macAddresses = null;
    private static String[] labels = new String[] { "LIBRARY 2", "LIBRARY 3", "LIBRARY 4", "LIBRARY 5", "BRENDON", "1W 3", "1W 2.101", "1W 2.102", "1W 2.103", "1W 2.104", "3WN 3.T", "3WN 2.1", "LAKE", "PARADE", "3 PARADE", "-3 PARADE", "1WN", "3E", "FRESH" };

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = MapFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        WiFi wifi = new WiFi(getContext(), new WiFi.WiFiReciever() {
            @Override
            public void handleSuccess(List<ScanResult> results)
            {
                HashMap<Long, Integer> scanResults = new HashMap<Long, Integer>();
                for(ScanResult result: results)
                {
                    String hexString = result.BSSID.replace(":","");
//                    BigInteger integer = new BigInteger(hexString, 16);
                    long bssid = Long.parseLong(hexString, 16);
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
                    TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 454}, DataType.FLOAT32);

                    ByteBuffer buffer = ByteBuffer.allocate(inputs.length * 4);
                    for (int input: inputs)
                    {
                        buffer.putInt(input);
                    }

                    inputFeature.loadBuffer(buffer);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature);
                    TensorBuffer outputFeature = outputs.getOutputFeature0AsTensorBuffer();

                    int[] _outputs = outputFeature.getIntArray();
                    // Releases model resources if no longer used.
                    model.close();


//                    String label = null;
//                    for (int i = 0; i < _outputs.length; i++)
//                    {
//                        if (_outputs[i] == 1)
//                        {
//                            label = labels[i];
//                        }
//                    }

//                    if (label != null)
//                    {
//                        binding.mapImage.setImageResource(R.id.);
//                    }

                    binding.mapImage.setImageResource(R.drawable.map_3wn);
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

        return root;
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