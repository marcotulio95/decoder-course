package com.ead.course.controllers;

import com.ead.course.dtos.LessonDTO;
import com.ead.course.dtos.ModuleDTO;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/modules/{moduleId}/lessons")
@CrossOrigin(value = "*", maxAge = 3600)
public class LessonController {
    @Autowired
    LessonService lessonService;
    @Autowired
    ModuleService moduleService;

    @PostMapping
    public ResponseEntity<Object> create(@PathVariable(value = "moduleId") UUID moduleId, @RequestBody @Valid LessonDTO lessonDTO) {
        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);
        if (moduleModelOptional.isEmpty()) {
            return notFound(moduleId);
        }
        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDTO, lessonModel);
        lessonModel.setCreationTime(DateUtils.getLocalDateTimeNow());
        lessonModel.setModule(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lessonModel));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Object> delete(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId) {
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("lessonId [%s] not found for moduleId [%s]", lessonId, moduleId));
        }
        lessonService.delete(lessonModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully");
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<Object> update(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId, @RequestBody @Valid ModuleDTO moduleDTO) {
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Module not found for courseId [%s]", lessonId));
        }
        final LessonModel lessonModel = lessonModelOptional.get();
        BeanUtils.copyProperties(moduleDTO, lessonModel);
        lessonModel.setLastUpdateTime(DateUtils.getLocalDateTimeNow());
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModel));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<Object> getOne(@PathVariable(value = "moduleId") UUID moduleId, @PathVariable(value = "lessonId") UUID lessonId) {
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        return lessonModelOptional.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("lessonId [%s] not found for moduleId [%s]", lessonId, moduleId)));
    }

    @GetMapping
    public ResponseEntity<Page<LessonModel>> getAll(@PathVariable(value = "moduleId") UUID moduleId,
                                                    SpecificationTemplate.LessonSpec spec, @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(lessonService.findAllByModule(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable));
    }

}
