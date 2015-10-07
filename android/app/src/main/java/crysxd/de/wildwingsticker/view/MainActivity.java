package crysxd.de.wildwingsticker.view;

import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import crysxd.de.wildwingsticker.R;
import crysxd.de.wildwingsticker.model.WwGameReportHolder;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mEventList;
    private RecyclerView.Adapter mEventListAdapter;
    private RecyclerView.LayoutManager mEventListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setSupportActionBar((Toolbar) this.findViewById(R.id.toolbar));
        this.getSupportActionBar().setTitle("");

        this.mEventList = (RecyclerView) this.findViewById(R.id.event_list);
        this.mEventList.setHasFixedSize(true);
        this.mEventListLayout = new LinearLayoutManager(this);
        this.mEventList.setLayoutManager(this.mEventListLayout);
        this.mEventListAdapter = new WwGameReportAdapter(WwGameReportHolder.i(this));
        this.mEventList.setAdapter(this.mEventListAdapter);

        CollapsingToolbarLayout cl = (CollapsingToolbarLayout) this.findViewById(R.id.toolbar_layout);
        cl.setTitle("SWW 6:5 ERCI");
        cl.setExpandedTitleColor(Color.argb(255, 40, 40, 40));

    }


}
