package com.gaggledemo.data;

import jakarta.persistence.*;


@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    protected AppUser() {}

    private AppUser(AppUser.Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.schoolAccount = builder.schoolAccount;
    }

    private String name;
    private String email;

    private String schoolAccount;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getSchoolAccount() {
        return this.schoolAccount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSchoolAccount(String schoolAccount) {
        this.schoolAccount = schoolAccount;
    }

    @Override
    public String toString() {
        return "AppUser {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", schoolAccount='" + schoolAccount + '\'' +
                '}';
    }

    public static class Builder {
        private String name;
        private String email;
        private String schoolAccount;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder schoolAccount(String schoolAccount) {
            this.schoolAccount = schoolAccount;
            return this;
        }

        public AppUser build() {
            return new AppUser(this);
        }
    }

    // Static helper to start the builder
    public static Builder builder() {
        return new Builder();
    }
}
