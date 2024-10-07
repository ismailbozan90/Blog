package org.scamlet.blogmvc.blog.Repository;

import org.scamlet.blogmvc.blog.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findById(long id);
    Long countByOwnerId(long ownerId);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:name%")
    Page<Post> searchByName(@Param("name") String name, Pageable pageable);

}
