package com.siaas.student;

import com.siaas.academic.Marks;
import com.siaas.academic.MarksRepository;
import com.siaas.attendance.AttendanceRepository;
import com.siaas.common.ResourceNotFoundException;
import com.siaas.student.dto.DashboardResponse;
import com.siaas.student.dto.StudentProfileResponse;
import com.siaas.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final MarksRepository marksRepository;
    private final AttendanceRepository attendanceRepository;

    public Student getStudentOrThrow(User user) {
        return studentRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
    }

    public StudentProfileResponse getProfile(User user) {
        Student s = getStudentOrThrow(user);
        return new StudentProfileResponse(
            s.getFullName(),
            user.getEmail(),
            s.getRollNumber(),
            s.getSemester(),
            s.getSection()
        );
    }

    public DashboardResponse getDashboard(User user) {
        Student student = getStudentOrThrow(user);

        List<Marks> currentMarks = marksRepository.findCurrentByStudent(student);
        List<Marks> allMarks     = marksRepository.findAllByStudent(student);

        Map<UUID, Long> presentMap = toMap(attendanceRepository.getPresentCountBySubject(student));
        Map<UUID, Long> totalMap   = toMap(attendanceRepository.getTotalCountBySubject(student));

        List<DashboardResponse.SubjectStats> subjects = new ArrayList<>();
        double sumScore = 0;
        String semesterName = "";

        for (Marks m : currentMarks) {
            UUID subjectId = m.getSubject().getId();
            long present = presentMap.getOrDefault(subjectId, 0L);
            long total   = totalMap.getOrDefault(subjectId, 0L);
            double attPct = total > 0 ? round1((double) present / total * 100) : 0.0;
            boolean isWeak = attPct < 75 || m.getTotal() < 50;

            subjects.add(new DashboardResponse.SubjectStats(
                m.getSubject().getCode(),
                m.getSubject().getName(),
                m.getSubject().getCredits(),
                m.getTotal(),
                m.getGrade(),
                m.getGradePoints(),
                attPct,
                isWeak
            ));
            sumScore += m.getTotal();
            semesterName = m.getSemester().getName();
        }

        double sgpa    = computeWeightedGpa(currentMarks);
        double cgpa    = computeWeightedGpa(allMarks);
        double avgScore = subjects.isEmpty() ? 0 : round1(sumScore / subjects.size());

        double totalPresent = presentMap.values().stream().mapToLong(Long::longValue).sum();
        double totalClasses = totalMap.values().stream().mapToLong(Long::longValue).sum();
        double overallAtt   = totalClasses > 0 ? round1(totalPresent / totalClasses * 100) : 0;

        long weakCount = subjects.stream().filter(DashboardResponse.SubjectStats::isWeak).count();

        return new DashboardResponse(
            cgpa, sgpa, overallAtt, avgScore,
            (int) weakCount, semesterName, subjects
        );
    }

    private double computeWeightedGpa(List<Marks> marksList) {
        double points = 0, credits = 0;
        for (Marks m : marksList) {
            int c = m.getSubject().getCredits();
            points  += m.getGradePoints() * c;
            credits += c;
        }
        return credits > 0 ? round2(points / credits) : 0;
    }

    private Map<UUID, Long> toMap(List<Object[]> rows) {
        Map<UUID, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((UUID) row[0], ((Number) row[1]).longValue());
        }
        return map;
    }

    private double round1(double v) { return Math.round(v * 10.0) / 10.0; }
    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }
}
