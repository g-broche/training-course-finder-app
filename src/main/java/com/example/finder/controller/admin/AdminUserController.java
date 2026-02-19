package com.example.finder.controller.admin;

import com.example.finder.service.UserService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(
            UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserDetail(@PathVariable String username) {
        return userService.getUserDetailForModeration(username);
    }

    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPaginatedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String displayName) {
        return userService.getPaginatedUsersForModeration(
                page,
                size,
                displayName);
    }

    @GetMapping("/{username}/announces")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserAnnounces(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUserAnnouncesForModeration(username, page, size);
    }

    @GetMapping("/{username}/discussions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserDiscussions(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean reportedOnly) {
        return userService.getUserDiscussionsForModeration(username, page, size, reportedOnly);
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> banUser(@PathVariable UUID id) {
        return userService.banUser(id);
    }

    @PatchMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unbanUser(@PathVariable UUID id) {
        return userService.unbanUser(id);
    }

    @PatchMapping("/{id}/promote-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteAdmin(@PathVariable UUID id) {
        return userService.promoteAdmin(id);
    }

    @PatchMapping("/{id}/revoke-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> revokeAdmin(@PathVariable UUID id) {
        return userService.revokeAdmin(id);
    }
}
