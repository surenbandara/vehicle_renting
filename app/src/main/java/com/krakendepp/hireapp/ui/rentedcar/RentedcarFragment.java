package com.krakendepp.hireapp.ui.rentedcar;

import android.annotation.SuppressLint;
import android.graphics.RenderNode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krakendepp.hireapp.R;
import com.krakendepp.hireapp.databinding.FragmentRentedcarBinding;
import com.krakendepp.hireapp.ui.rentedcar.item.ProductItemView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RentedcarFragment extends Fragment {
    private FragmentRentedcarBinding binding;


    private ArrayList<ArrayList<String>> vehicaldata = new ArrayList<ArrayList<String>>();
    private ProductItemView adpter;

    private ProgressBar progressBar;
    private  TextView Emptymessage;


    private static final String URL = "jdbc:mysql://sql7.freemysqlhosting.net/sql7617117";
    private static final String USER = "sql7617117";
    private static final String PASSWORD = "h8xTHz8dSK";




    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, ArrayList<ArrayList<String>>> {
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... voids) {
            vehicaldata.clear();
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {


                String sql = "SELECT name, imageurl, seat,status, renter ,price , timeperiod FROM vehical";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    ArrayList<String> info = new ArrayList<>();
                    info.add(resultSet.getString("name"));
                    info.add(resultSet.getString("imageurl"));
                    info.add(resultSet.getString("seat"));
                    info.add(resultSet.getString("status"));
                    info.add(resultSet.getString("renter"));
                    info.add(resultSet.getString("price"));
                    info.add(resultSet.getString("timeperiod"));

                    if(resultSet.getString("status").equals("1") || resultSet.getString("status").equals("2")){
                        vehicaldata.add(info);}
                }
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

            return vehicaldata;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            System.out.println(result.size());
            progressBar.setVisibility(View.GONE);
            if (!result.isEmpty()) {
                System.out.println(result.get(0));
                adpter.notifyItemInserted(result.size()-1);

            }

            else{
                Emptymessage.setVisibility(View.VISIBLE);

            }
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // GalleryViewModel galleryViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(GalleryViewModel.class);
        vehicaldata.clear();
        new RentedcarFragment.InfoAsyncTask().execute();

        binding = FragmentRentedcarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();




        progressBar = root.findViewById(R.id.loadingProgressBarRentedcar);
        Emptymessage = root.findViewById(R.id.noVehiclesrentedTextView);

        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = root.findViewById(R.id.recycleviewrented);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adpter = new ProductItemView(vehicaldata ,progressBar);
        recyclerView.setAdapter(adpter);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}