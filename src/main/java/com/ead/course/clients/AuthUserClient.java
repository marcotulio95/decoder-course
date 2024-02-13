package com.ead.course.clients;

import com.ead.course.dtos.CourseUserDTO;
import com.ead.course.dtos.ResponsePageDTO;
import com.ead.course.dtos.user.UserDTO;
import com.ead.course.utils.ServiceUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Log4j2
public class AuthUserClient {
    @Value("${ead.api.url.authuser}")
    private String REQUEST_URL_AUTHUSER;

    @Autowired
    private RestTemplate restTemplate;

    public Page<UserDTO> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        ResponseEntity<ResponsePageDTO<UserDTO>> response = null;
        String url = REQUEST_URL_AUTHUSER + ServiceUtils.createUrlGetAllUsersByCourse(courseId, pageable);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        try {
            ParameterizedTypeReference<ResponsePageDTO<UserDTO>> responseType = new ParameterizedTypeReference<ResponsePageDTO<UserDTO>>() {
            };
            response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            int numberOfElements = response.getBody() != null ? response.getBody().getSize() : 0;
            log.debug("Response Number of Elements: {} ", numberOfElements);
        } catch (HttpStatusCodeException e) {
            log.error("Error request /courses {} ", e);
        }
        log.info("Ending request /users courseId {} ", courseId);
        return response.getBody();
    }

    public ResponseEntity<UserDTO> getOneUserById(UUID userId) {
        final String requestUrl = REQUEST_URL_AUTHUSER + "/users/" + userId;
        return restTemplate.exchange(requestUrl, HttpMethod.GET, null, UserDTO.class);
    }

    public void postSubscriptionInCourse(UUID courseId, UUID userId) {
        final String requestUrl = REQUEST_URL_AUTHUSER + "/users/" + userId + "/courses/subscription";
        var courseUserDTO = new CourseUserDTO();
        courseUserDTO.setCourseId(courseId);
        courseUserDTO.setUserId(userId);
        restTemplate.postForEntity(requestUrl, courseUserDTO, String.class);
    }
}
