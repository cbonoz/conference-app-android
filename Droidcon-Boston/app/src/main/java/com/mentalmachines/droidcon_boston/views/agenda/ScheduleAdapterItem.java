package com.mentalmachines.droidcon_boston.views.agenda;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.mentalmachines.droidcon_boston.R;
import com.mentalmachines.droidcon_boston.data.ScheduleDatabase;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Used for displaying the schedule with sticky headers with optional day filtering
 */
public class ScheduleAdapterItem extends
        AbstractSectionableItem<ScheduleAdapterItem.ViewHolder, ScheduleAdapterItemHeader> {

    private ScheduleDatabase.ScheduleRow itemData;

    private Date startTime;

    private Integer roomOrder;

    public ScheduleDatabase.ScheduleRow getItemData() {
        return itemData;
    }

    public ScheduleAdapterItem(ScheduleDatabase.ScheduleRow scheduleRow,
            ScheduleAdapterItemHeader header) {
        super(header);
        this.itemData = scheduleRow;

        String dateTimeString = scheduleRow.date + " " + scheduleRow.time;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);
        try {
            startTime = format.parse(dateTimeString);
        } catch (ParseException e) {
            Log.e("ScheduleAdapterItem", "Parse error: " + e + " for " + dateTimeString);
        }

        if ("THEATER 1".equals(scheduleRow.room)) {
            roomOrder = 1;
        } else if ("THEATER 2".equals(scheduleRow.room)) {
            roomOrder = 2;
        } else if ("CYCLORAMA".equals(scheduleRow.room)) {
            roomOrder = 3;
        }
    }

    public String getTitle() {
        return itemData.talkTitle;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Integer getRoomSortOrder() {
        return roomOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScheduleAdapterItem) {
            ScheduleAdapterItem inItem = (ScheduleAdapterItem) o;
            return this.itemData.talkTitle.equals(inItem.itemData.talkTitle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return itemData.talkTitle.hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.schedule_item;
    }

    @Override
    public ScheduleAdapterItem.ViewHolder createViewHolder(FlexibleAdapter adapter,
            LayoutInflater inflater,
            ViewGroup parent) {
        return new ScheduleAdapterItem.ViewHolder(
                inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter,
            ScheduleAdapterItem.ViewHolder holder,
            int position,
            List payloads) {

        if (itemData.speakerName == null) {
            holder.avatar.setVisibility(View.GONE);
            holder.timeText.setVisibility(View.VISIBLE);

            holder.timeText.setText(itemData.talkTitle);

            if (itemData.photo == null) {
                holder.rootLayout.setBackground(null);
            } else {
                addBackgroundRipple(holder);
            }
        } else {
            holder.avatar.setVisibility(View.VISIBLE);
            holder.timeText.setVisibility(View.GONE);

            holder.title.setText(itemData.talkTitle);
            holder.speaker.setText(itemData.speakerName);
            holder.room.setText(itemData.room);

            Context context = holder.title.getContext();
            Glide.with(context)
                    .load(itemData.photo)
                    .transform(new CircleTransform(context))
                    .crossFade()
                    .into(holder.avatar);

            addBackgroundRipple(holder);
        }
    }

    private void addBackgroundRipple(ViewHolder holder) {
        TypedValue outValue = new TypedValue();
        Context context = holder.title.getContext();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        holder.rootLayout.setBackgroundResource(outValue.resourceId);
    }


    static class ViewHolder extends FlexibleViewHolder {

        View rootLayout;

        ImageView avatar;

        TextView title;

        TextView speaker;

        TextView room;

        TextView timeText;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            findViews(view);
        }

        public ViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
            super(view, adapter, stickyHeader);
            findViews(view);
        }

        private void findViews(View parent) {
            rootLayout = parent.findViewById(R.id.rootLayout);
            avatar = parent.findViewById(R.id.speaker_image);
            title = parent.findViewById(R.id.title_text);
            speaker = parent.findViewById(R.id.speaker_name_text);
            room = parent.findViewById(R.id.room_text);
            timeText = parent.findViewById(R.id.time_text);
        }
    }
}
