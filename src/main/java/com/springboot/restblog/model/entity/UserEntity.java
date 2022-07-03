package com.springboot.restblog.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @Column(name = "first_name")
//    private String firstName;
//
//    @Column(name = "last_name")
//    private String lastName;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column(name = "date_registered")
    private Date dateRegistered;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<PostEntity> postEntities = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<CommentEntity> commentEntities = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private UserProfileEntity userProfile;

//    @Override
//    public String toString() {
//        return "UserEntity{" +
//                "firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                '}';
//    }
}
