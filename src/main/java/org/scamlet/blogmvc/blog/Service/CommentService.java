package org.scamlet.blogmvc.blog.Service;

import org.scamlet.blogmvc.blog.Entity.Comment;
import org.scamlet.blogmvc.blog.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public Long getUserCommentCount(Long userId) {
        return commentRepository.countByOwner_Id(userId);
    }

}
