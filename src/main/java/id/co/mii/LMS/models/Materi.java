package id.co.mii.LMS.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_materi")
public class Materi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String fileName;

    // @Column(nullable = false, length = 10485760)
    // private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Person lecture;

    @ManyToOne
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment student;

    private transient MultipartFile file;

    @Column(nullable = true)
    private String filePath;

    // Additional fields for file information, if needed
    private String fileType;
    private long fileSize;

    // Constructor without file parameter
    public Materi(Integer id, String title, String fileName, Person lecture, Segment segment) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.lecture = lecture;
        this.student = segment;
    }

}
