package id.co.mii.LMS.Service;

import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import id.co.mii.LMS.Models.Materi;
import id.co.mii.LMS.Models.Person;
import id.co.mii.LMS.Models.Segment;
import id.co.mii.LMS.Repository.MateriRepository;
import id.co.mii.LMS.Repository.PersonRepository;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MateriService {

    private MateriRepository materiRepository;
    private PersonRepository personRepository;
    private SegmentService segmentService;
    private JavaMailSender javaMailSender;

    // public Materi createMateri(String title, MultipartFile file, Integer
    // segmentId, String lectureUsername) {

    // try {
    // Materi materi = new Materi();
    // materi.setTitle(title);
    // materi.setFileName(file.getOriginalFilename());
    // materi.setFileType(file.getContentType());
    // materi.setFileSize(file.getSize());

    // // Convert MultipartFile to byte[]
    // byte[] fileData = file.getBytes();
    // materi.setFileData(fileData);

    // // Get lecture by username
    // Person lecture = personRepository.findByUsername(lectureUsername);
    // if (lecture == null) {
    // throw new RuntimeException("Lecture not found.");
    // }
    // materi.setLecture(lecture);

    // // Get segment by segmentId
    // Segment segment = segmentService.getById(segmentId);
    // if (segment == null) {
    // throw new RuntimeException("Segment not found.");
    // }
    // materi.setStudent(segment);

    // return materiRepository.save(materi);
    // } catch (IOException e) {
    // throw new RuntimeException("Failed to create materi.", e);
    // }
    // }
    public Materi getMateriById(Integer materiId) {
        return materiRepository.findById(materiId).orElse(null);
    }

    public List<Materi> getAll() {
        return materiRepository.findAll();
    }

    public Integer getLectureIdByUsername(String username) {
        return personRepository.findIdByUsername(username);
    }

    public List<Materi> getAllMateriByLoggedInLecture(Authentication authentication) {
        String username = authentication.getName();
        Integer lectureId = getLectureIdByUsername(username);
        Person lecture = personRepository.findById(lectureId).orElse(null);
        return materiRepository.findByLecture(lecture);
    }

    public Materi createMateri(String title, MultipartFile file, Integer segmentId, String lectureUsername) {
        try {
            Materi materi = new Materi();
            materi.setTitle(title);
            materi.setFileName(file.getOriginalFilename());
            materi.setFileType(file.getContentType());
            materi.setFileSize(file.getSize());

            // Save the file to the local file system
            String filePath = saveFileToLocalSystem(file);
            materi.setFilePath(filePath);

            // Get lecture by username
            Person lecture = personRepository.findByUsername(lectureUsername);
            if (lecture == null) {
                throw new RuntimeException("Lecture not found.");
            }
            materi.setLecture(lecture);

            // Get segment by segmentId
            Segment segment = segmentService.getById(segmentId);
            if (segment == null) {
                throw new RuntimeException("Segment not found.");
            }
            materi.setStudent(segment);

            Materi createdMateri = materiRepository.save(materi);
            sendNotificationEmailToStudents(segment.getStudents(), createdMateri);
            return createdMateri;

        } catch (IOException e) {
            throw new RuntimeException("Failed to create materi.", e);
        }
    }

    private void sendNotificationEmailToStudents(List<Person> students, Materi materi) {
    for (Person student : students) {
        String recipientEmail = student.getEmail();
        String subject = "New Materi Available";
        String messageText = "Dear Student,\n\nA new materi titled '" + materi.getTitle() + "' has been added in the segment. Please check it out.";

        sendEmail(recipientEmail, subject, messageText);
    }
}

    private void sendEmail(String recipientEmail, String subject, String messageText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(messageText);

        javaMailSender.send(message);
    }

    private String saveFileToLocalSystem(MultipartFile file) throws IOException {
        // Generate a unique file name or use the original file name
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // Define the directory path where the file will be saved
        String directoryPath = "E:\\test\\";

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file to the local file system
        String filePath = directoryPath + File.separator + fileName;
        File localFile = new File(filePath);
        file.transferTo(localFile);

        return filePath;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        return uniqueFileName;
    }

    public List<Materi> getAllMateriByLoggedInStudent(Authentication authentication) {
        String username = authentication.getName();
        Person student = personRepository.findByUsername(username);

        // Assuming each student is associated with only one segment
        List<Segment> segments = student.getSegments_student();
        if (segments.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if the student has no segments
        }

        Segment segment = segments.get(0); // Get the first segment
        return materiRepository.findByStudent(segment);
    }

    public Materi delete(Integer id, Authentication authentication) {
        Materi materi = materiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materi not found!"));

        String username = authentication.getName();
        if (!materi.getLecture().getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this materi.");
        }
        materiRepository.delete(materi);
        return materi;
    }

    public Materi updateMateri(Integer materiId, String title, MultipartFile file, Integer segmentId,
            Authentication authentication) {
        Materi materi = materiRepository.findById(materiId).orElse(null);
        if (materi == null) {
            throw new RuntimeException("Materi not found.");
        }

        try {
            materi.setTitle(title);

            // Update file properties if a new file is provided
            if (file != null && !file.isEmpty()) {
                materi.setFileName(file.getOriginalFilename());
                materi.setFileType(file.getContentType());
                materi.setFileSize(file.getSize());

                // Save the file to the local file system
                String filePath = saveFileToLocalSystem(file);
                materi.setFilePath(filePath);
            }

            // Get lecture from authentication
            Person lecture = getLoggedInPerson(authentication);
            materi.setLecture(lecture);

            // Get segment by segmentId
            Segment segment = segmentService.getById(segmentId);
            if (segment == null) {
                throw new RuntimeException("Segment not found.");
            }
            materi.setStudent(segment);

            return materiRepository.save(materi);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update materi.", e);
        }
    }

    private Person getLoggedInPerson(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Authentication required.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return personRepository.findByUsername(username);
        }

        throw new RuntimeException("Failed to get logged-in person.");
    }

}
