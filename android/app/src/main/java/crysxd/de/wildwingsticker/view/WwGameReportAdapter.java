package crysxd.de.wildwingsticker.view;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import crysxd.de.wildwingsticker.R;
import crysxd.de.wildwingsticker.model.WwGameEvent;
import crysxd.de.wildwingsticker.model.WwGameReport;
import crysxd.de.wildwingsticker.model.WwGoalGameEvent;
import crysxd.de.wildwingsticker.model.WwPenaltyGameEvent;
import crysxd.de.wildwingsticker.model.WwPlayerCausedGameEvent;
import crysxd.de.wildwingsticker.model.WwTextGameEvent;
import crysxd.de.wildwingsticker.server.WwGoalImageLoadTask;
import crysxd.de.wildwingsticker.server.WwImageLoadTask;
import crysxd.de.wildwingsticker.server.WwPlayerImageLoadTask;
import crysxd.de.wildwingsticker.server.WwTeamImageLoadTask;

/**
 * Created by cwuer on 10/5/15.
 */
public class WwGameReportAdapter extends RecyclerView.Adapter<WwGameReportAdapter.ViewHolder> {

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewTitle;
        public TextView mTextViewTime;

        public ViewHolder(View itemView) {
            super(itemView);

            this.mTextViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.mTextViewTime = (TextView) itemView.findViewById(R.id.textViewTime);

        }
    }

    public static class TextViewHolder extends ViewHolder {

        public TextView mTextViewText;

        public TextViewHolder(View itemView) {
            super(itemView);

            this.mTextViewText = (TextView) itemView.findViewById(R.id.textViewText);

        }
    }

    public static class PenaltyViewHolder extends TextViewHolder {

        public AsyncTask mImageLoadTask;
        public Button mButtonPlayerStats;
        public TextView mTextViewPlayer;

        public PenaltyViewHolder(View itemView) {
            super(itemView);

            this.mButtonPlayerStats = (Button) itemView.findViewById(R.id.buttonPlayerStats);
            this.mTextViewPlayer = (TextView) itemView.findViewById(R.id.textViewPlayer);

        }
    }

    public static class GoalViewHolder extends PenaltyViewHolder {

        public Button mButtonShare;
        public ImageView mImageView;

        public GoalViewHolder(View itemView) {
            super(itemView);

            this.mButtonShare = (Button) itemView.findViewById(R.id.buttonShare);
            this.mImageView = (ImageView) itemView.findViewById(R.id.imageView);

        }
    }



    /*
     * =============================================================================================
     */

    private WwGameReport mDataSet;
    private Context mContext;

    public WwGameReportAdapter(Context con, WwGameReport report) {
        this.mDataSet = report;
        this.mContext = con;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        int layout;
        switch(viewType) {
            case 2:
            case 3: layout = R.layout.card_penalty_game_event; break;
            case 4:
            case 5: layout = R.layout.card_goal_game_event; break;
            default:  layout = R.layout.card_text_game_event;
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh;
        switch(viewType) {
            case 2:
            case 3: vh = new PenaltyViewHolder(v); break;
            case 4:
            case 5: vh = new GoalViewHolder(v); break;
            default:  vh = new TextViewHolder(v);
        }

        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WwGameEvent e = this.getItem(position);
        holder.mTextViewTime.setText(e.getTimeString());

        switch(e.getType()) {
            case 1: this.onBindTextViewHolder((TextViewHolder) holder, (WwTextGameEvent) e); break;
            case 2:
            case 3: this.onBindPenaltyViewHolder((PenaltyViewHolder) holder, (WwPenaltyGameEvent) e); break;
            case 4:
            case 5: this.onBindGoalViewHolder((GoalViewHolder) holder, (WwGoalGameEvent) e); break;
            default: holder.mTextViewTitle.setText("Unkown"); break;
        }
    }

    private void onBindTextViewHolder(TextViewHolder holder, WwTextGameEvent e) {
        holder.mTextViewText.setText(e.getText());

    }

    private void onBindGoalViewHolder(GoalViewHolder holder, WwGoalGameEvent e) {
        holder.mTextViewTitle.setText("Tor");
        holder.mTextViewText.setText("für " + e.getPlayerTeamName());
        holder.mTextViewPlayer.setText("durch " + e.getPlayer());

        this.onBindPlayerImageView(holder, new WwGoalImageLoadTask(this.mContext, holder.mImageView), e);

    }

    private void onBindPenaltyViewHolder(PenaltyViewHolder holder, WwPenaltyGameEvent e) {
        holder.mTextViewTitle.setText("Strafe");
        holder.mTextViewText.setText("gegen " + e.getPlayerTeamName());
        holder.mTextViewPlayer.setText("für " + e.getPlayer());

    }


    private void onBindPlayerImageView(PenaltyViewHolder holder, WwImageLoadTask task, WwPlayerCausedGameEvent e) {
        if(holder.mImageLoadTask != null && holder.mImageLoadTask.getStatus() != AsyncTask.Status.FINISHED) {
            holder.mImageLoadTask.cancel(true);

        }

        holder.mImageLoadTask = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, e.getPlayerTeamName(), e.getPlayer());
    }

    @Override
    public int getItemCount() {
        return this.mDataSet == null ? 0 : this.mDataSet.size();

    }

    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getType();

    }

    private WwGameEvent getItem(int position) {
        return (WwGameEvent) this.mDataSet.values().toArray()[this.mDataSet.size() - position - 1];
    }
}
