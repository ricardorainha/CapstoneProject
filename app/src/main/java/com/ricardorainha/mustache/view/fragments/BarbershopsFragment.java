package com.ricardorainha.mustache.view.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ricardorainha.mustache.R;
import com.ricardorainha.mustache.adapter.BarbershopsPagerAdapter;
import com.ricardorainha.mustache.databinding.FragmentBarbershopsBinding;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarbershopsFragment extends Fragment {

    private FragmentBarbershopsBinding binding;


    public BarbershopsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_barbershops, container, false);

        configureFields();

        return binding.getRoot();
    }

    private void configureFields() {
        binding.barbershopsPager.setAdapter(new BarbershopsPagerAdapter(getFragmentManager(), getResources().getStringArray(R.array.barbershops_tabs_titles)));
        binding.barbershopsTabs.setupWithViewPager(binding.barbershopsPager);
    }

}
