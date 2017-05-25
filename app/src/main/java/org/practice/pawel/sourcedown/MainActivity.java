package org.practice.pawel.sourcedown;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Helper.displayAlert(this, getString(R.string.action_about), getString(R.string.about_content));
        }
        if (id == R.id.action_dropcache) {
            DBHandler db = new DBHandler(this);
            db.dropDatabase();
            Helper.displayAlert(this, getString(R.string.alert_info), getString(R.string.alert_cacheclean));
        }

        return super.onOptionsItemSelected(item);
    }
}