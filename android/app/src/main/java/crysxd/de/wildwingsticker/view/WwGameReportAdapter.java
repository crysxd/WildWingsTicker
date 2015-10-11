package crysxd.de.wildwingsticker.view;

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

        public ImageView mImageViewPlayer;
        public AsyncTask mImageLoadTask;
        public Button mButtonPlayerStats;
        public TextView mTextViewPlayer;

        public PenaltyViewHolder(View itemView) {
            super(itemView);

            this.mButtonPlayerStats = (Button) itemView.findViewById(R.id.buttonPlayerStats);
            this.mImageViewPlayer = (ImageView) itemView.findViewById(R.id.imageViewPlayer);
            this.mTextViewPlayer = (TextView) itemView.findViewById(R.id.textViewPlayer);

        }
    }

    public static class GoalViewHolder extends PenaltyViewHolder {

        public TextView mTextViewScore;
        public Button mButtonShare;

        public GoalViewHolder(View itemView) {
            super(itemView);

            this.mTextViewScore = (TextView) itemView.findViewById(R.id.textViewScore);
            this.mButtonShare = (Button) itemView.findViewById(R.id.buttonShare);

        }
    }



    /*
     * =============================================================================================
     */

    private WwGameReport mDataSet;


    public WwGameReportAdapter(WwGameReport report) {
        this.mDataSet = report;
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
            case 1:  holder.mTextViewTitle.setText(""); this.onBindTextViewHolder((TextViewHolder) holder, (WwTextGameEvent) e); break;
            case 2:
            case 3: holder.mTextViewTitle.setText("Strafe"); this.onBindPenaltyViewHolder((PenaltyViewHolder) holder, (WwPenaltyGameEvent) e); break;
            case 4: holder.mTextViewTitle.setText("Toooooooor!"); this.onBindGoalViewHolder((GoalViewHolder) holder, (WwGoalGameEvent) e); break;
            case 5: holder.mTextViewTitle.setText("Tor"); this.onBindGoalViewHolder((GoalViewHolder) holder, (WwGoalGameEvent) e); break;
            default: holder.mTextViewTitle.setText("Unkown"); break;
        }
    }

    private void onBindTextViewHolder(TextViewHolder holder, WwTextGameEvent e) {
        holder.mTextViewText.setText(e.getText());

    }

    private void onBindGoalViewHolder(GoalViewHolder holder, WwGoalGameEvent e) {
        holder.mTextViewText.setText("Tor für die " + e.getPlayerTeamName());
        holder.mTextViewPlayer.setText(e.getPlayer());
        holder.mTextViewScore.setText("Neuer Spielstand: " + e.getScore());

        this.onBindPlayerImageView(holder, e);

    }

    private void onBindPenaltyViewHolder(PenaltyViewHolder holder, WwPenaltyGameEvent e) {
        holder.mTextViewText.setText("Strafe gegen die " + e.getPlayerTeamName());
        holder.mTextViewPlayer.setText("Spieler: " + e.getPlayer());

        this.onBindPlayerImageView(holder, e);

    }


    private void onBindPlayerImageView(PenaltyViewHolder holder, WwPlayerCausedGameEvent e) {
        if(holder.mImageLoadTask != null && holder.mImageLoadTask.getStatus() != AsyncTask.Status.FINISHED) {
            holder.mImageLoadTask.cancel(true);
        }

        holder.mImageViewPlayer.setImageResource(R.drawable.ice);
        holder.mImageLoadTask = new WwPlayerImageLoadTask(holder.mImageViewPlayer).execute(e.getPlayerTeamName(), e.getPlayer());

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
