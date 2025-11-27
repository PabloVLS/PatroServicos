package com.patroservicos.PatroServicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patroservicos.PatroServicos.model.Photo;
import java.util.Optional;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Optional<Photo> findByUserId(Integer userId);

    Optional<Photo> findFirstByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Photo> findByUserIdOrderByCreatedAtDesc(Integer userId);
}
