package com.alleynejr.brainmesh_backend.repository;

import com.alleynejr.brainmesh_backend.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
}
