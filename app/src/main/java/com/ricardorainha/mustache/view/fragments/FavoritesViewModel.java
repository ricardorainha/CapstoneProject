package com.ricardorainha.mustache.view.fragments;

import com.ricardorainha.mustache.adapter.BarbershopsAdapter;
import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Session;
import com.ricardorainha.mustache.model.User;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FavoritesViewModel extends ViewModel implements BarbershopsAdapter.ActionCallback {

    private MutableLiveData<BarbershopsAdapter> adapter = new MutableLiveData<>();
    private MutableLiveData<Barbershop> selectedBarbershop = new MutableLiveData<>();

    public FavoritesViewModel() {
        Session.getInstance().getUser().observeForever(user -> updateAdapter(user));
    }

    public MutableLiveData<BarbershopsAdapter> getAdapter() {
        return adapter;
    }

    public MutableLiveData<Barbershop> getSelectedBarbershop() {
        return selectedBarbershop;
    }

    private void updateAdapter(User user) {
        adapter.setValue(new BarbershopsAdapter(new ArrayList<>(user.getFavorites().values()), this));
    }

    @Override
    public void onDetailsClicked(Barbershop barbershop) {
        this.selectedBarbershop.setValue(barbershop);
    }
}
