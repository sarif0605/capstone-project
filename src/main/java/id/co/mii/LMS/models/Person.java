package id.co.mii.LMS.Models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 13)
    private String phone;

    @Column(nullable = false)
    private String address;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private User user;

    //segment
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tb_person_segment", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "segment_id"))
    private List<Segment> segments_student;

    @OneToMany(mappedBy = "lecture")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Segment> segment_lecture;

    //materi
    @OneToMany(mappedBy = "lecture")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Materi> materi_lecture;

    @OneToMany(mappedBy = "student")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Materi> materi_student;

    //asignment
    @OneToMany(mappedBy = "lecture")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Assignment> assignment_lecture;
}
