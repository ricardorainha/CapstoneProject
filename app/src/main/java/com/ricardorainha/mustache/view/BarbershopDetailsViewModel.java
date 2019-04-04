package com.ricardorainha.mustache.view;

import com.ricardorainha.mustache.model.Barbershop;
import com.ricardorainha.mustache.model.Repository;

import java.util.Observable;
import java.util.Observer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BarbershopDetailsViewModel extends ViewModel {

    private MutableLiveData<Barbershop> barbershop = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public BarbershopDetailsViewModel() {
        loading.setValue(false);
    }

    public MutableLiveData<Barbershop> getBarbershop() {
        return barbershop;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public void retrieveBarbershopDetails() {
        if (barbershop.getValue() != null) {
            Repository repo = Repository.getInstance();
            repo.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (arg instanceof Barbershop) {
                        barbershop.setValue((Barbershop) arg);
                        loading.setValue(false);
                        repo.deleteObserver(this);
                    }
                }
            });
            loading.setValue(true);
            repo.requestBarbershopDetails(barbershop.getValue());
        }
    }
}
