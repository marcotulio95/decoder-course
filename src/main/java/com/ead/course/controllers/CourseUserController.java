package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionDTO;
import com.ead.course.dtos.user.UserDTO;
import com.ead.course.enums.users.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static com.ead.course.utils.ControllerUtils.conflict;
import static com.ead.course.utils.ControllerUtils.notFound;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}/users")
public class CourseUserController {
    @Autowired
    private AuthUserClient authUserClient;
    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseUserService courseUserService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsersByCourse(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable, @PathVariable(value = "courseId") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersByCourse(courseId, pageable));
    }

    @PostMapping("/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId, @RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        ResponseEntity<UserDTO> responseUser;
        final Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return notFound(courseId);
        }
        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDTO.getUserId())) {
            return conflict(subscriptionDTO.getUserId(), "already subscribed");
        }

        try {
            responseUser = authUserClient.getOneUserById(subscriptionDTO.getUserId());
            final UserDTO userDTO = responseUser.getBody();
            if (userDTO != null && UserStatus.BLOCKED.equals(userDTO.getUserStatus())) {
                return conflict(subscriptionDTO.getUserId(), "is blocked");
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                return notFound(subscriptionDTO.getUserId());
            }
        }

        //final CourseUserModel courseUserModel = courseUserService.save(courseModelOptional.get().convertToCourseUserModel(subscriptionDTO.getUserId()));
        courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDTO.getUserId()));
        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully");
    }
}
