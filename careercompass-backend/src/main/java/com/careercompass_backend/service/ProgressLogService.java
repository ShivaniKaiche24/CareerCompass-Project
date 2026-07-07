package com.careercompass_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careercompass_backend.dto.ProgressLogDTO;
import com.careercompass_backend.model.User;
import com.careercompass_backend.model.ProgressLog;
import com.careercompass_backend.model.Task;
import com.careercompass_backend.repository.ProgressLogRepository;
import com.careercompass_backend.repository.TaskRepository;
import com.careercompass_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressLogService {

	private final  ProgressLogRepository progressLogRepository;
	private final UserRepository userRepository;
	private final TaskRepository taskRepository;
	
	
    // ─────────────────────────────────────────────────────
    // UPDATE DAILY LOG — called every time a task is completed
    //
    // This method does three things:
    // 1. Counts today's completed tasks
    // 2. Calculates streak based on yesterday's log
    // 3. Saves/updates today's ProgressLog row
    // ─────────────────────────────────────────────────────
   @Transactional
	public ProgressLogDTO updateDailyLog(Long userId) {
		
	   User user = userRepository.findById(userId)
			   .orElseThrow(()-> new RuntimeException("User Not Found " +userId));
	   
	   LocalDate today = LocalDate.now();
	   
	   List<Task> totalTask = taskRepository.findByTodaysTaskForUser(userId, LocalDate.now());
	
	    int completedToday = (int) totalTask.stream().filter(t->"COMPLETED".equals(t.getStatus())).count();
	   
	    int totalToday = totalTask.size();
	    
	    /*
         * STREAK CALCULATION:
         *
         * Check yesterday's log:
         *   - If yesterday has tasksCompleted > 0 → we were on a streak
         *     → today's streak = yesterday's streak + 1
         *   - If yesterday has no log or tasksCompleted == 0
         *     → streak broke → today's streak = 1 (if we completed today)
         *                    → or 0 (if nothing completed today either)
         */

	    LocalDate yesterday = today.minusDays(1);
	    
	    Optional<ProgressLog> yesterdayLog = progressLogRepository.findByUserAndLogDate(user, yesterday);
	    
	    int newStreak ;
	    if(completedToday > 0) {
	    	if(yesterdayLog.isPresent() && yesterdayLog.get().getTasksCompleted() >0 ) {
	    		 // Yesterday was active → continue streak
                newStreak = yesterdayLog.get().getStreakCount() +1;
	    	} else {
	    		// No yesterday log or yesterday was empty → start new 
	    		newStreak= 1;
	    	}
	    }else {
	    	 // Nothing completed today → streak is 0
             newStreak =0;
	    }
	    
	    /*
         * UPSERT pattern:
         * Find today's log if it exists (user might have completed
         * multiple tasks today → we UPDATE, not INSERT a duplicate)
         * If no log for today → create a new one.
         */

	    ProgressLog todayLog = progressLogRepository.findByUserAndLogDate(user, today)
	    		.orElse(new ProgressLog());
	    
	    todayLog.setUser(user);
	    todayLog.setLogDate(today);
	    todayLog.setTasksCompleted(completedToday);
	    todayLog.setTasksTotal(totalToday);
	    todayLog.setStreakCount(newStreak);
	    
	    ProgressLog saved = progressLogRepository.save(todayLog);
	    
	    log.info("Progress log updated: user={}, streak={}, done={}/{}",
                userId, newStreak, completedToday, totalToday);
	    
	    return toDTO (saved);
   }
	
	
	
	
// ─────────────────────────────────────────────────────
//   GET CURRENT STREAK — used by dashboard
//   Load the user
//   Find today's ProgressLog for that user
//   If today's log exists — return the streakCount from it
//   If today's log doesn't exist — check yesterday's log and return that streak
//   If nothing found at all — return 0
// ─────────────────────────────────────────────────────   
  
      
	   public int getCurrentStreak(Long userId) {
		   User user = userRepository.findById(userId)
				   .orElseThrow(()-> new RuntimeException("User Not Found " +userId));
		   
		   LocalDate today = LocalDate.now();
		   Optional<ProgressLog> todayLog = progressLogRepository.findByUserAndLogDate(user, today);
			
		   LocalDate yesterday = today.minusDays(1);
		   Optional<ProgressLog> yesterdayLog = progressLogRepository.findByUserAndLogDate(user, yesterday);
		   
	        if(todayLog.isPresent()) {
	        	return todayLog.get().getStreakCount();
	        }
	        if (yesterdayLog.isPresent()) {
	            return yesterdayLog.get().getStreakCount();
	        }

	        return 0;
	   }
	   
	

        


	
	

    // ─────────────────────────────────────────────────────
    // GET ALL LOGS — for history/chart view
    // ─────────────────────────────────────────────────────
    
   public List<ProgressLogDTO> getAllLogs (Long userId) {
	   User user = userRepository.findById(userId)
			   .orElseThrow(() -> new RuntimeException("User Not Found " +userId));
	   
	   List<ProgressLog> allLogs = progressLogRepository.findByUserOrderByLogDateDesc(user);
	  return allLogs.stream().map(this:: toDTO).collect(Collectors.toList());
   }
   
   
   
      // DTO 
   private ProgressLogDTO toDTO (ProgressLog log) {
	   ProgressLogDTO dto = new ProgressLogDTO();
	   
	   dto.setLogDate(log.getLogDate());
	   dto.setLogId(log.getLogId());
	   dto.setLogDate(log.getLogDate());
       dto.setTasksCompleted(log.getTasksCompleted());
       dto.setTasksTotal(log.getTasksTotal());
       dto.setStreakCount(log.getStreakCount());
       dto.setUserId(log.getUser().getUserId());
       return dto;
   }
}
