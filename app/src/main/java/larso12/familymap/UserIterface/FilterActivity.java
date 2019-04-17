package larso12.familymap.UserIterface;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import larso12.familymap.Model.Cache;
import larso12.familymap.Model.FilterData;
import larso12.familymap.Model.SettingsCache;
import larso12.familymap.R;

public class FilterActivity extends AppCompatActivity {

    private Cache cache = Cache.getInstance();
    private SettingsCache sCache = SettingsCache.getCache();
    private RecyclerView recyclerView;
    private FilterAdapter adapter;
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.filterActivityLabel);

        recyclerView = findViewById(R.id.filterRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (adapter == null) {
            adapter = new FilterAdapter(this, cache.getAllEventTypes());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        cache.sortAll();
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder> {

        private final ArrayList<FilterData> list;
        private LayoutInflater inflater;

        FilterAdapter(Context context, Map<String, FilterData> map) {
            this.inflater = LayoutInflater.from(context);
            list = new ArrayList<>();
            for (Map.Entry<String, FilterData> entry: map.entrySet()){
                list.add(entry.getValue());
            }
        }

        @NonNull
        @Override
        public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.filter_group_view, viewGroup, false);

            return new FilterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FilterViewHolder filterViewHolder, int i) {
            FilterData data = list.get(i);
            filterViewHolder.bindItem(data);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class FilterViewHolder extends RecyclerView.ViewHolder {

        private final TextView filterText;
        private final Switch filterSwitch;

        FilterViewHolder(View view) {
            super(view);
            filterText = itemView.findViewById(R.id.filterGroupTextTop);
            filterSwitch = itemView.findViewById(R.id.filterGroupSwitch);
        }

        void bindItem(final FilterData data){
            filterText.setText(data.getEventType().toUpperCase());
            if (data.getEventType().toLowerCase().contains("marriage")) {
                filterSwitch.setChecked(sCache.isShowMarriageEvents());
            } else if (data.getEventType().toLowerCase().contains("death")) {
                filterSwitch.setChecked(sCache.isShowDeathEvents());
            } else if(data.getEventType().toLowerCase().contains("birth")) {
                filterSwitch.setChecked(sCache.isShowBirthEvents());
            } else if (data.getEventType().toLowerCase().contains("female")){
                filterSwitch.setChecked(sCache.isShowFemaleEvents());
            } else if (data.getEventType().toLowerCase().contains("male events")) {
                filterSwitch.setChecked(sCache.isShowMaleEvents());
            } else if (data.getEventType().toLowerCase().contains("father")) {
                filterSwitch.setChecked(sCache.isShowFatherSide());
            } else if (data.getEventType().toLowerCase().contains("mother")) {
                filterSwitch.setChecked(sCache.isShowMotherSide());
            } else {
                filterSwitch.setChecked(data.isShow());
            }

            filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (data.getEventType().toLowerCase().contains("marriage")) {
                        sCache.setShowMarriageEvents(buttonView.isChecked());
                    } else if (data.getEventType().toLowerCase().contains("death")) {
                        sCache.setShowDeathEvents(buttonView.isChecked());
                    } else if(data.getEventType().toLowerCase().contains("birth")) {
                        sCache.setShowBirthEvents(buttonView.isChecked());
                    } else if (data.getEventType().toLowerCase().contains("male events")) {
                        sCache.setShowMaleEvents(buttonView.isChecked());
                    } else if (data.getEventType().toLowerCase().contains("female")){
                        sCache.setShowFemaleEvents(buttonView.isChecked());
                    } else if (data.getEventType().toLowerCase().contains("father")) {
                        sCache.setShowFatherSide(buttonView.isChecked());
                    } else if (data.getEventType().toLowerCase().contains("mother")) {
                        sCache.setShowMotherSide(buttonView.isChecked());
                    } else {
                        if (data.getEventType() == null) {
                            Log.e("Filter", "data is null");
                        }
                        cache.getAllEventTypes().get(data.getEventType().toLowerCase()).setShow(buttonView.isChecked());
                    }
                }
            });
        }
    }
}
