package id.co.mii.LMS.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Segment;

@Repository
public interface SegmentRepository extends JpaRepository <Segment, Integer> {

    List<Segment> findByLecture(Person lecture);
    List<Segment> findByStudentsContaining(Person student);
}
