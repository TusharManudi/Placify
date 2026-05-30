package com.app.placify.repository;

import com.app.placify.dto.AppliedDto;
import com.app.placify.dto.ExportDto;
import com.app.placify.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface AppRepo extends JpaRepository<Application,Long> {

    @Query("""
        select new com.app.placify.dto.AppliedDto(
            a.applicationId,
            j.jobListingId,
            a.appliedDate,
            a.appliedTime,
            j.companyName,
            j.jobRole
            )
            from Application a join JobListing j on a.jobListingId = j.jobListingId
            where a.studentId = :studentId
            order by a.appliedDate desc
    """)
    List<AppliedDto> fetchApplicationsByStudentId(Long studentId);

    @Query("""
    select new com.app.placify.dto.ExportDto(
        s.name,
        s.universityRollNo,
        s.email,
        s.tenthPercentage,
        s.twelfthPercentage,
        s.graduationCgpa,
        s.course,
        s.branch 
        )
    from Application a, Student s
    where a.studentId = s.studentId and a.jobListingId = :jobId
    order by s.name
""")
    List<ExportDto> fetchApplicantsForJob(Long jobId);

    boolean existsByStudentIdAndJobListingId(Long studentId, Long jobListingId);
}
