package com.cybattis.swiftycompanion.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cybattis.swiftycompanion.R;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {
    private List<User.Skill> skills;
    private Context context;

    public SkillAdapter(List<User.Skill> skills, Context context) {
        this.context = context;
        this.skills = skills;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skill_item, parent, false);
        return new SkillAdapter.SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        User.Skill skill = skills.get(position);
        holder.skillName.setText(skill.name);
        holder.skillLevel.setText(skill.getLevelString());
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public static class SkillViewHolder extends RecyclerView.ViewHolder {
        public TextView skillName;
        public TextView skillLevel;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            skillName = itemView.findViewById(R.id.skill_name);
            skillLevel = itemView.findViewById(R.id.skill_level);
        }
    }

}
