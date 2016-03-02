package fr.example.springboot.web.controller.article;

import fr.example.springboot.Constants;
import fr.example.springboot.domain.Post;
import fr.example.springboot.web.dto.CommentDetails;
import fr.example.springboot.web.dto.CommentForm;
import fr.example.springboot.web.dto.PostDetails;
import fr.example.springboot.web.dto.PostForm;
import fr.example.springboot.web.dto.ResponseMessage;
import fr.example.springboot.service.BlogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.net.URI;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = Constants.URI_API_PREFIX + Constants.URI_POSTS)
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @Inject
    private BlogService blogService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get all posts")
    @ApiResponses(
            value = {
                @ApiResponse(code = 200, message = "return all posts by page")
            }
    )
    public ResponseEntity<Page<PostDetails>> getAllPosts(
            @RequestParam(value = "q", required = false) String keyword, //
            @RequestParam(value = "status", required = false) Post.Status status, //
            @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Direction.DESC) Pageable page) {

        log.debug("get all posts of q@" + keyword + ", status @" + status + ", page@" + page);

        Page<PostDetails> posts = blogService.searchPostsByCriteria(keyword, status, page);

        log.debug("get posts size @" + posts.getTotalElements());

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get a post")
    public ResponseEntity<PostDetails> getPost(@PathVariable("id") Long id) {

        log.debug("get postsinfo by id @" + id);

        PostDetails post = blogService.findPostById(id);

        log.debug("get post @" + post);

        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Cretae a new post")
    public ResponseEntity<Void> createPost(@RequestBody @Valid PostForm post, HttpServletRequest request) {

        log.debug("create a new post@" + post);

        PostDetails saved = blogService.savePost(post);

        log.debug("saved post id is @" + saved.getId());
        URI loacationHeader = ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("/api/posts/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        
        HttpHeaders headers=new HttpHeaders();
        headers.setLocation(loacationHeader);
        
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update an existing post")
    public ResponseEntity<ResponseMessage> updatePost(@PathVariable("id") Long id, @RequestBody @Valid PostForm form) {

        log.debug("update post by id @" + id + ", form content@" + form);

        blogService.updatePost(id, form);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete an existing post")
    public ResponseEntity<ResponseMessage> deletePostById(@PathVariable("id") Long id) {

        log.debug("delete post by id @" + id);

        blogService.deletePostById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<CommentDetails>> getCommentsOfPost(
            @PathVariable("id") Long id,
            @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Direction.DESC) Pageable page) {

        log.debug("get comments of post@" + id + ", page@" + page);

        Page<CommentDetails> commentsOfPost = blogService.findCommentsByPostId(id, page);

        log.debug("get post comment size @" + commentsOfPost.getTotalElements());

        return new ResponseEntity<>(commentsOfPost, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> createCommentOfPost(
            @PathVariable("id") Long id, @RequestBody CommentForm comment) {

        log.debug("new comment of post@" + id + ", comment" + comment);

        CommentDetails saved = blogService.saveCommentOfPost(id, comment);

        log.debug("saved comment @" + saved.getId());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}