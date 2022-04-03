package com.example.camcam.ui.search;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.camcam.R;
import com.example.camcam.databinding.FragmentHomeBinding;
import com.example.camcam.databinding.SearchFragmentBinding;

import java.util.Locale;

public class SearchFragment extends Fragment {

    private SearchFragmentBinding binding;
    private SearchViewModel mViewModel;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = SearchFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.uobCard.setOnClickListener(this::card_onClick);
        binding.uobCard.setVisibility(View.GONE);

        binding.searchBar.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after)
            { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count)
            { }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.toString().toLowerCase().contains("bath"))
                {
                    binding.uobCard.setVisibility(View.VISIBLE);
                }
                else
                {
                    binding.uobCard.setVisibility(View.GONE);
                }
            }
        });

        return root;
        // return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel
    }

    public void card_onClick(View view)
    {
        Toast.makeText(getContext(), "You have clicked P1", Toast.LENGTH_SHORT).show();
    }
}