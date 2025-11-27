package com.patroservicos.PatroServicos.repository;

import com.patroservicos.PatroServicos.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    /**
     * Busca todas as denúncias pendentes
     */
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDENTE' ORDER BY r.criadoEm DESC")
    List<Report> findPendingReports();

    /**
     * Busca todas as denúncias de um profissional
     */
    @Query("SELECT r FROM Report r WHERE r.professionalId = ?1 ORDER BY r.criadoEm DESC")
    List<Report> findByProfessionalId(Integer professionalId);

    /**
     * Busca denúncias por status
     */
    @Query("SELECT r FROM Report r WHERE r.status = ?1 ORDER BY r.criadoEm DESC")
    List<Report> findByStatus(String status);

    /**
     * Busca se um usuário já denunciou um profissional
     */
    @Query("SELECT r FROM Report r WHERE r.professionalId = ?1 AND r.reporterId = ?2")
    Optional<Report> findByProfessionalAndReporter(Integer professionalId, Integer reporterId);

    /**
     * Busca todas as denúncias
     */
    @Query("SELECT r FROM Report r ORDER BY r.criadoEm DESC")
    List<Report> findAllReports();
}
