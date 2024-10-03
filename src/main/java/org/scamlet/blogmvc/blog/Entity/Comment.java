package org.scamlet.blogmvc.blog.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name="comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="owner_id", nullable=false)
    private User owner;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="post_id", nullable=false)
    private Post post;

    @Column(name="comment", nullable=false)
    private String comment;

    @Column(name="date", nullable = false)
    private Date date;

}
