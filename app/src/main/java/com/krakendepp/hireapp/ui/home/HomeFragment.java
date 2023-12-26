package com.krakendepp.hireapp.ui.home;

import android.annotation.SuppressLint;
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

import com.krakendepp.hireapp.databinding.FragmentHomeBinding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.krakendepp.hireapp.R;
import com.krakendepp.hireapp.ui.home.item.ProductItemView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    private ArrayList<ArrayList<String>> vehicaldata_ = new ArrayList<ArrayList<String>>();
    private  ProductItemView adpter_;

    private ProgressBar progressBar;
    private  TextView Emptymessage;


    private static final String URL = "mysql://avnadmin:AVNS_uflhxUVOijRksWtK82R@mysql-8cbc6e8-surenbandara7-1be0.a.aivencloud.com:13657/defaultdb?ssl-mode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_uflhxUVOijRksWtK82R";


    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, ArrayList<ArrayList<String>>> {
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... voids) {
            vehicaldata_.clear();
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {


                String sql = "SELECT name, imageurl, seat,status, renter ,price FROM vehical";
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

                    if(resultSet.getString("status").equals("0")){
                    vehicaldata_.add(info);}

                    System.out.println(resultSet.getString("name"));

                }
            } catch (Exception e) {
                Log.e("InfoAsyncTask", "Error reading school information", e);
            }

            return vehicaldata_;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            System.out.println(result.size());
            progressBar.setVisibility(View.GONE);
            if (!result.isEmpty()) {
                System.out.println(result.get(0));
                adpter_.notifyItemInserted(result.size()-1);

            }

            else{
                Emptymessage.setVisibility(View.VISIBLE);

            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // GalleryViewModel galleryViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(GalleryViewModel.class);
        vehicaldata_.clear();
        new  HomeFragment.InfoAsyncTask().execute();

        System.out.println("In hooome frgment");
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();




        progressBar = root.findViewById(R.id.loadingProgressBarRentcar);
        Emptymessage = root.findViewById(R.id.noVehiclesTextView);

        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = root.findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adpter_ = new ProductItemView(vehicaldata_ ,progressBar);
        recyclerView.setAdapter(adpter_);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}