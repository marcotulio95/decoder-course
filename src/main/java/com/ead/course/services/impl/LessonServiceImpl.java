package com.ead.course.services.impl;

import com.ead.course.models.LessonModel;
import com.ead.course.repositories.LesssonRepository;
import com.ead.course.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {
    @Autowired
    private LesssonRepository lesssonRepository;

    @Override
    public LessonModel save(LessonModel lessonModel) {
        return lesssonRepository.save(lessonModel);
    }

    @Override
    public Optional<LessonModel> findLessonIntoModule(UUID moduleId, UUID lessonId) {
        return lesssonRepository.findLessonIntoModule(moduleId, lessonId);
    }

    @Override
    public void delete(LessonModel lessonModel) {
        lesssonRepository.delete(lessonModel);
    }

    @Override
    public List<LessonModel> findAllByCourse(UUID moduleId) {
        return lesssonRepository.findAllLessonsIntoModule(moduleId);
    }
}
