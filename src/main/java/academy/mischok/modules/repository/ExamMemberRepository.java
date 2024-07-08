package academy.mischok.modules.repository;

import academy.mischok.modules.dtos.ModuleDto;
import academy.mischok.modules.model.ExamMemberEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExamMemberRepository extends JpaRepository<ExamMemberEntity, Long> {


}
