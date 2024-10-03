package org.scamlet.blogmvc.blog.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 4, max = 36, message = "Title must be between 4 and 36 characters")
    @Column(name="title", nullable=false)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 4, max = 255, message = "Description must be between 4 and 255 characters")
    @Column(name="description", nullable=false)
    private String description;

    @Lob
    @Column(name="thumbnail")
    private byte[] thumbnail;

    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 255, message = "Content must be between 4 and 500 characters")
    @Column(name="content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="date", nullable = false)
    private Date date;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="owner_id")
    private User owner;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public String getBase64Thumbnail() {
        return Base64.getEncoder().encodeToString(thumbnail);
    }

}
