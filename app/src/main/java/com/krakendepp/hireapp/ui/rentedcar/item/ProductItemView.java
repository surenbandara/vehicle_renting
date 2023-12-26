package com.krakendepp.hireapp.ui.rentedcar.item;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.krakendepp.hireapp.R;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class ProductItemView extends  RecyclerView.Adapter<DemoVH>{
    ArrayList<ArrayList<String>> items;
    ProgressBar progressBar;

    public ProductItemView(ArrayList<ArrayList<String>> items ,ProgressBar progressBar){
        this.items = items;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public DemoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rented_item ,parent ,false);
        return new DemoVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoVH holder, int position) {

        holder.Setdata(items.get(position) , progressBar ,items);
        holder.name.setText(items.get(position).get(0));
        holder.price.setText(items.get(position).get(5)+"$");
        holder.period.setText(items.get(position).get(6));

        System.out.println(items.get(position).get(3));
        if(items.get(position).get(3).equals("1")){
            holder.status.setText("Processing");
        }

        else{
            holder.status.setText("Booked");

        }
        String url = items.get(position).get(1);
        Picasso.get().load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

class DemoVH extends RecyclerView.ViewHolder {

    private ProgressBar progressBar;
    ArrayList<ArrayList<String>> items;

    TextView status,price ,name , period;
    ImageView imageView;
    private ProductItemView adapter;
    private ArrayList<String> data;


    private static final String URL = "jdbc:mysql://sql7.freemysqlhosting.net/sql7617117";
    private static final String USER = "sql7617117";
    private static final String PASSWORD = "h8xTHz8dSK";

    @SuppressLint("ResourceType")
    public DemoVH(@NonNull View view_) {

        super(view_);


        this.imageView = view_.findViewById(R.id.pic);
        this.price = view_.findViewById(R.id.price);
        this.status = view_.findViewById(R.id.status);
        this.name = view_.findViewById(R.id.name);
        this.period = view_.findViewById(R.id.period);


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view_.getContext());
        bottomSheetDialog.setContentView(R.layout.popupfor_rentedcar);




        bottomSheetDialog.findViewById(R.id.close).setOnClickListener(view ->{
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.findViewById(R.id.cancel_rentedcar).setOnClickListener(view ->{
            bottomSheetDialog.dismiss();
            progressBar.setVisibility(View.VISIBLE);
            new InfoAsyncTask().execute();
        });


        view_.findViewById(R.id.cancle_order).setOnClickListener(view ->{
            bottomSheetDialog.show();
        });

    }



    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                String updateSql = "UPDATE vehical SET status = ? WHERE name = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, "0"); // set status to "1"
                updateStatement.setString(2, data.get(0)); // set name to "vehical"
                updateStatement.executeUpdate();
            } catch (Exception e) {

                Log.e("InfoAsyncTask", "Error reading school information", e);
                return false;
            }

            return true ;


        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                int position = getAdapterPosition();
                items.remove(position);
                adapter.notifyItemRemoved(position);


            }

            progressBar.setVisibility(View.GONE);


        }
    }



    public void Setdata(ArrayList<String> data ,ProgressBar progressBar ,ArrayList<ArrayList<String>> items){
        this.data = data;
        this.items =  items;
        this.progressBar = progressBar;
    }
    public DemoVH linkAdapter(ProductItemView adapter){
        this.adapter = adapter;
        return this;
    }


}
