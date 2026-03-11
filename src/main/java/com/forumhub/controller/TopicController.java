package com.forumhub.controller;

import com.forumhub.dto.TopicDTOs.*;
import com.forumhub.entity.User;
import com.forumhub.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topics", description = "Forum topic management")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @PostMapping
    @Operation(summary = "Create a new topic", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TopicResponse> create(
            @RequestBody @Valid CreateTopicRequest request,
            @AuthenticationPrincipal User currentUser) {
        TopicResponse response = topicService.create(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all topics (paginated, optionally filtered by course)")
    public ResponseEntity<Page<TopicSummary>> listAll(
            @RequestParam(required = false) String courseName,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(topicService.listAll(courseName, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a topic by ID")
    public ResponseEntity<TopicResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a topic (only by author or admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TopicResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTopicRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(topicService.update(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a topic (only by author or admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        topicService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-topics")
    @Operation(summary = "List topics created by the current user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<TopicSummary>> myTopics(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(topicService.listByCurrentUser(currentUser, pageable));
    }
}
