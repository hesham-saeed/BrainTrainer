package com.example.braintrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.braintrainer.Model.User;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.BoardItemViewHolder> {

    private ArrayList<User> users;

    private Context context;

    public LeaderBoardAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public BoardItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_user_rank2, viewGroup, false);

        return new BoardItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardItemViewHolder boardItemViewHolder, int i) {
        boardItemViewHolder.bind(users.get(i));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class BoardItemViewHolder extends RecyclerView.ViewHolder {
        TextView scoreItemTextView;
        ImageView avatarImageView;
        TextView fullNameTextView;
        TextView gamesPlayedTextView;

        BoardItemViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreItemTextView = itemView.findViewById(R.id.list_item_score_text_view);
            avatarImageView = itemView.findViewById(R.id.list_item_avatar_image_view);
            fullNameTextView = itemView.findViewById(R.id.list_item_user_name);
            gamesPlayedTextView = itemView.findViewById(R.id.list_item_games_played_text_view);
        }

        void bind(User user) {
            scoreItemTextView.setText(String.valueOf(user.getScore()));
            if (user.getFullName().length() > 15)
                user.setFullName(user.getFullName().substring(0,15));
            fullNameTextView.setText(user.getFullName());
            gamesPlayedTextView.setText(String.valueOf(user.getGamesPlayed()));

            int resId = context.getResources().getIdentifier("avatar" + user.getAvatarNo(), "drawable", "com.example.braintrainer");
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            avatarImageView.setImageBitmap(bitmap);
        }
    }
}
