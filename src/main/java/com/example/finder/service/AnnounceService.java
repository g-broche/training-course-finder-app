package com.example.finder.service;

import com.example.finder.config.PaginationConfig;
import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.output.AnnounceDto;
import com.example.finder.dto.output.DetailedUserDto;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.exception.action.InvalidRequestException;
import com.example.finder.exception.entity.CategoryNotFoundException;
import com.example.finder.exception.entity.UserNotFoundException;
import com.example.finder.exception.file.FileException;
import com.example.finder.model.*;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.repository.*;
import com.example.finder.repository.specification.AnnounceSpecifications;
import com.example.finder.response.ApiResponseFactory;
import com.example.finder.response.PaginatedResponse;
import com.example.finder.response.enums.AnnounceError;
import com.example.finder.response.enums.AuthError;
import com.example.finder.utils.ImageUtil;
import com.example.finder.utils.SanitizerUtil;
import com.example.finder.utils.StringUtil;
import com.example.finder.utils.logger.Printer;
import com.example.finder.utils.validator.ValidatorAnnounce;
import com.example.finder.utils.validator.ValidatorAuth;
import com.example.finder.utils.validator.ValidatorImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class AnnounceService {
    private final AnnounceRepository announceRepository;
    private final AnnounceTypeRepository announceTypeRepository;
    private final AnnounceStatusRepository announceStatusRepository;
    private final CategoryRepository categoryRepository;
    private final InteractivityStateRepository interactivityStateRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final SanitizerUtil sanitizerUtil;
    private final ValidatorAuth validatorAuth;
    private final ValidatorAnnounce validatorAnnounce;
    private final ValidatorImage validatorImage;
    private final ImageUtil imageUtil;
    private final PaginationConfig paginationConfig;

    public AnnounceService(
            AnnounceRepository announceRepository,
            AnnounceTypeRepository announceTypeRepository,
            AnnounceStatusRepository announceStatusRepository,
            CategoryRepository categoryRepository,
            InteractivityStateRepository interactivityStateRepository,
            RecordStatusRepository recordStatusRepository,
            SanitizerUtil sanitizerUtil,
            ValidatorAuth validatorAuth,
            ValidatorAnnounce validatorAnnounce,
            ValidatorImage validatorImage,
            ImageUtil imageUtil,
            PaginationConfig paginationConfig
    ) {
        this.announceRepository = announceRepository;
        this.announceTypeRepository = announceTypeRepository;
        this.announceStatusRepository = announceStatusRepository;
        this.categoryRepository = categoryRepository;
        this.interactivityStateRepository = interactivityStateRepository;
        this.recordStatusRepository = recordStatusRepository;
        this.sanitizerUtil = sanitizerUtil;
        this.validatorAuth = validatorAuth;
        this.validatorAnnounce = validatorAnnounce;
        this.validatorImage = validatorImage;
        this.imageUtil = imageUtil;
        this.paginationConfig = paginationConfig;
    }

    /**
     * Get data related to a specific announce and creates an api response from it
     * @param uuid id of announce
     * @param mustShowHidden if false only gets an announce with a record status of shown,
     *                       if true return an announce as long as it exists (admin)
     * @return api response with the relevant data
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAnnounceDetail(UUID uuid, Boolean mustShowHidden){
        try {
            List<Specification<Announce>> specList = new ArrayList<>();
            if(!mustShowHidden){
                specList.add(AnnounceSpecifications.hasShownStatus());
            }
            Specification<Announce> spec = Specification.allOf(specList);
            Announce foundAnnounce = announceRepository.findById(uuid).orElse(null);
            if(foundAnnounce == null){
                return ApiResponseFactory.notFound("No corresponding announce was found");
            }
            AnnounceDto announceDto = new AnnounceDto(
                    foundAnnounce,
                    imageUtil.getBaseWebPathForPhotos()
            );
            return ApiResponseFactory.success(announceDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get page of announces depending on given arguments and returns the data in a ApiResponse
     * @param page page to get
     * @param size amount of announces per page
     * @param type AvailableAnnounceType value for filter between Found, Lost or pass null for both
     * @param searchQuery filter announces to match the search query if it exists
     * @param categoryId filter announces by a category if categoryId is not null
     * @param mustShowHidden false only get announces with a record status of Shown, true doesn't
     *                       filter based on such status (intended for admin board)
     * @return api response with data according the the given parameters
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPaginatedAnnounces(
            int page,
            int size,
            AvailableAnnounceTypes type,
            String searchQuery,
            Long categoryId,
            Boolean mustShowHidden
    ) {
        try {
            AnnounceType announceTypeRequired = type != null
                ? announceTypeRepository.findByName(type.getDisplayName()).orElse(null)
                : null;
            size = Math.min(size, paginationConfig.getMaxResultsPerPage());
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<Specification<Announce>> specList = new ArrayList<>(
                    Arrays.asList(
                            AnnounceSpecifications.hasCategory(categoryId),
                            AnnounceSpecifications.hasSearch(searchQuery)
                    )
            );
            if(announceTypeRequired != null){
                specList.add(AnnounceSpecifications.hasType(announceTypeRequired));
            }
            if(!mustShowHidden){
                specList.add(AnnounceSpecifications.hasShownStatus());
            }
            Specification<Announce> spec = Specification.allOf(specList);

            Page<Announce> announcePage = announceRepository.findAll(spec, pageable);
            Page<AnnounceDto> announceDtoPage = announcePage.map( (it) -> {
                return new AnnounceDto(
                        it,
                        imageUtil.getBaseWebPathForPhotos()
                );
            });
            var paginatedResult = PaginatedResponse.from(announceDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (Exception e) {
            e.printStackTrace();
            return  ApiResponseFactory.internalError();
        }
    }

    /**
     * Create a new found object announce
     * @param request data related to the announce
     * @param receivedImage image provided for the item
     * @return api response with DTO representation of the created announce
     */
    public ResponseEntity<?> createNewFoundAnnounce(
            RequestAnnounce request,
            MultipartFile receivedImage
    ) {
        try {
            validatorImage.validateImage(receivedImage);
        } catch (FileException e) {
            return ApiResponseFactory.badRequest(e.getMessage());
        }

        String savedImageName = null;
        boolean isSuccess = false;

        try {
            AppUser requester = validatorAuth.getUserFromSecurityContext();

            RequestAnnounce sanitizedRequest = sanitizerUtil.sanitizeAnnounceInputs(request);
            List<ErrorDto> validationErrors = validatorAnnounce.validateAnnounceInputs(sanitizedRequest);
            if (!validationErrors.isEmpty()) {
                return ApiResponseFactory.badRequest(
                        AnnounceError.INVALID_CREATION_DATA.getErrorMessage(),
                        validationErrors
                );
            }

            AnnounceType foundType = announceTypeRepository.getFoundAnnounceTypeOrThrow();
            AnnounceStatus unsolvedStatus = announceStatusRepository.getUnsolvedAnnounceStatusOrThrow();
            RecordStatus showStatus = recordStatusRepository.getShownRecordStatusOrThrow();
            InteractivityState openState = interactivityStateRepository.getOpenInteractivityStateOrThrow();

            Category itemCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(CategoryNotFoundException::new);

            savedImageName = ImageUtil.createImageName(foundType, itemCategory);

            imageUtil.saveImage(receivedImage, savedImageName);

            Announce newAnnounce = new Announce(
                    request.getTitle(),
                    request.getDescription(),
                    request.getRelevantDate(),
                    request.getCity(),
                    request.getCountry(),
                    request.getLatitude(),
                    request.getLongitude()
            );
            newAnnounce.setPhoto(savedImageName);
            newAnnounce.setAuthor(requester);
            newAnnounce.setCategory(itemCategory);
            newAnnounce.setType(foundType);
            newAnnounce.setStatus(unsolvedStatus);
            newAnnounce.setRecordStatus(showStatus);
            newAnnounce.setInteractivityState(openState);

            announceRepository.save(newAnnounce);

            String webPathToImage = imageUtil.getWebPathToPhoto(newAnnounce.getPhoto());
            Printer.printLog(StringUtil.concat(
                    "New announce:",
                    newAnnounce.getTitle(),
                    " -> web path to image:",
                    webPathToImage
                    ));
            AnnounceDto announceDto = new AnnounceDto(newAnnounce, webPathToImage);
            isSuccess = true;
            return ApiResponseFactory.success(announceDto);
        } catch (UserNotFoundException e) {
            return ApiResponseFactory.unauthorized("Invalid user or user is not logged");
        } catch (IOException e) {
            return ApiResponseFactory.internalError("An error occurred while processing the image");
        } catch (InvalidRequestException e) {
            return ApiResponseFactory.badRequest("Invalid request sent to create a new item found announce");
        } catch (Exception e) {
            Printer.printErrorLogWithDetails(e);
            return ApiResponseFactory.internalError();
        } finally {
            if (!isSuccess){
                try{
                    Files.deleteIfExists(imageUtil.getLocalImagePath(savedImageName));
                    Printer.printLog("deleted file after failure");
                } catch (Exception e) {
                    Printer.printLog("failed to delete file");
                    Printer.printErrorLogWithDetails(e);
                }
            }
        }
    }
}
