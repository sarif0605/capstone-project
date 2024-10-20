package id.co.mii.LMS.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import id.co.mii.LMS.Models.Materi;
import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Segment;

public interface MateriRepository extends JpaRepository<Materi, Integer> {
 
    List<Materi> findByLecture(Person lecture);

    List<Materi> findByStudent(Segment segment);
}
