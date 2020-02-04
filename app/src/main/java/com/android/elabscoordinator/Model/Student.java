package com.android.elabscoordinator.Model;

import androidx.annotation.Nullable;

public class Student {
    @Override
    public int hashCode() {
        return Integer.parseInt(this.roll);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this.roll.equals(roll);

    }

    String authKey;
    String roll;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public Student(String authKey, String roll) {
        this.authKey = authKey;
        this.roll = roll;
    }
}
