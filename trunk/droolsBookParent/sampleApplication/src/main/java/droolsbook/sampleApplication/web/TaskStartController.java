package droolsbook.sampleApplication.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.service.BankingService;

@Controller
public class TaskStartController {

  @Autowired
  private BankingService bankingService;
  
  @RequestMapping("/taskStart.htm")
  public String taskStart(Long taskId) {
    bankingService.start(taskId, "123");
    return "redirect:taskList.htm";
  }
  
}
