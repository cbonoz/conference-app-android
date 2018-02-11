package com.mentalmachines.droidcon_boston.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.mentalmachines.droidcon_boston.R;
import com.mentalmachines.droidcon_boston.data.ScheduleDatabase;
import java.util.HashMap;

public class FAQFragment extends Fragment {

  public class FaqExpandable extends BaseExpandableListAdapter {

    final HashMap<Integer, ScheduleDatabase.FaqData[]> childrens;

    final String[] questionsGroup;

    public FaqExpandable() {
      final Context ctx = getContext();
      questionsGroup = ScheduleDatabase.fetchQuestions(ctx);
      childrens = ScheduleDatabase.makeAnswers(ctx, questionsGroup);
    }

    @Override
    public Object getChild(int groupId, int dex) {
      return childrens.get(groupId)[dex];
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
      return groupPosition * 100 + childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
        ViewGroup parent) {
      final ScheduleDatabase.FaqData item = childrens.get(groupPosition)[childPosition];
      final Context ctx = getContext();
      if (convertView == null) {
        convertView = LayoutInflater.from(ctx).inflate(R.layout.faq_item, null);
        convertView.setTag(convertView.findViewById(R.id.q_top));
        convertView.setTag(R.id.q_photo_row, convertView.findViewById(R.id.q_photo_row));
        convertView.setTag(R.id.q_button_row, convertView.findViewById(R.id.q_button_row));
      }
      if (childPosition == 0) {
        ((View) convertView.getTag()).setVisibility(View.GONE);
      } else {
        ((View) convertView.getTag()).setVisibility(View.VISIBLE);
      }

      ((TextView) convertView.findViewById(R.id.q_text)).setText(item.answer);
      if (TextUtils.isEmpty(item.photoUrl) && TextUtils.isEmpty(item.mapCoords) &&
          TextUtils.isEmpty(item.bizLink)) {
        return convertView;
      }
      final ImageButton more, map;
      LinearLayout photoLayout = null;
      final LinearLayout buttonRow = (LinearLayout) convertView.getTag(R.id.q_button_row);
      if (!TextUtils.isEmpty(item.photoUrl)) {
        buttonRow.setVisibility(View.GONE);
        photoLayout = (LinearLayout) convertView.getTag(R.id.q_photo_row);
        photoLayout.setVisibility(View.VISIBLE);
        Glide.with(ctx)
            .load(item.photoUrl)
            .override(600, 600)
            .centerCrop()
            .into((ImageView) photoLayout.findViewById(R.id.q_photo));
        photoLayout = photoLayout.findViewById(R.id.q_button_col);
        more = photoLayout.findViewById(R.id.q_more_p);
        map = photoLayout.findViewById(R.id.q_map_p);
      } else {
        ((View) convertView.getTag(R.id.q_photo_row)).setVisibility(View.GONE);
        Log.i(TAG, "no photo " + childPosition);
        more = buttonRow.findViewById(R.id.q_more);
        map = buttonRow.findViewById(R.id.q_map);
      }

      //item may not have either bizLink or mapCoords or both
      if (TextUtils.isEmpty(item.bizLink) && TextUtils.isEmpty(item.mapCoords)) {
        Log.d(TAG, "hide buttons");
        if (photoLayout == null) {
          buttonRow.setVisibility(View.GONE);
        } else {
          photoLayout.setVisibility(View.GONE);
        }
      } else {
        //has either geo or biz link to set data on a view intent
        Log.d(TAG, "show buttons");
        if (photoLayout != null) {
          //this is the row of buttons now
          photoLayout.setVisibility(View.VISIBLE);
        } else {
          buttonRow.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(item.mapCoords)) {
          map.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(item.bizLink)) {
          more.setVisibility(View.GONE);
        }
      }
      return convertView;
    }

    @Override
    public int getChildrenCount(int position) {
      return childrens.get(position) == null ? 0 : childrens.get(position).length;
    }

    @Override
    public Object getGroup(int position) {
      return childrens.get(position);
    }

    @Override
    public int getGroupCount() {
      return questionsGroup == null ? 0 : questionsGroup.length;
    }

    @Override
    public long getGroupId(int position) {
      return position;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.faq_header, null);
      }
      ((TextView) convertView.findViewById(R.id.question_text)).setText(questionsGroup[groupPosition]);
      return convertView;
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
      return true;
    }
  }

  public static final String TAG = "FAQFragment";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    final View view = inflater.inflate(R.layout.faq_fragment, container, false);
    ((ExpandableListView) view.findViewById(R.id.faqlist)).setAdapter(new FaqExpandable());
    return view;
  }

}
