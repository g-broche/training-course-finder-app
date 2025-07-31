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
import com.example.finder.repository.*;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

    public ResponseEntity<?> getPaginatedAnnounces(int page, int size) {
        try {
            size = Math.min(size, paginationConfig.getMaxResultsPerPage());
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Announce> announcePage = announceRepository.findAll(pageable);
            Page<AnnounceDto> announceDtoPage = announcePage.map( (it) -> new AnnounceDto(
                it,
                imageUtil.getWebPathToPhoto(it.getPhoto())
            )
            );
            var paginatedResult = PaginatedResponse.from(announceDtoPage);
            return ApiResponseFactory.success(paginatedResult);
        } catch (Exception e) {
            return  ApiResponseFactory.internalError();
        }
    }

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
