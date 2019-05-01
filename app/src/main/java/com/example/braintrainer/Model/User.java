package com.example.braintrainer.Model;

public class User implements Comparable{
    public User(){}
    private String fullName;
    private String email;
    private int avatarNo;
    private int gamesPlayed;
    private int score;



    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public int getAvatarNo() {
        return avatarNo;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarNo(int avatarNo) {
        this.avatarNo = avatarNo;
    }

    public User(String fullName, String email, int avatarNo) {
        this.fullName = fullName;
        this.email = email;
        this.avatarNo = avatarNo;
    }

    @Override
    public int compareTo(Object o) {
        User user = (User) o;
        if (score < user.score)
            return -1;
        else if (score > user.score)
            return 1;
        return 0;
    }
}
