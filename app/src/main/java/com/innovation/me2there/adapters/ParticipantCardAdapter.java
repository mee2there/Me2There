package com.innovation.me2there.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.me2there.R;
import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.model.UserVO;

import org.apache.commons.collections4.IteratorUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by ashley on 9/29/15.
 */
public class ParticipantCardAdapter extends RecyclerView.Adapter<ParticipantCardAdapter.UserViewHolder>{

    protected List<UserVO> mUserVOs;
    private int rowLayout;
    private Context mContext;
    private int mPreviousPosition = 0;

    public ParticipantCardAdapter(Set<UserVO> userParm, int rowLayout, Context context) {
        if(userParm!= null) {
            this.mUserVOs = IteratorUtils.toList(userParm.iterator());

        }
        this.rowLayout = rowLayout;
        this.mContext = context;

    }

    @Override
    public int getItemCount() {
        return mUserVOs == null ? 0 : mUserVOs.size();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        UserViewHolder pvh = new UserViewHolder(v);
        return pvh;
    }
    @Override
    public void onBindViewHolder(UserViewHolder userViewHolder, int position) {
        final UserVO aUser = mUserVOs.get(position);

        userViewHolder.personName.setText(aUser.get_userFullName());
        //userViewHolder.personAge.setText(persons.get(i).age);
        //userViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        UserViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    public void addItem(UserVO item) {
        Log.i("CardViewAdapter", "add item?");

        mUserVOs.add(item);
        this.notifyDataSetChanged();
    }
    public void removeItem(UserVO item) {
        mUserVOs.remove(item);
        this.notifyDataSetChanged();
    }

}
