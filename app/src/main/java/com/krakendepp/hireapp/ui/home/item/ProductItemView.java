package com.krakendepp.hireapp.ui.home.item;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_item,parent ,false);
        return new DemoVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoVH holder, int position) {

        holder.Setdata(items.get(position) , progressBar ,items);
        holder.name.setText(items.get(position).get(0));
        holder.seat.setText(items.get(position).get(2));
        holder.price.setText(items.get(position).get(5)+"$");

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

    TextView seat,price ,name;
    ImageView imageView;
    private  ProductItemView  adapter;
    private ArrayList<String> data;
    private int  year,month,day,offset;
    private Calendar calendar = Calendar.getInstance();
    private TextView enddate;
    private int Corrected_month;

    private static final String URL = "jdbc:mysql://sql7.freemysqlhosting.net/sql7617117";
    private static final String USER = "sql7617117";
    private static final String PASSWORD = "h8xTHz8dSK";

    @SuppressLint("ResourceType")
    public DemoVH(@NonNull View view_) {

        super(view_);


        this.imageView = view_.findViewById(R.id.pic);
        this.price = view_.findViewById(R.id.price);
        this.seat = view_.findViewById(R.id.seat);
        this.name = view_.findViewById(R.id.name);


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view_.getContext());
        bottomSheetDialog.setContentView(R.layout.popupfor_rentacar);



        TextView totalpay = bottomSheetDialog.findViewById(R.id.totaltopay);


        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker startdate=bottomSheetDialog.findViewById(R.id.startdate);
        startdate.setMinDate(calendar.getTimeInMillis());


        startdate.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                // Here, you can get the selected date
                updateDate(year,month,dayOfMonth);



            }
        });


        EditText setnumberofdays = bottomSheetDialog.findViewById(R.id.numberofdays);
        setnumberofdays.setText("1");

        enddate =  bottomSheetDialog.findViewById(R.id.enddate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Corrected_month = calendar.get(Calendar.MONTH)+1;
        enddate.setText(calendar.get(Calendar.YEAR)+"/"+Corrected_month+"/"+calendar.get(Calendar.DAY_OF_MONTH));


        setnumberofdays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = setnumberofdays.getText().toString();

                if(inputText.equals("")){
                    offset=0;
                }
                else {
                    offset = Integer.parseInt(inputText);
                            }

                calendar.set(year, month, day);
                calendar.add(Calendar.DAY_OF_MONTH, offset);

                totalpay.setText(Integer.parseInt(data.get(5)) * offset + "$");
                Corrected_month = calendar.get(Calendar.MONTH)+1;
                enddate.setText(calendar.get(Calendar.YEAR) + "/" + Corrected_month+ "/" + calendar.get(Calendar.DAY_OF_MONTH));
            }
        });




        bottomSheetDialog.findViewById(R.id.cancel_rentaacar).setOnClickListener(view ->{
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.findViewById(R.id.send).setOnClickListener(view ->{
            bottomSheetDialog.dismiss();
            //progressBar.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.progress_bg));
            progressBar.setVisibility(View.VISIBLE);
            new InfoAsyncTask().execute();


        });


        view_.findViewById(R.id.book).setOnClickListener(view ->{
            totalpay.setText(data.get(5)+"$");
            bottomSheetDialog.show();
        });

    }



    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

                String updateSql = "UPDATE vehical SET status = ? ,timeperiod = ? WHERE name = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);

                calendar.set(year, month,day);
                calendar.add(Calendar.DAY_OF_MONTH, offset);

                Corrected_month = calendar.get(Calendar.MONTH)+1;

                updateStatement.setString(1, "1"); // set status to "1"
                updateStatement.setString(2, year+"/"+month+"/"+day+" - "+calendar.get(Calendar.YEAR)+"/"+Corrected_month+"/"+calendar.get(Calendar.DAY_OF_MONTH));
                updateStatement.setString(3, data.get(0)); // set name to "vehical"
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


    public void updateDate(int year,int month,int day){

        calendar.set(year, month,day);
        calendar.add(Calendar.DAY_OF_MONTH, offset);

        Corrected_month = calendar.get(Calendar.MONTH)+1;
        enddate.setText(calendar.get(Calendar.YEAR)+"/"+Corrected_month+"/"+calendar.get(Calendar.DAY_OF_MONTH));


        this.year= year;
        this.month= month;
        this.day= day ;
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
