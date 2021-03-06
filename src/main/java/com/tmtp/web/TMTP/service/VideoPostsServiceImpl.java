package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.dto.exceptions.NoDataFound;
import com.tmtp.web.TMTP.entity.Comment;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.repository.VideoPostsRepository;
import org.bson.types.ObjectId;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class VideoPostsServiceImpl implements VideoPostsService {

    private final VideoPostsRepository videoPostsRepository;
    private final MessageSource messageSource;

    public VideoPostsServiceImpl(
            final VideoPostsRepository videoPostsRepository,
            final MessageSource messageSource) {
        this.videoPostsRepository = videoPostsRepository;
        this.messageSource = messageSource;
    }

    @Override
    public List<VideoPosts> retrieveListOfVideoPosts() {
        return videoPostsRepository.findAll();
    }

    @Override
    public List<VideoPosts> retrieveListOfVideoPostsByUsername(String username) {
        return videoPostsRepository.findByCreator(username);
    }

    @Override
    public VideoPosts retrieveVideoPostById(String id) {
        return videoPostsRepository.findById(id);
    }

    @Override
    public VideoPosts createVideoPost(VideoPosts videoPosts, User user) {
        String url = "";

        if (videoPosts.getLink().contains("https://youtu.be/")) {
            url = "https://www.youtube.com/embed/" + videoPosts.getLink().replace("https://youtu.be/", "");
            videoPosts.setVideo(true);
        } else {
            videoPosts.setVideo(false);
        }
        videoPosts.setComments(Collections.emptyList());
        videoPosts.setCreator(user.getUsername());
        videoPosts.setUser(user);
        videoPosts.setLink(url);
        videoPosts.setDislikes(0);
        videoPosts.setLikes(0);
        videoPosts.setGrantPoint(false);
        videoPosts.setDislikeUsers(Collections.emptyList());
        videoPosts.setLikeUsers(Collections.emptyList());
        videoPostsRepository.save(videoPosts);

        return videoPosts;
    }

    @Override
    public VideoPosts addNewComment(String postId, String commentText, User user) {

        VideoPosts videoPosts = videoPostsRepository.findById(postId);

        if (videoPosts == null) {
            throw new NoDataFound(messageSource.getMessage(
                    "NO_VIDEOPOST_FOUND", new Object[]{postId}, Locale.ENGLISH));
        }

        Comment comment = new Comment();
        comment.setId(new ObjectId().toString());
        comment.setComment(commentText);
        comment.setTimestamp(LocalDateTime.now());
        comment.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        comment.setUser(user);
        videoPosts.getComments().add(comment);

        videoPostsRepository.save(videoPosts);
        return videoPosts;
    }

    @Override
    public void addNewComment(VideoPosts videoPosts, Comment comment, User user) {

        comment.setId(new ObjectId().toString());
        comment.setTimestamp(LocalDateTime.now());
        comment.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        comment.setUser(user);
        videoPosts.getComments().add(comment);

        videoPostsRepository.save(videoPosts);
    }

    @Override
    public void updatePost(VideoPosts videoPosts) {
        videoPostsRepository.save(videoPosts);
    }

    @Override
    public void deletePost(VideoPosts videoPosts) {
        videoPostsRepository.delete(videoPosts);
    }

    @Override
    public void deletePostComment(VideoPosts videoPosts) {
        videoPostsRepository.save(videoPosts);
    }

    @Override
    public void flagVideoPost(String postId, boolean flagStatus) {
        VideoPosts videoPosts = videoPostsRepository.findById(postId);
        videoPosts.setFlagged(flagStatus);
        videoPostsRepository.save(videoPosts);
    }

    @Override
    public VideoPosts updateLikeStatusForPost(String postId, String userName, boolean isLike) {

        VideoPosts videoPosts = videoPostsRepository.findById(postId);

        if (videoPosts != null) {
            if (isLike) {
                if (videoPosts.getLikeUsers().contains(userName)) {
                    videoPosts.getLikeUsers().remove(userName);
                    videoPosts.setLikes(videoPosts.getLikes() - 1);
                } else {
                    videoPosts.setLikes(videoPosts.getLikes() + 1);
                    videoPosts.getLikeUsers().add(userName);
                }
                if (videoPosts.getDislikeUsers().contains(userName)) {
                    videoPosts.setDislikes(videoPosts.getDislikes() - 1);
                    videoPosts.getDislikeUsers().remove(userName);
                }
            } else {
                if (videoPosts.getDislikeUsers().contains(userName)) {
                    videoPosts.getDislikeUsers().remove(userName);
                    videoPosts.setDislikes(videoPosts.getDislikes() - 1);
                } else {
                    videoPosts.setDislikes(videoPosts.getDislikes() + 1);
                    videoPosts.getDislikeUsers().add(userName);
                }
                if (videoPosts.getLikeUsers().contains(userName)) {
                    videoPosts.setLikes(videoPosts.getLikes() - 1);
                    videoPosts.getLikeUsers().remove(userName);
                }
            }
        } else {
            throw new NoDataFound(messageSource.getMessage("NO_VIDEOPOST_FOUND",
                    new Object[]{postId}, Locale.ENGLISH));
        }
        videoPostsRepository.save(videoPosts);
        return videoPosts;
    }

    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}