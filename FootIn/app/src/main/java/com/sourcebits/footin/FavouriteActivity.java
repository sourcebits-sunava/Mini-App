package com.sourcebits.footin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

public class FavouriteActivity extends Activity {

    private Toolbar toolbar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favouriteactivity);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setLogo(R.drawable.ic_messaging_like_icon_1);
        setActionBar(toolbar);


        PlaceDatabaseHelper db = new PlaceDatabaseHelper(this);

        db.insertData("Kolkata");
        db.insertData("Delhi");
        db.insertData("Bengaluru");

        listView = (ListView) findViewById(R.id.list_item_database);
        Cursor c = db.getAllData();
        CustomAdapter adapter = new CustomAdapter(this, c);
        listView.setAdapter(adapter);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatbt);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                setResult(RESULT_OK,intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search)
        {
            return true;


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
