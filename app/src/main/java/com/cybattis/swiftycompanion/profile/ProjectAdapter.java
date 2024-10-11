package com.cybattis.swiftycompanion.profile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cybattis.swiftycompanion.R;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private List<User.DisplayProject> projects;
    private Context context;

    public ProjectAdapter(List<User.DisplayProject> projects, Context context) {
        this.projects = projects;
        this.context = context;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        User.DisplayProject project = projects.get(position);
        holder.projectName.setText(project.name);
        holder.projectMark.setText(String.valueOf(project.finalMark));
        holder.projectMark.setTextColor(context.getResources().getColor(
                        project.validationStatus ? R.color.validated : R.color.failed, null));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        public TextView projectName;
        public TextView projectMark;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.project_name);
            projectMark = itemView.findViewById(R.id.projectr_mark_text);
        }
    }
}
