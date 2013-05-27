package droolsbook.sampleApplication.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.service.BankingService;

@Controller
public class TaskCompleteController {

  @Autowired
  private BankingService bankingService;

  @RequestMapping("/taskComplete.htm")
  public String taskComplete(Long taskId) {
    bankingService.complete(taskId, "123");
    return "redirect:taskList.htm";
  }

}
