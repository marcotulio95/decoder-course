package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import com.ead.course.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static com.ead.course.utils.ControllerUtils.notFound;

@RestController
@RequestMapping("/courses/{courseId}/modules")
public class ModuleController {
    @Autowired
    ModuleService moduleService;
    @Autowired
    CourseService courseService;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@PathVariable(value = "courseId") UUID courseId, @RequestBody @Valid ModuleDTO moduleDTO) {
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return notFound(courseId);
        }
        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDTO, moduleModel);
        moduleModel.setCreationTime(DateUtils.getLocalDateTimeNow());
        moduleModel.setLastUpdateTime(DateUtils.getLocalDateTimeNow());
        moduleModel.setCourse(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(moduleModel));
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Object> delete(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(moduleId, courseId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Module not found for courseId [%s]", courseId));
        }
        moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully");
    }

    @PutMapping("/{moduleId}")
    public ResponseEntity<Object> update(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "moduleId") UUID moduleId, @RequestBody @Valid ModuleDTO moduleDTO) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(moduleId, courseId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Module not found for courseId [%s]", courseId));
        }
        final ModuleModel moduleModel = moduleModelOptional.get();
        BeanUtils.copyProperties(moduleDTO, moduleModel);
        moduleModel.setLastUpdateTime(DateUtils.getLocalDateTimeNow());
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
    }

    @GetMapping("/{moduleId}")
    public ResponseEntity<Object> getOne(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "moduleId") UUID moduleId) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(moduleId, courseId);
        return moduleModelOptional.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("moduleId [%s] not found for courseId [%s]", moduleId, courseId)));
    }

    @GetMapping
    public ResponseEntity<Page<ModuleModel>> getAll(@PathVariable(value = "courseId") UUID courseId, SpecificationTemplate.ModuleSpec spec, @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(moduleService.findAllByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }
}
