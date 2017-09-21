package lib.view.bottom.bottomviewlib;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import lib.view.bottomview.BottomView;

public class ScrollingActivity extends AppCompatActivity {

    BottomView bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        bottomView = (BottomView) findViewById(R.id.bv_footer);
        showBottomView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showBottomView() {
        try {
            bottomView.removeAllViews();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bottomView.setElevation(
                        getResources().getDimension(R.dimen.bottom_view_elevation));
            }
            bottomView.setBehaviorTranslationEnabled(true);
            View v =
                    LayoutInflater.from(this).inflate(R.layout.bottom_layout, this.bottomView, false);
            bottomView.addView(v);
            int height = (int) getResources().getDimension(R.dimen.bottom_view_height);
            bottomView.getLayoutParams().height = height;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
