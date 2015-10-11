package crysxd.de.wildwingsticker.view;

import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import crysxd.de.wildwingsticker.R;
import crysxd.de.wildwingsticker.model.WwGameReport;
import crysxd.de.wildwingsticker.model.WwGameReportHolder;
import crysxd.de.wildwingsticker.server.WwTeamImageLoadTask;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mEventList;
    private RecyclerView.Adapter mEventListAdapter;
    private RecyclerView.LayoutManager mEventListLayout;

    private ImageView mImageViewTeamHome;
    private ImageView mImageViewTeamGuest;

    private CollapsingToolbarLayout mToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setSupportActionBar((Toolbar) this.findViewById(R.id.toolbar));
        this.getSupportActionBar().setTitle("");

        this.mImageViewTeamGuest = (ImageView) this.findViewById(R.id.imageViewTeamGuest);
        this.mImageViewTeamHome = (ImageView) this.findViewById(R.id.imageViewTeamHome);

        this.mEventList = (RecyclerView) this.findViewById(R.id.event_list);
        this.mEventList.setHasFixedSize(true);
        this.mEventListLayout = new LinearLayoutManager(this);
        this.mEventList.setLayoutManager(this.mEventListLayout);
        this.mEventListAdapter = new WwGameReportAdapter(WwGameReportHolder.i(this));
        this.mEventList.setAdapter(this.mEventListAdapter);

        this.mToolbarLayout = (CollapsingToolbarLayout) this.findViewById(R.id.toolbar_layout);
        this.mToolbarLayout.setTitle("0:0");
        this.mToolbarLayout.setExpandedTitleColor(Color.argb(255, 40, 40, 40));

        WwGameReport report = WwGameReportHolder.i(this);
        if(report != null) {
            new WwTeamImageLoadTask(this.mImageViewTeamGuest).execute(report.getGuestName());
            new WwTeamImageLoadTask(this.mImageViewTeamHome).execute(report.getHomeName());

            this.updateTitle(report);

        }
    }

    private void updateTitle(WwGameReport report) {
        String home = report.getHomeNameShort() != null ? report.getHomeNameShort() : "";
        String guest = report.getGuestNameShort() != null ? report.getGuestNameShort() : "";

        String title = home + " " + report.getGoalsHome() + ":" + report.getGoalsGuest() + " " + guest;
        this.mToolbarLayout.setTitle(title);

    }
}
