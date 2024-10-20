package id.co.mii.LMS.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import id.co.mii.LMS.Models.Materi;
import id.co.mii.LMS.Service.MateriService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/materi")
@AllArgsConstructor
public class MateriController {

    private MateriService materiService;

    @PostMapping
    public ResponseEntity<Materi> createMateri(@RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @RequestParam("segmentId") Integer segmentId,
            Authentication authentication) {
        // Check user role
        if (!hasRole(authentication, "ROLE_LECTURE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String lectureUsername = authentication.getName();

        // Create materi
        Materi createdMateri = materiService.createMateri(title, file, segmentId, lectureUsername);

        return ResponseEntity.ok(createdMateri);
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

    @GetMapping
    public ResponseEntity<List<Materi>> getAllMateri(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_LECTURE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Materi> materi = materiService.getAllMateriByLoggedInLecture(authentication);
        return ResponseEntity.ok(materi);
    }

    @GetMapping("/{materiId}/download")
    public ResponseEntity<ByteArrayResource> downloadMateri(@PathVariable Integer materiId) {
        Materi materi = materiService.getMateriById(materiId);
        if (materi == null) {
            return ResponseEntity.notFound().build();
        }

        // Baca file dari lokal file system
        File file = new File(materi.getFilePath());
        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Buat ByteArrayResource dari fileContent
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materi.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/student")
    public ResponseEntity<List<Materi>> getAllMateriByLoggedInStudent(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_STUDENT")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Materi> materi = materiService.getAllMateriByLoggedInStudent(authentication);
        return ResponseEntity.ok(materi);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Materi> deleteMateri(@PathVariable Integer id, Authentication authentication) {
        materiService.delete(id, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{materiId}")
    public ResponseEntity<Materi> updateMateri(
            @PathVariable Integer materiId,
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("segmentId") Integer segmentId,
            Authentication authentication) {
        Materi materi = materiService.updateMateri(materiId, title, file, segmentId, authentication);
        return ResponseEntity.ok(materi);
    }

}
