package com.forumhub.repository;

import com.forumhub.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTitleAndMessage(String title, String message);
    Page<Topic> findByCourseNameIgnoreCase(String courseName, Pageable pageable);
    Page<Topic> findByAuthorId(Long authorId, Pageable pageable);
    Optional<Topic> findByIdAndAuthorId(Long id, Long authorId);
}
