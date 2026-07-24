package com.school.scheduling.service.impl;

import com.school.scheduling.dto.ScheduleAdjustDTO;
import com.school.scheduling.entity.*;
import com.school.scheduling.mapper.*;
import com.school.scheduling.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private ScheduleChangeMapper scheduleChangeMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TimeSlotMapper timeSlotMapper;
    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleMapper.findAll();
    }

    @Override
    public List<Schedule> getSchedulesByClass(Long classId) {
        return scheduleMapper.findByClassId(classId);
    }

    @Override
    public List<Schedule> getSchedulesByTeacher(Long teacherId) {
        return scheduleMapper.findByTeacherId(teacherId);
    }

    @Override
    public Schedule getById(Long id) {
        return scheduleMapper.findById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> autoSchedule() {
        // 先清空原有排课
        scheduleMapper.deleteAll();

        List<ClassInfo> classes = classMapper.findAll();
        List<Course> courses = courseMapper.findAll();
        List<User> teachers = userMapper.findTeachers();
        List<TimeSlot> timeSlots = timeSlotMapper.findAll();

        int successCount = 0;
        int failCount = 0;
        List<String> messages = new ArrayList<>();

        // 记录已占用的时间槽：key=timeSlotId，教师集合和班级集合
        Map<Long, Set<Long>> teacherOccupied = new HashMap<>(); // 教师已占用的timeSlot
        Map<Long, Set<Long>> classOccupied = new HashMap<>();   // 班级已占用的timeSlot

        // 建立课程->教师列表映射
        Map<Long, List<Long>> courseTeachersMap = new HashMap<>();
        for (Course course : courses) {
            List<Long> teacherIds = teacherCourseMapper.findTeacherIdsByCourseId(course.getId());
            if (teacherIds.isEmpty()) {
                // 如果没配置，把所有老师都加上（演示用）
                teacherIds = teachers.stream().map(User::getId).collect(Collectors.toList());
            }
            courseTeachersMap.put(course.getId(), teacherIds);
        }

        // 按天按节次优先安排主科（语文数学英语排在上午）
        List<Course> priorityCourses = courses.stream()
                .filter(c -> Arrays.asList("语文", "数学", "英语").contains(c.getCourseName()))
                .collect(Collectors.toList());
        List<Course> otherCourses = courses.stream()
                .filter(c -> !Arrays.asList("语文", "数学", "英语").contains(c.getCourseName()))
                .collect(Collectors.toList());

        Random random = new Random();

        for (ClassInfo clazz : classes) {
            // 先安排优先课程
            List<Course> allCoursesInOrder = new ArrayList<>();
            allCoursesInOrder.addAll(priorityCourses);
            allCoursesInOrder.addAll(otherCourses);

            for (Course course : allCoursesInOrder) {
                int weeklyHours = course.getWeeklyHours();
                int scheduledHours = 0;
                List<Long> availableTeachers = courseTeachersMap.get(course.getId());

                for (int attempt = 0; attempt < 100 && scheduledHours < weeklyHours; attempt++) {
                    TimeSlot slot = findAvailableSlot(timeSlots, teacherOccupied, classOccupied,
                            clazz.getId(), availableTeachers, random, scheduledHours);
                    if (slot == null) break;

                    // 选一个可用老师
                    Long selectedTeacher = null;
                    Collections.shuffle(availableTeachers, random);
                    for (Long tid : availableTeachers) {
                        if (!teacherOccupied.computeIfAbsent(slot.getId(), k -> new HashSet<>()).contains(tid)) {
                            selectedTeacher = tid;
                            break;
                        }
                    }
                    if (selectedTeacher == null) continue;

                    // 插入排课
                    Schedule schedule = new Schedule();
                    schedule.setClassId(clazz.getId());
                    schedule.setCourseId(course.getId());
                    schedule.setTeacherId(selectedTeacher);
                    schedule.setTimeSlotId(slot.getId());
                    schedule.setWeekNumber(1);
                    schedule.setClassroom(clazz.getClassName() + "教室");
                    schedule.setStatus("NORMAL");
                    scheduleMapper.insert(schedule);

                    // 标记占用
                    teacherOccupied.computeIfAbsent(slot.getId(), k -> new HashSet<>()).add(selectedTeacher);
                    classOccupied.computeIfAbsent(slot.getId(), k -> new HashSet<>()).add(clazz.getId());
                    scheduledHours++;
                    successCount++;
                }

                if (scheduledHours < weeklyHours) {
                    failCount += (weeklyHours - scheduledHours);
                    messages.add(String.format("班级【%s】的【%s】有%d节课未能安排",
                            clazz.getClassName(), course.getCourseName(), weeklyHours - scheduledHours));
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("messages", messages);
        return result;
    }

    private TimeSlot findAvailableSlot(List<TimeSlot> slots, Map<Long, Set<Long>> teacherOccupied,
                                        Map<Long, Set<Long>> classOccupied, Long classId,
                                        List<Long> teacherIds, Random random, int currentHourCount) {
        // 上午时段优先（前4节），上午是slot_name 第1-4节，或者可以按start_time判断
        List<TimeSlot> morningSlots = slots.stream()
                .filter(s -> s.getStartTime().getHour() < 12)
                .collect(Collectors.toList());
        List<TimeSlot> afternoonSlots = slots.stream()
                .filter(s -> s.getStartTime().getHour() >= 12)
                .collect(Collectors.toList());

        // 前几节优先排上午
        List<TimeSlot> candidatePool;
        if (currentHourCount < 4) {
            candidatePool = new ArrayList<>(morningSlots);
            candidatePool.addAll(afternoonSlots);
        } else {
            candidatePool = new ArrayList<>(afternoonSlots);
            candidatePool.addAll(morningSlots);
        }

        List<TimeSlot> shuffled = new ArrayList<>(candidatePool);
        Collections.shuffle(shuffled, random);

        for (TimeSlot slot : shuffled) {
            Set<Long> classSet = classOccupied.computeIfAbsent(slot.getId(), k -> new HashSet<>());
            if (classSet.contains(classId)) continue;

            Set<Long> teacherSet = teacherOccupied.computeIfAbsent(slot.getId(), k -> new HashSet<>());
            for (Long tid : teacherIds) {
                if (!teacherSet.contains(tid)) {
                    return slot;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public boolean clearSchedules() {
        return scheduleMapper.deleteAll() >= 0;
    }

    @Override
    @Transactional
    public ScheduleChange adjustSchedule(ScheduleAdjustDTO dto, Long operatorId) {
        Schedule schedule = scheduleMapper.findById(dto.getScheduleId());
        if (schedule == null) {
            throw new RuntimeException("排课记录不存在");
        }

        // 检查时间冲突
        if (dto.getNewTimeSlotId() != null) {
            Schedule classConflict = scheduleMapper.findByTimeSlotAndClass(
                    dto.getNewTimeSlotId(), schedule.getClassId(), schedule.getWeekNumber());
            if (classConflict != null && !classConflict.getId().equals(schedule.getId())) {
                throw new RuntimeException("该班级在目标时间段已有课程");
            }
            Long checkTeacher = dto.getNewTeacherId() != null ? dto.getNewTeacherId() : schedule.getTeacherId();
            Schedule teacherConflict = scheduleMapper.findByTimeSlotAndTeacher(
                    dto.getNewTimeSlotId(), checkTeacher, schedule.getWeekNumber());
            if (teacherConflict != null && !teacherConflict.getId().equals(schedule.getId())) {
                throw new RuntimeException("该教师在目标时间段已有课程");
            }
        }

        // 检查教师冲突
        if (dto.getNewTeacherId() != null && dto.getNewTimeSlotId() == null) {
            Schedule teacherConflict = scheduleMapper.findByTimeSlotAndTeacher(
                    schedule.getTimeSlotId(), dto.getNewTeacherId(), schedule.getWeekNumber());
            if (teacherConflict != null && !teacherConflict.getId().equals(schedule.getId())) {
                throw new RuntimeException("该教师在当前时间段已有课程");
            }
        }

        // 记录变更
        ScheduleChange change = new ScheduleChange();
        change.setScheduleId(schedule.getId());
        change.setOldTimeSlotId(schedule.getTimeSlotId());
        change.setNewTimeSlotId(dto.getNewTimeSlotId());
        change.setNewTeacherId(dto.getNewTeacherId());
        change.setChangeType(dto.getChangeType());
        change.setChangeReason(dto.getChangeReason());
        change.setOperatorId(operatorId);
        scheduleChangeMapper.insert(change);

        // 更新排课
        Schedule update = new Schedule();
        update.setId(schedule.getId());
        update.setTimeSlotId(dto.getNewTimeSlotId());
        update.setTeacherId(dto.getNewTeacherId());
        update.setClassroom(dto.getClassroom());
        update.setStatus("ADJUSTED");
        update.setRemark(dto.getChangeReason());
        scheduleMapper.update(update);

        return change;
    }

    @Override
    public List<ScheduleChange> getChangeHistory() {
        return scheduleChangeMapper.findAll();
    }
}
