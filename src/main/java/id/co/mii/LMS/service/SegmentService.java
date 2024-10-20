package id.co.mii.LMS.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Segment;
import id.co.mii.LMS.Repository.PersonRepository;
import id.co.mii.LMS.Repository.SegmentRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SegmentService {
    private SegmentRepository segmentRepository;
    private PersonRepository personRepository;

    public Segment create(Segment segment) {
        return segmentRepository.save(segment);
    }

    public List<Segment> getAll() {
        return segmentRepository.findAll();
    }

    public Segment getById(Integer id) {
        return segmentRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment not found!!!"));
    }

    public Integer getLectureIdByUsername(String username) {
        return personRepository.findIdByUsername(username);
    }

    public List<Segment> getAllSegmentsByLoggedInLecture(Authentication authentication) {
        String username = authentication.getName();
        Integer lectureId = getLectureIdByUsername(username);
        Person lecture = personRepository.findById(lectureId).orElse(null);
        return segmentRepository.findByLecture(lecture);
    }

    public List<Segment> getSegmentsByLoggedInStudent(Authentication authentication) {
        String username = authentication.getName();
        Person student = personRepository.findByUsername(username);

        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        return segmentRepository.findByStudentsContaining(student);
    }

    public Segment update(Integer id, Segment updatedSegment) {
        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment not found!!!"));

        // Pengecekan pengguna yang masuk
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String loggedInUsername = userDetails.getUsername();

        if (!segment.getLecture().getUser().getUsername().equals(loggedInUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to update this segment.");
        }

        // Melakukan pembaruan data segment dengan nilai-nilai baru dari updatedSegment
        segment.setTitle(updatedSegment.getTitle());
        segment.setStart_date(updatedSegment.getStart_date());
        segment.setEnd_date(updatedSegment.getEnd_date());
        segment.setDescription(updatedSegment.getDescription());
        // Tambahkan pembaruan lainnya sesuai kebutuhan

        return segmentRepository.save(segment);
    }

    public Segment delete(Integer id, Authentication authentication) {
        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment not found!"));

        String username = authentication.getName();
        if (!segment.getLecture().getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this segment.");
        }

        segmentRepository.delete(segment);
        return segment;
    }

}
