package com.kjh.hairshopdesigner;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class PageReservation extends Fragment {

    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout layout =
                (LinearLayout)inflater.inflate(R.layout.viewpager_reservation, container, false);

        new getReservation().execute();

        return layout;
    }

    public class getReservation extends AsyncTask<Void, Void, ArrayList<ReservationVO>> {

        @Override
        protected ArrayList<ReservationVO> doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ReservationVO> reservationVOS) {
            super.onPostExecute(reservationVOS);
        }
    }
}
