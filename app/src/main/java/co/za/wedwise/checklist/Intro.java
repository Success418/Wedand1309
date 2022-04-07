package co.za.wedwise.checklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;


import co.za.wedwise.R;
import co.za.wedwise.checklist.checklist.Topics;
import co.za.wedwise.checklist.dashboard.DashboardBudgets;
import co.za.wedwise.checklist.dashboard.DashboardSchedules;
import co.za.wedwise.checklist.dashboard.DashboardVendors;
import co.za.wedwise.checklist.guestlist.Guests;
import co.za.wedwise.checklist.help.Help;
import java.io.File;

public class Intro extends Activity {
    Activity act = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.intro, menu);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        clearApplicationCache(null);
    }

    public void clearApplicationCache(File dir) {
        if (dir == null) {
            dir = getCacheDir();
        }
        if (dir != null) {
            File[] children = dir.listFiles();
            int i = 0;
            while (i < children.length) {
                try {
                    if (children[i].isDirectory()) {
                        clearApplicationCache(children[i]);
                    } else {
                        children[i].delete();
                    }
                    i++;
                } catch (Exception e) {
                    return;
                }
            }
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finishApp(null);
        }
        return true;
    }

    public void finishApp(View view) {
        finish();
    }

    public void gotoTopics(View v) {
        this.act.startActivity(new Intent(this.act, Topics.class));
    }

    public void gotoGuests(View v) {
        this.act.startActivity(new Intent(this.act, Guests.class));
    }

    public void gotoDashboardBudgets(View v) {
        this.act.startActivity(new Intent(this.act, DashboardBudgets.class));
    }

    public void gotoDashboardSchedules(View v) {
        this.act.startActivity(new Intent(this.act, DashboardSchedules.class));
    }

    public void gotoDashboardVendors(View v) {
        this.act.startActivity(new Intent(this.act, DashboardVendors.class));
    }

    public void gotoHelp(View v) {
        this.act.startActivity(new Intent(this.act, Help.class));
    }
}
