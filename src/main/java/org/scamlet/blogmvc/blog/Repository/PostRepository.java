package org.scamlet.blogmvc.blog.Repository;

import org.scamlet.blogmvc.blog.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Post findById(long id);
    Long countByOwnerId(long ownerId);
}
