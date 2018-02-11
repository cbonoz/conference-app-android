package com.mentalmachines.droidcon_boston.views.agenda;


import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mentalmachines.droidcon_boston.R;
import com.mentalmachines.droidcon_boston.data.ScheduleDatabase;
import com.mentalmachines.droidcon_boston.utils.StringUtils;
import com.mentalmachines.droidcon_boston.views.detail.AgendaDetailFragment;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment for an agenda day
 */
public class AgendaDayFragment extends Fragment {

  private static final String ARG_DAY = "day";

  @BindView(R.id.recycler)
  RecyclerView recycler;

  private String dayFilter;

  private Map<String, ScheduleAdapterItemHeader> timeHeaders = new HashMap<>();

  public static AgendaDayFragment newInstance(String day) {
    AgendaDayFragment fragment = new AgendaDayFragment();
    Bundle args = new Bundle();
    args.putString(ARG_DAY, day);
    fragment.setArguments(args);
    return fragment;
  }

  public AgendaDayFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      dayFilter = getArguments().getString(ARG_DAY);
    }


  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.agenda_day_fragment, container, false);
    ButterKnife.bind(this, view);

    recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    setupHeaderAdapter();

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // onBackPressed();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
          fragmentManager.popBackStack();
        }
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupHeaderAdapter() {
    List<ScheduleDatabase.ScheduleRow> rows = ScheduleDatabase
        .fetchScheduleListByDay(getActivity().getApplicationContext(), dayFilter);
    List<ScheduleAdapterItem> items = new ArrayList<>(rows.size());
    for (ScheduleDatabase.ScheduleRow row : rows) {
      String timeDisplay = ((row.time == null) || (row.time.length() == 0)) ? "Unscheduled" : row.time;
      ScheduleAdapterItemHeader header = timeHeaders.get(timeDisplay);
      if (header == null) {
        header = new ScheduleAdapterItemHeader(timeDisplay);
        timeHeaders.put(timeDisplay, header);
      }

      ScheduleAdapterItem item = new ScheduleAdapterItem(row, header);
      items.add(item);
    }
    Collections.sort(items, (s1, s2) -> {
      int timeComparison = s1.getStartTime().compareTo(s2.getStartTime());
      if (timeComparison != 0) {
        return timeComparison;
      }
      return s1.getRoomSortOrder().compareTo(s2.getRoomSortOrder());
    });

    FlexibleAdapter.enableLogs(true);
    FlexibleAdapter<ScheduleAdapterItem> headerAdapter =
        new FlexibleAdapter<>(items,
            (FlexibleAdapter.OnItemClickListener) position -> {
              Object listItem = items.get(position);
              //noinspection ConstantConditions
              if (listItem instanceof ScheduleAdapterItem) {
                ScheduleAdapterItem item = (ScheduleAdapterItem) listItem;
                if (StringUtils.isNullorEmpty(item.getItemData().speakerName)) {
                  String url = item.getItemData().photo;
                  if (item.getItemData().photo == null) {
                    return false;
                  }
                  // event where info URL is in the photo string
                  Intent i = new Intent(Intent.ACTION_VIEW);
                  i.setData(Uri.parse(url));
                  PackageManager packageManager = getActivity().getPackageManager();
                  if (i.resolveActivity(packageManager) != null) {
                    startActivity(i);
                  }
                  return false;
                }
                Bundle arguments = new Bundle();
                arguments.putString(ScheduleDatabase.NAME, item.getItemData().speakerName);
                arguments.putString(ScheduleDatabase.TALK_TIME, item.getItemData().time);
                arguments.putString(ScheduleDatabase.ROOM, item.getItemData().room);

                AgendaDetailFragment agendaDetailFragment = new AgendaDetailFragment();
                agendaDetailFragment.setArguments(arguments);

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, agendaDetailFragment)
                    .addToBackStack(null)
                    .commit();
              }

              return true;
            });
    headerAdapter
        .expandItemsAtStartUp()
        .setDisplayHeadersAtStartUp(true)
        .setStickyHeaders(true);
    recycler.setAdapter(headerAdapter);
  }
}
