package com.example.revzan.androidproductcatalog;

import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainActivity extends AppCompatActivity {
    private GridView mGridView;
    private ProductAdapter mAdapter;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      Custom App Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_customheader);
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }
    @Override
    public void onResume() {
        super.onResume();

        // Load from file "cities.json" first time
        if(mAdapter == null) {
            List<Product> products = null;
            try {
                products = loadProducts();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //This is the GridView adapter
            mAdapter = new ProductAdapter(this);
            mAdapter.setData(products);

            //This is the GridView which will display the list of cities
            mGridView = (GridView) findViewById(R.id.productList);
            mGridView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mGridView.invalidate();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    public List<Product> loadProducts() throws IOException {
        loadJsonFromStream();
        return realm.where(Product.class).findAll();
    }
    private void loadJsonFromStream() throws IOException {
        // Use streams if you are worried about the size of the JSON whether it was persisted on disk
        // or received from the network.
        InputStream stream = getAssets().open("products.json");

        // Open a transaction to store items into the realm
        realm.beginTransaction();
        try {
            realm.createAllFromJson(Product.class, stream);
            realm.commitTransaction();
        } catch (IOException e) {
            // Remember to cancel the transaction if anything goes wrong.
            realm.cancelTransaction();
            Log.e("REALM", "nOT WORKIINGGG");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
