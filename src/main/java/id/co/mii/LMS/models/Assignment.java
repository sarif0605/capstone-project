package id.co.mii.LMS.Models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;
    private String file_tugas;

    private Date deadline;

    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Person lecture;

    @OneToMany(mappedBy = "assignment")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<AssigmentSub> assigmentSubs;

}
