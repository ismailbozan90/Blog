package org.scamlet.blogmvc.blog.Service;

import org.scamlet.blogmvc.blog.Entity.Post;
import org.scamlet.blogmvc.blog.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    @Transactional
    public void createPost(Post post) {
        postRepository.save(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Long countUserPosts(Long userId) {
        return postRepository.countByOwnerId(userId);
    }

    public Page<Post> findPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }

    public Page<Post> search(int page, int size, String param) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.searchByName(param, pageable);
    }

}
