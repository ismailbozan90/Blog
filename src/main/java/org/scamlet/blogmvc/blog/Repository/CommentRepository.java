package org.scamlet.blogmvc.blog.Repository;

import org.scamlet.blogmvc.blog.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
