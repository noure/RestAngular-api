package fr.example.springboot.service;

import fr.example.springboot.DTOUtils;
import fr.example.springboot.domain.Comment;
import fr.example.springboot.domain.Post;
import fr.example.springboot.exception.ResourceNotFoundException;
import fr.example.springboot.web.dto.CommentDetails;
import fr.example.springboot.web.dto.CommentForm;
import fr.example.springboot.web.dto.PostDetails;
import fr.example.springboot.web.dto.PostForm;
import fr.example.springboot.repository.CommentRepository;
import fr.example.springboot.repository.PostRepository;
import fr.example.springboot.repository.PostSpecifications;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 *
 * @author Nour LABIHI
 * 
 */
@Service
@Transactional
public class BlogService {

    private static final Logger log = LoggerFactory.getLogger(BlogService.class);

    @Inject
    private PostRepository postRepository;

    @Inject
    private CommentRepository commentRepository;

    public Page<PostDetails> searchPostsByCriteria(String q, Post.Status status, Pageable page) {

        log.debug("search posts by keyword@" + q + ", page @" + page);

        Page<Post> posts = postRepository.findAll(PostSpecifications.filterByKeywordAndStatus(q, status),
                page);

        if (log.isDebugEnabled()) {
            log.debug("get posts size @" + posts.getTotalElements());
        }

        return DTOUtils.mapPage(posts, PostDetails.class);
    }

    public PostDetails savePost(PostForm form) {

        log.debug("save post @" + form);

        Post post = DTOUtils.map(form, Post.class);

        Post saved = postRepository.save(post);

        log.debug("saved post id is @" + saved);

        return DTOUtils.map(saved, PostDetails.class);

    }

    public PostDetails updatePost(Long id, PostForm form) {
        Assert.notNull(id, "post id can not be null");

        log.debug("updating post of @" + id + ", posst content @" + form);

        Post post = postRepository.findOne(id);
        DTOUtils.mapTo(form, post);

        Post saved = postRepository.save(post);

        log.debug("updated post@" + saved);

        return DTOUtils.map(saved, PostDetails.class);
    }

    public PostDetails findPostById(Long id) {
        Assert.notNull(id, "post id can not be null");

        log.debug("find post by id@" + id);

        Post post = postRepository.findOne(id);

        if (post == null) {
            throw new ResourceNotFoundException(id);
        }

        return DTOUtils.map(post, PostDetails.class);
    }

    public Page<CommentDetails> findCommentsByPostId(Long id, Pageable page) {

        log.debug("find comments by post id@" + id);

        Page<Comment> comments = commentRepository.findByPostId(id, page);

        if (log.isDebugEnabled()) {
            log.debug("found results@" + comments.getTotalElements());
        }

        return DTOUtils.mapPage(comments, CommentDetails.class);
    }

    public CommentDetails saveCommentOfPost(Long id, CommentForm fm) {
        Assert.notNull(id, "post id can not be null");

        log.debug("find post by id@" + id);

        Post post = postRepository.findOne(id);

        if (post == null) {
            throw new ResourceNotFoundException(id);
        }

        Comment comment = DTOUtils.map(fm, Comment.class);

        comment.setPost(post);

        comment = commentRepository.save(comment);

        if (log.isDebugEnabled()) {
            log.debug("comment saved@" + comment);
        }

        return DTOUtils.map(comment, CommentDetails.class);
    }

    public void deletePostById(Long id) {
        Assert.notNull(id, "post id can not be null");

        log.debug("find post by id@" + id);

        Post post = postRepository.findOne(id);

        if (post == null) {
            throw new ResourceNotFoundException(id);
        }

        postRepository.delete(post);
    }

    public void deleteCommentById(Long id) {
        Assert.notNull(id, "comment id can not be null");

        if (log.isDebugEnabled()) {
            log.debug("delete comment by id@" + id);
        }

        Comment comment = commentRepository.findOne(id);

        if (comment == null) {
            throw new ResourceNotFoundException(id);
        }

        commentRepository.delete(comment);
    }
}
