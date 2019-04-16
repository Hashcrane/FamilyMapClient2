package larso12.familymap.UserIterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import larso12.familymap.R;

public class FilterActivity extends AppCompatActivity {

    private Cache cache = Cache.getInstance();
    private RecyclerView recyclerView;
    private FilterAdapter adapter;
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.filterActivityLabel);

        recyclerView = view.findViewById(R.id.filterRecyclerView);

        if (adapter == null) {
            adapter = new FilterAdapter(cache.getAllEventTypes());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cache.setCurrentDisplayedEvents();
        //FIXME check this is getting called when leaving the activity through the up button
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterViewHolder> {

        private final ArrayList<FilterData> list;

        FilterAdapter(Map<String, FilterData> map) {
            list = new ArrayList<>();
            for (Map.Entry<String, FilterData> entry: map.entrySet()){
                list.add(entry.getValue());
            }
        }

        @NonNull
        @Override
        public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new FilterViewHolder(inflater, viewGroup);
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

        FilterViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.filter_group_view, parent, false));
            filterText = itemView.findViewById(R.id.filterGroupTextTop);
            filterSwitch = itemView.findViewById(R.id.filterGroupSwitch);
        }

        void bindItem(final FilterData data){
            filterText.setText(data.getEventType());
            filterSwitch.setChecked(data.isShow());
            filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    cache.getAllEventTypes().get(data.getEventType()).setShow(isChecked);
                }
            });
        }
    }
}
