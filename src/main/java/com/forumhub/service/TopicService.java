package com.forumhub.service;

import com.forumhub.dto.TopicDTOs.*;
import com.forumhub.entity.Topic;
import com.forumhub.entity.User;
import com.forumhub.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Transactional
    public TopicResponse create(CreateTopicRequest request, User author) {
        if (topicRepository.existsByTitleAndMessage(request.title(), request.message())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A topic with this title and message already exists");
        }

        Topic topic = Topic.builder()
                .title(request.title())
                .message(request.message())
                .courseName(request.courseName())
                .author(author)
                .status(Topic.Status.OPEN)
                .build();

        return TopicResponse.from(topicRepository.save(topic));
    }

    @Transactional(readOnly = true)
    public Page<TopicSummary> listAll(String courseName, Pageable pageable) {
        Page<Topic> topics;
        if (courseName != null && !courseName.isBlank()) {
            topics = topicRepository.findByCourseNameIgnoreCase(courseName, pageable);
        } else {
            topics = topicRepository.findAll(pageable);
        }
        return topics.map(TopicSummary::from);
    }

    @Transactional(readOnly = true)
    public TopicResponse findById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Topic not found with id: " + id));
        return TopicResponse.from(topic);
    }

    @Transactional
    public TopicResponse update(Long id, UpdateTopicRequest request, User currentUser) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Topic not found with id: " + id));

        // Only the author or admin can update
        if (!topic.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to update this topic");
        }

        if (request.title() != null) topic.setTitle(request.title());
        if (request.message() != null) topic.setMessage(request.message());
        if (request.courseName() != null) topic.setCourseName(request.courseName());
        if (request.status() != null) topic.setStatus(request.status());

        return TopicResponse.from(topicRepository.save(topic));
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Topic not found with id: " + id));

        // Only the author or admin can delete
        if (!topic.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this topic");
        }

        topicRepository.delete(topic);
    }

    @Transactional(readOnly = true)
    public Page<TopicSummary> listByCurrentUser(User currentUser, Pageable pageable) {
        return topicRepository.findByAuthorId(currentUser.getId(), pageable)
                .map(TopicSummary::from);
    }
}
