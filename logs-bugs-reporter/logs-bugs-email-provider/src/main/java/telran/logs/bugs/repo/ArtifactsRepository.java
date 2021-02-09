package telran.logs.bugs.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Artifact;

public interface ArtifactsRepository extends JpaRepository<Artifact, String> {

}
