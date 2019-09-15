package com.example.codex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Displays all the notes for the corresponding dept and semester
 * onItemClickListener() setup to download that file
 */
public class BuyItems extends AppCompatActivity {

    String TAG = "NotesListAndDownload", mPdfId;
    String deptSem;

    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

    ListView recyclerView;
    ItemListAdapter adapter;
    ArrayList<ItemInfo> itemsList = new ArrayList<>();
    FloatingActionButton fab;
    TextView emptyView1, emptyView2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sell, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.sell){
            startActivity(new Intent(BuyItems.this, SellItems.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the title
     * Makes the query to display all available notes
     * Initiates the uploading and downloading processes based on the user's actions
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        sharedPreferences = getSharedPreferences("gstInfo", MODE_PRIVATE);

        emptyView1 = findViewById(R.id.empty_view_1);
        emptyView2 = findViewById(R.id.empty_view_2);

        // Sets up the recycler view with the custom adapter
        recyclerView = findViewById(R.id.item_rv);
        adapter = new ItemListAdapter(this, itemsList);
        recyclerView.setAdapter(adapter);

        // Letting users know about the Upload button
        Toast.makeText(BuyItems.this, "Hit the Sell button in the bottom-right corner of the screen to sell stuff", Toast.LENGTH_LONG).show();

        // Checks for internet connection
        if(isNetworkAvailable()) {
            /*
                A query is made to Firebase database to fetch only those files corresponding to the
                dept and semester
             */
            Query query = databaseReference.orderByChild("deptSem").equalTo(deptSem);
            query.addListenerForSingleValueEvent(valueEventListener);
        }
        else
            Toast.makeText(BuyItems.this, "No network!!", Toast.LENGTH_SHORT).show();

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, sharedPreferences.getString("email", "svenkatesh525@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Interest in your product");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        /*
        // FAB button to initiate the upload process
        fab = findViewById(R.id.sell_items);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyItems.this, SellItems.class);
                startActivity(intent);
            }
        });
         */
    }

    /**
     * This is the one which actually fetches the files' info corresponding to the dept and semester
     * and stores it in an arraylist
     */
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            itemsList.clear();

            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String itemName = snapshot.child("name").getValue(String.class);
                    String itemDesc = snapshot.child("description").getValue(String.class);
                    String itemPrice = snapshot.child("price").getValue(String.class);
                    String itemId = snapshot.child("itemId").getValue(String.class);
                    String contact = snapshot.child("phone").getValue(String.class);

                    ItemInfo itemInfo = new ItemInfo(itemName, itemDesc, itemPrice, itemId, contact);
                    itemsList.add(itemInfo);
                }
            }

            if(itemsList.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                emptyView1.setVisibility(View.VISIBLE);
                emptyView2.setVisibility(View.VISIBLE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                emptyView1.setVisibility(View.GONE);
                emptyView2.setVisibility(View.GONE);

                // The custom adapter is notified of the new data set in the array list
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    /**
     * A helper method to check for internet connection
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
