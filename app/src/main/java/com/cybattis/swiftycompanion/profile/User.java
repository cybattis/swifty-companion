package com.cybattis.swiftycompanion.profile;

import com.cybattis.swiftycompanion.backend.ApiResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class User {
    public ApiResponse response;

    @SerializedName("id")
    public int id;
    @SerializedName("email")
    public String email;
    @SerializedName("login")
    public String login;
    @SerializedName("first_name")
    public String firstName;
    @SerializedName("last_name")
    public String lastName;
    @SerializedName("phone")
    public String phone;
    @SerializedName("displayname")
    public String displayName;
    @SerializedName("image")
    public Image image;
    @SerializedName("correction_point")
    public int correctionPoint;
    @SerializedName("location")
    public String location;
    @SerializedName("wallet")
    public int wallet;
    @SerializedName("cursus_users")
    public List<CursusUser> cursusUsers;
    @SerializedName("projects_users")
    public List<UserProject> projectsUsers;

    public static class Image {
        @SerializedName("link")
        public String link;
        @SerializedName("versions")
        public Versions versions;

        public static class Versions {
            @SerializedName("large")
            public String large;
            @SerializedName("medium")
            public String medium;
            @SerializedName("small")
            public String small;
            @SerializedName("micro")
            public String micro;
        }
    }

    public static class UserProject {
        @SerializedName("id")
        public int id;
        @SerializedName("occurrence")
        public int occurrence;
        @SerializedName("final_mark")
        public int finalMark;
        @SerializedName("status")
        public String status;
        @SerializedName("validated?")
        public boolean validated;
        @SerializedName("current_team_id")
        public int currentTeamId;
        @SerializedName("project")
        public Project project;

        public static class Project {
            @SerializedName("id")
            public int id;
            @SerializedName("name")
            public String name;
            @SerializedName("slug")
            public String slug;
            @SerializedName("parent_id")
            public Integer parentId;
        }
    }

    public static class CursusUser {
        @SerializedName("id")
        public int id;
        @SerializedName("begin_at")
        public String beginAt;
        @SerializedName("end_at")
        public String endAt;
        @SerializedName("grade")
        public String grade;
        @SerializedName("level")
        public double level;
        @SerializedName("skills")
        public List<Skill> skills;
        @SerializedName("cursus_id")
        public int cursusId;
        @SerializedName("has_coalition")
        public boolean hasCoalition;
        @SerializedName("user")
        public User user;
        @SerializedName("cursus")
        public Cursus cursus;

        public static class Cursus {
            @SerializedName("id")
            public int id;
            @SerializedName("created_at")
            public String createdAt;
            @SerializedName("name")
            public String name;
            @SerializedName("slug")
            public String slug;
        }
    }

    public static class Skill {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("level")
        public double level;

        public String getLevelString() {
            return "Level " + (int)level + " - " + (int)(level * 100) % 100 + "%";
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getCursusName() {
        for (CursusUser cursusUser : cursusUsers) {
            if (cursusUser.cursus.slug.equals("42cursus")) {
                return cursusUser.cursus.name;
            }
        }
        return "";
    }

    public String getImage() {
        return image.versions.small;
    }

    public int getLevel() {
        for (CursusUser cursusUser : cursusUsers) {
            if (cursusUser.cursus.slug.equals("42cursus")) {
                return (int)cursusUser.level;
            }
        }
        return 0;
    }

    public int getDecimalXp() {
        for (CursusUser cursusUser : cursusUsers) {
            if (cursusUser.cursus.slug.equals("42cursus")) {
                return (int)(cursusUser.level * 100) % 100;
            }
        }
        return 0;
    }

    public String getXpString() {
        return "Level " + getLevel() + " - " + getDecimalXp() + "%";
    }

    public String getId() {
        return String.valueOf(id);
    }

    public static class DisplayProject {
        public String name;
        public int finalMark;
        public boolean validationStatus;
    }

    public List<DisplayProject> getProjectsList() {
        List<DisplayProject> displayProjects = new ArrayList<>();
        for (UserProject project : projectsUsers) {
            if (project.project.slug.contains("c-piscine")) {
                continue;
            }
            DisplayProject displayProject = new DisplayProject();
            displayProject.name = project.project.name;
            displayProject.finalMark = project.finalMark;
            displayProject.validationStatus = project.validated;
            displayProjects.add(displayProject);
        }
        displayProjects.sort(Comparator.comparing(o -> o.name));
        return displayProjects;
    }

    public List<Skill> getSkillsList() {
        List<Skill> skills = new ArrayList<>();
        for (CursusUser cursusUser : cursusUsers) {
            if (cursusUser.cursus.slug.equals("42cursus")) {
                skills.addAll(cursusUser.skills);
            }
        }
        return skills;
    }
}

