package id.co.mii.LMS.Controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Segment;
import id.co.mii.LMS.Repository.PersonRepository;
import id.co.mii.LMS.Repository.SegmentRepository;
import id.co.mii.LMS.Service.SegmentService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/segment")
public class SegmentController {

    private SegmentService segmentService;
    private PersonRepository personRepository;
    private SegmentRepository segmentRepository;
    private JavaMailSender javaMailSender;

    // @GetMapping
    // public List<Segment> getAll() {
    // return segmentService.getAll();
    // }

    @GetMapping("/student")
    public ResponseEntity<List<Segment>> getSegmentsByLoggedInStudent(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Segment> segments = segmentService.getSegmentsByLoggedInStudent(authentication);
        return ResponseEntity.ok(segments);
    }

    @GetMapping
    public ResponseEntity<List<Segment>> getAllSegments(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_LECTURE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Segment> segments = segmentService.getAllSegmentsByLoggedInLecture(authentication);
        return ResponseEntity.ok(segments);
    }

    @PostMapping
    public ResponseEntity<Segment> create(@RequestBody Segment segment, Principal principal) {
        // Cek peran pengguna
        if (!hasRole(principal, "ROLE_LECTURE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Dapatkan ID pengguna yang terkait dengan pengguna saat ini
        Integer lectureId = segmentService.getLectureIdByUsername(principal.getName());
        Person lecture = null;

        // Periksa apakah pengguna memiliki peran lecture
        if (lectureId != null) {
            // Dapatkan objek Person lecture berdasarkan ID pengguna
            lecture = personRepository.findById(lectureId).orElse(null);
        }

        // Set objek Person lecture pada Segment jika ditemukan
        if (lecture != null) {
            segment.setLecture(lecture);
        }

        // Simpan segmen baru
        Segment createdSegment = segmentService.create(segment);
        return ResponseEntity.ok(createdSegment);
    }

    private boolean hasRole(Principal principal, String roleName) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;

            // Mendapatkan informasi peran dari autentikasi pengguna
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // Mencetak peran yang dimiliki oleh pengguna
            System.out.println("User Roles: " + authorities);

            // Memeriksa apakah pengguna memiliki peran yang diberikan
            return authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleName));
        }

        return false;
    }

    @GetMapping("/{id}")
    public Segment getById(@PathVariable Integer id) {
        return segmentService.getById(id);
    }

    @PreAuthorize("hasRole('LECTURE')")
    @PostMapping("/addStudents")
    public Segment addStudentToSegment(@RequestBody Map<String, Integer> requestBody) {
        Integer segmentId = requestBody.get("segmentId");
        Integer studentId = requestBody.get("studentId");

        Segment segment = segmentRepository.findById(segmentId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Segment not found with id: " + segmentId));

        Person student = personRepository.findById(studentId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + studentId));

        segment.addStudent(student);
        segmentRepository.save(segment);
        sendNotificationEmail(student.getEmail(), segment.getTitle());
        return segment;
    }

    private void sendNotificationEmail(String recipientEmail, String segmentName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("You have been added to a segment");
        message.setText("Dear Student,\n\nYou have been added to the segment: " + segmentName);

        javaMailSender.send(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Segment> updateSegment(@PathVariable Integer id, @RequestBody Segment updatedSegment,
            Authentication authentication) {
        Segment segment = segmentService.update(id, updatedSegment);
        return ResponseEntity.ok(segment);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Segment> deleteSegment(@PathVariable Integer id, Authentication authentication) {
        segmentService.delete(id, authentication);
        return ResponseEntity.ok().build();
    }

}
