package com.patroservicos.PatroServicos.impl;

import com.patroservicos.PatroServicos.model.Report;
import com.patroservicos.PatroServicos.repository.ReportRepository;
import com.patroservicos.PatroServicos.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReportServiceImpl implements IReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Report createReport(Integer professionalId, Integer reporterId, String descricao) {
        // Verificar se o usuário já denunciou este profissional
        Optional<Report> existingReport = reportRepository.findByProfessionalAndReporter(professionalId, reporterId);
        if (existingReport.isPresent()) {
            throw new IllegalArgumentException("Você já denunciou este profissional");
        }

        Report report = new Report(professionalId, reporterId, descricao);
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getPendingReports() {
        return reportRepository.findPendingReports();
    }

    @Override
    public List<Report> getReportsByProfessional(Integer professionalId) {
        return reportRepository.findByProfessionalId(professionalId);
    }

    @Override
    public Optional<Report> getReportById(Integer id) {
        return reportRepository.findById(id);
    }

    @Override
    public Report updateReportStatus(Integer reportId, String status, String respostaModerador, Integer moderadorId) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isPresent()) {
            Report report = reportOpt.get();
            report.setStatus(status);
            report.setRespostaModerador(respostaModerador);
            report.setModeradorId(moderadorId);
            return reportRepository.save(report);
        }
        return null;
    }

    @Override
    public boolean hasUserReportedProfessional(Integer professionalId, Integer reporterId) {
        Optional<Report> report = reportRepository.findByProfessionalAndReporter(professionalId, reporterId);
        return report.isPresent();
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAllReports();
    }
}
