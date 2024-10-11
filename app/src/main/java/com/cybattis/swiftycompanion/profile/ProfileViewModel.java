package com.cybattis.swiftycompanion.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> data = new MutableLiveData<>();

    public MutableLiveData<User> getUserData() {
        return data;
    }

    public void setUserData(User user) {
        data.setValue(user);
    }
}