package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.patroservicos.PatroServicos.model.Feedback;
import com.patroservicos.PatroServicos.repository.FeedbackRepository;
import com.patroservicos.PatroServicos.service.IFeedbackService;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public Optional<Feedback> getFeedbackById(Integer id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> getFeedbacksByProfessionalId(Integer professionalId) {
        return feedbackRepository.findByProfessionalId(professionalId);
    }

    @Override
    public List<Feedback> getFeedbacksByUserId(Integer userId) {
        return feedbackRepository.findByUserId(userId);
    }

    @Override
    public Optional<Feedback> getFeedbackByProfessionalAndUser(Integer professionalId, Integer userId) {
        return feedbackRepository.findByProfessionalIdAndUserId(professionalId, userId);
    }

    @Override
    public void deleteFeedback(Integer id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public Double getAverageRatingByProfessionalId(Integer professionalId) {
        List<Feedback> feedbacks = getFeedbacksByProfessionalId(professionalId);
        
        if (feedbacks.isEmpty()) {
            return 0.0;
        }
        
        return feedbacks.stream()
            .mapToDouble(Feedback::getAvaliacao)
            .average()
            .orElse(0.0);
    }

    @Override
    public Integer countFeedbacksByProfessionalId(Integer professionalId) {
        return (int) getFeedbacksByProfessionalId(professionalId).size();
    }
}
