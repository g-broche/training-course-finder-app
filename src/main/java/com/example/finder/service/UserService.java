package com.example.finder.service;

import com.example.finder.config.PaginationConfig;
import com.example.finder.dto.output.AdminDiscussionDTO;
import com.example.finder.dto.output.AnnounceDto;
import com.example.finder.dto.output.ExcerptUserDTOForAdmin;
import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.model.Announce;
import com.example.finder.model.AppUser;
import com.example.finder.model.Discussion;
import com.example.finder.model.Role;
import com.example.finder.model.UserStatus;
import com.example.finder.repository.AnnounceRepository;
import com.example.finder.repository.AppUserRepository;
import com.example.finder.repository.DiscussionRepository;
import com.example.finder.repository.RoleRepository;
import com.example.finder.repository.UserStatusRepository;
import com.example.finder.repository.specification.AnnounceSpecifications;
import com.example.finder.repository.specification.DiscussionSpecifications;
import com.example.finder.repository.specification.UserSpecifications;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.response.PaginatedResponse;
import com.example.finder.utils.ImageUtil;
import com.example.finder.utils.validator.ValidatorAuth;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final AppUserRepository userRepository;
    private final DiscussionRepository discussionRepository;
    private final AnnounceRepository announceRepository;
    private final UserStatusRepository userStatusRepository;
    private final RoleRepository roleRepository;
    private final PaginationConfig paginationConfig;
    private final ValidatorAuth validatorAuth;
    private final ImageUtil imageUtil;

    public UserService(
            AppUserRepository userRepository,
            DiscussionRepository discussionRepository,
            AnnounceRepository announceRepository,
            UserStatusRepository userStatusRepository,
            RoleRepository roleRepository,
            PaginationConfig paginationConfig,
            ValidatorAuth validatorAuth,
            ImageUtil imageUtil) {
        this.userRepository = userRepository;
        this.discussionRepository = discussionRepository;
        this.announceRepository = announceRepository;
        this.userStatusRepository = userStatusRepository;
        this.roleRepository = roleRepository;
        this.paginationConfig = paginationConfig;
        this.validatorAuth = validatorAuth;
        this.imageUtil = imageUtil;
    }

    public ResponseEntity<?> isDisplayNameAvailable(String displayName) {
        try {
            boolean isAvailable = !userRepository.existsByDisplayName(displayName);
            return ApiResponseFactory.success(isAvailable);
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getUserDetailForModeration(String username) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findByDisplayName(username).orElseThrow(UserNotFoundException::new);
            return ApiResponseFactory.success(user.toExcerptUserDTOForAdmin());
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getPaginatedUsersForModeration(int page, int size, String displayName) {
        try {
            System.out.println("Received getPaginatedUsersForModeration request with parameters: page=" + page
                    + ", size=" + size + ", displayName=" + displayName);
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            size = Math.min(size, paginationConfig.getMaxResultsPerPage());
            List<Specification<AppUser>> specList = new ArrayList<>();
            if (displayName != null && !displayName.isEmpty()) {
                specList.add(UserSpecifications.hasDisplayNameContaining(displayName));
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("displayName").ascending());
            Specification<AppUser> spec = Specification.allOf(specList);
            Page<AppUser> userPage = userRepository.findAll(spec, pageable);
            Page<ExcerptUserDTOForAdmin> userDtoPage = userPage.map(AppUser::toExcerptUserDTOForAdmin);
            var paginatedResult = PaginatedResponse.from(userDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getUserDiscussionsForModeration(String username, int page, int size,
            boolean reportedOnly) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findByDisplayName(username).orElseThrow(UserNotFoundException::new);

            List<Specification<Discussion>> specList = new ArrayList<>(
                    Arrays.asList(
                            DiscussionSpecifications.hasParticipant(user.getId())));
            Specification<Discussion> spec = Specification.allOf(specList);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Page<Discussion> discussionPage = discussionRepository.findAll(spec, pageable);
            Page<AdminDiscussionDTO> discussionDtoPage = discussionPage.map(Discussion::toAdminDiscussionDTO);
            var paginatedResult = PaginatedResponse.from(discussionDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> getUserAnnouncesForModeration(String username, int page, int size) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findByDisplayName(username).orElseThrow(UserNotFoundException::new);
            List<Specification<Announce>> specList = new ArrayList<>(
                    Arrays.asList(
                            AnnounceSpecifications.hasAuthor(user.getId())));
            Specification<Announce> spec = Specification.allOf(specList);
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            Page<Announce> announcePage = announceRepository.findAll(spec, pageable);
            Page<AnnounceDto> announceDtoPage = announcePage
                    .map(it -> new AnnounceDto(it, imageUtil.getBaseWebPathForPhotos()));
            var paginatedResult = PaginatedResponse.from(announceDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    @Transactional
    public ResponseEntity<?> banUser(UUID id) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            boolean isRequesterTryingToBanHimself = requester.getId().equals(user.getId());
            if (isRequesterTryingToBanHimself) {
                return ApiResponseFactory.badRequest("You cannot ban yourself");
            }
            if (user.isBanned()) {
                return ApiResponseFactory.badRequest("User is already banned");
            }
            UserStatus bannedStatus = userStatusRepository.getBannedUserStatusOrThrow();
            user.setUserStatus(bannedStatus);
            if (user.isAdmin()) {
                Role adminRole = roleRepository.getAdminRoleOrThrow();
                user.getRoles().remove(adminRole);
            }
            userRepository.save(user);
            return ApiResponseFactory.success("User banned successfully", user.toExcerptUserDTOForAdmin());
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> unbanUser(UUID id) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            if (!user.isBanned()) {
                return ApiResponseFactory.badRequest("User is not currently banned");
            }
            UserStatus allowedStatus = userStatusRepository.getAllowedUserStatusOrThrow();
            user.setUserStatus(allowedStatus);
            userRepository.save(user);
            return ApiResponseFactory.success("User unbanned successfully", user.toExcerptUserDTOForAdmin());
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> promoteAdmin(UUID id) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            boolean isRequesterTryingToPromoteHimself = requester.getId().equals(user.getId());
            if (isRequesterTryingToPromoteHimself) {
                return ApiResponseFactory.badRequest("You cannot promote yourself");
            }
            if (user.isBanned()) {
                return ApiResponseFactory.badRequest("You cannot promote a banned user");
            }
            if (user.isAdmin()) {
                return ApiResponseFactory.badRequest("This user is already an admin");
            }
            Role adminRole = roleRepository.getAdminRoleOrThrow();
            user.getRoles().add(adminRole);
            userRepository.save(user);
            return ApiResponseFactory.success("User promoted to admin successfully", user.toExcerptUserDTOForAdmin());
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }

    public ResponseEntity<?> revokeAdmin(UUID id) {
        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();
            if (!requester.isAdmin()) {
                return ApiResponseFactory.unauthorized("You are not authorized to perform this action");
            }
            AppUser user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            boolean isRequesterTryingToRevokeHimself = requester.getId().equals(user.getId());
            if (isRequesterTryingToRevokeHimself) {
                return ApiResponseFactory.badRequest("You cannot revoke yourself");
            }
            if (!user.isAdmin()) {
                return ApiResponseFactory.badRequest("This user is not an admin");
            }
            Role adminRole = roleRepository.getAdminRoleOrThrow();
            user.getRoles().remove(adminRole);
            userRepository.save(user);
            return ApiResponseFactory.success("User admin role revoked successfully", user.toExcerptUserDTOForAdmin());
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponseFactory.internalError();
        }
    }
}
