package com.forumhub.dto;

import com.forumhub.entity.Topic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TopicDTOs {

    public record CreateTopicRequest(
            @NotBlank(message = "Title is required")
            @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
            String title,

            @NotBlank(message = "Message is required")
            @Size(min = 10, message = "Message must be at least 10 characters")
            String message,

            @NotBlank(message = "Course name is required")
            String courseName
    ) {}

    public record UpdateTopicRequest(
            @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
            String title,

            @Size(min = 10, message = "Message must be at least 10 characters")
            String message,

            String courseName,

            Topic.Status status
    ) {}

    public record TopicResponse(
            Long id,
            String title,
            String message,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Topic.Status status,
            String courseName,
            String authorUsername
    ) {
        public static TopicResponse from(Topic topic) {
            return new TopicResponse(
                    topic.getId(),
                    topic.getTitle(),
                    topic.getMessage(),
                    topic.getCreatedAt(),
                    topic.getUpdatedAt(),
                    topic.getStatus(),
                    topic.getCourseName(),
                    topic.getAuthor().getUsername()
            );
        }
    }

    public record TopicSummary(
            Long id,
            String title,
            String courseName,
            Topic.Status status,
            LocalDateTime createdAt,
            String authorUsername
    ) {
        public static TopicSummary from(Topic topic) {
            return new TopicSummary(
                    topic.getId(),
                    topic.getTitle(),
                    topic.getCourseName(),
                    topic.getStatus(),
                    topic.getCreatedAt(),
                    topic.getAuthor().getUsername()
            );
        }
    }
}
